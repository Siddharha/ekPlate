package com.ekplate.android.fragments.collegemodule;


import android.os.Bundle;
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
import android.widget.Toast;

import com.ekplate.android.R;
import com.ekplate.android.adapters.collegemodule.CollegeListAdapter;
import com.ekplate.android.appinterfaces.BackgroundActionInterface;
import com.ekplate.android.models.collegemodule.CollageListItem;
import com.ekplate.android.utils.CallServiceAction;
import com.ekplate.android.utils.ConstantClass;
import com.ekplate.android.utils.NetworkConnectionCheck;
import com.ekplate.android.utils.Pref;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

public class CollegeListFragment extends Fragment implements BackgroundActionInterface {

    private RecyclerView rvCollageMostLikedList;
    private CollegeListAdapter adapter;
    LinearLayoutManager rvCollageLayoutManager;
    private ArrayList<CollageListItem> collageListItems;
    private LinearLayout llProgressbarPopularCollageList, llErrorThreeCollagePopularList;
    private Pref _pref;
    private NetworkConnectionCheck _connection;
    private CallServiceAction _serviceAction;
    private int optionId, collegeScreenPosition;
    private String routeScreen, collegeListApi;
    private TextView tvThirdErrorScreenTextComment, tvErrorTwoMessage;
    private ImageView ivReloadThirdErrorScreen, ivErrorMainImage;
    private ProgressBar pbPagination;
    private boolean loading = true;
    private int pastVisiblesItems, visibleItemCount, totalItemCount, totalPage, currentPage = 1;

    public static CollegeListFragment newInstance(int optionId, String routeScreen,
                                                        int collegeScreenPosition) {
        CollegeListFragment fragment = new CollegeListFragment();
        Bundle args = new Bundle();
        args.putInt("optionId", optionId);
        args.putString("routeScreen", routeScreen);
        args.putInt("collegeScreenPosition", collegeScreenPosition);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        optionId = getArguments().getInt("optionId");
        routeScreen = getArguments().getString("routeScreen");
        collegeScreenPosition = getArguments().getInt("collegeScreenPosition");
        setCollegeListApi(collegeScreenPosition);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setCollegeListApi(collegeScreenPosition);
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_collage_list_layout, container, false);
        initialize(rootView);


