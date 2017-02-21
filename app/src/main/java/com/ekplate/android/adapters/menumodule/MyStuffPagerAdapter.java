package com.ekplate.android.adapters.menumodule;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ekplate.android.fragments.menumodule.MyStuffBookmarksFragment;
import com.ekplate.android.fragments.menumodule.MyStuffCollegeFragment;
import com.ekplate.android.fragments.menumodule.MyStuffImageFragment;

/**
 * Created by user on 29-12-2015.
 */
public class MyStuffPagerAdapter extends FragmentPagerAdapter {

    private String[] TAB_TITLE;

    public MyStuffPagerAdapter(FragmentManager fm, String[] TAB_TITLE) {
        super(fm);
        this.TAB_TITLE = TAB_TITLE;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return MyStuffCollegeFragment.getInstance();
            case 1:
                return MyStuffBookmarksFragment.getInstance();
            default:
                return MyStuffImageFragment.getInstance();
        }
    }

    @Override
    public int getCount() {
        return TAB_TITLE.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TAB_TITLE[position];
    }
}
