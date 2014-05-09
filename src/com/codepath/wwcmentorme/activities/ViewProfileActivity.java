package com.codepath.wwcmentorme.activities;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.codepath.wwcmentorme.R;
import com.codepath.wwcmentorme.data.DataService;
import com.codepath.wwcmentorme.helpers.Async;
import com.codepath.wwcmentorme.helpers.Async.Block;
import com.codepath.wwcmentorme.helpers.Constants.Persona;
import com.codepath.wwcmentorme.helpers.MentorMeReceiver;
import com.codepath.wwcmentorme.helpers.UIUtils;
import com.codepath.wwcmentorme.helpers.Utils;
import com.codepath.wwcmentorme.models.Rating;
import com.codepath.wwcmentorme.models.User;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.PushService;

public class ViewProfileActivity extends AppActivity {
	public static final String USER_ID_KEY = "userId";
	public static final String LATITUDE_KEY = "latitude";
	public static final String LONGITUDE_KEY = "longitude";
	public static final String RESPOND_KEY = "inResponse";
	private User user;
	private ImageLoader mImageLoader;
	private ImageView ivMentorProfile;
	private Double mLat;
	private Double mLng;
	private TextView tvFirstName;
	private TextView tvLastName;
	private TextView tvPosition;
	private TextView tvLocation;
	private TextView tvDistance;
	private TextView tvMenteeCount;
	private RatingBar rbRating;
	private RatingBar rbMyRating;
	private TextView tvNoOfRating;
	private TextView tvAddReview;
	private TextView tvAboutMe;
	private LinearLayout llMentorSkills;
	private LinearLayout llMenteeSkills;
	private TextView tvYearsExperience;
	private TextView tvMentorSkills;
	private TextView tvMenteeSkills;
	private TextView tvAvailabilityHeader;
	private LinearLayout llAvailability;
	private ImageView ivMentee;
	private MapFragment fragment;
	private Menu menu;
	private MenuItem item;
	private boolean mIsResponse;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_profile);		

		if(getIntent().hasExtra(LATITUDE_KEY)) {
			mLat = getIntent().getDoubleExtra(LATITUDE_KEY, 0);
		}
		
		if(getIntent().hasExtra(LONGITUDE_KEY)) {
			mLng = getIntent().getDoubleExtra(LONGITUDE_KEY, 0);
		}
		
		if (getIntent().hasExtra(USER_ID_KEY)) {
			final long userId = getIntent().getLongExtra(USER_ID_KEY, 0);
			user = User.getUser(userId);
			final Runnable currentUserRunnable = new Runnable() {
				@Override
				public void run() {
					UIUtils.getOrCreateLoggedInUser(getActivity(), new Async.Block<User>() {
						@Override
						public void call(final User user) {
							setupViews();
							populateViews();
							updateMenuTitles();
						}
					});
				}
			};
			if (user == null) {
				getProgressBar().setVisibility(View.VISIBLE);
				DataService.getUser(userId, new GetCallback<User>() {
					@Override
					public void done(User result, ParseException e) {
						getProgressBar().setVisibility(View.INVISIBLE);
						if (result != null && e == null) {
							user = result;
							user.setFacebookId(userId);
							currentUserRunnable.run();
						} else {
							finish();
						}
					}
				});
			} else {
				currentUserRunnable.run();
			}
			mIsResponse = DataService.isResponsePending(userId);
		}
	}
	
	private void setupViews() {
		ivMentorProfile = (ImageView) findViewById(R.id.ivMentorProfile);
		tvFirstName = (TextView) findViewById(R.id.tvFirstName);
		tvLastName = (TextView) findViewById(R.id.tvLastName);
		tvPosition = (TextView) findViewById(R.id.tvPosition);
		tvLocation = (TextView) findViewById(R.id.tvLocation);
		tvDistance = (TextView) findViewById(R.id.tvDistance);
		tvMenteeCount = (TextView) findViewById(R.id.tvMenteeCount);
		rbRating = (RatingBar) findViewById(R.id.rbRating);
		rbMyRating = (RatingBar) findViewById(R.id.rbRatingMe);
		customizeProgressBar(rbRating, false);
		customizeProgressBar(rbMyRating, true);
		tvNoOfRating = (TextView) findViewById(R.id.tvNoOfRating);
		tvAddReview = (TextView) findViewById(R.id.tvAddReview);
		tvAboutMe = (TextView) findViewById(R.id.tvAboutMe);
		llMentorSkills = (LinearLayout) findViewById(R.id.llMentorSkills);
		llMenteeSkills = (LinearLayout) findViewById(R.id.llMenteeSkills);
		tvYearsExperience = (TextView) findViewById(R.id.tvYearsExperience);
		tvMentorSkills = (TextView) findViewById(R.id.tvMentorSkills);
		tvMenteeSkills = (TextView) findViewById(R.id.tvMenteeSkills);
		tvAvailabilityHeader = (TextView) findViewById(R.id.tvAvailabilityHeader);
		llAvailability = (LinearLayout) findViewById(R.id.llAvailability);
		ivMentee = (ImageView) findViewById(R.id.ivMentee);
		fragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
	}
	
	private void customizeProgressBar(final ProgressBar progressBar, final boolean selectable) {
		UIUtils.customizeProgressBar(progressBar, Color.parseColor(selectable ? "#00B6AA" : "#cccccc"), Color.parseColor("#eeeeee"));
	}

	private void populateViews() {
		mImageLoader = ImageLoader.getInstance();
		mImageLoader.init(ImageLoaderConfiguration.createDefault(this));
		mImageLoader.displayImage(user.getProfileImageUrl(640), ivMentorProfile);
		
		tvFirstName.setText(user.getFirstName());
		tvLastName.setText(user.getLastName());
		
		String formattedPosition = user.getJobTitle()  + ", " + user.getCompanyName();
		tvPosition.setText(Html.fromHtml(formattedPosition));
		
		String formattedLocation = user.getAddress();
		if (formattedLocation == null) formattedLocation = "";
		tvLocation.setText(Html.fromHtml(formattedLocation));
		
		if(mLat != null && mLng != null) {
			Double distance = Utils.getDistance(mLat, mLng, user.getLocation().getLatitude(), user.getLocation().getLongitude());
			tvDistance.setText(Utils.formatDouble(distance) + "mi");	
		}
		tvAboutMe.setText(user.getAboutMe());
		tvYearsExperience.setText(Integer.toString(user.getYearsExperience()));

		populateMenteeCount();
		populateAverageRating();
		populateAddReview();	
		populateMentorSkills();
		populateMenteeSkills();
		populateAvailability();
		populateMap();
		setOnClickMenteeCount();
	}
	
	private void populateMap() {
		final GoogleMap map = fragment.getMap();
		map.setMyLocationEnabled(false);
		fragment.getView().post(new Runnable() {
			@Override
			public void run() {
				LatLngBounds.Builder builder = new LatLngBounds.Builder();

				final ParseGeoPoint pt = user.getLocation();
				final LatLng latlng = new LatLng(pt.getLatitude(), pt
						.getLongitude());
				map.addMarker(new MarkerOptions()
						.icon(BitmapDescriptorFactory
								.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
						.position(latlng));
				builder.include(latlng);			

				map.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 13));
			}
		});

	}

	private void setOnClickMenteeCount() {
		ivMentee.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				final Intent intent = new Intent(ViewProfileActivity.this, UserListActivity.class);
				intent.putExtra("persona", Persona.MENTOR);
				intent.putExtra("userId", user.getFacebookId());
				startActivity(intent);			
			}
		});		
	}

	private void populateMenteeCount() {
		final int count = user.getMentees().size();
		tvMenteeCount.setText(Utils.formatNumber(Integer
				.toString(count))
				+ " "
				+ getResources().getQuantityString(R.plurals.mentee,
						count));
	}

	private void populateAvailability() {
		if(user.getAvailability() != null && user.getAvailability().length() > 0) {
			 for (String day : getResources().getStringArray(R.array.dayofweek_array)) {
				 TextView tvAvailabilityDay = (TextView) getLayoutInflater().inflate(R.layout.availability_textview_item, null);
				 tvAvailabilityDay.setText(day);
				 LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				params.setMargins(0,0,15,0);
				tvAvailabilityDay.setLayoutParams(params);
				 if(user.getAvailability().toString().contains(day)) {
					 tvAvailabilityDay.setBackgroundColor(getResources().getColor(R.color.actionbar));
					 tvAvailabilityDay.setTextColor(Color.parseColor("#ffffff"));
				 } 
				 llAvailability.addView(tvAvailabilityDay);
			}			
		} else {
			tvAvailabilityHeader.setVisibility(View.GONE);
			llAvailability.setVisibility(View.GONE);
		}
	}
	
	private void populateSkills(final JSONArray array, final LinearLayout ll, final View container) {
		if(array != null && array.length() > 0) {
			for(int i = 0; i <= array.length() - 1; i++) {
				TextView tvSkill = (TextView) getLayoutInflater().inflate(R.layout.skill_textview_item, null);
				try {
					tvSkill.setText(array.get(i).toString());
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				params.setMargins(0,0,UIUtils.p(4),0);
				tvSkill.setLayoutParams(params);
				ll.addView(tvSkill); 
			}
		} else {
			container.setVisibility(View.GONE);
		}
	}

	private void populateMenteeSkills() {
		populateSkills(user.getMenteeSkills(), llMenteeSkills, tvMenteeSkills);
	}

	private void populateMentorSkills() {
		populateSkills(user.getMentorSkills(), llMentorSkills, tvMentorSkills);
	}

	private void populateAddReview() {
		DataService.getRatingByUser(User.meId(), user.getFacebookId(), new GetCallback<Rating>() {
			@Override
			public void done(final Rating rating, ParseException e) {
				if(e == null && rating != null) {
					tvAddReview.setText("Edit review"); 
					rbMyRating.setRating((float)(double)rating.getRating());
				} else {
					tvAddReview.setText("Add review");
				}
				tvAddReview.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						UIUtils.login(getActivity(), "Login to add a review", Persona.MENTEE, new Async.Block<User>() {
							@Override
							public void call(final User result) {
								if (result == null) return;
								// Show an alert dialog to add a review
								final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
								final View view = getLayoutInflater().inflate(R.layout.progress_dialog, null);
								final RatingBar ratingBar = (RatingBar)view.findViewById(R.id.ratingBar);
								ratingBar.setStepSize(0.5f);
								ratingBar.setRating(rating != null ? (float)(double)rating.getRating() : 0);
								customizeProgressBar(ratingBar, true);
								builder.setTitle(tvAddReview.getText()).setView(view).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										
									}
								}).setPositiveButton("Submit", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										getProgressBar().setVisibility(View.VISIBLE);
										DataService.putRating(rating, user.getFacebookId(), ratingBar.getRating(), new Async.Block<Boolean>() {
											@Override
											public void call(Boolean result) {
												if (result.booleanValue()) {
													populateAverageRating();
													populateAddReview();
													getProgressBar().setVisibility(View.INVISIBLE);
												}
											}
										});
									}
								}).show();
							}
						}, false);
					}
				});
			}
		});
	}
	
	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		int id = item.getItemId();
		if (item.getTitle() == null) return true;
		if (id == R.id.miProfileAction) {
			if(item.getTitle().equals("Connect")) {
				UIUtils.login(getActivity(), "Login to connect to your mentor", Persona.MENTEE, new Async.Block<User>() {
					@Override
					public void call(User result) {
						if (result != null) {
							sendPushNotification();
							item.setTitle("Request Sent");
							DataService.addRequestsSent(user.getFacebookId());
						}
					}
				}, false);
			} else if (item.getTitle().equals("Email")) {
				DataService.removeResponsePending(user.getFacebookId());
				if (mIsResponse) {
		    		sendPushNotification();
		    		markConnected(user, User.me(), true);
		    	}
				Intent email = new Intent(Intent.ACTION_SEND);
				email.putExtra(Intent.EXTRA_EMAIL, new String[]{ user.getEmail() });
				email.setType("message/rfc822");
				email.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivityForResult(Intent.createChooser(email, "Choose an Email client:"), 1);
			} else if (item.getTitle().equals("Edit Profile")) {
				UIUtils.startActivity(getActivity(), EditProfileActivity.class);
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void markConnected(final User mentee, final User mentor, final boolean confirmed) {
		if (User.me() == null) return;
    	DataService.upsertRequest(mentee.getFacebookId(), mentor.getFacebookId(), confirmed, new Block<Boolean>() {
			@Override
			public void call(Boolean result) {
				if (result.booleanValue()) {
					mentee.getMentors().add(mentor.getFacebookId());
					if (confirmed) {
						mentor.getMentees().add(mentee.getFacebookId());
					}
				}
			}
		});
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
	    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
	    if (requestCode == 1) {
	    	if (mIsResponse) {
	    		sendPushNotification();
	    		markConnected(user, User.me(), true);
	    	}
	    } else {
	    	super.onActivityResult(requestCode, resultCode, data);
	    }
	}
	
	private void sendPushNotification() {
		if (User.me() == null) return;
		JSONObject obj;
		try {
			obj = new JSONObject();
			obj.put(MentorMeReceiver.alertKey, User.me().getDisplayName());
			obj.put(MentorMeReceiver.responseKey, mIsResponse);
			obj.put("action", MentorMeReceiver.intentAction);
			final StringBuilder builder = new StringBuilder();
			final JSONArray menteeSkills = User.me().getMenteeSkills();
			boolean useMentorSkills = true;
			if (menteeSkills != null && !mIsResponse) {
				useMentorSkills = false;
				builder.append("Seeking to learn ");
				for (int i = 0, count = menteeSkills.length(); i < count; ++i) {
					final Object skill = menteeSkills.get(i);
					builder.append(skill.toString());
					if (i != count - 1) {
						builder.append(", ");
					}
				}
			}
			if (useMentorSkills) {
				builder.append("Expert in ");
				final JSONArray mentorSkills = User.me().getMentorSkills();
				if (mentorSkills != null) {
					for (int i = 0, count = mentorSkills.length(); i < count; ++i) {
						final Object skill = mentorSkills.get(i);
						builder.append(skill.toString());
						if (i != count - 1) {
							builder.append(", ");
						}
					}
				}
			}
			obj.put(MentorMeReceiver.skillsKey, builder.toString());
			obj.put(ViewProfileActivity.USER_ID_KEY, User.meId());

			ParsePush push = new ParsePush();
			ParseQuery<ParseInstallation> query = ParseInstallation.getQuery();

			query.whereEqualTo("userId", user.getFacebookId()); 
			push.setQuery(query);
			push.setData(obj);
			push.sendInBackground();
			if (!mIsResponse) {
				markConnected(User.me(), user, false);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private void populateAverageRating() {
		DataService.getAverageRating(user.getFacebookId(), new FindCallback<Rating>() {			
			@Override
			public void done(List<Rating> ratings, ParseException e) {
				if(e == null) {
					if(ratings.size() > 0) {
						float numRating = 0;
						int count = 0;
						for (Rating rating : ratings) {
							if(rating.getRating() > 0) {
								count++;
								numRating += rating.getRating();
							}
						}
						numRating = numRating/count;
						tvNoOfRating.setText(Integer.toString(count) + " reviews");
						rbRating.setRating(numRating);
						
					} else {
						rbRating.setRating(0);
						tvNoOfRating.setText("0 reviews");
					}
				} else {
					e.printStackTrace();
				}				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.view_profile, menu);
		this.menu = menu;
		super.onCreateOptionsMenu(menu);
		updateMenuTitles();
		return true;
	}
	
	private void updateMenuTitles() {
		if (menu == null || user == null) return;
		final MenuItem item = menu.findItem(R.id.miProfileAction);
		if (User.me() == null) {
			item.setTitle("Connect");
			return;
		}
		final long meId = User.meId();
		final long currentUserId = user.getFacebookId();
		if (currentUserId == meId && !mIsResponse) {
			item.setTitle("Edit Profile");
		} else {
			DataService.getMentors(meId, new Runnable() {
				@Override
				public void run() {
					if (User.me() != null) {
						final ArrayList<Long> mentors = User.me().getMentors();
						if (mentors.contains(currentUserId)) {
							item.setTitle("Email");
						} else {
							item.setTitle(mIsResponse ? "Email" : DataService.isRequestsSent(currentUserId) ? "Request Sent" : "Connect");
						}
					}
				}
			});
		}
	}

}
