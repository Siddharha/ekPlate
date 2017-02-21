package com.ekplate.android.activities.addvendormodule;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ekplate.android.R;
import com.ekplate.android.activities.menumodule.QuestionSubmitResultActivity;
import com.ekplate.android.appinterfaces.BackgroundActionInterface;
import com.ekplate.android.config.BaseActivity;
import com.ekplate.android.localdbconfig.DbAdapter;
import com.ekplate.android.models.addvendormodule.MenuItem;
import com.ekplate.android.models.addvendormodule.VendorBasicInfoItem;
import com.ekplate.android.utils.CallServiceAction;
import com.ekplate.android.utils.ConstantClass;
import com.ekplate.android.utils.NetworkConnectionCheck;
import com.ekplate.android.utils.Pref;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Avishek on 9/30/2015.
 */
public class AddVendorRatingActivity extends BaseActivity
        implements BackgroundActionInterface {

    private Toolbar toolbarAddVendors;
    private TextView toolbarHeaderText;
    private RatingBar ratingBarHygieneOne, ratingBarTasteOne;
    private CardView cardViewBack, cardViewAddMenu;
    private DbAdapter dbAdapter;
    private float hygieneRating = 0.0f, tasteRating = 0.0f;
    private LinearLayout llSubmitRating;
    private ArrayList<MenuItem> menuItems;
    private Pref _pref;
    private NetworkConnectionCheck _connection;
    private CallServiceAction _serviceAction;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vendor_rating);
        getWindow().setBackgroundDrawable(null);
        getSupportActionBar().hide();

        initialize();
        setUpToolbar();
        setUpCustomHygieneRatingBar();
        setUpCustomTasteRatingBar();
        onClick();
        if (_pref.getIntegerSession(ConstantClass.TAG_ADD_VENDOR_RATING_FLAG) == 1) {
            setUpRatingParam();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void initialize(){
        toolbarAddVendors = (Toolbar) findViewById(R.id.toolbarAddVendors);
        toolbarHeaderText = (TextView) findViewById(R.id.toolbarHeaderText);
        cardViewBack = (CardView) findViewById(R.id.cardViewBack);
        cardViewAddMenu = (CardView) findViewById(R.id.cardViewAddMenu);
        llSubmitRating = (LinearLayout) findViewById(R.id.llSubmitRating);
        dbAdapter = new DbAdapter(this);
        menuItems = new ArrayList<>();
        _pref = new Pref(this);
        _connection = new NetworkConnectionCheck(this);
        _serviceAction = new CallServiceAction(this);
        _serviceAction.actionInterface = AddVendorRatingActivity.this;
    }

    private void setUpToolbar(){
        toolbarAddVendors.setNavigationIcon(R.drawable.ic_action_back);
        toolbarHeaderText.setText("RATING");
        toolbarAddVendors.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setUpRatingParam() {
        dbAdapter.open();
        ArrayList<VendorBasicInfoItem> vendorBasicInfoItems = dbAdapter.getVendorBasicInfo();
        dbAdapter.close();
        if (vendorBasicInfoItems.get(0).getHygieneRating().equals("")) {
            ratingBarHygieneOne.setRating(0.0f);
        } else {
            ratingBarHygieneOne.setRating(Float.parseFloat(vendorBasicInfoItems.get(0).getHygieneRating()));
        }

        if (vendorBasicInfoItems.get(0).getTasteRating().equals("")) {
            ratingBarTasteOne.setRating(0.0f);
        } else {
            ratingBarTasteOne.setRating(Float.parseFloat(vendorBasicInfoItems.get(0).getTasteRating()));
        }
    }

    private void onClick(){
        cardViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        cardViewAddMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveVendorRating(String.valueOf(hygieneRating), String.valueOf(tasteRating));
                _pref.setSession(ConstantClass.TAG_ADD_VENDOR_RATING_FLAG, 1);
                startActivity(new Intent(AddVendorRatingActivity.this, AddVendorMenuActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        llSubmitRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (_connection.isNetworkAvailable()){
                    checkingInfoBeforeLocalSave();
                    setUpProgressDialog();
                    setUpAddVendorParameter();
                } else {
                    _connection.getNetworkActiveAlert().show();
                }
            }
        });
    }

    private void saveVendorRating(String hygieneRating, String tasteRating){
        dbAdapter.open();
        int success =  dbAdapter.updateVendorRating(hygieneRating, tasteRating);
        dbAdapter.close();
        Log.e("rating success", String.valueOf(success));
    }

    private void setUpCustomHygieneRatingBar(){
        ratingBarHygieneOne = (RatingBar) findViewById(R.id.ratingBarHygieneOne);
        ratingBarHygieneOne.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                hygieneRating = rating;
            }
        });
    }

    private void setUpCustomTasteRatingBar(){
        ratingBarTasteOne = (RatingBar) findViewById(R.id.ratingBarTasteOne);
        ratingBarTasteOne.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                tasteRating = rating;
            }
        });
    }

    private void setUpProgressDialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait..");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void setUpAddVendorParameter(){
        dbAdapter.open();
        ArrayList<VendorBasicInfoItem> vendorBasicInfoItems = dbAdapter.getVendorBasicInfo();
        menuItems = dbAdapter.getMenuList(menuItems);
        dbAdapter.close();
        try {
            JSONObject parentJsonObj = new JSONObject();
            JSONObject childJsonObj = new JSONObject();
            JSONArray childFoodItemJsonArray = new JSONArray();
            childJsonObj.put("accessToken", _pref.getSession(ConstantClass.ACCESS_TOKEN));
            childJsonObj.put("vendorName", vendorBasicInfoItems.get(0).getVendorName());
            childJsonObj.put("vendorShop", vendorBasicInfoItems.get(0).getShopName());
            childJsonObj.put("vendorContactNo", vendorBasicInfoItems.get(0).getContactNo());
            childJsonObj.put("vendorMostSellingFood", vendorBasicInfoItems.get(0).getMostSellingFood());
            childJsonObj.put("address", vendorBasicInfoItems.get(0).getVendorAddress());
            childJsonObj.put("latitude", vendorBasicInfoItems.get(0).getLatitude());
            childJsonObj.put("longitude",vendorBasicInfoItems.get(0).getLongitude());
            childJsonObj.put("hygieneRating", vendorBasicInfoItems.get(0).getHygieneRating());
            childJsonObj.put("tasteRating", vendorBasicInfoItems.get(0).getTasteRating());
            for (int i=0; i<menuItems.size(); i++){
                JSONObject childFoodItemJsonObj = new JSONObject();
                childFoodItemJsonObj.put("foodItem", menuItems.get(i).getFoodName());
                childFoodItemJsonObj.put("foodPrice", menuItems.get(i).getFoodValue());
                childFoodItemJsonArray.put(childFoodItemJsonObj);
            }
            childJsonObj.put("foodMenuList", childFoodItemJsonArray);
            parentJsonObj.put("data", childJsonObj);
            Log.e("input data", parentJsonObj.toString());
            _serviceAction.requestVersionApi(parentJsonObj, "insert-new-vendor");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onStarted() {

    }

    @Override
    public void onCompleted(JSONObject response) {
        Log.e("suggestion response", response.toString());
        try {
            JSONObject errorNodeJsonObj = response.getJSONObject("errNode");
            if (errorNodeJsonObj.getInt("errCode") == 0) {
                JSONObject dataJsonObject = response.getJSONObject("data");
                if (dataJsonObject.getBoolean("success")){
                    //Toast.makeText(AddVendorRatingActivity.this, "Thank You for adding. We will review and notify you.", Toast.LENGTH_LONG).show();
                    _pref.setSession(ConstantClass.TAG_ADD_VENDOR_SUBMIT_FLAG, 0);
                    _pref.setSession(ConstantClass.TAG_ADD_VENDOR_BASIC_INFO_FLAG, 0);
                    _pref.setSession(ConstantClass.TAG_ADD_VENDOR_RATING_FLAG, 0);
                    Intent intent = new Intent(AddVendorRatingActivity.this, QuestionSubmitResultActivity.class);
                    intent.putExtra("screenTypeFlag", "add_vendor");
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else {
                    Toast.makeText(AddVendorRatingActivity.this, dataJsonObject.getString("sucessMsg"), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(AddVendorRatingActivity.this, errorNodeJsonObj.getString("errMsg"), Toast.LENGTH_LONG).show();
            }
            progressDialog.dismiss();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private boolean checkingInfoBeforeLocalSave(){
        boolean status = true;
        saveVendorRating(String.valueOf(hygieneRating), String.valueOf(tasteRating));
        return status;
    }
}

