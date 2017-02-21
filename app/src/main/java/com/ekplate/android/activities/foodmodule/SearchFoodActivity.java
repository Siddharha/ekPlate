package com.ekplate.android.activities.foodmodule;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.ekplate.android.R;
import com.ekplate.android.activities.searchmodule.SearchActivity;
import com.ekplate.android.adapters.foodmodule.SearchFoodAdapter;
import com.ekplate.android.config.BaseActivity;

public class SearchFoodActivity extends BaseActivity {

    private Toolbar tbSearchFood;
    private ViewPager vpSearchItemType;
    private SearchFoodAdapter searchFoodAdapter;
    private TabLayout tabLayoutSearchFood;
    private TextView toolbarHeaderText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_food);
        getWindow().setBackgroundDrawable(null);
        getSupportActionBar().hide();
        initialize();
        setUpToolBar();
        setUpPager();
    }

    private void initialize() {
        tbSearchFood = (Toolbar)findViewById(R.id.tbSearchFood);
        vpSearchItemType = (ViewPager) findViewById(R.id.vpSearchItemType);
        tabLayoutSearchFood = (TabLayout) findViewById(R.id.tabLayoutSearchFood);
        toolbarHeaderText = (TextView) findViewById(R.id.toolbarHeaderText);
        Log.e("tagIds", getIntent().getExtras().getString("tagIds"));
        searchFoodAdapter = new SearchFoodAdapter(getSupportFragmentManager(), this, 2,
                getIntent().getExtras().getInt("optionId"), getIntent().getExtras().getString("tagIds"),getIntent().getExtras().getString("food_category_type"));
    }

    private void setUpToolBar(){
        tbSearchFood.inflateMenu(R.menu.menu_search_food);
        tbSearchFood.setNavigationIcon(R.drawable.ic_action_back);
        toolbarHeaderText.setText("SEARCH FOOD");
        tbSearchFood.setBackground(getResources().getDrawable(R.drawable.toolbar_bg));
        tbSearchFood.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        tbSearchFood.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.iconSearchFood:
                        Intent intent = new Intent(SearchFoodActivity.this, SearchActivity.class);
                        intent.putExtra("searchType", "food");
                        startActivity(intent);
                        break;
                }
                return false;
            }
        });
    }

    private void setUpPager(){
        vpSearchItemType.setAdapter(searchFoodAdapter);
        tabLayoutSearchFood.setupWithViewPager(vpSearchItemType);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
