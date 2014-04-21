package com.codepath.wwcmentorme.helpers;

import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import com.codepath.wwcmentorme.activities.ViewProfileActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class MentorMeReceiver extends BroadcastReceiver {
	private static final String TAG = "com.codepath.wwcmentorme.helpers.MentorMeReceiver";

	public static final String intentAction = "SEND_PUSH";

	@Override
	public void onReceive(Context context, Intent intent) {
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
							Intent pupInt = new Intent(context, ViewProfileActivity.class);							
							pupInt.putExtra(ViewProfileActivity.USER_ID_KEY, json.getLong(key));
							pupInt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							context.getApplicationContext().startActivity(pupInt);

							LocalBroadcastManager.getInstance(context)
									.sendBroadcast(new Intent(intentAction));
						}
					}
				}
			}

		} catch (JSONException e) {
			Log.d(TAG, "JSONException: " + e.getMessage());
		}
	}

}
