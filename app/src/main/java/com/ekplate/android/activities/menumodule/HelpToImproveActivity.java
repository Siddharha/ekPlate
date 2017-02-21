package com.ekplate.android.activities.menumodule;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class HelpToImproveActivity extends BaseActivity implements BackgroundActionInterface {

    private Toolbar tbHelpToImprove;
    private TextView toolbarHeaderText;
    private EditText etSuggestions;
    private Button btnSubmitSuggestion;
    private Pref _pref;
    private NetworkConnectionCheck _connection;
    private CallServiceAction _serviceAction;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_to_improve);
        getSupportActionBar().hide();
        getWindow().setBackgroundDrawable(null);
        initialize();
        setUpToolBar();
        setUpFont();
    }

    private void setUpToolBar(){
        tbHelpToImprove = (Toolbar) findViewById(R.id.tbHelpToImprove);
        toolbarHeaderText = (TextView) findViewById(R.id.toolbarHeaderText);
        toolbarHeaderText.setText("HELP TO IMPROVE");
        tbHelpToImprove.setBackground(getResources().getDrawable(R.drawable.toolbar_bg));
        tbHelpToImprove.setNavigationIcon(R.drawable.ic_action_back);
        tbHelpToImprove.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void initialize(){
        etSuggestions = (EditText) findViewById(R.id.etSuggestions);
        btnSubmitSuggestion = (Button) findViewById(R.id.btnSubmitSuggestion);
        _pref = new Pref(this);
        _connection = new NetworkConnectionCheck(this);
        _serviceAction = new CallServiceAction(this);
        _serviceAction.actionInterface = HelpToImproveActivity.this;
        btnSubmitSuggestion.setOnClickListener(submitSuggestionListener);
    }

    private void setUpFont(){
        Typeface fontBariolLightItalic = Typeface.createFromAsset(getAssets(), "Bariol_Light_Italic.otf");
        etSuggestions.setTypeface(fontBariolLightItalic);
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
        Log.e("suggestion response", response.toString());
        try {
            JSONObject errorNodeJsonObj = response.getJSONObject("errNode");
            if (errorNodeJsonObj.getInt("errCode") == 0) {
                JSONObject dataJsonObject = response.getJSONObject("data");
                if (dataJsonObject.getBoolean("success")){
                    etSuggestions.setText("");
                    Intent intent = new Intent(HelpToImproveActivity.this, QuestionSubmitResultActivity.class);
                    intent.putExtra("screenTypeFlag", "suggestion");
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else {
                    Toast.makeText(HelpToImproveActivity.this, dataJsonObject.getString("msg"), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(HelpToImproveActivity.this, errorNodeJsonObj.getString("errMsg"), Toast.LENGTH_LONG).show();
            }
            progressDialog.dismiss();
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

    private void setInputParamForUserSuggestion(String suggestion){
        try {
            JSONObject parentJsonObj = new JSONObject();
            JSONObject childJsonObj = new JSONObject();
            childJsonObj.put("accessToken", _pref.getSession(ConstantClass.ACCESS_TOKEN));
            childJsonObj.put("suggestionOrComplain", suggestion);
            parentJsonObj.put("data", childJsonObj);
            Log.e("input data", parentJsonObj.toString());
            _serviceAction.requestVersionApi(parentJsonObj, "suggestion-complain");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    View.OnClickListener submitSuggestionListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (_connection.isNetworkAvailable()){
                if(etSuggestions.getText().toString().length()<0) {
                    Toast.makeText(HelpToImproveActivity.this, "The field is empty. Please enter your" +
                            " suggestion or complain.", Toast.LENGTH_LONG).show();
                } else if (etSuggestions.getText().toString().length()<10){
                    Toast.makeText(HelpToImproveActivity.this, "Please enter your" +
                            " suggestion or complain with 10 characters at least.", Toast.LENGTH_LONG).show();
                } else if (etSuggestions.getText().toString().length()>1000) {
                    Toast.makeText(HelpToImproveActivity.this, "Please enter your" +
                            " suggestion or complain within 1000 characters.", Toast.LENGTH_LONG).show();
                } else {
                    setUpProgressDialog();
                    setInputParamForUserSuggestion(etSuggestions.getText().toString());
                }
            } else {
                _connection.getNetworkActiveAlert().show();
            }
        }
    };
}
