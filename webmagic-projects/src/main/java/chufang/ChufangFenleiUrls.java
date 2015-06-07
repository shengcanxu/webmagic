package chufang;

import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.model.annotation.ExtractBy;
import us.codecraft.webmagic.model.annotation.ExtractByUrl;
import us.codecraft.webmagic.modelSpider.ModelSpider;
import us.codecraft.webmagic.modelSpider.PageModel;
import us.codecraft.webmagic.modelSpider.annotation.ExpandFieldValues;
import us.codecraft.webmagic.modelSpider.pipeline.ConsoleModelSpiderPipeline;
import us.codecraft.webmagic.modelSpider.pipeline.MysqlPipeline;
import us.codecraft.webmagic.scheduler.RedisScheduler;

import java.util.List;

/**
 * Created by cano on 2015/5/28.
 * 获得下厨房里面的分类的连接列表
 */

@ExpandFieldValues
public class ChufangFenleiUrls extends PageModel {

    @ExtractByUrl(regrex = "")
    private String pageUrl;

    @ExtractBy(value = "//div[@class=\"page-container\"]//li/a/@href", multi = true)
    private List<String> url;

    public ChufangFenleiUrls() {
    }

    public static void main(String[] args){
        Site site = Site.me().setRetryTimes(5).setTimeOut(100000).setCycleRetryTimes(5).setDeepFirst(false)
                .setDomain("xiachufang.com").addHeader("Referer","http://www.xiachufang.com/");
        System.out.println(site);

        ModelSpider modelSpider = ModelSpider.create(site, new ChufangFenleiUrls());
        modelSpider.scheduler(new RedisScheduler("127.0.0.1", site).setStartOver(true))
                .addPipeline(new MysqlPipeline().setShouldResetDb(true))
                .addPipeline(new ConsoleModelSpiderPipeline());

        modelSpider.addUrl("http://www.xiachufang.com/category/");

        modelSpider.thread(1).run();
    }
}
