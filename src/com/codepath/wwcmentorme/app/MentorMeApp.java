package com.codepath.wwcmentorme.app;

import android.content.Context;

import com.codepath.wwcmentorme.activities.MentorListActivity;
import com.codepath.wwcmentorme.helpers.Constants;
import com.codepath.wwcmentorme.models.Rating;
import com.codepath.wwcmentorme.models.Request;
import com.codepath.wwcmentorme.models.User;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.PushService;

public class MentorMeApp extends com.activeandroid.app.Application {
	private static Context context;
	
    @Override
    public void onCreate() {
        super.onCreate();
        MentorMeApp.context = this;
        initializeParse();
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().
        		cacheInMemory().cacheOnDisc().build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
            .defaultDisplayImageOptions(defaultOptions)
            .build();
        ImageLoader.getInstance().init(config);
    }
    
    private void initializeParse(){
    	ParseObject.registerSubclass(User.class);
    	ParseObject.registerSubclass(Request.class);
    	ParseObject.registerSubclass(Rating.class);
    	Parse.initialize(this, Constants.PARSE_APPLICATION_ID, Constants.PARSE_CLIENT_KEY);
    	
    	PushService.setDefaultPushCallback(this, MentorListActivity.class);
    	ParseFacebookUtils.initialize(Constants.FACEBOOK_APP_ID);
    	
    	ParseACL defaultACL = new ParseACL();
    	defaultACL.setPublicReadAccess(true);
    	ParseACL.setDefaultACL(defaultACL, true);
    }
}