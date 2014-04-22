package com.codepath.wwcmentorme.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.codepath.wwcmentorme.R;
import com.codepath.wwcmentorme.fragments.EditProfileExperiencesFragment;
import com.codepath.wwcmentorme.fragments.EditProfileLocationFragment;
import com.codepath.wwcmentorme.fragments.EditProfileSkillsFragment;
import com.viewpagerindicator.IconPagerAdapter;

public class EditProfileFragmentAdapter extends FragmentPagerAdapter {
	private static final int CNT = 3;
	
	public EditProfileFragmentAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
		switch (position % CNT) {
			case 0: return new EditProfileLocationFragment();
			case 1: return new EditProfileExperiencesFragment();
			case 2: return new EditProfileSkillsFragment();
		}
		return null;
	}

	@Override
	public int getCount() {
		return CNT;
	}
}
