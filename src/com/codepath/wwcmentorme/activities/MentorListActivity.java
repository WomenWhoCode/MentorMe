package com.codepath.wwcmentorme.activities;

import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.codepath.wwcmentorme.R;
import com.codepath.wwcmentorme.adapters.MentorListAdapter;
import com.codepath.wwcmentorme.data.DataService;
import com.codepath.wwcmentorme.helpers.UIUtils;
import com.codepath.wwcmentorme.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;

public class MentorListActivity extends FragmentActivity implements
		android.location.LocationListener {
	private ListView lvMentors;
	private MentorListAdapter mentorListAdapter;
	private ProgressBar mProgressBar;
	private LocationManager locationManager;
	private String provider;
	private String mSkill;
	private double mLat;
	private double mLng;
	private ParseGeoPoint mGeoPoint;

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

		mLat = (double) (location.getLatitude());
		mLng = (double) (location.getLongitude());
		
		mGeoPoint = new ParseGeoPoint();
		mGeoPoint.setLatitude(mLat);
		mGeoPoint.setLongitude(mLng);
		
		mentorListAdapter = new MentorListAdapter(MentorListActivity.this, mGeoPoint);
		lvMentors.setAdapter(mentorListAdapter);
		
		loadMentors(mGeoPoint);
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
		DataService.getMentors(MentorListActivity.this, geoPoint, distance, mSkill, new FindCallback<User>() {

			@Override
			public void done(final List<User> users, ParseException e) {
				if (e == null) {
					mProgressBar.setVisibility(View.VISIBLE);				
						
					mentorListAdapter.addAll(users);
					mentorListAdapter.addAll(users);
					mentorListAdapter.addAll(users);
					
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
	
	public void onRefineResultsPress(MenuItem mi) {
		UIUtils.showActionSheet(this, getString(R.string.miRefineResults), getResources().getStringArray(R.array.skill_array), new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog, final int position) {
				String[] skills = getResources().getStringArray(R.array.skill_array);
				mSkill = skills[position];
				mentorListAdapter.clear();
				loadMentors(mGeoPoint);
			}
		});
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
	}

}
