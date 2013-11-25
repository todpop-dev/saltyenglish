package com.todpop.saltyenglish;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;


import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class HomeMypageOptionCharacter extends Activity {
	
	String character = "";
	SharedPreferences rgInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_mypage_option_character);
		
		rgInfo = getSharedPreferences("rgInfo",0);;
		
		RadioGroup characterOption = (RadioGroup)findViewById(R.id.home_mypage_id_character_radiogroup);    
		characterOption.setOnCheckedChangeListener(new OnCheckedChangeListener() 
		{    
			@Override    
			public void onCheckedChanged(RadioGroup group, int checkedId) 
			{    
				if (checkedId == R.id.home_mypage_id_character_eric) 
				{    
					character = "1";
				} 
				else if(checkedId == R.id.home_mypage_id_character_selly)
				{    
					character = "2";
				}else if(checkedId == R.id.home_mypage_id_character_john)
				{    
					character = "3";
				}  else if(checkedId == R.id.home_mypage_id_character_amanda)
				{    
					character = "4";
				}  else if(checkedId == R.id.home_mypage_id_character_tom)
				{    
					character = "5";
				}  else if(checkedId == R.id.home_mypage_id_character_jenny)
				{    
					character = "6";
				}  else if(checkedId == R.id.home_mypage_id_character_monkey)
				{    
					character = "7";
				}  else if(checkedId == R.id.home_mypage_id_character_dino)
				{    
					character = "8";
				}    
				
				new GetCharacter().execute("http://todpop.co.kr/api/etc/"+rgInfo.getString("mem_id", "NO")+"/character.json?url="+character);
			}    
		});    
	}
	
	
	private class GetCharacter extends AsyncTask<String, Void, JSONObject> 
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

			return result;
		}

		@Override
		protected void onPostExecute(JSONObject json) {
		
			try {
				if(json.getBoolean("status")==true)
				{
				
					finish();
				}else
				{
					
				}
			} catch (Exception e) {

			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home_mypage_option_character, menu);
		return true;
	}

	//on click
	public void onClickBack(View v)
	{
		finish();
	}

}
