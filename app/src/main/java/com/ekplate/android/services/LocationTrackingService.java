package com.ekplate.android.services;

import android.app.Service;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ekplate.android.utils.ConstantClass;
import com.ekplate.android.utils.LocationProvider;
import com.ekplate.android.utils.Pref;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by user on 30-10-2015.
 */
public class LocationTrackingService extends Service implements LocationProvider.LocationCallback {

    private Pref _pref;
    private LocationProvider _LocationProvider;
    boolean service = false;
    private static Timer timer = new Timer();
    private final String TAG = "LocationTrackingService";
    private int flagGetCityComplete = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        initialize();
        startService();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        service = false;
        _LocationProvider.disconnect();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void initialize(){
        _pref = new Pref(LocationTrackingService.this);
        service = true;
    }

    @Override
    public void handleNewLocation(Location location) {
        Log.e("Latitude", "==>" + String.valueOf(location.getLatitude()));
        Log.e("Longitude", "==>" + String.valueOf(location.getLongitude()));
        _pref.setSession(ConstantClass.TAG_LATITUDE, String.valueOf(location.getLatitude()));
        _pref.setSession(ConstantClass.TAG_LONGITUDE, String.valueOf(location.getLongitude()));
        /*Toast.makeText(this, "Latitude = " + String.valueOf(location.getLatitude()) +
                ", Longitude = " + String.valueOf(location.getLongitude()), Toast.LENGTH_LONG).show();*/
        getAddressLocation(location.getLatitude(), location.getLongitude());

        embadeLog();
    }

    private void embadeLog() {

        if(_pref.getLogTrackStatus()) {
            String filePath = Environment.getExternalStorageDirectory() + "/ekplateLogcat.txt";
            try {
                Runtime.getRuntime().exec(new String[]{"logcat", "-f", filePath, "MyAppTAG:V", "*:S"});
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void startService() {
        timer.scheduleAtFixedRate(new mainTask(), 0, 1000 * 30);
    }

    private class mainTask extends TimerTask
    {
        public void run() {
            Log.e("start", "service");
            toastHandler.sendEmptyMessage(0);
        }
    }

    private final Handler toastHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {


            _LocationProvider = new LocationProvider(LocationTrackingService.this, LocationTrackingService.this);
            _LocationProvider.connect();
        }
    };

    //////////////////  CODE TO GET THE CURRENT LOCATION ADDRESS  ////////////////////

    private void getAddressLocation(final double latitude, final double longitude){

        Thread thread = new Thread() {
            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(LocationTrackingService.this, Locale.getDefault());
                String result = null;
                try {
                    List<Address> addressList = geocoder.getFromLocation(
                            latitude, longitude, 1);
                    if (addressList != null && addressList.size() > 0) {
                        Address address = addressList.get(0);
                       /* StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                            sb.append(address.getAddressLine(i)).append("\n");
                        }*/
                        _pref.setSession(ConstantClass.TAG_CURRENT_CITY_LOCATION, address.getLocality());
                        Log.e("city", address.getLocality());
                        Log.e("flagGetCityComplete", String.valueOf(flagGetCityComplete));
                        if(flagGetCityComplete == 0){
                            flagGetCityComplete = 1;
                            startService(new Intent(LocationTrackingService.this, LoadHomeMenuService.class));
                        }
                        /*sb.append(address.getPostalCode()).append("\n");
                        sb.append(address.getCountryName());
                        result = sb.toString();*/
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Unable connect to Geocoder", e);
                    //_pref.setSession(ConstantClass.TAG_CURRENT_CITY_LOCATION, address.getLocality());
                    //_pref.setSession(ConstantClass.TAG_SELECTED_CITY_LOCATION, "Mumbai");
                    Log.e("flagGetCityComplete", String.valueOf(flagGetCityComplete));
                    if(flagGetCityComplete == 0){
                        flagGetCityComplete = 1;
                        startService(new Intent(LocationTrackingService.this, LoadHomeMenuService.class));
                    }
                }
            }
        };
        thread.start();
    }

    //////////////////  END  ////////////////////
}
