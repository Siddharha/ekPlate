package com.ekplate.android.models.vendormodule;

import java.io.Serializable;

/**
 * Created by Rahul on 10/2/2015.
 */
public class GalleryItem implements Serializable{
    private int id, imageType;
    private String imageUrl, vendorCaption, vendorStoreName, imageTotalLike;
    private boolean imageLikeStatus;

    public boolean isImageLikeStatus() {
        return imageLikeStatus;
    }

    public void setImageLikeStatus(boolean imageLikeStatus) {
        this.imageLikeStatus = imageLikeStatus;
    }

    public int getId() {
        return id;
    }

    public int getImageType() {
        return imageType;
    }

    public void setImageType(int imageType) {
        this.imageType = imageType;
    }

    public String getVendorCaption() {
        return vendorCaption;
    }

    public void setVendorCaption(String vendorCaption) {
        this.vendorCaption = vendorCaption;
    }

    public String getImageTotalLike() {
        return imageTotalLike;
    }

    public void setImageTotalLike(String imageTotalLike) {
        this.imageTotalLike = imageTotalLike;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getVendorStoreName() {
        return vendorStoreName;
    }

    public void setVendorStoreName(String vendorStoreName) {
        this.vendorStoreName = vendorStoreName;
    }
}
