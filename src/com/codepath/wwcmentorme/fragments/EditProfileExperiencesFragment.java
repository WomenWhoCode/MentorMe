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
import com.codepath.wwcmentorme.helpers.Async;
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
	protected void updateProfile(final User profileUser) {
		profileUser.setJobTitle(etJobTitle.getText().toString().trim());
		profileUser.setCompanyName(etCompany.getText().toString().trim());
		String yearsInput = etYearsExperience.getText().toString().trim();
		int years = 0; 
		try {
			years = Integer.valueOf(yearsInput);
		} catch (Exception e) {
		}
		if (!TextUtils.isEmpty(yearsInput)) {
			profileUser.setYearsExperience(years);
		}
	}

	@Override
	public void validateInputs(final Async.Block<View> invalidView) {
		View view = null;
		if (TextUtils.getTrimmedLength(etJobTitle.getText().toString()) == 0) view = etJobTitle;
		if (view == null && TextUtils.getTrimmedLength(etCompany.getText().toString()) == 0) view = etCompany;
		if (view == null && TextUtils.getTrimmedLength(etYearsExperience.getText().toString()) == 0) view = etYearsExperience;
		if (invalidView != null) {
			invalidView.call(view);
		}
	}
	
	@Override
	void updateViews(User profileUser) {
		etJobTitle.setText(profileUser.getJobTitle());
		etCompany.setText(profileUser.getCompanyName());
		int years = profileUser.getYearsExperience();
		if (years > 0) {
			etYearsExperience.setText(String.valueOf(years));
		}
	}
}
