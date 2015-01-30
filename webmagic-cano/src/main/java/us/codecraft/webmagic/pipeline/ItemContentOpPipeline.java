package us.codecraft.webmagic.pipeline;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.model.ItemModel;
import us.codecraft.webmagic.model.PageModel;

import java.util.Map;

/**
 * Created by canoxu on 2015/1/29.
 */
public class ItemContentOpPipeline implements Pipeline {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void process(ResultItems resultItems, Task task) {
        PageModel pageModel = resultItems.getPageModel();
        if(pageModel == null){
            logger.error("page model is null");
        }

        Map<String,Object> items = resultItems.getAll();
        for(Map.Entry<String, Object> entry : items.entrySet()){
            String name = entry.getKey();
            String value = (String)entry.getValue();
            ItemModel itemModel = pageModel.getItemModelByName(name);

            if(itemModel != null && itemModel.hasOperation()){
                int[] operations = itemModel.getItemOperations();
                for(int i=0; i<operations.length; i++){
                    switch (operations[i]){
                        case ItemModel.ItemOpTrim:
                            value = doTrim(value);
                            resultItems.put(name,value);
                            break;
                        case ItemModel.ItemOpTrimLN:
                            value = doTrimLN(value);
                            resultItems.put(name,value);
                            break;
                        case ItemModel.ItemOpRemoveTag:
                            value = doRemoveTap(value);
                            resultItems.put(name,value);
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }

    private void doOperations(int[] operations, ResultItems resultItems, String name,String value){

    }

    private String doTrim(String value){
        return value.trim();
    }

    private String doTrimLN(String value){
        return value.replace("\n","").replace("\r","");
    }

    public String doRemoveTap(String value){
        return value.replaceAll("<*>","");
    }
}
