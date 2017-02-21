package com.ekplate.android.models.discovermodule;

/**
 * Created by user on 03-11-2015.
 */
public class SocialFeedListItem {
    private String id;
    private String imageUrl;
    private String heading;
    private String content;
    private String postingTime;
    private String socialType;
    private String link;
    private String twitter_id_str;

    public String getLink() {return link;}

    public void setLink(String link) {this.link = link;}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPostingTime() {
        return postingTime;
    }

    public void setPostingTime(String postingTime) {
        this.postingTime = postingTime;
    }

    public String getSocialType() {
        return socialType;
    }

    public void setSocialType(String socialType) {
        this.socialType = socialType;
    }

    public String getTwitter_id_str() {
        return twitter_id_str;
    }

    public void setTwitter_id_str(String twitter_id_str) {
        this.twitter_id_str = twitter_id_str;
    }
}
