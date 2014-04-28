package com.codepath.wwcmentorme.fragments;

import android.app.Fragment;
import android.util.Log;
import android.view.View;

import com.codepath.wwcmentorme.helpers.Async;
import com.codepath.wwcmentorme.models.User;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public abstract class AbstractEditProfileFragment extends Fragment {
	private long profileUserId;
	
	public abstract void validateInputs(final Async.Block<View> invalidView);
	abstract void updateViews(User user);
	abstract void updateProfile();
	
	public User getProfileUser() {
		return User.getUser(profileUserId);
	}
	
	public AbstractEditProfileFragment setProfileId(long profileId) {
		this.profileUserId = profileId;
		return this;
	}

	@Override
	public void onPause() {
		saveUserData();
		super.onPause();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		updateViews(getProfileUser());
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
