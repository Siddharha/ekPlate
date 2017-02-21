package com.ekplate.android.models.searchmodule;

/**
 * Created by user on 05-12-2015.
 */
public class VendorSearchResultItem {

    private int id;

    private String vendorName, shopName, keyValue, vendorDetailsJsonStr, searchType, jsonObjStr;

    public String getJsonObjStr() {
        return jsonObjStr;
    }

    public void setJsonObjStr(String jsonObjStr) {
        this.jsonObjStr = jsonObjStr;
    }

    public String getSearchType() {
        return searchType;
    }

    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }

    public String getKeyValue() {
        return keyValue;
    }

    public void setKeyValue(String keyValue) {
        this.keyValue = keyValue;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getVendorDetailsJsonStr() {
        return vendorDetailsJsonStr;
    }

    public void setVendorDetailsJsonStr(String vendorDetailsJsonStr) {
        this.vendorDetailsJsonStr = vendorDetailsJsonStr;
    }
}
