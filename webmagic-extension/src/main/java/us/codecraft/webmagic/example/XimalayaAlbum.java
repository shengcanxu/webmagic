package us.codecraft.webmagic.example;

import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.model.annotation.ExtractBy;
import us.codecraft.webmagic.model.annotation.ExtractByUrl;
import us.codecraft.webmagic.model.annotation.ParseUrl;
import us.codecraft.webmagic.model.annotation.ResetDB;
import us.codecraft.webmagic.modelSpider.ModelSpider;
import us.codecraft.webmagic.modelSpider.PageModel;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.scheduler.StackScheduler;

/**
 * Created by canoxu on 2015/2/1.
 */

@ResetDB(value = false)
@ParseUrl(urlPattern = "http://www.ximalaya.com/\\d+/album/\\d+",
        sourceRegion = "//*[@id=\"discoverAlbum\"]//div[@class=\"layout_right\"]",
        nextPageRegion = "//*[@id=\"discoverAlbum\"]//a[@rel=\"next\"]")
@ExtractBy(value = "//*[@id=\"timelinePage\"]//h1/text()")
public class XimalayaAlbum extends PageModel {
    @ExtractBy(value = "//*[@id=\"mainbox\"]//h1/text()")
    private String title;

    @ExtractBy(value = "//*[@id=\"mainbox\"]//div[@class=\"detailContent_category\"]//a/text()")
    private String category;

    @ExtractByUrl(value = "")
    private String PageURL;

    public static void main(String[] args) {
        Site site = Site.me().setTimeOut(10000).setRetryTimes(5).setDomain("www.ximalaya.com");
        ModelSpider.create(site,new XimalayaAlbum())
                .scheduler(new StackScheduler())
                .addPipeline(new ConsolePipeline())
                .addUrl("http://album.ximalaya.com/dq/book/").thread(1).run();
    }
}
