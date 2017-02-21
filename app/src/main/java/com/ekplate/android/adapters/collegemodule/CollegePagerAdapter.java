package com.ekplate.android.adapters.collegemodule;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ekplate.android.fragments.collegemodule.CollegeListFragment;

/**
 * Created by Rahul on 9/4/2015.
 */
public class CollegePagerAdapter extends FragmentPagerAdapter {
    private Context context;
    private String[] tabTitle;
    private int optionId;
    private String routeScreen;

    public CollegePagerAdapter(FragmentManager fm, Context context, String[] tabTitle, int optionId,
                               String routeScreen) {
        super(fm);
        this.context = context;
        this.tabTitle = tabTitle;
        this.optionId = optionId;
        this.routeScreen = routeScreen;
    }

    @Override
    public Fragment getItem(int position) {
        return CollegeListFragment.newInstance(optionId, routeScreen, position);
    }

    @Override
    public int getCount() {
        return tabTitle.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {

         return tabTitle[position];

    }
}
