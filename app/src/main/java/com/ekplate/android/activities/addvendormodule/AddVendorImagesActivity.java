package com.ekplate.android.activities.addvendormodule;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.ekplate.android.R;
import com.ekplate.android.activities.menumodule.QuestionSubmitResultActivity;
import com.ekplate.android.adapters.addvendormodule.GalleryAdapter;
import com.ekplate.android.appinterfaces.BackgroundActionInterface;
import com.ekplate.android.config.BaseActivity;
import com.ekplate.android.localdbconfig.DbAdapter;
import com.ekplate.android.models.addvendormodule.ImageItem;
import com.ekplate.android.models.addvendormodule.MenuItem;
import com.ekplate.android.models.addvendormodule.VendorBasicInfoItem;
import com.ekplate.android.models.addvendormodule.VideoItem;
import com.ekplate.android.utils.AsyncTaskListener;
import com.ekplate.android.utils.CallServiceAction;
import com.ekplate.android.utils.CommonFunction;
import com.ekplate.android.utils.CommonMethods;
import com.ekplate.android.utils.ConstantClass;
import com.ekplate.android.utils.NetworkConnectionCheck;
import com.ekplate.android.utils.PostObject;
import com.ekplate.android.utils.Pref;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Avishek on 9/30/2015.
 */
