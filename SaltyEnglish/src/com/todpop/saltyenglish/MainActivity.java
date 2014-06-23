package com.todpop.saltyenglish;

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
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;
import com.todpop.api.FileManager;
import com.todpop.api.LoadingDialog;
import com.todpop.api.TypefaceActivity;
import com.todpop.saltyenglish.db.PronounceDBHelper;
import com.todpop.saltyenglish.db.WordDBHelper;

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
		
		// Force create Database
		
		SQLiteDatabase db = mHelper.getReadableDatabase();
		try {
			Log.e("STEVEN", "Main Activity line 98");
			db.execSQL("CREATE TABLE mywordtest ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
					"name TEXT, mean TEXT, xo TEXT);");
			db.execSQL("CREATE TABLE dic ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
					"name TEXT, mean TEXT, example_en TEXT, example_ko TEXT, phonetics TEXT, picture INTEGER, image_url TEXT, stage INTEGER, xo TEXT);");
			db.execSQL("CREATE TABLE mywords ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
					"name TEXT NOT NULL UNIQUE, mean TEXT);");
			db.execSQL("CREATE TABLE flip ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
						"name TEXT, mean TEXT, xo TEXT);");
			db.execSQL("CREATE TABLE cpxInfo ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
					"name TEXT NOT NULL UNIQUE, ad_id INTEGER, ad_type INTEGER, reward INTEGER, installed TEXT);");
			Log.e("STEVEN", "Main Activity line 107");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		SQLiteDatabase pDB = pHelper.getReadableDatabase();
		try {
			pDB.execSQL("CREATE TABLE pronounce ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
					"word TEXT NOT NULL UNIQUE, version TEXT, category INTEGER);");
		} catch (Exception e) {
			e.printStackTrace();
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

//	private class GetStageInfoAPI extends AsyncTask<String, Void, JSONObject> {
//		@Override
//		protected JSONObject doInBackground(String... urls) 
//		{
//			Log.d("M A","183");
//			
//			JSONObject result = null;
//			try
//			{
//				DefaultHttpClient httpClient;
//				String getURL = urls[0];
//				HttpGet httpGet = new HttpGet(getURL);
//				HttpParams httpParameters = new BasicHttpParams(); 
//				
//				int timeoutConnection = 3000; 
//				HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection); 
//				int timeoutSocket = 5000; 
//				HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket); 
//
//				httpClient = new DefaultHttpClient(httpParameters); 
//				HttpResponse httpResponse = httpClient.execute(httpGet);
//				HttpEntity resEntity = httpResponse.getEntity();
//
//				Log.d("M A","194");
//				
//				if (resEntity != null)
//				{    
//					result = new JSONObject(EntityUtils.toString(resEntity)); 
//					Log.e("getStage result", result.toString());
//					return result;
//				}
//				return result;
//			}
//			catch (Exception e)
//			{
//			    Log.e("STEVEN", e.toString());
//            	return null;
//			}
//		}
//
//		@Override
//		protected void onPostExecute(JSONObject json) {
//			try {
//				if(json != null){
//					Log.d("FbNickname","243");
//					
//					if(json.getBoolean("status")) {
//						
//						String stage_info = json.getJSONObject("data").getString("stage");
//						studyInfoEdit.putString("stageInfo",stage_info);
//						studyInfoEdit.apply();
//        				if(rgInfo.getBoolean("introMainOk", false)){
//		    				Intent intent = new Intent(getApplicationContext(), StudyHome.class);
//		    				startActivity(intent);
//		    				overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//		    				finish();
//        				}
//        				else{
//	        				Intent intent = new Intent(getApplicationContext(), RgRegisterTutorial.class);
//	        				startActivity(intent);
//		    				overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//	        				finish();
//        				}
//					}
//					else
//					{
//	        			settingEdit = setting.edit();
//	        			settingEdit.putString("isLogin","NO");
//	        			settingEdit.apply();
//	        			
//						popupText.setText(R.string.popup_auto_sign_in_error);
//						popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
//					}
//				}
//				else{
//					popupText.setText(R.string.popup_common_timeout);
//					popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
//				}
//				
//			} catch (Exception e) {
//
//			}
//		}
//	}

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
				Intent intent = new Intent(getApplicationContext(), StudyHome.class);
				startActivity(intent);
				finish();
			} else {																					// not logged in yet
				Intent intent = new Intent(getApplicationContext(), RgLogin.class);
				startActivity(intent);
				finish();
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
