package us.codecraft.webmagic.modelSpider.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 在field上使用，可以爬取完field后调用自定义函数。
 * 函数定义类似于:
 *      public Object minusOne(Object value, Page page)
 * Created by canoxu on 2015/2/10.
 */

@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface CustomFunction {

    /**
     * the function name, no parameter is needed
     * @return
     */
    String name();

}
