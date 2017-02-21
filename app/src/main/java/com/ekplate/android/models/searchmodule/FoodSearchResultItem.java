package com.ekplate.android.models.searchmodule;

/**
 * Created by user on 05-12-2015.
 */
public class FoodSearchResultItem {
    private int id;
    private String foodTitle, foodKeyValue, searchType, jsonObjStr;

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFoodTitle() {
        return foodTitle;
    }

    public void setFoodTitle(String foodTitle) {
        this.foodTitle = foodTitle;
    }

    public String getFoodKeyValue() {
        return foodKeyValue;
    }

    public void setFoodKeyValue(String foodKeyValue) {
        this.foodKeyValue = foodKeyValue;
    }
}
