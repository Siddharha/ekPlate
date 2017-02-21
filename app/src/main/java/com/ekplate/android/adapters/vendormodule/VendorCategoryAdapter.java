package com.ekplate.android.adapters.vendormodule;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ekplate.android.fragments.vendormodule.VendorListFragment;


/**
 * Created by Rahul on 8/27/2015.
 */
public class VendorCategoryAdapter extends FragmentPagerAdapter {

    private Context context;
    private String[] pageCount;
    private int optionId;
    private String keyValue, routeFrom,foodCategoryType;

    public VendorCategoryAdapter(FragmentManager fm, Context context, String[] pageCount, int optionId,
                                 String keyValue, String routeFrom, String foodCategoryType) {
        super(fm);
        this.context = context;
        this.pageCount = pageCount;
        this.optionId = optionId;
        this.keyValue = keyValue;
        this.routeFrom = routeFrom;
        this.foodCategoryType = foodCategoryType;
    }

    @Override
    public int getCount() {
        return pageCount.length;
    }

    @Override
    public Fragment getItem(int position) {
        return VendorListFragment.newInstance(optionId, keyValue, routeFrom,foodCategoryType, position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return pageCount[position];
    }
}
