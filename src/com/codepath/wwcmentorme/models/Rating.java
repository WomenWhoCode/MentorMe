package com.codepath.wwcmentorme.models;

import java.io.Serializable;
import java.util.Date;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Rating")
public class Rating extends ParseObject implements Serializable {
	private static final long serialVersionUID = 795747122656694729L;

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
