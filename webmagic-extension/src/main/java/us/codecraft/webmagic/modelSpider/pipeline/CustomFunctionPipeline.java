package us.codecraft.webmagic.modelSpider.pipeline;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.modelSpider.PageModel;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Print page model in console.<br>
 * Usually used in test.<br>
 * @author code4crafter@gmail.com <br>
 * @since 0.2.0
 */
public class CustomFunctionPipeline implements Pipeline {

    @Override
    public void process(ResultItems resultItems, Task task) {
        if(resultItems.isSkip()) return;

        PageModel pageModel = (PageModel)resultItems.getPageModel();
        Map<String, String> customFunctions = pageModel.getCustomFunctionMap();
        if(customFunctions.size() == 0) return;

        for (Map.Entry<String,String> entry : customFunctions.entrySet()) {
            String name = entry.getKey();
            String value = resultItems.get(name);
            try {
                Method customFunction = pageModel.getClass().getMethod(entry.getValue(),String.class, Page.class);
                String newValue = (String) customFunction.invoke(pageModel,value,resultItems.getPage());
                resultItems.put(name,newValue);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
