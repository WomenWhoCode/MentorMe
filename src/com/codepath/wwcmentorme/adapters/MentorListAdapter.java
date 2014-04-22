package com.codepath.wwcmentorme.adapters;

import org.json.JSONException;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.wwcmentorme.R;
import com.codepath.wwcmentorme.data.DataService;
import com.codepath.wwcmentorme.helpers.Utils;
import com.codepath.wwcmentorme.helpers.ViewHolder;
import com.codepath.wwcmentorme.models.User;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.parse.CountCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;

public class MentorListAdapter extends ArrayAdapter<User> {
	private ParseGeoPoint currentGeoPoint;
	
	private static boolean sImageLoaderInitialized = false;

	public MentorListAdapter(Context context, ParseGeoPoint geoPoint) {
		super(context, 0);
		currentGeoPoint = geoPoint;
		if (!sImageLoaderInitialized) {
			sImageLoaderInitialized = true;
			final ImageLoader imageLoader = ImageLoader.getInstance();
			imageLoader.init(ImageLoaderConfiguration.createDefault(getContext()));
		}
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			LayoutInflater inflator = (LayoutInflater) getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflator.inflate(R.layout.mentor_list_item, null);
			final ViewHolder.UserItem holder = new ViewHolder.UserItem();
			holder.ivMentorProfile = (ImageView) view.findViewById(R.id.ivMentorProfile);
			holder.tvFirstName = (TextView) view.findViewById(R.id.tvFirstName);
			holder.tvLastName = (TextView) view.findViewById(R.id.tvLastName);
			holder.tvPosition = (TextView) view.findViewById(R.id.tvPosition);
			holder.tvAbout = (TextView) view.findViewById(R.id.tvAbout);
			holder.tvDistance = (TextView) view.findViewById(R.id.tvDistance);
			holder.tvMenteeCount = (TextView) view.findViewById(R.id.tvMenteeCount);
			holder.tvSkill1 = (TextView) view.findViewById(R.id.tvSkill1);
			holder.tvSkill2 = (TextView) view.findViewById(R.id.tvSkill2);
			holder.tvSkill3 = (TextView) view.findViewById(R.id.tvSkill3);
			view.setTag(holder);
		}
		final ViewHolder.UserItem holder = (ViewHolder.UserItem) view.getTag();
		final User user = (User) getItem(position);
		try {
			populate(holder, user);
		} catch (JSONException e) {
			e.printStackTrace();
		}		
		return view;
	}
	
	private void populate(final ViewHolder.UserItem holder, User user) throws JSONException {
		final ImageLoader imageLoader = ImageLoader.getInstance();
		imageLoader.displayImage(user.getProfileImageUrl(200), holder.ivMentorProfile);
		
		holder.tvFirstName.setText(user.getFirstName());
		holder.tvLastName.setText(user.getLastName());
		
		String formattedPosition = user.getJobTitle()  + ", " + user.getCompanyName();
		holder.tvPosition.setText(Html.fromHtml(formattedPosition));
		
		if (user.getAboutMe().length() > 120) {
			holder.tvAbout.setText(user.getAboutMe().substring(0, 120) + "...");
		} else {
			holder.tvAbout.setText(user.getAboutMe());
		}
		
		if(user.getLocation() != null) {
			Double distance = Utils.getDistance(currentGeoPoint.getLatitude(), currentGeoPoint.getLongitude(), user.getLocation().getLatitude(), user.getLocation().getLongitude());
			holder.tvDistance.setText(Utils.formatDouble(distance) + "mi"); 
		}
		
		final int numMentees = user.getMentees().size();
		
		holder.tvMenteeCount.setText(Utils.formatNumber(Integer
				.toString(numMentees)) + " " + getContext().getResources().getQuantityString(R.plurals.mentee, numMentees));
		
		holder.tvSkill1.setText(null);
		holder.tvSkill2.setText(null);
		holder.tvSkill3.setText(null);
		
		if(user.getMentorSkills() != null && user.getMentorSkills().length() > 0) {
			for(int i = 0; i <= user.getMentorSkills().length() - 1; i++) {
				if(i == 0) {
					holder.tvSkill3.setText(user.getMentorSkills().get(i).toString());
				}
				if(i == 1) {
					holder.tvSkill2.setText(user.getMentorSkills().get(i).toString());
				}
				if(i == 2) {
					holder.tvSkill1.setText(user.getMentorSkills().get(i).toString());
					break; 
				}
			}
			if(holder.tvSkill1.getText().length() == 0)  holder.tvSkill1.setVisibility(View.INVISIBLE);
			if(holder.tvSkill2.getText().length() == 0)  holder.tvSkill2.setVisibility(View.INVISIBLE);
			if(holder.tvSkill3.getText().length() == 0)  holder.tvSkill3.setVisibility(View.INVISIBLE);
		}
	}
}
