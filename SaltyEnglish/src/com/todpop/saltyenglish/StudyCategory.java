package com.todpop.saltyenglish;


import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class StudyCategory extends Activity {

	RelativeLayout mainLayout;
	
	SharedPreferences studyInfo;
	
	static String selectedCategoryString;
	static int selectedCategoryInt;
	
	private static final int LATER = 0;
	private static final int NEVER = 2;
	
	PopupWindow askPopupWindow;
	View askPopupView;
	TextView askPopupText;

    PopupWindow progressPopupWindow;
    View progressPopupView;
	
	DownloadPronounce downLoadTask;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_study_category);
		
		mainLayout = (RelativeLayout)findViewById(R.id.study_category_main);
		
		studyInfo = getSharedPreferences("studyInfo",0);
		
		askPopupView = View.inflate(this, R.layout.popup_view_download_pronunciation, null);
		askPopupWindow = new PopupWindow(askPopupView, ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT,true);
		askPopupText = (TextView)askPopupView.findViewById(R.id.popup_id_text);
		askPopupWindow.setBackgroundDrawable(new BitmapDrawable());
		askPopupWindow.setOutsideTouchable(true);
		askPopupView.setOnKeyListener(new View.OnKeyListener() {

	        @Override
	        public boolean onKey(View v, int keyCode, KeyEvent event) {
	            if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
	            	askPopupWindow.dismiss();
	            }
	            return true;
	        }
	    });
		
		progressPopupView = View.inflate(this, R.layout.popup_view_download_progressbar, null);
		progressPopupWindow = new PopupWindow(progressPopupView, ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT,true);
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
		selectedCategoryInt = 1;
		selectedCategoryString = "basicCategorySound";
		int soundState = studyInfo.getInt(selectedCategoryString, LATER);
		if(soundState == LATER){
			askPopupText.setText(R.string.popup_pronounce_info_basic);
			askPopupWindow.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);
		}
		else{
			this.saveInfoGotoStudyLearn(1);
		}
	}
	
	public void studyCategoryTwoCB(View view)
	{
		selectedCategoryInt = 2;
		selectedCategoryString = "middleCategorySound";
		int soundState = studyInfo.getInt(selectedCategoryString, LATER);
		if(soundState == LATER){
			askPopupText.setText(R.string.popup_pronounce_info_middle);
			askPopupWindow.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);
		}
		else{
			this.saveInfoGotoStudyLearn(2);
		}
	}
	
	public void studyCategoryThreeCB(View view)
	{
		selectedCategoryInt = 3;
		selectedCategoryString = "highCategorySound";
		int soundState = studyInfo.getInt(selectedCategoryString, LATER);
		if(soundState == LATER){
			askPopupText.setText(R.string.popup_pronounce_info_high);
			askPopupWindow.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);
		}
		else{
			this.saveInfoGotoStudyLearn(3);
		}
	}
	
	public void studyCategoryFourCB(View view)
	{
		selectedCategoryInt = 4;
		selectedCategoryString = "toeicCategorySound";
		int soundState = studyInfo.getInt(selectedCategoryString, LATER);
		if(soundState == LATER){
			askPopupText.setText(R.string.popup_pronounce_info_toeic);
			askPopupWindow.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);
		}
		else{
			this.saveInfoGotoStudyLearn(4);
		}
	}
	
	public void downloadNever(View view) {
		SharedPreferences.Editor studyInfoEdit = studyInfo.edit();
		studyInfoEdit.putInt(selectedCategoryString, NEVER);
		studyInfoEdit.apply();
		askPopupWindow.dismiss();
		this.saveInfoGotoStudyLearn(selectedCategoryInt);
	}
	public void downloadLater(View view) {
		SharedPreferences.Editor studyInfoEdit = studyInfo.edit();
		studyInfoEdit.putInt(selectedCategoryString, LATER);
		studyInfoEdit.apply();
		askPopupWindow.dismiss();
		this.saveInfoGotoStudyLearn(selectedCategoryInt);
	}
	public void downloadNow(View view) {
		askPopupWindow.dismiss();
		progressPopupWindow.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);
		//TODO insert uri
		downLoadTask = new DownloadPronounce(getApplicationContext(), this, selectedCategoryInt, progressPopupView);
		downLoadTask.execute("");
		//new GetWordList().execute(""+selectedCategoryInt);
		
	}

	private void saveInfoGotoStudyLearn(int tmpCategory)
	{
		SharedPreferences.Editor studyInfoEdit = studyInfo.edit();
		studyInfoEdit.putInt("tmpCategory", tmpCategory);
		studyInfoEdit.apply();
		
		Intent intent = new Intent(getApplicationContext(), StudyLearn.class);
		startActivity(intent);
		//finish();
	}

	public void cancelDownload(View view){
		downLoadTask.cancel();
	}
	public void doneDownload(View view){
		progressPopupWindow.dismiss();
	}
	
	@Override
	protected void onStart()
	{
		super.onStart();
		FlurryAgent.onStartSession(this, "ZKWGFP6HKJ33Y69SP5QY");
		FlurryAgent.logEvent("Study Category");
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
