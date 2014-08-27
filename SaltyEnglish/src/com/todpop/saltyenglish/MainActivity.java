package com.todpop.saltyenglish;

import org.apache.http.HttpEntity;

import com.facebook.Session;
import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;
import com.todpop.api.LoadingDialog;
import com.todpop.api.FileManager;
import com.todpop.api.TypefaceActivity;
import com.todpop.saltyenglish.db.PronounceDBHelper;
import com.todpop.saltyenglish.db.WordDBHelper;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends TypefaceActivity 
{
	AnimationDrawable mainLoading;
	
	//popup view
	PopupWindow popupWindow;
	View popupview;
	RelativeLayout relative;
	TextView popupText;
	
	SharedPreferences rgInfo;
	SharedPreferences setting;
	SharedPreferences.Editor settingEdit;
	SharedPreferences studyInfo;
	SharedPreferences.Editor studyInfoEdit;
	
	TextView totalReward;
	
	WordDBHelper mHelper;
	PronounceDBHelper pHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//popupview
		relative = (RelativeLayout)findViewById(R.id.main_activity_id_main);;
		popupview = View.inflate(this, R.layout.popup_view, null);
		popupWindow = new PopupWindow(popupview,ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT,true);
		popupText = (TextView)popupview.findViewById(R.id.popup_id_text);
		
		setFont(popupText);
		
		rgInfo = getSharedPreferences("rgInfo",0);
		setting = getSharedPreferences("setting", 0);
		settingEdit = setting.edit();
		studyInfo = getSharedPreferences("studyInfo", 0);
		studyInfoEdit = studyInfo.edit();
		
		mHelper = new WordDBHelper(this);
		pHelper = new PronounceDBHelper(this);

		totalReward = (TextView)findViewById(R.id.main_id_total_reward_amount);
		new GetTotalRewardAPI().execute("http://todpop.co.kr/api/etc/show_service_stat.json");

		if(setting.getString("check","NO").equals("YES"))												// want to quit
		{
			settingEdit.putString("check","NO");
			settingEdit.apply();

			finish();
		} else {
			if(isTableExisting("wordSound")){
				new Migration().execute();
			}
			else{
				Handler handler = new Handler();
		        handler.postDelayed(delayed, 2000);
			}
		}
		
		if(setting.getBoolean("lockerEnabled", true)){
			Intent i = new Intent(this, LockScreenService.class);
			startService(i);
		}
	}
	
	public void closePopup(View v)
	{
		popupWindow.dismiss();
		finish();
	}
	
	// -------------- get stage info ---------------------------------------------------------------

	private class GetStageInfoAPI extends AsyncTask<String, Void, JSONObject> {
		@Override
		protected JSONObject doInBackground(String... urls) 
		{
			JSONObject result = null;
			try
			{
				DefaultHttpClient httpClient;
				String getURL = urls[0];
				HttpGet httpGet = new HttpGet(getURL);
				HttpParams httpParameters = new BasicHttpParams(); 
				
				int timeoutConnection = 3000; 
				HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection); 
				int timeoutSocket = 5000; 
				HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket); 

				httpClient = new DefaultHttpClient(httpParameters); 
				HttpResponse httpResponse = httpClient.execute(httpGet);
				HttpEntity resEntity = httpResponse.getEntity();

				if (resEntity != null)
				{    
					result = new JSONObject(EntityUtils.toString(resEntity)); 
					Log.e("getStage result", result.toString());
					return result;
				}
				return result;
			}
			catch (Exception e)
			{
            	return null;
			}
		}

		@Override
		protected void onPostExecute(JSONObject json) {
			try {
				if(json != null){
					if(json.getBoolean("status")) {
						
						String stage_info = json.getJSONObject("data").getString("stage");
						studyInfoEdit.putString("stageInfo",stage_info);
						studyInfoEdit.apply();
        				if(rgInfo.getBoolean("introMainOk", false)){
		    				Intent intent = new Intent(getApplicationContext(), StudyHome.class);
		    				startActivity(intent);
		    				overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		    				finish();
        				}
        				else{
	        				Intent intent = new Intent(getApplicationContext(), RgRegisterTutorial.class);
	        				startActivity(intent);
		    				overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	        				finish();
        				}
					}
					else
					{
	        			settingEdit = setting.edit();
	        			settingEdit.putString("isLogin","NO");
	        			settingEdit.apply();
	        			
						popupText.setText(R.string.popup_auto_sign_in_error);
						popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
					}
				}
				else{
					popupText.setText(R.string.popup_common_timeout);
					popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
				}
				
			} catch (Exception e) {

			}
		}
	}
	// -------------- get total reward amount from server ---------------------------------------------------------------

	private class GetTotalRewardAPI extends AsyncTask<String, Void, JSONObject> {
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
					return result;
				}
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
				if(json.getBoolean("status")) {
					totalReward.setText(json.getString("total_reward_amount") + getResources().getString(R.string.testname8));
				}
				else
				{
				}
				
			} catch (Exception e) {

			}
		}
	}

	// --------------------------------------------------------------------------------------------------------------
	private class Migration extends AsyncTask<Void, Void, Boolean> {
		LoadingDialog loading = new LoadingDialog(MainActivity.this);
		
		@Override
		protected void onPreExecute(){
			loading.showMigration();
		}
		
		@Override
		protected Boolean doInBackground(Void... params) {
			
			SQLiteDatabase oldDB = mHelper.getWritableDatabase();
			SQLiteDatabase newDB = pHelper.getWritableDatabase();
				
			Cursor find = oldDB.rawQuery("SELECT distinct word, version, category FROM wordSound", null);
				
			find.moveToFirst();
				
			FileManager mFile = new FileManager();
				
			while(!find.isAfterLast()){
				if(mFile.moveFile(MainActivity.this, find.getString(0), "/Android/data/com.todpop.saltyenglish/pronounce/")){
					oldDB.delete("wordSound", "word='" + find.getString(0) + "'", null);
					
			        ContentValues row = new ContentValues();
					row.put("word", find.getString(0));
					row.put("version", find.getString(1));
					row.put("category", find.getString(2));

					newDB.insert("pronounce", null, row);
				}
				else{
					oldDB.close();
					newDB.close();
					return false;
				}
				find.moveToNext();
			}
			find.close();
			oldDB.close();
			newDB.close();
			
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			SQLiteDatabase db = mHelper.getWritableDatabase();
			db.execSQL("DROP TABLE IF EXISTS wordSound");
			db.close();
			
			loading.dissmiss();
			
			Handler handler = new Handler();
	        handler.postDelayed(delayed, 2000);
		}
	}

	//---disable back btn---
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private boolean isTableExisting(String tableName){
		try{
			SQLiteDatabase db = mHelper.getReadableDatabase();
			Cursor cursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + tableName + "'", null);
			if(cursor != null){
				if(cursor.getCount() > 0){
					cursor.close();
					return true;
				}
				cursor.close();
			}
			return false;
		} catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	Runnable delayed = new Runnable() {
    	@Override
        public void run() {
			if(setting.getString("isLogin","NO").equals("YES")) {										// already logged in
				// update user info (only level)
				new GetStageInfoAPI().execute("http://todpop.co.kr/api/studies/get_stage_info.json?user_id=" + rgInfo.getString("mem_id",null));
			} else {																					// not logged in yet
    		    Session session = Session.getActiveSession();
    		    if (session != null) {
    		        if (!session.isClosed()) {
    		            session.closeAndClearTokenInformation();
    		            //clear your preferences if saved
    		        }
    		    } else {
    		        session = new Session(getApplicationContext());
    		        Session.setActiveSession(session);

    		        session.closeAndClearTokenInformation();
    		            //clear your preferences if saved
    		    }
				Intent intent = new Intent(getApplicationContext(), RgLoginAndRegister.class);
				startActivity(intent);
				overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			}
        }
    };
    
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		mHelper.close();
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
}
