package us.codecraft.webmagic.modelSpider.extractors;

import us.codecraft.webmagic.model.annotation.ParseUrl;
import us.codecraft.webmagic.selector.Selector;
import us.codecraft.webmagic.selector.XpathSelector;

import java.util.regex.Pattern;

/**
 * Created by cano on 2015/2/7.
 */
public class ParseUrlExtractor implements ModelExtractor {

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


    @Override
    public void extract() {

    }
}
