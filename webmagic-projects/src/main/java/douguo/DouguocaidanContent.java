package douguo;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.model.annotation.ExtractBy;
import us.codecraft.webmagic.model.annotation.ExtractByUrl;
import us.codecraft.webmagic.modelSpider.ModelSpider;
import us.codecraft.webmagic.modelSpider.PageModel;
import us.codecraft.webmagic.modelSpider.annotation.CustomFunction;
import us.codecraft.webmagic.modelSpider.annotation.FieldType;
import us.codecraft.webmagic.modelSpider.pipeline.ConsoleModelSpiderPipeline;
import us.codecraft.webmagic.modelSpider.pipeline.DownloadRawPipeline;
import us.codecraft.webmagic.modelSpider.pipeline.MysqlPipeline;
import us.codecraft.webmagic.scheduler.RedisScheduler;

import java.util.List;

/**
 * Created by cano on 2015/5/28.
 */

public class DouguocaidanContent extends PageModel {


    @ExtractByUrl(regrex = "")
    private String pageUrl;

    @ExtractBy(value = "//*[@id=\"page_cm_id\"]")
    private String title;

    @ExtractBy(value = "//*[@id=\"main\"]//div[@class=\"melef clearfix\"]/span[1]/text()")
    @CustomFunction(name = "removeCreateText")
    private String createDate;

    @ExtractBy(value = "//*[@id=\"main\"]//div[@class=\"melef clearfix\"]/span[2]/text()")
    @CustomFunction(name = "removeUpdateText")
    private String updateDate;

    @ExtractBy(value = "//*[@id=\"main\"]//div[@class=\"melef clearfix\"]/span[3]/text()")
    @FieldType(type = FieldType.Type.INT)
    @CustomFunction(name = "removeReadsText")
    private int reads;

    @ExtractBy(value = "//*[@id=\"main\"]//div[@class=\"melef clearfix\"]/span/font/text()")
    @FieldType(type = FieldType.Type.INT)
    private int souchang;

    @ExtractBy(value = "//*[@id=\"main\"]/div[@class=\"meview\"]//p/text()")
    @FieldType(type = FieldType.Type.TEXT)
    private String description;

    @ExtractBy(value = "//*[@id=\"main\"]/div[@class=\"mecai\"]/div/div/a/@href")
    @FieldType(type = FieldType.Type.TEXT)
    private List<String> caipus;

    public Object removeCreateText(Object value, Page page) {
        if (value instanceof String) {
            String str = (String) value;
            str = str.replace("创建时间：", "").trim();
            return str;
        }
        return value;
    }

    public Object removeUpdateText(Object value, Page page) {
        if (value instanceof String) {
            String str = (String) value;
            str = str.replace("最后更新：", "").trim();
            return str;
        }
        return value;
    }

    public Object removeReadsText(Object value, Page page) {
        if (value instanceof String) {
            String str = (String) value;
            str = str.replace("浏览：", "").trim();
            return str;
        }
        return value;
    }



    public static void main(String[] args){
        //FileUtils.getFromFileToRedis("D:\\software\\redis\\data\\douguocaidanurls.txt", "DouguocaidanContent", true);


        Site site = Site.me().setRetryTimes(5).setTimeOut(10000).setCycleRetryTimes(5).setDeepFirst(false)
                .setDomain("douguo.com").addHeader("Referer","http://www.douguo.com/");
        System.out.println(site);

        ModelSpider modelSpider = ModelSpider.create(site, new DouguocaidanContent());
        modelSpider.scheduler(new RedisScheduler("127.0.0.1", site).setStartOver(false))
                .addPipeline(new MysqlPipeline().setShouldResetDb(false))
                .addPipeline(new DownloadRawPipeline("D:/software/redis/data/caidanrawfile/"))
                .addPipeline(new ConsoleModelSpiderPipeline());

        modelSpider.thread(20).run();
    }
}
