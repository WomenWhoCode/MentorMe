package com.codepath.wwcmentorme.adapters;

import org.json.JSONException;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.wwcmentorme.R;
import com.codepath.wwcmentorme.activities.MentorListActivity;
import com.codepath.wwcmentorme.activities.UserListActivity;
import com.codepath.wwcmentorme.activities.ViewProfileActivity;
import com.codepath.wwcmentorme.app.MentorMeApp;
import com.codepath.wwcmentorme.helpers.ViewHolder;
import com.codepath.wwcmentorme.helpers.Constants.UserType;
import com.codepath.wwcmentorme.models.User;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class DrawerListAdapter extends
		ArrayAdapter<DrawerListAdapter.DrawerItem> {
	public static final int HEADER_ID = android.R.id.home;

	public static class DrawerItem {
		public int stringId;
		public int iconId;

		public DrawerItem(final int stringId, final int iconId) {
			this.stringId = stringId;
			this.iconId = iconId;
		}
	}

	public DrawerListAdapter(Context context) {
		super(context, 0);
	}

	public View getHeaderView() {
		final User user = MentorMeApp.getCurrentUser();
		if (user == null) {
			return getView(new DrawerItem(R.string.fb_login,
					R.drawable.ic_fb_login), null, null);
		}
		LayoutInflater inflator = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View view = inflator.inflate(R.layout.user_header, null);
		view.setId(HEADER_ID);
		final ViewHolder.UserItem holder = new ViewHolder.UserItem();
		holder.ivMentorProfile = (ImageView) view
				.findViewById(R.id.ivMentorProfile);
		holder.tvFirstName = (TextView) view.findViewById(R.id.tvFirstName);
		holder.tvLastName = (TextView) view.findViewById(R.id.tvLastName);
		holder.tvPosition = (TextView) view.findViewById(R.id.tvPosition);
		try {
			populate(holder, user);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return view;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return getView(getItem(position), convertView, parent);
	}

	private View getView(final DrawerItem item, View convertView,
			ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			LayoutInflater inflator = (LayoutInflater) getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflator.inflate(R.layout.drawer_list_item, null);
			final ViewHolder.DrawerItem holder = new ViewHolder.DrawerItem();
			holder.btnItem = (Button) view.findViewById(R.id.btnDrawerItem);
			view.setTag(holder);
		}
		final ViewHolder.DrawerItem holder = (ViewHolder.DrawerItem) view
				.getTag();
		holder.btnItem.setCompoundDrawablesWithIntrinsicBounds(item.iconId, 0,
				0, 0);
		holder.btnItem.setText(item.stringId);
		holder.btnItem.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Button btnItem = (Button) v;
				String buttonText = btnItem.getText().toString();
				if (buttonText == getContext().getResources().getString(
						R.string.drawer_requests_received)) {
					final Intent intent = new Intent(getContext(),
							UserListActivity.class);
					intent.putExtra("usertype", UserType.MENTOR.toString());
					intent.putExtra("userId", MentorMeApp.getCurrentUser().getFacebookId());
					getContext().startActivity(intent);
				} else if (buttonText == getContext().getResources().getString(
						R.string.drawer_requests_Sent)) {
					final Intent intent = new Intent(getContext(),
							UserListActivity.class);
					intent.putExtra("usertype", UserType.MENTEE.toString());
					intent.putExtra("userId", MentorMeApp.getCurrentUser().getFacebookId());
					getContext().startActivity(intent);
				}

			}
		});
		return view;
	}

	private void populate(final ViewHolder.UserItem holder, User user)
			throws JSONException {
		final ImageLoader imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(getContext()));
		imageLoader.displayImage(user.getProfileImageUrl(200),
				holder.ivMentorProfile);

		holder.tvFirstName.setText(user.getFirstName());
		holder.tvLastName.setText(user.getLastName());

		String formattedPosition = user.getJobTitle() + ", "
				+ user.getCompanyName();
		holder.tvPosition.setText(Html.fromHtml(formattedPosition));
	}
}
