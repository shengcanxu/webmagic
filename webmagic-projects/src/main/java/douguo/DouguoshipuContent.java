package douguo;

import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.model.annotation.ExtractBy;
import us.codecraft.webmagic.model.annotation.ExtractByUrl;
import us.codecraft.webmagic.modelSpider.ModelSpider;
import us.codecraft.webmagic.modelSpider.PageModel;
import us.codecraft.webmagic.modelSpider.annotation.DownloadFile;
import us.codecraft.webmagic.modelSpider.pipeline.ConsoleModelSpiderPipeline;
import us.codecraft.webmagic.modelSpider.pipeline.MysqlPipeline;
import us.codecraft.webmagic.scheduler.RedisScheduler;

/**
 * Created by cano on 2015/5/28.
 */

public class DouguoshipuContent extends PageModel {


    @ExtractByUrl(regrex = "")
    private String pageUrl;

    @ExtractBy(value = "//*[@id=\"main\"]//div[@class=\"bokpic\"]//a/img/@href")
    @DownloadFile(savepath = "D:\\software\\redis\\data\\pictures\\", type = DownloadFile.Type.PICTURE)
    private String picutre;

    public static void main(String[] args){
        //FileUtils.getFromFileToRedis("D:\\software\\redis\\data\\douguocookbooklinks.txt", "DouguoshipuContent", true);


        Site site = Site.me().setRetryTimes(5).setTimeOut(10000).setCycleRetryTimes(5).setDeepFirst(false)
                .setDomain("douguo.com").addHeader("Referer","http://www.douguo.com/");
        System.out.println(site);

        ModelSpider modelSpider = ModelSpider.create(site, new DouguoshipuContent());
        modelSpider.scheduler(new RedisScheduler("127.0.0.1",site).setStartOver(true))
                .addPipeline(new MysqlPipeline().setShouldResetDb(true))
                .addPipeline(new ConsoleModelSpiderPipeline());

        modelSpider.thread(1).run();
    }
}
