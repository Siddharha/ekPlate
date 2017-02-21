package com.ekplate.android.activities.vendormodule;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ekplate.android.R;
import com.ekplate.android.activities.addvendormodule.CustomGallery;
import com.ekplate.android.activities.menumodule.QuestionSubmitResultActivity;
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
import com.kbeanie.imagechooser.api.ChooserType;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.kbeanie.imagechooser.api.ChosenImages;
import com.kbeanie.imagechooser.api.ImageChooserListener;
import com.kbeanie.imagechooser.api.ImageChooserManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class WriteReviewActivity extends BaseActivity implements BackgroundActionInterface, ImageChooserListener {

    private EditText etWriteUserReview;
    String originalFilePath;
    private ImageView ivBackWriteReview, ivSubmitWriteReview,imgReview;
    private RatingBar ratingBarHygieneOne, ratingBarTasteOne;
    private TextView toolbarHeaderTextWriteReview;
    private LinearLayout llHygieneRatingContainer, llTasteRatingContainer, llAddPhotos;
    private CommonMethods _commonMethods;
    private float hygieneRating = 0.0f, tasteRating = 0.0f;
    private TextView tvHygieneRatingWriteReview, tvTasteRatingWriteReview;
    private Pref _pref;
    private NetworkConnectionCheck _connection;
    private CallServiceAction _serviceAction;
    private ProgressDialog progressDialog;
    public static ArrayList<CustomGallery> dataT = new ArrayList<CustomGallery>();
    private CommonFunction _comFunc;
    private String reviewId, successMsg, captureImagePath = "";
    private static final int REQUEST_CAMERA = 100;
    private int backFlag = 0;
    private int chooserType;
    private String filePath;
    private ImageChooserManager imageChooserManager;
    LinearLayout llReviewImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_review);
        getSupportActionBar().hide();
        initialize();
        setUpCommonInfo();
        setUpCustomHygieneRatingBar();
        setUpCustomTasteRatingBar();
        onClick();
        setFont();
    }

    private void initialize(){
        etWriteUserReview = (EditText) findViewById(R.id.etWriteUserReview);
        ivBackWriteReview = (ImageView) findViewById(R.id.ivBackWriteReview);
        llReviewImage = (LinearLayout)findViewById(R.id.llReviewImage);
        imgReview = (ImageView)findViewById(R.id.imgReview);
        _commonMethods = new CommonMethods(this);
        llHygieneRatingContainer = (LinearLayout) findViewById(R.id.llHygieneRatingContainer);
        llTasteRatingContainer = (LinearLayout) findViewById(R.id.llTasteRatingContainer);
        tvHygieneRatingWriteReview = (TextView) findViewById(R.id.tvHygieneRatingWriteReview);
        tvTasteRatingWriteReview = (TextView) findViewById(R.id.tvTasteRatingWriteReview);
        toolbarHeaderTextWriteReview = (TextView) findViewById(R.id.toolbarHeaderTextWriteReview);
        llAddPhotos = (LinearLayout) findViewById(R.id.llAddPhotos);
        ivSubmitWriteReview = (ImageView) findViewById(R.id.ivSubmitWriteReview);
        _pref = new Pref(this);
        _connection = new NetworkConnectionCheck(this);
        _serviceAction = new CallServiceAction(this);
        _serviceAction.actionInterface = this;
        _comFunc = new CommonFunction(this);
    }

    private void setUpCommonInfo(){
        toolbarHeaderTextWriteReview.setText(getIntent().getExtras().getString("vendorName"));
    }

    private void setFont(){
        Typeface fontBariolLight = Typeface.createFromAsset(getAssets(), "Bariol_Light.otf");
        etWriteUserReview.setTypeface(fontBariolLight);
    }

    private void onClick(){
        ivSubmitWriteReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (_connection.isNetworkAvailable()) {
                    if (etWriteUserReview.getText().toString().equals("")) {
                        Toast.makeText(WriteReviewActivity.this, "Please write your review.",
                                Toast.LENGTH_LONG).show();
                    } else if (etWriteUserReview.getText().toString().length() < 10) {
                        Toast.makeText(WriteReviewActivity.this, "Your review must contains at least " +
                                "10 characters", Toast.LENGTH_LONG).show();
                    } else if ((tasteRating==0)||(hygieneRating==0)) {
                        Toast.makeText(WriteReviewActivity.this, "Please Give Hygine" +
                                " and Test Rating", Toast.LENGTH_LONG).show();
                    }

                    else {
                        setUpProgressDialog();
                        setInputParamForWriteReview(etWriteUserReview.getText().toString());
                    }
                } else {
                    _connection.getNetworkActiveAlert().show();
                }
            }
        });

        llAddPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        imgReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        ivBackWriteReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (backFlag == 1) {
            Intent intent = new Intent(WriteReviewActivity.this, QuestionSubmitResultActivity.class);
            intent.putExtra("screenTypeFlag", "write_reviews");
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else {
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }

    private void setUpCustomHygieneRatingBar(){
        ratingBarHygieneOne = (RatingBar) findViewById(R.id.ratingBarHygieneOne);

        ratingBarHygieneOne.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                hygieneRating = rating;
                _commonMethods.setRatingContainerLayoutBackground(llHygieneRatingContainer,
                        String.valueOf(hygieneRating));
                tvHygieneRatingWriteReview.setText(String.valueOf(hygieneRating).replace(".0",""));
            }
        });
    }

    private void setUpCustomTasteRatingBar(){
        ratingBarTasteOne = (RatingBar) findViewById(R.id.ratingBarTasteOne);

        ratingBarTasteOne.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                tasteRating = rating;
                _commonMethods.setRatingContainerLayoutBackground(llTasteRatingContainer,
                        String.valueOf(tasteRating));
                tvTasteRatingWriteReview.setText(String.valueOf(tasteRating).replace(".0",""));
            }
        });
    }

    private void setInputParamForWriteReview(String userReview){
        try {
            JSONObject parentJsonObj = new JSONObject();
            JSONObject childJsonObj = new JSONObject();
            childJsonObj.put("accessToken", _pref.getSession(ConstantClass.ACCESS_TOKEN));
            childJsonObj.put("vendorId", getIntent().getExtras().getInt("vendorId"));
            childJsonObj.put("rateTaste", String.valueOf(tasteRating));
            childJsonObj.put("rateHygine",  String.valueOf(hygieneRating));
            childJsonObj.put("reviewDetails", userReview);
            parentJsonObj.put("data", childJsonObj);
            Log.e("input data", parentJsonObj.toString());
            _serviceAction.requestVersionApi(parentJsonObj, "user-review");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setUpProgressDialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait..");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
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
                    reviewId = jsonObjData.getString("reviewId");
                    successMsg = jsonObjData.getString("msg");
                    postSelectedImage();
                } else {
                    Toast.makeText(WriteReviewActivity.this, jsonObjData.getString("msg"), Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            } else {
                Toast.makeText(WriteReviewActivity.this, jsonObjError.getString("errMsg"), Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        } catch (Exception e){
            e.printStackTrace();
            progressDialog.dismiss();
        }
    }

    private void unSetRatingBar(){
        ratingBarHygieneOne.setRating(0.0f);
        ratingBarTasteOne.setRating(0.0f);
        etWriteUserReview.setText("");
        tvHygieneRatingWriteReview.setText("0");
        tvTasteRatingWriteReview.setText("0");
        _commonMethods.setRatingContainerLayoutBackground(llHygieneRatingContainer, "0");
        _commonMethods.setRatingContainerLayoutBackground(llTasteRatingContainer, "0");
        dataT.clear();
        ConstantClass.TAG_SELECTED_PROFILE_IMAGE = "";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
/*
        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
            *//*Bitmap bitmap = (Bitmap) data.getExtras().get("data");
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
            }*//*
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            imgReview.setImageBitmap(bitmap);
        }
        super.onActivityResult(requestCode, resultCode, data);*/
        imageChooserManager.submit(requestCode, data);
    }

    private void postSelectedImage(){
        HashMap<String, PostObject> _postMap = new HashMap<String, PostObject>();

        _postMap.put("accessToken", _comFunc.getPostObject(
                String.valueOf(_pref.getSession(ConstantClass.ACCESS_TOKEN)), false));

        _postMap.put("reviewId", _comFunc.getPostObject(
                String.valueOf(reviewId), false));

        _postMap.put("vendorId", _comFunc.getPostObject(
                String.valueOf(getIntent().getExtras().getInt("vendorId")), false));

        if(dataT.size()>0){

            _postMap.put("no_image", _comFunc.getPostObject(
                    String.valueOf(dataT.size()),
                    false));

            for (int i = 0; i < dataT.size(); i++) {
                Log.e("image_" + i, dataT.get(i).sdcardPath);
                _postMap.put("image_" + i, _comFunc.getPostObject(dataT.get(i).sdcardPath, true));
            }

        } else {
            _postMap.put("no_image", _comFunc.getPostObject(
                    String.valueOf(1), false));
            Log.e("capturedImagePath in", ">>>" + ConstantClass.TAG_CAPTURED_IMAGE_PATH);
            _postMap.put("image_0", _comFunc.getPostObject(ConstantClass.TAG_CAPTURED_IMAGE_PATH, true));
        }

        _comFunc.callPostWebservice(ConstantClass.BASE_URL + "write-user-review-image", _postMap, _profileChangeAsync, true);
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
            //Toast.makeText(WriteReviewActivity.this, successMsg, Toast.LENGTH_LONG).show();
            unSetRatingBar();
            backFlag = 1;
            ConstantClass.TAG_CAPTURED_IMAGE_PATH = "";
            onBackPressed();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("camera image", ConstantClass.TAG_SELECTED_PROFILE_IMAGE);
        if(dataT.size() > 0) {
            Toast.makeText(WriteReviewActivity.this, String.valueOf(dataT.size())
                    + " images have been selected.", Toast.LENGTH_LONG).show();
        }
    }

    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Gallery", "Cancel" };
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(WriteReviewActivity.this);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                  /*  dataT.clear();
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File imageFile = _commonMethods.getOutputMediaFile();
                    captureImagePath = imageFile.getAbsolutePath();
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
                    Log.e("capturedImagePath in", ">>>" + captureImagePath);
                    ConstantClass.TAG_CAPTURED_IMAGE_PATH = captureImagePath;
                    startActivityForResult(intent, REQUEST_CAMERA);*/
                    takePicture();
                } else if (items[item].equals("Choose from Gallery")) {
                   /* Intent intent = new Intent(Action.ACTION_MULTIPLE_PICK);
                    intent.putExtra("routeFrom", "write_review");
                    intent.putExtra("vendorId", getIntent().getExtras().getInt("vendorId"));
                    startActivityForResult(intent, 100);*/

                    chooseImage();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void takePicture() {
        chooserType = ChooserType.REQUEST_CAPTURE_PICTURE;
        imageChooserManager = new ImageChooserManager(this,
                ChooserType.REQUEST_CAPTURE_PICTURE, true);
        imageChooserManager.setImageChooserListener(this);
        imageChooserManager.clearOldFiles();
        try {
            filePath = imageChooserManager.choose();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void chooseImage() {
        chooserType = ChooserType.REQUEST_PICK_PICTURE;
        imageChooserManager = new ImageChooserManager(this,
                ChooserType.REQUEST_PICK_PICTURE, true);
        Bundle bundle = new Bundle();
        // bundle.putBoolean(Intent.EXTRA_ALLOW_MULTIPLE, true);
        imageChooserManager.setExtras(bundle);
        imageChooserManager.setImageChooserListener(this);
        imageChooserManager.clearOldFiles();
        try {
            filePath = imageChooserManager.choose();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onImageChosen(final ChosenImage chosenImage) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                // isActivityResultOver = true;
                originalFilePath = chosenImage.getFilePathOriginal();
                String thumbnailFilePath = chosenImage.getFileThumbnail();
                String thumbnailSmallFilePath = chosenImage.getFileThumbnailSmall();
                /*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, originalFilePath);
                startActivityForResult(intent, REQUEST_CAMERA);*/
                    ConstantClass.TAG_CAPTURED_IMAGE_PATH = originalFilePath;
                    Log.e("PATH_:", originalFilePath);
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 4;
                    imgReview.setImageBitmap(BitmapFactory.decodeFile(ConstantClass.TAG_CAPTURED_IMAGE_PATH, options));
                    llReviewImage.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onError(String s) {

    }

    @Override
    public void onImagesChosen(ChosenImages chosenImages) {

    }
}
