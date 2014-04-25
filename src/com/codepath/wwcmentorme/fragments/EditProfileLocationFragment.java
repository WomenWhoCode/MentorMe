package com.codepath.wwcmentorme.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
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

public class EditProfileLocationFragment extends Fragment {	
	private User mentorMeUser;
	private EditText etCity;
	private EditText etZip;
	private EditText etAboutme;
	private Button btnGoToStep2;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_edit_profile_location, container, false);
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
		// suppress back button for this fragment
		v.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if( keyCode == KeyEvent.KEYCODE_BACK && v.getId() != R.id.etAboutme)
				{
					return true;
				}
				return false;
			}
		});
		etCity = (EditText) v.findViewById(R.id.etCity);
		etZip = (EditText) v.findViewById(R.id.etZip);
		etAboutme = (EditText) v.findViewById(R.id.etAboutme);
		btnGoToStep2 = (Button) v.findViewById(R.id.btnGoToStep2);
		OnEditorActionListener listener = new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
					saveUserData();
					maybeEnableNextButton();
				}
				return false;
			}
		};
		etCity.setOnEditorActionListener(listener);
		etZip.setOnEditorActionListener(listener);
		etAboutme.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					UIUtils.hideSoftKeyboard(getActivity(), v);
					saveUserData();
					maybeEnableNextButton();
				}
				return false;
			}
		});
	}
	
	protected void saveUserData() {
		mentorMeUser.setCity(etCity.getText().toString().trim());
		String zipInput = etZip.getText().toString().trim();
		if (!TextUtils.isEmpty(zipInput)) {
			mentorMeUser.setZip(Integer.valueOf(zipInput));
		}
		mentorMeUser.setAboutMe(etAboutme.getText().toString().trim());
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
		if (TextUtils.getTrimmedLength(etCity.getText().toString()) > 0 &&
				TextUtils.getTrimmedLength(etZip.getText().toString()) > 0 &&
				TextUtils.getTrimmedLength(etAboutme.getText().toString()) > 0) {
			UIUtils.enableButton(btnGoToStep2);
		} else {
			UIUtils.disableButton(btnGoToStep2);
		}
	}
	
	private void updateViews(User user) {
		etCity.setText(user.getCity());
		if (user.getZip() > 0) {
			etZip.setText(String.valueOf(user.getZip()));
		}
		etAboutme.setText(user.getAboutMe());
		maybeEnableNextButton();
	}
}