        loadPopularCollageListItem();
        return rootView;
    }

    private void initialize(View rootView){
        rvCollageMostLikedList = (RecyclerView)
                rootView.findViewById(R.id.rvCollageMostLikedList);
        pbPagination = (ProgressBar)rootView.findViewById(R.id.pbPagination);
        tvThirdErrorScreenTextComment = (TextView) rootView.findViewById(R.id.tvThirdErrorScreenTextComment);
        ivReloadThirdErrorScreen = (ImageView) rootView.findViewById(R.id.ivReloadThirdErrorScreen);
        ivErrorMainImage = (ImageView) rootView.findViewById(R.id.ivErrorMainImage);
        tvErrorTwoMessage = (TextView) rootView.findViewById(R.id.tvErrorTwoMessage);
        llProgressbarPopularCollageList = (LinearLayout) rootView.findViewById(R.id.llProgressbarPopularCollageList);
        llErrorThreeCollagePopularList = (LinearLayout) rootView.findViewById(R.id.llErrorThreeCollagePopularList);
        rvCollageLayoutManager = new LinearLayoutManager(getActivity());
        rvCollageMostLikedList.setLayoutManager(rvCollageLayoutManager);
        collageListItems = new ArrayList<CollageListItem>();
        adapter = new CollegeListAdapter(getActivity(), collageListItems, routeScreen);
        _pref = new Pref(getActivity());
        _connection = new NetworkConnectionCheck(getActivity());
        _serviceAction = new CallServiceAction(getActivity());
        _serviceAction.actionInterface = CollegeListFragment.this;
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

    private void loadPopularCollageListItem(){
        rvCollageMostLikedList.setAdapter(adapter);
        if(_connection.isNetworkAvailable()) {
            setInputParamForPopularCollageList(currentPage);
        } else {
            tvErrorTwoMessage.setText(R.string.message_on_internet_not_connection);
            llErrorThreeCollagePopularList.setVisibility(View.VISIBLE);
            llProgressbarPopularCollageList.setVisibility(View.GONE);
        }

        rvCollageMostLikedList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(dy > 0) {
                    visibleItemCount = rvCollageLayoutManager.getChildCount();
                    totalItemCount = rvCollageLayoutManager.getItemCount();
                    pastVisiblesItems = rvCollageLayoutManager.findFirstVisibleItemPosition();
                    Log.w("val:","i"+ visibleItemCount+","+pastVisiblesItems+","+totalItemCount);
                    if (loading) {
                        if ( (visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            loading = false;
                            //Do pagination.. i.e. fetch new data
                            if(currentPage < totalPage){
                                setInputParamForPopularCollageList(++currentPage);
                                pbPagination.setVisibility(View.VISIBLE);

                            }
                        }
                    }
                }
            }
        });
    }

    private void setInputParamForPopularCollageList(int currentPage){
        try {
            JSONObject parentJsonObj = new JSONObject();
            JSONObject childJsonObj = new JSONObject();
            childJsonObj.put("accessToken", _pref.getSession(ConstantClass.ACCESS_TOKEN));
            childJsonObj.put("optionId", optionId);
            childJsonObj.put("city", _pref.getSession(ConstantClass.TAG_SELECTED_CITY_LOCATION));
            childJsonObj.put("currentPage", currentPage);
            childJsonObj.put("latitude", _pref.getSession(ConstantClass.TAG_LATITUDE));
            childJsonObj.put("longitude", _pref.getSession(ConstantClass.TAG_LONGITUDE));
            parentJsonObj.put("data", childJsonObj);
            Log.e("input data", parentJsonObj.toString());
            _serviceAction.requestVersionApi(parentJsonObj, collegeListApi);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onStarted() {

    }

    @Override
    public void onCompleted(JSONObject response) {
        Log.e("popular response", response.toString());
        //collageListItems.clear();
        try {
            JSONObject errorNodeJsonObj = response.getJSONObject("errNode");
            if (errorNodeJsonObj.getInt("errCode") == 0) {
                JSONObject dataJsonObject = response.getJSONObject("data");
                JSONArray collageListJsonArray = dataJsonObject.getJSONArray("listItem");
                totalPage = dataJsonObject.getInt("totalPage");
                if (collageListJsonArray.length() > 0) {
                    for (int i = 0; i < collageListJsonArray.length(); i++) {
                        JSONObject _collageListItem = collageListJsonArray.getJSONObject(i);
                        CollageListItem _item = new CollageListItem();
                        _item.setId(_collageListItem.getInt("id"));
                        _item.setTitle(_collageListItem.getString("collageName"));
                        _item.setAddress(_collageListItem.getString("collageAddress"));
                        _item.setDistance(_collageListItem.getString("collageDistance"));
                        _item.setLikeStatus(_collageListItem.getBoolean("collageLikeStatus"));
                        _item.setKeyValue(_collageListItem.getString("keyValue"));
                        if (!_collageListItem.getString("collageLatitude").equalsIgnoreCase("")) {
                            _item.setLatitude(_collageListItem.getDouble("collageLatitude"));
                        } else {
                            _item.setLatitude(0.0);
                        }
                        if (!_collageListItem.getString("collageLongitude").equalsIgnoreCase("")) {
                            _item.setLongitude(_collageListItem.getDouble("collageLongitude"));
                        } else {
                            _item.setLongitude(0.0);
                        }
                        collageListItems.add(_item);
                        pbPagination.setVisibility(View.GONE);
                    }
                    adapter.notifyDataSetChanged();
                    rvCollageMostLikedList.setVisibility(View.VISIBLE);
                    llProgressbarPopularCollageList.setVisibility(View.GONE);
                    loading = true;
                } else {
                    llErrorThreeCollagePopularList.setVisibility(View.VISIBLE);
                    llProgressbarPopularCollageList.setVisibility(View.GONE);
                    tvThirdErrorScreenTextComment.setText("No college available near your location.");
                    ivReloadThirdErrorScreen.setVisibility(View.GONE);
                }
            } else {
                Toast.makeText(getActivity(), errorNodeJsonObj.getString("errMsg"), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setCollegeListApi(int collegeScreenPosition){
        switch (collegeScreenPosition) {
            case 0:
                collegeListApi = "collage-popularity-list";
                break;
            case 1:
                collegeListApi = "collage-alphabet-list";
                break;
            case 2:
                collegeListApi = "collage-near-by-list";
                break;
        }
    }

}
