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

/**
 * Created by user on 29-12-2015.
 */
public class MyStuffCollegeFragment extends Fragment implements BackgroundActionInterface {
    private RecyclerView rlMyStuffCollege;
    private ArrayList<CollageListItem> CollageItemsListItems;
    private CollegeListAdapter adapter;
    private Pref _pref;
    private NetworkConnectionCheck _connection;
    private CallServiceAction _serviceAction;
    private LinearLayout llProgressbarMyStuffCollageList, llErrorThreeMyStuffCollageList;
    private TextView tvErrorTwoMessage;
    private ImageView ivReloadThirdErrorScreen, ivErrorMainImage;

    public static MyStuffCollegeFragment getInstance(){
        MyStuffCollegeFragment collegeFragment = new MyStuffCollegeFragment();
        return collegeFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_stuff_college, container, false);
        initialize(rootView);
        setUpCollegeList();
        return rootView;
    }

    private void initialize(View rootView){
        rlMyStuffCollege = (RecyclerView) rootView.findViewById(R.id.rlMyStuffCollege);
        llProgressbarMyStuffCollageList = (LinearLayout) rootView.findViewById(R.id.llProgressbarMyStuffCollageList);
        llErrorThreeMyStuffCollageList = (LinearLayout) rootView.findViewById(R.id.llErrorThreeMyStuffCollageList);
        ivErrorMainImage = (ImageView) rootView.findViewById(R.id.ivErrorMainImage);
        tvErrorTwoMessage = (TextView) rootView.findViewById(R.id.tvErrorTwoMessage);
        ivReloadThirdErrorScreen = (ImageView) rootView.findViewById(R.id.ivReloadThirdErrorScreen);
        rlMyStuffCollege.setLayoutManager(new LinearLayoutManager(getActivity()));
        CollageItemsListItems = new ArrayList<>();
        adapter = new CollegeListAdapter(getActivity(), CollageItemsListItems, "College");
        _pref = new Pref(getActivity());
        _connection = new NetworkConnectionCheck(getActivity());
        _serviceAction = new CallServiceAction(getActivity());
        _serviceAction.actionInterface = MyStuffCollegeFragment.this;
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

    private void setUpCollegeList(){
        rlMyStuffCollege.setAdapter(adapter);
        if (_connection.isNetworkAvailable()) {
            setInputParamForAlphabetCollageList(1);
        } else {
            tvErrorTwoMessage.setText(R.string.message_on_internet_not_connection);
            llProgressbarMyStuffCollageList.setVisibility(View.GONE);
            llErrorThreeMyStuffCollageList.setVisibility(View.VISIBLE);
        }
    }

    private void setInputParamForAlphabetCollageList(int currentPage){
        try {
            JSONObject parentJsonObj = new JSONObject();
            JSONObject childJsonObj = new JSONObject();
            childJsonObj.put("accessToken", _pref.getSession(ConstantClass.ACCESS_TOKEN));
            childJsonObj.put("currentPage", currentPage);
            childJsonObj.put("latitude", _pref.getSession(ConstantClass.TAG_LATITUDE));
            childJsonObj.put("longitude", _pref.getSession(ConstantClass.TAG_LONGITUDE));
            parentJsonObj.put("data", childJsonObj);
            Log.e("input data", parentJsonObj.toString());
            _serviceAction.requestVersionApi(parentJsonObj, "get-my-stuff-college-list");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onStarted() {

    }

    @Override
    public void onCompleted(JSONObject response) {
        Log.e("alphabet response", response.toString());
        try {
            JSONObject errorNodeJsonObj = response.getJSONObject("errNode");
            if (errorNodeJsonObj.getInt("errCode") == 0) {
                JSONObject dataJsonObject = response.getJSONObject("data");
                JSONArray collageListJsonArray = dataJsonObject.getJSONArray("listItem");
                if (collageListJsonArray.length() > 0) {
                    for (int i = 0; i < collageListJsonArray.length(); i++) {
                        JSONObject _collageListItem = collageListJsonArray.getJSONObject(i);
                        CollageListItem _item = new CollageListItem();
                        _item.setId(_collageListItem.getInt("id"));
                        _item.setTitle(_collageListItem.getString("collageName"));
                        _item.setAddress(_collageListItem.getString("collageAddress"));
                        _item.setDistance(_collageListItem.getString("collageDistance"));
                        _item.setLikeStatus(_collageListItem.getBoolean("collageLikeStatus"));
                        _item.setLatitude(_collageListItem.getDouble("collageLatitude"));
                        _item.setLongitude(_collageListItem.getDouble("collageLongitude"));
                        CollageItemsListItems.add(_item);
                    }
                    adapter.notifyDataSetChanged();
                    rlMyStuffCollege.setVisibility(View.VISIBLE);
                    llProgressbarMyStuffCollageList.setVisibility(View.GONE);
                } else {
                    llErrorThreeMyStuffCollageList.setVisibility(View.VISIBLE);
                    llProgressbarMyStuffCollageList.setVisibility(View.GONE);
                    tvErrorTwoMessage.setText("No college available in your location.");
                    ivReloadThirdErrorScreen.setVisibility(View.GONE);
                }
            } else {
                Toast.makeText(getActivity(), errorNodeJsonObj.getString("errMsg"), Toast.LENGTH_LONG).show();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
