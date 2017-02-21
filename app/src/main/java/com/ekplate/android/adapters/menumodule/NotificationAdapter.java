package com.ekplate.android.adapters.menumodule;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ekplate.android.R;
import com.ekplate.android.models.menumodule.NotificationItem;
import com.ekplate.android.views.CircularImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by Rahul on 10/8/2015.
 */
public class NotificationAdapter extends
        RecyclerView.Adapter<NotificationAdapter.NotificationItemHolder> {

    private Context context;
    private ArrayList<NotificationItem> notificationItems;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;

    public NotificationAdapter(Context context, ArrayList<NotificationItem> notificationItems) {
        this.context = context;
        this.notificationItems = notificationItems;
        this.imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                    .cacheOnDisk(true)
                    .cacheInMemory(true)
                    .showImageForEmptyUri(R.drawable.default_image_vendor_inside)
                    .showImageOnFail(R.drawable.default_image_vendor_inside)
                    .showImageOnLoading(R.drawable.default_image_vendor_inside)
                    .delayBeforeLoading(200)
                    .build();
    }

    @Override
    public NotificationItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView  = LayoutInflater.from(context).inflate(R.layout.notification_list_row_layout,
                parent, false);
        NotificationItemHolder holder = new NotificationItemHolder(rowView);
        return holder;
    }

    @Override
    public void onBindViewHolder(NotificationItemHolder holder, int position) {
        imageLoader.displayImage(notificationItems.get(position).getImageUrl(), holder.civNotificationImage, options);
        holder.tvNotificationHeading.setText(notificationItems.get(position).getHeading());
        holder.tvNotificationText.setText(notificationItems.get(position).getBodyText());
        holder.tvNotificationDate.setText(notificationItems.get(position).getTime());

        holder.llNotificationItemListContainer.setOnClickListener(onClickListener);
        holder.llNotificationItemListContainer.setTag(holder);
    }

    @Override
    public int getItemCount() {
        return notificationItems.size();
    }

    public class NotificationItemHolder extends RecyclerView.ViewHolder{

        CircularImageView civNotificationImage;
        TextView tvNotificationHeading, tvNotificationText, tvNotificationDate;
        LinearLayout llNotificationItemListContainer;

        public NotificationItemHolder(View itemView) {
            super(itemView);
            civNotificationImage = (CircularImageView) itemView.findViewById(R.id.civNotificationImage);
            tvNotificationHeading = (TextView) itemView.findViewById(R.id.tvNotificationHeading);
            tvNotificationText = (TextView) itemView.findViewById(R.id.tvNotificationText);
            tvNotificationDate = (TextView) itemView.findViewById(R.id.tvNotificationDate);
            llNotificationItemListContainer = (LinearLayout) itemView.findViewById(R.id.llNotificationItemListContainer);
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            NotificationItemHolder holder = (NotificationItemHolder) view.getTag();
            int position = holder.getAdapterPosition();
            initializePopup(view, position);
        }
    };

    private void initializePopup(View itemView, int position){
        final PopupWindow infoPopup;
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View infoPopupLayout = layoutInflater.inflate(R.layout.notification_popup_layout,
                (ViewGroup) itemView.findViewById(R.id.llNotificationPopup));
        TextView tvNotificationHeading = (TextView) infoPopupLayout.findViewById(R.id.tvNotificationHeading);
        TextView tvNotificationText = (TextView) infoPopupLayout.findViewById(R.id.tvNotificationText);
        ImageView ivNotificationPopupCross = (ImageView) infoPopupLayout.findViewById(R.id.ivNotificationPopupCross);
        tvNotificationHeading.setText(notificationItems.get(position).getHeading());
        tvNotificationText.setText(notificationItems.get(position).getBodyText());
        infoPopup = new PopupWindow(infoPopupLayout, 480, 580, true);
        infoPopup.showAtLocation(infoPopupLayout, Gravity.CENTER, 0, 40);
        ivNotificationPopupCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoPopup.dismiss();
            }
        });
    }
}
