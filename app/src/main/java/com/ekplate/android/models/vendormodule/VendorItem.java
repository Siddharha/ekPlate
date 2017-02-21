package com.ekplate.android.models.vendormodule;

import java.io.Serializable;

/**
 * Created by Rahul on 8/28/2015.
 */
public class VendorItem implements Serializable{
    private int id;
    private int innerCircleIcon;

    public int getNoOfReviews() {
        return noOfReviews;
    }

    public void setNoOfReviews(int noOfReviews) {
        this.noOfReviews = noOfReviews;
    }

    private int noOfReviews;
    private String innerCircleText, vendorName, inlineAddress, inlineFoodMenu, noOfBookmark,
            noOfLikes, rating, distance, foodType, vendorImages;
    private boolean bookmarkStatus, openStatus;
    private double latitude, longitude;
    private String locationPositionId;

    public String getNoOfBookmark() {
        return noOfBookmark;
    }

    public void setNoOfBookmark(String noOfBookmark) {
        this.noOfBookmark = noOfBookmark;
    }

    public boolean isOpenStatus() {
        return openStatus;
    }

    public void setOpenStatus(boolean openStatus) {
        this.openStatus = openStatus;
    }

    public String getLocationPositionId() {
        return locationPositionId;
    }

    public void setLocationPositionId(String locationPositionId) {
        this.locationPositionId = locationPositionId;
    }

    public int getInnerCircleIcon() {
        return innerCircleIcon;
    }

    public void setInnerCircleIcon(int innerCircleIcon) {
        this.innerCircleIcon = innerCircleIcon;
    }

    public String getInnerCircleText() {
        return innerCircleText;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getInlineAddress() {
        return inlineAddress;
    }

    public void setInlineAddress(String inlineAddress) {
        this.inlineAddress = inlineAddress;
    }

    public String getInlineFoodMenu() {
        return inlineFoodMenu;
    }

    public void setInlineFoodMenu(String inlineFoodMenu) {
        this.inlineFoodMenu = inlineFoodMenu;
    }

   /* public int getNoOfReviews() {
        return noOfReviews;
    }

    public void setNoOfReviews(int noOfReviews) {
        this.noOfReviews = noOfReviews;
    }*/

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

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getFoodType() {
        return foodType;
    }

    public void setFoodType(String foodType) {
        this.foodType = foodType;
    }

    public boolean isBookmarkStatus() {
        return bookmarkStatus;
    }

    public String getVendorImages() {
        return vendorImages;
    }

    public void setVendorImages(String vendorDetails) {
        this.vendorImages = vendorDetails;
    }

    public void setBookmarkStatus(boolean bookmarkStatus) {
        this.bookmarkStatus = bookmarkStatus;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setInnerCircleText(String innerCircleText) {

        this.innerCircleText = innerCircleText;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
