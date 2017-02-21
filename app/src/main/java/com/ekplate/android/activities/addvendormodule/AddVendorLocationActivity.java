package com.ekplate.android.activities.addvendormodule;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ekplate.android.R;
import com.ekplate.android.activities.menumodule.QuestionSubmitResultActivity;
import com.ekplate.android.appinterfaces.BackgroundActionInterface;
import com.ekplate.android.config.BaseActivity;
import com.ekplate.android.localdbconfig.DbAdapter;
import com.ekplate.android.models.addvendormodule.MenuItem;
import com.ekplate.android.models.addvendormodule.VendorBasicInfoItem;
import com.ekplate.android.utils.CallServiceAction;
import com.ekplate.android.utils.ConstantClass;
import com.ekplate.android.utils.NetworkConnectionCheck;
import com.ekplate.android.utils.Pref;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Avishek on 9/30/2015.
 */
public class AddVendorLocationActivity extends BaseActivity
        implements BackgroundActionInterface {

    private Toolbar toolbarAddVendors;
    private TextView toolbarHeaderText;
    private CardView cardViewBack, cardViewAddRating;
    private MapView mapView;
    private GoogleMap googleMap;
    private TextView tvVendorLocation;
    private ImageView marker;
    private DbAdapter dbAdapter;
    private String currentLatitude, currentLongitude;
    private LinearLayout llSubmitLocation;
    private LatLng latlng;
    private boolean locationFound = false;
    private double latitude = 0.0, longitude = 0.0;

    private Geocoder geocoder;
    private List<Address> addresses;
    private ArrayList<MenuItem> menuItems;
    private Pref _pref;
    private NetworkConnectionCheck _connection;
    private CallServiceAction _serviceAction;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vendor_location);
        getWindow().setBackgroundDrawable(null);
        getSupportActionBar().hide();
        init();
        initializeMap(savedInstanceState);
        setUpToolbar();
        onClick();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude)).zoom(12f).tilt(70).build();
        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
        marker.animate().translationY(-marker.getHeight() / 2);
        try {
            new GetLocationAsync().execute(latitude, longitude);
        } catch (Exception e) {

        }
        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                if (_pref.getIntegerSession(ConstantClass.TAG_ADD_VENDOR_LOCATION_FLAG) == 1) {
                    Log.e("old", "store1");
                    setUpLocation();
                } else {
                    Log.e("new", "store2");
                    if (googleMap.getMyLocation() != null) {
                        setVendorLocationInMap(googleMap.getMyLocation().getLatitude(),
                                googleMap.getMyLocation().getLongitude());
                    }

                }
            }
        });

    }

    private void setVendorLocationInMap(double latitude, double longitude) {
        if (!(googleMap.getMyLocation() == null)) {
            //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15));
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(latitude, longitude)).zoom(15f).build();
            googleMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));
            marker.animate().translationY(-marker.getHeight() / 2);
            try {
                new GetLocationAsync().execute(latitude, longitude);

            } catch (Exception e) {

            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    private void init() {
        dbAdapter = new DbAdapter(this);
        llSubmitLocation = (LinearLayout) findViewById(R.id.llSubmitLocation);
        menuItems = new ArrayList<>();
        _pref = new Pref(this);
        _connection = new NetworkConnectionCheck(this);
        _serviceAction = new CallServiceAction(this);
        _serviceAction.actionInterface = AddVendorLocationActivity.this;
    }

    private void setUpLocation() {
        dbAdapter.open();
        ArrayList<VendorBasicInfoItem> vendorBasicInfoItems = dbAdapter.getVendorBasicInfo();
        dbAdapter.close();

        // Log.e("store latitude", vendorBasicInfoItems.get(0).getLatitude());
        //Log.e("store longitude", vendorBasicInfoItems.get(0).getLongitude());
        setVendorLocationInMap(Double.parseDouble(vendorBasicInfoItems.get(0).getLatitude()),
                Double.parseDouble(vendorBasicInfoItems.get(0).getLongitude()));
    }

    private void initializeMap(Bundle savedInstanceState) {
        toolbarAddVendors = (Toolbar) findViewById(R.id.toolbarAddVendors);
        toolbarHeaderText = (TextView) findViewById(R.id.toolbarHeaderText);
        cardViewBack = (CardView) findViewById(R.id.cardViewBack);
        cardViewAddRating = (CardView) findViewById(R.id.cardViewAddRating);
        tvVendorLocation = (TextView) findViewById(R.id.tvVendorLocation);
        marker = (ImageView) findViewById(R.id.marker);

        // Gets the MapView from the XML layout and creates it
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        googleMap = mapView.getMap();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        googleMap.setMyLocationEnabled(true);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.getUiSettings().setCompassEnabled(false);
        googleMap.getUiSettings().setRotateGesturesEnabled(true);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);

        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        MapsInitializer.initialize(AddVendorLocationActivity.this);
    }

    private void setUpToolbar(){
        toolbarAddVendors.setNavigationIcon(R.drawable.ic_action_back);
        toolbarHeaderText.setText("LOCATION");
        toolbarAddVendors.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void onClick(){
        googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                latlng = googleMap.getCameraPosition().target;
                latitude = latlng.latitude;
                longitude = latlng.longitude;

                try {
                    new GetLocationAsync().execute(latitude, longitude);

                } catch (Exception e) {

                }
            }
        });

        cardViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        cardViewAddRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(locationFound) {
                    saveLocationInfo(tvVendorLocation.getText().toString(), currentLatitude, currentLongitude);
                    _pref.setSession(ConstantClass.TAG_ADD_VENDOR_LOCATION_FLAG, 1);
                    startActivity(new Intent(AddVendorLocationActivity.this, AddVendorRatingActivity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
                else {
                        Toast.makeText(getBaseContext(),"Trying to retriving Location.. wait.!!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        llSubmitLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (locationFound) {
                    if (_connection.isNetworkAvailable()) {
                        saveLocationInfo(tvVendorLocation.getText().toString(), currentLatitude, currentLongitude);
                        setUpProgressDialog();
                        setUpAddVendorParameter();

                    } else {
                        _connection.getNetworkActiveAlert().show();
                    }
                }
                else {
                    Toast.makeText(getBaseContext(),"Trying to retriving Location.. wait.!!",Toast.LENGTH_SHORT).show();
                }
            }
        });
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
                    //Toast.makeText(AddVendorLocationActivity.this, "Thank You for adding. We will review and notify you.", Toast.LENGTH_LONG).show();
                    _pref.setSession(ConstantClass.TAG_ADD_VENDOR_SUBMIT_FLAG, 0);
                    _pref.setSession(ConstantClass.TAG_ADD_VENDOR_LOCATION_FLAG, 0);
                    _pref.setSession(ConstantClass.TAG_ADD_VENDOR_RATING_FLAG, 0);
                    _pref.setSession(ConstantClass.TAG_ADD_VENDOR_BASIC_INFO_FLAG, 0);
                    Intent intent = new Intent(AddVendorLocationActivity.this, QuestionSubmitResultActivity.class);
                    intent.putExtra("screenTypeFlag", "add_vendor");
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else {
                    Toast.makeText(AddVendorLocationActivity.this, dataJsonObject.getString("sucessMsg"), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(AddVendorLocationActivity.this, errorNodeJsonObj.getString("errMsg"), Toast.LENGTH_LONG).show();
            }
            progressDialog.dismiss();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private class GetLocationAsync extends AsyncTask<Double, Void, String> {
        double x, y;
        @Override
        protected void onPreExecute() {
            tvVendorLocation.setText("Search Location");
            locationFound = false;
        }

        @Override
        protected String doInBackground(Double... params) {
            x = params[0];
            y = params[1];
            try {
                geocoder = new Geocoder(AddVendorLocationActivity.this, Locale.ENGLISH);
                addresses = geocoder.getFromLocation(x, y, 1);
                Log.v("addresses", addresses.toString());

            } catch (IOException e) {
                Log.e("tag", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                tvVendorLocation.setText(addresses.get(0).getAddressLine(0)
                        + ", " + addresses.get(0).getAddressLine(1));
                //tvVendorLocation.setPadding(50, 0, 30, 0);
                currentLatitude = String.valueOf(addresses.get(0).getLatitude());
                currentLongitude = String.valueOf(addresses.get(0).getLongitude());
                locationFound = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {

        }
    }

    private void saveLocationInfo(String address, String latitude, String longitude){
        dbAdapter.open();
        int success = dbAdapter.updateVendorLocation(address, latitude, longitude);
        dbAdapter.close();
        Log.e("location success", String.valueOf(success));
    }

    private void setUpProgressDialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait..");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void setUpAddVendorParameter(){
        dbAdapter.open();
        ArrayList<VendorBasicInfoItem> vendorBasicInfoItems = dbAdapter.getVendorBasicInfo();
        menuItems = dbAdapter.getMenuList(menuItems);
        dbAdapter.close();
        try {
            JSONObject parentJsonObj = new JSONObject();
            JSONObject childJsonObj = new JSONObject();
            JSONArray childFoodItemJsonArray = new JSONArray();
            childJsonObj.put("accessToken", _pref.getSession(ConstantClass.ACCESS_TOKEN));
            childJsonObj.put("vendorName", vendorBasicInfoItems.get(0).getVendorName());
            childJsonObj.put("vendorShop", vendorBasicInfoItems.get(0).getShopName());
            childJsonObj.put("vendorContactNo", vendorBasicInfoItems.get(0).getContactNo());
            childJsonObj.put("vendorMostSellingFood", vendorBasicInfoItems.get(0).getMostSellingFood());
            childJsonObj.put("address", vendorBasicInfoItems.get(0).getVendorAddress());
            childJsonObj.put("latitude", vendorBasicInfoItems.get(0).getLatitude());
            childJsonObj.put("longitude",vendorBasicInfoItems.get(0).getLongitude());
            childJsonObj.put("hygieneRating", vendorBasicInfoItems.get(0).getHygieneRating());
            childJsonObj.put("tasteRating", vendorBasicInfoItems.get(0).getTasteRating());
            for (int i=0; i<menuItems.size(); i++){
                JSONObject childFoodItemJsonObj = new JSONObject();
                childFoodItemJsonObj.put("foodItem", menuItems.get(i).getFoodName());
                childFoodItemJsonObj.put("foodPrice", menuItems.get(i).getFoodValue());
                childFoodItemJsonArray.put(childFoodItemJsonObj);
            }
            childJsonObj.put("foodMenuList", childFoodItemJsonArray);
            parentJsonObj.put("data", childJsonObj);
            Log.e("input data", parentJsonObj.toString());
            _serviceAction.requestVersionApi(parentJsonObj, "insert-new-vendor");
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}

