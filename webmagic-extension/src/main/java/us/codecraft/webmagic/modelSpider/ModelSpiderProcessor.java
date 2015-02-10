package us.codecraft.webmagic.modelSpider;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.modelSpider.extractors.FieldValueExtractor;
import us.codecraft.webmagic.modelSpider.extractors.ParseUrlExtractor;
import us.codecraft.webmagic.modelSpider.formatter.Formatter;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selector;
import us.codecraft.webmagic.utils.DoubleKeyMap;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The extension to PageProcessor for page model extractor.
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.2.0
 */
public class ModelSpiderProcessor implements PageProcessor {

    private Site site;

    private PageModel pageModel;

    private DoubleKeyMap<String,String,Object> referalExtractedValues = new DoubleKeyMap<>();
    private DoubleKeyMap<String,String,Boolean> pageMap = new DoubleKeyMap<>();

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
        page.setPageModel(pageModel);

        int depth = page.getDepth();
        List<ParseUrlExtractor> linkExtractors = pageModel.getLinkExtractors();
        if ( depth < linkExtractors.size()){
            Object linkExtractor = linkExtractors.get(depth);
            ParseUrlExtractor parseUrlExtractor = (ParseUrlExtractor) linkExtractor;
            List<String> links = parseUrlExtractor.extract(page);
            for(String link : links){
                page.addTargetRequest(new Request(link));
            }

            //next page links
            String nextPageLink = parseUrlExtractor.extractNextPageLinks(page);
            if(nextPageLink != null){
                page.addNextPageRequest(new Request(nextPageLink));
            }

            page.getResultItems().setSkip(true);
            return;

        }else{ // parse content
            if(checkAndAddContentNextPage(page)){
                processFatherPage(page);
            }else if(page.getRequest().hasFatherPage()) {
                getNextPageContent(page);
            }else {
                getNormalPageContent(page);
            }
        }
    }

    /**
     * save the father page before getting all next pages
     * @param page
     */
    private void processFatherPage(Page page){
        String url = page.getUrl().toString();
        for (FieldValueExtractor extractor : pageModel.getFieldExtractors()) {
            List<String> fieldValues = extractor.extract(page);
            String name = extractor.getName();
            if (fieldValues == null) {
                continue;
            } else if (fieldValues.size() == 1) {
                referalExtractedValues.put(url,name,fieldValues.get(0));
            } else {
                referalExtractedValues.put(url,name,fieldValues);
            }
        }
    }

    /**
     * get the next page content
     * @param page
     */
    private void getNextPageContent(Page page){
        String url = page.getUrl().toString();
        for (FieldValueExtractor extractor : pageModel.getFieldExtractors()) {
            List<String> fieldValues = extractor.extract(page);
            String name = extractor.getName();
            if (fieldValues == null) {
                continue;
            } else if (fieldValues.size() == 1) {
                referalExtractedValues.put(url,name,fieldValues.get(0));
            } else {
                referalExtractedValues.put(url,name,fieldValues);
            }
        }

        //set the page Map to mark next page is extracted
        Page fatherPage = page.getRequest().getFatherPage();
        String fatherUrl = fatherPage.getUrl().toString();
        pageMap.put(fatherUrl,url,Boolean.TRUE);

        //check if all next pages are extracted
        boolean allExtracted = true;
        Map<String,Boolean> map = pageMap.get(fatherUrl);
        for(Map.Entry<String, Boolean> entry : map.entrySet()){
            if(entry.getValue() == Boolean.FALSE){
                allExtracted = false;
                break;
            }
        }

        if(allExtracted){
            List<Map<String,Object>> valueList = new ArrayList<>();
            for(Map.Entry<String, Boolean> entry : map.entrySet()) {
                Map<String, Object> extractedValues = referalExtractedValues.get(entry.getKey());
                valueList.add(extractedValues);
            }
            Map<String,Object> combined = combineValues(valueList);
            //TODO: add code for format value and addField

            pageMap.remove(fatherUrl);
        }
    }

    private Map<String,Object> combineValues(List<Map<String,Object>> valueList){
        if(valueList.size() == 0) return null;

        Field field = pageModel.getNextPageField();
        String combineFieldName = field.getName();
        if(List.class.isAssignableFrom(field.getType())){
            List combinedList = new ArrayList<>();
            for(Map<String,Object> value : valueList){
                Object o = value.get(combineFieldName);
                if(o != null){
                    combinedList.addAll((List) o);
                }
            }

            Map<String,Object> combinedValue = valueList.get(0);
            combinedValue.put(combineFieldName, combinedList);
            return combinedValue;

        }else{
            String combinedString = "";
            for(Map<String, Object> value : valueList){
                Object o = value.get(combineFieldName);
                if(o!= null){
                    combinedString = combinedString + o;
                }
            }

            Map<String,Object> combinedValue = valueList.get(0);
            combinedValue.put(combineFieldName, combinedString);
            return combinedValue;
        }
    }

    /**
     * get page content, page is normal, not contains next page or not the next page
     * @param page
     */
    private void getNormalPageContent(Page page){
        for (FieldValueExtractor extractor : pageModel.getFieldExtractors()) {
            List<String> fieldValues = extractor.extract(page);

            //do formatter
            String name = extractor.getName();
            List<Formatter> formatters = pageModel.getFormatterMap().get(name);
            if (formatters != null) {
                for (Formatter formatter : formatters) {
                    fieldValues = formatter.format(fieldValues);
                }
            }

            if (fieldValues == null) {
                page.putField(name, "");
            } else if (fieldValues.size() == 1) {
                page.putField(name, fieldValues.get(0));
            } else {
                page.putField(name, fieldValues);
            }
        }
    }

    /**
     * join content with "nextpage" settings
     * @param page
     * @return
     */
    private boolean checkAndAddContentNextPage(Page page){
        Selector nextPageSelector = pageModel.getNextPageSelector();
        if (nextPageSelector != null) {
            List<String> nextPageLinks = page.getHtml().selectDocumentForList(nextPageSelector);
            if(nextPageLinks != null && nextPageLinks.size() != 0){
                String url = page.getUrl().toString();
                for (String link : nextPageLinks){
                    page.addContentNextPageRequest(new Request(link),page);
                    pageMap.put(url,link,Boolean.FALSE);
                }
                return true;
            }
        }
        return false;
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
