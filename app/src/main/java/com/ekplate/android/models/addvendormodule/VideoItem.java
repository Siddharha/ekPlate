package com.ekplate.android.models.addvendormodule;

import android.graphics.Bitmap;

/**
 * Created by user on 21-02-2016.
 */
public class VideoItem {
    private String videoPath, captionStr;
    private Bitmap videoThump;

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public String getCaptionStr() {
        return captionStr;
    }

    public void setCaptionStr(String captionStr) {
        this.captionStr = captionStr;
    }

    public Bitmap getVideoThump() {
        return videoThump;
    }

    public void setVideoThump(Bitmap videoThump) {
        this.videoThump = videoThump;
    }
}
