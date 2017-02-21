package com.ekplate.android.adapters.collegemodule;

import android.app.Activity;
import android.app.ProgressDialog;
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
import android.widget.Toast;

import com.ekplate.android.R;
import com.ekplate.android.activities.menumodule.AccountSettingActivity;
import com.ekplate.android.activities.vendormodule.VendorsActivity;
import com.ekplate.android.appinterfaces.BackgroundActionInterface;
import com.ekplate.android.models.collegemodule.CollageListItem;
import com.ekplate.android.models.vendormodule.AddCollegeItem;
import com.ekplate.android.utils.CallServiceAction;
import com.ekplate.android.utils.ConstantClass;
import com.ekplate.android.utils.NetworkConnectionCheck;
import com.ekplate.android.utils.Pref;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Rahul on 9/15/2015.
 */
public class CollegeListAdapter extends RecyclerView.Adapter<CollegeListAdapter.MostLikedCollageListViewHolder>
        implements BackgroundActionInterface {
    private Context context;
    private ArrayList<CollageListItem> collegeItems;
    private ArrayList<MostLikedCollageListViewHolder> holderArrayList;
    private Pref _pref;
    private NetworkConnectionCheck _connectionCheck;
    private CallServiceAction _serviceAction;
    private ProgressDialog progressDialog;
    private boolean collageLikeStatus;
    private int position;
    private String routeScreen;


    public CollegeListAdapter(Context context, ArrayList<CollageListItem> collegeItems, String routeScreen) {
        this.context = context;
        this.collegeItems = collegeItems;
        this.routeScreen = routeScreen;
        _pref = new Pref(context);
        _connectionCheck = new NetworkConnectionCheck(context);
        holderArrayList = new ArrayList<>();
        _serviceAction = new CallServiceAction(context);
        _serviceAction.actionInterface = CollegeListAdapter.this;
    }

    @Override
    public MostLikedCollageListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(context).inflate(R.layout.collage_list_row_layout,
                parent, false);
        MostLikedCollageListViewHolder collageListItemHolder = new MostLikedCollageListViewHolder(rowView);
        return collageListItemHolder;
    }

    @Override
    public void onBindViewHolder(MostLikedCollageListViewHolder holder, int position) {
        holder.tvCollageTitle.setText(collegeItems.get(position).getTitle());
        holder.tvCollageAddress.setText(collegeItems.get(position).getAddress());
        holder.tvCollageDistance.setText(collegeItems.get(position).getDistance());
        if (collegeItems.get(position).isLikeStatus()){
            holder.ivLikeStatus.setImageResource(R.drawable.icon_like_collage_selected);
        } else {
            holder.ivLikeStatus.setImageResource(R.drawable.icon_like_collage);
        }
        if(position%2==0){
            holder.llVendorRowLayout.setBackgroundColor(context.getResources().getColor(R.color.white));
        } else {
            holder.llVendorRowLayout.setBackgroundColor(context.getResources().getColor(R.color.list_row_color_2));
        }
        holder.ivLikeStatus.setTag(holder);
        holder.ivLikeStatus.setOnClickListener(imageLikeListener);
        holder.llVendorRowLayout.setTag(holder);
        holder.llVendorRowLayout.setOnClickListener(listItemClickListener);
        holderArrayList.add(holder);
    }

    @Override
    public int getItemCount() {
        return collegeItems.size();
    }

    @Override
    public void onStarted() {

    }

    @Override
    public void onCompleted(JSONObject response) {
        Log.e("response", response.toString());
        try {
            JSONObject jsonObjError = response.getJSONObject("errNode");
            if (jsonObjError.getInt("errCode") == 0) {
                JSONObject jsonObjData = response.getJSONObject("data");
                if(jsonObjData.getBoolean("success")){
                    Toast.makeText(context, jsonObjData.getString("msg"), Toast.LENGTH_LONG).show();
                    if(collageLikeStatus){
                        holderArrayList.get(position).ivLikeStatus.setImageResource(R.drawable.icon_like_collage);
                        collegeItems.get(position).setLikeStatus(false);
                    } else {
                        holderArrayList.get(position).ivLikeStatus.setImageResource(R.drawable.icon_like_collage_selected);
                        collegeItems.get(position).setLikeStatus(true);
                    }

                    this.notifyDataSetChanged();
                }
            } else {
                Toast.makeText(context, jsonObjError.getString("errMsg"), Toast.LENGTH_LONG).show();
            }
            progressDialog.dismiss();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public class MostLikedCollageListViewHolder extends RecyclerView.ViewHolder {
        TextView tvCollageTitle, tvCollageAddress, tvCollageDistance;
        LinearLayout llVendorRowLayout;
        ImageView ivLikeStatus;

        public MostLikedCollageListViewHolder(View itemView) {
            super(itemView);
            llVendorRowLayout = (LinearLayout) itemView.findViewById(R.id.llVendorRowLayout);
            tvCollageTitle = (TextView) itemView.findViewById(R.id.tvCollageTitle);
            tvCollageAddress = (TextView) itemView.findViewById(R.id.tvCollageAddress);
            tvCollageDistance = (TextView) itemView.findViewById(R.id.tvCollageDistance);
            ivLikeStatus = (ImageView) itemView.findViewById(R.id.ivLikeStatus);
        }
    }

    View.OnClickListener imageLikeListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(_connectionCheck.isNetworkAvailable()) {
                MostLikedCollageListViewHolder collageListItemHolder = (MostLikedCollageListViewHolder) view.getTag();
                position = collageListItemHolder.getAdapterPosition();
                collageLikeStatus = collegeItems.get(position).isLikeStatus();
                setCollageLikeParameter(collegeItems.get(position).getId(),
                        collegeItems.get(position).isLikeStatus());
                setUpProgressDialog();
            } else {
                Toast.makeText(context, "Internet connection is not available", Toast.LENGTH_LONG).show();
            }
        }
    };

    View.OnClickListener listItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            MostLikedCollageListViewHolder collageListItemHolder = (MostLikedCollageListViewHolder) view.getTag();
            position = collageListItemHolder.getAdapterPosition();
            if (routeScreen.equalsIgnoreCase("profile")){
                int flag = 0;
                Intent intentProfile = new Intent(context, AccountSettingActivity.class);
                AddCollegeItem _item = new AddCollegeItem();
                _item.setId(collegeItems.get(position).getId());
                _item.setCollegeTitle(collegeItems.get(position).getTitle());
                for(int i=0; i< ConstantClass.addCollegeItems.size(); i++){
                    if(collegeItems.get(position).getId() ==
                            ConstantClass.addCollegeItems.get(i).getId()){
                        flag = 1;
                        break;
                    }
                }
                if (flag == 0) {
                    ConstantClass.addCollegeItems.add(_item);
                } else {
                    Toast.makeText(context, "This college all ready added", Toast.LENGTH_LONG).show();
                }
                intentProfile.putExtra("routeScreenForAccountSetting", "college");
                context.startActivity(intentProfile);
            } else if(routeScreen.equalsIgnoreCase("home")) {
                Intent intent = new Intent(context, VendorsActivity.class);
                intent.putExtra("optionId", collegeItems.get(position).getId());
                intent.putExtra("keyValue", collegeItems.get(position).getKeyValue());
                intent.putExtra("routeFrom", "college_list");
                context.startActivity(intent);
                ((Activity)context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        }
    };

    private void setCollageLikeParameter(int collageId, boolean likeStatus){
        try {
            JSONObject parentJsonObj = new JSONObject();
            JSONObject childJsonObj = new JSONObject();
            childJsonObj.put("accessToken", _pref.getSession(ConstantClass.ACCESS_TOKEN));
            childJsonObj.put("collageId", collageId);
            if(likeStatus) {
                childJsonObj.put("likeStatus", "false");
            } else {
                childJsonObj.put("likeStatus", "true");
            }
            parentJsonObj.put("data", childJsonObj);
            Log.e("input data", parentJsonObj.toString());
            _serviceAction.requestVersionApi(parentJsonObj, "collage-like");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setUpProgressDialog(){
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait..");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
}
