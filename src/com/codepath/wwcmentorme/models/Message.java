package com.codepath.wwcmentorme.models;

import java.util.Date;

import android.util.Pair;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Message")
public class Message extends ParseObject {
	public static String GROUP_ID_KEY = "groupId";
	public static String USER_ID_KEY = "userId";
	public static String TEXT_KEY = "text";
	
	public static Pair<Long, Long> getGroup(final String groupId) {
		final String[] userIds = groupId.split("_");
		return new Pair<Long, Long>(Long.valueOf(userIds[0]), Long.valueOf(userIds[1]));
	}
	
	public static String getGroup(long userId1, long userId2) {
		final StringBuilder sb = new StringBuilder();
		if (userId2 < userId1) {
			final long temp = userId1;
			userId1 = userId2;
			userId2 = temp;
		}
		sb.append(userId1);
		sb.append('_');
		sb.append(userId2);
		return sb.toString();
	}
	
	public String getGroupId() {
		return getString(GROUP_ID_KEY);
	}
	
	public void setGroupId(final String groupId) {
		put(GROUP_ID_KEY, groupId);
	}
	
	public long getUserId() {
		return getLong(USER_ID_KEY);
	}
	
	public void setUserId(final long userId) {
		put(USER_ID_KEY, userId);
	}
	
	public String getText() {
		return getString(TEXT_KEY);
	}
	
	public void setText(final String text) {
		put(TEXT_KEY, text);
	}
}
