package com.ekplate.android.fragments.homemodule;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ekplate.android.R;
import com.ekplate.android.adapters.homemodule.DiscoverItemListAdapter;
import com.ekplate.android.models.homemodule.DiscoverListItem;
import com.ekplate.android.utils.ConstantClass;
import com.ekplate.android.utils.Pref;
import com.ekplate.android.utils.StoreInLocal;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class DiscoverFragment extends Fragment {

    private RecyclerView rvDiscoverList;
    private DiscoverItemListAdapter itemAdapter;
    private ArrayList<DiscoverListItem> arrayList;
    private StoreInLocal _storeInLocal;
    private Pref _pref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_discover, container, false);
        initialize(rootView);
        rvDiscoverList.setAdapter(itemAdapter);
        loadListItem();
        onClick();
        return rootView;
    }

    private void initialize(View rootView){
        rvDiscoverList = (RecyclerView) rootView.findViewById(R.id.rvDiscoverList);
        rvDiscoverList.setLayoutManager(new LinearLayoutManager(getActivity()));
        arrayList = new ArrayList<DiscoverListItem>();
        itemAdapter = new DiscoverItemListAdapter(getActivity(), arrayList);
        _storeInLocal = new StoreInLocal(getActivity());
        _pref = new Pref(getActivity());
    }

    private void loadListItem(){
        try {
            String homeMenuJsonStr = _storeInLocal.getStringFromFile(_pref.getSession(ConstantClass.TAG_HOME_MENU_JSON_FILE));
            JSONObject homeMenuJsonObj = new JSONObject(homeMenuJsonStr);
            JSONObject dataJsonObj = homeMenuJsonObj.getJSONObject("data");
            JSONArray headerJsonArray = dataJsonObj.getJSONArray("header");
            JSONObject discoverOptionJsonObj = headerJsonArray.getJSONObject(2);
            JSONArray optionJsonArray = discoverOptionJsonObj.getJSONArray("option");
            for (int i = 0; i < optionJsonArray.length(); i++) {
                DiscoverListItem _item = new DiscoverListItem();
                JSONObject optionItemJsonObj = optionJsonArray.getJSONObject(i);
                _item.setId(optionItemJsonObj.getInt("id"));
                _item.setTitle(optionItemJsonObj.getString("title"));
                _item.setKey(optionItemJsonObj.getString("type"));
                switch (i) {
                    case 0:
                        _item.setImageUrl(R.drawable.icon_gaidos_picks);
                        break;
                    case 1:
                        _item.setImageUrl(R.drawable.icon_help_me);
                        break;
                    case 2:
                        _item.setImageUrl(R.drawable.icon_social_feeds);
                        break;
                    case 3:
                        _item.setImageUrl(R.drawable.icon_discover_more);
                        break;
                }
                arrayList.add(_item);
            }
            itemAdapter.notifyDataSetChanged();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        arrayList.clear();
        loadListItem();
    }

    private void onClick(){


    }

}
