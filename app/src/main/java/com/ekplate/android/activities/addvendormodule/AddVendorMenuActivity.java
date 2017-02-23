package com.ekplate.android.activities.addvendormodule;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ekplate.android.R;
import com.ekplate.android.activities.menumodule.QuestionSubmitResultActivity;
import com.ekplate.android.adapters.addvendormodule.FoodMenuItemAdapter;
import com.ekplate.android.appinterfaces.BackgroundActionInterface;
import com.ekplate.android.config.BaseActivity;
import com.ekplate.android.localdbconfig.DbAdapter;
import com.ekplate.android.models.addvendormodule.ImageItem;
import com.ekplate.android.models.addvendormodule.MenuItem;
import com.ekplate.android.models.addvendormodule.VendorBasicInfoItem;
import com.ekplate.android.models.addvendormodule.VideoItem;
import com.ekplate.android.utils.AsyncTaskListener;
import com.ekplate.android.utils.CallServiceAction;
import com.ekplate.android.utils.CommonFunction;
import com.ekplate.android.utils.ConstantClass;
import com.ekplate.android.utils.NetworkConnectionCheck;
import com.ekplate.android.utils.PostObject;
import com.ekplate.android.utils.Pref;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Avishek on 9/30/2015.
 */
public class AddVendorMenuActivity extends BaseActivity
        implements BackgroundActionInterface {
    private Toolbar toolbarAddVendors;
    private TextView toolbarHeaderText;
    private EditText etFoodName, etFoodValue;
    private CardView cardViewAddMenu, cardViewBack;
    private String foodName="", foodValue ="";
    private DbAdapter dbAdapter;
    private ArrayList<MenuItem> menuList;
    private ListView lvMenu;
    private FoodMenuItemAdapter foodMenuItemAdapter;
    private LinearLayout llMenu;
    private ArrayList<MenuItem> menuItems;
    private ArrayList<ImageItem> imageItems;
    private  ArrayList<VideoItem> videoItems;
    private CallServiceAction _serviceAction;
    private ProgressDialog progressDialog;
    private LinearLayout llSubmitFromVendorMenu;
    private NetworkConnectionCheck _connection;
    public static ArrayList<CustomGallery> dataT = new ArrayList<CustomGallery>();
    private CommonFunction _comFunc;
    private Pref _pref;
    private String vendorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vendor_menu);
        getWindow().setBackgroundDrawable(null);
        getSupportActionBar().hide();
        initialize();
        setUpToolbar();
        onClick();
        if (_pref.getIntegerSession(ConstantClass.TAG_ADD_VENDOR_MENU_FLAG) == 1) {
            setUpFoodMenu();
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
        etFoodName = (EditText) findViewById(R.id.etFoodName);
        etFoodValue = (EditText) findViewById(R.id.etFoodValue);
        cardViewAddMenu = (CardView) findViewById(R.id.cardViewAddMenu);
        cardViewBack = (CardView) findViewById(R.id.cardViewBack);
        llMenu = (LinearLayout) findViewById(R.id.llMenu);
        llSubmitFromVendorMenu = (LinearLayout) findViewById(R.id.llSubmitFromVendorMenu);
        lvMenu = (ListView) findViewById(R.id.lvMenu);
        menuList = new ArrayList<>();
        foodMenuItemAdapter = new FoodMenuItemAdapter(this, menuList);
        dbAdapter = new DbAdapter(AddVendorMenuActivity.this);
        _pref = new Pref(this);
        _connection = new NetworkConnectionCheck(this);
        _serviceAction = new CallServiceAction(this);
        _serviceAction.actionInterface = AddVendorMenuActivity.this;
        _comFunc = new CommonFunction(this);
        lvMenu.setAdapter(foodMenuItemAdapter);
        menuItems = new ArrayList<>();
        imageItems = new ArrayList<>();
        videoItems = new ArrayList<>();

      // dataT = AddVendorImagesActivity.dataT;
    }

    private void setUpToolbar(){
        toolbarAddVendors.setNavigationIcon(R.drawable.ic_action_back);
        toolbarHeaderText.setText("MENU");
        toolbarAddVendors.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setUpFoodMenu() {
        dbAdapter.open();
        menuList = dbAdapter.getMenuList(menuList);
        dbAdapter.close();
        if(menuList.size() > 0) {
            llMenu.setVisibility(View.GONE);
        } else {
            llMenu.setVisibility(View.VISIBLE);
        }
        foodMenuItemAdapter.notifyDataSetChanged();
    }

    private void onClick(){
        cardViewAddMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foodName = etFoodName.getText().toString();
                foodValue = etFoodValue.getText().toString();
                if(foodName.equals("") || foodValue.equals("")){
                    Toast.makeText(AddVendorMenuActivity.this, "Please insert food item and price.", Toast.LENGTH_SHORT).show();
                }
                else{
                    dbAdapter.open();
                    dbAdapter.insertVendorFoodMenu(foodName, foodValue);
                    menuList = dbAdapter.getMenuList(menuList);
                    Log.v("size", String.valueOf(menuList.size()));
                    dbAdapter.close();
                    _pref.setSession(ConstantClass.TAG_ADD_VENDOR_MENU_FLAG, 1);
                    if(menuList.size() > 0){
                        llMenu.setVisibility(View.GONE);
                        foodMenuItemAdapter.notifyDataSetChanged();
                        etFoodName.setText("");
                        etFoodValue.setText("");
                    }
                }
            }
        });

        cardViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        etFoodName.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                etFoodName.setFocusable(true);
                etFoodName.setFocusableInTouchMode(true);
                return false;
            }
        });

        etFoodValue.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                etFoodValue.setFocusable(true);
                etFoodValue.setFocusableInTouchMode(true);
                return false;
            }
        });

        llSubmitFromVendorMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (_connection.isNetworkAvailable()) {
                    setUpProgressDialog();
                    setUpAddVendorParameter();
                } else {
                    _connection.getNetworkActiveAlert().show();
                }
            }
        });
    }

    private void setUpAddVendorParameter(){
        dbAdapter.open();
        ArrayList<VendorBasicInfoItem> vendorBasicInfoItems = dbAdapter.getVendorBasicInfo();
        menuItems = dbAdapter.getMenuList(menuItems);
        imageItems = dbAdapter.getImages(imageItems);
        videoItems = dbAdapter.getVideo(videoItems);
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

    private void setUpProgressDialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait..");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
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
                    _pref.setSession(ConstantClass.TAG_ADD_VENDOR_SUBMIT_FLAG, 0);
                    vendorId = dataJsonObject.getString("vendorId");
                    _pref.setSession(ConstantClass.TAG_INSERTED_VENDOR_ID,vendorId);
                    postSelectedImage();
                } else {
                    Toast.makeText(AddVendorMenuActivity.this, dataJsonObject.getString("sucessMsg"), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(AddVendorMenuActivity.this, errorNodeJsonObj.getString("errMsg"), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void postSelectedImage(){
        HashMap<String, PostObject> _postMap = new HashMap<String, PostObject>();

        _postMap.put("accessToken", _comFunc.getPostObject(
                String.valueOf(_pref.getSession(ConstantClass.ACCESS_TOKEN)), false));

        _postMap.put("vendorId", _comFunc.getPostObject(
                String.valueOf(vendorId), false));

        _postMap.put("no_image", _comFunc.getPostObject(
                String.valueOf(imageItems.size()),
                false));
        _postMap.put("imageType", _comFunc.getPostObject("1", false));

        _postMap.put("vendorCaption", _comFunc.getPostObject(String.valueOf(" "), false));

        for(int i=0; i<imageItems.size(); i++){
            Log.e("imageX", imageItems.get(i).getImagePath());
            _postMap.put("image_" + i, _comFunc.getPostObject(imageItems.get(i).getImagePath(), true));
        }

        _comFunc.callPostWebservice(ConstantClass.BASE_URL + "vendor-add-image", _postMap, _profileChangeAsync, true);
    }

    private void postSelectedVideo(){
        HashMap<String, PostObject> _postMap = new HashMap<String, PostObject>();

        _postMap.put("accessToken", _comFunc.getPostObject(
                String.valueOf(_pref.getSession(ConstantClass.ACCESS_TOKEN)), false));

        _postMap.put("vendorId", _comFunc.getPostObject(
                String.valueOf(vendorId), false));

        _postMap.put("no_video", _comFunc.getPostObject(
                String.valueOf(videoItems.size()), false));

        for (int i=0; i<videoItems.size(); i++){
            Log.e("video path", videoItems.get(i).getVideoPath());
            _postMap.put("id_" + i, _comFunc.getPostObject(String.valueOf(vendorId), false));
            _postMap.put("videoUrl_" + i, _comFunc.getPostObject(videoItems.get(i).getVideoPath(), true));
        }

        _comFunc.callPostWebservice(ConstantClass.BASE_URL + "upload-video", _postMap, _videoSubmitAsync, true);
    }

    AsyncTaskListener _profileChangeAsync = new AsyncTaskListener() {

        @Override
        public void onTaskPreExecute() {
            // TODO Auto-generated method stub

        }

        @Override
        public void onTaskCompleted(String result) {
            // TODO Auto-generated method stub
            Log.e("result", result);
            //Toast.makeText(AddVendorMenuActivity.this, "Thank You for adding. We will review and notify you.", Toast.LENGTH_LONG).show();
            AddVendorImagesActivity.dataT.clear();
            _pref.setSession(ConstantClass.TAG_ADD_VENDOR_SUBMIT_FLAG, 0);
            _pref.setSession(ConstantClass.TAG_ADD_VENDOR_LOCATION_FLAG, 0);
            _pref.setSession(ConstantClass.TAG_ADD_VENDOR_RATING_FLAG, 0);
            _pref.setSession(ConstantClass.TAG_ADD_VENDOR_BASIC_INFO_FLAG, 0);
            _pref.setSession(ConstantClass.TAG_ADD_VENDOR_IMAGE_VIDEO_FLAG, 0);
            _pref.setSession(ConstantClass.TAG_ADD_VENDOR_MENU_FLAG, 0);
            if (videoItems.size()==0) {
                Intent intent = new Intent(AddVendorMenuActivity.this, QuestionSubmitResultActivity.class);
                intent.putExtra("screenTypeFlag", "add_vendor");
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                progressDialog.dismiss();
            } else {
                postSelectedVideo();
            }
        }
    };

    AsyncTaskListener _videoSubmitAsync = new AsyncTaskListener() {
        @Override
        public void onTaskCompleted(String result) {
            Log.e("result", result);
            Intent intent = new Intent(AddVendorMenuActivity.this, QuestionSubmitResultActivity.class);
            intent.putExtra("screenTypeFlag", "add_vendor");
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            progressDialog.dismiss();
        }

        @Override
        public void onTaskPreExecute() {

        }
    };
}

