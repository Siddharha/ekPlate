package com.ekplate.android.fragments.foodmodule;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.exceptions.CleverTapMetaDataNotFoundException;
import com.clevertap.android.sdk.exceptions.CleverTapPermissionsNotSatisfied;
import com.ekplate.android.R;
import com.ekplate.android.adapters.foodmodule.FoodItemAdapter;
import com.ekplate.android.appinterfaces.BackgroundActionInterface;
import com.ekplate.android.models.foodmodule.FoodItem;
import com.ekplate.android.utils.CallServiceAction;
import com.ekplate.android.utils.ConstantClass;
import com.ekplate.android.utils.NetworkConnectionCheck;
import com.ekplate.android.utils.Pref;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Rahul on 8/26/2015.
 */
public class FoodListItemFragment extends Fragment implements BackgroundActionInterface {

    private RecyclerView rvAlphabetFoodList;
    private GridLayoutManager mLayoutManager;
    private ArrayList<FoodItem> foodItems;
    private FoodItemAdapter itemAdapter;
    private LinearLayout llProgressbarAlphabetFoodList, llErrorTwoFoodListAlphabet;
    private Pref _pref;
    private NetworkConnectionCheck _connection;
    private CallServiceAction _serviceAction;
    private int optionId, screenPosition;
    private int pastVisiblesItems, visibleItemCount, totalItemCount, totalNoFoodItem, currentPage = 1;
    private String tagIds, foodListScreen,foodCategoryType;
    private boolean loading = true;
    private ImageView ivErrorMainImage;
    private TextView tvErrorTwoMessage;


