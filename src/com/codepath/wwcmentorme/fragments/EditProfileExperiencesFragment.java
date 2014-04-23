package com.codepath.wwcmentorme.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class EditProfileExperiencesFragment extends Fragment {
	private User mentorMeUser;
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

	@Override
	public void onResume() {
		super.onResume();
		ParseUser currentUser = ParseUser.getCurrentUser();
		if (currentUser != null && currentUser.get("profile") != null) {
			mentorMeUser = (User) currentUser.get("profile");
			mentorMeUser.fetchIfNeededInBackground(new GetCallback<User>() {
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
	
	protected void saveUserData() {
		mentorMeUser.setJobTitle(etJobTitle.getText().toString().trim());
		mentorMeUser.setCompanyName(etCompany.getText().toString().trim());
		String yearsInput = etYearsExperience.getText().toString().trim();
		if (!TextUtils.isEmpty(yearsInput)) {
			mentorMeUser.setYearsExperience(Integer.valueOf(yearsInput));
		}
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

	protected void maybeEnableNextButton() {
		if (TextUtils.getTrimmedLength(etJobTitle.getText().toString()) > 0 &&
				TextUtils.getTrimmedLength(etYearsExperience.getText().toString()) > 0 &&
				TextUtils.getTrimmedLength(etCompany.getText().toString()) > 0) {
			UIUtils.enableButton(btnGoToAddSkills);
		} else {
			UIUtils.disableButton(btnGoToAddSkills);
		}
	}
	
	private void updateViews(User user) {
		etJobTitle.setText(user.getJobTitle());
		etCompany.setText(user.getCompanyName());
		etYearsExperience.setText(String.valueOf(user.getYearsExperience()));
		maybeEnableNextButton();
	}
}
