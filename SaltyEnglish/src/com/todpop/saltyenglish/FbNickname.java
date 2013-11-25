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
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FbNickname extends Activity {

	EditText nickName;

	//declare define popup view
	PopupWindow popupWindow;
	View popupview;
	RelativeLayout relative;
	TextView popupText;
		
	EditText nicknamerefre;
	Button checkNicknameBtn;
	
	String fbEmail = "";
	boolean isRegisterFailed;
	
	SharedPreferences rgInfo;
	SharedPreferences.Editor rgInfoEdit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fb_nickname);
		
		rgInfo = getSharedPreferences("rgInfo",0);
		rgInfoEdit = rgInfo.edit();
		
		Bundle args = getIntent().getExtras();
		
		if (args != null) {
			fbEmail = args.getString("fbEmail");
			Log.d("fbNickname -- fb email -----", fbEmail);
		}

		nickName = (EditText)findViewById(R.id.fb_nickname_id_nickname);
		nicknamerefre = (EditText)findViewById(R.id.fb_nickname_id_nicknamerefre);
		checkNicknameBtn = (Button)findViewById(R.id.fb_nickname_id_check_btn);
		if(!rgInfo.getString("nickname", "NO").equals("NO"))
		{
			nickName.setEnabled(false);			
			nickName.setText(rgInfo.getString("nickname", "NO"));
			nicknamerefre.setEnabled(false);			
			nicknamerefre.setText(rgInfo.getString("recommend", "NO"));
			checkNicknameBtn.setVisibility(View.GONE);
		}

		//popupview
		relative = (RelativeLayout)findViewById(R.id.fb_nickname_id_main_activity);
		popupview = View.inflate(this, R.layout.popup_view, null);
		float density = getResources().getDisplayMetrics().density;
		popupWindow = new PopupWindow(popupview,(int)(300*density),(int)(100*density),true);
		popupText = (TextView)popupview.findViewById(R.id.popup_id_text);
		
		
		isRegisterFailed = false;

	}
	
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

	private class SendInfo extends AsyncTask<String, Void, JSONObject> 
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
				
				if(!rgInfo.getString("email", "NO").equals("NO"))
				{
					params.add(new BasicNameValuePair("facebook", fbEmail));
					params.add(new BasicNameValuePair("mem_no", rgInfo.getString("mem_id", "NO")));
				}else{
					params.add(new BasicNameValuePair("facebook", fbEmail));
					params.add(new BasicNameValuePair("nickname", rgInfo.getString("nickname", "NO")));
					params.add(new BasicNameValuePair("mobile", rgInfo.getString("mobile", "NO")));
					params.add(new BasicNameValuePair("recommend", rgInfo.getString("recommend", "NO")));
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
					SharedPreferences settings = getSharedPreferences("setting", 0);
					SharedPreferences.Editor edit = settings.edit();
					edit.putString("isLogin", "YES");
					edit.putString("loginType", "fb");
					rgInfoEdit.putString("facebookEmail",json.getJSONObject("data").getString("facebook"));
					rgInfoEdit.putString("mem_id", json.getJSONObject("data").getString("mem_id"));
					rgInfoEdit.commit();
					edit.commit();
					
					
					if(json.getJSONObject("data").getInt("level_test")==0)
					{
						Intent intent = new Intent(getApplicationContext(), RgRegisterFinish.class);
						startActivity(intent);
					}else{
						Intent intent = new Intent(getApplicationContext(), StudyHome.class);
						startActivity(intent);
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.fb_nickname, menu);
		return true;
	}

	private class CheckNickname extends AsyncTask<String, Void, JSONObject> {
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
					Log.d("nickname", result.getString("result"));				        	
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
				if(json.getBoolean("status")==true&&nickName.getText().toString().length()>3)
				{
					rgInfoEdit.putString("nickname",nickName.getText().toString());
					rgInfoEdit.commit();

					popupText.setText(R.string.popup_nickname_yes);
					popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
				}else{
					popupText.setText(R.string.popup_nickname_no);
					popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
					//popupWindow.showAsDropDown(checkNickname);

				}
			} catch (Exception e) {

			}
		}
	}

	//----button onClick----
	public void closePopup(View v)
	{
		popupWindow.dismiss();
		if (isRegisterFailed) {
			Intent intent = new Intent(getApplicationContext(), RgRegister.class);
			startActivity(intent);
			finish();
		}
	}

	public void checkNickName(View v)
	{
		new CheckNickname().execute("http://todpop.co.kr/api/users/check_nickname_exist.json?nickname="+nickName.getText().toString());
	}

	public void showLvtest(View v)
	{

		
		if(!rgInfo.getString("nickname", "NO").equals(nickName.getText().toString()))
		{
			popupText.setText(R.string.popup_nickname_needcheck);
			popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
		}
		else if(nickName.getText().toString().length()<3){
			popupText.setText(R.string.popup_nickname_null);
			popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
		}else 
		{
			new SendInfo().execute("http://todpop.co.kr/api/users/sign_up.json");
		}
	}

}
