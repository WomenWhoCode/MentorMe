package com.codepath.wwcmentorme.helpers;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.LayerDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

public class UIUtils {
	public static final ColorDrawable TRANSPARENT = new ColorDrawable(Color.TRANSPARENT);
	public static final Typeface SANS_SERIF_LIGHT = Typeface.create("sans-serif-light", Typeface.NORMAL);
	public static final Typeface SANS_SERIF_THIN = Typeface.create("sans-serif-thin", Typeface.NORMAL);
	public static void showActionSheet(final Context context, final CharSequence title, final CharSequence[] items,
			final DialogInterface.OnClickListener onClickListener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title).setItems(items, onClickListener);
		final AlertDialog dialog = builder.create();
		dialog.getWindow().setGravity(Gravity.BOTTOM);
		dialog.getWindow().setWindowAnimations(android.R.style.Animation_InputMethod);
		dialog.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialogInterface) {
				enumerateSubviews(dialog.getWindow().getDecorView(), new Async.Block<View>() {
					@Override
					public void call(View result) {
						if (result instanceof TextView) {
							final TextView tv = (TextView)result;
							tv.setTypeface(SANS_SERIF_LIGHT);
						}
					}
				});
			}
		});
		dialog.show();
	}
	
	public static void enumerateSubviews(final View view, final Async.Block<View> block) {
		final AtomicBoolean stop = new AtomicBoolean(false);
		enumerateSubviews(view, block, stop);
	}

	public static void enumerateSubviews(final View view, final Async.Block<View> block, final AtomicBoolean stop) {
		if (view instanceof ViewGroup) {
			final ViewGroup vg = (ViewGroup)view;
			for (int i = 0, count = vg.getChildCount(); i < count; ++i) {
				final View child = vg.getChildAt(i);
				enumerateSubviews(child, block, stop);
				block.call(child);
				if (stop.get()) break;
			}
		}
	}
	
	public static void customizeProgressBar(final ProgressBar progressBar, final int progressTintColor, final int backgroundColor) {
		LayerDrawable stars = (LayerDrawable) progressBar.getProgressDrawable();
		stars.getDrawable(0).setColorFilter(backgroundColor, PorterDuff.Mode.SRC_ATOP);
		stars.getDrawable(1).setColorFilter(backgroundColor, PorterDuff.Mode.SRC_ATOP);
		stars.getDrawable(2).setColorFilter(progressTintColor, PorterDuff.Mode.SRC_ATOP);
	}

	public static void startActivity(final Context context, final Class clazz) {
		startActivity(context, clazz, null, null);
	}

	public static void startActivity(final Context context, final Class clazz, final String extra, 
			final ArrayList<String> extraValue) {
		final Intent intent = new Intent(context, clazz);
		if (extra != null && extraValue != null) {
			intent.putStringArrayListExtra(extra, extraValue);
		}
		context.startActivity(intent);
	}

	private static final float sDeviceScale = Resources.getSystem().getDisplayMetrics().density;
	private static final float sOneByScale = 1 / sDeviceScale;

	public static final float sDeviceWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
	public static final float sDeviceHeight = Resources.getSystem().getDisplayMetrics().heightPixels;

	public static float getDeviceScale() {
		return sDeviceScale;
	}

	public static int p(final float dp) {
		return (int)(dp * sDeviceScale);
	}

	public static int dp(final float p) {
		return (int)(p * sOneByScale);
	}

	public static final Bitmap decode(final byte[] bytes) {
		if (bytes == null) return null;
		return BitmapFactory.decodeByteArray(bytes, 0, (int)bytes.length);
	}
}
