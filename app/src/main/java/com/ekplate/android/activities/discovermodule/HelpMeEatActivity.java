package com.ekplate.android.activities.discovermodule;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.ekplate.android.R;
import com.ekplate.android.appinterfaces.BackgroundActionInterface;
import com.ekplate.android.config.BaseActivity;
import com.ekplate.android.models.discovermodule.HelpMeEatOptionItem;
import com.ekplate.android.utils.CallServiceAction;
import com.ekplate.android.utils.ConstantClass;
import com.ekplate.android.utils.NetworkConnectionCheck;
import com.ekplate.android.utils.Pref;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;

public class HelpMeEatActivity extends BaseActivity implements BackgroundActionInterface {

    private Toolbar tbHelpToEat;
    private TextView toolbarHeaderText, tvHelpMeEatQuestion;
    private RecyclerView rvHelpMeEatOptions;
    private RelativeLayout rlMainContainerHelpMeEat, rlMainContainerGaido;
    private LinearLayout llProgressbarContainerHelpMeEat,llEnnroPage;
    private ImageView ivReloadGaido, ivGaidoFoodImage, ivGaidoFoodType;
    private TextView tvGaidoFoodName, tvGaidoVendorName, tvGaidoVendorAddress, tvGaidoVendorDistance, tvGaidoVendorLike,
            tvGaidoVendorRating, tvGaidoVendorReview;

    private ArrayList<HelpMeEatOptionItem> eatOptionItems;
    private HelpMeEatOptionAdapter eatOptionAdapter;

    private Pref _pref;
    private NetworkConnectionCheck _connection;
    private CallServiceAction _serviceAction;

    private ImageLoader imageLoader;
    private DisplayImageOptions options;

