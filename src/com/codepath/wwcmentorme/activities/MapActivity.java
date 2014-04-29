package com.codepath.wwcmentorme.activities;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.codepath.wwcmentorme.R;
import com.codepath.wwcmentorme.helpers.UIUtils;
import com.codepath.wwcmentorme.models.User;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLoadedCallback;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseGeoPoint;

public class MapActivity extends AppActivity {
	public static final String INTENT_EXTRA_MARKERS = "markers";
	public static HashMap <Marker, Object> markerMap = new HashMap <Marker, Object>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_fragment);
	}
	
	public static void populateMapFragment(final MapFragment fragment, final ProgressBar progressBar, final ArrayList<String> markers, final Context context, final ParseGeoPoint mGeoPoint) {
		final GoogleMap map = fragment.getMap();
		map.setOnMapLoadedCallback(new OnMapLoadedCallback() {
			@Override
			public void onMapLoaded() {
			}
		});
		map.setMyLocationEnabled(true);
		
		
		fragment.getView().post(new Runnable() {
			@Override
			public void run() {
				if (markers != null && markers.size() > 0) {
					LatLngBounds.Builder builder = new LatLngBounds.Builder();
					for (final String objectId : markers) {
						final User user = User.getUser(Long.parseLong(objectId));
						final ParseGeoPoint pt = user.getLocation();
						final LatLng latlng = new LatLng(pt.getLatitude(), pt.getLongitude());
						Marker marker = map.addMarker(new MarkerOptions()
						.title(user.getDisplayName())
						.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
						.snippet(user.getPosition())
						.position(latlng));
						markerMap.put(marker, user);
						builder.include(latlng);	
					}
					LatLngBounds bounds = builder.build();
					map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, UIUtils.p(100)));	
					
					
					map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
						
						@Override
						public void onInfoWindowClick(Marker marker) {
							User user = (User) markerMap.get(marker);
							if(user != null) {
								final Intent intent = new Intent(context, ViewProfileActivity.class);
								intent.putExtra(ViewProfileActivity.USER_ID_KEY, user.getFacebookId());
								intent.putExtra(ViewProfileActivity.LATITUDE_KEY, mGeoPoint.getLatitude());
								intent.putExtra(ViewProfileActivity.LONGITUDE_KEY, mGeoPoint.getLongitude());
								context.startActivity(intent);
							}
						}
					});
				}
				
				
			}
		});
		
	}
	
}
