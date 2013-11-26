package com.todpop.saltyenglish;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.os.AsyncTask;
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
	
	
	SharedPreferences rgInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_study_test_finish);
		 
		rgInfo = getSharedPreferences("rgInfo",0);
		new GetCPDM().execute("http://todpop.co.kr/api/advertises/get_cpdm_ad.json?user_id="+rgInfo.getString("mem_id", "0"));

		
//		VideoView video = (VideoView)findViewById(R.id.test_video_view);
//		video.setVideoPath("http://todpop.co.kr/uploads/cpdm_advertisement/video/4/CPDM_sample.mp4");
//		
//		final MediaController mc = new MediaController(StudyTestFinish.this);
		//mc.hide();
		//video.setMediaController(mc);
//		video.postDelayed(new Runnable() {
//			public void run() {
//				mc.show(0);
//			}
//		}, 100);
		
//		video.start();
	}
	
	//--- request class ---
		private class GetCPDM extends AsyncTask<String, Void, JSONObject> 
		{
			@Override
			protected JSONObject doInBackground(String... urls) 
			{
				JSONObject result = null;
				try {
					DefaultHttpClient httpClient = new DefaultHttpClient();
					String getURL = urls[0];
					HttpGet httpGet = new HttpGet(getURL);
					HttpResponse httpResponse = httpClient.execute(httpGet);
					HttpEntity resEntity = httpResponse.getEntity();

					if (resEntity != null) {    
						result = new JSONObject(EntityUtils.toString(resEntity)); 
						Log.d("CPDM RESPONSE ---- ", result.toString());				        	
					}
					return result;
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				return result;
			}

			@Override
			protected void onPostExecute(JSONObject json) {

				try {
					if(json.getBoolean("status")==true) {
						VideoView video = (VideoView)findViewById(R.id.test_video_view);
						video.setVideoPath("http://todpop.co.kr/"+json.getJSONObject("data").getString("url"));
						//final MediaController mc = new MediaController(LvTestFinish.this);
						//mc.hide();
						//video.setMediaController(mc);
						video.start();
					} else {		        
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
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











