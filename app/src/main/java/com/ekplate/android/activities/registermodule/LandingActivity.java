package com.ekplate.android.activities.registermodule;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.ekplate.android.R;
import com.ekplate.android.appinterfaces.BackgroundActionInterface;
import com.ekplate.android.config.BaseActivity;
import com.ekplate.android.utils.CallServiceAction;
import com.ekplate.android.utils.CommonFunction;
import com.ekplate.android.utils.CommonMethods;
import com.ekplate.android.utils.ConstantClass;
import com.ekplate.android.utils.NetworkConnectionCheck;
import com.ekplate.android.utils.Pref;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
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

import java.util.List;


public class LandingActivity extends BaseActivity
        implements ConnectionCallbacks, OnConnectionFailedListener, BackgroundActionInterface {

    private static final int REQUEST_CODE_ASK_PERMISSIONS = 1;
    private CardView cvSignIn, cvRegister;
    private FrameLayout flFbLoginBtnLanding, flGpLoginBtnLanding;
    private SimpleFacebook mSimpleFacebook;
    private static final int RC_SIGN_IN = 0;
    private Boolean CONNECTION_FLAG = false;
    private static GoogleApiClient mGoogleApiClient;
    private boolean mIntentInProgress;
    private Pref _pref;
    private NetworkConnectionCheck _connection;
    private CallServiceAction _serviceAction;
    private ProgressDialog progressDialog;
    private ConnectionResult mConnectionResult;
    private boolean signedInUser;
    private String email, name, oauthId, gender, mobileNo, dpUrl,socialLoginType;
    private Profile.Properties properties;
    private CommonFunction _commonFunction;
    private CommonMethods _commonMethods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        getWindow().setBackgroundDrawable(null);
        getSupportActionBar().hide();
        initialize();
        onClick();
        Log.e("device id", "==>" + _commonFunction.getGCMRegistrationId());
    }

    private void initialize() {
        cvSignIn = (CardView) findViewById(R.id.cvSignIn);
        cvRegister = (CardView) findViewById(R.id.cvRegister);
        flFbLoginBtnLanding = (FrameLayout) findViewById(R.id.flFbLoginBtnLanding);
        flGpLoginBtnLanding = (FrameLayout) findViewById(R.id.gpFbLoginBtnLanding);
        _pref = new Pref(this);
        _connection = new NetworkConnectionCheck(this);
        _serviceAction = new CallServiceAction(this);
        _serviceAction.actionInterface = LandingActivity.this;
        _commonFunction = new CommonFunction(this);
        _commonMethods = new CommonMethods(this);
        properties = new Profile.Properties.Builder()
                .add(Profile.Properties.EMAIL)
                .add(Profile.Properties.ID)
                .add(Profile.Properties.FIRST_NAME)
                .add(Profile.Properties.LAST_NAME)
                .add(Profile.Properties.PICTURE)
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(LandingActivity.this)
                .addConnectionCallbacks(LandingActivity.this)
                .addOnConnectionFailedListener(LandingActivity.this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();

    }

    private void onClick() {
        cvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                _pref.setSession(ConstantClass.TAG_LOGIN_TYPE,"");
                        startActivity(new Intent(LandingActivity.this, LoginActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        cvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LandingActivity.this, RegisterActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        flFbLoginBtnLanding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (_connection.isNetworkAvailable()){
                    mSimpleFacebook.login(fbLoginListener);
                } else {
                    _connection.getNetworkActiveAlert().show();
                }
            }
        });

        flGpLoginBtnLanding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    int hasWriteContactsPermission = checkSelfPermission(Manifest.permission.GET_ACCOUNTS);
                    if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[] {Manifest.permission.GET_ACCOUNTS},
                                REQUEST_CODE_ASK_PERMISSIONS);
                        return;
                    }else {
                        if (_connection.isNetworkAvailable()) {
                            mGoogleApiClient.connect();
                        } else {
                            _connection.getNetworkActiveAlert().show();
                        }
                    }
                }else {
                    if (_connection.isNetworkAvailable()) {
                        mGoogleApiClient.connect();
                    } else {
                        _connection.getNetworkActiveAlert().show();
                    }
                }
                
                

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    if (_connection.isNetworkAvailable()) {
                        mGoogleApiClient.connect();
                    } else {
                        _connection.getNetworkActiveAlert().show();
                    }
                } else {
                    // Permission Denied
                    Toast.makeText(this,"Can't login due to Permission issue! please Grand GET ACCOUNT Permission.",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        mSimpleFacebook = SimpleFacebook.getInstance(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
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
    public void onBackPressed() {
        mGoogleApiClient.disconnect();
        super.onBackPressed();
    }

    ////////////////// FB LOGIN CODE BEGIN ///////////////////

    OnLoginListener fbLoginListener = new OnLoginListener() {
        @Override
        public void onLogin(String s, List<Permission> permissions, List<Permission> permissions2) {
            mSimpleFacebook.getProfile(properties, new OnProfileListener() {
                @Override
                public void onComplete(Profile response) {
                    super.onComplete(response);
                    Log.v("response", response.toString());
                    /*Toast.makeText(LandingActivity.this, "email: " + response.getEmail(),
                            Toast.LENGTH_LONG).show();*/

                    FacebookSdk.sdkInitialize(getApplicationContext());

                    String id = response.getId();
                    Log.v("id", id);
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
            progressDialog.dismiss();
        }

        @Override
        public void onException(Throwable throwable) {

        }

        @Override
        public void onFail(String s) {

        }
    };

    ///////////////// FB LOGIN CODE END //////////////////

    ///////////////// G+ LOGIN CODE BEGIN ////////////////


    @Override
    public void onConnected(Bundle bundle) {
        Log.v("connected", "connected");
        setUpProgressDialog();
            mGoogleApiClient.connect();
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi
                        .getCurrentPerson(mGoogleApiClient);
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
        Log.e("onConnectionSuspended", "onConnectionSuspended");
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
                //mGoogleApiClient.connect();
            }
        }
    }

    /////////////////// G+ LOGIN CODE END ////////////////////

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
            Log.e("oauth login parameter", jsonObjParams.toString());
            //Toast.makeText(LandingActivity.this, jsonObjParams.toString(), Toast.LENGTH_LONG).show();
            _serviceAction.requestVersionApi(jsonObjParams, "oauth-login");
            //_pref.setSession(ConstantClass.TAG_LOGIN_TYPE, "");
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    private void setUpProgressDialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait..");
        progressDialog.show();
    }

    @Override
    public void onStarted() {

    }

    @Override
    public void onCompleted(JSONObject response) {
        Log.e("response", response.toString());
        try{
            JSONObject jsonObjError = response.getJSONObject("errNode");
            if(jsonObjError.getInt("errCode") == 0) {
                JSONObject jsonObjData = response.getJSONObject("data");
                if(jsonObjData.getBoolean("success")){
                    Toast.makeText(this, jsonObjData.getString("msg"), Toast.LENGTH_LONG).show();
                    _pref.setSession(ConstantClass.ACCESS_TOKEN,
                            jsonObjData.getString(ConstantClass.ACCESS_TOKEN));
                    _pref.setSession(ConstantClass.TAG_USER_NAME, jsonObjData.getString("useName"));
                    _pref.setSession(ConstantClass.TAG_SELECTED_FOOD_TYPE,jsonObjData.getString("foodType"));
                    _pref.setSession(ConstantClass.FIRST_USER, "true");
                    Log.e("first user", _pref.getSession(ConstantClass.FIRST_USER));
                    Toast.makeText(LandingActivity.this, "dpUrl ==>>> " + jsonObjData.getString("dpUrl"),
                            Toast.LENGTH_LONG);
                    _pref.setSession(ConstantClass.TAG_USER_DP_IMAGE, jsonObjData.getString("dpUrl"));
                    if(jsonObjData.getString("phoneNo") != null && !jsonObjData.getString("phoneNo").isEmpty()) {
                        _pref.setSession(ConstantClass.TAG_USER_PHONE_NO, jsonObjData.getString("phoneNo"));
                    } else {
                        _pref.setSession(ConstantClass.TAG_USER_PHONE_NO, "");
                    }
                    _pref.setSession(ConstantClass.TAG_USER_EMAIL_ID, jsonObjData.getString("emailId"));
                    _pref.setSession(ConstantClass.TAG_NOTIFICATION_ENABLED, jsonObjData.getString("notificationEnabled"));
                    JSONArray jsonArrayAddedCollege = jsonObjData.getJSONArray("addedCollege");
                    if(jsonArrayAddedCollege.length() > 0){
                        _commonMethods.setAddedColleges(jsonArrayAddedCollege);
                    }
                    Toast.makeText(LandingActivity.this, "dpUrl ==>>> " + _pref.getSession(ConstantClass.TAG_USER_DP_IMAGE), Toast.LENGTH_LONG);
                    Intent intent = new Intent(LandingActivity.this, VegetarianSelectionActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                } else {
                    Toast.makeText(this, jsonObjData.getString("msg"), Toast.LENGTH_LONG).show();
                    mGoogleApiClient.disconnect();
                }
            } else {
                Toast.makeText(this, jsonObjError.getString("errMsg"), Toast.LENGTH_LONG).show();
                mGoogleApiClient.disconnect();
            }
            progressDialog.dismiss();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
