package com.ekplate.android.fragments.vendormodule;

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
import android.widget.ProgressBar;
import android.widget.TextView;
import com.ekplate.android.R;
import com.ekplate.android.activities.vendormodule.VendorsActivity;
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
 * Created by Rahul on 8/27/2015.
 */
public class VendorListFragment extends Fragment implements BackgroundActionInterface {

    private RecyclerView rvRatingVendorList;
    public ArrayList<VendorItem> vendorItems, vendorItemsForMap;
    private VendorListItemAdapter itemAdapter;
    private LinearLayout llProgressbarVendorRatingList;
    private View llErrorTwoVendorRatingListLayout;
    private Pref _pref;
    private int optionId;
    private ProgressBar pbPagination;
    private String keyValue, routeFrom, prefixKeyValue, vendorListType,foodCategoryType;
    private NetworkConnectionCheck _connection;
    private CallServiceAction _serviceAction;
    private int totalItem, screenPosition;
    private ImageView ivLoaderErrorTwo, ivErrorMainImage;
    private TextView tvErrorTwoMessage;
    private boolean loading = true;
    private int pastVisiblesItems, visibleItemCount, totalItemCount, totalPage, currentPage = 1;
    private LinearLayoutManager rvVendorListLayoutManager;
    //private String responseStr = "{\"data\":{\"totalItem\":\"2\",\"listItem\":[{\"id\":\"1\",\"vendorName\":\"Rahul Das\",\"inlineAddress\":\"Garia, Kolkata\",\"inlineFoodMenu\":\"Pani Puri, Bhel\",\"closeStatus\":\"true\",\"noOfReviews\":\"10\",\"noOfLikes\":\"5\",\"rating\":\"5.5\",\"distance\":\"2.5 km\",\"bookmarkStatus\":\"true\",\"foodType\":\"veg/nonveg\",\"latitude\":\"22.5790512\",\"longitude\":\"88.4808574\",\"vendorDetails\":{\"mainImageUrl\":\"\",\"noOfChecking\":\"200\",\"noOfLikes\":\"100\",\"noOfRating\":\"2.6\",\"noOfReviews\":\"20\",\"shopName\":\"\",\"longAddress\":\"BandraEast,           Mumbai\",\"bookmarkedStatus\":\"true/false\",\"hyginePoint\":\"2.4\",\"tastePoint\":\"1.9\",\"inlineFoodMenu\":\"PaniPuri,           Bhel\",\"openStatus\":\"true/false\",\"closingTime\":\"11pm\",\"latitude\":\"22.5790512\",\"longitude\":\"88.4808574\",\"imageGallery\":[{\"imageUrl\":\"\"},{\"imageUrl\":\"\"},{\"imageUrl\":\"\"}],\"longFoodMenu\":[{\"id\":\"1\",\"itemName\":\"PaniPuri\",\"itemPrice\":\"rs.10perPlate\"},{\"id\":\"2\",\"itemName\":\"BhelPuri\",\"itemPrice\":\"rs.10perPlate\"},{\"id\":\"3\",\"itemName\":\"VaraPao\",\"itemPrice\":\"rs.10perPlate\"}],\"userReview\":[{\"id\":\"1\",\"reviewerImage\":\"\",\"reviewerName\":\"xxxxx\",\"reviewerRating\":\"2\",\"lastVisitDate\":\"\",\"review\":\"\",\"noOfAgreed\":\"12\"},{\"id\":\"2\",\"reviewerImage\":\"\",\"reviewerName\":\"yyyyy\",\"reviewerRating\":\"4\",\"lastVisitDate\":\"\",\"review\":\"\",\"noOfAgreed\":\"20\"},{\"id\":\"3\",\"reviewerImage\":\"\",\"reviewerName\":\"aaaaaa\",\"reviewerRating\":\"6\",\"lastVisitDate\":\"\",\"review\":\"\",\"noOfAgreed\":\"18\"},{\"id\":\"4\",\"reviewerImage\":\"\",\"reviewerName\":\"zzzzzz\",\"reviewerRating\":\"22\",\"lastVisitDate\":\"\",\"review\":\"\",\"noOfAgreed\":\"28\"}],\"myStoryDetails\":{\"myStoryId\":\"\",\"myStoryLikeCount\":\"\",\"myStoryCommentCount\":\"\",\"myStoryContentUrl\":\"http://uat.ekplate.com/html/my_story.html\"}}},{\"id\":\"1\",\"vendorName\":\"Avinash Bhal\",\"inlineAddress\":\"Bandra East, Mumbai\",\"inlineFoodMenu\":\"Pani Puri, Bhel\",\"closeStatus\":\"true\",\"noOfReviews\":\"10\",\"noOfLikes\":\"5\",\"rating\":\"5.5\",\"distance\":\"2.5 km\",\"bookmarkStatus\":\"true\",\"foodType\":\"veg\",\"latitude\":\"22.5790512\",\"longitude\":\"88.4808574\",\"vendorDetails\":{\"mainImageUrl\":\"\",\"noOfChecking\":\"200\",\"noOfLikes\":\"100\",\"noOfRating\":\"2.6\",\"noOfReviews\":\"20\",\"shopName\":\"\",\"longAddress\":\"BandraEast,           Mumbai\",\"bookmarkedStatus\":\"true/false\",\"hyginePoint\":\"2.4\",\"tastePoint\":\"1.9\",\"inlineFoodMenu\":\"PaniPuri,           Bhel\",\"openStatus\":\"true/false\",\"closingTime\":\"11pm\",\"latitude\":\"22.5790512\",\"longitude\":\"88.4808574\",\"imageGallery\":[{\"imageUrl\":\"\"},{\"imageUrl\":\"\"},{\"imageUrl\":\"\"}],\"longFoodMenu\":[{\"id\":\"1\",\"itemName\":\"PaniPuri\",\"itemPrice\":\"rs.10perPlate\"},{\"id\":\"2\",\"itemName\":\"BhelPuri\",\"itemPrice\":\"rs.10perPlate\"},{\"id\":\"3\",\"itemName\":\"VaraPao\",\"itemPrice\":\"rs.10perPlate\"}],\"userReview\":[{\"id\":\"1\",\"reviewerImage\":\"\",\"reviewerName\":\"xxxxx\",\"reviewerRating\":\"2\",\"lastVisitDate\":\"\",\"review\":\"\",\"noOfAgreed\":\"12\"},{\"id\":\"2\",\"reviewerImage\":\"\",\"reviewerName\":\"yyyyy\",\"reviewerRating\":\"4\",\"lastVisitDate\":\"\",\"review\":\"\",\"noOfAgreed\":\"20\"},{\"id\":\"3\",\"reviewerImage\":\"\",\"reviewerName\":\"aaaaaa\",\"reviewerRating\":\"6\",\"lastVisitDate\":\"\",\"review\":\"\",\"noOfAgreed\":\"18\"},{\"id\":\"4\",\"reviewerImage\":\"\",\"reviewerName\":\"zzzzzz\",\"reviewerRating\":\"22\",\"lastVisitDate\":\"\",\"review\":\"\",\"noOfAgreed\":\"28\"}],\"myStoryDetails\":{\"myStoryId\":\"\",\"myStoryLikeCount\":\"\",\"myStoryCommentCount\":\"\",\"myStoryContentUrl\":\"http://uat.ekplate.com/html/my_story.html\"}}}]},\"errNode\":{\"errCode\":\"0\",\"errMsg\":\"\"}}";

