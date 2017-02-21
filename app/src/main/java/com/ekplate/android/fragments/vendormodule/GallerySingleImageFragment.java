package com.ekplate.android.fragments.vendormodule;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ekplate.android.R;
import com.ekplate.android.activities.socialsharemodule.SocialShareActivity;
import com.ekplate.android.activities.vendormodule.ReportProblemActivity;
import com.ekplate.android.appinterfaces.BackgroundActionInterface;
import com.ekplate.android.models.vendormodule.GalleryItem;
import com.ekplate.android.utils.CallServiceAction;
import com.ekplate.android.utils.ConstantClass;
import com.ekplate.android.utils.NetworkConnectionCheck;
import com.ekplate.android.utils.Pref;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.json.JSONObject;

import java.util.ArrayList;

public class GallerySingleImageFragment extends Fragment implements BackgroundActionInterface {

    private ArrayList<GalleryItem> singleImageItemArrayList;
    private GalleryItem singleImageItem;
    private int position;
    private ImageView ivMainSingleImage, ivSingleImageShare, ivSingleImageLike;
    private TextView tvSingleImageLikeNumber, tvSingleImageFoodTitle;
    private LinearLayout llReportProblem;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private int vendorId;
    private Pref _pref;
    private NetworkConnectionCheck _connection;
    private CallServiceAction _serviceAction;
    private ProgressDialog progressDialog;
    private String vendorName, vendorAddress;

