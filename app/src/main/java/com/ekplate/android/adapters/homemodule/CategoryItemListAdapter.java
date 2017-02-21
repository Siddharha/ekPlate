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
import com.ekplate.android.activities.foodmodule.SearchFoodActivity;
import com.ekplate.android.models.homemodule.CategoryListItem;

import java.util.ArrayList;

/**
 * Created by Rahul on 9/7/2015.
 */
public class CategoryItemListAdapter extends
        RecyclerView.Adapter<CategoryItemListAdapter.CategoryItemHolder> {

    private Context context;
    private ArrayList<CategoryListItem> categoryListItems;

    public CategoryItemListAdapter(Context context, ArrayList<CategoryListItem> categoryListItems){
        this.context = context;
        this.categoryListItems = categoryListItems;
    }

    @Override
    public CategoryItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(context).
                inflate(R.layout.home_options_row_layout, parent, false);
        CategoryItemHolder viewHolder = new CategoryItemHolder(rowView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CategoryItemHolder holder, int position) {
        holder.tvHomeMenuItemTitle.setText(categoryListItems.get(position).getTitle());
        holder.ivHomeMenuItemIcon.setImageResource(categoryListItems.get(position).getImageUrl());
        holder.rlRowMailLayout.setOnClickListener(onClickListener);
        holder.rlRowMailLayout.setTag(holder);
    }

    @Override
    public int getItemCount() {
        return categoryListItems.size();
    }

    public class CategoryItemHolder extends RecyclerView.ViewHolder{

        TextView tvHomeMenuItemTitle;
        ImageView ivHomeMenuItemIcon;
        RelativeLayout rlRowMailLayout;

        public CategoryItemHolder(View itemView) {
            super(itemView);
            tvHomeMenuItemTitle = (TextView) itemView.findViewById(R.id.tvHomeMenuItemTitle);
            ivHomeMenuItemIcon = (ImageView) itemView.findViewById(R.id.ivHomeMenuItemIcon);
            rlRowMailLayout = (RelativeLayout) itemView.findViewById(R.id.rlRowMailLayout);
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            CategoryItemHolder holder = (CategoryItemHolder) view.getTag();
            int position = holder.getAdapterPosition();
            Intent intentFoodList = new Intent(context, SearchFoodActivity.class);
            intentFoodList.putExtra("optionId", categoryListItems.get(position).getId());
            intentFoodList.putExtra("keyValue", categoryListItems.get(position).getKey());
            intentFoodList.putExtra("tagIds", categoryListItems.get(position).getTagIds());
            intentFoodList.putExtra("food_category_type",categoryListItems.get(position).getKey());
            context.startActivity(intentFoodList);
            ((Activity)context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    };

}
