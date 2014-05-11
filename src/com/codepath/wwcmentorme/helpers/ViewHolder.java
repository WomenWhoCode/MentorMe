package com.codepath.wwcmentorme.helpers;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ViewHolder {
	
	public static class UserItem {
		public ImageView ivMentorProfile;
		public TextView tvFirstName;
		public TextView tvLastName;
		public TextView tvAboutMe;
		public TextView tvPosition;
		public TextView tvAbout;
		public TextView tvDistance;
		public TextView tvMenteeCount;
		public LinearLayout llSkills;
		public TextView tvSkill1;
		public TextView tvSkill2;
		public TextView tvSkill3;
	}
	
	public static class DrawerItem {
		public Button btnItem;
	}
	
	public static class ChatItem {
		public ImageView ivUserProfile;
		public TextView tvMessage;
		public TextView tvTime;
	}
	
}
