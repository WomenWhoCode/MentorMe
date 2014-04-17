package com.codepath.wwcmentorme.activities;

import java.util.Arrays;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.wwcmentorme.R;
import com.codepath.wwcmentorme.adapters.EditProfileFragmentAdapter;
import com.codepath.wwcmentorme.fragments.FbLoginDialogFragment;
import com.codepath.wwcmentorme.fragments.FbLoginDialogFragment.FbLoginDialogListener;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.ParseFacebookUtils.Permissions;
import com.viewpagerindicator.IconPageIndicator;
import com.viewpagerindicator.PageIndicator;

public class EditProfileActivity extends FragmentActivity implements FbLoginDialogListener {
	private FragmentPagerAdapter mAdapter;
	private ViewPager mPager;
	private PageIndicator mIndicator;
	private TextView tvFullName;
	private ImageView ivUserProfile;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_profile);
		setupViewPager();
		setupTopViews();
		
		// Fetch Facebook user info if the session is active
//		Session session = ParseFacebookUtils.getSession();
//		if (session != null && session.isOpened()) {
//			// makeMeRequest();
//		} else {
//			// pop up dialog to prompt fb login
//		}
		
		if (ParseUser.getCurrentUser() == null) {
			// If facebook session isn't open. Show dialog fragment
			showFbLoginDialog();
			// Else updateTopView()
		} else {
			updateTopView();
		}
	}

	private void showFbLoginDialog() {
		FbLoginDialogFragment fbLoginDialog = new FbLoginDialogFragment();
		fbLoginDialog.show(getSupportFragmentManager(), "fragment_fb_login");
	}
	
	private void setupTopViews() {
		tvFullName = (TextView) findViewById(R.id.tvFullName);
		ivUserProfile = (ImageView) findViewById(R.id.ivUserProfile);
	}
	
	private void updateTopView() {
		ParseUser currentUser = ParseUser.getCurrentUser();
		if (currentUser.get("profile") != null) {
			try {
//				if (userProfile.getString("facebookId") != null) {
//					String facebookId = userProfile.get("facebookId")
//							.toString();
//					ivUserProfile.setProfileId(facebookId);
//				} else {
//					// Show the default, blank user profile picture
//					userProfilePictureView.setProfileId(null);
//				}
//				if (userProfile.getString("name") != null) {
//					userNameView.setText(userProfile.getString("name"));
//				} else {
//					userNameView.setText("");
//				}
//				if (userProfile.getString("location") != null) {
//					userLocationView.setText(userProfile.getString("location"));
//				} else {
//					userLocationView.setText("");
//				}
//				if (userProfile.getString("gender") != null) {
//					userGenderView.setText(userProfile.getString("gender"));
//				} else {
//					userGenderView.setText("");
//				}
			} catch (Exception e) {
				Log.d("MentorMeApp",
						"Error parsing saved user data.");
			}

		}
	}
	
	private void setupViewPager() {
		mAdapter = new EditProfileFragmentAdapter(getSupportFragmentManager());

        mPager = (ViewPager)findViewById(R.id.vpPager);
        mPager.setAdapter(mAdapter);

        mIndicator = (IconPageIndicator) findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_profile, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onFbLogin() {
		ParseUser user = ParseUser.getCurrentUser();
		if (user == null) {
			// Show a fragment with login button that says you must login
			Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
		} else {
			Log.d("MyApp", "User logged in through Facebook!");
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
	}
}
