package com.ekplate.android.localdbconfig;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ekplate.android.activities.addvendormodule.AddVendorImagesActivity;
import com.ekplate.android.activities.addvendormodule.CustomGallery;
import com.ekplate.android.models.addvendormodule.ImageItem;
import com.ekplate.android.models.addvendormodule.MenuItem;
import com.ekplate.android.models.addvendormodule.VendorBasicInfoItem;
import com.ekplate.android.models.addvendormodule.VideoItem;
import com.ekplate.android.utils.ConstantClass;
import com.ekplate.android.utils.Pref;

import java.util.ArrayList;

/**
 * Created by user on 26-11-2015.
 */
public class DbAdapter {

    private DbHandler dbHandler;
    private Context context;
    private SQLiteDatabase dbSqLiteDatabase;
    private Pref _pref;

    public DbAdapter(Context context) {
        this.context = context;
        this.dbHandler = new DbHandler(context);
        this._pref = new Pref(context);
    }

    public DbAdapter open(){
        dbHandler = new DbHandler(context);
        dbSqLiteDatabase = dbHandler.getWritableDatabase();
        return this;
    }

    public void close(){
        dbHandler.close();
    }

    private class DbHandler extends SQLiteOpenHelper {

        public DbHandler(Context context) {
            super(context, DbConstantClass.TAG_DB_NAME, null, DbConstantClass.TAG_DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(DbConstantClass.TAG_CREATE_VENDOR_BASIC_INFO_TAB);
            sqLiteDatabase.execSQL(DbConstantClass.TAG_CREATE_VENDOR_FOOD_MENU_TB);
            sqLiteDatabase.execSQL(DbConstantClass.TAG_CREATE_VENDOR_IMAGE_TB);
            sqLiteDatabase.execSQL(DbConstantClass.TAG_CREATE_VENDOR_VIDEO_TB);
            sqLiteDatabase.execSQL(DbConstantClass.TAG_CREATE_USER_PROF_TB);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
            sqLiteDatabase.execSQL(DbConstantClass.TAG_CREATE_VENDOR_BASIC_INFO_TAB);
            sqLiteDatabase.execSQL(DbConstantClass.TAG_CREATE_VENDOR_FOOD_MENU_TB);
            sqLiteDatabase.execSQL(DbConstantClass.TAG_CREATE_VENDOR_IMAGE_TB);
            sqLiteDatabase.execSQL(DbConstantClass.TAG_CREATE_VENDOR_VIDEO_TB);
            sqLiteDatabase.execSQL(DbConstantClass.TAG_CREATE_USER_PROF_TB);
            onCreate(sqLiteDatabase);
        }
    }

    public void insertVendorBasicInfo(String vendorName, String shopName, String mostSellingFood, String contactNo){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbConstantClass.TAG_VENDOR_NAME, vendorName);
        contentValues.put(DbConstantClass.TAG_CONTACT_NO, contactNo);
        contentValues.put(DbConstantClass.TAG_SHOP_NAME, shopName);
        contentValues.put(DbConstantClass.TAG_MOST_SELLING_FOOD, mostSellingFood);
        Long lastInsertedVendorId = dbSqLiteDatabase.insert(DbConstantClass.TAG_TB_VENDOR_BASIC_INFO, null, contentValues);
        Log.e("lastInsertedVendorId", String.valueOf(lastInsertedVendorId));
        _pref.setSession(ConstantClass.TAG_INSERTED_VENDOR_ID, String.valueOf(lastInsertedVendorId));
    }

