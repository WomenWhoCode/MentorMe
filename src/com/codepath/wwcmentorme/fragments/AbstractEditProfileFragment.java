package com.codepath.wwcmentorme.fragments;

import android.app.Fragment;
import android.util.Log;

import com.codepath.wwcmentorme.activities.EditProfileActivity.OnKeyboardVisibilityListener;
import com.codepath.wwcmentorme.models.User;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public abstract class AbstractEditProfileFragment extends Fragment implements OnKeyboardVisibilityListener {
	private User profileUser;
	
	abstract void maybeEnableNextButton();
	abstract void updateViews(User user);
	abstract void updateProfile();
	
	public User getProfileUser() {
		return profileUser;
	}

	public void setProfileUser(User profileUser) {
		this.profileUser = profileUser;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		ParseUser currentUser = ParseUser.getCurrentUser();
		if (currentUser != null && currentUser.get("profile") != null) {
			profileUser = (User) currentUser.get("profile");
			profileUser.fetchIfNeededInBackground(new GetCallback<User>() {
				@Override
				public void done(User user, ParseException pe) {
					if (pe == null) {
						updateViews(user);
					}
				}
			});
		}
	}
	
	@Override
	public void onPause() {
		saveUserData();
		super.onPause();
	}
	
	@Override
	public void onVisibilityChanged(boolean visible) {
		if (!visible) {
			saveUserData();
			maybeEnableNextButton();
		}
	}

	void saveUserData() {
		updateProfile();
		ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
			@Override
			public void done(ParseException pe) {
				if (pe == null) {
					Log.d("MentorMe", "Update user success!");
				} else {
					Log.d("MentorMe", "Update failed");
				}
			}
		});
	}
}
