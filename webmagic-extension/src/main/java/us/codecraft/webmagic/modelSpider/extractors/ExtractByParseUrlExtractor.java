package us.codecraft.webmagic.modelSpider.extractors;

import us.codecraft.webmagic.modelSpider.annotation.ExtractByParseUrl;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selector;
import us.codecraft.webmagic.selector.XpathSelector;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by cano on 2015/2/7.
 */
public class ExtractByParseUrlExtractor {

    protected Field field;

    private String name;

    protected Selector selector;

    protected boolean notNull = false;

    protected int depth;

    public ExtractByParseUrlExtractor(ExtractByParseUrl extractByParseUrl, Field field){
        selector = new XpathSelector(extractByParseUrl.xpath());
        notNull = extractByParseUrl.notNull();
        this.depth = extractByParseUrl.depth();
        this.field = field;
        this.name = field.getName();
    }


    public Map<String,String> extract(Html html) {
        String content = html.selectDocument(selector);
        Map<String, String> contentMap = new HashMap<>();
        contentMap.put(name,content);
        return contentMap;
    }

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
