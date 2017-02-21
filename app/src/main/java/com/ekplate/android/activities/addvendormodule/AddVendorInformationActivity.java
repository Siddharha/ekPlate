package com.ekplate.android.activities.addvendormodule;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Avishek on 9/29/2015.
 */
public class AddVendorInformationActivity extends BaseActivity
        implements BackgroundActionInterface {

    private Toolbar toolbarAddVendors;
    private TextView toolbarHeaderText;
    private CardView cardViewAddLocation;
    private EditText etVendorName, etVendorShop, etVendorMostSellingFood, etVendorContactNo;
    private DbAdapter dbAdapter;
    private ArrayList<MenuItem> menuItems;
    private Pref _pref;
    private NetworkConnectionCheck _connection;
    private CallServiceAction _serviceAction;
    private ProgressDialog progressDialog;
    private LinearLayout llSubmitBasicInfo;
    private int submitStatus = 0;
    private Boolean phoneNumberChecked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vendor_information);
        getWindow().setBackgroundDrawable(null);
        getSupportActionBar().hide();
        initialize();
        setUpToolbar();
        onClick();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void initialize(){
        toolbarAddVendors = (Toolbar) findViewById(R.id.toolbarAddVendors);
        toolbarHeaderText = (TextView) findViewById(R.id.toolbarHeaderText);
        cardViewAddLocation = (CardView) findViewById(R.id.cardViewAddLocation);
        etVendorName = (EditText) findViewById(R.id.etVendorName);
        etVendorShop = (EditText) findViewById(R.id.etVendorShop);
        etVendorMostSellingFood = (EditText) findViewById(R.id.etVendorMostSellingFood);
        etVendorContactNo = (EditText) findViewById(R.id.etVendorContactNo);
        llSubmitBasicInfo = (LinearLayout) findViewById(R.id.llSubmitBasicInfo);
        dbAdapter = new DbAdapter(this);
        menuItems = new ArrayList<>();
        _pref = new Pref(this);
        phoneNumberChecked = false;
        _connection = new NetworkConnectionCheck(this);
        _serviceAction = new CallServiceAction(this);
        _serviceAction.actionInterface = AddVendorInformationActivity.this;
    }

    private void setUpToolbar(){
        toolbarAddVendors.setNavigationIcon(R.drawable.ic_action_back);
        toolbarHeaderText.setText("VENDOR INFORMATION");
        toolbarAddVendors.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void onClick(){
        cardViewAddLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitStatus = 1;
                checkingInfoBeforeLocalSave();
            }
        });

        llSubmitBasicInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (_connection.isNetworkAvailable()) {
                    if (checkingInfoBeforeLocalSave()) {
                        setUpProgressDialog();
                        setUpAddVendorParameter();
                    }
                } else {
                    _connection.getNetworkActiveAlert().show();
                }
            }
        });

        etVendorContactNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (etVendorContactNo.length() == 10) {
                    JSONObject parentJsonObj = new JSONObject();
                    JSONObject childJsonObj = new JSONObject();
                    try {
                        childJsonObj.put("mobile", s);
                        parentJsonObj.put("data", childJsonObj);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    _serviceAction.requestVersionApi(parentJsonObj, "check-vendor-mobile");
                }
            }
        });
    }

    private void storeVendorBasicInfo(String vendorName, String shopName, String mostSellingFood, String contactNo) {
        dbAdapter.open();
        dbAdapter.insertVendorBasicInfo(vendorName, shopName, mostSellingFood, contactNo);
        dbAdapter.close();
        _pref.setSession(ConstantClass.TAG_ADD_VENDOR_SUBMIT_FLAG, 1);
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


                    if(dataJsonObject.has("exist")) {
                        if (dataJsonObject.getString("exist").equals("yes")) {
                            Toast.makeText(AddVendorInformationActivity.this, "Mobile No. Already exists", Toast.LENGTH_LONG).show();
                            phoneNumberChecked = false;
                        } else if (dataJsonObject.getString("exist").equals("no")) {
                           // Toast.makeText(AddVendorInformationActivity.this, "Number Validation checked. now you good to go", Toast.LENGTH_LONG).show();
                            phoneNumberChecked = true;
                        }
                    }
                        else {
                            //Toast.makeText(AddVendorInformationActivity.this, "Thank You for adding. We will review and notify you.", Toast.LENGTH_LONG).show();
                            _pref.setSession(ConstantClass.TAG_ADD_VENDOR_SUBMIT_FLAG, 0);
                            _pref.setSession(ConstantClass.TAG_ADD_VENDOR_BASIC_INFO_FLAG, 0);
                            Intent intent = new Intent(AddVendorInformationActivity.this, QuestionSubmitResultActivity.class);
                            intent.putExtra("screenTypeFlag", "add_vendor");
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                } else {
                    Toast.makeText(AddVendorInformationActivity.this, dataJsonObject.getString("sucessMsg"), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(AddVendorInformationActivity.this, errorNodeJsonObj.getString("errMsg"), Toast.LENGTH_LONG).show();
            }
            progressDialog.dismiss();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private boolean checkingInfoBeforeLocalSave(){
        boolean status = true;;
        if (etVendorName.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(AddVendorInformationActivity.this, "Please insert the vendor name", Toast.LENGTH_LONG).show();
            status = false;
        } else if (etVendorShop.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(AddVendorInformationActivity.this, "Please insert the vendor shop name", Toast.LENGTH_LONG).show();
            status = false;
        } else if (etVendorMostSellingFood.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(AddVendorInformationActivity.this, "Please insert the most selling food", Toast.LENGTH_LONG).show();
            status = false;
        } else if (etVendorContactNo.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(AddVendorInformationActivity.this, "Please insert the vendor contact number", Toast.LENGTH_LONG).show();
            status = false;
        } else if (etVendorContactNo.getText().toString().length() < 10) {
            Toast.makeText(AddVendorInformationActivity.this, "Please insert correct contact number", Toast.LENGTH_LONG).show();
            status = false;
        }else if (!phoneNumberChecked) {
            Toast.makeText(AddVendorInformationActivity.this, "This number already exists!,Please insert new contact number", Toast.LENGTH_LONG).show();
            status = false;
        }

        else {
            if (_pref.getIntegerSession(ConstantClass.TAG_ADD_VENDOR_BASIC_INFO_FLAG) == 0) {
                storeVendorBasicInfo(etVendorName.getText().toString(), etVendorShop.getText().toString(),
                        etVendorMostSellingFood.getText().toString(), etVendorContactNo.getText().toString());
                _pref.setSession(ConstantClass.TAG_ADD_VENDOR_BASIC_INFO_FLAG, 1);
            }
            if (submitStatus == 1) {
                startActivity(new Intent(AddVendorInformationActivity.this, AddVendorLocationActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                submitStatus = 0;
            }
        }
        return status;
    }
}
