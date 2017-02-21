package com.ekplate.android.adapters.vendormodule;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.ekplate.android.R;
import com.ekplate.android.models.vendormodule.UserReviewItem;

import java.util.ArrayList;

/**
 * Created by user on 15-11-2015.
 */
public class UserReviewAdapter extends RecyclerView.Adapter<UserReviewAdapter.UserReviewItemHolder> {
    private Context context;
    private ArrayList<UserReviewItem> userReviewItems;
    private ArrayList<View> rowViewList;
    private RecyclerView rvUserReviewVendorDetails;

    public ArrayList<View> getRowViewList() {
        return rowViewList;
    }

    public UserReviewAdapter(Context context, ArrayList<UserReviewItem> userReviewItems, RecyclerView rvUserReviewVendorDetails) {
        this.context = context;
        this.userReviewItems = userReviewItems;
        this.rowViewList = new ArrayList<View>();
        this.rvUserReviewVendorDetails = rvUserReviewVendorDetails;
    }

    @Override
    public UserReviewItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(context).inflate(R.layout.user_review_layout, parent, false);
        UserReviewItemHolder holder = new UserReviewItemHolder(rowView);

        return holder;
    }

    @Override
    public void onBindViewHolder(final UserReviewItemHolder holder, int position) {
        holder.itemView.post(new Runnable()
        {
            @Override
            public void run()
            {
                int cellWidth = holder.itemView.getWidth();// this will give you cell width dynamically
                int cellHeight = holder.itemView.getHeight();// this will give you cell height dynamically
                Log.e("cellHeight", String.valueOf(cellHeight));
            }
        });
    }

    @Override
    public int getItemCount() {
        return userReviewItems.size();
    }

    public class UserReviewItemHolder extends RecyclerView.ViewHolder{
        LinearLayout llUserReviewContainer;
        public UserReviewItemHolder(View itemView) {
            super(itemView);
            llUserReviewContainer = (LinearLayout) itemView.findViewById(R.id.llUserReviewContainer);
        }
    }
}
