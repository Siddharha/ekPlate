package com.ekplate.android.activities.registermodule;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ekplate.android.R;
import com.ekplate.android.appinterfaces.BackgroundActionInterface;
import com.ekplate.android.config.BaseActivity;
import com.ekplate.android.utils.CallServiceAction;
import com.ekplate.android.utils.NetworkConnectionCheck;
import com.ekplate.android.utils.Pref;

import org.json.JSONObject;


public class ForgetPasswordActivity extends BaseActivity
        implements BackgroundActionInterface {

    private LinearLayout llForgetPasswordGoToRegister;
    private EditText etEmailIdForgetPassword;
    private CardView cvRecoverPassword;
    private ProgressDialog progressDialog;
    private Pref _pref;
    private NetworkConnectionCheck _connection;
    private CallServiceAction _serviceAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        getWindow().setBackgroundDrawable(null);
        getSupportActionBar().hide();
        initialize();
        onClick();
    }

    private void initialize(){
        llForgetPasswordGoToRegister = (LinearLayout) findViewById(R.id.llForgetPasswordGoToRegister);
        etEmailIdForgetPassword = (EditText) findViewById(R.id.etEmailIdForgetPassword);
        cvRecoverPassword = (CardView) findViewById(R.id.cvRecoverPassword);
        _pref = new Pref(this);
        _connection = new NetworkConnectionCheck(this);
        _serviceAction = new CallServiceAction(this);
        _serviceAction.actionInterface = ForgetPasswordActivity.this;
    }

    private void onClick(){
        llForgetPasswordGoToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ForgetPasswordActivity.this, RegisterActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        cvRecoverPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                if (_connection.isNetworkAvailable()) {
                    if (etEmailIdForgetPassword.getText().toString().equals("")) {
                        Toast.makeText(ForgetPasswordActivity.this, "Please give your email id", Toast.LENGTH_LONG).show();
                    } else if (etEmailIdForgetPassword.getText().toString().indexOf("@") == -1 ||
                            etEmailIdForgetPassword.getText().toString().indexOf(".") == -1) {
                        Toast.makeText(ForgetPasswordActivity.this, "Please insert correct email id", Toast.LENGTH_LONG).show();
                    } else {
                        setUpParameter(etEmailIdForgetPassword.getText().toString());
                    }
                } else {
                    _connection.getNetworkActiveAlert().show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void setUpParameter(String emailId){
        try{
            JSONObject jsonObjInnerParams = new JSONObject();
            jsonObjInnerParams.put("uniqueId", emailId);
            JSONObject jsonObjParams = new JSONObject();
            jsonObjParams.put("data", jsonObjInnerParams);
            setUpProgressDialog();
            _serviceAction.requestVersionApi(jsonObjParams, "forgot-password");
        } catch (Exception e){
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
        Log.e("forget password", response.toString());
        progressDialog.dismiss();
        try {
            JSONObject jsonObjError = response.getJSONObject("errNode");
            if (jsonObjError.getInt("errCode") == 0) {
                JSONObject jsonObjData = response.getJSONObject("data");
                if (jsonObjData.getBoolean("success")) {
                    Toast.makeText(ForgetPasswordActivity.this, jsonObjData.getString("msg"), Toast.LENGTH_LONG).show();
                    startActivity(new Intent(ForgetPasswordActivity.this, LandingActivity.class));
                } else {
                    Toast.makeText(ForgetPasswordActivity.this, jsonObjData.getString("msg"), Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
