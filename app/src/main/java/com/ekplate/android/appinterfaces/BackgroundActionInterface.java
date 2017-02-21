package com.ekplate.android.appinterfaces;

import org.json.JSONObject;

/**
 * Created by Rahul on 9/28/2015.
 */
public interface BackgroundActionInterface {
    public void onStarted();
    public void onCompleted(JSONObject response);
}
