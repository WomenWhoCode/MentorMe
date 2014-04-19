package com.codepath.wwcmentorme.activities;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.codepath.wwcmentorme.R;
import com.codepath.wwcmentorme.adapters.MentorListAdapter;
import com.codepath.wwcmentorme.data.DataService;
import com.codepath.wwcmentorme.helpers.UIUtils;
import com.codepath.wwcmentorme.models.User;
import com.nhaarman.listviewanimations.swinginadapters.prepared.ScaleInAnimationAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;

public class MentorListActivity extends AppActivity implements
		android.location.LocationListener {
	private ListView lvMentors;
	private MentorListAdapter mentorListAdapter;
	private LocationManager locationManager;
	private String provider;
	private String mSkill;
	private Location mLocation;
	private ParseGeoPoint mGeoPoint;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mentor_list);		 	

		setCurrentLocation();
		
		lvMentors = (ListView) findViewById(R.id.lvMentors);
		mentorListAdapter = new MentorListAdapter(MentorListActivity.this, mGeoPoint);		
		ScaleInAnimationAdapter scaleInAnimationAdapter = new ScaleInAnimationAdapter(mentorListAdapter);
		scaleInAnimationAdapter.setAbsListView(lvMentors);
		lvMentors.setAdapter(scaleInAnimationAdapter);
		
		loadMentors(mGeoPoint);
		getActionBar().setDisplayHomeAsUpEnabled(false);
		
		setupListViewClickListener();
	}
	
	private void setupListViewClickListener() {
		lvMentors.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				final User user = (User) lvMentors.getItemAtPosition(position);
				final Intent intent = new Intent(MentorListActivity.this, ViewProfileActivity.class);
				intent.putExtra(ViewProfileActivity.USER_ID_KEY, user.getObjectId());
				intent.putExtra(ViewProfileActivity.LATITUDE_KEY, mGeoPoint.getLatitude());
				intent.putExtra(ViewProfileActivity.LONGITUDE_KEY, mGeoPoint.getLongitude());
				startActivity(intent);				
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
		getProgressBar().setVisibility(View.VISIBLE);
		DataService.getMentors(MentorListActivity.this, geoPoint, distance, mSkill, new FindCallback<User>() {
			@Override
			public void done(final List<User> users, ParseException e) {
				if (e == null) {
					mentorListAdapter.addAll(users);
					mentorListAdapter.addAll(users);
					mentorListAdapter.addAll(users);
					getProgressBar().setVisibility(View.INVISIBLE);
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
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onProviderDisabled(String provider) {
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
	}
	
	
	public void onShowMap(final MenuItem mi) {
		final ArrayList<String> markers = new ArrayList<String>();
		for (int i = 0, count = mentorListAdapter.getCount(); i < count; ++i) {
			final User user = mentorListAdapter.getItem(i);
			markers.add(user.getObjectId());
		}
		UIUtils.startActivity(this, MapActivity.class, MapActivity.INTENT_EXTRA_MARKERS, markers);
	}

}
