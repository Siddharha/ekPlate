package com.ekplate.android.activities.collegemodule;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.ekplate.android.R;
import com.ekplate.android.activities.searchmodule.SearchActivity;
import com.ekplate.android.adapters.collegemodule.CollegePagerAdapter;
import com.ekplate.android.appinterfaces.BackgroundActionInterface;
import com.ekplate.android.config.BaseActivity;
import com.ekplate.android.utils.CallServiceAction;
import com.ekplate.android.utils.CommonMethods;
import com.ekplate.android.utils.ConstantClass;
import com.ekplate.android.utils.NetworkConnectionCheck;
import com.ekplate.android.utils.Pref;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.json.JSONObject;

import java.util.HashMap;

public class CollegeActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener,
        BackgroundActionInterface {

    private ViewPager vpCollageList;
    private TabLayout tabLayoutCollageList;
    private CollegePagerAdapter collegePagerAdapter;
    private Toolbar tbCollageList;
    private TextView toolbarHeaderText;
    private String[] pageTitle = {"BY POPULARITY", "BY ALPHABET", "NEARBY"};
    private LinearLayout llAddCollage;
    protected GoogleApiClient mGoogleApiClient;
    private PlaceAutocompleteAdapter mAdapter;
    private AutoCompleteTextView mAutocompleteView;
    private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(
            new LatLng(72.767258, 18.878351), new LatLng(73.055649, 19.272906));
    private Pref _pref;
    private NetworkConnectionCheck _connection;
    private CallServiceAction _serviceAction;
    private ProgressDialog progressDialog;
    private PopupWindow infoPopup;
    private CommonMethods _commonMethods;
    private View infoPopupLayout;
    private LayoutInflater layoutInflater;
    private AlertDialog helpDialog;
    private int flagKeyBoardOpen = 0;
    private HashMap<String, String> locationDetails;
    public int PagePosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_college);
        getWindow().setBackgroundDrawable(null);
        getSupportActionBar().hide();
        initialize();
        setUpToolBar();
        setUpPager();
        onClick();
    }

    private void initialize(){
        vpCollageList = (ViewPager) findViewById(R.id.vpCollageList);
        tabLayoutCollageList = (TabLayout) findViewById(R.id.tabLayoutCollageList);
        collegePagerAdapter = new CollegePagerAdapter(getSupportFragmentManager(), this,
                pageTitle, getIntent().getExtras().getInt("optionId"),
                getIntent().getExtras().getString("routeScreen"));
        tbCollageList = (Toolbar) findViewById(R.id.tbCollageList);
        toolbarHeaderText = (TextView) findViewById(R.id.toolbarHeaderText);
        llAddCollage = (LinearLayout) findViewById(R.id.llAddCollage);
        _pref = new Pref(this);
        _connection = new NetworkConnectionCheck(this);
        _serviceAction = new CallServiceAction(this);
        _serviceAction.actionInterface = CollegeActivity.this;
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .build();
        _commonMethods = new CommonMethods(this);
    }

    private void setUpToolBar(){
        tbCollageList.inflateMenu(R.menu.menu_collage);
        tbCollageList.setNavigationIcon(R.drawable.ic_action_back);
        tbCollageList.setBackground(getResources().getDrawable(R.drawable.toolbar_bg));
        toolbarHeaderText.setText("NEAR MY COLLEGE");
        tbCollageList.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        tbCollageList.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.iconSearchCollage:
                        Intent intent = new Intent(CollegeActivity.this, SearchActivity.class);
                        intent.putExtra("searchType", "college");
                        startActivity(intent);
                        break;
                }
                return false;
            }
        });
    }

    private void setUpPager(){
        vpCollageList.setAdapter(collegePagerAdapter);
        vpCollageList.setOffscreenPageLimit(2);
        tabLayoutCollageList.setupWithViewPager(vpCollageList);
    }

    private void onClick(){
        llAddCollage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initializeAddVendorPopup();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (getIntent().getExtras().getString("routeScreen")
                .equalsIgnoreCase("profile")) {
            finish();
        }
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        
        if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) {
            Toast.makeText(this, "keyboard visible", Toast.LENGTH_SHORT).show();
        }

        super.onConfigurationChanged(newConfig);
    }

    private void initializeAddVendorPopup(){
        final AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
        helpBuilder.setTitle("");

        layoutInflater = getLayoutInflater();
        infoPopupLayout = layoutInflater.inflate(R.layout.add_college_popup_layout, null);
        helpBuilder.setView(infoPopupLayout);
        helpDialog = helpBuilder.create();
        helpDialog.show();

        ImageView ivPopupCross = (ImageView) infoPopupLayout.findViewById(R.id.ivPopupCrossAddCollege);
        final EditText etAddCollegeName = (EditText) infoPopupLayout.findViewById(R.id.etAddCollegeName);
        Button btnSubmitNewCollege = (Button) infoPopupLayout.findViewById(R.id.btnSubmitNewCollege);
        mAutocompleteView = (AutoCompleteTextView)
                infoPopupLayout.findViewById(R.id.autocomplete_places);
        mAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient, BOUNDS_GREATER_SYDNEY,
                null);
        mAutocompleteView.setAdapter(mAdapter);
        mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);

        ivPopupCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helpDialog.dismiss();
            }
        });

        mAutocompleteView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                flagKeyBoardOpen = 1;
                Log.e("flagKeyBoardOpen", String.valueOf(flagKeyBoardOpen));
                return false;
            }
        });

        btnSubmitNewCollege.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (_connection.isNetworkAvailable()) {
                    if (etAddCollegeName.getText().toString().equalsIgnoreCase("")) {
                        Toast.makeText(CollegeActivity.this, "Please give the college name.", Toast.LENGTH_LONG).show();
                    } else if (mAutocompleteView.getText().toString().equalsIgnoreCase("")) {
                        Toast.makeText(CollegeActivity.this, "Please give the college address.", Toast.LENGTH_LONG).show();
                    } else {
                        setUpProgressDialog();
                        setInputParamForAddCollege(etAddCollegeName.getText().toString(),
                                mAutocompleteView.getText().toString());
                    }
                } else {
                    _connection.getNetworkActiveAlert().show();
                }
            }
        });
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e("ConnectionFailed", "onConnectionFailed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());

        // TODO(Developer): Check error code and notify the user of error state and resolution.
        Toast.makeText(this,
                "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
            final AutocompletePrediction item = mAdapter.getItem(position);
            final String placeId = item.getPlaceId();
            final CharSequence primaryText = item.getPrimaryText(null);
            locationDetails = _commonMethods.getCoordinatesFromAddress(item.getFullText(null).toString());
            Log.i("Autocomplete", "Autocomplete item selected: " + primaryText);
            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
             details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);

            /*Toast.makeText(getApplicationContext(), "Clicked: " + item.getFullText(null).toString(),
                    Toast.LENGTH_SHORT).show();*/
            Log.i("getPlaceById", "Called getPlaceById to get Place details for " + placeId);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                Log.e("Place query did not", "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }
            // Get the Place object from the buffer.
            final Place place = places.get(0);

            // Format details of the place for display and show it in a TextView.
            /*mPlaceDetailsText.setText(formatPlaceDetails(getResources(), place.getName(),
                    place.getId(), place.getAddress(), place.getPhoneNumber(),
                    place.getWebsiteUri()));*/

            // Display the third party attributions if set.
            final CharSequence thirdPartyAttribution = places.getAttributions();
            if (thirdPartyAttribution == null) {
               // mPlaceDetailsAttribution.setVisibility(View.GONE);
            } else {
               /* mPlaceDetailsAttribution.setVisibility(View.VISIBLE);
                mPlaceDetailsAttribution.setText(Html.fromHtml(thirdPartyAttribution.toString()));*/
            }
            Log.i("Place details", "Place details received: " + place.getName());
            places.release();
        }
    };

    private void setUpProgressDialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait..");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void setInputParamForAddCollege(String collegeName, String collegeAddress){
        try {
            JSONObject parentJsonObj = new JSONObject();
            JSONObject childJsonObj = new JSONObject();
            childJsonObj.put("accessToken", _pref.getSession(ConstantClass.ACCESS_TOKEN));
            childJsonObj.put("collegeName", collegeName);
            childJsonObj.put("collegeAddress", collegeAddress);
            childJsonObj.put("city", locationDetails.get("city"));
            childJsonObj.put("latitude", locationDetails.get("latitude"));
            childJsonObj.put("longitude", locationDetails.get("longitude"));
            parentJsonObj.put("data", childJsonObj);
            Log.e("input data", parentJsonObj.toString());
            _serviceAction.requestVersionApi(parentJsonObj, "add-new-college");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onStarted() {

    }

    @Override
    public void onCompleted(JSONObject response) {
        Log.e("suggestion response", response.toString());
        try {
            JSONObject errorNodeJsonObj = response.getJSONObject("errNode");
            if (errorNodeJsonObj.getInt("errCode") == 0) {
                JSONObject dataJsonObject = response.getJSONObject("data");
                if (dataJsonObject.getBoolean("success")){
                    Toast.makeText(CollegeActivity.this, dataJsonObject.getString("successMsg"), Toast.LENGTH_LONG).show();
                    helpDialog.dismiss();
                } else {
                    Toast.makeText(CollegeActivity.this, dataJsonObject.getString("msg"), Toast.LENGTH_LONG).show();
                    helpDialog.dismiss();
                }
            } else {
                Toast.makeText(CollegeActivity.this, errorNodeJsonObj.getString("errMsg"), Toast.LENGTH_LONG).show();
            }
            progressDialog.dismiss();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
