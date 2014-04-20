package com.codepath.wwcmentorme.activities;

import java.util.List;

import com.codepath.wwcmentorme.R;
import com.codepath.wwcmentorme.adapters.MentorListAdapter;
import com.codepath.wwcmentorme.data.DataService;
import com.codepath.wwcmentorme.helpers.Async;
import com.codepath.wwcmentorme.models.Request;
import com.codepath.wwcmentorme.models.User;
import com.nhaarman.listviewanimations.swinginadapters.prepared.ScaleInAnimationAdapter;
import com.parse.FindCallback;
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
	private String usertype;
	private int userId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_list_fragment);
		setCurrentLocation();

		if (getIntent().hasExtra("usertype")) {
			usertype = getIntent().getStringExtra("usertype");
		}
		
		if (getIntent().hasExtra("userId")) {
			userId = getIntent().getIntExtra("userId", 0);
		}

		Async.dispatchMain(new Runnable() {
			@Override
			public void run() {
				populateListView();
			}
		});

	}

	private void setCurrentLocation() {
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		provider = locationManager.getBestProvider(criteria, false);
		mLocation = locationManager.getLastKnownLocation(provider);

		mGeoPoint = new ParseGeoPoint();
		mGeoPoint.setLatitude(mLocation.getLatitude());
		mGeoPoint.setLongitude(mLocation.getLongitude());
		locationManager.removeUpdates(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		//locationManager.requestLocationUpdates(provider, 400, 1, this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		//locationManager.removeUpdates(this);
	}

	private void populateListView() {
		lvMentors = (ListView) findViewById(R.id.lvMentors);
		mentorListAdapter = new MentorListAdapter(UserListActivity.this,
				mGeoPoint);
		ScaleInAnimationAdapter scaleInAnimationAdapter = new ScaleInAnimationAdapter(
				mentorListAdapter);
		scaleInAnimationAdapter.setAbsListView(lvMentors);
		lvMentors.setAdapter(scaleInAnimationAdapter);
		if (usertype.equals("Mentor")) {
			loadIncomingRequests(userId);
		} else {
			loadOutgoingRequests(userId);
		}
		setupListViewClickListener();
		didChangeContentView();
	}

	private void loadIncomingRequests(int userId) {
		getProgressBar().setVisibility(View.VISIBLE);

		DataService.getIncomingRequests(userId, new FindCallback<Request>() {

			@Override
			public void done(List<Request> requests, ParseException e) {
				if (e == null) {
					if (requests != null && requests.size() > 0) {

						for (Request request : requests) {

							DataService.getUser(request.getMenteeId(),
									new FindCallback<User>() {

										@Override
										public void done(List<User> mentee,
												ParseException e) {
											if (e == null) {
												if (mentee != null) {
													for (User menteeUser : mentee) {
														mentorListAdapter
																.add(menteeUser);
														break;
													}
												}
											} else {
												e.printStackTrace();
											}
										}
									});
						}

					}

				} else {
					e.printStackTrace();
				}

			}
		});
		getProgressBar().setVisibility(View.INVISIBLE);
	}

	private void loadOutgoingRequests(int userId) {
		getProgressBar().setVisibility(View.VISIBLE);

		DataService.getOutgoingRequests(userId, new FindCallback<Request>() {

			@Override
			public void done(List<Request> requests, ParseException e) {
				if (e == null) {
					if (requests != null && requests.size() > 0) {

						for (Request request : requests) {

							DataService.getUser(request.getMentorId(),
									new FindCallback<User>() {

										@Override
										public void done(List<User> mentee,
												ParseException e) {
											if (e == null) {
												if (mentee != null) {
													for (User menteeUser : mentee) {
														mentorListAdapter
																.add(menteeUser);
														break;
													}
												}
											} else {
												e.printStackTrace();
											}
										}
									});
						}

					}

				} else {
					e.printStackTrace();
				}

			}
		});
		getProgressBar().setVisibility(View.INVISIBLE);
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
