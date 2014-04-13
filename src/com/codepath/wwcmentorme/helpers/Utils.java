package com.codepath.wwcmentorme.helpers;

import java.io.IOException;
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
	
	public static double getDistance(double lat1, double lon1, double lat2, double lon2) {
	      double theta = lon1 - lon2;
	      double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
	      dist = Math.acos(dist);
	      dist = rad2deg(dist);
	      dist = dist * 60 * 1.1515;
	      return round(dist, 1);
	}
	
	/*::  This function converts decimal degrees to radians             :*/
	private static double deg2rad(double deg) {
	      return (deg * Math.PI / 180.0);
	}
	
	/*::  This function converts radians to decimal degrees             :*/
	private static double rad2deg(double rad) {
	      return (rad * 180.0 / Math.PI);
	}
	
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    long factor = (long) Math.pow(10, places);
	    value = value * factor;
	    long tmp = Math.round(value);
	    return (double) tmp / factor;
	}
	
	public static String formatDouble(double d)
	{
	    if(d < 1) {
	    	return Double.toString(d).substring(Double.toString(d).indexOf("."));
	    } else {
	    	return Double.toString(d);
	    }
	}
}