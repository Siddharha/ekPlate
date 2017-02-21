package com.ekplate.android.models.menumodule;

import java.util.ArrayList;

/**
 * Created by user on 05-11-2015.
 */
public class CountryNameItem  {
    private int id;
    private String countryName;
    private boolean selected;
    private String itemType;
    private ArrayList<CityNameItem> cityNameItems;

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public ArrayList<CityNameItem> getCityNameItems() {
        return cityNameItems;
    }

    public void setCityNameItems(ArrayList<CityNameItem> cityNameItems) {
        this.cityNameItems = cityNameItems;
    }

    public int getId() {
        return id;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public void setId(int id) {

        this.id = id;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
