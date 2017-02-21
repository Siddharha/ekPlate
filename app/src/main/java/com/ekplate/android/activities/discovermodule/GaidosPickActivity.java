package com.ekplate.android.activities.discovermodule;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.ekplate.android.R;
import com.ekplate.android.activities.socialsharemodule.SocialShareActivity;
import com.ekplate.android.activities.vendormodule.VendorDetailsActivity;
import com.ekplate.android.appinterfaces.BackgroundActionInterface;
import com.ekplate.android.config.BaseActivity;
import com.ekplate.android.utils.CallServiceAction;
import com.ekplate.android.utils.ConstantClass;
import com.ekplate.android.utils.NetworkConnectionCheck;
import com.ekplate.android.utils.Pref;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import org.json.JSONArray;
import org.json.JSONObject;

public class GaidosPickActivity extends BaseActivity implements BackgroundActionInterface{
//
    private Toolbar tbGaidosSpik;
    private TextView toolbarHeaderText;
    private RelativeLayout rlMainContainerGaido;
    private LinearLayout llProgressbarContainerGaido;
    private ImageView ivReloadGaido, ivGaidoFoodImage, ivGaidoFoodType;
    private TextView tvGaidoFoodName, tvGaidoVendorName, tvGaidoVendorAddress, tvGaidoVendorDistance, tvGaidoVendorLike,
            tvGaidoVendorRating, tvGaidoVendorReview;

