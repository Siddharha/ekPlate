package com.ekplate.android.localdbconfig;

/**
 * Created by user on 26-11-2015.
 */
public class DbConstantClass {
    public static final String TAG_DB_NAME = "db_ekplate";
    public static final int TAG_DB_VERSION = 1;
    public static final String TAG_TB_VENDOR_BASIC_INFO = "tb_vendor_basic_info";
    public static final String TAG_TB_VENDOR_FOOD_MENU = "tb_vendor_food_menu";
    public static final String TAG_TB_VENDOR_IMAGE = "tb_vendor_image";
    public static final String TAG_TB_VENDOR_VIDEO = "tb_vendor_video";
    public static final String TAG_TB_USER_PROF = "tb_user_prof";

    public static final String TAG_ID = "id";
    public static final String TAG_BASIC_VENDOR_ID = "vendor_id";
    public static final String TAG_VENDOR_NAME = "vendor_name";
    public static final String TAG_SHOP_NAME = "shop_name";
    public static final String TAG_MOST_SELLING_FOOD = "most_selling_food";
    public static final String TAG_CONTACT_NO = "contact_no";
    public static final String TAG_ADDRESS = "address";
    public static final String TAG_LATITUDE = "latitude";
    public static final String TAG_LONGITUDE = "longitude";
    public static final String TAG_HYGIENE_RATING = "hygiene_rating";
    public static final String TAG_TASTE_RATING = "taste_rating";

    public static final String TAG_FOOD_ID = "id";
    public static final String TAG_FOOD_NAME = "food_name";
    public static final String TAG_FOOD_PRICE = "food_price";
    public static final String TAG_VENDOR_ID = "vendor_id";

    public static final String TAG_IMAGE_ID = "id";
    public static final String TAG_IMAGE_CAPTION = "image_caption";
    public static final String TAG_IMAGE_PATH = "image_path";
   // public static final String TAG_IMAGE_VENDOR_ID = "vendor_id";

    public static final String TAG_VIDEO_ID = "id";
    public static final String TAG_VIDEO_CAPTION = "video_caption";
    public static final String TAG_VIDEO_PATH = "video_path";
    public static final String TAG_VIDEO_VENDOR_ID = "vendor_id";

    public static final String TAG_USER_PROF_ID = "id";
    public static final String TAG_USER_ID = "user_id";
    public static final String TAG_VEG_STATUS = "veg_status";

    public static final String TAG_CREATE_VENDOR_BASIC_INFO_TAB = "create table tb_vendor_basic_info" +
            "(id integer primary key autoincrement," +
            " vendor_name text not null," +
            " shop_name text not null," +
            " most_selling_food text not null," +
            " contact_no text not null," +
            " address text," +
            " latitude text," +
            " longitude text," +
            " hygiene_rating text," +
            " taste_rating text," +
            " vendor_id integer not null)";

    public static final String TAG_CREATE_VENDOR_FOOD_MENU_TB = "create table tb_vendor_food_menu" +
            "(id integer primary key autoincrement," +
            " food_name text not null," +
            " food_price text not null," +
            " vendor_id integer not null)";

    public static final String TAG_CREATE_VENDOR_IMAGE_TB = "create table tb_vendor_image" +
            "(id integer primary key autoincrement," +
            " image_path text not null," +
            " image_caption text not null)";

    public static final String TAG_CREATE_VENDOR_VIDEO_TB = "create table tb_vendor_video" +
            "(id integer primary key autoincrement," +
            " video_path text not null," +
            " video_caption text not null," +
            " vendor_id integer not null)";

    public static final String TAG_CREATE_USER_PROF_TB = "create table tb_user_prof" +
            "(id integer primary key autoincrement," +
            " user_id text not null," +
            " veg_status text not null)";
}
