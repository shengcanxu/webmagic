package us.codecraft.webmagic.ximalaya;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.AbstractPageProcessor;
import us.codecraft.webmagic.scheduler.StackScheduler;

import java.util.List;

/**
 * Created by cano on 2015/1/17.
 */
public class AlbumList extends AbstractPageProcessor {
    Site site = Site.me().setDomain("www.ximalaya.com");
    Logger logger = LoggerFactory.getLogger(getClass());

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
                links =  this.getLinksFromRegrex(page,"http://www.ximalaya.com/\\d+/album/\\d+",null,null);
                logger.info("get " + links.size() + " links to follow");
                break;
            case 1:
                links =  this.getLinksFromRegrex(page,"http://www.ximalaya.com/zhubo/\\d+",null,null);
                logger.info("get " + links.size() + " links to follow");
                break;
            default:
                break;
        }
        return links;
    }

    @Override
    public void extractContent(Page page) {
        page.putField("title",page.getHtml().xpath("//*[@id=\"timelinePage\"]//h1/text()").toString());
    }

    public static void main(String[] args){
        Spider.create(new AlbumList()).setScheduler(new StackScheduler()).addUrl("http://album.ximalaya.com/dq/book/").run();
    }

}
