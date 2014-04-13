package com.todpop.saltyenglish;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
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
	Button checkNickname;
	EditText nicknameRefre;
	
	Button doneBtn;
	
	//declare define popup view
	PopupWindow popupWindow;
	View popupview;
	RelativeLayout relative;
	TextView popupText;
	
	SharedPreferences rgInfo;
	SharedPreferences.Editor rgInfoEdit;
	
	int passRecommend;			// if 1 , no check recommend

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rg_register_email);
		
		rgInfo = getSharedPreferences("rgInfo",0);
		rgInfoEdit = rgInfo.edit();
		
		email = (EditText)findViewById(R.id.rgregisteremail_id_email);
		emailPassword = (EditText)findViewById(R.id.rgregisteremail_id_emailpwd);
		emailPasswordCheck = (EditText)findViewById(R.id.rgregisteremail_id_emailpwdcheck);
		nickname = (EditText)findViewById(R.id.rgregisteremail_id_nickname);
		checkNickname = (Button)findViewById(R.id.rgregisteremail_id_checknickname);
		nicknameRefre = (EditText)findViewById(R.id.rgregisteremail_id_nicknamerefre);

		doneBtn = (Button)findViewById(R.id.rgregisteremail_id_donebtn);
		
		passRecommend = 0;
		
		Log.d("nick=",rgInfo.getString("nickname","no"));
		
		if(!rgInfo.getString("facebook","no").equals("no"))						// cross join (facebook -> email)
		{
			nickname.setEnabled(false);			
			nickname.setText(rgInfo.getString("nickname", null));
			nicknameRefre.setEnabled(false);
			
			Log.e("RRE-cross",rgInfo.getString("recommend", getResources().getString(R.string.rg_register_recommend_none)) );
			
			nicknameRefre.setText(rgInfo.getString("recommend", getResources().getString(R.string.rg_register_recommend_none)));
			passRecommend=1;
			checkNickname.setVisibility(View.GONE);
			
			if(rgInfo.getString("password", "0").equals("1"))
			{
				emailPassword.setEnabled(false);
				emailPasswordCheck.setEnabled(false);
				emailPassword.setText(getString(R.string.rg_register_have_password));
				emailPasswordCheck.setText(getString(R.string.rg_register_have_password));
			}
			
		}
		else																		// first join with email
		{
			rgInfoEdit.putString("nickname", "no");
		}
		
		
		//popupview
		relative = (RelativeLayout)findViewById(R.id.rgregisteremail_id_main_activity);
		popupview = View.inflate(this, R.layout.popup_view, null);
		popupWindow = new PopupWindow(popupview,ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT,true);
		popupText = (TextView)popupview.findViewById(R.id.popup_id_text);
	}
		


	// Click Nickname_Duplication_Check ------------------------------------------------------------------------
	
	public void checkDuplicatedNickname(View view)
	{
		Log.i("STEVEN----dupicated nickname", "right now nick is "+nickname.getText());
		Log.e("RRE",nickname.getText().toString());
		String tmpNickname = nickname.getText().toString();
		if(tmpNickname.contains(" "))
		{
			popupText.setText(R.string.popup_nickname_no_blank);
			popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
			popupWindow.showAsDropDown(checkNickname);
		}
		else if(tmpNickname.contains("\\")){
			popupText.setText(R.string.popup_nickname_no_error);
			popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
			popupWindow.showAsDropDown(checkNickname);
		}
		else if(tmpNickname.length()<3||tmpNickname.length()>8){
			Log.e("RRE-string","1212");
			rgInfoEdit.putString("nickname","no");
			popupText.setText(R.string.popup_nickname_length);
			popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
			popupWindow.showAsDropDown(checkNickname);
		}
		else
		{
			new CheckNicknameExistAPI().execute("http://todpop.co.kr/api/users/check_nickname_exist.json?nickname="+nickname.getText().toString());
		}
	}

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
					Log.d("nickname", result.toString());				        	
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
			Log.e("RRE-string",nickname.getText().toString());
			Log.e("RRE-length",String.valueOf(nickname.getText().toString().length()));
			try {
				if(json.getBoolean("status")){
					if(json.getJSONObject("data").getBoolean("result")){
						Log.e("RRE-string","3434");
						rgInfoEdit.putString("nickname",nickname.getText().toString());
						popupText.setText(R.string.popup_nickname_yes);
						popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
						popupWindow.showAsDropDown(checkNickname);
					}else{
						Log.e("RRE-string","5656");
						rgInfoEdit.putString("nickname","no");
						popupText.setText(R.string.popup_nickname_no);
						popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
						popupWindow.showAsDropDown(checkNickname);
					}
					rgInfoEdit.apply();
				}
				else{
					popupText.setText(R.string.popup_nickname_no_error);
					popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
					popupWindow.showAsDropDown(checkNickname);
				}
			} catch (Exception e) {
				popupText.setText(R.string.popup_nickname_no_error);
				popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
				popupWindow.showAsDropDown(checkNickname);
				Log.e("RRE","asdf");
			}
			Log.e("RRE","qweasd");
		}
	}

	// Click Nest_Stage ----------------------------------------------------------------------------------------------------
	
	public void bridgeToEmailInfoActivity(View view)
	{
		if(!nicknameRefre.getText().toString().isEmpty() && passRecommend==0){
			if(nicknameRefre.getText().toString().contains("\\")){
				popupText.setText(R.string.popup_recom_error);
				popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
				popupWindow.showAsDropDown(checkNickname);
			}
			else{
				Log.i("STEVEN----not empty", "right now nick is"+nicknameRefre.getText().toString());
				new CheckRecommendExistAPI().execute("http://todpop.co.kr/api/users/check_recommend_exist.json?recommend="+nicknameRefre.getText().toString());
			}
		}
		else{
			rgInfoEdit.putString("recommend",null);
			rgInfoEdit.apply();
			showRgRegisterEmailInfoActivity();
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
					Log.d("recommender nickname", result.toString());				        	
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
					rgInfoEdit.putString("recommend", nicknameRefre.getText().toString());
					Log.i("STEVEN----", nicknameRefre.getText().toString());
				}
				else{
					rgInfoEdit.putString("recommend",null);
					Log.i("STEVEN-----", "NO");
				}
				rgInfoEdit.apply();
				showRgRegisterEmailInfoActivity();
			} catch (Exception e) {
				rgInfoEdit.putString("recommend",null);
				rgInfoEdit.apply();
				showRgRegisterEmailInfoActivity();

			}
		}
	}

	// -------------------------------------------------------------------------
	
	public void showRgRegisterEmailInfoActivity(){
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
		else if(!rgInfo.getString("nickname","no").equals(nickname.getText().toString()))
		{
			isOk = false;
			popupText.setText(R.string.popup_nickname_needcheck);
			popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
			popupWindow.showAsDropDown(doneBtn);
		}
		else if(!nicknameRefre.getText().toString().isEmpty() && !rgInfo.getString("recommend","no").equals(nicknameRefre.getText().toString()) && passRecommend==0){
				Log.i("STEVEN----compare", "savedInfo = "+rgInfo.getString("recommend", "NO")+"rightnow is = "+nicknameRefre.getText().toString());
				isOk = false;
				popupText.setText(R.string.popup_recom_no);
				popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
				popupWindow.showAsDropDown(checkNickname);
		}
		
		if (isOk == true) {
			new CheckEmailExistAPI().execute("http://todpop.co.kr/api/users/check_email_exist.json?email="+email.getText().toString());
		}
	}
	
	//---email check format---
	public static boolean isEmailValid(String email) {
	    boolean isValid = false;

	    //String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
	    String expression = "^([0-9a-zA-Z._-]+)@([0-9a-zA-Z_-]+)(\\.[0-9a-zA-Z_-]+){1,2}$";
	    CharSequence inputStr = email;

	    Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
	    Matcher matcher = pattern.matcher(inputStr);
	    if (matcher.matches()) {
	        isValid = true;
	    }
	    return isValid;
	}
	
	//---check email
	private class CheckEmailExistAPI extends AsyncTask<String, Void, JSONObject> {
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
					Log.d("email", result.toString());				        	
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
					rgInfoEdit.putString("email", email.getText().toString());
					
					Intent intent = new Intent(getApplicationContext(), RgRegisterEmailInfo.class);
					intent.putExtra("email", email.getText().toString());
					intent.putExtra("password", returnSHA512(emailPassword.getText().toString()));
					intent.putExtra("nickname", nickname.getText().toString());
					intent.putExtra("recommender", nicknameRefre.getText().toString());
					startActivity(intent);
				}else{
					rgInfoEdit.putString("email",null);
					
					popupText.setText(R.string.popup_send_info_error);
					popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
					popupWindow.showAsDropDown(doneBtn);
				}
				rgInfoEdit.apply();
			} catch (Exception e) {

			}
		}
	}
	
	// ---------------------------------------------------------------------------------------------------------------------
	
	
	//----button onClick----
	public void closePopup(View v)
	{
		popupWindow.dismiss();
	}
	
	public void onClickBack(View view)
	{
		finish();
	}
		
		
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.rg_register_email, menu);
		return true;
	}

	public String returnSHA512(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(password.getBytes());
        byte byteData[] = md.digest();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
 
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
            String hex = Integer.toHexString(0xff & byteData[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
      //  System.out.println("SHA512: " + hexString.toString());
        return hexString.toString();
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
