package com.codepath.wwcmentorme.helpers;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

public class Async {
	private static class RunnableTask extends AsyncTask<Object, Object, Object> {
		private final Runnable mRunnable;

		public RunnableTask(final Runnable runnable) {
			mRunnable = runnable;
		}

		@Override
		protected Object doInBackground(Object... params) {
			mRunnable.run();
			return null;
		}
	}

	public static interface Block<V> {
		public abstract void call(V result);
	}

	public static void dispatch(final Runnable runnable) {
		new RunnableTask(runnable).execute(null, null, null);
	}

	private static final Handler MAIN_HANDLER = new Handler(
			Looper.getMainLooper());

	public static void dispatchMain(final Runnable runnable) {
		MAIN_HANDLER.post(runnable);
	}

	public static void dispatchMain(final Runnable runnable, final long delayMs) {
		MAIN_HANDLER.postDelayed(runnable, delayMs);
	}
}
