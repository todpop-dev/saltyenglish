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
import android.content.res.Resources;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class HomeMyPageAttendance extends Activity {

	SharedPreferences rgInfo;
	TextView attendancDay;
	TextView attendanceReward;
	Button adDay;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_my_page_attendance);
		rgInfo = getSharedPreferences("rgInfo",0);
		
		attendancDay = (TextView)findViewById(R.id.home_my_page_attendance_day);
		attendanceReward = (TextView)findViewById(R.id.home_my_page_attendance_reward);
		new GetAttendance().execute("http://todpop.co.kr/api/users/"+rgInfo.getString("mem_id", "NO")+"/get_attendance_time.json");
	}
	
	private class GetAttendance extends AsyncTask<String, Void, JSONObject> 
	{
		@Override
		protected JSONObject doInBackground(String... urls) 
		{
			JSONObject result = null;
			try
			{
				String getURL = urls[0];
				HttpGet httpGet = new HttpGet(getURL); 
				HttpParams httpParameters = new BasicHttpParams(); 
				DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters); 
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
					attendancDay.setText(json.getJSONObject("data").getString("attendance_time"));
					attendanceReward.setText(json.getJSONObject("data").getString("attendance_reward"));
					for(int i=0;i<json.getJSONObject("data").getInt("attendance_time") ;i++)
					{
						Resources res = getResources();
						int id = res.getIdentifier("home_mypage_attendance_id_btn"+i, "id",getApplicationContext().getPackageName());
						adDay = (Button)findViewById(id);
						adDay.setEnabled(true);
					}
				}else{
					
				}				
			} catch (Exception e) {

			}
		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home_my_page_attendance, menu);
		return true;
	}

	// on click
	public void onClickBack(View v)
	{
		finish();
	}
}
