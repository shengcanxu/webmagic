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
            logger.error("pageModel is not set!");
            return;
        }
        if(pageModel.getName() == null){
            logger.error("pageModel name is not set");
            return;
        }

        int level = page.getLevel();
        if(level < pageModel.getLinksModel().size()) {  //add links
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
            page.setSkip(true);
            logger.info("get " + nextLinks.size() + " links to follow in level " + level);
        }else{ //parse content
            page.setSkip(false);
            List<Map<String, String>> items = pageModel.getItemsModel();
            for(int i=0;i<items.size(); i++) {
                Map<String,String> item = items.get(i);
                String name = item.get(PageModel.itemModelName);
                String xpath = item.get(PageModel.itemModelXpath);
                page.putField(name, page.getHtml().xpath(xpath).toString());
            }
        }
    }
}
