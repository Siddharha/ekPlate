package com.ekplate.android.adapters.homemodule;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ekplate.android.R;
import com.ekplate.android.activities.collegemodule.CollegeActivity;
import com.ekplate.android.activities.foodmodule.SearchFoodActivity;
import com.ekplate.android.activities.vendormodule.NearVendorActivity;
import com.ekplate.android.activities.vendormodule.VendorsActivity;
import com.ekplate.android.models.homemodule.HungryListItem;

import java.util.ArrayList;

/**
 * Created by Rahul on 9/7/2015.
 */
public class HungryItemListAdapter extends
        RecyclerView.Adapter<HungryItemListAdapter.HungryItemHolder> {

    private Context context;
    private ArrayList<HungryListItem> hungryListItems;

    public HungryItemListAdapter(Context context, ArrayList<HungryListItem> hungryListItems){
        this.context = context;
        this.hungryListItems = hungryListItems;
    }

    @Override
    public HungryItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(context).
                inflate(R.layout.home_options_row_layout, parent, false);
        HungryItemHolder viewHolder = new HungryItemHolder(rowView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(HungryItemHolder holder, int position) {
        holder.tvHomeMenuItemTitle.setText(hungryListItems.get(position).getTitle());
        holder.ivHomeMenuItemIcon.setImageResource(hungryListItems.get(position).getImageUrl());
        holder.rlRowMailLayout.setOnClickListener(onClickListener);
        holder.rlRowMailLayout.setTag(holder);
    }

    @Override
    public int getItemCount() {
        return hungryListItems.size();
    }

    public class HungryItemHolder extends RecyclerView.ViewHolder{
        TextView tvHomeMenuItemTitle;
        ImageView ivHomeMenuItemIcon;
        RelativeLayout rlRowMailLayout;

        public HungryItemHolder(View itemView) {
            super(itemView);
            tvHomeMenuItemTitle = (TextView) itemView.findViewById(R.id.tvHomeMenuItemTitle);
            ivHomeMenuItemIcon = (ImageView) itemView.findViewById(R.id.ivHomeMenuItemIcon);
            rlRowMailLayout = (RelativeLayout) itemView.findViewById(R.id.rlRowMailLayout);
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            HungryItemHolder holder = (HungryItemHolder) view.getTag();
            int position = holder.getAdapterPosition();
            switch (position){
                case 0:
                    Intent intentFoodList = new Intent(context, SearchFoodActivity.class);
                    intentFoodList.putExtra("optionId", hungryListItems.get(position).getId());
                    //intentFoodList.putExtra("tagIds", hungryListItems.get(position).getKey());
                    intentFoodList.putExtra("food_category_type",hungryListItems.get(position).getKey());
                    intentFoodList.putExtra("tagIds", "");
                    context.startActivity(intentFoodList);
                    ((Activity)context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    break;
                case 1:
                    Intent intentNearestVendor = new Intent(context, NearVendorActivity.class);
                    intentNearestVendor.putExtra("optionId", hungryListItems.get(position).getId());
                    intentNearestVendor.putExtra("keyValue", hungryListItems.get(position).getKey());
                    context.startActivity(intentNearestVendor);
                    ((Activity)context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    break;
                case 2:
                    Intent intent = new Intent(context, VendorsActivity.class);
                    intent.putExtra("optionId", hungryListItems.get(position).getId());
                    intent.putExtra("keyValue", hungryListItems.get(position).getKey());
                    intent.putExtra("routeFrom", "home");
                    context.startActivity(intent);
                    ((Activity)context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    break;
                default:
                    Intent intentCollages = new Intent(context, CollegeActivity.class);
                    intentCollages.putExtra("optionId", hungryListItems.get(position).getId());
                    intentCollages.putExtra("routeScreen", "home");
                    context.startActivity(intentCollages);
                    ((Activity)context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    break;
            }
        }
    };
}
