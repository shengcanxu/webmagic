package us.codecraft.webmagic.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by canoxu on 2015/1/22.
 */
public class PageModel{
    public static final String linksModelRegrex = "regrex";
    public static final String linksModelSourceRegionXpath = "sourceXpath";
    public static final String itemModelName = "name";
    public static final String itemModelXpath = "xpath";
    public static final String itemModeItemType = "itemType";
    public static final String itemTextTypeInt = "float";
    public static final String itemTextTypeText = "varchar(200)";
    public static final String itemTextTypeLongText = "text";

    private List<Map<String,String>> linksModel = new ArrayList<Map<String, String>>();
    private List<Map<String,String>> itemsModel = new ArrayList<Map<String, String>>();

    public List<Map<String,String>> getLinksModel() {
        return linksModel;
    }

    public List<Map<String, String>> getItemsModel() {
        return itemsModel;
    }

    public void addLink(String linkRegrex, String sourceRegionXpath){
        Map<String, String> link = new HashMap<String, String>();
        link.put(PageModel.linksModelRegrex, linkRegrex);
        link.put(PageModel.linksModelSourceRegionXpath, sourceRegionXpath);
        linksModel.add(link);
    }

    public void addItem(String itemName, String itemXpath,String itemType){
        Map<String, String> item = new HashMap<String, String>();
        item.put(PageModel.itemModelName,itemName);
        item.put(PageModel.itemModelXpath, itemXpath);
        item.put(PageModel.itemModeItemType, itemType);
        itemsModel.add(item);
    }

    public void addItem(String itemName, String itemXpath){
        addItem(itemName, itemXpath, PageModel.itemTextTypeText);
    }
}
