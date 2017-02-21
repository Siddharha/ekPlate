package com.ekplate.android.adapters.vendormodule;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ekplate.android.R;
import com.ekplate.android.activities.vendormodule.VendorDetailsActivity;
import com.ekplate.android.models.vendormodule.ClusterPopupListItem;
import com.ekplate.android.utils.CommonMethods;

import java.util.ArrayList;

/**
 * Created by user on 18-11-2015.
 */
public class ClusterPopupListAdapter extends RecyclerView.Adapter<ClusterPopupListAdapter.ClusterPopupListHolder> {
    private Context context;
    private CommonMethods _commonMethods;

    public ClusterPopupListAdapter(Context context, ArrayList<ClusterPopupListItem> listItems) {
        this.context = context;
        this.listItems = listItems;
        _commonMethods = new CommonMethods(context);
    }

    private ArrayList<ClusterPopupListItem> listItems;

    @Override
    public ClusterPopupListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(context).inflate(R.layout.cluster_popup_list_layout, parent, false);
        ClusterPopupListHolder holder = new ClusterPopupListHolder(rowView);
        return holder;
    }

    @Override
    public void onBindViewHolder(ClusterPopupListHolder holder, int position) {
        holder.tvVendorNameClusterPopup.setText(listItems.get(position).getVendorName());
        holder.tvVendorAddressClusterPopup.setText(listItems.get(position).getVendorAddress());
        holder.tvVendorFoodItemClusterPopup.setText(listItems.get(position).getFoodItem());
        holder.tvVendorDistanceClusterPopup.setText(listItems.get(position).getDistance());
        holder.tvVendorNoOfLikesClusterPopup.setText(listItems.get(position).getNoOfLikes() + " Likes");
        holder.tvVendorRatingClusterPopup.setText(listItems.get(position).getRating());
        holder.tvVendorReviewsClusterPopup.setText(listItems.get(position).getNoOfReviews() + " Reviews");
        _commonMethods.setRatingContainerLayoutBackground(holder.llRatingContainerLayoutClusterPopup,
                listItems.get(position).getRating());
        holder.llClusterItemContainer.setOnClickListener(onClickListener);
        holder.llClusterItemContainer.setTag(holder);

    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class ClusterPopupListHolder extends RecyclerView.ViewHolder{
        TextView tvVendorNameClusterPopup, tvVendorAddressClusterPopup, tvVendorFoodItemClusterPopup,
                tvVendorDistanceClusterPopup, tvVendorNoOfLikesClusterPopup, tvVendorRatingClusterPopup,
                tvVendorReviewsClusterPopup;
        LinearLayout llRatingContainerLayoutClusterPopup, llClusterItemContainer;
        public ClusterPopupListHolder(View itemView) {
            super(itemView);
            tvVendorNameClusterPopup = (TextView) itemView.findViewById(R.id.tvVendorNameClusterPopup);
            tvVendorAddressClusterPopup = (TextView) itemView.findViewById(R.id.tvVendorAddressClusterPopup);
            tvVendorFoodItemClusterPopup = (TextView) itemView.findViewById(R.id.tvVendorFoodItemClusterPopup);
            tvVendorDistanceClusterPopup = (TextView) itemView.findViewById(R.id.tvVendorDistanceClusterPopup);
            tvVendorNoOfLikesClusterPopup = (TextView) itemView.findViewById(R.id.tvVendorNoOfLikesClusterPopup);
            tvVendorRatingClusterPopup = (TextView) itemView.findViewById(R.id.tvVendorRatingClusterPopup);
            tvVendorReviewsClusterPopup = (TextView) itemView.findViewById(R.id.tvVendorReviewsClusterPopup);
            llRatingContainerLayoutClusterPopup = (LinearLayout) itemView.findViewById(R.id.llRatingContainerLayoutClusterPopup);
            llClusterItemContainer = (LinearLayout) itemView.findViewById(R.id.llClusterItemContainer);
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ClusterPopupListHolder holder = (ClusterPopupListHolder) view.getTag();
            int position = holder.getAdapterPosition();
            Intent intent = new Intent(context, VendorDetailsActivity.class);
            intent.putExtra("vendorDetailsJsonStr", listItems.get(position).getVendorDetails());
            intent.putExtra("vendorId", listItems.get(position).getId());
            intent.putExtra("routeFrom", "vendor_map");
            context.startActivity(intent);
            ((Activity)context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    };
}
