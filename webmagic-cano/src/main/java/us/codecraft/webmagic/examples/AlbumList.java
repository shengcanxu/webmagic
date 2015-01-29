package us.codecraft.webmagic.examples;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.model.PageModel;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ItemContentOpPipeline;
import us.codecraft.webmagic.pipeline.MysqlPipeline;
import us.codecraft.webmagic.processor.AbstractPageProcessor;
import us.codecraft.webmagic.scheduler.StackScheduler;

/**
 * Created by cano on 2015/1/17.
 */
public class AlbumList extends AbstractPageProcessor {
    private Site site = Site.me().setDomain("www.ximalaya.com").setTimeOut(10000);
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args){
        Spider.create(new AlbumList())
                .setScheduler(new StackScheduler())
                //.addUrl("http://album.ximalaya.com/dq/book/")
                //.addUrl("http://www.ximalaya.com/14675060/album/280961")
                .addUrl("http://www.ximalaya.com/zhubo/14675060")
                .addPipeline(new ItemContentOpPipeline())
                .addPipeline(new MysqlPipeline())
                .run();
    }

    @Override
    public PageModel buildPageModel() {
        PageModel pageModel = new PageModel();
        pageModel.setModelName("ximalaya");

        //pageModel.addLink("http://www.ximalaya.com/\\d+/album/\\d+", "//*[@id=\"discoverAlbum\"]//div[@class=\"layout_right\"]");
        //pageModel.addLink("http://www.ximalaya.com/zhubo/\\d+","//*[@id=\"mainbox\"]//div[@class=\"personal_header\"]");

        pageModel.addItem("title", "//*[@id=\"timelinePage\"]//h1/text()");
        pageModel.addItem("sounds","//*[@id=\"timelinePage\"]//div[@class=\"timelinepersonPanel\"]//div[@class=\"count\"]//a/text()");
        return pageModel;
    }
}
