package com.codepath.wwcmentorme.helpers;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Utils {
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	private Utils() {
		// Don't new me
	}

	public static <T> T fromJson(Class<T> type, String jsonObjString) {
		assert (type != null);
		T result = null;
		try {
			result = OBJECT_MAPPER.readValue(jsonObjString, type);
		} catch (IOException e) {
			Log.e("ERROR", String.format("Could not deserialize to type %s", type));
		}
		return result;
	}

	public static <T> T fromJson(Class<T> type, JSONObject jsonObject) {
		return fromJson(type, jsonObject.toString());
	}

	public static <E> ArrayList<E> fromJson(Class<E> type, JSONArray jsonArray) {
		ArrayList<E> results = new ArrayList<E>();
		for (int i = 0; i < jsonArray.length(); i++) {
			try {
				E item = fromJson(type, jsonArray.getJSONObject(i).toString());
				if (item != null) {
					results.add(item);
				}
			} catch (JSONException e) {
				Log.w("WARN", String.format("Cannot get jsonObject at index %s from jsonArray", i), e);
				continue;
			}
		}
		return results;
	}

	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null
				&& activeNetworkInfo.isConnectedOrConnecting();
	}
}