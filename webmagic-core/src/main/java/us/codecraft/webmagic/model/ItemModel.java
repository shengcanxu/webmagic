package us.codecraft.webmagic.model;

/**
 * Created by canoxu on 2015/1/29.
 */
public class ItemModel  {
    //item types
    public static final String TypeInt = "float";
    public static final String TypeText = "varchar(200)";
    public static final String TypeLongText = "text";

    //item operations
    //remove the leading and tailing space
    public static final int ItemOpTrim = 1;
    //remove new-lines
    public static final int ItemOpTrimLN = 2;
    //remove tag like "<a>"
    public static final int ItemOpRemoveTag = 3;

    private String name;
    private String xpath;
    private String itemType;
    private int[] itemOperations = null;

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

    public void setItemOperations(int[] itemOperations) {
        this.itemOperations = itemOperations;
    }

    public int[] getItemOperations() {
        return itemOperations;
    }

    public boolean hasOperation(){
        return itemOperations != null && itemOperations.length != 0;
    }
}
