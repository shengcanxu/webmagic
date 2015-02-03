package us.codecraft.webmagic.example;

import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.model.OOSpider;
import us.codecraft.webmagic.model.annotation.*;
import us.codecraft.webmagic.pipeline.MysqlPageModelPipeline;
import us.codecraft.webmagic.scheduler.StackScheduler;

/**
 * Created by canoxu on 2015/2/1.
 */

@ResetDB(value = false)
@ParseUrl(value = "http://www.ximalaya.com/\\d+/album/\\d+", sourceRegion = "//*[@id=\"discoverAlbum\"]//div[@class=\"layout_right\"]")
public class XimalayaAlbum {
    @ExtractBy(value = "//*[@id=\"mainbox\"]//h1/text()")
    private String title;

    @ExtractBy(value = "//*[@id=\"mainbox\"]//div[@class=\"detailContent_category\"]//a/text()")
    private String category;

    @ExtractByUrl(value = "")
    private String PageURL;

    public static void main(String[] args) {
        Site site = Site.me().setTimeOut(10000).setRetryTimes(5).setDomain("www.ximalaya.com");
        OOSpider.create(site, new MysqlPageModelPipeline(), XimalayaAlbum.class)
                .scheduler(new StackScheduler())
                .addUrl("http://album.ximalaya.com/dq/book/").run();
    }
}
