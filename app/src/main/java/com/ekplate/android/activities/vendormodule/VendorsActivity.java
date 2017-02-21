package com.ekplate.android.activities.vendormodule;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ekplate.android.R;
import com.ekplate.android.activities.addvendormodule.Action;
import com.ekplate.android.activities.addvendormodule.CustomGallery;
import com.ekplate.android.activities.searchmodule.SearchActivity;
import com.ekplate.android.adapters.vendormodule.VendorCategoryAdapter;
import com.ekplate.android.appinterfaces.BackgroundActionInterface;
import com.ekplate.android.config.BaseActivity;
import com.ekplate.android.models.vendormodule.VendorItem;
import com.ekplate.android.utils.AsyncTaskListener;
import com.ekplate.android.utils.CallServiceAction;
import com.ekplate.android.utils.CommonFunction;
import com.ekplate.android.utils.CommonMethods;
import com.ekplate.android.utils.ConstantClass;
import com.ekplate.android.utils.PostObject;
import com.ekplate.android.utils.Pref;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;


public class VendorsActivity extends BaseActivity implements BackgroundActionInterface{
    private ViewPager vpVendorCategoryType;
    private String[] tabHeaderTitle = {"Nearby", "Open", "Rating", "Bookmarks", "Likes"};
    private VendorCategoryAdapter categoryAdapter;
    private TabLayout tabLayoutVendor;
    private Toolbar tbVendors;
    private TextView toolbarHeaderText;
    public static ArrayList<VendorItem> nearVendorItems = new ArrayList<>();
    public static ArrayList<VendorItem> ratingVendorItems = new ArrayList<>();
    public static ArrayList<VendorItem> bookmarkVendorItems = new ArrayList<>();
    public static ArrayList<VendorItem> likesVendorItems = new ArrayList<>();
    public static ArrayList<VendorItem> openVendorItems = new ArrayList<>();
    ArrayAdapter<String> myAdapter;
    ListView listView;
    String[] dataArray = new String[] {"India","Androidhub4you", "Pakistan", "Srilanka", "Nepal", "Japan"};
    private static final int REQUEST_CAMERA = 200;
    private CommonMethods _commonMethods;
    private Context context;
    public static int vendorId;
    public static ArrayList<CustomGallery> dataT = new ArrayList<CustomGallery>();
    private Pref _pref;
    private CommonFunction _comFunc;
    private ProgressDialog progressDialog;
    public static int flagImageSelected = 0;
    private int currentScreenIndex;
    private CallServiceAction _serviceAction;
    private String captureImagePath;
    private String keyValue, routeFrom, prefixKeyValue, vendorListType;
    private String ADD_SEARCH_COUNT = "add-search-count";
    public VendorsActivity(Context context) {
        this.context = context;
    }

