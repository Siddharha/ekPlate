package com.ekplate.android.utils;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by user on 18-11-2015.
 */
public class ClusterPopupList implements ClusterItem {
    private final LatLng mPosition;
    public final int profilePhoto;
    public final int markerIndex;
    public  String rating;

    public int getMarkerIndex() {
        return markerIndex;
    }

    public ClusterPopupList(double lat, double lng, int pictureResource, int markerIndex) {
        mPosition = new LatLng(lat, lng);
        profilePhoto = pictureResource;
        this.markerIndex = markerIndex;
    }
    public ClusterPopupList(double lat, double lng, int pictureResource, int markerIndex,String rating1) {
        mPosition = new LatLng(lat, lng);
        profilePhoto = pictureResource;
        this.markerIndex = markerIndex;
        rating=rating1;
    }
    @Override
    public LatLng getPosition() {
        return mPosition;
    }
}
