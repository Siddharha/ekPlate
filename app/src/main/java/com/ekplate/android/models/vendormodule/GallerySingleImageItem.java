package com.ekplate.android.models.vendormodule;

/**
 * Created by Rahul on 10/5/2015.
 */
public class GallerySingleImageItem {
    private int id, noOfLikes;
    private String foodName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNoOfLikes() {
        return noOfLikes;
    }

    public void setNoOfLikes(int noOfLikes) {
        this.noOfLikes = noOfLikes;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }
}
