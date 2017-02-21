package com.ekplate.android.activities.menumodule;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.ekplate.android.R;
import com.ekplate.android.activities.addvendormodule.AddVendorInformationActivity;
import com.ekplate.android.activities.collegemodule.CollegeActivity;
import com.ekplate.android.activities.vendormodule.VendorsActivity;
import com.ekplate.android.appinterfaces.BackgroundActionInterface;
import com.ekplate.android.config.BaseActivity;
import com.ekplate.android.localdbconfig.DbAdapter;
import com.ekplate.android.models.vendormodule.AddCollegeItem;
import com.ekplate.android.utils.CallServiceAction;
import com.ekplate.android.utils.CommonMethods;
import com.ekplate.android.utils.ConstantClass;
import com.ekplate.android.utils.NetworkConnectionCheck;
import com.ekplate.android.utils.Pref;
import com.ekplate.android.views.BariolLightTextView;
import com.ekplate.android.views.CircularImageView;
import com.kbeanie.imagechooser.api.ChooserType;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.kbeanie.imagechooser.api.ChosenImages;
import com.kbeanie.imagechooser.api.ImageChooserListener;
import com.kbeanie.imagechooser.api.ImageChooserManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class AccountSettingActivity extends BaseActivity implements BackgroundActionInterface, ImageChooserListener {
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 1;
    private Toolbar tbAccountSetting;
    String originalFilePath;
    private EditText etUserNameAccountSetting, etUserEmailIdAccountSetting,
            etUserMobileNoAccountSetting;
    private CircularImageView civProfileImageAccountSetting;
    private ImageView ivEditProfileSelectImage;
    private LinearLayout llAddCollegeChangeProfile, llAddCollegeContainer;
    private RelativeLayout rlChangePasswordAccountSetting;
    private TextView tvAddCollegeTextChangeProfile;
    private Switch switchFoodTypeAccountSetting;
    private ImageLoader imageLoader;
    private String filePath;
    private DisplayImageOptions options;
    private int flagFocusEnable = 0;
    private ImageChooserManager imageChooserManager;
    private CommonMethods _CommonMethods;
    private ProgressDialog progressDialog;
    private Pref _pref;
    private NetworkConnectionCheck _connection;
    private CallServiceAction _serviceAction;
    private static final int REQUEST_CAMERA = 100;
    private static final int SELECT_FILE = 200;
    private DbAdapter dbAdapter;
    private int chooserType, orientation;
    private Uri imageUri;

    private TextView tvCurrentCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setting);
        getSupportActionBar().hide();
        getWindow().setBackgroundDrawable(null);
        initialize();
        setUnfousableAllFields();
        setUpToolbar();
        setUpUserInfo();
        setUpFont();
        onClick();
        /*if (getIntent().getExtras().getString("routeScreenForAccountSetting").equalsIgnoreCase("college")){
            setAddCollegeContainer();
        }*/

        if (ConstantClass.addCollegeItems.size()>0) {
            setAddCollegeContainer();
        }

    }

    private void checkCameraPermission() {
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            int hasWriteAccessPermission = checkSelfPermission(Manifest.permission.CAMERA);
            if (hasWriteAccessPermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] {Manifest.permission.CAMERA},
                        REQUEST_CODE_ASK_PERMISSIONS);
                return;
            }else{
                selectImage();
            }




        }
        else {
            selectImage();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {


        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    selectImage();
                } else {
                    // Permission Denied
                    Toast.makeText(this, "Can't Get Camera due to Permission issue! please Grand CAMERA Permission.", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //region Extra Code..
       // super.onActivityResult(requestCode, resultCode, data);
     /*   if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK && null != data) {
            *//*Bitmap photo = (Bitmap) data.getExtras().get("data");
            civProfileImageAccountSetting.setImageBitmap(photo);
            // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
            Uri tempUri = _CommonMethods.getImageUri(getApplicationContext(), photo);
            // CALL THIS METHOD TO GET THE ACTUAL PATH
            File finalFile = new File(_CommonMethods.getRealPathFromURI(tempUri));
            ConstantClass.TAG_SELECTED_PROFILE_IMAGE = _CommonMethods.getRealPathFromURI(tempUri);
            checkValidationForEditProfile();*//*


            try {
              *//*  Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                        getContentResolver(), imageUri);*//*
                //String bitmap_path = getRealPathFromURI(imageUri);

                 Bitmap bitmap = (Bitmap) data.getExtras().get("data");

            *//*String bitmap_path = MediaStore.Images.Media.insertImage(getContentResolver(),
                    bitmap, "Title", null);*//*
           // String bitmap_path = getRealPathFromURI(imageUri);;
            Uri contentUri = Uri.parse(bitmap_path);
            Cursor cursor = getContentResolver().query(contentUri, null,
                    null, null, null);
            if (cursor == null) {
                ConstantClass.TAG_SELECTED_PROFILE_IMAGE = contentUri.getPath();
            } else {
                cursor.moveToFirst();
                int index = cursor
                        .getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                ConstantClass.TAG_SELECTED_PROFILE_IMAGE = cursor.getString(index);
            }
            Log.e("image path", ConstantClass.TAG_SELECTED_PROFILE_IMAGE);
            } catch (Exception e) {
                e.printStackTrace();
            }
            checkValidationForEditProfile();
        } else if (requestCode == SELECT_FILE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(
                    selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            ConstantClass.TAG_SELECTED_PROFILE_IMAGE = cursor.getString(columnIndex);
            civProfileImageAccountSetting.setImageBitmap(BitmapFactory.decodeFile(ConstantClass.TAG_SELECTED_PROFILE_IMAGE));*/
        //endregion

        if (resultCode == RESULT_OK
                && (requestCode == ChooserType.REQUEST_PICK_PICTURE || requestCode == ChooserType.REQUEST_CAPTURE_PICTURE)) {
            if (imageChooserManager == null) {
                reinitializeImageChooser();

            }
            imageChooserManager.submit(requestCode, data);
        }
       // Bitmap bitmap = (Bitmap) data.getExtras().get("data");

      //  if (requestCode == ChooserType.REQUEST_PICK_PICTURE && resultCode == ChooserType.REQUEST_CAPTURE_PICTURE) {
           // civProfileImageAccountSetting.setImageBitmap(BitmapFactory.decodeFile(ConstantClass.TAG_SELECTED_PROFILE_IMAGE));
           // checkValidationForEditProfile();
      //  }
       // }
    }

    private void reinitializeImageChooser() {
        imageChooserManager = new ImageChooserManager(this, chooserType, true);
        Bundle bundle = new Bundle();
        bundle.putBoolean(Intent.EXTRA_ALLOW_MULTIPLE, true);
        imageChooserManager.setExtras(bundle);
        imageChooserManager.setImageChooserListener(this);
        imageChooserManager.reinitialize(filePath);
    }

    private void initialize(){
        etUserNameAccountSetting = (EditText) findViewById(R.id.etUserNameAccountSetting);
        etUserEmailIdAccountSetting = (EditText) findViewById(R.id.etUserEmailIdAccountSetting);
        etUserMobileNoAccountSetting = (EditText) findViewById(R.id.etUserMobileNoAccountSetting);
        civProfileImageAccountSetting = (CircularImageView) findViewById(R.id.civProfileImageAccountSetting);
        switchFoodTypeAccountSetting = (Switch) findViewById(R.id.switchFoodTypeAccountSetting);
        llAddCollegeChangeProfile = (LinearLayout) findViewById(R.id.llAddCollegeChangeProfile);
        llAddCollegeContainer = (LinearLayout) findViewById(R.id.llAddCollegeContainer);
        tvAddCollegeTextChangeProfile = (TextView) findViewById(R.id.tvAddCollegeTextChangeProfile);
        ivEditProfileSelectImage = (ImageView) findViewById(R.id.ivEditProfileSelectImage);
        rlChangePasswordAccountSetting = (RelativeLayout) findViewById(R.id.rlChangePasswordAccountSetting);
        dbAdapter = new DbAdapter(this);
        _pref = new Pref(this);
        _CommonMethods = new CommonMethods(this);
        _connection = new NetworkConnectionCheck(this);
        _serviceAction = new CallServiceAction(this);
        _serviceAction.actionInterface = AccountSettingActivity.this;
        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                    .cacheOnDisk(true)
                    .cacheInMemory(true)
                    .showImageOnLoading(R.drawable.profile_dp)
                    .showImageForEmptyUri(R.drawable.profile_dp)
                    .showImageOnFail(R.drawable.profile_dp)
                    .build();

        tvCurrentCity = (TextView)findViewById(R.id.tvCurrentCity);

        tvCurrentCity.setText(_pref.getSession(ConstantClass.TAG_SELECTED_CITY_LOCATION));
    }

    private void setUpToolbar(){
        tbAccountSetting = (Toolbar) findViewById(R.id.tbAccountSetting);
        tbAccountSetting.setNavigationIcon(R.drawable.ic_action_back);
        tbAccountSetting.inflateMenu(R.menu.menu_accout_setting);
        tbAccountSetting.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        tbAccountSetting.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.iconUserAccountEdit){
                    if (flagFocusEnable == 0) {
                        setFocusableAllField();
                        item.setIcon(R.drawable.icon_submit_user_account);
                        flagFocusEnable = 1;

                    } else {
                        checkValidationForEditProfile();
                        item.setIcon(R.drawable.icon_edit_account_setting);
                        setUnfousableAllFields();
                        flagFocusEnable = 0;
                    }
                }
                return false;
            }
        });
    }

    private void checkValidationForEditProfile(){
        String imageString = "";
        if(!ConstantClass.TAG_SELECTED_PROFILE_IMAGE.equalsIgnoreCase("")) {
            imageString = _CommonMethods.getBase64String(ConstantClass.TAG_SELECTED_PROFILE_IMAGE);
        }
        if(_connection.isNetworkAvailable()) {
            if (etUserNameAccountSetting.getText().toString().equalsIgnoreCase("")) {
                Toast.makeText(AccountSettingActivity.this, "User name should not be blank", Toast.LENGTH_LONG).show();
            }else if(etUserNameAccountSetting.getText().toString().matches("[0-9]+") && etUserNameAccountSetting.getText().length() > 2) {
                    Toast.makeText(AccountSettingActivity.this, "User name should not be only Numbers!", Toast.LENGTH_LONG).show();
            } else if (etUserEmailIdAccountSetting.getText().toString().equalsIgnoreCase("")) {
                Toast.makeText(AccountSettingActivity.this, "Email id should not be blank", Toast.LENGTH_LONG).show();
            } else if (etUserEmailIdAccountSetting.getText().toString().indexOf("@") == -1
                    || etUserEmailIdAccountSetting.getText().toString().indexOf(".") == -1) {
                Toast.makeText(AccountSettingActivity.this, "Please insert a correct email id", Toast.LENGTH_LONG).show();
            } else if (etUserMobileNoAccountSetting.getText().toString().equalsIgnoreCase("")) {
              //  Toast.makeText(AccountSettingActivity.this, "Phone no. should not be blank", Toast.LENGTH_LONG).show();
                etUserMobileNoAccountSetting.setText("No Mobile number Available!");
            } else if (etUserMobileNoAccountSetting.getText().toString().length()<10) {
                  Toast.makeText(AccountSettingActivity.this, "Phone no. should be not be less than 10 digits", Toast.LENGTH_LONG).show();
                etUserMobileNoAccountSetting.setText(_pref.getSession(ConstantClass.TAG_USER_PHONE_NO));
            }

            else {
                setUpProgressDialog();
                setUpParameter(etUserNameAccountSetting.getText().toString(),
                        etUserEmailIdAccountSetting.getText().toString(),
                        etUserMobileNoAccountSetting.getText().toString(),
                        imageString);
            }
        } else {
            _connection.getNetworkActiveAlert().show();
        }
    }

    private void setUpUserInfo(){
        etUserNameAccountSetting.setText(_pref.getSession(ConstantClass.TAG_USER_NAME));
        etUserEmailIdAccountSetting.setText(_pref.getSession(ConstantClass.TAG_USER_EMAIL_ID));
        etUserMobileNoAccountSetting.setText(_pref.getSession(ConstantClass.TAG_USER_PHONE_NO));
        Log.e("food type", _pref.getSession(ConstantClass.TAG_SELECTED_FOOD_TYPE));
        if(_pref.getSession(ConstantClass.TAG_SELECTED_FOOD_TYPE).equalsIgnoreCase("Veg")) {
            switchFoodTypeAccountSetting.setChecked(true);
        } else {
            switchFoodTypeAccountSetting.setChecked(false);
        }
    }

    private void setUpProgressDialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait..");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ConstantClass.TAG_SELECTED_PROFILE_IMAGE = "";
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void setUpFont(){
        Typeface fontBariolLight = Typeface.createFromAsset(getAssets(), "Bariol_Light.otf");
        etUserNameAccountSetting.setTypeface(fontBariolLight);
        etUserEmailIdAccountSetting.setTypeface(fontBariolLight);
        etUserMobileNoAccountSetting.setTypeface(fontBariolLight);
    }

    private void onClick(){
        switchFoodTypeAccountSetting.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    _pref.setSession(ConstantClass.TAG_SELECTED_FOOD_TYPE, "Veg");
                    Log.e("food type", _pref.getSession(ConstantClass.TAG_SELECTED_FOOD_TYPE));
                    dbAdapter.open();
                    dbAdapter.updateVegStatus(_pref.getSession(ConstantClass.ACCESS_TOKEN),
                            _pref.getSession(ConstantClass.TAG_SELECTED_FOOD_TYPE));
                    dbAdapter.close();
                    sendFeedbackForFoodStatus();
                } else {
                    _pref.setSession(ConstantClass.TAG_SELECTED_FOOD_TYPE, "Non-Veg");
                    Log.e("food type", _pref.getSession(ConstantClass.TAG_SELECTED_FOOD_TYPE));
                    dbAdapter.open();
                    dbAdapter.updateVegStatus(_pref.getSession(ConstantClass.ACCESS_TOKEN),
                            _pref.getSession(ConstantClass.TAG_SELECTED_FOOD_TYPE));
                    dbAdapter.close();

                    sendFeedbackForFoodStatus();
                }
            }
        });



        llAddCollegeChangeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentCollages = new Intent(AccountSettingActivity.this, CollegeActivity.class);
                intentCollages.putExtra("optionId", 4);
                intentCollages.putExtra("routeScreen", "profile");
                startActivity(intentCollages);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        ivEditProfileSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(AccountSettingActivity.this, CameraActivity.class);
                intent.putExtra("routeFrom", "account_setting");
                startActivity(intent);
                ConstantClass.TAG_SELECTED_PROFILE_IMAGE = "";*/
              checkCameraPermission();
            }
        });

        civProfileImageAccountSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            checkCameraPermission();
            }
        });

        rlChangePasswordAccountSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentCollages = new Intent(AccountSettingActivity.this, ChangePasswordActivity.class);
                startActivity(intentCollages);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    private void sendFeedbackForFoodStatus() {
        try {
            JSONObject jsonObjInnerParams = new JSONObject();
            jsonObjInnerParams.put("accessToken", _pref.getSession(ConstantClass.ACCESS_TOKEN));
            jsonObjInnerParams.put("foodType", _pref.getSession(ConstantClass.TAG_SELECTED_FOOD_TYPE));
            JSONObject jsonObjParams = new JSONObject();
            jsonObjParams.put("data", jsonObjInnerParams);
            Log.e("login data", jsonObjParams.toString());
            _serviceAction.requestVersionApi(jsonObjParams, "change-food-type");
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    private void setFocusableAllField(){
        etUserNameAccountSetting.setFocusable(true);
        etUserNameAccountSetting.setFocusableInTouchMode(true);
        etUserNameAccountSetting.setClickable(true);
        etUserNameAccountSetting.setTextColor(Color.BLACK);
        etUserNameAccountSetting.setBackgroundColor(Color.LTGRAY);


      /*  etUserEmailIdAccountSetting.setFocusable(false);
        etUserEmailIdAccountSetting.setFocusableInTouchMode(true);
        etUserEmailIdAccountSetting.setClickable(true);*/

        etUserMobileNoAccountSetting.setFocusable(false);
        etUserMobileNoAccountSetting.setFocusableInTouchMode(true);
        etUserMobileNoAccountSetting.setClickable(true);
        etUserMobileNoAccountSetting.setTextColor(Color.BLACK);
        etUserMobileNoAccountSetting.setBackgroundColor(Color.LTGRAY);

        etUserNameAccountSetting.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(etUserNameAccountSetting, InputMethodManager.SHOW_IMPLICIT);
    }

    private void setUnfousableAllFields(){
        etUserNameAccountSetting.setFocusable(false);
        etUserEmailIdAccountSetting.setFocusable(false);
        etUserMobileNoAccountSetting.setFocusable(false);
        etUserNameAccountSetting.setTextColor(Color.GRAY);
        etUserMobileNoAccountSetting.setTextColor(Color.GRAY);
        etUserMobileNoAccountSetting.setBackgroundColor(Color.argb(0, 235, 235, 235));
        etUserNameAccountSetting.setBackgroundColor(Color.argb(0, 235, 235, 235));

    }

    private void setAddCollegeContainer(){
        tvAddCollegeTextChangeProfile.setVisibility(View.GONE);
        int i=0;
        for (AddCollegeItem collegeItem : ConstantClass.addCollegeItems) {
            final LinearLayout collegeLayoutContainer = new LinearLayout(this);
            LinearLayout.LayoutParams collegeLayoutContainerParam = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            collegeLayoutContainerParam.setMargins(4, 4, 4, 4);
            collegeLayoutContainer.setLayoutParams(collegeLayoutContainerParam);
            collegeLayoutContainer.setOrientation(LinearLayout.HORIZONTAL);
            collegeLayoutContainer.setPadding(4, 4, 4, 4);
            collegeLayoutContainer.setBackgroundResource(R.drawable.icon_add_college_box);
            collegeLayoutContainer.setWeightSum(10);

            LinearLayout collegeTitleLayoutContainer = new LinearLayout(this);
            LinearLayout.LayoutParams collegeTitleLayoutContainerParam = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,  ViewGroup.LayoutParams.WRAP_CONTENT, 10);
            collegeTitleLayoutContainer.setLayoutParams(collegeTitleLayoutContainerParam);
            collegeTitleLayoutContainer.setOrientation(LinearLayout.HORIZONTAL);
            collegeTitleLayoutContainer.setGravity(Gravity.CENTER);

            BariolLightTextView collegeTitle = new BariolLightTextView(this);
            ViewGroup.LayoutParams collegeTitleParam = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            collegeTitle.setLayoutParams(collegeTitleParam);
            collegeTitle.setText(collegeItem.getCollegeTitle());
            collegeTitle.setTextSize(16);
            collegeTitle.setTextColor(Color.parseColor("#000000"));
            collegeTitle.setSingleLine();
            collegeTitleLayoutContainer.addView(collegeTitle);
            collegeLayoutContainer.addView(collegeTitleLayoutContainer);

            final LinearLayout collegeCrossLayoutContainer = new LinearLayout(this);
            LinearLayout.LayoutParams collegeCrossLayoutContainerParam = new LinearLayout.
                    LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,  ViewGroup.LayoutParams.WRAP_CONTENT,10);
            collegeCrossLayoutContainer.setLayoutParams(collegeCrossLayoutContainerParam);
            collegeCrossLayoutContainer.setGravity(Gravity.CENTER);
            collegeCrossLayoutContainer.setOrientation(LinearLayout.HORIZONTAL);
            collegeCrossLayoutContainer.setTag(i);
            collegeCrossLayoutContainer.setPadding(5, 5, 5, 5);
            collegeCrossLayoutContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(ConstantClass.addCollegeItems.size()<2) {

                        for (int j = 0; j < ConstantClass.addCollegeItems.size(); j++) {
                            collegeCrossLayoutContainer.setTag(j);
                        }
                    }
                    collegeLayoutContainer.setVisibility(View.GONE);
                    ConstantClass.addCollegeItems.remove(Integer.parseInt(
                            collegeCrossLayoutContainer.getTag().toString()));
                    checkValidationForEditProfile();

                  //  Log.e("position", collegeCrossLayoutContainer.getTag().toString());
                  //  Log.e("remove success",
                         //   String.valueOf(ConstantClass.addCollegeItems.remove(Integer.parseInt(
                         //           collegeCrossLayoutContainer.getTag().toString()))));
                  //  Log.e("Size", String.valueOf(ConstantClass.addCollegeItems.size()));
                }
            });

            ImageView crossLine = new ImageView(this);
            ViewGroup.LayoutParams crossLineParam = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            crossLine.setLayoutParams(crossLineParam);
            crossLine.setImageResource(R.drawable.icon_line_add_collage);
            crossLine.setAdjustViewBounds(true);
            crossLine.setPadding(0,0,10,0);
            collegeCrossLayoutContainer.addView(crossLine);
            ImageView crossDeleteCollegeName = new ImageView(this);
            ViewGroup.LayoutParams crossDeleteCollegeNameParam = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            crossDeleteCollegeName.setLayoutParams(crossDeleteCollegeNameParam);
            crossDeleteCollegeName.setImageResource(R.drawable.icon_cross_add_college);
            crossDeleteCollegeName.setPadding(5, 5, 5, 5);
            crossDeleteCollegeName.setScaleType(ImageView.ScaleType.FIT_CENTER);
            collegeCrossLayoutContainer.addView(crossDeleteCollegeName);
            collegeLayoutContainer.addView(collegeCrossLayoutContainer);
            llAddCollegeContainer.addView(collegeLayoutContainer);
            i++;

        }
        checkValidationForEditProfile();
    }

    private void setUpParameter(String userName, String emailId, String phoneNo, String userProfileImage){
        try{
            JSONObject jsonObjInnerParams = new JSONObject();
            JSONArray jsonCollegeArray = new JSONArray();
            jsonObjInnerParams.put("accessToken", _pref.getSession(ConstantClass.ACCESS_TOKEN));
            jsonObjInnerParams.put("userName", userName);
            jsonObjInnerParams.put("userEmail", emailId);
            jsonObjInnerParams.put("userPhNo", phoneNo);
            jsonObjInnerParams.put("cityId",_pref.getSession(ConstantClass.TAG_SELECTED_CITY_ID));
            for(int i=0; i<ConstantClass.addCollegeItems.size(); i++){
                JSONObject jsonObjectItem = new JSONObject();
                jsonObjectItem.put("collegeId", String.valueOf(ConstantClass.addCollegeItems.get(i).getId()));
                jsonCollegeArray.put(jsonObjectItem);
            }
            jsonObjInnerParams.put("collegeIdArray", jsonCollegeArray);
            jsonObjInnerParams.put("userProfileImg", userProfileImage);

            JSONObject jsonObjParams = new JSONObject();
            jsonObjParams.put("data", jsonObjInnerParams);
            Log.e("input json", jsonObjParams.toString());
            _serviceAction.requestVersionApi(jsonObjParams, "edit-profile");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!ConstantClass.TAG_SELECTED_PROFILE_IMAGE.equalsIgnoreCase("")) {
            civProfileImageAccountSetting.setImageURI(Uri.parse(ConstantClass.TAG_SELECTED_PROFILE_IMAGE));
        } else {
            Toast.makeText(AccountSettingActivity.this, "dpUrl ==>>> " + _pref.getSession(ConstantClass.TAG_USER_DP_IMAGE), Toast.LENGTH_LONG);
            imageLoader.displayImage(_pref.getSession(ConstantClass.TAG_USER_DP_IMAGE), civProfileImageAccountSetting, options);
        }
    }

    @Override
    public void onStarted() {

    }

    @Override
    public void onCompleted(JSONObject response) {
        Log.e("response", response.toString());

        try {
            JSONObject jsonObjError = response.getJSONObject("errNode");
            if (jsonObjError.getInt("errCode") == 0) {
                JSONObject jsonObjData = response.getJSONObject("data");
                if (jsonObjData.getBoolean("success")) {
                    Toast.makeText(this, jsonObjData.getString("msg"), Toast.LENGTH_LONG).show();
                    _pref.setSession(ConstantClass.TAG_USER_NAME, jsonObjData.getString("userName"));
                    if (jsonObjData.getString("phoneNo") != null && !jsonObjData.getString("phoneNo").isEmpty()) {
                        _pref.setSession(ConstantClass.TAG_USER_PHONE_NO, jsonObjData.getString("phoneNo"));
                    } else {
                        _pref.setSession(ConstantClass.TAG_USER_PHONE_NO, "");
                    }
                    _pref.setSession(ConstantClass.TAG_USER_DP_IMAGE, jsonObjData.getString("dpUrl"));
                    _pref.setSession(ConstantClass.TAG_USER_EMAIL_ID, jsonObjData.getString("userEmail"));

                    JSONArray jsonArrayAddedCollege = jsonObjData.getJSONArray("addedCollege");

                    if(jsonArrayAddedCollege.length() > 0){
                        _CommonMethods.setAddedColleges(jsonArrayAddedCollege);
                    }
                    else
                    {
                        tvAddCollegeTextChangeProfile.setVisibility(View.VISIBLE);
                    }
                    progressDialog.dismiss();

                } else {
                    Toast.makeText(this, jsonObjData.getString("msg"), Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                    etUserMobileNoAccountSetting.setText(_pref.getSession(ConstantClass.TAG_USER_PHONE_NO));
                }
            } else {
                Toast.makeText(AccountSettingActivity.this, jsonObjError.getString("errMsg"), Toast.LENGTH_LONG);
            }
        } catch(Exception e){
            e.printStackTrace();
        }

       /* if (ConstantClass.addCollegeItems.size()==0) {
            tvAddCollegeTextChangeProfile.setVisibility(View.VISIBLE);
        }*/

    }

    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Gallery", "Cancel" };
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(AccountSettingActivity.this);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    takePicture();
                } else if (items[item].equals("Choose from Gallery")) {
                    chooseImage();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

   /* public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }*/

    private void takePicture() {
        chooserType = ChooserType.REQUEST_CAPTURE_PICTURE;
        imageChooserManager = new ImageChooserManager(this,
                ChooserType.REQUEST_CAPTURE_PICTURE, true);
        imageChooserManager.setImageChooserListener(this);
        try {
            filePath = imageChooserManager.choose();
            imageChooserManager.clearOldFiles();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onImageChosen(final ChosenImage chosenImage) {

        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                // isActivityResultOver = true;
                originalFilePath = chosenImage.getFilePathOriginal();
                String thumbnailFilePath = chosenImage.getFileThumbnail();
                String thumbnailSmallFilePath = chosenImage.getFileThumbnailSmall();
                /*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, originalFilePath);
                startActivityForResult(intent, REQUEST_CAMERA);*/

                ConstantClass.TAG_SELECTED_PROFILE_IMAGE = originalFilePath;
                Log.e("PATH_:", originalFilePath);
                ExifInterface ei = null;
                try {
                    ei = new ExifInterface(originalFilePath);
                    orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 4;
                Bitmap b;
                switch(orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        b = rotateImage(BitmapFactory.decodeFile(ConstantClass.TAG_SELECTED_PROFILE_IMAGE,options), 90);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        b = rotateImage(BitmapFactory.decodeFile(ConstantClass.TAG_SELECTED_PROFILE_IMAGE,options), 180);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        b = rotateImage(BitmapFactory.decodeFile(ConstantClass.TAG_SELECTED_PROFILE_IMAGE,options), 270);
                        break;
                    default:
                        b = rotateImage(BitmapFactory.decodeFile(ConstantClass.TAG_SELECTED_PROFILE_IMAGE,options), 0);
                        break;
                }
                civProfileImageAccountSetting.setImageBitmap( b);
                checkValidationForEditProfile();

            }
        });

    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Bitmap retVal;

        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        retVal = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);

        return retVal;
    }

    @Override
    public void onError(String s) {

    }

    @Override
    public void onImagesChosen(ChosenImages chosenImages) {

    }

    private void chooseImage() {
        chooserType = ChooserType.REQUEST_PICK_PICTURE;
        imageChooserManager = new ImageChooserManager(this,
                ChooserType.REQUEST_PICK_PICTURE, true);
        Bundle bundle = new Bundle();
       // bundle.putBoolean(Intent.EXTRA_ALLOW_MULTIPLE, true);
        imageChooserManager.setExtras(bundle);
        imageChooserManager.setImageChooserListener(this);
        imageChooserManager.clearOldFiles();
        try {
            filePath = imageChooserManager.choose();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
