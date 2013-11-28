package com.todpop.saltyenglish;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

public class StudyTestFinish extends Activity {
	
	Button skipBtn;	
	SharedPreferences rgInfo;
	private int video_length =0;
	private VideoView video;
	private int ad_id = -1;
	private int ad_type = 201;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_study_test_finish);
		skipBtn = (Button)findViewById(R.id.testfinish_id_skip_btn);
		rgInfo = getSharedPreferences("rgInfo",0);
		video = (VideoView)findViewById(R.id.test_video_view);
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


	private Runnable mLaunchTaskMain = new Runnable() {
		public void run() {
			skipBtn.setEnabled(true);
			skipBtn.setBackgroundResource(R.drawable.studytestfinish_drawable_btn_skip);
		}
	};

	
	
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
					video_length = Integer.parseInt(json.getJSONObject("data").getString("length"));
					ad_id = json.getJSONObject("data").getInt("ad_id");
					video.setVideoPath("http://todpop.co.kr/"+json.getJSONObject("data").getString("url"));
					video.setOnCompletionListener(cl);
					video.start();
					
					
					Handler mHandler = new Handler();
					mHandler.postDelayed(mLaunchTaskMain, 8000);	// should be 5000 but timing difficulty
					
				} else {		        
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}
	private class SetCPDMlog extends AsyncTask<String, Void, JSONObject> {

		@Override
		protected JSONObject doInBackground(String... urls) {
			JSONObject result = null;
			try {
				DefaultHttpClient httpClient = new DefaultHttpClient();
				String getURL = urls[0];
				HttpGet httpGet = new HttpGet(getURL);
				HttpResponse httpResponse = httpClient.execute(httpGet);
				HttpEntity resEntity = httpResponse.getEntity();

				if (resEntity != null) {    
					result = new JSONObject(EntityUtils.toString(resEntity)); 
					Log.d("SET CPDM LOG RESPONSE ---- ", result.toString());				        	
				}
				return result;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {

			try {
				if(json.getBoolean("status")==true) {
					Intent intent = new Intent(getApplicationContext(), StudyTestResult.class);
					startActivity(intent);
					StudyTestFinish.this.finish();
				} else {		        
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		
	}
	
	
	private MediaPlayer.OnCompletionListener cl = new MediaPlayer.OnCompletionListener() {
		
		@Override
		public void onCompletion(MediaPlayer mp) {
			skipBtn.setEnabled(false);
			Log.d("cpdm view_time----", String.valueOf(video_length));
			new SetCPDMlog().execute("http://todpop.co.kr/api/advertises/set_cpdm_log.json?ad_id="+ad_id+"&ad_type="+ad_type+"&user_id="+rgInfo.getString("mem_id", "0")+"&view_time="+video_length);
		}
	};
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.study_test_finish, menu);
		return true;
	}
	
	public void showTestFinishViewCB(View v)
	{
		v.setEnabled(false);
		int view_time = (int)Math.floor(video.getCurrentPosition()/1000);
		Log.d("cpdm view_time----",""+view_time);
		new SetCPDMlog().execute("http://todpop.co.kr/api/advertises/set_cpdm_log.json?ad_id="+ad_id+"&ad_type="+ad_type+"&user_id="+rgInfo.getString("mem_id", "0")+"&view_time="+view_time);
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
		
	}

}











