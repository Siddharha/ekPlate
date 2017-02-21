package com.ekplate.android.activities.menumodule;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ekplate.android.R;
import com.ekplate.android.appinterfaces.BackgroundActionInterface;
import com.ekplate.android.config.BaseActivity;
import com.ekplate.android.utils.CallServiceAction;
import com.ekplate.android.utils.ConstantClass;
import com.ekplate.android.utils.NetworkConnectionCheck;
import com.ekplate.android.utils.Pref;

import org.json.JSONObject;

public class ChangePasswordActivity extends BaseActivity implements BackgroundActionInterface {

    private Toolbar tbChangePassword;
    private TextView toolbarHeaderText;
    private Pref _pref;
    private NetworkConnectionCheck _connection;
    private CallServiceAction _serviceAction;
    private ProgressDialog progressDialog;
    private EditText etOldPassword, etNewPassword, etConFirmPassword;
    private LinearLayout llOldPass;
    private Button btnChangePasswordSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        getSupportActionBar().hide();
        getWindow().setBackgroundDrawable(null);
        setUpToolBar();
        initialize();
        setVisualForPasswordLayout();
        onClick();
    }

    private void setVisualForPasswordLayout() {

        if(_pref.getSession(ConstantClass.TAG_LOGIN_TYPE).equals(""))
        {
            llOldPass.setVisibility(View.VISIBLE);
        }
        else
        {
            llOldPass.setVisibility(View.GONE);
        }
    }

    private void setUpToolBar(){
        tbChangePassword = (Toolbar) findViewById(R.id.tbChangePassword);
        toolbarHeaderText = (TextView) findViewById(R.id.toolbarHeaderText);
        toolbarHeaderText.setText("CHANGE PASSWORD");
        tbChangePassword.setBackground(getResources().getDrawable(R.drawable.toolbar_bg));
        tbChangePassword.setNavigationIcon(R.drawable.ic_action_back);
        tbChangePassword.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void initialize(){
        etOldPassword = (EditText) findViewById(R.id.etOldPassword);
        etNewPassword = (EditText) findViewById(R.id.etNewPassword);
        etConFirmPassword = (EditText) findViewById(R.id.etConFirmPassword);
        btnChangePasswordSubmit = (Button) findViewById(R.id.btnChangePasswordSubmit);
        llOldPass = (LinearLayout)findViewById(R.id.llOldPass);
        _pref = new Pref(this);
        _connection = new NetworkConnectionCheck(this);
        _serviceAction = new CallServiceAction(this);
        _serviceAction.actionInterface = ChangePasswordActivity.this;
    }

    private void onClick(){
        btnChangePasswordSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (_connection.isNetworkAvailable()) {

                    if(_pref.getSession(ConstantClass.TAG_LOGIN_TYPE).equals(""))
                    {
                    if (etOldPassword.getText().toString().equalsIgnoreCase("")) {
                        Toast.makeText(ChangePasswordActivity.this, "Please enter the old password.", Toast.LENGTH_LONG).show();
                    }

                    else if (etNewPassword.getText().toString().equalsIgnoreCase("")) {
                        Toast.makeText(ChangePasswordActivity.this, "Please enter the new password.", Toast.LENGTH_LONG).show();
                    } else if (etNewPassword.getText().toString().length() < 6) {
                        Toast.makeText(ChangePasswordActivity.this, "Please enter at least 6 character long password.",
                                Toast.LENGTH_LONG).show();
                    } else if (etConFirmPassword.getText().toString().equalsIgnoreCase("")) {
                        Toast.makeText(ChangePasswordActivity.this, "Please enter the confirm password.", Toast.LENGTH_LONG).show();
                    } else if (!etNewPassword.getText().toString().equalsIgnoreCase(etConFirmPassword.getText().toString())) {
                        Toast.makeText(ChangePasswordActivity.this, "New and confirm passwords are different.", Toast.LENGTH_LONG).show();
                    } else {
                        setUpProgressDialog();
                        setInputParamForChangePassword(etOldPassword.getText().toString(),
                                etNewPassword.getText().toString());
                    }} else if (etNewPassword.getText().toString().equalsIgnoreCase("")) {
                        Toast.makeText(ChangePasswordActivity.this, "Please enter the new password.", Toast.LENGTH_LONG).show();
                    } else if (etNewPassword.getText().toString().length() < 6) {
                        Toast.makeText(ChangePasswordActivity.this, "Please enter at least 6 character long password.",
                                Toast.LENGTH_LONG).show();
                    } else if (etConFirmPassword.getText().toString().equalsIgnoreCase("")) {
                        Toast.makeText(ChangePasswordActivity.this, "Please enter the confirm password.", Toast.LENGTH_LONG).show();
                    } else if (!etNewPassword.getText().toString().equalsIgnoreCase(etConFirmPassword.getText().toString())) {
                        Toast.makeText(ChangePasswordActivity.this, "New and confirm passwords are different.", Toast.LENGTH_LONG).show();
                    } else {
                        setUpProgressDialog();
                        setInputParamForChangePassword(etOldPassword.getText().toString(),
                                etNewPassword.getText().toString());
                    }
                } else {
                    _connection.getNetworkActiveAlert().show();
                }
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onStarted() {

    }

    @Override
    public void onCompleted(JSONObject response) {
        Log.e("response", response.toString());
        try {
            JSONObject errorNodeJsonObj = response.getJSONObject("errNode");
            if (errorNodeJsonObj.getInt("errCode") == 0) {
                JSONObject dataJsonObject = response.getJSONObject("data");
                if (dataJsonObject.getBoolean("success")) {
                    Toast.makeText(ChangePasswordActivity.this, dataJsonObject.getString("msg"), Toast.LENGTH_LONG).show();
                    onBackPressed();
                } else {
                    Toast.makeText(ChangePasswordActivity.this, dataJsonObject.getString("msg"), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(ChangePasswordActivity.this, errorNodeJsonObj.getString("errMsg"), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        progressDialog.dismiss();
    }

    private void setInputParamForChangePassword(String oldPassword, String newPassword){


        try {
            JSONObject parentJsonObj = new JSONObject();
            JSONObject childJsonObj = new JSONObject();
            childJsonObj.put("accesstoken", _pref.getSession(ConstantClass.ACCESS_TOKEN));
            childJsonObj.put("old_password", oldPassword);
            childJsonObj.put("new_password", newPassword);
            childJsonObj.put("loginType", _pref.getSession(ConstantClass.TAG_LOGIN_TYPE));
            parentJsonObj.put("data", childJsonObj);
            Log.e("input data", parentJsonObj.toString());
            _serviceAction.requestVersionApi(parentJsonObj, "change-password");
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