    private Pref _pref;
    private NetworkConnectionCheck _connection;
    private CallServiceAction _serviceAction;

    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private int vendorsId;
    private String foodDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gaidos_pick);
        getWindow().setBackgroundDrawable(null);
        getSupportActionBar().hide();
        initialize();
        setUpToolBar();
        onClick();
        getGaidosPick();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void initialize(){

        tbGaidosSpik = (Toolbar) findViewById(R.id.tbGaidosSpik);
        toolbarHeaderText = (TextView) findViewById(R.id.toolbarHeaderText);

        rlMainContainerGaido = (RelativeLayout) findViewById(R.id.rlMainContainerGaido);
        llProgressbarContainerGaido = (LinearLayout) findViewById(R.id.llProgressbarContainerGaido);

        ivReloadGaido = (ImageView) findViewById(R.id.ivReloadGaido);
        ivGaidoFoodImage = (ImageView) findViewById(R.id.ivGaidoFoodImage);
        ivGaidoFoodType = (ImageView) findViewById(R.id.ivGaidoFoodType);

        tvGaidoFoodName = (TextView) findViewById(R.id.tvGaidoFoodName);
        tvGaidoVendorName = (TextView) findViewById(R.id.tvGaidoVendorName);
        tvGaidoVendorAddress = (TextView) findViewById(R.id.tvGaidoVendorAddress);
        tvGaidoVendorDistance = (TextView) findViewById(R.id.tvGaidoVendorDistance);
        tvGaidoVendorLike = (TextView) findViewById(R.id.tvGaidoVendorLike);
        tvGaidoVendorRating = (TextView) findViewById(R.id.tvGaidoVendorRating);
        tvGaidoVendorReview = (TextView) findViewById(R.id.tvGaidoVendorReview);

        _pref = new Pref(GaidosPickActivity.this);
        _connection = new NetworkConnectionCheck(GaidosPickActivity.this);
        _serviceAction = new CallServiceAction(GaidosPickActivity.this);
        _serviceAction.actionInterface = this;

        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .showImageOnLoading(R.drawable.default_image_vendor_inside)
                .showImageOnFail(R.drawable.default_image_vendor_inside)
                .showImageForEmptyUri(R.drawable.default_image_vendor_inside)
                .build();
    }

    private void setUpToolBar(){

        toolbarHeaderText.setText("GAIDO'S FOOD PICKS");
        tbGaidosSpik.inflateMenu(R.menu.menu_gaidos_pick);
        tbGaidosSpik.setNavigationIcon(R.drawable.ic_action_back);
        tbGaidosSpik.setBackgroundColor(Color.parseColor("#66000000"));

        tbGaidosSpik.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        tbGaidosSpik.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
               /* int itemId = item.getItemId();
                startActivity(new Intent(GaidosPickActivity.this, SocialShareActivity.class));
                return false;*/

                int optionId = item.getItemId();
                Intent intent = new Intent(GaidosPickActivity.this, SocialShareActivity.class);
                /*intent.putExtra("vendor_name", vendorName);
                intent.putExtra("vendor_address", vendorAddress);
                intent.putExtra("url", headerImageUrl);*/
                intent.putExtra("route_from", "discovar");
                startActivity(intent);
                return false;
            }
        });
    }

    private void onClick(){

        ivReloadGaido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getGaidosPick();
            }
        });
    }

    private void getGaidosPick(){

        if(_connection.isNetworkAvailable()) {
            llProgressbarContainerGaido.setVisibility(View.VISIBLE);
            rlMainContainerGaido.setVisibility(View.GONE);
            try {
                JSONObject parentJsonObj = new JSONObject();
                JSONObject childJsonObj = new JSONObject();
                childJsonObj.put("accessToken", _pref.getSession(ConstantClass.ACCESS_TOKEN));
                childJsonObj.put("latitude", _pref.getSession(ConstantClass.TAG_LATITUDE));
                childJsonObj.put("longitude", _pref.getSession(ConstantClass.TAG_LONGITUDE));
                childJsonObj.put("city", _pref.getSession(ConstantClass.TAG_SELECTED_CITY_LOCATION));
                childJsonObj.put("food_type", _pref.getSession(ConstantClass.TAG_SELECTED_FOOD_TYPE));
                childJsonObj.put("tagIds", "");
                parentJsonObj.put("data", childJsonObj);
                Log.e("input data", parentJsonObj.toString());
                _serviceAction = new CallServiceAction(GaidosPickActivity.this);
                _serviceAction.actionInterface = this;
                _serviceAction.requestVersionV2Api(parentJsonObj, "get-gaidos-pick");
            } catch (Exception e){
                e.printStackTrace();
            }
        } else {
            _connection.getNetworkActiveAlert().show();
        }
    }

    @Override
    public void onStarted() {

    }

    @Override
    public void onCompleted(JSONObject response) {
        Log.v("response", response.toString());


        try {
            JSONObject jsonObjError = response.getJSONObject("errNode");
            if (jsonObjError.getInt("errCode") == 0) {
                llProgressbarContainerGaido.setVisibility(View.GONE);
                rlMainContainerGaido.setVisibility(View.VISIBLE);
                JSONObject jsonObjData = response.getJSONObject("data");
                JSONArray jsonArrayFoodDetails = jsonObjData.getJSONArray("details");
                JSONObject jsonObjFood = jsonArrayFoodDetails.getJSONObject(0);
                JSONObject jsonObjVendorDetails = jsonObjFood.getJSONObject("nearestVendor");
                tvGaidoFoodName.setText(jsonObjFood.getString("foodName"));
                tvGaidoVendorName.setText(jsonObjVendorDetails.getString("vendorName"));
                tvGaidoVendorAddress.setText(jsonObjVendorDetails.getString("inlineAddress"));
                tvGaidoVendorDistance.setText(jsonObjVendorDetails.getString("distance") + " km");
                tvGaidoVendorLike.setText(jsonObjVendorDetails.getString("noOfLikes") + " Likes");
                tvGaidoVendorRating.setText(jsonObjVendorDetails.getString("rating"));
                tvGaidoVendorReview.setText(jsonObjVendorDetails.getString("noOfReviews") + " Reviews");

                vendorsId = jsonObjVendorDetails.optInt("id");
                foodDetails = jsonObjFood.toString();
                imageLoader.displayImage(jsonObjFood.getString("imageUrl"), ivGaidoFoodImage, options);
                if(jsonObjFood.getString("foodType").contains("Non-Veg")){
                    ivGaidoFoodType.setImageResource(R.drawable.icon_nonveg);
                }
                else{
                    ivGaidoFoodType.setImageResource(R.drawable.icon_veg);
                }
            }
            else {
                //Toast.makeText(GaidosPickActivity.this, jsonObjError.getString("errMsg"), Toast.LENGTH_LONG).show();
                getGaidosPick();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    public void clkContaintGDsp(View view)
    {
             Intent intent = new Intent(getBaseContext(), VendorDetailsActivity.class);
        intent.putExtra("routeFrom", "gaidosPick");
        intent.putExtra("vendorId", vendorsId);
        // intent.putExtra("vendorBookMark", jsonObjectItem.getBoolean("bookmarkedStatus"));
        startActivity(intent);
    }
}
