package com.ekplate.android.fragments.homemodule;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ekplate.android.R;
import com.ekplate.android.adapters.homemodule.HungryItemListAdapter;
import com.ekplate.android.fragments.menumodule.MyStuffBookmarksFragment;
import com.ekplate.android.models.homemodule.HungryListItem;
import com.ekplate.android.utils.ConstantClass;
import com.ekplate.android.utils.Pref;
import com.ekplate.android.utils.StoreInLocal;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class HungryFragment extends Fragment {

    private RecyclerView rvHungryList;
    private HungryItemListAdapter itemAdapter;
    private ArrayList<HungryListItem> arrayList;
    private StoreInLocal _storeInLocal;
    private Pref _pref;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_hungry, container, false);
        initialize(rootView);
        rvHungryList.setAdapter(itemAdapter);

        try {
            if(!_storeInLocal.getStringFromFile(_pref.getSession(ConstantClass.TAG_HOME_MENU_JSON_FILE)).isEmpty()) {
                loadListItem();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        onClick();
        return rootView;
    }

    private void initialize(View rootView){
        rvHungryList = (RecyclerView) rootView.findViewById(R.id.rvHungryList);
        rvHungryList.setLayoutManager(new LinearLayoutManager(getActivity()));
        arrayList = new ArrayList<HungryListItem>();
        itemAdapter = new HungryItemListAdapter(getActivity(), arrayList);
        _storeInLocal = new StoreInLocal(getActivity());
        _pref = new Pref(getActivity());
    }

    private void loadListItem(){
        try {
            String homeMenuJsonStr = _storeInLocal.getStringFromFile(_pref.getSession(ConstantClass.TAG_HOME_MENU_JSON_FILE));
            Log.e("Hungry list", homeMenuJsonStr);
            JSONObject homeMenuJsonObj = new JSONObject(homeMenuJsonStr);
            JSONObject dataJsonObj = homeMenuJsonObj.getJSONObject("data");
            JSONArray headerJsonArray = dataJsonObj.getJSONArray("header");
            JSONObject hungryOptionJsonObj = headerJsonArray.getJSONObject(0);
            JSONArray optionJsonArray = hungryOptionJsonObj.getJSONArray("option");
            for (int i = 0; i < optionJsonArray.length(); i++) {
                HungryListItem _item = new HungryListItem();
                JSONObject optionItemJsonObj = optionJsonArray.getJSONObject(i);
                _item.setId(optionItemJsonObj.getInt("id"));
                _item.setTitle(optionItemJsonObj.getString("title"));
                _item.setKey(optionItemJsonObj.getString("type"));
                switch (i) {
                    case 0:
                        _item.setImageUrl(R.drawable.icon_search_food);
                        break;
                    case 1:
                        _item.setImageUrl(R.drawable.icon_near_places);
                        break;
                    case 2:
                        _item.setImageUrl(R.drawable.icon_searchby_vendor);
                        HomeFragment.optionId = String.valueOf(optionItemJsonObj.getInt("id"));
                        HomeFragment.keyValue = optionItemJsonObj.getString("type");
                        MyStuffBookmarksFragment.optionId = String.valueOf(optionItemJsonObj.getInt("id"));
                        break;
                    case 3:
                        _item.setImageUrl(R.drawable.icon_near_college);
                        break;
                }
                arrayList.add(_item);
            }
            itemAdapter.notifyDataSetChanged();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void onClick(){

    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
