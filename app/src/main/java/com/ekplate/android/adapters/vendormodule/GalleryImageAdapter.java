package com.ekplate.android.adapters.vendormodule;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ekplate.android.R;
import com.ekplate.android.activities.vendormodule.GallerySingleImageActivity;
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

/**
 * Created by Rahul on 10/2/2015.
 */
public class GalleryImageAdapter extends BaseAdapter implements BackgroundActionInterface {

    private Context context;
    private ArrayList<GalleryItem> galleryItems;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private int vendorId, selectedImageId;
    private Pref _pref;
    private NetworkConnectionCheck _connection;
    private CallServiceAction _serviceAction;
    private ProgressDialog progressDialog;
    private ImageView ivSelectedLike;
    private String vendorName, vendorAddress;

    public GalleryImageAdapter(Context context, ArrayList<GalleryItem> galleryItems, int vendorId,
                               String vendorName, String vendorAddress) {
        this.context = context;
        this.galleryItems = galleryItems;
        this.imageLoader = ImageLoader.getInstance();
        this.vendorId = vendorId;
        this._pref = new Pref(context);
        this._connection = new NetworkConnectionCheck(context);
        this._serviceAction = new CallServiceAction(context);
        this._serviceAction.actionInterface = this;
        this.vendorName = vendorName;
        this.vendorAddress = vendorAddress;
        this.options = new DisplayImageOptions.Builder()
                            .cacheOnDisk(true)
                            .cacheInMemory(true)
                            .showImageOnLoading(R.drawable.default_image_vendor_inside)
                            .showImageOnFail(R.drawable.default_image_vendor_inside)
                            .showImageForEmptyUri(R.drawable.default_image_vendor_inside)
                            .build();
    }

    @Override
    public int getCount() {
        return galleryItems.size();
    }

    @Override
    public GalleryItem getItem(int position) {
        return galleryItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        final int positionFinal = position;
        if (rowView == null){
            rowView = LayoutInflater.from(context).inflate(R.layout.gallery_item_row_layout, parent, false);
            GalleryItemHolder holder = new GalleryItemHolder();
            holder.ivGalleryListImage = (ImageView) rowView.findViewById(R.id.ivGalleryListImage);
            holder.ivFoodLikeGalleryList = (ImageView) rowView.findViewById(R.id.ivFoodLikeGalleryList);
            holder.tvFoodNameGalleryList = (TextView) rowView.findViewById(R.id.tvFoodNameGalleryList);
            holder.tvCaptionGalleryList = (TextView) rowView.findViewById(R.id.tvCaptionGalleryList);
            holder.rlGalleryImageItem = (RelativeLayout) rowView.findViewById(R.id.rlGalleryImageItem);
            rowView.setTag(holder);
        }

        final GalleryItemHolder newHolder = (GalleryItemHolder) rowView.getTag();
        imageLoader.displayImage(galleryItems.get(position).getImageUrl(), newHolder.ivGalleryListImage,
                options, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String s, View view) {
                        Animation anim = AnimationUtils.loadAnimation(context,
                                android.R.anim.fade_in);
                        newHolder.ivGalleryListImage.setAnimation(anim);
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
        newHolder.tvCaptionGalleryList.setText(getItem(position).getVendorCaption());
        if (_pref.getBooleanSession(ConstantClass.TAG_GALLERY_IMAGE_LIKE_STATUS +
                String.valueOf(getItem(position).getId()))) {
            newHolder.ivFoodLikeGalleryList.setImageResource(R.drawable.icon_single_image_like_selected);
        } else {
            newHolder.ivFoodLikeGalleryList.setImageResource(R.drawable.icon_like_gallery_image);
        }
        newHolder.rlGalleryImageItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, GallerySingleImageActivity.class);
                intent.putExtra("selected_item_position", positionFinal);
                intent.putExtra("gallery_image_list", galleryItems);
                intent.putExtra("vendor_name", vendorName);
                intent.putExtra("vendor_address", vendorAddress);
                intent.putExtra("vendorId", vendorId);
                context.startActivity(intent);
                ((Activity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        newHolder.rlGalleryImageItem.setTag(newHolder);
        newHolder.ivFoodLikeGalleryList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(_connection.isNetworkAvailable()){
                    selectedImageId = getItem(position).getId();
                    ivSelectedLike = newHolder.ivFoodLikeGalleryList;
                    setUpProgressDialog();
                    setImageLikeParam(getItem(position).getId(), getItem(position).getImageType(),
                            _pref.getBooleanSession(ConstantClass.TAG_GALLERY_IMAGE_LIKE_STATUS +
                                    String.valueOf(getItem(position).getId())));
                } else {
                    _connection.getNetworkActiveAlert().show();
                }
            }
        });
        return rowView;
    }

    private void setUpProgressDialog(){
        progressDialog = new ProgressDialog(context);
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
                    Toast.makeText(context, jsonObjData.getString("msg"), Toast.LENGTH_LONG).show();
                    _pref.setSession(ConstantClass.TAG_GALLERY_IMAGE_LIKE_COUNT +
                                    String.valueOf(selectedImageId), jsonObjData.getString("noofcount"));
                    if (_pref.getBooleanSession(ConstantClass.TAG_GALLERY_IMAGE_LIKE_STATUS +
                            String.valueOf(selectedImageId))) {
                        _pref.setSession(ConstantClass.TAG_GALLERY_IMAGE_LIKE_STATUS +
                                String.valueOf(selectedImageId), false);
                        ivSelectedLike.setImageResource(R.drawable.icon_like_gallery_image);
                    } else {
                        _pref.setSession(ConstantClass.TAG_GALLERY_IMAGE_LIKE_STATUS +
                                String.valueOf(selectedImageId), true);
                        ivSelectedLike.setImageResource(R.drawable.icon_single_image_like_selected);
                    }
                } else {
                    Toast.makeText(context, jsonObjData.getString("msg"), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(context, jsonObjError.getString("errMsg"), Toast.LENGTH_LONG).show();
            }
            progressDialog.dismiss();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public class GalleryItemHolder {
        private ImageView ivGalleryListImage, ivFoodLikeGalleryList;
        private TextView tvFoodNameGalleryList, tvCaptionGalleryList;
        private RelativeLayout rlGalleryImageItem;
    }
 }
