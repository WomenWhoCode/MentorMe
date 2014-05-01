package com.codepath.wwcmentorme.activities;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.wwcmentorme.R;

public class ThankMentorActivity extends AppActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_thank_mentor);
		
		ImageView img = (ImageView)findViewById(R.id.ivBackground);
		img.setImageResource(R.drawable.spin_animation);
		AnimationDrawable frameAnimation = (AnimationDrawable) img.getDrawable();
		frameAnimation.setEnterFadeDuration(3000);
		frameAnimation.setExitFadeDuration(3000);
		frameAnimation.start();
		
		ImageView ivClose = (ImageView) findViewById(R.id.ivClose);
		ivClose.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		TextView tvShare = (TextView) findViewById(R.id.tvShare);
		tvShare.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent shareIntent=new Intent(android.content.Intent.ACTION_SEND);
				shareIntent.setType("text/plain");
				shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
				String strShareText = "";
				shareIntent.putExtra(Intent.EXTRA_TEXT, strShareText);
				startActivity(Intent.createChooser(shareIntent, "Share"));				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.thank_mentor, menu);
		return true;
	}

}
