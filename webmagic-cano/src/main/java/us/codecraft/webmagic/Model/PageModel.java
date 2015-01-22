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
    public static String itemModelName = "name";
    public static String itemModelXpath = "xpath";

    private List<Map<String,String>> linksModel = new ArrayList<Map<String, String>>();
    private List<Map<String,String>> itemsModel = new ArrayList<Map<String, String>>();
    private String name = null;

    public List<Map<String,String>> getLinksModel() {
        return linksModel;
    }

    public List<Map<String, String>> getItemsModel() {
        return itemsModel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addLink(String linkRegrex, String sourceRegionXpath){
        Map<String, String> link = new HashMap<String, String>();
        link.put(PageModel.linksModelRegrex, linkRegrex);
        link.put(PageModel.linksModelSourceRegionXpath, sourceRegionXpath);
        linksModel.add(link);
    }

    public void addItem(String itemName, String itemXpath){
        Map<String, String> item = new HashMap<String, String>();
        item.put(PageModel.itemModelName,itemName);
        item.put(PageModel.itemModelXpath, itemXpath);
        itemsModel.add(item);
    }
}
