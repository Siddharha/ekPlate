package com.ekplate.android.models.homemodule;

/**
 * Created by Rahul on 9/7/2015.
 */
public class HungryListItem {
    private int id, imageUrl;
    private String key, title;

    public int getId() {
        return id;
    }

    public int getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(int imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
