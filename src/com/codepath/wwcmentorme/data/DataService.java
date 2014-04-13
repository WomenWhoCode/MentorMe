package com.codepath.wwcmentorme.data;

import com.codepath.wwcmentorme.models.Request;
import com.codepath.wwcmentorme.models.User;
import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;

public class DataService {
	
	public static void getMentors(ParseGeoPoint geoPoint, Double distance, FindCallback<User> callback) {
		ParseQuery<User> query = User.getQuery();		
		query.whereEqualTo(User.IS_MENTOR_KEY, true);
		query.include("Request");
		if(geoPoint != null) {
			query.whereWithinMiles(User.LOCATION_KEY, geoPoint, distance);
		}
		query.findInBackground(callback);
	}
	
	public static void getMenteeCount(int mentorId, CountCallback callback) {
		ParseQuery<Request> query = Request.getQuery();
		query.whereEqualTo(Request.MENTOR_ID_KEY, mentorId);
		query.countInBackground(callback);
	}
	
}
