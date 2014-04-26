package com.codepath.wwcmentorme.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.codepath.wwcmentorme.R;
import com.codepath.wwcmentorme.helpers.UIUtils;
import com.codepath.wwcmentorme.models.User;

public class EditProfileExperiencesFragment extends AbstractEditProfileFragment {
	private EditText etJobTitle;
	private EditText etCompany;
	private EditText etYearsExperience;
	private Button btnGoToAddSkills;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_edit_profile_experience, container, false);
		setupViews(v);
		return v;
	}
	
	private void setupViews(View v) {
		v.setFocusableInTouchMode(true);
		v.requestFocus();
		etJobTitle = (EditText) v.findViewById(R.id.etJobTitle);
		etCompany = (EditText) v.findViewById(R.id.etCompany);
		etYearsExperience = (EditText) v.findViewById(R.id.etYearsExperience);
		btnGoToAddSkills = (Button) v.findViewById(R.id.btnGoToAddSkills);
		OnEditorActionListener listener = new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
					saveUserData();
					maybeEnableNextButton();
					return true;
				}
				return false;
			}
		};
		etJobTitle.setOnEditorActionListener(listener);
		etCompany.setOnEditorActionListener(listener);
		etYearsExperience.setOnEditorActionListener(listener);
	}
	
	@Override
	protected void updateProfile() {
		getProfileUser().setJobTitle(etJobTitle.getText().toString().trim());
		getProfileUser().setCompanyName(etCompany.getText().toString().trim());
		String yearsInput = etYearsExperience.getText().toString().trim();
		if (!TextUtils.isEmpty(yearsInput)) {
			getProfileUser().setYearsExperience(Integer.valueOf(yearsInput));
		}
	}

	@Override
	protected void maybeEnableNextButton() {
		if (TextUtils.getTrimmedLength(etJobTitle.getText().toString()) > 0 &&
				TextUtils.getTrimmedLength(etYearsExperience.getText().toString()) > 0 &&
				TextUtils.getTrimmedLength(etCompany.getText().toString()) > 0) {
			UIUtils.enableButton(btnGoToAddSkills);
		} else {
			UIUtils.disableButton(btnGoToAddSkills);
		}
	}
	
	@Override
	void updateViews(User profileUser) {
		etJobTitle.setText(profileUser.getJobTitle());
		etCompany.setText(profileUser.getCompanyName());
		etYearsExperience.setText(String.valueOf(profileUser.getYearsExperience()));
		maybeEnableNextButton();
	}
}
