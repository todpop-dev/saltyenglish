package com.todpop.saltyenglish;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
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

public class RgRegisterEmailInfo extends Activity {

	//declare define UI Item
	Button birthBtn;
	RadioGroup sexRodioGroup;
	Spinner city;
	Spinner contry;
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

	//declare define popup view
	PopupWindow popupWindow;
	View popupview;
	RelativeLayout relative;
	TextView popupText;

	String cityLocal = "";

	String[] addr = {""};
	
	int interest = 0;
	
	SharedPreferences rgInfo;
	SharedPreferences.Editor rgInfoEdit;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rg_register_email_info);

		rgInfo = getSharedPreferences("rgInfo",0);
		rgInfoEdit = rgInfo.edit();
		doneBtn = (Button)findViewById(R.id.rgregisteremailinfo_id_donebtn);
		
		//popupview
		relative = (RelativeLayout)findViewById(R.id.rgregisteremailinfo_id_main_activity);;
		popupview = View.inflate(this, R.layout.popup_view, null);
		float density = getResources().getDisplayMetrics().density;
		popupWindow = new PopupWindow(popupview,(int)(300*density),(int)(100*density),true);
		popupText = (TextView)popupview.findViewById(R.id.popup_id_text);

		//picker
		birthBtn = (Button)findViewById(R.id.rgregisteremailinfo_id_birthdate);
		final Calendar c = Calendar.getInstance();   
		mYear = c.get(Calendar.YEAR);   
		mMonth = c.get(Calendar.MONTH);   
		mDay = c.get(Calendar.DAY_OF_MONTH);   
		birthBtn.setText(mYear+"-"+(mMonth+1)+"-"+mDay); 
		//spinner city contry
		city =(Spinner)findViewById(R.id.rgregisteremailinfo_id_spinner_city);
		city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

				cityLocal = city.getSelectedItem().toString();
				new GetAddrLocal().execute("http://todpop.co.kr/api/users/address_list.json?depth=2&s="+cityLocal);

			}
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		new GetAddr().execute("http://todpop.co.kr/api/users/address_list.json?depth=1");

		contry =(Spinner)findViewById(R.id.rgregisteremailinfo_id_spinner_contry);
		contry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

				contry.getSelectedItem().toString();

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
				if (checkedId == R.id.rgregisteremailinfo_id_male) 
				{    
					sex = 1;
				} 
				else 
				{    
					sex = 2; 
				}  
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
			mYear =year;   
			mMonth= monthOfYear;   
			mDay= dayOfMonth;   
			birthBtn.setText(mYear+"-"+(mMonth+1)+"-"+mDay);   
		}   
	};

	//--- request class ---
	private class GetAddr extends AsyncTask<String, Void, JSONObject> 
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
					contry.setAdapter(aa);
					contry.setSelection(0);

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
		protected JSONObject doInBackground(String... urls) 
		{
			try
			{
				HttpClient client = new DefaultHttpClient();  
				String postURL = urls[0];
				HttpPost post = new HttpPost(postURL); 
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				
				if(!rgInfo.getString("facebookEmail", "NO").equals("NO"))
				{
					params.add(new BasicNameValuePair("email", rgInfo.getString("email", "NO")));
					params.add(new BasicNameValuePair("birth", birthBtn.getText().toString()));
					params.add(new BasicNameValuePair("sex",Integer.toString(sex)));
					params.add(new BasicNameValuePair("address", city.getSelectedItem().toString()+" "+contry.getSelectedItem().toString()));
					params.add(new BasicNameValuePair("interest",Integer.toString(interest)));
					params.add(new BasicNameValuePair("mem_no",rgInfo.getString("mem_id", "NO")));
					Log.i("SETVEN", "INTEREST!!!-------"+interest);
					
					if(rgInfo.getString("password", "NO").equals("0"))
					{
						params.add(new BasicNameValuePair("password", rgInfo.getString("tempPassword", "NO")));
					}

				}else
				{
					params.add(new BasicNameValuePair("email", rgInfo.getString("email", "NO")));
					params.add(new BasicNameValuePair("password", rgInfo.getString("tempPassword", "NO")));
					params.add(new BasicNameValuePair("nickname", rgInfo.getString("nickname", "NO")));
					params.add(new BasicNameValuePair("mobile", rgInfo.getString("mobile", "NO")));
					params.add(new BasicNameValuePair("recommend", rgInfo.getString("recommend", "NO")));
					params.add(new BasicNameValuePair("birth", birthBtn.getText().toString()));
					params.add(new BasicNameValuePair("address", city.getSelectedItem().toString()+" "+contry.getSelectedItem().toString()));
					params.add(new BasicNameValuePair("interest",Integer.toString(interest)));
					params.add(new BasicNameValuePair("sex",Integer.toString(sex)));
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
		protected void onPostExecute(JSONObject result) {
			//System.out.print(result);
			//textView.setText(result);
			try {
				if (result.getBoolean("status")==true) {
					SharedPreferences settings = getSharedPreferences("setting", 0);
					SharedPreferences.Editor settingsEdit = settings.edit();
					settingsEdit.putString("isLogin", "YES");
					settingsEdit.putString("loginType", "email");
					rgInfoEdit.putString("mem_id", result.getJSONObject("data").getString("mem_id"));
					rgInfoEdit.putString("tempPassword", "NO");
					rgInfoEdit.commit();
					settingsEdit.commit();
					
					if(result.getJSONObject("data").getInt("level_test")==0)
					{
						Intent intent = new Intent(getApplicationContext(), RgRegisterFinish.class);
						startActivity(intent);
					}else{
						Intent intent = new Intent(getApplicationContext(), StudyHome.class);
						startActivity(intent);
					}
					finish();
				} else {

				}

			} catch (Exception e) {

			}

		}

	}
	/*
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
    }*/




	//----button onClick----

	public void onClickBack(View view)
	{
		finish();
	}

	public void showRgRegisterFinishActivity(View view)
	{
		checkCount = 0;
		interest = 0;
		if(sports.isChecked())
		{
			checkCount++;
			interest = interest +1;
		//}else{
		//	interest = interest +1;
		}
		if(love.isChecked()){
			checkCount++;
			interest = interest +2;
		//}else{
		//	interest = interest +2;
		}
		if(electronic.isChecked()){
			checkCount++;
			interest = interest +4;
		//}else{
		//	interest = interest +4;
		}
		if(music.isChecked()){
			checkCount++;
			interest = interest +2000;
		//}else{
		//	interest = interest +2000;
		}
		if(fashion.isChecked()){
			checkCount++;
			interest = interest +10;
		//}else{
		//	interest = interest +10;
		}
		if(game.isChecked()){
			checkCount++;
			interest = interest +20;
		//}else{
		//	interest = interest +20;
		}
		if(food.isChecked())
		{
			checkCount++;
			interest = interest +40;
		//}else{
		//	interest = interest +40;
		}
		if(language.isChecked()){
			checkCount++;
			interest = interest +200;
		//}else{
		//	interest = interest +200;
		}
		if(travel.isChecked()){
			checkCount++;
			interest = interest +100;
		//}else{
		//	interest = interest +100;
		}
		if(movie.isChecked()){
			checkCount++;
			interest = interest +400;
		//}else{
		//	interest = interest +400;
		}
		if(haircut.isChecked())
		{
			checkCount++;
			interest = interest +1000;
		//}else{
		//	interest = interest +1000;
		}
		if(money.isChecked())
		{
			checkCount++;
			interest = interest +4000;
		//}else{
		//	interest = interest +4000;
		}

		if(checkCount>2)
		{

			Log.i("SETVEN", "INTEREST!!!-------"+interest);
			new SignUp().execute("http://todpop.co.kr/api/users/sign_up.json");
		}else{
			popupText.setText(R.string.popup_interest_More_than_three);
			popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
			popupWindow.showAsDropDown(doneBtn);
		}

	}
	public void closePopup(View v)
	{
		popupWindow.dismiss();
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
			tv.setText(items.get(position));
			return convertView;  
		}  


	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.rg_register_email_info, menu);
		return true;
	}

}
