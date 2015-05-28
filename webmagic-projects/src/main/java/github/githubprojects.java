package github;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.model.annotation.ExtractBy;
import us.codecraft.webmagic.model.annotation.ExtractByUrl;
import us.codecraft.webmagic.modelSpider.ModelSpider;
import us.codecraft.webmagic.modelSpider.PageModel;
import us.codecraft.webmagic.modelSpider.annotation.CustomFunction;
import us.codecraft.webmagic.modelSpider.annotation.ExpandFieldValues;
import us.codecraft.webmagic.modelSpider.annotation.ParseUrl;
import us.codecraft.webmagic.modelSpider.pipeline.ConsoleModelSpiderPipeline;
import us.codecraft.webmagic.scheduler.RedisScheduler;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by cano on 2015/2/19.
 */

@ParseUrl(expression = "\"login\":\"[^\"]*\"", type = ParseUrl.Type.Regex, customFunction = "getUrl")
@ExpandFieldValues
public class githubprojects extends PageModel {

    @ExtractBy(value = "//*div[@class=\"repo-tab\"]/ul/li/h3/a/text()")
    @CustomFunction(name = "addRequest")
    private List<String> projectNames;

    @ExtractBy(value = "//*div[@class=\"repo-tab\"]/ul/li/div[@class=\"repo-list-stats\"]/text()")
    private List<String> programLanguages;

    @ExtractBy(value = "//*div[@class=\"repo-tab\"]/ul/li/div[@class=\"repo-list-stats\"]/a[1]/text()")
    private List<String> stars;

    @ExtractBy(value = "//*div[@class=\"repo-tab\"]/ul/li/div[@class=\"repo-list-stats\"]/a[2]/text()")
    private List<String> forks;

    @ExtractByUrl(regrex = "")
    private String pageUrl;

    public String getUrl(String value){
        Pattern regex = Pattern.compile("\"login\":\"([^\"]*)\"");
        Matcher matcher = regex.matcher(value);
        String author =  matcher.replaceAll("$1");
        return "https://github.com/" + author + "?tab=repositories";
    }

    public String addRequest(String projectName,Page page){
        String author = page.getUrl().toString();
        author = author.replace("https://github.com/", "");
        author = author.replace("?tab=repositories","");
        String newUrl = "https://github.com/" + author.trim() + "/" + projectName.trim() + "/graphs/contributors-data";

        //add new request
        page.addTargetRequest(new Request(newUrl));

        return projectName;
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
