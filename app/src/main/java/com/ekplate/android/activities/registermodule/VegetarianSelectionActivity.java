package com.ekplate.android.activities.registermodule;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.ekplate.android.R;
import com.ekplate.android.activities.homemodule.HomeResideActivity;
import com.ekplate.android.appinterfaces.BackgroundActionInterface;
import com.ekplate.android.config.BaseActivity;
import com.ekplate.android.localdbconfig.DbAdapter;
import com.ekplate.android.utils.CallServiceAction;
import com.ekplate.android.utils.ConstantClass;
import com.ekplate.android.utils.Pref;

import org.json.JSONException;
import org.json.JSONObject;


public class VegetarianSelectionActivity extends BaseActivity implements BackgroundActionInterface {

    private Switch switchVegToggleButton;
    private CardView cvEnterButton;
    private TextView tvFooterText;
    private Pref _pref;
    private DbAdapter dbAdapter;
    private CallServiceAction _serviceAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(null);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_vegetarian_selection);
        initialize();
        setUpText();
        onClick();
        Toast.makeText(VegetarianSelectionActivity.this, "dpUrl ==>>> " +
                _pref.getSession(ConstantClass.TAG_USER_DP_IMAGE), Toast.LENGTH_LONG);
    }

    private void initialize(){
        switchVegToggleButton = (Switch) findViewById(R.id.switchVegToggleButton);
        cvEnterButton = (CardView) findViewById(R.id.cvEnterButton);
        tvFooterText = (TextView) findViewById(R.id.tvFooterText);
        _serviceAction = new CallServiceAction(this);
        _serviceAction.actionInterface = VegetarianSelectionActivity.this;
        dbAdapter = new DbAdapter(this);
        _pref = new Pref(VegetarianSelectionActivity.this);
    }

    private void setUpText(){
        tvFooterText.setText(Html.fromHtml("<font color=#FFFFFF>You can change this option from </font>" +
                "<font color=#FFBB00>\"My Account\"</font>"));
        String vegStatus = _pref.getSession(ConstantClass.TAG_SELECTED_FOOD_TYPE);
        dbAdapter.open();
        if (dbAdapter.isUserExist(_pref.getSession(ConstantClass.ACCESS_TOKEN))) {

            if (vegStatus.equalsIgnoreCase("Veg")) {
                switchVegToggleButton.setChecked(true);
            }
            else
            {
                switchVegToggleButton.setChecked(false);
            }
            _pref.setSession(ConstantClass.TAG_SELECTED_FOOD_TYPE, vegStatus);
        }
        else
        {
            if (vegStatus.equalsIgnoreCase("Veg")) {
                switchVegToggleButton.setChecked(true);
            }
            else
            {
                switchVegToggleButton.setChecked(false);
            }
        }
        dbAdapter.close();


    }

    private void onClick(){
        cvEnterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(VegetarianSelectionActivity.this, HomeActivity.class));
                dbAdapter.open();
                if (dbAdapter.isUserExist(_pref.getSession(ConstantClass.ACCESS_TOKEN))) {
                    dbAdapter.updateVegStatus(_pref.getSession(ConstantClass.ACCESS_TOKEN),
                            _pref.getSession(ConstantClass.TAG_SELECTED_FOOD_TYPE));
                } else {
                    dbAdapter.insertUserVegStatus(_pref.getSession(ConstantClass.ACCESS_TOKEN),
                            _pref.getSession(ConstantClass.TAG_SELECTED_FOOD_TYPE));
                }
                dbAdapter.close();
                _pref.setSession(ConstantClass.HINT_STATUS,true);
                Intent intent = new Intent(VegetarianSelectionActivity.this, HomeResideActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        });

        switchVegToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    _pref.setSession(ConstantClass.TAG_SELECTED_FOOD_TYPE, "Veg");
                    Log.e("food type", _pref.getSession(ConstantClass.TAG_SELECTED_FOOD_TYPE));
                    sendFeedbackForFoodStatus();
                } else {
                    _pref.setSession(ConstantClass.TAG_SELECTED_FOOD_TYPE, "Non-Veg");
                    Log.e("food type", _pref.getSession(ConstantClass.TAG_SELECTED_FOOD_TYPE));
                    sendFeedbackForFoodStatus();
                }
            }
        });
    }

    private void sendFeedbackForFoodStatus() {
        try {
            JSONObject jsonObjInnerParams = new JSONObject();
            jsonObjInnerParams.put("accessToken", _pref.getSession(ConstantClass.ACCESS_TOKEN));
            jsonObjInnerParams.put("foodType", _pref.getSession(ConstantClass.TAG_SELECTED_FOOD_TYPE));
            JSONObject jsonObjParams = new JSONObject();
            jsonObjParams.put("data", jsonObjInnerParams);
            Log.e("login data", jsonObjParams.toString());
            _serviceAction.requestVersionApi(jsonObjParams, "change-food-type");
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onStarted() {

    }

    @Override
    public void onCompleted(JSONObject response) {
        Log.e("Response",response.toString());

    }
}
