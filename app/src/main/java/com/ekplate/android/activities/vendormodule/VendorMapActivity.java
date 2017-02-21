package com.ekplate.android.activities.vendormodule;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ekplate.android.R;
import com.ekplate.android.adapters.vendormodule.ClusterPopupListAdapter;
import com.ekplate.android.appinterfaces.BackgroundActionInterface;
import com.ekplate.android.config.BaseActivity;
import com.ekplate.android.models.vendormodule.ClusterPopupListItem;
import com.ekplate.android.models.vendormodule.VendorItem;
import com.ekplate.android.utils.CallServiceAction;
import com.ekplate.android.utils.ClusterPopupList;
import com.ekplate.android.utils.CommonMethods;
import com.ekplate.android.utils.ConstantClass;
import com.ekplate.android.utils.Pref;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by user on 04-11-2015.
 */
public class VendorMapActivity extends BaseActivity
        implements ClusterManager.OnClusterItemInfoWindowClickListener<ClusterPopupList>, BackgroundActionInterface {

    private Toolbar tbVendorMap;
    private TextView toolbarHeaderText, tvNoOfVendorShowText;
    private MapView mvVendorList;
    private RecyclerView rvClusterItemPopup;
    private ArrayList<ClusterPopupListItem> clusterPopupListItems;
    private ClusterPopupListAdapter clusterPopupListAdapter;
    private PopupWindow infoPopup;
    private GoogleMap map;
    private Bundle savedInstanceState;
    private Pref _pref;
    private ClusterManager<ClusterPopupList> mClusterManager;
    private ClusterPopupList clickedClusterItem;
    private ImageView mImageView = null;
    private float zoomLevel;
    int zoomLevel_2;
    private boolean isSinglePopupOpen;
    private LinearLayout llMapContainer;
    public static ArrayList<VendorItem> vendorItemsList;
    private CommonMethods _commonMethods;
    private int clusterPopupOpenFlag = 0, singleItemPopupOpenFlag = 0;
    private CallServiceAction _callServiceAction;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_map);
        getSupportActionBar().hide();
        getWindow().setBackgroundDrawable(null);
        this.savedInstanceState = savedInstanceState;
        initialize();
        setUpToolBar();
        setUpMapView();
    }

    private void initialize() {
        _pref = new Pref(this);
        llMapContainer = (LinearLayout) findViewById(R.id.llMapContainer);
        if (getIntent().hasExtra("vendor_info_List")) {
            vendorItemsList = (ArrayList<VendorItem>) getIntent()
                    .getExtras().getSerializable("vendor_info_List");
        } else {
            vendorItemsList = ConstantClass.vendorItemsList;
        }
        _commonMethods = new CommonMethods(this);
        tvNoOfVendorShowText = (TextView) findViewById(R.id.tvNoOfVendorShowText);
        zoomLevel_2 = 0;
        isSinglePopupOpen = false;

        _callServiceAction = new CallServiceAction(this);
        _callServiceAction.actionInterface = this;
    }

    private void setUpToolBar() {
        tbVendorMap = (Toolbar) findViewById(R.id.tbVendorMap);
        toolbarHeaderText = (TextView) findViewById(R.id.toolbarHeaderText);
        tbVendorMap.setNavigationIcon(R.drawable.ic_action_back);
        tbVendorMap.setBackground(getResources().getDrawable(R.drawable.toolbar_bg));
        toolbarHeaderText.setText("CURRENT LOCATION");
        tbVendorMap.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setUpMapView() {
        mvVendorList = (MapView) findViewById(R.id.mvVendorList);
        mvVendorList.onCreate(savedInstanceState);
        mvVendorList.onResume();
        map = mvVendorList.getMap();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        map.setMyLocationEnabled(true);
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        MapsInitializer.initialize(this);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setCompassEnabled(true);
        mClusterManager = new ClusterManager<ClusterPopupList>(this, map);
        mClusterManager.setRenderer(new ItemRenderer());
        map.setOnCameraChangeListener(mClusterManager);
        map.setOnMarkerClickListener(mClusterManager);
        map.setInfoWindowAdapter(mClusterManager.getMarkerManager());
        map.setOnInfoWindowClickListener(mClusterManager);
        mClusterManager.setOnClusterItemInfoWindowClickListener(this);
        mClusterManager.setOnClusterClickListener(clusterClickListener);
        mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<ClusterPopupList>() {
            @Override
            public boolean onClusterItemClick(ClusterPopupList clusterPopupList) {
                clickedClusterItem = clusterPopupList;
                isSinglePopupOpen = true;
                loadMapVendorDetails(clusterPopupList.getMarkerIndex());
                return false;
            }
        });


        addItems();
        //mClusterManager.getMarkerCollection().setOnInfoWindowAdapter(new CustomAdapterForItems());
       /* mClusterManager.getMarkerCollection().setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
               // Log.e("title", marker.getTitle());
                for (int i=0; i<vendorItemsList.size(); i++) {
                    VendorItem _item = vendorItemsList.get(i);
                    Log.e("item position", _item.getLocationPositionId());
                    if (marker.getPosition().toString().equalsIgnoreCase(_item.getLocationPositionId())) {
                        initializeSingleVendorItemPopup(_item);
                    }
                }
                initializeSingleVendorItemPopup(_item);
                moveToBottom(marker.getPosition());
                return false;
            }
        });*/
        mClusterManager.cluster();
        HashMap<String, String> locationDetails = _commonMethods.getCoordinatesFromAddress(
                _pref.getSession(ConstantClass.TAG_SELECTED_CITY_LOCATION));
        LatLng currentCityLatLng = new LatLng(Double.parseDouble(_pref.getSession(ConstantClass.TAG_LATITUDE)),
                Double.parseDouble(_pref.getSession(ConstantClass.TAG_LONGITUDE)));
        if (!(locationDetails.get("latitude").isEmpty() || locationDetails.get("longitude").isEmpty())) {
            LatLng selectedCityLatLng = new LatLng(Double.parseDouble(locationDetails.get("latitude")),
                    Double.parseDouble(locationDetails.get("longitude")));


            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(Double.parseDouble(_pref.getSession(ConstantClass.TAG_LATITUDE)),
                            Double.parseDouble(_pref.getSession(ConstantClass.TAG_LONGITUDE)))).
                            zoom(_commonMethods.getZoomLevel(_commonMethods.CalculationByDistance(currentCityLatLng,
                                    selectedCityLatLng))).tilt(70).build();

            map.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));
        }
    }

    private void loadMapVendorDetails(int markerIndex) {
        //--------------------------------------------------------------------------------------------------
        JSONObject jsonObjectParent = new JSONObject();
        JSONObject jsonObjectChild = new JSONObject();

        try {
            jsonObjectChild.put("accessToken", _pref.getSession(ConstantClass.ACCESS_TOKEN));

            JSONArray jsonArrayVendorIds = new JSONArray();

            if (isSinglePopupOpen) {
                JSONObject jsonObjectChildArray = new JSONObject();
                jsonObjectChildArray.put("id", vendorItemsList.get(markerIndex).getId());
                jsonArrayVendorIds.put(jsonObjectChildArray);
            } else {
                for (int p = 0; p < vendorItemsList.size(); p++) {
                    JSONObject jsonObjectChildArray = new JSONObject();
                    jsonObjectChildArray.put("id", String.valueOf(vendorItemsList.get(markerIndex).getId()));
                    jsonArrayVendorIds.put(jsonObjectChildArray);
                }
            }
            jsonObjectChild.put("vendor_ids", jsonArrayVendorIds);
            jsonObjectChild.put("latitude", _pref.getSession(ConstantClass.TAG_LATITUDE));
            jsonObjectChild.put("longitude", _pref.getSession(ConstantClass.TAG_LONGITUDE));
            jsonObjectParent.put("data", jsonObjectChild);
            Log.e("detailsMap", jsonObjectParent.toString());
            loadProgressBar();
            _callServiceAction.requestVersionApi(jsonObjectParent, "vendor-details");       //Called for Details for Map. _Siddhartha Maji

        } catch (JSONException e) {
            e.printStackTrace();
        }
        //--------------------------------------------------------------------------------------------------
    }

    @Override
    protected void onResume() {
        super.onResume();
        //setUpMapView();
    }

    ClusterManager.OnClusterClickListener<ClusterPopupList> clusterClickListener =
            new ClusterManager.OnClusterClickListener<ClusterPopupList>() {
                @Override
                public boolean onClusterClick(Cluster<ClusterPopupList> cluster) {
                    zoomLevel_2++;
                    if (cluster.getSize() <= 20) {

                        if (cluster.getSize() >= 2) {
                            isSinglePopupOpen = false;
                            loadMapVendorList(cluster);
                        }
                    } else {
                        LatLng cur_Latlng = new LatLng(cluster.getPosition().latitude, cluster.getPosition().longitude);
                        map.moveCamera(CameraUpdateFactory.newLatLng(cur_Latlng));
                        map.animateCamera(CameraUpdateFactory.zoomTo(zoomLevel_2));
                    }

                    return true;
                }
            };

    private void loadMapVendorList(Cluster<ClusterPopupList> cluster) {      //MODIFIED BY SIDDHARTHA MAJI.....(07/13/2016)
        JSONObject jsonObjectParent = new JSONObject();
        JSONObject jsonObjectChild = new JSONObject();

        try {
            jsonObjectChild.put("accessToken", _pref.getSession(ConstantClass.ACCESS_TOKEN));

            JSONArray jsonArrayVendorIds = new JSONArray();
            for (ClusterPopupList p : cluster.getItems()) {
                JSONObject jsonObjectChildArray = new JSONObject();
                jsonObjectChildArray.put("id", String.valueOf(vendorItemsList.get(p.markerIndex).getId()));
                jsonArrayVendorIds.put(jsonObjectChildArray);
            }
            jsonObjectChild.put("vendor_ids", jsonArrayVendorIds);
            jsonObjectChild.put("latitude", _pref.getSession(ConstantClass.TAG_LATITUDE));
            jsonObjectChild.put("longitude", _pref.getSession(ConstantClass.TAG_LONGITUDE));
            jsonObjectParent.put("data", jsonObjectChild);
            Log.e("detailsMap", jsonObjectParent.toString());
            loadProgressBar();
            _callServiceAction.requestVersionApi(jsonObjectParent, "vendor-details");       //Called for Details for Map. _Siddhartha Maji

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void loadProgressBar() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }


    @Override
    public void onClusterItemInfoWindowClick(ClusterPopupList clusterPopupList) {

    }

    @Override
    public void onStarted() {

    }

    @Override
    public void onCompleted(JSONObject response) {

        try {
            JSONObject jsonObjectErrorNode = response.getJSONObject("errNode");
            JSONObject jsonObject = response.getJSONObject("data");
            if (jsonObjectErrorNode.getString("errCode").equals("0")) {
                if (jsonObject.getBoolean("success")) {
                    JSONArray jsonArray = jsonObject.getJSONArray("listItem");


                    if (isSinglePopupOpen) {
                        for (int k = 0; k < jsonArray.length(); k++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(k);
                            progressDialog.dismiss();
                            initializeSingleVendorItemPopup(jsonObject1);
                        }

                    } else {
                        initializeClusterPopup(jsonArray);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private class ItemRenderer extends DefaultClusterRenderer<ClusterPopupList> {
     //   private final IconGenerator mIconGenerator = new IconGenerator(getApplicationContext());
        private final IconGenerator mClusterIconGenerator = new IconGenerator(getApplicationContext());
        private final int mDimension;
        Bitmap icon,icon1;
        public ItemRenderer() {
            super(getApplicationContext(), map, mClusterManager);

            View multiProfile = getLayoutInflater().inflate(R.layout.multi_profile, null);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mClusterIconGenerator.setBackground(getResources().getDrawable(R.color.transparent_color, null));
            } else {
                mClusterIconGenerator.setBackground(getResources().getDrawable(R.color.transparent_color));
            }
            mClusterIconGenerator.setContentView(multiProfile);
          //  mImageView = new ImageView(getApplicationContext());
            mDimension = (int) getResources().getDimension(R.dimen.custom_profile_image);
         //   mImageView.setLayoutParams(new ViewGroup.LayoutParams(mDimension, mDimension));
            int padding = (int) getResources().getDimension(R.dimen.custom_profile_padding);
          //  mImageView.setPadding(padding, padding, padding, padding);
         //   mIconGenerator.setContentView(mImageView);
       /*     if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mIconGenerator.setBackground(getResources().getDrawable(R.color.transparent_color, null));
            } else {
                mIconGenerator.setBackground(getResources().getDrawable(R.color.transparent_color));
            }*/

        }

        @Override
        protected void onBeforeClusterItemRendered(ClusterPopupList item, MarkerOptions markerOptions) {
            try {
             //   mImageView.setImageResource(item.profilePhoto);
            //    icon  = mIconGenerator.makeIcon();
                markerOptions.icon(BitmapDescriptorFactory.fromResource(item.profilePhoto));
            } catch (OutOfMemoryError e) {

                e.printStackTrace();
            }
            catch (RuntimeException e)
            { e.printStackTrace();

            }
            catch (Exception e) {

                e.printStackTrace();
            }

        }


        @Override
        protected void onBeforeClusterRendered(Cluster<ClusterPopupList> cluster, MarkerOptions markerOptions) {
            // Draw multiple people.
            // Note: this method runs on the UI thread. Don't spend too much time in here (like in this example).
            List<Drawable> profilePhotos = new ArrayList<Drawable>(Math.min(4, cluster.getSize()));
            int width = mDimension;
            int height = mDimension;

            for (ClusterPopupList p : cluster.getItems()) {
                // Draw 4 at most.
                if (profilePhotos.size() == 4) break;
                Drawable drawable = getResources().getDrawable(p.profilePhoto);
                drawable.setBounds(0, 0, width, height);
                profilePhotos.add(drawable);
            }
           icon1 = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon1));
        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster<ClusterPopupList> cluster) {
            return cluster.getSize() > 1;
        }
    }

    public class CustomAdapterForItems implements GoogleMap.InfoWindowAdapter {
        private final View myContentsView;

        CustomAdapterForItems() {
            myContentsView = getLayoutInflater().inflate(
                    R.layout.vendor_infowindow_layout, null);
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return myContentsView;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }
    }

    private void addItems() {
        try {
            // Add ten cluster items in close proximity, for purposes of this example.
            ClusterPopupList offsetItem;
            tvNoOfVendorShowText.setText(String.valueOf(vendorItemsList.size()) + " vendors shown in map");
            for (int i = 0; i < vendorItemsList.size(); i++) {
                switch (vendorItemsList.get(i).getRating()) {
                    case "0.5":
                        offsetItem = new ClusterPopupList(vendorItemsList.get(i).getLatitude(),
                                vendorItemsList.get(i).getLongitude(), R.drawable.icon_rating05, i);
                        break;
                    case "1.0":
                        offsetItem = new ClusterPopupList(vendorItemsList.get(i).getLatitude(),
                                vendorItemsList.get(i).getLongitude(), R.drawable.icon_rating10, i);
                        break;
                    case "1.5":
                        offsetItem = new ClusterPopupList(vendorItemsList.get(i).getLatitude(),
                                vendorItemsList.get(i).getLongitude(), R.drawable.icon_rating15, i);
                        break;

                    case "2.0":
                        offsetItem = new ClusterPopupList(vendorItemsList.get(i).getLatitude(),
                                vendorItemsList.get(i).getLongitude(), R.drawable.icon_rating20, i);
                        break;

                    case "2.5":
                        offsetItem = new ClusterPopupList(vendorItemsList.get(i).getLatitude(),
                                vendorItemsList.get(i).getLongitude(), R.drawable.icon_rating25, i);
                        break;

                    case "3.0":
                        offsetItem = new ClusterPopupList(vendorItemsList.get(i).getLatitude(),
                                vendorItemsList.get(i).getLongitude(), R.drawable.icon_rating30, i);
                        break;

                    case "3.5":
                        offsetItem = new ClusterPopupList(vendorItemsList.get(i).getLatitude(),
                                vendorItemsList.get(i).getLongitude(), R.drawable.icon_rating35, i);
                        break;

                    case "4.0":
                        offsetItem = new ClusterPopupList(vendorItemsList.get(i).getLatitude(),
                                vendorItemsList.get(i).getLongitude(), R.drawable.icon_rating40, i);
                        break;

                    case "4.5":
                        offsetItem = new ClusterPopupList(vendorItemsList.get(i).getLatitude(),
                                vendorItemsList.get(i).getLongitude(), R.drawable.icon_rating45, i);
                        break;

                    case "5.0":
                        offsetItem = new ClusterPopupList(vendorItemsList.get(i).getLatitude(),
                                vendorItemsList.get(i).getLongitude(), R.drawable.icon_rating50, i);
                        break;

                    default:
                        offsetItem = new ClusterPopupList(vendorItemsList.get(i).getLatitude(),
                                vendorItemsList.get(i).getLongitude(), R.drawable.icon_rating00, i);
                        break;
                }
                mClusterManager.addItem(offsetItem);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeClusterPopup(JSONArray jsonArray) {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View infoPopupLayout = layoutInflater.inflate(R.layout.cluster_popup_layout,
                (ViewGroup) findViewById(R.id.llClusterPopupWindow));
        ImageView ivCrossClusterPopup = (ImageView) infoPopupLayout.findViewById(R.id.ivCrossClusterPopup);
        rvClusterItemPopup = (RecyclerView) infoPopupLayout.findViewById(R.id.rvClusterItemPopup);
        rvClusterItemPopup.setLayoutManager(new LinearLayoutManager(this));
        clusterPopupListItems = new ArrayList<>();
        clusterPopupListAdapter = new ClusterPopupListAdapter(this, clusterPopupListItems);
        rvClusterItemPopup.setAdapter(clusterPopupListAdapter);
        Log.e("re height>>", rvClusterItemPopup.getLayoutManager().getMinimumHeight() + "");
        loadClusterPopupList(jsonArray);
        if (jsonArray.length() <= 2) {
            infoPopup = new PopupWindow(infoPopupLayout, _commonMethods.getScreenWidth() - 50,
                    _commonMethods.dipToPixels(160) * jsonArray.length(), false);
        } else {
            infoPopup = new PopupWindow(infoPopupLayout, _commonMethods.getScreenWidth() - 50,
                    _commonMethods.getScreenHeight() - 300, false);
        }
        infoPopup.showAtLocation(infoPopupLayout, Gravity.CENTER, 0, 50);
        clusterPopupOpenFlag = 1;
        ivCrossClusterPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clusterPopupOpenFlag = 0;
                infoPopup.dismiss();
            }
        });
    }

    public void initializeSingleVendorItemPopup(final JSONObject jvendorItemObj) throws JSONException {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View infoPopupLayout = layoutInflater.inflate(R.layout.vendor_infowindow_layout,
                (ViewGroup) findViewById(R.id.llMainContainerInfoWindow));
        ImageView ivCrossSinglePopup = (ImageView) infoPopupLayout.findViewById(R.id.ivCrossSinglePopup);
        TextView tvVendorNameSinglePopup = (TextView) infoPopupLayout.findViewById(R.id.tvVendorNameSinglePopup);
        TextView tvVendorAddressSinglePopup = (TextView) infoPopupLayout.findViewById(R.id.tvVendorAddressSinglePopup);
        TextView tvVendorFoodItemSinglePopup = (TextView) infoPopupLayout.findViewById(R.id.tvVendorFoodItemSinglePopup);
        TextView tvDistanceSinglePopup = (TextView) infoPopupLayout.findViewById(R.id.tvDistanceSinglePopup);
        TextView tvOnOfLikeSinglePopup = (TextView) infoPopupLayout.findViewById(R.id.tvOnOfLikeSinglePopup);
        TextView tvRatingSinglePopup = (TextView) infoPopupLayout.findViewById(R.id.tvRatingSinglePopup);
        TextView tvReviewsSinglePopup = (TextView) infoPopupLayout.findViewById(R.id.tvReviewsSinglePopup);
        TextView tvOpenStatusInfoWindow = (TextView) infoPopupLayout.findViewById(R.id.tvOpenStatusInfoWindow);
        TextView tvVendorTimeInfoSinglePopup = (TextView) infoPopupLayout.findViewById(R.id.tvVendorTimeInfoSinglePopup);
        LinearLayout rlPopupHeaderMapView = (LinearLayout) infoPopupLayout.findViewById(R.id.rlPopupHeaderMapView);
        LinearLayout llGalleryImageContainerMapPopup = (LinearLayout) infoPopupLayout.findViewById(R.id.llGalleryImageContainerMapPopup);
        LinearLayout llRatingContainerMapPopup = (LinearLayout) infoPopupLayout.findViewById(R.id.llRatingContainerMapPopup);
        LinearLayout llGalleryImageFirst = (LinearLayout) infoPopupLayout.findViewById(R.id.llGalleryImageFirst);
        LinearLayout llGalleryImageSecond = (LinearLayout) infoPopupLayout.findViewById(R.id.llGalleryImageSecond);
        LinearLayout llGalleryImageThird = (LinearLayout) infoPopupLayout.findViewById(R.id.llGalleryImageThird);
        LinearLayout llAllValueContainerMapPopup = (LinearLayout) infoPopupLayout.findViewById(R.id.llAllValueContainerMapPopup);
        ImageView ivMapPopupImageFirst = (ImageView) infoPopupLayout.findViewById(R.id.ivMapPopupImageFirst);
        ImageView ivMapPopupImageSecond = (ImageView) infoPopupLayout.findViewById(R.id.ivMapPopupImageSecond);
        ImageView ivMapPopupImageThird = (ImageView) infoPopupLayout.findViewById(R.id.ivMapPopupImageThird);
        TextView tvNoImageTextMapPopup = (TextView) infoPopupLayout.findViewById(R.id.tvNoImageTextMapPopup);
        ImageLoader imageLoader = ImageLoader.getInstance();
        DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .showImageForEmptyUri(R.drawable.default_image_vendor_inside)
                .showImageOnFail(R.drawable.default_image_vendor_inside)
                .showImageOnLoading(R.drawable.default_image_vendor_inside)
                .build();


        tvVendorNameSinglePopup.setText(jvendorItemObj.getString("vendorName"));
        tvVendorAddressSinglePopup.setText(jvendorItemObj.getString("inlineAddress"));
        tvVendorFoodItemSinglePopup.setText(jvendorItemObj.getString("inlineFoodMenu"));
        tvDistanceSinglePopup.setText((jvendorItemObj.getString("distance")) + "KM");
        tvOnOfLikeSinglePopup.setText(jvendorItemObj.getString("noOfLikes") + " Likes");
        tvRatingSinglePopup.setText(jvendorItemObj.getString("rating"));
        tvReviewsSinglePopup.setText(jvendorItemObj.getString("noOfReviews") + " Reviews");
        _commonMethods.setRatingContainerLayoutBackground(llRatingContainerMapPopup, jvendorItemObj.getString("rating"));
        if (jvendorItemObj.getBoolean("openStatus")) {
            tvOpenStatusInfoWindow.setText("OPEN");
            tvOpenStatusInfoWindow.setTextColor(getResources().getColor(R.color.theme_green_color));
            tvVendorTimeInfoSinglePopup.setVisibility(View.VISIBLE);
          /*  try {
                JSONObject Jdetailse =new JSONObject(vendorItem.getVendorDetails()) ;
                tvVendorTimeInfoSinglePopup.setText("- "+Jdetailse.getString("openingTime")+" - "+Jdetailse.getString("closingTime"));
            } catch (JSONException e) {
                e.printStackTrace();
            }*/
        } else {
            tvOpenStatusInfoWindow.setText("CLOSED");
            tvOpenStatusInfoWindow.setTextColor(getResources().getColor(R.color.nonveg_mode_red_color));
            tvVendorTimeInfoSinglePopup.setVisibility(View.GONE);
           /* try {
                JSONObject Jdetailse =new JSONObject(vendorItem.getVendorDetails()) ;
                tvVendorTimeInfoSinglePopup.setText("- "+Jdetailse.getString("closingTime")+" - "+Jdetailse.getString("openingTime"));
            } catch (JSONException e) {
                e.printStackTrace();
            }*/
        }

        infoPopup = new PopupWindow(infoPopupLayout, _commonMethods.getScreenWidth() - 50, 600, false);
        infoPopup.showAtLocation(infoPopupLayout, Gravity.CENTER, 0, 10);
        singleItemPopupOpenFlag = 1;
        llAllValueContainerMapPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("clicked", "checked");
                Intent intent = new Intent(VendorMapActivity.this, VendorDetailsActivity.class);
                //intent.putExtra("vendorDetailsJsonStr", vendorItem.getVendorDetails());
                try {
                    intent.putExtra("vendorId", jvendorItemObj.getInt("id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                intent.putExtra("routeFrom", "vendor_map");
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        rlPopupHeaderMapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("clicked", "checked");
                Intent intent = new Intent(VendorMapActivity.this, VendorDetailsActivity.class);
                //intent.putExtra("vendorDetailsJsonStr", vendorItem.getVendorDetails());
                try {
                    intent.putExtra("vendorId", jvendorItemObj.getInt("id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                intent.putExtra("routeFrom", "vendor_map");
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        try {
            //JSONObject jsonObjectDetails = new JSONObject(vendorItem.getVendorImages());
            JSONArray jsonArrayImageGallery = jvendorItemObj.getJSONArray("imageGallery");
            if (jsonArrayImageGallery.length() > 0) {
                tvNoImageTextMapPopup.setVisibility(View.GONE);
            }

            for (int i = 0; i < jsonArrayImageGallery.length(); i++) {
                JSONObject jsonObjectImageItem = jsonArrayImageGallery.getJSONObject(i);
                switch (i) {
                    case 0:
                        Log.e("image url", jsonObjectImageItem.getString("imageUrl"));
                        imageLoader.displayImage(jsonObjectImageItem.getString("imageUrl"), ivMapPopupImageFirst, imageOptions);
                        llGalleryImageFirst.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        imageLoader.displayImage(jsonObjectImageItem.getString("imageUrl"), ivMapPopupImageSecond, imageOptions);
                        llGalleryImageSecond.setVisibility(View.VISIBLE);
                        break;
                    default:
                        imageLoader.displayImage(jsonObjectImageItem.getString("imageUrl"), ivMapPopupImageThird, imageOptions);
                        llGalleryImageThird.setVisibility(View.VISIBLE);
                        break;
                }
                if (i == 2) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ivCrossSinglePopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                singleItemPopupOpenFlag = 0;
                infoPopup.dismiss();
            }
        });

    }

    private void loadClusterPopupList(JSONArray jsonArray) {
        clusterPopupListItems.clear();

        progressDialog.dismiss();
        for (int p = 0; p < jsonArray.length(); p++) {
            // Draw 4 at most.
            //Log.e("PP", p.getPosition().toString());
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(p);
                ClusterPopupListItem _item = new ClusterPopupListItem();
                _item.setId(Integer.parseInt(jsonObject.getString("id")));
                _item.setVendorName(jsonObject.getString("vendorName"));
                _item.setRating(jsonObject.getString("rating"));
                _item.setVendorAddress(jsonObject.getString("inlineAddress"));
                _item.setNoOfReviews(jsonObject.getInt("noOfReviews"));
                _item.setNoOfLikes(jsonObject.getString("noOfLikes"));
                _item.setDistance(jsonObject.getString("distance") + "KM");
                clusterPopupListItems.add(_item);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


        clusterPopupListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        if (clusterPopupOpenFlag == 1) {
            Log.e("clusterPopupOpenFlag", String.valueOf(clusterPopupOpenFlag));
            clusterPopupOpenFlag = 0;
            infoPopup.dismiss();
        } else if (singleItemPopupOpenFlag == 1) {
            Log.e("singleItemPopupOpenFlag", String.valueOf(singleItemPopupOpenFlag));
            singleItemPopupOpenFlag = 0;
            infoPopup.dismiss();
        } else {
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }

    private void moveToBottom(LatLng latlng) {
        zoomLevel = map.getCameraPosition().zoom;
        double dpPerdegree = 256.0 * Math.pow(2, zoomLevel) / 170.0;
        double screen_height = (double) llMapContainer.getHeight();
        double screen_height_50p = 50.0 * screen_height / 100.0;
        double degree_50p = screen_height_50p / dpPerdegree;
        LatLng centerlatlng = new LatLng(latlng.latitude + degree_50p, latlng.longitude);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(centerlatlng, zoomLevel), 1000, null);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }
}
