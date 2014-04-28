package com.codepath.wwcmentorme.fragments;

import android.location.Address;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.codepath.wwcmentorme.helpers.Async;
import com.codepath.wwcmentorme.helpers.Utils;
import com.codepath.wwcmentorme.models.User;
import com.parse.ParseGeoPoint;

public class EditProfileLocationFragment extends AbstractEditProfileFragment {	
	private EditText etAddress;
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
		etAddress = (EditText) v.findViewById(R.id.etAddress);
		etAboutme = (EditText) v.findViewById(R.id.etAboutme);
		btnGoToStep2 = (Button) v.findViewById(R.id.btnGoToStep2);
		OnEditorActionListener listener = new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
					saveUserData();
				}
				return false;
			}
		};
		etAddress.setOnEditorActionListener(listener);
	}
	
	@Override
	protected void updateProfile() {
		if (etAddress.getText() != null) {
			getProfileUser().setAddress(etAddress.getText().toString().trim());
			getProfileUser().setAboutMe(etAboutme.getText().toString().trim());
		}
	}

	@Override
	void updateViews(User profileUser) {
		etAddress.setText(profileUser.getAddress());
		etAboutme.setText(profileUser.getAboutMe());
	}
	
	@Override 
	public void validateInputs(final Async.Block<View> invalidView) {
		View view = null;
		if (TextUtils.getTrimmedLength(etAddress.getText().toString()) == 0) view = etAddress;
		if (view == null && TextUtils.getTrimmedLength(etAboutme.getText().toString()) == 0) view = etAboutme;
		if (view == null) {
			Utils.geocode(getActivity(), new Utils.LocationParams(etAddress.getText().toString()), new Async.Block<Address>() {
				@Override
				public void call(final Address address) {
					if (address == null) {
						invalidView.call(etAddress);
					} else {
						getProfileUser().setLocation(new ParseGeoPoint(address.getLatitude(), address.getLongitude()));
						invalidView.call(null);
					}
				}
			});
		} else {
			invalidView.call(view);
		}
	}
}
