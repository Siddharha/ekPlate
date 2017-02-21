package com.ekplate.android.utils;

import com.ekplate.android.models.vendormodule.AddCollegeItem;
import com.ekplate.android.models.vendormodule.VendorItem;

import java.util.ArrayList;

/**
 * Created by Rahul on 9/28/2015.
 */
public class ConstantClass {
    public static final String TAG_SELECTED_CITY_ID = "selected_city_id";
    //public static final String BASE_URL = "http://uat.ekplate.com/api/v1/";
    public static String BASE_URL;
    public static final String BASE_URL_DEF = "http://api.ekplate.com/api/v1/";
    public static final String BASE_URL_V2 = "http://api.ekplate.com/api/v2/";
    public static final String ACCESS_TOKEN = "accessToken";
    public static final String FIRST_USER = "FirstTimeUser" ;
    public static final String TAG_USER_NAME = "user_name";
    public static final String TAG_USER_DP_IMAGE = "user_profile_pics";
    public static final String TAG_USER_PHONE_NO = "user_phone_no";
    public static final String TAG_USER_EMAIL_ID = "user_email_id";
    public static final String TAG_LATITUDE = "latitude";
    public static final String TAG_LONGITUDE = "longitude";
    public static final String TAG_CURRENT_CITY_LOCATION = "current_city";
    public static final String TAG_SELECTED_CITY_LOCATION = "selected_city";
    public static final String TAG_ADD_VENDOR_BASIC_INFO_FLAG = "add_vendor_basic_info_flag";
    public static final String TAG_ADD_VENDOR_SUBMIT_FLAG = "add_vendor_submit_flag";
    public static final String TAG_ADD_VENDOR_RATING_FLAG = "add_vendor_rating_flag";
    public static final String TAG_ADD_VENDOR_LOCATION_FLAG = "add_vendor_location_flag";
    public static final String TAG_ADD_VENDOR_IMAGE_VIDEO_FLAG = "add_vendor_image_video_flag";
    public static final String TAG_ADD_VENDOR_MENU_FLAG = "add_vendor_menu_flag";
    public static final String TAG_SELECTED_COUNTRY_LOCATION = "selected_country";
    public static final String TAG_SELECTED_FOOD_TYPE = "food_type";
    public static final String TAG_HOME_MENU_JSON_FILE_PATH = "home_menu_path";
    public static final String TAG_HOME_MENU_JSON_FILE = "home_menu.json";
    public static final String TAG_INSERTED_VENDOR_ID = "inserted_vendor_id";
    public static final String TAG_NOTIFICATION_ENABLED = "notification_enabled";
    public static final String TAG_VENDOR_DETAILS_LIKE_COUNT = "vendor_details_like_count_";
    public static final String TAG_VENDOR_DETAILS_BOOKMARK_COUNT = "vendor_details_bookmark_count_";
    public static final String TAG_VENDOR_DETAILS_CHECK_IN_COUNT = "vendor_details_check_in_count_";
    public static final String TAG_GALLERY_IMAGE_LIKE_STATUS = "vendor_gallary_image_like_status_";
    public static final String TAG_GALLERY_IMAGE_LIKE_COUNT = "vendor_gallary_image_like_count_";
    public static final String TAG_USER_REVIEW_AGREE_FLAG = "user_review_agree_flag_";
    public static final String TAG_USER_REVIEW_AGREE_COUNT = "user_review_agree_count_";
    public static final String TAG_USER_REVIEW_AGREE_STATUS = "user_review_agree_status_";
    public static final String TAG_RECENT_SEARCHES_FIRST = "recent_searches_first";
    public static final String TAG_RECENT_SEARCHES_SECOND = "recent_searches_second";
    public static final String TAG_RECENT_SEARCHES_THIRD = "recent_searches_third";
    public static final String TAG_RECENT_SEARCHES_FOURTH = "recent_searches_fourth";
    public static final String TAG_RECENT_TOP_INDEX = "recent_top_index";
    public static final String TAG_RECENT_BOTTOM_INDEX = "recent_bottom_index";
    public static final String TAG_SELECTED_CITY_STATUS = "select_city_status";
    public static final String TAG_LOGIN_TYPE = "loginType";
    public static ArrayList<AddCollegeItem> addCollegeItems = new ArrayList<>();
    public static String TAG_SELECTED_PROFILE_IMAGE = "";
    public static String TAG_CAPTURED_IMAGE_PATH = "";
    public static ArrayList<VendorItem> vendorItemsList = new ArrayList<>();


    public static String HINT_STATUS="hint_status";
}
