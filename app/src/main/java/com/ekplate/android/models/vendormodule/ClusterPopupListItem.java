package com.ekplate.android.models.vendormodule;

/**
 * Created by user on 18-11-2015.
 */
public class ClusterPopupListItem {
    private int id,noOfReviews;
    private String vendorName, vendorAddress, foodItem, distance, noOfLikes, rating, vendorDetails;

    public String getVendorDetails() {
        return vendorDetails;
    }

    public void setVendorDetails(String vendorDetails) {
        this.vendorDetails = vendorDetails;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getVendorAddress() {
        return vendorAddress;
    }

    public void setVendorAddress(String vendorAddress) {
        this.vendorAddress = vendorAddress;
    }

    public String getFoodItem() {
        return foodItem;
    }

    public void setFoodItem(String foodItem) {
        this.foodItem = foodItem;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getNoOfLikes() {
        return noOfLikes;
    }

    public void setNoOfLikes(String noOfLikes) {
        this.noOfLikes = noOfLikes;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public int getNoOfReviews() {
        return noOfReviews;
    }

    public void setNoOfReviews(int noOfReviews) {
        this.noOfReviews = noOfReviews;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
