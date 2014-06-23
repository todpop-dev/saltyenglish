package com.todpop.saltyenglish;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;
import com.todpop.api.TypefaceActivity;

public class WordListTestFinish extends TypefaceActivity {
	// popup view
	PopupWindow popupWindow;
	View popupview;
	RelativeLayout relative;
	TextView popupText;

	Button skipBtn;
	ImageView marking;
	ImageView markingDone;
	Button shareBtn;

	private LinearLayout fbShareLayout;
	private TextView fbShareReward;
	
	String reward;
	String point;
	String name;
	String caption;
	String description;
	String link;
	String picture;

	String sharedId;
	
	SharedPreferences rgInfo;

	AudioManager audio;
	int oldVolume;
	
	private int video_length =0;
	private VideoView video;
	private int ad_id = -1;
	private int ad_type;
	int view_time = 0;
	private boolean shareDone = false;
	private String sharedHistory;
	private boolean shareTried = false;
	private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
	private boolean pendingPublishReauthorization = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_study_test_finish);

		//popupview
		relative = (RelativeLayout)findViewById(R.id.testfinish_id_main);;
		popupview = View.inflate(this, R.layout.popup_view, null);
		popupWindow = new PopupWindow(popupview,ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT,true);
		popupText = (TextView)popupview.findViewById(R.id.popup_id_text);
		
		setFont(popupText);
		
		skipBtn = (Button) findViewById(R.id.testfinish_id_skip_btn);
		rgInfo = getSharedPreferences("rgInfo", 0);
		video = (VideoView) findViewById(R.id.test_video_view);
		marking = (ImageView) findViewById(R.id.testfinish_id_marking);
		markingDone = (ImageView) findViewById(R.id.testfinish_id_marking_completed);
		shareBtn = (Button) findViewById(R.id.testfinish_fb_share_btn);

		fbShareLayout = (LinearLayout)findViewById(R.id.testfinish_fb_share_layout);
		fbShareReward = (TextView)findViewById(R.id.testfinish_fb_share_reward);
		
		audio = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
		oldVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
		
		new GetCPDM()
				.execute("http://todpop.co.kr/api/advertises/get_cpdm_ad.json?user_id="
						+ rgInfo.getString("mem_id", "0"));
		}
	


	private Runnable mLaunchTaskMain = new Runnable() {
		public void run() {
			marking.setVisibility(View.INVISIBLE);
			markingDone.setVisibility(View.VISIBLE);
			skipBtn.setEnabled(true);
			skipBtn.setBackgroundResource(R.drawable.studytestfinish_drawable_btn_skip);
		}
	};
	
	//--- request class ---
	private class GetCPDM extends AsyncTask<String, Void, JSONObject> {
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
					Log.d("CPDM RESPONSE ---- ", result.toString());
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
					ad_type = json.getJSONObject("data").getInt("ad_type");
					video_length = Integer.parseInt(json.getJSONObject("data")
							.getString("length"));
					ad_id = json.getJSONObject("data").getInt("ad_id");
					video.setVideoPath("http://todpop.co.kr/"
							+ json.getJSONObject("data").getString("url"));
					video.setOnPreparedListener(opl);
					video.start();
					
					if(ad_type == 202){
						shareBtn.setVisibility(View.VISIBLE);
						reward = json.getJSONObject("data").getString("reward");
						point = json.getJSONObject("data").getString("point");
						name = json.getJSONObject("data").getString("name");
						caption = json.getJSONObject("data").getString("caption");
						description = json.getJSONObject("data").getString("description");
						link = json.getJSONObject("data").getString("link");
						picture = json.getJSONObject("data").getString("picture");

						if(sharedHistory.equals("0")){
							shareBtn.setEnabled(false);
							fbShareReward.setText(R.string.facebook_share_history);
						}
						else{
							if(reward.equals("0") || reward.equals("null")){
								fbShareReward.setText(point + " point");
							}
							else{
								fbShareReward.setText(reward + getResources().getString(R.string.testname8));
							}
						}
					}
					else{
						video.setOnCompletionListener(cl);
					}

					Map<String, String> cpdmParams = new HashMap<String, String>();
					cpdmParams.put("CPDM ID", String.valueOf(ad_id));
					FlurryAgent.logEvent("CPDM", cpdmParams, true);

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
				String getURL;
				if(shareDone){
					getURL = urls[0] + "&facebook_id=" + sharedId;
				}
				else{
					getURL = urls[0];
				}
				HttpGet httpGet = new HttpGet(getURL);
				HttpResponse httpResponse = httpClient.execute(httpGet);
				HttpEntity resEntity = httpResponse.getEntity();

				if (resEntity != null) {
					result = new JSONObject(EntityUtils.toString(resEntity));
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
					/*Intent intent = new Intent(getApplicationContext(), WordListTestResult.class);
					startActivity(intent);
					WordListTestFinish.this.finish();*/
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
			new SetCPDMlog()
			.execute("http://todpop.co.kr/api/advertises/set_cpdm_log.json?ad_id="
					+ ad_id
					+ "&ad_type="
					+ ad_type
					+ "&user_id="
					+ rgInfo.getString("mem_id", "0")
					+ "&act=1&view_time="
					+ video_length);

			Intent intent = new Intent(getApplicationContext(), WordListTestResult.class);
			startActivity(intent);
			WordListTestFinish.this.finish();
		}
	};
	
	private MediaPlayer.OnPreparedListener opl = new MediaPlayer.OnPreparedListener() {

		@Override
		public void onPrepared(MediaPlayer arg0) {
			
			Log.e("cpdm----", "ready");
			
			Handler mHandler = new Handler();
			mHandler.postDelayed(mLaunchTaskMain, 5000);	// exact 5000 timing try
			
		}
	
	};

	public void showTestFinishViewCB(View v){		
		v.setEnabled(false);
	
		if(view_time == 0)
			view_time = (int) Math.floor(video.getCurrentPosition() / 1000);
			
		Log.d("cpdm view_time----", "" + view_time);
		FlurryAgent.endTimedEvent("CPDM");
		new SetCPDMlog()
				.execute("http://todpop.co.kr/api/advertises/set_cpdm_log.json?ad_id="
						+ ad_id
						+ "&ad_type="
						+ ad_type
						+ "&user_id="
						+ rgInfo.getString("mem_id", "0")
						+ "&act=1&view_time="
						+ view_time);
		Intent intent = new Intent(getApplicationContext(), WordListTestResult.class);
		startActivity(intent);
		WordListTestFinish.this.finish();
	}
	

	private boolean isSubsetOf(Collection<String> subset,
			Collection<String> superset) {
		for (String string : subset) {
			if (!superset.contains(string)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}
	public void closePopup(View v)
	{
		popupWindow.dismiss();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		return false;
	}
	@Override
	public void onResume() {
		super.onResume();

		int maxVol = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		int volume = (int) (maxVol * 0.3);
		if(oldVolume > volume)
			audio.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
	}

	@Override
	public void onPause() {
		super.onPause();
		video.stopPlayback();
		audio.setStreamVolume(AudioManager.STREAM_MUSIC, oldVolume, 0);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}	
	
	@Override
	public void onRestart() {
		super.onRestart();
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
	
	@Override
	public void onBackPressed(){
		//Do nothing
	}
}
