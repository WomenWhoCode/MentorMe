package com.codepath.wwcmentorme.models;

import java.util.Date;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

@ParseClassName("User")
public class User extends ParseObject{

    public User() {
    }
	
	public long getFacebookId() {
        return getInt("facebookId");
    }
	
	public void setFacebookId(int id) {
		put("facebookId", id);
	}
	
	public String getFacebookAccessToken() {
		return getString("facebookAccessToken");
	}
	
	public void setFacebookAccessToken(String token) {
		put("facebookAccessToken", token);
	}
	
	public String getFirstName() {
		return getString("firstName");
	}
	
	public void setFirstName(String firstName) {
		put("firstName", firstName);
	}
	
	public String getLastName() {
		return getString("lastName");
	}
	
	public void setLastName(String lastName) {
		put("lastName", lastName);
	}
	
	public String getDisplayName() {
		return getString("displayName");
	}
	
	public void setDisplayName(String displayName) {
		put("displayName", displayName);
	}
	
	public String getEmail() {
		return getString("email");
	}
	
	public void setEmail(String email) {
		put("email", email);
	}
	
	public String getGender() {
		return getString("gender");
	}
	
	public void setGender(String gender) {
		put("gender", gender);
	}
	
	public String getCity() {
		return getString("gender");
	}
	
	public void setCity(String city) {
		put("city", city);
	}
	
	public long getZip() {
        return getInt("zip");
    }
	
	public void setZip(int zip) {
		put("zip", zip);
	}
	
	public String getAboutMe() {
		return getString("aboutMe");
	}
	
	public void setAboutMe(String aboutMe) {
		put("aboutMe", aboutMe);
	}
	
	public String getJobTitle() {
		return getString("jobTitle");
	}
	
	public void setJobTitle(String jobTitle) {
		put("jobTitle", jobTitle);
	}
	
	public String getCompanyName() {
		return getString("companyName");
	}
	
	public void setCompanyName(String companyName) {
		put("companyName", companyName);
	}
	
	public int getYearsExperience() {
		return getInt("yearsExperience");
	}
	
	public void setYearsExperience(int years) {
		put("yearsExperience", years);
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
	
    // adding skills to learn
    // removing skills to learn
    
 //  adding skills to teach
    // removing skills to teach
    
    
//	private ArrayList<Skill> skillsToLearn;
//	private ArrayList<Skill> skillsToTeach;
    
    
	//...
	public static ParseQuery<User> getQuery() {
	    return ParseQuery.getQuery(User.class);
	  }
}
