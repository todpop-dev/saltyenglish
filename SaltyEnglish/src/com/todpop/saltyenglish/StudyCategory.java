package com.todpop.saltyenglish;

import com.flurry.android.FlurryAgent;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.View;

public class StudyCategory extends Activity {
	/*
	private Intent intent;
	private SharedPreferences pref;
	private SharedPreferences.Editor ed;
	private int test_level;*/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_study_category);
		/*
		intent = getIntent();
		if(intent.getBooleanExtra("from_home", false)) {
			pref = getSharedPreferences("StudyLevelInfo", MODE_PRIVATE);
			test_level = pref.getInt("totalStage", 1);
			if(test_level >= 1 && test_level <= 150) {
				this.saveInfoGotoStudyLearn(1);
			} else if(test_level >=151 && test_level <= 600) {
				this.saveInfoGotoStudyLearn(2);
			} else if(test_level >=601 && test_level <= 1200) {
				this.saveInfoGotoStudyLearn(3);
			} else if(test_level >=1201 && test_level <= 1800) {
				this.saveInfoGotoStudyLearn(4);
			}
		}*/
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.study_category, menu);
		return true;
	}
	
	//--- on click---
	public void onClickBack(View view)
	{
		finish();
	}
	
	public void studyCategoryOneCB(View view)
	{
		this.saveInfoGotoStudyLearn(1);
	}
	
	public void studyCategoryTwoCB(View view)
	{
		this.saveInfoGotoStudyLearn(2);
	}
	
	public void studyCategoryThreeCB(View view)
	{
		this.saveInfoGotoStudyLearn(3);
	}
	
	public void studyCategoryFourCB(View view)
	{
		this.saveInfoGotoStudyLearn(4);
	}
	
	private void saveInfoGotoStudyLearn(int categoryCount)
	{
		SharedPreferences prefs = getSharedPreferences("rgInfo",0);
		SharedPreferences.Editor ed = prefs.edit();
		ed.putInt("categoryStage", categoryCount);
		ed.commit();
		
		Intent intent = new Intent(getApplicationContext(), StudyLearn.class);
		startActivity(intent);
		//finish();
	}


	@Override
	protected void onStart()
	{
		super.onStart();
		FlurryAgent.onStartSession(this, "ZKWGFP6HKJ33Y69SP5QY");
		FlurryAgent.logEvent("Study Category");
	}
	 
	@Override
	protected void onStop()
	{
		super.onStop();		
		FlurryAgent.onEndSession(this);
	}
}
