package com.ekplate.android.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ekplate.android.appinterfaces.BackgroundActionInterface;
import com.ekplate.android.utils.CallServiceAction;
import com.ekplate.android.utils.ConstantClass;
import com.ekplate.android.utils.Pref;
import com.ekplate.android.utils.StoreInLocal;

import org.json.JSONObject;

/**
 * Created by user on 05-11-2015.
 */
public class LoadHomeMenuService extends Service implements BackgroundActionInterface {

    private Pref _pref;
    private CallServiceAction _serviceAction;
    private StoreInLocal _storeInLocal;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("Home service", "start");
        initialize();
        callForHomeMenu();
        return super.onStartCommand(intent, flags, startId);
    }

    private void initialize(){
        _pref = new Pref(this);
        _serviceAction = new CallServiceAction(this);
        _serviceAction.actionInterface = LoadHomeMenuService.this;
        _storeInLocal = new StoreInLocal(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onStarted() {

    }

    @Override
    public void onCompleted(JSONObject response) {
        Log.e("home response", response.toString());
        try {
            JSONObject jsonObjError = response.getJSONObject("errNode");
            if (jsonObjError.getInt("errCode")== 0) {
                Log.e("In", "Home menu response");
                JSONObject jsonData = response.getJSONObject("data");
                if(jsonData.getBoolean("success"))
                {
                    if(jsonData.getJSONArray("header").length()>1)
                    {
                        _storeInLocal.saveInLocalFile(response, ConstantClass.TAG_HOME_MENU_JSON_FILE);
                    }
                }

            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void callForHomeMenu(){
        try {
            JSONObject jsonObjInnerParams = new JSONObject();
            jsonObjInnerParams.put("city", _pref.getSession(ConstantClass.TAG_SELECTED_CITY_LOCATION));
            JSONObject jsonObjParams = new JSONObject();
            jsonObjParams.put("data", jsonObjInnerParams);
            Log.e("Home service input", jsonObjParams.toString());
            _serviceAction.requestVersionApi(jsonObjParams, "get-home-menu-options");
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
