package com.ekplate.android.activities.menumodule;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ekplate.android.R;
import com.ekplate.android.adapters.menumodule.ChangeCountryNameAdapter;
import com.ekplate.android.appinterfaces.BackgroundActionInterface;
import com.ekplate.android.config.BaseActivity;
import com.ekplate.android.models.menumodule.CityNameItem;
import com.ekplate.android.models.menumodule.CountryNameItem;
import com.ekplate.android.utils.CallServiceAction;
import com.ekplate.android.utils.ConstantClass;
import com.ekplate.android.utils.NetworkConnectionCheck;
import com.ekplate.android.utils.Pref;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ChangeCountryLocationActivity extends BaseActivity implements BackgroundActionInterface {

    private Toolbar tbChangeCountryLocation;
    private TextView toolbarHeaderText, tvListHeading;
    private RecyclerView rcvCountryName;
    private LinearLayout llMainCountryContainer, llProgressbarContainerCountry;
    private ArrayList<CountryNameItem> countryNameItems;
    private ChangeCountryNameAdapter changeCountryNameAdapter;
    private ProgressDialog progressDialog;
    private NetworkConnectionCheck _connection;
    private CallServiceAction _serviceAction;
    private Pref _pref;
    private LinearLayout llErrorInfo;
    private ImageView ivLoaderErrorOne;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_country_location);
        getSupportActionBar().hide();
        getWindow().setBackgroundDrawable(null);
        initialize();
        setUpToolBar();
        onClick();
    }

    private void initialize(){
        rcvCountryName = (RecyclerView) findViewById(R.id.rcvCountryName);
        llMainCountryContainer = (LinearLayout) findViewById(R.id.llMainCountryContainer);
        llProgressbarContainerCountry = (LinearLayout) findViewById(R.id.llProgressbarContainerCountry);
        llErrorInfo = (LinearLayout) findViewById(R.id.llErrorInfo);
        ivLoaderErrorOne = (ImageView) findViewById(R.id.ivLoaderErrorOne);
        tvListHeading = (TextView)findViewById(R.id.tvListHeading);
        rcvCountryName.setLayoutManager(new LinearLayoutManager(this));
        countryNameItems = new ArrayList<>();
        changeCountryNameAdapter = new ChangeCountryNameAdapter(this, countryNameItems, llMainCountryContainer,
                llProgressbarContainerCountry);
        _connection = new NetworkConnectionCheck(this);
        _serviceAction = new CallServiceAction(this);
        _serviceAction.actionInterface = ChangeCountryLocationActivity.this;
        _pref = new Pref(ChangeCountryLocationActivity.this);
    }

    private void setUpToolBar(){
        tbChangeCountryLocation = (Toolbar) findViewById(R.id.tbChangeCountryLocation);
        toolbarHeaderText = (TextView) findViewById(R.id.toolbarHeaderText);
        tbChangeCountryLocation.setBackground(getResources().getDrawable(R.drawable.toolbar_bg));
        tbChangeCountryLocation.setNavigationIcon(R.drawable.ic_action_back);
        tbChangeCountryLocation.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void onClick(){
        ivLoaderErrorOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _connection.getNetworkActiveAlert().show();
            }
        });
    }

    private void setUpCountryList(){
        if(_connection.isNetworkAvailable()){
            countryNameItems.clear();
            changeCountryNameAdapter.notifyDataSetChanged();
            llErrorInfo.setVisibility(View.GONE);
            llProgressbarContainerCountry.setVisibility(View.VISIBLE);
            rcvCountryName.setAdapter(changeCountryNameAdapter);
            loadCountryItem();
        } else {
            llErrorInfo.setVisibility(View.VISIBLE);
            llProgressbarContainerCountry.setVisibility(View.GONE);
        }
    }

    private void loadCountryItem(){
        try {
            JSONObject jsonObjInnerParams = new JSONObject();
            jsonObjInnerParams.put("accesstoken", _pref.getSession(ConstantClass.ACCESS_TOKEN));
            JSONObject jsonObjParams = new JSONObject();
            jsonObjParams.put("data", jsonObjInnerParams);
            Log.e("ChangeCity", jsonObjInnerParams.toString());
            _serviceAction.requestVersionApi(jsonObjParams, "get-cities");
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
    protected void onResume() {
        super.onResume();
        setUpCountryList();
    }

    @Override
    public void onStarted() {

    }

    @Override
    public void onCompleted(JSONObject response) {
        Log.e("response_city ", response.toString());
        try {
            JSONObject jsonObjError = response.getJSONObject("errNode");
            if (jsonObjError.getInt("errCode") == 0) {
                JSONObject jsonObjData = response.getJSONObject("data");
                if (jsonObjData.getString("type").equals("country-city")){
                    toolbarHeaderText.setText("SELECT COUNTRY");
                    tvListHeading.setText("Select your country");
                    JSONArray countryJsonArray = jsonObjData.getJSONArray("country");
                    for (int i = 0; i < countryJsonArray.length(); i++) {
                        JSONObject countryJsonObj = countryJsonArray.getJSONObject(i);
                        CountryNameItem _countryItem = new CountryNameItem();
                        _countryItem.setId(countryJsonObj.getInt("countryId"));
                        _countryItem.setCountryName(countryJsonObj.getString("countryTitle"));
                        _countryItem.setItemType("country");
                        JSONArray cityJsonArray = countryJsonObj.getJSONArray("city");
                        ArrayList<CityNameItem> cityNameItems = new ArrayList<>();
                        for (int j = 0; j < cityJsonArray.length(); j++) {
                            CityNameItem _cityItem = new CityNameItem();
                            JSONObject cityJsonObject = cityJsonArray.getJSONObject(j);
                            _cityItem.setId(cityJsonObject.getInt("cityId"));
                            _cityItem.setCityName(cityJsonObject.getString("cityName"));
                            cityNameItems.add(_cityItem);
                        }
                        _countryItem.setCityNameItems(cityNameItems);
                        countryNameItems.add(_countryItem);
                    }
                    changeCountryNameAdapter.notifyDataSetChanged();
                    toggleContainerLayout();
                } else if(jsonObjData.getString("type").equals("only-city")) {
                    toolbarHeaderText.setText("SELECT CITY");
                    tvListHeading.setText("Select your city");
                    JSONArray cityJsonArray = jsonObjData.getJSONArray("city");
                    for (int j = 0; j < cityJsonArray.length(); j++) {
                        CountryNameItem _cityItem = new CountryNameItem();
                        JSONObject cityJsonObject = cityJsonArray.getJSONObject(j);
                        _cityItem.setId(cityJsonObject.getInt("cityId"));
                        _cityItem.setCountryName(cityJsonObject.getString("cityName"));
                        _cityItem.setItemType("city");
                        countryNameItems.add(_cityItem);
                    }
                    changeCountryNameAdapter.notifyDataSetChanged();
                    toggleContainerLayout();
                }
            } else {
                Toast.makeText(ChangeCountryLocationActivity.this, "Error in server Side.", Toast.LENGTH_LONG).show();
                llMainCountryContainer.setVisibility(View.GONE);
                toggleErrorContainerLayout();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void toggleContainerLayout(){
        llProgressbarContainerCountry.setVisibility(View.GONE);
        llMainCountryContainer.setVisibility(View.VISIBLE);
    }

    private void toggleErrorContainerLayout(){
        llErrorInfo.setVisibility(View.VISIBLE);
        llProgressbarContainerCountry.setVisibility(View.GONE);
    }
}
