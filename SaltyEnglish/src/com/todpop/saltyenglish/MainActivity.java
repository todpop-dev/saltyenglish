package com.todpop.saltyenglish;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends Activity 
{
	//declare define popup view
	PopupWindow popupWindow;
	View popupview;
	RelativeLayout relative;
	TextView popupText;
	AnimationDrawable mainLoading;
	String mobile = "";

	
	SharedPreferences rgInfo;
	SharedPreferences.Editor rgInfoEdit;
	
	SharedPreferences settings;
	SharedPreferences.Editor settingsEditor;
	
	WordDBHelper mHelper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		settings = getSharedPreferences("setting", 0);
		settingsEditor = settings.edit();
		
		rgInfo = getSharedPreferences("rgInfo",0);
		rgInfoEdit = rgInfo.edit();
	
		mHelper = new WordDBHelper(this);
		
		/*//popupview
		relative = (RelativeLayout)findViewById(R.id.main_activity_id_main);
		popupview = View.inflate(this, R.layout.popup_view, null);
		float density = getResources().getDisplayMetrics().density;
		popupWindow = new PopupWindow(popupview,(int)(300*density),(int)(200*density),true);
		popupText = (TextView)popupview.findViewById(R.id.http_popup_id_text);*/

		//loading animation
		ImageView rocketImage = (ImageView) findViewById(R.id.main_id_loading);
		rocketImage.setBackgroundResource(R.drawable.main_drawable_loading);
		mainLoading = (AnimationDrawable) rocketImage.getBackground();
		mainLoading.start();

		//get phone number
		try {
			TelephonyManager phoneMgr=(TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE); 
			mobile =phoneMgr.getLine1Number().toString();
			mobile = mobile.replace("+82", "0");
		} catch(Exception e) {
			mobile = "01000001001";
		}

		//SharedPreferences settings = getSharedPreferences("setting", 0);
		if(settings.getString("check","NO").equals("YES"))
		{
			
			settingsEditor.putString("check","NO");
			settingsEditor.commit();

			finish();
		} else {
			Log.d("Phone No............. ", mobile);

			new CheckLogin().execute("http://todpop.co.kr/api/users/check_mobile_exist.json?mobile="+mobile);
			//new RgInfo().execute("http://todpop.co.kr/api/users/resign_up_info.json?mobile="+mobile);
		}

		
		// Force create Database
		
		SQLiteDatabase db = mHelper.getReadableDatabase();
		try {
			db.execSQL("CREATE TABLE dic ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
					"name TEXT, mean TEXT, example_en TEXT, example_ko TEXT, phonetics TEXT, picture INTEGER, image_url TEXT, stage INTEGER, xo TEXT);");
			db.execSQL("CREATE TABLE mywords ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
					"name TEXT NOT NULL UNIQUE, mean TEXT);");
			db.execSQL("CREATE TABLE flip ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
						"name TEXT, mean TEXT, xo TEXT);");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//--- request class ---
	private class CheckLogin extends AsyncTask<String, Void, JSONObject> 
	{
		DefaultHttpClient httpClient ;
		@Override
		protected JSONObject doInBackground(String... urls) 
		{
			JSONObject result = null;
			try
			{
				String getURL = urls[0];
				HttpGet httpGet = new HttpGet(getURL); 
				HttpParams httpParameters = new BasicHttpParams(); 
				
				int timeoutConnection = 3000; 
				HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection); 
				int timeoutSocket = 3000; 
				HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket); 

				httpClient = new DefaultHttpClient(httpParameters); 
				HttpResponse response = httpClient.execute(httpGet); 
				HttpEntity resEntity = response.getEntity();

				if (resEntity != null)
				{    
					result = new JSONObject(EntityUtils.toString(resEntity)); 
					Log.d("RESPONSE JSON CHECK MOBILE EXIST ---- ", result.toString());				        	
				}
				return result;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally 
			{     
				httpClient.getConnectionManager().shutdown();     
			} 
			return result;
		}

		@Override
		protected void onPostExecute(JSONObject json) {
//			if(json == null) {
//				popupText.setText(R.string.rg_register_network_error);
//				popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
//			}

			try {
				if(json.getBoolean("status")==false) {
					// Setup emailCheck and fbCheck to NO and Jump to Register & Login Activity
					rgInfoEdit.putString("email", "NO");
					rgInfoEdit.putString("facebookEmail", "NO");
					rgInfoEdit.putString("mobile", mobile);
					rgInfoEdit.putString("level", "NO");
					rgInfoEdit.commit();
					
					Intent intent = new Intent(getApplicationContext(), RgLoginAndRegister.class);
					startActivity(intent);
				} else {	
					
					new RgInfo().execute("http://todpop.co.kr/api/users/resign_up_info.json?mobile="+mobile);
						      
					rgInfoEdit.putString("mobile",json.getJSONObject("data").getString("mobile"));
					rgInfoEdit.putString("level", json.getJSONObject("data").getString("level_test"));
					if(settings.getString("isLogin","NO").equals("YES")) {
						if(json.getJSONObject("data").getInt("level_test")>0)
						{
							Intent intent = new Intent(getApplicationContext(), StudyHome.class);
							startActivity(intent);
						}else{
							Intent intent = new Intent(getApplicationContext(), LvTestBigin.class);
							startActivity(intent);
						}
					} else {
						if(json.getJSONObject("data").getString("email")=="null") {
							rgInfoEdit.putString("email","NO");
						} else {
							rgInfoEdit.putString("email",json.getJSONObject("data").getString("email"));
						}
						if(json.getJSONObject("data").getString("facebook")=="null") {
							rgInfoEdit.putString("facebookEmail", "NO");
						} else {
							rgInfoEdit.putString("facebookEmail",json.getJSONObject("data").getString("facebook"));
						}
						
						Intent intent = new Intent(getApplicationContext(), RgLoginAndRegister.class);
						startActivity(intent);
					}
				}
				rgInfoEdit.commit();
				Log.d("return info",rgInfo.getString("email", "--")+"  "
						+rgInfo.getString("facebookEmail", "--")+"  "
						+rgInfo.getString("mobile", "--")+"  "
						+rgInfo.getString("level", "--")+"  "
						+rgInfo.getString("nickname", "--")+"  "
						+rgInfo.getString("recommend", "--")+"  "
						+rgInfo.getString("mem_id", "-- ")+"  "
						+rgInfo.getString("password", "--"));
				mainLoading.stop();

			} catch (Exception e) {

			}
		}
	}
	
	private class RgInfo extends AsyncTask<String, Void, JSONObject> 
	{
		DefaultHttpClient httpClient ;
		@Override
		protected JSONObject doInBackground(String... urls) 
		{
			JSONObject result = null;
			try
			{
				String getURL = urls[0];
				HttpGet httpGet = new HttpGet(getURL); 
				HttpParams httpParameters = new BasicHttpParams(); 
				httpClient = new DefaultHttpClient(httpParameters); 
				HttpResponse response = httpClient.execute(httpGet); 
				HttpEntity resEntity = response.getEntity();


				if (resEntity != null)
				{    
					result = new JSONObject(EntityUtils.toString(resEntity)); 
					Log.d("RESPONSE JSON CHECK MOBILE EXIST ---- ", result.toString());				        	
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
					rgInfoEdit.putString("nickname", json.getJSONObject("data").getString("nickname"));
					rgInfoEdit.putString("password", json.getJSONObject("data").getString("is_set_password"));
					if(json.getJSONObject("data").getString("recommend").equals(""))
					{
						rgInfoEdit.putString("recommend", "NO");
					}else{
						rgInfoEdit.putString("recommend", json.getJSONObject("data").getString("recommend"));
					}
					rgInfoEdit.putString("mem_id", json.getJSONObject("data").getString("mem_id"));
				}else{
					rgInfoEdit.putString("nickname", "NO");
					rgInfoEdit.putString("recommend", "NO");
					rgInfoEdit.putString("mem_id", "NO");
					rgInfoEdit.putString("password", "NO");
				}
				rgInfoEdit.commit();
				
				//new CheckLogin().execute("http://todpop.co.kr/api/users/check_mobile_exist.json?mobile="+mobile);
			} catch (Exception e) {

			}
		}
	}


	/*//onClick
	public void closePopup(View v)
	{
		popupWindow.dismiss();
		new CheckLogin().execute("http://todpop.co.kr/api/users/resign_up_info.json?mobile="+mobile);
	}*/

	//---disable back btn---
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	
	//------- Database Operation ------------------
	private class WordDBHelper extends SQLiteOpenHelper {
		public WordDBHelper(Context context) {
			super(context, "EngWord.db", null, 1);
		}
		
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE dic ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
		"name TEXT, mean TEXT, example_en TEXT, example_ko TEXT, phonetics TEXT, picture INTEGER, image_url TEXT, stage INTEGER, xo TEXT);");
			db.execSQL("CREATE TABLE mywords ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
		"name TEXT NOT NULL UNIQUE, mean TEXT);");
			db.execSQL("CREATE TABLE flip ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
		"name TEXT, mean TEXT, xo TEXT);");
		}
		
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS dic");
			db.execSQL("DROP TABLE IF EXISTS flip");
			db.execSQL("DROP TABLE IF EXISTS mywords");
			onCreate(db);
		}
	}


	@Override
	public void onDestroy()
	{
		super.onDestroy();
		mHelper.close();
	}

}
