package com.codepath.wwcmentorme.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.wwcmentorme.R;
import com.codepath.wwcmentorme.adapters.EditProfileFragmentAdapter;
import com.viewpagerindicator.IconPageIndicator;
import com.viewpagerindicator.PageIndicator;

public class EditProfileActivity extends FragmentActivity {
	FragmentPagerAdapter mAdapter;
    ViewPager mPager;
    PageIndicator mIndicator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_profile);
		
		mAdapter = new EditProfileFragmentAdapter(getSupportFragmentManager());

        mPager = (ViewPager)findViewById(R.id.vpPager);
        mPager.setAdapter(mAdapter);

        mIndicator = (IconPageIndicator) findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_profile, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
