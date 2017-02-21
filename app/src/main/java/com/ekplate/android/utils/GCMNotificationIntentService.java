package com.ekplate.android.utils;

import android.app.ActivityManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.ekplate.android.R;
import com.ekplate.android.activities.menumodule.NotificationActivity;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GCMNotificationIntentService extends IntentService {

	String TAG = "Service Class";
	Context _mContext;

	public static final int NOTIFICATION_ID = 1;

	NotificationCompat.Builder builder;

	public static final String NOTIFICATION_POINTS =
            "com.ekplate.apk.utils.GCMNotificationIntentService";
	public static final String RESULT = "result";

	private void publishResults(Bundle _extras, int result) {
		Intent intent = new Intent(NOTIFICATION_POINTS);
		intent.putExtras(_extras);
		sendBroadcast(intent);
	}

	public GCMNotificationIntentService() {
		super("GCMNotificationIntentService");
		_mContext = this;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		Log.v("Push String", extras.toString());
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		// The getMessageType() intent parameter must be the intent you received
		// in your BroadcastReceiver.
		String messageType = gcm.getMessageType(intent);
		// Log.v("", msg)

		if (!extras.isEmpty()) { // has effect of unparcelling Bundle
			/*
			 * Filter messages based on message type. Since it is likely that
			 * GCM will be extended in the future with new message types, just
			 * ignore any message types you're not interested in, or that you
			 * don't recognize.
			 */
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
					.equals(messageType)) {
				// sendNotification("Send error: " + extras.toString());
				Log.e("GCM Send error:", extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
					.equals(messageType)) {
				// sendNotification("Deleted messages on server: "+
				// extras.toString());
				// If it's a regular GCM message, do some work.

				Log.e("GCM Deleted msg on:", extras.toString());

			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
					.equals(messageType)) {

				// checking if app is in foreground or not

				ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
				// List<RunningTaskInfo> taskInfo = am.getRunningTasks(1);
				// Log.d("current task :", "CURRENT Activity ::" +
				// taskInfo.get(0).topActivity.getClass().getSimpleName());
				// ComponentName componentInfo = taskInfo.get(0).topActivity;

				// Post notification of received message.
				try {

					String type = extras.getString("type");
					String message = extras.getString("message");
					Log.e("App Running Stat", "In background");
					sendNotification("Ekplate", message, type);

				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}

				Log.i(TAG, "Received: " + extras.toString());

			}
		}
		// Release the wake lock provided by the WakefulBroadcastReceiver.

		// GcmBroadcastReceiver.completeWakefulIntent(intent);

	}

	// Put the message into a notification and post it.
	// a GCM message.
	@SuppressWarnings("deprecation")
	private void sendNotification(String title, String msg, String type) {

		NotificationCompat.Builder notification = new NotificationCompat.Builder(
				this);
		notification.setContentTitle(title);
		notification.setContentText(msg);
		notification.setSmallIcon(R.drawable.ic_launcher);
		notification.setAutoCancel(true);
		notification.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);

		Intent launchIntent = new Intent(this, NotificationActivity.class);
		launchIntent.setAction(Intent.ACTION_VIEW); // necessary for extras to
													// be passed on to Action
		notification.setContentIntent(PendingIntent.getActivity(this,
				(int) (Math.random() * 100), launchIntent,
				PendingIntent.FLAG_UPDATE_CURRENT));

		NotificationManager nm = (NotificationManager) this
				.getSystemService(NOTIFICATION_SERVICE);
		nm.notify((int) (Math.random() * 100), notification.getNotification());

		stopSelf();

	}

}