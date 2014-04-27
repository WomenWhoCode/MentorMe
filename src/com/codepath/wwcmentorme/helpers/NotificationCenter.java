package com.codepath.wwcmentorme.helpers;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

public class NotificationCenter {
	public static interface Listener<V> {
		public void didChange(final V oldValue, final V newValue);
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
	}
	
	private static void prune(final String key) {
		final ArrayList<WeakReference<Listener<? extends Object>>> references = sMap.get(key);
		if (references == null) return;
		final ArrayList<WeakReference<Listener<? extends Object>>> referencesToRemove = new ArrayList<WeakReference<Listener<? extends Object>>>();
		for (final WeakReference<Listener<? extends Object>> weakReference : references) {
			if (weakReference.get() == null) referencesToRemove.add(weakReference);
		}
		references.removeAll(referencesToRemove);
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
		final ArrayList<WeakReference<Listener<? extends Object>>> references = sMap.get(key);
		if (references == null) return;
		for (final WeakReference<Listener<? extends Object>> weakReference : references) {
			final Listener<V> listener = (Listener<V>)weakReference.get();
			if (listener != null) {
				listener.didChange(oldValue, newValue);
			}
		}
	}
}
