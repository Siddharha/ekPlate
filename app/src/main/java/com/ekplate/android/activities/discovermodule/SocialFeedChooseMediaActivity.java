package com.ekplate.android.activities.discovermodule;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ekplate.android.R;
import com.ekplate.android.config.BaseActivity;

/**
 * Created by Avishek on 10/6/2016.
 */
public class SocialFeedChooseMediaActivity extends BaseActivity {
    LinearLayout llFbFeed,llTTFeed,llIGFeed;
    private String[] tabHeaderTitle = {"FaceBook", "Twitter", "Instagram"};
    private ViewPager pager;
    private SocialFeedPagerAdapter socialFeedPagerAdapter;
    private TabLayout taskTab;
    private Toolbar tbSocialFeed;
    private TextView toolbarHeaderText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.media_feed_chooser_layour);
        getWindow().setBackgroundDrawable(null);
        getSupportActionBar().hide();
        initialize();
        setUpToolbar();
    }

    private void setUpToolbar() {
        toolbarHeaderText = (TextView) findViewById(R.id.toolbarHeaderText);
        tbSocialFeed = (Toolbar) findViewById(R.id.tbSocialFeed);
        // tbSocialFeed.inflateMenu(R.menu.menu_social_feed);
        tbSocialFeed.setNavigationIcon(R.drawable.ic_action_back);
        toolbarHeaderText.setText("SOCIAL FEED");
        tbSocialFeed.setBackground(getResources().getDrawable(R.drawable.toolbar_bg));
        tbSocialFeed.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }



    private void onClick() {
        llFbFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), SocialFeedActivity.class);
                intent.putExtra("mediaChannel", 1);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        llTTFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), SocialFeedActivity.class);
                intent.putExtra("mediaChannel", 2);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        llIGFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), SocialFeedActivity.class);
                intent.putExtra("mediaChannel",3);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    private void initialize() {
        llFbFeed = (LinearLayout) findViewById(R.id.llFbFeed);
         llTTFeed = (LinearLayout) findViewById(R.id.llTTFeed);
         llIGFeed = (LinearLayout) findViewById(R.id.llIGFeed);
        taskTab = (TabLayout)findViewById(R.id.taskTab);
        pager = (ViewPager)findViewById(R.id.content_pager_frame);
        socialFeedPagerAdapter = new SocialFeedPagerAdapter(getSupportFragmentManager(),
                this, tabHeaderTitle);
        pager.setOffscreenPageLimit(tabHeaderTitle.length-1);
        pager.setAdapter(socialFeedPagerAdapter);
        taskTab.setupWithViewPager(pager);
    }
}
