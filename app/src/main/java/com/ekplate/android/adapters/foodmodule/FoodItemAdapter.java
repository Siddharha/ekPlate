package com.ekplate.android.adapters.foodmodule;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.exceptions.CleverTapMetaDataNotFoundException;
import com.clevertap.android.sdk.exceptions.CleverTapPermissionsNotSatisfied;
import com.ekplate.android.R;
import com.ekplate.android.activities.vendormodule.VendorsActivity;
import com.ekplate.android.models.foodmodule.FoodItem;
import com.ekplate.android.utils.ConstantClass;
import com.ekplate.android.utils.Pref;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Rahul on 8/26/2015.
 */
public class FoodItemAdapter extends RecyclerView.Adapter
        <FoodItemAdapter.FoodItemHolder> {

    private Context context;
    private ArrayList<FoodItem> foodItems;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private Pref _pref;
    private String foodCategoryType;
    CleverTapAPI cleverTap;

    public FoodItemAdapter(Context context, ArrayList<FoodItem> foodItems, String foodCategoryType){
        this.context = context;
        this.foodItems = foodItems;
        this.foodCategoryType = foodCategoryType;
        _pref = new Pref(context);
        this.imageLoader = ImageLoader.getInstance();
        this.options = new DisplayImageOptions.Builder()
                            .cacheOnDisk(true)
                            .cacheInMemory(true)
                            .showImageForEmptyUri(R.drawable.default_image_vendor_inside)
                            .showImageOnFail(R.drawable.default_image_vendor_inside)
                            .showImageOnLoading(R.drawable.default_image_vendor_inside)
                            .build();
        try {
            cleverTap = CleverTapAPI.getInstance(context);
        } catch (CleverTapMetaDataNotFoundException e) {
            // thrown if you haven't specified your CleverTap Account ID or Token in your AndroidManifest.xml
        } catch (CleverTapPermissionsNotSatisfied e) {
            // thrown if you haven’t requested the required permissions in your AndroidManifest.xml
        }
    }

    @Override
    public FoodItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(context).
                inflate(R.layout.search_food_item_layout, parent, false);
        FoodItemHolder viewHolder =
                new FoodItemHolder(rowView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final FoodItemHolder holder, int position) {
        holder.tvItemTitleFoodList.setText(foodItems.get(position).getFoodTitle());
        if (foodItems.get(position).getFoodType().equalsIgnoreCase("veg")){
            holder.ivFoodTypeFoodList.setImageResource(R.drawable.icon_veg);
        } else {
            holder.ivFoodTypeFoodList.setImageResource(R.drawable.icon_nonveg);
        }
        imageLoader.displayImage(foodItems.get(position).getFoodItemImageUrl(), holder.ivFoodItemFoodList,
                options, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String s, View view) {
                        Animation anim = AnimationUtils.loadAnimation(context,
                                android.R.anim.fade_in);
                        holder.ivFoodItemFoodList.setAnimation(anim);

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


        holder.ivFoodInfoImage.setTag(holder);
        holder.ivFoodInfoImage.setOnClickListener(infoListener);
        holder.flFoodListItemContainer.setTag(holder);
        holder.flFoodListItemContainer.setOnClickListener(listenerForFoodItem);

    }

    @Override
    public int getItemCount() {
        return foodItems.size();
    }

    public class FoodItemHolder extends RecyclerView.ViewHolder{

        private ImageView ivFoodItemFoodList, ivFoodInfoImage, ivFoodTypeFoodList;
        private TextView tvItemTitleFoodList;
        private FrameLayout flFoodListItemContainer;
        
        public FoodItemHolder(View itemView) {
            super(itemView);
            ivFoodItemFoodList = (ImageView) itemView.findViewById(R.id.ivFoodItemFoodList);
            tvItemTitleFoodList = (TextView) itemView.findViewById(R.id.tvItemTitleFoodList);
            ivFoodInfoImage = (ImageView) itemView.findViewById(R.id.ivFoodInfoImage);
            ivFoodTypeFoodList = (ImageView) itemView.findViewById(R.id.ivFoodTypeFoodList);
            flFoodListItemContainer = (FrameLayout) itemView.findViewById(R.id.flFoodListItemContainer);
        }
    }

    View.OnClickListener infoListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            FoodItemHolder holder = (FoodItemHolder) view.getTag();
            int position = holder.getAdapterPosition();
            if(!foodItems.get(position).getFoodDescription().equals("")){
                initializePopup(view, position);
            } else {
                Toast.makeText(context, "Food info is not available", Toast.LENGTH_LONG).show();
            }
        }
    };

    private void initializePopup(View itemView, int position){
        final PopupWindow infoPopup;

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View infoPopupLayout = layoutInflater.inflate(R.layout.food_info_popup_layout, (ViewGroup) itemView.findViewById(R.id.llPopupWindow));
        ImageView ivPopupCross = (ImageView) infoPopupLayout.findViewById(R.id.ivPopupCross);
        TextView tvFoodItemName = (TextView) infoPopupLayout.findViewById(R.id.tvFoodItemName);
        TextView tvFoodItemDescription = (TextView) infoPopupLayout.findViewById(R.id.tvFoodItemDescription);
        tvFoodItemName.setText(foodItems.get(position).getFoodTitle());
        tvFoodItemDescription.setText(foodItems.get(position).getFoodDescription());
        infoPopup = new PopupWindow(infoPopupLayout, 480, 580, true);
        infoPopup.setOutsideTouchable(true);
        infoPopup.setTouchable(true);
        infoPopup.setFocusable(true);
        infoPopup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        infoPopup.showAtLocation(infoPopupLayout, Gravity.CENTER, 0, 40);
        ivPopupCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoPopup.dismiss();
            }
        });
    }

    private void setRecentSearchValue(String jsonObjString, String itemType) {
        Log.v("jsonObjString", jsonObjString);
        try {
            int id;
            if (itemType.equalsIgnoreCase("vendor")) {
                JSONObject vendorJsonObj = new JSONObject(jsonObjString);
                id = vendorJsonObj.getInt("id");
            } else {
                JSONObject vendorJsonObj = new JSONObject(jsonObjString);
                id = vendorJsonObj.getInt("id");
            }
            switch (_pref.getIntegerSession(ConstantClass.TAG_RECENT_TOP_INDEX)) {
                case 0:
                    _pref.setSession(ConstantClass.TAG_RECENT_TOP_INDEX, 1);
                    _pref.setSession(ConstantClass.TAG_RECENT_BOTTOM_INDEX, 1);
                    _pref.setSession(ConstantClass.TAG_RECENT_SEARCHES_FIRST, jsonObjString);
                    break;
                case 1:
                    if (isRecentSearchItemExist(id, itemType)) {
                        _pref.setSession(ConstantClass.TAG_RECENT_TOP_INDEX, 2);
                        _pref.setSession(ConstantClass.TAG_RECENT_SEARCHES_SECOND, jsonObjString);
                    }
                    break;
                case 2:
                    if (isRecentSearchItemExist(id, itemType)) {
                        _pref.setSession(ConstantClass.TAG_RECENT_TOP_INDEX, 3);
                        _pref.setSession(ConstantClass.TAG_RECENT_SEARCHES_THIRD, jsonObjString);
                    }
                    break;
                case 3:
                    if (isRecentSearchItemExist(id, itemType)) {
                        _pref.setSession(ConstantClass.TAG_RECENT_TOP_INDEX, 4);
                        _pref.setSession(ConstantClass.TAG_RECENT_SEARCHES_FOURTH, jsonObjString);
                    }
                    break;
                default:
                    switch (_pref.getIntegerSession(ConstantClass.TAG_RECENT_BOTTOM_INDEX)) {
                        case 1:
                            if (isRecentSearchItemExist(id, itemType)) {
                                _pref.setSession(ConstantClass.TAG_RECENT_SEARCHES_FIRST, jsonObjString);
                                _pref.setSession(ConstantClass.TAG_RECENT_BOTTOM_INDEX, 2);
                            }
                            break;
                        case 2:
                            if (isRecentSearchItemExist(id, itemType)) {
                                _pref.setSession(ConstantClass.TAG_RECENT_SEARCHES_SECOND, jsonObjString);
                                _pref.setSession(ConstantClass.TAG_RECENT_BOTTOM_INDEX, 3);
                            }
                            break;
                        case 3:
                            if (isRecentSearchItemExist(id, itemType)) {
                                _pref.setSession(ConstantClass.TAG_RECENT_SEARCHES_THIRD, jsonObjString);
                                _pref.setSession(ConstantClass.TAG_RECENT_BOTTOM_INDEX, 4);
                            }
                            break;
                        case 4:
                            if (isRecentSearchItemExist(id, itemType)) {
                                _pref.setSession(ConstantClass.TAG_RECENT_SEARCHES_FOURTH, jsonObjString);
                                _pref.setSession(ConstantClass.TAG_RECENT_BOTTOM_INDEX, 1);
                            }
                            break;
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isRecentSearchItemExist(int id, String itemType){
        boolean exist = true;
        String storedJsonObjStr;
        try {
            for (int i=1; i<=4; i++) {
                switch (i) {
                    case 1:
                        storedJsonObjStr = _pref.getSession(ConstantClass.TAG_RECENT_SEARCHES_FIRST);
                        break;
                    case 2:
                        storedJsonObjStr = _pref.getSession(ConstantClass.TAG_RECENT_SEARCHES_SECOND);
                        break;
                    case 3:
                        storedJsonObjStr = _pref.getSession(ConstantClass.TAG_RECENT_SEARCHES_THIRD);
                        break;
                    default:
                        storedJsonObjStr = _pref.getSession(ConstantClass.TAG_RECENT_SEARCHES_FOURTH);
                        break;
                }
                if (!storedJsonObjStr.equals("")) {
                    if (itemType.equalsIgnoreCase("vendor")) {
                        JSONObject storedJsonObj = new JSONObject(storedJsonObjStr);
                        if (id == storedJsonObj.getInt("id")) {
                            exist = false;
                            Log.e("exist", exist + "");
                            break;
                        }
                    } else {
                        JSONObject storedJsonObj = new JSONObject(storedJsonObjStr);
                        if (id == storedJsonObj.getInt("id")) {
                            exist = false;
                            Log.e("exist", exist + "");
                            break;
                        }
                    }
                } else {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return exist;
    }
    
    View.OnClickListener listenerForFoodItem = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            FoodItemHolder holder = (FoodItemHolder) view.getTag();
            int position = holder.getAdapterPosition();
            Intent intent = new Intent(context, VendorsActivity.class);
            intent.putExtra("optionId", foodItems.get(position).getId());
            intent.putExtra("keyValue", foodItems.get(position).getKeyValue());
            intent.putExtra("foodCategoryType",foodCategoryType);
            intent.putExtra("routeFrom", "food_list");
            //{"id":136,"foodName":"Samosa ","searchType":"food","keyValue":"food-samosa ","foodImgUrl":"http:\/\/api.ekplate.com\/uploads\/food_images\/136_767_Samosa .jpg"}

            JSONObject RecentJsonObject = new JSONObject();
            //JSONObject jsonObjectChild = new JSONObject();
            try {
                RecentJsonObject.put("id",String.valueOf(foodItems.get(position).getId()));
                RecentJsonObject.put("foodName",foodItems.get(position).getFoodTitle());
                RecentJsonObject.put("searchType","food");
                RecentJsonObject.put("keyValue",foodItems.get(position).getKeyValue());
                RecentJsonObject.put("foodImgUrl",foodItems.get(position).getFoodItemImageUrl());

                setRecentSearchValue(RecentJsonObject.toString(), "food");


            } catch (JSONException e) {
                e.printStackTrace();
            }
            HashMap<String, Object> prodViewedAction = new HashMap<String, Object>();
            prodViewedAction.put("Food Name",foodItems.get(position).getFoodTitle());
            prodViewedAction.put("Category", "foodCategoryType");
            prodViewedAction.put("Date", new java.util.Date());
            cleverTap.event.push("Food viewed", prodViewedAction);
            context.startActivity(intent);
            ((Activity)context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    };
}
