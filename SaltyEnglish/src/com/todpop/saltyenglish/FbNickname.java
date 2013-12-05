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

import com.facebook.Session;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FbNickname extends Activity {

	EditText nickName;
	Button checkNicknameBtn;
	
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
		
		nickName = (EditText)findViewById(R.id.fb_nickname_id_nickname);
		checkNicknameBtn = (Button)findViewById(R.id.fb_nickname_id_check_btn);
		nicknamerefre = (EditText)findViewById(R.id.fb_nickname_id_nicknamerefre);
		
		//popupview
		relative = (RelativeLayout)findViewById(R.id.fb_nickname_id_main_activity);
		popupview = View.inflate(this, R.layout.popup_view, null);
		float density = getResources().getDisplayMetrics().density;
		popupWindow = new PopupWindow(popupview,(int)(300*density),(int)(100*density),true);
		popupText = (TextView)popupview.findViewById(R.id.popup_id_text);
		
		if(!rgInfo.getString("email","no").equals("no"))
		{
			new SignUpAPI().execute("http://todpop.co.kr/api/users/sign_up.json");
		}
		else
		{
			rgInfoEdit.putString("nickname", "no");
			rgInfoEdit.commit();
			
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
				Log.d("URL ---- ", postURL);
				HttpPost post = new HttpPost(postURL); 
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				
				if(!rgInfo.getString("email","no").equals("no"))										// cross join ( email -> facebook)
				{
					params.add(new BasicNameValuePair("facebook", rgInfo.getString("facebook", null)));
					params.add(new BasicNameValuePair("mem_no", rgInfo.getString("mem_id", null)));
				}else{																					// first join with facebook
					params.add(new BasicNameValuePair("facebook", rgInfo.getString("facebook", null)));
					params.add(new BasicNameValuePair("nickname", rgInfo.getString("nickname", null)));
					params.add(new BasicNameValuePair("mobile", rgInfo.getString("mobile", "010test0000")));
					
					if(!rgInfo.getString("recommend", "no").equals("no"))
					{
						params.add(new BasicNameValuePair("recommend", rgInfo.getString("recommend", null)));
					}
					
				}

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
					settingEdit.commit();
					
					rgInfoEdit.putString("facebook",json.getJSONObject("data").getString("facebook"));
					rgInfoEdit.putString("mem_id", json.getJSONObject("data").getString("mem_id"));
					rgInfoEdit.commit();
					
					if(json.getJSONObject("data").getInt("level_test")==0)
					{
						Intent intent = new Intent(getApplicationContext(), RgRegisterFinish.class);
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
			Log.d("F N","192");
			
			JSONObject result = null;
			try
			{
				DefaultHttpClient httpClient = new DefaultHttpClient();
				String getURL = urls[0];
				HttpGet httpGet = new HttpGet(getURL);
				HttpResponse httpResponse = httpClient.execute(httpGet);
				HttpEntity resEntity = httpResponse.getEntity();

				Log.d("F N","203");
				
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
			
			Log.d("F N","216");
			
			return result;
		}

		@Override
		protected void onPostExecute(JSONObject json) {
			try {
				
				Log.d("F N","225");
				
				if(json.getBoolean("status")) {
					
					String stage_info = json.getJSONObject("data").getString("stage");
					studyInfoEdit.putString("stageInfo",stage_info);
					studyInfoEdit.commit();
					
    				Intent intent = new Intent(getApplicationContext(), StudyHome.class);
    				startActivity(intent);
				}
				else
				{
					Log.d("F N","238");
				}
				
			} catch (Exception e) {

			}
		}
	}

	
	// Click nickname_duplication_check -----------------------------------------------------------------------------
	
	public void checkNickName(View v)
	{
		Log.d("FbNickname","192");
		//Log.d("chk=",nickName.getText().toString());
		try {
			new CheckNicknameExistAPI().execute("http://todpop.co.kr/api/users/check_nickname_exist.json?nickname="+nickName.getText().toString());
		}
		catch (Exception e)
		{
			Log.d("FbNickname","197");
			
		}
	}

	//---check nickname
	private class CheckNicknameExistAPI extends AsyncTask<String, Void, JSONObject> {
		@Override
		protected JSONObject doInBackground(String... urls) 
		{
			Log.d("FbNickname","210");
			
			JSONObject result = null;
			try
			{
				DefaultHttpClient httpClient = new DefaultHttpClient();
				String getURL = urls[0];
				HttpGet httpGet = new HttpGet(getURL);
				HttpResponse httpResponse = httpClient.execute(httpGet);
				HttpEntity resEntity = httpResponse.getEntity();

				Log.d("FbNickname","221");
				
				if (resEntity != null)
				{    
					result = new JSONObject(EntityUtils.toString(resEntity)); 
					Log.d("nickname", result.getString("result"));				        	
					return result;

				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			
			Log.d("FbNickname","236");
			
			return result;
		}

		@Override
		protected void onPostExecute(JSONObject json) {
			try {
				
				Log.d("FbNickname","243");
				
				if(nickName.getText().toString().length() < 3 || nickName.getText().toString().length() > 8){
					rgInfoEdit.putString("nickname","no");
					popupText.setText(R.string.popup_nickname_length);
					popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
				}else if(json.getJSONObject("data").getBoolean("result")){
					rgInfoEdit.putString("nickname",nickName.getText().toString());
					popupText.setText(R.string.popup_nickname_yes);
					popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
				}else{
					rgInfoEdit.putString("nickname","no");
					popupText.setText(R.string.popup_nickname_no);
					popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
				}
				rgInfoEdit.commit();
			} catch (Exception e) {

			}
		}
	}

	
	// Click next stage ------------------------------------------------------------------------------------
	
	public void bridgeToShowLvTest(View v){
		if(!nicknamerefre.getText().toString().isEmpty()){
			Log.i("STEVEN----not empty", "right now nick is"+nicknamerefre.getText().toString());
			new CheckRecommendExistAPI().execute("http://todpop.co.kr/api/users/check_recommend_exist.json?recommend="+nicknamerefre.getText().toString());
		}
		else{
			rgInfoEdit.putString("recommend",null);
			rgInfoEdit.commit();
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
					Log.d("recommender nickname", result.getString("result"));				        	
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
					Log.i("STEVEN----", nicknamerefre.getText().toString());
				}
				else{
					rgInfoEdit.putString("recommend",null);
					Log.i("STEVEN-----", "NO");
				}
				rgInfoEdit.commit();
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
			Log.i("STEVEN----compare", "savedInfo = "+rgInfo.getString("recommend", null)+"rightnow is = "+nicknamerefre.getText().toString());
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
		}
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
	

}

