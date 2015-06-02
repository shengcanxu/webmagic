package douguo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.model.annotation.ExtractBy;
import us.codecraft.webmagic.model.annotation.ExtractByUrl;
import us.codecraft.webmagic.modelSpider.ModelSpider;
import us.codecraft.webmagic.modelSpider.PageModel;
import us.codecraft.webmagic.modelSpider.annotation.CustomFunction;
import us.codecraft.webmagic.modelSpider.annotation.DownloadFile;
import us.codecraft.webmagic.modelSpider.annotation.FieldType;
import us.codecraft.webmagic.modelSpider.annotation.TextFormatter;
import us.codecraft.webmagic.modelSpider.pipeline.ConsoleModelSpiderPipeline;
import us.codecraft.webmagic.modelSpider.pipeline.DownloadRawPipeline;
import us.codecraft.webmagic.modelSpider.pipeline.MysqlPipeline;
import us.codecraft.webmagic.scheduler.RedisScheduler;
import us.codecraft.webmagic.utils.FileUtils;
import us.codecraft.xsoup.Xsoup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cano on 2015/5/28.
 */

public class DouguoshipuContent extends PageModel {


    @ExtractByUrl(regrex = "")
    private String pageUrl;

    @ExtractBy(value = "//*[@id=\"main\"]//div[@class=\"bokpic\"]//a/@href")
    @DownloadFile(savepath = "D:/software/redis/data/pictures/", type = DownloadFile.Type.PICTURE, inSeperateFolder = true)
    private String picutre;

    @ExtractBy(value = "//*[@id=\"main\"]//div[@class=\"falisc mbm mb40\"]/span[1]/text()")
    @FieldType(type = FieldType.Type.INT)
    @TextFormatter(types = TextFormatter.Type.TRIM)
    private int reads;

    @ExtractBy(value = "//*[@id=\"collectsnum\"]/text()")
    @FieldType(type = FieldType.Type.INT)
    @TextFormatter(types = TextFormatter.Type.TRIM)
    private int souchang;

    @ExtractBy(value = "//*[@id=\"main\"]//div[@class=\"falisc mbm mb40\"]/span[@class=\"fcc\"]/text()")
    @TextFormatter(types = TextFormatter.Type.TRIM)
    private String updateTime;

    //@ExtractBy(value = "//*[@id=\"main\"]//div[@class=\"xtip\"]/text()")
    @ExtractBy(value = "//*[@id=\"fullStory\"]/text()|//*[@id=\"main\"]//div[@class=\"xtip\"]/text()")
    @TextFormatter(types = TextFormatter.Type.TRIM)
    @FieldType(type = FieldType.Type.TEXT)
    private String tips;

    @ExtractBy(value = "//*[@id=\"main\"]//table[@class=\"retamr\"]/tbody/tr[@class=\"mtim\"][1]/td[1]/text()")
    @TextFormatter(types = TextFormatter.Type.TRIM)
    private String difficulty;

    @ExtractBy(value = "//*[@id=\"main\"]//table[@class=\"retamr\"]/tbody/tr[@class=\"mtim\"][1]/td[2]/text()")
    @TextFormatter(types = TextFormatter.Type.TRIM)
    private String last;

    @ExtractBy(value = "//*[@id=\"main\"]//table[@class=\"retamr\"]/tbody/tr/td/html()")
    @CustomFunction(name = "getZhuLiao")
    @FieldType(type = FieldType.Type.TEXT)
    private List<String> zhuliao;

    @ExtractBy(value = "//*[@id=\"main\"]//table[@class=\"retamr\"]/tbody/tr/td/html()")
    @CustomFunction(name = "getFuLiao")
    @FieldType(type = FieldType.Type.TEXT)
    private List<String> fuliao;

    @ExtractBy(value = "//*[@id=\"main\"]//div[@class=\"xtieshi\"]/p/html()")
    @FieldType(type = FieldType.Type.TEXT)
    private List<String> xiaotieshi;

