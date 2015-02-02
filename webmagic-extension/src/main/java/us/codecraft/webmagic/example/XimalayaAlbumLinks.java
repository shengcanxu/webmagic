package us.codecraft.webmagic.example;

import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.model.ConsolePageModelPipeline;
import us.codecraft.webmagic.model.OOSpider;
import us.codecraft.webmagic.model.annotation.ExtractBy;
import us.codecraft.webmagic.model.annotation.TargetUrl;

/**
 * Created by canoxu on 2015/2/1.
 */

//@TargetUrl(value = "//*[@id=\"discoverAlbum\"]//div[@class=\"discoverAlbum_wrapper\"]", sourceRegion = "//div[@class=\"discoverAlbum_item\"]")
public class XimalayaAlbumLinks {
    @ExtractBy(value = "//*[@id=\"discoverAlbum\"]//div[@class=\"albumfaceOutter\"]/a/@href")
    private String albumURL;

    public String getAlbumURL() {
        return albumURL;
    }

    public static void main(String[] args) {
        Site site = Site.me().setTimeOut(10000).setRetryTimes(5).setDomain("www.ximalaya.com");
        OOSpider.create(site, new ConsolePageModelPipeline(), XimalayaAlbumLinks.class)
                .addUrl("http://album.ximalaya.com/dq/book/").run();
    }
}
