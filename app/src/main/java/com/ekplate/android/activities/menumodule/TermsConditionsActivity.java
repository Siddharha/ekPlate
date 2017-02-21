package com.ekplate.android.activities.menumodule;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ekplate.android.R;
import com.ekplate.android.config.BaseActivity;
import com.ekplate.android.utils.NetworkConnectionCheck;


public class TermsConditionsActivity extends BaseActivity {

    private Toolbar tbTermsCondition;
    private TextView toolbarHeaderText;
    private WebView wvTermsCondition;
    private LinearLayout llErrorNetConnectionTermsCondition;
    private RelativeLayout rlProgressBarContainerTermsCondition;
    private NetworkConnectionCheck _connectionCheck;
    private ImageView ivLoaderErrorOne;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_conditions);
        getSupportActionBar().hide();
        setUpToolBar();
        initialize();
        onClick();
    }

    private void setUpToolBar(){
        tbTermsCondition = (Toolbar) findViewById(R.id.tbTermsCondition);
        toolbarHeaderText = (TextView) findViewById(R.id.toolbarHeaderText);
        toolbarHeaderText.setText("Terms & Conditions");
        tbTermsCondition.setBackground(getResources().getDrawable(R.drawable.toolbar_bg));
        tbTermsCondition.setNavigationIcon(R.drawable.ic_action_back);
        tbTermsCondition.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void initialize(){
        wvTermsCondition = (WebView) findViewById(R.id.wvTermsCondition);
        llErrorNetConnectionTermsCondition = (LinearLayout) findViewById(R.id.llErrorNetConnectionTermsCondition);
        rlProgressBarContainerTermsCondition = (RelativeLayout) findViewById(R.id.rlProgressBarContainerTermsCondition);
        _connectionCheck = new NetworkConnectionCheck(this);
        ivLoaderErrorOne = (ImageView) findViewById(R.id.ivLoaderErrorOne);
    }

    private void setUpTermsConditionContent(){
        wvTermsCondition.getSettings().setJavaScriptEnabled(true);
        final Activity activity = this;

        wvTermsCondition.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                Toast.makeText(activity, description, Toast.LENGTH_SHORT).show();
            }
        });
        wvTermsCondition.loadUrl("http://api.ekplate.com/page/terms-conditions");
        wvTermsCondition.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                rlProgressBarContainerTermsCondition.setVisibility(View.GONE);
                wvTermsCondition.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                rlProgressBarContainerTermsCondition.setVisibility(View.VISIBLE);
                wvTermsCondition.setVisibility(View.GONE);
            }
        });
    }

    private void onClick(){
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
            rlProgressBarContainerTermsCondition.setVisibility(View.VISIBLE);
            llErrorNetConnectionTermsCondition.setVisibility(View.GONE);
            setUpTermsConditionContent();
        } else {
            rlProgressBarContainerTermsCondition.setVisibility(View.GONE);
            llErrorNetConnectionTermsCondition.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
