package com.codepath.wwcmentorme.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.wwcmentorme.R;
import com.codepath.wwcmentorme.fragments.EditProfileExperiencesFragment;
import com.codepath.wwcmentorme.fragments.EditProfileLocationFragment;
import com.codepath.wwcmentorme.models.User;
import com.facebook.FacebookRequestError;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

public class EditProfileActivity extends FragmentActivity {
	public static final String PROFILE_REF = "profile";
	
	private ImageView ivUserProfile;
	private TextView tvFirstName;
	private TextView tvLastName;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_profile);
		setupViews();
	}

	@Override
	public void onResume() {
		super.onResume();
		ParseUser currentUser = ParseUser.getCurrentUser();
		if (currentUser == null) {
			// If the user is not logged in, go to the
			// activity showing the login view.
			startLoginActivity();
		} else if (currentUser.get(PROFILE_REF) != null) {
			// Check if the user is currently logged
			// and show any cached content
			User mentorMeUser = (User)currentUser.get(PROFILE_REF);
			mentorMeUser.fetchIfNeededInBackground(new GetCallback<User>() {

				@Override
				public void done(User user, ParseException pe) {
					if (pe == null) {
						populateViewsWithUserInfo(user);
					}
				}
			});
		} else {
			// user is logged in but profile hasn't been sync'd
			// Fetch Facebook user info if the session is active
			Session session = ParseFacebookUtils.getSession();
			if (session != null && session.isOpened()) {
				makeMeRequest();
			} else {
				startLoginActivity();
			}
		}
	}

	private void startLoginActivity() {
		Intent intent = new Intent(this, FbLoginActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}
	
	private void makeMeRequest() {
		// Populate mentor me user with facebook profile info
		Request request = Request.newMeRequest(ParseFacebookUtils.getSession(),
				new Request.GraphUserCallback() {
					@Override
					public void onCompleted(GraphUser fbGraphUser, Response response) {
						if (fbGraphUser != null) {
							User mentorMeUser = new User();
							mentorMeUser.setFacebookId(Long.valueOf(fbGraphUser.getId()));
							mentorMeUser.setFirstName(fbGraphUser.getFirstName());
							mentorMeUser.setLastName(fbGraphUser.getLastName());
							if (fbGraphUser.getProperty("email") != null) {
								mentorMeUser.setEmail((String)fbGraphUser.getProperty("email"));
							}
							if (fbGraphUser.getLocation() != null && fbGraphUser.getLocation().getProperty("name") != null) {
								String locationName = (String) fbGraphUser.getLocation().getProperty("name");
								String city = TextUtils.substring(locationName, 0, locationName.indexOf(","));
								mentorMeUser.setCity(city);
							}
							if (fbGraphUser.getProperty("gender") != null) {
								mentorMeUser.setGender((String) fbGraphUser.getProperty("gender"));
							}
							if (fbGraphUser.getProperty("about") != null) {
								mentorMeUser.setAboutMe((String) fbGraphUser.getProperty("about"));
							}
							ParseUser currentUser = ParseUser.getCurrentUser();
							currentUser.put(PROFILE_REF, mentorMeUser);
							currentUser.saveInBackground();

							// Show the user info
							populateViewsWithUserInfo(mentorMeUser);

						} else if (response.getError() != null) {
							if ((response.getError().getCategory() == FacebookRequestError.Category.AUTHENTICATION_RETRY)
									|| (response.getError().getCategory() == FacebookRequestError.Category.AUTHENTICATION_REOPEN_SESSION)) {
								Log.d("MentorMe", "The facebook session was invalidated.");
								onLogoutButtonClicked();
							} else {
								Log.d("MentorMe", "Some other error: " + response.getError().getErrorMessage());
							}
						}
					}
				});
		request.executeAsync();
	}

	private void onLogoutButtonClicked() {
		// Log the user out
		ParseUser.logOut();

		// Go to the login view
		startLoginActivity();
	}
	
	private void populateViewsWithUserInfo(User mentorMeUser) {
		User.setMe(mentorMeUser.getFacebookId());
		ImageLoader.getInstance().displayImage(mentorMeUser.getProfileImageUrl(200), ivUserProfile);
		tvFirstName.setText(mentorMeUser.getFirstName());
		tvLastName.setText(mentorMeUser.getLastName());
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.replace(R.id.flContainer, new EditProfileLocationFragment() );
		ft.commit();
	}
	
	private void setupViews() {
		ivUserProfile = (ImageView) findViewById(R.id.ivUserProfile);
		tvFirstName = (TextView) findViewById(R.id.tvFirstName);
		tvLastName = (TextView) findViewById(R.id.tvLastName);
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

	public void goToStep2(View v) {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.replace(R.id.flContainer, new EditProfileExperiencesFragment()).addToBackStack(null);
		ft.commit();
	}
}
