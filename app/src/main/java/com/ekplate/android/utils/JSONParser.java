package com.ekplate.android.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;

public class JSONParser {
	static InputStream is = null;
	static JSONObject jObj = null;
	static String json = "";

	public  String baseURL = "http://mo.bluehorse.in/proximity/admin/index.php/candidate/";
	public  String webservicePostURL="http://mo.bluehorse.in/proximity/admin/index.php/candidate/";

	public String _URL = baseURL ;

	private Context ctx;
	private static final String TAG = "LOGTAG";

	public enum AndroidVersion {
		HC, ICS, JB, UNKNOWN
	};

	public static AndroidVersion getAndroidVersion() {

		int sdk = android.os.Build.VERSION.SDK_INT;
		if (11 <= sdk && sdk <= 13) {
			Log.v(TAG, "We are running on HoneyComb");
			return AndroidVersion.HC;
		} else if (14 <= sdk && sdk <= 15) {
			Log.v(TAG, "We are running on IceCreamSandwich");
			return AndroidVersion.ICS;
		} else if (16 == sdk) {
			Log.v(TAG, "We are running on JellyBean");
			return AndroidVersion.JB;
		} else {
			Log.v(TAG, "We don't know what we are running on");
			return AndroidVersion.UNKNOWN;
		}
	}

	public void appedndWithUrl(String parts) {
		_URL = _URL + "?" + parts;
		Log.v("FINAL URL", _URL);
	}

	public static boolean haveInternet(Context ctx) {

		NetworkInfo info = ((ConnectivityManager) ctx
				.getSystemService(Context.CONNECTIVITY_SERVICE))
				.getActiveNetworkInfo();

		if (info == null || !info.isConnected()) {
			return false;
		}
		if (info.isRoaming()) {
			// here is the roaming option you can change it if you want to
			// disable internet while roaming, just return false
			return true;
		}
		return true;
	}

	public JSONParser(Context ctx) {
		this.ctx = ctx;
	}

	// constructor
	public JSONParser(Activity activity) {
		this.ctx = activity.getApplicationContext();
	}

	public void showError(String msg) {
		if (msg.length() > 0) {
			try {
				Toast.makeText(ctx, msg + "", Toast.LENGTH_SHORT).show();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	public JSONObject getJSONFromUrl() {
		String url = _URL;

		try {
			URL __url = new URL(_URL);
			URI __uri = new URI(__url.getProtocol(), __url.getHost(),
					__url.getPath(), __url.getQuery(), null);
			url = __uri.toString();
		} catch (Exception e) {
			// TODO: handle exception
		}
		// Making HTTP request
		try {
			// defaultHttpClient

			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);

			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			// showError(e.getMessage());
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			// showError("Network Failure !");
		} catch (IOException e) {
			e.printStackTrace();
			// showError("Network Failure !");
		}

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			json = sb.toString();
		} catch (Exception e) {
			Log.e("Buffer Error", "Error converting result " + e.toString());
		}

		// try parse the string to a JSON object
		try {
			jObj = new JSONObject(json);
		} catch (JSONException e) {
			Log.e("JSON Parser", "Error parsing data " + e.toString());
			try {
				jObj = new JSONObject("{\"lost\":0}");
			} catch (Exception ex) {

			}
		}
		Log.v("RAWSTRING", json);

		// return JSON String
		return jObj;

	}

	public static JSONObject getJSONFromUrl(String youtube_url) {
		String url = youtube_url;

		try {
			URL __url = new URL(youtube_url);
			URI __uri = new URI(__url.getProtocol(), __url.getHost(),
					__url.getPath(), __url.getQuery(), null);
			url = __uri.toString();
		} catch (Exception e) {
			// TODO: handle exception
		}
		// Making HTTP request
		try {
			// defaultHttpClient

			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);

			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			// showError(e.getMessage());
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			// showError("Network Failure !");
		} catch (IOException e) {
			e.printStackTrace();
			// showError("Network Failure !");
		}

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			json = sb.toString();
			Log.e("RESPONSE",json);
		} catch (Exception e) {
			Log.e("Buffer Error", "Error converting result " + e.toString());
		}

		// try parse the string to a JSON object
		try {
			jObj = new JSONObject(json);
		} catch (JSONException e) {
			Log.e("JSON Parser", "Error parsing data " + e.toString());
			try {
				jObj = new JSONObject("{\"lost\":0}");
			} catch (Exception ex) {

			}
		}
		Log.v("RAWSTRING", json);

		// return JSON String
		return jObj;

	}

	public String getWebservicePostURL() {
		return webservicePostURL;
	}

	public void setWebservicePostURL(String webservicePostURL) {
		this.webservicePostURL = webservicePostURL;
	}
	
	
}
