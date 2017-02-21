package com.ekplate.android.adapters.foodmodule;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.ekplate.android.fragments.foodmodule.FoodListItemFragment;

/**
 * Created by Rahul on 8/26/2015.
 */
public class SearchFoodAdapter extends FragmentPagerAdapter {

    private Context context;
    private int pageCount, optionId;
    private String tagIds;
    private String foodCategoryType;

    public SearchFoodAdapter(FragmentManager fm, Context context, int pageCount, int optionId, String tagIds,String foodCategoryType) {
        super(fm);
        this.context=context;
        this.pageCount=pageCount;
        this.optionId = optionId;
        this.tagIds = tagIds;
        this.foodCategoryType = foodCategoryType;
    }

    @Override
    public Fragment getItem(int position) {
        return FoodListItemFragment.newInstance(optionId, tagIds,foodCategoryType, position);
    }

    @Override
    public int getCount() {
        return pageCount;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "BY POPULARITY";
            default:
                return "BY ALPHABET";
        }
    }
}
