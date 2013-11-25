package com.todpop.saltyenglish;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

public class LvTestFinish extends Activity {

	Button skipBtn;
	ImageView adView;
	VideoView video;
	SharedPreferences rgInfo;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lv_test_finish);
		skipBtn = (Button)findViewById(R.id.lvtestfinish_id_skip_btn);
		adView = (ImageView)findViewById(R.id.rgregisterfinish_id_ad);
		Handler mHandler = new Handler();
		  mHandler.postDelayed(mLaunchTaskMain, 5000);
		  
		  video = (VideoView)findViewById(R.id.test_video_view);
		  rgInfo = getSharedPreferences("rgInfo",0);
		  new GetCPDM().execute("http://todpop.co.kr/api/advertises/get_cpdm_ad.json?user_id="+rgInfo.getString("mem_id", "NO"));
//			video.setVideoPath("http://todpop.co.kr/uploads/cpdm_advertisement/video/4/CPDM_sample.mp4");
//			
//			final MediaController mc = new MediaController(LvTestFinish.this);
//			video.setMediaController(mc);
////			video.postDelayed(new Runnable() {
////				public void run() {
////					mc.show(0);
////				}
////			}, 100);
//			video.start();
	}
	


	private Runnable mLaunchTaskMain = new Runnable() {
	public void run() {
			skipBtn.setEnabled(true);
			skipBtn.setBackgroundResource(R.drawable.lvtestfinish_drawable_skip_btn);
			
		}
	};
	
	//--- request class ---
	private class GetCPDM extends AsyncTask<String, Void, JSONObject> 
	{
		@Override
		protected JSONObject doInBackground(String... urls) 
		{
			JSONObject result = null;
			try
			{
				DefaultHttpClient httpClient = new DefaultHttpClient();
				String getURL = urls[0];
				HttpGet httpGet = new HttpGet(getURL);
				HttpResponse httpResponse = httpClient.execute(httpGet);
				HttpEntity resEntity = httpResponse.getEntity();

				if (resEntity != null)
				{    
					result = new JSONObject(EntityUtils.toString(resEntity)); 
					Log.d("RESPONSE ---- ", result.toString());				        	
				}
				return result;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			return result;
		}

		@Override
		protected void onPostExecute(JSONObject json) {

			try {
				if(json.getBoolean("status")==true)
				{
					video.setVideoPath("http://todpop.co.kr/"+json.getJSONObject("data").getString("url"));
					final MediaController mc = new MediaController(LvTestFinish.this);
					//mc.hide();
					//video.setMediaController(mc);
					video.start();
				}else{		        
					Log.d("-----------------------", "Login Failed");
				}
			} catch (Exception e) {

			}

		}
	}

	public void onClick(View view)
	{
		Intent intent = new Intent(getApplicationContext(), LvTestResult.class);
		startActivity(intent);
		finish();
		
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) 
		{
			final AlertDialog.Builder isExit = new AlertDialog.Builder(this);

			DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() 
			{
				public void onClick(DialogInterface dialog, int which) 
				{
					switch (which) 
					{
					case AlertDialog.BUTTON_POSITIVE:
						SharedPreferences settings = getSharedPreferences("setting", 0);
						SharedPreferences.Editor editor = settings.edit();
						editor.putString("check","YES");
						editor.commit();
						
						Intent intent = new Intent();
				        intent.setClass(LvTestFinish.this, MainActivity.class);    
				        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
				        startActivity(intent);
						finish();	
						break;
					case AlertDialog.BUTTON_NEGATIVE:
						break;
					default:
						break;
					}
				}
			};

			isExit.setTitle(getResources().getString(R.string.register_alert_title));
			isExit.setMessage(getResources().getString(R.string.register_alert_text));
			isExit.setPositiveButton("OK", listener);
			isExit.setNegativeButton("Cancel", listener);
			isExit.show();

			return false;
		}
		return false;
	}
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.lv_test_finish, menu);
		return true;
	}

}
