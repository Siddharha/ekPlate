package com.ekplate.android.activities.vendormodule;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.ekplate.android.R;
import com.ekplate.android.activities.socialsharemodule.SocialShareActivity;
import com.ekplate.android.appinterfaces.BackgroundActionInterface;
import com.ekplate.android.config.BaseActivity;
import com.ekplate.android.utils.CallServiceAction;
import com.ekplate.android.utils.ConstantClass;
import com.ekplate.android.utils.NetworkConnectionCheck;
import com.ekplate.android.utils.Pref;

import org.json.JSONObject;

public class MyStoryActivity extends BaseActivity implements BackgroundActionInterface {

    private Toolbar tbMyStory;
    private TextView toolbarHeaderText;
    private LinearLayout llCommentMyStory, llGotoVendorPage, llMyStoryLike;
    private TextView tvNoOfLikesMyStory, tvCommentMyStory;
    private WebView wvMyStoryContent;
    private ProgressBar pbMyStory;
    private int storyId;
    private Pref _pref;
    private NetworkConnectionCheck _connection;
    private CallServiceAction _serviceAction;
    private ProgressDialog progressDialog;
    private String myStoryUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_story);
        getSupportActionBar().hide();
        getWindow().setBackgroundDrawable(null);
        Log.e("story json data", getIntent().getExtras().getString("myStoryDetails"));
        initialize();
        setUpToolBar();
        setUpMyStoryContent();
        onClick();
    }

    private void initialize(){
        llCommentMyStory = (LinearLayout) findViewById(R.id.llCommentMyStory);
        wvMyStoryContent = (WebView) findViewById(R.id.wvMyStoryContent);
        pbMyStory = (ProgressBar) findViewById(R.id.pbMyStory);
        tvNoOfLikesMyStory = (TextView) findViewById(R.id.tvNoOfLikesMyStory);
        tvCommentMyStory = (TextView) findViewById(R.id.tvCommentMyStory);
        llGotoVendorPage = (LinearLayout) findViewById(R.id.llGotoVendorPage);
        llMyStoryLike = (LinearLayout) findViewById(R.id.llMyStoryLike);
        _pref = new Pref(this);
        _connection = new NetworkConnectionCheck(this);
        _serviceAction = new CallServiceAction(this);
        _serviceAction.actionInterface = MyStoryActivity.this;
    }

    private void onClick(){
        llCommentMyStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyStoryActivity.this, MyStoryCommentActivity.class);
                intent.putExtra("vendorId", getIntent().getExtras().getInt("vendorId"));
                intent.putExtra("storyId", storyId);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        llGotoVendorPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        llMyStoryLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (_connection.isNetworkAvailable()) {
                    setUpProgressDialog();
                    setParameterMyStoryLike();
                } else {
                    _connection.getNetworkActiveAlert().show();
                }
            }
        });
    }

    private void setUpToolBar(){
        tbMyStory = (Toolbar) findViewById(R.id.tbMyStory);
        toolbarHeaderText = (TextView) findViewById(R.id.toolbarHeaderText);
        toolbarHeaderText.setText("MY STORY PAGE");
        tbMyStory.setBackground(getResources().getDrawable(R.drawable.toolbar_bg));
        tbMyStory.setNavigationIcon(R.drawable.ic_action_back);
        tbMyStory.inflateMenu(R.menu.menu_my_story);
        tbMyStory.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        tbMyStory.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_share_bookmark) {
                    Intent intent = new Intent(MyStoryActivity.this, SocialShareActivity.class);
                    intent.putExtra("url", myStoryUrl);
                    intent.putExtra("vendor_name", getIntent().getExtras().getString("vendorName"));
                    intent.putExtra("vendor_address", getIntent().getExtras().getString("vendorAddress"));
                    intent.putExtra("route_from", "my_story");
                    startActivity(intent);
                }
                return false;
            }
        });
    }

    private void setUpMyStoryContent(){
        try {
            JSONObject myStoryDetailsJsonObj = new JSONObject(getIntent().getExtras().getString("myStoryDetails"));
            tvNoOfLikesMyStory.setText(myStoryDetailsJsonObj.getString("myStoryLikeCount") + " Likes");
            tvCommentMyStory.setText(myStoryDetailsJsonObj.getString("myStoryCommentCount") + " Comments");
            storyId = myStoryDetailsJsonObj.getInt("myStoryId");
            wvMyStoryContent.getSettings().setJavaScriptEnabled(true);
            myStoryUrl = myStoryDetailsJsonObj.getString("myStoryContentUrl");
            wvMyStoryContent.loadUrl(myStoryUrl);
            wvMyStoryContent.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    pbMyStory.setVisibility(View.GONE);
                    wvMyStoryContent.setVisibility(View.VISIBLE);
                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                    pbMyStory.setVisibility(View.VISIBLE);
                    wvMyStoryContent.setVisibility(View.GONE);
                }
            });
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

    private void setParameterMyStoryLike(){
        try {
            JSONObject parentJsonObj = new JSONObject();
            JSONObject childJsonObj = new JSONObject();
            childJsonObj.put("accessToken", _pref.getSession(ConstantClass.ACCESS_TOKEN));
            childJsonObj.put("vendorId", getIntent().getExtras().getInt("vendorId"));
            childJsonObj.put("storyId", storyId);
            childJsonObj.put("likeStatus", "true");
            parentJsonObj.put("data", childJsonObj);
            Log.e("input data", parentJsonObj.toString());
            _serviceAction.requestVersionApi(parentJsonObj, "story-like");
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
                    tvNoOfLikesMyStory.setText(jsonObjData.getString("noofcount") + " Likes");
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
}
