package com.ekplate.android.activities.discovermodule;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.ekplate.android.R;
import com.ekplate.android.utils.NetworkConnectionCheck;

public class SocialFeedWebActivity extends AppCompatActivity {
    private Toolbar tbSocialFeedWeb;
    private TextView toolbarHeaderText;
    private WebView wvSocialFeed;
    private NetworkConnectionCheck _connection;
    String URL;
    String title;
    public ProgressDialog prDialog;

    @Override
    public void onBackPressed() {
        prDialog.setCancelable(true);
        prDialog.dismiss();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_feed_web);
        getWindow().setBackgroundDrawable(null);
        getSupportActionBar().hide();
        URL = getIntent().getStringExtra("linkurl");
        title = getIntent().getStringExtra("title");
        initialize();
        setUpToolbar();
        loadPage();
    }

    private void loadPage() {
        if(_connection.isNetworkAvailable()) {
           wvSocialFeed.setWebViewClient(new Callback());
            WebSettings w =wvSocialFeed.getSettings();
            wvSocialFeed.requestFocus(View.FOCUS_DOWN);
           wvSocialFeed.setFocusableInTouchMode(true);
            wvSocialFeed.getSettings().setLoadsImagesAutomatically(true);
            wvSocialFeed.getSettings().setJavaScriptEnabled(true);
           wvSocialFeed.setFocusable(true);
           wvSocialFeed.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
           wvSocialFeed.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            w.setDefaultTextEncodingName("utf-8");
           wvSocialFeed.loadUrl(URL);
            // wvFb.loadData(wvFb.data, "text/html; charset=utf-8",null);
        }
        else
        {
            _connection.getNetworkActiveAlert().show();
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void initialize() {

        wvSocialFeed = (WebView) findViewById(R.id.wvSocialFeed);
        _connection = new NetworkConnectionCheck(SocialFeedWebActivity.this);
        
    }

    private void setUpToolbar() {
        tbSocialFeedWeb = (Toolbar) findViewById(R.id.tbSocialFeedWeb);
        toolbarHeaderText = (TextView) findViewById(R.id.toolbarHeaderText);
        //tbSocialFeedWeb.inflateMenu(R.menu.menu_social_feed);
        tbSocialFeedWeb.setNavigationIcon(R.drawable.ic_action_back);
        toolbarHeaderText.setText(title);
        tbSocialFeedWeb.setBackground(getResources().getDrawable(R.drawable.toolbar_bg));
        tbSocialFeedWeb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private class Callback extends WebViewClient {


        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            try{
                prDialog = ProgressDialog.show(SocialFeedWebActivity.this, null, "loading, please wait for completeing...",false,true);
            }catch(WindowManager.BadTokenException e){
                e.printStackTrace();
            }
            //prDialog.setCanceledOnTouchOutside(true);
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return (true);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            try{
                prDialog.dismiss();
            }catch (IllegalArgumentException e){
                e.printStackTrace();
            }
            super.onPageFinished(view, url);
        }
    }

}
