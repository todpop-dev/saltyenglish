package com.todpop.saltyenglish;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;
import com.todpop.api.TypefaceActivity;

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

public class HomeMyPageAttendance extends TypefaceActivity {

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
					Log.d("RESPONSE JSON ---- ", result.toString());				        	
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
					setAttendanceView(json.getJSONObject("data").getInt("attendance_time"));
					attendancDay.setText(json.getJSONObject("data").getString("attendance_time"));
					attendanceReward.setText(json.getJSONObject("data").getString("attendance_reward"));
					/*for(int i=0;i<json.getJSONObject("data").getInt("attendance_time") ;i++)
					{
						Resources res = getResources();
						int id = res.getIdentifier("home_mypage_attendance_id_btn"+i, "id",getApplicationContext().getPackageName());
						adDay = (Button)findViewById(id);
						adDay.setEnabled(true);
					}*/
				}else{
					
				}				
			} catch (Exception e) {

			}
		}
	}

	private void setAttendanceView(int attInARow){
		int divTen = attInARow / 10;
		Resources res = getResources();
		
		//if attendance days are less than 30 days
		if(divTen < 3){
			for(int i = 0; i < attInARow; i++)
			{
				int id = res.getIdentifier("home_mypage_attendance_id_btn" + i, "id", getApplicationContext().getPackageName());
				adDay = (Button)findViewById(id);
				adDay.setEnabled(true);
			}
			adDay = (Button)findViewById(res.getIdentifier("home_mypage_attendance_id_btn" + 4, "id", getApplicationContext().getPackageName()));
			adDay.setBackgroundResource(R.drawable.homemypageattendance_drawable_btn_chick);
			adDay = (Button)findViewById(res.getIdentifier("home_mypage_attendance_id_btn" + 9, "id", getApplicationContext().getPackageName()));
			adDay.setBackgroundResource(R.drawable.homemypageattendance_drawable_btn_chicken);
			adDay = (Button)findViewById(res.getIdentifier("home_mypage_attendance_id_btn" + 19, "id", getApplicationContext().getPackageName()));
			adDay.setBackgroundResource(R.drawable.homemypageattendance_drawable_btn_cat);
			adDay = (Button)findViewById(res.getIdentifier("home_mypage_attendance_id_btn" + 29, "id", getApplicationContext().getPackageName()));
			adDay.setBackgroundResource(R.drawable.homemypageattendance_drawable_btn_dinosaur);
		}
		else{
			int calVal = 20 + (attInARow % 10);
			if(divTen == 3){	//if 30~39
				adDay = (Button)findViewById(res.getIdentifier("home_mypage_attendance_id_btn" + 9, "id", getApplicationContext().getPackageName()));
				adDay.setBackgroundResource(R.drawable.homemypageattendance_drawable_btn_cat);
			}
			else{
				adDay = (Button)findViewById(res.getIdentifier("home_mypage_attendance_id_btn" + 9, "id", getApplicationContext().getPackageName()));
				adDay.setBackgroundResource(R.drawable.homemypageattendance_drawable_btn_dinosaur);
			}
			adDay = (Button)findViewById(res.getIdentifier("home_mypage_attendance_id_btn" + 19, "id", getApplicationContext().getPackageName()));
			adDay.setBackgroundResource(R.drawable.homemypageattendance_drawable_btn_dinosaur);
			adDay = (Button)findViewById(res.getIdentifier("home_mypage_attendance_id_btn" + 29, "id", getApplicationContext().getPackageName()));
			adDay.setBackgroundResource(R.drawable.homemypageattendance_drawable_btn_dinosaur);
			for(int i = 0; i < 30; i++){
				int id = res.getIdentifier("home_mypage_attendance_id_btn" + i, "id", getApplicationContext().getPackageName());
				adDay = (Button)findViewById(id);
				if(i % 10 != 9){
					adDay.setText(String.valueOf((i + 1) + ((divTen - 2) * 10)));
				}
				else{
					adDay.setText("");
				}
				if(i < calVal){
					adDay.setEnabled(true);
				}
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.home_my_page_attendance, menu);
		return false;
	}

	// on click
	public void onClickBack(View v)
	{
		finish();
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
