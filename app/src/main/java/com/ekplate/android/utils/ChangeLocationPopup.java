package com.ekplate.android.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.ekplate.android.activities.menumodule.ChangeCountryLocationActivity;
import com.ekplate.android.appinterfaces.BackgroundActionInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by user on 12/19/2016.
 */

public class ChangeLocationPopup implements BackgroundActionInterface{

    private static final String API_CITY_CALL = "find-city";
    private Context context;
    private Pref pref;
    private Geocoder geocoder;
    private List<Address> addresses;
    private String cityName,stateName,countryName;
    private  AlertDialog.Builder a;
    private CallServiceAction callServiceAction;

    public ChangeLocationPopup(Context context) {
        this.context = context;
        pref = new Pref(context);
        callServiceAction = new CallServiceAction(context);
        callServiceAction.actionInterface = this;
        geocoder = new Geocoder(context, Locale.getDefault());
        a = new AlertDialog.Builder(context);
        try {
            addresses = geocoder.getFromLocation(Float.parseFloat(pref.getSession(ConstantClass.TAG_LATITUDE)),Float.parseFloat(pref.getSession(ConstantClass.TAG_LONGITUDE)), 1);
            if(!addresses.isEmpty()){


                cityName = addresses.get(0).getAddressLine(0);
                stateName = addresses.get(0).getAddressLine(1);
                countryName = addresses.get(0).getAddressLine(2);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

       // countryName = pref.getSession(ConstantClass.TAG_CURRENT_CITY_LOCATION);
    }

    public void callServiceForCityList(){
        JSONObject jsonObject1 = new JSONObject();
        JSONObject jsonObject2 = new JSONObject();

        try {

            if(countryName !=null){
            if(!countryName.isEmpty()) {
                String[] CityNameArray = countryName.split(",");
                jsonObject1.put("accessToken", pref.getSession(ConstantClass.ACCESS_TOKEN));
                jsonObject1.put("cityName", CityNameArray[0]);

                jsonObject2.put("data", jsonObject1);

                callServiceAction.requestVersionApi(jsonObject2, API_CITY_CALL);
            }else {
                Toast.makeText(context, "It Seems like you have Bad Network connection!!", Toast.LENGTH_SHORT).show();
            }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void checkLocationForPopup(String s){
        Log.e("Adrses:",countryName+"-->"+ pref.getSession(ConstantClass.TAG_CURRENT_CITY_LOCATION));
        if(!countryName.contains(pref.getSession(ConstantClass.TAG_SELECTED_CITY_LOCATION))){

            if(!locationChangePopup(s).isShowing()) {

                try{
                    locationChangePopup(s).show();
                }catch(WindowManager.BadTokenException e){
                    e.printStackTrace();
                }

            }
        }
    }
    public  AlertDialog locationChangePopup(String s){


                a.setTitle("Location Warning!")
                        .setMessage(s)
                        .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                       Intent intent = new Intent(context, ChangeCountryLocationActivity.class);
                        intent.putExtra("city_details", countryName);
                        context.startActivity(intent);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing....
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert);

        AlertDialog alertDialog = a.create();
        return alertDialog;

    }


    @Override
    public void onStarted() {

    }

    @Override
    public void onCompleted(JSONObject response) {

Log.e("responsevendorlocation",response.toString());
        if(response.optJSONObject("errNode").optInt("errCode") == 0){
            String s = response.optJSONObject("data").optString("msg");
            if(response.optJSONObject("data").optBoolean("Listed")){
                checkLocationForPopup(s);
            }else {
                    Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
