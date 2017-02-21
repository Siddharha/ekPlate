package com.ekplate.android.activities.vendormodule;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ekplate.android.R;
import com.ekplate.android.activities.addvendormodule.Action;
import com.ekplate.android.activities.addvendormodule.CustomGallery;
import com.ekplate.android.adapters.vendormodule.GallerySingleImageAdapter;
import com.ekplate.android.config.BaseActivity;
import com.ekplate.android.models.vendormodule.GalleryItem;
import com.ekplate.android.utils.AsyncTaskListener;
import com.ekplate.android.utils.CommonFunction;
import com.ekplate.android.utils.CommonMethods;
import com.ekplate.android.utils.ConstantClass;
import com.ekplate.android.utils.PostObject;
import com.ekplate.android.utils.Pref;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class GallerySingleImageActivity extends BaseActivity {

    private Toolbar tbSingeImage;
    private TextView toolbarHeaderText;
    private ViewPager pvSingleImage;
    private ImageView ivPreviousImage, ivNextImage;
    private ArrayList<GalleryItem> galleryItems;
    private GallerySingleImageAdapter adapter;
    private int selectedItem;
    private static final int REQUEST_CAMERA = 200;
    public static ArrayList<CustomGallery> dataT = new ArrayList<CustomGallery>();
    public static int flagImageSelected = 0;
    private Pref _pref;
    private CommonFunction _comFunc;
    private ProgressDialog progressDialog;
    private CommonMethods _CommonMethods;
    private String captureImagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        getWindow().setBackgroundDrawable(null);
        setContentView(R.layout.activity_gallery_single_image);
        galleryItems = (ArrayList<GalleryItem>) getIntent().getExtras().getSerializable("gallery_image_list");
        selectedItem = getIntent().getExtras().getInt("selected_item_position");
        Log.e("galleryItems size", String.valueOf(galleryItems.size()));
        initialize();
        setUpToolBar();
    }

    private void initialize(){
        pvSingleImage = (ViewPager) findViewById(R.id.pvSingleImage);
        ivPreviousImage = (ImageView) findViewById(R.id.ivPreviousImage);
        ivNextImage = (ImageView) findViewById(R.id.ivNextImage);
        adapter = new GallerySingleImageAdapter(getSupportFragmentManager(), this, galleryItems,
                getIntent().getExtras().getInt("vendorId"), getIntent().getExtras().getString("vendor_name"),
                getIntent().getExtras().getString("vendor_address"));
        _pref = new Pref(this);
        _comFunc = new CommonFunction(this);
        _CommonMethods = new CommonMethods(this);
    }

    private void setUpToolBar(){
        tbSingeImage = (Toolbar) findViewById(R.id.tbSingeImage);
        toolbarHeaderText = (TextView) findViewById(R.id.toolbarHeaderText);
        tbSingeImage.setNavigationIcon(R.drawable.ic_action_back);
        tbSingeImage.setBackgroundColor(Color.parseColor("#66000000"));
        tbSingeImage.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        tbSingeImage.inflateMenu(R.menu.menu_gallery_single_image);
        tbSingeImage.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.iconTakePhoto:
                        selectImage();
                        break;
                }
                return false;
            }
        });
    }

    private void setUpViewPager(){
        pvSingleImage.setAdapter(adapter);
        pvSingleImage.setCurrentItem(selectedItem);
        ivPreviousImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pvSingleImage.getCurrentItem() >= 0) {
                    pvSingleImage.setCurrentItem(pvSingleImage.getCurrentItem() - 1);
                }
            }
        });
        ivNextImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pvSingleImage.getCurrentItem() < galleryItems.size()) {
                    pvSingleImage.setCurrentItem(pvSingleImage.getCurrentItem() + 1);
                }
            }
        });
    }

    private void setUpProgressDialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait..");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
            Log.e("image path", ConstantClass.TAG_CAPTURED_IMAGE_PATH);
            postSelectedImage();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpViewPager();
        if (flagImageSelected == 1){
            flagImageSelected = 0;
            postSelectedImage();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Gallery", "Cancel" };
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    dataT.clear();
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File imageFile = _CommonMethods.getOutputMediaFile();
                    captureImagePath = imageFile.getAbsolutePath();
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
                    Log.e("capturedImagePath in", ">>>" + captureImagePath);
                    ConstantClass.TAG_CAPTURED_IMAGE_PATH = captureImagePath;
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (items[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent(Action.ACTION_MULTIPLE_PICK);
                    intent.putExtra("routeFrom", "gallery_single_image");
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
            Log.e("captureImagePath", ConstantClass.TAG_CAPTURED_IMAGE_PATH);
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
            Toast.makeText(GallerySingleImageActivity.this, "Gallery image submitted successfully", Toast.LENGTH_LONG).show();
        }
    };

}
