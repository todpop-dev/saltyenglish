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

import com.facebook.Session;
import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class HomeMoreAccountInfo extends Activity {

	// popup view
	PopupWindow popupWindow;
	View popupview;
	RelativeLayout relative;
	TextView popupText;
	
	PopupWindow popupWindow1;
	View popupview1;
	TextView popupText1;
	
	Button setPwBtn;
	
	SharedPreferences settings;
	SharedPreferences.Editor settingsEditor;
	
	boolean isLogoutBtn = false;
	boolean isDeleteAccountBtn = false;
	boolean isSetPwBtn = false;
	
	SharedPreferences rgInfo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		
		settings = getSharedPreferences("setting", 0);
		settingsEditor = settings.edit();
		rgInfo = getSharedPreferences("rgInfo",0);
		
		if(settings.getString("loginType", "NO").equals("email"))
		{
			FlurryAgent.logEvent("Account Info (Email)");
			setContentView(R.layout.activity_home_more_acount_info_email);
			relative = (RelativeLayout)findViewById(R.id.home_more_acount_info_id_main_email_activity);
		}else{
			FlurryAgent.logEvent("Account Info (Facebook)");
			setContentView(R.layout.activity_home_more_acount_info_fb);
			relative = (RelativeLayout)findViewById(R.id.home_more_acount_info_id_main_fb_activity);
			setPwBtn = (Button)findViewById(R.id.home_more_accoun_setpw_btn);
			// if(rgInfo.getString("password", "NO").equals("1")) {setPwBtn.setEnabled(false);}    // not needed by CYS
		}
		

		//popupview
		popupview = View.inflate(this, R.layout.popup_view_home_more_acount_info, null);
		popupWindow = new PopupWindow(popupview,ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT,true);
		popupText = (TextView)popupview.findViewById(R.id.popup_id_text);

		popupview1 = View.inflate(this, R.layout.popup_view, null);
		popupWindow1 = new PopupWindow(popupview1,ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT,true);
		popupText1 = (TextView)popupview1.findViewById(R.id.popup_id_text);
	}

	@Override
	public void onResume()
	{
		super.onResume();

		com.facebook.AppEventsLogger.activateApp(this, "218233231697811");
		isLogoutBtn = false;
		isDeleteAccountBtn = false;
		isSetPwBtn = false;
	}
	
	private class SetPw extends AsyncTask<String, Void, JSONObject> 
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
				if(json.getBoolean("status")==true) {
					popupText1.setText(R.string.home_more_acount_set_pwd3);
					popupWindow1.showAtLocation(relative, Gravity.CENTER, 0, 0);
				} else {		      
					popupText1.setText(R.string.home_more_acount_set_pwd4);
					popupWindow1.showAtLocation(relative, Gravity.CENTER, 0, 0);
				}
			} catch (Exception e) {

			}
		}
	}

	// on click

	public void closePopup(View v)
	{
		isLogoutBtn = false;
		isDeleteAccountBtn = false;
		isSetPwBtn = false;
		popupWindow.dismiss();
		popupWindow1.dismiss();
	}

	public void confirmPopup(View v)
	{
		if(isLogoutBtn == true)
		{
			settingsEditor.putString("isLogin","NO");
			settingsEditor.putString("loginType", "NO");
			settingsEditor.apply();
			/*
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
			}*/

			Intent intent = new Intent();
			intent.setClass(HomeMoreAccountInfo.this, MainActivity.class);    
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
			startActivity(intent);
			finish();
		}else if(isDeleteAccountBtn == true)
		{
			Intent intent = new Intent(getApplicationContext(), HomeMoreAccountDelete.class);
			startActivity(intent);
		}else if(isSetPwBtn = true)
		{	
			new SetPw().execute("http://todpop.co.kr/api/users/"+rgInfo.getString("mem_id", "NO")+"/setting_facebook_password.json?nickname="+rgInfo.getString("nickname", "NO")+"&mobile="+rgInfo.getString("mobile", "NO"));
		}
		popupWindow.dismiss();
	}


	public void onClickBack(View view)
	{
		finish();
	}

	public void showChangePwActivity(View view)
	{
		Intent intent = new Intent(getApplicationContext(), HomeMoreAccountChangePw.class);
		startActivity(intent);
	} 

	public void logout(View view)
	{
		isLogoutBtn = true;
		
		popupText.setText(R.string.home_more_acountinfo_logoutemail);
		popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
	}

	public void deleteAcount(View view)
	{
		isDeleteAccountBtn = true;
		
		popupText.setText(R.string.home_more_acountinfo_deleteacount);
		popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);

	}
	
	public void setPassword(View view)
	{
		FlurryAgent.logEvent("Set Password");
		isSetPwBtn = true;
		popupText.setText(getString(R.string.home_more_acount_set_pwd1)+rgInfo.getString("nickname", "NO")+getString(R.string.home_more_acount_set_pwd2));
		popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
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
