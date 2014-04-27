package com.codepath.wwcmentorme.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.app.FragmentManager.OnBackStackChangedListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.codepath.wwcmentorme.R;
import com.codepath.wwcmentorme.adapters.DrawerListAdapter;
import com.codepath.wwcmentorme.adapters.MentorListAdapter;
import com.codepath.wwcmentorme.data.DataService;
import com.codepath.wwcmentorme.helpers.Async;
import com.codepath.wwcmentorme.helpers.NotificationCenter;
import com.codepath.wwcmentorme.helpers.UIUtils;
import com.codepath.wwcmentorme.models.User;
import com.google.android.gms.maps.MapFragment;
import com.nhaarman.listviewanimations.swinginadapters.prepared.ScaleInAnimationAdapter;
import com.parse.FindCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;

public class MentorListActivity extends AppActivity implements
android.location.LocationListener, OnBackStackChangedListener, NotificationCenter.Listener<User> {
	public static final class ListFragment extends Fragment {
		private View mCachedView;
		private ViewGroup mCachedViewGroup;
		public ListFragment() {
			setRetainInstance(true);
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			if (mCachedViewGroup == container) return mCachedView;
			mCachedViewGroup = container;
			mCachedView = inflater.inflate(R.layout.user_list_fragment, container, false);
			mCachedView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
			return mCachedView;
		}
	}

	private ListView lvMentors;
	private MentorListAdapter mentorListAdapter;
	private LocationManager locationManager;
	private String provider;
	private String mSkill;
	private Location mLocation;
	private ParseGeoPoint mGeoPoint;
	private boolean mShowingBack;

	private static final ListFragment sListFragment = new ListFragment();
	private static final MapFragment sMapFragment = new MapFragment();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mentor_list);
		setCurrentLocation();
    	ParseAnalytics.trackAppOpened(getIntent());

		if (savedInstanceState == null) {
			// If there is no saved instance state, add a fragment representing the
			// front of the card to this activity. If there is saved instance state,
			// this fragment will have already been added to the activity.
			getFragmentManager()
			.beginTransaction()
			.add(R.id.rlContainer, sListFragment)
			.commit();
		} else {
			mShowingBack = (getFragmentManager().getBackStackEntryCount() > 0);
		}

		Async.dispatchMain(new Runnable() {
			@Override
			public void run() {
				populateListView();
			}
		});
		enableDrawer((DrawerLayout) findViewById(R.id.drawer_layout));
		getFragmentManager().addOnBackStackChangedListener(this);
		NotificationCenter.registerListener(this, User.NOTIFICATION_ME);
	}

	private void populateListView() {
		lvMentors = (ListView) findViewById(R.id.lvMentors);
		mentorListAdapter = new MentorListAdapter(MentorListActivity.this, mGeoPoint);		
		ScaleInAnimationAdapter scaleInAnimationAdapter = new ScaleInAnimationAdapter(mentorListAdapter);
		scaleInAnimationAdapter.setAbsListView(lvMentors);
		lvMentors.setAdapter(scaleInAnimationAdapter);
		loadMentors(mGeoPoint);
		setupListViewClickListener();
		didChangeContentView();
	}

	private void setupListViewClickListener() {
		lvMentors.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				final User user = (User) lvMentors.getItemAtPosition(position);
				final Intent intent = new Intent(MentorListActivity.this, ViewProfileActivity.class);
				intent.putExtra(ViewProfileActivity.USER_ID_KEY, user.getFacebookId());
				intent.putExtra(ViewProfileActivity.LATITUDE_KEY, mGeoPoint.getLatitude());
				intent.putExtra(ViewProfileActivity.LONGITUDE_KEY, mGeoPoint.getLongitude());
				startActivity(intent);
			}
		});
	}
	
	@Override
	public void didChange(User oldValue, User newValue) {
		setupDrawer();
	}
	
	private static final int HEADER_ID = android.R.id.home;

	private void setupDrawer() {
		UIUtils.getOrCreateLoggedInUser(getActivity(), new Async.Block<User>() {
			@Override
			public void call(final User user) {
				final ListView listView = (ListView)findViewById(R.id.left_drawer);
				final DrawerListAdapter adapter = new DrawerListAdapter(getActivity());
				LinearLayout headerView = (LinearLayout)listView.findViewById(HEADER_ID);
				if (headerView == null) {
					headerView = new LinearLayout(getActivity());
					headerView.setOrientation(LinearLayout.VERTICAL);
					headerView.setId(HEADER_ID);
					listView.addHeaderView(headerView);
				}
				headerView.removeAllViews();
				headerView.addView(adapter.getHeaderView());
				listView.setAdapter(adapter);
				listView.setOnItemClickListener(new SlideMenuClickListener());
				if (User.me() != null) {
					adapter.add(new DrawerListAdapter.DrawerItem(R.string.drawer_edit_profile, R.drawable.ic_edit));
					adapter.add(new DrawerListAdapter.DrawerItem(R.string.drawer_requests_received, R.drawable.ic_inbox));
					adapter.add(new DrawerListAdapter.DrawerItem(R.string.drawer_requests_Sent, R.drawable.ic_outbox));
					adapter.add(new DrawerListAdapter.DrawerItem(R.string.drawer_sign_out, R.drawable.ic_signout));
				}				
			}
		});
	}

	private class SlideMenuClickListener implements
	ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			displayView(position);
		}
	}

	private void displayView(int position) {
		switch (position) {
		case 0:
			final Intent intent = new Intent(MentorListActivity.this, ViewProfileActivity.class);
			intent.putExtra(ViewProfileActivity.USER_ID_KEY, User.meId());
			intent.putExtra(ViewProfileActivity.LATITUDE_KEY, mGeoPoint.getLatitude());
			intent.putExtra(ViewProfileActivity.LONGITUDE_KEY, mGeoPoint.getLongitude());
			startActivity(intent);
			break;
		default:
			break;
		}
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

	private void loadMentors(final ParseGeoPoint geoPoint) {
		Double distance = 50.0;
		getProgressBar().setVisibility(View.VISIBLE);
		DataService.getMentors(MentorListActivity.this, geoPoint, distance, mSkill, new FindCallback<User>() {
			@Override
			public void done(final List<User> users, ParseException e) {
				if (e == null) {
					User.saveAllUsers(users);
					DataService.getMentees(User.getUserFacebookIds(users), new Runnable() {
						@Override
						public void run() {
							mentorListAdapter.addAll(User.sortedByMenteeCount(users));
							getProgressBar().setVisibility(View.INVISIBLE);
						}
					});
				} else {
					e.printStackTrace();
				}
				setupDrawer();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (mShowingBack) {
			getMenuInflater().inflate(R.menu.map, menu);
		} else {
			getMenuInflater().inflate(R.menu.mentor_list, menu);
		}
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

	private void flipCard() {
		if (mShowingBack) {
			getFragmentManager().popBackStack();
			return;
		}

		// Flip to the back.

		mShowingBack = true;

		// Create and commit a new fragment transaction that adds the fragment for the back of
		// the card, uses custom animations, and is part of the fragment manager's back stack.

		getFragmentManager()
		.beginTransaction()

		// Replace the default fragment animations with animator resources representing
		// rotations when switching to the back of the card, as well as animator
		// resources representing rotations when flipping back to the front (e.g. when
		// the system Back button is pressed).
		.setCustomAnimations(
				R.animator.card_flip_right_in, R.animator.card_flip_right_out,
				R.animator.card_flip_left_in, R.animator.card_flip_left_out)

				// Replace any fragments currently in the container view with a fragment
				// representing the next page (indicated by the just-incremented currentPage
				// variable).
				.replace(R.id.rlContainer, sMapFragment)

				// Add this transaction to the back stack, allowing users to press Back
				// to get to the front of the card.
				.addToBackStack(null)

				// Commit the transaction.
				.commit();

		// Defer an invalidation of the options menu (on modern devices, the action bar). This
		// can't be done immediately because the transaction may not yet be committed. Commits
		// are asynchronous in that they are posted to the main thread's message loop.
		Async.dispatch(new Runnable() {
			@Override
			public void run() {
				invalidateOptionsMenu();
			}
		});
	}

	public void onShowMap(final MenuItem mi) {
		if (mShowingBack) {
			flipCard();
			Async.dispatchMain(new Runnable() {
				@Override
				public void run() {
					getProgressBar().setVisibility(View.INVISIBLE);
				}
			});
		} else {
			final ArrayList<String> markers = new ArrayList<String>();
			for (int i = 0, count = mentorListAdapter.getCount(); i < count; ++i) {
				final User user = mentorListAdapter.getItem(i);
				markers.add(String.valueOf(user.getFacebookId()));
			}
			flipCard();
			Async.dispatchMain(new Runnable() {
				@Override
				public void run() {
					MapActivity.populateMapFragment(sMapFragment, getProgressBar(), markers);
				}
			});
		}
	}

	@Override
	public void onBackStackChanged() {
		mShowingBack = (getFragmentManager().getBackStackEntryCount() > 0);
		// When the back stack changes, invalidate the options menu (action bar).
		invalidateOptionsMenu();
	}

}
