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
		return v;
	}

	@Override
	void maybeEnableNextButton() {
		// TODO Auto-generated method stub
		
	}

	@Override
	void saveUserData() {
		// TODO Auto-generated method stub
		
	}

	@Override
	void updateViews(User user) {
		// TODO Auto-generated method stub
		
	}

}
