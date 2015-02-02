package us.codecraft.webmagic.example;

import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.model.OOSpider;
import us.codecraft.webmagic.model.annotation.ExtractBy;
import us.codecraft.webmagic.model.annotation.TargetUrl;
import us.codecraft.webmagic.pipeline.MysqlPageModelPipeline;

/**
 * Created by canoxu on 2015/2/1.
 */

@TargetUrl(value = "http://www.ximalaya.com/\\d+/album/\\d+", sourceRegion = "//*[@id=\"discoverAlbum\"]//div[@class=\"layout_right\"]")
public class XimalayaAlbum {
    @ExtractBy(value = "//*[@id=\"mainbox\"]//h1/text()")
    private String title;

    public String getTitle() {
        return title;
    }

    public static void main(String[] args) {
        Site site = Site.me().setTimeOut(10000).setRetryTimes(5).setDomain("www.ximalaya.com");
        OOSpider.create(site, new MysqlPageModelPipeline(), XimalayaAlbum.class)
                .addUrl("http://album.ximalaya.com/dq/book/").run();
    }
}
