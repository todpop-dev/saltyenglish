package com.todpop.saltyenglish;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class HomeMoreAccountChangePw extends Activity {

	String mobile = "";
	EditText currentPw;
	EditText newPw;
	EditText newPwCheck;

	// popup view
	PopupWindow popupWindow;
	View popupview;
	RelativeLayout relative;
	TextView popupText;

	SharedPreferences rgInfo;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_more_acount_change_pw);

		rgInfo = getSharedPreferences("rgInfo",0);
		//popupview
		relative = (RelativeLayout)findViewById(R.id.home_more_account_changepw_main);
		popupview = View.inflate(this, R.layout.popup_view, null);
		popupWindow = new PopupWindow(popupview,ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT,true);
		popupText = (TextView)popupview.findViewById(R.id.popup_id_text);

		currentPw = (EditText)findViewById(R.id.home_more_account_change_pw_current);
		newPw = (EditText)findViewById(R.id.home_more_account_change_pw_new);
		newPwCheck = (EditText)findViewById(R.id.home_more_account_change_pw_newcheck);


	}

	//---- send info -----
	private class ChangePw extends AsyncTask<String, Void, JSONObject> {

		JSONObject result = null;
		@Override
		protected JSONObject doInBackground(String... urls) 
		{
			try
			{
				HttpClient client = new DefaultHttpClient();  
				String postURL = urls[0];
				HttpPost post = new HttpPost(postURL); 
				List<NameValuePair> params = new ArrayList<NameValuePair>();

				params.add(new BasicNameValuePair("nickname", rgInfo.getString("nickname", "NO")));
				params.add(new BasicNameValuePair("mobile",rgInfo.getString("mobile", "NO") ));
				params.add(new BasicNameValuePair("current_password", returnSHA512(currentPw.getText().toString())));
				params.add(new BasicNameValuePair("new_password", returnSHA512(newPw.getText().toString())));					

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
		protected void onPostExecute(JSONObject result) {
			try {
				if (result.getBoolean("status")==true) {
					popupText.setText(R.string.home_more_acount_change_pw_success);
					popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
					popupWindow.showAsDropDown(null);

				} else if(result.getString("msg").equals("incorrect current password"))
				{
					popupText.setText(R.string.home_more_acount_change_pw_incorrect_password);
					popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
					popupWindow.showAsDropDown(null);
				}

			} catch (Exception e) {

			}

		}

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
	// on click

	public void closePopup(View v)
	{
		popupWindow.dismiss();
	}


	public void onClickBack(View view)
	{
		finish();
	}

	public void changePw(View v)
	{
		if(currentPw.getText().toString().length()<5)
		{
			popupText.setText(R.string.home_more_acount_change_pw_current);
			popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
			popupWindow.showAsDropDown(null);
		}else if(newPw.getText().toString().length()<5)
		{
			popupText.setText(R.string.home_more_acount_change_pw_length);
			popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
			popupWindow.showAsDropDown(null);
		}else if(!newPw.getText().toString().equals(newPwCheck.getText().toString()))
		{
			popupText.setText(R.string.home_more_acount_change_pw_check);
			popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
			popupWindow.showAsDropDown(null);	
		}else{
			new ChangePw().execute("http://todpop.co.kr/api/users/"+rgInfo.getString("mem_id", "NO")+"/change_password.json");
		}
		
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
