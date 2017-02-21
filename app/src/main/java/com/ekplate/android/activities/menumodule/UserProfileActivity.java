package com.ekplate.android.activities.menumodule;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.ekplate.android.R;
import com.ekplate.android.activities.registermodule.LandingActivity;
import com.ekplate.android.activities.socialsharemodule.SocialShareActivity;
import com.ekplate.android.appinterfaces.BackgroundActionInterface;
import com.ekplate.android.config.BaseActivity;
import com.ekplate.android.utils.CallServiceAction;
import com.ekplate.android.utils.ConstantClass;
import com.ekplate.android.utils.NetworkConnectionCheck;
import com.ekplate.android.utils.Pref;
import com.ekplate.android.views.CircularImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

public class UserProfileActivity extends BaseActivity
        implements BackgroundActionInterface {

    private RelativeLayout rlAccountSetting, rlMyStuff, rlShareApp, rlRateApp, rlLogout;
    private TextView tvAccountSetting, tvMyStuff, tvShareApp, tvRateApp, tvLogout, tvProfileNameViewProfile,
            tvProfileEmailIdViewProfile, tvProfileFoodTypeStatusViewProfile, tvProfilePhoneNoViewProfile;
    private ImageView ivArrowAccountSetting, ivArrowStuff, ivArrowShareApp, ivArrowRateApp, ivArrowLogout;
    private CircularImageView civProfileImageViewProfile;
    private Switch switchNotificationAction;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private Pref _pref;
    private NetworkConnectionCheck _connection;
    private CallServiceAction _serviceAction;
    private ProgressDialog progressDialog;
    private int logoutServiceFlag = 0, notificationEnableFlag = 0;
    private Toolbar tbMyProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        getSupportActionBar().hide();
        getWindow().setBackgroundDrawable(null);
        initialize();
        setUpToolbar();
        setUpUserInfo();
        onClick();
    }

    private void setUpToolbar(){
        tbMyProfile = (Toolbar) findViewById(R.id.tbMyProfile);
        tbMyProfile.setNavigationIcon(R.drawable.ic_action_back);
        tbMyProfile.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initialize(){
        rlAccountSetting = (RelativeLayout) findViewById(R.id.rlAccountSetting);
        rlMyStuff = (RelativeLayout) findViewById(R.id.rlMyStuff);
        rlShareApp = (RelativeLayout) findViewById(R.id.rlShareApp);
        rlRateApp = (RelativeLayout) findViewById(R.id.rlRateApp);
        rlLogout = (RelativeLayout) findViewById(R.id.rlLogout);
        civProfileImageViewProfile = (CircularImageView) findViewById(R.id.civProfileImageViewProfile);
        tvAccountSetting = (TextView) findViewById(R.id.tvAccountSetting);
        tvMyStuff= (TextView) findViewById(R.id.tvMyStuff);
        tvShareApp = (TextView) findViewById(R.id.tvShareApp);
        tvRateApp = (TextView) findViewById(R.id.tvRateApp);
        tvLogout = (TextView) findViewById(R.id.tvLogout);
        ivArrowAccountSetting = (ImageView) findViewById(R.id.ivArrowAccountSetting);
        ivArrowStuff = (ImageView) findViewById(R.id.ivArrowStuff);
        ivArrowShareApp = (ImageView) findViewById(R.id.ivArrowShareApp);
        ivArrowRateApp = (ImageView) findViewById(R.id.ivArrowRateApp);
        ivArrowLogout = (ImageView) findViewById(R.id.ivArrowLogout);
        tvProfileNameViewProfile = (TextView) findViewById(R.id.tvProfileNameViewProfile);
        tvProfileEmailIdViewProfile = (TextView) findViewById(R.id.tvProfileEmailIdViewProfile);
        tvProfileFoodTypeStatusViewProfile = (TextView) findViewById(R.id.tvProfileFoodTypeStatusViewProfile);
        tvProfilePhoneNoViewProfile = (TextView) findViewById(R.id.tvProfilePhoneNoViewProfile);
        switchNotificationAction = (Switch) findViewById(R.id.switchNotificationAction);

        _pref = new Pref(this);
        _connection = new NetworkConnectionCheck(this);
        _serviceAction = new CallServiceAction(this);
        _serviceAction.actionInterface = UserProfileActivity.this;
        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                        .cacheInMemory(true)
                        .cacheOnDisk(true)
                        .showImageOnFail(R.drawable.profile_dp)
                        .showImageForEmptyUri(R.drawable.profile_dp)
                        .showImageOnLoading(R.drawable.profile_dp)
                        .build();
    }

    private void onClick(){
        rlAccountSetting.setOnClickListener(onClickListenerAccountSetting);

        civProfileImageViewProfile.setOnClickListener(onClickListenerAccountSetting);

        rlMyStuff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unSetTextColor();
                unSetArrowImage();
                ivArrowStuff.setSelected(true);
                tvMyStuff.setTextColor(getResources().getColor(R.color.theme_color));
                startActivity(new Intent(UserProfileActivity.this, MyStuffActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        rlShareApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unSetTextColor();
                unSetArrowImage();
                tvShareApp.setTextColor(getResources().getColor(R.color.theme_color));
                ivArrowShareApp.setSelected(true);
                Intent intent = new Intent(UserProfileActivity.this, SocialShareActivity.class);
                intent.putExtra("route_from", "user_profile");
                startActivity(intent);
            }
        });
        rlRateApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unSetTextColor();
                unSetArrowImage();
                tvRateApp.setTextColor(getResources().getColor(R.color.theme_color));
                ivArrowRateApp.setSelected(true);
            }
        });
        rlLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getLogoutActiveAlert().show();

            }
        });
        switchNotificationAction.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (_connection.isNetworkAvailable()) {
                    notificationEnableFlag = 1;
                    setUpProgressDialog();
                    if (isChecked) {
                        setInputParamForNotificationEnabled(isChecked);
                    } else {
                        setInputParamForNotificationEnabled(isChecked);
                    }
                } else {
                    _connection.getNetworkActiveAlert().show();
                }
            }
        });
    }

    private void unSetTextColor(){
        tvAccountSetting.setTextColor(Color.parseColor("#FFFFFF"));
        tvMyStuff.setTextColor(Color.parseColor("#FFFFFF"));
        tvShareApp.setTextColor(Color.parseColor("#FFFFFF"));
        tvRateApp.setTextColor(Color.parseColor("#FFFFFF"));
        tvLogout.setTextColor(Color.parseColor("#FFFFFF"));
    }

    private void unSetArrowImage(){
        ivArrowAccountSetting.setSelected(false);
        ivArrowStuff.setSelected(false);
        ivArrowShareApp.setSelected(false);
        ivArrowRateApp.setSelected(false);
        ivArrowLogout.setSelected(false);
    }


    private void logOut() {
        _pref.setSession(ConstantClass.ACCESS_TOKEN, "");
        Intent intent = new Intent(UserProfileActivity.this, LandingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        clearPrefData();
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void clearPrefData() {
        _pref.setSession(ConstantClass.TAG_USER_DP_IMAGE,"");
        _pref.setSession(ConstantClass.TAG_USER_NAME,"");
    }

    private void setUpUserInfo(){
        if(_pref.getSession(ConstantClass.TAG_SELECTED_FOOD_TYPE).equalsIgnoreCase("veg")) {
            Log.e("food type", _pref.getSession(ConstantClass.TAG_SELECTED_FOOD_TYPE));
            tvProfileFoodTypeStatusViewProfile.setText("ONLY VEG MODE");
            tvProfileFoodTypeStatusViewProfile.setTextColor(getResources().getColor(R.color.veg_mode_green_color));
        }

        if(_pref.getSession(ConstantClass.TAG_NOTIFICATION_ENABLED).equalsIgnoreCase("true")) {
            switchNotificationAction.setChecked(true);
        } else {
            switchNotificationAction.setChecked(false);
        }
        tvProfileNameViewProfile.setText(_pref.getSession(ConstantClass.TAG_USER_NAME));
        tvProfileEmailIdViewProfile.setText(_pref.getSession(ConstantClass.TAG_USER_EMAIL_ID));
        tvProfilePhoneNoViewProfile.setText(_pref.getSession(ConstantClass.TAG_USER_PHONE_NO));
        Toast.makeText(UserProfileActivity.this, "dpUrl ==>>> " + _pref.getSession(ConstantClass.TAG_USER_DP_IMAGE), Toast.LENGTH_LONG);
        imageLoader.displayImage(_pref.getSession(ConstantClass.TAG_USER_DP_IMAGE), civProfileImageViewProfile, options);
    }

    private void setUpProgressDialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait..");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void setInputParamForNotificationEnabled(boolean notifyStatus){
        try {
            JSONObject parentJsonObj = new JSONObject();
            JSONObject childJsonObj = new JSONObject();
            childJsonObj.put("accessToken", _pref.getSession(ConstantClass.ACCESS_TOKEN));
            childJsonObj.put("notificationActiveStatus", String.valueOf(notifyStatus));
            parentJsonObj.put("data", childJsonObj);
            Log.e("input data", parentJsonObj.toString());
            _serviceAction.requestVersionApi(parentJsonObj, "enable-notifications");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setInputParamForLogout(){
        try {
            JSONObject parentJsonObj = new JSONObject();
            JSONObject childJsonObj = new JSONObject();
            childJsonObj.put("accessToken", _pref.getSession(ConstantClass.ACCESS_TOKEN));
            parentJsonObj.put("data", childJsonObj);
            Log.e("input data", parentJsonObj.toString());
            _serviceAction.requestVersionApi(parentJsonObj, "logout");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    protected void onResume() {
        super.onResume();
        unSetTextColor();
        realTimeUpdate();
        if(_pref.getSession(ConstantClass.TAG_SELECTED_FOOD_TYPE).equalsIgnoreCase("veg")) {
            Log.e("food type", _pref.getSession(ConstantClass.TAG_SELECTED_FOOD_TYPE));
            tvProfileFoodTypeStatusViewProfile.setText("ONLY VEG MODE");
            tvProfileFoodTypeStatusViewProfile.setTextColor(getResources().getColor(R.color.veg_mode_green_color));
        } else {
            tvProfileFoodTypeStatusViewProfile.setText("");
        }
    }

    @Override
    public void onStarted() {

    }

    @Override
    public void onCompleted(JSONObject response) {
        Log.e("notification enabled response", response.toString());
        try {
            if (notificationEnableFlag == 1) {
                JSONObject errorNodeJsonObj = response.getJSONObject("errNode");
                if (errorNodeJsonObj.getInt("errCode") == 0) {
                    JSONObject dataJsonObject = response.getJSONObject("data");
                    if (dataJsonObject.getBoolean("success")) {
                        Toast.makeText(UserProfileActivity.this, dataJsonObject.getString("sucessMsg"), Toast.LENGTH_LONG).show();
                        if (_pref.getSession(ConstantClass.TAG_NOTIFICATION_ENABLED).equalsIgnoreCase("true")) {
                            switchNotificationAction.setChecked(false);
                            _pref.setSession(ConstantClass.TAG_NOTIFICATION_ENABLED, "false");
                        } else {
                            switchNotificationAction.setChecked(true);
                            _pref.setSession(ConstantClass.TAG_NOTIFICATION_ENABLED, "true");
                        }
                    } else {
                        Toast.makeText(UserProfileActivity.this, dataJsonObject.getString("msg"), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(UserProfileActivity.this, errorNodeJsonObj.getString("errMsg"), Toast.LENGTH_LONG).show();
                }
                notificationEnableFlag = 0;
            } else if (logoutServiceFlag == 1){
                JSONObject errorNodeJsonObj = response.getJSONObject("errNode");
                if (errorNodeJsonObj.getInt("errCode") == 0) {
                    JSONObject dataJsonObject = response.getJSONObject("data");
                    if (dataJsonObject.getBoolean("success")) {
                        Toast.makeText(UserProfileActivity.this, dataJsonObject.getString("msg"), Toast.LENGTH_LONG).show();
                      //  logOutAction();
                        logOut();
                    } else {
                        Toast.makeText(UserProfileActivity.this, dataJsonObject.getString("msg"), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(UserProfileActivity.this, errorNodeJsonObj.getString("errMsg"), Toast.LENGTH_LONG).show();
                }
                logoutServiceFlag = 0;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        progressDialog.dismiss();
    }

    public AlertDialog getLogoutActiveAlert(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to logout?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                       // _pref.setSession(ConstantClass.TAG_SELECTED_CITY_LOCATION, "Mumbai");
                        unSetTextColor();
                        unSetArrowImage();
                        tvLogout.setTextColor(getResources().getColor(R.color.theme_color));
                        ConstantClass.addCollegeItems.clear();
                        ivArrowLogout.setSelected(true);
                        logoutServiceFlag = 1;
                        dialogInterface.cancel();
                        setUpProgressDialog();
                        setInputParamForLogout();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        return  alertDialog;
    }

    View.OnClickListener onClickListenerAccountSetting = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            unSetTextColor();
            unSetArrowImage();
            tvAccountSetting.setTextColor(getResources().getColor(R.color.theme_color));
            ivArrowAccountSetting.setSelected(true);
            Intent intentAccountSetting = new Intent(UserProfileActivity.this, AccountSettingActivity.class);
            intentAccountSetting.putExtra("routeScreenForAccountSetting", "userprofile");
            startActivity(intentAccountSetting);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    };

    private void realTimeUpdate() {
        tvProfileNameViewProfile.setText(_pref.getSession(ConstantClass.TAG_USER_NAME));
        tvProfileEmailIdViewProfile.setText(_pref.getSession(ConstantClass.TAG_USER_EMAIL_ID));
        tvProfilePhoneNoViewProfile.setText(_pref.getSession(ConstantClass.TAG_USER_PHONE_NO));
        imageLoader.displayImage(_pref.getSession(ConstantClass.TAG_USER_DP_IMAGE), civProfileImageViewProfile, options);
    }
}
