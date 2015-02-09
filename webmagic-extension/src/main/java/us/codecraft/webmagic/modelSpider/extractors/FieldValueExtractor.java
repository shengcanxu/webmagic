package us.codecraft.webmagic.modelSpider.extractors;

import us.codecraft.webmagic.Page;

import java.util.List;

/**
 * Created by cano on 2015/2/8.
 */
public interface FieldValueExtractor {

    public List<String> extract(Page page);

    public String getName();
}
