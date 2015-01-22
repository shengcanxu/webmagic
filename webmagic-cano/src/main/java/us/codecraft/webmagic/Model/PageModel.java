package us.codecraft.webmagic.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by canoxu on 2015/1/22.
 */
public class PageModel{
    public static String linksModelRegrex = "regrex";
    public static String linksModelSourceRegionXpath = "sourceXpath";

    private List<Map<String,String>> linksModel = new ArrayList<Map<String, String>>();
    private Map<String,String> itemsModel = new HashMap<String, String>();

    public List<Map<String,String>> getLinksModel() {
        return linksModel;
    }

    public Map<String, String> getItemsModel() {
        return itemsModel;
    }

    public void addLink(String linkRegrex, String sourceRegionXpath){
        Map<String, String> item = new HashMap<String, String>();
        item.put(linkRegrex, sourceRegionXpath);
        linksModel.add(item);
    }

    public void addItem(String itemName, String itemXpath){
        itemsModel.put(itemName,itemXpath);
    }
}
