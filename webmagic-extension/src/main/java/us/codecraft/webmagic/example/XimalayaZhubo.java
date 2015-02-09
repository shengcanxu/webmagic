package us.codecraft.webmagic.example;

import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.model.annotation.ExtractBy;
import us.codecraft.webmagic.model.annotation.ExtractByUrl;
import us.codecraft.webmagic.modelSpider.annotation.ParseUrl;

/**
 * Created by canoxu on 2015/2/1.
 */


@ParseUrl(urlPattern = "http://www.ximalaya.com/\\d+/album/\\d+", sourceRegion = "//*[@id=\"discoverAlbum\"]//div[@class=\"layout_right\"]")
@ParseUrl(urlPattern = "http://www.ximalaya.com/zhubo/\\d+/", sourceRegion = "//*[@id=\"mainbox\"]//div[@class=\"personal_header\"]")
public class XimalayaZhubo {
    @ExtractBy(value = "//*[@id=\"timelinePage\"]//h1/text()")
    private String name;

    @ExtractByUrl(value = "")
    private String PageURL;

    public static void main(String[] args) {
        Site site = Site.me().setTimeOut(10000).setRetryTimes(5).setDomain("www.ximalaya.com");
//        OOSpider.create(site, new MysqlPipeline(), XimalayaZhubo.class)
//                .scheduler(new StackScheduler())
//                .addUrl("http://album.ximalaya.com/dq/book/").run();
    }
}
