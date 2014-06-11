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
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;
import com.todpop.api.TypefaceActivity;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FbNickname extends TypefaceActivity {

	TelephonyManager phoneMgr;

	String mobile;
	
	String operator;
	String country;
	
	EditText nickName;
	Button checkNicknameBtn;
	Button nextBtn;
	
	EditText nicknamerefre;

	//declare define popup view
	PopupWindow popupWindow;
	View popupview;
	RelativeLayout relative;
	TextView popupText;

	boolean isRegisterFailed;
	
	SharedPreferences rgInfo;
	SharedPreferences.Editor rgInfoEdit;
	SharedPreferences setting;
	SharedPreferences.Editor settingEdit;
	SharedPreferences studyInfo;
	SharedPreferences.Editor studyInfoEdit;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fb_nickname);
		rgInfo = getSharedPreferences("rgInfo",0);
		rgInfoEdit = rgInfo.edit();;
		setting = getSharedPreferences("setting", 0);
		settingEdit = setting.edit();
		studyInfo = getSharedPreferences("studyInfo", 0);
		studyInfoEdit = studyInfo.edit();
		
		Intent intent = getIntent();
		mobile = rgInfo.getString("mobile", null);
		if(mobile == null || mobile.equals("010test0000")){
			mobile = intent.getStringExtra("mobile");
		}
		
		phoneMgr = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE); 

		try{
			operator = phoneMgr.getNetworkOperatorName();
			country = phoneMgr.getNetworkCountryIso();
		} catch(Exception e){
			
		}
		
		nextBtn = (Button) findViewById(R.id.button1);
		nickName = (EditText)findViewById(R.id.fb_nickname_id_nickname);
		checkNicknameBtn = (Button)findViewById(R.id.fb_nickname_id_check_btn);
		nicknamerefre = (EditText)findViewById(R.id.fb_nickname_id_nicknamerefre);
		
		//popupview
		relative = (RelativeLayout)findViewById(R.id.fb_nickname_id_main_activity);
		popupview = View.inflate(this, R.layout.popup_view, null);
		popupWindow = new PopupWindow(popupview, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
		popupText = (TextView)popupview.findViewById(R.id.popup_id_text);
		setFont(popupText);
		
		if(!rgInfo.getString("email","no").equals("no"))
		{
			new SignUpAPI().execute("http://todpop.co.kr/api/users/sign_up.json");
		}
		else
		{
			rgInfoEdit.putString("nickname", "no");
			rgInfoEdit.apply();
			
			isRegisterFailed = false;
		}

	}
	
	
	// Sign Up -------------------------------------------------------------------------------
	
	private class SignUpAPI extends AsyncTask<String, Void, JSONObject> 
	{
		@Override
		protected JSONObject doInBackground(String... urls) 
		{
			JSONObject result = null;
			try
			{
				HttpClient client = new DefaultHttpClient();  
				String postURL = urls[0];
				//Log.d("URL ---- ", postURL);
				HttpPost post = new HttpPost(postURL); 
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				//junho determine unique device
				String android_id = Secure.getString(getApplicationContext().getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
				params.add(new BasicNameValuePair("device_id",android_id));
				//junho end
				if(!rgInfo.getString("email","no").equals("no"))										// cross join ( email -> facebook)
				{
					params.add(new BasicNameValuePair("facebook", rgInfo.getString("facebook", null)));
					params.add(new BasicNameValuePair("mem_no", rgInfo.getString("mem_id", null)));
				}else{																					// first join with facebook
					params.add(new BasicNameValuePair("facebook", rgInfo.getString("facebook", null)));
					params.add(new BasicNameValuePair("nickname", rgInfo.getString("nickname", null)));
					params.add(new BasicNameValuePair("mobile", mobile));
					
					if(!rgInfo.getString("recommend", "no").equals("no"))
					{
						params.add(new BasicNameValuePair("recommend", rgInfo.getString("recommend", null)));
					}
				}
				
				if(rgInfo.getString("fbBDay", null) != null){
					params.add(new BasicNameValuePair("birth", rgInfo.getString("fbBDay", null)));
				}
				if(rgInfo.getString("fbGender", null) != null){
					params.add(new BasicNameValuePair("sex", rgInfo.getString("fbGender", null)));
				}
				if(rgInfo.getString("fbLocation", null) != null){
					params.add(new BasicNameValuePair("address", rgInfo.getString("fbLocation", null)));
				}

				params.add(new BasicNameValuePair("device", Build.DEVICE));
				params.add(new BasicNameValuePair("android_version", Build.VERSION.RELEASE));
				params.add(new BasicNameValuePair("operator", operator));
				params.add(new BasicNameValuePair("operator_region", country));
				
				UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params,HTTP.UTF_8);
				post.setEntity(ent);
				HttpResponse responsePOST = client.execute(post);  
				HttpEntity resEntity = responsePOST.getEntity();

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
					// Login OK
					settingEdit.putString("isLogin", "YES");
					settingEdit.putString("loginType", "fb");
					settingEdit.apply();
					
					rgInfoEdit.putString("facebook",json.getJSONObject("data").getString("facebook"));
					rgInfoEdit.putString("mem_id", json.getJSONObject("data").getString("mem_id"));
					rgInfoEdit.apply();
					
					if(json.getJSONObject("data").getInt("level_test")==0)
					{
						Intent intent = new Intent(getApplicationContext(), RgRegisterTutorial.class);
						startActivity(intent);
					}
					else
					{
						new GetStageInfoAPI().execute("http://todpop.co.kr/api/studies/get_stage_info.json?user_id=" + rgInfo.getString("mem_id",null));
					}
					
					finish();
					
				}else{		        
					isRegisterFailed = true;
					popupText.setText(R.string.rg_register_failed);
					popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
				}

			} catch (Exception e) {

			}
		}
	}
	
	// -------------- get stage info ---------------------------------------------------------------

	private class GetStageInfoAPI extends AsyncTask<String, Void, JSONObject> {
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
					
					String stage_info = json.getJSONObject("data").getString("stage");
					studyInfoEdit.putString("stageInfo",stage_info);
					studyInfoEdit.apply();
					
    				Intent intent = new Intent(getApplicationContext(), StudyHome.class);
    				startActivity(intent);
				}
				else
				{
				}
				
			} catch (Exception e) {

			}
		}
	}

	
	// Click nickname_duplication_check -----------------------------------------------------------------------------
	
	public void checkNickName(View v)
	{
		try {
			String tmpNickname = nickName.getText().toString();
			if(tmpNickname.contains(" "))
			{
				popupText.setText(R.string.popup_nickname_no_blank);
				popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
			}
			else if(tmpNickname.contains("\\")){
				popupText.setText(R.string.popup_nickname_no_error);
				popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
			}
			else if(tmpNickname.length()<3||tmpNickname.length()>8){
				popupText.setText(R.string.popup_nickname_length);
				popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
			}
			else
			{
				new CheckNicknameExistAPI().execute("http://todpop.co.kr/api/users/check_nickname_exist.json?nickname="+tmpNickname);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	//---check nickname
	private class CheckNicknameExistAPI extends AsyncTask<String, Void, JSONObject> {
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
					//Log.d("nickname", result.getString("result"));				        	
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
				if(json.getBoolean("status")){
					
					if(json.getJSONObject("data").getBoolean("result")){
						rgInfoEdit.putString("nickname",nickName.getText().toString());
						popupText.setText(R.string.popup_nickname_yes);
						popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
					}else{
						rgInfoEdit.putString("nickname","no");
						popupText.setText(R.string.popup_nickname_no);
						popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
					}
					rgInfoEdit.apply();
				}	
				else{
					popupText.setText(R.string.popup_nickname_no_error);
					popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
				}
			} catch (Exception e) {

			}
		}
	}

	
	// Click next stage ------------------------------------------------------------------------------------
	
	public void bridgeToShowLvTest(View v){

		nextBtn.setClickable(false);
		if(!nicknamerefre.getText().toString().isEmpty()){
			new CheckRecommendExistAPI().execute("http://todpop.co.kr/api/users/check_recommend_exist.json?recommend="+nicknamerefre.getText().toString());
		}
		else{
			rgInfoEdit.putString("recommend",null);
			rgInfoEdit.apply();
			showLvtest();
		}
	}

	//---check recommender nickname
	private class CheckRecommendExistAPI extends AsyncTask<String, Void, JSONObject> {
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
				if(json.getJSONObject("data").getBoolean("result")){
					rgInfoEdit.putString("recommend", nicknamerefre.getText().toString());
				}
				else{
					rgInfoEdit.putString("recommend",null);
				}
				rgInfoEdit.apply();
				showLvtest();
			} catch (Exception e) {

			}
		}
	}

	public void showLvtest()
	{
		boolean isOk = true;
		if(!rgInfo.getString("nickname","no").equals(nickName.getText().toString()))
		{
			isOk = false;
			popupText.setText(R.string.popup_nickname_needcheck);
			popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
		}else if(!nicknamerefre.getText().toString().isEmpty() && !rgInfo.getString("recommend","no").equals(nicknamerefre.getText().toString())){
			isOk = false;
			popupText.setText(R.string.popup_recom_no);
			popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
		}
		if(isOk == true){
			new SignUpAPI().execute("http://todpop.co.kr/api/users/sign_up.json");
		}
	}

	
	// -------------------------------------------------------------------------------------
	
		
	@Override
	public void onResume()
	{
		super.onResume();

		com.facebook.AppEventsLogger.activateApp(this, "218233231697811");
		/*
		// Facebook Logout Forcely
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
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.fb_nickname, menu);
		return true;
	}


	//----button onClick----
	public void closePopup(View v)
	{
		popupWindow.dismiss();
//		if (isRegisterFailed) {
//			Intent intent = new Intent(getApplicationContext(), RgRegister.class);
//			startActivity(intent);
//			finish();
//		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) 
		{
			Intent intent = new Intent(getApplicationContext(), RgRegister.class);
			startActivity(intent);

			finish();
		}
		return false;
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

