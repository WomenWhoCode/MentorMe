package com.codepath.wwcmentorme.models;

import java.io.Serializable;
import java.util.Date;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

@ParseClassName("Request")
public class Request extends ParseObject {
	public static String MENTEE_ID_KEY = "menteeId";
	public static String MENTOR_ID_KEY = "mentorId";
	public static String CREATED_AT_KEY = "createdAt";
	
	public Request() {
		super();
	}
	
	public long getMenteeId() {
		return getLong(MENTEE_ID_KEY);
	}
	
	public void setMenteeId(long menteeId) {
		put(MENTEE_ID_KEY, menteeId);
	}
	
	public long getMentorId() {
		return getLong(MENTOR_ID_KEY);
	}
	
	public void setMentorId(long mentorId) {
		put(MENTOR_ID_KEY, mentorId);
	}
	
	public Date getCreatedAt() {
    	return getDate(CREATED_AT_KEY);
	}
	
	public void setCreatedAt(Date createdAt) {
		put(CREATED_AT_KEY, createdAt);
	}
	
	public static ParseQuery<Request> getQuery() {
		return ParseQuery.getQuery(Request.class);
	}
	
	
}
