package com.codepath.wwcmentorme.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.codepath.wwcmentorme.R;
import com.codepath.wwcmentorme.fragments.EditProfileExperiencesFragment;
import com.codepath.wwcmentorme.fragments.EditProfileLocationFragment;
import com.codepath.wwcmentorme.fragments.EditProfileSkillsFragment;
import com.viewpagerindicator.IconPagerAdapter;

public class EditProfileFragmentAdapter extends FragmentPagerAdapter implements IconPagerAdapter {
	protected static final int[] ICONS = new int[] {
		R.drawable.edit_profile_step1, //location
		R.drawable.edit_profile_step2, //experience
		R.drawable.edit_profile_step3, //skills
	};
	
	public EditProfileFragmentAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
		switch (position % ICONS.length) {
			case 0: return new EditProfileLocationFragment();
			case 1: return new EditProfileExperiencesFragment();
			case 2: return new EditProfileSkillsFragment();
		}
		return null;
	}

	@Override
	public int getCount() {
		return ICONS.length;
	}

	@Override
	public int getIconResId(int index) {
		return ICONS[index % ICONS.length];
	}
}
