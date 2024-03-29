package com.todpop.saltyenglish;

import java.util.ArrayList;
import java.util.Calendar;
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
import org.json.JSONArray;
import org.json.JSONObject;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;
import com.todpop.api.LoadingDialog;
import com.todpop.api.TypefaceActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.audiofx.BassBoost.Settings;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;

public class RgRegisterEmailInfo extends TypefaceActivity {
	TelephonyManager phoneMgr;
	
	String email;
	String password;
	String nickname;
	String recommender;
	String mobile;
	String operator;
	String country;
	
	Boolean kickBack = false;

	//declare define UI Item
	Button birthBtn;
	RadioGroup sexRodioGroup;
	Spinner city;
	Spinner county;
	Button doneBtn;

	CheckBox sports=null; 
	CheckBox love=null; 
	CheckBox electronic=null; 
	CheckBox music=null; 
	CheckBox fashion=null; 
	CheckBox game=null; 
	CheckBox food=null; 
	CheckBox language=null; 
	CheckBox travel=null; 
	CheckBox movie=null; 
	CheckBox haircut=null; 
	CheckBox money=null;
	int checkCount = 0;
	//sex select
	int sex;
	
	boolean setBirth = false;

	//declare define popup view
	PopupWindow popupWindow;
	View popupview;
	RelativeLayout relative;
	TextView popupText;

	String cityLocal = "";				// Si/Do
	int interest = 0;
	
	SharedPreferences rgInfo;
	SharedPreferences.Editor rgInfoEdit;
	SharedPreferences setting;
	SharedPreferences.Editor settingEdit;
	SharedPreferences studyInfo;
	SharedPreferences.Editor studyInfoEdit;
	
