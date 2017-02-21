package com.ekplate.android.models.vendormodule;

/**
 * Created by user on 15-11-2015.
 */
public class UserReviewItem {
    private int id;
    private String userImageUrl, userName, rating, lastSeenTime, review, noOfAgreed;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserImageUrl() {
        return userImageUrl;
    }

    public void setUserImageUrl(String userImageUrl) {
        this.userImageUrl = userImageUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getLastSeenTime() {
        return lastSeenTime;
    }

    public void setLastSeenTime(String lastSeenTime) {
        this.lastSeenTime = lastSeenTime;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getNoOfAgreed() {
        return noOfAgreed;
    }

    public void setNoOfAgreed(String noOfAgreed) {
        this.noOfAgreed = noOfAgreed;
    }
}
