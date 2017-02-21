package com.ekplate.android.fragments.homemodule;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ekplate.android.R;
import com.ekplate.android.activities.searchmodule.SearchActivity;
import com.ekplate.android.activities.vendormodule.VendorDetailsActivity;
import com.ekplate.android.activities.vendormodule.VendorsActivity;
import com.ekplate.android.appinterfaces.BackgroundActionInterface;
import com.ekplate.android.models.homemodule.MostSearchListItem;
import com.ekplate.android.utils.CallServiceAction;
import com.ekplate.android.utils.ConstantClass;
import com.ekplate.android.utils.NetworkConnectionCheck;
import com.ekplate.android.utils.Pref;
import com.ekplate.android.views.CircularImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment implements BackgroundActionInterface {
    private LinearLayout llSearchViewContainer, llMainContainerSearchProgress, llMainContainerSearchInfo,
            llMostSearchFirst, llMostSearchSecond, llMostSearchThird, llMostSearchFourth, llSearchViewTextContainer,
            llRecentSearchFirst, llRecentSearchSecond, llRecentSearchThird, llRecentSearchFourth, llRecentSearchContainer, llRecentSearch;
    private CircularImageView ivMostSearchFirst, ivMostSearchSecond, ivMostSearchThird, ivMostSearchFourth,
            ivRecentSearchFirst, ivRecentSearchSecond, ivRecentSearchThird, ivRecentSearchFourth;
    private TextView tvMostSearchedItemNameFirst, tvMostSearchedItemNameSecond, tvMostSearchedItemNameThird,
            tvMostSearchedItemNameFourth, tvRecentSearchedItemNameFirst, tvRecentSearchedItemNameSecond,
            tvRecentSearchedItemNameThird, tvRecentSearchedItemNameFourth, tvNoSearchResultFoundText;
    private ProgressBar progressBarSearch;
    private Pref _pref;
    private NetworkConnectionCheck _connection;
    private CallServiceAction _serviceAction;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private List<MostSearchListItem> mostSearchListItems;
    Bundle savedInsState;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        initialize(rootView);
        onClick();
        savedInsState =  savedInstanceState;
        return rootView;
    }



    private void initialize(View rootView){
        llSearchViewContainer = (LinearLayout) rootView.findViewById(R.id.llSearchViewContainer);
        llSearchViewTextContainer = (LinearLayout) rootView.findViewById(R.id.llSearchViewTextContainer);
        ivMostSearchFirst = (CircularImageView) rootView.findViewById(R.id.ivMostSearchFirst);
        ivMostSearchSecond = (CircularImageView) rootView.findViewById(R.id.ivMostSearchSecond);
        ivMostSearchThird = (CircularImageView) rootView.findViewById(R.id.ivMostSearchThird);
        ivMostSearchFourth = (CircularImageView) rootView.findViewById(R.id.ivMostSearchFourth);
        ivRecentSearchFirst = (CircularImageView) rootView.findViewById(R.id.ivRecentSearchFirst);
        ivRecentSearchSecond = (CircularImageView) rootView.findViewById(R.id.ivRecentSearchSecond);
        ivRecentSearchThird = (CircularImageView) rootView.findViewById(R.id.ivRecentSearchThird);
        ivRecentSearchFourth = (CircularImageView) rootView.findViewById(R.id.ivRecentSearchFourth);
        tvMostSearchedItemNameFirst = (TextView) rootView.findViewById(R.id.tvMostSearchedItemNameFirst);
        tvMostSearchedItemNameSecond = (TextView) rootView.findViewById(R.id.tvMostSearchedItemNameSecond);
        tvMostSearchedItemNameThird = (TextView) rootView.findViewById(R.id.tvMostSearchedItemNameThird);
        tvMostSearchedItemNameFourth = (TextView) rootView.findViewById(R.id.tvMostSearchedItemNameFourth);
        tvRecentSearchedItemNameFirst = (TextView) rootView.findViewById(R.id.tvRecentSearchedItemNameFirst);
        tvRecentSearchedItemNameSecond = (TextView) rootView.findViewById(R.id.tvRecentSearchedItemNameSecond);
        tvRecentSearchedItemNameThird = (TextView) rootView.findViewById(R.id.tvRecentSearchedItemNameThird);
        tvRecentSearchedItemNameFourth = (TextView) rootView.findViewById(R.id.tvRecentSearchedItemNameFourth);
        llMainContainerSearchProgress = (LinearLayout) rootView.findViewById(R.id.llMainContainerSearchProgress);
        llMainContainerSearchInfo = (LinearLayout) rootView.findViewById(R.id.llMainContainerSearchInfo);
        llMostSearchFirst = (LinearLayout) rootView.findViewById(R.id.llMostSearchFirst);
        llMostSearchSecond = (LinearLayout) rootView.findViewById(R.id.llMostSearchSecond);
        llMostSearchThird = (LinearLayout) rootView.findViewById(R.id.llMostSearchThird);
        llMostSearchFourth = (LinearLayout) rootView.findViewById(R.id.llMostSearchFourth);
        llRecentSearchFirst = (LinearLayout) rootView.findViewById(R.id.llRecentSearchFirst);
        llRecentSearchSecond = (LinearLayout) rootView.findViewById(R.id.llRecentSearchSecond);
        llRecentSearchThird = (LinearLayout) rootView.findViewById(R.id.llRecentSearchThird);
        llRecentSearchFourth = (LinearLayout) rootView.findViewById(R.id.llRecentSearchFourth);
        llRecentSearchContainer = (LinearLayout) rootView.findViewById(R.id.llRecentSearchContainer);
       // llRecentSearch = (LinearLayout) rootView.findViewById(R.id.llRecentSearch);
        tvNoSearchResultFoundText = (TextView) rootView.findViewById(R.id.tvNoSearchResultFoundText);
        progressBarSearch = (ProgressBar) rootView.findViewById(R.id.progressBarSearch);
        mostSearchListItems = new ArrayList<>();
        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                    .cacheOnDisk(true)
                    .cacheInMemory(true)
                    .showImageOnLoading(R.drawable.default_image_vendor_inside)
                    .showImageOnFail(R.drawable.default_image_vendor_inside)
                    .showImageForEmptyUri(R.drawable.default_image_vendor_inside)
                    .build();

        _pref = new Pref(getActivity());
        _connection = new NetworkConnectionCheck(getActivity());
        _serviceAction = new CallServiceAction(getActivity());
        _serviceAction.actionInterface = this;
    }

    private void onClick(){
        llSearchViewContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                intent.putExtra("searchType", "global");
                startActivity(intent);
            }
        });

        llSearchViewTextContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                intent.putExtra("searchType", "global");
                startActivity(intent);
            }
        });

        llMostSearchFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mostSearchListItems.get(0).getItemType().equalsIgnoreCase("vendor")) {
                    JSONObject RecentJsonObject = new JSONObject();
                    try {
                        RecentJsonObject.put("id", mostSearchListItems.get(0).getId());
                        RecentJsonObject.put("keyValue", "Vendor-" + mostSearchListItems.get(0).getId());
                        RecentJsonObject.put("vendorName", mostSearchListItems.get(0).getFoodName());
                        RecentJsonObject.put("searchType", "vendor");
                        RecentJsonObject.put("vendorDetails",  mostSearchListItems.get(0).getVendorDetails());
                        Log.w("RecentJsonObject", RecentJsonObject.toString());
                        setRecentSearchValue(RecentJsonObject.toString(), "vendor");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Intent intent;
                    intent = new Intent(getActivity(), VendorDetailsActivity.class);
                    intent.putExtra("routeFrom", "home");
                    intent.putExtra("vendorDetailsJsonStr", mostSearchListItems.get(0).getVendorDetails());
                    intent.putExtra("vendorId", mostSearchListItems.get(0).getId());
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else {

                    JSONObject RecentJsonObject = new JSONObject();
                    //JSONObject jsonObjectChild = new JSONObject();
                    try {
                        RecentJsonObject.put("id",mostSearchListItems.get(0).getId());
                        RecentJsonObject.put("foodName",mostSearchListItems.get(0).getFoodName());
                        RecentJsonObject.put("searchType","food");
                        RecentJsonObject.put("keyValue",mostSearchListItems.get(0).getItemType()+"-"+mostSearchListItems.get(0).getFoodName());
                        RecentJsonObject.put("foodImgUrl", mostSearchListItems.get(0).getImageUrl());

                        setRecentSearchValue(RecentJsonObject.toString(), "food");


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Intent intent;
                    intent = new Intent(getActivity(), VendorsActivity.class);
                    intent.putExtra("routeFrom", "food_list");
                    intent.putExtra("keyValue", mostSearchListItems.get(0).getFoodTyp());
                    intent.putExtra("optionId", mostSearchListItems.get(0).getId());
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            }
        });

        llMostSearchSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mostSearchListItems.get(1).getItemType().equalsIgnoreCase("vendor")) {
                    JSONObject RecentJsonObject = new JSONObject();
                    try {
                        RecentJsonObject.put("id", mostSearchListItems.get(1).getId());
                        RecentJsonObject.put("keyValue", "Vendor-" + mostSearchListItems.get(1).getId());
                        RecentJsonObject.put("vendorName", mostSearchListItems.get(1).getFoodName());
                        RecentJsonObject.put("searchType", "vendor");
                        RecentJsonObject.put("vendorDetails", mostSearchListItems.get(1).getVendorDetails());
                        Log.w("RecentJsonObject", RecentJsonObject.toString());
                        setRecentSearchValue(RecentJsonObject.toString(), "vendor");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Intent intent;
                    intent = new Intent(getActivity(), VendorDetailsActivity.class);
                    intent.putExtra("routeFrom", "home");
                    intent.putExtra("vendorDetailsJsonStr", mostSearchListItems.get(1).getVendorDetails());
                    intent.putExtra("vendorId", mostSearchListItems.get(1).getId());
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else {

                    JSONObject RecentJsonObject = new JSONObject();
                    //JSONObject jsonObjectChild = new JSONObject();
                    try {
                        RecentJsonObject.put("id", mostSearchListItems.get(1).getId());
                        RecentJsonObject.put("foodName", mostSearchListItems.get(1).getFoodName());
                        RecentJsonObject.put("searchType", "food");
                        RecentJsonObject.put("keyValue", mostSearchListItems.get(1).getItemType() + "-" + mostSearchListItems.get(1).getFoodName());
                        RecentJsonObject.put("foodImgUrl", mostSearchListItems.get(1).getImageUrl());

                        setRecentSearchValue(RecentJsonObject.toString(), "food");


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Intent intent;
                    intent = new Intent(getActivity(), VendorsActivity.class);
                    intent.putExtra("routeFrom", "food_list");
                    intent.putExtra("keyValue", mostSearchListItems.get(1).getFoodTyp());
                    intent.putExtra("optionId", mostSearchListItems.get(1).getId());
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            }
        });

        llMostSearchThird.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mostSearchListItems.get(2).getItemType().equalsIgnoreCase("vendor")) {

                    JSONObject RecentJsonObject = new JSONObject();
                    try {
                        RecentJsonObject.put("id", mostSearchListItems.get(2).getId());
                        RecentJsonObject.put("keyValue", "Vendor-" + mostSearchListItems.get(2).getId());
                        RecentJsonObject.put("vendorName", mostSearchListItems.get(2).getFoodName());
                        RecentJsonObject.put("searchType", "vendor");
                        RecentJsonObject.put("vendorDetails", mostSearchListItems.get(2).getVendorDetails());
                        Log.w("RecentJsonObject", RecentJsonObject.toString());
                        setRecentSearchValue(RecentJsonObject.toString(), "vendor");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Intent intent;
                    intent = new Intent(getActivity(), VendorDetailsActivity.class);
                    intent.putExtra("routeFrom", "home");
                    intent.putExtra("vendorDetailsJsonStr", mostSearchListItems.get(2).getVendorDetails());
                    intent.putExtra("vendorId", mostSearchListItems.get(2).getId());
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else {

                    JSONObject RecentJsonObject = new JSONObject();
                    //JSONObject jsonObjectChild = new JSONObject();
                    try {
                        RecentJsonObject.put("id", mostSearchListItems.get(2).getId());
                        RecentJsonObject.put("foodName", mostSearchListItems.get(2).getFoodName());
                        RecentJsonObject.put("searchType", "food");
                        RecentJsonObject.put("keyValue", mostSearchListItems.get(2).getItemType() + "-" + mostSearchListItems.get(2).getFoodName());
                        RecentJsonObject.put("foodImgUrl", mostSearchListItems.get(2).getImageUrl());

                        setRecentSearchValue(RecentJsonObject.toString(), "food");


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    Intent intent;
                    intent = new Intent(getActivity(), VendorsActivity.class);
                    intent.putExtra("routeFrom", "food_list");
                    intent.putExtra("keyValue", mostSearchListItems.get(2).getFoodTyp());
                    intent.putExtra("optionId", mostSearchListItems.get(2).getId());
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            }
        });

        llMostSearchFourth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mostSearchListItems.get(3).getItemType().equalsIgnoreCase("vendor")) {

                    JSONObject RecentJsonObject = new JSONObject();
                    try {
                        RecentJsonObject.put("id", mostSearchListItems.get(3).getId());
                        RecentJsonObject.put("keyValue", "Vendor-" + mostSearchListItems.get(3).getId());
                        RecentJsonObject.put("vendorName", mostSearchListItems.get(3).getFoodName());
                        RecentJsonObject.put("searchType", "vendor");
                        RecentJsonObject.put("vendorDetails",  mostSearchListItems.get(3).getVendorDetails());
                        Log.w("RecentJsonObject", RecentJsonObject.toString());
                        setRecentSearchValue(RecentJsonObject.toString(), "vendor");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Intent intent;
                    intent = new Intent(getActivity(), VendorDetailsActivity.class);
                    intent.putExtra("routeFrom", "home");
                    intent.putExtra("vendorDetailsJsonStr", mostSearchListItems.get(3).getVendorDetails());
                    intent.putExtra("vendorId", mostSearchListItems.get(3).getId());
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else {

                    JSONObject RecentJsonObject = new JSONObject();
                    //JSONObject jsonObjectChild = new JSONObject();
                    try {
                        RecentJsonObject.put("id",mostSearchListItems.get(3).getId());
                        RecentJsonObject.put("foodName",mostSearchListItems.get(3).getFoodName());
                        RecentJsonObject.put("searchType","food");
                        RecentJsonObject.put("keyValue",mostSearchListItems.get(3).getItemType()+"-"+mostSearchListItems.get(3).getFoodName());
                        RecentJsonObject.put("foodImgUrl", mostSearchListItems.get(3).getImageUrl());

                        setRecentSearchValue(RecentJsonObject.toString(), "food");


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Intent intent;
                    intent = new Intent(getActivity(), VendorsActivity.class);
                    intent.putExtra("routeFrom", "food_list");
                    intent.putExtra("keyValue", mostSearchListItems.get(3).getFoodTyp());
                    intent.putExtra("optionId", mostSearchListItems.get(3).getId());
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            }
        });
    }

    private void setInputParamForSearchedItem(){
        try {
            JSONObject parentJsonObj = new JSONObject();
            JSONObject childJsonObj = new JSONObject();
            childJsonObj.put("accessToken", _pref.getSession(ConstantClass.ACCESS_TOKEN));
            childJsonObj.put("latitude", _pref.getSession(ConstantClass.TAG_LATITUDE));
            childJsonObj.put("longitude", _pref.getSession(ConstantClass.TAG_LONGITUDE));
            childJsonObj.put("city", _pref.getSession(ConstantClass.TAG_SELECTED_CITY_LOCATION));
            parentJsonObj.put("data", childJsonObj);
            Log.e("input data", parentJsonObj.toString());
            _serviceAction.requestVersionApi(parentJsonObj, "get-most-search-item");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mostSearchListItems.clear();
        llMostSearchFirst.setEnabled(false);
        llMostSearchSecond.setEnabled(false);
        llMostSearchThird.setEnabled(false);
        llMostSearchFourth.setEnabled(false);

        llMainContainerSearchInfo.setVisibility(View.GONE);
        llMainContainerSearchProgress.setVisibility(View.VISIBLE);
        setInputParamForSearchedItem();
        setUpRecentSearchItem();
        llMainContainerSearchInfo.refreshDrawableState();
       llRecentSearchContainer.setVisibility(View.VISIBLE);
        llRecentSearchSecond.setVisibility(View.VISIBLE);
        llRecentSearchThird.setVisibility(View.VISIBLE);
        llRecentSearchFirst.setVisibility(View.VISIBLE);
        llRecentSearchFourth.setVisibility(View.VISIBLE);

    }

    @Override
    public void onStarted() {

    }

    @Override
    public void onCompleted(JSONObject response) {
        Log.e("response", response.toString());
        try {
            /*llMostSearchFirst.setEnabled(false);
            llMostSearchSecond.setEnabled(false);
            llMostSearchThird.setEnabled(false);
            llMostSearchFourth.setEnabled(false);*/
            JSONObject jsonObjError = response.getJSONObject("errNode");
            if (jsonObjError.getInt("errCode") == 0) {
                JSONObject jsonObjData = response.getJSONObject("data");
                if (jsonObjData.getBoolean("success")) {
                JSONArray mostSearchedItemJsonArray = jsonObjData.getJSONArray("mostSearchedItem");
                if (mostSearchedItemJsonArray.length() > 0) {
                    for (int mostSearchCount = 0; mostSearchCount < mostSearchedItemJsonArray.length(); mostSearchCount++) {
                        Log.e("k", String.valueOf(mostSearchCount));
                        JSONObject mostSearchedItemJsonObj = mostSearchedItemJsonArray.getJSONObject(mostSearchCount);
                        MostSearchListItem _item = new MostSearchListItem();
                        _item.setId(mostSearchedItemJsonObj.getInt("id"));
                        _item.setItemType(mostSearchedItemJsonObj.getString("itemKeyValue"));

                        JSONObject jsonObjectDetails = null;
                        if (_item.getItemType().equals("vendor")) {
                            _item.setVendorDetails(mostSearchedItemJsonObj.getJSONObject("vendorDetails").toString());
                            jsonObjectDetails = mostSearchedItemJsonObj.getJSONObject("vendorDetails");


                        } else {
                            _item.setFoodTyp(mostSearchedItemJsonObj.getString("keyValue"));
                        }

                        switch (mostSearchCount) {
                            case 0:
                                llMostSearchFirst.setEnabled(true);
                                if (mostSearchedItemJsonObj.getString("itemKeyValue").equals("vendor")) {
                                    imageLoader.displayImage(jsonObjectDetails.getString("mainImageUrl"), ivMostSearchFirst, options);
                                    tvMostSearchedItemNameFirst.setText(jsonObjectDetails.getString("shopName"));
                                } else {
                                    imageLoader.displayImage(mostSearchedItemJsonObj.getString("foodImgUrl"), ivMostSearchFirst, options);
                                    tvMostSearchedItemNameFirst.setText(mostSearchedItemJsonObj.getString("foodName"));
                                    _item.setImageUrl(mostSearchedItemJsonObj.getString("foodImgUrl"));
                                }
                                _item.setFoodName(tvMostSearchedItemNameFirst.getText().toString());
                                break;
                            case 1:
                                llMostSearchSecond.setEnabled(true);
                                if (mostSearchedItemJsonObj.getString("itemKeyValue").equals("vendor")) {
                                    imageLoader.displayImage(jsonObjectDetails.getString("mainImageUrl"), ivMostSearchSecond, options);
                                    tvMostSearchedItemNameSecond.setText(jsonObjectDetails.getString("shopName"));
                                } else {
                                    imageLoader.displayImage(mostSearchedItemJsonObj.getString("foodImgUrl"), ivMostSearchSecond, options);
                                    tvMostSearchedItemNameSecond.setText(mostSearchedItemJsonObj.getString("foodName"));
                                    _item.setImageUrl(mostSearchedItemJsonObj.getString("foodImgUrl"));
                                    //_item.setVendorDetails(mostSearchedItemJsonObj.optString("vendorDetails"));
                                }
                                _item.setFoodName(tvMostSearchedItemNameSecond.getText().toString());
                                break;
                            case 2:
                                llMostSearchThird.setEnabled(true);
                                if (mostSearchedItemJsonObj.getString("itemKeyValue").equals("vendor")) {
                                    imageLoader.displayImage(jsonObjectDetails.getString("mainImageUrl"), ivMostSearchThird, options);
                                    tvMostSearchedItemNameThird.setText(jsonObjectDetails.getString("shopName"));
                                } else {
                                    imageLoader.displayImage(mostSearchedItemJsonObj.getString("foodImgUrl"), ivMostSearchThird, options);
                                    tvMostSearchedItemNameThird.setText(mostSearchedItemJsonObj.getString("foodName"));
                                    _item.setImageUrl(mostSearchedItemJsonObj.getString("foodImgUrl"));
                                }
                                _item.setFoodName(tvMostSearchedItemNameThird.getText().toString());
                                break;
                            case 3:
                                llMostSearchFourth.setEnabled(true);
                                if (mostSearchedItemJsonObj.getString("itemKeyValue").equals("vendor")) {
                                    imageLoader.displayImage(jsonObjectDetails.getString("mainImageUrl"), ivMostSearchFourth, options);
                                    tvMostSearchedItemNameFourth.setText(jsonObjectDetails.getString("shopName"));
                                } else {
                                    imageLoader.displayImage(mostSearchedItemJsonObj.getString("foodImgUrl"), ivMostSearchFourth, options);
                                    tvMostSearchedItemNameFourth.setText(mostSearchedItemJsonObj.getString("foodName"));
                                    _item.setImageUrl(mostSearchedItemJsonObj.getString("foodImgUrl"));
                                }
                                _item.setFoodName(tvMostSearchedItemNameFourth.getText().toString());
                                break;
                        }
                        mostSearchListItems.add(_item);
//--------------------------------------------------------------------------------------------
                        switch (mostSearchListItems.size())
                        {
                            case 0:
                                llMostSearchFirst.setVisibility(View.INVISIBLE);
                                llMostSearchSecond.setVisibility(View.INVISIBLE);
                                llMostSearchThird.setVisibility(View.INVISIBLE);
                                llMostSearchFourth.setVisibility(View.INVISIBLE);
                                break;
                            case 1:
                                llMostSearchFirst.setVisibility(View.VISIBLE);
                                llMostSearchSecond.setVisibility(View.INVISIBLE);
                                llMostSearchThird.setVisibility(View.INVISIBLE);
                                llMostSearchFourth.setVisibility(View.INVISIBLE);
                                break;
                            case 2:
                                llMostSearchFirst.setVisibility(View.VISIBLE);
                                llMostSearchSecond.setVisibility(View.VISIBLE);
                                llMostSearchThird.setVisibility(View.INVISIBLE);
                                llMostSearchFourth.setVisibility(View.INVISIBLE);
                                break;
                            case 3:
                                llMostSearchFirst.setVisibility(View.VISIBLE);
                                llMostSearchSecond.setVisibility(View.VISIBLE);
                                llMostSearchThird.setVisibility(View.VISIBLE);
                                llMostSearchFourth.setVisibility(View.INVISIBLE);
                                break;
                            case 4:
                                llMostSearchFirst.setVisibility(View.VISIBLE);
                                llMostSearchSecond.setVisibility(View.VISIBLE);
                                llMostSearchThird.setVisibility(View.VISIBLE);
                                llMostSearchFourth.setVisibility(View.VISIBLE);
                                break;
                        }
                        //--------------------------------------------------------------------------------------
                    }

                    llMainContainerSearchInfo.setVisibility(View.VISIBLE);
                    llMainContainerSearchProgress.setVisibility(View.GONE);

                } else {
                    Log.e("Most search count", String.valueOf(mostSearchedItemJsonArray.length()));
                    tvMostSearchedItemNameFirst.setVisibility(View.GONE);
                    tvMostSearchedItemNameSecond.setVisibility(View.GONE);
                    tvMostSearchedItemNameThird.setVisibility(View.GONE);
                    tvMostSearchedItemNameFourth.setVisibility(View.GONE);
                    tvNoSearchResultFoundText.setVisibility(View.VISIBLE);
                    tvNoSearchResultFoundText.setText("You did not search any food in " +
                            _pref.getSession(ConstantClass.TAG_SELECTED_CITY_LOCATION));
                    progressBarSearch.setVisibility(View.GONE);
                }
            }else
                {
                    progressBarSearch.setVisibility(View.GONE);
                }

            } else {
                Toast.makeText(getActivity(), jsonObjError.getString("errMsg"), Toast.LENGTH_LONG).show();
                progressBarSearch.setVisibility(View.GONE);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setUpRecentSearchItem(){
        try {
            if (!_pref.getSession(ConstantClass.TAG_RECENT_SEARCHES_FIRST).equalsIgnoreCase("") ||
                 !_pref.getSession(ConstantClass.TAG_RECENT_SEARCHES_SECOND).equalsIgnoreCase("") ||
                 !_pref.getSession(ConstantClass.TAG_RECENT_SEARCHES_THIRD).equalsIgnoreCase("") ||
                 !_pref.getSession(ConstantClass.TAG_RECENT_SEARCHES_FOURTH).equalsIgnoreCase("")) {

               // llRecentSearch.setVisibility(View.VISIBLE);

                if (!_pref.getSession(ConstantClass.TAG_RECENT_SEARCHES_FIRST).equalsIgnoreCase("")) {
                    JSONObject jsonObjectFirstItem = new JSONObject(_pref.getSession(ConstantClass.TAG_RECENT_SEARCHES_FIRST));
                    if (jsonObjectFirstItem.getString("searchType").equalsIgnoreCase("vendor")) {
                        setUpRecentSearchVendor(_pref.getSession(ConstantClass.TAG_RECENT_SEARCHES_FIRST),
                                ivRecentSearchFirst, tvRecentSearchedItemNameFirst, llRecentSearchFirst);
                    } else if (jsonObjectFirstItem.getString("searchType").equalsIgnoreCase("food")) {
                        setUpRecentSearchFood(_pref.getSession(ConstantClass.TAG_RECENT_SEARCHES_FIRST),
                                ivRecentSearchFirst, tvRecentSearchedItemNameFirst, llRecentSearchFirst);
                    } else {
                        setUpRecentSearchCollege(_pref.getSession(ConstantClass.TAG_RECENT_SEARCHES_FIRST),
                                ivRecentSearchFirst, tvRecentSearchedItemNameFirst, llRecentSearchFirst);
                    }
                } else {
                    llRecentSearchFirst.setVisibility(View.INVISIBLE);
                }

                if (!_pref.getSession(ConstantClass.TAG_RECENT_SEARCHES_SECOND).equalsIgnoreCase("")) {
                    JSONObject jsonObjectSecondItem = new JSONObject(_pref.getSession(ConstantClass.TAG_RECENT_SEARCHES_SECOND));
                    if (jsonObjectSecondItem.getString("searchType").equalsIgnoreCase("vendor")) {
                        setUpRecentSearchVendor(_pref.getSession(ConstantClass.TAG_RECENT_SEARCHES_SECOND),
                                ivRecentSearchSecond, tvRecentSearchedItemNameSecond, llRecentSearchSecond);
                    } else if (jsonObjectSecondItem.getString("searchType").equalsIgnoreCase("food")) {
                        setUpRecentSearchFood(_pref.getSession(ConstantClass.TAG_RECENT_SEARCHES_SECOND),
                                ivRecentSearchSecond, tvRecentSearchedItemNameSecond, llRecentSearchSecond);
                    } else {
                        setUpRecentSearchCollege(_pref.getSession(ConstantClass.TAG_RECENT_SEARCHES_SECOND),
                                ivRecentSearchSecond, tvRecentSearchedItemNameSecond, llRecentSearchSecond);
                    }
                } else {
                    llRecentSearchSecond.setVisibility(View.INVISIBLE);
                }

                if (!_pref.getSession(ConstantClass.TAG_RECENT_SEARCHES_THIRD).equalsIgnoreCase("")) {
                    JSONObject jsonObjectThirdItem = new JSONObject(_pref.getSession(ConstantClass.TAG_RECENT_SEARCHES_THIRD));
                    if (jsonObjectThirdItem.getString("searchType").equalsIgnoreCase("vendor")) {
                        setUpRecentSearchVendor(_pref.getSession(ConstantClass.TAG_RECENT_SEARCHES_THIRD),
                                ivRecentSearchThird, tvRecentSearchedItemNameThird, llRecentSearchThird);
                    } else if (jsonObjectThirdItem.getString("searchType").equalsIgnoreCase("food")) {
                        setUpRecentSearchFood(_pref.getSession(ConstantClass.TAG_RECENT_SEARCHES_THIRD),
                                ivRecentSearchThird, tvRecentSearchedItemNameThird, llRecentSearchThird);
                    } else {
                        setUpRecentSearchCollege(_pref.getSession(ConstantClass.TAG_RECENT_SEARCHES_THIRD),
                                ivRecentSearchThird, tvRecentSearchedItemNameThird, llRecentSearchThird);
                    }
                } else {
                    llRecentSearchThird.setVisibility(View.INVISIBLE);
                }

                if (!_pref.getSession(ConstantClass.TAG_RECENT_SEARCHES_FOURTH).equalsIgnoreCase("")) {
                    JSONObject jsonObjectFourthItem = new JSONObject(_pref.getSession(ConstantClass.TAG_RECENT_SEARCHES_FOURTH));
                    if (jsonObjectFourthItem.getString("searchType").equalsIgnoreCase("vendor")) {
                        setUpRecentSearchVendor(_pref.getSession(ConstantClass.TAG_RECENT_SEARCHES_FOURTH),
                                ivRecentSearchFourth, tvRecentSearchedItemNameFourth, llRecentSearchFourth);
                    } else if (jsonObjectFourthItem.getString("searchType").equalsIgnoreCase("food")) {
                        setUpRecentSearchFood(_pref.getSession(ConstantClass.TAG_RECENT_SEARCHES_FOURTH),
                                ivRecentSearchFourth, tvRecentSearchedItemNameFourth, llRecentSearchFourth);
                    } else {
                        setUpRecentSearchCollege(_pref.getSession(ConstantClass.TAG_RECENT_SEARCHES_FOURTH),
                                ivRecentSearchFourth, tvRecentSearchedItemNameFourth, llRecentSearchFourth);
                    }
                } else {
                    llRecentSearchFourth.setVisibility(View.INVISIBLE);
                }
            } else {
                llRecentSearchContainer.setVisibility(View.INVISIBLE);
                llRecentSearch.setVisibility(View.INVISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setUpRecentSearchVendor(String jsonObjItem, CircularImageView searchItemImage,
                                         TextView searchItemName, LinearLayout llRecentSearchItemContainer) {
        try {
            final JSONObject jsonObjectItem = new JSONObject(jsonObjItem);
            final JSONObject jsonObjectItemDetails = new JSONObject(jsonObjectItem.getString("vendorDetails"));
            imageLoader.displayImage(jsonObjectItemDetails.getString("mainImageUrl"), searchItemImage, options);
            searchItemName.setText(jsonObjectItem.getString("vendorName"));
            llRecentSearchItemContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(getActivity(), VendorDetailsActivity.class);
                        intent.putExtra("routeFrom", "home");
                        intent.putExtra("vendorDetailsJsonStr", jsonObjectItemDetails.toString());
                        intent.putExtra("vendorId", jsonObjectItem.getInt("id"));
                       // intent.putExtra("vendorBookMark", jsonObjectItem.getBoolean("bookmarkedStatus"));
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setUpRecentSearchFood(String jsonObjItem, CircularImageView searchItemImage,
                                       TextView searchItemName, LinearLayout llRecentSearchItemContainer) {
        try {
            final JSONObject jsonObjectItem = new JSONObject(jsonObjItem);
            imageLoader.displayImage(jsonObjectItem.getString("foodImgUrl"), searchItemImage, options);
            searchItemName.setText(jsonObjectItem.getString("foodName"));
            llRecentSearchItemContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(getActivity(), VendorsActivity.class);
                        intent.putExtra("optionId", jsonObjectItem.getInt("id"));
                        intent.putExtra("keyValue", jsonObjectItem.getString("keyValue"));
                        intent.putExtra("routeFrom", "food_list");
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setUpRecentSearchCollege(String jsonObjItem, CircularImageView searchItemImage,
                                          TextView searchItemName, LinearLayout llRecentSearchItemContainer) {
        try {
            JSONObject jsonObjectItem = new JSONObject(jsonObjItem);
            imageLoader.displayImage("", searchItemImage, options);
            searchItemName.setText(jsonObjectItem.getString("collegeName"));
            llRecentSearchItemContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }
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
