package com.ekplate.android.fragments.homemodule;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ekplate.android.R;
import com.ekplate.android.adapters.homemodule.CategoryItemListAdapter;
import com.ekplate.android.models.homemodule.CategoryListItem;
import com.ekplate.android.utils.ConstantClass;
import com.ekplate.android.utils.Pref;
import com.ekplate.android.utils.StoreInLocal;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class CategoriesFragment extends Fragment {

    private RecyclerView rvCategoryList;
    private CategoryItemListAdapter itemAdapter;
    private ArrayList<CategoryListItem> arrayList;
    private StoreInLocal _storeInLocal;
    private Pref _pref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_categories, container, false);
        initialize(rootView);
        rvCategoryList.setAdapter(itemAdapter);
        loadListItem();
        onClick();
        return rootView;
    }

    private void initialize(View rootView){
        rvCategoryList = (RecyclerView) rootView.findViewById(R.id.rvCategoryList);
        rvCategoryList.setLayoutManager(new LinearLayoutManager(getActivity()));
        arrayList = new ArrayList<CategoryListItem>();
        itemAdapter = new CategoryItemListAdapter(getActivity(), arrayList);
        _storeInLocal = new StoreInLocal(getActivity());
        _pref = new Pref(getActivity());
    }

    private void loadListItem(){
        try {
            String homeMenuJsonStr = _storeInLocal.getStringFromFile(_pref.getSession(ConstantClass.TAG_HOME_MENU_JSON_FILE));
            JSONObject homeMenuJsonObj = new JSONObject(homeMenuJsonStr);
            JSONObject dataJsonObj = homeMenuJsonObj.getJSONObject("data");
            JSONArray headerJsonArray = dataJsonObj.getJSONArray("header");
            JSONObject categoryOptionJsonObj = headerJsonArray.getJSONObject(1);
            JSONArray optionJsonArray = categoryOptionJsonObj.getJSONArray("option");
            for (int i = 0; i < optionJsonArray.length(); i++) {
                CategoryListItem _item = new CategoryListItem();
                JSONObject optionItemJsonObj = optionJsonArray.getJSONObject(i);
                _item.setId(optionItemJsonObj.getInt("id"));
                _item.setTitle(optionItemJsonObj.getString("title"));
                _item.setKey(optionItemJsonObj.getString("type"));
                _item.setTagIds(optionItemJsonObj.getString("tag_ids"));
                switch (i) {
                    case 0:
                        _item.setImageUrl(R.drawable.icon_breakfast);
                        break;
                    case 1:
                        _item.setImageUrl(R.drawable.icon_quick_bites);
                        break;
                    case 2:
                        _item.setImageUrl(R.drawable.icon_meal);
                        break;
                    case 3:
                        _item.setImageUrl(R.drawable.icon_late_night);
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
