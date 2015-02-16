package us.codecraft.webmagic.modelSpider.pipeline;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.modelSpider.PageModel;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.selector.Selector;
import us.codecraft.webmagic.utils.DoubleKeyMap;

import java.util.Map;

/**
 * parse subpages and conbine the valueas into one resultitems.<br>
 */
public class SubpagePipeline implements Pipeline {

    //<fatherpageurl, subpageurl,extractedvalues>
    private DoubleKeyMap<String,String,ResultItems> referalExtractedValues = new DoubleKeyMap<>();
    // <fatherpageurl, subpagerul, ifcurrentpageisparsed>
    private DoubleKeyMap<String,String,Boolean> pageMap = new DoubleKeyMap<>();

    @Override
    public void process(ResultItems resultItems, Task task) {
        PageModel pageModel = (PageModel)resultItems.getPageModel();
        if(!pageModel.hasSubpage() && !resultItems.getRequest().isSubPage()) return;

        //popup requests for sub-pages
        Page page = resultItems.getPage();
        Map<String, Selector> subpageMap = pageModel.getSubpageMap();
        String currentUrl = page.getUrl().toString();
        String fatherUrl = resultItems.getRequest().isSubPage() ? resultItems.getRequest().getSubPageFatherPage().getUrl().toString() : currentUrl;
        for(Map.Entry<String, Selector> entry : subpageMap.entrySet()){
            String subpageUrl = page.getHtml().selectList(entry.getValue()).links().get();
            page.addSubPageRequest(new Request(subpageUrl),page, entry.getKey());
            pageMap.put(fatherUrl, subpageUrl, Boolean.FALSE);
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
            Map<String,ResultItems> resultItemsMap = referalExtractedValues.get(fatherUrl);
            for(Map.Entry<String, ResultItems> entry : resultItemsMap.entrySet()){
                if(entry.getKey().equals(currentUrl)) continue;
                ResultItems r = entry.getValue();
                for(Map.Entry<String, Object> entry1 : r.getAll().entrySet()){
                    resultItems.put(entry1.getKey(),entry1.getValue());
                }
            }

            referalExtractedValues.remove(fatherUrl);
            pageMap.remove(fatherUrl);
        }else{
            resultItems.setSkip(true);
        }
    }
}
