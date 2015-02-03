package us.codecraft.webmagic.pipeline;

import us.codecraft.webmagic.Task;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;

/**
 * Print page model in console.<br>
 * Usually used in test.<br>
 * @author code4crafter@gmail.com <br>
 * @since 0.2.0
 */
public class ConsolePageModelPipeline implements PageModelPipeline {
    @Override
    public void process(Object o, Task task) {
        Class<?> clazz = o.getClass();
        Field[] fields = clazz.getDeclaredFields();
        AccessibleObject.setAccessible(fields, true);

        try {
            for (Field field : fields) {
                System.out.println(field.getName() + ": " + field.get(o));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