    public static VendorListFragment newInstance(int optionId, String keyValue, String routeFrom,String foodCategoryType,
                                                   int screenPosition) {
        VendorListFragment fragment = new VendorListFragment();
        Bundle args = new Bundle();
        args.putInt("optionId", optionId);
        args.putString("keyValue", keyValue);
        args.putString("routeFrom", routeFrom);
        args.putString("foodCategoryType", foodCategoryType);
        args.putInt("screenPosition", screenPosition);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        optionId = getArguments().getInt("optionId");
        keyValue = getArguments().getString("keyValue");
        routeFrom = getArguments().getString("routeFrom");
        foodCategoryType = getArguments().getString("foodCategoryType");
        screenPosition = getArguments().getInt("screenPosition");
        getVendorScreenType(screenPosition);
        if (!routeFrom.equalsIgnoreCase("home")){
            if (keyValue.equalsIgnoreCase("vendor")) {
                keyValue = prefixKeyValue;
            } else {
                keyValue = prefixKeyValue + "-" + keyValue;
                Log.e("//",keyValue);
                String[] p = keyValue.split("-");
                if(p.length >=3)
                {
                    keyValue = p[p.length-2]+"-"+p[p.length-1];
                    keyValue = prefixKeyValue + "-" + keyValue;
                }
            }
        } else {
            keyValue = prefixKeyValue;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_vendors_list_layout, container, false);
        initialize(rootView);
        rvRatingVendorList.setAdapter(itemAdapter);
        setListeners();
        if(_connection.isNetworkAvailable()) {
            setInputParamForList(currentPage);
        } else {
            tvErrorTwoMessage.setText(R.string.message_on_internet_not_connection);
            ivLoaderErrorTwo.setVisibility(View.VISIBLE);
            llProgressbarVendorRatingList.setVisibility(View.GONE);
            llErrorTwoVendorRatingListLayout.setVisibility(View.VISIBLE);
        }
        return rootView;
    }

