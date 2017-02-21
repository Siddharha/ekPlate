package com.ekplate.android.adapters.menumodule;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.ekplate.android.R;
import com.ekplate.android.models.menumodule.MyStuffImageItem;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

/**
 * Created by user on 21-02-2016.
 */
public class MyStuffImageAdapter extends RecyclerView.Adapter<MyStuffImageAdapter.ImageItemHolder> {

    private Context context;
    private ArrayList<MyStuffImageItem> imageItems;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;

    public MyStuffImageAdapter(Context context, ArrayList<MyStuffImageItem> imageItems) {
        this.context = context;
        this.imageItems = imageItems;
        this.imageLoader = ImageLoader.getInstance();
        this.options = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .delayBeforeLoading(200)
                .showImageForEmptyUri(R.drawable.default_image_vendor_inside)
                .showImageOnFail(R.drawable.default_image_vendor_inside)
                .showImageOnLoading(R.drawable.default_image_vendor_inside)
                .build();
    }

    @Override
    public ImageItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(context).inflate(R.layout.my_stuff_image_item_layout,
                parent, false);
        ImageItemHolder holder = new ImageItemHolder(rowView);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ImageItemHolder holder, int position) {
        holder.tvImageItemTitle.setText(imageItems.get(position).getTitle());
        imageLoader.displayImage(imageItems.get(position).getImageUrl(), holder.ivImageListItem,
                options, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String s, View view) {
                        Animation anim = AnimationUtils.loadAnimation(context,
                                android.R.anim.fade_in);
                        holder.ivImageListItem.setAnimation(anim);
                        anim.start();
                    }

                    @Override
                    public void onLoadingFailed(String s, View view, FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete(String s, View view, Bitmap bitmap) {

                    }

                    @Override
                    public void onLoadingCancelled(String s, View view) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return imageItems.size();
    }

    public class ImageItemHolder extends RecyclerView.ViewHolder{
        ImageView ivImageListItem;
        TextView tvImageItemTitle;
        public ImageItemHolder(View itemView) {
            super(itemView);
            ivImageListItem = (ImageView) itemView.findViewById(R.id.ivImageListItem);
            tvImageItemTitle = (TextView) itemView.findViewById(R.id.tvImageItemTitle);
        }
    }
}
