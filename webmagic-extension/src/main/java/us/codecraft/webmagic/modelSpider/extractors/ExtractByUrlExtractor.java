package us.codecraft.webmagic.modelSpider.extractors;

import us.codecraft.webmagic.model.annotation.ExtractByUrl;

import java.lang.reflect.Field;
import java.util.regex.Pattern;

/**
 * Created by cano on 2015/2/7.
 */
public class ExtractByUrlExtractor implements FieldValueExtractor {

    protected Field field;

    private String name;

    protected Pattern urlPattern;

    protected boolean notNull = false;

    protected boolean multi = false;

    public ExtractByUrlExtractor(ExtractByUrl extractByUrl, Field field){
        urlPattern = Pattern.compile("(" + extractByUrl.value().replace(".", "\\.").replace("*", "[^\"'#]*") + ")");
        notNull = extractByUrl.notNull();
        multi = extractByUrl.multi();
        this.field = field;
        this.name = field.getName();
    }


    @Override
    public String extract() {
        return null;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
