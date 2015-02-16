package us.codecraft.webmagic.modelSpider.pipeline;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.modelSpider.PageModel;
import us.codecraft.webmagic.modelSpider.annotation.DownloadFile;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Print page model in console.<br>
 * Usually used in test.<br>
 * @author code4crafter@gmail.com <br>
 * @since 0.2.0
 */
public class FileDownloadPipeline implements Pipeline {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private final int tryTimes = 3;

    @Override
    public void process(ResultItems resultItems, Task task) {
        if(resultItems.isSkip()) return;

        PageModel pageModel = (PageModel) resultItems.getPageModel();
        if(!pageModel.hasFileToDownload()) return;

        Map<String, DownloadFile> fileDownloadMap = pageModel.getFileDownloadMap();
        for(Map.Entry<String, DownloadFile> entry : fileDownloadMap.entrySet()){
            DownloadFile downloadFile = entry.getValue();
            switch (downloadFile.type()){
                case FILE:
                    break;
                case PICTURE:
                    Object urls = resultItems.get(entry.getKey());
                    Page page = resultItems.getPage();
                    if(urls instanceof List){
                        List<String> urlList = (List<String>) urls;
                        for(String url : urlList){
                            String filePath = downloadFile.savepath() + DigestUtils.md5Hex(url) +  url.substring(url.lastIndexOf("."));

                            boolean succ = false;
                            for (int i=0; i<tryTimes; i++) {
                                succ = downloadAndSaveFile(url, filePath, task.getSite());
                                if(succ) break;
                            }
                            if(succ){
                                resultItems.put(entry.getKey()+"File", filePath);
                            }else{
                                logger.error("download fail " + tryTimes + " times for " + url);
                            }
                        }
                    }else{
                        String url = (String) urls;
                        String filePath = downloadFile.savepath() + DigestUtils.md5Hex(url) +  url.substring(url.lastIndexOf("."));

                        boolean succ = false;
                        for (int i=0; i<tryTimes; i++) {
                            succ = downloadAndSaveFile(url, filePath, task.getSite());
                            if (succ) break;
                        }
                        if(succ){
                            resultItems.put(entry.getKey()+"File", filePath);
                        }else{
                            logger.error("download fail " + tryTimes + " times for " + url);
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private boolean downloadAndSaveFile(String url, String filePath, Site site){
        HttpClient httpclient = new DefaultHttpClient();
        try {
            File storeFile = new File(filePath);
            if(storeFile.exists()){
                logger.info("file exists for " + url);
                return true;
            }
            FileOutputStream output = new FileOutputStream(storeFile);

            HttpGet httpget = new HttpGet(url);
            Map<String, String> headers = site.getHeaders();
            for(Map.Entry<String, String> entry : headers.entrySet()){
                httpget.addHeader(entry.getKey(), entry.getValue());
            }
            logger.info("downloading file: " + url);
            HttpResponse response = httpclient.execute(httpget);

            // 得到网络资源的字节数组,并写入文件
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream instream = entity.getContent();
                try {
                    byte b[] = new byte[1024];
                    int j = 0;
                    while( (j = instream.read(b))!=-1){
                        output.write(b,0,j);
                    }
                    output.flush();
                    output.close();
                    logger.info(url + " downloaded and save to " + filePath);
                } catch (IOException ex) {
                    logger.error(ex.getMessage(),ex);
                    return false;
                } catch (RuntimeException ex) {
                    httpget.abort();
                    logger.error(ex.getMessage(),ex);
                    return false;
                } finally {
                    try { instream.close(); } catch (Exception ignore) { return false; }
                }
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        } finally {
            httpclient.getConnectionManager().shutdown();
        }

        return true;
    }
}