    private String optionId = "", optionTagId = "", requestedWS = "HowToEat", tagIds = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_to_eat);
        getWindow().setBackgroundDrawable(null);
        getSupportActionBar().hide();
        setUpToolBar();
        initialize();
        onClick();
        getHowToEat();
    }

    private void initialize(){

        rlMainContainerHelpMeEat = (RelativeLayout) findViewById(R.id.rlMainContainerHelpMeEat);
        rlMainContainerGaido = (RelativeLayout) findViewById(R.id.rlMainContainerGaido);
        llProgressbarContainerHelpMeEat = (LinearLayout) findViewById(R.id.llProgressbarContainerHelpMeEat);
        llEnnroPage = (LinearLayout)findViewById(R.id.llEnnroPage);
        tvHelpMeEatQuestion = (TextView) findViewById(R.id.tvHelpMeEatQuestion);

        rvHelpMeEatOptions = (RecyclerView) findViewById(R.id.rvHelpMeEatOptions);
        rvHelpMeEatOptions.setLayoutManager(new LinearLayoutManager(this));

        ivReloadGaido = (ImageView) findViewById(R.id.ivReloadGaido);
        ivGaidoFoodImage = (ImageView) findViewById(R.id.ivGaidoFoodImage);
        ivGaidoFoodType = (ImageView) findViewById(R.id.ivGaidoFoodType);

        tvGaidoFoodName = (TextView) findViewById(R.id.tvGaidoFoodName);
        tvGaidoVendorName = (TextView) findViewById(R.id.tvGaidoVendorName);
        tvGaidoVendorAddress = (TextView) findViewById(R.id.tvGaidoVendorAddress);
        tvGaidoVendorDistance = (TextView) findViewById(R.id.tvGaidoVendorDistance);
        tvGaidoVendorLike = (TextView) findViewById(R.id.tvGaidoVendorLike);
        tvGaidoVendorRating = (TextView) findViewById(R.id.tvGaidoVendorRating);
        tvGaidoVendorReview = (TextView) findViewById(R.id.tvGaidoVendorReview);

        _pref = new Pref(HelpMeEatActivity.this);
        _connection = new NetworkConnectionCheck(HelpMeEatActivity.this);
        _serviceAction = new CallServiceAction(HelpMeEatActivity.this);
        _serviceAction.actionInterface = this;

        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .showImageOnLoading(R.drawable.default_image_vendor_inside)
                .showImageOnFail(R.drawable.default_image_vendor_inside)
                .showImageForEmptyUri(R.drawable.default_image_vendor_inside)
                .build();
    }

    private void onClick(){

        ivReloadGaido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getGaidosPick();
            }
        });
    }

    private void setUpToolBar(){
        tbHelpToEat = (Toolbar) findViewById(R.id.tbHelpToEat);
        toolbarHeaderText = (TextView) findViewById(R.id.toolbarHeaderText);
        toolbarHeaderText.setText("HELP ME DECIDE TO EAT");
        tbHelpToEat.setNavigationIcon(R.drawable.ic_action_back);
        tbHelpToEat.setBackgroundColor(Color.parseColor("#34000000"));
        tbHelpToEat.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void getHowToEat(){

        if(_connection.isNetworkAvailable()) {
            llProgressbarContainerHelpMeEat.setVisibility(View.VISIBLE);
            rlMainContainerHelpMeEat.setVisibility(View.GONE);
            try {
                JSONObject parentJsonObj = new JSONObject();
                JSONObject childJsonObj = new JSONObject();
                childJsonObj.put("accessToken", _pref.getSession(ConstantClass.ACCESS_TOKEN));
                childJsonObj.put("city", _pref.getSession(ConstantClass.TAG_SELECTED_CITY_LOCATION));
                childJsonObj.put("food_type", _pref.getSession(ConstantClass.TAG_SELECTED_FOOD_TYPE));
                childJsonObj.put("optionId", optionId);
                childJsonObj.put("optionTagId", optionTagId);
                parentJsonObj.put("data", childJsonObj);
                Log.e("input data", parentJsonObj.toString());
                requestedWS = "HowToEat";
                _serviceAction = new CallServiceAction(HelpMeEatActivity.this);
                _serviceAction.actionInterface = this;
                _serviceAction.requestVersionV2Api(parentJsonObj, "get-help-what-to-eat");
            } catch (Exception e){
                e.printStackTrace();
            }
        } else {
            _connection.getNetworkActiveAlert().show();
        }
    }

    private void getGaidosPick(){

        if(_connection.isNetworkAvailable()) {
            llProgressbarContainerHelpMeEat.setVisibility(View.VISIBLE);
            rlMainContainerGaido.setVisibility(View.GONE);
            try {
                JSONObject parentJsonObj = new JSONObject();
                JSONObject childJsonObj = new JSONObject();
                childJsonObj.put("accessToken", _pref.getSession(ConstantClass.ACCESS_TOKEN));
                childJsonObj.put("latitude", _pref.getSession(ConstantClass.TAG_LATITUDE));
                childJsonObj.put("longitude", _pref.getSession(ConstantClass.TAG_LONGITUDE));
                childJsonObj.put("city", _pref.getSession(ConstantClass.TAG_SELECTED_CITY_LOCATION));
                childJsonObj.put("food_type", _pref.getSession(ConstantClass.TAG_SELECTED_FOOD_TYPE));
                childJsonObj.put("tagIds", tagIds);
                parentJsonObj.put("data", childJsonObj);
                Log.e("input data", parentJsonObj.toString());
                requestedWS = "GaidosPick";
                _serviceAction = new CallServiceAction(HelpMeEatActivity.this);
                _serviceAction.actionInterface = this;
                _serviceAction.requestVersionV2Api(parentJsonObj, "get-gaidos-pick");
            } catch (Exception e){
                e.printStackTrace();
            }
        } else {
            _connection.getNetworkActiveAlert().show();
        }
    }

    @Override
    public void onStarted() {

    }

    @Override
    public void onCompleted(JSONObject response) {
        Log.v("response", response.toString());
        llProgressbarContainerHelpMeEat.setVisibility(View.GONE);
        llEnnroPage.setVisibility(View.GONE);
        try {
            JSONObject jsonObjError = response.getJSONObject("errNode");
            if (jsonObjError.getInt("errCode") == 0) {
                JSONObject jsonObjData = response.getJSONObject("data");
                if(requestedWS.equals("HowToEat")){
                    if(jsonObjData.getString("mapfound").equals("true")){
                        rlMainContainerHelpMeEat.setVisibility(View.VISIBLE);
                        JSONArray jsonArrayQuesDetails = jsonObjData.getJSONArray("quesDetails");
                        if(jsonArrayQuesDetails.length()>0){
                            JSONObject jsonObjQues = jsonArrayQuesDetails.getJSONObject(0);
                            tvHelpMeEatQuestion.setText(jsonObjQues.getString("question"));
                            JSONArray jsonArrayOptionDetails = jsonObjQues.getJSONArray("optionDetails");
                            eatOptionItems = new ArrayList<>();
                            for (int i=0; i<jsonArrayOptionDetails.length(); i++) {
                                HelpMeEatOptionItem _item = new HelpMeEatOptionItem();
                                JSONObject jsonObjOption = jsonArrayOptionDetails.getJSONObject(i);
                                _item.setId(jsonObjOption.getString("optionId"));
                                _item.setOption(jsonObjOption.getString("optionName"));
                                _item.setTag(jsonObjOption.getString("optionTag"));
                                eatOptionItems.add(_item);
                            }
                            eatOptionAdapter = new HelpMeEatOptionAdapter(this, eatOptionItems);
                            rvHelpMeEatOptions.setAdapter(eatOptionAdapter);
                            eatOptionAdapter.notifyDataSetChanged();
                        }
                        else{
                            getGaidosPick();
                        }
                    }
                    else{
                        getGaidosPick();
                    }
                }
                else{
                    rlMainContainerGaido.setVisibility(View.VISIBLE);
                    JSONArray jsonArrayFoodDetails = jsonObjData.getJSONArray("details");
                    JSONObject jsonObjFood = jsonArrayFoodDetails.getJSONObject(0);
                    JSONObject jsonObjVendorDetails = jsonObjFood.getJSONObject("nearestVendor");
                    tvGaidoFoodName.setText(jsonObjFood.getString("foodName"));
                    tvGaidoVendorName.setText(jsonObjVendorDetails.getString("vendorName"));
                    tvGaidoVendorAddress.setText(jsonObjVendorDetails.getString("inlineAddress"));
                    tvGaidoVendorDistance.setText(jsonObjVendorDetails.getString("distance") + " km");
                    tvGaidoVendorLike.setText(jsonObjVendorDetails.getString("noOfLikes") + " Likes");
                    tvGaidoVendorRating.setText(jsonObjVendorDetails.getString("rating"));
                    tvGaidoVendorReview.setText(jsonObjVendorDetails.getString("noOfReviews") + " Reviews");

                    imageLoader.displayImage(jsonObjFood.getString("imageUrl"), ivGaidoFoodImage, options);
                    if(jsonObjFood.getString("foodType").contains("Non-Veg")){
                        ivGaidoFoodType.setImageResource(R.drawable.icon_nonveg);
                    }
                    else{
                        ivGaidoFoodType.setImageResource(R.drawable.icon_veg);
                    }
                }
            }
            else {
               // Toast.makeText(HelpMeEatActivity.this, jsonObjError.getString("errMsg"), Toast.LENGTH_LONG).show();
                llEnnroPage.setVisibility(View.VISIBLE);

            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    public class HelpMeEatOptionAdapter extends RecyclerView.Adapter<HelpMeEatOptionAdapter.OptionItemHolder> {
        private Context context;
        private ArrayList<HelpMeEatOptionItem> eatOptionItems;

        public HelpMeEatOptionAdapter(Context context, ArrayList<HelpMeEatOptionItem> eatOptionItems) {
            this.context = context;
            this.eatOptionItems = eatOptionItems;
        }

        @Override
        public OptionItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View rowView = LayoutInflater.from(context).inflate(R.layout.help_me_eat_option_layout, parent, false);
            OptionItemHolder optionItemHolder = new OptionItemHolder(rowView);
            return optionItemHolder;
        }

        @Override
        public void onBindViewHolder(OptionItemHolder holder, int position) {
            int rem = position % 3;
            switch (rem){
                case 0:
                    holder.llOptionBg.setBackgroundResource(R.drawable.round_btn_red_help_eat_gb);
                    break;
                case 1:
                    holder.llOptionBg.setBackgroundResource(R.drawable.round_btn_green_help_eat_gb);
                    break;
                case 2:
                    holder.llOptionBg.setBackgroundResource(R.drawable.round_btn_blue_help_eat_gb);
                    break;
            }
            holder.tvOptions.setText(eatOptionItems.get(position).getOption());
            holder.llOptionBg.setOnClickListener(onClickListener);
            holder.llOptionBg.setTag(holder);
        }

        @Override
        public int getItemCount() {
            return eatOptionItems.size();
        }

        public class OptionItemHolder extends RecyclerView.ViewHolder {
            private LinearLayout llOptionBg;
            private TextView tvOptions;
            public OptionItemHolder(View itemView) {
                super(itemView);
                llOptionBg = (LinearLayout) itemView.findViewById(R.id.llOptionBg);
                tvOptions = (TextView) itemView.findViewById(R.id.tvOptions);
            }
        }

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OptionItemHolder holder = (OptionItemHolder) view.getTag();
                int position = holder.getAdapterPosition();
                optionId = eatOptionItems.get(position).getId();
                optionTagId = eatOptionItems.get(position).getTag();
                if(tagIds.equals("")){
                    tagIds = optionTagId;
                }
                else{
                    if(!tagIds.contains(optionTagId)){
                        tagIds = tagIds + "," + optionTagId;
                    }
                }
                Log.v("tagIds", tagIds);
                getHowToEat();
            }
        };
    }

    public void clkReFrash(View view)
    {
        getGaidosPick();
    }
}
