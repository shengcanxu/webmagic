package us.codecraft.webmagic.modelSpider.extractors;

import us.codecraft.webmagic.model.annotation.ExtractBy;
import us.codecraft.webmagic.selector.Selector;
import us.codecraft.webmagic.selector.XpathSelector;

import java.lang.reflect.Field;

/**
 * Created by cano on 2015/2/7.
 */
public class ExtractByExtractor implements FieldExtractor {

    protected Field field;

    protected Selector xPathSelector;

    protected ExtractBy.Type type = ExtractBy.Type.XPath;

    protected boolean notNull = false;

    protected ExtractBy.Source source = ExtractBy.Source.SelectedHtml;

    protected boolean multi = false;

    protected Selector nextPageRegion;

    public ExtractByExtractor(ExtractBy extractBy, Field field){
        xPathSelector = new XpathSelector(extractBy.value());
        type = extractBy.type();
        notNull = extractBy.notNull();
        source = extractBy.source();
        multi = extractBy.multi();
        nextPageRegion = new XpathSelector(extractBy.nextPage());
        this.field = field;
    }


    @Override
    public void extract() {

    }
}
