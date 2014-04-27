package com.codepath.wwcmentorme.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
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
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.codepath.wwcmentorme.R;
import com.codepath.wwcmentorme.activities.EditProfileActivity;
import com.codepath.wwcmentorme.models.User;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

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
	
	public static void hideSoftKeyboard(Activity activity, View view){
		InputMethodManager imm =(InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}
	
	public static void disableButton(Button btn) {
		btn.setEnabled(false);
		btn.getBackground().setAlpha(64);
	}

	public static void enableButton(Button btn) {
		btn.setEnabled(true);
		btn.getBackground().setAlpha(255);
	}
	
	public static View getLoginView(final Context context) {
		LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View view = inflator.inflate(R.layout.activity_fb_login, null);
		final Button loginButton = (Button) view.findViewById(R.id.loginButton);
		loginButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.com_facebook_inverse_icon, 0, 0, 0);
		loginButton.setCompoundDrawablePadding(
				context.getResources().getDimensionPixelSize(R.dimen.com_facebook_loginview_compound_drawable_padding));
		loginButton.setPadding(context.getResources().getDimensionPixelSize(R.dimen.com_facebook_loginview_padding_left),
				context.getResources().getDimensionPixelSize(R.dimen.com_facebook_loginview_padding_top),
				context.getResources().getDimensionPixelSize(R.dimen.com_facebook_loginview_padding_right),
				context.getResources().getDimensionPixelSize(R.dimen.com_facebook_loginview_padding_bottom));
		return view;
	}
	
	public static void logout(final Context context) {
		ParseUser.logOut();
		User.setMe(null);
	}
	
	public static void getOrCreateLoggedInUser(final Activity activity, final Async.Block<User> completion) {
		if (User.me() != null) {
			if (completion != null) {
				completion.call(User.me());
			}
			return;
		}
		ParseUser currentUser = ParseUser.getCurrentUser();
		if (currentUser != null) {
			if (currentUser.get(EditProfileActivity.PROFILE_REF) != null) {
				// Check if the user is currently logged
				// and show any cached content
				final User mentorMeUser = (User)currentUser.get(EditProfileActivity.PROFILE_REF);
				mentorMeUser.fetchIfNeededInBackground(new GetCallback<User>() {
					@Override
					public void done(User user, ParseException pe) {
						if (pe == null) {
							User.setMe(mentorMeUser);
							if (completion != null) {
								completion.call(mentorMeUser);
							}
						}
					}
				});
			} else {
				if (completion != null) {
					completion.call(null);
				}
			}
		} else {
			if (completion != null) {
				completion.call(null);
			}
		}
	}
	
	public static void login(final Activity context, final String title, final Async.Block<User> completion, final boolean skipUI) {
		getOrCreateLoggedInUser(context, new Async.Block<User>() {
			@Override
			public void call(User result) {
				if (result != null) {
					if (completion != null) {
						completion.call(result);
					}
				} else {
					ParseUser currentUser = ParseUser.getCurrentUser();
					final Runnable showEditProfileActivity = new Runnable() {
						@Override
						public void run() {
							EditProfileActivity.startWithCompletion(context, new Runnable() {
								@Override
								public void run() {
									if (completion != null) {
										completion.call(User.me());
									}
								}
							});
						}
					};
					if (currentUser != null) {
						if (currentUser.get(EditProfileActivity.PROFILE_REF) != null) {
							// Check if the user is currently logged
							// and show any cached content
							final User mentorMeUser = (User)currentUser.get(EditProfileActivity.PROFILE_REF);
							mentorMeUser.fetchIfNeededInBackground(new GetCallback<User>() {
								@Override
								public void done(User user, ParseException pe) {
									if (pe == null) {
										User.setMe(mentorMeUser);
										if (completion != null) {
											completion.call(mentorMeUser);
										}
									}
								}
							});
						} else {
							// The login process has not yet been completed.
							showEditProfileActivity.run();
						}
					} else {
						final Runnable login = new Runnable() {
							@Override
							public void run() {
								final ProgressDialog progressDialog = ProgressDialog.show(context, null, "Logging in with Facebook");
								List<String> permissions = Arrays.asList("basic_info", "email", "user_about_me", "user_location");
								ParseFacebookUtils.logIn(permissions, context, new LogInCallback() {
									@Override
									public void done(ParseUser user, ParseException err) {
										progressDialog.dismiss();
										if (user == null) {
											Log.d("MentorMe", "Uh oh. The user cancelled the Facebook login.");
											if (completion != null) {
												logout(context);
												completion.call(null);
											}
										} else if (user.isNew() || user.get(EditProfileActivity.PROFILE_REF) == null) {
											Log.d("MentorMe", "User signed up and logged in through Facebook!");
											showEditProfileActivity.run();
										} else {
											getOrCreateLoggedInUser(context, completion);
										}
									}
								});
							}
						};
						if (skipUI) {
							login.run();
						} else {
							final View view = getLoginView(context);
							final Button loginButton = (Button) view.findViewById(R.id.loginButton);
							final AlertDialog.Builder builder = new AlertDialog.Builder(context).setTitle(title).setView(view).setOnCancelListener(new OnCancelListener() {
								@Override
								public void onCancel(DialogInterface dialog) {
									completion.call(null);
								}
							});							
							final AlertDialog dialog = builder.create();
							loginButton.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									login.run();
									dialog.dismiss();
								}
							});
							dialog.show();
						}
					}
				}
			}
		});
		
	}
}
