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
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.facebook.*;
import com.facebook.model.*;
import com.facebook.widget.LoginButton;

public class RgRegister extends Activity {
	
    private UiLifecycleHelper uiHelper;
    private String userInfo = null;
    
    String fbEmail = null;
    String fbId = null;
    
    Button emailBtn;
    ImageView rgNotice;
    CheckBox rgCheckbox;
    
	PopupWindow popupWindow;
	View popupview;
	RelativeLayout relative;
	TextView popupText;

	SharedPreferences rgInfo;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rg_register);
		
		rgInfo = getSharedPreferences("rgInfo",0);
		emailBtn = (Button)findViewById(R.id.register_17_email_btn);
		final LoginButton fb_btn = (LoginButton)findViewById(R.id.register_id_facebook_btn);
		rgNotice = (ImageView)findViewById(R.id.register_17_email_notice);
		rgCheckbox = (CheckBox)findViewById(R.id.rg_register_id_checkbox);
		
		//popupview
		relative = (RelativeLayout)findViewById(R.id.id_rg_regster_relative_layout);;
		popupview = View.inflate(this, R.layout.popup_view, null);
		float density = getResources().getDisplayMetrics().density;
		popupWindow = new PopupWindow(popupview,(int)(300*density),(int)(110*density),true);
		popupText = (TextView)popupview.findViewById(R.id.popup_id_text);
		
		if(!rgInfo.getString("facebookEmail","NO").equals("NO"))
		{
			fb_btn.setEnabled(false);
		}else{
			fb_btn.setEnabled(true);
		}
		if(!rgInfo.getString("email","NO").equals("NO"))
		{
			emailBtn.setEnabled(false);
		}else{
			emailBtn.setEnabled(true);
		}
		
		if(rgInfo.getString("facebookEmail","NO").equals("NO")&&rgInfo.getString("email","NO").equals("NO"))
		{
			rgNotice.setImageResource(R.drawable.register_19_text_info3);
		}
		
		else if(!rgInfo.getString("facebookEmail","NO").equals("NO")&&rgInfo.getString("email","NO").equals("NO"))
		{
			rgNotice.setImageResource(R.drawable.register_19_text_info2);
		}
		else if(rgInfo.getString("facebookEmail","NO").equals("NO")&&!rgInfo.getString("email","NO").equals("NO"))
		{
			rgNotice.setImageResource(R.drawable.register_19_text_info4);
		}else if(!rgInfo.getString("facebookEmail","NO").equals("NO")&&!rgInfo.getString("email","NO").equals("NO"))
		{
			rgNotice.setImageResource(R.drawable.register_19_text_info1);
		}
		
		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);
		
		// Facebook Login Button
		fb_btn.setReadPermissions(Arrays.asList("user_location", "user_birthday", "user_likes", "email"));
		fb_btn.setBackgroundResource(R.drawable.rgregister_drawable_btn_fb);
//	       try {
//	            PackageInfo info = getPackageManager().getPackageInfo(
//	                    "com.todpop.saltyenglish", 
//	                    PackageManager.GET_SIGNATURES);
//	            for (Signature signature : info.signatures) {
//	                MessageDigest md = MessageDigest.getInstance("SHA");
//	                md.update(signature.toByteArray());
//	                Log.d("KeyHash: ******* ", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//	                }
//	        } catch (NameNotFoundException e) {
//
//	        } catch (NoSuchAlgorithmException e) {
//
//	        }
	}
	
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
			Intent intent = new Intent(getApplicationContext(), RgRegisterEmail.class);
			startActivity(intent);
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
		getMenuInflater().inflate(R.menu.rg_register, menu);
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

		    // Example: typed access (birthday)
		    // - requires user_birthday permission
		    userInfo.append(String.format("Birthday: %s\n\n", 
		        user.getBirthday()));

		    // Example: partially typed access, to location field,
		    // name key (location)
		    // - requires user_location permission
//		    userInfo.append(String.format("Location: %s\n\n", 
//		        user.getLocation().getProperty("name")));
	//
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

		    return userInfo.toString();
		}
		return null;
	}
	
	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state, Exception exception) {
			if(rgCheckbox.isChecked()){
				onSessionStateChange(session, state, exception);
			}
			else{
				//popupview
				popupText.setText(R.string.popup_register_agree);
    			popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
    			popupWindow.showAsDropDown(rgCheckbox);
    			Log.i("STEVEN", "PopUp");
    			session.close();
    			Log.i("STEVEN", "Session Close");
			}
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
		        		new CheckFacebookEmail().execute("http://todpop.co.kr/api/users/check_facebook_exist.json?facebook="+fbEmail);
		            	
		            }
		        }
		    }).executeAsync();
		} 
	}
	
	public void closePopup(View v)
	{
		popupWindow.dismiss();
	}
	
	private class CheckFacebookEmail extends AsyncTask<String, Void, JSONObject> 
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
						
						Intent intent = new Intent(getApplicationContext(), FbNickname.class);
						intent.putExtra("fbEmail",fbEmail);
						startActivity(intent);
		    			finish();
					} else {
						// Popup duplication
						//popupview
						relative = (RelativeLayout)findViewById(R.id.id_rg_regster_relative_layout);
						popupview = View.inflate(getApplicationContext(), R.layout.popup_view, null);
						float density = getResources().getDisplayMetrics().density;
						popupWindow = new PopupWindow(popupview,(int)(300*density),(int)(100*density),true);
						popupText = (TextView)popupview.findViewById(R.string.rg_register_facebook_duplicate);
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
