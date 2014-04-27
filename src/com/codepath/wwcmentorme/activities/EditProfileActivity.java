package com.codepath.wwcmentorme.activities;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager.OnBackStackChangedListener;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.wwcmentorme.R;
import com.codepath.wwcmentorme.fragments.EditProfileExperiencesFragment;
import com.codepath.wwcmentorme.fragments.EditProfileLocationFragment;
import com.codepath.wwcmentorme.fragments.EditProfileSkillsFragment;
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

public class EditProfileActivity extends AppActivity {
	public static final String PROFILE_REF = "profile";
	
	public interface OnKeyboardVisibilityListener {
		void onVisibilityChanged(boolean visible);
	}
	
	private ImageView ivUserProfile;
	private TextView tvFirstName;
	private TextView tvLastName;
	
	private static ArrayList<Runnable> sCompletion = new ArrayList<Runnable>();
	
	public static void startWithCompletion(final Activity context, final Runnable completion) {
		if (sCompletion.size() > 0) {
			// There is already one instance of EditProfileActivity waiting to get completed.
			sCompletion.add(completion);
		}
		Intent intent = new Intent(context, EditProfileActivity.class);
		context.startActivity(intent);
		sCompletion.add(completion);
	}
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_profile);
		setupViews();
		setupKeyboardVisibilityListener();
		getFragmentManager().addOnBackStackChangedListener(new OnBackStackChangedListener() {
			@Override
			public void onBackStackChanged() {
				Fragment f = getFragmentManager().findFragmentById(R.id.flContainer);
			    if (f != null) {
			    	updateTitle(f);
			    }				
			}
		});
		goToStep1(null);
	}

	@Override
	public void onResume() {
		super.onResume();
		Session session = ParseFacebookUtils.getSession();
		if (session != null && session.isOpened()) {
			makeMeRequest();
		}
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
								mentorMeUser.setIsMentee("female".equalsIgnoreCase(mentorMeUser.getGender()));
							}
							if (fbGraphUser.getProperty("about") != null) {
								mentorMeUser.setAboutMe((String) fbGraphUser.getProperty("about"));
							}
							ParseUser currentUser = ParseUser.getCurrentUser();
							currentUser.put(PROFILE_REF, mentorMeUser);
							currentUser.saveInBackground();
							User.setMe(mentorMeUser);

							// Show the user info
							populateViewsWithUserInfo(mentorMeUser);

						} else if (response.getError() != null) {
							if ((response.getError().getCategory() == FacebookRequestError.Category.AUTHENTICATION_RETRY)
									|| (response.getError().getCategory() == FacebookRequestError.Category.AUTHENTICATION_REOPEN_SESSION)) {
								Log.d("MentorMe", "The facebook session was invalidated.");
								finish();
							} else {
								Log.d("MentorMe", "Some other error: " + response.getError().getErrorMessage());
							}
						}
					}
				});
		request.executeAsync();
	}
	
	@Override
	public void finish() {
		for (final Runnable runnable : sCompletion) {
			runnable.run();
		}
		sCompletion.clear();
		super.finish();
	}

	private void populateViewsWithUserInfo(User mentorMeUser) {
		ImageLoader.getInstance().displayImage(mentorMeUser.getProfileImageUrl(200), ivUserProfile);
		tvFirstName.setText(mentorMeUser.getFirstName());
		tvLastName.setText(mentorMeUser.getLastName());
	}
	
	private void setupViews() {
		ivUserProfile = (ImageView) findViewById(R.id.ivUserProfile);
		tvFirstName = (TextView) findViewById(R.id.tvFirstName);
		tvLastName = (TextView) findViewById(R.id.tvLastName);
	}
	
	private void setupKeyboardVisibilityListener() {
		final View activityRootView = findViewById(R.id.rlEditProfileRootContainer);
		activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			private final Rect r = new Rect();
			private boolean wasOpened;			
			
			@Override
			public void onGlobalLayout() {
				//r will be populated with the coordinates of your view that area still visible.
				activityRootView.getWindowVisibleDisplayFrame(r);

				int heightDiff = activityRootView.getRootView().getHeight() - r.height();
				boolean isOpen = heightDiff > 100;
				if (isOpen != wasOpened) { // if more than 100 pixels, it's probably an open keyboard
					wasOpened = isOpen;
					Fragment currentFragment = getFragmentManager().findFragmentById(R.id.flContainer);
					if (currentFragment != null) {
						((OnKeyboardVisibilityListener) currentFragment).onVisibilityChanged(isOpen);
					}
				}
			}
		}); 
	}
	
	private void updateTitle(Fragment f) {
		setTitle(String.format("%s %s/3", getString(R.string.title_activity_edit_profile), f.getTag()));
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

	public void goToStep1(View v) {
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.flContainer, new EditProfileLocationFragment(), "1");
		ft.commit();
	}
	
	public void goToStep2(View v) {
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.flContainer, new EditProfileExperiencesFragment(), "2").addToBackStack(null);
		ft.commit();
	}
	
	public void goToAddSkills(View v) {
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.flContainer, new EditProfileSkillsFragment(), "3").addToBackStack(null);
		ft.commit();
	}
	
	public void addMentorSkills(View v) {
		
	}
	
	public void addMenteeSkills(View v) {
		
	}
	
	
}
