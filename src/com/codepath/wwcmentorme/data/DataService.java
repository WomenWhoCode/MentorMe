package com.codepath.wwcmentorme.data;

import com.codepath.wwcmentorme.models.User;
import com.parse.FindCallback;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class DataService {
	
	public static void getMentors(ParseGeoPoint geoPoint, Double distance, FindCallback<User> callback) {
		ParseQuery<User> query = User.getQuery();
		query.whereEqualTo(User.IS_MENTOR_KEY, true);
		if(geoPoint != null) {
			query.whereWithinMiles(User.LOCATION_KEY, geoPoint, distance);
		}
		query.findInBackground(callback);
	}

}
