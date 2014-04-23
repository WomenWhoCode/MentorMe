package com.codepath.wwcmentorme.activities;

import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.codepath.wwcmentorme.R;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

public class FbLoginActivity extends AppActivity {

	private Button loginButton;
	private Dialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fb_login);
		setupFbLoginButton();

		// Check if there is a currently logged in user
		// and they are linked to a Facebook account.
		ParseUser currentUser = ParseUser.getCurrentUser();
		if ((currentUser != null) && ParseFacebookUtils.isLinked(currentUser)) {
			// Go to the user info activity
			showEditProfileActivity();
		}
	}

	private void setupFbLoginButton() {
		loginButton = (Button) findViewById(R.id.loginButton);
		loginButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.com_facebook_inverse_icon, 0, 0, 0);
        loginButton.setCompoundDrawablePadding(
                getResources().getDimensionPixelSize(R.dimen.com_facebook_loginview_compound_drawable_padding));
        loginButton.setPadding(getResources().getDimensionPixelSize(R.dimen.com_facebook_loginview_padding_left),
                getResources().getDimensionPixelSize(R.dimen.com_facebook_loginview_padding_top),
                getResources().getDimensionPixelSize(R.dimen.com_facebook_loginview_padding_right),
                getResources().getDimensionPixelSize(R.dimen.com_facebook_loginview_padding_bottom));
		loginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onLoginButtonClicked();
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.fb_login, menu);
		return true;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
	}

	private void onLoginButtonClicked() {
		FbLoginActivity.this.progressDialog = ProgressDialog.show(
				FbLoginActivity.this, "", "Logging in...", true);
		List<String> permissions = Arrays.asList("basic_info", "email", "user_about_me", "user_location");
		ParseFacebookUtils.logIn(permissions, this, new LogInCallback() {
			@Override
			public void done(ParseUser user, ParseException err) {
				FbLoginActivity.this.progressDialog.dismiss();
				if (user == null) {
					Log.d("MentorMe", "Uh oh. The user cancelled the Facebook login.");
				} else if (user.isNew()) {
					Log.d("MentorMe", "User signed up and logged in through Facebook!");
					showEditProfileActivity();
				} else {
					Log.d("MentorMe", "User logged in through Facebook!");
					showEditProfileActivity();
				}
			}
		});
	}

	private void showEditProfileActivity() {
		Intent intent = new Intent(this, EditProfileActivity.class);
		startActivity(intent);
	}
}

