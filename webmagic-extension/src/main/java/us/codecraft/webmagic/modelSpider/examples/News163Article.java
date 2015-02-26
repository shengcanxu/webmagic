package us.codecraft.webmagic.modelSpider.examples;

import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.model.annotation.ExtractBy;
import us.codecraft.webmagic.model.annotation.ExtractByUrl;
import us.codecraft.webmagic.modelSpider.ModelSpider;
import us.codecraft.webmagic.modelSpider.PageModel;
import us.codecraft.webmagic.modelSpider.annotation.FieldType;
import us.codecraft.webmagic.modelSpider.annotation.MultiplePagesField;
import us.codecraft.webmagic.modelSpider.annotation.TextFormatter;
import us.codecraft.webmagic.modelSpider.pipeline.MysqlPipeline;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.scheduler.StackScheduler;

/**
 * Created by canoxu on 2015/2/1.
 */

public class News163Article extends PageModel{
    @MultiplePagesField(multiPageRegion = "//*[@id=\"epContentLeft\"]/div[@class=\"ep-pages\"]/a[@class!=\"ep-pages-all\"]/@href")
    @ExtractBy("//div[@id=\"endText\"]")
    @TextFormatter(types = TextFormatter.Type.REMOVETAG)
    @FieldType(type = FieldType.Type.TEXT)
    private String content;

    @ExtractByUrl(regrex = "")
    private String PageURL;

    public static void main(String[] args) {
        Site site = Site.me().setTimeOut(10000).setRetryTimes(5).setDomain("163.com");
        ModelSpider.create(site, new News163Article())
                .scheduler(new StackScheduler())
                //.scheduler(new RedisScheduler("127.0.0.1"))
                .addPipeline(new ConsolePipeline())
                .addPipeline(new MysqlPipeline().setShouldResetDb(true))
                .addUrl("http://news.163.com/13/0802/05/958I1E330001124J.html").thread(1).run();
    }
}
