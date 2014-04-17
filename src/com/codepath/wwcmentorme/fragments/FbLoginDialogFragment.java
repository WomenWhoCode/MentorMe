package com.codepath.wwcmentorme.fragments;

import java.util.Arrays;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.codepath.wwcmentorme.R;
import com.facebook.widget.LoginButton;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseFacebookUtils.Permissions;
import com.parse.ParseUser;

public class FbLoginDialogFragment extends DialogFragment {
	public interface FbLoginDialogListener {
		public void onFbLogin();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_fb_login, container);
		getDialog().setTitle("Log in with Facebook");
		LoginButton btnLogin = (LoginButton) view.findViewById(R.id.authButton);
		btnLogin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ParseFacebookUtils.logIn(Arrays.asList(Permissions.User.ABOUT_ME, Permissions.User.EMAIL), getActivity(), 
						new LogInCallback() {
					@Override
					public void done(ParseUser user, ParseException err) {
						((FbLoginDialogListener) getActivity()).onFbLogin();
					}
				});
				dismiss();
			}
		});
	    return view;
	}
}
