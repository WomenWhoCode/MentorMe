package com.codepath.wwcmentorme.activities;
import java.util.ArrayList;

import android.os.Bundle;
import android.view.View;

import com.codepath.wwcmentorme.R;
import com.codepath.wwcmentorme.helpers.UIUtils;
import com.codepath.wwcmentorme.models.User;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLoadedCallback;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseGeoPoint;

public class MapActivity extends AppActivity {
	public static final String INTENT_EXTRA_MARKERS = "markers";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_fragment);

		getProgressBar().setVisibility(View.VISIBLE);
		// Get a handle to the Map Fragment
		final MapFragment fragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
		final GoogleMap map = fragment.getMap();
		map.setOnMapLoadedCallback(new OnMapLoadedCallback() {
			@Override
			public void onMapLoaded() {
				getProgressBar().setVisibility(View.INVISIBLE);
			}
		});
		map.setMyLocationEnabled(true);
		fragment.getView().post(new Runnable() {
			@Override
			public void run() {
				if (getIntent().hasExtra(INTENT_EXTRA_MARKERS)) {
					LatLngBounds.Builder builder = new LatLngBounds.Builder();
					final ArrayList<String> markers = getIntent().getStringArrayListExtra(INTENT_EXTRA_MARKERS);
					for (final String objectId : markers) {
						final User user = User.getUser(objectId);
						final ParseGeoPoint pt = user.getLocation();
						final LatLng latlng = new LatLng(pt.getLatitude(), pt.getLongitude());
						map.addMarker(new MarkerOptions()
						.title(user.getDisplayName())
						.icon(BitmapDescriptorFactory.defaultMarker())
						.snippet(user.getPosition())
						.position(latlng));
						builder.include(latlng);
					}
					LatLngBounds bounds = builder.build();
					map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, UIUtils.p(100)));
				}
			}
		});
	}	
}
