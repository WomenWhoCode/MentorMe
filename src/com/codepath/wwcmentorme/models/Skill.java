package com.codepath.wwcmentorme.models;

import java.util.Arrays;

import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

@ParseClassName("Skill")
public class Skill  extends ParseObject{
	public Skill() {
    }
	
	public String getName() {
		return getString("name");
	}
	
	//returns arraylist of skill ObjectIds and names
	public void getSkills(FindCallback<Skill> callback) {
		ParseQuery<Skill> query = ParseQuery.getQuery(Skill.class);
		query.selectKeys(Arrays.asList("name"));
		query.findInBackground(callback);
		
		// this returns a List; the below would return ArrayList but needs callback
//		query.findInBackground(new FindCallback<Skill>() {
//		    public void done(List<Skill> skillList, ParseException e) {
//		        if (e == null) {
//		        	ArrayList<Skill> skillArrayList = new ArrayList<Skill>();
//		        	for (Skill s : skillList) {
//		        		skillArrayList.add(s);
//		        	}
//		        	callback(skillArrayList);
//		        } else {
//		            Log.d("score", "Error: " + e.getMessage());
//		        }
//		    }
//		});
	}
	
}
