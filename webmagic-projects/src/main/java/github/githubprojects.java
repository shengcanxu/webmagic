package github;

import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.model.annotation.ExtractByUrl;
import us.codecraft.webmagic.modelSpider.ModelSpider;
import us.codecraft.webmagic.modelSpider.PageModel;
import us.codecraft.webmagic.modelSpider.annotation.ParseUrl;
import us.codecraft.webmagic.modelSpider.pipeline.ConsoleModelSpiderPipeline;
import us.codecraft.webmagic.scheduler.RedisScheduler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by cano on 2015/2/19.
 */

@ParseUrl(express = "\"login\":\"[^\"]*\"", type = ParseUrl.Type.Regex, customFunction = "getUrl")
public class githubprojects extends PageModel {

    @ExtractByUrl(regrex = "")
    private String pageUrl;

    public String getUrl(String value){
        Pattern regex = Pattern.compile("\"login\":\"([^\"]*)\"");
        Matcher matcher = regex.matcher(value);
        String author =  matcher.replaceAll("$1");
        return "https://github.com/" + author + "?tab=repositories";
    }


    public static void main(String[] args){
        Site site = Site.me().setRetryTimes(5).setTimeOut(10000).setCycleRetryTimes(5)
                .setDomain("github.com").addHeader("Referer","https://www.github.com/");
        System.out.println(site);

        ModelSpider modelSpider = ModelSpider.create(site, new githubprojects());
        modelSpider.scheduler(new RedisScheduler("127.0.0.1").setStartOver(true))
                //.addPipeline(new MysqlPipeline().setShouldResetDb(true))
                .addPipeline(new ConsoleModelSpiderPipeline())
                .addUrl("https://github.com/fogleman/craft/graphs/contributors-data").thread(1).run();
    }
}
