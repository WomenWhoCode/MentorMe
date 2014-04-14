package com.codepath.wwcmentorme.data;

import java.util.ArrayList;

import android.content.Context;

import com.codepath.wwcmentorme.models.Request;
import com.codepath.wwcmentorme.models.User;
import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;

public class DataService {
	
	public static void getMentors(Context context, ParseGeoPoint geoPoint, Double distance, String skill, FindCallback<User> callback) {
		ParseQuery<User> query = User.getQuery();		
		query.whereEqualTo(User.IS_MENTOR_KEY, true);
		if(geoPoint != null) {
			query.whereWithinMiles(User.LOCATION_KEY, geoPoint, distance);
		}
		if(skill != null) {
			ArrayList<String> names = new ArrayList<String>();
			if(!skill.equals("All")){
				names.add(skill);
				query.whereContainsAll(User.MENTOR_SKILLS_KEY, names);
			}			
		}
		query.findInBackground(callback);
	}
	
	public static void getMenteeCount(int mentorId, CountCallback callback) {
		ParseQuery<Request> query = Request.getQuery();
		query.whereEqualTo(Request.MENTOR_ID_KEY, mentorId);
		query.countInBackground(callback);
	}
	
	public static void getMentor(int mentorId, FindCallback<Request> callback) {
		ParseQuery<Request> query = Request.getQuery();
		query.whereEqualTo(Request.MENTOR_ID_KEY, mentorId);
		query.findInBackground(callback);
	}
	
}
