package com.codepath.wwcmentorme.models;

import java.util.List;

import org.json.JSONArray;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

@ParseClassName("User")
public class User extends ParseObject{
	public static String FACEBOOK_ID_KEY = "facebookId";
	public static String FIRST_NAME_KEY = "firstName";
	public static String LAST_NAME_KEY  = "lastName";
	public static String EMAIL_KEY  = "email";
	public static String CITY_KEY = "city";
	public static String ZIP_KEY = "zip";
	public static String GENDER_KEY = "gender";
	public static String ABOUT_ME_KEY = "aboutMe";
	public static String JOB_TITLE_KEY = "jobTitle";
	public static String COMPANY_KEY = "company";
	public static String YEARS_EXPERIENCE_KEY = "yearsExperience";
	public static String IS_MENTOR_KEY = "isMentor";
	public static String IS_MENTEE_KEY = "isMentee";
	public static String LOCATION_KEY = "location";
	public static String MENTOR_SKILLS_KEY = "mentorSkills";
	public static String MENTEE_SKILLS_KEY = "menteeSkills";
	
	private int mMenteeCount;
	
    public User() {
    	super();
    }
	
	public int getFacebookId() {
        return getInt(FACEBOOK_ID_KEY);
    }
	
	public void setFacebookId(int facebookId) {
		put(FACEBOOK_ID_KEY, facebookId);
	}
	
	public String getFirstName() {
		return getString(FIRST_NAME_KEY);
	}
	
	public void setFirstName(String firstName) {
		put(FIRST_NAME_KEY, firstName);
	}
	
	public String getLastName() {
		return getString(LAST_NAME_KEY);
	}
	
	public void setLastName(String lastName) {
		put(LAST_NAME_KEY, lastName);
	}
	
	public String getEmail() {
		return getString(EMAIL_KEY);
	}
	
	public void setEmail(String email) {
		put(EMAIL_KEY, email);
	}
	
	public String getGender() {
		return getString(GENDER_KEY);
	}
	
	public void setGender(String gender) {
		put(GENDER_KEY, gender);
	}
	
	public String getCity() {
		return getString(CITY_KEY);
	}
	
	public void setCity(String city) {
		put(CITY_KEY, city);
	}
	
	public int getZip() {
        return getInt(ZIP_KEY);
    }
	
	public void setZip(int zip) {
		put(ZIP_KEY, zip);
	}
	
	public String getAboutMe() {
		return getString(ABOUT_ME_KEY);
	}
	
	public void setAboutMe(String aboutMe) {
		put(ABOUT_ME_KEY, aboutMe);
	}
	
	public String getJobTitle() {
		return getString(JOB_TITLE_KEY);
	}
	
	public void setJobTitle(String jobTitle) {
		put(JOB_TITLE_KEY, jobTitle);
	}
	
	public String getCompanyName() {
		return getString(COMPANY_KEY);
	}
	
	public void setCompanyName(String companyName) {
		put(COMPANY_KEY, companyName);
	}
	
	public int getYearsExperience() {
		return getInt(YEARS_EXPERIENCE_KEY);
	}
	
	public void setYearsExperience(int years) {
		put(YEARS_EXPERIENCE_KEY, years);
	}
	
	public Boolean getIsMentor() {
		return getBoolean(IS_MENTOR_KEY);
	}
	
	public void setIsMentor(Boolean isMentor) {
		put(IS_MENTOR_KEY, isMentor);
	}
	
	public Boolean getIsMentee() {
		return getBoolean(IS_MENTEE_KEY);
	}
	
	public void setIsMentee(Boolean isMentee) {
		put(IS_MENTEE_KEY, isMentee);
	}
	
	public String getProfileImageUrl() {
		return new StringBuilder("https://graph.facebook.com/").append(getFacebookId()).append("/picture").toString();
	}
	
	public String getProfileImageUrl(final int size) {
		return new StringBuilder(getProfileImageUrl()).append("?width=").append(size).append("&height=").append(size).toString();
	}
	
	public ParseGeoPoint getLocation() {
		return getParseGeoPoint(LOCATION_KEY);
	}
	
	public void setLocation(ParseGeoPoint geoPoint) {
		put(LOCATION_KEY, geoPoint);
	}
	
	public int getMenteeCount() {
		return mMenteeCount;
	}
	
	public void setMenteeCount(int menteeCount) {
		mMenteeCount = menteeCount;
	}
	
	public JSONArray getMentorSkills() {
		return getJSONArray(MENTOR_SKILLS_KEY);
	}
	
	public void setMentorSkills(JSONArray mentorSkills) {
		put(MENTOR_SKILLS_KEY, mentorSkills);
	}
	
	public JSONArray getMenteeSkills() {
		return getJSONArray(MENTEE_SKILLS_KEY);
	}
	
	public void setMenteeSkills(JSONArray menteeSkills) {
		put(MENTEE_SKILLS_KEY, menteeSkills);
	}
	
	public static ParseQuery<User> getQuery() {
		return ParseQuery.getQuery(User.class);
	}
}