    public VendorsActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendors);
        getWindow().setBackgroundDrawable(null);
        getSupportActionBar().hide();
        initialize();

        setUpToolbar();
        //setSupportActionBar(tbVendors);
        setUpMostSearchIncrementParamForFood();
    }

    private void initialize(){
        progressDialog = new ProgressDialog(this);
        vpVendorCategoryType = (ViewPager) findViewById(R.id.vpVendorCategoryType);
        categoryAdapter = new VendorCategoryAdapter(getSupportFragmentManager(), this, tabHeaderTitle,
                getIntent().getExtras().getInt("optionId"), getIntent().getExtras().getString("keyValue"),
                getIntent().getExtras().getString("routeFrom"),getIntent().getExtras().getString("foodCategoryType") );
        keyValue = getIntent().getExtras().getString("keyValue");
        tbVendors = (Toolbar) findViewById(R.id.tbVendors);
        _serviceAction = new CallServiceAction(this);
        _serviceAction.actionInterface = VendorsActivity.this;
        toolbarHeaderText = (TextView) findViewById(R.id.toolbarHeaderText);
        tabLayoutVendor = (TabLayout) findViewById(R.id.tabLayoutVendor);
        //listView = (ListView) findViewById(R.id.listview);
        myAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataArray);
        /*listView.setAdapter(myAdapter);
        listView.setTextFilterEnabled(true);*/
        _commonMethods = new CommonMethods(VendorsActivity.this);
        _pref = new Pref(this);
        _comFunc = new CommonFunction(this);
    }

    private void setListener(){
        vpVendorCategoryType.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentScreenIndex = position;

                getVendorScreenType(position);
                if (!getIntent().getExtras().getString("routeFrom").equalsIgnoreCase("home")) {
                    if (keyValue.equalsIgnoreCase("vendor")) {
                        keyValue = "";
                        keyValue = prefixKeyValue;

                        keyValue = prefixKeyValue + "-" + keyValue;
                        Log.e("//",keyValue);
                        String[] p = keyValue.split("-");
                        if(p.length >=3)
                        {
                            keyValue = p[p.length-2]+"-"+p[p.length-1];
                            keyValue = prefixKeyValue + "-" + keyValue;
                        }
                    } else {
                        keyValue = prefixKeyValue + "-" + keyValue;
                        Log.e("//",keyValue);
                        String[] p = keyValue.split("-");
                        if(p.length >=3)
                        {
                            keyValue = p[p.length-2]+"-"+p[p.length-1];
                            keyValue = prefixKeyValue + "-" + keyValue;
                        }

                    }
                } else {
                    keyValue = "";
                    keyValue = prefixKeyValue;
                }

               // keyValue = prefixKeyValue;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setUpToolbar(){
        tbVendors.inflateMenu(R.menu.menu_vendors);
        tbVendors.setNavigationIcon(R.drawable.ic_action_back);
        toolbarHeaderText.setText("VENDORS");
        tbVendors.setBackground(getResources().getDrawable(R.drawable.toolbar_bg));
        tbVendors.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        tbVendors.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.iconVendorMap:
                       /* keyValue = "nearbymap";*/
                        setInputParamForFullList();
                        setUpProgressDialog();
                        break;
                    case R.id.iconSearchVendor:
                        Intent intentSearch = new Intent(VendorsActivity.this, SearchActivity.class);
                        intentSearch.putExtra("searchType", "vendor");
                        startActivity(intentSearch);
                        break;
                }
                return false;
            }
        });
    }

    private void setInputParamForFullList(){

        nearVendorItems.clear();
        ratingVendorItems.clear();
        openVendorItems.clear();
        bookmarkVendorItems.clear();
        likesVendorItems.clear();


        try {
            JSONObject parentJsonObj = new JSONObject();
            JSONObject childJsonObj = new JSONObject();
            childJsonObj.put("accessToken", _pref.getSession(ConstantClass.ACCESS_TOKEN));
            childJsonObj.put("city", _pref.getSession(ConstantClass.TAG_SELECTED_CITY_LOCATION));
            childJsonObj.put("foodType", _pref.getSession(ConstantClass.TAG_SELECTED_FOOD_TYPE));
            childJsonObj.put("keyPage", keyValue);
            childJsonObj.put("optionId", getIntent().getExtras().getInt("optionId"));
            //childJsonObj.put("currentPage", "0");
            childJsonObj.put("food_category_type",getIntent().getExtras().getString("foodCategoryType"));
            childJsonObj.put("latitude", _pref.getSession(ConstantClass.TAG_LATITUDE));
            childJsonObj.put("longitude", _pref.getSession(ConstantClass.TAG_LONGITUDE));
            parentJsonObj.put("data", childJsonObj);
            Log.e("input data", parentJsonObj.toString());
            _serviceAction.requestVersionApi(parentJsonObj, "get-vendor-map");
        } catch (Exception e){
            e.printStackTrace();
        }
    }           //For Tomorrow Work _Siddhartha Maji

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_vendors, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.iconSearchVendor).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        Log.e("on menu create", "checking");
        SearchView.OnQueryTextListener textChangeListener = new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextChange(String newText)
            {
                // this is your adapter that will be filtered
                myAdapter.getFilter().filter(newText);
                System.out.println("on text chnge text: "+newText);
                return true;
            }
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                // this is your adapter that will be filtered
                myAdapter.getFilter().filter(query);
                System.out.println("on query submit: "+query);
                return true;
            }
        };
        searchView.setOnQueryTextListener(textChangeListener);

        return super.onCreateOptionsMenu(menu);
    }*/

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    protected void onResume() {
        setListener();
        vpVendorCategoryType.setAdapter(categoryAdapter);
        tabLayoutVendor.setupWithViewPager(vpVendorCategoryType);
        super.onResume();
        if (flagImageSelected == 1){
            flagImageSelected = 0;
            postSelectedImage();
        }
    }

    private void getVendorScreenType(int screenPosition){
        switch (screenPosition) {
            case 0:
                prefixKeyValue = "nearByList";
                vendorListType = "nearby";
                break;
            case 1:
                prefixKeyValue = "openList";
                vendorListType = "open";
                break;
            case 2:
                prefixKeyValue = "ratingList";
                vendorListType = "rating";
                break;
            case 3:
                prefixKeyValue = "bookmark";
                vendorListType = "booking";
                break;
            default:
                prefixKeyValue = "likeList";
                vendorListType = "likes";
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
            /*Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            String bitmap_path = MediaStore.Images.Media.insertImage(getContentResolver(),
                    bitmap, "Title", null);
            Log.e("bitmap_path", bitmap_path);
            Uri contentUri = Uri.parse(bitmap_path);
            Cursor cursor = getContentResolver().query(contentUri, null,
                    null, null, null);
            data.getData();
            if (cursor == null) {
                captureImagePath = contentUri.getPath();
            } else {
                cursor.moveToFirst();
                int index = cursor
                        .getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                captureImagePath = cursor.getString(index);
            }*/
            Log.e("capturedImagePath in", ">>>" + ConstantClass.TAG_CAPTURED_IMAGE_PATH);
            postSelectedImage();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }



    public void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Gallery", "Cancel" };
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    dataT.clear();
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    _commonMethods = new CommonMethods(VendorsActivity.this);
                    File imageFile = _commonMethods.getOutputMediaFile();
                    captureImagePath = imageFile.getAbsolutePath();
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
                    Log.e("capturedImagePath in", ">>>" + captureImagePath);
                    ConstantClass.TAG_CAPTURED_IMAGE_PATH = captureImagePath;
                    ((VendorsActivity) context).startActivityForResult(intent, REQUEST_CAMERA);
                } else if (items[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent(Action.ACTION_MULTIPLE_PICK);
                    intent.putExtra("routeFrom", "vendor_list");
                    ((VendorsActivity) context).startActivityForResult(intent, 100);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void setUpProgressDialog(){
        progressDialog.setMessage("Please wait..");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
    }

    private void postSelectedImage(){
        setUpProgressDialog();
        HashMap<String, PostObject> _postMap = new HashMap<String, PostObject>();

        _postMap.put("accessToken", _comFunc.getPostObject(
                String.valueOf(_pref.getSession(ConstantClass.ACCESS_TOKEN)), false));

        _postMap.put("vendorId", _comFunc.getPostObject(
                String.valueOf(vendorId), false));

        //_postMap.put("vendorCaption", _comFunc.getPostObject(String.valueOf(" "), false));
        _postMap.put("vendorCaption", _comFunc.getPostObject("", false));

        Log.v("vendorId", String.valueOf(vendorId));

        if (dataT.size()>0){
            _postMap.put("no_image", _comFunc.getPostObject(
                    String.valueOf(dataT.size()),
                    false));

            for (int i = 0; i < dataT.size(); i++) {
                Log.e("image path", dataT.get(i).sdcardPath);
                _postMap.put("image_" + i, _comFunc.getPostObject(dataT.get(i).sdcardPath, true));
            }
        } else {
            _postMap.put("no_image", _comFunc.getPostObject(
                    String.valueOf(1), false));
            Log.e("capturedImagePath", ">>>>" + ConstantClass.TAG_CAPTURED_IMAGE_PATH);
            _postMap.put("image_0", _comFunc.getPostObject(ConstantClass.TAG_CAPTURED_IMAGE_PATH, true));
        }
        _comFunc.callPostWebservice(ConstantClass.BASE_URL + "vendor-add-image", _postMap, _profileChangeAsync, true);
    }

    AsyncTaskListener _profileChangeAsync = new AsyncTaskListener() {

        @Override
        public void onTaskPreExecute() {
            // TODO Auto-generated method stub

        }

        @Override
        public void onTaskCompleted(String result) {
            // TODO Auto-generated method stub
            Log.e("result", result);
            progressDialog.dismiss();
            ConstantClass.TAG_CAPTURED_IMAGE_PATH = "";
            Toast.makeText(VendorsActivity.this, "Gallery image submitted successfully", Toast.LENGTH_LONG).show();
        }
    };

    private ArrayList<VendorItem> setVendorMapItem(){
        switch (currentScreenIndex) {
            case 0:
                return nearVendorItems;
            case 1:
                return openVendorItems;
            case 2:
                return ratingVendorItems;
            case 3:
                return bookmarkVendorItems;
            default:
                return likesVendorItems;
        }
    }

    private void setUpMostSearchIncrementParamForFood(){
        try {
            JSONObject parentJsonObj = new JSONObject();
            JSONObject childJsonObj = new JSONObject();
            JSONObject childJsonObj2 = new JSONObject();
            childJsonObj.put("accessToken", _pref.getSession(ConstantClass.ACCESS_TOKEN));
            childJsonObj.put("foodId", getIntent().getExtras().getInt("optionId"));
            childJsonObj.put("city", _pref.getSession(ConstantClass.TAG_SELECTED_CITY_LOCATION));
            parentJsonObj.put("data", childJsonObj);
            _serviceAction.requestVersionApi(parentJsonObj, "add-search-count");
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void onStarted() {

    }

    @Override
    public void onCompleted(JSONObject response) {
        Log.v("rating response", response.toString());
        if(progressDialog.isShowing())
        {
            progressDialog.dismiss();
        }
        try {
            JSONObject jsonObjError = response.getJSONObject("errNode");
            if(jsonObjError.getInt("errCode") == 0) {
                JSONObject jsonObjData = response.getJSONObject("data");
                JSONArray jsonArrListItem = jsonObjData.getJSONArray("listItem");
                //totalPage = jsonObjData.getInt("totalPage");
                if (jsonArrListItem.length() > 0) {
                    Log.e("jsonArrListItem length ", String.valueOf(jsonArrListItem.length()));
                    /*if(jsonArrListItem.length() < 30) {
                        currentPage = 1;
                    }*/
                    for (int i = 0; i < jsonArrListItem.length(); i++) {
                    /*for (int i = 0; i < 100; i++) {*/
                        JSONObject jsonObjListItem = jsonArrListItem.getJSONObject(i);
                        VendorItem _item = new VendorItem();
                        _item.setId(jsonObjListItem.getInt("id"));
                        _item.setLatitude(Double.parseDouble(jsonObjListItem.getString("latitude")));
                        _item.setLongitude(Double.parseDouble(jsonObjListItem.getString("longitude")));
                        _item.setRating(jsonObjListItem.getString("rating"));
                        //region Extra...
                        /*_item.setVendorName(jsonObjListItem.getString("vendorName"));
                        _item.setInlineAddress(jsonObjListItem.getString("inlineAddress"));
                        _item.setInlineFoodMenu(jsonObjListItem.getString("inlineFoodMenu"));
                        _item.setOpenStatus(jsonObjListItem.getBoolean("openStatus"));
                        _item.setNoOfReviews(jsonObjListItem.getString("noOfReviews"));
                        _item.setNoOfLikes(jsonObjListItem.getString("noOfLikes"));
                        _item.setInnerCircleIcon(R.drawable.icon_vendor_list_location);
                        _item.setInnerCircleText(jsonObjListItem.getString("distance") + "KM");
                        _item.setInnerCircleIcon(R.drawable.icon_vendor_list_reviews);
                        _item.setInnerCircleText(jsonObjListItem.getString("noOfReviews") + " Reviews");
                        _item.setInnerCircleIcon(R.drawable.icon_vendor_list_likes);
                        _item.setInnerCircleText(jsonObjListItem.getString("noOfLikes") + " Likes");
                        _item.setRating(jsonObjListItem.getString("rating"));
                        _item.setDistance(jsonObjListItem.getString("distance"));
                        //_item.setBookmarkStatus(jsonObjListItem.getBoolean("bookmarkStatus"));
                        //_item.setFoodType(jsonObjListItem.getString("foodType"));
                        _item.setLatitude(Double.parseDouble(jsonObjListItem.getString("latitude")));
                        _item.setLongitude(Double.parseDouble(jsonObjListItem.getString("longitude")));
                        _item.setLocationPositionId("(" + jsonObjListItem.getString("latitude") + ","
                                + jsonObjListItem.getString("longitude") + ")");
                       // _item.setVendorDetails(jsonObjListItem.getJSONObject("vendorDetails").toString());*/

                        //endregion
                        nearVendorItems.add(_item) ;
                        bookmarkVendorItems.add(_item) ;
                        ratingVendorItems.add(_item) ;
                        openVendorItems.add(_item) ;
                        likesVendorItems.add(_item);
                        // pbPagination.setVisibility(View.GONE);
                    }

                    Log.v("size", String.valueOf(nearVendorItems.size()));

                    Intent intentMap = new Intent(VendorsActivity.this, VendorMapActivity.class);
                    ConstantClass.vendorItemsList = setVendorMapItem();
                    //intentMap.putExtra("vendor_info_List", setVendorMapItem());
                    startActivity(intentMap);

                    // setVendorListForMap(vendorItemsForMap);
                   // itemAdapter.notifyDataSetChanged();
                   // llProgressbarVendorRatingList.setVisibility(View.GONE);
                   // rvRatingVendorList.setVisibility(View.VISIBLE);
                   // loading = true;
                } else {
                  //  tvErrorTwoMessage.setText(R.string.message_on_data_found);
                    //llProgressbarVendorRatingList.setVisibility(View.GONE);
                    //ivLoaderErrorTwo.setVisibility(View.GONE);
                    //llErrorTwoVendorRatingListLayout.setVisibility(View.VISIBLE);

                }
            } else {
                //Toast.makeText(VendorsActivity.this, jsonObjError.getString("errMsg"), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }


}
