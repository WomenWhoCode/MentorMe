package com.codepath.wwcmentorme.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.wwcmentorme.R;
import com.codepath.wwcmentorme.helpers.Utils;
import com.codepath.wwcmentorme.helpers.ViewHolder;
import com.codepath.wwcmentorme.models.Message;
import com.codepath.wwcmentorme.models.User;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ChatAdapter extends ArrayAdapter<Message> {
	public ChatAdapter(Context context) {
		super(context, 0);
	}
	
	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		return getView(getItem(position), convertView, parent);
	}
	
	private View getView(final Message message, final View convertView, final ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			LayoutInflater inflator = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflator.inflate(R.layout.chat_list_item, null);
			final ViewHolder.ChatItem holder = new ViewHolder.ChatItem();
			holder.ivUserProfile = (ImageView)view.findViewById(R.id.ivUserProfile);
			holder.tvMessage = (TextView)view.findViewById(R.id.tvMessage);
			holder.tvTime = (TextView)view.findViewById(R.id.tvTime);
			view.setTag(holder);
		}
		populateView(view, message);
		return view;
	}
	
	private void populateView(final View view, final Message message) {
		final ViewHolder.ChatItem holder = (ViewHolder.ChatItem) view.getTag();
		final ImageLoader imageLoader = ImageLoader.getInstance();
		final User user = User.getUser(message.getUserId());
		imageLoader.cancelDisplayTask(holder.ivUserProfile);
		imageLoader.displayImage(user.getProfileImageUrl(200), holder.ivUserProfile);
		holder.tvMessage.setText(message.getText());
		holder.tvTime.setText(Utils.getShortRelativeTime(message.getCreatedAt()));
	}
}

