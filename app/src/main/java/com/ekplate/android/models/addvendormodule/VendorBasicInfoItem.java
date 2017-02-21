package com.ekplate.android.models.addvendormodule;

/**
 * Created by user on 11-01-2016.
 */
public class VendorBasicInfoItem {
    public int getVendorId() {
        return vendorId;
    }

    public void setVendorId(int vendorId) {
        this.vendorId = vendorId;
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

    public String getMostSellingFood() {
        return mostSellingFood;
    }

    public void setMostSellingFood(String mostSellingFood) {
        this.mostSellingFood = mostSellingFood;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public String getVendorAddress() {
        return vendorAddress;
    }

    public void setVendorAddress(String vendorAddress) {
        this.vendorAddress = vendorAddress;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getHygieneRating() {
        return hygieneRating;
    }

    public void setHygieneRating(String hygieneRating) {
        this.hygieneRating = hygieneRating;
    }

    public String getTasteRating() {
        return tasteRating;
    }

    public void setTasteRating(String tasteRating) {
        this.tasteRating = tasteRating;
    }

    private int vendorId;
    private String vendorName, shopName, mostSellingFood, contactNo, vendorAddress, longitude, latitude,
            hygieneRating, tasteRating;
}
