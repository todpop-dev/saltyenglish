package com.todpop.saltyenglish;


import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;
import com.todpop.api.FileManager;
import com.todpop.api.TypefaceActivity;
import com.todpop.api.request.DownloadPronounce;

public class StudyTestMockStart extends TypefaceActivity {

	RelativeLayout mainLayout;
	
	SharedPreferences studyInfo;
	
	static String selectedCategoryString;
	static int selectedCategoryInt;
	
	PopupWindow askPopupWindow;
	View askPopupView;
	TextView askPopupText;
	TextView askPopupSize;

    PopupWindow progressPopupWindow;
    View progressPopupView;
	
	DownloadPronounce downLoadTask;

	ImageView ivPoster;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_study_test_mock_start);
//		String android_id = Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
//		Log.e("AND_ID",android_id);
		
		mainLayout = (RelativeLayout)findViewById(R.id.study_category_main);
		ivPoster = (ImageView)findViewById(R.id.iv_mocktest_poster);
		
		Bitmap poster = new FileManager().getImgFile("4120");
		ivPoster.setImageBitmap(poster); //4120 is mocktest intro image groupid
		studyInfo = getSharedPreferences("studyInfo",0);
		
		progressPopupView = View.inflate(this, R.layout.popup_view_download_progressbar, null);
		progressPopupWindow = new PopupWindow(progressPopupView, ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT,true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.study_category, menu);
		return false;
	}
	
	//--- on click---
	public void onClickBack(View view)
	{
		finish();
	}
	
	public void startMockTest(View view)
	{
		//Intent intent = new Intent(getApplicationContext(),StudyTestMock.class);
		//startActivity(intent);
		SharedPreferences studyInfo = getSharedPreferences("studyInfo", 0);
		Editor ed = studyInfo.edit();
		ed.putInt("tmpStageAccumulated", 99999);
		ed.apply();
		Intent intent=new Intent(getApplicationContext(),StudyTestMockCnt.class);
		startActivity(intent);
		finish();
	}
	
	@Override
	protected void onStart()
	{
		super.onStart();
		FlurryAgent.onStartSession(this, "ZKWGFP6HKJ33Y69SP5QY");
		FlurryAgent.logEvent("MockTest Start Activity");
	    EasyTracker.getInstance(this).activityStart(this);
	}
	 
	@Override
	protected void onStop()
	{
		super.onStop();		
		FlurryAgent.onEndSession(this);
	    EasyTracker.getInstance(this).activityStop(this);
	}
	
	// Add get_word by api and save into dic table
}
