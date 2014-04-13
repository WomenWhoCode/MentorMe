package com.codepath.wwcmentorme.models;

import java.util.List;

import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

@ParseClassName("Skill")
public class Skill extends ParseObject{
	public static String SKILL_ID_KEY = "skillId";
	public static String SKILL_NAME_KEY = "skillName";
	
	public Skill() {
		super();
    }
	
	public String getSkillId() {
		return getString(SKILL_ID_KEY);
	}
	
	public void setSkillId(String skillId) {
		put(SKILL_ID_KEY, skillId);
	}
	
	public String getSkillName() {
		return getString(SKILL_NAME_KEY);
	}
	
	public void setSkillName(String skillName) {
		put(SKILL_NAME_KEY, skillName);
	}
	
	public static ParseQuery<Skill> getQuery() {
		return ParseQuery.getQuery(Skill.class);
	}
	
	public static void getSkills() {
		ParseQuery<Skill> query = ParseQuery.getQuery(Skill.class);
		query.findInBackground(new FindCallback<Skill>() {
			
			@Override
			public void done(List<Skill> skills, ParseException e) {
				if (e == null) {
				
				} else {
					e.printStackTrace();
				}				
			}
		});
	}	
}
