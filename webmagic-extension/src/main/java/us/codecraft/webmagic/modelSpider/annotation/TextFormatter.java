package us.codecraft.webmagic.modelSpider.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Created by canoxu on 2015/2/10.
 */

@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface TextFormatter {

    /**
     * The type of text formatter
     */
    public static enum Type {TRIM,REMOVETAG}

    Type[] types();

}
