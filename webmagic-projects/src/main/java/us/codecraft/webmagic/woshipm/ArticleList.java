package us.codecraft.webmagic.woshipm;

import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.modelSpider.ModelSpider;
import us.codecraft.webmagic.modelSpider.PageModel;
import us.codecraft.webmagic.modelSpider.annotation.ParseUrl;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.scheduler.RedisScheduler;

/**
 * Created by cano on 2015/2/19.
 */

@ParseUrl(value = "//*[@id=\"post-138510\"]/div",
        subXpath = "//h2/a/@href")
public class ArticleList extends PageModel {




    public static void main(String[] args){
        Site site = Site.me().setRetryTimes(5).setTimeOut(10000).setDomain("woshipm.com");
        ModelSpider modelSpider = ModelSpider.create(site, new ArticleList());
        modelSpider.scheduler(new RedisScheduler("127.0.0.1"))
                .addPipeline(new ConsolePipeline());
        for(int i=1; i<=1; i++) {
            modelSpider.addUrl("http://www.woshipm.com/page/1");
        }
        modelSpider.thread(1).run();
    }
}
