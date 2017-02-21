package com.ekplate.android.activities.homemodule;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.ekplate.android.R;
import com.ekplate.android.activities.registermodule.LandingActivity;
import com.ekplate.android.appinterfaces.BackgroundActionInterface;
import com.ekplate.android.config.BaseActivity;
import com.ekplate.android.config.EkplateApplication;
import com.ekplate.android.services.LocationTrackingService;
import com.ekplate.android.utils.CallServiceAction;
import com.ekplate.android.utils.CommonFunction;
import com.ekplate.android.utils.CommonMethods;
import com.ekplate.android.utils.ConstantClass;
import com.ekplate.android.utils.NetworkConnectionCheck;
import com.ekplate.android.utils.Pref;
import com.ekplate.android.utils.StoreInLocal;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class SplashActivity extends BaseActivity implements BackgroundActionInterface {
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 1;
    private static final int REQUEST_CODE_ASK_PERMISSIONS_2 = 2;
    private static final int REQUEST_CODE_ASK_PERMISSIONS_3 = 3;
    private CallServiceAction _callServiceAction;
    private Pref _pref;
    private String version;
    private NetworkConnectionCheck _connection;
    private final String TAG = "LocationService.this";
    private CommonMethods _commonMethods;
    private CommonFunction _commonFunction;
    private StoreInLocal _storeInLocal;
    private int flagForVersionService = 0, flagForHomeMenuService = 0;
    private Tracker mTracker;
    private List<Address> addresses;
    private Geocoder geocoder;
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().setBackgroundDrawable(null);
        getSupportActionBar().hide();
        initialize();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("select city status", String.valueOf(
                _pref.getIntegerSession(ConstantClass.TAG_SELECTED_CITY_STATUS)));
        if (_pref.getIntegerSession(ConstantClass.TAG_SELECTED_CITY_STATUS) != 1) {
            _pref.setSession(ConstantClass.TAG_SELECTED_CITY_STATUS, 1);
            if(permissionsGranted()) {
                _pref.setSession(ConstantClass.TAG_SELECTED_CITY_LOCATION, pickCity());

                Toast.makeText(getBaseContext(), pickCity(), Toast.LENGTH_SHORT).show();
            }else {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 3);
            }
        }

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            int hasWriteAccessPermission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            if (hasWriteAccessPermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_CODE_ASK_PERMISSIONS);
            } else {
                startService(new Intent(SplashActivity.this, LocationTrackingService.class));
                permissionForExternalStorage();
            }


        } else {
            startService(new Intent(SplashActivity.this, LocationTrackingService.class));
            permissionForExternalStorage();
        }
        try {
            PackageManager manager = getPackageManager();
            PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
            version = info.versionName;
        } catch (Exception e) {
            Log.e("error data", e.getMessage());
        }


    }

    private String pickCity() {
        String s = "Mumbai";
        try {

            addresses = geocoder.getFromLocation(getLat(), getLng(), 1);
            if (!addresses.isEmpty()) {
                String[] p = addresses.get(0).getAddressLine(2).split(",");
                s = p[0];
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }

    private double getLng() {
        Geocoder geocoder;
        String bestProvider;
        List<Address> user = null;
        double lng = 0;


        Criteria criteria = new Criteria();
        bestProvider = locationManager.getBestProvider(criteria, false);

      /*  if(permissionsGranted()){*/
            Location location = locationManager.getLastKnownLocation(bestProvider);

            if (location == null){
                Toast.makeText(this,"Location Not found",Toast.LENGTH_LONG).show();
            }else{
                geocoder = new Geocoder(this);
                try {
                    user = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    lng=(double)user.get(0).getLongitude();

                }catch (Exception e) {
                    e.printStackTrace();
                }
            }

      /*  }else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 3);
            lng = 0;
        }*/
        return lng;
    }

    private double getLat() {
        LatLng latLng = null;
        Geocoder geocoder;
        String bestProvider;
        List<Address> user = null;
        double lat = 0;


        Criteria criteria = new Criteria();
        bestProvider = locationManager.getBestProvider(criteria, false);
      /*  if(permissionsGranted()){*/
        Location location = locationManager.getLastKnownLocation(bestProvider);

        if (location == null) {
            Toast.makeText(this, "Location Not found", Toast.LENGTH_LONG).show();
        } else {
            geocoder = new Geocoder(this);
            try {
                user = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                lat = (double) user.get(0).getLatitude();

            } catch (Exception e) {
                e.printStackTrace();
            }
            }
      /*  }else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 3);
            lat = 0;
        }*/
        return lat;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    startService(new Intent(SplashActivity.this, LocationTrackingService.class));
                    permissionForExternalStorage();
                } else {
                    // Permission Denied
                    Toast.makeText(this, "Can't Get Location due to Permission issue! please Grand GET ACCOUNT Permission.", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_CODE_ASK_PERMISSIONS_2:
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        // Permission Granted
                        if(_connection.isNetworkAvailable()) {
                            if(_commonMethods.isGpsOn()) {
                                setRequestParam();
                            } else {
                                _commonMethods.getGpsActiveAlert().show();
                            }
                        } else{
                            _connection.getNetworkActiveAlert().show();
                        }

                        EkplateApplication application = (EkplateApplication) getApplication();
                        mTracker = application.getDefaultTracker();
                    } else {
                        // Permission Denied
                        Toast.makeText(this, "Can't Get Location due to Permission issue! please Grand GET ACCOUNT Permission.", Toast.LENGTH_SHORT).show();
                    }


                break;
            case REQUEST_CODE_ASK_PERMISSIONS_3:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    _pref.setSession(ConstantClass.TAG_SELECTED_CITY_LOCATION, pickCity());
                } else {
                    // User refused to grant permission. You can add AlertDialog here
                    Toast.makeText(this, "You didn't give permission to access device location", Toast.LENGTH_LONG).show();
                    //startInstalledAppDetailsActivity();
                    _pref.setSession(ConstantClass.TAG_SELECTED_CITY_LOCATION, "Mumbai");
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void initialize(){
        _callServiceAction = new CallServiceAction(this);
        _callServiceAction.actionInterface = SplashActivity.this;
        _pref = new Pref(this);
        _connection = new NetworkConnectionCheck(this);
        _commonMethods = new CommonMethods(this);
        _commonFunction = new CommonFunction(this);
        _storeInLocal = new StoreInLocal(this);
        //_pref.setSession(ConstantClass.TAG_SELECTED_CITY_LOCATION, "Mumbai");
        locationManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);
        ConstantClass.BASE_URL = ConstantClass.BASE_URL_DEF;
        geocoder = new Geocoder(this, Locale.getDefault());
    }

    private void initPref(){
        _pref.setSession(ConstantClass.TAG_RECENT_TOP_INDEX, 0);
        _pref.setSession(ConstantClass.TAG_RECENT_BOTTOM_INDEX, 0);
        _pref.setSession(ConstantClass.TAG_RECENT_SEARCHES_FIRST, "");
        _pref.setSession(ConstantClass.TAG_RECENT_SEARCHES_SECOND, "");
        _pref.setSession(ConstantClass.TAG_RECENT_SEARCHES_THIRD, "");
        _pref.setSession(ConstantClass.TAG_RECENT_SEARCHES_FOURTH, "");
        //_pref.setSession(ConstantClass.TAG_SELECTED_CITY_LOCATION, "Mumbai");
    }

    private void setRequestParam(){
        try {
            flagForVersionService = 1;
            JSONObject jsonObjInnerParams = new JSONObject();
            jsonObjInnerParams.put("version", version);
            JSONObject jsonObjParams = new JSONObject();
            jsonObjParams.put("data", jsonObjInnerParams);
            _callServiceAction.requestVersionApi(jsonObjParams, "version-check");
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onStarted() {

    }

    @Override
    public void onCompleted(JSONObject response) {
        Log.e("response", response.toString());
        Log.e("google registration", _commonFunction.getGCMRegistrationId());
        if(flagForVersionService == 1) {
            flagForVersionService = 0;
            callForHomeMenu();
        } else if(flagForHomeMenuService == 1) {

            try {
                JSONObject jsonObject = response.getJSONObject("data");
                if(jsonObject.getBoolean("success"))
                {
                   JSONArray jsonArray =  jsonObject.getJSONArray("header");
                    if(jsonArray.length()>=2)
                    {
                        _storeInLocal.saveInLocalFile(response, ConstantClass.TAG_HOME_MENU_JSON_FILE);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (_pref.getSession(ConstantClass.ACCESS_TOKEN).equals("")) {
                initPref();
                startActivity(new Intent(SplashActivity.this, LandingActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            } else {
                startActivity(new Intent(SplashActivity.this, HomeResideActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        }
    }


    private void permissionForExternalStorage(){
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int hasWriteReadPermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (hasWriteReadPermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_ASK_PERMISSIONS_2);
                return;
            }
            else {
                if(_connection.isNetworkAvailable()) {
                    if(_commonMethods.isGpsOn()) {
                        setRequestParam();
                    } else {
                        _commonMethods.getGpsActiveAlert().show();
                    }
                } else{
                    _connection.getNetworkActiveAlert().show();
                }

                EkplateApplication application = (EkplateApplication) getApplication();
                mTracker = application.getDefaultTracker();
            }
        }else {
            if(_connection.isNetworkAvailable()) {
                if(_commonMethods.isGpsOn()) {
                    setRequestParam();
                } else {
                    _commonMethods.getGpsActiveAlert().show();
                }
            } else{
                _connection.getNetworkActiveAlert().show();
            }

            EkplateApplication application = (EkplateApplication) getApplication();
            mTracker = application.getDefaultTracker();
        }
    }

    private void callForHomeMenu(){
        try {
            flagForHomeMenuService = 1;
            JSONObject jsonObjInnerParams = new JSONObject();
            jsonObjInnerParams.put("city", _pref.getSession(ConstantClass.TAG_SELECTED_CITY_LOCATION));
            JSONObject jsonObjParams = new JSONObject();
            jsonObjParams.put("data", jsonObjInnerParams);
            Log.e("Home service input", jsonObjParams.toString());
            _callServiceAction.requestVersionApi(jsonObjParams, "get-home-menu-options");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private Boolean permissionsGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
}
