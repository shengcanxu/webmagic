package us.codecraft.webmagic.model;

import java.util.List;

/**
 * Created by canoxu on 2015/1/29.
 */
public class ItemModel  {
    private String name;
    private String xpath;
    private String itemType;
    private List<String> itemOperations;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getXpath() {
        return xpath;
    }

    public void setXpath(String xpath) {
        this.xpath = xpath;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public List<String> getItemOperations() {
        return itemOperations;
    }

    public void setItemOperations(List<String> itemOperations) {
        this.itemOperations = itemOperations;
    }
}
