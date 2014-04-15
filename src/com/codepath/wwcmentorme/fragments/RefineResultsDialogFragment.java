package com.codepath.wwcmentorme.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.WindowManager;

import com.codepath.wwcmentorme.R;

public class RefineResultsDialogFragment extends DialogFragment {
	
	public RefineResultsDialogFragment() {

	}
	
	public static RefineResultsDialogFragment newInstance(String title) {
		RefineResultsDialogFragment frag = new RefineResultsDialogFragment();
		Bundle args = new Bundle();
		args.putString("title", title);
		frag.setArguments(args);
		return frag;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		String title = getArguments().getString("title");
		AlertDialog.Builder b = new AlertDialog.Builder(getActivity())
				.setTitle(title)
				.setItems(getResources().getStringArray(R.array.skill_array), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
            	   
					}
				});		
		
		return b.create();
	}
	
	@Override
	public void onStart() {
		super.onStart();
		  if (getDialog() == null) {
		    return;
		  }
		  WindowManager manager = (WindowManager) getActivity().getSystemService(Activity.WINDOW_SERVICE);
		  Point point = new Point();
	      manager.getDefaultDisplay().getSize(point);
		  int dialogWidth = point.x; 
		  int dialogHeight = point.y; 

		  getDialog().getWindow().setLayout(dialogWidth, dialogHeight);

	}
}
