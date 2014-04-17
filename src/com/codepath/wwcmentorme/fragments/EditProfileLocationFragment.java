package com.codepath.wwcmentorme.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codepath.wwcmentorme.R;

public class EditProfileLocationFragment extends Fragment {
	TextView tvCity;
	TextView tvZip;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_edit_profile_location, container, false);
		return v;
	}

}
