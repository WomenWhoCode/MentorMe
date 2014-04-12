package com.codepath.wwcmentorme.activities;

import android.app.Application;

import com.codepath.wwcmentorme.helpers.Constants;
import com.codepath.wwcmentorme.models.User;
import com.parse.Parse;
import com.parse.ParseObject;

public class MentorMeApp extends Application {
	
	@Override
    public void onCreate() {
        super.onCreate();
        // must register subclasses *before* initializing Parse
        ParseObject.registerSubclass(User.class);
        Parse.initialize(this, Constants.PARSE_APPLICATION_ID, Constants.PARSE_APPLICATION_ID);
    }
}
