package com.ekplate.android.activities.homemodule;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ekplate.android.R;
import com.ekplate.android.activities.menumodule.AboutActivity;
import com.ekplate.android.activities.menumodule.ChangeCountryLocationActivity;
import com.ekplate.android.activities.menumodule.DevOptionActivity;
import com.ekplate.android.activities.menumodule.FaqActivity;
import com.ekplate.android.activities.menumodule.HelpToImproveActivity;
import com.ekplate.android.activities.menumodule.NotificationActivity;
import com.ekplate.android.activities.menumodule.UserProfileActivity;
import com.ekplate.android.config.BaseActivity;
import com.ekplate.android.fragments.homemodule.HomeFragment;
import com.ekplate.android.utils.ChangeLocationPopup;
import com.ekplate.android.utils.ConstantClass;
import com.ekplate.android.utils.Pref;
import com.special.ResideMenu.ResideMenu;
import com.special.ResideMenu.ResideMenuItem;

public class HomeResideActivity extends BaseActivity {

    public Toolbar tbHomeReside;
    public ResideMenu resideMenu;
    int devOptionActivator;
    RelativeLayout mMainLayout;
    private String titles[] = { "Change location", "My profile", "FAQ", "About",
            "Notifications", "Help us improve", "Developer"};
    private int icon[] = { R.drawable.icon_location, R.drawable.icon_profile,
            R.drawable.icon_faq, R.drawable.icon_about, R.drawable.icon_notifications,
            R.drawable.icon_help_us,R.drawable.icon_dev};
    private Pref _pref;
    public String migratedFrom="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final int sdk = android.os.Build.VERSION.SDK_INT;
        setContentView(R.layout.activity_home_reside); 
        getWindow().setBackgroundDrawable(null);
        if(getIntent().hasExtra("migratedFrom"))
        {
            migratedFrom=getIntent().getStringExtra("migratedFrom");
        }
        getSupportActionBar().hide();
        initialize();
        setUpMenu();
        loadHomeFragment();
    }

    private void initialize(){
        tbHomeReside = (Toolbar) findViewById(R.id.tbHomeReside);
        _pref = new Pref(this);
        resideMenu = new ResideMenu(this);
        mMainLayout=(RelativeLayout)findViewById(R.id.main_layout);
        final int sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            mMainLayout.setBackgroundDrawable( getResources().getDrawable(R.drawable.background) );
        } else {
            mMainLayout.setBackground( getResources().getDrawable(R.drawable.background));
        }
        if(getApplication().getPackageName().equals("com.ekplate.android"))     //This is Done for Automatic Detection --> whether It is Dev version or not.
        {
            devOptionActivator = 1;
        }else {
            devOptionActivator = 0;
        }

    }

    private void setUpMenu(){
        resideMenu.setBackground(R.drawable.background);
        resideMenu.attachToActivity(this);
        resideMenu.setScaleValue(0.5f);
        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);
        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_LEFT);


        for (int i = 0; i < titles.length - devOptionActivator; i++){
            ResideMenuItem item = new ResideMenuItem(this, icon[i], titles[i]);
            switch (i){
                case 0:
                    item.setOnClickListener(changeLocationListener);
                    break;
                case 1:
                    item.setOnClickListener(myProfileListener);
                    break;
                case 2:
                    item.setOnClickListener(faqListener);
                    break;
                case 3:
                    item.setOnClickListener(aboutListener);
                    break;
                case 4:
                    item.setOnClickListener(notificationsListener);
                    break;
                case 5:
                    item.setOnClickListener(helpUsListener);
                    break;
                case 6:
                    item.setOnClickListener(devOptionListener);
                    break;
            }
            resideMenu.addMenuItem(item, ResideMenu.DIRECTION_LEFT);
        }
        tbHomeReside.setNavigationIcon(R.drawable.ic_action);
        tbHomeReside.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
            }
        });
    }

    private View.OnClickListener changeLocationListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //resideMenu.closeMenu();
            startActivity(new Intent(HomeResideActivity.this, ChangeCountryLocationActivity.class));
        }
    };

    private View.OnClickListener myProfileListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //resideMenu.closeMenu();
            startActivity(new Intent(HomeResideActivity.this, UserProfileActivity.class));
        }
    };

    private View.OnClickListener faqListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //resideMenu.closeMenu();
            startActivity(new Intent(HomeResideActivity.this, FaqActivity.class));
        }
    };

    private View.OnClickListener aboutListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //resideMenu.closeMenu();
            startActivity(new Intent(HomeResideActivity.this, AboutActivity.class));
        }
    };

    private View.OnClickListener notificationsListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //resideMenu.closeMenu();
            startActivity(new Intent(HomeResideActivity.this, NotificationActivity.class));
        }
    };

    private View.OnClickListener helpUsListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //resideMenu.closeMenu();
            startActivity(new Intent(HomeResideActivity.this, HelpToImproveActivity.class));
        }
    };

    private View.OnClickListener devOptionListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            startActivity(new Intent(HomeResideActivity.this, DevOptionActivity.class));
        }
    };

    private void loadHomeFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment fragment = new HomeFragment();
        transaction.replace(R.id.container_body_reside, fragment);
        transaction.commit();
    }

    private void changeProfileImageAndName(){
        resideMenu.setProfileImage(_pref.getSession(ConstantClass.TAG_USER_DP_IMAGE), R.drawable.profile_dp);
        resideMenu.setProfileName(_pref.getSession(ConstantClass.TAG_USER_NAME));
    }
   /* public void closeMenu(){
        tbHomeReside.setVisibility(View.GONE);
    }*/

    @Override
    protected void onResume() {
        super.onResume();
        changeProfileImageAndName();
    }

    @Override
    protected void onPause() {
        super.onPause();
        migratedFrom="";
    }
}
