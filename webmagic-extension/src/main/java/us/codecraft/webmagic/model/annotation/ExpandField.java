package us.codecraft.webmagic.model.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Define a extractor to extract data in url of current page. Only regex can be used. <br>
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.2.0
 */
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ExpandField {

    /**
     * if should expand to multiple records
     */
    boolean shouldExpand() default false;

    /**
     * seperator
     */
    String seperator() default "@#$";

}
