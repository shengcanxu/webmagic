package us.codecraft.webmagic.examples;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.MysqlPipeline;
import us.codecraft.webmagic.processor.AbstractPageProcessor;
import us.codecraft.webmagic.scheduler.StackScheduler;

import java.util.List;

/**
 * Created by cano on 2015/1/17.
 */
public class AlbumList extends AbstractPageProcessor {
    private Site site = Site.me().setDomain("www.ximalaya.com");
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public Site getSite() {
        return site;
    }

    @Override
    public List<String> extractLinks(Page page) {
        List<String> links  = null;
        int level = page.getLevel();
        switch (level){
            case 0:
                links =  this.getLinksFromRegrex(page,"http://www.ximalaya.com/\\d+/album/\\d+","//*[@id=\"discoverAlbum\"]//div[@class=\"layout_right\"]");
                links = links.subList(0,2);
                logger.info("get " + links.size() + " links to follow");
                break;
            case 1:
                links =  this.getLinksFromRegrex(page,"http://www.ximalaya.com/zhubo/\\d+","//*[@id=\"mainbox\"]//div[@class=\"personal_header\"]");
                logger.info("get " + links.size() + " links to follow");
                break;
            default:
                break;
        }
        return links;
    }

    @Override
    public void extractContent(Page page) {
        this.getContentFromXpath(page,"title", "//*[@id=\"timelinePage\"]//h1/text()");
        this.getContentFromXpath(page,"sounds","//*[@id=\"timelinePage\"]//div[@class=\"timelinepersonPanel\"]//div[@class=\"count\"]//a/text()");
    }

    public static void main(String[] args){
        Spider.create(new AlbumList())
                .setScheduler(new StackScheduler())
                .addUrl("http://album.ximalaya.com/dq/book/")
                .addPipeline(new MysqlPipeline())
                .run();
    }

}
