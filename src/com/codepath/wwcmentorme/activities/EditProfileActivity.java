package com.codepath.wwcmentorme.activities;

import java.util.ArrayList;

import org.json.JSONArray;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager.OnBackStackChangedListener;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.location.Address;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.codepath.wwcmentorme.R;
import com.codepath.wwcmentorme.data.DataService;
import com.codepath.wwcmentorme.fragments.AbstractEditProfileFragment;
import com.codepath.wwcmentorme.fragments.EditProfileExperiencesFragment;
import com.codepath.wwcmentorme.fragments.EditProfileLocationFragment;
import com.codepath.wwcmentorme.fragments.EditProfileSkillsFragment;
import com.codepath.wwcmentorme.helpers.Async;
import com.codepath.wwcmentorme.helpers.Utils;
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
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

public class EditProfileActivity extends AppActivity {
	public static final String PROFILE_REF = "profile";
	
	private ImageView ivUserProfile;
	private TextView tvFirstName;
	private TextView tvLastName;
	private long mUserId;
	private int mPersona;
	
	public static final int PERSONA_BOTH = 0;
	public static final int PERSONA_MENTEE = 1;
	public static final int PERSONA_MENTOR = 2;
	
	private static ArrayList<Runnable> sCompletion = new ArrayList<Runnable>();
	
