package com.ekplate.android.activities.searchmodule;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.exceptions.CleverTapMetaDataNotFoundException;
import com.clevertap.android.sdk.exceptions.CleverTapPermissionsNotSatisfied;
import com.ekplate.android.R;
import com.ekplate.android.adapters.searchmodule.SearchResultAdapter;
import com.ekplate.android.appinterfaces.BackgroundActionInterface;
import com.ekplate.android.config.BaseActivity;
import com.ekplate.android.models.searchmodule.CollegeSearchResultItem;
import com.ekplate.android.models.searchmodule.FoodSearchResultItem;
import com.ekplate.android.models.searchmodule.GlobalSearchResultItem;
import com.ekplate.android.models.searchmodule.VendorSearchResultItem;
import com.ekplate.android.utils.CallServiceAction;
import com.ekplate.android.utils.ConstantClass;
import com.ekplate.android.utils.NetworkConnectionCheck;
import com.ekplate.android.utils.Pref;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchActivity extends BaseActivity
        implements BackgroundActionInterface {

    private Toolbar tbSearchVendor;
    private SearchView svTextArea;
    private RecyclerView rvSearchResult;
    private ArrayList<VendorSearchResultItem> vendorSearchResultItems;
    private ArrayList<FoodSearchResultItem> foodSearchResultItems;
    private ArrayList<CollegeSearchResultItem> collegeSearchResultItems;
    private ArrayList<GlobalSearchResultItem> globalSearchResultItems;
    private SearchResultAdapter searchResultAdapter;
    private String searchType;
    private Pref _pref;
    private NetworkConnectionCheck _connection;
    private CallServiceAction _serviceAction;
    private TextView tvNoResultFoundText;
    CleverTapAPI cleverTap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getWindow().setBackgroundDrawable(null);
        getSupportActionBar().hide();
        initialize();
        setUpVendorSearchToolbar();
        setUpSearchActivity();
    }

    private void initialize(){
        searchType = getIntent().getExtras().getString("searchType");
        svTextArea = (SearchView) findViewById(R.id.svTextArea);
        rvSearchResult = (RecyclerView) findViewById(R.id.rvSearchResult);
        tvNoResultFoundText = (TextView) findViewById(R.id.tvNoResultFoundText);
        rvSearchResult.setLayoutManager(new LinearLayoutManager(this));
        _pref = new Pref(this);
        _connection = new NetworkConnectionCheck(this);
        _serviceAction = new CallServiceAction(this);
        _serviceAction.actionInterface = SearchActivity.this;
        vendorSearchResultItems = new ArrayList<>();
        foodSearchResultItems = new ArrayList<>();
        collegeSearchResultItems = new ArrayList<>();
        globalSearchResultItems = new ArrayList<>();
        try {
            cleverTap = CleverTapAPI.getInstance(getApplicationContext());
        } catch (CleverTapMetaDataNotFoundException e) {
            // thrown if you haven't specified your CleverTap Account ID or Token in your AndroidManifest.xml
        } catch (CleverTapPermissionsNotSatisfied e) {
            // thrown if you haven’t requested the required permissions in your AndroidManifest.xml
        }
        switch (searchType) {
            case "vendor":
                svTextArea.setQueryHint("Search by Vendor name");
                searchResultAdapter = new SearchResultAdapter(searchType, vendorSearchResultItems, this);
                break;
            case "food":
                svTextArea.setQueryHint("Search by Food item");
                searchResultAdapter = new SearchResultAdapter(this, foodSearchResultItems, searchType);
                break;
            case "college":
                svTextArea.setQueryHint("Search by College name");
                searchResultAdapter = new SearchResultAdapter(this, searchType, collegeSearchResultItems);
                break;
            default:
                searchResultAdapter = new SearchResultAdapter(searchType, this, globalSearchResultItems);
                break;
        }
        rvSearchResult.setAdapter(searchResultAdapter);
    }

    private void setUpVendorSearchToolbar() {
        tbSearchVendor = (Toolbar) findViewById(R.id.tbSearchActivity);
        tbSearchVendor.setNavigationIcon(R.drawable.ic_action_back);
        tbSearchVendor.setBackground(getResources().getDrawable(R.drawable.toolbar_bg));
        tbSearchVendor.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void setUpSearchActivity(){
        svTextArea.setIconified(false);
        svTextArea.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(!newText.equalsIgnoreCase("") && newText.length() > 2) {
                    HashMap<String, Object> prodViewedAction = new HashMap<String, Object>();
                    prodViewedAction.put("Food Name",newText);
                    prodViewedAction.put("Date", new java.util.Date());
                    cleverTap.event.push("Food Searched", prodViewedAction);
                    setUpSearchResultList(newText);
                }
                else {
                    rvSearchResult.setVisibility(View.GONE);
                }
                return false;
            }
        });
        ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).
                toggleSoftInput(InputMethodManager.SHOW_FORCED,
                        InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    private void setUpSearchResultList(String searchQueryText) {
        try {
            JSONObject parentJsonObj = new JSONObject();
            JSONObject childJsonObj = new JSONObject();
            childJsonObj.put("accessToken", _pref.getSession(ConstantClass.ACCESS_TOKEN));
            childJsonObj.put("keyWord", searchQueryText);
            childJsonObj.put("city", _pref.getSession(ConstantClass.TAG_SELECTED_CITY_LOCATION));
            childJsonObj.put("searchType", searchType);
            childJsonObj.put("foodType", _pref.getSession(ConstantClass.TAG_SELECTED_FOOD_TYPE));
            childJsonObj.put("latitude", _pref.getSession(ConstantClass.TAG_LATITUDE));
            childJsonObj.put("longitude", _pref.getSession(ConstantClass.TAG_LONGITUDE));
            childJsonObj.put("sendVendorDetails","no");
            parentJsonObj.put("data", childJsonObj);
            Log.e("input data", parentJsonObj.toString());
            _serviceAction.requestVersionApi(parentJsonObj, "search");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onStarted() {

    }

    @Override
    public void onCompleted(JSONObject response) {
        Log.e("suggestion response", response.toString());
        try {
            foodSearchResultItems.clear();
            collegeSearchResultItems.clear();
            vendorSearchResultItems.clear();
            globalSearchResultItems.clear();
            JSONObject errorNodeJsonObj = response.getJSONObject("errNode");
            if (errorNodeJsonObj.getInt("errCode") == 0) {
                JSONObject dataJsonObject = response.getJSONObject("data");
                if (dataJsonObject.getBoolean("success")){
                    JSONArray searchItemArray = dataJsonObject.getJSONArray("listItem");
                    rvSearchResult.setVisibility(View.VISIBLE);
                    tvNoResultFoundText.setVisibility(View.GONE);
                    for (int i = 0; i < searchItemArray.length(); i++) {
                        if (searchType.equalsIgnoreCase("food")) {
                            FoodSearchResultItem _item = new FoodSearchResultItem();
                            JSONObject listItemObject = searchItemArray.getJSONObject(i);
                            _item.setId(listItemObject.getInt("id"));
                            _item.setFoodTitle(listItemObject.getString("foodName") + " (Item)");
                            _item.setSearchType(listItemObject.getString("searchType"));
                            _item.setFoodKeyValue(listItemObject.getString("keyValue"));
                            _item.setJsonObjStr(listItemObject.toString());
                            foodSearchResultItems.add(_item);
                        }else if (searchType.equalsIgnoreCase("college")) {
                            CollegeSearchResultItem _item = new CollegeSearchResultItem();
                            JSONObject listItemObject = searchItemArray.getJSONObject(i);
                            _item.setId(listItemObject.getInt("id"));
                            _item.setCollegeTitle(listItemObject.getString("collegeName") + " (College)");
                            _item.setSearchType(listItemObject.getString("searchType"));
                            _item.setCollegeKeyValue(listItemObject.getString("keyValue"));
                            _item.setJsonObjStr(listItemObject.toString());
                            collegeSearchResultItems.add(_item);
                        } else if (searchType.equalsIgnoreCase("vendor")) {
                            VendorSearchResultItem _item = new VendorSearchResultItem();
                            JSONObject listItemObject = searchItemArray.getJSONObject(i);
                            _item.setId(listItemObject.getInt("id"));
                            _item.setVendorName(listItemObject.getString("vendorName") + " (Vendor)");
                            _item.setSearchType(listItemObject.getString("searchType"));
                            _item.setVendorDetailsJsonStr(listItemObject.getString("vendorDetails"));
                            _item.setJsonObjStr(listItemObject.toString());
                            vendorSearchResultItems.add(_item);
                        } else if (searchType.equalsIgnoreCase("global")) {
                            GlobalSearchResultItem _item = new GlobalSearchResultItem();
                            JSONObject listItemObject = searchItemArray.getJSONObject(i);
                            _item.setId(listItemObject.getInt("id"));
                            if (listItemObject.getString("searchType").equalsIgnoreCase("vendor")) {
                                _item.setItemTitle(listItemObject.getString("vendorName") + " (Vendor)");
                            } else if (listItemObject.getString("searchType").equalsIgnoreCase("food")){
                                _item.setItemTitle(listItemObject.getString("foodName") + " (Item)");
                            }else if (listItemObject.getString("searchType").equalsIgnoreCase("area")){
                                _item.setItemTitle(listItemObject.getString("areaName") + " (Location)");
                            } else {
                                _item.setItemTitle(listItemObject.getString("collegeName") + " (College)");
                            }
                            _item.setItemType(listItemObject.getString("searchType"));
                            _item.setJsonObjStr(listItemObject.toString());

                            if(svTextArea.getQuery().length()>2) {
                                globalSearchResultItems.add(_item);
                            }

                        }
                    }
                    searchResultAdapter.notifyDataSetChanged();
                } else {
                    rvSearchResult.setVisibility(View.GONE);
                    tvNoResultFoundText.setVisibility(View.VISIBLE);
                }
            } else {
                if (searchType.equalsIgnoreCase("food")) {
                    foodSearchResultItems.clear();
                } else if (searchType.equalsIgnoreCase("college")) {
                    collegeSearchResultItems.clear();
                } else if (searchType.equalsIgnoreCase("vendor")) {
                    vendorSearchResultItems.clear();
                } else if (searchType.equalsIgnoreCase("global")) {
                    globalSearchResultItems.clear();
                }
                searchResultAdapter.notifyDataSetChanged();
                rvSearchResult.setVisibility(View.GONE);
                tvNoResultFoundText.setVisibility(View.VISIBLE);
               // Toast.makeText(SearchActivity.this, errorNodeJsonObj.getString("errMsg"), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
