package com.codepath.wwcmentorme.activities;

import android.app.Application;

import com.codepath.wwcmentorme.models.User;
import com.parse.Parse;
import com.parse.ParseObject;

public class MentorMeApp extends Application {
	
	private static final String APPLICATION_ID = "";
	private static final String CLIENT_KEY = "";
	
	@Override
    public void onCreate() {
        super.onCreate();
        // must register subclasses *before* initializing Parse
        ParseObject.registerSubclass(User.class);
        Parse.initialize(this, APPLICATION_ID, CLIENT_KEY);
    }
    
	// not needed?
//    public static ParseClient getRestClient() {
//    	return (ParseClient) ParseClient.getInstance(ParseClient.class, MentorMeApp.context);
//    }
}
