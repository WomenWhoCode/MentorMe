package com.codepath.wwcmentorme.adapters;

import com.codepath.wwcmentorme.R;
import com.codepath.wwcmentorme.helpers.RoundedImageView;
import com.codepath.wwcmentorme.helpers.Utils;
import com.codepath.wwcmentorme.helpers.ViewHolder;
import com.codepath.wwcmentorme.models.User;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MentorListAdapter extends ArrayAdapter<User> {
	private ImageLoader mImageLoader;
	private ParseGeoPoint currentGeoPoint;

	public MentorListAdapter(Context context, ParseGeoPoint geoPoint) {
		super(context, 0);
		currentGeoPoint = geoPoint;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			LayoutInflater inflator = (LayoutInflater) getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflator.inflate(R.layout.mentor_list_item, null);
			final ViewHolder.UserItem holder = new ViewHolder.UserItem();
			holder.ivMentorProfile = (RoundedImageView) view.findViewById(R.id.ivMentorProfile);
			holder.tvFirstName = (TextView) view.findViewById(R.id.tvFirstName);
			holder.tvLastName = (TextView) view.findViewById(R.id.tvLastName);
			holder.tvPosition = (TextView) view.findViewById(R.id.tvPosition);
			holder.tvAbout = (TextView) view.findViewById(R.id.tvAbout);
			holder.tvDistance = (TextView) view.findViewById(R.id.tvDistance);
			view.setTag(holder);
		}
		final ViewHolder.UserItem holder = (ViewHolder.UserItem) view
				.getTag();
		final User user = (User) getItem(position);
		populate(holder, user);		
		return view;
	}
	
	private void populate(ViewHolder.UserItem holder, User user) {
		mImageLoader = ImageLoader.getInstance();
		mImageLoader.init(ImageLoaderConfiguration.createDefault(getContext()));
		mImageLoader.displayImage(user.getProfileImageUrl(200), holder.ivMentorProfile);
		
		holder.tvFirstName.setText(user.getFirstName());
		holder.tvLastName.setText(user.getLastName());
		
		String formattedPosition = "<b>" + user.getJobTitle() + "</b>" + ", " + user.getCompanyName();
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
		
	}

}
