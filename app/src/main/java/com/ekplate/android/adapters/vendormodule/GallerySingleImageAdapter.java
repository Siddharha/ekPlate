package com.ekplate.android.adapters.vendormodule;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ekplate.android.fragments.vendormodule.GallerySingleImageFragment;
import com.ekplate.android.models.vendormodule.GalleryItem;

import java.util.ArrayList;

/**
 * Created by Rahul on 10/5/2015.
 */
public class GallerySingleImageAdapter extends FragmentPagerAdapter {

    private ArrayList<GalleryItem> singleImageItemArrayList;
    private Context context;
    private int vendorId;
    private String vendorName, vendorAddress;

    public GallerySingleImageAdapter(FragmentManager fm, Context context,
                                     ArrayList<GalleryItem> singleImageItemArrayList, int vendorId,
                                     String vendorName, String vendorAddress) {
        super(fm);
        this.context = context;
        this.singleImageItemArrayList = singleImageItemArrayList;
        this.vendorId = vendorId;
        this.vendorName = vendorName;
        this.vendorAddress = vendorAddress;
    }

    @Override
    public Fragment getItem(int position) {
        return GallerySingleImageFragment.newInstance(position, singleImageItemArrayList, vendorId
                , vendorName, vendorAddress);
    }

    @Override
    public int getCount() {
        return singleImageItemArrayList.size();
    }
}
