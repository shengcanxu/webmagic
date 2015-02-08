package us.codecraft.webmagic.modelSpider.extractors;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.model.annotation.ParseUrl;
import us.codecraft.webmagic.selector.Selector;
import us.codecraft.webmagic.selector.XpathSelector;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by cano on 2015/2/7.
 */
public class ParseUrlExtractor {

    protected Class clazz;

    protected Pattern[] urlPatterns;

    protected Selector sourceRegion;

    protected Selector nextPageRegion;

    public ParseUrlExtractor(ParseUrl parseUrl, Class clazz){
        String[] patternStrings = parseUrl.urlPattern();
        urlPatterns = new Pattern[patternStrings.length];
        for(int i=0; i<patternStrings.length; i++){
            urlPatterns[i] = Pattern.compile("(" + patternStrings[i].replace(".", "\\.").replace("*", "[^\"'#]*") + ")");
        }

        if (!parseUrl.sourceRegion().equals("")) {
            sourceRegion = new XpathSelector(parseUrl.sourceRegion());
        }
        if(!parseUrl.nextPageRegion().equals("")) {
            nextPageRegion = new XpathSelector(parseUrl.nextPageRegion());
        }
        this.clazz = clazz;
    }

    /**
     * return the links extract by parseurl extractor
     * @param page
     * @return
     */
    public List<String> extract(Page page) {
        if(urlPatterns.length == 0) return new ArrayList<>();

        List<String> links;
        if(sourceRegion == null){
            links = page.getHtml().links().all();
        }else{
            links = page.getHtml().selectList(sourceRegion).links().all();
        }

        List<String> matchLinks = new ArrayList<>();
        for(String link : links){
            for(int i=0; i<urlPatterns.length; i++){
                Matcher matcher = urlPatterns[i].matcher(link);
                if(matcher.find()){
                    matchLinks.add(matcher.group(1));
                }
            }
        }
        return matchLinks;
    }

    /**
     * return the next page link or null if not any
     * @param page
     * @return
     */
    public String extractNextPageLinks(Page page){
        if(nextPageRegion != null){
            return page.getHtml().selectList(nextPageRegion).links().get();

        }
        return null;
    }
}
