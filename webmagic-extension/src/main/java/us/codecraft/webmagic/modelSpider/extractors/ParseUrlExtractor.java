package us.codecraft.webmagic.modelSpider.extractors;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.modelSpider.annotation.ParseUrl;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selector;
import us.codecraft.webmagic.selector.XpathSelector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cano on 2015/2/7.
 */
public class ParseUrlExtractor {

    protected Class clazz;

    protected Selector selector;

    protected Selector subSelector;

    protected Selector nextPageRegion;

    public ParseUrlExtractor(ParseUrl parseUrl, Class clazz){
        String xpathStrings = parseUrl.value();
        selector = new XpathSelector(xpathStrings);

        if (!parseUrl.subXpath().equals("")) {
            subSelector = new XpathSelector(parseUrl.subXpath());
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
        if(subSelector == null){
            List<String> links = page.getHtml().selectDocumentForList(selector);
            return links;
        }else{
            List<String> regions = page.getHtml().selectDocumentForList(selector);
            List<String> links = new ArrayList<>();
            for(String region : regions){
                Html html = new Html(region);
                String link = html.selectDocument(subSelector);
                links.add(link);
            }
            return links;
        }
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
