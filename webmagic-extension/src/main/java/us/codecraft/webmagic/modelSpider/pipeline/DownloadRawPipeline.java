package us.codecraft.webmagic.modelSpider.pipeline;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.io.File;
import java.io.FileOutputStream;

/**
 * 将源文件下载并保存下来
 * @author code4crafter@gmail.com <br>
 * @since 0.2.0
 */
public class DownloadRawPipeline implements Pipeline {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private String filePath;

    private DownloadRawPipeline() {
    }

    public DownloadRawPipeline(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void process(ResultItems resultItems, Task task) {
        String rawText = resultItems.getPage().getRawText();

        String url = resultItems.getRequest().getUrl();
        url = url.replace(".","").replace("/","").replace(":","").replace("?","").replace("&","").replace("-","");
        if(url.length() >=300){
            url = url.substring(url.length()-300,url.length());
        }

        saveFile(url,rawText);
    }

    private void saveFile(String fileName, String content){
        try {
            File storeFile = new File(filePath + fileName);
            if(storeFile.exists()){
                logger.info("file exists for path: " + fileName);
                return;
            }
            FileOutputStream output = new FileOutputStream(storeFile);
            output.write(content.getBytes());

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return ;
        }
    }
}
