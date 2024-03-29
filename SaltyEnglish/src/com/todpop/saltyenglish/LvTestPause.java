package com.todpop.saltyenglish;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;
import com.todpop.api.TypefaceActivity;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;

public class LvTestPause extends TypefaceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lv_test_pause);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.lv_test_pause, menu);
		return true;
	}
	
	public void continueTest(View v)
	{
		finish();
	}
	@Override
	protected void onStart()
	{
		super.onStart();
		FlurryAgent.onStartSession(this, "ZKWGFP6HKJ33Y69SP5QY");
	    EasyTracker.getInstance(this).activityStart(this);
	}
	 
	@Override
	protected void onStop()
	{
		super.onStop();		
		FlurryAgent.onEndSession(this);
	    EasyTracker.getInstance(this).activityStop(this);
	}
}
