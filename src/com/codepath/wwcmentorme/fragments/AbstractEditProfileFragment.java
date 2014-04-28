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
	private int persona;
	
	public abstract void validateInputs(final Async.Block<View> invalidView);
	abstract void updateViews(final User user);
	abstract void updateProfile(final User user);
	
	public User getProfileUser() {
		return User.getUser(profileUserId);
	}
	
	public int getPersona() {
		return persona;
	}
	
	public AbstractEditProfileFragment setProfileId(long profileId, int persona) {
		this.profileUserId = profileId;
		this.persona = persona;
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
		updateProfile(getProfileUser());
		getProfileUser().saveInBackground();
	}
}
