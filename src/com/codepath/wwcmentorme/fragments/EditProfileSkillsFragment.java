package com.codepath.wwcmentorme.fragments;

import java.util.Arrays;
import java.util.HashMap;

import org.json.JSONArray;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.codepath.wwcmentorme.R;
import com.codepath.wwcmentorme.activities.EditProfileActivity;
import com.codepath.wwcmentorme.helpers.Async;
import com.codepath.wwcmentorme.helpers.UIUtils;
import com.codepath.wwcmentorme.helpers.Constants.Persona;
import com.codepath.wwcmentorme.models.User;

public class EditProfileSkillsFragment extends AbstractEditProfileFragment {
	private LinearLayout llMentorSkills;
	private LinearLayout llMenteeSkills;
	private LinearLayout llAvailability;
	private Button btEditMentorSkills;
	private Button btEditMenteeSkills;
	private TextView tvAvailability;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_edit_profile_skills, container, false);
		setupViews(v);
		return v;
	}
	
	private void setupViews(View v) {
		llMentorSkills = (LinearLayout)v.findViewById(R.id.llMentorSkills);
		llMenteeSkills = (LinearLayout)v.findViewById(R.id.llMenteeSkills);
		llAvailability = (LinearLayout)v.findViewById(R.id.llAvailable);
		tvAvailability = (TextView)v.findViewById(R.id.tvAvailable);
		btEditMentorSkills = (Button)v.findViewById(R.id.btMentorSkills);
		btEditMentorSkills.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showSkillsFromLinearLayout(btEditMentorSkills.getText().toString(), llMentorSkills);
			}
		});
		btEditMenteeSkills = (Button)v.findViewById(R.id.btMenteeSkills);
		btEditMenteeSkills.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showSkillsFromLinearLayout(btEditMenteeSkills.getText().toString(), llMenteeSkills);
			}
		});
		final View.OnClickListener onClickListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final TextView tv = (TextView)v;
				Boolean currentSelected = (Boolean)tv.getTag();
				final Boolean selected = Boolean.valueOf(!currentSelected.booleanValue());
				setDaySelected(tv, selected.booleanValue());
			}
		};
		for (int i = 0, count = llAvailability.getChildCount(); i < count; ++i) {
			final TextView tv = (TextView)llAvailability.getChildAt(i);
			tv.setTag(Boolean.valueOf(false));
			tv.setOnClickListener(onClickListener);
		}
		if (getPersona().equals(Persona.MENTEE)) {
			llAvailability.setVisibility(View.GONE);
			llMentorSkills.setVisibility(View.GONE);
			btEditMentorSkills.setVisibility(View.GONE);
			tvAvailability.setVisibility(View.GONE);
		} else if (getPersona().equals(Persona.MENTOR)) {
			btEditMenteeSkills.setVisibility(View.GONE);
			llMenteeSkills.setVisibility(View.GONE);
		}
	}
	
	private void setDaySelected(final TextView tv, final boolean selected) {
		tv.setTag(Boolean.valueOf(selected));
		tv.setTypeface(selected ? UIUtils.SANS_SERIF_BOLD : UIUtils.SANS_SERIF_THIN);
		tv.setTextColor(selected ? UIUtils.COLOR_ACTIONBAR : Color.BLACK);
	}
	
	private TextView createTextViewFromItem(final String item) {
		final TextView tv = (TextView) getActivity().getLayoutInflater().inflate(R.layout.skill_textview_item, null);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.setMargins(0,0,UIUtils.p(4),0);
		tv.setLayoutParams(params);
		tv.setText(item);
		return tv;
	}
	
	private void showSkillsFromLinearLayout(final String title, final LinearLayout ll) {
		final HashMap<String, TextView> skillsSelected = new HashMap<String, TextView>();
		for (int i = 0, count = ll.getChildCount(); i < count; ++i) {
			final TextView tv = (TextView)ll.getChildAt(i);
			skillsSelected.put(tv.getText().toString(), tv);
		}
		final String[] allItems = getActivity().getResources().getStringArray(R.array.skill_array);
		final String[] items = Arrays.copyOfRange(allItems, 1, allItems.length);
		final boolean[] checkedItems = new boolean[items.length];
		for (int i = 0, count = items.length; i < count; ++i) {
			checkedItems[i] = skillsSelected.containsKey(items[i]);
		}
		UIUtils.showActionSheet(getActivity(), title, items, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which, boolean isChecked) {
				final String item = items[which];
				if (isChecked) {
					if (!skillsSelected.containsKey(item)) {
						final TextView tv = createTextViewFromItem(item);
						skillsSelected.put(item, tv);
						ll.addView(tv);
					}
				} else {
					final TextView tv = skillsSelected.get(item);
					if (tv != null) {
						ll.removeView(tv);
						skillsSelected.remove(item);
					}
				}
			}
		});
	}
	
	private static interface FilterInterface<V> {
		boolean shouldAdd(final V tv);
	}
	
	private JSONArray populateJSONArray(final LinearLayout ll, final FilterInterface<TextView> filter) {
		final JSONArray array = new JSONArray();
		for (int i = 0, count = ll.getChildCount(); i < count; ++i) {
			final TextView tv = (TextView)ll.getChildAt(i);
			final boolean shouldAdd = filter != null ? filter.shouldAdd(tv) : true;
			if (shouldAdd) {
				array.put(tv.getText().toString());
			}
		}
		return array;
	}
	
	private void populateLinearLayout(final JSONArray array, final LinearLayout ll, final boolean shouldAdd) {
		try {
			for (int i = 0, count = array.length(); i < count; ++i) {
				final String item = array.getString(i);
				if (shouldAdd) {
					final TextView tv = createTextViewFromItem(item);
					tv.setText(item);
					ll.addView(tv);
				} else {
					for (int j = 0, numChildren = ll.getChildCount(); j < numChildren; ++j) {
						final TextView tv = (TextView)ll.getChildAt(j);
						if (tv.getText().toString().equals(item)) {
							setDaySelected(tv, true);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void updateProfile(final User profileUser) {
		final JSONArray availability = populateJSONArray(llAvailability, new FilterInterface<TextView>() {
			@Override
			public boolean shouldAdd(TextView tv) {
				return ((Boolean)tv.getTag()).booleanValue();
			}
		});
		final JSONArray mentorSkills = populateJSONArray(llMentorSkills, null);
		final JSONArray menteeSkills = populateJSONArray(llMenteeSkills, null);
		profileUser.setAvailability(availability);
		profileUser.setMentorSkills(mentorSkills);
		profileUser.setMenteeSkills(menteeSkills);
	}

	@Override
	void updateViews(final User profileUser) {
		populateLinearLayout(profileUser.getAvailability(), llAvailability, false);
		populateLinearLayout(profileUser.getMentorSkills(), llMentorSkills, true);
		populateLinearLayout(profileUser.getMenteeSkills(), llMenteeSkills, true);
	}
	
	boolean validateLinearLayout(final LinearLayout ll, final FilterInterface<TextView> filter) {
		if (ll.getVisibility() == View.GONE) return true;
		boolean retVal = false;
		for (int i = 0, count = ll.getChildCount(); i < count; ++i) {
			final TextView tv = (TextView)ll.getChildAt(i);
			retVal |= (filter != null ? filter.shouldAdd(tv) : true);
		}
		return retVal;
	}
	
	@Override 
	public void validateInputs(final Async.Block<View> invalidView) {
		View view = null;
		boolean validateMentors = validateLinearLayout(llMentorSkills, null);
		boolean validateMentees = validateLinearLayout(llMenteeSkills, null);
		if (getPersona().equals(Persona.MENTOR)) {
			if (!validateMentors) view = btEditMentorSkills;
			if (view == null && !validateLinearLayout(llAvailability, new FilterInterface<TextView>() {
				@Override
				public boolean shouldAdd(TextView tv) {
					return ((Boolean)tv.getTag()).booleanValue();
				}
			})) view = llAvailability;
		} else if (getPersona().equals(Persona.MENTEE)) {
			if (!validateMentees) view = btEditMenteeSkills;
		} else if (getPersona().equals(Persona.BOTH)) {
			if (!validateMentors && !validateMentees) view = getView();
		}
		invalidView.call(view);
	}
}
