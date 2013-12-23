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
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class HomeMoreAccountDelete extends Activity {

	EditText inputPw;
	
	// popup view
	PopupWindow popupWindow;
	View popupview;
	RelativeLayout relative;
	TextView popupText;
	
	SharedPreferences rgInfo;
	SharedPreferences studyLevelInfo;
	SharedPreferences settings;
	SharedPreferences checkStageNew;
	SharedPreferences.Editor settingsEditor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_more_account_delete);
		
		studyLevelInfo = getSharedPreferences("StudyLevelInfo",0);
		
		settings = getSharedPreferences("setting", 0);
		settingsEditor = settings.edit();
		
		checkStageNew = getSharedPreferences("CheckStageIsNew", 0);
		
		rgInfo = getSharedPreferences("rgInfo",0);
		inputPw = (EditText)findViewById(R.id.home_more_account_delete_input_pw);

		//popupview
		popupview = View.inflate(this, R.layout.popup_view, null);
		relative = (RelativeLayout)findViewById(R.id.home_more_account_delete_main);
		popupWindow = new PopupWindow(popupview,ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,true);
		popupText = (TextView)popupview.findViewById(R.id.popup_id_text);
	}

	//---- send info -----
	private class DeleteAcount extends AsyncTask<String, Void, JSONObject> {

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

				params.add(new BasicNameValuePair("password", returnSHA512(inputPw.getText().toString())));
				params.add(new BasicNameValuePair("mobile",rgInfo.getString("mobile", "NO") ));
				
				Log.d("params----------",""+inputPw.getText().toString()+""+rgInfo.getString("mobile", "NO")+rgInfo.getString("mem_id", "NO"));
				
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
//					popupText.setText(R.string.home_more_acount_change_pw_success);
//					popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
//					popupWindow.showAsDropDown(null);
					
					settingsEditor.putString("isLogin","NO");
					settingsEditor.putString("loginType", "NO");
					settingsEditor.commit();
					
					Intent intent = new Intent();
					intent.setClass(HomeMoreAccountDelete.this, MainActivity.class);    
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
					startActivity(intent);
					rgInfo.edit().clear().commit(); 
					settings.edit().clear().commit();
					checkStageNew.edit().clear().commit();
					studyLevelInfo.edit().clear().commit();
					
					// Delete DB
					getApplicationContext().deleteDatabase("EngWord.db");
					
					finish();

				} else {
					popupText.setText(R.string.home_more_acount_delete_password);
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


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home_more_account_delete, menu);
		return true;
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

	public void deleteAccount(View view)
	{
		new DeleteAcount().execute("http://todpop.co.kr/api/users/"+rgInfo.getString("mem_id", "NO")+"/delete_user.json");
	}
	@Override
	protected void onStart()
	{
		super.onStart();
		FlurryAgent.onStartSession(this, "ZKWGFP6HKJ33Y69SP5QY");
	}
	 
	@Override
	protected void onStop()
	{
		super.onStop();		
		FlurryAgent.onEndSession(this);
	}
}
