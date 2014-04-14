package com.codepath.wwcmentorme.helpers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class UIUtils {
	public static final ColorDrawable TRANSPARENT = new ColorDrawable(Color.TRANSPARENT);
	public static final Typeface SANS_SERIF_LIGHT = Typeface.create("sans-serif-light", Typeface.NORMAL); 
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
				enumerateSubviews(dialog.getWindow().getDecorView(), new Async.Block<ViewGroup, View>() {
					@Override
					public void call(ViewGroup context, View result) {
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
	
	public static void enumerateSubviews(final View view, final Async.Block<ViewGroup, View> block) {
		if (view instanceof ViewGroup) {
			final ViewGroup vg = (ViewGroup)view;
			for (int i = 0, count = vg.getChildCount(); i < count; ++i) {
				final View child = vg.getChildAt(i);
				enumerateSubviews(child, block);
				block.call(vg, child);
			}
		}
	}
}
