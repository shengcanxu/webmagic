package us.codecraft.webmagic.modelSpider;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.modelSpider.extractors.FieldValueExtractor;
import us.codecraft.webmagic.modelSpider.extractors.ParseUrlExtractor;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.List;

/**
 * The extension to PageProcessor for page model extractor.
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.2.0
 */
public class ModelSpiderProcessor implements PageProcessor {

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
        int depth = page.getDepth();
        List<ParseUrlExtractor> linkExtractors = pageModel.getLinkExtractors();
        if ( depth < linkExtractors.size()){
            Object linkExtractor = linkExtractors.get(depth);
            ParseUrlExtractor parseUrlExtractor = (ParseUrlExtractor) linkExtractor;
            List<String> links = parseUrlExtractor.extract(page);
            for(String link : links){
                page.addTargetRequest(new Request(link));
                System.out.println(link);
            }

            //next page links
            String nextPageLink = parseUrlExtractor.extractNextPageLinks(page);
            if(nextPageLink != null){
                page.addNextPageRequest(new Request(nextPageLink));
            }

            page.getResultItems().setSkip(true);
            return;

        }else{ // parse content
            for (FieldValueExtractor extractor : pageModel.getFieldExtractors()){
                String fieldValue = extractor.extract();
                page.putField(extractor.getName(), fieldValue);
            }
        }
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
