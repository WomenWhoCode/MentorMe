package com.codepath.wwcmentorme.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.wwcmentorme.R;
import com.codepath.wwcmentorme.models.User;

public class EditProfileSkillsFragment extends AbstractEditProfileFragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_edit_profile_skills, container, false);
		setupViews(v);
		return v;
	}

	private void setupViews(View v) {
		
	}
	
	@Override
	protected void maybeEnableNextButton() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void updateProfile() {
		// TODO Auto-generated method stub
		
	}

	@Override
	void updateViews(User profileUser) {
		// TODO Auto-generated method stub
		
	}
}
