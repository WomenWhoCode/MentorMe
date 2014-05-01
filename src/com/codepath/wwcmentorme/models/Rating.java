package com.codepath.wwcmentorme.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

@ParseClassName("Rating")
public class Rating extends ParseObject{
	public static String FACEBOOK_ID_KEY = "facebookId";
	public static String RATED_FACEBOOK_ID_KEY = "ratedFacebookId";
	public static String RATING_KEY = "rating";
	public static String COMMENT_KEY = "comment";
	
	public Rating() {
		super();
	}

	public int getFacebookId() {
        return getInt(FACEBOOK_ID_KEY);
    }
	
	public void setFacebookId(int facebookId) {
		put(FACEBOOK_ID_KEY, facebookId);
	}
	
	public int getRatedFacebookId() {
		return getInt(RATED_FACEBOOK_ID_KEY);
	}
	
	public void setRatedFacebookId(int ratedFacebookId) {
		put(RATED_FACEBOOK_ID_KEY, ratedFacebookId);
	}
	
	public Double getRating() {
		return getDouble(RATING_KEY);
	}
	
	public void setRating(Double rating) {
		put(RATING_KEY, rating);
	}
	
	public String getComment() {
		return getString(COMMENT_KEY);
	}
	
	public void setComment(String comment) {
		put(COMMENT_KEY, comment);
	}
	
	public static ParseQuery<Rating> getQuery() {
		return ParseQuery.getQuery(Rating.class);
	}
}
