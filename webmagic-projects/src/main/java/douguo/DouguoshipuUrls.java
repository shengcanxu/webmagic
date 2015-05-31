package douguo;

import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.model.annotation.ExtractByUrl;
import us.codecraft.webmagic.modelSpider.ModelSpider;
import us.codecraft.webmagic.modelSpider.PageModel;
import us.codecraft.webmagic.modelSpider.annotation.ParseUrl;
import us.codecraft.webmagic.modelSpider.pipeline.ConsoleModelSpiderPipeline;
import us.codecraft.webmagic.scheduler.RedisScheduler;

/**
 * Created by cano on 2015/5/28.
 */

@ParseUrl(expression = "//*[@id=\"main\"]/div[@class=\"sortf\"]//ul/li/a/@href", nextPageRegion = "")
@ParseUrl(expression = "//*[@id=\"container\"]//h3/a/@href", nextPageRegion = "//*[@id=\"main\"]//div[@class=\"pagination\"]//span/")
public class DouguoshipuUrls extends PageModel {


    @ExtractByUrl(regrex = "")
    private String pageUrl;

    public static void main(String[] args){
        Site site = Site.me().setRetryTimes(5).setTimeOut(10000).setCycleRetryTimes(5).setDeepFirst(true).setMaxDeep(2)
                .setDomain("douguo.com").addHeader("Referer","http://www.douguo.com/");
        System.out.println(site);

        ModelSpider modelSpider = ModelSpider.create(site, new DouguoshipuUrls());
        modelSpider.scheduler(new RedisScheduler("127.0.0.1",site).setStartOver(false))
                //.addPipeline(new MysqlPipeline().setShouldResetDb(true))
                .addPipeline(new ConsoleModelSpiderPipeline());
        modelSpider.addUrl("http://www.douguo.com/caipu/fenlei");

        modelSpider.thread(20).run();
    }
}
