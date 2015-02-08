package us.codecraft.webmagic.example;

import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.model.OOSpider;
import us.codecraft.webmagic.model.annotation.ExtractBy;
import us.codecraft.webmagic.model.annotation.ExtractByUrl;
import us.codecraft.webmagic.modelSpider.pipeline.ConsolePageModelPipeline;
import us.codecraft.webmagic.scheduler.StackScheduler;

/**
 * Created by canoxu on 2015/2/1.
 */

public class News163Article {
    @ExtractBy("//div[@id=\"endText\"]")
    private String content;

    @ExtractByUrl(value = "")
    private String PageURL;

    public static void main(String[] args) {
        Site site = Site.me().setTimeOut(10000).setRetryTimes(5).setDomain("163.com");
        OOSpider.create(site, new ConsolePageModelPipeline(), News163Article.class)
                .scheduler(new StackScheduler())
                .addUrl("http://news.163.com/13/0802/05/958I1E330001124J.html").thread(1).run();
    }
}
