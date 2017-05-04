package com.ekplate.android.fragments.homemodule;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ekplate.android.R;
import com.ekplate.android.activities.addvendormodule.AddVendorImagesActivity;
import com.ekplate.android.activities.addvendormodule.AddVendorInformationActivity;
import com.ekplate.android.activities.addvendormodule.CustomGallery;
import com.ekplate.android.activities.homemodule.HomeResideActivity;
import com.ekplate.android.activities.vendormodule.VendorsActivity;
import com.ekplate.android.adapters.addvendormodule.DbAdapter;
import com.ekplate.android.adapters.homemodule.HomePagerAdapter;
import com.ekplate.android.config.EkplateApplication;
import com.ekplate.android.services.LocationTrackingService;
import com.ekplate.android.utils.ChangeLocationPopup;
import com.ekplate.android.utils.CommonFunction;
import com.ekplate.android.utils.CommonMethods;
import com.ekplate.android.utils.ConstantClass;
import com.ekplate.android.utils.Pref;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private static final int REQUEST_CODE_ASK_PERMISSIONS = 1;
    private static final int REQUEST_CODE_ASK_PERMISSIONS_1 = 2;
    private ViewPager vpHomeSlidingItem;
    private CirclePageIndicator cvpIndicator;
    private HomePagerAdapter homePagerAdapter;
    private int showIndicatorFlag = 0, firstPageFlag = 0;
    private RelativeLayout.LayoutParams llIndicatorContainerParams;
    private RelativeLayout rlTransparentBg;
    private LinearLayout llUnderContainer;
    private Animation MenuFabZoomOut, MenuFabZoomIn, MenuIconRotation;
    private FloatingActionButton fabMenu;
    private LinearLayout rootLayout;
    private boolean expanded = false;
    private View llOrderOnline;
    private View llAddReviews;
    private View llAddPicture;
    private View llCheckIn;
    private View llAddVendor;
    public View llInstruction;

    private float offset1;
    private float offset2;
    private float offset3;
    private float offset4;
    private float offset5;
    public static String optionId, keyValue, captureImagePath;
    public static ArrayList<CustomGallery> dataT = new ArrayList<CustomGallery>();
    private CommonFunction _comFunc;
    private CommonMethods _CommonMethods;
    RelativeLayout mMainLayout;
    public static int flagImageSelected = 0;
    private TextView tvAddVendorTextHome, tvCheckinTextHome, tvAddPictureTextHome,
            tvAddReviewTextHome, tvOrderOnlineTextHome;
    private FloatingActionButton fabAddVendor, fabCheckIn, fabAddPicture, fabAddReview,
            fabOrderOnline;
    private Pref _pref;
    public String migratedFrom="";
    private ChangeLocationPopup changeLocationPopup;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        initialize(rootView);
        setUpViewPager();
        onClick();

        final ViewGroup fabContainer = (ViewGroup) rootView.findViewById(R.id.fab_container);
        setFabMenuTextInvisible();
        fabMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llInstruction.setVisibility(View.GONE);
                expanded = !expanded;
                if (expanded) {
                    expandFab();
                    ((HomeResideActivity)getActivity()).tbHomeReside.setVisibility(View.VISIBLE);
                   /* HomeResideActivity activity = (HomeResideActivity) getActivity();
                    activity.closeMenu();*/
                    rlTransparentBg.setVisibility(View.VISIBLE);
                    llUnderContainer.setVisibility(View.VISIBLE);
                    setFabMenuTextVisible();
                } else {
                    collapseFab();
                    ((HomeResideActivity)getActivity()).tbHomeReside.setVisibility(View.VISIBLE);
                    rlTransparentBg.setVisibility(View.GONE);
                    llUnderContainer.setVisibility(View.GONE);
                    setFabMenuTextInvisible();
                }
            }
        });

        fabContainer.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                fabContainer.getViewTreeObserver().removeOnPreDrawListener(this);
                offset1 = fabMenu.getY() - llOrderOnline.getY();
                llOrderOnline.setTranslationY(offset1);
                offset2 = fabMenu.getY() - llAddReviews.getY();
                llAddReviews.setTranslationY(offset2);
                offset3 = fabMenu.getY() - llAddPicture.getY();
                llAddPicture.setTranslationY(offset3);
                offset4 = fabMenu.getY() - llCheckIn.getY();
                llCheckIn.setTranslationY(offset4);
                offset5 = fabMenu.getY() - llAddVendor.getY();
                llAddVendor.setTranslationY(offset5);
                return true;
            }
        });
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onResume() {


        if(_pref.getSession(ConstantClass.FIRST_USER).equals("true")){
            llInstruction.setVisibility(View.VISIBLE);
  //    ((HomeResideActivity)getActivity()).tbHomeReside.setVisibility(View.VISIBLE);
            _pref.setSession(ConstantClass.FIRST_USER,"false");
        }

        if(!getActivity().isFinishing())
        {
            //show dialog
            changeLocationPopup.callServiceForCityList();
        }
        super.onResume();
           // changeLocationPopup.callServiceForCityList();
    }

    private void initialize(View rootView){
        changeLocationPopup=new ChangeLocationPopup(getActivity());
        vpHomeSlidingItem = (ViewPager) rootView.findViewById(R.id.vpHomeSlidingItem);
        mMainLayout=(RelativeLayout)rootView.findViewById(R.id.main_layout);
        cvpIndicator = (CirclePageIndicator) rootView.findViewById(R.id.cvpIndicator);
        llIndicatorContainerParams = (RelativeLayout.LayoutParams)
                cvpIndicator.getLayoutParams();
        rlTransparentBg = (RelativeLayout) rootView.findViewById(R.id.rlTransparentBg);
        //fabBtnAddVendor = (FloatingActionButton) rootView.findViewById(R.id.fabBtnAddVendor);
        llUnderContainer = (LinearLayout) rootView.findViewById(R.id.llUnderContainer);
        rootLayout = (LinearLayout) rootView.findViewById(R.id.llInstruction);

        rootLayout.setEnabled(true);
        _pref = new Pref(getActivity());
        if(_pref.getBooleanSession(ConstantClass.HINT_STATUS))
        {
            ((HomeResideActivity)getActivity()).tbHomeReside.setVisibility(View.GONE);
            rootLayout.setVisibility(View.VISIBLE);
            _pref.setSession(ConstantClass.HINT_STATUS,false);
        }
        else
        {
          //  rlTransparentBg.setVisibility(View.GONE);
            ((HomeResideActivity)getActivity()).tbHomeReside.setVisibility(View.VISIBLE);
            rootLayout.setVisibility(View.GONE);
        }
        tvAddVendorTextHome = (TextView) rootView.findViewById(R.id.tvAddVendorTextHome);
        tvCheckinTextHome = (TextView) rootView.findViewById(R.id.tvCheckinTextHome);
        tvAddPictureTextHome = (TextView) rootView.findViewById(R.id.tvAddPictureTextHome);
        tvAddReviewTextHome = (TextView) rootView.findViewById(R.id.tvAddReviewTextHome);
        tvOrderOnlineTextHome = (TextView) rootView.findViewById(R.id.tvOrderOnlineTextHome);
        fabAddVendor = (FloatingActionButton) rootView.findViewById(R.id.fabAddVendor);
        fabCheckIn = (FloatingActionButton) rootView.findViewById(R.id.fabCheckIn);
        fabAddPicture = (FloatingActionButton) rootView.findViewById(R.id.fabAddPicture);
        fabAddReview = (FloatingActionButton) rootView.findViewById(R.id.fabAddReview);
        fabOrderOnline = (FloatingActionButton) rootView.findViewById(R.id.fabOrderOnline);
        fabMenu = (FloatingActionButton) rootView.findViewById(R.id.fab);
        llOrderOnline = rootView.findViewById(R.id.llOrderOnline);
        llAddReviews = rootView.findViewById(R.id.llAddReviews);
        llAddPicture = rootView.findViewById(R.id.llAddPicture);
        llCheckIn = rootView.findViewById(R.id.llCheckIn);
        llAddVendor = rootView.findViewById(R.id.llAddVendor);
        llInstruction = rootView.findViewById(R.id.llInstruction);
        MenuFabZoomOut = AnimationUtils.loadAnimation(getActivity(), R.anim.menu_fab_zoom_out);
        MenuFabZoomIn = AnimationUtils.loadAnimation(getActivity(), R.anim.menu_fab_zoom_in);
        MenuFabZoomIn.setAnimationListener(fabMenuZoomInListener);
        MenuIconRotation = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_fab_menu_icon);
        _comFunc = new CommonFunction(getActivity());
        _CommonMethods = new CommonMethods(getActivity());
        mMainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rootLayout.setVisibility(View.GONE);
                ((HomeResideActivity)getActivity()).tbHomeReside.setVisibility(View.VISIBLE);
            }
        });


    }

    private void onClick(){
        llAddVendor.setOnClickListener(addVendorListener);
        fabAddVendor.setOnClickListener(addVendorListener);
        llCheckIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), VendorsActivity.class);
                intent.putExtra("optionId", optionId);
                intent.putExtra("keyValue", keyValue);
                intent.putExtra("routeFrom", "home-checkin");
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        fabCheckIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), VendorsActivity.class);
                intent.putExtra("optionId", optionId);
                intent.putExtra("keyValue", keyValue);
                intent.putExtra("routeFrom", "home-checkin");
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        llAddPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), VendorsActivity.class);
                intent.putExtra("routeFrom", "menu_picture");
                intent.putExtra("optionId", optionId);
                intent.putExtra("keyValue", keyValue);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        fabAddPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    int hasWriteAccessPermission = getActivity().checkSelfPermission(Manifest.permission.CAMERA);
                    if (hasWriteAccessPermission != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[] {Manifest.permission.CAMERA},
                                REQUEST_CODE_ASK_PERMISSIONS);
                        return;
                    }else{
                        //----------------Check permission in android M-------------------------//
                        Intent intent = new Intent(getActivity(), VendorsActivity.class);
                        intent.putExtra("routeFrom", "menu_picture");
                        intent.putExtra("optionId", optionId);
                        intent.putExtra("keyValue", keyValue);
                        getActivity().startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        //--------------------------------------------------------------------//
                    }




                }else{
                    //----------------Check permission in android M-------------------------//
                    Intent intent = new Intent(getActivity(), VendorsActivity.class);
                    intent.putExtra("routeFrom", "menu_picture");
                    intent.putExtra("optionId", optionId);
                    intent.putExtra("keyValue", keyValue);
                    getActivity().startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    //--------------------------------------------------------------------//
                }

            }
        });

        llAddReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), VendorsActivity.class);
                intent.putExtra("optionId", optionId);
                intent.putExtra("keyValue", keyValue);
                intent.putExtra("routeFrom", "menu_review");
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        fabAddReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), VendorsActivity.class);
                intent.putExtra("optionId", optionId);
                intent.putExtra("keyValue", keyValue);
                intent.putExtra("routeFrom", "menu_review");
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        llUnderContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("test", "test click");
            }
        });

        llInstruction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llInstruction.setVisibility(View.GONE);
                ((HomeResideActivity)getActivity()).tbHomeReside.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setUpViewPager(){
        homePagerAdapter = new HomePagerAdapter(getActivity().getSupportFragmentManager(),
                getActivity(), 3);
        vpHomeSlidingItem.setAdapter(homePagerAdapter);
        vpHomeSlidingItem.setCurrentItem(1);
        vpHomeSlidingItem.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position==0) {
                    cvpIndicator.setVisibility(View.VISIBLE);
                    fabAddVendor.setVisibility(View.GONE);
                    fabAddVendor.setClickable(false);
                    fabCheckIn.setVisibility(View.GONE);
                    fabCheckIn.setClickable(false);
                    fabAddPicture.setVisibility(View.GONE);
                    fabAddPicture.setClickable(false);
                    fabAddReview.setVisibility(View.GONE);
                    fabAddReview.setClickable(false);
                    fabOrderOnline.setVisibility(View.GONE);
                    fabOrderOnline.setClickable(false);
                    rlTransparentBg.setVisibility(View.GONE);
                    setFabMenuTextInvisible();
                    collapseFab();
                    fabMenu.startAnimation(MenuFabZoomOut);
                    fabMenu.setVisibility(View.INVISIBLE);
                    expanded = false;
                    firstPageFlag = 1;
                } else {
                    if(firstPageFlag == 1) {
                        cvpIndicator.setVisibility(View.VISIBLE);
                        fabMenu.startAnimation(MenuFabZoomIn);
                        fabMenu.setVisibility(View.VISIBLE);
                        firstPageFlag = 0;
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        cvpIndicator.setViewPager(vpHomeSlidingItem);
    }


    private void collapseFab() {
        //fabMenu.setImageResource(R.drawable.icon_plus);
        fabMenu.animate().rotation(0);
        fabMenu.clearAnimation();
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(createCollapseAnimator(llOrderOnline, offset1),
                createCollapseAnimator(llAddReviews, offset2),
                createCollapseAnimator(llAddPicture, offset3),
                createCollapseAnimator(llCheckIn, offset4),
                createCollapseAnimator(llAddVendor, offset5));
        animatorSet.start();
        animateFab();
    }

    private void expandFab() {
        fabMenu.animate().rotation(135);
        //fabMenu.setImageResource(R.drawable.icon_close);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(createExpandAnimator(llOrderOnline, offset1),
                createExpandAnimator(llAddReviews, offset2),
                createExpandAnimator(llAddPicture, offset3),
                createExpandAnimator(llCheckIn, offset4),
                createExpandAnimator(llAddVendor, offset5));
        animatorSet.start();
        animateFab();
    }

    private static final String TRANSLATION_Y = "translationY";

    private Animator createCollapseAnimator(View view, float offset) {
        return ObjectAnimator.ofFloat(view, TRANSLATION_Y, 0, offset)
                .setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
    }

    private Animator createExpandAnimator(View view, float offset) {
        return ObjectAnimator.ofFloat(view, TRANSLATION_Y, offset, 0)
                .setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
    }

    private void animateFab() {
        Drawable drawable = fabMenu.getDrawable();
        if (drawable instanceof Animatable) {
            ((Animatable) drawable).start();
        }
    }

    private void setFabMenuTextInvisible(){
        tvAddVendorTextHome.setVisibility(View.GONE);
        tvCheckinTextHome.setVisibility(View.GONE);
        tvAddPictureTextHome.setVisibility(View.GONE);
        tvAddReviewTextHome.setVisibility(View.GONE);
        tvOrderOnlineTextHome.setVisibility(View.GONE);
    }

    private void setFabMenuTextVisible(){
        tvAddVendorTextHome.setVisibility(View.VISIBLE);
        tvCheckinTextHome.setVisibility(View.VISIBLE);
        tvAddPictureTextHome.setVisibility(View.VISIBLE);
        tvAddReviewTextHome.setVisibility(View.VISIBLE);
        tvOrderOnlineTextHome.setVisibility(View.VISIBLE);
    }

    View.OnClickListener addVendorListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                int hasWriteAccessPermission = getActivity().checkSelfPermission(Manifest.permission.CAMERA);
                if (hasWriteAccessPermission != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[] {Manifest.permission.CAMERA},
                            REQUEST_CODE_ASK_PERMISSIONS_1);
                    return;
                }else{
                    //---------------------Set Permission for Android M-----------------------------------//
                    _pref.setSession(ConstantClass.TAG_ADD_VENDOR_BASIC_INFO_FLAG, 0);
                    _pref.setSession(ConstantClass.TAG_ADD_VENDOR_RATING_FLAG, 0);
                    _pref.setSession(ConstantClass.TAG_ADD_VENDOR_LOCATION_FLAG, 0);
                    _pref.setSession(ConstantClass.TAG_ADD_VENDOR_IMAGE_VIDEO_FLAG, 0);
                    _pref.setSession(ConstantClass.TAG_ADD_VENDOR_MENU_FLAG, 0);
                    startActivity(new Intent(getActivity(), AddVendorInformationActivity.class));
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                    //-------------------------------------------------------------------------------------------//
                }




            }else{
                //---------------------Set Permission for Android M-----------------------------------//
                _pref.setSession(ConstantClass.TAG_ADD_VENDOR_BASIC_INFO_FLAG, 0);
                _pref.setSession(ConstantClass.TAG_ADD_VENDOR_RATING_FLAG, 0);
                _pref.setSession(ConstantClass.TAG_ADD_VENDOR_LOCATION_FLAG, 0);
                _pref.setSession(ConstantClass.TAG_ADD_VENDOR_IMAGE_VIDEO_FLAG, 0);
                _pref.setSession(ConstantClass.TAG_ADD_VENDOR_MENU_FLAG, 0);

                Intent intent = new Intent(getActivity(), AddVendorImagesActivity.class);
                intent.putExtra("isNewVendor",true);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                //-------------------------------------------------------------------------------------------//
            }



        }
    };

    Animation.AnimationListener fabMenuZoomInListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            fabAddVendor.setVisibility(View.VISIBLE);
            fabAddVendor.setClickable(true);
            fabCheckIn.setVisibility(View.VISIBLE);
            fabCheckIn.setClickable(true);
            fabAddPicture.setVisibility(View.VISIBLE);
            fabAddPicture.setClickable(true);
            fabAddReview.setVisibility(View.VISIBLE);
            fabAddReview.setClickable(true);
            fabOrderOnline.setVisibility(View.VISIBLE);
            fabOrderOnline.setClickable(true);
            fabMenu.setVisibility(View.VISIBLE);
            fabMenu.setClickable(true);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    //----------------Check permission in android M-------------------------//
                    Intent intent = new Intent(getActivity(), VendorsActivity.class);
                    intent.putExtra("routeFrom", "menu_picture");
                    intent.putExtra("optionId", optionId);
                    intent.putExtra("keyValue", keyValue);
                    getActivity().startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    //--------------------------------------------------------------------//
                } else {
                    // Permission Denied
                    Toast.makeText(getActivity(), "Can't Get Camera due to Permission issue! please Grand CAMERA Permission.", Toast.LENGTH_SHORT).show();
                }
                break;

            case REQUEST_CODE_ASK_PERMISSIONS_1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    //---------------------Set Permission for Android M-----------------------------------//
                    _pref.setSession(ConstantClass.TAG_ADD_VENDOR_BASIC_INFO_FLAG, 0);
                    _pref.setSession(ConstantClass.TAG_ADD_VENDOR_RATING_FLAG, 0);
                    _pref.setSession(ConstantClass.TAG_ADD_VENDOR_LOCATION_FLAG, 0);
                    _pref.setSession(ConstantClass.TAG_ADD_VENDOR_IMAGE_VIDEO_FLAG, 0);
                    _pref.setSession(ConstantClass.TAG_ADD_VENDOR_MENU_FLAG, 0);
                    startActivity(new Intent(getActivity(), AddVendorInformationActivity.class));
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                    //-------------------------------------------------------------------------------------------//
                } else {
                    // Permission Denied
                    Toast.makeText(getActivity(), "Can't Get Camera due to Permission issue! please Grand CAMERA Permission.", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
