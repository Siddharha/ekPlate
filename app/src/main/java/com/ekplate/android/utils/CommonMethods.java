package com.ekplate.android.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.ekplate.android.R;
import com.ekplate.android.models.vendormodule.AddCollegeItem;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by user on 30-11-2015.
 */
public class CommonMethods {
    private Context context;

    public CommonMethods(Context context) {
        this.context = context;
    }

    public void setRatingContainerLayoutBackground(LinearLayout containerLayout, String Rating){
        switch (Rating){
            case "0.5":
                containerLayout.setBackgroundResource(R.drawable.round_rating_one_layout_bg);
                break;
            case "1":
                containerLayout.setBackgroundResource(R.drawable.round_rating_two_layout_bg);
                break;
            case "1.0":
                containerLayout.setBackgroundResource(R.drawable.round_rating_two_layout_bg);
                break;
            case "1.5":
                containerLayout.setBackgroundResource(R.drawable.round_rating_three_layout_bg);
                break;
            case "2":
                containerLayout.setBackgroundResource(R.drawable.round_rating_four_layout_bg);
                break;
            case "2.0":
                containerLayout.setBackgroundResource(R.drawable.round_rating_four_layout_bg);
                break;
            case "2.5":
                containerLayout.setBackgroundResource(R.drawable.round_rating_five_layout_bg);
                break;
            case "3":
                containerLayout.setBackgroundResource(R.drawable.round_rating_six_layout_bg);
                break;
            case "3.0":
                containerLayout.setBackgroundResource(R.drawable.round_rating_six_layout_bg);
                break;
            case "3.5":
                containerLayout.setBackgroundResource(R.drawable.round_rating_seven_layout_bg);
                break;
            case "4":
                containerLayout.setBackgroundResource(R.drawable.round_rating_eight_layout_bg);
                break;
            case "4.0":
                containerLayout.setBackgroundResource(R.drawable.round_rating_eight_layout_bg);
                break;
            case "4.5":
                containerLayout.setBackgroundResource(R.drawable.round_rating_nine_layout_bg);
                break;
            case "5":
                containerLayout.setBackgroundResource(R.drawable.round_rating_ten_layout_bg);
                break;
            case "5.0":
                containerLayout.setBackgroundResource(R.drawable.round_rating_ten_layout_bg);
                break;
            default:
                containerLayout.setBackgroundResource(R.drawable.round_rating_one_layout_bg);
                break;
        }
    }

    public String getBase64String(String imagePath){

        try {
            Bitmap imageBitmap = BitmapFactory.decodeFile(imagePath);
            ByteArrayOutputStream outputStreamImage = new ByteArrayOutputStream();

            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStreamImage);
            byte[] imageByteArray = outputStreamImage.toByteArray();
            String encodedString = Base64.encodeToString(imageByteArray, Base64.DEFAULT);
            return encodedString;
        }
        catch (OutOfMemoryError e)
        {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4;
            Bitmap imageBitmap = BitmapFactory.decodeFile(imagePath,options);
            ByteArrayOutputStream outputStreamImage = new ByteArrayOutputStream();

            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStreamImage);
            byte[] imageByteArray = outputStreamImage.toByteArray();
            String encodedString = Base64.encodeToString(imageByteArray, Base64.DEFAULT);
            return encodedString;
        }

    }

    public boolean isGpsOn(){
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE );
        boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return statusOfGPS;
    }

    public AlertDialog getGpsActiveAlert(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("GPS Status");
        builder.setMessage("GPS is not available. Please enable GPS.")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();

                        Intent startMain = new Intent(Intent.ACTION_MAIN);
                        startMain.addCategory(Intent.CATEGORY_HOME);
                        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(startMain);
                    }
                });
        AlertDialog alertDialog = builder.create();
        return  alertDialog;
    }

    public double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);

        return Radius * c;
    }

    public float getZoomLevel(double distance){
        if (distance >= 1500.00) {
            return 3f;
        } else  if(distance >= 1000.00 && distance < 1500) {
            return 8f;
        } else {
            return 18f;
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public int getScreenHeight() {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        return display.getHeight();
    }

    public int getScreenWidth(){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        return display.getWidth();
    }

    private int pxToDp(int px) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }

    public void setAddedColleges(JSONArray jsonArrayAddedCollege) {
        try {
            for (int i=0; i<jsonArrayAddedCollege.length(); i++) {
                AddCollegeItem _item = new AddCollegeItem();
                JSONObject collegeItemJsonObj = jsonArrayAddedCollege.getJSONObject(i);
                _item.setId(collegeItemJsonObj.getInt("id"));
                _item.setCollegeTitle(collegeItemJsonObj.getString("collageName"));
                ConstantClass.addCollegeItems.add(_item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getRealPathFromUri(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public int dipToPixels(float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }

    public File getOutputMediaFile(){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.
        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "IMG_"+ timeStamp + ".jpg");
        return mediaFile;
    }

    public HashMap<String, String> getCoordinatesFromAddress(String address) {
        HashMap<String, String> locationDetails = new HashMap<>();
        try {
            Geocoder geocoder = new Geocoder(context);
            List<Address> addresses;
            addresses = geocoder.getFromLocationName(address, 1);
            if (addresses.size() > 0) {
                locationDetails.put("latitude", String.valueOf(addresses.get(0).getLatitude()));
                locationDetails.put("longitude", String.valueOf(addresses.get(0).getLongitude()));
                locationDetails.put("city", addresses.get(0).getLocality());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return locationDetails;
    }
}
