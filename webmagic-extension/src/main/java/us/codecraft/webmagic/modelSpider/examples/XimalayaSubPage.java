package us.codecraft.webmagic.modelSpider.examples;

import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.model.annotation.ExtractBy;
import us.codecraft.webmagic.model.annotation.ExtractByUrl;
import us.codecraft.webmagic.modelSpider.ModelSpider;
import us.codecraft.webmagic.modelSpider.PageModel;
import us.codecraft.webmagic.modelSpider.annotation.DownloadFile;
import us.codecraft.webmagic.modelSpider.annotation.SubPageField;
import us.codecraft.webmagic.modelSpider.pipeline.MysqlPipeline;
import us.codecraft.webmagic.pipeline.ConsolePipeline;

import java.util.List;

/**
 * Created by canoxu on 2015/2/1.
 */

public class XimalayaSubPage extends PageModel {

    @ExtractBy(value = "//*[@id=\"mainbox\"]//h1/text()")
    private List<String> name;

    @DownloadFile(savepath = "E:/test/",type = DownloadFile.Type.PICTURE)
    @ExtractBy(value = "//*[@id=\"mainbox\"]//a[@class=\"albumface180\"]/span/img/@src")
    private List<String> picture;


    @ExtractByUrl(regrex = "")
    private String PageURL;

    @SubPageField(SubPageRegion = "//*div[@class=\"personal_header\"]/div[@class=\"picture\"]")
    private ChildPage subpage;

    public class ChildPage extends PageModel {

        @ExtractBy(value = "//*[@id=\"timelinePage\"]//span[@class=\"user_name\"]/h1/text()")
        private String author;

    }

    public static void main(String[] args) {
        Site site = Site.me().setTimeOut(10000).setRetryTimes(5).setDomain("www.ximalaya.com");
        ModelSpider.create(site, new XimalayaSubPage())
                //.scheduler(new RedisScheduler("127.0.0.1"))
                //.scheduler(new StackScheduler())
                .addPipeline(new MysqlPipeline().setShouldResetDb(true))
                .addPipeline(new ConsolePipeline())
                .addUrl("http://www.ximalaya.com/20115042/album/339173").thread(10).run();
    }
}


