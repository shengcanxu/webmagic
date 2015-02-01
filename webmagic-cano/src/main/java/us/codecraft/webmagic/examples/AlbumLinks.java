package us.codecraft.webmagic.examples;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.model.PageModel;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.AbstractPageProcessor;
import us.codecraft.webmagic.scheduler.StackScheduler;


/**
 * Created by cano on 2015/2/1.
 */
public class AlbumLinks extends AbstractPageProcessor {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private Site site = Site.me().setDomain("www.ximalaya.com").setTimeOut(10000).setRetryTimes(5);

    @Override
    public PageModel buildPageModel() {
        PageModel pageModel = new PageModel();
        pageModel.setModelName("AlbumList");

        //pageModel.addItem("link","//*[@id=\"discoverAlbum\"]//div[@class=\"discoverAlbum_wrapper\"]");
        pageModel.addMultipleItems("link",
                "//*[@id=\"discoverAlbum\"]//div[@class=\"discoverAlbum_wrapper\"]",
                "//div[@class=\"discoverAlbum_item\"]");

        return pageModel;
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args){
        Spider.create(new AlbumLinks())
                .setScheduler(new StackScheduler())
                .addUrl("http://album.ximalaya.com/dq/book/")
                //.addPipeline(new ItemContentOpPipeline())
                //.addPipeline(new MysqlPipeline())
                .addPipeline(new ConsolePipeline())
                .thread(1)
                .run();
    }
}
