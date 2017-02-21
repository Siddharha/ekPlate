package com.ekplate.android.adapters.addvendormodule;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ekplate.android.models.addvendormodule.MenuItem;

import java.util.ArrayList;

/**
 * Created by Avishek on 10/13/2015.
 */
public class DbAdapter {

    static final String KEY_ROW_ID = "id";

    static final String KEY_FoodName = "foodName";
    static final String KEY_FoodValue = "foodValue";

    static final String TAG = "DbAdapter";
    static final String DATABASE_NAME = "EkplateVendorMenuDB";
    static final String DATABASE_TABLE = "VendorMenuTable";
    static final int DATABASE_VERSION = 1;

    static final String DATABASE_CREATE_TABLE = "create table VendorMenuTable (id integer primary key autoincrement, foodName text not null, foodValue text not null);";

    final Context context;

    DatabaseHelper DBHelper;

    SQLiteDatabase db;

    String[] col = new String[] { KEY_ROW_ID, KEY_FoodName, KEY_FoodValue};

    public DbAdapter(Context ctx) {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    // ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(DATABASE_CREATE_TABLE);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            Log.v(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");

            db.execSQL("DROP TABLE IF EXISTS VendorMenuTable");

            onCreate(db);
        }

        @Override
        public void onDowngrade(SQLiteDatabase db, int oldVersion,
                                int newVersion) {

            Log.v(TAG, "Downgrading database from version " + oldVersion
                    + " to " + newVersion + ", which will destroy all old data");

            db.execSQL("DROP TABLE IF EXISTS VendorMenuTable");

            onCreate(db);
        }

    }

    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // ---opens the database---
    public DbAdapter open() throws SQLException {

        DBHelper = new DatabaseHelper(context);

        db = DBHelper.getWritableDatabase();

        return this;

    }

    // ---closes the database---
    public void close() {

        DBHelper.close();

    }

    // ---insert a contact into the database---
    public long insertValue(String foodName, String foodValue) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_FoodName, foodName);
        initialValues.put(KEY_FoodValue, foodValue);
        return db.insert(DATABASE_TABLE, null, initialValues);
    }

    // ---deletes all record---
    public boolean deleteAllRecord() {

        return db.delete(DATABASE_TABLE, null, null) > 0;

    }

    // ---retrieves all records---

    public ArrayList<MenuItem> getMenuList() {
        // TODO Auto-generated method stub

        ArrayList<MenuItem> menuList = new ArrayList<MenuItem>();
        menuList.clear();

        Cursor c;
        c = db.rawQuery("select * from VendorMenuTable" , null);

        int row_id_pos = c.getColumnIndex(KEY_ROW_ID);
        int foodName_pos = c.getColumnIndex(KEY_FoodName);
        int foodValue_pos = c.getColumnIndex(KEY_FoodValue);

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

}

