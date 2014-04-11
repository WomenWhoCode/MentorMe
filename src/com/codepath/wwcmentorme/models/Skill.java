package com.codepath.wwcmentorme.models;

import java.util.Date;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Skill")
public class Skill  extends ParseObject{
	public Skill() {
    }
	
	public String getName() {
		return getString("name");
	}
	
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
