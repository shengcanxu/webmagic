package us.codecraft.webmagic.modelSpider.extractors;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.modelSpider.PageModel;
import us.codecraft.webmagic.modelSpider.annotation.ParseUrl;
import us.codecraft.webmagic.selector.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

    public static enum Type {XPath, Regex, Css, JsonPath}

    protected Class clazz;

    protected Type type = Type.XPath;

    protected Selector selector;

    protected Selector subSelector;

    protected Selector nextPageRegion;

    protected Pattern nextPageLinkPattern;

    protected String customFunction;

    protected List<ExtractByParseUrlExtractor> contentExtractors = new ArrayList<>();

    public ParseUrlExtractor(ParseUrl parseUrl, Class clazz){
        String express = parseUrl.expression();
        switch(parseUrl.type()){
            case XPath:
                type = Type.XPath;
                selector = new XpathSelector(express);
                break;
            case Regex:
                type = Type.Regex;
                selector = new RegexSelector(express);
                break;
            case JsonPath:
                type = Type.JsonPath;
                selector = new JsonPathSelector(express);
                break;
            case Css:
                type = Type.Css;
                selector = new CssSelector(express);
                break;
            default:
                type = Type.XPath;
                selector = new XpathSelector(express);
        }

        if (!parseUrl.subXpath().equals("")) {
            subSelector = new XpathSelector(parseUrl.subXpath());
        }
        if(!parseUrl.nextPageRegion().equals("")) {
            nextPageRegion = new XpathSelector(parseUrl.nextPageRegion());
        }
        if(!parseUrl.nextPageRegion().equals("")) {
            nextPageLinkPattern = Pattern.compile("(" + parseUrl.nextPageLinkRegex().replace(".", "\\.").replace("*", "[^\"'#]*") + ")");
        }
        if(!parseUrl.customFunction().equals("")){
            customFunction = parseUrl.customFunction();
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

            //call custom function is exists
            if(customFunction != null){
                List<String> newLinks = new ArrayList<>();
                for(String link : links) {
                    String newLink = applyCustomFunction(link, page, customFunction);
                    newLinks.add(newLink);
                }
                links = newLinks;
            }

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

                //call custom function is exists
                if(customFunction != null){
                    link = applyCustomFunction(link,page,customFunction);
                }

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

    public String applyCustomFunction(String url, Page page, String functionStr){
        String newValue = "";
        try {
                PageModel pageModel = (PageModel) page.getPageModel();
                Method customFunction = pageModel.getClass().getMethod(functionStr, String.class);
                newValue = (String) customFunction.invoke(pageModel, url);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return newValue;
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
