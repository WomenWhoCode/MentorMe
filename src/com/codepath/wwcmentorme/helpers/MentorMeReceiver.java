package com.codepath.wwcmentorme.helpers;

import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.codepath.wwcmentorme.R;
import com.codepath.wwcmentorme.activities.ChatActivity;
import com.codepath.wwcmentorme.activities.ViewProfileActivity;
import com.codepath.wwcmentorme.data.DataService;
import com.codepath.wwcmentorme.models.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BinaryHttpResponseHandler;

public class MentorMeReceiver extends BroadcastReceiver {
	private static final String TAG = "MentorMeReceiver";

	public static final String intentAction = "SEND_PUSH";
	public static final String alertKey = "username";
	public static final String skillsKey = "skills";
	public static final String responseKey = "inresponse";
	public static final String messageKey = "message";

	@Override
	public void onReceive(final Context context, final Intent intent) {
		try {
			if (intent == null) {
				Log.d(TAG, "Receiver intent null");
			} else {
				String action = intent.getAction();
				if (action.equals(intentAction)) {
					JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));
					Iterator<String> itr = json.keys();
					while (itr.hasNext()) {
						String key = (String) itr.next();
						if (key.equals(ViewProfileActivity.USER_ID_KEY)) {
							final long userId = json.getLong(key);
							final boolean inResponse = json.getBoolean(responseKey);
							final int notificationId = (int)userId;
							final String username = json.getString(alertKey);
							String jsonChatMessage = null;
							if (json.has(messageKey)) {
								jsonChatMessage = json.getString(messageKey);
							}
							final String chatMessage = jsonChatMessage;
							final String message = chatMessage != null ? chatMessage : (username + (inResponse ? " has accepted your request." : " has requested to connect with you."));
							final String skills = json.getString(skillsKey);
							final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
							final NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle()
							.setBigContentTitle(username).bigText(message)
							.setSummaryText(skills);
							final AsyncHttpClient client = new AsyncHttpClient();
							client.get(User.getProfileImageUrl(userId, 200), new BinaryHttpResponseHandler() {
								@Override
								public void onSuccess(byte[] fileData) {
									final Bitmap icon = UIUtils.decode(fileData);
									Intent pupInt = new Intent(context, chatMessage != null ? ChatActivity.class : ViewProfileActivity.class);
									pupInt.putExtra(ViewProfileActivity.USER_ID_KEY, userId);
									if (!inResponse && chatMessage == null) {
										DataService.addResponsePending(userId);
									}
									int requestID = (int) System.currentTimeMillis();
									pupInt.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP); 
									PendingIntent contentIntent = PendingIntent.getActivity(context, requestID, pupInt, PendingIntent.FLAG_UPDATE_CURRENT);
									final NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
									.setSmallIcon(R.drawable.ic_launcher)
									.setContentTitle(username)
									.setContentText(message)
									.setOnlyAlertOnce(true)
									.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
									.setAutoCancel(true)
									.setStyle(style)
									.setLargeIcon(icon)
									.setContentIntent(contentIntent);
									notificationManager.notify(notificationId, builder.build());
								}
							});
						}
					}
				}
			}

		} catch (JSONException e) {
			Log.d(TAG, "JSONException: " + e.getMessage());
		}
	}

}
