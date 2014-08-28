package com.todpop.saltyenglish;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;
import com.todpop.api.TypefaceActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;

public class HomeMore extends TypefaceActivity {
	private int count = 0;
	private ImageView makers;
	
	SharedPreferences rgInfo;
	SharedPreferences.Editor rgInfoEdit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_more);
		
		makers = (ImageView)findViewById(R.id.homemore_id_makers);
		
		rgInfo = getSharedPreferences("rgInfo",0);
		rgInfoEdit = rgInfo.edit();
	}
	
	@Override
	public void onResume()
	{
		super.onResume();

		com.facebook.AppEventsLogger.activateApp(this, "218233231697811");
		new CheckPw().execute("http://todpop.co.kr/api/users/"+rgInfo.getString("mem_id", "NO")+"/is_set_facebook_password.json");
		
		
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.home_more, menu);
		return false;
	}
	
	private class CheckPw extends AsyncTask<String, Void, JSONObject> 
	{
		
		@Override
		protected JSONObject doInBackground(String... urls) 
		{
			JSONObject result = null;
			try
			{
				String getURL = urls[0];
				HttpGet httpGet = new HttpGet(getURL); 
				HttpParams httpParameters = new BasicHttpParams(); 
				DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters); 
				HttpResponse response = httpClient.execute(httpGet); 
				HttpEntity resEntity = response.getEntity();
				if (resEntity != null)
				{    
					result = new JSONObject(EntityUtils.toString(resEntity)); 
					Log.d("RESPONSE JSON ---- ", result.toString());				        	
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
					if(json.getJSONObject("data").getBoolean("is_set"))
					{
						rgInfoEdit.putString("password", "1");
					}else{
						rgInfoEdit.putString("password", "0");
					}
					rgInfoEdit.apply();
				}else{
					
				}
				
			} catch (Exception e) {

			}
		}
	}

	// on click
	public void onClickBack(View view)
	{
		finish();
	}
	
	public void showSettingActivity(View view)
	{
		Intent intent = new Intent(getApplicationContext(), HomeMoreSetting.class);
		startActivity(intent);
	}
	
	public void showNoticeActivity(View view)
	{
		Intent intent = new Intent(getApplicationContext(), HomeMoreNotice.class);
		startActivity(intent);
	}
	
	public void showMore(View view){
		count++;
		if(count >= 5){
			makers.setVisibility(View.VISIBLE);
		}
	}
	
	public void dismiss(View view){
		makers.setVisibility(View.GONE);
	}
	
	public void showAccountInfoActivity(View view)
	{
		Intent intent = new Intent(getApplicationContext(), HomeMoreAccountInfo.class);
		startActivity(intent);
	}
	
	public void showHelpActivity(View view)
	{
		Intent intent = new Intent(getApplicationContext(), HomeMoreHelp.class);
		startActivity(intent);
	}
	
	public void showPronounceActivity(View view){
		Intent intent = new Intent(getApplicationContext(), HomeMorePronounce.class);
		startActivity(intent);
	}
	@Override
	protected void onStart()
	{
		super.onStart();
		FlurryAgent.onStartSession(this, "ZKWGFP6HKJ33Y69SP5QY");
		FlurryAgent.logEvent("See More");
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