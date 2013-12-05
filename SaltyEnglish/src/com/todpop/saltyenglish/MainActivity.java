package com.todpop.saltyenglish;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
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
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends Activity 
{
	AnimationDrawable mainLoading;
	
	SharedPreferences rgInfo;
	SharedPreferences setting;
	SharedPreferences.Editor settingEdit;
	
	WordDBHelper mHelper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		rgInfo = getSharedPreferences("rgInfo",0);
		setting = getSharedPreferences("setting", 0);
		settingEdit = setting.edit();
	
		mHelper = new WordDBHelper(this);


		//loading animation
		ImageView rocketImage = (ImageView) findViewById(R.id.main_id_loading);
		rocketImage.setBackgroundResource(R.drawable.main_drawable_loading);
		mainLoading = (AnimationDrawable) rocketImage.getBackground();
		mainLoading.start();

		if(setting.getString("check","NO").equals("YES"))												// want to quit
		{
			settingEdit.putString("check","NO");
			settingEdit.commit();

			finish();
		} else {
			Log.d("[Register-1]","New service started");
			
			if(setting.getString("isLogin","NO").equals("YES")) {										// already logged in
				// update user info (only level)
				new SignInAPI().execute("http://todpop.co.kr/api/users/sign_in.json");
			} else {																					// not logged in yet
				Intent intent = new Intent(getApplicationContext(), RgLoginAndRegister.class);
				startActivity(intent);
			}
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
			db.execSQL("CREATE TABLE cpxInfo ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
					"name TEXT NOT NULL UNIQUE, ad_id INTEGER, ad_type INTEGER, reward INTEGER, installed TEXT);");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	private class SignInAPI extends AsyncTask<String, Void, JSONObject> 
	{
        @Override
        protected JSONObject doInBackground(String... urls) 
        {
        	JSONObject json = null;

        	try
        	{
        		HttpClient client = new DefaultHttpClient();  
        		String postURL = urls[0];
        		HttpPost post = new HttpPost(postURL); 
        		List<NameValuePair> params = new ArrayList<NameValuePair>();

        		String user_id = rgInfo.getString("mem_id", "");
        		params.add(new BasicNameValuePair("user_id", user_id));

        		UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
        		post.setEntity(ent);
        		HttpResponse responsePOST = client.execute(post);  
        		HttpEntity resEntity = responsePOST.getEntity();

        		if (resEntity != null)
        		{    
        			json = new JSONObject(EntityUtils.toString(resEntity)); 
        			Log.d("[Register-1] user info check", json.toString());				        	
        			return json;
        		}
        		return json;
        	}
        	catch (Exception e)
        	{
			        e.printStackTrace();
			}
        	
        	return json;
        }
        
        @Override
        protected void onPostExecute(JSONObject result) {
        	try {
        		if (result.getBoolean("status")==true) {
        			if (result.getJSONObject("data").getJSONObject("user").getInt("level_test")>0)
        			{
        				
        				// cyscys load UserStageOpened
        				
        				
        				
        				
        				
        				Intent intent = new Intent(getApplicationContext(), StudyHome.class);
        				startActivity(intent);
        			}else{
        				Intent intent = new Intent(getApplicationContext(), LvTestBigin.class);
        				startActivity(intent);
        			}
        		} else {
        			// something wrong (ex: user deleted) = logout
        			settingEdit = setting.edit();
        			settingEdit.putString("isLogin","NO");
        			settingEdit.commit();
        			
        			finish();
        		}
        	}catch (Exception e) {
        	}
        }
	}

	// --------------------------------------------------------------------------------------------------------------
	

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
			db.execSQL("CREATE TABLE cpxInfo ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
					"name TEXT NOT NULL UNIQUE, ad_id INTEGER, ad_type INTEGER, reward INTEGER, installed TEXT);");
		}
		
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS dic");
			db.execSQL("DROP TABLE IF EXISTS flip");
			db.execSQL("DROP TABLE IF EXISTS mywords");
			db.execSQL("DROP TABLE IF EXISTS cpxInfo");
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
