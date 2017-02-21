package com.ekplate.android.utils;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class AsyncTaskMain extends AsyncTask<String, Long, String> {

	AsyncTaskListener listener;
	HashMap<String, PostObject> map;
	private DefaultHttpClient mHttpClient;
	private String _data = "";
	private Activity _activity;
	private boolean useArrayList = false;
	private ArrayList<String> keys;
	private ArrayList<String> values;
	
	String postedUrl="";
	boolean isSetPostedUrl=false;
	
	String calling_method="";
	boolean isSetCallingMethod=false;
	
	
	public String getCalling_method() {
		return calling_method;
	}

	public void setCalling_method(String calling_method) {
		this.calling_method = calling_method;
		setSetCallingMethod(true);
	}

	public boolean isSetCallingMethod() {
		return isSetCallingMethod;
	}

	public void setSetCallingMethod(boolean isSetCallingMethod) {
		this.isSetCallingMethod = isSetCallingMethod;
	}

	public boolean isSetPostedUrl() {
		return isSetPostedUrl;
	}

	public void setSetPostedUrl(boolean isSetPostedUrl) {
		this.isSetPostedUrl = isSetPostedUrl;
	}

	public void setPostedUrl(String _postedUrl){
		postedUrl=_postedUrl;
		setSetPostedUrl(true);		
	}
	
	public String getPostedUrl(){
		return postedUrl;
	}
	
	

	public AsyncTaskMain(AsyncTaskListener listener,
			HashMap<String, PostObject> map, Activity _activity) {
		this.listener = listener;
		this.map = map;
		serverCommunication();
		this._activity = _activity;
	}

	public AsyncTaskMain(AsyncTaskListener listener, ArrayList<String> keys,
			ArrayList<String> values, Activity _activity) {
		this.listener = listener;
		this.keys = keys;
		this.values = values;
		serverCommunication();
		this._activity = _activity;
		useArrayList = true;
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		if (!JSONParser.haveInternet(_activity)) {
			/*CommonFunction _commFunction = new CommonFunction(_activity);
			_commFunction.showToastMessage("Oops! No Internet Connection!",
					_commFunction.TOAST_LENGTH_SHORT);*/
		}
		if (listener!=null)
		listener.onTaskPreExecute();
	}

	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		if (listener!=null)
		listener.onTaskCompleted(this._data);
		// Log.d("RESPONSE", this._data);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
		String baseUrl = new JSONParser(_activity).getWebservicePostURL();
		
		if(isSetPostedUrl() && (getPostedUrl().length()>0)){
			baseUrl=getPostedUrl();
		}
		
		if(isSetCallingMethod() && (getCalling_method().length()>0)){
			baseUrl=baseUrl+getCalling_method();
		}
		
		Log.v("POSTEDURL", baseUrl);
		HttpPost _httpPost = new HttpPost(baseUrl);
		MultipartEntity multipartEntity = new MultipartEntity(
				HttpMultipartMode.BROWSER_COMPATIBLE);

		if (!useArrayList) {

			@SuppressWarnings("rawtypes")
			Iterator it = map.entrySet().iterator();
			// Log.e("COUNT", map.entrySet().size() + "");
			while (it.hasNext()) {
				Entry<String, PostObject> eachPair = (Entry<String, PostObject>) it
						.next();
				String key = eachPair.getKey();
				try {
					if (eachPair.getValue().isFileAvailable()) {
						File _file = new File(eachPair.getValue().getFile_url());
						multipartEntity.addPart(key, new FileBody(_file));
					} else {
						multipartEntity.addPart(key, new StringBody(eachPair
								.getValue().getString_value()));
					}
					_httpPost.setEntity(multipartEntity);

				} catch (Exception e) {

				}
			}
		} else {
			try {
				for (int i = 0; i < keys.size(); i++) {
					String eachkey = keys.get(i);
					String eachVal = values.get(i);
					multipartEntity.addPart(eachkey, new StringBody(eachVal));
				}
				_httpPost.setEntity(multipartEntity);
			} catch (Exception e) {

			}
		}
		try {
			mHttpClient.execute(_httpPost, new WebserviceResponseHandler());
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	@SuppressWarnings("rawtypes")
	private class WebserviceResponseHandler implements ResponseHandler {

		@Override
		public Object handleResponse(HttpResponse response)
				throws ClientProtocolException, IOException {
			// TODO Auto-generated method stub
			HttpEntity r_entity = response.getEntity();
			String responseString = EntityUtils.toString(r_entity);
			_data = responseString;
			return null;
		}

	}

	public void serverCommunication() {
		HttpParams params = new BasicHttpParams();
		params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
				HttpVersion.HTTP_1_1);
		mHttpClient = new DefaultHttpClient(params);
	}

}
