package us.codecraft.webmagic.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by canoxu on 2015/1/22.
 */
public class PageModel{
    public static final String itemModelName = "name";

    //item operations
    public static final String itemModelItemOpTrim = "opTrim";

    //item types
    public static final String itemTextTypeInt = "float";
    public static final String itemTextTypeText = "varchar(200)";
    public static final String itemTextTypeLongText = "text";

    private List<LinkModel> linksModel = new ArrayList<LinkModel>();
    private List<ItemModel> itemsModel = new ArrayList<ItemModel>();
    private String modelName = null;

    public List<LinkModel> getLinksModel() {
        return linksModel;
    }

    public List<ItemModel> getItemsModel() {
        return itemsModel;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public void addLink(String linkRegrex, String sourceRegionXpath){
        LinkModel linkModel = new LinkModel();
        linkModel.setRegrex(linkRegrex);
        linkModel.setSourceRegion(sourceRegionXpath);
        linksModel.add(linkModel);
    }

    public void addItem(String itemName, String itemXpath,String itemType){
        ItemModel itemModel = new ItemModel();
        itemModel.setName(itemName);
        itemModel.setXpath(itemXpath);
        itemModel.setItemType(itemType);
        itemsModel.add(itemModel);
    }

    public void addItem(String itemName, String itemXpath){
        addItem(itemName, itemXpath, PageModel.itemTextTypeText);
    }
}
