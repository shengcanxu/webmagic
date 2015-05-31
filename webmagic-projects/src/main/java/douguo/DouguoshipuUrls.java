package douguo;

import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.model.annotation.ExtractBy;
import us.codecraft.webmagic.model.annotation.ExtractByUrl;
import us.codecraft.webmagic.modelSpider.ModelSpider;
import us.codecraft.webmagic.modelSpider.PageModel;
import us.codecraft.webmagic.modelSpider.annotation.ExpandFieldValues;
import us.codecraft.webmagic.modelSpider.pipeline.ConsoleModelSpiderPipeline;
import us.codecraft.webmagic.modelSpider.pipeline.MysqlPipeline;
import us.codecraft.webmagic.scheduler.RedisScheduler;

/**
 * Created by cano on 2015/5/28.
 */

@ExpandFieldValues
public class DouguoshipuUrls extends PageModel {

    @ExtractByUrl(regrex = "")
    private String pageUrl;

    @ExtractBy(value = "//*[@id=\"main\"]//h1/text()")
    private String category;

    @ExtractBy(value = "//*[@id=\"container\"]//h3/a/@href", multi = true)
    private String url;

    public DouguoshipuUrls() {
    }

    public static void main(String[] args){
        Site site = Site.me().setRetryTimes(5).setTimeOut(10000).setCycleRetryTimes(5).setDeepFirst(false)
                .setDomain("douguo.com").addHeader("Referer","http://www.douguo.com/");
        System.out.println(site);

        ModelSpider modelSpider = ModelSpider.create(site, new DouguoshipuUrls());
        modelSpider.scheduler(new RedisScheduler("127.0.0.1", site).setStartOver(false))
                .addPipeline(new MysqlPipeline().setShouldResetDb(true))
                .addPipeline(new ConsoleModelSpiderPipeline());

//        List<String> urls = FileUtils.getUrlsFromFile("D:\\software\\redis\\data\\douguocaipumulu.txt");
//        modelSpider.addUrls(urls);

        modelSpider.thread(1).run();
    }
}
