package com.todpop.saltyenglish;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

public class StudyTestFinish extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_study_test_finish);
		
		VideoView video = (VideoView)findViewById(R.id.test_video_view);
		video.setVideoPath("http://todpop.co.kr/uploads/cpdm_advertisement/video/4/CPDM_sample.mp4");
		
		final MediaController mc = new MediaController(StudyTestFinish.this);
		//mc.hide();
		//video.setMediaController(mc);
//		video.postDelayed(new Runnable() {
//			public void run() {
//				mc.show(0);
//			}
//		}, 100);
		
		video.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.study_test_finish, menu);
		return true;
	}
	
	public void showTestFinishViewCB(View v)
	{
		v.setEnabled(false);
		
		
//		SharedPreferences sp = getSharedPreferences("StudyLevelInfo", 0);
//		SharedPreferences.Editor editor = sp.edit();
//		int totalStage = sp.getInt("totalStage", 1);
//		int currentStage = sp.getInt("currentStage", 1);
//		
//
//
//		
//		if ((currentStage+1) > totalStage) {
//			editor.putInt("currentStage", (currentStage+1));
//			editor.commit();
//			
//			editor.putInt("totalStage", (currentStage+1));
//			editor.commit();
//			
//			SharedPreferences pref = getSharedPreferences("rgInfo",0);
//			SharedPreferences.Editor editor2 = pref.edit();
//			
//			int testLevel = currentStage/10+1;
//			int savedLevel = Integer.parseInt(pref.getString("level", "1"));
//			
//			if (testLevel > savedLevel) {
//				editor2.putString("level", Integer.toString(testLevel));
//				String levelLabel = "Level"+testLevel;
//				editor.putInt(levelLabel, 1);
//			} else {
//				String levelLabel = "Level"+(testLevel);
//				int levelInt = sp.getInt(levelLabel, 1)+1;
//				editor.putInt(levelLabel, levelInt);
//			}
//			
//			editor.commit();
//			editor2.commit();
//		}
		

		
		Intent intent = new Intent(getApplicationContext(), StudyTestResult.class);
		startActivity(intent);
		finish();
	}

}











