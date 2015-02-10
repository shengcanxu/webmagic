package us.codecraft.webmagic.modelSpider.extractors;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.model.annotation.ExtractBy;
import us.codecraft.webmagic.selector.Selector;
import us.codecraft.webmagic.selector.XpathSelector;
import us.codecraft.webmagic.utils.ExtractorUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cano on 2015/2/7.
 */
public class ExtractByExtractor implements FieldValueExtractor {

    protected Field field;

    protected String name;

    protected Selector selector;

    protected boolean notNull = false;

    protected boolean multi = false;

    protected Selector nextPageSelector;

    public ExtractByExtractor(ExtractBy extractBy, Field field){
        selector = ExtractorUtils.getSelector(extractBy);
        notNull = extractBy.notNull();
        multi = extractBy.multi() || List.class.isAssignableFrom(field.getType());
        if(extractBy.nextPage().length() > 0) {
            nextPageSelector = new XpathSelector(extractBy.nextPage());
        }
        this.field = field;
        this.name = field.getName();
    }


    @Override
    public List<String> extract(Page page) {
        if (this.multi) {
            List<String> value;
            value = page.getHtml().selectDocumentForList(this.selector);
            if ((value == null || value.size() == 0) && this.notNull) {
                return null;
            }
            return value;
        } else {
            String value;
            value = page.getHtml().selectDocument(this.selector);
            if (value == null && this.notNull) {
                return null;
            }
            List<String> values = new ArrayList<>();
            values.add(value);
            return values;
        }
    }

    public boolean isHasNextPage() {
        return nextPageSelector != null;
    }

    public Selector getNextPageSelector() {
        return nextPageSelector;
    }

    @Override
    public String getName() {
        return this.name;
    }


}
