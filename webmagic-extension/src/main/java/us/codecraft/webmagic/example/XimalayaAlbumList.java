package us.codecraft.webmagic.example;

import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.model.OOSpider;
import us.codecraft.webmagic.model.annotation.ExtractBy;
import us.codecraft.webmagic.model.annotation.ExtractByUrl;
import us.codecraft.webmagic.pipeline.ConsolePageModelPipeline;
import us.codecraft.webmagic.scheduler.StackScheduler;

/**
 * Created by canoxu on 2015/2/1.
 */

public class XimalayaAlbumList {
    @ExtractBy(value = "//*[@id=\"discoverAlbum\"]//div[@class=\"discoverAlbum_item\"]/a/text()")
    private String name;


    @ExtractByUrl(value = "")
    private String PageURL;

    public static void main(String[] args) {
        Site site = Site.me().setTimeOut(10000).setRetryTimes(5).setDomain("www.ximalaya.com");
        OOSpider.create(site, new ConsolePageModelPipeline(), XimalayaAlbumList.class)
                .scheduler(new StackScheduler())
                .addUrl("http://album.ximalaya.com/dq/book/").run();
    }
}
