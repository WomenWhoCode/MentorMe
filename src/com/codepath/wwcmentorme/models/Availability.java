package com.codepath.wwcmentorme.models;

import java.io.Serializable;
import java.util.Date;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Availability")
public class Availability extends ParseObject implements Serializable {
	private static final long serialVersionUID = 4789722467837932858L;

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
