package com.ekplate.android.utils;

public interface AsyncTaskListener {
	
	public void onTaskCompleted(String result);
	public void onTaskPreExecute();
}
