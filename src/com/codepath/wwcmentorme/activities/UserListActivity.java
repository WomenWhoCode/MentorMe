package com.codepath.wwcmentorme.activities;

import com.codepath.wwcmentorme.R;
import com.codepath.wwcmentorme.adapters.MentorListAdapter;
import com.codepath.wwcmentorme.data.DataService;
import com.codepath.wwcmentorme.helpers.Async;
import com.codepath.wwcmentorme.helpers.Constants.UserType;
import com.codepath.wwcmentorme.models.User;
import com.nhaarman.listviewanimations.swinginadapters.prepared.ScaleInAnimationAdapter;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.FragmentManager.OnBackStackChangedListener;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class UserListActivity extends AppActivity implements
		android.location.LocationListener, OnBackStackChangedListener {
	private ListView lvMentors;
	private MentorListAdapter mentorListAdapter;
	private ParseGeoPoint mGeoPoint;
	private LocationManager locationManager;
	private Location mLocation;
	private String provider;
	private UserType usertype;
	private long userId;
	private int mPersona;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_list_fragment);
		setCurrentLocation();

		if (getIntent().hasExtra("usertype")) {
			usertype = UserType.valueOf(getIntent().getStringExtra("usertype"));
		}

		if (getIntent().hasExtra("userId")) {
			userId = getIntent().getLongExtra("userId", 0);
		}
		
		mPersona = getIntent().getIntExtra("persona", EditProfileActivity.PERSONA_MENTOR);
		
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
						title.append("Your");
					} else {
						title.append(user.getFirstName() + "'s");
					}

					if (usertype.equals(UserType.MENTOR)) {
						title.append(" Incoming Requests");
					} else {
						title.append(" Outgoing Requests");
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
				mGeoPoint, mPersona);
		ScaleInAnimationAdapter scaleInAnimationAdapter = new ScaleInAnimationAdapter(
				mentorListAdapter);
		scaleInAnimationAdapter.setAbsListView(lvMentors);
		lvMentors.setAdapter(scaleInAnimationAdapter);
		if (usertype.equals(UserType.MENTOR)) {
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
				mentorListAdapter.addAll(User.getUsers(User.getUser(userId).getConnections(incoming)));
				getProgressBar().setVisibility(View.INVISIBLE);
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
