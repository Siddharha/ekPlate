package com.ekplate.android.activities.vendormodule;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.exceptions.CleverTapMetaDataNotFoundException;
import com.clevertap.android.sdk.exceptions.CleverTapPermissionsNotSatisfied;
import com.ekplate.android.R;
import com.ekplate.android.activities.addvendormodule.Action;
import com.ekplate.android.activities.addvendormodule.AddVendorInformationActivity;
import com.ekplate.android.activities.addvendormodule.CustomGallery;
import com.ekplate.android.activities.socialsharemodule.SocialShareActivity;
import com.ekplate.android.appinterfaces.BackgroundActionInterface;
import com.ekplate.android.config.BaseActivity;
import com.ekplate.android.utils.AsyncTaskListener;
import com.ekplate.android.utils.CallServiceAction;
import com.ekplate.android.utils.CommonFunction;
import com.ekplate.android.utils.CommonMethods;
import com.ekplate.android.utils.ConstantClass;
import com.ekplate.android.utils.NetworkConnectionCheck;
import com.ekplate.android.utils.PostObject;
import com.ekplate.android.utils.Pref;
import com.ekplate.android.views.BariolItalicTextView;
import com.ekplate.android.views.BariolLightTextView;
import com.ekplate.android.views.BariolRegularTextView;
import com.ekplate.android.views.CircularImageView;
import com.google.android.gms.maps.model.LatLng;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.entities.Feed;
import com.sromku.simple.fb.listeners.OnPublishListener;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class VendorDetailsActivity extends BaseActivity implements BackgroundActionInterface {
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 1;
    private Toolbar toolbarVendorDetails;
    private RelativeLayout rlDetailse;
    private TextView toolbarHeaderText, tvCheckinSideVendorDetails, tvLikesSideVendorDetails,tvFoodMenuMoreVendorDetails,
            tvBookmarkSideVendorDetails, tvReviewsSideVendorDetails, tvVendorNameVendorDetails, tvInlineMenuVendorDetails,
            tvHygieneRatingVendorDetails, tvTasteRatingVendorDetails, tvAddressVendorDetails, tvShopOpenStatusVendorDetails,
            tvShopClosingTimeVendorDetails, tvDistanceVendorDetails, tvNoOfGalleryImageVendorDetails, tvReadAllReviewText;
    private ScrollView svMainContainer;
    private LinearLayout llSidePanel, llShowGallery, llVendorDetailsReportProblem, llWriteReview,llProgressbarVendorDetails,
            llMyStory, llFoodMenuVendorDetails, llSeparatorOneVendorDetails, llSeparatorTwoVendorDetails,
            llSeparatorThreeVendorDetails, llUserReviewContainer, llReadAllReviews, llReviewContainerMoreThanTwo,
            llOpenCamera, llGalleryHeadingIcon;
    private RelativeLayout.LayoutParams layoutParams;
    private RelativeLayout rlLikeVendor, rlCheckIn, rlVendorDetailsBookmark, rlVendorDetailsReview;
    private ImageView ivHeadingImageVendorDetails, ivBookMarkVendorDetails, ivVegStatusVendorDetails,
            ivRatingVendorDetails, ivGalleryImageOneVendorDetails, ivGalleryImageTwoVendorDetails,
            ivGalleryImageThreeVendorDetails, ivBookmarkSideVendorDetails, imgAddressVendorDetails;
    private BariolLightTextView tvAgreedCount;
    private ArrayList<BariolLightTextView> lightTextViewsArrListForAgreeCount;
    private ArrayList<Button> btnArrListForAgreeAction;
    private CardView cvGetDirectionVendorDetails, cvFoodMenuMoreVendorDetails;
    private boolean flagFoodMenuMore = false, flagReadAllReviewsOpen = false;
    private ImageLoader imageLoader;
    private CircularImageView imgUsrName;
    private CardView crAddedByVendor;
    private DisplayImageOptions optionsHeadingImage, optionReviewerProfileImage;
    private Pref _pref;
    private boolean bookMarkStatus, likeStatus;
    private NetworkConnectionCheck _connection;
    private CallServiceAction _serviceAction;
    private ProgressDialog progressDialog;
    private int flagBookMarkService = 0, flagAgreedService = 0, flagCheckinService = 0,
            flagLikeService = 0, flagTwitterShare = 0, selectedAgreedItem, flagCheckInComplete = 0;
    private CommonMethods _commonMethods;
    private String vendorName, captureImagePath, checkInCaption, headerImageUrl,
            vendorAddress, selectedReviewerId;
    private static final int REQUEST_CAMERA = 100;
    private static final int SELECT_FILE = 200;
    public static ArrayList<CustomGallery> dataT = new ArrayList<CustomGallery>();
    public static int galleryImageSelection = 0;
    private CommonFunction _comFunc;
    private SimpleFacebook mSimpleFacebook;
    private final static String TAG = "VendorDetailsActivity";
    private Feed feed;
    private PopupWindow infoPopup;
    private EditText etShareFeeling = null;
    private String vendorLatitude, vendorLongitude;
    private LayoutInflater layoutInflaterCheckIn;
    private AlertDialog helpDialog;
    private TextView tvAddedBy;
    private String VENDOR_DETAILSE_API = "get-vendor-details";
    CleverTapAPI cleverTap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_details);
        getSupportActionBar().hide();
        initialize();
        getVendorDetails();
        //setUpVendorAddress();
       // setUpHeaderImageAndBookmark();
       // setUpVendorPersonalInfo();
       // setUpVendorDirectionInfo();
       // setUpFoodMenu();
      //  setUpImageGallery();
        //setUpUserReviewList();
        //setUpSideMenu();
        setUpToolbar();
        onActivityListener();
        onClick();
      //  if (getIntent().getExtras().getString("routeFrom").equalsIgnoreCase("home_search")) {

       // }
    }

    private void getVendorDetails() {
        try {
            JSONObject parentJsonObj = new JSONObject();
            JSONObject childJsonObj = new JSONObject();
            childJsonObj.put("accessToken", _pref.getSession(ConstantClass.ACCESS_TOKEN));
            childJsonObj.put("vendorId", getIntent().getExtras().getInt("vendorId"));
            childJsonObj.put("latitude",ConstantClass.TAG_LATITUDE);
            childJsonObj.put("longitude",ConstantClass.TAG_LONGITUDE);
            parentJsonObj.put("data", childJsonObj);
            Log.e("inputData", parentJsonObj.toString());
            _serviceAction.requestVersionApi(parentJsonObj, VENDOR_DETAILSE_API);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setBookMarkStatus() {
        setInputParamForBookMark();
    }

    private void initialize(){
        toolbarVendorDetails = (Toolbar) findViewById(R.id.toolbarVendorDetails);
        toolbarHeaderText = (TextView) findViewById(R.id.toolbarHeaderText);
        tvAddedBy = (TextView)findViewById(R.id.tvAddedBy);
        svMainContainer = (ScrollView) findViewById(R.id.svMainContainer);
        llSidePanel = (LinearLayout) findViewById(R.id.llSidePanel);
        rlDetailse = (RelativeLayout)findViewById(R.id.rlDetailse);
        imgUsrName = (CircularImageView)findViewById(R.id.imgUsrName);
        crAddedByVendor = (CardView)findViewById(R.id.crAddedByVendor);
        llProgressbarVendorDetails = (LinearLayout)findViewById(R.id.llProgressbarVendorDetails);
        layoutParams = (RelativeLayout.LayoutParams)
                llSidePanel.getLayoutParams();
        llVendorDetailsReportProblem = (LinearLayout) findViewById(R.id.llVendorDetailsReportProblem);
        llWriteReview = (LinearLayout) findViewById(R.id.llWriteReview);
        lightTextViewsArrListForAgreeCount = new ArrayList<>();
        btnArrListForAgreeAction = new ArrayList<>();
        try {
            cleverTap = CleverTapAPI.getInstance(getApplicationContext());
        } catch (CleverTapMetaDataNotFoundException e) {
            // thrown if you haven't specified your CleverTap Account ID or Token in your AndroidManifest.xml
        } catch (CleverTapPermissionsNotSatisfied e) {
            // thrown if you haven’t requested the required permissions in your AndroidManifest.xml
        }

        optionsHeadingImage = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .showImageForEmptyUri(R.drawable.default_image_vendor_inside)
                .showImageOnFail(R.drawable.default_image_vendor_inside)
                .showImageOnLoading(R.drawable.default_image_vendor_inside)
                .delayBeforeLoading(100)
                .build();
        optionReviewerProfileImage = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .showImageForEmptyUri(R.drawable.profile_dp)
                .showImageOnFail(R.drawable.profile_dp)
                .showImageOnLoading(R.drawable.profile_dp)
                .delayBeforeLoading(100)
                .build();
        _commonMethods = new CommonMethods(this);
        _pref = new Pref(this);
        _connection = new NetworkConnectionCheck(this);
        _serviceAction = new CallServiceAction(this);
        _serviceAction.actionInterface = VendorDetailsActivity.this;
        _comFunc = new CommonFunction(this);
    }

    private void setUpToolbar(){
        toolbarVendorDetails.inflateMenu(R.menu.menu_vendor_details);
        toolbarVendorDetails.setNavigationIcon(R.drawable.ic_action_back);
        toolbarVendorDetails.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        toolbarVendorDetails.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int optionId = item.getItemId();
                Intent intent = new Intent(VendorDetailsActivity.this, SocialShareActivity.class);
                intent.putExtra("vendor_name", vendorName);
                intent.putExtra("vendor_address", vendorAddress);
                intent.putExtra("url", headerImageUrl);
                intent.putExtra("route_from", "vendor_details");
                startActivity(intent);
                return false;
            }
        });
    }

    private void onClick(){
        llVendorDetailsReportProblem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VendorDetailsActivity.this, ReportProblemActivity.class);
                intent.putExtra("vendorId", getIntent().getExtras().getInt("vendorId"));
                intent.putExtra("routeFrom", "vendorDetails");
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        llWriteReview.setOnClickListener(onClickListenerForWriteReview);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void onActivityListener(){
        svMainContainer.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int scrollY = svMainContainer.getScrollY();
                if ((scrollY * -1) >= 0) {
                    setLeftMargin();
                } else {
                    layoutParams.leftMargin = (scrollY * -1);
                }

                if (scrollY < 500 && scrollY > 0) {
                    int opacityIndex = scrollY / 30;

                    if (opacityIndex <= 0) {
                        toolbarVendorDetails.setBackgroundColor(Color.parseColor("#00FFBB00"));
                    } else if (opacityIndex <= 1) {
                        toolbarVendorDetails.setBackgroundColor(Color.parseColor("#1AFFBB00"));
                    } else if (opacityIndex <= 2) {
                        toolbarVendorDetails.setBackgroundColor(Color.parseColor("#33FFBB00"));
                    } else if (opacityIndex <= 3) {
                        toolbarVendorDetails.setBackgroundColor(Color.parseColor("#4DFFBB00"));
                    } else if (opacityIndex <= 4) {
                        toolbarVendorDetails.setBackgroundColor(Color.parseColor("#66FFBB00"));
                    } else if (opacityIndex <= 5) {
                        toolbarVendorDetails.setBackgroundColor(Color.parseColor("#80FFBB00"));
                    } else if (opacityIndex <= 6) {
                        toolbarVendorDetails.setBackgroundColor(Color.parseColor("#99FFBB00"));
                    } else if (opacityIndex <= 7) {
                        toolbarVendorDetails.setBackgroundColor(Color.parseColor("#B3FFBB00"));
                    } else if (opacityIndex <= 8) {
                        toolbarVendorDetails.setBackgroundColor(Color.parseColor("#CCFFBB00"));
                    } else if (opacityIndex <= 9) {
                        toolbarVendorDetails.setBackgroundColor(Color.parseColor("#E6FFBB00"));
                    } else {
                        toolbarVendorDetails.setBackgroundColor(Color.parseColor("#FFBB00"));
                    }
                    llSidePanel.setLayoutParams(layoutParams);
                }
            }
        });
    }

    private void setUpSideMenu(JSONObject vendorDetailsJsonObj){
        rlLikeVendor = (RelativeLayout) findViewById(R.id.rlLikeVendor);
        rlCheckIn = (RelativeLayout) findViewById(R.id.rlCheckIn);
        rlVendorDetailsBookmark = (RelativeLayout) findViewById(R.id.rlVendorDetailsRating);
        rlVendorDetailsReview = (RelativeLayout) findViewById(R.id.rlVendorDetailsReview);
        tvCheckinSideVendorDetails = (TextView) findViewById(R.id.tvCheckinSideVendorDetails);
        tvLikesSideVendorDetails = (TextView) findViewById(R.id.tvLikesSideVendorDetails);
        tvBookmarkSideVendorDetails = (TextView) findViewById(R.id.tvBookmarkSideVendorDetails);
        tvReviewsSideVendorDetails = (TextView) findViewById(R.id.tvReviewsSideVendorDetails);
        ivBookmarkSideVendorDetails = (ImageView) findViewById(R.id.ivBookmarkSideVendorDetails);
         likeStatus = false;
        rlLikeVendor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flagLikeService = 1;
                setUpProgressDialog();
                setInputParamForLike();
            }
        });

        rlVendorDetailsBookmark.setOnClickListener(bookmarkListener);
        rlVendorDetailsReview.setOnClickListener(onClickListenerForWriteReview);
        ivBookmarkSideVendorDetails.setOnClickListener(bookmarkListener);

        rlCheckIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initializeCheckinPopup();
            }
        });
        try {
           // JSONObject vendorDetailsJsonObj = new JSONObject(getIntent().getExtras().getString("vendorDetailsJsonStr"));
            _pref.setSession(ConstantClass.TAG_VENDOR_DETAILS_CHECK_IN_COUNT + getIntent().getExtras().getInt("vendorId")
                    , vendorDetailsJsonObj.getString("noOfChecking"));
            tvLikesSideVendorDetails.setText(_pref.getSession(ConstantClass.TAG_VENDOR_DETAILS_LIKE_COUNT + getIntent()
                    .getExtras().getInt("vendorId")) + " Likes");
            tvBookmarkSideVendorDetails.setText(_pref.getSession(ConstantClass.TAG_VENDOR_DETAILS_BOOKMARK_COUNT + getIntent()
                    .getExtras().getInt("vendorId")) + " Bookmarks");
            tvReviewsSideVendorDetails.setText(vendorDetailsJsonObj.getString("noOfReviews") + " Reviews");
            setCheckinValue();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private void setCheckinValue(){
        tvCheckinSideVendorDetails.setText(_pref.getSession(ConstantClass.TAG_VENDOR_DETAILS_CHECK_IN_COUNT +
                getIntent().getExtras().getInt("vendorId")) + " Checkin");
    }

    private void setUpHeaderImageAndBookmark(){
        ivHeadingImageVendorDetails = (ImageView) findViewById(R.id.ivHeadingImageVendorDetails);
        ivBookMarkVendorDetails = (ImageView) findViewById(R.id.ivBookMarkVendorDetails);
        imageLoader = ImageLoader.getInstance();
        try {
            JSONObject vendorDetailsJsonObj = new JSONObject(getIntent().getStringExtra("vendorDetailsJsonStr"));
            headerImageUrl = vendorDetailsJsonObj.getString("mainImageUrl");
            imageLoader.displayImage(headerImageUrl, ivHeadingImageVendorDetails, optionsHeadingImage);
            bookMarkStatus =vendorDetailsJsonObj.getBoolean("bookmarkedStatus");

            if(!bookMarkStatus) {
                bookMarkStatus = getIntent().getBooleanExtra("vendorBookMark", false);
            }
            if(bookMarkStatus){
                ivBookMarkVendorDetails.setImageResource(R.drawable.icon_ratting);
            } else {
                ivBookMarkVendorDetails.setImageResource(R.drawable.icon_ratting_gray);
            }
            ivBookMarkVendorDetails.setOnClickListener(bookmarkListener);
            final JSONArray imageGalleryJsonArray = vendorDetailsJsonObj.getJSONArray("imageGallery");
            ivHeadingImageVendorDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(VendorDetailsActivity.this, GalleryActivity.class);
                    intent.putExtra("galleryImageJsonArrayStr", imageGalleryJsonArray.toString());
                    intent.putExtra("vendorId", getIntent().getExtras().getInt("vendorId"));
                    intent.putExtra("vendorName", vendorName);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setUpVendorPersonalInfo(JSONObject vendorDetailsJsonObj){
        tvVendorNameVendorDetails = (TextView) findViewById(R.id.tvVendorNameVendorDetails);
        tvInlineMenuVendorDetails = (TextView) findViewById(R.id.tvInlineMenuVendorDetails);
        tvHygieneRatingVendorDetails = (TextView) findViewById(R.id.tvHygieneRatingVendorDetails);
        tvTasteRatingVendorDetails = (TextView) findViewById(R.id.tvTasteRatingVendorDetails);
        ivVegStatusVendorDetails = (ImageView) findViewById(R.id.ivVegStatusVendorDetails);
        ivRatingVendorDetails = (ImageView) findViewById(R.id.ivRatingVendorDetails);
        llMyStory = (LinearLayout) findViewById(R.id.llMyStory);
        try {
            Log.e("Ven_DETAILSE:",vendorDetailsJsonObj.toString());
            if(vendorDetailsJsonObj.getString("foodType").equals("Veg"))
            {
                ivVegStatusVendorDetails.setImageResource(R.drawable.icon_veg);
            }
            else
            {
                ivVegStatusVendorDetails.setImageResource(R.drawable.icon_nonveg);
            }
            tvVendorNameVendorDetails.setText(vendorDetailsJsonObj.getString("shopName"));
            tvInlineMenuVendorDetails.setText(vendorDetailsJsonObj.getString("inlineFoodMenu"));
            tvHygieneRatingVendorDetails.setText(vendorDetailsJsonObj.getString("hyginePoint"));
            tvTasteRatingVendorDetails.setText(vendorDetailsJsonObj.getString("tastePoint"));
            vendorName = vendorDetailsJsonObj.getString("shopName");
            HashMap<String, Object> prodViewedAction = new HashMap<String, Object>();
            prodViewedAction.put("Vendor Name", vendorName);
            prodViewedAction.put("Date", new java.util.Date());
            cleverTap.event.push("Vendor viewed", prodViewedAction);




            final String myStoryDetailsStr = vendorDetailsJsonObj.getString("myStoryDetails");
            setRatingIcon(vendorDetailsJsonObj.getString("noOfRating"), ivRatingVendorDetails);
            JSONArray myStoryJsonArr = new JSONArray(myStoryDetailsStr);
            if (myStoryJsonArr.length() == 0) {
                llMyStory.setVisibility(View.GONE);
            }
            llMyStory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Log.e("myStoryDetailsStr", myStoryDetailsStr);
                        JSONArray myStoryJsonArr = new JSONArray(myStoryDetailsStr);
                        Intent intent = new Intent(VendorDetailsActivity.this, MyStoryActivity.class);
                        intent.putExtra("myStoryDetails", myStoryJsonArr.get(0).toString());
                        intent.putExtra("vendorName", vendorName);
                        intent.putExtra("vendorAddress", vendorAddress);
                        intent.putExtra("vendorId", getIntent().getExtras().getInt("vendorId"));
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setUpVendorAddress(JSONObject vendorDetailsJsonObj){
        tvAddressVendorDetails = (TextView) findViewById(R.id.tvAddressVendorDetails);
        try {
            //JSONObject vendorDetailsJsonObj = new JSONObject(getIntent().getExtras().getString("vendorDetailsJsonStr"));
            vendorAddress = vendorDetailsJsonObj.getString("longAddress");
            tvAddressVendorDetails.setText(vendorDetailsJsonObj.getString("longAddress"));
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setUpVendorDirectionInfo(JSONObject vendorDetailsJsonObj){
        tvShopOpenStatusVendorDetails = (TextView) findViewById(R.id.tvShopOpenStatusVendorDetails);
        tvShopClosingTimeVendorDetails = (TextView) findViewById(R.id.tvShopClosingTimeVendorDetails);
        tvDistanceVendorDetails = (TextView) findViewById(R.id.tvDistanceVendorDetails);
        cvGetDirectionVendorDetails = (CardView) findViewById(R.id.cvGetDirectionVendorDetails);
        imgAddressVendorDetails = (ImageView) findViewById(R.id.imgAddressVendorDetails);
        try{
          //  JSONObject vendorDetailsJsonObj = new JSONObject(getIntent().getExtras().getString("vendorDetailsJsonStr"));
            vendorLatitude = vendorDetailsJsonObj.getString("latitude");
            vendorLongitude = vendorDetailsJsonObj.getString("longitude");
            if(vendorDetailsJsonObj.getBoolean("openStatus")){
                tvShopOpenStatusVendorDetails.setText("Open Now ");
                tvShopClosingTimeVendorDetails.setText("Until " + vendorDetailsJsonObj.getString("closingTime"));
                tvShopOpenStatusVendorDetails.setTextColor(Color.parseColor("#8bc058"));
            } else {
                tvShopOpenStatusVendorDetails.setText("Close Now ");
                tvShopClosingTimeVendorDetails.setText("Until " + vendorDetailsJsonObj.getString("openingTime"));
                tvShopOpenStatusVendorDetails.setTextColor(Color.parseColor("#DD3535"));
            }
            LatLng currentLatLng = new LatLng(Double.parseDouble(_pref.getSession(ConstantClass.TAG_LATITUDE)),
                    Double.parseDouble(_pref.getSession(ConstantClass.TAG_LONGITUDE)));
            LatLng vendorsLatLng = new LatLng(Double.parseDouble(vendorLatitude), Double.parseDouble(vendorLongitude));
            double distance =  Math.round(_commonMethods.CalculationByDistance(currentLatLng, vendorsLatLng) * 100.0) / 100.0;
            tvDistanceVendorDetails.setText(String.valueOf(distance) + " KM");
            cvGetDirectionVendorDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(VendorDetailsActivity.this);
                    builder.setMessage("Are you sure you want to get directed?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    String uri = "http://maps.google.com/maps?saddr=" + _pref.getSession(ConstantClass.TAG_LATITUDE) + "," +
                                            _pref.getSession(ConstantClass.TAG_LONGITUDE) +
                                            "&daddr=" + vendorLatitude + "," + vendorLongitude;
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                                    intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            });
            imgAddressVendorDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(VendorDetailsActivity.this);
                    builder.setMessage("Are you sure you want to get directed?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    String uri = "http://maps.google.com/maps?saddr=" + _pref.getSession(ConstantClass.TAG_LATITUDE) + "," +
                                            _pref.getSession(ConstantClass.TAG_LONGITUDE) +
                                            "&daddr=" + vendorLatitude + "," + vendorLongitude;
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                                    intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setUpImageGallery(JSONObject vendorDetailsJsonObj){
        ivGalleryImageOneVendorDetails = (ImageView) findViewById(R.id.ivGalleryImageOneVendorDetails);
        ivGalleryImageTwoVendorDetails = (ImageView) findViewById(R.id.ivGalleryImageTwoVendorDetails);
        ivGalleryImageThreeVendorDetails = (ImageView) findViewById(R.id.ivGalleryImageThreeVendorDetails);
        tvNoOfGalleryImageVendorDetails  = (TextView) findViewById(R.id.tvNoOfGalleryImageVendorDetails);
        llSeparatorOneVendorDetails = (LinearLayout) findViewById(R.id.llSeparatorOneVendorDetails);
        llSeparatorTwoVendorDetails = (LinearLayout) findViewById(R.id.llSeparatorTwoVendorDetails);
        llSeparatorThreeVendorDetails = (LinearLayout) findViewById(R.id.llSeparatorThreeVendorDetails);
        llGalleryHeadingIcon = (LinearLayout) findViewById(R.id.llGalleryHeadingIcon);
        llShowGallery = (LinearLayout) findViewById(R.id.llShowGallery);
        llOpenCamera = (LinearLayout) findViewById(R.id.llOpenCamera);
        final int noOfGalleryImage;
        try {
            //JSONObject vendorDetailsJsonObj = new JSONObject(getIntent().getExtras().getString("vendorDetailsJsonStr"));
            final JSONArray imageGalleryJsonArray = vendorDetailsJsonObj.getJSONArray("imageGallery");
            tvNoOfGalleryImageVendorDetails.setText(String.valueOf(imageGalleryJsonArray.length()));
            noOfGalleryImage = imageGalleryJsonArray.length();
            for(int i=0; i<imageGalleryJsonArray.length(); i++){
                JSONObject imageUrlJsonObj = imageGalleryJsonArray.getJSONObject(i);
                switch (i){
                    case 0:
                        ivGalleryImageOneVendorDetails.setVisibility(View.VISIBLE);
                        imageLoader.displayImage(imageUrlJsonObj.getString("imageUrl"), ivGalleryImageOneVendorDetails,
                                optionsHeadingImage);
                        llSeparatorOneVendorDetails.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        ivGalleryImageTwoVendorDetails.setVisibility(View.VISIBLE);
                        imageLoader.displayImage(imageUrlJsonObj.getString("imageUrl"), ivGalleryImageTwoVendorDetails,
                                optionsHeadingImage);
                        llSeparatorTwoVendorDetails.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        ivGalleryImageThreeVendorDetails.setVisibility(View.VISIBLE);
                        imageLoader.displayImage(imageUrlJsonObj.getString("imageUrl"), ivGalleryImageThreeVendorDetails,
                                optionsHeadingImage);
                        llSeparatorThreeVendorDetails.setVisibility(View.VISIBLE);
                        break;
                }
            }

            llShowGallery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(noOfGalleryImage > 0) {
                        Intent intent = new Intent(VendorDetailsActivity.this, GalleryActivity.class);
                        intent.putExtra("galleryImageJsonArrayStr", imageGalleryJsonArray.toString());
                        intent.putExtra("vendorName", vendorName);
                        intent.putExtra("vendorAddress", vendorAddress);
                        intent.putExtra("vendorId", getIntent().getExtras().getInt("vendorId"));
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    } else {
                        Toast.makeText(VendorDetailsActivity.this, "No gallery image is available.", Toast.LENGTH_LONG).show();
                    }
                }
            });

            ivGalleryImageOneVendorDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(noOfGalleryImage > 0) {
                        Intent intent = new Intent(VendorDetailsActivity.this, GalleryActivity.class);
                        intent.putExtra("galleryImageJsonArrayStr", imageGalleryJsonArray.toString());
                        intent.putExtra("vendorName", vendorName);
                        intent.putExtra("vendorAddress", vendorAddress);
                        intent.putExtra("vendorId", getIntent().getExtras().getInt("vendorId"));
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    } else {
                        Toast.makeText(VendorDetailsActivity.this, "No gallery image is available.", Toast.LENGTH_LONG).show();
                    }
                }
            });

            ivGalleryImageTwoVendorDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(noOfGalleryImage > 0) {
                        Intent intent = new Intent(VendorDetailsActivity.this, GalleryActivity.class);
                        intent.putExtra("galleryImageJsonArrayStr", imageGalleryJsonArray.toString());
                        intent.putExtra("vendorName", vendorName);
                        intent.putExtra("vendorAddress", vendorAddress);
                        intent.putExtra("vendorId", getIntent().getExtras().getInt("vendorId"));
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    } else {
                        Toast.makeText(VendorDetailsActivity.this, "No gallery image is available.", Toast.LENGTH_LONG).show();
                    }
                }
            });

            ivGalleryImageThreeVendorDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(noOfGalleryImage > 0) {
                        Intent intent = new Intent(VendorDetailsActivity.this, GalleryActivity.class);
                        intent.putExtra("galleryImageJsonArrayStr", imageGalleryJsonArray.toString());
                        intent.putExtra("vendorName", vendorName);
                        intent.putExtra("vendorAddress", vendorAddress);
                        intent.putExtra("vendorId", getIntent().getExtras().getInt("vendorId"));
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    } else {
                        Toast.makeText(VendorDetailsActivity.this, "No gallery image is available.", Toast.LENGTH_LONG).show();
                    }
                }
            });

            llGalleryHeadingIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (noOfGalleryImage > 0) {
                        Intent intent = new Intent(VendorDetailsActivity.this, GalleryActivity.class);
                        intent.putExtra("galleryImageJsonArrayStr", imageGalleryJsonArray.toString());
                        intent.putExtra("vendorName", vendorName);
                        intent.putExtra("vendorAddress", vendorAddress);
                        intent.putExtra("vendorId", getIntent().getExtras().getInt("vendorId"));
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    } else {
                        Toast.makeText(VendorDetailsActivity.this, "No gallery image is available.", Toast.LENGTH_LONG).show();
                    }
                }
            });

            llOpenCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*Intent intent = new Intent(VendorDetailsActivity.this, CameraActivity.class);
                    intent.putExtra("vendorId", getIntent().getExtras().getInt("vendorId"));
                    intent.putExtra("routeFrom", "vendor_details");
                    startActivity(intent);*/

                    if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                        int hasWriteAccessPermission = checkSelfPermission(Manifest.permission.CAMERA);
                        if (hasWriteAccessPermission != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(new String[] {Manifest.permission.CAMERA},
                                    REQUEST_CODE_ASK_PERMISSIONS);
                            return;
                        }else{
                            selectImage();
                        }




                    }else {
                        selectImage();
                    }

                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setUpUserReviewList(JSONObject vendorDetailsJsonObj){
        llUserReviewContainer = (LinearLayout) findViewById(R.id.llUserReviewContainer);
        llReadAllReviews = (LinearLayout) findViewById(R.id.llReadAllReviews);
        tvReadAllReviewText = (TextView) findViewById(R.id.tvReadAllReviewText);
        llReviewContainerMoreThanTwo = new LinearLayout(this);
        LinearLayout.LayoutParams llReviewContainerMoreThanTwoParam =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        llReviewContainerMoreThanTwo.setLayoutParams(llReviewContainerMoreThanTwoParam);
        llReviewContainerMoreThanTwo.setOrientation(LinearLayout.VERTICAL);
        llReviewContainerMoreThanTwo.setVisibility(View.GONE);
        try {
            //JSONObject vendorDetailsJsonObj = new JSONObject(getIntent().getExtras().getString("vendorDetailsJsonStr"));
            JSONArray vendorUserReviewListJsonArray = vendorDetailsJsonObj.getJSONArray("userReview");
            tvReadAllReviewText.setText("READ ALL REVIEWS("+ String.valueOf(vendorUserReviewListJsonArray.length())+")");
            for (int i = 0; i < vendorUserReviewListJsonArray.length(); i++) {
                initUserReviewLayout(i, vendorUserReviewListJsonArray.getJSONObject(i));
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        llUserReviewContainer.addView(llReviewContainerMoreThanTwo);

        llReadAllReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!flagReadAllReviewsOpen){
                    llReviewContainerMoreThanTwo.setVisibility(View.VISIBLE);
                    flagReadAllReviewsOpen = true;
                } else {
                    llReviewContainerMoreThanTwo.setVisibility(View.GONE);
                    flagReadAllReviewsOpen = false;
                }
            }
        });
    }

    private void setUpFoodMenu(  JSONObject vendorDetailsJsonObj){
        llFoodMenuVendorDetails = (LinearLayout) findViewById(R.id.llFoodMenuVendorDetails);
        cvFoodMenuMoreVendorDetails = (CardView) findViewById(R.id.cvFoodMenuMoreVendorDetails);
        tvFoodMenuMoreVendorDetails = (TextView)findViewById(R.id.tvFoodMenuMoreVendorDetails);
        final ArrayList<LinearLayout> parentMenuLayoutList = new ArrayList<>();
        try {
           // JSONObject vendorDetailsJsonObj = new JSONObject(getIntent().getExtras().getString("vendorDetailsJsonStr"));
            JSONArray vendorFoodMenuJsonArray = vendorDetailsJsonObj.getJSONArray("longFoodMenu");
            int noOfRow = vendorFoodMenuJsonArray.length();
            int foodMenuCount = 0;

           /* if(vendorFoodMenuJsonArray.length()%2 == 0){
                noOfRow = vendorFoodMenuJsonArray.length()/2;
            } else {
                noOfRow = (vendorFoodMenuJsonArray.length()/2)+1;
            }*/

            for (int i = 0; i<vendorFoodMenuJsonArray.length(); i++) {
                LinearLayout parentMenuLayout = new LinearLayout(this);
                LinearLayout.LayoutParams parentMenuLayoutParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 70);
                parentMenuLayout.setWeightSum(10);
                parentMenuLayout.setOrientation(LinearLayout.VERTICAL);
                if (i > 1) {
                    parentMenuLayout.setVisibility(View.GONE);
                }
               /* boolean rightMenuAvailable = true;
                if((foodMenuCount+1) == vendorFoodMenuJsonArray.length()){
                    rightMenuAvailable = false;
                }
                JSONObject foodMenuLeftItemJsonObj = vendorFoodMenuJsonArray.getJSONObject(foodMenuCount);
                LinearLayout leftChildMenuLayout = initiateLeftFoodMenu(foodMenuLeftItemJsonObj.getString("itemName").trim(),
                        foodMenuLeftItemJsonObj.getString("itemPrice"), rightMenuAvailable);
                foodMenuCount++;
                parentMenuLayout.addView(leftChildMenuLayout);*/
                if(foodMenuCount < vendorFoodMenuJsonArray.length()) {
                    JSONObject foodMenuRightItemJsonObj = vendorFoodMenuJsonArray.getJSONObject(foodMenuCount);
                    LinearLayout rightChildMenuLayout = initiateRightFoodMenu(foodMenuRightItemJsonObj.getString("itemName").trim(),
                            foodMenuRightItemJsonObj.getString("itemPrice"));
                    foodMenuCount++;
                    parentMenuLayout.addView(rightChildMenuLayout);
                }
                parentMenuLayoutList.add(parentMenuLayout);
                llFoodMenuVendorDetails.addView(parentMenuLayout, parentMenuLayoutParam);
            }

            if(noOfRow > 2){
                cvFoodMenuMoreVendorDetails.setVisibility(View.VISIBLE);
            }else{
                cvFoodMenuMoreVendorDetails.setVisibility(View.GONE);
            }
        } catch (Exception e){
            e.printStackTrace();
        }



        cvFoodMenuMoreVendorDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flagFoodMenuMore){
                    flagFoodMenuMore = false;
                    for (int i=2; i<parentMenuLayoutList.size(); i++){
                        parentMenuLayoutList.get(i).setVisibility(View.GONE);
                        tvFoodMenuMoreVendorDetails.setText("MORE");
                    }
                } else {
                    flagFoodMenuMore = true;
                    for (int i=2; i<parentMenuLayoutList.size(); i++){
                        parentMenuLayoutList.get(i).setVisibility(View.VISIBLE);
                        tvFoodMenuMoreVendorDetails.setText("LESS");

                    }
                }
            }
        });
    }

    private LinearLayout initiateLeftFoodMenu(String foodItem, String price, boolean rightMenuAvailable){
        LinearLayout leftChildMenuLayout = new LinearLayout(this);
        LinearLayout.LayoutParams leftChildMenuLayoutParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 5);
        leftChildMenuLayout.setLayoutParams(leftChildMenuLayoutParam);
        if(rightMenuAvailable) {
            leftChildMenuLayout.setBackgroundResource(R.drawable.menu_item_border_left);
        } else {
            leftChildMenuLayout.setBackgroundResource(R.drawable.menu_item_border_without_side);
        }
        leftChildMenuLayout.setOrientation(LinearLayout.HORIZONTAL);
        leftChildMenuLayout.setWeightSum(10);

        LinearLayout leftMenuItemLayout = new LinearLayout(this);
        LinearLayout.LayoutParams leftMenuItemLayoutParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, 5);
        leftMenuItemLayout.setLayoutParams(leftMenuItemLayoutParam);
        leftMenuItemLayout.setGravity(Gravity.CENTER_VERTICAL);

        BariolRegularTextView leftFoodItem = new BariolRegularTextView(this);
        ViewGroup.LayoutParams leftFoodItemParam = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        leftFoodItem.setLayoutParams(leftFoodItemParam);
        leftFoodItem.setText(foodItem);
        leftFoodItem.setTextSize(10);
        leftFoodItem.setMaxLines(5);
        leftFoodItem.setEllipsize(TextUtils.TruncateAt.END);
        leftFoodItem.setPadding(8, 0, 0, 0);
         leftFoodItem.setSingleLine(true);
        leftMenuItemLayout.addView(leftFoodItem);
        leftChildMenuLayout.addView(leftMenuItemLayout);

        LinearLayout leftMenuPriceItemLayout = new LinearLayout(this);
        LinearLayout.LayoutParams leftMenuPriceItemLayoutParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, 5);
        leftMenuPriceItemLayout.setLayoutParams(leftMenuPriceItemLayoutParam);
        leftMenuPriceItemLayout.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);

        BariolRegularTextView leftFoodItemPrice = new BariolRegularTextView(this);
        ViewGroup.LayoutParams leftFoodItemPriceParam = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        leftFoodItemPrice.setLayoutParams(leftFoodItemPriceParam);
        leftFoodItemPrice.setText(price);
        leftFoodItemPrice.setTextSize(10);
        leftFoodItemPrice.setMaxLines(30);
        leftFoodItemPrice.setPadding(0, 0, 4, 0);
         leftFoodItemPrice.setSingleLine(true);
        leftMenuPriceItemLayout.addView(leftFoodItemPrice);
        leftChildMenuLayout.addView(leftMenuPriceItemLayout);

        return leftChildMenuLayout;
    }

    private LinearLayout initiateRightFoodMenu(String foodItem, String price){
        LinearLayout rightChildMenuLayout = new LinearLayout(this);
        LinearLayout.LayoutParams rightChildMenuLayoutParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 5);
        rightChildMenuLayout.setBackgroundResource(R.drawable.menu_item_border_right);
        rightChildMenuLayout.setLayoutParams(rightChildMenuLayoutParam);
        rightChildMenuLayout.setWeightSum(10);

        LinearLayout rightMenuItemLayout = new LinearLayout(this);
        LinearLayout.LayoutParams rightMenuItemLayoutParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, 5);
        rightMenuItemLayout.setLayoutParams(rightMenuItemLayoutParam);
        rightMenuItemLayout.setGravity(Gravity.CENTER_VERTICAL);

        BariolRegularTextView rightFoodItem = new BariolRegularTextView(this);
        ViewGroup.LayoutParams rightFoodItemParam = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rightFoodItem.setLayoutParams(rightFoodItemParam);
        rightFoodItem.setText(foodItem);
        rightFoodItem.setTextSize(10);
        rightFoodItem.setMaxLines(30);
        rightFoodItem.setPadding(8, 0, 0, 0);
        rightFoodItem.setEllipsize(TextUtils.TruncateAt.END);
        rightFoodItem.setSingleLine(true);
        rightMenuItemLayout.addView(rightFoodItem);
        rightChildMenuLayout.addView(rightMenuItemLayout);

        LinearLayout rightMenuPriceItemLayout = new LinearLayout(this);
        LinearLayout.LayoutParams rightMenuPriceItemLayoutParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, 5);
        rightMenuPriceItemLayout.setLayoutParams(rightMenuPriceItemLayoutParam);
        rightMenuPriceItemLayout.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);

        BariolRegularTextView rightFoodItemPrice = new BariolRegularTextView(this);
        ViewGroup.LayoutParams rightFoodItemPriceParam = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rightFoodItemPrice.setLayoutParams(rightFoodItemPriceParam);
        rightFoodItemPrice.setText(price);
        rightFoodItemPrice.setTextSize(10);
        rightFoodItemPrice.setMaxLines(30);
        rightFoodItemPrice.setPadding(0, 0, 4, 0);
        rightFoodItemPrice.setSingleLine(true);
        rightMenuPriceItemLayout.addView(rightFoodItemPrice);
        rightChildMenuLayout.addView(rightMenuPriceItemLayout);

        return rightChildMenuLayout;
    }

    private void initUserReviewLayout(int reviewIndex, final JSONObject reviewInfo){
        try {
            final String reviewerId = String.valueOf(reviewInfo.getInt("id"));
            LinearLayout llReviewRowContainer = new LinearLayout(this);
            LinearLayout.LayoutParams llReviewRowContainerParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            llReviewRowContainer.setLayoutParams(llReviewRowContainerParam);
            llReviewRowContainer.setOrientation(LinearLayout.VERTICAL);
            if (_pref.getIntegerSession(ConstantClass.TAG_USER_REVIEW_AGREE_FLAG + reviewerId) != 1) {
                Log.e(ConstantClass.TAG_USER_REVIEW_AGREE_FLAG + reviewerId,
                        String.valueOf(_pref.getIntegerSession(ConstantClass.TAG_USER_REVIEW_AGREE_FLAG + reviewerId)));
                _pref.setSession(ConstantClass.TAG_USER_REVIEW_AGREE_FLAG + reviewerId, 0);
            }

            ////////////////// Set Profile Name and Image /////////////////////
            LinearLayout llProfileImageNameContainer = new LinearLayout(this);
            LinearLayout.LayoutParams llProfileImageNameContainerParam = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            llProfileImageNameContainer.setGravity(Gravity.CENTER);
            llProfileImageNameContainer.setOrientation(LinearLayout.HORIZONTAL);
            llProfileImageNameContainer.setLayoutParams(llProfileImageNameContainerParam);
            llProfileImageNameContainer.setPadding(0, 35, 0, 0);

            CircularImageView civProfileImage = new CircularImageView(this);
            ViewGroup.LayoutParams civProfileImageParam = new ViewGroup.LayoutParams(70, 70);
            civProfileImage.setLayoutParams(civProfileImageParam);
            llProfileImageNameContainer.addView(civProfileImage);
            imageLoader.displayImage(reviewInfo.getString("reviewerImage"), civProfileImage, optionReviewerProfileImage);

            BariolItalicTextView tvProfileName = new BariolItalicTextView(this);
            ViewGroup.LayoutParams tvProfileNameParam = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            tvProfileName.setLayoutParams(tvProfileNameParam);
            tvProfileName.setPadding(10, 0, 0, 0);
            tvProfileName.setText(reviewInfo.getString("reviewerName"));
            tvProfileName.setTextColor(Color.parseColor("#4E4F53"));
            tvProfileName.setTextSize(16);
            llProfileImageNameContainer.addView(tvProfileName);
            ////////////////// Set Profile Name and Image End /////////////////////

            LinearLayout llRatingContainerLayout = new LinearLayout(this);
            LinearLayout.LayoutParams llRatingContainerLayoutParam = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            llRatingContainerLayoutParam.setMargins(0, 4, 0, 0);
            llRatingContainerLayout.setLayoutParams(llRatingContainerLayoutParam);
            llRatingContainerLayout.setOrientation(LinearLayout.HORIZONTAL);
            llRatingContainerLayout.setGravity(Gravity.CENTER);

            BariolRegularTextView tvRatingText = new BariolRegularTextView(this);
            ViewGroup.LayoutParams tvRatingTextParam = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            tvRatingText.setLayoutParams(tvRatingTextParam);
            tvRatingText.setText("RATED");
            llRatingContainerLayout.addView(tvRatingText);

            LinearLayout llRatingPointContainer = new LinearLayout(this);
            LinearLayout.LayoutParams llRatingPointContainerParam = new LinearLayout.LayoutParams(75, 38);
            llRatingPointContainerParam.setMargins(8, 0, 8, 0);
            llRatingPointContainer.setLayoutParams(llRatingPointContainerParam);
            llRatingPointContainer.setGravity(Gravity.CENTER);
            _commonMethods.setRatingContainerLayoutBackground(llRatingPointContainer,
                    String.valueOf(reviewInfo.getDouble("reviewerRating")));

            BariolLightTextView llRatingPoint = new BariolLightTextView(this);
            ViewGroup.LayoutParams llRatingPointParam = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            llRatingPoint.setLayoutParams(llRatingPointParam);
            llRatingPoint.setText(String.valueOf(reviewInfo.getDouble("reviewerRating")));
            llRatingPoint.setTextColor(Color.parseColor("#000000"));
            llRatingPointContainer.addView(llRatingPoint);
            llRatingContainerLayout.addView(llRatingPointContainer);

            BariolItalicTextView tvLastVisitedTime = new BariolItalicTextView(this);
            ViewGroup.LayoutParams tvLastVisitedTimeParam = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            tvLastVisitedTime.setLayoutParams(tvLastVisitedTimeParam);
            tvLastVisitedTime.setText("[" + reviewInfo.getString("lastVisitDate") + "]");
            tvLastVisitedTime.setPadding(4, 0, 0, 0);
            tvLastVisitedTime.setTextColor(Color.parseColor("#4D606F"));
            llRatingContainerLayout.addView(tvLastVisitedTime);

            LinearLayout llUserReviewContainerLayout = new LinearLayout(this);
            LinearLayout.LayoutParams llUserReviewContainerLayoutParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            llUserReviewContainerLayout.setLayoutParams(llUserReviewContainerLayoutParam);
            llUserReviewContainerLayout.setGravity(Gravity.CENTER);
            llUserReviewContainerLayout.setOrientation(LinearLayout.HORIZONTAL);
            llUserReviewContainerLayout.setPadding(30, 15, 30, 15);

            BariolLightTextView tvReviewText = new BariolLightTextView(this);
            ViewGroup.LayoutParams tvReviewTextParam = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            tvReviewText.setLayoutParams(tvReviewTextParam);
            tvReviewText.setGravity(Gravity.CENTER);
            tvReviewText.setText(reviewInfo.getString("review"));
            tvReviewText.setTextSize(16);
            tvReviewText.setTextColor(Color.parseColor("#5D6871"));
            llUserReviewContainerLayout.addView(tvReviewText);

            LinearLayout llAgreedContainerLayout = new LinearLayout(this);
            LinearLayout.LayoutParams llAgreedContainerLayoutParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            llAgreedContainerLayoutParam.setMargins(0, 12, 0, 0);
            llAgreedContainerLayout.setLayoutParams(llAgreedContainerLayoutParam);
            llAgreedContainerLayout.setGravity(Gravity.CENTER);
            llAgreedContainerLayout.setOrientation(LinearLayout.HORIZONTAL);
            llAgreedContainerLayout.setPadding(0, 0, 0, 35);

            LinearLayout llAgreedCountContainer = new LinearLayout(this);
            LinearLayout.LayoutParams llAgreedCountContainerParam = new LinearLayout.LayoutParams(50, 50);
            llAgreedCountContainer.setLayoutParams(llAgreedCountContainerParam);
            llAgreedCountContainer.setGravity(Gravity.CENTER);
            llAgreedCountContainer.setBackground(getResources().getDrawable(R.drawable.round_green_agreed_bg));

            tvAgreedCount = new BariolLightTextView(this);
            ViewGroup.LayoutParams tvAgreedCountParam = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            tvAgreedCount.setLayoutParams(tvAgreedCountParam);
           /* if (_pref.getIntegerSession(ConstantClass.TAG_USER_REVIEW_AGREE_FLAG + reviewerId) == 0) {*/
                _pref.setSession(ConstantClass.TAG_USER_REVIEW_AGREE_COUNT + reviewerId, reviewInfo.getInt("noOfAgreed"));
                _pref.setSession(ConstantClass.TAG_USER_REVIEW_AGREE_STATUS + reviewerId, reviewInfo.getBoolean("agreedStaus"));
                _pref.setSession(ConstantClass.TAG_USER_REVIEW_AGREE_FLAG + reviewerId, 1);
           // }
            tvAgreedCount.setText(String.valueOf(
                    _pref.getIntegerSession(ConstantClass.TAG_USER_REVIEW_AGREE_COUNT + reviewerId)));
            tvAgreedCount.setTextSize(16);
            tvAgreedCount.setTextColor(Color.parseColor("#FFFFFF"));
            lightTextViewsArrListForAgreeCount.add(tvAgreedCount);
            llAgreedCountContainer.addView(tvAgreedCount);
            llAgreedContainerLayout.addView(llAgreedCountContainer);

            BariolRegularTextView tvAgreedText = new BariolRegularTextView(this);
            ViewGroup.LayoutParams tvAgreedTextParam = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            tvAgreedText.setLayoutParams(tvAgreedTextParam);
            tvAgreedText.setText("Agreed");
            tvAgreedText.setTextColor(Color.parseColor("#4E4F53"));
            tvAgreedText.setTextSize(17);
            tvAgreedText.setPadding(10, 0, 15, 0);
            llAgreedContainerLayout.addView(tvAgreedText);

            FrameLayout flDividerLine = new FrameLayout(this);
            FrameLayout.LayoutParams flDividerLineParam = new FrameLayout.LayoutParams(2, ViewGroup.LayoutParams.MATCH_PARENT);
            flDividerLine.setLayoutParams(flDividerLineParam);
            flDividerLine.setBackgroundColor(Color.parseColor("#D9C0C9"));
            flDividerLine.setPadding(0, 0, 18, 0);
            llAgreedContainerLayout.addView(flDividerLine);
            LinearLayout llAgreedButtonContainerLayout = new LinearLayout(this);
            LinearLayout.LayoutParams llAgreedButtonContainerLayoutParam = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            llAgreedButtonContainerLayout.setOrientation(LinearLayout.HORIZONTAL);
            llAgreedButtonContainerLayout.setLayoutParams(llAgreedButtonContainerLayoutParam);
            llAgreedButtonContainerLayout.setPadding(18, 0, 0, 0);
            Button btnAgreedActivity = new Button(this);
            ViewGroup.LayoutParams btnAgreedActivityParam = new ViewGroup.LayoutParams(145, 45);
            btnAgreedActivity.setLayoutParams(btnAgreedActivityParam);
            if (!_pref.getBooleanSession(ConstantClass.TAG_USER_REVIEW_AGREE_STATUS + reviewerId)) {
                btnAgreedActivity.setText("I AGREE");
            } else {
                btnAgreedActivity.setText("DISAGREE");
            }

            btnAgreedActivity.setTextSize(15);
            btnAgreedActivity.setTextColor(Color.parseColor("#FFFFFF"));
            btnAgreedActivity.setBackground(getResources().getDrawable(R.drawable.round_corners_side_option_bg));
            btnAgreedActivity.setTag(reviewIndex);
            btnArrListForAgreeAction.add(btnAgreedActivity);
            llAgreedButtonContainerLayout.addView(btnAgreedActivity);
            llAgreedContainerLayout.addView(llAgreedButtonContainerLayout);
            btnAgreedActivity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(VendorDetailsActivity.this, v.getTag().toString(), Toast.LENGTH_LONG).show();
                    selectedAgreedItem = Integer.parseInt(v.getTag().toString());
                    selectedReviewerId = reviewerId;
                    setUpProgressDialog();
                    flagAgreedService = 1;
                    setInputParamForAgreed(reviewerId,
                            _pref.getBooleanSession(ConstantClass.TAG_USER_REVIEW_AGREE_STATUS + reviewerId));
                }
            });
            FrameLayout flDividerHorizontalLine = new FrameLayout(this);
            FrameLayout.LayoutParams flDividerHorizontalLineParam = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 2);
            flDividerHorizontalLine.setLayoutParams(flDividerHorizontalLineParam);
            flDividerHorizontalLine.setBackgroundColor(Color.parseColor("#D9C0C9"));

            llReviewRowContainer.addView(llProfileImageNameContainer);
            llReviewRowContainer.addView(llRatingContainerLayout);
            llReviewRowContainer.addView(llUserReviewContainerLayout);
            llReviewRowContainer.addView(llAgreedContainerLayout);
            llReviewRowContainer.addView(flDividerHorizontalLine);
            if (reviewIndex < 2) {
                llUserReviewContainer.addView(llReviewRowContainer);
            } else {
                llReviewContainerMoreThanTwo.addView(llReviewRowContainer);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    View.OnClickListener bookmarkListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(_connection.isNetworkAvailable()) {
                flagBookMarkService = 1;
                setUpProgressDialog();
                setInputParamForBookMark();
            } else {
                _connection.getNetworkActiveAlert().show();
            }
        }
    };

    private void setInputParamForBookMark(){
        try {
            JSONObject parentJsonObj = new JSONObject();
            JSONObject childJsonObj = new JSONObject();
            childJsonObj.put("accessToken", _pref.getSession(ConstantClass.ACCESS_TOKEN));
            childJsonObj.put("vendorId", getIntent().getExtras().getInt("vendorId"));
            if(bookMarkStatus) {
                childJsonObj.put("status", "false");
            } else {
                childJsonObj.put("status", "true");
            }
            parentJsonObj.put("data", childJsonObj);
            Log.e("input data", parentJsonObj.toString());
            _serviceAction.requestVersionApi(parentJsonObj, "add-bookmark");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setInputParamForLike(){
        try {
            JSONObject parentJsonObj = new JSONObject();
            JSONObject childJsonObj = new JSONObject();
            childJsonObj.put("accessToken", _pref.getSession(ConstantClass.ACCESS_TOKEN));
            childJsonObj.put("vendorId", getIntent().getExtras().getInt("vendorId"));
            if(likeStatus) {
                childJsonObj.put("status", "false");
            } else {
                childJsonObj.put("status", "true");
            }
            parentJsonObj.put("data", childJsonObj);
            Log.e("input data", parentJsonObj.toString());
            _serviceAction.requestVersionApi(parentJsonObj, "add-like-vendor");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setInputParamForAgreed(String reviewerId, Boolean agreeStatus){
        try {
            JSONObject parentJsonObj = new JSONObject();
            JSONObject childJsonObj = new JSONObject();
            childJsonObj.put("accessToken", _pref.getSession(ConstantClass.ACCESS_TOKEN));
            childJsonObj.put("vendorId", getIntent().getExtras().getInt("vendorId"));
            childJsonObj.put("reviewerId", reviewerId);
            if(agreeStatus){
                childJsonObj.put("status", "false");
            } else {
                childJsonObj.put("status", "true");
            }
            parentJsonObj.put("data", childJsonObj);
            Log.e("input data", parentJsonObj.toString());
            _serviceAction.requestVersionApi(parentJsonObj, "add-agreed");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setUpCheckinParam(){
        try {
            JSONObject parentJsonObj = new JSONObject();
            JSONObject childJsonObj = new JSONObject();
            childJsonObj.put("accessToken", _pref.getSession(ConstantClass.ACCESS_TOKEN));
            childJsonObj.put("vendorId", getIntent().getExtras().getInt("vendorId"));
            childJsonObj.put("status", "true");
            parentJsonObj.put("data", childJsonObj);
            Log.e("input data", parentJsonObj.toString());
            _serviceAction.requestVersionApi(parentJsonObj, "add-checkin");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setUpMostSearchIncrementParam(){
        try {
            JSONObject parentJsonObj = new JSONObject();
            JSONObject childJsonObj = new JSONObject();
            childJsonObj.put("accessToken", _pref.getSession(ConstantClass.ACCESS_TOKEN));
            childJsonObj.put("vendorId", getIntent().getExtras().getInt("vendorId"));
            childJsonObj.put("city", _pref.getSession(ConstantClass.TAG_SELECTED_CITY_LOCATION));
            parentJsonObj.put("data", childJsonObj);
            Log.e("input data", parentJsonObj.toString());
            _serviceAction.requestVersionApi(parentJsonObj, "add-search-count");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSimpleFacebook = SimpleFacebook.getInstance(this);
        if (galleryImageSelection == 1){
            galleryImageSelection = 0;
            postSelectedImage();
        }
        checkCheckInMode();
       // setCheckinValue();

        if(flagTwitterShare == 1) {
            helpDialog.dismiss();
            flagCheckinService = 1;
            setUpCheckinParam();
            setUpProgressDialog();
        }
    }

    private void setUpProgressDialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait..");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
    }

    OnPublishListener onPublishListener = new OnPublishListener() {
        @Override
        public void onComplete(String postId) {
            Log.i(TAG, "Published successfully. The new post id = " + postId);
            //Toast.makeText(VendorDetailsActivity.this, "Published successfully", Toast.LENGTH_LONG).show();
            helpDialog.dismiss();
            flagCheckinService = 1;
            setUpCheckinParam();
            setUpProgressDialog();
        }

        @Override
        public void onException(Throwable throwable) {
            super.onException(throwable);
        }

        @Override
        public void onFail(String reason) {
            Log.i(TAG, "Failed message sharing = " + reason);
           // Toast.makeText(VendorDetailsActivity.this, "Publish failed.", Toast.LENGTH_LONG).show();
            super.onFail(reason);
        }
    };

    @Override
    public void onStarted() {

    }

    @Override
    public void onCompleted(JSONObject response) {
        Log.e("response", response.toString());

        try {
            JSONObject jsonObjData1 = response.getJSONObject("data");

            if(jsonObjData1.has("listItem"))
            {
                JSONArray jsonArrayDetails = jsonObjData1.getJSONArray("listItem");
                for(int g = 0;g<jsonArrayDetails.length();g++)
                {
                    JSONObject jsonObjectDetailseItems = jsonArrayDetails.getJSONObject(g);
                    ivHeadingImageVendorDetails = (ImageView) findViewById(R.id.ivHeadingImageVendorDetails);
                    ivBookMarkVendorDetails = (ImageView) findViewById(R.id.ivBookMarkVendorDetails);
                    imageLoader = ImageLoader.getInstance();
                    headerImageUrl = jsonObjectDetailseItems.getString("itemImageUrl");
                    imageLoader.displayImage(headerImageUrl, ivHeadingImageVendorDetails, optionsHeadingImage);
                    bookMarkStatus = jsonObjectDetailseItems.getBoolean("bookmarkStatus");
                    if(bookMarkStatus){
                        ivBookMarkVendorDetails.setImageResource(R.drawable.icon_ratting);
                    } else {
                        ivBookMarkVendorDetails.setImageResource(R.drawable.icon_ratting_gray);
                    }
                    _pref.setSession(ConstantClass.TAG_VENDOR_DETAILS_BOOKMARK_COUNT + getIntent()
                            .getExtras().getInt("vendorId"), String.valueOf(jsonObjectDetailseItems.getInt("bookmarkNo")));
                    ivBookMarkVendorDetails.setOnClickListener(bookmarkListener);

                    JSONObject jsonObjectVendorDetails = jsonObjectDetailseItems.getJSONObject("vendorDetails");
                    final JSONArray imageGalleryJsonArray = jsonObjectVendorDetails.getJSONArray("imageGallery");

                    setUpVendorAddress(jsonObjectVendorDetails);
                    setUpVendorPersonalInfo(jsonObjectVendorDetails);
                    setUpVendorDirectionInfo(jsonObjectVendorDetails);
                    setUpImageGallery(jsonObjectVendorDetails);
                    setUpFoodMenu(jsonObjectVendorDetails);
                    setUpUserReviewList(jsonObjectVendorDetails);
                    setUpSideMenu(jsonObjectVendorDetails);
                    setUpMostSearchIncrementParam();
                    setRecentSearchParam(jsonObjectVendorDetails);
                    setAddedUser(jsonObjectVendorDetails);
                    ivHeadingImageVendorDetails.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(VendorDetailsActivity.this, GalleryActivity.class);
                            intent.putExtra("galleryImageJsonArrayStr", imageGalleryJsonArray.toString());
                            intent.putExtra("vendorId", getIntent().getExtras().getInt("vendorId"));
                            intent.putExtra("vendorName", vendorName);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        }
                    });
                            /*
        try {
            JSONObject vendorDetailsJsonObj = new JSONObject(getIntent().getStringExtra("vendorDetailsJsonStr"));

            if(!bookMarkStatus) {
                bookMarkStatus = getIntent().getBooleanExtra("vendorBookMark", false);
            }


        } catch (Exception e){
            e.printStackTrace();
        }*/
                }

                rlDetailse.setVisibility(View.VISIBLE);
                llProgressbarVendorDetails.setVisibility(View.GONE);
            }
            if(flagBookMarkService == 1){
                JSONObject jsonObjError = response.getJSONObject("errNode");
                if (jsonObjError.getInt("errCode") == 0) {
                    JSONObject jsonObjData = response.getJSONObject("data");


                    if(jsonObjData.getBoolean("success")){
                        Toast.makeText(VendorDetailsActivity.this, jsonObjData.getString("msg"),
                                Toast.LENGTH_LONG).show();
                        if(bookMarkStatus){
                            ivBookMarkVendorDetails.setImageResource(R.drawable.icon_ratting_gray);
                            bookMarkStatus = false;
                            String bookmarkCount = _pref.getSession(ConstantClass.TAG_VENDOR_DETAILS_BOOKMARK_COUNT + getIntent()
                                    .getExtras().getInt("vendorId"));
                            _pref.setSession(ConstantClass.TAG_VENDOR_DETAILS_BOOKMARK_COUNT + getIntent()
                                    .getExtras().getInt("vendorId"), String.valueOf(Integer.parseInt(bookmarkCount) - 1));
                        } else {
                            ivBookMarkVendorDetails.setImageResource(R.drawable.icon_ratting);
                            bookMarkStatus = true;
                            _pref.setSession(ConstantClass.TAG_VENDOR_DETAILS_BOOKMARK_COUNT + getIntent()
                                    .getExtras().getInt("vendorId"), String.valueOf(jsonObjData.getInt("totalBookmark")));
                        }
                        tvBookmarkSideVendorDetails.setText(_pref.getSession(ConstantClass.TAG_VENDOR_DETAILS_BOOKMARK_COUNT + getIntent()
                                .getExtras().getInt("vendorId")) + " Bookmarks");
                    }
                } else {
                    Toast.makeText(VendorDetailsActivity.this, jsonObjError.getString("errMsg"), Toast.LENGTH_LONG).show();
                }
                flagBookMarkService = 0;
            } else if(flagAgreedService == 1){
                JSONObject jsonObjError = response.getJSONObject("errNode");
                if (jsonObjError.getInt("errCode") == 0) {
                    JSONObject jsonObjData = response.getJSONObject("data");
                    if (jsonObjData.getBoolean("success")) {
                        lightTextViewsArrListForAgreeCount.get(selectedAgreedItem).setText(jsonObjData.getString("total"));
                        if(jsonObjData.getBoolean("aggreed")) {
                            btnArrListForAgreeAction.get(selectedAgreedItem).setText("DISAGREED");
                        } else {
                            btnArrListForAgreeAction.get(selectedAgreedItem).setText("I AGREE");
                        }
                        _pref.setSession(ConstantClass.TAG_USER_REVIEW_AGREE_COUNT + selectedReviewerId,
                                Integer.parseInt(jsonObjData.getString("total")));
                        _pref.setSession(ConstantClass.TAG_USER_REVIEW_AGREE_STATUS + selectedReviewerId,
                                jsonObjData.getBoolean("aggreed"));
                        Toast.makeText(VendorDetailsActivity.this, jsonObjData.getString("msg"), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(VendorDetailsActivity.this, jsonObjData.getString("msg"), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(VendorDetailsActivity.this, jsonObjError.getString("errMsg"), Toast.LENGTH_LONG).show();
                }
                flagAgreedService = 0;
            } else if (flagLikeService == 1){
                JSONObject jsonObjError = response.getJSONObject("errNode");
                if (jsonObjError.getInt("errCode") == 0) {
                    JSONObject jsonObjData = response.getJSONObject("data");
                    if(jsonObjData.getBoolean("success")){
                        Toast.makeText(VendorDetailsActivity.this, jsonObjData.getString("msg"), Toast.LENGTH_LONG).show();
                        tvLikesSideVendorDetails.setText(jsonObjData.getString("nooflike") + " Likes");
                        _pref.setSession(ConstantClass.TAG_VENDOR_DETAILS_LIKE_COUNT + getIntent()
                                .getExtras().getInt("vendorId"), jsonObjData.getString("nooflike"));
                        if(likeStatus){
                            likeStatus = false;
                        } else {
                            likeStatus = true;
                        }
                    }
                } else {
                    Toast.makeText(VendorDetailsActivity.this, jsonObjError.getString("errMsg"), Toast.LENGTH_LONG).show();
                }
                flagLikeService = 0;
            } else if (flagCheckinService == 1){
                JSONObject jsonObjError = response.getJSONObject("errNode");
                if (jsonObjError.getInt("errCode") == 0) {
                    JSONObject jsonObjData = response.getJSONObject("data");
                    if(jsonObjData.getBoolean("success")){
                        Toast.makeText(VendorDetailsActivity.this, jsonObjData.getString("msg"), Toast.LENGTH_LONG).show();
                        _pref.setSession(ConstantClass.TAG_VENDOR_DETAILS_CHECK_IN_COUNT + getIntent().getExtras().getInt("vendorId")
                                , jsonObjData.getString("totalCheckin"));
                        tvCheckinSideVendorDetails.setText(jsonObjData.getString("totalCheckin") + " Checkin");
                    }
                } else {
                    Toast.makeText(VendorDetailsActivity.this, jsonObjError.getString("errMsg"), Toast.LENGTH_LONG).show();
                }
                flagCheckinService = 0;
                flagTwitterShare = 0;
            }
            progressDialog.dismiss();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setAddedUser(JSONObject jsonObjectVendorDetails) {


            String addedUserName = jsonObjectVendorDetails.optString("UserName");
            String url = jsonObjectVendorDetails.optString("UserImage");
            if(addedUserName.equals(""))
                {
                    crAddedByVendor.setVisibility(View.GONE);
                    tvAddedBy.setText("Vendor added by Unknown");

                }else {
                crAddedByVendor.setVisibility(View.VISIBLE);
                tvAddedBy.setText(addedUserName);
                imageLoader.displayImage(url,imgUsrName);
            }

    }

    private void setRecentSearchParam(JSONObject jsonObjectVendorDetails) {
        try {
            JSONObject RecentJsonObject = new JSONObject();
            RecentJsonObject.put("id",getIntent().getExtras().getInt("vendorId"));
            RecentJsonObject.put("keyValue","Vendor-"+getIntent().getExtras().getInt("vendorId"));
            RecentJsonObject.put("vendorName",jsonObjectVendorDetails.getString("shopName"));
            RecentJsonObject.put("searchType", "vendor");
            RecentJsonObject.put("vendorDetails", jsonObjectVendorDetails);
            Log.w("RecentJsonObject", RecentJsonObject.toString());
            setRecentSearchValue(RecentJsonObject.toString(), "vendor");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setRecentSearchValue(String jsonObjString, String itemType) {
        Log.v("jsonObjString", jsonObjString);
        try {
            int id;
            if (itemType.equalsIgnoreCase("vendor")) {
                JSONObject vendorJsonObj = new JSONObject(jsonObjString);
                id = vendorJsonObj.getInt("id");
            } else {
                JSONObject vendorJsonObj = new JSONObject(jsonObjString);
                id = vendorJsonObj.getInt("id");
            }
            switch (_pref.getIntegerSession(ConstantClass.TAG_RECENT_TOP_INDEX)) {
                case 0:
                    _pref.setSession(ConstantClass.TAG_RECENT_TOP_INDEX, 1);
                    _pref.setSession(ConstantClass.TAG_RECENT_BOTTOM_INDEX, 1);
                    _pref.setSession(ConstantClass.TAG_RECENT_SEARCHES_FIRST, jsonObjString);
                    break;
                case 1:
                    if (isRecentSearchItemExist(id, itemType)) {
                        _pref.setSession(ConstantClass.TAG_RECENT_TOP_INDEX, 2);
                        _pref.setSession(ConstantClass.TAG_RECENT_SEARCHES_SECOND, jsonObjString);
                    }
                    break;
                case 2:
                    if (isRecentSearchItemExist(id, itemType)) {
                        _pref.setSession(ConstantClass.TAG_RECENT_TOP_INDEX, 3);
                        _pref.setSession(ConstantClass.TAG_RECENT_SEARCHES_THIRD, jsonObjString);
                    }
                    break;
                case 3:
                    if (isRecentSearchItemExist(id, itemType)) {
                        _pref.setSession(ConstantClass.TAG_RECENT_TOP_INDEX, 4);
                        _pref.setSession(ConstantClass.TAG_RECENT_SEARCHES_FOURTH, jsonObjString);
                    }
                    break;
                default:
                    switch (_pref.getIntegerSession(ConstantClass.TAG_RECENT_BOTTOM_INDEX)) {
                        case 1:
                            if (isRecentSearchItemExist(id, itemType)) {
                                _pref.setSession(ConstantClass.TAG_RECENT_SEARCHES_FIRST, jsonObjString);
                                _pref.setSession(ConstantClass.TAG_RECENT_BOTTOM_INDEX, 2);
                            }
                            break;
                        case 2:
                            if (isRecentSearchItemExist(id, itemType)) {
                                _pref.setSession(ConstantClass.TAG_RECENT_SEARCHES_SECOND, jsonObjString);
                                _pref.setSession(ConstantClass.TAG_RECENT_BOTTOM_INDEX, 3);
                            }
                            break;
                        case 3:
                            if (isRecentSearchItemExist(id, itemType)) {
                                _pref.setSession(ConstantClass.TAG_RECENT_SEARCHES_THIRD, jsonObjString);
                                _pref.setSession(ConstantClass.TAG_RECENT_BOTTOM_INDEX, 4);
                            }
                            break;
                        case 4:
                            if (isRecentSearchItemExist(id, itemType)) {
                                _pref.setSession(ConstantClass.TAG_RECENT_SEARCHES_FOURTH, jsonObjString);
                                _pref.setSession(ConstantClass.TAG_RECENT_BOTTOM_INDEX, 1);
                            }
                            break;
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isRecentSearchItemExist(int id, String itemType){
        boolean exist = true;
        String storedJsonObjStr;
        try {
            for (int i=1; i<=4; i++) {
                switch (i) {
                    case 1:
                        storedJsonObjStr = _pref.getSession(ConstantClass.TAG_RECENT_SEARCHES_FIRST);
                        break;
                    case 2:
                        storedJsonObjStr = _pref.getSession(ConstantClass.TAG_RECENT_SEARCHES_SECOND);
                        break;
                    case 3:
                        storedJsonObjStr = _pref.getSession(ConstantClass.TAG_RECENT_SEARCHES_THIRD);
                        break;
                    default:
                        storedJsonObjStr = _pref.getSession(ConstantClass.TAG_RECENT_SEARCHES_FOURTH);
                        break;
                }
                if (!storedJsonObjStr.equals("")) {
                    if (itemType.equalsIgnoreCase("vendor")) {
                        JSONObject storedJsonObj = new JSONObject(storedJsonObjStr);
                        if (id == storedJsonObj.getInt("id")) {
                            exist = false;
                            Log.e("exist", exist + "");
                            break;
                        }
                    } else {
                        JSONObject storedJsonObj = new JSONObject(storedJsonObjStr);
                        if (id == storedJsonObj.getInt("id")) {
                            exist = false;
                            Log.e("exist", exist + "");
                            break;
                        }
                    }
                } else {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return exist;
    }

    private void checkCheckInMode(){
        if(getIntent().getExtras().getString("routeFrom").equalsIgnoreCase("home-checkin") &&
                flagCheckInComplete == 0){
            new Handler().postDelayed(new Runnable(){
                public void run() {
                    initializeCheckinPopup();
                }
            }, 100L);
        }
    }

    private void setLeftMargin(){
        new Handler().postDelayed(new Runnable(){
            public void run() {
                layoutParams.leftMargin = -30;
                llSidePanel.setLayoutParams(layoutParams);
                layoutParams.leftMargin = -20;
                llSidePanel.setLayoutParams(layoutParams);
                layoutParams.leftMargin = 0;
                llSidePanel.setLayoutParams(layoutParams);
                toolbarVendorDetails.setBackgroundColor(Color.parseColor("#00FFBB00"));
            }
        }, 50L);
    }

    private void setRatingIcon(String rating, ImageView ivRatingVendorDetails){

        double doubleRating = Double.parseDouble(rating);
        //region rating old images set
       /* switch (rating){
            case "0.0":
                ivRatingVendorDetails.setImageResource(R.drawable.icon_rating_00);
                break;

            case "0":
                ivRatingVendorDetails.setImageResource(R.drawable.icon_rating_00);
                break;

            case "0.5":
                ivRatingVendorDetails.setImageResource(R.drawable.icon_rating_05);
                break;

            case "1.0":
                ivRatingVendorDetails.setImageResource(R.drawable.icon_rating_10);
                break;

            case "1.5":
                ivRatingVendorDetails.setImageResource(R.drawable.icon_rating_15);
                break;

            case "2.0":
                ivRatingVendorDetails.setImageResource(R.drawable.icon_rating_20);
                break;

            case "2.5":
                ivRatingVendorDetails.setImageResource(R.drawable.icon_rating_25);
                break;

            case "3.0":
                ivRatingVendorDetails.setImageResource(R.drawable.icon_rating_30);
                break;

            case "3.5":
                ivRatingVendorDetails.setImageResource(R.drawable.icon_rating_35);
                break;

            case "4.0":
                ivRatingVendorDetails.setImageResource(R.drawable.icon_rating_40);
                break;

            case "4.5":
                ivRatingVendorDetails.setImageResource(R.drawable.icon_rating_45);
                break;

            default:
                ivRatingVendorDetails.setImageResource(R.drawable.icon_rating_50);
                break;
        }*/
        //endregion

        if(doubleRating<=5.0)
        {
                ivRatingVendorDetails.setImageResource(R.drawable.icon_rating_05);

            if(doubleRating<=4.9)
            {
                ivRatingVendorDetails.setImageResource(R.drawable.icon_rating_45);

                if(doubleRating<=4.0)
                {
                    ivRatingVendorDetails.setImageResource(R.drawable.icon_rating_40);

                    if(doubleRating<=3.9)
                    {
                        ivRatingVendorDetails.setImageResource(R.drawable.icon_rating_35);

                        if(doubleRating<=3.0)
                        {
                            ivRatingVendorDetails.setImageResource(R.drawable.icon_rating_30);

                            if(doubleRating<=2.9)
                            {
                                ivRatingVendorDetails.setImageResource(R.drawable.icon_rating_25);

                                if(doubleRating<=2.0)
                                {
                                    ivRatingVendorDetails.setImageResource(R.drawable.icon_rating_20);

                                    if(doubleRating<=1.9)
                                    {
                                        ivRatingVendorDetails.setImageResource(R.drawable.icon_rating_15);

                                        if(doubleRating<=1.0)
                                        {
                                            ivRatingVendorDetails.setImageResource(R.drawable.icon_rating_10);

                                            if(doubleRating<=0.9)
                                            {
                                                ivRatingVendorDetails.setImageResource(R.drawable.icon_rating_05);
                                                if(doubleRating<=0)
                                                {
                                                    ivRatingVendorDetails.setImageResource(R.drawable.icon_rating_00);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //mSimpleFacebook.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
            /*Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            String bitmap_path = MediaStore.Images.Media.insertImage(getContentResolver(),
                    bitmap, "Title", null);
            Uri contentUri = Uri.parse(bitmap_path);
            Cursor cursor = getContentResolver().query(contentUri, null,
                    null, null, null);
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

    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Gallery", "Cancel" };
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(VendorDetailsActivity.this);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    dataT.clear();
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File imageFile = _commonMethods.getOutputMediaFile();
                    captureImagePath = imageFile.getAbsolutePath();
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
                    Log.e("capturedImagePath in", ">>>" + captureImagePath);
                    ConstantClass.TAG_CAPTURED_IMAGE_PATH = captureImagePath;
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (items[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent(Action.ACTION_MULTIPLE_PICK);
                    intent.putExtra("routeFrom", "vendor_details");
                    intent.putExtra("vendorId", getIntent().getExtras().getInt("vendorId"));
                    startActivityForResult(intent, 100);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void postSelectedImage(){
        setUpProgressDialog();
        HashMap<String, PostObject> _postMap = new HashMap<String, PostObject>();

        _postMap.put("accessToken", _comFunc.getPostObject(
                String.valueOf(_pref.getSession(ConstantClass.ACCESS_TOKEN)), false));

        _postMap.put("vendorId", _comFunc.getPostObject(
                String.valueOf(getIntent().getExtras().getInt("vendorId")), false));

        _postMap.put("vendorCaption", _comFunc.getPostObject(String.valueOf(" "), false));

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

            _postMap.put("image_0" , _comFunc.getPostObject(ConstantClass.TAG_CAPTURED_IMAGE_PATH, true));
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
            Toast.makeText(VendorDetailsActivity.this, "Gallery image submitted successfully", Toast.LENGTH_LONG).show();
        }
    };

    /*private void initializeCheckinPopup(){
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View infoPopupLayout = layoutInflater.inflate(R.layout.checkid_popup,
                (ViewGroup) findViewById(R.id.llCheckInPopup));
        ImageView ivCrossCheckinPopup = (ImageView) infoPopupLayout.findViewById(R.id.ivCrossCheckinPopup);
        Button btnCheckInWithFb = (Button) infoPopupLayout.findViewById(R.id.btnCheckInWithFb);
        Button btnCheckInWithTwitter = (Button) infoPopupLayout.findViewById(R.id.btnCheckInWithTwitter);
        etShareFeeling = (EditText) infoPopupLayout.findViewById(R.id.etShareFeeling);
        infoPopup = new PopupWindow(infoPopupLayout, _commonMethods.getScreenWidth() - 40, 350, false);
        infoPopup.showAtLocation(infoPopupLayout, Gravity.CENTER, 0, 0);
        infoPopup.setFocusable(true);
        infoPopup.update();

        ivCrossCheckinPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("check in", "cross");
                infoPopup.dismiss();
            }
        });
        btnCheckInWithFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etShareFeeling.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(VendorDetailsActivity.this, "Please give a caption", Toast.LENGTH_LONG).show();
                } else {
                    flagCheckInComplete = 1;
                    checkInCaption = etShareFeeling.getText().toString();
                    Log.e("checkInCaption", checkInCaption);

                    feed = new Feed.Builder()
                            .setMessage(checkInCaption)
                            .setName(vendorName)
                            .setCaption(checkInCaption)
                            .setDescription("I am checking in " + vendorName + " | " + vendorAddress
                                    + " | " + etShareFeeling.getText().toString())
                            .setPicture(headerImageUrl)
                            .setLink("http://maps.google.com/maps?q=loc:" + vendorLatitude + "," + vendorLongitude)
                            .build();

                    mSimpleFacebook.publish(feed, true, onPublishListener);
                }
            }
        });

        btnCheckInWithTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etShareFeeling.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(VendorDetailsActivity.this, "Please give a caption", Toast.LENGTH_LONG).show();
                } else {
                    flagTwitterShare = 1;
                    flagCheckInComplete = 1;
                    *//*TweetComposer.Builder builder = new TweetComposer.Builder(VendorDetailsActivity.this)
                            .text("I am checking in " + vendorName + " | " + vendorAddress
                                    + " | " + etShareFeeling.getText().toString() + " | Google Location: " +
                                    headerImageUrl);*//*
                    TweetComposer.Builder builder = new TweetComposer.Builder(VendorDetailsActivity.this)
                            .text("I am checking in " + vendorName + " | " + vendorAddress
                                    + " | " + etShareFeeling.getText().toString() + " | Google Location: " +
                                    "http://maps.google.com/maps?q=loc:" + vendorLatitude + "," + vendorLongitude);
                    builder.show();
                }
            }
        });
    }*/

    private void initializeCheckinPopup(){
        final AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
        helpBuilder.setTitle("");
        layoutInflaterCheckIn = getLayoutInflater();
        View infoPopupLayout = layoutInflaterCheckIn.inflate(R.layout.checkid_popup,
                (ViewGroup) findViewById(R.id.llCheckInPopup));
        infoPopupLayout.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        helpBuilder.setView(infoPopupLayout);
        helpDialog = helpBuilder.create();
         helpDialog.show();

        ImageView ivCrossCheckinPopup = (ImageView) infoPopupLayout.findViewById(R.id.ivCrossCheckinPopup);
        Button btnCheckInWithFb = (Button) infoPopupLayout.findViewById(R.id.btnCheckInWithFb);
        Button btnCheckInWithTwitter = (Button) infoPopupLayout.findViewById(R.id.btnCheckInWithTwitter);
        etShareFeeling = (EditText) infoPopupLayout.findViewById(R.id.etShareFeeling);

        ivCrossCheckinPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("check in", "cross");
                helpDialog.dismiss();
            }
        });

        btnCheckInWithFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  if (etShareFeeling.getText().toString().equalsIgnoreCase("")) {
                   // Toast.makeText(VendorDetailsActivity.this, "Please give a caption", Toast.LENGTH_LONG).show();
                    String dec = "I am checking in " + vendorName +
                                        " | "+ vendorAddress;
                    publish(dec);
                } else {
                    String dec = " My Feelings:" +etShareFeeling.getText().toString()
                                        + " | "+ vendorAddress;
                   publish(dec);
            }*/
                String dec = " I am eating at " + vendorName +" found it on Ekplate app https://www.facebook.com/ekplate2014"+
                        " | "+ vendorAddress;
                publish(dec);
        }

            private void publish(String decription) {
                flagCheckInComplete = 1;
                //checkInCaption = etShareFeeling.getText().toString();
               // Log.e("checkInCaption", checkInCaption);

                feed = new Feed.Builder()
                        .setName(vendorName)
                        .setDescription(decription
                        )
                        .setPicture(headerImageUrl)
                        .setLink("http://maps.google.com/maps?q=loc:" + vendorLatitude + "," + vendorLongitude)
                        .build();
                mSimpleFacebook.publish(feed, true, onPublishListener);
            }
            }

            );

        btnCheckInWithTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if (etShareFeeling.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(VendorDetailsActivity.this, "Please give a caption", Toast.LENGTH_LONG).show();
                } else {*/
                    flagTwitterShare = 1;
                    flagCheckInComplete = 1;
                    /*TweetComposer.Builder builder = new TweetComposer.Builder(VendorDetailsActivity.this)
                            .text("I am checking in " + vendorName + " | " + vendorAddress
                                    + " | " + etShareFeeling.getText().toString() + " | Google Location: " +
                                    headerImageUrl);*/
                    /*TweetComposer.Builder builder = new TweetComposer.Builder(VendorDetailsActivity.this)
                            .text("I am checking in " + vendorName + " | " + vendorAddress
                                    + " | " + etShareFeeling.getText().toString() + " | Google Location: " +
                                    "http://maps.google.com/maps?q=loc:" + vendorLatitude + "," + vendorLongitude);*/
                TweetComposer.Builder builder = new TweetComposer.Builder(VendorDetailsActivity.this)
                        .text(" I am eating at " + vendorName +" found it on Ekplate app https://www.facebook.com/ekplate2014"+" | " + vendorAddress
                                + " | " + " | Google Location: " +
                                "http://maps.google.com/maps?q=loc:" + vendorLatitude + "," + vendorLongitude);
                    builder.show();
           //     }
            }
        });
    }

    private View.OnClickListener onClickListenerForWriteReview = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intentWriteReview = new Intent(VendorDetailsActivity.this, WriteReviewActivity.class);
            intentWriteReview.putExtra("vendorId", getIntent().getExtras().getInt("vendorId"));
            intentWriteReview.putExtra("vendorName", vendorName);
            intentWriteReview.putExtra("routeFrom", "vendor_details");
            startActivity(intentWriteReview);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    selectImage();
                } else {
                    // Permission Denied
                    Toast.makeText(this, "Can't Get Camera due to Permission issue! please Grand CAMERA Permission.", Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
