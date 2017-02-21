package com.ekplate.android.models.searchmodule;

/**
 * Created by user on 05-12-2015.
 */
public class CollegeSearchResultItem {
    private int id;
    private String collegeTitle, collegeKeyValue, searchType, jsonObjStr;

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

    public String getCollegeTitle() {
        return collegeTitle;
    }

    public void setCollegeTitle(String collegeTitle) {
        this.collegeTitle = collegeTitle;
    }

    public String getCollegeKeyValue() {
        return collegeKeyValue;
    }

    public void setCollegeKeyValue(String collegeKeyValue) {
        this.collegeKeyValue = collegeKeyValue;
    }
}
