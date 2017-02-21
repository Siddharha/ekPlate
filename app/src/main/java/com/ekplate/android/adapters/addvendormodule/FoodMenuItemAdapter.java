package com.ekplate.android.adapters.addvendormodule;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ekplate.android.R;
import com.ekplate.android.models.addvendormodule.MenuItem;

import java.util.ArrayList;

/**
 * Created by user on 02-12-2015.
 */
public class FoodMenuItemAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<MenuItem> menuList;

    public FoodMenuItemAdapter(Context context, ArrayList<MenuItem> menuList) {
        this.context = context;
        this.menuList = menuList;
    }

    @Override
    public int getCount() {
        return menuList.size();
    }

    @Override
    public MenuItem getItem(int position) {
        return menuList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        if(rowView == null){
            rowView = LayoutInflater.from(context).inflate(R.layout.food_menu_row_addvendor_layout, parent, false);
            FoodItemHolder holder = new FoodItemHolder();
            holder.tvFoodNameAddVendor = (TextView) rowView.findViewById(R.id.tvFoodNameAddVendor);
            holder.tvFoodPriceAddVendor = (TextView) rowView.findViewById(R.id.tvFoodPriceAddVendor);
            rowView.setTag(holder);
        }
        FoodItemHolder newHolder = (FoodItemHolder) rowView.getTag();
        newHolder.tvFoodNameAddVendor.setText(getItem(position).getFoodName());
        newHolder.tvFoodPriceAddVendor.setText(getItem(position).getFoodValue());
        return rowView;
    }

    class FoodItemHolder{
        TextView tvFoodNameAddVendor, tvFoodPriceAddVendor;
    }
}
