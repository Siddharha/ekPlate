package com.ekplate.android.adapters.menumodule;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
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
import com.ekplate.android.models.menumodule.CityNameItem;
import com.ekplate.android.utils.CallServiceAction;
import com.ekplate.android.utils.ConstantClass;
import com.ekplate.android.utils.NetworkConnectionCheck;
import com.ekplate.android.utils.Pref;
import com.ekplate.android.utils.StoreInLocal;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 29-10-2015.
 */
public class ChangeCityNameAdapter extends RecyclerView.Adapter<ChangeCityNameAdapter.CityViewHolder>
        implements BackgroundActionInterface {

    private Context context;
    private ArrayList<CityNameItem> cityNameItems;
    private List<CityViewHolder> cityViewHoldersItems;
    private Pref _pref;
    private CallServiceAction _serviceAction;
    private NetworkConnectionCheck _connectionCheck;
    private StoreInLocal _storeInLocal;
    private LinearLayout llMainCityContainer, llProgressbarContainerCity, llErrorInfoCity;
    private static String changeUsear =  "change-user-location";

    public ChangeCityNameAdapter(Context context, ArrayList<CityNameItem> cityNameItems,
                                 LinearLayout llMainCityContainer, LinearLayout llProgressbarContainerCity, LinearLayout llErrorInfoCity) {
        this.context = context;
        this.cityNameItems = cityNameItems;
        this.cityViewHoldersItems = new ArrayList<>();
        this._pref = new Pref(context);
        this._connectionCheck = new NetworkConnectionCheck(context);
        this._serviceAction = new CallServiceAction(context);
        this._serviceAction.actionInterface = ChangeCityNameAdapter.this;
        this._storeInLocal = new StoreInLocal(context);
        this.llMainCityContainer = llMainCityContainer;
        this.llProgressbarContainerCity = llProgressbarContainerCity;
        this.llErrorInfoCity =llProgressbarContainerCity;
    }

    @Override
    public CityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(context).inflate(R.layout.city_name_row_layout, parent, false);
        CityViewHolder holder = new CityViewHolder(rowView);
        return holder;
    }

    @Override
    public void onBindViewHolder(CityViewHolder holder, int position) {
        holder.tvChangeCityName.setText(cityNameItems.get(position).getCityName());
        if(_pref.getSession(ConstantClass.TAG_SELECTED_CITY_LOCATION).equalsIgnoreCase(cityNameItems.get(position).getCityName())){
            holder.ivChangeCitySelect.setSelected(true);
        }
        holder.ivChangeCitySelect.setTag(holder);
        holder.ivChangeCitySelect.setOnClickListener(onClickListenerIv);
        holder.rlCitySelect.setTag(holder);
        holder.rlCitySelect.setOnClickListener(onClickListenerRl);

        cityViewHoldersItems.add(holder);
    }

    @Override
    public int getItemCount() {
        return cityNameItems.size();
    }

    @Override
    public void onStarted() {

    }

    @Override
    public void onCompleted(JSONObject response) {
        Log.d("Response",response.toString());
        _storeInLocal.saveInLocalFile(response, _pref.getSession(ConstantClass.TAG_HOME_MENU_JSON_FILE));
        llMainCityContainer.setVisibility(View.VISIBLE);
        llProgressbarContainerCity.setVisibility(View.GONE);

    }

    public class CityViewHolder extends RecyclerView.ViewHolder{
        TextView tvChangeCityName;
        ImageView ivChangeCitySelect;
        RelativeLayout rlCitySelect;
        public CityViewHolder(View itemView) {
            super(itemView);
            tvChangeCityName = (TextView) itemView.findViewById(R.id.tvChangeCityName);
            ivChangeCitySelect = (ImageView) itemView.findViewById(R.id.ivChangeCitySelect);
            rlCitySelect = (RelativeLayout) itemView.findViewById(R.id.rlCitySelect);
        }
    }

    View.OnClickListener onClickListenerIv = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(_connectionCheck.isNetworkAvailable()) {
                CityViewHolder holder = (CityViewHolder) view.getTag();
                int position = holder.getAdapterPosition();
                for (int i = 0; i < cityNameItems.size(); i++) {
                    cityNameItems.get(i).setSelected(false);
                    cityViewHoldersItems.get(i).ivChangeCitySelect.setSelected(false);
                }
                changeCityService( String.valueOf(cityNameItems.get(position).getId()));
                _pref.setSession(ConstantClass.TAG_SELECTED_CITY_LOCATION, cityNameItems.get(position).getCityName());
                _pref.setSession(ConstantClass.TAG_SELECTED_CITY_ID, String.valueOf(cityNameItems.get(position).getId()));

                String.valueOf(cityNameItems.get(position).getId());
                holder.ivChangeCitySelect.setSelected(true);
                cityNameItems.get(position).setSelected(true);
                callForHomeMenu();
                llMainCityContainer.setVisibility(View.GONE);
                llProgressbarContainerCity.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(context, "Please check your internet connection.", Toast.LENGTH_LONG).show();
            }
        }
    };

    View.OnClickListener onClickListenerRl = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.d("farman","view clicked");
            if(_connectionCheck.isNetworkAvailable()) {
                CityViewHolder holder = (CityViewHolder) view.getTag();
                int position = holder.getAdapterPosition();
                for (int i = 0; i < cityNameItems.size(); i++) {
                    cityNameItems.get(i).setSelected(false);
                    cityViewHoldersItems.get(i).ivChangeCitySelect.setSelected(false);
                }
                _pref.setSession(ConstantClass.TAG_SELECTED_CITY_LOCATION, cityNameItems.get(position).getCityName());
                _pref.setSession(ConstantClass.TAG_SELECTED_CITY_ID, String.valueOf(cityNameItems.get(position).getId()));

                changeCityService( String.valueOf(cityNameItems.get(position).getId()));
                holder.ivChangeCitySelect.setSelected(true);
                cityNameItems.get(position).setSelected(true);
                llMainCityContainer.setVisibility(View.GONE);
                llProgressbarContainerCity.setVisibility(View.VISIBLE);
                callForHomeMenu();
            } else {
                Toast.makeText(context, "Please check your internet connection.", Toast.LENGTH_LONG).show();
            }
        }
    };

    private void changeCityService(String s) {

        JSONObject jsonObject1 = new JSONObject();
        JSONObject jsonObject2 = new JSONObject();

        try {
            jsonObject2.put("accessToken",_pref.getSession(ConstantClass.ACCESS_TOKEN));
            jsonObject2.put("cityId",s);

            jsonObject1.put("data",jsonObject2);

            _serviceAction.requestVersionApi(jsonObject1, changeUsear);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

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