	public static void startWithCompletion(final Activity context, final int persona, final Runnable completion) {
		if (sCompletion.size() > 0) {
			// There is already one instance of EditProfileActivity waiting to get completed.
			sCompletion.add(completion);
		}
		Intent intent = new Intent(context, EditProfileActivity.class);
		intent.putExtra("persona", persona);
		context.startActivity(intent);
		sCompletion.add(completion);
	}
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_profile);
		mPersona = getIntent().getIntExtra("persona", PERSONA_BOTH);
		setupViews();
		getFragmentManager().addOnBackStackChangedListener(new OnBackStackChangedListener() {
			@Override
			public void onBackStackChanged() {
				updateTitle();
			}
		});
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		setupKeyboardVisibilityListener();
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
					final ScrollView sv = (ScrollView)findViewById(R.id.svEditProfileRoot);
					int y = isOpen ? sv.getHeight() : 0;
					sv.smoothScrollTo(0, y);
					setActionBarVisible(!isOpen);
				}
			}
		}); 
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
		getProgressBar().setVisibility(View.VISIBLE);
		Request request = Request.newMeRequest(ParseFacebookUtils.getSession(),
				new Request.GraphUserCallback() {
					@Override
					public void onCompleted(final GraphUser fbGraphUser, Response response) {
						if (fbGraphUser != null) {
							mUserId = Long.valueOf(fbGraphUser.getId());
							DataService.getUser(mUserId, new GetCallback<User>() {
								@Override
								public void done(User mentorMeUser, ParseException e) {
									boolean isGeocoding = false;
									if (mentorMeUser == null || e != null) {
										mentorMeUser = new User();
										mentorMeUser.setFacebookId(mUserId);
										mentorMeUser.setFirstName(fbGraphUser.getFirstName());
										mentorMeUser.setLastName(fbGraphUser.getLastName());
										if (fbGraphUser.getProperty("email") != null) {
											mentorMeUser.setEmail((String)fbGraphUser.getProperty("email"));
										}
										if (fbGraphUser.getLocation() != null && fbGraphUser.getLocation().getProperty("name") != null) {
											String locationName = (String) fbGraphUser.getLocation().getProperty("name");
											mentorMeUser.setAddress(locationName);
											final User user = mentorMeUser;
											Utils.geocode(getActivity(), new Utils.LocationParams(locationName), new Async.Block<Address>() {
												@Override
												public void call(final Address address) {
													if (address != null) {
														user.setLocation(new ParseGeoPoint(address.getLatitude(), address.getLongitude()));
													}
													goToStep1(null);
													Async.dispatchMain(new Runnable() {
														@Override
														public void run() {
															updateTitle();
														}
													});
												}
											});
											isGeocoding = true;
										}
										if (fbGraphUser.getProperty("gender") != null) {
											mentorMeUser.setGender((String) fbGraphUser.getProperty("gender"));
											mentorMeUser.setIsMentee("female".equalsIgnoreCase(mentorMeUser.getGender()));
										}
										if (fbGraphUser.getProperty("about") != null) {
											mentorMeUser.setAboutMe((String) fbGraphUser.getProperty("about"));
										}
									}
									mentorMeUser.setFacebookId(mUserId);
									mentorMeUser.saveInBackground();
									populateViewsWithUserInfo(mentorMeUser);
									getProgressBar().setVisibility(View.INVISIBLE);
									if (!isGeocoding) {
										goToStep1(null);
										Async.dispatchMain(new Runnable() {
											@Override
											public void run() {
												updateTitle();
											}
										});
									}
								}
							});
						} else if (response.getError() != null) {
							if ((response.getError().getCategory() == FacebookRequestError.Category.AUTHENTICATION_RETRY)
									|| (response.getError().getCategory() == FacebookRequestError.Category.AUTHENTICATION_REOPEN_SESSION)) {
								Log.d("MentorMe", "The facebook session was invalidated.");
								finish();
							} else {
								Log.d("MentorMe", "Some other error: " + response.getError().getErrorMessage());
							}
							getProgressBar().setVisibility(View.INVISIBLE);
						}
					}
				});
		request.executeAsync();
	}
	
	public void finishWithSuccess() {
		if (mUserId != 0) {
			User.setMe(User.getUser(mUserId));
			ParseInstallation installation = ParseInstallation.getCurrentInstallation();
	    	installation.put("userId", User.meId());
	    	installation.saveInBackground();
			ParseUser.getCurrentUser().put(PROFILE_REF, User.me());
			ParseUser.getCurrentUser().saveInBackground();
			final JSONArray menteeSkills = User.me().getMenteeSkills();
			final JSONArray mentorSkills = User.me().getMentorSkills();
			User.me().setIsMentee(menteeSkills != null && menteeSkills.length() > 0);
			User.me().setIsMentor(mentorSkills != null && mentorSkills.length() > 0);
			User.me().saveInBackground();
		}
		finish();
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
	
	private void updateTitle() {
		Fragment f = getFragmentManager().findFragmentById(R.id.flContainer);
		if (f != null) {
			int step = Integer.valueOf(f.getTag());
			final String s1 = step == 1 ? "\u2776" : "\u2780";
			final String s2 = step == 2 ? "\u2777" : "\u2781";
			final String s3 = step == 3 ? "\u2778" : "\u2782";
			setTitle(String.format("%s %s\u2015%s\u2015%s", getString(R.string.title_activity_edit_profile), s1, s2, s3));
		}
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
	
	public void validate(final Async.Block<Boolean> completion) {
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
		final AbstractEditProfileFragment f = (AbstractEditProfileFragment)getFragmentManager().findFragmentById(R.id.flContainer);
		if (f != null) {
			f.validateInputs(new Async.Block<View>() {
				@Override
				public void call(View result) {
					if (result != null) {
						result.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.shake));
					}
					if (result == null) f.saveUserData();
					completion.call(result == null);
				}
			});
		} else {
			completion.call(true);
		}
	}
	
	private void presentFragment(final AbstractEditProfileFragment fragment, final String tag) {
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.setCustomAnimations(
				R.animator.card_slide_right_in, R.animator.card_slide_right_out,
				R.animator.card_slide_left_in, R.animator.card_slide_left_out);
		ft.replace(R.id.flContainer, fragment.setProfileId(mUserId, mPersona), tag).addToBackStack(null);
		ft.commit();
		Async.dispatchMain(new Runnable() {
			@Override
			public void run() {
				updateTitle();
			}
		});
	}
	
	public void goToStep1(View v) {
		validate(new Async.Block<Boolean>() {
			@Override
			public void call(Boolean result) {
				if (!result.booleanValue()) return;
				presentFragment(new EditProfileLocationFragment(), "1");
			}
		});
	}
	
	public void goToStep2(View v) {
		validate(new Async.Block<Boolean>() {
			@Override
			public void call(Boolean result) {
				if (!result.booleanValue()) return;
				presentFragment(new EditProfileExperiencesFragment(), "2");
			}
		});
	}
	
	public void goToAddSkills(View v) {
		validate(new Async.Block<Boolean>() {
			@Override
			public void call(Boolean result) {
				if (!result.booleanValue()) return;
				presentFragment(new EditProfileSkillsFragment(), "3");
			}
		});
	}
	
	public void goToDoneEditProfile(final View v) {
		validate(new Async.Block<Boolean>() {
			@Override
			public void call(Boolean result) {
				if (!result.booleanValue()) return;
				finishWithSuccess();
			}
		});
	}
}
