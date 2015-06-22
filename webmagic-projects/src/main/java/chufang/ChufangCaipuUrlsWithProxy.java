package chufang;

import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.model.annotation.ExtractByUrl;
import us.codecraft.webmagic.modelSpider.ModelSpider;
import us.codecraft.webmagic.modelSpider.PageModel;
import us.codecraft.webmagic.modelSpider.annotation.ParseUrl;
import us.codecraft.webmagic.modelSpider.pipeline.ConsoleModelSpiderPipeline;
import us.codecraft.webmagic.modelSpider.pipeline.MysqlPipeline;
import us.codecraft.webmagic.scheduler.RedisScheduler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cano on 2015/5/28.
 * 获得下厨房里面的菜谱连接列表
 */


@ParseUrl(expression = "//div[@class=\"normal-recipe-list\"]/ul/li//p[@class=\"name\"]/a/@href", nextPageRegion = "//div[@class=\"pager\"]")
public class ChufangCaipuUrlsWithProxy extends PageModel {


    @ExtractByUrl(regrex = "")
    private String pageUrl;

    public static void main(String[] args){
        //FileUtils.getFromFileToRedis("D:\\software\\redis\\data\\categoryurls.txt", "ChufangCaipuUrlsWithProxy", true);

        Site site = Site.me().setRetryTimes(5).setTimeOut(10000).setCycleRetryTimes(5).setDeepFirst(true).setGetContent(true)
                .setDomain("xiachufang.com").addHeader("Referer","http://www.xiachufang.com/");

        List<String[]> httpProxyList = new ArrayList<>();
        String[] ips = new String[2];
        ips[0] = "120.203.158.148"; ips[1] = "8118";
        httpProxyList.add(ips);
        String[] ips2 = new String[2];
        ips2[0] = "120.203.148.7"; ips2[1] = "8118";
        httpProxyList.add(ips2);
        site.setHttpProxyPool(httpProxyList);
        System.out.println(site);

        ModelSpider modelSpider = ModelSpider.create(site, new ChufangCaipuUrlsWithProxy());
        modelSpider.scheduler(new RedisScheduler("127.0.0.1", site).setStartOver(false))
                .addPipeline(new MysqlPipeline().setShouldResetDb(false))
                .addPipeline(new ConsoleModelSpiderPipeline());

        modelSpider.thread(1).run();
    }
}

