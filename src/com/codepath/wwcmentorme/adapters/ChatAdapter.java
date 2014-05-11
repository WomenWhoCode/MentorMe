package com.codepath.wwcmentorme.adapters;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.wwcmentorme.R;
import com.codepath.wwcmentorme.helpers.Utils;
import com.codepath.wwcmentorme.helpers.ViewHolder;
import com.codepath.wwcmentorme.models.Message;
import com.codepath.wwcmentorme.models.User;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ChatAdapter extends ArrayAdapter<ChatAdapter.MessageGroup> {
	public static class MessageGroup {
		private final LinkedHashMap<String, Message> messages = new LinkedHashMap<String, Message>();
		private final Message head;
		private Message tail;
		private StringBuilder text = new StringBuilder();
		
		public static List<MessageGroup> processMessages(final List<Message> messages, final MessageGroup lastGroup) {
			final ArrayList<MessageGroup> newMessageGroups = new ArrayList<MessageGroup>();
			MessageGroup messageGroup = lastGroup;
			Message lastMessage = messageGroup != null ? messageGroup.last() : null;
			for (final Message message : messages) {
				if (shouldGroup(messageGroup, lastMessage, message)) {
					messageGroup.addMessage(message);
				} else {
					messageGroup = new MessageGroup(message);
					newMessageGroups.add(messageGroup);
				}
				lastMessage = message;
			}
			return newMessageGroups;
		}
		
		private static boolean shouldGroup(final MessageGroup group, final Message tail, final Message newMessage) {
			if (group == null || tail == null || newMessage == null) return false;
			if (group.contains(newMessage)) return false;
			if (group.size() > 10) return false;
			long diff = (long)Math.abs(tail.getCreatedAt().getTime() - newMessage.getCreatedAt().getTime()); 
			if (diff > 1L * 60L * 1000L) return false;
			return tail.getUserId() == newMessage.getUserId();
		}
		
		private MessageGroup(final Message head) {
			this.head = head;
			addMessage(head);
		}
		
		private boolean contains(final Message message) {
			return messages.containsKey(message.getObjectId());
		}
		
		private void addMessage(final Message message) {
			if (messages.containsKey(message.getObjectId())) return;
			tail = message;
			messages.put(message.getObjectId(), message);
			if (head != tail) {
				text.append("\n");
			}
			text.append(message.getText());
		}
		
		public Message first() {
			return head;
		}
		
		public Message last() {
			return tail;
		}
		
		public String getText() {
			return text.toString();
		}
		
		public int size() {
			return messages.size();
		}
	}
	
	private HashSet<MessageGroup> mProcessedMessageGroups = new HashSet<MessageGroup>();
	
	public ChatAdapter(Context context) {
		super(context, 0);
	}
	
	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		return getView(getItem(position), convertView, parent);
	}
	
	@Override
	public int getViewTypeCount() {
		return 2;
	}
	
	@Override
	public int getItemViewType(int position) {
		return getItem(position).last().getUserId() == User.meId() ? 0 : 1;
	}
	
	private View getView(final MessageGroup message, final View convertView, final ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			LayoutInflater inflator = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflator.inflate(message.last().getUserId() == User.meId() ? R.layout.chat_list_item_self : R.layout.chat_list_item, null);
			final ViewHolder.ChatItem holder = new ViewHolder.ChatItem();
			holder.ivUserProfile = (ImageView)view.findViewById(R.id.ivUserProfile);
			holder.tvMessage = (TextView)view.findViewById(R.id.tvMessage);
			holder.tvTime = (TextView)view.findViewById(R.id.tvTime);
			view.setTag(holder);
		}
		populateView(parent, view, message);
		return view;
	}
	
	private void populateView(final ViewGroup parent, final View view, final MessageGroup message) {
		final ViewHolder.ChatItem holder = (ViewHolder.ChatItem) view.getTag();
		final ImageLoader imageLoader = ImageLoader.getInstance();
		final Message last = message.last();
		final User user = User.getUser(last.getUserId());
		imageLoader.cancelDisplayTask(holder.ivUserProfile);
		imageLoader.displayImage(user.getProfileImageUrl(200), holder.ivUserProfile);
		holder.tvMessage.setText(message.getText());
		holder.tvTime.setText(Utils.getShortRelativeTime(last.getCreatedAt()));
		if (!mProcessedMessageGroups.contains(message) && parent.getHeight() != 0) {
			
			final ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", 0, 1);
			animator.addListener(new AnimatorListener() {
				@Override
				public void onAnimationStart(Animator animation) {}
				@Override
				public void onAnimationCancel(Animator animation) {}
				@Override
				public void onAnimationRepeat(Animator animation) {}
				
				@Override
				public void onAnimationEnd(Animator animation) {
					mProcessedMessageGroups.add(message);
				}
			});
			float amountX = parent.getWidth() * 0.2f;
			float rotate = 270;
			final ObjectAnimator animatorMessage = ObjectAnimator.ofFloat(holder.tvMessage, "translationX", last.getUserId() == User.meId() ? amountX : -amountX, 0);
			final ObjectAnimator animatorProfile = ObjectAnimator.ofFloat(holder.ivUserProfile, "translationX", last.getUserId() == User.meId() ? amountX : -amountX, 0);
			final ObjectAnimator animatorProfileRot = ObjectAnimator.ofFloat(holder.ivUserProfile, "rotation", last.getUserId() == User.meId() ? rotate : -rotate, 0);
			final ObjectAnimator animatorTime = ObjectAnimator.ofFloat(holder.tvTime, "alpha", 0, 1);
			final AnimatorSet set = new AnimatorSet();
			set.playTogether(animator, animatorMessage, animatorProfile, animatorProfileRot, animatorTime);
			set.setDuration(600);
			set.start();
		}
	}
	
	public List<MessageGroup> processMessages(final List<Message> messages) {
		final int count = getCount();
		final ArrayList<Message> dedupedMessages = new ArrayList<Message>();
		for (final Message message : messages) {
			boolean found = false;
			for (int i = 0; i < count; ++i) {
				final MessageGroup group = getItem(i);
				if (group.contains(message)) {
					found = true;
					break;
				}
			}
			if (!found) {
				dedupedMessages.add(message);
			}
		}
		ChatAdapter.MessageGroup lastMessageGroup = count > 0 ? getItem(count - 1) : null;
		return MessageGroup.processMessages(dedupedMessages, lastMessageGroup);
	}
}

