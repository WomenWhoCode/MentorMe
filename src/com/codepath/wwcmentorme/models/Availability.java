package com.codepath.wwcmentorme.models;

import java.util.Date;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Availability")
public class Availability extends ParseObject {
	
	public String getObjectId() {
		return getObjectId();
	}
	
	public Date getUpdatedAt() {
		return getUpdatedAt();
	}
	
    public Date getCreatedAt() {
    	return getCreatedAt();
	}
}
