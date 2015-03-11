package us.codecraft.webmagic.modelSpider.examples;

import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.model.annotation.ExtractBy;
import us.codecraft.webmagic.model.annotation.ExtractByUrl;
import us.codecraft.webmagic.modelSpider.ModelSpider;
import us.codecraft.webmagic.modelSpider.PageModel;
import us.codecraft.webmagic.modelSpider.annotation.ExpandFieldValues;
import us.codecraft.webmagic.modelSpider.pipeline.MysqlPipeline;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.scheduler.RedisScheduler;

import java.util.List;

/**
 * Created by canoxu on 2015/2/1.
 */

@ExpandFieldValues
public class XimalayaAlbumList extends PageModel {

    @ExtractBy(value = "//*[@id=\"discoverAlbum\"]//div[@class=\"discoverAlbum_item\"]/a/text()")
    private List<String> name;

    @ExtractBy(value = "//*[@id=\"discoverAlbum\"]//ul[@class=\"sort_list\"]/li/a/text()")
    private List<String> category;


    @ExtractByUrl(regrex = "")
    private String PageURL;

    public static void main(String[] args) {
        Site site = Site.me().setTimeOut(10000).setRetryTimes(5).setDomain("www.ximalaya.com");
        ModelSpider.create(site, new XimalayaAlbumList())
                .scheduler(new RedisScheduler("127.0.0.1").setStartOver(true))
                .addPipeline(new MysqlPipeline().setShouldResetDb(true))
                .addPipeline(new ConsolePipeline())
                .addUrlForRefresh("http://album.ximalaya.com/dq/book/")
                .setRecoverQueue(true).run();
    }
}