public class AddVendorImagesActivity extends BaseActivity
        implements BackgroundActionInterface {

    private Toolbar toolbarAddVendors;
    private TextView toolbarHeaderText;
    GridView gridGallery, gridGalleryVideo;
    GalleryAdapter adapter;
    ImageView imgViewDelete, imgView, ivOpenCameraAddVendor,
            ivOpenVideoCameraAddVendor, ivVideoPlayIcon;
    EditText etCaption;
    private CardView cvAddMenuImage, cvAddVendorImageBack;
    String action, captureImagePath;
    ImageLoader imageLoader;
    public static ArrayList<CustomGallery> dataT = new ArrayList<CustomGallery>();
    private ArrayList<VideoItem> videoItems;
    private ArrayList<MenuItem> menuItems;
    int pos, flagImageFromGallery = 0, selectedVideoPosition = 0,
        videoDeleteFlag = 0;
    private DbAdapter dbAdapter;
    private SelectedVideoAdapter selectedVideoAdapter;
    private LinearLayout llSubmitImage;
    private CommonFunction _comFunc;
    private Pref _pref;
    private NetworkConnectionCheck _connection;
    private CallServiceAction _serviceAction;
    private CommonMethods _CommonMethods;
    private ProgressDialog progressDialog;
    private String vendorId;
    private static final int REQUEST_CAMERA = 100;
    static final int REQUEST_VIDEO_CAPTURE = 200;
    private static final int SELECT_FILE = 300;
    private Uri fileUri;
    public static int flagImageSelected = 0;
    private Bitmap bmThumbnail;
    private String videoAbsolutePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vendor_images);
        getWindow().setBackgroundDrawable(null);
        getSupportActionBar().hide();
        initialize();
        setUpToolbar();
        onClick();
        if (_pref.getIntegerSession(ConstantClass.TAG_ADD_VENDOR_IMAGE_VIDEO_FLAG) == 1) {
            setUpSelectedImage();
            setSelectedVideo();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void initialize(){
        toolbarAddVendors = (Toolbar) findViewById(R.id.toolbarAddVendors);
        toolbarHeaderText = (TextView) findViewById(R.id.toolbarHeaderText);
        gridGallery = (GridView) findViewById(R.id.gridGallery);
        gridGalleryVideo = (GridView) findViewById(R.id.gridGalleryVideo);
        gridGallery.setFastScrollEnabled(true);
        imgViewDelete = (ImageView) findViewById(R.id.imgViewDelete);
        imgView = (ImageView) findViewById(R.id.imgView);
        etCaption = (EditText) findViewById(R.id.etCaption);
        cvAddMenuImage = (CardView) findViewById(R.id.cvAddMenuImage);
        cvAddVendorImageBack = (CardView) findViewById(R.id.cvAddVendorImageBack);
        llSubmitImage = (LinearLayout) findViewById(R.id.llSubmitImage);
        ivOpenVideoCameraAddVendor = (ImageView) findViewById(R.id.ivOpenVideoCameraAddVendor);
        adapter = new GalleryAdapter(AddVendorImagesActivity.this, imageLoader);
        ivOpenCameraAddVendor = (ImageView) findViewById(R.id.ivOpenCameraAddVendor);
        ivVideoPlayIcon = (ImageView) findViewById(R.id.ivVideoPlayIcon);
        dbAdapter = new DbAdapter(this);
        adapter.setMultiplePick(false);
        _comFunc = new CommonFunction(this);
        _pref = new Pref(this);
        _connection = new NetworkConnectionCheck(this);
        _serviceAction = new CallServiceAction(this);
        _serviceAction.actionInterface = AddVendorImagesActivity.this;
        _CommonMethods = new CommonMethods(this);
        menuItems = new ArrayList<>();
        videoItems = new ArrayList<>();
        selectedVideoAdapter = new SelectedVideoAdapter(videoItems);
        gridGallery.setAdapter(adapter);
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc().imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
        ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(
                this).defaultDisplayImageOptions(defaultOptions).memoryCache(
                new WeakMemoryCache());
        ImageLoaderConfiguration config = builder.build();
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);
        gridGalleryVideo.setAdapter(selectedVideoAdapter);
    }

    private void setUpToolbar(){
        toolbarAddVendors.setNavigationIcon(R.drawable.ic_action_back);
        toolbarHeaderText.setText("ADD IMAGES");
        toolbarAddVendors.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setUpSelectedImage() {
        dbAdapter.open();
        flagImageSelected = 0;
        dbAdapter.getImages(new ArrayList<ImageItem>());
        dbAdapter.close();
        imgView.setVisibility(View.VISIBLE);
        setGridView();
    }

    private void setSelectedVideo() {
        dbAdapter.open();
        videoItems = dbAdapter.getVideo(videoItems);
        dbAdapter.close();
        if (videoItems.size() > 0) {
            imgView.setVisibility(View.VISIBLE);
            videoAbsolutePath = _CommonMethods.getRealPathFromURI(getApplicationContext(),
                    Uri.fromFile(new File(videoItems.get(0).getVideoPath())));
            bmThumbnail = ThumbnailUtils.createVideoThumbnail(videoAbsolutePath, MediaStore.Images.Thumbnails.MINI_KIND);
            setVideoGallery();
        }
    }

    private void onClick(){
        imgViewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dataT.size()>0 || videoItems.size()>0) {
                    if (imgView.getTag().equals("image")) {
                        flagImageFromGallery = 1;
                        if (dataT.size() > 1) {
                            if (_pref.getIntegerSession(ConstantClass.TAG_ADD_VENDOR_IMAGE_VIDEO_FLAG) == 1) {
                                dbAdapter.open();
                                dbAdapter.deleteSelectedImage(dataT.get(pos).selectedImageId);
                                dbAdapter.close();
                            }
                            dataT.remove(pos);
                            setGridView();
                        } else if (dataT.size() == 1) {
                            if (_pref.getIntegerSession(ConstantClass.TAG_ADD_VENDOR_IMAGE_VIDEO_FLAG) == 1) {
                                dbAdapter.open();
                                dbAdapter.deleteSelectedImage(dataT.get(pos).selectedImageId);
                                dbAdapter.close();
                            }
                            dataT.remove(pos);
                            videoDeleteFlag = 1;
                            setGridView();
                        } else {
                            if (videoItems.size() > 0) {
                                setVideoGallery();
                            } else {
                                Toast.makeText(AddVendorImagesActivity.this, "No item to delete", Toast.LENGTH_LONG).show();
                            }
                        }
                    } else {
                        if (videoItems.size() > 0) {
                            videoItems.remove(selectedVideoPosition);
                            videoDeleteFlag = 1;
                            setVideoGallery();
                        } else {
                            if (dataT.size() > 0) {
                                setGridView();
                            } else {
                                Toast.makeText(AddVendorImagesActivity.this, "No item to delete", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }else{
                  //  imgView.setImageResource(R.drawable.default_image_vendor_inside);
                    String uri = "@drawable/default_image_vendor_inside";  // where myresource (without the extension) is the file
                    int imageResource = getResources().getIdentifier(uri, null, getPackageName());
                    Drawable res = getResources().getDrawable(imageResource);
                    imgView.setImageDrawable(res);
                }
            }
        });

        ivOpenCameraAddVendor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_CAMERA);*/
                selectImage();
            }
        });

        ivOpenVideoCameraAddVendor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoItems.size()<1) {
                    selectVideo();
                } else {
                    Toast.makeText(AddVendorImagesActivity.this, "You can not select more than one videos.",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        gridGallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                ivVideoPlayIcon.setVisibility(View.GONE);
                imageLoader.displayImage(
                        "file://" + adapter.getItem(position).sdcardPath,
                        imgView);
                imgView.setTag("image");
            }
        });

        gridGalleryVideo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedVideoPosition = position;
                ivVideoPlayIcon.setVisibility(View.VISIBLE);
                imgView.setImageBitmap(videoItems.get(position).getVideoThump());
                imgView.setTag("video");
            }
        });

        gridGallery.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        gridGalleryVideo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        cvAddMenuImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dbAdapter.open();
                dbAdapter.deleteMultipleSelectedImages();
                dbAdapter.close();

                for (int i = 0; i < dataT.size(); i++) {
                    Log.e("sdcardPath", dataT.get(i).sdcardPath);
                    if (dataT.get(i).selectedImageId == 0) {
                        saveGalleryImage(etCaption.getText().toString(), dataT.get(i).sdcardPath);
                    }
                }

                for (int i = 0; i < videoItems.size(); i++) {
                    saveSelectedVideo(etCaption.getText().toString(), videoItems.get(i).getVideoPath());
                }
                _pref.setSession(ConstantClass.TAG_ADD_VENDOR_IMAGE_VIDEO_FLAG, 1);
                startActivity(new Intent(AddVendorImagesActivity.this, AddVendorInformationActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        llSubmitImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (_connection.isNetworkAvailable()) {
                    if (dataT.size() > 0) {
                        setUpProgressDialog();
                        setUpAddVendorParameter();
                    } else {
                        Toast.makeText(AddVendorImagesActivity.this, "Please select images.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    _connection.getNetworkActiveAlert().show();
                }
            }
        });

        cvAddVendorImageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            String[] all_path = data.getStringArrayExtra("all_path");
            for (String string : all_path) {
                CustomGallery item = new CustomGallery();
                item.sdcardPath = string;
                item.fileType = "image";
                dataT.add(item);
            }
            setGridView();
        } else if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK && null != data) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            imgView.setImageBitmap(photo);
            // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
            Uri tempUri = _CommonMethods.getImageUri(getApplicationContext(), photo);
            // CALL THIS METHOD TO GET THE ACTUAL PATH
            File finalFile = new File(_CommonMethods.getRealPathFromURI(tempUri));
            CustomGallery _item = new CustomGallery();
            _item.sdcardPath = _CommonMethods.getRealPathFromURI(tempUri);
            _item.fileType = "image";
            dataT.add(_item);
            etCaption.setVisibility(View.VISIBLE);
        } else if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK && null != data) {
            Uri videoUri = data.getData();
            String videoAbsolutePath = _CommonMethods.getRealPathFromURI(getApplicationContext(), videoUri);
            Bitmap bmThumbnail = ThumbnailUtils.createVideoThumbnail(videoAbsolutePath, MediaStore.Images.Thumbnails.MICRO_KIND);
            imgView.setImageBitmap(bmThumbnail);
            CustomGallery _item = new CustomGallery();
            _item.sdcardPath = videoAbsolutePath;
            _item.fileType = "video";
            dataT.add(_item);
            Log.e("videoAbsolutePath", videoAbsolutePath);
        }*/

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
            CustomGallery _item = new CustomGallery();
            _item.sdcardPath = ConstantClass.TAG_CAPTURED_IMAGE_PATH;
            _item.fileType = "image";
            _item.selectedImageId = 0;
            dataT.add(_item);
            setGridView();
            imgView.setVisibility(View.VISIBLE);
            etCaption.setVisibility(View.VISIBLE);
        } else if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK && null != data){
            Uri videoUri = data.getData();
            videoAbsolutePath = _CommonMethods.getRealPathFromURI(getApplicationContext(), videoUri);
            bmThumbnail = ThumbnailUtils.createVideoThumbnail(videoAbsolutePath, MediaStore.Images.Thumbnails.MINI_KIND);
            setDelayForLoadItem();

        } else if (requestCode == SELECT_FILE && resultCode == RESULT_OK && null != data) {
            Uri videoUri = data.getData();
            // OI FILE Manager
            videoAbsolutePath=_CommonMethods.getRealPathFromURI(getApplicationContext(), videoUri);
            bmThumbnail=ThumbnailUtils.createVideoThumbnail(videoAbsolutePath, MediaStore.Images.Thumbnails.MINI_KIND);
            setDelayForLoadItem();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setDelayForLoadItem(){
        new Handler().postDelayed(new Runnable() {
            public void run() {
                VideoItem _item = new VideoItem();
                _item.setVideoPath(videoAbsolutePath);
                _item.setVideoThump(bmThumbnail);
                videoItems.add(_item);
                setVideoGallery();
            }
        }, 500L);
    }

    private void setGridView(){
        adapter = new GalleryAdapter(AddVendorImagesActivity.this, imageLoader);
        adapter.addAll(dataT);
        adapter.setMultiplePick(false);
        gridGallery.setAdapter(adapter);
        ivVideoPlayIcon.setVisibility(View.GONE);
        imgView.setTag("image");
        pos = 0;
        if (adapter.getCount() > 0) {
            imageLoader.displayImage("file://"
                    + adapter.getItem(0).sdcardPath, imgView);
            etCaption.setVisibility(View.VISIBLE);
        }
        else {
            //imgView.setImageResource(R.drawable.no_media);
            if(videoItems.size() > 0) {
                Log.d("video count", String.valueOf(videoItems.size()));
                setVideoGallery();
            } else {
                //imgView.setVisibility(View.GONE);
                etCaption.setVisibility(View.GONE);
            }
        }
    }

    private void setVideoGallery() {
        ivVideoPlayIcon.setVisibility(View.VISIBLE);
        videoItems.trimToSize();
        if (videoDeleteFlag == 1 && videoItems.size()>0){
            imgView.setImageBitmap(videoItems.get(0).getVideoThump());
            imgView.setVisibility(View.VISIBLE);
            etCaption.setVisibility(View.VISIBLE);
            videoDeleteFlag = 0;
        } else if(bmThumbnail != null) {
            imgView.setImageBitmap(bmThumbnail);
            imgView.setVisibility(View.VISIBLE);
            etCaption.setVisibility(View.VISIBLE);
        } else {
            if(dataT.size()>0) {
                setGridView();
            } else {
               // imgView.setVisibility(View.GONE);
                etCaption.setVisibility(View.GONE);
            }
        }
        imgView.setTag("video");
        if (videoItems.size() > 1) {
            selectedVideoPosition = 1;
        } else {
            selectedVideoPosition = 0;
        }
        bmThumbnail = null;
        loadVideoItem();
    }

    private void saveGalleryImage(String imageCaption, String imagePath){
        dbAdapter.open();
        dbAdapter.insertVendorImageInfo(imageCaption, imagePath);
        dbAdapter.close();
    }

    private void saveSelectedVideo(String videoCaption, String videoPath) {
        dbAdapter.open();
        dbAdapter.insertVendorVideoInfo(videoCaption, videoPath);
        dbAdapter.close();
    }

    private void setUpProgressDialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait..");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    AsyncTaskListener _profileChangeAsync = new AsyncTaskListener() {
        @Override
        public void onTaskPreExecute() {
            // TODO Auto-generated method stub

        }

        @Override
        public void onTaskCompleted(String result) {
            // TODO Auto-generated method stub
            //Toast.makeText(AddVendorImagesActivity.this, "Thank You for adding. We will review and notify you.", Toast.LENGTH_LONG).show();
            Log.e("result", result);
            _pref.setSession(ConstantClass.TAG_ADD_VENDOR_SUBMIT_FLAG, 0);
            _pref.setSession(ConstantClass.TAG_ADD_VENDOR_LOCATION_FLAG, 0);
            _pref.setSession(ConstantClass.TAG_ADD_VENDOR_RATING_FLAG, 0);
            _pref.setSession(ConstantClass.TAG_ADD_VENDOR_BASIC_INFO_FLAG, 0);
            _pref.setSession(ConstantClass.TAG_ADD_VENDOR_IMAGE_VIDEO_FLAG, 0);
            dataT.clear();
            if (videoItems.size()==0) {
                progressDialog.dismiss();
                ConstantClass.TAG_CAPTURED_IMAGE_PATH = "";
                _pref.setSession(ConstantClass.TAG_ADD_VENDOR_SUBMIT_FLAG, 0);
                Intent intent = new Intent(AddVendorImagesActivity.this, QuestionSubmitResultActivity.class);
                intent.putExtra("screenTypeFlag", "add_vendor");
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            } else {
                postSelectedVideo();
            }
        }
    };

    AsyncTaskListener _videoSubmitAsync = new AsyncTaskListener() {
        @Override
        public void onTaskCompleted(String result) {
            Log.e("result", result);
            videoItems.clear();
            progressDialog.dismiss();
            Intent intent = new Intent(AddVendorImagesActivity.this, QuestionSubmitResultActivity.class);
            intent.putExtra("screenTypeFlag", "add_vendor");
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }

        @Override
        public void onTaskPreExecute() {

        }
    };

    private void setUpAddVendorParameter(){
        dbAdapter.open();
        ArrayList<VendorBasicInfoItem> vendorBasicInfoItems = dbAdapter.getVendorBasicInfo();
        menuItems = dbAdapter.getMenuList(menuItems);
        dbAdapter.close();
        try {
            JSONObject parentJsonObj = new JSONObject();
            JSONObject childJsonObj = new JSONObject();
            JSONArray childFoodItemJsonArray = new JSONArray();
            childJsonObj.put("accessToken", _pref.getSession(ConstantClass.ACCESS_TOKEN));
            childJsonObj.put("vendorName", vendorBasicInfoItems.get(0).getVendorName());
            childJsonObj.put("vendorShop", vendorBasicInfoItems.get(0).getShopName());
            childJsonObj.put("vendorContactNo", vendorBasicInfoItems.get(0).getContactNo());
            childJsonObj.put("vendorMostSellingFood", vendorBasicInfoItems.get(0).getMostSellingFood());
            childJsonObj.put("address", vendorBasicInfoItems.get(0).getVendorAddress());
            childJsonObj.put("latitude", vendorBasicInfoItems.get(0).getLatitude());
            childJsonObj.put("longitude",vendorBasicInfoItems.get(0).getLongitude());
            childJsonObj.put("hygieneRating", vendorBasicInfoItems.get(0).getHygieneRating());
            childJsonObj.put("tasteRating", vendorBasicInfoItems.get(0).getTasteRating());
            for (int i=0; i<menuItems.size(); i++){
                JSONObject childFoodItemJsonObj = new JSONObject();
                childFoodItemJsonObj.put("foodItem", menuItems.get(i).getFoodName());
                childFoodItemJsonObj.put("foodPrice", menuItems.get(i).getFoodValue());
                childFoodItemJsonArray.put(childFoodItemJsonObj);
            }
            childJsonObj.put("foodMenuList", childFoodItemJsonArray);
            parentJsonObj.put("data", childJsonObj);
            Log.e("inputIIdata", parentJsonObj.toString());
            _serviceAction.requestVersionApi(parentJsonObj, "insert-new-vendor");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onStarted() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (flagImageSelected == 1){
            flagImageSelected = 0;
            imgView.setVisibility(View.VISIBLE);
            setGridView();
        }
    }

    @Override
    public void onCompleted(JSONObject response) {
        Log.e("suggestion response", response.toString());
        try {
            JSONObject errorNodeJsonObj = response.getJSONObject("errNode");
            if (errorNodeJsonObj.getInt("errCode") == 0) {
                JSONObject dataJsonObject = response.getJSONObject("data");
                if (dataJsonObject.getBoolean("success")){
                    vendorId = dataJsonObject.getString("vendorId");
                    _pref.setSession(ConstantClass.TAG_INSERTED_VENDOR_ID,vendorId);
                    postSelectedImage();
                } else {
                    Toast.makeText(AddVendorImagesActivity.this, dataJsonObject.getString("sucessMsg"), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(AddVendorImagesActivity.this, errorNodeJsonObj.getString("errMsg"), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void postSelectedImage(){
        HashMap<String, PostObject> _postMap = new HashMap<String, PostObject>();

        _postMap.put("accessToken", _comFunc.getPostObject(
                String.valueOf(_pref.getSession(ConstantClass.ACCESS_TOKEN)), false));

        _postMap.put("vendorId", _comFunc.getPostObject(
                String.valueOf(vendorId), false));

        _postMap.put("no_image", _comFunc.getPostObject(
                String.valueOf(dataT.size()),
                false));

        for(int i=0; i<dataT.size(); i++){
            Log.e("image path", dataT.get(i).sdcardPath);
            _postMap.put("file_type_" + i, _comFunc.getPostObject(dataT.get(i).fileType, false));
            _postMap.put("image_" + i, _comFunc.getPostObject(dataT.get(i).sdcardPath, true));
        }

        _comFunc.callPostWebservice(ConstantClass.BASE_URL + "insert-select-image", _postMap, _profileChangeAsync, true);
    }

    private void postSelectedVideo() {
        HashMap<String, PostObject> _postMap = new HashMap<String, PostObject>();

        _postMap.put("accessToken", _comFunc.getPostObject(
                String.valueOf(_pref.getSession(ConstantClass.ACCESS_TOKEN)), false));

        _postMap.put("vendorId", _comFunc.getPostObject(
                String.valueOf(vendorId), false));

        _postMap.put("no_video", _comFunc.getPostObject(
                String.valueOf(videoItems.size()), false));

        for (int i=0; i<videoItems.size(); i++) {
            _postMap.put("id_" + i, _comFunc.getPostObject(String.valueOf(vendorId), false));
            _postMap.put("videoUrl_" + i, _comFunc.getPostObject(videoItems.get(i).getVideoPath(), true));
        }

        _comFunc.callPostWebservice(ConstantClass.BASE_URL + "upload-video", _postMap, _videoSubmitAsync, true);
        Log.i("videoServicePara:",_postMap.toString());
    }

    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Gallery", "Cancel" };
        android.support.v7.app.AlertDialog.Builder builder =
                new android.support.v7.app.AlertDialog.Builder(AddVendorImagesActivity.this);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    //dataT.clear();
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File imageFile = _CommonMethods.getOutputMediaFile();
                    captureImagePath = imageFile.getAbsolutePath();
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
                    Log.e("capturedImagePath in", ">>>" + captureImagePath);
                    ConstantClass.TAG_CAPTURED_IMAGE_PATH = captureImagePath;
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (items[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent(Action.ACTION_MULTIPLE_PICK);
                    intent.putExtra("routeFrom", "add_vendor_image");
                    startActivityForResult(intent, 200);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void selectVideo() {
        final CharSequence[] items = { "Take Video", "Choose from Gallery", "Cancel" };
        android.support.v7.app.AlertDialog.Builder builder =
                new android.support.v7.app.AlertDialog.Builder(AddVendorImagesActivity.this);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Video")) {
                    //dataT.clear();
                    Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
                    }
                } else if (items[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("video/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"), SELECT_FILE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


    ///////////////////  Selected Video Adapter /////////////////

    class SelectedVideoAdapter extends BaseAdapter {
        private ArrayList<VideoItem> videoItemArrayList;
        private LayoutInflater layoutInflater;

        public SelectedVideoAdapter(ArrayList<VideoItem> videoItemArrayList) {
            this.videoItemArrayList = videoItemArrayList;
            this.layoutInflater = LayoutInflater.from(AddVendorImagesActivity.this);
        }

        @Override
        public int getCount() {
            return videoItemArrayList.size();
        }

        @Override
        public VideoItem getItem(int position) {
            return videoItemArrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rootView = convertView;
            if (rootView == null) {
                rootView = layoutInflater.inflate(R.layout.selected_video_item_layout, parent, false);
                VideoItemHolder holder = new VideoItemHolder();
                holder.ivVideoThumb = (ImageView) rootView.findViewById(R.id.ivVideoThumb);
                holder.rlThumbContainer = (RelativeLayout) rootView.findViewById(R.id.rlThumbContainer);
                rootView.setTag(holder);
            }
            VideoItemHolder newHolder = (VideoItemHolder) rootView.getTag();
            newHolder.ivVideoThumb.setImageBitmap(getItem(position).getVideoThump());
            return rootView;
        }

        public class VideoItemHolder {
            ImageView ivVideoThumb;
            RelativeLayout rlThumbContainer;
        }
    }

    private void loadVideoItem() {
        selectedVideoAdapter.notifyDataSetChanged();
    }

    //////////////////           End            /////////////////
}
