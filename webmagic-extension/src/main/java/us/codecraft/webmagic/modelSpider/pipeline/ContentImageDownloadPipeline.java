package us.codecraft.webmagic.modelSpider.pipeline;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.modelSpider.PageModel;
import us.codecraft.webmagic.modelSpider.annotation.DownloadContentImage;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.XpathSelector;

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
public class ContentImageDownloadPipeline implements Pipeline {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void process(ResultItems resultItems, Task task) {
        PageModel pageModel = (PageModel) resultItems.getPageModel();
        if(!pageModel.hasContentImageToDownload()) return;

        Map<String, DownloadContentImage> fileDownloadMap = pageModel.getDownloadContentImageMap();
        int tryTimes = task.getSite().getRetryTimes() == 0 ? 1 : task.getSite().getRetryTimes();
        for(Map.Entry<String, DownloadContentImage> entry : fileDownloadMap.entrySet()){
            DownloadContentImage downloadImage = entry.getValue();
            String content = resultItems.get(entry.getKey());
            Html html = new Html(content);

            //get the urls
            List<String> urls = html.selectDocumentForList(new XpathSelector("//img/@src"));

            String imageUrlToPath = "";
            for(String url : urls){
                String filePath = downloadImage.savepath() + DigestUtils.md5Hex(url) +  url.substring(url.lastIndexOf("."));

                boolean succ = false;
                for (int i=0; i<tryTimes; i++) {
                    succ = downloadAndSaveFile(url, filePath, task.getSite());
                    if(succ) break;
                }
                if(succ){
                    imageUrlToPath = imageUrlToPath + url + "=" + filePath + ";";
                }else{
                    logger.error("download fail " + tryTimes + " times for " + url);
                }
            }
            resultItems.put(entry.getKey()+"FileMap", imageUrlToPath);
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
