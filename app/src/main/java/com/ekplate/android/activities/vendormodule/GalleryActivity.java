package com.ekplate.android.activities.vendormodule;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.ekplate.android.R;
import com.ekplate.android.adapters.vendormodule.GalleryImageAdapter;
import com.ekplate.android.config.BaseActivity;
import com.ekplate.android.models.vendormodule.GalleryItem;
import com.ekplate.android.utils.ConstantClass;
import com.ekplate.android.utils.Pref;
import com.nirhart.parallaxscroll.views.ParallaxListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class GalleryActivity extends BaseActivity {

    private Toolbar tbImageGallery;
    private TextView toolbarHeaderText;
    private ParallaxListView plvGalleryImage;
    private ArrayList<GalleryItem> galleryItems;
    private GalleryImageAdapter imageAdapter;
    private Pref _pref;
    //private String galleryImageJsonArrayStr = "[{\"imageId\":\"1\", \"imageUrl\":\"http://uat.ekplate.com/uploads/vendor_images/108_117_food_list1.png\", \"imageTitle\": \"Chinese Food Salad\", \"imageShortDesc\": \"Chinese Fast Food Center\", \"imageLikeStatus\": \"true\", \"noOfLikes\":\"10\"},{\"imageId\":\"2\", \"imageUrl\":\"http://uat.ekplate.com/uploads/vendor_images/108_117_food_list1.png\", \"imageTitle\": \"Chinese Food Salad\", \"imageShortDesc\": \"Chinese Fast Food Center\", \"imageLikeStatus\": \"false\", \"noOfLikes\":\"10\"},{\"imageId\":\"3\", \"imageUrl\":\"http://uat.ekplate.com/uploads/vendor_images/108_117_food_list1.png\", \"imageTitle\": \"Chinese Food Salad\", \"imageShortDesc\": \"Chinese Fast Food Center\", \"imageLikeStatus\": \"true\", \"noOfLikes\":\"10\"},{\"imageId\":\"3\", \"imageUrl\":\"http://uat.ekplate.com/uploads/vendor_images/108_117_food_list1.png\", \"imageTitle\": \"Chinese Food Salad\", \"imageShortDesc\": \"Chinese Fast Food Center\", \"imageLikeStatus\": \"true\", \"noOfLikes\":\"10\"},{\"imageId\":\"3\", \"imageUrl\":\"http://uat.ekplate.com/uploads/vendor_images/108_117_food_list1.png\", \"imageTitle\": \"Chinese Food Salad\", \"imageShortDesc\": \"Chinese Fast Food Center\", \"imageLikeStatus\": \"true\", \"noOfLikes\":\"10\"}]";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        getSupportActionBar().hide();
        initialize();
        setUpToolBar();
       // Log.e("galleryImageJsonArrayStr", getIntent().getExtras().getString("galleryImageJsonArrayStr"));
    }

    private void initialize(){
        plvGalleryImage = (ParallaxListView) findViewById(R.id.plvGalleryImage);
        galleryItems = new ArrayList<GalleryItem>();
        imageAdapter = new GalleryImageAdapter(this, galleryItems, getIntent().getExtras().getInt("vendorId"),
                getIntent().getExtras().getString("vendorName"), getIntent().getExtras().getString("vendorAddress"));
        _pref = new Pref(this);
    }

    private void setUpToolBar(){
        tbImageGallery = (Toolbar) findViewById(R.id.tbImageGallery);
        toolbarHeaderText = (TextView) findViewById(R.id.toolbarHeaderText);
        toolbarHeaderText.setText("GALLERY");
        tbImageGallery.setNavigationIcon(R.drawable.ic_action_back);
        tbImageGallery.setBackgroundColor(Color.parseColor("#66000000"));
        tbImageGallery.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void setUpGalleryList(){
        plvGalleryImage.setAdapter(imageAdapter);
        loadItems();
    }

    private void loadItems(){
        try {
            galleryItems.clear();
            JSONArray galleryImageJsonArray = new JSONArray(getIntent().getExtras().getString("galleryImageJsonArrayStr"));
            for (int i = 0; i < galleryImageJsonArray.length(); i++) {
                GalleryItem _item = new GalleryItem();
                JSONObject imageItemJsonObj = galleryImageJsonArray.getJSONObject(i);
                _item.setId(imageItemJsonObj.getInt("imageId"));
                _item.setImageUrl(imageItemJsonObj.getString("imageUrl"));
                if(imageItemJsonObj.has("vendorCaption"))
                {
                    _item.setVendorCaption(imageItemJsonObj.getString("vendorCaption"));
                }
                _item.setImageLikeStatus(imageItemJsonObj.getBoolean("imageLikeStatus"));
                try {
                    if (_pref.getBooleanSession(ConstantClass.TAG_GALLERY_IMAGE_LIKE_STATUS +
                            String.valueOf(imageItemJsonObj.getInt("imageId")))) {
                        Log.e("Exception", "Exception");
                    } else {
                        _pref.setSession(ConstantClass.TAG_GALLERY_IMAGE_LIKE_COUNT +
                                        String.valueOf(imageItemJsonObj.getInt("imageId")),
                                String.valueOf(imageItemJsonObj.getInt("imageTotalLike")));
                    }
                } catch (Exception e) {
                    _pref.setSession(ConstantClass.TAG_GALLERY_IMAGE_LIKE_STATUS +
                                    String.valueOf(imageItemJsonObj.getInt("imageId")),
                            imageItemJsonObj.getBoolean("imageLikeStatus"));
                }
                _item.setImageTotalLike(imageItemJsonObj.getString("imageTotalLike"));
                _item.setImageType(imageItemJsonObj.getInt("imageType"));
                galleryItems.add(_item);
            }
            imageAdapter.notifyDataSetChanged();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpGalleryList();
    }
}
