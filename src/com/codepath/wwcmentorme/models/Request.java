package com.codepath.wwcmentorme.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

@ParseClassName("Request")
public class Request extends ParseObject {
	public static String MENTEE_ID_KEY = "menteeId";
	public static String MENTOR_ID_KEY = "mentorId";
	
	public Request() {
		super();
	}
	
	public int getMenteeId() {
		return getInt(MENTEE_ID_KEY);
	}
	
	public void setMenteeId(int menteeId) {
		put(MENTEE_ID_KEY, menteeId);
	}
	
	public int getMentorId() {
		return getInt(MENTOR_ID_KEY);
	}
	
	public void setMentorId(int mentorId) {
		put(MENTOR_ID_KEY, mentorId);
	}
	
	public static ParseQuery<Request> getQuery() {
		return ParseQuery.getQuery(Request.class);
	}
}
