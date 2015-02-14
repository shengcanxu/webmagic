package us.codecraft.webmagic.modelSpider.pipeline;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.modelSpider.PageModel;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.selector.Selector;
import us.codecraft.webmagic.utils.DoubleKeyMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Print page model in console.<br>
 * Usually used in test.<br>
 * @author code4crafter@gmail.com <br>
 * @since 0.2.0
 */
public class MultiplePagesPipeline implements Pipeline {

    //<fatherpageurl, currentpageurl,extractedvalues>
    private DoubleKeyMap<String,String,ResultItems> referalExtractedValues = new DoubleKeyMap<>();
    // <fatherpageurl, currentpagerul, ifcurrentpageisparsed>
    private DoubleKeyMap<String,String,Boolean> pageMap = new DoubleKeyMap<>();
    //page orders
    private List<String> pageOrderedUrls = new ArrayList<>();

    @Override
    public void process(ResultItems resultItems, Task task) {
        if(resultItems.isSkip()) return;

        PageModel pageModel = (PageModel)resultItems.getPageModel();
        Selector multiPagesSelector = pageModel.getMultiPageSelector();
        if(multiPagesSelector == null) return;

        //popup requests for other pages
        Page page = resultItems.getPage();
        List<String> multiPageUrls = page.getHtml().selectList(multiPagesSelector).all();
        String currentUrl = page.getUrl().toString();
        String fatherUrl = resultItems.getRequest().hasFatherPage() ? resultItems.getRequest().getFatherPage().getUrl().toString() : currentUrl;
        if(multiPageUrls != null && multiPageUrls.size() != 0){
            for(String link : multiPageUrls){
                if(pageMap.get(fatherUrl,link) == null) {
                    page.addContentNextPageRequest(new Request(link), page);
                    pageMap.put(fatherUrl, link, Boolean.FALSE);
                    pageOrderedUrls.add(link);
                }
            }
        }

        //referal extract values
        referalExtractedValues.put(fatherUrl,currentUrl,resultItems);
        pageMap.put(fatherUrl,currentUrl,Boolean.TRUE);

        //check if all multiple pages are extracted
        Map<String,Boolean> boolMap = pageMap.get(fatherUrl);
        boolean allExtracted = true;
        for(Map.Entry<String, Boolean> entry : boolMap.entrySet()){
            if(entry.getValue() == Boolean.FALSE){
                allExtracted = false;
                break;
            }
        }

        //combine multiple pages' values
        if(allExtracted){
            String fieldName = pageModel.getMultiPageFieldName();
            Map<String,ResultItems> resultItemsMap = referalExtractedValues.get(fatherUrl);
            String value = "";
            for(String link : pageOrderedUrls){
                value = value + (String)resultItemsMap.get(link).get(fieldName);
            }
            resultItems.put(fieldName,value);
        }else{
            resultItems.setSkip(true);
        }
    }
}
