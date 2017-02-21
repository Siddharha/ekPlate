package com.ekplate.android.models.searchmodule;

/**
 * Created by user on 28-01-2016.
 */
public class GlobalSearchResultItem {
    private int id;
    private String itemTitle, itemType, jsonObjStr;

    public String getItemTitle() {
        return itemTitle;
    }

    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }

    public int getId() {
        return id;

    }

    public void setId(int id) {
        this.id = id;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getJsonObjStr() {
        return jsonObjStr;
    }

    public void setJsonObjStr(String jsonObjStr) {
        this.jsonObjStr = jsonObjStr;
    }
}
