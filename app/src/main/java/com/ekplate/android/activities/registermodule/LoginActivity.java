package com.ekplate.android.activities.registermodule;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
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


public class LoginActivity extends BaseActivity implements BackgroundActionInterface,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private LinearLayout llResetPasswordLogin, llLoginGoToRegister;
    private FrameLayout btnFbLoginScreen, btnGpLoginScreen;
    private CardView cvLogin;
    private EditText etSignInEmailid, etSignInPassword;
    private Pref _pref;
    private Boolean CONNECTION_FLAG = false;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setBackgroundDrawable(null);
        getSupportActionBar().hide();
        initialize();
        onClick();
    }

    private void initialize(){
        llResetPasswordLogin=(LinearLayout) findViewById(R.id.llResetPasswordLogin);
        llLoginGoToRegister=(LinearLayout) findViewById(R.id.llLoginGoToRegister);
        cvLogin = (CardView) findViewById(R.id.cvLogin);
        etSignInEmailid = (EditText) findViewById(R.id.etSignInEmailid);
        etSignInPassword = (EditText) findViewById(R.id.etSignInPassword);
        btnFbLoginScreen = (FrameLayout) findViewById(R.id.flFbLoginBtnLanding);
        btnGpLoginScreen = (FrameLayout) findViewById(R.id.gpFbLoginBtnLanding);
        _pref = new Pref(this);
        _connection = new NetworkConnectionCheck(this);
        _serviceAction = new CallServiceAction(this);
        _serviceAction.actionInterface = LoginActivity.this;
        _commonFunction = new CommonFunction(this);
        _commonMethods = new CommonMethods(this);
        properties = new Profile.Properties.Builder()
                .add(Profile.Properties.EMAIL)
                .add(Profile.Properties.ID)
                .add(Profile.Properties.FIRST_NAME)
                .add(Profile.Properties.LAST_NAME)
                .add(Profile.Properties.PICTURE)
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(LoginActivity.this)
                .addConnectionCallbacks(LoginActivity.this)
                .addOnConnectionFailedListener(LoginActivity.this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();
    }

    private void onClick(){
        llResetPasswordLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            startActivity(new Intent(LoginActivity.this, ForgetPasswordActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        llLoginGoToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        cvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(_connection.isNetworkAvailable()){
                    signInAction();
                } else {
                    _connection.getNetworkActiveAlert().show();
                }
            }
        });

        btnFbLoginScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSimpleFacebook.login(fbLoginListener);
            }
        });

        btnGpLoginScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGoogleApiClient.connect();
            }
        });
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
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void signInAction(){
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        if(etSignInEmailid.getText().toString().equals("")){
            Toast.makeText(this, "Please give the email id/ mobile no", Toast.LENGTH_LONG).show();
        } /*else if(etSignInEmailid.getText().toString().indexOf("@") == -1 &&
                etSignInEmailid.getText().toString().indexOf(".") == -1){
            Toast.makeText(this, "Please give correct email id", Toast.LENGTH_LONG).show();
        } */else if(etSignInPassword.getText().toString().equals("")){
            Toast.makeText(this, "Please give password", Toast.LENGTH_LONG).show();
        } else if(etSignInPassword.getText().toString().length() < 6){
            Toast.makeText(this, "Please give 6 character password", Toast.LENGTH_LONG).show();
        }
        else{
            setRequestParam(etSignInEmailid.getText().toString(),
                    etSignInPassword.getText().toString());
        }
    }

    private void setRequestParam(String email, String password){
        try {
            JSONObject jsonObjInnerParams = new JSONObject();
            jsonObjInnerParams.put("emailId", email);
            jsonObjInnerParams.put("password", password);
            jsonObjInnerParams.put("deviceType", "android");
            jsonObjInnerParams.put("deviceId", _commonFunction.getGCMRegistrationId());
            jsonObjInnerParams.put("latitude", _pref.getSession(ConstantClass.TAG_LATITUDE));
            jsonObjInnerParams.put("longitude", _pref.getSession(ConstantClass.TAG_LONGITUDE));
            JSONObject jsonObjParams = new JSONObject();
            jsonObjParams.put("data", jsonObjInnerParams);
            Log.e("login param", jsonObjParams.toString());
            setUpProgressDialog();
            _serviceAction.requestVersionApi(jsonObjParams, "login");
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
            Log.e("login data", jsonObjParams.toString());
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
        Log.e("login response", response.toString());
        try{
            JSONObject jsonObjError = response.getJSONObject("errNode");
            if(jsonObjError.getInt("errCode") == 0) {
                JSONObject jsonObjData = response.getJSONObject("data");
                if(jsonObjData.getBoolean("success")){
                    Toast.makeText(this, jsonObjData.getString("msg"), Toast.LENGTH_LONG).show();
                    _pref.setSession(ConstantClass.ACCESS_TOKEN,
                            jsonObjData.getString(ConstantClass.ACCESS_TOKEN));
                    _pref.setSession(ConstantClass.TAG_SELECTED_FOOD_TYPE, jsonObjData.getString("foodType"));
                    _pref.setSession(ConstantClass.TAG_USER_NAME, jsonObjData.getString("useName"));
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
                    Intent intent = new Intent(LoginActivity.this, VegetarianSelectionActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|
                            Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                } else {
                    Toast.makeText(this, jsonObjData.getString("msg"), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, jsonObjError.getString("errMsg"), Toast.LENGTH_LONG).show();
            }
            progressDialog.dismiss();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setUpProgressDialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait..");
        progressDialog.show();
    }

    /////////////// G+ LOGIN CODE START //////////////

    @Override
    public void onConnected(Bundle bundle) {
        Log.v("connected", "connected");
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
            _pref.setSession(ConstantClass.TAG_LOGIN_TYPE,socialLoginType);
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

    /////////////////// G+ LOGIN CODE END //////////////////////

    ////////////////// FB LOGIN CODE BEGIN ////////////////////

    OnLoginListener fbLoginListener = new OnLoginListener() {
        @Override
        public void onLogin(String s, List<Permission> permissions, List<Permission> permissions2) {
            mSimpleFacebook.getProfile(properties, new OnProfileListener() {
                @Override
                public void onComplete(Profile response) {
                    super.onComplete(response);
                    Log.v("response", response.toString());
                    /*Toast.makeText(LoginActivity.this, "Sign Up user: " + response.getName(),
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
