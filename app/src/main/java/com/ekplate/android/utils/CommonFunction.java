package com.ekplate.android.utils;

import java.io.IOException;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class CommonFunction {
	private Context mctx;
	private Activity _activity;
	
	
	private Pref _spref;

	GoogleCloudMessaging gcm;
	private String regId;
	
	private static final String GOOGLE_PROJECT_ID = "715381946929";
	public static final String REG_ID = "regId";
	private static final String APP_VERSION = "appVersion";

	public CommonFunction(Context ctx) {
		// TODO Auto-generated constructor stub
		this.mctx = ctx;
		this._activity = (Activity) mctx;
		_spref= new Pref(mctx);

	}

	public Pref getPrefInstance() {
		return _spref;
	}


	public String getSrcString(int strRes) {
		return _activity.getResources().getString(strRes);
	}

	public void showToastMessage(String message, int toastLength) {
		Toast.makeText(mctx, message, toastLength).show();
	}

	public void callPostWebservice(String method,
			HashMap<String, PostObject> postMap, AsyncTaskListener asyncListner) {
		AsyncTaskMain _asyncTask = new AsyncTaskMain(asyncListner, postMap,
				_activity);
		_asyncTask.setCalling_method(method);
		_asyncTask.execute("");
	}

	
	public void callPostWebservice(String methodOrUrl,
			HashMap<String, PostObject> postMap, AsyncTaskListener asyncListner,boolean isUrl) {
		AsyncTaskMain _asyncTask = new AsyncTaskMain(asyncListner, postMap,
				_activity);
		if(isUrl){
			_asyncTask.setPostedUrl(methodOrUrl);
		}else {
			_asyncTask.setCalling_method(methodOrUrl);
		}
		
		_asyncTask.execute("");
	}
	
	public PostObject getPostObject(String value, boolean isFile) {
		PostObject _postObject = new PostObject();
		_postObject.setFile(isFile);
		if (isFile) {
			_postObject.setFile_url(value);
		}
		_postObject.setString_value(value);
		return _postObject;
	}
	
	
	private String getRegistrationId(Context context) {
		// final SharedPreferences prefs = getSharedPreferences(
		// SplashActivity.class.getSimpleName(), Context.MODE_PRIVATE);

		String registrationId = getPrefInstance().getSession(REG_ID);
		if (registrationId.equalsIgnoreCase("")) {
			Log.i("getRegistrationId", "Registration not found.");
			return "";
		}
		int registeredVersion = getPrefInstance().getIntegerSession(
				"APP_VERSION");
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion) {
			Log.i("getRegistrationId", "App version changed.");
			return "";
		}
		return registrationId;
	}

	private static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			Log.d("RegisterActivity",
					"I never expected this! Going down, going down!" + e);
			throw new RuntimeException(e);
		}
	}

	private void registerInBackground() {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(mctx);
					}
					regId = gcm.register(GOOGLE_PROJECT_ID);
					Log.d("RegisterActivity", "registerInBackground - regId: "
							+ regId);
					msg = "Device registered, registration ID=" + regId;

					storeRegistrationId(mctx, regId);
				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
					Log.d("RegisterActivity", "Error: " + msg);
				}
				Log.d("RegisterActivity", "AsyncTask completed: " + msg);
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				// Toast.makeText(getApplicationContext(),
				// "Registered with GCM Server." + msg, Toast.LENGTH_LONG)
				// .show();
			}
		}.execute(null, null, null);
	}

	// storing gcm,deviceid, and app version
	private void storeRegistrationId(Context context, String regId) {
		final SharedPreferences prefs = getPrefInstance().getSharedPreferencesInstance();

		//TelephonyManager tManager = (TelephonyManager) mctx.getSystemService(Context.TELEPHONY_SERVICE);
		//String device_id = tManager.getDeviceId();

		int appVersion = getAppVersion(context);
		Log.i("storeRegistrationId", "Saving regId on app version "
				+ appVersion);

		getPrefInstance().setSession(REG_ID, regId);
		//getPrefInstance().setSession("deviceId", device_id);
		getPrefInstance().setSession("APP_VERSION", appVersion);

	}

	public String registerGCM() {

		gcm = GoogleCloudMessaging.getInstance(mctx);
		regId = getRegistrationId(mctx);

		if (TextUtils.isEmpty(regId)) {

			registerInBackground();

			Log.d("RegisterActivity","registerGCM - successfully registered with GCM server - regId: "+ regId);
		} else {
			// Toast.makeText(getApplicationContext(),
			// "RegId already available. RegId: " + regId,
			// Toast.LENGTH_LONG).show();
		}
		return regId;
	}
	
	public String getGCMRegistrationId(){
		
		if (TextUtils.isEmpty(regId)) {
			regId = registerGCM();
			Log.i("RegisterActivity", "GCM RegId: " + regId);			
		} else {
			Log.i("RegisterActivity", "GCM RegId: " + regId);
		}
		
		return regId;
	}

	
}
