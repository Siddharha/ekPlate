package com.ekplate.android.activities.vendormodule;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ekplate.android.R;
import com.ekplate.android.activities.socialsharemodule.SocialShareActivity;
import com.ekplate.android.adapters.vendormodule.ClusterPopupListAdapter;
import com.ekplate.android.appinterfaces.BackgroundActionInterface;
import com.ekplate.android.config.BaseActivity;
import com.ekplate.android.models.vendormodule.ClusterPopupListItem;
import com.ekplate.android.models.vendormodule.VendorItem;
import com.ekplate.android.utils.CallServiceAction;
import com.ekplate.android.utils.ClusterPopupList;
import com.ekplate.android.utils.CommonMethods;
import com.ekplate.android.utils.ConstantClass;
import com.ekplate.android.utils.NetworkConnectionCheck;
import com.ekplate.android.utils.Pref;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptor;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class NearVendorActivity extends BaseActivity implements BackgroundActionInterface,
        ClusterManager.OnClusterItemInfoWindowClickListener<ClusterPopupList> {

    private Toolbar tbNearVendor;
    private TextView toolbarHeaderText;
    private MapView mvNearVendor;
    private Bundle savedInstanceState;
    private RecyclerView rvClusterItemPopup;
    private ArrayList<VendorItem> vendorItems;
    private ArrayList<VendorItem> vendorDetailsItems;
    private ArrayList<ClusterPopupListItem> clusterPopupListItems;
    private ClusterPopupListAdapter clusterPopupListAdapter;
    private Pref _pref;
    private NetworkConnectionCheck _connection;
    private CallServiceAction _serviceAction;
    private LinearLayout llProgressbarNearVendorMapContainer;
    private GoogleMap map;
    private ClusterManager<ClusterPopupList> mClusterManager;
    private ClusterPopupList clickedClusterItem;
    private ImageView mImageView = null;
    private PopupWindow infoPopup;
    private ProgressDialog progressDialog;
    private Cluster<ClusterPopupList> globalCluster;
    //private int clusterIndex;
    private int popupOpenFlag = 0, maxDistanceFlag = 0;
    private CommonMethods _CommonMethods;
    private int clusterPopupOpenFlag = 0, singleItemPopupOpenFlag = 0;
    private double maxDistance, minDistance = 0;
    private int noOfResponse;
    private boolean isSinglePopupOpen;
    private int zoomLevel = 0;
    Bitmap mIcon;
    BitmapDescriptor mBitmapDescriptor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_vendor);
        getSupportActionBar().hide();
        getWindow().setBackgroundDrawable(null);
        this.savedInstanceState = savedInstanceState;
        setUpToolBar();
        initialize();

    }

    @Override
    protected void onResume() {
        super.onResume();
        setInputParamForList();
    }

    private void initialize() {
        llProgressbarNearVendorMapContainer = (LinearLayout) findViewById(R.id.llProgressbarNearVendorMapContainer);
        vendorItems = new ArrayList<>();
        vendorDetailsItems = new ArrayList<>();
        _pref = new Pref(NearVendorActivity.this);
        _connection = new NetworkConnectionCheck(NearVendorActivity.this);
        _serviceAction = new CallServiceAction(NearVendorActivity.this);
        _serviceAction.actionInterface = NearVendorActivity.this;
        _CommonMethods = new CommonMethods(this);
        globalCluster = null;
        noOfResponse = 0;
        isSinglePopupOpen = false;

    }

    private void setUpToolBar() {
        tbNearVendor = (Toolbar) findViewById(R.id.tbNearVendor);
        toolbarHeaderText = (TextView) findViewById(R.id.toolbarHeaderText);
        toolbarHeaderText.setText("NEAREST VENDOR");
        tbNearVendor.setBackground(getResources().getDrawable(R.drawable.toolbar_bg));
        tbNearVendor.setNavigationIcon(R.drawable.ic_action_back);
        tbNearVendor.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        tbNearVendor.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_share_bookmark) {
                    startActivity(new Intent(NearVendorActivity.this, SocialShareActivity.class));
                }
                return false;
            }
        });
    }

    private void setUpMap() {
        mvNearVendor = (MapView) findViewById(R.id.mvNearestVendor);
        mvNearVendor.setVisibility(View.VISIBLE);
        mvNearVendor.onCreate(savedInstanceState);
        mvNearVendor.onResume();
        map = mvNearVendor.getMap();
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
                //moveToBottom(clusterPopupList.getPosition());
                isSinglePopupOpen = true;
                loadMapVendorDetails(vendorItems, clusterPopupList.getMarkerIndex());
                return false;
            }
        });
        addItems();
       /* mClusterManager.getMarkerCollection().setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // Log.e("title", marker.getTitle());
                for (int i = 0; i < vendorItems.size(); i++) {
                    VendorItem _item = vendorItems.get(i);
                    if (marker.getPosition().toString().equalsIgnoreCase(_item.getLocationPositionId())) {
                        initializeSingleVendorItemPopup(_item);
                    }
                }
                //moveToBottom(marker.getPosition());
                return false;
            }
        });*/
        mClusterManager.cluster();
        HashMap<String, String> locationDetails = _CommonMethods.getCoordinatesFromAddress(
                _pref.getSession(ConstantClass.TAG_SELECTED_CITY_LOCATION));
        LatLng currentCityLatLng = new LatLng(Double.parseDouble(_pref.getSession(ConstantClass.TAG_LATITUDE)),
                Double.parseDouble(_pref.getSession(ConstantClass.TAG_LONGITUDE)));
        LatLng selectedCityLatLng = new LatLng(Double.parseDouble(locationDetails.get("latitude")),
                Double.parseDouble(locationDetails.get("longitude")));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(Double.parseDouble(_pref.getSession(ConstantClass.TAG_LATITUDE)),
                        Double.parseDouble(_pref.getSession(ConstantClass.TAG_LONGITUDE)))).
                        zoom(_CommonMethods.getZoomLevel(_CommonMethods.CalculationByDistance(currentCityLatLng,
                                selectedCityLatLng))).tilt(70).build();
        map.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
    }

    private void loadMapVendorDetails(ArrayList<VendorItem> vendorItems, int markerIndex) {

        //--------------------------------------------------------------------------------------------------
        JSONObject jsonObjectParent = new JSONObject();
        JSONObject jsonObjectChild = new JSONObject();

        try {
            jsonObjectChild.put("accessToken", _pref.getSession(ConstantClass.ACCESS_TOKEN));

            JSONArray jsonArrayVendorIds = new JSONArray();

            if (isSinglePopupOpen) {
                JSONObject jsonObjectChildArray = new JSONObject();
                jsonObjectChildArray.put("id", vendorItems.get(markerIndex).getId());
                jsonArrayVendorIds.put(jsonObjectChildArray);
            } else {
                for (int p = 0; p < vendorItems.size(); p++) {
                    JSONObject jsonObjectChildArray = new JSONObject();
                    jsonObjectChildArray.put("id", vendorItems.get(p).getId());
                    jsonArrayVendorIds.put(jsonObjectChildArray);
                }
            }
            jsonObjectChild.put("vendor_ids", jsonArrayVendorIds);
            jsonObjectChild.put("latitude", _pref.getSession(ConstantClass.TAG_LATITUDE));
            jsonObjectChild.put("longitude", _pref.getSession(ConstantClass.TAG_LONGITUDE));
            jsonObjectParent.put("data", jsonObjectChild);
            Log.e("detailsMap", jsonObjectParent.toString());
            loadProgressBar();
            _serviceAction.requestVersionApi(jsonObjectParent, "vendor-details");       //Called for Details for Map. _Siddhartha Maji

        } catch (JSONException e) {
            e.printStackTrace();
        }
        //--------------------------------------------------------------------------------------------------

    }

    private void setInputParamForList(/*int currentPageIndex*/) {
        try {
            JSONObject parentJsonObj = new JSONObject();
            JSONObject childJsonObj = new JSONObject();
            childJsonObj.put("accessToken", _pref.getSession(ConstantClass.ACCESS_TOKEN));
            childJsonObj.put("city", _pref.getSession(ConstantClass.TAG_SELECTED_CITY_LOCATION));
            childJsonObj.put("foodType", _pref.getSession(ConstantClass.TAG_SELECTED_FOOD_TYPE));
            childJsonObj.put("keyPage", "nearbymap");
            // childJsonObj.put("optionId", getIntent().getExtras().getInt("optionId"));
            // childJsonObj.put("currentPage", currentPageIndex);
            childJsonObj.put("latitude", _pref.getSession(ConstantClass.TAG_LATITUDE));
            childJsonObj.put("longitude", _pref.getSession(ConstantClass.TAG_LONGITUDE));
            parentJsonObj.put("data", childJsonObj);
            Log.e("input data", parentJsonObj.toString());
            _serviceAction.requestVersionApi(parentJsonObj, "get-vendor-map");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class ItemRenderer extends DefaultClusterRenderer<ClusterPopupList> {
    //    IconGenerator mIconGenerator = new IconGenerator(getApplicationContext());
        IconGenerator mClusterIconGenerator = new IconGenerator(getApplicationContext());
        private final int mDimension;
        View multiProfile;
        Bitmap icon,icon1;
        public ItemRenderer() {
            super(getApplicationContext(), map, mClusterManager);

            multiProfile = getLayoutInflater().inflate(R.layout.multi_profile, null);
          //  mIconGenerator.setBackground(getResources().getDrawable(R.color.transparent_color));

            //   mClusterIconGenerator.setBackground(getDrawable(R.drawable.circle));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mClusterIconGenerator.setBackground(getResources().getDrawable(R.color.transparent_color, null));
            } else {
                mClusterIconGenerator.setBackground(getResources().getDrawable(R.color.transparent_color));
            }
            mClusterIconGenerator.setContentView(multiProfile);

       //     mImageView = new ImageView(getApplicationContext());
            // mImageView.setBackground(getDrawable(R.color.transparent_color));
            mDimension = (int) getResources().getDimension(R.dimen.custom_profile_image);
        //    mImageView.setLayoutParams(new ViewGroup.LayoutParams(mDimension, mDimension));
        //    mIconGenerator.setContentView(mImageView);


        }

        @Override
        protected void onBeforeClusterItemRendered(ClusterPopupList item, MarkerOptions markerOptions) {
     //     mImageView.setImageResource(item.profilePhoto);

            // Bitmap icon = mIconGenerator.makeIcon();
        //    mIcon = mIconGenerator.makeIcon();

         //   RelativeLayout relativeLayout=(RelativeLayout)multiProfile.findViewById(R.id.backgrondimage);


            try {

    //   icon = mIconGenerator.makeIcon();
markerOptions.icon(BitmapDescriptorFactory.fromResource(item.profilePhoto));



            } catch (OutOfMemoryError e) {
                e.printStackTrace();

            } catch (Exception e) {

                e.printStackTrace();
            } finally {
                //  onBackPressed();
            }


        }

        @Override
        protected void onBeforeClusterRendered(Cluster<ClusterPopupList> cluster, MarkerOptions markerOptions) {
            List<Drawable> profilePhotos = new ArrayList<Drawable>(Math.min(4, cluster.getSize()));
            int width = mDimension;
            int height = mDimension;

            for (ClusterPopupList p : cluster.getItems()) {
                // Draw 4 at most.
                if (profilePhotos.size() == 4) break;
                try {
                    Drawable drawable = getResources().getDrawable(p.profilePhoto);
                    drawable.setBounds(0, 0, width, height);
                    profilePhotos.add(drawable);
                } catch (OutOfMemoryError e) {
                    onBackPressed();
                    e.printStackTrace();
                } catch (RuntimeException e) {
                    onBackPressed();
                } catch (Exception e) {

                    e.printStackTrace();
                }

            }


            try {
             icon1 = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon1));
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onClusterRendered(Cluster<ClusterPopupList> cluster, Marker marker) {
            super.onClusterRendered(cluster, marker);
        }

        @Override
        public ClusterPopupList getClusterItem(Marker marker) {
            return super.getClusterItem(marker);
        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster<ClusterPopupList> cluster) {
            return cluster.getSize() > 1;
        }
    }


    ClusterManager.OnClusterClickListener<ClusterPopupList> clusterClickListener =
            new ClusterManager.OnClusterClickListener<ClusterPopupList>() {
                @Override
                public boolean onClusterClick(Cluster<ClusterPopupList> cluster) {
                    zoomLevel++;
                    if (cluster.getSize() <= 20) {

                        if (cluster.getSize() >= 2) {
                            isSinglePopupOpen = false;
                            loadMapVendorList(cluster);
                        }
                    } else {
                        LatLng cur_Latlng = new LatLng(cluster.getPosition().latitude, cluster.getPosition().longitude);
                        map.moveCamera(CameraUpdateFactory.newLatLng(cur_Latlng));
                        map.animateCamera(CameraUpdateFactory.zoomTo(zoomLevel));
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
                jsonObjectChildArray.put("id", String.valueOf(vendorItems.get(p.markerIndex).getId()));
                jsonArrayVendorIds.put(jsonObjectChildArray);
            }
            jsonObjectChild.put("vendor_ids", jsonArrayVendorIds);
            jsonObjectChild.put("latitude", _pref.getSession(ConstantClass.TAG_LATITUDE));
            jsonObjectChild.put("longitude", _pref.getSession(ConstantClass.TAG_LONGITUDE));
            jsonObjectParent.put("data", jsonObjectChild);
            Log.e("detailsMap", jsonObjectParent.toString());
            loadProgressBar();
            _serviceAction.requestVersionApi(jsonObjectParent, "vendor-details");       //Called for Details for Map. _Siddhartha Maji

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void loadProgressBar() {
        vendorDetailsItems.clear();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }


    @Override
    public void onStarted() {

    }

    @Override
    public void onCompleted(JSONObject response) {
        Log.e("response", response.toString());

        if (noOfResponse < 3) {

            noOfResponse++;
        } else {
            noOfResponse = 2;
        }
        try {
            JSONObject jsonObjError = response.getJSONObject("errNode");
            if (jsonObjError.getInt("errCode") == 0) {
                JSONObject jsonObjData = response.getJSONObject("data");
                JSONArray jsonArrListItem = jsonObjData.getJSONArray("listItem");
                if (jsonArrListItem.length() > 0) {
                    for (int i = 0; i < jsonArrListItem.length(); i++) {
                        JSONObject jsonObjListItem = jsonArrListItem.getJSONObject(i);
                        VendorItem _item = new VendorItem();
                        VendorItem _detailsItem = new VendorItem();
                        _item.setId(jsonObjListItem.getInt("id"));
                        _item.setLatitude(Double.parseDouble(jsonObjListItem.getString("latitude")));
                        _item.setLongitude(Double.parseDouble(jsonObjListItem.getString("longitude")));
                        _item.setRating(jsonObjListItem.getString("rating"));

                        if (jsonObjListItem.has("vendorName")) {
                            progressDialog.dismiss();
                            _detailsItem.setLatitude(Double.parseDouble(jsonObjListItem.getString("latitude")));
                            _detailsItem.setLongitude(Double.parseDouble(jsonObjListItem.getString("longitude")));
                            _detailsItem.setId(jsonObjListItem.getInt("id"));
                            _detailsItem.setVendorName(jsonObjListItem.getString("vendorName"));
                            _detailsItem.setInlineAddress(jsonObjListItem.getString("inlineAddress"));
                            _detailsItem.setInlineFoodMenu(jsonObjListItem.getString("inlineFoodMenu"));
                            _detailsItem.setOpenStatus(jsonObjListItem.getBoolean("openStatus"));
                            _detailsItem.setNoOfReviews(jsonObjListItem.getInt("noOfReviews"));
                            _detailsItem.setNoOfLikes(jsonObjListItem.getString("noOfLikes"));
                            _pref.setSession(ConstantClass.TAG_VENDOR_DETAILS_LIKE_COUNT +
                                    String.valueOf(jsonObjListItem.getInt("id")), jsonObjListItem.getString("noOfLikes"));
                            _pref.setSession(ConstantClass.TAG_VENDOR_DETAILS_BOOKMARK_COUNT +
                                    String.valueOf(jsonObjListItem.getInt("id")), jsonObjListItem.optString("bookmarkNo"));
                            _detailsItem.setInnerCircleIcon(R.drawable.icon_vendor_list_reviews);
                            _detailsItem.setInnerCircleText(jsonObjListItem.getString("noOfReviews") + " Reviews");
                            _detailsItem.setNoOfBookmark(jsonObjListItem.optString("bookmarkNo"));
                            _detailsItem.setRating(jsonObjListItem.optString("rating"));
                            _detailsItem.setDistance(jsonObjListItem.optString("distance"));
                            String distanceStringArr[] = jsonObjListItem.optString("distance").split(",");
                            String distanceStr = "";
                            if (distanceStringArr.length > 1) {
                                distanceStr = distanceStringArr[0] + distanceStringArr[1];
                            } else {
                                distanceStr = distanceStringArr[0];
                            }
                            double distance = Double.parseDouble(distanceStr);
                            if (maxDistanceFlag == 0) {
                                maxDistance = distance;
                                maxDistanceFlag = 1;
                            }
                            if (maxDistance < distance) {
                                maxDistance = distance;
                            }
                            if (minDistance > distance) {
                                minDistance = distance;
                            }
                            _detailsItem.setBookmarkStatus(Boolean.parseBoolean(jsonObjListItem.optString("bookmarkStatus")));
                            _detailsItem.setFoodType(jsonObjListItem.optString("foodType"));
                            _detailsItem.setLocationPositionId("(" + jsonObjListItem.getString("latitude") + ","
                                    + jsonObjListItem.getString("longitude") + ")");

                            JSONArray jsonArray = jsonObjListItem.getJSONArray("imageGallery");

                            JSONObject jj = new JSONObject();
                            jj.put("imageGallery", jsonArray);
                            _detailsItem.setVendorImages(jj.toString());
                            vendorDetailsItems.add(_detailsItem);


                        }


                        if (noOfResponse < 2) {
                            vendorItems.add(_item);
                        }
                    }
                }
                /*Log.e("maxDistance", String.valueOf(maxDistance));
                Log.e("minDistance", String.valueOf(minDistance));*/
                if (noOfResponse < 2) {
                    setUpMap();
                    llProgressbarNearVendorMapContainer.setVisibility(View.GONE);
                }
            } else {
                Toast.makeText(NearVendorActivity.this, jsonObjError.getString("errMsg"), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (noOfResponse > 1) {
            if (isSinglePopupOpen) {
                for (int q = 0; q < vendorDetailsItems.size(); q++) {
                    initializeSingleVendorItemPopup(vendorDetailsItems.get(q));
                }
            } else {
                initializeClusterPopup(vendorDetailsItems, mClusterManager);
            }
        }

    }

    @Override
    public void onClusterItemInfoWindowClick(ClusterPopupList clusterPopupList) {

    }

    private void addItems() {
        try {
            // Add ten cluster items in close proximity, for purposes of this example.
            ClusterPopupList offsetItem;
            for (int i = 0; i < vendorItems.size(); i++) {
                switch (vendorItems.get(i).getRating()) {
                    case "0.5":
                        offsetItem = new ClusterPopupList(vendorItems.get(i).getLatitude(),
                                vendorItems.get(i).getLongitude(), R.drawable.icon_rating05, i,"0.5");
                        break;
                    case "1.0":
                        offsetItem = new ClusterPopupList(vendorItems.get(i).getLatitude(),
                                vendorItems.get(i).getLongitude(), R.drawable.icon_rating10, i,"1.0");
                        break;
                    case "1.5":
                        offsetItem = new ClusterPopupList(vendorItems.get(i).getLatitude(),
                                vendorItems.get(i).getLongitude(), R.drawable.icon_rating15, i,"1.5");
                        break;

                    case "2.0":
                        offsetItem = new ClusterPopupList(vendorItems.get(i).getLatitude(),
                                vendorItems.get(i).getLongitude(), R.drawable.icon_rating20, i,"2.0");
                        break;

                    case "2.5":
                        offsetItem = new ClusterPopupList(vendorItems.get(i).getLatitude(),
                                vendorItems.get(i).getLongitude(), R.drawable.icon_rating25, i,"2.5");
                        break;

                    case "3.0":
                        offsetItem = new ClusterPopupList(vendorItems.get(i).getLatitude(),
                                vendorItems.get(i).getLongitude(), R.drawable.icon_rating30, i,"3.0");
                        break;

                    case "3.5":
                        offsetItem = new ClusterPopupList(vendorItems.get(i).getLatitude(),
                                vendorItems.get(i).getLongitude(), R.drawable.icon_rating35, i,"3.5");
                        break;

                    case "4.0":
                        offsetItem = new ClusterPopupList(vendorItems.get(i).getLatitude(),
                                vendorItems.get(i).getLongitude(), R.drawable.icon_rating40, i);
                        break;

                    case "4.5":
                        offsetItem = new ClusterPopupList(vendorItems.get(i).getLatitude(),
                                vendorItems.get(i).getLongitude(), R.drawable.icon_rating45, i,"4.0");
                        break;

                    case "5.0":
                        offsetItem = new ClusterPopupList(vendorItems.get(i).getLatitude(),
                                vendorItems.get(i).getLongitude(), R.drawable.icon_rating50, i,"5.0");
                        break;
                    default:
                        offsetItem = new ClusterPopupList(vendorItems.get(i).getLatitude(),
                                vendorItems.get(i).getLongitude(), R.drawable.icon_rating00, i,"0.0");
                        break;
                }
                mClusterManager.addItem(offsetItem);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeClusterPopup(ArrayList<VendorItem> GrouparrayList, ClusterManager clusterManager) {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View infoPopupLayout = layoutInflater.inflate(R.layout.cluster_popup_layout,
                (ViewGroup) findViewById(R.id.llClusterPopupWindow));
        ImageView ivCrossClusterPopup = (ImageView) infoPopupLayout.findViewById(R.id.ivCrossClusterPopup);
        rvClusterItemPopup = (RecyclerView) infoPopupLayout.findViewById(R.id.rvClusterItemPopup);
        rvClusterItemPopup.setLayoutManager(new LinearLayoutManager(this));
        clusterPopupListItems = new ArrayList<>();
        clusterPopupListAdapter = new ClusterPopupListAdapter(this, clusterPopupListItems);
        rvClusterItemPopup.setAdapter(clusterPopupListAdapter);
        loadClusterPopupList(GrouparrayList, clusterManager);
        if (GrouparrayList.size() <= 2) {
            infoPopup = new PopupWindow(infoPopupLayout, _CommonMethods.getScreenWidth() - 50,
                    _CommonMethods.dipToPixels(160) * GrouparrayList.size(), false);
        } else {
            infoPopup = new PopupWindow(infoPopupLayout, _CommonMethods.getScreenWidth() - 50,
                    _CommonMethods.getScreenHeight() - 300, false);
        }
        infoPopup.showAtLocation(infoPopupLayout, Gravity.CENTER, 0, 40);
        singleItemPopupOpenFlag = 0;
        ivCrossClusterPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoPopup.dismiss();
                clusterPopupListItems.clear();
                vendorDetailsItems.clear();
            }
        });
    }

    private void initializeSingleVendorItemPopup(final VendorItem vendorItem) {
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

        _pref.setSession(ConstantClass.TAG_VENDOR_DETAILS_LIKE_COUNT +
                String.valueOf(vendorItem.getId()), vendorItem.getNoOfLikes());
        _pref.setSession(ConstantClass.TAG_VENDOR_DETAILS_BOOKMARK_COUNT +
                String.valueOf(vendorItem.getId()), vendorItem.getNoOfBookmark());

        tvVendorNameSinglePopup.setText(vendorItem.getVendorName());
        tvVendorAddressSinglePopup.setText(vendorItem.getInlineAddress());
        tvVendorFoodItemSinglePopup.setText(vendorItem.getInlineFoodMenu());
        tvDistanceSinglePopup.setText(vendorItem.getDistance() + "KM");
        tvOnOfLikeSinglePopup.setText(vendorItem.getNoOfLikes() + " Likes");
        tvRatingSinglePopup.setText(vendorItem.getRating());
        tvReviewsSinglePopup.setText(vendorItem.getNoOfReviews() + " Reviews");
        _CommonMethods.setRatingContainerLayoutBackground(llRatingContainerMapPopup, vendorItem.getRating());


        if (vendorItem.isOpenStatus()) {
            tvOpenStatusInfoWindow.setText("OPEN");
            tvOpenStatusInfoWindow.setTextColor(getResources().getColor(R.color.theme_green_color));
            tvVendorTimeInfoSinglePopup.setVisibility(View.VISIBLE);
            try {
                JSONObject Jdetailse = new JSONObject(vendorItem.getVendorImages());
                // tvVendorTimeInfoSinglePopup.setText("- "+Jdetailse.getString("openingTime")+" - "+Jdetailse.getString("closingTime"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            tvOpenStatusInfoWindow.setText("CLOSED");
            tvOpenStatusInfoWindow.setTextColor(getResources().getColor(R.color.nonveg_mode_red_color));
            tvVendorTimeInfoSinglePopup.setVisibility(View.GONE);
           /* try {
                JSONObject Jdetailse =new JSONObject(vendorItem.getVendorDetails()) ;
                tvVendorTimeInfoSinglePopup.setText("- "+Jdetailse.optString("closingTime")+" - "+Jdetailse.optString("openingTime"));
            } catch (JSONException e) {
                e.printStackTrace();
            }*/
        }
        singleItemPopupOpenFlag = 1;
        infoPopup = new PopupWindow(infoPopupLayout, _CommonMethods.getScreenWidth() - 50, 600, false);
        infoPopup.showAtLocation(infoPopupLayout, Gravity.CENTER, 0, 10);
        llAllValueContainerMapPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("clicked", "checked");
                Intent intent = new Intent(NearVendorActivity.this, VendorDetailsActivity.class);
                // intent.putExtra("vendorDetailsJsonStr", vendorItem.getVendorDetails());
                intent.putExtra("vendorId", vendorItem.getId());
                intent.putExtra("routeFrom", "vendor_map");
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        rlPopupHeaderMapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("clicked", "checked");
                Intent intent = new Intent(NearVendorActivity.this, VendorDetailsActivity.class);
                // intent.putExtra("vendorDetailsJsonStr", vendorItem.getVendorDetails());
                intent.putExtra("vendorId", vendorItem.getId());
                intent.putExtra("routeFrom", "vendor_map");
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        try {
            JSONObject jsonObjectDetails = new JSONObject(vendorItem.getVendorImages());
            JSONArray jsonArrayImageGallery = jsonObjectDetails.getJSONArray("imageGallery");
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
                vendorDetailsItems.clear();
                infoPopup.dismiss();
            }
        });
    }

    private void loadClusterPopupList(/*Cluster<ClusterPopupList> cluster*/ ArrayList<VendorItem> list, ClusterManager clusterManager) {
      /*  clusterPopupListItems.clear();
        ArrayList<LatLng> allClusterMarkers = new ArrayList<>();
        if(!allClusterMarkers.isEmpty())
        {
            allClusterMarkers.clear();
        }
        Collection<Marker> marker =clusterManager.getClusterMarkerCollection().getMarkers();


                for (int i=0; i<allClusterMarkers.size(); i++){
                    LatLng latLng = new LatLng(vendorItems.get(i).getLatitude(),vendorItems.get(i).getLongitude());*/
              /*  if(allClusterMarkers.contains(latLng))
                {*/

      /*  for (ClusterPopupList p: list.getItems()) {*/
        // Draw 4 at most.
        // Log.e("PP",p.getPosition().toString());
        for (int p = 0; p < list.size(); p++) {

            ClusterPopupListItem _item = new ClusterPopupListItem();
            _item.setId(list.get(p).getId());
            _item.setVendorName(list.get(p).getVendorName());
            _item.setVendorAddress(list.get(p).getInlineAddress());
            _item.setFoodItem(list.get(p).getFoodType());
            _item.setDistance(list.get(p).getDistance() + "KM");
            _item.setNoOfLikes(list.get(p).getNoOfLikes());
            _item.setNoOfReviews(list.get(p).getNoOfReviews());
            _item.setRating(list.get(p).getRating());
            // _item.setVendorDetails(list.get(p).getVendorDetails());
            clusterPopupListItems.add(_item);
        }
        // Collection<Marker> ff =    clusterManager.getClusterMarkerCollection().getMarkers();



       /* while (cluster.getItems().iterator().hasNext()) {
            Log.w("LT_LL:", cluster.getItems().iterator().next().toString());
        }*/


        /*for (int i=0; i<cluster.getSize(); i++){


                    ClusterPopupListItem _item = new ClusterPopupListItem();
                    _item.setId(vendorItems.get(i).getId());
                    _item.setVendorName(vendorItems.get(i).getVendorName());
                    _item.setVendorAddress(vendorItems.get(i).getInlineAddress());
                    _item.setFoodItem(vendorItems.get(i).getFoodType());
                    _item.setDistance(vendorItems.get(i).getDistance() + "KM");
                    _item.setNoOfLikes(vendorItems.get(i).getNoOfLikes());
                    _item.setNoOfReviews(vendorItems.get(i).getNoOfReviews());
                    _item.setRating(vendorItems.get(i).getRating());
                    _item.setVendorDetails(vendorItems.get(i).getVendorDetails());
            clusterPopupListItems.add(_item);
                }
*/
//}

        //}
        clusterPopupListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        if (clusterPopupOpenFlag == 1) {
            clusterPopupOpenFlag = 0;
            infoPopup.dismiss();
            vendorDetailsItems.clear();
            clusterPopupListItems.clear();
        } else if (singleItemPopupOpenFlag == 1) {
            singleItemPopupOpenFlag = 0;
            infoPopup.dismiss();
            vendorDetailsItems.clear();
        } else {
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }


    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }
}
