package com.codepath.wwcmentorme.helpers;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubException;

public class NotificationCenter {
	private static String PUBNUB_PREFIX = "pubnub_";
	public static interface Listener<V> {
		public void didChange(final String key, final V oldValue, final V newValue);
	}
	
	final static HashMap<String, ArrayList<WeakReference<Listener<? extends Object>>>> sMap = new HashMap<String, ArrayList<WeakReference<Listener<? extends Object>>>>();
	
	public static void registerListener(final Listener<? extends Object> listener, final String key) {
		if (!sMap.containsKey(key)) {
			final ArrayList<WeakReference<Listener<? extends Object>>> references = new ArrayList<WeakReference<Listener<? extends Object>>>();
			sMap.put(key, references);
		}
		final ArrayList<WeakReference<Listener<? extends Object>>> references = sMap.get(key);
		prune(key);
		if (indexOf(references, listener) < 0) {
			references.add(new WeakReference<NotificationCenter.Listener<? extends Object>>(listener));
		}
		if (key.startsWith(PUBNUB_PREFIX)) {
			subscribe(key);
		}
	}
	
	private static void prune(final String key) {
		final ArrayList<WeakReference<Listener<? extends Object>>> references = sMap.get(key);
		if (references == null) return;
		final ArrayList<WeakReference<Listener<? extends Object>>> referencesToRemove = new ArrayList<WeakReference<Listener<? extends Object>>>();
		for (final WeakReference<Listener<? extends Object>> weakReference : references) {
			if (weakReference.get() == null) referencesToRemove.add(weakReference);
		}
		references.removeAll(referencesToRemove);
		if (key.startsWith(PUBNUB_PREFIX)) {
			if (references.size() == 0) {
				unsubscribe(key);
			}
		}
	}
	
	private static void pruneAll() {
		for (final String key : sMap.keySet()) {
			prune(key);
		}
	}
	
	private static int indexOf(final ArrayList<WeakReference<Listener<? extends Object>>> references, final Listener<? extends Object> reference) {
		int index = 0;
		for (final WeakReference<Listener<? extends Object>> weakReference : references) {
			if (weakReference.get() == reference) return index;
			++index;
		}
		return -1;
	}
	
	public static void unregisterListener(final Listener<? extends Object> listener, final String key) {
		final ArrayList<WeakReference<Listener<? extends Object>>> references = sMap.get(key);
		if (references != null) {
			final int index = indexOf(references, listener);
			if (index > 0) {
				references.remove(index);
			}
		}
	}
	
	public static <V> void broadcastChange(final String key, final V oldValue, final V newValue) {
		if (key.startsWith(PUBNUB_PREFIX)) {
			publish(key, "");
		} else {
			broadcastChangeToReferences(key, oldValue, newValue);
		}
	}
	
	private static <V> void broadcastChangeToReferences(final String key, final V oldValue, final V newValue) {
		final ArrayList<WeakReference<Listener<? extends Object>>> references = sMap.get(key);
		if (references == null) return;
		for (final WeakReference<Listener<? extends Object>> weakReference : references) {
			final Listener<V> listener = (Listener<V>)weakReference.get();
			if (listener != null) {
				listener.didChange(key, oldValue, newValue);
			}
		}
	}
	
	private static Pubnub sPubnub = new Pubnub("pub-c-5b029b3f-1a53-49ba-8dde-9c63b99fc0f9", "sub-c-3e884ebc-d933-11e3-a226-02ee2ddab7fe");
	
	public static String keyForPubnubChannel(final String channel) {
		return PUBNUB_PREFIX + channel;
	}
	
	private static HashSet<String> sSubscribedChannels = new HashSet<String>();
	
	private static void subscribe(final String channel) {
		if (sSubscribedChannels.contains(channel)) return;
		final Pubnub pubnub = sPubnub;
		try {
			pubnub.subscribe(channel, new Callback() {
				@Override
				public void successCallback(String channel, Object payload) {
					broadcastChangeToReferences(channel, null, null);
				}
			});
			sSubscribedChannels.add(channel);
		} catch (PubnubException e) {
			e.printStackTrace();
		}
	}
	
	private static void unsubscribe(final String channel) {
		sSubscribedChannels.remove(channel);
		final Pubnub pubnub = sPubnub;
		pubnub.unsubscribe(channel);
	}
	
	private static void publish(final String channel, final String payload) {
		final Pubnub pubnub = sPubnub;
		pubnub.publish(channel, payload, new Callback() {
			@Override
			public void successCallback(String arg0, Object arg1) {
			}
		});
	}
}
