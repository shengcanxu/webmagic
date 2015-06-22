package chufang;

import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.model.annotation.ExtractByUrl;
import us.codecraft.webmagic.modelSpider.ModelSpider;
import us.codecraft.webmagic.modelSpider.PageModel;
import us.codecraft.webmagic.modelSpider.annotation.ParseUrl;
import us.codecraft.webmagic.modelSpider.pipeline.ConsoleModelSpiderPipeline;
import us.codecraft.webmagic.modelSpider.pipeline.MysqlPipeline;
import us.codecraft.webmagic.scheduler.RedisScheduler;
import us.codecraft.webmagic.utils.file.FileUtils;

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
        if(false){
            FileUtils.getFromFileToRedis("D:\\software\\redis\\data\\categoryurls.txt", "ChufangCaipuUrlsWithProxy", true);
        }else {

            Site site = Site.me().setRetryTimes(5).setTimeOut(10000).setCycleRetryTimes(5).setDeepFirst(true).setGetContent(true)
                    .setDomain("xiachufang.com").addHeader("Referer", "http://www.xiachufang.com/");

            List<String[]> httpProxyList = FileUtils.getProxyFromFile("D:\\software\\redis\\data\\proxies.txt");
            site.setHttpProxyPool(httpProxyList);
            System.out.println(site);

            ModelSpider modelSpider = ModelSpider.create(site, new ChufangCaipuUrlsWithProxy());
            modelSpider.scheduler(new RedisScheduler("127.0.0.1", site).setStartOver(false))
                    .addPipeline(new MysqlPipeline().setShouldResetDb(false))
                    .addPipeline(new ConsoleModelSpiderPipeline());

            modelSpider.thread(20).run();

        }
    }
}

