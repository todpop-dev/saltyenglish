package com.todpop.saltyenglish;

import java.util.Arrays;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.facebook.*;
import com.facebook.model.*;
import com.facebook.widget.LoginButton;
import com.flurry.android.FlurryAgent;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;




public class RgRegister extends Activity {
	
    private UiLifecycleHelper uiHelper;
    private String userInfo = null;

    String fbId = null;
    String fbName = null;
    String fbBDay = null;
    String fbLocation = null;
    String fbGender = null;
    String fbEmail = null;

    ImageView rgNotice;
    Button fbCheckPointBtn;
    Button emailBtn;
    CheckBox rgCheckbox;
    
	PopupWindow popupWindow;
	View popupview;
	RelativeLayout relative;
	TextView popupText;
	LoginButton fb_btn;
	
	String mobile;
	
	SharedPreferences rgInfo;
	SharedPreferences.Editor rgInfoEdit;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rg_register);
		
		Log.d("RgRegister","1");
		
		rgInfo = getSharedPreferences("rgInfo", 0);
		rgInfoEdit = rgInfo.edit();

		rgNotice = (ImageView)findViewById(R.id.register_17_email_notice);
		fbCheckPointBtn = (Button)findViewById(R.id.register_id_facebook_btn_checkPoint);
		fbCheckPointBtn.setBackgroundResource(R.drawable.rgregister_drawable_btn_fb);
		fb_btn = (LoginButton)findViewById(R.id.register_id_facebook_btn);
		emailBtn = (Button)findViewById(R.id.register_17_email_btn);
		rgCheckbox = (CheckBox)findViewById(R.id.rg_register_id_checkbox);
		
		//popupview
		relative = (RelativeLayout)findViewById(R.id.id_rg_regster_relative_layout);;
		popupview = View.inflate(this, R.layout.popup_view, null);
		popupWindow = new PopupWindow(popupview,ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT,true);
		popupText = (TextView)popupview.findViewById(R.id.popup_id_text);
		
		
		//get phone number
		try {
			TelephonyManager phoneMgr=(TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE); 
			mobile =phoneMgr.getLine1Number().toString();
			mobile = mobile.replace("+82", "0");
		} catch(Exception e) {
			mobile = "010test0000";
		}
		
		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);

		new CheckMobileExist().execute("http://todpop.co.kr/api/users/check_mobile_exist.json?mobile="+mobile);
	}
	
	
	//--- request class ---
	private class CheckMobileExist extends AsyncTask<String, Void, JSONObject> 
	{
		DefaultHttpClient httpClient ;
		@Override
		protected JSONObject doInBackground(String... urls) 
		{
			JSONObject result = null;
			try
			{
				String getURL = urls[0];
				HttpGet httpGet = new HttpGet(getURL); 
				HttpParams httpParameters = new BasicHttpParams(); 
				
				int timeoutConnection = 3000; 
				HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection); 
				int timeoutSocket = 3000; 
				HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket); 

				httpClient = new DefaultHttpClient(httpParameters); 
				HttpResponse response = httpClient.execute(httpGet); 
				HttpEntity resEntity = response.getEntity();

				if (resEntity != null)
				{    
					result = new JSONObject(EntityUtils.toString(resEntity)); 
					Log.d("RESPONSE JSON CHECK MOBILE EXIST ---- ", result.toString());				        	
				}
				return result;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally 
			{     
				httpClient.getConnectionManager().shutdown();     
			} 
			return result;
		}

		@Override
		protected void onPostExecute(JSONObject json) {

			try {
				if(json.getBoolean("status")==false) {

					rgInfoEdit.putString("email",null);
					rgInfoEdit.putString("facebook",null);
					rgInfoEdit.putString("mobile",mobile);
					rgInfoEdit.putString("level", "0");
					
					rgInfoEdit.putString("mem_id", null);
					rgInfoEdit.putString("nickname", null);
					rgInfoEdit.putString("recommend", null);
					rgInfoEdit.putString("password", "0");
					
					rgInfoEdit.commit();

				} else {	
					
					rgInfoEdit.putString("email",json.getJSONObject("data").getString("email"));
					rgInfoEdit.putString("facebook",json.getJSONObject("data").getString("facebook"));
					rgInfoEdit.putString("mobile",json.getJSONObject("data").getString("mobile"));
					rgInfoEdit.putString("level", json.getJSONObject("data").getString("level_test"));
					
					rgInfoEdit.putString("mem_id", json.getJSONObject("data").getString("id"));
					rgInfoEdit.putString("nickname", json.getJSONObject("data").getString("nickname"));
					
					String recommend = json.getJSONObject("data").getString("recommend");
					if(recommend.equals("null"))	{rgInfoEdit.putString("recommend", null);}
					else							{rgInfoEdit.putString("recommend", recommend);}
					
					rgInfoEdit.putString("password", json.getJSONObject("data").getString("is_set_facebook_password"));
					
					if(json.getJSONObject("data").getString("email").equals("null")) {rgInfoEdit.putString("email",null);}
					if(json.getJSONObject("data").getString("facebook").equals("null")) {rgInfoEdit.putString("facebook",null);}
										
					rgInfoEdit.commit();

				}

				Log.d("return info",rgInfo.getString("email", "--")+"  "
						+rgInfo.getString("facebook", "--")+"  "
						+rgInfo.getString("mobile", "--")+"  "
						+rgInfo.getString("level", "--")+"  "
						+rgInfo.getString("mem_id", "--")+"  "
						+rgInfo.getString("nickname", "--")+"  "
						+rgInfo.getString("recommend", "-- ")+"  "
						+rgInfo.getString("password", "--"));

			} catch (Exception e) {

			}
			
			
			if(!rgInfo.getString("facebook","no").equals("no")) 
			{
				fbCheckPointBtn.setEnabled(false);
			}else{
				fbCheckPointBtn.setEnabled(true);
			}
			
			if(!rgInfo.getString("email","no").equals("no"))
			{
				emailBtn.setEnabled(false);
			}else{
				emailBtn.setEnabled(true);
			}
			
			
			Log.d("f=",rgInfo.getString("facebook","no"));
			Log.d("e=",rgInfo.getString("email","no"));
			
			if(rgInfo.getString("facebook","no").equals("no") && rgInfo.getString("email","no").equals("no"))
			{
				rgNotice.setImageResource(R.drawable.register_19_text_info3);
			}
			else if(!rgInfo.getString("facebook","no").equals("no") && rgInfo.getString("email","no").equals("no"))
			{
				rgNotice.setImageResource(R.drawable.register_19_text_info2);
			}
			else if(rgInfo.getString("facebook","no").equals("no") && !rgInfo.getString("email","no").equals("no"))
			{
				rgNotice.setImageResource(R.drawable.register_19_text_info4);
			}else if(!rgInfo.getString("facebook","no").equals("no") && !rgInfo.getString("email","no").equals("no"))
			{
				rgNotice.setImageResource(R.drawable.register_19_text_info1);
			}
			
			// Facebook Login Button
			fb_btn.setReadPermissions(Arrays.asList("user_location", "user_birthday", "user_likes", "email"));
			//fb_btn.setBackgroundResource(R.drawable.rgregister_drawable_btn_fb);
//		       try {
//		            PackageInfo info = getPackageManager().getPackageInfo(
//		                    "com.todpop.saltyenglish", 
//		                    PackageManager.GET_SIGNATURES);
//		            for (Signature signature : info.signatures) {
//		                MessageDigest md = MessageDigest.getInstance("SHA");
//		                md.update(signature.toByteArray());
//		                Log.d("KeyHash: ******* ", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//		                }
//		        } catch (NameNotFoundException e) {
	//
//		        } catch (NoSuchAlgorithmException e) {
	//
//		        }

			
		}
	}

	
	// ------------------------------------------------------------------------------------------------------------------------------
		
	
	//----button onClick----
	public void onClickBack(View view)
	{
		finish();
	}
	
	// Go do email register activity
	public void showRgRegisterEmailActivity(View view)
	{
		if(!rgCheckbox.isChecked()){
			//popupview
			popupText.setText(R.string.popup_register_agree);
			popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
			popupWindow.showAsDropDown(rgCheckbox);
		}
		else{
			FlurryAgent.logEvent("Email Register Clicked");
			Intent intent = new Intent(getApplicationContext(), RgRegisterEmail.class);
			startActivity(intent);
		}
	}

	public void checkAgreementAndClickFB(View view){
		if(!rgCheckbox.isChecked()){
			//popupview
			popupText.setText(R.string.popup_register_agree);
			popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
			popupWindow.showAsDropDown(rgCheckbox);
		}
		else{
			FlurryAgent.logEvent("Facebook Register Clicked");
			fb_btn.performClick();
		}
	}
	
	public void showProvisionActivity_user(View view)
	{
		Intent intent = new Intent(getApplicationContext(), RgRegisterProvision.class);
		intent.putExtra("wButton", 1);
		startActivity(intent);
	}
	
	public void showProvisionActivity_personal(View view)
	{
		Intent intent = new Intent(getApplicationContext(), RgRegisterProvision.class);
		intent.putExtra("wButton", 2);
		startActivity(intent);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.rg_register, menu);
		return true;
	}
	
	// Facebook delegates
	
    // For Email Address
    private interface MyGraphEmail extends GraphObject {
        // Getter for the Email field
        String getEmail();
    }
    
    // Get User Info
	private String buildUserInfoDisplay(GraphUser user) {
		if (user != null) {
		    StringBuilder userInfo = new StringBuilder("");

		    userInfo.append(String.format("ID: %s\n\n", 
		        user.getId()));
		    fbId = user.getId();
		    
		    // Example: typed access (name)
		    // - no special permissions required
		    userInfo.append(String.format("Name: %s\n\n", 
		        user.getName()));
		    fbName = user.getName();

		    // Example: typed access (birthday)
		    // - requires user_birthday permission
		    userInfo.append(String.format("Birthday: %s\n\n", 
		        user.getBirthday()));
		    fbBDay = user.getBirthday();

		    // Example: partially typed access, to location field,
		    // name key (location)
		    // - requires user_location permission
		    try{
		    	userInfo.append(String.format("Location: %s\n\n", 
		    			user.getLocation().getProperty("name")));
			    fbLocation = String.valueOf(user.getLocation().getProperty("name"));
		    }catch(NullPointerException e){
		    	e.printStackTrace();
		    }
		    
		    //get gender
		    try{
		    	userInfo.append(String.format("Gender: %s\n\n", 
				        user.getProperty("gender")));
			    fbGender = String.valueOf(user.getProperty("gender"));
		    }catch(NullPointerException e){
		    	e.printStackTrace();
		    }
		    
//		    // Example: access via property name (locale)
//		    // - no special permissions required
//		    userInfo.append(String.format("Locale: %s\n\n", 
//		        user.getProperty("locale")));

		    // Example: access via key for array (languages) 
		    // - requires user_likes permission
//		    JSONArray languages = (JSONArray)user.getProperty("languages");
//		    if (languages.length() > 0) {
//		        ArrayList<String> languageNames = new ArrayList<String> ();
//		        for (int i=0; i < languages.length(); i++) {
//		            JSONObject language = languages.optJSONObject(i);
//		            // Add the language name to a list. Use JSON
//		            // methods to get access to the name field. 
//		            languageNames.add(language.optString("name"));
//		        }           
//		        userInfo.append(String.format("Languages: %s\n\n", 
//		        languageNames.toString()));
		    
		    userInfo.append(String.format("Email: %s\n\n", user.getProperty("email")));
		    fbEmail = String.format("%s", user.getProperty("email"));
		    Log.i("STEVEN", "facebook email works fine "+ fbEmail);
		    return userInfo.toString();
		}
		return null;
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
		            	Log.d("facebook -------------------------%s", userInfo);
		            	
		            	// Check if facebook id exist
		            	if(fbEmail.equals("null")){
		            		popupText.setText(R.string.rg_register_facebook_email_null);
		            		popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
		            		popupWindow.showAsDropDown(rgCheckbox);
		            	}
		            	else{
		            		Log.i("STEVEN", "Check facebook id exist "+ fbEmail);
		            		new CheckFacebookExistAPI().execute("http://todpop.co.kr/api/users/check_facebook_exist.json?facebook="+fbEmail);
		            	}
		            }
		        }
		    }).executeAsync();
		} 
	}
	
	public void closePopup(View v)
	{
		popupWindow.dismiss();
	}
	
	private class CheckFacebookExistAPI extends AsyncTask<String, Void, JSONObject> 
	{
		DefaultHttpClient httpClient ;
		@Override
		protected JSONObject doInBackground(String... urls) 
		{
			JSONObject result = null;
			try
			{
				String getURL = urls[0];
				HttpGet httpGet = new HttpGet(getURL); 
				HttpParams httpParameters = new BasicHttpParams(); 
				int timeoutConnection = 3000; 
				HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection); 
				int timeoutSocket = 3000; 
				HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket); 

				httpClient = new DefaultHttpClient(httpParameters); 
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
			finally 
			{     

				httpClient.getConnectionManager().shutdown();     

			} 
			return result;
		}

		@Override
		protected void onPostExecute(JSONObject json) {
			try {
				if(json.getBoolean("status")==true) {
					boolean result = json.getJSONObject("data").getBoolean("result");
					if (result == true) {

					    Log.i("STEVEN", "change activity to FbNickname "+ fbEmail);
					    
						rgInfoEdit.putString("facebook", fbEmail);
						rgInfoEdit.putString("fbName", fbName);
						String y = fbBDay.substring(6, 10);
						String m = fbBDay.substring(0, 2);
						String d = fbBDay.substring(3, 5);
						fbBDay = y + "-" + m + "-" + d;
						rgInfoEdit.putString("fbBDay", fbBDay);
						rgInfoEdit.putString("fbLocation", fbLocation);
						if(fbGender.equals("male")){
							rgInfoEdit.putString("fbGender", "1");		
						}
						else if(fbGender.equals("female")){
							rgInfoEdit.putString("fbGender", "2");
						}
							
						rgInfoEdit.commit();
						
						Log.i("STEVEN", rgInfo.getString("fbLocation", "NO"));
						Log.i("STEVEN", rgInfo.getString("fbBDay", "NO"));
					    
						Intent intent = new Intent(getApplicationContext(), FbNickname.class);
//						intent.putExtra("fbEmail",fbEmail);
						startActivity(intent);
		    			finish();
					} else {
					    Log.i("STEVEN", "Facebook Email duplication"+ fbEmail);
						// Popup duplication
						popupText.setText(R.string.rg_register_facebook_duplicate);
						popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
						popupWindow.showAsDropDown(rgCheckbox);
					}
				} else {
					// Should never be here TODO: Popup

				}
			} catch (Exception e) {

			}
		}
	}
	
	// Life cycle control for Facebook Login
    @Override
    protected void onResume() 
    {
        super.onResume();
		com.facebook.AppEventsLogger.activateApp(this, "539574922799801");
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
	@Override
	protected void onStart()
	{
		super.onStart();
		FlurryAgent.onStartSession(this, "ZKWGFP6HKJ33Y69SP5QY");
		FlurryAgent.logEvent("Register");
	}
	 
	@Override
	protected void onStop()
	{
		super.onStop();		
		FlurryAgent.onEndSession(this);
	}
}
