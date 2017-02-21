package com.ekplate.android.adapters.homemodule;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ekplate.android.R;
import com.ekplate.android.activities.discovermodule.ExploreActivity;
import com.ekplate.android.activities.discovermodule.GaidosPickActivity;
import com.ekplate.android.activities.discovermodule.HelpMeEatActivity;
import com.ekplate.android.activities.discovermodule.SocialFeedActivity;
import com.ekplate.android.activities.discovermodule.SocialFeedChooseMediaActivity;
import com.ekplate.android.models.homemodule.DiscoverListItem;

import java.util.ArrayList;

/**
 * Created by Rahul on 9/7/2015.
 */
public class DiscoverItemListAdapter extends
        RecyclerView.Adapter<DiscoverItemListAdapter.DiscoverItemHolder> {

    private Context context;
    private ArrayList<DiscoverListItem> discoverListItems;
    private PopupWindow popupWindow;
    private View viewPopup;
    public DiscoverItemListAdapter(Context context, ArrayList<DiscoverListItem> discoverListItems){
        this.context = context;
        this.discoverListItems = discoverListItems;

    }

    @Override
    public DiscoverItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(context).
                inflate(R.layout.home_options_row_layout, parent, false);
        DiscoverItemListAdapter.DiscoverItemHolder viewHolder = new DiscoverItemHolder(rowView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(DiscoverItemHolder holder, int position) {
        holder.tvHomeMenuItemTitle.setText(discoverListItems.get(position).getTitle());
        holder.ivHomeMenuItemIcon.setImageResource(discoverListItems.get(position).getImageUrl());
        holder.rlRowMailLayout.setOnClickListener(onClickListener);
        holder.rlRowMailLayout.setTag(holder);
    }

    @Override
    public int getItemCount() {
        return discoverListItems.size();
    }

    public class DiscoverItemHolder extends RecyclerView.ViewHolder{
        TextView tvHomeMenuItemTitle;
        ImageView ivHomeMenuItemIcon;
        RelativeLayout rlRowMailLayout;

        public DiscoverItemHolder(View itemView) {
            super(itemView);
            tvHomeMenuItemTitle = (TextView) itemView.findViewById(R.id.tvHomeMenuItemTitle);
            ivHomeMenuItemIcon = (ImageView) itemView.findViewById(R.id.ivHomeMenuItemIcon);
            rlRowMailLayout = (RelativeLayout) itemView.findViewById(R.id.rlRowMailLayout);
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            DiscoverItemHolder holder = (DiscoverItemHolder) view.getTag();
            int position = holder.getAdapterPosition();
            switch (position){
                case 0:
                    context.startActivity(new Intent(context, GaidosPickActivity.class));
                    ((Activity)context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    break;
                case 1:
                    context.startActivity(new Intent(context, HelpMeEatActivity.class));
                    ((Activity)context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    break;
                case 2:
                    //context.startActivity(new Intent(context, SocialFeedActivity.class));
                    context.startActivity(new Intent(context, SocialFeedChooseMediaActivity.class));
                    ((Activity)context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                   // chooseMediaChannelPopup(view).showAsDropDown(viewPopup);
                    break;
                default:
                    context.startActivity(new Intent(context, ExploreActivity.class));
                    ((Activity)context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    break;
            }
        }
    };

    public PopupWindow chooseMediaChannelPopup(View view) {
        LayoutInflater layoutinflater = (LayoutInflater)this.context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        viewPopup = layoutinflater.inflate(R.layout.media_feed_chooser_layour,
                null);
        popupWindow = new PopupWindow(viewPopup,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        //popupWindow =new PopupWindow(context);
       // dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //dialog.setContentView(R.layout.media_feed_chooser_layour);
        //dialog.getWindow().setFeatureInt(Window.FEATURE_NO_TITLE, R.layout.feed_media_chooser_title_layout);
        //dialog.setContentView(R.layout.media_feed_chooser_layour);
        LinearLayout llFbFeed = (LinearLayout) viewPopup.findViewById(R.id.llFbFeed);
        LinearLayout llTTFeed = (LinearLayout) viewPopup.findViewById(R.id.llTTFeed);
        LinearLayout llIGFeed = (LinearLayout) viewPopup.findViewById(R.id.llIGFeed);
        //popupWindow.setCancelable(true);
        llFbFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SocialFeedActivity.class);
                intent.putExtra("mediaChannel", 1);
                context.startActivity(intent);
                ((Activity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        llTTFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SocialFeedActivity.class);
                intent.putExtra("mediaChannel",2);
                context.startActivity(intent);
                ((Activity)context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        llIGFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SocialFeedActivity.class);
                intent.putExtra("mediaChannel", 3);
                context.startActivity(intent);
                ((Activity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
       return popupWindow;
    }

    }
