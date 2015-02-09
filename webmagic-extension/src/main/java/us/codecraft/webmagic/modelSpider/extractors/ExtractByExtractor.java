package us.codecraft.webmagic.modelSpider.extractors;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.model.FieldExtractor;
import us.codecraft.webmagic.model.annotation.ExtractBy;
import us.codecraft.webmagic.selector.Selector;
import us.codecraft.webmagic.selector.XpathSelector;
import us.codecraft.webmagic.utils.ExtractorUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
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

    public ExtractByExtractor(ExtractBy extractBy, Field field){
        selector = ExtractorUtils.getSelector(extractBy);
        notNull = extractBy.notNull();
        multi = extractBy.multi() || List.class.isAssignableFrom(field.getType());
        nextPageRegion = new XpathSelector(extractBy.nextPage());
        this.field = field;
        this.name = field.getName();
    }


    @Override
    public String extract(Page page) {
        if (this.multi) {
            List<String> value;
            value = page.getHtml().selectDocumentForList(this.selector);
            if ((value == null || value.size() == 0) && this.notNull) {
                return null;
            }
            setField(o, fieldExtractor, value);
        } else {
            String value;
            value = page.getHtml().selectDocument(this.selector);
            if (value == null && this.notNull) {
                return null;
            }
            setField(o, fieldExtractor, value);
        }
    }

    private void setField(Object o, FieldExtractor fieldExtractor, Object value) throws IllegalAccessException, InvocationTargetException {
        if (value == null) {
            return;
        }
        if (fieldExtractor.getSetterMethod() != null) {
            fieldExtractor.getSetterMethod().invoke(o, value);
        }
        fieldExtractor.getField().set(o, value);
    }

    @Override
    public String getName() {
        return this.name;
    }


}
