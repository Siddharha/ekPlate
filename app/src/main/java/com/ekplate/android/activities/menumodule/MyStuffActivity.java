package com.ekplate.android.activities.menumodule;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.ekplate.android.R;
import com.ekplate.android.adapters.menumodule.MyStuffPagerAdapter;
import com.ekplate.android.config.BaseActivity;


public class MyStuffActivity extends BaseActivity {

    private Toolbar tbMyStuff;
    private TextView toolbarHeaderText;
    private TabLayout tabLayoutMyStuff;
    private MyStuffPagerAdapter myStuffPagerAdapter;
    private ViewPager vpMyStuff;
    private String[] TAB_TITLE = {"My College", "My Bookmarks", "My Images"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_stuff);
        getWindow().setBackgroundDrawable(null);
        getSupportActionBar().hide();
        setUpToolbar();
        setUpMyStuffTabs();
        setUpMyStuffTabs();
    }

    private void setUpToolbar(){
        tbMyStuff = (Toolbar) findViewById(R.id.tbMyStuff);
        toolbarHeaderText = (TextView) findViewById(R.id.toolbarHeaderText);
        tbMyStuff.setNavigationIcon(R.drawable.ic_action_back);
        toolbarHeaderText.setText("MY STUFF");
        tbMyStuff.setBackground(getResources().getDrawable(R.drawable.toolbar_bg));
        tbMyStuff.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setUpMyStuffTabs(){
        tabLayoutMyStuff = (TabLayout) findViewById(R.id.tabLayoutMyStuff);
        myStuffPagerAdapter = new MyStuffPagerAdapter(getSupportFragmentManager(), TAB_TITLE);
        vpMyStuff = (ViewPager) findViewById(R.id.vpMyStuff);
        vpMyStuff.setAdapter(myStuffPagerAdapter);
        tabLayoutMyStuff.setupWithViewPager(vpMyStuff);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
