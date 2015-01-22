package us.codecraft.webmagic.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Model.PageModel;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.selector.Selector;
import us.codecraft.webmagic.selector.XpathSelector;

import java.util.List;
import java.util.Map;

/**
 * Created by cano on 2015/1/17.
 */
public abstract class AbstractPageProcessor implements PageProcessor {
    Logger logger = LoggerFactory.getLogger(getClass());
    private PageModel pageModel = null;

    /**
     * extend class should overrite this method to add the page Model
     */
    public abstract PageModel buildPageModel();

    @Override
    public void process(Page page){
        pageModel = buildPageModel();
        if(pageModel == null){
            logger.error("pageMode is not set!");
            return;
        }

        int level = page.getLevel();
        if(level >= pageModel.getLinksModel().size()) {  //add links
            Map<String, String> link = pageModel.getLinksModel().get(level);
            String regrex = link.get(PageModel.linksModelRegrex);
            String sourceRegion = link.get(PageModel.linksModelSourceRegionXpath);
            List<String> nextLinks = null;
            if (sourceRegion == null) {
                regrex = regrex.replace(".", "\\.").replace("*", "[^\"'#]*");
                nextLinks = page.getHtml().links().regex(regrex).all();
            } else {
                Selector sourceRegionSelector = new XpathSelector(sourceRegion);
                nextLinks = page.getHtml().selectList(sourceRegionSelector).links().regex(regrex).all();
            }
            page.addTargetRequests(nextLinks);
            logger.info("get " + nextLinks.size() + " links to follow in level " + level);
        }else{ //parse content
            page.setSkip(true);
            Map<String, String> items = pageModel.getItemsModel();
            for(Map.Entry<String,String> name : items.entrySet()) {
                page.putField(name.getKey(), page.getHtml().xpath(name.getValue()).toString());
            }
        }
    }
}
