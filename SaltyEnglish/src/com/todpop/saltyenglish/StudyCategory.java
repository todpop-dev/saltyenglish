package com.todpop.saltyenglish;

import com.flurry.android.FlurryAgent;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;

public class StudyCategory extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_study_category);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.study_category, menu);
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
	
	private void saveInfoGotoStudyLearn(int tmpCategory)
	{
		SharedPreferences studyInfo = getSharedPreferences("studyInfo",0);
		SharedPreferences.Editor studyInfoEdit = studyInfo.edit();
		studyInfoEdit.putInt("tmpCategory", tmpCategory);
		studyInfoEdit.commit();
		
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
