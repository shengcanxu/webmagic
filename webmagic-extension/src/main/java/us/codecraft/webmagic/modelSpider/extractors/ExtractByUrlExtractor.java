package us.codecraft.webmagic.modelSpider.extractors;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.model.annotation.ExtractByUrl;
import us.codecraft.webmagic.selector.RegexSelector;
import us.codecraft.webmagic.selector.Selector;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cano on 2015/2/7.
 */
public class ExtractByUrlExtractor implements FieldValueExtractor {

    protected Field field;

    private String name;

    protected Selector selector;

    protected boolean notNull = false;

    protected boolean multi = false;

    public ExtractByUrlExtractor(ExtractByUrl extractByUrl, Field field){
        String regexPattern = extractByUrl.regrex();
        if (regexPattern.trim().equals("")) {
            regexPattern = ".*";
        }
        selector = new RegexSelector(regexPattern);
        notNull = extractByUrl.notNull();
        multi = extractByUrl.multi() || List.class.isAssignableFrom(field.getClass());
        this.field = field;
        this.name = field.getName();
    }


    @Override
    public List<String> extract(Page page) {
        if (this.multi) {
            List<String> value;
            value = this.selector.selectList(page.getUrl().toString());
            if ((value == null || value.size() == 0) && this.notNull) {
                return null;
            }
            return value;
        } else {
            String value;
            value = this.selector.select(page.getUrl().toString());
            if (value == null && this.notNull) {
                return null;
            }
            List<String> values = new ArrayList<>();
            values.add(value);
            return values;
        }
    }

    @Override
    public String getName() {
        return this.name;
    }
}
