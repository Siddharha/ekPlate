package com.ekplate.android.activities.discovermodule;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Avishek on 10/13/2016.
 */
public class SocialFeedPagerAdapter extends FragmentPagerAdapter {
    private Context context;
    private String[] pageCount;

    public SocialFeedPagerAdapter(FragmentManager fm, Context context, String[] pageCount) {
        super(fm);
        this.context = context;
        this.pageCount = pageCount;
    }


    @Override
    public Fragment getItem(int position) {
        SocialFeedChooseMediaActivity mainActivity=(SocialFeedChooseMediaActivity)context;

        switch (position){
            case 0:
                return new FaceBookFragent();
            case 1:
                return new TwitterFragent();
            case 2:
                return new InstagramFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return pageCount.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return pageCount[position];
    }
}