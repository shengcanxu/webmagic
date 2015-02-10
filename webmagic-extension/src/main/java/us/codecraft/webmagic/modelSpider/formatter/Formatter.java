package us.codecraft.webmagic.modelSpider.formatter;

import us.codecraft.webmagic.Page;

import java.util.List;

/**
 * Created by cano on 2015/2/8.
 */
public interface Formatter {

    public List<String> format(List<String> value);
}
