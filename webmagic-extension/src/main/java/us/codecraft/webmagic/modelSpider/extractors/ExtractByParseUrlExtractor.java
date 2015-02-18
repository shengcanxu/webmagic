package us.codecraft.webmagic.modelSpider.extractors;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.modelSpider.annotation.ExtractByParseUrl;
import us.codecraft.webmagic.selector.Selector;
import us.codecraft.webmagic.selector.XpathSelector;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by cano on 2015/2/7.
 */
public class ExtractByParseUrlExtractor implements FieldValueExtractor {

    protected Field field;

    private String name;

    protected Selector selector;

    protected boolean notNull = false;

    protected int depth;

    public ExtractByParseUrlExtractor(ExtractByParseUrl extractByParseUrl, Field field){
        selector = new XpathSelector(extractByParseUrl.value());
        notNull = extractByParseUrl.notNull();
        this.depth = extractByParseUrl.depth();
        this.field = field;
        this.name = field.getName();
    }


    @Override
    public List<String> extract(Page page) {
        return null;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public Selector getSelector() {
        return selector;
    }

    public int getDepth() {
        return depth;
    }
}
