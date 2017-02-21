package com.ekplate.android.utils;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;


public class GcmBroadcastReceiver extends BroadcastReceiver {
	
	private SharedPreferences.Editor _pEditor;
	@Override
	public void onReceive(Context context, Intent intent) {
		ComponentName comp = new ComponentName(context.getPackageName(),
				GCMNotificationIntentService.class.getName());
		//startWakefulService(context, (intent.setComponent(comp)));
		context.startService(intent.setComponent(comp));
		//setResultCode(Activity.RESULT_OK);
		_pEditor = new Pref(context).getSharedPreferencesEditorInstance();
		_pEditor.putString("noitice", "true");
		_pEditor.commit();
		//Toast.makeText(context, "StallChat Receive notification" ,Toast.LENGTH_LONG).show();
	}
}
