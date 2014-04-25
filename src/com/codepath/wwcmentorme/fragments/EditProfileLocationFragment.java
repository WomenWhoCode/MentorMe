package com.codepath.wwcmentorme.fragments;

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
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class EditProfileLocationFragment extends AbstractEditProfileFragment {	
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
		
	private void setupViews(View v) {
		v.setFocusableInTouchMode(true);
		v.requestFocus();
		// suppress back button for this fragment
		v.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if( keyCode == KeyEvent.KEYCODE_BACK)
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
	}
	
	@Override
	protected void saveUserData() {
		getProfileUser().setCity(etCity.getText().toString().trim());
		String zipInput = etZip.getText().toString().trim();
		if (!TextUtils.isEmpty(zipInput)) {
			getProfileUser().setZip(Integer.valueOf(zipInput));
		}
		getProfileUser().setAboutMe(etAboutme.getText().toString().trim());
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

	@Override
	protected void maybeEnableNextButton() {
		if (TextUtils.getTrimmedLength(etCity.getText().toString()) > 0 &&
				TextUtils.getTrimmedLength(etZip.getText().toString()) > 0 &&
				TextUtils.getTrimmedLength(etAboutme.getText().toString()) > 0) {
			UIUtils.enableButton(btnGoToStep2);
		} else {
			UIUtils.disableButton(btnGoToStep2);
		}
	}
	
	@Override
	void updateViews(User user) {
		etCity.setText(user.getCity());
		if (user.getZip() > 0) {
			etZip.setText(String.valueOf(user.getZip()));
		}
		etAboutme.setText(user.getAboutMe());
		maybeEnableNextButton();
	}
}
