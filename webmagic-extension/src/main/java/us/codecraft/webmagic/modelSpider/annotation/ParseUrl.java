package us.codecraft.webmagic.modelSpider.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Define the 'help' url patterns for class. <br>
 * All urls matching the pattern will be crawled and but not extracted for new objects. <br>
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.2.0
 */
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Repeatable(ParseUrls.class)
public @interface ParseUrl {

    /**
     * Extractor expression, support XPath only
     *
     * @return extractor expression
     */
    String express();

    /**
     * types of extractor expressions
     */
    public static enum Type {XPath, Regex, Css, JsonPath}

    /**
     * Extractor type, support XPath, CSS Selector and regex.
     *
     * @return extractor type
     */
    Type type() default Type.XPath;

    /**
     * if subXpath is set, first parse and get strings from "express()" setting and then use subXpath to parse those strings.
     * @return
     */
    String subXpath() default "";

    /**
     * the next page express
     * @return
     */
    String nextPageRegion() default "";

    /**
     * the regex of next page link
     * @return
     */
    String nextPageLinkRegex() default "";

    /**
     * the custom function used to called before parse-url are done
     * @return
     */
    String customFunction() default  "";
}
