package douguo;

import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.model.annotation.ExtractBy;
import us.codecraft.webmagic.model.annotation.ExtractByUrl;
import us.codecraft.webmagic.modelSpider.ModelSpider;
import us.codecraft.webmagic.modelSpider.PageModel;
import us.codecraft.webmagic.modelSpider.annotation.ExpandFieldValues;
import us.codecraft.webmagic.modelSpider.annotation.FieldType;
import us.codecraft.webmagic.modelSpider.pipeline.ConsoleModelSpiderPipeline;
import us.codecraft.webmagic.modelSpider.pipeline.DownloadRawPipeline;
import us.codecraft.webmagic.modelSpider.pipeline.MysqlPipeline;
import us.codecraft.webmagic.scheduler.RedisScheduler;
import us.codecraft.webmagic.utils.FileUtils;

import java.util.List;

/**
 * Created by cano on 2015/5/28.
 * 获得豆果里面的菜单列表里面的菜单链接
 */

@ExpandFieldValues
public class DouguocaidanUrls extends PageModel {

    @ExtractByUrl(regrex = "")
    private String pageUrl;

    @ExtractBy(value = "//*[@id=\"main\"]//h3/a/@href", multi = true)
    @FieldType(type = FieldType.Type.TEXT)
    private List<String> url;

    public DouguocaidanUrls() {
    }

    public static void main(String[] args){
        Site site = Site.me().setRetryTimes(5).setTimeOut(100000).setCycleRetryTimes(5).setDeepFirst(false)
                .setDomain("douguo.com").addHeader("Referer","http://www.douguo.com/");
        System.out.println(site);

        ModelSpider modelSpider = ModelSpider.create(site, new DouguocaidanUrls());
        modelSpider.scheduler(new RedisScheduler("127.0.0.1", site).setStartOver(true))
                .addPipeline(new MysqlPipeline().setShouldResetDb(false))
                .addPipeline(new DownloadRawPipeline("D:/software/redis/data/rawfile/"))
                .addPipeline(new ConsoleModelSpiderPipeline());


        List<String> urls = FileUtils.getUrlsFromFile("D:\\software\\redis\\data\\douguocaipucaidanmulu.txt");
        modelSpider.addUrls(urls);

        modelSpider.thread(20).run();
    }
}
