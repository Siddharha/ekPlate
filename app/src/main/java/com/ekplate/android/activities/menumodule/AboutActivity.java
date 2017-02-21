package com.ekplate.android.activities.menumodule;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ekplate.android.R;
import com.ekplate.android.config.BaseActivity;
import com.ekplate.android.utils.NetworkConnectionCheck;

public class AboutActivity extends BaseActivity {

    private Toolbar tbAbout;
    private LinearLayout llAboutTermsCondition;
    private WebView wvAboutEkplate;
    private RelativeLayout rlProgressBarContainerAbout;
    private NetworkConnectionCheck _connectionCheck;
    private LinearLayout llErrorNetConnectionAbout;
    private ImageView ivLoaderErrorOne;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        getSupportActionBar().hide();
        getWindow().setBackgroundDrawable(null);
        setUpToolBar();
        initialize();
        onClick();
    }

    private void initialize(){
        llAboutTermsCondition = (LinearLayout) findViewById(R.id.llAboutTermsCondition);
        wvAboutEkplate = (WebView) findViewById(R.id.wvAboutEkplate);
        rlProgressBarContainerAbout = (RelativeLayout) findViewById(R.id.rlProgressBarContainerAbout);
        _connectionCheck = new NetworkConnectionCheck(this);
        llErrorNetConnectionAbout = (LinearLayout) findViewById(R.id.llErrorNetConnectionAbout);
        ivLoaderErrorOne = (ImageView) findViewById(R.id.ivLoaderErrorOne);
    }

    private void setUpToolBar(){
        tbAbout = (Toolbar) findViewById(R.id.tbAbout);
        tbAbout.setNavigationIcon(R.drawable.ic_action_back);
        tbAbout.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void setUpAboutEkplateContent(){
        wvAboutEkplate.getSettings().setJavaScriptEnabled(true);
        wvAboutEkplate.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        final Activity activity = this;

        wvAboutEkplate.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                Toast.makeText(activity, description, Toast.LENGTH_SHORT).show();
            }
        });
        wvAboutEkplate.loadUrl("http://api.ekplate.com/page/about-ekplate");
        wvAboutEkplate.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                rlProgressBarContainerAbout.setVisibility(View.GONE);
                wvAboutEkplate.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                rlProgressBarContainerAbout.setVisibility(View.VISIBLE);
                wvAboutEkplate.setVisibility(View.GONE);
            }
        });
    }

    private void onClick(){
        llAboutTermsCondition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AboutActivity.this, TermsConditionsActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        ivLoaderErrorOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _connectionCheck.getNetworkActiveAlert().show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (_connectionCheck.isNetworkAvailable()) {
            llErrorNetConnectionAbout.setVisibility(View.GONE);
            rlProgressBarContainerAbout.setVisibility(View.VISIBLE);
            setUpAboutEkplateContent();
        } else {
            llErrorNetConnectionAbout.setVisibility(View.VISIBLE);
            rlProgressBarContainerAbout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