    public int updateVendorLocation(String address, String latitude, String longitude){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbConstantClass.TAG_ADDRESS, address);
        contentValues.put(DbConstantClass.TAG_LATITUDE, latitude);
        contentValues.put(DbConstantClass.TAG_LONGITUDE, longitude);
        int success = dbSqLiteDatabase.update(DbConstantClass.TAG_TB_VENDOR_BASIC_INFO, contentValues,
                "id = " + _pref.getSession(ConstantClass.TAG_INSERTED_VENDOR_ID), null);
        return success;
    }

    public int updateVendorRating(String hygieneRating, String tasteRating){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbConstantClass.TAG_HYGIENE_RATING, hygieneRating);
        contentValues.put(DbConstantClass.TAG_TASTE_RATING, tasteRating);
        int success = dbSqLiteDatabase.update(DbConstantClass.TAG_TB_VENDOR_BASIC_INFO, contentValues,
                "id = " + _pref.getSession(ConstantClass.TAG_INSERTED_VENDOR_ID), null);
        return success;
    }

    public void insertVendorFoodMenu(String foodName, String price){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbConstantClass.TAG_FOOD_NAME, foodName);
        contentValues.put(DbConstantClass.TAG_FOOD_PRICE, price);
        contentValues.put(DbConstantClass.TAG_VENDOR_ID, _pref.getSession(ConstantClass.TAG_INSERTED_VENDOR_ID));
        Long lastInsertedVendorId = dbSqLiteDatabase.insert(DbConstantClass.TAG_TB_VENDOR_FOOD_MENU, null, contentValues);
    }

    public void insertVendorImageInfo(String caption, String imagePath){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbConstantClass.TAG_IMAGE_PATH, imagePath);
        contentValues.put(DbConstantClass.TAG_IMAGE_CAPTION, caption);
        contentValues.put(DbConstantClass.TAG_IMAGE_VENDOR_ID, _pref.getSession(ConstantClass.TAG_INSERTED_VENDOR_ID));
        Long lastInsertedVendorId = dbSqLiteDatabase.insert(DbConstantClass.TAG_TB_VENDOR_IMAGE, null, contentValues);
        Log.e("lastInsertedImage", String.valueOf(lastInsertedVendorId));
    }

    public void insertVendorVideoInfo(String caption, String videoPath){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbConstantClass.TAG_VIDEO_PATH, videoPath);
        contentValues.put(DbConstantClass.TAG_VIDEO_CAPTION, caption);
        contentValues.put(DbConstantClass.TAG_VENDOR_ID, _pref.getSession(ConstantClass.TAG_INSERTED_VENDOR_ID));
        Long lastInsertedVendorId = dbSqLiteDatabase.insert(DbConstantClass.TAG_TB_VENDOR_VIDEO, null, contentValues);
        Log.e("lastInsertedImage", String.valueOf(lastInsertedVendorId));
    }

    public void insertUserVegStatus(String userId, String vegStatus) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbConstantClass.TAG_USER_ID, userId);
        contentValues.put(DbConstantClass.TAG_VEG_STATUS, vegStatus);
        Long lastInsertedVendorId = dbSqLiteDatabase.insert(DbConstantClass.TAG_TB_USER_PROF, null, contentValues);
        Log.e("lastInsertedImage", String.valueOf(lastInsertedVendorId));
    }

    public int updateVegStatus(String userId, String vegStatus) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbConstantClass.TAG_VEG_STATUS, vegStatus);
        int success = dbSqLiteDatabase.update(DbConstantClass.TAG_TB_USER_PROF, contentValues,
                "user_id = '" + userId + "'", null);
        return success;
    }

    public String getVegStatus(String user_id) {
        Cursor cursorScan;
        String vegStatus = "non-veg";
        cursorScan = dbSqLiteDatabase.rawQuery("select *" +
                " from tb_user_prof where user_id = '" + user_id + "'", null);
        for(cursorScan.moveToFirst(); !(cursorScan.isAfterLast()); cursorScan.moveToNext()){
            vegStatus = cursorScan.getString(cursorScan.getColumnIndex("veg_status"));
        }

        return vegStatus;
    }

    public boolean isUserExist(String user_id) {
        Cursor cursorScan;
        boolean userExist = false;
        cursorScan = dbSqLiteDatabase.rawQuery("select *" +
                " from tb_user_prof where user_id = '" + user_id + "'", null);
        for(cursorScan.moveToFirst(); !(cursorScan.isAfterLast()); cursorScan.moveToNext()){
            userExist = true;
        }

        return userExist;
    }

    public ArrayList<VendorBasicInfoItem> getVendorBasicInfo(){
        ArrayList<VendorBasicInfoItem> vendorBasicInfoItems = new ArrayList<VendorBasicInfoItem>();
        Cursor cursorScan;
        cursorScan = dbSqLiteDatabase.rawQuery("select *" +
                " from tb_vendor_basic_info where id = " +
                _pref.getSession(ConstantClass.TAG_INSERTED_VENDOR_ID), null);

        int pos_id = cursorScan.getColumnIndex(DbConstantClass.TAG_ID);
        int pos_name = cursorScan.getColumnIndex(DbConstantClass.TAG_VENDOR_NAME);
        int pos_shop_name = cursorScan.getColumnIndex(DbConstantClass.TAG_SHOP_NAME);
        int pos_most_selling_food = cursorScan.getColumnIndex(DbConstantClass.TAG_MOST_SELLING_FOOD);
        int pos_contact_no = cursorScan.getColumnIndex(DbConstantClass.TAG_CONTACT_NO);
        int pos_address = cursorScan.getColumnIndex(DbConstantClass.TAG_ADDRESS);
        int pos_latitude = cursorScan.getColumnIndex(DbConstantClass.TAG_LATITUDE);
        int pos_longitude = cursorScan.getColumnIndex(DbConstantClass.TAG_LONGITUDE);
        int pos_hygiene_rating = cursorScan.getColumnIndex(DbConstantClass.TAG_HYGIENE_RATING);
        int pos_taste_rating = cursorScan.getColumnIndex(DbConstantClass.TAG_TASTE_RATING);

        for(cursorScan.moveToFirst(); !(cursorScan.isAfterLast()); cursorScan.moveToNext()){
            VendorBasicInfoItem _item = new VendorBasicInfoItem();
            _item.setVendorId(cursorScan.getInt(pos_id));
            _item.setVendorName(cursorScan.getString(pos_name));
            _item.setShopName(cursorScan.getString(pos_shop_name));
            _item.setMostSellingFood(cursorScan.getString(pos_most_selling_food));
            _item.setContactNo(cursorScan.getString(pos_contact_no));
            _item.setVendorAddress(cursorScan.getString(pos_address));
            _item.setLatitude(cursorScan.getString(pos_latitude));
            _item.setLongitude(cursorScan.getString(pos_longitude));
            _item.setHygieneRating(cursorScan.getString(pos_hygiene_rating));
            _item.setTasteRating(cursorScan.getString(pos_taste_rating));

            vendorBasicInfoItems.add(_item);
        }
        return vendorBasicInfoItems;
    }

    public ArrayList<MenuItem> getMenuList(ArrayList<MenuItem> menuList) {
        // TODO Auto-generated method stub
        menuList.clear();
        Cursor c;
        c = dbSqLiteDatabase.rawQuery("select * from " + DbConstantClass.TAG_TB_VENDOR_FOOD_MENU + " where " +
                "vendor_id = " + _pref.getSession(ConstantClass.TAG_INSERTED_VENDOR_ID), null);
        int row_id_pos = c.getColumnIndex(DbConstantClass.TAG_FOOD_ID);
        int foodName_pos = c.getColumnIndex(DbConstantClass.TAG_FOOD_NAME);
        int foodValue_pos = c.getColumnIndex(DbConstantClass.TAG_FOOD_PRICE);
        for (c.moveToFirst(); !(c.isAfterLast()); c.moveToNext()) {
            MenuItem menuItem = new MenuItem();
            menuItem.setFoodName(c.getString(foodName_pos));
            menuItem.setFoodValue(c.getString(foodValue_pos));
            menuList.add(menuItem);
            Log.v("ShipmentList", String.valueOf(menuList.size()));
        }
        c.close();
        return menuList;
    }

    public ArrayList<ImageItem> getImages(ArrayList<ImageItem> imageList) {
        // TODO Auto-generated method stub
        imageList.clear();
        AddVendorImagesActivity.dataT.clear();
        Cursor c;
        String v_id =_pref.getSession(ConstantClass.TAG_INSERTED_VENDOR_ID);
        c = dbSqLiteDatabase.rawQuery("select * from " + DbConstantClass.TAG_TB_VENDOR_IMAGE /*+ " where " +
                "vendor_id = " + v_id*/, null);
        int imagePath_pos = c.getColumnIndex(DbConstantClass.TAG_IMAGE_PATH);
        int i=0;
        for (c.moveToFirst(); !(c.isAfterLast()); c.moveToNext()) {
            ImageItem _item = new ImageItem();
            _item.setId(c.getInt(c.getColumnIndex("id")));
            _item.setImagePath(c.getString(imagePath_pos));
            imageList.add(_item);
            CustomGallery _customGalleryItem = new CustomGallery();
            _customGalleryItem.sdcardPath = c.getString(imagePath_pos);
            _customGalleryItem.selectedImageId = c.getInt(c.getColumnIndex("id"));
            AddVendorImagesActivity.dataT.add(_customGalleryItem);
            Log.v("sdcardPath", AddVendorImagesActivity.dataT.get(i).sdcardPath);
            i++;
        }
        c.close();
        return imageList;
    }

    public boolean deleteSelectedImage(int imageId) {
        return dbSqLiteDatabase.delete(DbConstantClass.TAG_TB_VENDOR_IMAGE, DbConstantClass.TAG_ID +
        "=" + imageId, null) > 0;
    }

    public ArrayList<VideoItem> getVideo(ArrayList<VideoItem> videoList) {
        // TODO Auto-generated method stub
        videoList.clear();
        Cursor c;
        c = dbSqLiteDatabase.rawQuery("select * from " + DbConstantClass.TAG_TB_VENDOR_VIDEO + " where " +
                "vendor_id = " + _pref.getSession(ConstantClass.TAG_INSERTED_VENDOR_ID), null);
        int videoPath_pos = c.getColumnIndex(DbConstantClass.TAG_VIDEO_PATH);
        for (c.moveToFirst(); !(c.isAfterLast()); c.moveToNext()) {
            VideoItem _item = new VideoItem();
            _item.setVideoPath(c.getString(videoPath_pos));
            videoList.add(_item);
            Log.v("video List", String.valueOf(videoList.size()));
        }
        c.close();
        return videoList;
    }
}
