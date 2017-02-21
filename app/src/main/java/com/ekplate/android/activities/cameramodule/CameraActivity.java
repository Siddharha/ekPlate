package com.ekplate.android.activities.cameramodule;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ekplate.android.R;
import com.ekplate.android.activities.addvendormodule.Action;
import com.ekplate.android.activities.addvendormodule.CustomGallery;
import com.ekplate.android.config.BaseActivity;
import com.ekplate.android.utils.AsyncTaskListener;
import com.ekplate.android.utils.CommonFunction;
import com.ekplate.android.utils.CommonMethods;
import com.ekplate.android.utils.ConstantClass;
import com.ekplate.android.utils.ImageCompressCropp;
import com.ekplate.android.utils.NetworkConnectionCheck;
import com.ekplate.android.utils.PostObject;
import com.ekplate.android.utils.Pref;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by user on 01-12-2015.
 */
public class CameraActivity extends BaseActivity implements SurfaceHolder.Callback{

    private Camera camera;
    private SurfaceView cameraView;
    private SurfaceHolder surfaceHolder;
    private Camera.Parameters cameraParameters;
    private LinearLayout llCameraFlashLight, llCameraGallery;
    private boolean isFlashOn = false;
    private PackageManager packageManager;
    private LinearLayout llCaptureImage;
    private ImageView ivCapturedImage;
    private String iconsStoragePathFile, iconsStoragePathDir, selectedProfImagePath;
    private File sdIconStorageDir, sdIconStorageFile;
    private Toolbar tbCameraView;
    private ImageCompressCropp _CompressCropp;
    private static int RESULT_LOAD_IMG = 1;
    private ProgressDialog progressDialog;
    private CommonMethods _commonMethods;
    private NetworkConnectionCheck _connection;
    public static ArrayList<CustomGallery> dataT = new ArrayList<CustomGallery>();
    private CommonFunction _comFunc;
    private Pref _pref;
    private String reviewId, successMsg, captureImagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        getWindow().setBackgroundDrawableResource(R.color.primary);
        getSupportActionBar().hide();
        setUpToolbar();
        initialize();
        setUpCameraView();
        onClick();
        /*if(getIntent().getExtras().getString("routeFrom").equalsIgnoreCase("customGallery")) {
            setSelectedImageForPost();
        }*/
    }

    private void initialize(){
        cameraView = (SurfaceView) findViewById(R.id.cameraView);
        surfaceHolder = cameraView.getHolder();
        llCameraFlashLight = (LinearLayout) findViewById(R.id.llCameraFlashLight);
        llCameraGallery = (LinearLayout) findViewById(R.id.llCameraGallery);
        llCaptureImage = (LinearLayout) findViewById(R.id.llCaptureImage);
        ivCapturedImage = (ImageView) findViewById(R.id.ivCapturedImage);
        _CompressCropp = new ImageCompressCropp(this);
        packageManager = getPackageManager();
        _pref = new Pref(this);
        _connection = new NetworkConnectionCheck(this);
        _comFunc = new CommonFunction(this);
    }

    private void setUpToolbar(){
        tbCameraView = (Toolbar) findViewById(R.id.tbCameraView);
        tbCameraView.inflateMenu(R.menu.menu_camera);
        tbCameraView.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        tbCameraView.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
            if (item.getItemId() == R.id.actionSelectImage) {
                if (getIntent().getExtras().getString("routeFrom").equalsIgnoreCase("vendor_details")) {
                    if (_connection.isNetworkAvailable()){
                        setUpProgressDialog();
                        postSelectedImage();
                    } else {
                        _connection.getNetworkActiveAlert().show();
                    }
                } else {
                    onBackPressed();
                }
            }
            return false;
            }
        });
    }

    private void setUpCameraView(){
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    private void onClick(){
        llCameraFlashLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConstantClass.TAG_SELECTED_PROFILE_IMAGE = "";
                Toast.makeText(CameraActivity.this, "No image selected now.", Toast.LENGTH_LONG).show();
                onBackPressed();
            }
        });

        llCaptureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //camera1.takePicture(myShutterCallback, myPictureCallback_RAW, myPictureCallback_JPG);
                setUpProgressDialog();
                camera.takePicture(shutterCallback, pictureCallbackRaw, pictureCallbackJpg);
            }
        });

        llCameraGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getIntent().getExtras().getString("routeFrom").equalsIgnoreCase("account_setting")) {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
                } else {
                    Intent intent = new Intent(Action.ACTION_MULTIPLE_PICK);
                    intent.putExtra("routeFrom", getIntent().getExtras().getString("routeFrom"));
                    intent.putExtra("vendorId", getIntent().getExtras().getInt("vendorId"));
                    startActivityForResult(intent, 100);
                }
            }
        });
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            progressDialog.dismiss();
        }
    };

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            camera = Camera.open();
        } catch (RuntimeException e) {
            System.err.println(e);
            return;
        }
        camera.setDisplayOrientation(90);
        cameraParameters = camera.getParameters();
        List<Camera.Size> sizes = cameraParameters.getSupportedPictureSizes();
        cameraParameters.setPictureSize(sizes.get(0).width, sizes.get(0).height); // mac dinh solution 0
        cameraParameters.set("orientation", "portrait");
        //parameters.setPreviewSize(viewWidth, viewHeight);
        List<Camera.Size> size = cameraParameters.getSupportedPreviewSizes();
        cameraParameters.setPreviewSize(size.get(0).width, size.get(0).height);
        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (Exception e) {
            System.err.println(e);
            return;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (surfaceHolder.getSurface() == null) {
            return;
        }
        try {
            camera.stopPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.stopPreview();
        camera.release();
        camera = null;
    }

    private void flashLightAction(){
        if (isFlashOn) {
            Log.i("info", "torch is turned off!");
            cameraParameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(cameraParameters);
            isFlashOn = false;
        }
        else {
            Log.i("info", "torch is turned on!");
            cameraParameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            camera.setParameters(cameraParameters);
            isFlashOn = true;
        }
    }

    Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {

        }
    };

    Camera.PictureCallback pictureCallbackRaw = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

        }
    };

    Camera.PictureCallback pictureCallbackJpg = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(final byte[] data, final  Camera camera) {
            new Thread() {
                public void run() {
                    Bitmap bitmapPicture = BitmapFactory.decodeByteArray(data, 0, data.length);
                    Bitmap correctBmp = Bitmap.createBitmap(bitmapPicture, 0, 0, bitmapPicture.getWidth(), bitmapPicture.getHeight(), null, true);
                    String imagePath = storeImage(correctBmp);
                    _CompressCropp.compressImageForPath(imagePath);
                    ConstantClass.TAG_SELECTED_PROFILE_IMAGE = _CompressCropp.getImageFilePath();
                    camera.startPreview();
                    handler.sendEmptyMessage(0);
                }
            }.start();
        }
    };

    private String storeImage(Bitmap imageData) {
        iconsStoragePathDir = Environment.getExternalStorageDirectory()
                + File.separator + getPackageName()
                + File.separator + "images";
        sdIconStorageDir = new File(iconsStoragePathDir);

        // get path to external storage (SD card)
        if (!sdIconStorageDir.isDirectory()) {
            sdIconStorageDir.mkdirs();
        }
        String filePath = "";
        try {
            iconsStoragePathFile = Environment.getExternalStorageDirectory()
                    + File.separator + getPackageName()
                    + File.separator + "images" + File.separator + "images_1";
            sdIconStorageFile = new File(iconsStoragePathFile);

            filePath = sdIconStorageFile.toString();
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);
            // choose another format if PNG doesn't suit you
            imageData.compress(Bitmap.CompressFormat.PNG, 100, bos);
            bos.flush();
            bos.close();
        } catch (FileNotFoundException e) {
            Log.w("TAG", "Error saving image file: " + e.getMessage());

        } catch (IOException e) {
            Log.w("TAG", "Error saving image file: " + e.getMessage());

        } catch (Exception e) {
            Toast.makeText(CameraActivity.this, "Image format not supported. Retry later.",
                    Toast.LENGTH_LONG).show();
        }
        return filePath;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                Uri uri = data.getData();
                String[] projection = { MediaStore.Images.Media.DATA };

                Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(projection[0]);
                String picturePath = cursor.getString(columnIndex); // returns null
                cursor.close();
                ConstantClass.TAG_SELECTED_PROFILE_IMAGE = picturePath.toString();
                Log.e("selectedImage", picturePath);
            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void setUpProgressDialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait..");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
    }

    private void postSelectedImage(){
        HashMap<String, PostObject> _postMap = new HashMap<String, PostObject>();

        _postMap.put("accessToken", _comFunc.getPostObject(
                String.valueOf(_pref.getSession(ConstantClass.ACCESS_TOKEN)), false));

        _postMap.put("vendorId", _comFunc.getPostObject(
                String.valueOf(getIntent().getExtras().getInt("vendorId")), false));

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

            _postMap.put("image_0" , _comFunc.getPostObject(ConstantClass.TAG_SELECTED_PROFILE_IMAGE, true));
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
            Toast.makeText(CameraActivity.this, "Gallery image submitted successfully", Toast.LENGTH_LONG).show();
            onBackPressed();
        }
    };
}