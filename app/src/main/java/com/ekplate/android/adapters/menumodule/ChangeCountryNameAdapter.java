package com.ekplate.android.adapters.menumodule;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ekplate.android.R;
import com.ekplate.android.activities.menumodule.ChangeCityLocationActivity;
import com.ekplate.android.appinterfaces.BackgroundActionInterface;
import com.ekplate.android.models.menumodule.CountryNameItem;
import com.ekplate.android.utils.CallServiceAction;
import com.ekplate.android.utils.ConstantClass;
import com.ekplate.android.utils.NetworkConnectionCheck;
import com.ekplate.android.utils.Pref;
import com.ekplate.android.utils.StoreInLocal;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 05-11-2015.
 */
public class ChangeCountryNameAdapter extends RecyclerView.Adapter<ChangeCountryNameAdapter.CountryItemHolder>
        implements BackgroundActionInterface {

    private Context context;
    private ArrayList<CountryNameItem> countryNameItems;
    private List<CountryItemHolder> countryViewHoldersItems;
    private Pref _pref;
    private CallServiceAction _serviceAction;
    private NetworkConnectionCheck _connectionCheck;
    private StoreInLocal _storeInLocal;
    private LinearLayout llMainCountryContainer, llProgressbarContainerCountry;

    public ChangeCountryNameAdapter(Context context, ArrayList<CountryNameItem> countryNameItems,
                                    LinearLayout llMainCountryContainer, LinearLayout llProgressbarContainerCountry) {
        this.context = context;
        this.countryNameItems = countryNameItems;
        this.countryViewHoldersItems = new ArrayList<>();
        this._pref = new Pref(context);
        this._connectionCheck = new NetworkConnectionCheck(context);
        this._serviceAction = new CallServiceAction(context);
        this._serviceAction.actionInterface = ChangeCountryNameAdapter.this;
        this._storeInLocal = new StoreInLocal(context);
        this.llMainCountryContainer = llMainCountryContainer;
        this.llProgressbarContainerCountry = llProgressbarContainerCountry;
    }

    @Override
    public CountryItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(context).inflate(R.layout.city_name_row_layout, parent, false);
        CountryItemHolder itemHolder = new CountryItemHolder(rowView);
        return itemHolder;
    }

    @Override
    public void onBindViewHolder(CountryItemHolder holder, int position) {
        holder.tvChangeCityName.setText(countryNameItems.get(position).getCountryName());

        holder.ivChangeCitySelect.setTag(holder);
        holder.ivChangeCitySelect.setOnClickListener(onClickListenerCheck);
        holder.rlCitySelect.setTag(holder);
        holder.rlCitySelect.setOnClickListener(onClickListenerLayout);
        countryViewHoldersItems.add(holder);

      //  Log.e("SELECTED:", _pref.getSession(ConstantClass.TAG_SELECTED_CITY_LOCATION));
        if(_pref.getSession(ConstantClass.TAG_SELECTED_CITY_LOCATION).equalsIgnoreCase(countryNameItems.get(position).getCountryName())){
            holder.ivChangeCitySelect.setSelected(true);
            countryNameItems.get(position).setSelected(true);
        }

      //  Log.e("SELECTED:",_pref.getSession(ConstantClass.TAG_SELECTED_CITY_LOCATION));
        if(_pref.getSession(ConstantClass.TAG_SELECTED_COUNTRY_LOCATION).equalsIgnoreCase(countryNameItems.get(position).getCountryName())){
            holder.ivChangeCitySelect.setSelected(true);
            countryNameItems.get(position).setSelected(true);
        }
    }

    @Override
    public int getItemCount() {
        return countryNameItems.size();
    }

    @Override
    public void onStarted() {

    }

    @Override
    public void onCompleted(JSONObject response) {
        _storeInLocal.saveInLocalFile(response, _pref.getSession(ConstantClass.TAG_HOME_MENU_JSON_FILE));
        llMainCountryContainer.setVisibility(View.VISIBLE);
        llProgressbarContainerCountry.setVisibility(View.GONE);
    }

    public class CountryItemHolder extends RecyclerView.ViewHolder {
        TextView tvChangeCityName;
        ImageView ivChangeCitySelect;
        RelativeLayout rlCitySelect;
        public CountryItemHolder(View itemView) {
            super(itemView);
            tvChangeCityName = (TextView) itemView.findViewById(R.id.tvChangeCityName);
            ivChangeCitySelect = (ImageView) itemView.findViewById(R.id.ivChangeCitySelect);
            rlCitySelect = (RelativeLayout) itemView.findViewById(R.id.rlCitySelect);
        }
    }

    View.OnClickListener onClickListenerCheck = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            CountryItemHolder holder = (CountryItemHolder) view.getTag();
            int position = holder.getAdapterPosition();
            if (countryNameItems.get(position).getItemType().equals("country")) {
                for (int i = 0; i < countryNameItems.size(); i++) {
                    countryNameItems.get(i).setSelected(false);
                    countryViewHoldersItems.get(i).ivChangeCitySelect.setSelected(false);
                }
                _pref.setSession(ConstantClass.TAG_SELECTED_COUNTRY_LOCATION, countryNameItems.get(position).getCountryName());
                holder.ivChangeCitySelect.setSelected(true);
                Intent intent = new Intent(context, ChangeCityLocationActivity.class);
                intent.putExtra("city_details", countryNameItems.get(position).getCityNameItems());
                context.startActivity(intent);
                ((Activity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            } else {
                if(_connectionCheck.isNetworkAvailable()) {
                    for (int i = 0; i < countryNameItems.size(); i++) {
                        countryNameItems.get(i).setSelected(false);
                        countryViewHoldersItems.get(i).ivChangeCitySelect.setSelected(false);
                    }
                    _pref.setSession(ConstantClass.TAG_SELECTED_CITY_LOCATION, countryNameItems.get(position).getCountryName());
                    _pref.setSession(ConstantClass.TAG_SELECTED_CITY_ID,String.valueOf(countryNameItems.get(position).getId()));
                    holder.ivChangeCitySelect.setSelected(true);
                    countryNameItems.get(position).setSelected(true);
                    callForHomeMenu();
                    llMainCountryContainer.setVisibility(View.GONE);
                    llProgressbarContainerCountry.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(context, "Please check your internet connection.", Toast.LENGTH_LONG).show();
                }
            }
        }
    };

    View.OnClickListener onClickListenerLayout = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            CountryItemHolder holder = (CountryItemHolder) view.getTag();
            int position = holder.getAdapterPosition();
            if (countryNameItems.get(position).getItemType().equals("country")) {
                for (int i = 0; i < countryNameItems.size(); i++) {
                    countryNameItems.get(i).setSelected(false);
                    countryViewHoldersItems.get(i).ivChangeCitySelect.setSelected(false);
                }
                _pref.setSession(ConstantClass.TAG_SELECTED_COUNTRY_LOCATION, countryNameItems.get(position).getCountryName());
                holder.ivChangeCitySelect.setSelected(true);
                Intent intent = new Intent(context, ChangeCityLocationActivity.class);
                intent.putExtra("city_details", countryNameItems.get(position).getCityNameItems());
                context.startActivity(intent);
                ((Activity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            } else {
                if(_connectionCheck.isNetworkAvailable()) {
                    for (int i = 0; i < countryNameItems.size(); i++) {
                        countryNameItems.get(i).setSelected(false);
                        countryViewHoldersItems.get(i).ivChangeCitySelect.setSelected(false);
                    }
                    _pref.setSession(ConstantClass.TAG_SELECTED_CITY_LOCATION, countryNameItems.get(position).getCountryName());
                    _pref.setSession(ConstantClass.TAG_SELECTED_CITY_ID,String.valueOf(countryNameItems.get(position).getId()));
                    holder.ivChangeCitySelect.setSelected(true);
                    countryNameItems.get(position).setSelected(true);
                    callForHomeMenu();
                    llMainCountryContainer.setVisibility(View.GONE);
                    llProgressbarContainerCountry.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(context, "Please check your internet connection.", Toast.LENGTH_LONG).show();
                }
            }
        }
    };

    private void callForHomeMenu(){
        try {
            JSONObject jsonObjInnerParams = new JSONObject();
            jsonObjInnerParams.put("city", _pref.getSession(ConstantClass.TAG_SELECTED_CITY_LOCATION));
            JSONObject jsonObjParams = new JSONObject();
            jsonObjParams.put("data", jsonObjInnerParams);
            _serviceAction.requestVersionApi(jsonObjParams, "get-home-menu-options");
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
