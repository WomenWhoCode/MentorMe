package com.codepath.wwcmentorme.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.wwcmentorme.R;


public class HomeActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		ImageView img = (ImageView)findViewById(R.id.ivBackground);
		img.setImageResource(R.drawable.spin_animation);
		AnimationDrawable frameAnimation = (AnimationDrawable) img.getDrawable();
		frameAnimation.setEnterFadeDuration(3000);
		frameAnimation.setExitFadeDuration(3000);
		frameAnimation.start();
		final TextView tvFindMentors = (TextView)findViewById(R.id.tvFindMentor);
		tvFindMentors.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final Intent intent = new Intent(HomeActivity.this, MentorListActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.activity_slide_in, R.anim.activity_slide_out);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
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
