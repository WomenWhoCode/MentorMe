package com.codepath.wwcmentorme.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;

import com.codepath.wwcmentorme.helpers.Utils;
import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

@ParseClassName("User")
public class User extends ParseObject {
	private static Map<Long, User> sUsers = Collections.synchronizedMap(new HashMap<Long, User>());
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
	public static String AVAILABILITY_KEY = "availability";
	
	private ArrayList<Long> mMentees = new ArrayList<Long>();
	private ArrayList<Long> mMentors = new ArrayList<Long>();
	
	private static long sMe;
	
	public static void setMe(final long me) {
		sMe = me;
	}
	
	public static long meId() {
		return sMe;
	}
	
	public static User me() {
		return User.getUser(meId());
	}
	
	public static User getUser(final Long facebookId) {
		return sUsers.get(facebookId);
	}
	
	public static List<User> getUsers(final ArrayList<Long> facebookIds) {
		final ArrayList<User> users = new ArrayList<User>();
		for (final Long facebookId : facebookIds) {
			final User user = getUser(facebookId);
			if (user != null) {
				users.add(getUser(facebookId));
			}
		}
		return users;
	}
	
    public User() {
    	super();
    }
    
    @Override
    public void setObjectId(final String objectId) {
    	super.setObjectId(objectId);
    }
	
	public long getFacebookId() {
        return getLong(FACEBOOK_ID_KEY);
    }
	
	public void setFacebookId(long facebookId) {
		put(FACEBOOK_ID_KEY, facebookId);
    	sUsers.put(facebookId, this);
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
	
	public String getDisplayName() {
		return new StringBuilder(getFirstName()).append(" ").append(getLastName()).toString();
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
	
	public String getPosition() {
		return new StringBuilder(getJobTitle()).append(", ").append(getCompanyName()).toString();
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
	
	public JSONArray getAvailability() {
		return getJSONArray(AVAILABILITY_KEY);
	}
	
	public void setAvailability(JSONArray availability) {
		put(AVAILABILITY_KEY, availability);
	}
	
	public ArrayList<Long> getMentees() {
		return mMentees;
	}
	
	public ArrayList<Long> getMentors() {
		return mMentors;
	}
	
	public ArrayList<Long> getConnections(final boolean incoming) {
		return incoming ? mMentees : mMentors;
	}
	
	public static ParseQuery<User> getQuery() {
		return ParseQuery.getQuery(User.class);
	}
	
	// This method will add the user by deduping it and sorting by mentee count.
	private static Comparator<User> sCompareByMenteeCount = new Comparator<User>() {
		@Override
		public int compare(final User lhs, final User rhs) {
			int userIdCompare = Long.valueOf(lhs.getFacebookId()).compareTo(rhs.getFacebookId());
			if (userIdCompare == 0) return 0;
			int countCompare = Integer.valueOf(lhs.getMentees().size()).compareTo(rhs.getMentees().size());
			if (countCompare == 0) return userIdCompare;
			return countCompare;
		}
	};
	
	private static Comparator<Long> sCompareByMenteeCountId = new Comparator<Long>() {
		@Override
		public int compare(final Long lhsId, final Long rhsId) {
			return sCompareByMenteeCount.compare(User.getUser(lhsId), User.getUser(rhsId));
		}
	};
	
	private static Comparator<Long> sCompareByMenteeCountDescendingId = Collections.reverseOrder(sCompareByMenteeCountId);
	private static Comparator<User> sCompareByMenteeCountDescending = Collections.reverseOrder(sCompareByMenteeCount);
	
	public static List<User> sortedByMenteeCount(final List<User> input) {
		final ArrayList<User> users = new ArrayList<User>();
		for (final User user : input) {
			addSortedByMenteeCount(users, user);
		}
		return users;
	}
	
	public static void addSortedByMenteeCount(final List<User> container, final User user) {
		Utils.insertDeduped(container, user, sCompareByMenteeCountDescending);
	}
	
	public static ArrayList<Long> getUserFacebookIds(final List<User> users) {
		final ArrayList<Long> userIds = new ArrayList<Long>();
		for (final User user : users) {
			userIds.add(user.getFacebookId());
		}
		return userIds;
	}
	
	public static void addSortedByMenteeCountInIds(final List<Long> container, final Long userId) {
		Utils.insertDeduped(container, userId, sCompareByMenteeCountDescendingId);
	}
	
	public static void saveAll(final List<User> users) {
		for (final User user : users) {
			sUsers.put(user.getFacebookId(), user);
		}
	}
}
