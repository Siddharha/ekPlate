package com.ekplate.android.fragments.menumodule;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
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
import com.ekplate.android.adapters.vendormodule.VendorListItemAdapter;
import com.ekplate.android.appinterfaces.BackgroundActionInterface;
import com.ekplate.android.models.vendormodule.VendorItem;
import com.ekplate.android.utils.CallServiceAction;
import com.ekplate.android.utils.ConstantClass;
import com.ekplate.android.utils.NetworkConnectionCheck;
import com.ekplate.android.utils.Pref;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by user on 29-12-2015.
 */
public class MyStuffBookmarksFragment extends Fragment implements BackgroundActionInterface {

    private RecyclerView rlMyStuffBookmarks;
    private ArrayList<VendorItem> vendorItems;
    private VendorListItemAdapter itemAdapter;
    private Pref _pref;
    private NetworkConnectionCheck _connection;
    private CallServiceAction _serviceAction;
    private LinearLayout llProgressbarMyStuffBookmarkList, llErrorThreeMyStuffBookmarkList;
    private TextView tvErrorTwoMessage;
    private ImageView ivReloadThirdErrorScreen, ivErrorMainImage;
    public static String optionId;

    public static MyStuffBookmarksFragment getInstance(){
        MyStuffBookmarksFragment fragment = new MyStuffBookmarksFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_stuff_bookmarks, container, false);
        initialize(rootView);
        setUpBookmarksVendorList();
        return rootView;
    }

    private void initialize(View rootView){
        rlMyStuffBookmarks = (RecyclerView) rootView.findViewById(R.id.rlMyStuffBookmarks);
        llProgressbarMyStuffBookmarkList = (LinearLayout) rootView.findViewById(R.id.llProgressbarMyStuffBookmarkList);
        llErrorThreeMyStuffBookmarkList = (LinearLayout) rootView.findViewById(R.id.llErrorThreeMyStuffBookmarkList);
        tvErrorTwoMessage = (TextView) rootView.findViewById(R.id.tvErrorTwoMessage);
        ivErrorMainImage = (ImageView) rootView.findViewById(R.id.ivErrorMainImage);
        ivReloadThirdErrorScreen = (ImageView) rootView.findViewById(R.id.ivReloadThirdErrorScreen);
        rlMyStuffBookmarks.setLayoutManager(new LinearLayoutManager(getActivity()));
        vendorItems = new ArrayList<>();
        itemAdapter = new VendorListItemAdapter(getActivity(), vendorItems, "bookmarks", "my_stuff");
        _pref = new Pref(getActivity());
        _connection = new NetworkConnectionCheck(getActivity());
        _serviceAction = new CallServiceAction(getActivity());
        _serviceAction.actionInterface = MyStuffBookmarksFragment.this;
        Random random = new Random();
        int randomNumber = random.nextInt(3);
        if (randomNumber == 0){
            ivErrorMainImage.setImageResource(R.drawable.icon_error_one);
        } else if (randomNumber == 1) {
            ivErrorMainImage.setImageResource(R.drawable.icon_error_two);
        } else if (randomNumber == 2) {
            ivErrorMainImage.setImageResource(R.drawable.icon_error_three);
        }
    }

    private void setUpBookmarksVendorList(){
        if (_connection.isNetworkAvailable()) {
            rlMyStuffBookmarks.setAdapter(itemAdapter);
            setInputParamForList(1);
        } else {
            tvErrorTwoMessage.setText(R.string.message_on_internet_not_connection);
            llProgressbarMyStuffBookmarkList.setVisibility(View.GONE);
            llErrorThreeMyStuffBookmarkList.setVisibility(View.VISIBLE);
        }
    }

    private void setInputParamForList(int currentPageIndex){
        try {
            JSONObject parentJsonObj = new JSONObject();
            JSONObject childJsonObj = new JSONObject();
            childJsonObj.put("accessToken", _pref.getSession(ConstantClass.ACCESS_TOKEN));
            childJsonObj.put("city", _pref.getSession(ConstantClass.TAG_SELECTED_CITY_LOCATION));
            childJsonObj.put("foodType", _pref.getSession(ConstantClass.TAG_SELECTED_FOOD_TYPE));
            childJsonObj.put("keyPage", "bookmark");
            childJsonObj.put("optionId", optionId);
            childJsonObj.put("currentPage", currentPageIndex);
            childJsonObj.put("latitude", _pref.getSession(ConstantClass.TAG_LATITUDE));
            childJsonObj.put("longitude", _pref.getSession(ConstantClass.TAG_LONGITUDE));
            parentJsonObj.put("data", childJsonObj);
            Log.e("input data", parentJsonObj.toString());
            _serviceAction.requestVersionApi(parentJsonObj, "get-vendor-list");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onStarted() {

    }

    @Override
    public void onCompleted(JSONObject response) {
        Log.e("booking response", response.toString());
        try {
            JSONObject jsonObjError = response.getJSONObject("errNode");
            if(jsonObjError.getInt("errCode") == 0) {
                JSONObject jsonObjData = response.getJSONObject("data");
                JSONArray jsonArrListItem = jsonObjData.getJSONArray("listItem");
                if(jsonArrListItem.length() > 0) {
                    for (int i = 0; i < jsonArrListItem.length(); i++) {
                        JSONObject jsonObjListItem = jsonArrListItem.getJSONObject(i);
                        VendorItem _item = new VendorItem();
                        _item.setId(jsonObjListItem.getInt("id"));
                        _item.setVendorName(jsonObjListItem.getString("vendorName"));
                        _item.setInlineAddress(jsonObjListItem.getString("inlineAddress"));
                        _item.setInlineFoodMenu(jsonObjListItem.getString("inlineFoodMenu"));
                        _item.setOpenStatus(jsonObjListItem.getBoolean("openStatus"));
                        _item.setNoOfReviews(jsonObjListItem.getInt("noOfReviews"));
                        _item.setNoOfLikes(jsonObjListItem.getString("noOfLikes"));
                        _item.setRating(jsonObjListItem.getString("rating"));
                        _item.setDistance(jsonObjListItem.getString("distance"));
                        _item.setBookmarkStatus(jsonObjListItem.getBoolean("bookmarkStatus"));
                        _item.setFoodType(jsonObjListItem.getString("foodType"));
                        _item.setLatitude(Double.parseDouble(jsonObjListItem.getString("latitude")));
                        _item.setLongitude(Double.parseDouble(jsonObjListItem.getString("longitude")));
                        _item.setInnerCircleIcon(R.drawable.icon_vendor_list_location);
                        _item.setInnerCircleText(jsonObjListItem.getString("distance"));
                       // _item.setVendorDetails(jsonObjListItem.getJSONObject("vendorDetails").toString());
                        vendorItems.add(_item);
                    }
                    itemAdapter.notifyDataSetChanged();
                    rlMyStuffBookmarks.setVisibility(View.VISIBLE);
                    llProgressbarMyStuffBookmarkList.setVisibility(View.GONE);
                } else {
                    llProgressbarMyStuffBookmarkList.setVisibility(View.GONE);
                    llErrorThreeMyStuffBookmarkList.setVisibility(View.VISIBLE);
                    Log.e("length booking", String.valueOf(jsonArrListItem.length()));
                    tvErrorTwoMessage.setText("No bookmarked vendor available.");
                    ivReloadThirdErrorScreen.setVisibility(View.GONE);
                }
            } else {
                Toast.makeText(getActivity(), jsonObjError.getString("errMsg"), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
