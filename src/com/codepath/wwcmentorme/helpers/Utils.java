package com.codepath.wwcmentorme.helpers;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.AndroidHttpClient;
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
	
	public static String formatNumber(String num) {
		DecimalFormat formatter = new DecimalFormat("#,###");
		Long longNum = Long.parseLong(num);
		if (longNum <= 9999) {
			return formatter.format(longNum);
		}
		int exp = (int) (Math.log(longNum) / Math.log(1000));

		return String.format(Locale.US, "%.1f %c",
				longNum / Math.pow(1000, exp), "kMGTPE".charAt(exp - 1));
	}
	
	// Returns the insertion index for a sorted array "container" by the same comparator.
	public static <V> int insertDeduped(final List<V> container, V object, Comparator<V> comparator) {
		// First we check if the object already exists and we remove it in that case.
		for (int i = 0, count = container.size(); i < count; ++i) {
			final V currentObj = container.get(i);
			int compare = comparator.compare(object, currentObj);
			if (compare == 0) {
				container.remove(i);
				break;
			}
		}
		// We now insert in the right location.
		int insertionIndex = 0;
		for (int i = 0, count = container.size(); i < count; ++i) {
			final V currentObj = container.get(i);
			int compare = comparator.compare(object, currentObj);
			if (compare < 0) break;
			++insertionIndex;
		}
		container.add(insertionIndex, object);
		return insertionIndex;
	}
	
	private static Geocoder sGeocoder = null;
	
	public static class LocationParams {
		double lat; double lng;
		String address;
		
		public LocationParams(double lat, double lng) {
			this.lat = lat; this.lng = lng;
		}
		
		public LocationParams(final String address) {
			this.address = address;
		}
	}
	
	public static LocationParams DEFAULT_LOCATION = new LocationParams("San Francisco, CA");
	
	private static Address getLocationInfo(String address) {
		JSONObject jsonObject = null;
		String query = "https://maps.google.com/maps/api/geocode/json?address=" + address.replaceAll(" ","%20")
				+ "&sensor=false&key=AIzaSyCwhOZqlGg3wLJR2zoYEkkxKDzcD4B-RsA";
		Address addr = null;
		HttpClient client = AndroidHttpClient.newInstance("ReverseGeocoder");
		HttpGet httpGet = new HttpGet(query);

		HttpResponse response;
		StringBuilder stringBuilder = new StringBuilder();

		try {
			response = client.execute(httpGet);
			if (response.getStatusLine().getStatusCode() == 200) {

				HttpEntity entity = response.getEntity();
				InputStream stream = entity.getContent();
				int b;
				while ((b = stream.read()) != -1) {
					stringBuilder.append((char) b);
				}
				try {
					jsonObject = new JSONObject(stringBuilder.toString());
					addr = new Address(Locale.getDefault());
					JSONArray addrComp = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
							.getJSONArray("address_components");
					String locality = ((JSONArray)((JSONObject)addrComp.get(0)).get("types")).getString(0);
					if (locality.compareTo("locality") == 0) {
						locality = ((JSONObject)addrComp.get(0)).getString("long_name");
						addr.setLocality(locality);
					}
					String adminArea = ((JSONArray)((JSONObject)addrComp.get(2)).get("types")).getString(0);
					if (adminArea.compareTo("administrative_area_level_1") == 0) {
						adminArea = ((JSONObject)addrComp.get(2)).getString("long_name");
						addr.setAdminArea(adminArea);
					}
					String country = ((JSONArray)((JSONObject)addrComp.get(3)).get("types")).getString(0);
					if (country.compareTo("country") == 0) {
						country = ((JSONObject)addrComp.get(3)).getString("long_name");
						addr.setCountryName(country);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				Double lon = Double.valueOf(0);
				Double lat = Double.valueOf(0);

				try {

					lon = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
						.getJSONObject("geometry").getJSONObject("location")
						.getDouble("lng");

					lat = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
						.getJSONObject("geometry").getJSONObject("location")
						.getDouble("lat");

				} catch (JSONException e) {
					e.printStackTrace();
				}
				addr.setLatitude(lat);
				addr.setLongitude(lon);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return addr;
	}
	
	public static void geocode(final Context context, final LocationParams params, final Async.Block<Address> completion) {
		if (sGeocoder == null) {
			sGeocoder = new Geocoder(context, Locale.getDefault());
		}
		Async.dispatch(new Runnable() {
			@Override
			public void run() {
				List<Address> addresses = null;
				try {
					if (params.address == null) {
						addresses = sGeocoder.getFromLocation(params.lat, params.lng, 1);
					} else {
						addresses = new ArrayList<Address>();
						addresses.add(getLocationInfo(params.address));
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				final Address address = addresses != null && addresses.size() > 0 ? addresses.get(0) : null;
				Async.dispatchMain(new Runnable() {
					@Override
					public void run() {
						if (completion != null) {
							if (address != null  && !(address.getLatitude() == 0 && address.getLongitude() == 0)) {
								completion.call(address);
							} else {
								completion.call(null);
							}
						}
					}
				});
			}
		});
	}
}