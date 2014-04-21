package com.codepath.wwcmentorme.activities;

import java.util.concurrent.atomic.AtomicBoolean;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import com.codepath.wwcmentorme.R;
import com.codepath.wwcmentorme.helpers.Async;
import com.codepath.wwcmentorme.helpers.Async.Block;
import com.codepath.wwcmentorme.helpers.UIUtils;


public class AppActivity extends Activity {
	private ProgressBar mProgressBar;
	private boolean mEnableBackButton;
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mToggle;
	private boolean mSettingActionBarVisible;
	private boolean mActionBarVisible;
	
	public final AppActivity getActivity() {
		return this;
	}

	@Override
	public void setContentView(View view) {
		init().addView(view);
	}

	@Override
	public void setContentView(int layoutResID) {
		getLayoutInflater().inflate(layoutResID, init(), true);
		didChangeContentView();
	}

	@Override
	public void setContentView(View view, ViewGroup.LayoutParams params) {
		init().addView(view, params);
		didChangeContentView();
	}
	
	public void didChangeContentView() {
		final AtomicBoolean stop = new AtomicBoolean(false);
		UIUtils.enumerateSubviews(findViewById(android.R.id.content), new Block<View>() {
			@Override
			public void call(View result) {
				if (result instanceof AbsListView || result instanceof ScrollView) {
					result.setOnTouchListener(new OnTouchListener() {
						float mPreviousY = 0;
						private static final int MIN_DP_MOVEMENT = 10;
						@Override
						public boolean onTouch(View v, MotionEvent event) {
							if(mDrawerLayout != null) {
								if (mDrawerLayout.isDrawerVisible(GravityCompat.START)) return false;
							}
							boolean setPreviousY = true;
							if ((event.getAction() & MotionEvent.ACTION_MASK) != MotionEvent.ACTION_DOWN) {
								float diff = 0;
								float currentY = event.getY();
								diff = currentY - mPreviousY;
								float pixelMovement = UIUtils.dp(Math.abs(diff));
								if (pixelMovement < MIN_DP_MOVEMENT) {
									diff = 0;
									setPreviousY = false;
								}
								if (diff < 0) {
									setActionBarVisible(false);
								} else if (diff > 0) {
									setActionBarVisible(true);
								}
							} else {
								mSettingActionBarVisible = false;
							}
							if (setPreviousY) {
								mPreviousY = event.getY();
							}
							return false;
						}
					});
					stop.set(true);
				}
			}
		}, stop);
	}
	
	public void setActionBarVisible(final boolean visible) {
		setActionBarVisible(visible, true);
	}
	
	public void setActionBarVisible(final boolean visible, final boolean animated) {
		mActionBarVisible = visible;
		if (mSettingActionBarVisible) return;
		mSettingActionBarVisible = true;
		float yOffset = visible ? getActionBarHeight() : 0;
		if (visible && !getActionBar().isShowing()) {
			getActionBar().show();
		} else if (!visible) {
			getActionBar().hide();
		}
		if (animated) {
			findViewById(R.id.activity_bar).animate().y(yOffset - UIUtils.p(6));
			findViewById(R.id.activity_frame).animate().y(yOffset);
		} else {
			findViewById(R.id.activity_bar).setY(yOffset - UIUtils.p(6));
			findViewById(R.id.activity_frame).setY(yOffset);
		}
		Async.dispatchMain(new Runnable() {
			@Override
			public void run() {
				mSettingActionBarVisible = false;
				if (mActionBarVisible != visible) {
					setActionBarVisible(mActionBarVisible, animated);
				}
			}
		}, animated ? 1000 : 0);
	}
	
	private float getActionBarHeight() {
		TypedValue tv = new TypedValue();
		float actionBarHeight = 0;
		if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
		    actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
		}
		return actionBarHeight;
	}

	private ViewGroup init() {
		super.setContentView(R.layout.progress);
		mProgressBar = (ProgressBar) findViewById(R.id.activity_bar);
		mProgressBar.setVisibility(View.INVISIBLE);
		setActionBarVisible(true, false);
		return (ViewGroup) findViewById(R.id.activity_frame);
	}

	public ProgressBar getProgressBar() {
		return mProgressBar;
	}
	
	@Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (mToggle != null) {
        	// Sync the toggle state after onRestoreInstanceState has occurred.
        	mToggle.syncState();
        }
    }
 
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mToggle != null) {
        	// Pass any configuration change to the drawer toggles.
        	mToggle.onConfigurationChanged(newConfig);
        }
    }
	
	public void enableBackButtonBehavior(final boolean enableBackButton) {
		mEnableBackButton = enableBackButton;
		getActionBar().setDisplayHomeAsUpEnabled(enableBackButton);
	}
	
	public void enableDrawer(final DrawerLayout drawerLayout) {
		mDrawerLayout = drawerLayout;
		mToggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_navigation_drawer, 0, 0);
		mDrawerLayout.setDrawerListener(mToggle);
	}
	
	@Override
	public void setTitle(CharSequence title) {
		final SpannableString s = new SpannableString(title);
		s.setSpan(UIUtils.SANS_SERIF_LIGHT, 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		super.setTitle(s);
		getActionBar().setTitle(s);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		enableBackButtonBehavior(true);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mEnableBackButton || mDrawerLayout != null) {
			switch (item.getItemId()) {
			case android.R.id.home:
				if (mDrawerLayout != null) {
					 return mToggle.onOptionsItemSelected(item);
				} else {
					this.finish();
				}
				return true;
			default:
				break;
			}
		}
		return super.onOptionsItemSelected(item);
	}
}
