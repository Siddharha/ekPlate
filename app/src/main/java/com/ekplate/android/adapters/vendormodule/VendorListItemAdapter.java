package com.ekplate.android.adapters.vendormodule;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ekplate.android.R;
import com.ekplate.android.activities.vendormodule.VendorDetailsActivity;
import com.ekplate.android.activities.vendormodule.VendorsActivity;
import com.ekplate.android.activities.vendormodule.WriteReviewActivity;
import com.ekplate.android.appinterfaces.BackgroundActionInterface;
import com.ekplate.android.models.vendormodule.VendorItem;
import com.ekplate.android.utils.CallServiceAction;
import com.ekplate.android.utils.CommonMethods;
import com.ekplate.android.utils.ConstantClass;
import com.ekplate.android.utils.NetworkConnectionCheck;
import com.ekplate.android.utils.Pref;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Rahul on 8/28/2015.
 */
public class VendorListItemAdapter extends
        RecyclerView.Adapter<VendorListItemAdapter.RatingVendorItemHolder> implements
        BackgroundActionInterface {

    private Context context;
    private ArrayList<VendorItem> vendorItems;
    private String vendorListType, routeFrom;
    private CommonMethods _commonMethods;
    private ProgressDialog progressDialog;
    private Pref _pref;
    private CallServiceAction _serviceAction;
    private NetworkConnectionCheck _connectionCheck;
    private RatingVendorItemHolder holderBookmark, holderDirection;
    private int positionSelected, positionDirection;
    private String ADD_SEARCH_ITEM = "add-search-count";

    public VendorListItemAdapter(Context context, ArrayList<VendorItem> vendorItems,
                                 String vendorListType, String routeFrom){
        this.context = context;
        this.vendorItems = vendorItems;
        this.vendorListType = vendorListType;
        this._commonMethods = new CommonMethods(context);
        this._pref = new Pref(context);
        this._connectionCheck = new NetworkConnectionCheck(context);
        this._serviceAction = new CallServiceAction(context);
        this._serviceAction.actionInterface = VendorListItemAdapter.this;
        this.routeFrom = routeFrom;
    }

    @Override
    public RatingVendorItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(context).
                inflate(R.layout.vendor_list_row_layout, parent, false);
        RatingVendorItemHolder viewHolder =
                new RatingVendorItemHolder(rowView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RatingVendorItemHolder holder, int position) {
        holder.ivVendorListIcon.setImageResource(vendorItems.get(position).getInnerCircleIcon());
        holder.tvVendorTextInRound.setText(vendorItems.get(position).getInnerCircleText());
        holder.tvDistenceVendorList.setText(vendorItems.get(position).getDistance() + "KM");

        if(vendorItems.get(position).isOpenStatus()){
            holder.tvCloseStatusVendorList.setVisibility(View.GONE);
        } else {
            holder.tvCloseStatusVendorList.setVisibility(View.VISIBLE);
        }

        if(vendorItems.get(position).isBookmarkStatus()){
            holder.ivVendorListBookmark.setImageResource(R.drawable.icon_bookmark_selected);
            vendorItems.get(position).setBookmarkStatus(true);
        } else {
            holder.ivVendorListBookmark.setImageResource(R.drawable.icon_bookmark);
            vendorItems.get(position).setBookmarkStatus(false);
        }
        holder.tvNameVendorList.setText(vendorItems.get(position).getVendorName());
        holder.tvInlineAddressVendorList.setText(vendorItems.get(position).getInlineAddress());
        holder.tvFoodMenuVendorList.setText(vendorItems.get(position).getInlineFoodMenu());

        if(position%2==0){
            holder.llVendorRowLayout.setBackgroundColor(context.getResources()
                    .getColor(R.color.vendor_item_row_bg_color2));
        } else {
            holder.llVendorRowLayout.setBackgroundColor(context.getResources()
                    .getColor(R.color.vendor_item_row_bg_color1));
        }

        _commonMethods.setRatingContainerLayoutBackground(holder.llRatingContainerVendorList,
                vendorItems.get(position).getRating());
        if(vendorListType.equals("rating")){
            //holder.llRatingContainerVendorList.setVisibility(View.GONE);
            holder.tvReviewCountVendorList.setVisibility(View.GONE);
            holder.ivRatingListLike.setVisibility(View.VISIBLE);
            holder.tvRatingListLikeCount.setVisibility(View.VISIBLE);
            holder.tvRatingListLikeCount.setText(vendorItems.get(position).getNoOfLikes() + " Likes");
        } else {
            //holder.llRatingContainerVendorList.setVisibility(View.VISIBLE);
            holder.tvReviewCountVendorList.setVisibility(View.VISIBLE);
            holder.ivRatingListLike.setVisibility(View.GONE);
            holder.tvRatingListLikeCount.setVisibility(View.GONE);
            holder.tvReviewCountVendorList.setText(vendorItems.get(position).getNoOfReviews() + " Reviews");
        }

        holder.tvRatingVendorList.setText(vendorItems.get(position).getRating());
        holder.llVendorRowLayout.setOnClickListener(onClickListener);
        holder.llVendorRowLayout.setTag(holder);

        holder.ivVendorListBookmark.setOnClickListener(bookmarkListener);
        holder.ivVendorListBookmark.setTag(holder);

        holder.llDirectionValueContainerVendorList.setOnClickListener(directionListener);
        holder.llDirectionValueContainerVendorList.setTag(holder);
    }

    @Override
    public int getItemCount() {
        return vendorItems.size();
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
                if(jsonObjData.getBoolean("success")) {

                    if (!jsonObjData.getString("msg").equals("Vendor Most Search Count Added"))
                    {
                        Toast.makeText(context, jsonObjData.getString("msg"), Toast.LENGTH_LONG).show();
                    if (vendorItems.get(positionSelected).isBookmarkStatus()) {
                        holderBookmark.ivVendorListBookmark.setImageResource(R.drawable.icon_ratting_gray);
                        vendorItems.get(positionSelected).setBookmarkStatus(false);
                    } else {
                        holderBookmark.ivVendorListBookmark.setImageResource(R.drawable.icon_ratting);
                        vendorItems.get(positionSelected).setBookmarkStatus(true);
                    }
                        progressDialog.dismiss();
                }


                }
            } else {
                Toast.makeText(context, jsonObjError.getString("errMsg"), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e){
            e.printStackTrace();
        }



    }

    public class RatingVendorItemHolder extends RecyclerView.ViewHolder{

        private LinearLayout llVendorRowLayout, llRatingContainerVendorList, llDirectionValueContainerVendorList;
        private RelativeLayout rlCloseStatus;
        private ImageView ivVendorListIcon, ivRatingListLike, ivVendorListBookmark;
        private TextView tvVendorTextInRound, tvRatingVendorList, tvReviewCountVendorList, tvRatingListLikeCount, tvDistenceVendorList,
                tvCloseStatusVendorList, tvNameVendorList, tvInlineAddressVendorList, tvFoodMenuVendorList;

        public RatingVendorItemHolder(View itemView) {
            super(itemView);
            llVendorRowLayout = (LinearLayout) itemView.findViewById(R.id.llVendorRowLayout);
            llRatingContainerVendorList = (LinearLayout) itemView.findViewById(R.id.llRatingContainerVendorList);
            //rlCloseStatus = (RelativeLayout) itemView.findViewById(R.id.rlCloseStatus);
            ivVendorListIcon = (ImageView) itemView.findViewById(R.id.ivVendorListIcon);
            ivRatingListLike = (ImageView) itemView.findViewById(R.id.ivRatingListLike);
            ivVendorListBookmark = (ImageView) itemView.findViewById(R.id.ivVendorListBookmark);
            tvVendorTextInRound = (TextView) itemView.findViewById(R.id.tvVendorTextInRound);
            tvRatingVendorList = (TextView) itemView.findViewById(R.id.tvRatingVendorList);
            tvReviewCountVendorList = (TextView) itemView.findViewById(R.id.tvReviewCountVendorList);
            tvRatingListLikeCount = (TextView) itemView.findViewById(R.id.tvRatingListLikeCount);
            tvDistenceVendorList = (TextView) itemView.findViewById(R.id.tvDistenceVendorList);
            tvCloseStatusVendorList = (TextView) itemView.findViewById(R.id.tvCloseStatusVendorList);
            tvNameVendorList = (TextView) itemView.findViewById(R.id.tvNameVendorList);
            tvInlineAddressVendorList = (TextView) itemView.findViewById(R.id.tvInlineAddressVendorList);
            tvFoodMenuVendorList = (TextView) itemView.findViewById(R.id.tvFoodMenuVendorList);
            llDirectionValueContainerVendorList = (LinearLayout) itemView.findViewById(R.id.llDirectionValueContainerVendorList);

        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            RatingVendorItemHolder holder = (RatingVendorItemHolder) view.getTag();
            int position = holder.getAdapterPosition();
            Intent intent;
            if (routeFrom.equalsIgnoreCase("menu_review")){
                intent = new Intent(context, WriteReviewActivity.class);
                intent.putExtra("routeFrom", routeFrom);
                intent.putExtra("vendorName", vendorItems.get(position).getVendorName());
                intent.putExtra("vendorId", vendorItems.get(position).getId());


                JSONObject jsonObjInnerParams = new JSONObject();

               /* try {
                    jsonObjInnerParams.put("accessToken", _pref.getSession(ConstantClass.ACCESS_TOKEN));
                    jsonObjInnerParams.put("vendorId", String.valueOf(vendorItems.get(position).getId()));
                    jsonObjInnerParams.put("foodId", "");
                    jsonObjInnerParams.put("city", _pref.getSession(ConstantClass.TAG_SELECTED_CITY_STATUS));
                    JSONObject jsonObjParams = new JSONObject();
                    jsonObjParams.put("data", jsonObjInnerParams);
                    Log.e("count data", jsonObjParams.toString());
                    _serviceAction.requestVersionApi(jsonObjParams, ADD_SEARCH_ITEM);
                }catch (JSONException e)
                {
                    e.printStackTrace();
                }*/

               // setUpMostSearchIncrementParam(vendorItems.get(position).getId());
                context.startActivity(intent);


                        ((Activity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            } else if (routeFrom.equalsIgnoreCase("menu_picture")) {
                Log.e("vendorId 4", String.valueOf(vendorItems.get(position).getId()));
                VendorsActivity.vendorId = vendorItems.get(position).getId();
                new VendorsActivity(context).selectImage();
            } else {
                intent = new Intent(context, VendorDetailsActivity.class);
                intent.putExtra("routeFrom", routeFrom);
               // intent.putExtra("vendorDetailsJsonStr", vendorItems.get(position).getVendorDetails());
               // intent.putExtra("vendorBookMark", vendorItems.get(position).isBookmarkStatus());
                intent.putExtra("vendorId", vendorItems.get(position).getId());
                /*JSONObject RecentJsonObject = new JSONObject();*/
                //{"id":1014,"keyValue":"Vendor-1014","vendorName":"Bikaner","searchType":"vendor","vendorDetails":{"vendor_id":1014,"vendor_popularity":1,"vendor_most_search":0,"mainImageUrl":"http:\/\/api.ekplate.com\/uploads\/vendor_images\/1457439116d07fff53f2d10ce27e12ebeadeada628_1456938121.jpg","noOfChecking":"0","noOfLikes":"0","noOfRating":"3.0","noOfReviews":0,"shopName":"Bikaner","longAddress":"B-78, Rd Number 13, MIG Colony, Bandra East, Mumbai, Maharashtra 400051, India","foodType":"Veg","bookmarkedStatus":"false","hyginePoint":"3","tastePoint":"3","inlineFoodMenu":"Vada Pav,Pav Bhaji","openStatus":"true","openingTime":"08:30 am","closingTime":"10:00 pm","latitude":"19.060619587550843","longitude":" 72.85020338650816","imageGallery":[{"imageId":1086,"imageUrl":"http:\/\/api.ekplate.com\/uploads\/vendor_images\/1457439116d07fff53f2d10ce27e12ebeadeada628_1456938121.jpg","imageType":1,"vendorCaption":"","imageTotalLike":0,"imageLikeStatus":"false"},{"imageId":1087,"imageUrl":"http:\/\/api.ekplate.com\/uploads\/vendor_images\/1457439129eca0914be2d30c8c389e919879eb605d_1443115088.jpg","imageType":1,"vendorCaption":"","imageTotalLike":0,"imageLikeStatus":"false"}],"longFoodMenu":[{"id":1947,"itemName":"Vada Paav-Vada Pav","itemPrice":"Rs. 10\/Plate"},{"id":1948,"itemName":"  Pavbhaji-Pav Bhaji","itemPrice":"Rs. 45\/Plate"}],"userReview":[],"myStoryDetails":[],"likeStatus":"false","distance":"0"}}

               /* try {
                    RecentJsonObject.put("id",String.valueOf(vendorItems.get(position).getId()));
                    RecentJsonObject.put("keyValue","Vendor-"+vendorItems.get(position).getId());
                    RecentJsonObject.put("vendorName",vendorItems.get(position).getVendorName());
                    RecentJsonObject.put("searchType", "vendor");
                    RecentJsonObject.put("vendorDetails", vendorItems.get(position).getVendorDetails());
                    Log.w("RecentJsonObject", RecentJsonObject.toString());
                    setRecentSearchValue(RecentJsonObject.toString(), "vendor");
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/
                context.startActivity(intent);
                ((Activity)context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        }
    };


    private void setUpProgressDialog(){
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait..");
        progressDialog.show();
    }

    private void setInputParamForBookMark(String vendorId){
        try {
            JSONObject parentJsonObj = new JSONObject();
            JSONObject childJsonObj = new JSONObject();
            childJsonObj.put("accessToken", _pref.getSession(ConstantClass.ACCESS_TOKEN));
            childJsonObj.put("vendorId", vendorId);
            if(vendorItems.get(positionSelected).isBookmarkStatus()) {
                childJsonObj.put("status", "false");
            } else {
                childJsonObj.put("status", "true");
            }
            parentJsonObj.put("data", childJsonObj);
            Log.e("input data", parentJsonObj.toString());
            _serviceAction.requestVersionApi(parentJsonObj, "add-bookmark");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    View.OnClickListener bookmarkListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (_connectionCheck.isNetworkAvailable()) {
                holderBookmark = (RatingVendorItemHolder) view.getTag();
                positionSelected = holderBookmark.getAdapterPosition();
                setUpProgressDialog();
                setInputParamForBookMark(String.valueOf(vendorItems.get(positionSelected).getId()));
            } else {
                _connectionCheck.getNetworkActiveAlert().show();
            }
        }
    };

    View.OnClickListener directionListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            holderDirection = (RatingVendorItemHolder) view.getTag();
            positionDirection = holderDirection.getAdapterPosition();
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Are you sure you want to get directed?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String uri = "http://maps.google.com/maps?saddr=" + _pref.getSession(ConstantClass.TAG_LATITUDE) + "," +
                                    _pref.getSession(ConstantClass.TAG_LONGITUDE) +
                                    "&daddr=" + vendorItems.get(positionDirection).getLatitude() + "," +
                                    vendorItems.get(positionDirection).getLongitude();
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                            intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                            context.startActivity(intent);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    };

  /*  private void setUpMostSearchIncrementParam(int VendorId){
        try {
            JSONObject parentJsonObj = new JSONObject();
            JSONObject childJsonObj = new JSONObject();
            childJsonObj.put("accessToken", _pref.getSession(ConstantClass.ACCESS_TOKEN));
            childJsonObj.put("vendorId",VendorId);
            parentJsonObj.put("data", childJsonObj);
            Log.e("input data", parentJsonObj.toString());
            _serviceAction.requestVersionApi(parentJsonObj, "add-search-count");
        } catch (Exception e){
            e.printStackTrace();
        }
    }*/

}
