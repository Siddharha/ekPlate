package com.ekplate.android.models.menumodule;

import java.io.Serializable;

/**
 * Created by user on 29-10-2015.
 */
public class CityNameItem implements Serializable {
    private int id;
    private String cityName;
    private boolean selected;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
