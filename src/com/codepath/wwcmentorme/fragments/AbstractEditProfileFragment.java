package com.codepath.wwcmentorme.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.wwcmentorme.helpers.Async;
import com.codepath.wwcmentorme.models.User;

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
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		final View view = super.onCreateView(inflater, container, savedInstanceState);
		view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
		return view;
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
	
	public void saveUserData() {
		updateProfile(getProfileUser());
		getProfileUser().saveInBackground();
	}
}
