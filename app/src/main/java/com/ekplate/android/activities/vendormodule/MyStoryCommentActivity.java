package com.ekplate.android.activities.vendormodule;

import android.app.ProgressDialog;
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

public class MyStoryCommentActivity extends BaseActivity implements BackgroundActionInterface {

    private Toolbar tbMyStoryComment;
    private TextView toolbarHeaderText, tvNameOfUser;
    private EditText etCommentMyStory;
    private Pref _pref;
    private NetworkConnectionCheck _connection;
    private CallServiceAction _serviceAction;
    private ProgressDialog progressDialog;
    private Button btnSubmitCommentMyStory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_story_comment);
        getSupportActionBar().hide();
        getWindow().setBackgroundDrawable(null);
        setUpToolBar();
        initialize();
        setUpInfoData();
        setUpFont();
        onClick();
    }

    private void setUpToolBar(){
        tbMyStoryComment = (Toolbar) findViewById(R.id.tbMyStoryComment);
        toolbarHeaderText = (TextView) findViewById(R.id.toolbarHeaderText);
        toolbarHeaderText.setText("COMMENT");
        tbMyStoryComment.setBackground(getResources().getDrawable(R.drawable.toolbar_bg));
        tbMyStoryComment.setNavigationIcon(R.drawable.ic_action_back);
        tbMyStoryComment.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void initialize(){
        tvNameOfUser = (TextView) findViewById(R.id.tvNameOfUser);
        etCommentMyStory = (EditText) findViewById(R.id.etCommentMyStory);
        btnSubmitCommentMyStory = (Button) findViewById(R.id.btnSubmitCommentMyStory);
        _pref = new Pref(this);
        _connection = new NetworkConnectionCheck(this);
        _serviceAction = new CallServiceAction(this);
        _serviceAction.actionInterface = MyStoryCommentActivity.this;
    }

    private void onClick(){
        btnSubmitCommentMyStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (_connection.isNetworkAvailable()) {
                    if (etCommentMyStory.getText().toString().equalsIgnoreCase("")) {
                        Toast.makeText(MyStoryCommentActivity.this, "Comment fiend should not be empty.", Toast.LENGTH_LONG).show();
                    } else {
                        setUpProgressDialog();
                        setInputParamForComment(etCommentMyStory.getText().toString());
                    }
                } else {
                    _connection.getNetworkActiveAlert().show();
                }
            }
        });
    }

    private void setUpInfoData(){
        tvNameOfUser.setText(_pref.getSession(ConstantClass.TAG_USER_NAME));
    }

    private void setUpFont(){
        Typeface fontBariolLightItalic = Typeface.createFromAsset(getAssets(), "Bariol_Light_Italic.otf");
        etCommentMyStory.setTypeface(fontBariolLightItalic);
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
        Log.e("response Comment", response.toString());
        try {
            JSONObject jsonObjError = response.getJSONObject("errNode");
            if (jsonObjError.getInt("errCode") == 0) {
                JSONObject jsonObjData = response.getJSONObject("data");
                if (jsonObjData.getBoolean("success")) {
                    Toast.makeText(this, jsonObjData.getString("msg"), Toast.LENGTH_LONG).show();
                    jsonObjData.getString("commentCount");
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

    private void setInputParamForComment(String commentStr){
        try {
            JSONObject parentJsonObj = new JSONObject();
            JSONObject childJsonObj = new JSONObject();
            childJsonObj.put("accessToken", _pref.getSession(ConstantClass.ACCESS_TOKEN));
            childJsonObj.put("vendorId", getIntent().getExtras().getInt("vendorId"));
            childJsonObj.put("storyId", getIntent().getExtras().getInt("storyId"));
            childJsonObj.put("commentDetails", commentStr);
            parentJsonObj.put("data", childJsonObj);
            Log.e("input data", parentJsonObj.toString());
            _serviceAction.requestVersionApi(parentJsonObj, "story-comment");
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
}
