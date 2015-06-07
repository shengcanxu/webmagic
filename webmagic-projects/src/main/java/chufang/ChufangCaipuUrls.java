package chufang;

import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.model.annotation.ExtractByUrl;
import us.codecraft.webmagic.modelSpider.ModelSpider;
import us.codecraft.webmagic.modelSpider.PageModel;
import us.codecraft.webmagic.modelSpider.annotation.ParseUrl;
import us.codecraft.webmagic.modelSpider.pipeline.ConsoleModelSpiderPipeline;
import us.codecraft.webmagic.modelSpider.pipeline.MysqlPipeline;
import us.codecraft.webmagic.scheduler.RedisScheduler;

/**
 * Created by cano on 2015/5/28.
 * 获得下厨房里面的菜谱连接列表
 */


@ParseUrl(expression = "//div[@class=\"normal-recipe-list\"]/ul/li//p[@class=\"name\"]/a/@href", nextPageRegion = "//div[@class=\"pager\"]")
public class ChufangCaipuUrls extends PageModel {


    @ExtractByUrl(regrex = "")
    private String pageUrl;

    public static void main(String[] args){
        //FileUtils.getFromFileToRedis("D:\\software\\redis\\data\\categoryurls.txt", "ChufangCaipuUrls", true);

        Site site = Site.me().setRetryTimes(5).setTimeOut(10000).setCycleRetryTimes(5).setDeepFirst(true).setGetContent(false)
                .setDomain("xiachufang.com").addHeader("Referer","http://www.xiachufang.com/");
        System.out.println(site);

        ModelSpider modelSpider = ModelSpider.create(site, new ChufangCaipuUrls());
        modelSpider.scheduler(new RedisScheduler("127.0.0.1", site).setStartOver(false))
                .addPipeline(new MysqlPipeline().setShouldResetDb(true))
                .addPipeline(new ConsoleModelSpiderPipeline());

        modelSpider.thread(2000000).run();
    }
}