    private void initialize(View rootView){
        llProgressbarVendorRatingList = (LinearLayout) rootView.findViewById(R.id.llProgressbarVendorRatingList);
        llErrorTwoVendorRatingListLayout = rootView.findViewById(R.id.llErrorTwoVendorRatingListLayout);
        rvRatingVendorList = (RecyclerView) rootView.findViewById(R.id.rvRatingVendorList);
        ivLoaderErrorTwo = (ImageView) rootView.findViewById(R.id.ivLoaderErrorTwo);
        ivErrorMainImage = (ImageView) rootView.findViewById(R.id.ivErrorMainImage);
        tvErrorTwoMessage = (TextView) rootView.findViewById(R.id.tvErrorTwoMessage);
        rvVendorListLayoutManager = new LinearLayoutManager(getActivity());
        rvRatingVendorList.setLayoutManager(rvVendorListLayoutManager);
        vendorItems = new ArrayList<VendorItem>();
        pbPagination = (ProgressBar)rootView.findViewById(R.id.pbPagination);
        vendorItemsForMap = new ArrayList<>();
        itemAdapter = new VendorListItemAdapter(getActivity(), vendorItems, vendorListType, routeFrom);
        _pref = new Pref(getActivity());
        _connection = new NetworkConnectionCheck(getActivity());
        _serviceAction = new CallServiceAction(getActivity());
        _serviceAction.actionInterface = VendorListFragment.this;
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

    private void setListeners(){
        ivLoaderErrorTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _connection.getNetworkActiveAlert().show();
            }
        });

        rvRatingVendorList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy > 0) {
                    visibleItemCount = rvVendorListLayoutManager.getChildCount();
                    totalItemCount = rvVendorListLayoutManager.getItemCount();
                    pastVisiblesItems = rvVendorListLayoutManager.findFirstVisibleItemPosition();
                    if (loading) {
                        if ( (visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            loading = false;
                            //Do pagination.. i.e. fetch new data
                            if(currentPage < totalPage){
                                setInputParamForList(++currentPage);
                                pbPagination.setVisibility(View.VISIBLE);

                            }
                        }
                    }
                }
            }
        });
    }

    private void setInputParamForList(int currentPageIndex){
        try {
            JSONObject parentJsonObj = new JSONObject();
            JSONObject childJsonObj = new JSONObject();
            childJsonObj.put("accessToken", _pref.getSession(ConstantClass.ACCESS_TOKEN));
            childJsonObj.put("city", _pref.getSession(ConstantClass.TAG_SELECTED_CITY_LOCATION));
            childJsonObj.put("foodType", _pref.getSession(ConstantClass.TAG_SELECTED_FOOD_TYPE));
            childJsonObj.put("keyPage", keyValue);
            childJsonObj.put("optionId", optionId);
            childJsonObj.put("currentPage", currentPageIndex);
            childJsonObj.put("food_category_type",foodCategoryType );
            childJsonObj.put("sendVendorDetails","no");
            childJsonObj.put("latitude", _pref.getSession(ConstantClass.TAG_LATITUDE));
            childJsonObj.put("longitude", _pref.getSession(ConstantClass.TAG_LONGITUDE));
            parentJsonObj.put("data", childJsonObj);
            Log.e("inputVrndorListdata", parentJsonObj.toString());
            _serviceAction.requestVersionApi(parentJsonObj, "get-vendor-list");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setInputParamForFullList(){
        try {
            JSONObject parentJsonObj = new JSONObject();
            JSONObject childJsonObj = new JSONObject();
            childJsonObj.put("accessToken", _pref.getSession(ConstantClass.ACCESS_TOKEN));
            childJsonObj.put("city", _pref.getSession(ConstantClass.TAG_SELECTED_CITY_LOCATION));
            childJsonObj.put("foodType", _pref.getSession(ConstantClass.TAG_SELECTED_FOOD_TYPE));
            childJsonObj.put("keyPage", keyValue);
            childJsonObj.put("optionId", optionId);
            childJsonObj.put("currentPage", "0");
            childJsonObj.put("latitude", _pref.getSession(ConstantClass.TAG_LATITUDE));
            childJsonObj.put("longitude", _pref.getSession(ConstantClass.TAG_LONGITUDE));
            parentJsonObj.put("data", childJsonObj);
            Log.e("input data", parentJsonObj.toString());
            _serviceAction.requestVersionApi(parentJsonObj, "get-vendor-list");
        } catch (Exception e){
            e.printStackTrace();
        }
    }           //For Tomorrow Work _Siddhartha Maji
    @Override
    public void onStarted() {

    }

    @Override
    public void onCompleted(JSONObject response) {
        Log.e("rating response", response.toString());
        try {
            JSONObject jsonObjError = response.getJSONObject("errNode");
            if(jsonObjError.getInt("errCode") == 0) {
                JSONObject jsonObjData = response.getJSONObject("data");
                JSONArray jsonArrListItem = jsonObjData.getJSONArray("listItem");
                totalPage = jsonObjData.getInt("totalPage");
                if (jsonArrListItem.length() > 0) {
                    Log.e("jsonArrListItem length ", String.valueOf(jsonArrListItem.length()));
                    /*if(jsonArrListItem.length() < 30) {
                        currentPage = 1;
                    }*/
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
                        _pref.setSession(ConstantClass.TAG_VENDOR_DETAILS_LIKE_COUNT +
                                String.valueOf(jsonObjListItem.getInt("id")), jsonObjListItem.getString("noOfLikes"));
                        _pref.setSession(ConstantClass.TAG_VENDOR_DETAILS_BOOKMARK_COUNT +
                                String.valueOf(jsonObjListItem.getInt("id")), jsonObjListItem.optString("bookmarkNo"));
                        if(screenPosition == 0 || screenPosition == 1) {
                            _item.setInnerCircleIcon(R.drawable.icon_vendor_list_location);
                            _item.setInnerCircleText(jsonObjListItem.getString("distance") + "KM");
                        } else if (screenPosition == 2) {
                            _item.setInnerCircleIcon(R.drawable.icon_vendor_list_reviews);
                            _item.setInnerCircleText(jsonObjListItem.getInt("noOfReviews") + " Reviews");
                        } else if (screenPosition == 3 || screenPosition == 4) {
                            _item.setInnerCircleIcon(R.drawable.icon_vendor_list_likes);
                            _item.setInnerCircleText(jsonObjListItem.getString("noOfLikes") + " Likes");
                        }
                        _item.setRating(jsonObjListItem.getString("rating"));
                        _item.setDistance(jsonObjListItem.getString("distance"));
                        _item.setBookmarkStatus(Boolean.parseBoolean(jsonObjListItem.getString("bookmarkStatus")));
                        _item.setFoodType(jsonObjListItem.optString("foodType"));
                        _item.setLatitude(Double.parseDouble(jsonObjListItem.getString("latitude")));
                        _item.setLongitude(Double.parseDouble(jsonObjListItem.getString("longitude")));
                        _item.setLocationPositionId("(" + jsonObjListItem.getString("latitude") + ","
                                + jsonObjListItem.getString("longitude") + ")");
                      //  _item.setVendorDetails(jsonObjListItem.getJSONObject("vendorDetails").toString());
                        vendorItems.add(_item);
                       // vendorItemsForMap.add(_item);
                        pbPagination.setVisibility(View.GONE);
                        llProgressbarVendorRatingList.setVisibility(View.GONE);
                    }
                    setVendorListForMap(vendorItemsForMap);
                    itemAdapter.notifyDataSetChanged();
                    llProgressbarVendorRatingList.setVisibility(View.GONE);
                    rvRatingVendorList.setVisibility(View.VISIBLE);
                    loading = true;
                } else {
                    //tvErrorTwoMessage.setText(R.string.message_on_data_found);
                    tvErrorTwoMessage.setText("Oh no ! You don't have any bookmarks ! Click the bookmark icon on any vendor to have him saved here !");
                    llProgressbarVendorRatingList.setVisibility(View.GONE);
                    ivLoaderErrorTwo.setVisibility(View.GONE);
                    llErrorTwoVendorRatingListLayout.setVisibility(View.VISIBLE);
                    pbPagination.setVisibility(View.GONE);
                }
            } else {
                //Toast.makeText(getActivity(), jsonObjError.getString("errMsg"), Toast.LENGTH_LONG).show();
                pbPagination.setVisibility(View.GONE);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
       // VendorMapActivity.vendorItemsList = vendorItemsForMap;
        Log.e("currentPage", String.valueOf(currentPage));
        currentPage = 1;
    }

    private void getVendorScreenType(int screenPosition){
        switch (screenPosition) {
            case 0:
                prefixKeyValue = "nearByList";
                vendorListType = "nearby";
                break;
            case 1:
                prefixKeyValue = "openList";
                vendorListType = "open";
                break;
            case 2:
                prefixKeyValue = "ratingList";
                vendorListType = "rating";
                break;
            case 3:
                prefixKeyValue = "bookmark";
                vendorListType = "booking";
                break;
            default:
                prefixKeyValue = "likeList";
                vendorListType = "likes";
                break;
        }
    }

    private void setVendorListForMap(ArrayList<VendorItem> vendorItems){
        switch (screenPosition) {
            case 0:
               // ((VendorsActivity)getActivity()).nearVendorItems.clear();
                ((VendorsActivity)getActivity()).nearVendorItems = vendorItems;
                break;
            case 1:
               // ((VendorsActivity)getActivity()).openVendorItems.clear();
                ((VendorsActivity)getActivity()).openVendorItems = vendorItems;
                break;
            case 2:
               // ((VendorsActivity)getActivity()).ratingVendorItems.clear();
                ((VendorsActivity)getActivity()).ratingVendorItems = vendorItems;
                break;
            case 3:
                // ((VendorsActivity)getrahulActivity()).bookmarkVendorItems.clear();
                ((VendorsActivity)getActivity()).bookmarkVendorItems = vendorItems;
                break;
            default:
              // ((VendorsActivity)getActivity()).likesVendorItems.clear();
                ((VendorsActivity)getActivity()).likesVendorItems = vendorItems;
                break;
        }
    }
}
