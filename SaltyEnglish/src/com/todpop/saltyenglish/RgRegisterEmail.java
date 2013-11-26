package com.todpop.saltyenglish;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class RgRegisterEmail extends Activity {
	//declare define UI EditText 
	EditText email;
	EditText emailPassword;
	EditText emailPasswordCheck;
	EditText nickname;
	EditText nicknameRefre;
	Button checkNickname;
	Button doneBtn;
	//declare define popup view
	PopupWindow popupWindow;
	View popupview;
	RelativeLayout relative;
	TextView popupText;
	//nickname value
	SharedPreferences rgInfo;
	SharedPreferences.Editor edit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rg_register_email);
		
		rgInfo = getSharedPreferences("rgInfo",0);
		edit = rgInfo.edit();
		email = (EditText)findViewById(R.id.rgregisteremail_id_email);
		emailPassword = (EditText)findViewById(R.id.rgregisteremail_id_emailpwd);
		emailPasswordCheck = (EditText)findViewById(R.id.rgregisteremail_id_emailpwdcheck);
		nickname = (EditText)findViewById(R.id.rgregisteremail_id_nickname);
		nicknameRefre = (EditText)findViewById(R.id.rgregisteremail_id_nicknamerefre);
		checkNickname = (Button)findViewById(R.id.rgregisteremail_id_checknickname);
		doneBtn = (Button)findViewById(R.id.rgregisteremail_id_donebtn);
		
		if(!rgInfo.getString("nickname", "NO").equals("NO"))
		{
			nickname.setEnabled(false);			
			nickname.setText(rgInfo.getString("nickname", "NO"));
			nicknameRefre.setEnabled(false);			
			nicknameRefre.setText(rgInfo.getString("recommend", "NO"));
			checkNickname.setVisibility(View.GONE);
			
			if(rgInfo.getString("password", "NO").equals("1"))
			{
				emailPassword.setEnabled(false);
				emailPasswordCheck.setEnabled(false);
				emailPassword.setText("password is ok");
				emailPasswordCheck.setText("password is ok");
			}
			
		}
		//popupview
		relative = (RelativeLayout)findViewById(R.id.rgregisteremail_id_main_activity);
		popupview = View.inflate(this, R.layout.popup_view, null);
		float density = getResources().getDisplayMetrics().density;
		popupWindow = new PopupWindow(popupview,(int)(300*density),(int)(100*density),true);
		popupText = (TextView)popupview.findViewById(R.id.popup_id_text);
	}
	
	//---email check format---
	
	public static boolean isEmailValid(String email) {
	    boolean isValid = false;

	    //String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
	    String expression = "^([0-9a-zA-Z_-]+)@([0-9a-zA-Z_-]+)(\\.[0-9a-zA-Z_-]+){1,2}$";
	    CharSequence inputStr = email;

	    Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
	    Matcher matcher = pattern.matcher(inputStr);
	    if (matcher.matches()) {
	        isValid = true;
	    }
	    return isValid;
	}
	
	//---check nickname
	
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
				if(json.getBoolean("status")==true&&nickname.getText().toString().length()>3)
				{
					edit.putString("nickname",nickname.getText().toString());
					popupText.setText(R.string.popup_nickname_yes);
					popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
					popupWindow.showAsDropDown(checkNickname);
				}else{
					edit.putString("nickname","NO");
					popupText.setText(R.string.popup_nickname_no);
					popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
					popupWindow.showAsDropDown(checkNickname);
				}
				edit.commit();
			} catch (Exception e) {

			}
		}
	}
	
	//---check email
	
		private class CheckEmail extends AsyncTask<String, Void, JSONObject> {
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
						Log.d("email", result.getString("result"));				        	
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
					if(json.getJSONObject("data").getBoolean("result"))
					{
						edit.putString("email", email.getText().toString());
						edit.putString("Password", emailPassword.getText().toString());
						
						Intent intent = new Intent(getApplicationContext(), RgRegisterEmailInfo.class);
						startActivity(intent);
					}else{
						edit.putString("email","NO");
						edit.putString("Password", "NO");
						
						popupText.setText(R.string.popup_send_info_error);
						popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
						popupWindow.showAsDropDown(doneBtn);

					}
					edit.commit();
				} catch (Exception e) {

				}
			}
		}
	
	//----button onClick----
	public void closePopup(View v)
	{
		popupWindow.dismiss();
	}
	
	public void onClickBack(View view)
	{
		finish();
	}
	
	public void checkDuplicatedNickname(View view)
	{
		new CheckNickname().execute("http://todpop.co.kr/api/users/check_nickname_exist.json?nickname="+nickname.getText().toString());
	}
		
	public void showRgRegisterEmailInfoActivity(View view)
	{
		boolean isOk = true;

		if(!isEmailValid(email.getText().toString()))
		{
			isOk = false;
			popupText.setText(R.string.popup_email_format_wrong);
			popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
			popupWindow.showAsDropDown(doneBtn);
		}
		else if(emailPassword.getText().length()<6)
		{
			isOk = false;

			popupText.setText(R.string.popup_pwd_length_wrong);
			popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
			popupWindow.showAsDropDown(doneBtn);
			
		}
		else if(!emailPassword.getText().toString().equals(emailPasswordCheck.getText().toString()))
		{
			isOk = false;
			popupText.setText(R.string.popup_pwd_check_wrong);
			popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
			popupWindow.showAsDropDown(doneBtn);
		}
		else if(nickname.getText().toString().length()<4)
		{
			isOk = false;

			popupText.setText(R.string.popup_nickname_null);
			popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
			popupWindow.showAsDropDown(doneBtn);
		} 
		else if(!rgInfo.getString("nickname", "NO").equals(nickname.getText().toString()))
		{
			isOk = false;
			popupText.setText(R.string.popup_nickname_needcheck);
			popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
			popupWindow.showAsDropDown(doneBtn);
		}
		
		
		if (isOk == true) {
			edit.putString("recommend", nicknameRefre.getText().toString());
			edit.commit();
			new CheckEmail().execute("http://todpop.co.kr/api/users/check_email_exist.json?email="+email.getText().toString());
		}
	}
	
		
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.rg_register_email, menu);
		return true;
	}

}
