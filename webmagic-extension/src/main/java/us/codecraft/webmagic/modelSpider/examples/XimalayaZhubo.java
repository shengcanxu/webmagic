package us.codecraft.webmagic.modelSpider.examples;

import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.model.annotation.ExtractBy;
import us.codecraft.webmagic.model.annotation.ExtractByUrl;
import us.codecraft.webmagic.modelSpider.ModelSpider;
import us.codecraft.webmagic.modelSpider.PageModel;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.scheduler.RedisScheduler;

/**
 * Created by canoxu on 2015/2/1.
 */


//@ParseUrl(urlPattern = "http://www.ximalaya.com/\\d+/album/\\d+", sourceRegion = "//*[@id=\"discoverAlbum\"]//div[@class=\"layout_right\"]")
//@ParseUrl(urlPattern = "http://www.ximalaya.com/zhubo/\\d+/", sourceRegion = "//*[@id=\"mainbox\"]//div[@class=\"personal_header\"]")
public class XimalayaZhubo extends PageModel{
    @ExtractBy(value = "//*[@id=\"timelinePage\"]//h1/text()")
    private String name;

    @ExtractByUrl(regrex = "")
    private String PageURL;

    public static void main(String[] args) {
        Site site = Site.me().setTimeOut(10000).setRetryTimes(5).setDomain("www.ximalaya.com");
        ModelSpider.create(site, new XimalayaZhubo())
                .scheduler(new RedisScheduler("127.0.0.1",true))
                .addPipeline(new ConsolePipeline())
                .addUrl("http://album.ximalaya.com/dq/book/").thread(1).run();
    }
}
