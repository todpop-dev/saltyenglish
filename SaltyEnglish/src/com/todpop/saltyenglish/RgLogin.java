package com.todpop.saltyenglish;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
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

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
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

public class RgLogin extends Activity {

	// email
	EditText email;
	EditText emailPassword;
	Button loginBtn;

	//declare define popup view
	PopupWindow popupWindow;
	View popupview;
	RelativeLayout relative;
	TextView popupText;
	
	// facebook 
    private UiLifecycleHelper uiHelper;
    private String userInfo = null;
    String fbId = null;    
    String fbEmail = null;
    
	SharedPreferences rgInfo;
    SharedPreferences.Editor rgInfoEdit;
	SharedPreferences setting;
	SharedPreferences.Editor settingEdit;
	SharedPreferences studyInfo;
	SharedPreferences.Editor studyInfoEdit;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

	    rgInfo = getSharedPreferences("rgInfo",0);
	    rgInfoEdit = rgInfo.edit();
		setting = getSharedPreferences("setting", 0);
		settingEdit = setting.edit();
		studyInfo = getSharedPreferences("studyInfo", 0);
		studyInfoEdit = studyInfo.edit();
		
		Log.d("RgLogin","1");
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rg_login);
		
		Log.d("RgLogin","2");
		
		email = (EditText)findViewById(R.id.rglogin_id_email);
		emailPassword = (EditText)findViewById(R.id.rglogin_id_emailpassword);
		loginBtn = (Button)findViewById(R.id.rglogin_id_loginbtn);
		
		//popupview
		relative = (RelativeLayout)findViewById(R.id.rglogin_id_main_activity);;
		popupview = View.inflate(this, R.layout.popup_view, null);
		float density = getResources().getDisplayMetrics().density;
		popupWindow = new PopupWindow(popupview,(int)(300*density),(int)(100*density),true);
		popupText = (TextView)popupview.findViewById(R.id.popup_id_text);
		
		// facebook login btn
		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);
		
		final LoginButton fb_btn = (LoginButton)findViewById(R.id.rg_facebook_login_btn);
		fb_btn.setReadPermissions(Arrays.asList("user_location", "user_birthday", "user_likes", "email"));
		fb_btn.setBackgroundResource(R.drawable.rglogin_drawable_btn_facebook);
	}
	
	
	public void emailLogin(View view)				// email login button click
	{
		new SignInWithEmailAPI().execute("http://todpop.co.kr/api/users/sign_in.json");
		
	}
	
	//--- request class ---
	private class SignInWithEmailAPI extends AsyncTask<String, Void, JSONObject> 
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

        		params.add(new BasicNameValuePair("email",email.getText().toString()));
        		params.add(new BasicNameValuePair("password", returnSHA512(emailPassword.getText().toString())));

        		UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params,HTTP.UTF_8);
        		post.setEntity(ent);
        		HttpResponse responsePOST = client.execute(post);  
        		HttpEntity resEntity = responsePOST.getEntity();

        		if (resEntity != null)
        		{    
        			json = new JSONObject(EntityUtils.toString(resEntity)); 
        			Log.d("login", json.toString());				        	
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
        			
        			settingEdit.putString("isLogin","YES");
        			settingEdit.putString("loginType", "email");
        			settingEdit.commit();

        			// email, facebook skip (not needed)
        			rgInfoEdit.putString("mem_id", result.getJSONObject("data").getJSONObject("user").getString("id"));
        			rgInfoEdit.putString("nickname", result.getJSONObject("data").getJSONObject("user").getString("nickname"));
        			rgInfoEdit.putString("mobile", result.getJSONObject("data").getJSONObject("user").getString("mobile"));
        			rgInfoEdit.putString("level", result.getJSONObject("data").getJSONObject("user").getString("level_test"));
        			rgInfoEdit.putString("password", result.getJSONObject("data").getJSONObject("user").getString("is_set_facebook_password"));
        			rgInfoEdit.commit();
    			
        			new GetStageInfoAPI().execute("http://todpop.co.kr/api/studies/get_stage_info.json?user_id=" + rgInfo.getString("mem_id",null));        			

        			finish();
        		}
        		else
        		{
        			popupText.setText(R.string.popup_emailogin_error);
        			popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
        			popupWindow.showAsDropDown(loginBtn);
        		}
        	}catch (Exception e) {
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

	// -------------- get stage info ---------------------------------------------------------------

	private class GetStageInfoAPI extends AsyncTask<String, Void, JSONObject> {
		@Override
		protected JSONObject doInBackground(String... urls) 
		{
			Log.d("R L","222");
			
			JSONObject result = null;
			try
			{
				DefaultHttpClient httpClient = new DefaultHttpClient();
				String getURL = urls[0];
				HttpGet httpGet = new HttpGet(getURL);
				HttpResponse httpResponse = httpClient.execute(httpGet);
				HttpEntity resEntity = httpResponse.getEntity();

				Log.d("R L","233");
				
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
			
			Log.d("R L","246");
			
			return result;
		}

		@Override
		protected void onPostExecute(JSONObject json) {
			try {
				
				Log.d("R L","255");
				
				if(json.getBoolean("status")) {
					
					String stage_info = json.getJSONObject("data").getString("stage");
					studyInfoEdit.putString("stageInfo",stage_info);
					studyInfoEdit.commit();
					
	    				Intent intent = new Intent(getApplicationContext(), StudyHome.class);
	    				startActivity(intent);
				}
				else
				{
					Log.d("R L","268");
				}
				
			} catch (Exception e) {

			}
		}
	}

	// ----------------------------------------------------------------------------------------------------------------
		
	//----button onClick----
	
	public void onClickBack(View view)
	{
		
		finish();
	}
	
	
	public void closePopup(View v)
	{
		popupWindow.dismiss();
	}
	


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.rg_login, menu);
		return true;
	}

	
	// Facebook delegates
					
    // Get User Info
	private String buildUserInfoDisplay(GraphUser user) {
	    StringBuilder userInfo = new StringBuilder("");

	    userInfo.append(String.format("ID: %s\n\n", 
	        user.getId()));
	    fbId = user.getId();
	    
	    // Example: typed access (name)
	    // - no special permissions required
	    userInfo.append(String.format("Name: %s\n\n", 
	        user.getName()));

	    // Example: typed access (birthday)
	    // - requires user_birthday permission
	    userInfo.append(String.format("Birthday: %s\n\n", 
	        user.getBirthday()));

	    // Example: partially typed access, to location field,
	    // name key (location)
	    // - requires user_location permission
//	    userInfo.append(String.format("Location: %s\n\n", 
//	        user.getLocation().getProperty("name")));
	    
	    userInfo.append(String.format("Email: %s\n\n", user.getProperty("email")));
	    fbEmail = String.format("%s", user.getProperty("email"));

	    return userInfo.toString();
	}
	
	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state, Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};
	
	private void onSessionStateChange(Session session, SessionState state, Exception exception) {

		if (session.isOpened()) {
			
			// Request user data and show the results
		    Request.newMeRequest(session, new Request.GraphUserCallback() {

		        @Override
		        public void onCompleted(GraphUser user, Response response) {
		            if (user != null) {
		                // Display the parsed user info
		            	userInfo = buildUserInfoDisplay(user);
		            	Log.d("facebook ------------------------- ", userInfo);
		            	
		            	// try login to SaltyEnglish server with obtained fbEmail
		        		new SignInWithFacebookAPI().execute("http://todpop.co.kr/api/users/sign_in.json");
		            	
		            }
		        }
		    }).executeAsync();
		}
	}
			
	
	private class SignInWithFacebookAPI extends AsyncTask<String, Void, JSONObject> 
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

        		params.add(new BasicNameValuePair("facebook",fbEmail));

        		UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params,HTTP.UTF_8);
        		post.setEntity(ent);
        		HttpResponse responsePOST = client.execute(post);  
        		HttpEntity resEntity = responsePOST.getEntity();

        		if (resEntity != null)
        		{    
        			json = new JSONObject(EntityUtils.toString(resEntity)); 
        			Log.d("login", json.toString());				        	
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

        			settingEdit.putString("isLogin","YES");
        			settingEdit.putString("loginType", "fb");
        			settingEdit.commit();

        			// email, facebook skip (not needed)
        			rgInfoEdit.putString("mem_id", result.getJSONObject("data").getJSONObject("user").getString("id"));
        			rgInfoEdit.putString("nickname", result.getJSONObject("data").getJSONObject("user").getString("nickname"));
        			rgInfoEdit.putString("mobile", result.getJSONObject("data").getJSONObject("user").getString("mobile"));
        			rgInfoEdit.putString("level", result.getJSONObject("data").getJSONObject("user").getString("level_test"));
        			rgInfoEdit.putString("password", result.getJSONObject("data").getJSONObject("user").getString("is_set_facebook_password"));
        			rgInfoEdit.commit();

        			new GetStageInfoAPI().execute("http://todpop.co.kr/api/studies/get_stage_info.json?user_id=" + rgInfo.getString("mem_id",null));
        			
        			finish();
        		}
        		else
        		{
        			
        			// Popup facebook login error message (not registered fb ID)
        			popupText.setText(R.string.popup_facebooklogin_error);
        			popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
        			popupWindow.showAsDropDown(loginBtn);
        			
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
        	}catch (Exception e) {
        	}

        }
	}
	
	
    @Override
    protected void onResume() 
    {
        super.onResume();
        uiHelper.onResume();
    }
	    
    @Override
    public void onPause() 
    {
        super.onPause();
        uiHelper.onPause();
    }
	
    @Override
    public void onDestroy() 
    {
        super.onDestroy();
        uiHelper.onDestroy();
    }
	
    @Override
    protected void onSaveInstanceState(Bundle outState) 
    {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }
	    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) 
    {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }
}



