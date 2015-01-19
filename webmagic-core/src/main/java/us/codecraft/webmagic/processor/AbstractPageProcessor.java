package us.codecraft.webmagic.processor;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.selector.Selector;
import us.codecraft.webmagic.selector.XpathSelector;

import java.util.List;

/**
 * Created by cano on 2015/1/17.
 */
public abstract class AbstractPageProcessor implements PageProcessor {

    /**
     * extract links from page and add to parsing list
     * return null means no next links
     * @param page
     */
    public abstract List<String> extractLinks(Page page);

    /**
     * extract content from page
     * @param page
     */
    public abstract void extractContent(Page page);

    @Override
    public void process(Page page){
        List<String> nextURLs = this.extractLinks(page);
        if(nextURLs == null){
            this.extractContent(page);
        }else{
            page.addTargetRequests(nextURLs);
        }
    }

    /**
     * get links from regrex expression , and limit to sourceregion
     * @param page
     * @param regrex input regrex string
     * @param sourceRegion input xpath string
     * @return
     */
    public List<String> getLinksFromRegrex(Page page, String regrex, String sourceRegion){
        if(sourceRegion == null) {
            regrex = regrex.replace(".", "\\.").replace("*", "[^\"'#]*");
            return page.getHtml().links().regex(regrex).all();
        }else{
            Selector sourceRegionSelector = new XpathSelector(sourceRegion);
            return page.getHtml().selectList(sourceRegionSelector).links().regex(regrex).all();
        }
    }

    /**
     * @param page
     * @param name item name
     * @param xpath xpath of the item
     */
    public void getContentFromXpath(Page page, String name, String xpath){
        page.putField(name,page.getHtml().xpath(xpath).toString());
    }
}
