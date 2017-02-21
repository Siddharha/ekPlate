package com.ekplate.android.models.foodmodule;

/**
 * Created by Rahul on 8/26/2015.
 */
public class FoodItem {
    private int id;
    private String foodTitle, foodItemImageUrl, foodType, foodDescription, keyValue;

    public String getKeyValue() {
        return keyValue;
    }

    public void setKeyValue(String keyValue) {
        this.keyValue = keyValue;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFoodTitle() {
        return foodTitle;
    }

    public void setFoodTitle(String foodTitle) {
        this.foodTitle = foodTitle;
    }

    public String getFoodItemImageUrl() {
        return foodItemImageUrl;
    }

    public void setFoodItemImageUrl(String foodItemImageUrl) {
        this.foodItemImageUrl = foodItemImageUrl;
    }

    public String getFoodType() {
        return foodType;
    }

    public void setFoodType(String foodType) {
        this.foodType = foodType;
    }

    public String getFoodDescription() {
        return foodDescription;
    }

    public void setFoodDescription(String foodDescription) {
        this.foodDescription = foodDescription;
    }
}
