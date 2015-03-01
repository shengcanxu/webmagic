package us.codecraft.webmagic.modelSpider.extractors;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.modelSpider.annotation.ParseUrl;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selector;
import us.codecraft.webmagic.selector.XpathSelector;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by cano on 2015/2/7.
 */
public class ParseUrlExtractor {
    public static final String CONTENT_NAME = "content";

    protected Class clazz;

    protected Selector selector;

    protected Selector subSelector;

    protected Selector nextPageRegion;

    protected Pattern nextPageLinkPattern;

    protected List<ExtractByParseUrlExtractor> contentExtractors = new ArrayList<>();

    public ParseUrlExtractor(ParseUrl parseUrl, Class clazz){
        String xpathStrings = parseUrl.xpath();
        selector = new XpathSelector(xpathStrings);

        if (!parseUrl.subXpath().equals("")) {
            subSelector = new XpathSelector(parseUrl.subXpath());
        }
        if(!parseUrl.nextPageRegion().equals("")) {
            nextPageRegion = new XpathSelector(parseUrl.nextPageRegion());
        }
        if(!parseUrl.nextPageRegion().equals("")) {
            nextPageLinkPattern = Pattern.compile("(" + parseUrl.nextPageLinkRegex().replace(".", "\\.").replace("*", "[^\"'#]*") + ")");
        }
        this.clazz = clazz;
    }

    /**
     * return the links extract by parseurl extractor
     * @param page
     * @return
     */
    public List<Request> extract(Page page) {
        List<Request> requests = new ArrayList<>();
        if(subSelector == null){
            List<String> links = page.getHtml().selectDocumentForList(selector);
            for(String link : links){
                Request request = new Request(link);

                //add content from upper-level parseurl into requests
                Map<String,String> upperContent = page.getRequest().getContents();
                if(upperContent != null) {
                    request.addContents(upperContent);
                }

                requests.add(request);
            }
            return requests;

        }else{
            List<String> regions = page.getHtml().selectDocumentForList(selector);
            for(String region : regions){
                Html html = new Html(region);
                String link = html.selectDocument(subSelector);
                Request request = new Request(link);

                //get content in parseurl page and pass to the pages in next level (depth)
                for (ExtractByParseUrlExtractor contentExtractor : contentExtractors) {
                    Map<String, String> contentMap = contentExtractor.extract(html);
                    request.addContents(contentMap);
                }

                //add content from upper-level parseurl into requests
                Map<String,String> upperContent = page.getRequest().getContents();
                if(upperContent != null) {
                    request.addContents(upperContent);
                }

                requests.add(request);
            }
            return requests;
        }
    }

    /**
     * return the next page link or null if not any
     * @param page
     * @return
     */
    public List<String> extractNextPageLinks(Page page){
        if(nextPageRegion != null){
            List<String> links = page.getHtml().selectList(nextPageRegion).links().all();
            if(nextPageLinkPattern == null){
                Iterator<String> iterator = links.iterator();
                while(iterator.hasNext()){
                    String link = iterator.next();
                    Matcher matcher = nextPageLinkPattern.matcher(link);
                    if(!matcher.find()){
                        iterator.remove();
                    }
                }
            }
            return links;
        }
        return null;
    }

    public Selector getSelector() {
        return selector;
    }

    public void addExtractorByParseUrlExtractor(ExtractByParseUrlExtractor extractByParseUrlExtractor){
        contentExtractors.add(extractByParseUrlExtractor);
    }
}
