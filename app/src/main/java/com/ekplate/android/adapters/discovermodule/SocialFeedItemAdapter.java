package com.ekplate.android.adapters.discovermodule;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ekplate.android.R;
import com.ekplate.android.activities.discovermodule.SocialFeedWebActivity;
import com.ekplate.android.models.discovermodule.SocialFeedListItem;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by user on 03-11-2015.
 */
public class SocialFeedItemAdapter extends RecyclerView.Adapter<SocialFeedItemAdapter.SocialItemHolder> {
    private Context context;
    private ArrayList<SocialFeedListItem> listItems;
    private ImageLoader imageLoader;
    SimpleDateFormat formatter;

    public SocialFeedItemAdapter(Context context, ArrayList<SocialFeedListItem> listItems) {
        this.context = context;
        this.listItems = listItems;
        formatter = new SimpleDateFormat("EE MMM d  HH:mm:ss yyyy");
    }

    @Override
    public SocialItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(context).inflate(R.layout.social_feed_row_layout, parent, false);
        SocialItemHolder holder = new SocialItemHolder(rowView);
        return holder;
    }

    @Override
    public void onBindViewHolder(final SocialItemHolder holder, final int position) {
        /*if(position%2 == 1){
            holder.ivSocialFeed.setImageResource(R.drawable.image_social_feed_1);
        } else {
            holder.ivSocialFeed.setImageResource(R.drawable.image_social_feed_2);
        }*/
        holder.tvSocialFeedHeading.setText(listItems.get(position).getHeading());
        holder.tvSocialFeedDescription.setText(listItems.get(position).getContent());
        //holder.ivSocialFeed.setImageResource(Integer.parseInt(listItems.get(position).getImageUrl()));
        if(listItems.get(position).getImageUrl()!=null) {
            imageLoader.displayImage(listItems.get(position).getImageUrl(), holder.ivSocialFeed);
        }
        if(listItems.get(position).getSocialType().equals("twitter")){
            holder.imgSocialType.setImageResource(R.drawable.icon_twitter_social_feed);
            holder.tvSocialFeedTime.setText(listItems.get(position).getPostingTime().replace("+0000", ""));
        }else if(listItems.get(position).getSocialType().equals("facebook")){
            holder.imgSocialType.setImageResource(R.drawable.icon_facebook_social_feed);
            holder.tvSocialFeedTime.setText(listItems.get(position).getPostingTime().replace("T", "  ").replace("+0000", ""));
        }else {
            holder.imgSocialType.setImageResource(R.drawable.instagram);
            String dateString = formatter.format(new Date((Integer.parseInt(listItems.get(position).getPostingTime())* 1000L)));
            holder.tvSocialFeedTime.setText(dateString);
        }
        holder.llSocialFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SocialFeedWebActivity.class);
                intent.putExtra("title",holder.tvSocialFeedHeading.getText().toString());
                if(listItems.get(position).getLink()!= null) {
                    intent.putExtra("linkurl", listItems.get(position).getLink());
                }else{
                    String url = "http://www.ekplate.com/blog";
                    intent.putExtra("linkurl", url);
                }
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class SocialItemHolder extends RecyclerView.ViewHolder {
        ImageView ivSocialFeed;
        TextView tvSocialFeedHeading, tvSocialFeedTime,tvSocialFeedDescription;
        ImageView imgSocialType;
        LinearLayout llSocialFeed;
        public SocialItemHolder(View itemView) {
            super(itemView);
            ivSocialFeed = (ImageView) itemView.findViewById(R.id.ivSocialFeed);
            tvSocialFeedHeading = (TextView) itemView.findViewById(R.id.tvSocialFeedHeading);
            tvSocialFeedTime = (TextView) itemView.findViewById(R.id.tvSocialFeedTime);
            tvSocialFeedDescription = (TextView) itemView.findViewById(R.id.tvSocialFeedDescription);
            imgSocialType = (ImageView) itemView.findViewById(R.id.imgSocialType);
            imageLoader = ImageLoader.getInstance();
            llSocialFeed = (LinearLayout) itemView.findViewById(R.id.llSocialFeed);
        }
    }
}
