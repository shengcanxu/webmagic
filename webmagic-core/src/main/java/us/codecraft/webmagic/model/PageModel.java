package us.codecraft.webmagic.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by canoxu on 2015/1/22.
 */
public class PageModel{

    private List<LinkModel> linksModel = new ArrayList<LinkModel>();
    private List<ItemModel> itemsModel = new ArrayList<ItemModel>();
    private String modelName = null;

    public List<LinkModel> getLinksModel() {
        return linksModel;
    }

    public List<ItemModel> getItemsModel() {
        return itemsModel;
    }

    /**
     * get ItemModel with name, return null if not found
     * @param name
     * @return
     */
    public ItemModel getItemModelByName(String name){
        for(int i=0; i<itemsModel.size(); i++){
            ItemModel itemModel = itemsModel.get(i);
            if(itemModel.getName().compareTo(name) == 0){
               return itemModel;
            }
        }
        return null;
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


    public void addItem(String itemName, String itemXpath,int... operations){
        addItem(itemName,itemXpath,ItemModel.TypeText,operations);
    }

    public void addItem(String itemName, String itemXpath){
        addItem(itemName, itemXpath, ItemModel.TypeText,null);
    }

    public void addItem(String itemName, String itemXpath,String itemType){
        addItem(itemName,itemXpath,itemType,null);
    }

    private void addItem(String itemName, String itemXpath,String itemType,int... operations){
        ItemModel itemModel = new ItemModel();
        itemModel.setName(itemName);
        itemModel.setXpath(itemXpath);
        itemModel.setItemType(itemType);
        itemModel.setItemOperations(operations);
        itemsModel.add(itemModel);
    }
}
