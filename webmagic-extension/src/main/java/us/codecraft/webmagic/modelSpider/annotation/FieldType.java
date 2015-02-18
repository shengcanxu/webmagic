package us.codecraft.webmagic.modelSpider.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Created by canoxu on 2015/2/10.
 */

@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface FieldType {

    /**
     * The type of DownloadFile annotation
     */
    public static enum Type {INT,STRING,TEXT,DATETIME}

    Type type() default Type.STRING;

}
