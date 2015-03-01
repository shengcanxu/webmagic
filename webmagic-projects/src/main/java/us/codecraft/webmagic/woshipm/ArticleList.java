package us.codecraft.webmagic.woshipm;

import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.model.annotation.ExtractBy;
import us.codecraft.webmagic.model.annotation.ExtractByUrl;
import us.codecraft.webmagic.modelSpider.ModelSpider;
import us.codecraft.webmagic.modelSpider.PageModel;
import us.codecraft.webmagic.modelSpider.annotation.ExtractByParseUrl;
import us.codecraft.webmagic.modelSpider.annotation.ParseUrl;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.scheduler.RedisScheduler;

import java.util.List;

/**
 * Created by cano on 2015/2/19.
 */

@ParseUrl(xpath = "//*[@id=\"ajaxContent\"]/div",
        subXpath = "//h2/a/@href")
public class ArticleList extends PageModel {

    @ExtractByParseUrl(xpath = "//span[@class=\"f_c_tag\"]/a",depth = 1)
    private List<String> tags;

    @ExtractByParseUrl(xpath = "//span[@class=\"f_c_view\"]/i/text()",depth = 1)
    private String readNum;

    @ExtractByParseUrl(xpath = "//p[@class=\"f_content\"]/text()", depth = 1)
    private String summary;

    @ExtractBy(value = "//h1/text()")
    private String title;

    @ExtractBy(value = "//div[@class=\"con_tit_info\"]/p/span[@class=\"con_t_time\"]/text()")
    private String updateTime;

    @ExtractBy(value = "//div[@class=\"con_tit_info\"]/p/span/a[@rel=\"author\"]/text()")
    private String author;

    @ExtractBy(value = "//div[@class=\"con_tit_info\"]/p/span/a[@rel=\"category tag\"]/text()")
    private String category;

    @ExtractBy(value = "//div[@class=\"content_box\"]/div[@class=\"con_txt clx\"]/blockquote/p/text()")
    private String daodu;

//    @ExtractBy(value = "//div[@class=\"content_box\"]/div[@class=\"con_txt clx\"]")
//    @FieldType(type = FieldType.Type.TEXT)
//    private String content;

    @ExtractBy(value = "//div[@class=\"content_box\"]/div[@class=\"con_txt clx\"]/div[@class=\"ludouVA\"]//div[@class=\"option hxItem\"]/div[@class=\"count\"]/text()")
    private String haixing;

    @ExtractBy(value = "//div[@class=\"content_box\"]/div[@class=\"con_txt clx\"]/div[@class=\"ludouVA\"]//div[@class=\"option hhkItem\"]/div[@class=\"count\"]/text()")
    private String haokan;

    @ExtractBy(value = "//div[@class=\"content_box\"]/div[@class=\"con_txt clx\"]/div[@class=\"ludouVA\"]//div[@class=\"option bhkItem\"]/div[@class=\"count\"]/text()")
    private String buhaokan;

    @ExtractByUrl(regrex = "")
    private String pageUrl;


    public static void main(String[] args){
        Site site = Site.me().setRetryTimes(5).setTimeOut(1000).setCycleRetryTimes(5)
                .setDomain("woshipm.com").addHeader("Referer","http://www.woshipm.com/");
        System.out.println(site);

        ModelSpider modelSpider = ModelSpider.create(site, new ArticleList());
        modelSpider.scheduler(new RedisScheduler("127.0.0.1").setStartOver(true))
                //.addPipeline(new MysqlPipeline())
                .addPipeline(new ConsolePipeline());
        for(int i=1; i<=1; i++) {
            modelSpider.addUrlPost("http://www.woshipm.com/page/" + i);
        }
        modelSpider.thread(1).run();
    }
}