    public static GallerySingleImageFragment newInstance
            (int position, ArrayList<GalleryItem> singleImageItemArrayList, int vendorId,
             String vendorName, String vendorAddress) {
        GallerySingleImageFragment fragment = new GallerySingleImageFragment();
        Bundle args = new Bundle();
        args.putSerializable("valueObject", singleImageItemArrayList);
        args.putInt("position", position);
        args.putInt("vendorId", vendorId);
        args.putString("vendorName", vendorName);
        args.putString("vendorAddress", vendorAddress);
        Log.e("frag vendorId", String.valueOf(vendorId));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt("position");
        singleImageItemArrayList = (ArrayList<GalleryItem>)
                getArguments().getSerializable("valueObject");
        vendorId = getArguments().getInt("vendorId");
        vendorName = getArguments().getString("vendorName");
        vendorAddress = getArguments().getString("vendorAddress");
        singleImageItem = singleImageItemArrayList.get(position);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_gallery_single_image, container, false);
        initialize(rootView);
        setUpItemData();
        onClick();
        return rootView;
    }

    private void initialize(View rootView){
        ivMainSingleImage = (ImageView) rootView.findViewById(R.id.ivMainSingleImage);
        ivSingleImageShare = (ImageView) rootView.findViewById(R.id.ivSingleImageShare);
        ivSingleImageLike = (ImageView) rootView.findViewById(R.id.ivSingleImageLike);
        tvSingleImageLikeNumber = (TextView) rootView.findViewById(R.id.tvSingleImageLikeNumber);
        tvSingleImageFoodTitle = (TextView) rootView.findViewById(R.id.tvSingleImageFoodTitle);
        llReportProblem = (LinearLayout) rootView.findViewById(R.id.llReportProblem);
        _pref = new Pref(getActivity());
        _connection = new NetworkConnectionCheck(getActivity());
        _serviceAction = new CallServiceAction(getActivity());
        _serviceAction.actionInterface = this;
        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                        .cacheInMemory(true)
                        .cacheOnDisk(true)
                        .showImageOnFail(R.drawable.default_image_vendor_inside)
                        .showImageOnLoading(R.drawable.default_image_vendor_inside)
                        .showImageForEmptyUri(R.drawable.default_image_vendor_inside)
                        .delayBeforeLoading(200)
                        .build();
    }

    private void  setUpItemData(){
        imageLoader.displayImage(singleImageItemArrayList.get(position).getImageUrl(),
                ivMainSingleImage, options, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String s, View view) {
                        Animation anim = AnimationUtils.loadAnimation(getActivity(),
                                android.R.anim.fade_in);
                        ivMainSingleImage.setAnimation(anim);
                        anim.start();
                    }

                    @Override
                    public void onLoadingFailed(String s, View view, FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete(String s, View view, Bitmap bitmap) {

                    }

                    @Override
                    public void onLoadingCancelled(String s, View view) {

                    }
                });
        if (_pref.getBooleanSession(ConstantClass.TAG_GALLERY_IMAGE_LIKE_STATUS +
                String.valueOf(singleImageItemArrayList.get(position).getId()))){
            ivSingleImageLike.setImageResource(R.drawable.icon_single_image_like_selected);
        } else {
            ivSingleImageLike.setImageResource(R.drawable.icon_like_gallery_image);
        }
        tvSingleImageFoodTitle.setText(singleImageItemArrayList.get(position).getVendorCaption());
        tvSingleImageLikeNumber.setText(_pref.getSession(ConstantClass.TAG_GALLERY_IMAGE_LIKE_COUNT +
                        String.valueOf(singleImageItemArrayList.get(position).getId())) + " Likes");
    }

    private void onClick(){
        llReportProblem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ReportProblemActivity.class);
                intent.putExtra("vendorId", vendorId);
                intent.putExtra("routeFrom", "gallerySingleImage");
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        ivSingleImageShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SocialShareActivity.class);
                intent.putExtra("vendor_name", vendorName);
                intent.putExtra("vendor_address", vendorAddress);
                intent.putExtra("url", singleImageItemArrayList.get(position).getImageUrl());
                intent.putExtra("route_from", "gallery_single_image");
                startActivity(intent);
            }
        });

        ivSingleImageLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (_connection.isNetworkAvailable()){
                    setUpProgressDialog();
                    setImageLikeParam(singleImageItemArrayList.get(position).getId(),
                            singleImageItemArrayList.get(position).getImageType(),
                            _pref.getBooleanSession(ConstantClass.TAG_GALLERY_IMAGE_LIKE_STATUS +
                                    String.valueOf(singleImageItemArrayList.get(position).getId())));
                } else {
                    _connection.getNetworkActiveAlert().show();
                }
            }
        });
    }

    private void setUpProgressDialog(){
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait..");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void setImageLikeParam(int imageId, int imageType, boolean status){
        try {
            JSONObject parentJsonObj = new JSONObject();
            JSONObject childJsonObj = new JSONObject();
            childJsonObj.put("accessToken", _pref.getSession(ConstantClass.ACCESS_TOKEN));
            childJsonObj.put("vendorId", vendorId);
            childJsonObj.put("imageId", imageId);
            childJsonObj.put("imageType", imageType);
            if (status) {
                childJsonObj.put("status", String.valueOf(false));
            } else {
                childJsonObj.put("status", String.valueOf(true));
            }
            parentJsonObj.put("data", childJsonObj);
            Log.e("input data", parentJsonObj.toString());
            _serviceAction.requestVersionApi(parentJsonObj, "add-like-image");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onStarted() {

    }

    @Override
    public void onCompleted(JSONObject response) {
        Log.e("response", response.toString());
        try {
            JSONObject jsonObjError = response.getJSONObject("errNode");
            if (jsonObjError.getInt("errCode") == 0) {
                JSONObject jsonObjData = response.getJSONObject("data");
                if (jsonObjData.getBoolean("success")) {
                    Toast.makeText(getActivity(), jsonObjData.getString("msg"), Toast.LENGTH_LONG).show();
                    tvSingleImageLikeNumber.setText(jsonObjData.getString("noofcount") + " Likes");
                    _pref.setSession(ConstantClass.TAG_GALLERY_IMAGE_LIKE_COUNT +
                                    String.valueOf(singleImageItemArrayList.get(position).getId()),
                            jsonObjData.getString("noofcount"));
                    if (_pref.getBooleanSession(ConstantClass.TAG_GALLERY_IMAGE_LIKE_STATUS +
                            String.valueOf(singleImageItemArrayList.get(position).getId()))) {
                        _pref.setSession(ConstantClass.TAG_GALLERY_IMAGE_LIKE_STATUS +
                                String.valueOf(singleImageItemArrayList.get(position).getId()), false);
                        ivSingleImageLike.setImageResource(R.drawable.icon_like_gallery_image);
                    } else {
                        _pref.setSession(ConstantClass.TAG_GALLERY_IMAGE_LIKE_STATUS +
                                String.valueOf(singleImageItemArrayList.get(position).getId()), true);
                        ivSingleImageLike.setImageResource(R.drawable.icon_single_image_like_selected);
                    }
                } else {
                    Toast.makeText(getActivity(), jsonObjData.getString("msg"), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getActivity(), jsonObjError.getString("errMsg"), Toast.LENGTH_LONG).show();
            }
            progressDialog.dismiss();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
