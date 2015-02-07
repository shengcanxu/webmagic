package us.codecraft.webmagic.modelSpider;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.model.PageModelExtractor;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selector;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The extension to PageProcessor for page model extractor.
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.2.0
 */
public class ModelSpiderProcessor implements PageProcessor {

    private List<PageModelExtractor> pageModelExtractorList = new ArrayList<PageModelExtractor>();

    private Site site;

    private PageModel pageModel;

    public static ModelSpiderProcessor create(Site site, PageModel pageModel) {
        ModelSpiderProcessor modelPageProcessor = new ModelSpiderProcessor(site);
        modelPageProcessor.setPageModel(pageModel);

        //create the page Model
        pageModel.createModel();
        return modelPageProcessor;
    }


    private ModelSpiderProcessor(Site site) {
        this.site = site;
    }

    @Override
    public void process(Page page) {
        for (PageModelExtractor pageModelExtractor : pageModelExtractorList) {
            //process parseurls, if urls found, skip extract content
            int depth = page.getDepth();
            if( depth < pageModelExtractor.getParseUrlDepth() &&
                    extractParseUrls(page,pageModelExtractor.getParseUrlRegionSelector(depth),pageModelExtractor.getParseUrlPatterns(depth),pageModelExtractor.getParseUrlNextPageSelectors(depth))){
                page.getResultItems().setSkip(true);
                continue;
            }

            Object process = pageModelExtractor.process(page);
            if (process == null || (process instanceof List && ((List) process).size() == 0)) {
                continue;
            }
            postProcessPageModel(pageModelExtractor.getClazz(), process);
            page.putField(pageModelExtractor.getClazz().getCanonicalName(), process);
        }
    }

    /**
     * extract parseurls from page
     * @param page
     * @param urlRegionSelector
     * @param urlPatterns
     * @return true if any url is extracted, else false;
     */
    private boolean extractParseUrls(Page page, Selector urlRegionSelector, Pattern[] urlPatterns, Selector nextPageSelector){
        if(urlPatterns.length == 0) return false;

        List<String> links;
        if(urlRegionSelector == null){
            links = page.getHtml().links().all();
        }else{
            links = page.getHtml().selectList(urlRegionSelector).links().all();
        }

        boolean found = false;
        for(String link : links){
            for(int i=0; i<urlPatterns.length; i++){
                Matcher matcher = urlPatterns[i].matcher(link);
                if(matcher.find()){
                    found = true;
                    page.addTargetRequest(new Request(matcher.group(1)));
                }
            }
        }

        //next page
        if(nextPageSelector != null){
            String link = page.getHtml().selectList(nextPageSelector).links().get();
            page.addNextPageRequest(new Request(link));
        }

        return found;
    }

    protected void postProcessPageModel(Class clazz, Object object) {
    }

    @Override
    public Site getSite() {
        return site;
    }

    public PageModel getPageModel() {
        return pageModel;
    }

    public void setPageModel(PageModel pageModel) {
        this.pageModel = pageModel;
    }
}
