package us.codecraft.webmagic.modelSpider.examples;

import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.model.annotation.ExtractBy;
import us.codecraft.webmagic.model.annotation.ExtractByUrl;
import us.codecraft.webmagic.modelSpider.ModelSpider;
import us.codecraft.webmagic.modelSpider.PageModel;
import us.codecraft.webmagic.modelSpider.annotation.ExtractByParseUrl;
import us.codecraft.webmagic.modelSpider.annotation.ParseUrl;
import us.codecraft.webmagic.modelSpider.annotation.TextFormatter;
import us.codecraft.webmagic.modelSpider.pipeline.MysqlPipeline;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.scheduler.RedisScheduler;

/**
 * Created by canoxu on 2015/2/1.
 */


@ParseUrl(subXpath = "//div/a/@href",
        express = "//*[@id=\"discoverAlbum\"]//div[@class=\"discoverAlbum_item\"]",
        nextPageRegion = "//*[@id=\"discoverAlbum\"]//a[@rel=\"next\"]")
@ExtractBy(value = "//*[@id=\"timelinePage\"]//h1/text()")
public class XimalayaAlbum extends PageModel {
    @ExtractBy(value = "//*[@id=\"mainbox\"]//h1/text()")
    private String title;

    @ExtractBy(value = "//*[@id=\"mainbox\"]//div[@class=\"detailContent_category\"]//a/text()")
    @TextFormatter(types={TextFormatter.Type.TRIM, TextFormatter.Type.REMOVETAG})
    private String category;

    @ExtractByParseUrl(xpath = "//span[@class=\"sound_playcount\"]/text()", depth = 1)
    private String playNum;

    @ExtractByUrl(regrex = "")
    private String PageURL;

    public static void main(String[] args) {
        Site site = Site.me().setTimeOut(10000).setRetryTimes(5).setDomain("www.ximalaya.com");
        ModelSpider.create(site,new XimalayaAlbum())
                //.scheduler(new StackScheduler())
                .scheduler(new RedisScheduler("127.0.0.1").setStartOver(true))
                .addPipeline(new MysqlPipeline().setShouldResetDb(true))
                .addPipeline(new ConsolePipeline())
                .addUrl("http://album.ximalaya.com/dq/book/").thread(1).run();
    }
}
