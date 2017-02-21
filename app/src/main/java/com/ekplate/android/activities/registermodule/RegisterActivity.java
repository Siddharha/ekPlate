package com.ekplate.android.activities.registermodule;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ekplate.android.R;
import com.ekplate.android.activities.menumodule.TermsConditionsActivity;
import com.ekplate.android.appinterfaces.BackgroundActionInterface;
import com.ekplate.android.config.BaseActivity;
import com.ekplate.android.utils.CallServiceAction;
import com.ekplate.android.utils.CommonFunction;
import com.ekplate.android.utils.CommonMethods;
import com.ekplate.android.utils.ConstantClass;
import com.ekplate.android.utils.NetworkConnectionCheck;
import com.ekplate.android.utils.Pref;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.entities.Profile;
import com.sromku.simple.fb.listeners.OnLoginListener;
import com.sromku.simple.fb.listeners.OnProfileListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class RegisterActivity extends BaseActivity implements
        BackgroundActionInterface, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private LinearLayout llLoginGoToLogin, llTermsConditions;
    private CardView cvRegister;
    private EditText etSignUpName, etSignUpEmail, etSignUpMobile, etSignUpPassword;
    private FrameLayout flFbLoginBtnLanding, flGpLoginBtnLanding;
    private Boolean CONNECTION_FLAG = false;
    private Pref _pref;
    private NetworkConnectionCheck _connection;
    private CallServiceAction _serviceAction;
    private ProgressDialog progressDialog;
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 0;
    private boolean mIntentInProgress;
    private SimpleFacebook mSimpleFacebook;
    private String email, name, oauthId, gender, mobileNo, dpUrl,socialLoginType;
    private Profile.Properties properties;
    private CommonFunction _commonFunction;
    private CommonMethods _commonMethods;
    private List<Address> addresses;
    private Geocoder geocoder;
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_register);
        getWindow().setBackgroundDrawable(null);
        getSupportActionBar().hide();

        initialize();
        onClick();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mSimpleFacebook.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            mIntentInProgress = false;

            if(resultCode !=-1)
            {
                if (!mGoogleApiClient.isConnecting()) {
                    if(!CONNECTION_FLAG) {
                        mGoogleApiClient.connect();
                    }
                    // Toast.makeText(getBaseContext(),"Connected!",Toast.LENGTH_LONG).show();
                }}
            else
            {
                mGoogleApiClient.connect();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("select city status", String.valueOf(
                _pref.getIntegerSession(ConstantClass.TAG_SELECTED_CITY_STATUS)));
        if (_pref.getIntegerSession(ConstantClass.TAG_SELECTED_CITY_STATUS) == 1) {
            _pref.setSession(ConstantClass.TAG_SELECTED_CITY_STATUS, 1);
                _pref.setSession(ConstantClass.TAG_SELECTED_CITY_LOCATION, pickCity());

                Toast.makeText(getBaseContext(), pickCity(), Toast.LENGTH_SHORT).show();
        }
        mSimpleFacebook = SimpleFacebook.getInstance(this);
    }

    private String pickCity() {
        String s = "Mumbai";
        if(_connection.isNetworkAvailable()){

            try {

                addresses = geocoder.getFromLocation(getLat(), getLng(), 1);
                if (!addresses.isEmpty()) {
                    String[] p = addresses.get(0).getAddressLine(2).split(",");
                    s = p[0];
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }else {
            _connection.getNetworkActiveAlert().show();
        }
        return s;
    }

    private double getLng() {
        Geocoder geocoder;
        String bestProvider;
        List<Address> user = null;
        double lng = 0;


       Criteria criteria = new Criteria();
       bestProvider = locationManager.getBestProvider(criteria, false);

      /*  if(permissionsGranted()){*/
        Location location = locationManager.getLastKnownLocation(bestProvider);

        if (location == null){
            Toast.makeText(this,"Location Not found",Toast.LENGTH_LONG).show();
        }else{
            geocoder = new Geocoder(this);
            try {
                user = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                lng=(double)user.get(0).getLongitude();

            }catch (Exception e) {
                e.printStackTrace();
            }
        }

      /*  }else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 3);
            lng = 0;
        }*/
        return lng;
    }

    private double getLat() {
        LatLng latLng = null;
        Geocoder geocoder;
        String bestProvider;
        List<Address> user = null;
        double lat = 0;

        Criteria criteria = new Criteria();
        bestProvider = locationManager.getBestProvider(criteria, false);
      /*  if(permissionsGranted()){*/
        Location location = locationManager.getLastKnownLocation(bestProvider);

        if (location == null) {
            Toast.makeText(this, "Location Not found", Toast.LENGTH_LONG).show();
        } else {
            geocoder = new Geocoder(this);
            try {
                user = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                lat = (double) user.get(0).getLatitude();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
      /*  }else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 3);
            lat = 0;
        }*/
        return lat;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void initialize(){
        llLoginGoToLogin = (LinearLayout) findViewById(R.id.llLoginGoToLogin);
        llTermsConditions = (LinearLayout) findViewById(R.id.llTermsConditions);
        cvRegister = (CardView) findViewById(R.id.cvRegister);
        etSignUpName = (EditText) findViewById(R.id.etSignUpName);
        etSignUpEmail = (EditText) findViewById(R.id.etSignUpEmail);
        etSignUpMobile = (EditText) findViewById(R.id.etSignUpMobile);
        etSignUpPassword = (EditText) findViewById(R.id.etSignUpPassword);
        _pref = new Pref(this);
        _connection = new NetworkConnectionCheck(this);
        _serviceAction = new CallServiceAction(this);
        _commonMethods = new CommonMethods(this);
        _serviceAction.actionInterface = RegisterActivity.this;
        flFbLoginBtnLanding = (FrameLayout) findViewById(R.id.flFbLoginBtnLanding);
        flGpLoginBtnLanding = (FrameLayout) findViewById(R.id.gpFbLoginBtnLanding);
        _commonFunction = new CommonFunction(this);
        locationManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);
        geocoder = new Geocoder(this, Locale.getDefault());
        properties = new Profile.Properties.Builder()
                .add(Profile.Properties.EMAIL)
                .add(Profile.Properties.ID)
                .add(Profile.Properties.FIRST_NAME)
                .add(Profile.Properties.LAST_NAME)
                .add(Profile.Properties.PICTURE)
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(RegisterActivity.this)
                .addConnectionCallbacks(RegisterActivity.this)
                .addOnConnectionFailedListener(RegisterActivity.this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();
    }

    private void onClick(){
        llLoginGoToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        llTermsConditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, TermsConditionsActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        cvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(_connection.isNetworkAvailable()){
                    signUpAction();
                } else {
                    _connection.getNetworkActiveAlert().show();
                }
            }
        });

        flFbLoginBtnLanding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSimpleFacebook.login(fbLoginListener);
            }
        });

        flGpLoginBtnLanding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGoogleApiClient.connect();
            }
        });
    }

    private void signUpAction(){
        if(etSignUpName.getText().toString().equals("")){
            Toast.makeText(this, "Please give the name", Toast.LENGTH_LONG).show();
        }else if(etSignUpName.getText().toString().matches("[0-9]+") && etSignUpName.getText().length() > 2) {
            Toast.makeText(RegisterActivity.this, "User name should not be only Numbers!", Toast.LENGTH_LONG).show();
        }
        else if(etSignUpMobile.getText().toString().equals("")){
            Toast.makeText(this, "Please give the mobile no", Toast.LENGTH_LONG).show();
        } else if(etSignUpMobile.getText().toString().length()<10){
            Toast.makeText(this, "Please give correct mobile no", Toast.LENGTH_LONG).show();
        } else if(etSignUpPassword.getText().toString().equals("")){
            Toast.makeText(this, "Please give password", Toast.LENGTH_LONG).show();
        } else if(etSignUpPassword.getText().toString().length() < 6){
            Toast.makeText(this, "Please give 6 character password", Toast.LENGTH_LONG).show();
        }
        else{
            setRequestParam(etSignUpName.getText().toString(), etSignUpEmail.getText().toString(),
                    etSignUpMobile.getText().toString(), etSignUpPassword.getText().toString());
        }
    }

    private void setRequestParam(String name, String email, String mobileno, String password){
        try {
            JSONObject jsonObjInnerParams = new JSONObject();
            jsonObjInnerParams.put("name", name);
            jsonObjInnerParams.put("emailId", email);
            jsonObjInnerParams.put("mobileNo", mobileno);
            jsonObjInnerParams.put("password", password);
            jsonObjInnerParams.put("deviceType", "android");
            jsonObjInnerParams.put("deviceId", _commonFunction.getGCMRegistrationId());
            jsonObjInnerParams.put("latitude", _pref.getSession(ConstantClass.TAG_LATITUDE));
            jsonObjInnerParams.put("longitude", _pref.getSession(ConstantClass.TAG_LONGITUDE));
            JSONObject jsonObjParams = new JSONObject();
            jsonObjParams.put("data", jsonObjInnerParams);
            Log.e("input", jsonObjParams.toString());
            setUpProgressDialog();
            _serviceAction.requestVersionApi(jsonObjParams, "signup");
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    private void setRequestParam(String emailId, String oauthId, String name, String gender, String dpUrl){
        try {
            JSONObject jsonObjInnerParams = new JSONObject();
            jsonObjInnerParams.put("emailId", emailId);
            jsonObjInnerParams.put("oauthId", oauthId);
            jsonObjInnerParams.put("name", name);
            jsonObjInnerParams.put("profImage", dpUrl);
            jsonObjInnerParams.put("socialType", socialLoginType);
            jsonObjInnerParams.put("deviceType", "android");
            jsonObjInnerParams.put("deviceId", _commonFunction.getGCMRegistrationId());
            jsonObjInnerParams.put("latitude", _pref.getSession(ConstantClass.TAG_LATITUDE));
            jsonObjInnerParams.put("longitude", _pref.getSession(ConstantClass.TAG_LONGITUDE));
            JSONObject jsonObjParams = new JSONObject();
            jsonObjParams.put("data", jsonObjInnerParams);
            Log.e("registration data", jsonObjParams.toString());
            //Toast.makeText(RegisterActivity.this, jsonObjParams.toString(), Toast.LENGTH_LONG).show();

            _serviceAction.requestVersionApi(jsonObjParams, "oauth-login");
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onStarted() {

    }

    @Override
    public void onCompleted(JSONObject response) {
        Log.e("response", response.toString());
        progressDialog.dismiss();
        try{
            JSONObject jsonObjError = response.getJSONObject("errNode");
            if(jsonObjError.getInt("errCode") == 0) {
                JSONObject jsonObjData = response.getJSONObject("data");
                if(jsonObjData.getBoolean("success")) {
                    Toast.makeText(this, jsonObjData.getString("msg"), Toast.LENGTH_LONG).show();
                    if (jsonObjData.getString("msg").equals("You have successfully signed up to Ekplate")) {
                        _pref.setSession(ConstantClass.ACCESS_TOKEN,
                                jsonObjData.getString(ConstantClass.ACCESS_TOKEN));
                        _pref.setSession(ConstantClass.TAG_USER_NAME, jsonObjData.getString("useName"));
                        if (jsonObjData.getString("phoneNo") != null && !jsonObjData.getString("phoneNo").isEmpty()) {
                            _pref.setSession(ConstantClass.TAG_USER_PHONE_NO, jsonObjData.getString("phoneNo"));
                        } else {
                            _pref.setSession(ConstantClass.TAG_USER_PHONE_NO, "");
                        }
                        _pref.setSession(ConstantClass.TAG_USER_EMAIL_ID, jsonObjData.getString("emailId"));
                        _pref.setSession(ConstantClass.TAG_NOTIFICATION_ENABLED, jsonObjData.getString("notificationEnabled"));
                        JSONArray jsonArrayAddedCollege = jsonObjData.getJSONArray("addedCollege");
                        if (jsonArrayAddedCollege.length() > 0) {
                            _commonMethods.setAddedColleges(jsonArrayAddedCollege);
                        }
                        Intent intent = new Intent(RegisterActivity.this, VegetarianSelectionActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);


                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                    }
                    else{
                    _pref.setSession(ConstantClass.ACCESS_TOKEN,
                            jsonObjData.getString(ConstantClass.ACCESS_TOKEN));
                    _pref.setSession(ConstantClass.TAG_USER_NAME, jsonObjData.getString("useName"));
                    _pref.setSession(ConstantClass.TAG_SELECTED_FOOD_TYPE, jsonObjData.getString("foodType"));
                    Toast.makeText(RegisterActivity.this, "dpUrl ==>>> " + jsonObjData.getString("dpUrl"),
                            Toast.LENGTH_LONG);
                    _pref.setSession(ConstantClass.TAG_USER_DP_IMAGE, jsonObjData.getString("dpUrl"));
                    if (jsonObjData.getString("phoneNo") != null && !jsonObjData.getString("phoneNo").isEmpty()) {
                        _pref.setSession(ConstantClass.TAG_USER_PHONE_NO, jsonObjData.getString("phoneNo"));
                    } else {
                        _pref.setSession(ConstantClass.TAG_USER_PHONE_NO, "");
                    }
                    _pref.setSession(ConstantClass.TAG_USER_EMAIL_ID, jsonObjData.getString("emailId"));
                    _pref.setSession(ConstantClass.TAG_NOTIFICATION_ENABLED, jsonObjData.getString("notificationEnabled"));
                    JSONArray jsonArrayAddedCollege = jsonObjData.getJSONArray("addedCollege");
                    if (jsonArrayAddedCollege.length() > 0) {
                        _commonMethods.setAddedColleges(jsonArrayAddedCollege);
                    }
                    Intent intent = new Intent(RegisterActivity.this, VegetarianSelectionActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                }
                } else {
                    Toast.makeText(this, jsonObjData.getString("msg"), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, jsonObjError.getString("errMsg"), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setUpProgressDialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait..");
        progressDialog.show();
    }

    ////////////////// G+ LOGIN CODE BEING ////////////

    @Override
    public void onConnected(Bundle bundle) {
        //Log.v("connected", "connected");
        setUpProgressDialog();
        mGoogleApiClient.connect();
        if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
            Person currentPerson = Plus.PeopleApi
                    .getCurrentPerson(mGoogleApiClient);
            // Toast.makeText(this, "name: " + currentPerson.getDisplayName(), Toast.LENGTH_LONG).show();
            email = Plus.AccountApi.getAccountName(mGoogleApiClient);
            name = currentPerson.getDisplayName();
            oauthId = currentPerson.getId();
            gender = String.valueOf(currentPerson.getGender());
            dpUrl = currentPerson.getImage().getUrl();
            socialLoginType = "gp";
            _pref.setSession(ConstantClass.TAG_LOGIN_TYPE, socialLoginType);
            setRequestParam(email, oauthId, name, gender, dpUrl);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        //mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (!mIntentInProgress && connectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                startIntentSenderForResult(connectionResult.getResolution()
                        .getIntentSender(), RC_SIGN_IN, null, 0, 0, 0);
                CONNECTION_FLAG = true;
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    ////////////////// G+ LOGIN CDE END /////////////////

    ////////////////// FB LOGIN CODE BEGIN ///////////////////

    OnLoginListener fbLoginListener = new OnLoginListener() {
        @Override
        public void onLogin(String s, List<Permission> permissions, List<Permission> permissions2) {
            mSimpleFacebook.getProfile(properties, new OnProfileListener() {
                @Override
                public void onComplete(Profile response) {
                    super.onComplete(response);
                     /*Toast.makeText(LandingActivity.this, "email: " + response.getEmail(),
                            Toast.LENGTH_LONG).show();*/
                    email = response.getEmail();
                    name = response.getFirstName() + " " + response.getLastName();
                    oauthId = response.getId();
                    gender = response.getGender();
                    dpUrl = "http://graph.facebook.com/" + response.getId()
                            + "/picture?type=large";
                    socialLoginType = "fb";
                    _pref.setSession(ConstantClass.TAG_LOGIN_TYPE,socialLoginType);
                    setRequestParam(email, oauthId, name, gender, dpUrl);
                    setUpProgressDialog();
                }

                @Override
                public void onException(Throwable throwable) {
                    super.onException(throwable);
                }

                @Override
                public void onFail(String reason) {
                    super.onFail(reason);
                }

                @Override
                public void onThinking() {
                    super.onThinking();
                }
            });
        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onException(Throwable throwable) {

        }

        @Override
        public void onFail(String s) {

        }
    };

    ///////////////// FB LOGIN CODE END //////////////////
}
