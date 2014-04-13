package com.codepath.wwcmentorme.activities;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.codepath.wwcmentorme.R;
import com.codepath.wwcmentorme.adapters.MentorListAdapter;
import com.codepath.wwcmentorme.data.DataService;
import com.codepath.wwcmentorme.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;

public class MentorListActivity extends Activity implements
		android.location.LocationListener {
	private ListView lvMentors;
	private MentorListAdapter mentorListAdapter;
	private ProgressBar mProgressBar;
	private LocationManager locationManager;
	private String provider;

	@Override
	public void setContentView(View view) {
		init().addView(view);
	}

	@Override
	public void setContentView(int layoutResID) {
		getLayoutInflater().inflate(layoutResID, init(), true);
	}

	@Override
	public void setContentView(View view, ViewGroup.LayoutParams params) {
		init().addView(view, params);
	}

	private ViewGroup init() {
		super.setContentView(R.layout.progress);
		mProgressBar = (ProgressBar) findViewById(R.id.activity_bar);
		mProgressBar.setVisibility(View.INVISIBLE);
		return (ViewGroup) findViewById(R.id.activity_frame);
	}

	protected ProgressBar getProgressBar() {
		return mProgressBar;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mentor_list);

		lvMentors = (ListView) findViewById(R.id.lvMentors);
		

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		provider = locationManager.getBestProvider(criteria, false);
		Location location = locationManager.getLastKnownLocation(provider);

		if (location != null) {
			onLocationChanged(location);
		} else {
			loadMentors(null);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		locationManager.requestLocationUpdates(provider, 400, 1, this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		locationManager.removeUpdates(this);
	}

	private void loadMentors(final ParseGeoPoint geoPoint) {
		Double distance = 50.0;
		DataService.getMentors(geoPoint, distance, new FindCallback<User>() {

			@Override
			public void done(List<User> users, ParseException e) {
				if (e == null) {
					mProgressBar.setVisibility(View.VISIBLE);
					if (mentorListAdapter == null) {
						mentorListAdapter = new MentorListAdapter(MentorListActivity.this, geoPoint);
						mentorListAdapter.addAll(users);
						mentorListAdapter.addAll(users);
						mentorListAdapter.addAll(users);
						lvMentors.setAdapter(mentorListAdapter);

					}
					mProgressBar.setVisibility(View.INVISIBLE);
				} else {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.mentor_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onLocationChanged(Location location) {
		double lat = (double) (location.getLatitude());
		double lng = (double) (location.getLongitude());
		
		ParseGeoPoint geoPoint = new ParseGeoPoint();
		geoPoint.setLatitude(lat);
		geoPoint.setLongitude(lng);
		loadMentors(geoPoint);
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
