package com.ekplate.android.adapters.searchmodule;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.ekplate.android.R;
import com.ekplate.android.activities.vendormodule.VendorDetailsActivity;
import com.ekplate.android.activities.vendormodule.VendorsActivity;
import com.ekplate.android.models.searchmodule.CollegeSearchResultItem;
import com.ekplate.android.models.searchmodule.FoodSearchResultItem;
import com.ekplate.android.models.searchmodule.GlobalSearchResultItem;
import com.ekplate.android.models.searchmodule.VendorSearchResultItem;
import com.ekplate.android.utils.ConstantClass;
import com.ekplate.android.utils.Pref;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by user on 05-12-2015.
 */
public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.SearchResultItemHolder> {

    private Context context;
    private ArrayList<VendorSearchResultItem> vendorSearchResultItems;
    private ArrayList<FoodSearchResultItem> foodSearchResultItems;
    private ArrayList<CollegeSearchResultItem> collegeSearchResultItems;
    private ArrayList<GlobalSearchResultItem> globalSearchResultItems;
    private String listType;
    private Pref _pref;

    public SearchResultAdapter(String listType, ArrayList<VendorSearchResultItem> vendorSearchResultItems, Context context) {
        this.vendorSearchResultItems = vendorSearchResultItems;
        this.context = context;
        this.listType = listType;
        this._pref = new Pref(context);
    }

    public SearchResultAdapter(Context context, ArrayList<FoodSearchResultItem> foodSearchResultItems, String listType) {
        this.context = context;
        this.foodSearchResultItems = foodSearchResultItems;
        this.listType = listType;
        this._pref = new Pref(context);
    }

    public SearchResultAdapter(Context context, String listType, ArrayList<CollegeSearchResultItem> collegeSearchResultItems) {
        this.context = context;
        this.collegeSearchResultItems = collegeSearchResultItems;
        this.listType = listType;
        this._pref = new Pref(context);
    }

    public SearchResultAdapter(String listType, Context context, ArrayList<GlobalSearchResultItem> globalSearchResultItems) {
        this.context = context;
        this.globalSearchResultItems = globalSearchResultItems;
        this.listType = listType;
        this._pref = new Pref(context);
    }

    @Override
    public SearchResultItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(context).inflate(R.layout.search_result_row_layout, parent, false);
        SearchResultItemHolder holder = new SearchResultItemHolder(rowView);
        return holder;
    }

    @Override
    public void onBindViewHolder(SearchResultItemHolder holder, int position) {
        Log.e("listType", listType);
        if (listType.equalsIgnoreCase("vendor")) {
            holder.tvSearchResult.setText(vendorSearchResultItems.get(position).getVendorName());
        } else if(listType.equalsIgnoreCase("food")) {
            holder.tvSearchResult.setText(foodSearchResultItems.get(position).getFoodTitle());
        } else if(listType.equalsIgnoreCase("college")) {
            holder.tvSearchResult.setText(collegeSearchResultItems.get(position).getCollegeTitle());
        } else if (listType.equalsIgnoreCase("global")) {
            holder.tvSearchResult.setText(globalSearchResultItems.get(position).getItemTitle());
        }
        holder.llSearchResultContainer.setTag(holder);
        holder.llSearchResultContainer.setOnClickListener(containerClickListener);
    }

    @Override
    public int getItemCount() {
        if (listType.equalsIgnoreCase("vendor")) {
            return vendorSearchResultItems.size();
        } else if(listType.equalsIgnoreCase("food")) {
            return foodSearchResultItems.size();
        } else if(listType.equalsIgnoreCase("college")){
            return collegeSearchResultItems.size();
        } else {
            return globalSearchResultItems.size();
        }
    }

    public class SearchResultItemHolder extends RecyclerView.ViewHolder{
        TextView tvSearchResult;
        LinearLayout llSearchResultContainer;
        public SearchResultItemHolder(View itemView) {
            super(itemView);
            tvSearchResult = (TextView) itemView.findViewById(R.id.tvSearchResult);
            llSearchResultContainer = (LinearLayout) itemView.findViewById(R.id.llSearchResultContainer);
        }
    }

    View.OnClickListener containerClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                SearchResultItemHolder holder = (SearchResultItemHolder) view.getTag();
                int position = holder.getAdapterPosition();
                if (listType.equalsIgnoreCase("vendor")) {
                  /*  setRecentSearchValue(vendorSearchResultItems.get(position).getJsonObjStr(),
                            listType);*/
                    goToVendorActivity(vendorSearchResultItems.get(position).getVendorDetailsJsonStr(),
                            vendorSearchResultItems.get(position).getId());
                } else if (listType.equalsIgnoreCase("food")) {
                    setRecentSearchValue(foodSearchResultItems.get(position).getJsonObjStr(),
                            listType);
                    goToVendorListActivity(foodSearchResultItems.get(position).getId(),
                            foodSearchResultItems.get(position).getFoodKeyValue(),
                            "food_list");
                } else if (listType.equalsIgnoreCase("college")) {
                    Log.e("Key Value", collegeSearchResultItems.get(position).getCollegeKeyValue());
                    goToVendorListActivity(collegeSearchResultItems.get(position).getId(),
                            collegeSearchResultItems.get(position).getCollegeKeyValue(),
                            "food_list");
                } else {
                    if (globalSearchResultItems.get(position).getItemType().equalsIgnoreCase("vendor")) {
                        JSONObject jsonObjectItem = new JSONObject(globalSearchResultItems.get(position).getJsonObjStr());
                        setRecentSearchValue(globalSearchResultItems.get(position).getJsonObjStr(),
                                globalSearchResultItems.get(position).getItemType());
                        goToVendorActivity(jsonObjectItem.getString("vendorDetails"), jsonObjectItem.getInt("id"));
                    } else if (globalSearchResultItems.get(position).getItemType().equalsIgnoreCase("food")) {
                        JSONObject jsonObjectItem = new JSONObject(globalSearchResultItems.get(position).getJsonObjStr());
                        setRecentSearchValue(globalSearchResultItems.get(position).getJsonObjStr(),
                                globalSearchResultItems.get(position).getItemType());
                        goToVendorListActivity(jsonObjectItem.getInt("id"),
                                jsonObjectItem.getString("keyValue"), "food_list");
                    } else {
                        Log.e("Key Value", globalSearchResultItems.get(position).getItemType());
                        JSONObject jsonObjectItem = new JSONObject(globalSearchResultItems.get(position).getJsonObjStr());
                        goToVendorListActivity(jsonObjectItem.getInt("id"),
                                jsonObjectItem.getString("keyValue"), "college_list");
                    }
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    private void goToVendorActivity(String vendorJsonString, int vendorId){
        Intent intent = new Intent(context, VendorDetailsActivity.class);
        intent.putExtra("routeFrom", "home_search");
        intent.putExtra("vendorDetailsJsonStr", vendorJsonString);
        intent.putExtra("vendorId", vendorId);
        context.startActivity(intent);
        ((Activity)context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void goToVendorListActivity(int optionId, String KeyValue, String routeFrom){
        Intent intent = new Intent(context, VendorsActivity.class);
        intent.putExtra("optionId", optionId);
        intent.putExtra("keyValue", KeyValue);
        intent.putExtra("routeFrom", routeFrom);
        context.startActivity(intent);
        ((Activity)context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void setRecentSearchValue(String jsonObjString, String itemType) {
        Log.v("jsonObjString",jsonObjString);
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
}
