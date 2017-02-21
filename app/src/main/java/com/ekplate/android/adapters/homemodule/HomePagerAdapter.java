package com.ekplate.android.adapters.homemodule;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ekplate.android.fragments.homemodule.CategoriesFragment;
import com.ekplate.android.fragments.homemodule.DiscoverFragment;
import com.ekplate.android.fragments.homemodule.HungryFragment;
import com.ekplate.android.fragments.homemodule.SearchFragment;


/**
 * Created by Rahul on 8/28/2015.
 */
public class HomePagerAdapter extends FragmentPagerAdapter {

    private Context context;
    private int pageCount;

    public HomePagerAdapter(FragmentManager fm, Context context, int pageCount) {
        super(fm);
        this.context=context;
        this.pageCount=pageCount;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            /*case 0:
                return new SearchFragment();
            case 1:
                return new HungryFragment();
            default:
                return new CategoriesFragment();*/
            case 0:
                return new SearchFragment();
            case 1:
                return new HungryFragment();
            case 2:
                return new CategoriesFragment();
            default:
                return new DiscoverFragment();
        }
    }

    @Override
    public int getCount() {
        return pageCount;
    }
}
