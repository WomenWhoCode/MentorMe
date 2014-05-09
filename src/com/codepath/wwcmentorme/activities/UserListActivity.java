package com.codepath.wwcmentorme.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.FragmentManager.OnBackStackChangedListener;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.codepath.wwcmentorme.R;
import com.codepath.wwcmentorme.adapters.MentorListAdapter;
import com.codepath.wwcmentorme.data.DataService;
import com.codepath.wwcmentorme.helpers.Async;
import com.codepath.wwcmentorme.helpers.Constants.Persona;
import com.codepath.wwcmentorme.helpers.Constants.UserDisplayMode;
import com.codepath.wwcmentorme.models.User;
import com.nhaarman.listviewanimations.swinginadapters.prepared.ScaleInAnimationAdapter;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;

public class UserListActivity extends AppActivity implements
		android.location.LocationListener, OnBackStackChangedListener {
	private ListView lvMentors;
	private MentorListAdapter mentorListAdapter;
	private ParseGeoPoint mGeoPoint;
	private LocationManager locationManager;
	private Location mLocation;
	private String provider;
	private long userId;
	private Persona mPersona;
	private UserDisplayMode mUserDisplayMode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_list_fragment);
		setCurrentLocation();

		if (getIntent().hasExtra("userId")) {
			userId = getIntent().getLongExtra("userId", 0);
		}
		
		if (getIntent().hasExtra("persona")) {
			mPersona = (Persona)getIntent().getSerializableExtra("persona");
		} else {
			mPersona = Persona.MENTOR;
		}
		
		if (getIntent().hasExtra("userDisplayMode")) {
			mUserDisplayMode = (UserDisplayMode)getIntent().getSerializableExtra("userDisplayMode");
		} else {
			mUserDisplayMode = UserDisplayMode.PROFILE;
		}
		
		setTitle();		

		Async.dispatchMain(new Runnable() {
			@Override
			public void run() {
				populateListView();
			}
		});

	}

	private void setTitle() {
		DataService.getUser(userId, new GetCallback<User>() {
			@Override
			public void done(User user, ParseException e) {
				if (e == null) {
					StringBuilder title = new StringBuilder();
					if (userId == User.meId()) {
						title.append("Your ");
					} else {
						title.append(user.getFirstName() + "'s");
					}
					if (mUserDisplayMode.equals(UserDisplayMode.CHAT)) {
						title.append("Messages");
					} else {
						if (mPersona.equals(Persona.MENTOR)) {
							title.append("Incoming Requests");
						} else {
							title.append("Outgoing Requests");
						}
					}
					setTitle(title);
				} else {
					e.printStackTrace();
				}

			}
		});

	}

	private void setCurrentLocation() {
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		provider = locationManager.getBestProvider(criteria, false);
		mLocation = locationManager.getLastKnownLocation(provider);

		if (mLocation != null) {
			mGeoPoint = new ParseGeoPoint();
			mGeoPoint.setLatitude(mLocation.getLatitude());
			mGeoPoint.setLongitude(mLocation.getLongitude());
			locationManager.removeUpdates(this);
		} else {
			mGeoPoint = new ParseGeoPoint(37.7833, -122.4167);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		// locationManager.requestLocationUpdates(provider, 400, 1, this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		// locationManager.removeUpdates(this);
	}

	private void populateListView() {
		lvMentors = (ListView) findViewById(R.id.lvMentors);
		mentorListAdapter = new MentorListAdapter(UserListActivity.this,
				mGeoPoint, mPersona.equals(Persona.MENTOR) ? Persona.MENTEE : Persona.MENTOR, mUserDisplayMode);
		ScaleInAnimationAdapter scaleInAnimationAdapter = new ScaleInAnimationAdapter(
				mentorListAdapter);
		scaleInAnimationAdapter.setAbsListView(lvMentors);
		lvMentors.setAdapter(scaleInAnimationAdapter);
		if (mPersona.equals(Persona.MENTOR)) {
			loadRequests(userId, true);
		} else {
			loadRequests(userId, false);
		}
		setupListViewClickListener();
		didChangeContentView();
	}

	private void loadRequests(final long userId, final boolean incoming) {
		getProgressBar().setVisibility(View.VISIBLE);
		DataService.getConnections(userId, new Runnable() {
			@Override
			public void run() {
				final List<User> users = User.getUsers(User.getUser(userId).getConnections(incoming));
				mentorListAdapter.addAll(users);
				if (mUserDisplayMode.equals(UserDisplayMode.CHAT)) {
					DataService.getConnections(userId, new Runnable() {
						@Override
						public void run() {
							final List<User> otherUsers = User.getUsers(User.getUser(userId).getConnections(!incoming));
							final ArrayList<User> newUsers = new ArrayList<User>();
							for (final User user : otherUsers) {
								if (!users.contains(user)) {
									newUsers.add(user);
								}
							}
							mentorListAdapter.addAll(newUsers);
							getProgressBar().setVisibility(View.INVISIBLE);
						}
					}, !incoming);
				} else {
					getProgressBar().setVisibility(View.INVISIBLE);
				}
			}
		}, incoming);
	}

	private void setupListViewClickListener() {
		lvMentors.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				final User user = (User) lvMentors.getItemAtPosition(position);
				final Intent intent = new Intent(UserListActivity.this,
						ViewProfileActivity.class);
				intent.putExtra(ViewProfileActivity.USER_ID_KEY,
						user.getFacebookId());
				intent.putExtra(ViewProfileActivity.LATITUDE_KEY,
						mGeoPoint.getLatitude());
				intent.putExtra(ViewProfileActivity.LONGITUDE_KEY,
						mGeoPoint.getLongitude());
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.user_list, menu);
		return true;
	}

	@Override
	public void onBackStackChanged() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

}
