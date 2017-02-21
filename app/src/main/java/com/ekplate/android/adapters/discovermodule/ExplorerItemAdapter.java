package com.ekplate.android.adapters.discovermodule;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ekplate.android.R;
import com.ekplate.android.activities.discovermodule.VendorStoriesActivity;
import com.ekplate.android.models.discovermodule.ExplorerItem;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by Avishek on 8/4/2016.
 */
public class ExplorerItemAdapter extends BaseAdapter {
    private ArrayList<ExplorerItem> explorerItems;
    private Context context;
    private LayoutInflater inflater;
    private ImageLoader imageLoader;

    public ExplorerItemAdapter(Context context, ArrayList<ExplorerItem> explorerItems) {
        this.context = context;
        this.explorerItems = explorerItems;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return explorerItems.size();
    }

    @Override
    public ExplorerItem getItem(int position) {
        return explorerItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rootview = convertView;
        if(rootview==null){
            rootview = inflater.inflate(R.layout.explorer_item_grid_layout,parent,false);
            ItemHolder holder = new ItemHolder();
            holder.llImages = (LinearLayout)rootview.findViewById(R.id.llImages);
            holder.tvTitle = (TextView)rootview.findViewById(R.id.tvTitle);
            holder.imgImages = (ImageView)rootview.findViewById(R.id.imgImages);
            imageLoader=ImageLoader.getInstance();
            rootview.setTag(holder);
        }
        ItemHolder myholder = (ItemHolder)rootview.getTag();
        myholder.tvTitle.setText(explorerItems.get(position).getTitle());
        imageLoader.displayImage(explorerItems.get(position).getImage(), myholder.imgImages);
        myholder.llImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, VendorStoriesActivity.class);
                if(explorerItems.get(position).getUrl()!= null) {
                    intent.putExtra("link", explorerItems.get(position).getUrl());
                }else{
                    String url = "http://www.ekplate.com/blog";
                    intent.putExtra("link", url);
                }
                intent.putExtra("title", explorerItems.get(position).getTitle());
                context.startActivity(intent);
            }
        });
        return rootview;
    }
    private class ItemHolder{
        LinearLayout llImages;
        TextView tvTitle;
        ImageView imgImages;

    }
}
