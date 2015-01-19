package us.codecraft.webmagic.processor;

import us.codecraft.webmagic.Page;

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

    public List<String> getLinksFromRegrex(Page page, String regrex, String from, String to){
        regrex = regrex.replace(".", "\\.").replace("*", "[^\"'#]*");
        return page.getHtml().links().regex(regrex).all();
    }
}
