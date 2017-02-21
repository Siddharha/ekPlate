package com.ekplate.android.models.homemodule;

/**
 * Created by user on 15-01-2016.
 */
public class MostSearchListItem {
    private int id;
    private String vendorDetails;
    private String itemType;
    private String imageUrl;
    private String foodName;

    public String getFoodTyp() {
        return foodTyp;
    }

    public void setFoodTyp(String foodTyp) {
        this.foodTyp = foodTyp;
    }

    private String foodTyp;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVendorDetails() {
        return vendorDetails;
    }

    public void setVendorDetails(String vendorDetails) {
        this.vendorDetails = vendorDetails;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }
}
