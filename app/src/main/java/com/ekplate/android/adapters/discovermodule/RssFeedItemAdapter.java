package com.ekplate.android.adapters.discovermodule;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.ekplate.android.R;
import com.ekplate.android.activities.discovermodule.SocialFeedWebActivity;
import com.ekplate.android.models.discovermodule.RssFeedListItem;
import com.nostra13.universalimageloader.core.ImageLoader;
import java.util.ArrayList;

/**
 * Created by Avishek on 7/11/2016.
 */
public class RssFeedItemAdapter extends RecyclerView.Adapter<RssFeedItemAdapter.RssItemHolder> {
    private Context context;
    private ArrayList<RssFeedListItem> listItems;
    private ImageLoader imageLoader;

    public RssFeedItemAdapter(Context context, ArrayList<RssFeedListItem> listItems) {
        this.context = context;
        this.listItems = listItems;
    }

    @Override
    public RssItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(context).inflate(R.layout.rss_feed_row_layout, parent, false);
        RssItemHolder holder = new RssItemHolder(rowView);
        return holder;
    }

    @Override
    public void onBindViewHolder(RssItemHolder holder,  final int position) {
        /*if(position%2 == 1){
            holder.ivRssFeed.setImageResource(R.drawable.image_social_feed_1);
        } else {
            holder.ivRssFeed.setImageResource(R.drawable.image_social_feed_2);
        }*/
        if(!listItems.get(position).getImageUrl().equals("")) {
            if(!listItems.get(position).getImageUrl().isEmpty()) {
                imageLoader.displayImage(listItems.get(position).getImageUrl(), holder.ivRssFeed);
            }
           // holder.ivRssFeed.setImageResource(R.drawable.no_media);
        }else {
            holder.ivRssFeed.setImageResource(R.drawable.no_media);
        }
        holder.tvRssFeedHeading.setText(listItems.get(position).getHeading());

        Log.e("description",listItems.get(position).getContent());
       // holder.tv_rss_description.setText(Html.fromHtml(listItems.get(position).getContent()));
        holder.tv_rss_description.setVisibility(View.GONE);
        holder.tvRssFeedTime.setText(listItems.get(position).getPostingTime());
        //holder.tvRssFeedTime.setText(listItems.get(position).getPostingTime().replace("T", "  ").replace("+0000", ""));
        holder.llRssFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SocialFeedWebActivity.class);
                intent.putExtra("title", listItems.get(position).getHeading());
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

    public class RssItemHolder extends RecyclerView.ViewHolder {
        ImageView ivRssFeed;
        TextView tvRssFeedHeading, tvRssFeedTime , tv_rss_description;
        LinearLayout llRssFeed;
        public RssItemHolder(View itemView) {
            super(itemView);
            ivRssFeed = (ImageView) itemView.findViewById(R.id.ivRssFeed);
            tvRssFeedHeading = (TextView) itemView.findViewById(R.id.tvRssFeedHeading);
            tvRssFeedTime = (TextView) itemView.findViewById(R.id.tvRssFeedTime);
            tv_rss_description = (TextView) itemView.findViewById(R.id.tv_rss_description);
            llRssFeed = (LinearLayout) itemView.findViewById(R.id.llRssFeed);
            imageLoader = ImageLoader.getInstance();
        }
    }
}