    @ExtractBy(value = "//*[@id=\"displaytag\"]//a[@class=\"btnta\"]/text()")
    private List<String> tags;

    @ExtractBy(value = "//h3[@class=\"mb15 fwb\"]/a/text()")
    private String zuopinliang;

    @ExtractBy(value = "//*[@id=\"main\"]//div[@class=\"step clearfix\"]/div/p/html()")
    @FieldType(type = FieldType.Type.TEXT)
    private List<String> stepContent;

    @ExtractBy(value = "//*[@id=\"main\"]//div[@class=\"step clearfix\"]/div/html()")
    @CustomFunction(name = "getStepImages")
    @DownloadFile(savepath = "D:/software/redis/data/pictures/", type = DownloadFile.Type.PICTURE, inSeperateFolder = true)
    @FieldType(type = FieldType.Type.TEXT)
    private List<String> stepImage;

    @ExtractBy(value = "//*[@id=\"main\"]/div[@class=\"reright\"]//h4/a[1]/text()")
    private String author;

    @ExtractBy(value = "//*[@id=\"main\"]/div[@class=\"reright\"]//h4/a[1]/@href")
    private String authorLink;

    public Object getStepImages(Object value, Page page){
        List<String> result = new ArrayList<>();
        List<String> list;
        if(value instanceof List) {
            list = (List<String>) value;
        }else{
            list = new ArrayList<>();
            list.add((String) value);
        }
        for(int i=0; i<list.size(); i++){
            String str = list.get(i);
            Document document = Jsoup.parse(str);
            String url = Xsoup.compile("//div/a/@href").evaluate(document).get();
            if(url == null){
                result.add("");
            }else{
                result.add(url);
            }
        }
        return result;
    }

    public Object getZhuLiao(Object value, Page page){
        List<String> result = new ArrayList<>();
        List<String> list = (List<String>) value;
        boolean start = false;
        for(int i=0; i<list.size(); i++){
            String str = list.get(i);
            if(!start){
                if(str.contains("主料")){
                    start = true;
                }
            }else{
                if(str.contains("辅料")){
                    break;
                }
                if(str.trim().length() == 0) continue;

                Document document = Jsoup.parse(str);
                String name = Xsoup.compile("//span[@class!=\"right\"]/allText()").evaluate(document).get();
                String liang = Xsoup.compile("//span[@class=\"right\"]/allText()").evaluate(document).get();
                result.add(name + "+" + liang);
            }
        }

        return result;
    }

    public Object getFuLiao(Object value, Page page){
        List<String> result = new ArrayList<>();
        List<String> list = (List<String>) value;
        boolean start = false;
        for(int i=0; i<list.size(); i++){
            String str = list.get(i);
            if(!start){
                if(str.contains("辅料")){
                    start = true;
                }
            }else{
                if(str.trim().length() == 0) continue;

                Document document = Jsoup.parse(str);
                String name = Xsoup.compile("//span[@class!=\"right\"]/allText()").evaluate(document).get();
                String liang = Xsoup.compile("//span[@class=\"right\"]/allText()").evaluate(document).get();
                if(name != null) result.add(name + "+" + liang);
            }
        }

        return result;
    }

    public static void main(String[] args){
        //FileUtils.getFromFileToRedis("D:\\software\\redis\\data\\links.txt", "DouguoshipuContent", true);


        Site site = Site.me().setRetryTimes(5).setTimeOut(10000).setCycleRetryTimes(5).setDeepFirst(false)
                .setDomain("douguo.com").addHeader("Referer","http://www.douguo.com/");
        System.out.println(site);

        ModelSpider modelSpider = ModelSpider.create(site, new DouguoshipuContent());
        modelSpider.scheduler(new RedisScheduler("127.0.0.1", site).setStartOver(false))
                .addPipeline(new MysqlPipeline().setShouldResetDb(false))
                .addPipeline(new DownloadRawPipeline("D:/software/redis/data/contentrawfile/"))
                .addPipeline(new ConsoleModelSpiderPipeline());

        modelSpider.thread(20).run();
    }
}
