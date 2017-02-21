package com.ekplate.android.fragments.menumodule;

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
import android.widget.Toast;

import com.ekplate.android.R;
import com.ekplate.android.adapters.menumodule.MyStuffImageAdapter;
import com.ekplate.android.appinterfaces.BackgroundActionInterface;
import com.ekplate.android.models.menumodule.MyStuffImageItem;
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
public class MyStuffImageFragment extends Fragment implements BackgroundActionInterface {

    private RecyclerView rlMyStuffImages;
    private RecyclerView.LayoutManager rlMyStuffImagesLayoutManager;
    private ArrayList<MyStuffImageItem> imageItem;
    private MyStuffImageAdapter itemAdapter;
    private Pref _pref;
    private NetworkConnectionCheck _connection;
    private CallServiceAction _serviceAction;
    private LinearLayout llProgressbarMyStuffImageList, llErrorThreeMyStuffImageList;
    private TextView tvErrorTwoMessage;
    private ImageView ivReloadThirdErrorScreen, ivErrorMainImage;

    public static MyStuffImageFragment getInstance(){
        MyStuffImageFragment fragment = new MyStuffImageFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_stuff_images, container, false);
        initialize(rootView);
        setUpImageList();
        return rootView;
    }

    private void initialize(View rootView){
        rlMyStuffImages = (RecyclerView) rootView.findViewById(R.id.rlMyStuffImages);
        llProgressbarMyStuffImageList = (LinearLayout) rootView.findViewById(R.id.llProgressbarMyStuffImageList);
        llErrorThreeMyStuffImageList = (LinearLayout) rootView.findViewById(R.id.llErrorThreeMyStuffImageList);
        tvErrorTwoMessage = (TextView) rootView.findViewById(R.id.tvErrorTwoMessage);
        ivReloadThirdErrorScreen = (ImageView) rootView.findViewById(R.id.ivReloadThirdErrorScreen);
        ivErrorMainImage = (ImageView) rootView.findViewById(R.id.ivErrorMainImage);
        rlMyStuffImagesLayoutManager = new GridLayoutManager(getActivity(), 2);
        rlMyStuffImages.setLayoutManager(rlMyStuffImagesLayoutManager);
        imageItem = new ArrayList<>();
        itemAdapter = new MyStuffImageAdapter(getActivity(), imageItem);
        _pref = new Pref(getActivity());
        _connection = new NetworkConnectionCheck(getActivity());
        _serviceAction = new CallServiceAction(getActivity());
        _serviceAction.actionInterface = MyStuffImageFragment.this;
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

    private void setUpImageList(){
        rlMyStuffImages.setHasFixedSize(true);
        rlMyStuffImages.addItemDecoration(new SpacesItemDecoration(getActivity()
                .getResources().getDimensionPixelSize(R.dimen.grid_space_size)));
        rlMyStuffImages.setAdapter(itemAdapter);
        if (_connection.isNetworkAvailable()) {
            setInputParamForAlphabetCollageList(1);
        } else {
            tvErrorTwoMessage.setText(R.string.message_on_internet_not_connection);
            llProgressbarMyStuffImageList.setVisibility(View.GONE);
            llErrorThreeMyStuffImageList.setVisibility(View.VISIBLE);
        }
    }

    private void setInputParamForAlphabetCollageList(int currentPage){
        try {
            JSONObject parentJsonObj = new JSONObject();
            JSONObject childJsonObj = new JSONObject();
            childJsonObj.put("accessToken", _pref.getSession(ConstantClass.ACCESS_TOKEN));
            childJsonObj.put("currentPage", currentPage);
            parentJsonObj.put("data", childJsonObj);
            Log.e("input data", parentJsonObj.toString());
            _serviceAction.requestVersionApi(parentJsonObj, "get-my-stuff-image-list");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

   /* private void loadImageList(){
        for (int i=1; i<10; i++){
            FoodItem _item = new FoodItem();
            _item.setId(i);
            _item.setFoodTitle("Image Title");
            _item.setFoodItemImageUrl("");
            _item.setFoodType("veg");
            _item.setFoodDescription("");

            imageItem.add(_item);
        }
        itemAdapter.notifyDataSetChanged();
    }*/

    @Override
    public void onStarted() {

    }

    @Override
    public void onCompleted(JSONObject response) {
        Log.e("response", response.toString());
        try {
            JSONObject errorNodeJsonObj = response.getJSONObject("errNode");
            if (errorNodeJsonObj.getInt("errCode") == 0) {
                JSONObject dataJsonObject = response.getJSONObject("data");
                JSONArray collageListJsonArray = dataJsonObject.getJSONArray("foodlist");
                if (collageListJsonArray.length() > 0) {
                    for (int i = 0; i < collageListJsonArray.length(); i++) {
                        JSONObject _imageItem = collageListJsonArray.getJSONObject(i);
                        MyStuffImageItem _item = new MyStuffImageItem();
                        _item.setId(_imageItem.getInt("foodid"));
                        _item.setTitle(_imageItem.getString("foodName"));
                        _item.setImageUrl(_imageItem.getString("foodImgUrl"));
                        imageItem.add(_item);
                    }
                    itemAdapter.notifyDataSetChanged();
                    rlMyStuffImages.setVisibility(View.VISIBLE);
                    llProgressbarMyStuffImageList.setVisibility(View.GONE);
                } else {
                    llErrorThreeMyStuffImageList.setVisibility(View.VISIBLE);
                    llProgressbarMyStuffImageList.setVisibility(View.GONE);
                    tvErrorTwoMessage.setText("No image available .");
                    ivReloadThirdErrorScreen.setVisibility(View.GONE);
                }
            } else {
                Toast.makeText(getActivity(), errorNodeJsonObj.getString("errMsg"), Toast.LENGTH_LONG).show();

            }
        } catch (Exception e) {
            e.printStackTrace();
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
