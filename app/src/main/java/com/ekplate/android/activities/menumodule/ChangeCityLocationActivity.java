package com.ekplate.android.activities.menumodule;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ekplate.android.R;
import com.ekplate.android.adapters.menumodule.ChangeCityNameAdapter;
import com.ekplate.android.config.BaseActivity;
import com.ekplate.android.models.menumodule.CityNameItem;

import java.util.ArrayList;

public class ChangeCityLocationActivity extends BaseActivity {
    private Toolbar tbChangeLocation;
    private TextView toolbarHeaderText;
    private RecyclerView rcvCityName;
    private ArrayList<CityNameItem> cityNameItems;
    private ChangeCityNameAdapter cityNameAdapter;
    private LinearLayout llMainCityContainer, llProgressbarContainerCity, llErrorInfoCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_city_location);
        getSupportActionBar().hide();
        getWindow().setBackgroundDrawable(null);
        initialize();
        setUpToolBar();
        setUpCityList();
    }

    private void initialize(){
        llMainCityContainer = (LinearLayout) findViewById(R.id.llMainCityContainer);
        llProgressbarContainerCity  = (LinearLayout) findViewById(R.id.llProgressbarContainerCity);
        llErrorInfoCity  = (LinearLayout) findViewById(R.id.llErrorInfoCity);
        rcvCityName = (RecyclerView) findViewById(R.id.rcvCityName);
        rcvCityName.setLayoutManager(new LinearLayoutManager(this));
        cityNameItems = (ArrayList<CityNameItem>)getIntent().getExtras().getSerializable("city_details");
        cityNameAdapter = new ChangeCityNameAdapter(ChangeCityLocationActivity.this, cityNameItems,
                llMainCityContainer, llProgressbarContainerCity, llErrorInfoCity);
    }

    private void setUpToolBar(){
        tbChangeLocation = (Toolbar) findViewById(R.id.tbChangeLocation);
        toolbarHeaderText = (TextView) findViewById(R.id.toolbarHeaderText);
        toolbarHeaderText.setText("SELECT LOCATION");
        tbChangeLocation.setBackground(getResources().getDrawable(R.drawable.toolbar_bg));
        tbChangeLocation.setNavigationIcon(R.drawable.ic_action_back);
        tbChangeLocation.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void setUpCityList(){
        rcvCityName.setAdapter(cityNameAdapter);
        cityNameAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }
}
