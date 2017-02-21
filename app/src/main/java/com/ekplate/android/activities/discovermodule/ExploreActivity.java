package com.ekplate.android.activities.discovermodule;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ekplate.android.R;
import com.ekplate.android.adapters.discovermodule.ExplorerItemAdapter;
import com.ekplate.android.appinterfaces.BackgroundActionInterface;
import com.ekplate.android.config.BaseActivity;
import com.ekplate.android.models.discovermodule.ExplorerItem;
import com.ekplate.android.utils.CallServiceAction;
import com.ekplate.android.utils.ConstantClass;
import com.ekplate.android.utils.NetworkConnectionCheck;
import com.ekplate.android.utils.Pref;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ExploreActivity extends BaseActivity implements BackgroundActionInterface{

    private Toolbar tbExplore;
    private LinearLayout llVendorStories, llRecipes, llAboutFood, llFoodTrucks, llUsersEkplate,
            llFoodTours, llFoodVideo, llGlobalStreetFood, llUserArticles;
    private ArrayList<ExplorerItem> explorerItems;
    private GridView gridExplorer;
    private ExplorerItemAdapter explorerItemAdapter;
    private NetworkConnectionCheck _connection;
    private CallServiceAction _serviceAction;
    private ProgressDialog progressDialog;
    private TextView toolbarHeaderText;
    private Pref _pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);
        getWindow().setBackgroundDrawable(null);
        getSupportActionBar().hide();
        initialize();
        setUpToolBar();
        getExplorerSection();
        explorerItemAdapter = new ExplorerItemAdapter(ExploreActivity.this,explorerItems);
        gridExplorer.setAdapter(explorerItemAdapter);
        explorerItemAdapter.notifyDataSetChanged();
        //setRoundLayoutColor();
        //onClick();
    }

    private void setUpToolBar(){
        tbExplore = (Toolbar) findViewById(R.id.tbExplore);
        toolbarHeaderText = (TextView) findViewById(R.id.toolbarHeaderText);
        toolbarHeaderText.setText("DISCOVER MORE");
        tbExplore.setNavigationIcon(R.drawable.ic_action_back);
        tbExplore.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initialize(){
       /* llVendorStories = (LinearLayout) findViewById(R.id.llVendorStories);
        llRecipes = (LinearLayout) findViewById(R.id.llRecipes);
        llAboutFood = (LinearLayout) findViewById(R.id.llAboutFood);
        llFoodTrucks = (LinearLayout) findViewById(R.id.llFoodTrucks);
        llUsersEkplate = (LinearLayout) findViewById(R.id.llUsersEkplate);
        llFoodTours = (LinearLayout) findViewById(R.id.llFoodTours);
        llFoodVideo = (LinearLayout) findViewById(R.id.llFoodVideo);
        llGlobalStreetFood = (LinearLayout) findViewById(R.id.llGlobalStreetFood);
        llUserArticles = (LinearLayout) findViewById(R.id.llUserArticles);*/
        gridExplorer = (GridView)findViewById(R.id.gridExplorer);
        explorerItems = new ArrayList<>();
        _connection = new NetworkConnectionCheck(ExploreActivity.this);
        _serviceAction = new CallServiceAction(ExploreActivity.this);
        _serviceAction.actionInterface = this;
        _pref = new Pref(ExploreActivity.this);

    }
    private void getExplorerSection(){

        if(_connection.isNetworkAvailable()) {
            try {
                JSONObject parentJsonObj = new JSONObject();
                JSONObject childJsonObj = new JSONObject();
                childJsonObj.put("accessToken", _pref.getSession(ConstantClass.ACCESS_TOKEN));
                parentJsonObj.put("data", childJsonObj);
                Log.e("input data", parentJsonObj.toString());
                _serviceAction = new CallServiceAction(ExploreActivity.this);
                _serviceAction.actionInterface = this;
                _serviceAction.requestVersionV2Api(parentJsonObj, "explore-category");
                showProgressDialogue();
            } catch (Exception e){
                e.printStackTrace();
            }
        } else {
            _connection.getNetworkActiveAlert().show();
        }
    }

    private void setRoundLayoutColor(){
        LayerDrawable bgVendorStoriesDrawable = (LayerDrawable)llVendorStories.getBackground();
        final GradientDrawable shapeVendorStories = (GradientDrawable) bgVendorStoriesDrawable.findDrawableByLayerId(R.id.colorContainer);
        shapeVendorStories.setColor(Color.parseColor("#cc467c"));

        LayerDrawable bgRecipesDrawable = (LayerDrawable)llRecipes.getBackground();
        final GradientDrawable shapeRecipes = (GradientDrawable) bgRecipesDrawable.findDrawableByLayerId(R.id.colorContainer);
        shapeRecipes.setColor(Color.parseColor("#99c11c"));

        LayerDrawable bgAboutFoodDrawable = (LayerDrawable)llAboutFood.getBackground();
        final GradientDrawable shapeAboutFood = (GradientDrawable) bgAboutFoodDrawable.findDrawableByLayerId(R.id.colorContainer);
        shapeAboutFood.setColor(Color.parseColor("#e67706"));

        LayerDrawable bgFoodTrucksDrawable = (LayerDrawable)llFoodTrucks.getBackground();
        final GradientDrawable shapeFoodTrucks = (GradientDrawable) bgFoodTrucksDrawable.findDrawableByLayerId(R.id.colorContainer);
        shapeFoodTrucks.setColor(Color.parseColor("#5ab4da"));

        LayerDrawable bgUsersEkplateDrawable = (LayerDrawable)llUsersEkplate.getBackground();
        final GradientDrawable shapeUsersEkplate = (GradientDrawable) bgUsersEkplateDrawable.findDrawableByLayerId(R.id.colorContainer);
        shapeUsersEkplate.setColor(Color.parseColor("#ffae00"));

        LayerDrawable bgFoodToursDrawable = (LayerDrawable)llFoodTours.getBackground();
        final GradientDrawable shapeFoodTours = (GradientDrawable) bgFoodToursDrawable.findDrawableByLayerId(R.id.colorContainer);
        shapeFoodTours.setColor(Color.parseColor("#15b74e"));

        LayerDrawable bgFoodVideoDrawable = (LayerDrawable)llFoodVideo.getBackground();
        final GradientDrawable shapeFoodVideo = (GradientDrawable) bgFoodVideoDrawable.findDrawableByLayerId(R.id.colorContainer);
        shapeFoodVideo.setColor(Color.parseColor("#fb3838"));

        LayerDrawable bgGlobalStreetFoodDrawable = (LayerDrawable)llGlobalStreetFood.getBackground();
        final GradientDrawable shapeGlobalStreetFood = (GradientDrawable) bgGlobalStreetFoodDrawable.findDrawableByLayerId(R.id.colorContainer);
        shapeGlobalStreetFood.setColor(Color.parseColor("#9777a8"));

        LayerDrawable bgUserArticlesDrawable = (LayerDrawable)llUserArticles.getBackground();
        final GradientDrawable shapeUserArticles = (GradientDrawable) bgUserArticlesDrawable.findDrawableByLayerId(R.id.colorContainer);
        shapeUserArticles.setColor(Color.parseColor("#ff7521"));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void onClick(){
        llVendorStories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(ExploreActivity.this, VendorStoriesActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }
    private void showProgressDialogue(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();
    }

    @Override
    public void onStarted() {

    }

    @Override
    public void onCompleted(JSONObject response) {
        Log.v("response", response.toString());
        progressDialog.dismiss();

            try {
                JSONObject jsonObjError = response.getJSONObject("errNode");
                if (jsonObjError.getInt("errCode") == 0) {

                    JSONObject jsonObjData = response.getJSONObject("data");
                    if(jsonObjData.getString("success").equals("true")){
                        JSONArray jcategoryArray = jsonObjData.getJSONArray("exploreCategories");
                        for (int i = 0;i <jcategoryArray.length();i++){
                            JSONObject jcatObject = jcategoryArray.getJSONObject(i);
                            ExplorerItem item = new ExplorerItem();
                            item.setId(jcatObject.getInt("id"));
                            item.setTitle(jcatObject.getString("title"));
                            item.setImage(jcatObject.getString("imageUrl"));
                            item.setUrl(jcatObject.getString("url"));
                            explorerItems.add(item);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        explorerItemAdapter.notifyDataSetChanged();

    }
}