	LoadingDialog loadingDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rg_register_email_info);
		
		Intent intent = getIntent();
		
		email = intent.getStringExtra("email");
		password = intent.getStringExtra("password");
		nickname = intent.getStringExtra("nickname");
		recommender = intent.getStringExtra("recommender");
		
		rgInfo = getSharedPreferences("rgInfo",0);
		rgInfoEdit = rgInfo.edit();
		setting = getSharedPreferences("setting", 0);
		settingEdit = setting.edit();
		studyInfo = getSharedPreferences("studyInfo", 0);
		studyInfoEdit = studyInfo.edit();
		
		//popupview
		relative = (RelativeLayout)findViewById(R.id.rgregisteremailinfo_id_main_activity);;
		popupview = View.inflate(this, R.layout.popup_view, null);
		popupWindow = new PopupWindow(popupview,ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT,true);
		popupText = (TextView)popupview.findViewById(R.id.popup_id_text);

		setFont(popupText);
		
		mobile = rgInfo.getString("mobile", null);
		phoneMgr = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE); 
		if(mobile == null){
			//get phone number
			try {
				mobile = phoneMgr.getLine1Number().toString();
				mobile = mobile.replace("+82", "0");
			} catch(Exception e) {
				kickBack = true;
				popupText.setText(R.string.popup_get_mobile_fail);
				popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
			}
		}
		
		if(mobile == null || mobile.equals("010test0000")){
			mobile = intent.getStringExtra("mobile");
		}
		
		try{
			operator = phoneMgr.getNetworkOperatorName();
			country = phoneMgr.getNetworkCountryIso();
		} catch(Exception e){
			
		}
		
		doneBtn = (Button)findViewById(R.id.rgregisteremailinfo_id_donebtn);

		//picker
		birthBtn = (Button)findViewById(R.id.rgregisteremailinfo_id_birthdate);
		final Calendar c = Calendar.getInstance();   
		mYear = c.get(Calendar.YEAR);   
		mMonth = c.get(Calendar.MONTH);   
		mDay = c.get(Calendar.DAY_OF_MONTH);   
		
		birthBtn.setText(mYear + getResources().getString(R.string.rg_register_email_info_year) + " " + 
				(mMonth+1) + getResources().getString(R.string.rg_register_email_info_month) + " " + 
				mDay + getResources().getString(R.string.rg_register_email_info_day)); 
		
		//spinner city county
		city =(Spinner)findViewById(R.id.rgregisteremailinfo_id_spinner_city);
		city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

				cityLocal = city.getSelectedItem().toString();
				new GetAddrLocal().execute("http://todpop.co.kr/api/users/address_list.json?depth=2&s="+cityLocal);

			}
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		
		loadingDialog = new LoadingDialog(this);
		
		new GetAddr().execute("http://todpop.co.kr/api/users/address_list.json?depth=1");

		county = (Spinner)findViewById(R.id.rgregisteremailinfo_id_spinner_contry);
		county.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

				county.getSelectedItem().toString();

			}
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		//sex radioGroup
		sex = 1;
		sexRodioGroup = (RadioGroup)findViewById(R.id.rgregisteremailinfo_id_sexgroup);    
		sexRodioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() 
		{    
			@Override    
			public void onCheckedChanged(RadioGroup group, int checkedId) 
			{    
				if (checkedId == R.id.rgregisteremailinfo_id_male) {sex = 1;} 
				else {sex = 2;}  
			}    
		});    

		//interest check box
		sports=(CheckBox)findViewById(R.id.sports);
		love=(CheckBox)findViewById(R.id.love); 
		electronic=(CheckBox)findViewById(R.id.electronic); 
		music=(CheckBox)findViewById(R.id.music); 
		fashion=(CheckBox)findViewById(R.id.fashion); 
		game=(CheckBox)findViewById(R.id.game); 
		food=(CheckBox)findViewById(R.id.food); 
		language=(CheckBox)findViewById(R.id.language); 
		travel=(CheckBox)findViewById(R.id.travel); 
		movie=(CheckBox)findViewById(R.id.movie); 
		haircut=(CheckBox)findViewById(R.id.haircut); 
		money=(CheckBox)findViewById(R.id.money);
	}



	//declare define picker    
	private int mYear;   
	private int mMonth;   
	private int mDay;   
	static final int DATE_DIALOG_ID = 0;    


	private OnDateSetListener dsl = new DatePickerDialog.OnDateSetListener()
	{   
		@Override  
		public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth) 
		{   
			setBirth = true;
			mYear =year;   
			mMonth= monthOfYear;   
			mDay= dayOfMonth;   
			birthBtn.setText(mYear + getResources().getString(R.string.rg_register_email_info_year) + " " + 
					(mMonth+1) + getResources().getString(R.string.rg_register_email_info_month) + " " + 
					mDay + getResources().getString(R.string.rg_register_email_info_day));   
		}   
	};

	//--- request class ---
	private class GetAddr extends AsyncTask<String, Void, JSONObject> 
	{
		@Override
		protected void onPreExecute(){
			super.onPreExecute();
			loadingDialog.show();
		}
		@Override
		protected JSONObject doInBackground(String... urls) 
		{
			JSONObject result = null;
			try
			{
				DefaultHttpClient httpClient;
				String getURL = urls[0];
				HttpGet httpGet = new HttpGet(getURL);
				HttpParams httpParameters = new BasicHttpParams(); 
				
				int timeoutConnection = 3000; 
				HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection); 
				int timeoutSocket = 5000; 
				HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket); 

				httpClient = new DefaultHttpClient(httpParameters); 
				
				HttpResponse httpResponse = httpClient.execute(httpGet);
				HttpEntity resEntity = httpResponse.getEntity();

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
				return result;
			}
		}

		@Override
		protected void onPostExecute(JSONObject json) {

			try {
				loadingDialog.dissmiss();
				if(json != null){
					if(json.getBoolean("status")==true)
					{
	
						JSONArray cityScr = json.getJSONObject("data").getJSONArray("addr");
						List<String> list = new ArrayList<String>();
						for(int i=0;i<cityScr.length();i++)
						{
							list.add( cityScr.getString(i) );
						}	
	
						SpinnerAdapter aa = new SpinnerAdapter(RgRegisterEmailInfo.this,  
								android.R.layout.simple_spinner_item, list);  
						city.setAdapter(aa);
						city.setSelection(0);
						cityLocal = cityScr.getString(0);
						new GetAddrLocal().execute("http://todpop.co.kr/api/users/address_list.json?depth=2&s="+cityLocal);
					}else{		        
						Log.d("-----------------------", "Login Failed");
					}
				}
				else{
					kickBack = true;
					popupText.setText(R.string.popup_common_timeout);
					popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
				}
			} catch (Exception e) {

			}

		}
	}

	private class GetAddrLocal extends AsyncTask<String, Void, JSONObject> 
	{
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

					JSONArray contryScr = json.getJSONObject("data").getJSONArray("addr");
					List<String> list = new ArrayList<String>();
					for(int i=0;i<contryScr.length();i++)
					{
						list.add( contryScr.getJSONArray(i).getString(1) );
					}	

					SpinnerAdapter aa = new SpinnerAdapter(RgRegisterEmailInfo.this,  
							android.R.layout.simple_spinner_item, list);  
					county.setAdapter(aa);
					county.setSelection(0);

				}else{		        
					Log.d("-----------------------", "Login Failed");
				}
			} catch (Exception e) {

			}

		}
	}

	@Override  
	protected Dialog onCreateDialog(int id)
	{   
		switch(id)
		{   
		case DATE_DIALOG_ID:   
			return new DatePickerDialog(this, dsl, mYear, mMonth, mDay);   
		}   
		return null;   
	}   
	
	
	//---- send info -----
	private class SignUp extends AsyncTask<String, Void, JSONObject> {

		JSONObject result = null;
		
		@Override
		protected void onPreExecute(){
			super.onPreExecute();
			loadingDialog.show();
		}
		
		@Override
		protected JSONObject doInBackground(String... urls) 
		{
			try
			{
				String postURL = urls[0];
				HttpPost post = new HttpPost(postURL); 
        		HttpParams httpParams = new BasicHttpParams();
        		
				List<NameValuePair> params = new ArrayList<NameValuePair>();

				//junho determine unique device
				String android_id = Secure.getString(getApplicationContext().getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
				params.add(new BasicNameValuePair("device_id",android_id));
				//junho end
				
				String birthDay = Integer.toString(mYear) + "-" + Integer.toString(mMonth + 1) + "-" + Integer.toString(mDay);
				if(!rgInfo.getString("facebook","no").equals("no"))						// cross join (facebook->email)
				{
					params.add(new BasicNameValuePair("email", rgInfo.getString("email", null)));
					params.add(new BasicNameValuePair("birth", birthDay));
					params.add(new BasicNameValuePair("sex",Integer.toString(sex)));
					params.add(new BasicNameValuePair("address", city.getSelectedItem().toString()+" "+county.getSelectedItem().toString()));
					params.add(new BasicNameValuePair("interest",Integer.toString(interest)));
					params.add(new BasicNameValuePair("mem_no",rgInfo.getString("mem_id", null)));
					
					if(rgInfo.getString("password", "0").equals("0"))
					{
						params.add(new BasicNameValuePair("password", rgInfo.getString("tempPassword","no")));
					}

				}else																			// first join (email->facebook)
				{
					params.add(new BasicNameValuePair("email", email));
					params.add(new BasicNameValuePair("password", password));
					params.add(new BasicNameValuePair("nickname", nickname));
					params.add(new BasicNameValuePair("mobile", mobile));
					
					if(!rgInfo.getString("recommend", "no").equals("no"))
					{
						params.add(new BasicNameValuePair("recommend", recommender));
					}
					
					params.add(new BasicNameValuePair("birth", birthDay));
					params.add(new BasicNameValuePair("address", city.getSelectedItem().toString()+" "+county.getSelectedItem().toString()));
					params.add(new BasicNameValuePair("interest",Integer.toString(interest)));
					params.add(new BasicNameValuePair("sex",Integer.toString(sex)));
				}
				
				params.add(new BasicNameValuePair("device", Build.DEVICE));
				params.add(new BasicNameValuePair("android_version", Build.VERSION.RELEASE));
				params.add(new BasicNameValuePair("operator", operator));
				params.add(new BasicNameValuePair("operator_region", country));
				
				UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params,HTTP.UTF_8);
				post.setEntity(ent);
				Log.i("STEVEN", "parans!! = " + params.toString());
				HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
        		HttpConnectionParams.setSoTimeout(httpParams, 10000);
        		DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
        		
				HttpResponse responsePOST = httpClient.execute(post);  
				HttpEntity resEntity = responsePOST.getEntity();

				if (resEntity != null)
				{    
					result = new JSONObject(EntityUtils.toString(resEntity)); 
					Log.d("send contents", params.toString());
					Log.d("RESPONSE ---- ", result.toString());				        	
				}
				return result;

			}
			catch (Exception e)
			{
				e.printStackTrace();
				return result;
			}
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			//System.out.print(result);
			//textView.setText(result);
			try {
				loadingDialog.dissmiss();
				if(result != null){
					if (result.getBoolean("status")==true) {
						
						settingEdit.putString("isLogin", "YES");
						settingEdit.putString("loginType", "email");
						settingEdit.apply();
						
						rgInfoEdit.putString("mem_id", result.getJSONObject("data").getString("mem_id"));
						rgInfoEdit.putString("tempPassword", null);
						rgInfoEdit.apply();
						
						Log.d("RgRegisterEmailInfo","400");
						Log.d("chk=",result.getJSONObject("data").getString("mem_id") + "     " + result.getJSONObject("data").getString("level_test"));

						new GetStageInfoAPI().execute("http://todpop.co.kr/api/studies/get_stage_info.json?user_id=" + rgInfo.getString("mem_id",null));
						
						Intent intent = new Intent(getApplicationContext(), RgRegisterTutorial.class);
						startActivity(intent);
						finish();
						
					} else {
						doneBtn.setClickable(true);
						int code = result.getInt("code");
						if(code == 71){
							popupText.setText(R.string.popup_sign_up_fail_mobile_error);
							popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);						
						}
						else if(code == 72){
							popupText.setText(R.string.popup_sign_up_fail_mobile_duplicated);
							popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);		
						}
						else{
							popupText.setText(R.string.popup_sign_up_fail_false);
							popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
						}
					}
				}
				else{
					doneBtn.setClickable(true);
					popupText.setText(R.string.popup_common_timeout);
					popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
				}
			} catch (Exception e) {
				doneBtn.setClickable(true);
				popupText.setText(R.string.popup_sign_up_fail);
				popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
			}
		}

	}
	
	// -------------- get stage info ---------------------------------------------------------------

	private class GetStageInfoAPI extends AsyncTask<String, Void, JSONObject> {
		@Override
		protected JSONObject doInBackground(String... urls) 
		{
			Log.d("RRE","445");
			
			JSONObject result = null;
			try
			{
				DefaultHttpClient httpClient = new DefaultHttpClient();
				String getURL = urls[0];
				HttpGet httpGet = new HttpGet(getURL);
				HttpResponse httpResponse = httpClient.execute(httpGet);
				HttpEntity resEntity = httpResponse.getEntity();

				Log.d("RRE","456");
				
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
			
			Log.d("RRE","469");
			
			return result;
		}

		@Override
		protected void onPostExecute(JSONObject json) {
			try {
				
				Log.d("RRE","478");
				
				if(json.getBoolean("status")) {
					
					String stage_info = json.getJSONObject("data").getString("stage");
					studyInfoEdit.putString("stageInfo",stage_info);
					studyInfoEdit.apply();
				}
				else
				{
					Log.d("RRE","491");
				}
				
			} catch (Exception e) {

			}
		}
	}
	

	//----button onClick----

	public void onClickBack(View view)
	{
		finish();
	}

	public void showRgRegisterFinishActivity(View view)
	{
		doneBtn.setClickable(false);
		checkCount = 0;
		interest = 0;
		if(sports.isChecked())			{	checkCount++;	interest = interest +1;		}
		if(love.isChecked())			{	checkCount++;	interest = interest +2;		}
		if(electronic.isChecked())		{	checkCount++;	interest = interest +4;		}
		if(music.isChecked())			{	checkCount++;	interest = interest +2000;	}
		if(fashion.isChecked())			{	checkCount++;	interest = interest +10;	}
		if(game.isChecked())			{	checkCount++;	interest = interest +20;	}
		if(food.isChecked())			{	checkCount++;	interest = interest +40;	}
		if(language.isChecked())		{	checkCount++;	interest = interest +200;	}
		if(travel.isChecked())			{	checkCount++;	interest = interest +100;	}
		if(movie.isChecked())			{	checkCount++;	interest = interest +400;	}
		if(haircut.isChecked())			{	checkCount++;	interest = interest +1000;	}
		if(money.isChecked())			{	checkCount++;	interest = interest +4000;	}

		if(setBirth){
			if(checkCount>2)
			{
				Log.i("SETVEN", "INTEREST!!!-------"+interest);
				new SignUp().execute("http://todpop.co.kr/api/users/sign_up.json");
			}else{
				popupText.setText(R.string.popup_interest_More_than_three);
				popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
				popupWindow.showAsDropDown(null);
				doneBtn.setClickable(true);
			}
		}
		else{
			popupText.setText(R.string.popup_birth_not_selected);
			popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
			popupWindow.showAsDropDown(null);
			doneBtn.setClickable(true);
		}
	}
	
	public void closePopup(View v)
	{
		popupWindow.dismiss();
		if(kickBack)
			finish();
	}

	public void setBirthdate(View view)
	{
		//showDialog(DATE_DIALOG_ID);
		new DatePickerDialog(RgRegisterEmailInfo.this, dsl, mYear, mMonth, mDay).show();
	}

	private class SpinnerAdapter extends ArrayAdapter<String> {  
		Context context;  
		//String[] items = new String[] {};  
		List<String> items = new ArrayList<String>();
		public SpinnerAdapter(final Context context,  
				final int textViewResourceId, final List<String> objects) {  
			super(context, textViewResourceId, objects);  
			this.items = objects;  
			this.context = context;  
		}  

		@Override  
		public View getDropDownView(int position, View convertView,  
				ViewGroup parent) {  

			if (convertView == null) {  
				LayoutInflater inflater = LayoutInflater.from(context);  
				convertView = inflater.inflate(  
						android.R.layout.simple_spinner_dropdown_item, parent, false);  
			}  

			TextView tv = (TextView) convertView  
					.findViewById(android.R.id.text1);  
			
			setFont(tv);
			
			tv.setText(items.get(position));  
			tv.setPadding(30, 10, 0, 10);
			//tv.setGravity(BIND_AUTO_CREATE);
			tv.setTextSize(18);  

			return convertView;  
		}  

		@Override  
		public View getView(int position, View convertView, ViewGroup parent) {  
			if (convertView == null) {  
				LayoutInflater inflater = LayoutInflater.from(context);  
				convertView = inflater.inflate(  
						android.R.layout.simple_spinner_item, parent, false);  
			}  

			// android.R.id.text1 is default text view in resource of the android.  
			// android.R.layout.simple_spinner_item is default layout in resources of android.  

			TextView tv = (TextView) convertView  
					.findViewById(android.R.id.text1);  
			
			setFont(tv);
			
			tv.setText(items.get(position));
			return convertView;  
		}  


	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.rg_register_email_info, menu);
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