    public static FoodListItemFragment newInstance(int optionId, String tagIds, String foodCategoryType,
                                                         int screenPosition) {
        FoodListItemFragment fragment = new FoodListItemFragment();
        Bundle args = new Bundle();
        args.putInt("optionId", optionId);
        args.putString("tagIds", tagIds);
        args.putString("foodCategoryType", foodCategoryType);
        args.putInt("screenPosition", screenPosition);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        optionId = getArguments().getInt("optionId");
        tagIds = getArguments().getString("tagIds");
        foodCategoryType = getArguments().getString("foodCategoryType");
        screenPosition = getArguments().getInt("screenPosition");
        setFoodListScreen(screenPosition);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_food_list_layout, container, false);
        initialize(rootView);
        setUpFoodListItem();
        setListeners();
        return rootView;
    }

    private void initialize(View rootView){
        rvAlphabetFoodList = (RecyclerView) rootView.findViewById(R.id.rvAlphabetFoodList);
        llProgressbarAlphabetFoodList = (LinearLayout) rootView.findViewById(R.id.llProgressbarAlphabetFoodList);
        llErrorTwoFoodListAlphabet = (LinearLayout) rootView.findViewById(R.id.llErrorTwoFoodListAlphabet);
        ivErrorMainImage = (ImageView) rootView.findViewById(R.id.ivErrorMainImage);
        tvErrorTwoMessage = (TextView) rootView.findViewById(R.id.tvErrorTwoMessage);
       // rvAlphabetFoodList.setHasFixedSize(true);
    /*    rvAlphabetFoodList.addItemDecoration(new SpacesItemDecoration(getActivity()
                .getResources().getDimensionPixelSize(R.dimen.grid_space_size)));*/
        mLayoutManager = new GridLayoutManager(getActivity(), 2);
        rvAlphabetFoodList.setLayoutManager(mLayoutManager);
        foodItems = new ArrayList<FoodItem>();
        itemAdapter = new FoodItemAdapter(getActivity(), foodItems,foodCategoryType);
        _pref = new Pref(getActivity());
        _connection = new NetworkConnectionCheck(getActivity());
        _serviceAction = new CallServiceAction(getActivity());
        _serviceAction.actionInterface = FoodListItemFragment.this;
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
        rvAlphabetFoodList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy > 0) {
                    visibleItemCount = mLayoutManager.getChildCount();
                    totalItemCount = mLayoutManager.getItemCount();
                    pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();
                    if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            loading = false;
                            Log.v("...", "Last Item Wow !");
                            //Do pagination.. i.e. fetch new data
                            Log.e("foodItems size", String.valueOf(foodItems.size()));
                            Log.e("totalNoFoodItem", String.valueOf(totalNoFoodItem));
                            if(foodItems.size() < totalNoFoodItem){
                                setInputParamForAlphabetFoodList(++currentPage);
                            }
                        }
                    }
                }
            }
        });
    }

    private void setUpFoodListItem(){
        rvAlphabetFoodList.setAdapter(itemAdapter);
        if(_connection.isNetworkAvailable()) {
            setInputParamForAlphabetFoodList(currentPage);
        } else {
            tvErrorTwoMessage.setText(R.string.message_on_internet_not_connection);
            llProgressbarAlphabetFoodList.setVisibility(View.GONE);
            llErrorTwoFoodListAlphabet.setVisibility(View.VISIBLE);
        }
    }

    private void setInputParamForAlphabetFoodList(int currentPage){
        Log.e("currentPage", String.valueOf(currentPage));
        try {
            JSONObject parentJsonObj = new JSONObject();
            JSONObject childJsonObj = new JSONObject();
            childJsonObj.put("accessToken", _pref.getSession(ConstantClass.ACCESS_TOKEN));
            childJsonObj.put("optionId", optionId);
            childJsonObj.put("orderBy", foodListScreen);
            childJsonObj.put("tagIds", tagIds);
            childJsonObj.put("city", _pref.getSession(ConstantClass.TAG_SELECTED_CITY_LOCATION));
            childJsonObj.put("foodType", _pref.getSession(ConstantClass.TAG_SELECTED_FOOD_TYPE));
            childJsonObj.put("currentPage", currentPage);
            childJsonObj.put("food_category_type", foodCategoryType);
            parentJsonObj.put("data", childJsonObj);
            Log.v("input data", parentJsonObj.toString());
            _serviceAction.requestVersionApi(parentJsonObj, "get-food-list");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onStarted() {

    }

    @Override
    public void onCompleted(JSONObject response) {
        Log.e("alphabet food response", response.toString());
        try {
            JSONObject errorNodeJsonObj = response.getJSONObject("errNode");
            if (errorNodeJsonObj.getInt("errCode") == 0) {
                JSONObject dataJsonObject = response.getJSONObject("data");
                JSONArray foodListJsonArray = dataJsonObject.getJSONArray("foodlist");
                totalNoFoodItem = dataJsonObject.getInt("totalNoFoodItem");
                for(int i=0; i<foodListJsonArray.length(); i++){
                    JSONObject foodListItemJsonObj = foodListJsonArray.getJSONObject(i);
                    FoodItem _item = new FoodItem();
                    _item.setId(foodListItemJsonObj.getInt("foodId"));
                    _item.setFoodTitle(foodListItemJsonObj.getString("foodName"));
                    _item.setFoodItemImageUrl(foodListItemJsonObj.getString("foodImgUrl"));
                    _item.setFoodType(foodListItemJsonObj.getString("foodIsVegetarian"));
                    _item.setFoodDescription(foodListItemJsonObj.getString("foodDetails"));
                    _item.setKeyValue(foodListItemJsonObj.getString("keyValue"));
                    foodItems.add(_item);
                }
            }
            itemAdapter.notifyDataSetChanged();
            loading = true;
            llProgressbarAlphabetFoodList.setVisibility(View.GONE);
            rvAlphabetFoodList.setVisibility(View.VISIBLE);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setFoodListScreen(int screenPosition){
        switch (screenPosition){
            case 0:
                foodListScreen = "popularity";
                break;
            default:
                foodListScreen = "alphabet";
                break;
        }
    }

    /*********  Class to set the column space in food recycler view grid *********/

    private class SpacesItemDecoration extends RecyclerView.ItemDecoration{

        private int spaces;

        private SpacesItemDecoration(int spaces) {
            this.spaces = spaces;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            //outRect.left = spaces;
            //outRect.right = spaces;
            outRect.bottom = spaces;

            if(parent.getChildLayoutPosition(view) == 0 ||
                    parent.getChildLayoutPosition(view) == 1){
                outRect.top = spaces;
            }
        }
    }

    /**********  End of class **********/
}
