package ximalaya;

import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.model.OOSpider;
import us.codecraft.webmagic.model.annotation.ExtractBy;
import us.codecraft.webmagic.model.annotation.ExtractByUrl;
import us.codecraft.webmagic.model.annotation.ParseUrl;
import us.codecraft.webmagic.model.annotation.ResetDB;
import us.codecraft.webmagic.pipeline.ConsolePageModelPipeline;
import us.codecraft.webmagic.scheduler.StackScheduler;

/**
 * Created by canoxu on 2015/2/1.
 */

@ResetDB(value = false)
@ParseUrl(urlPattern = "http://www.ximalaya.com/\\d+/album/\\d+",
        sourceRegion = "//*[@id=\"discoverAlbum\"]//div[@class=\"layout_right\"]",
        nextPageRegion = "//*[@id=\"discoverAlbum\"]//a[@rel=\"next\"]")
public class XimalayaAlbum {
    @ExtractBy(value = "//*[@id=\"mainbox\"]//h1/text()")
    private String title;

    @ExtractBy(value = "//*[@id=\"mainbox\"]//div[@class=\"detailContent_category\"]//a/text()")
    private String category;

    @ExtractByUrl(value = "")
    private String PageURL;

    public static void main(String[] args) {
        Site site = Site.me().setTimeOut(10000).setRetryTimes(5).setDomain("www.ximalaya.com");
        OOSpider.create(site, new ConsolePageModelPipeline(), XimalayaAlbum.class)
                .scheduler(new StackScheduler())
                .addUrl("http://album.ximalaya.com/dq/book/").thread(10).run();
    }
}
