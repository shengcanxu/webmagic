package us.codecraft.webmagic.modelSpider.pipeline;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.modelSpider.PageModel;
import us.codecraft.webmagic.modelSpider.annotation.FieldType;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Write results in console.<br>
 * Usually used in test.
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.1.0
 */
public class ConsoleModelSpiderPipeline implements Pipeline {

    private Map<String,FieldType> fieldTypeMap = new HashMap<>();

    @Override
    public void process(ResultItems resultItems, Task task) {
        if(resultItems.isSkip()) return;

        PageModel pageModel = (PageModel) resultItems.getPageModel();
        Field[] fields = pageModel.getClazz().getDeclaredFields();
        AccessibleObject.setAccessible(fields, true);
        for(Field field : fields){
            FieldType fieldType = field.getAnnotation(FieldType.class);
            if(fieldType != null){
                fieldTypeMap.put(field.getName(), fieldType);
            }
        }

        System.out.println("get page: " + resultItems.getRequest().getUrl());
        for (Map.Entry<String, Object> entry : resultItems.getAll().entrySet()) {
            String name = entry.getKey();
            String value = (String)entry.getValue();

            //just output part of TEXT type content
            if(fieldTypeMap.containsKey(name) && fieldTypeMap.get(name).type() == FieldType.Type.TEXT){
                value = value.substring(0,100) + "......";
            }

            System.out.println(name + ":\t" + value);
        }
    }
}
