package us.codecraft.webmagic.modelSpider.extractors;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.model.FieldExtractor;
import us.codecraft.webmagic.model.annotation.ExtractBy;
import us.codecraft.webmagic.selector.Selector;
import us.codecraft.webmagic.selector.XpathSelector;
import us.codecraft.webmagic.utils.ExtractorUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import static us.codecraft.webmagic.model.annotation.ExtractBy.Source;
import static us.codecraft.webmagic.model.annotation.ExtractBy.Source.RawHtml;
import static us.codecraft.webmagic.model.annotation.ExtractBy.Source.SelectedHtml;
import static us.codecraft.webmagic.model.annotation.ExtractBy.Type;

/**
 * Created by cano on 2015/2/7.
 */
public class ExtractByExtractor implements FieldValueExtractor {

    protected Field field;

    protected String name;

    protected Selector selector;

    protected boolean notNull = false;

    protected boolean multi = false;

    protected Selector nextPageRegion;

    protected boolean hasNextPage = false;

    public ExtractByExtractor(ExtractBy extractBy, Field field){
        selector = ExtractorUtils.getSelector(extractBy);
        notNull = extractBy.notNull();
        multi = extractBy.multi() || List.class.isAssignableFrom(field.getType());
        if(extractBy.nextPage().length() > 0) {
            nextPageRegion = new XpathSelector(extractBy.nextPage());
            hasNextPage = true;
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
        return hasNextPage;
    }

    @Override
    public String getName() {
        return this.name;
    }


}
