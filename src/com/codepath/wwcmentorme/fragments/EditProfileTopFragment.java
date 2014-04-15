package com.codepath.wwcmentorme.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.wwcmentorme.R;

public class EditProfileTopFragment extends Fragment {
	TextView tvFullName;
	ImageView ivProfile;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_edit_profile_top, container, false);
		tvFullName = (TextView) v.findViewById(R.id.tvFullName);
		ivProfile = (ImageView) v.findViewById(R.id.ivUserProfile);
		return v;
	}
}
