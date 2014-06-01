package com.todpop.saltyenglish;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.ImageView;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;
import com.todpop.api.TypefaceActivity;
import com.todpop.saltyenglish.db.LockerDBHelper;
import com.todpop.saltyenglish.db.WordDBHelper;

public class StudyTestMockCnt extends TypefaceActivity {

	SharedPreferences rgInfo;
	LockerDBHelper lHelper;
	SQLiteDatabase db;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_study_test_mock_cnt);

		int ad_id=-1;
		rgInfo = getApplicationContext().getSharedPreferences("rgInfo",0);
		lHelper=new LockerDBHelper(getApplicationContext());
		db = lHelper.getReadableDatabase();
		Log.e("Jun Test1","before");
		Cursor cursor = db.rawQuery("SELECT * FROM latest where category=413 order by id asc", null);
		if(cursor.moveToLast())
			ad_id = cursor.getInt(2); //id    ad_id
		Log.e("Jun Test1","after");
		String url = "http://www.todpop.co.kr/api/screen_lock/exam_words.json?";
		url += "ad_id="+ad_id+"&";
		url += "user_id="+rgInfo.getString("mem_id", null);
		Log.e("Jun Test1","url");
		new GetMockTestWords().execute(url);

		new CountDownTimer(4000,1000) {
			int cur_cnt_img_id = R.drawable.test_3_img_second_3;
			ImageView iv_cnt_img_view = (ImageView)findViewById(R.id.iv_test_cntdown);
			@Override
			public void onTick(long millisUntilFinished) {
				iv_cnt_img_view.setBackgroundResource(cur_cnt_img_id--);
				if(cur_cnt_img_id == R.drawable.test_3_img_second_0)
				{
					Intent intent = new Intent(getApplicationContext(),StudyTestMock.class);
					startActivity(intent);
					finish();
				}
			}

			@Override
			public void onFinish() {
			}
		}.start();
	}

	@Override
	protected void onStart()
	{
		super.onStart();
		FlurryAgent.onStartSession(this, "ZKWGFP6HKJ33Y69SP5QY");
		FlurryAgent.logEvent("MockTest Countdown Activity");
		EasyTracker.getInstance(this).activityStart(this);
	}

	@Override
	protected void onStop()
	{
		super.onStop();		
		FlurryAgent.onEndSession(this);
		EasyTracker.getInstance(this).activityStop(this);
	}



	private class GetMockTestWords extends AsyncTask<String, Void, JSONObject> 
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
					Log.d("RESPONSE---- ", result.toString());				        	
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
		protected void onPostExecute(final JSONObject json) {
			if(json == null) {
			}

			try {
				new Thread(new Runnable() {

					@Override
					public void run() {
						try{
							db = new WordDBHelper(getApplicationContext()).getWritableDatabase();
							db.execSQL("DELETE FROM dic WHERE stage=-1");
							JSONArray word_arr=json.getJSONArray("list");
							for(int i=0;i < word_arr.length();i++)
							{
								JSONObject job = word_arr.getJSONObject(i);
								ContentValues row = new ContentValues();
								row.put("name", job.getString("word"));
								row.put("mean", job.getString("mean"));
								row.put("stage", -1); //stage -1 = mock test (tmp)
								db.insert("dic", null, row);
							}
							SharedPreferences studyInfo = getSharedPreferences("studyInfo", 0);
							Editor editor = studyInfo.edit();
							editor.putInt("tmpStageAccumulated", -1);
							editor.apply();
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
					}
				}).start();


				//		    	ContentValues row = new ContentValues();
				//				row.put("name", jsonWords.getJSONObject(i).get("name").toString());
				//				row.put("mean", jsonWords.getJSONObject(i).get("mean").toString());
				//				row.put("example_en", jsonWords.getJSONObject(i).get("example_en").toString());
				//				row.put("example_ko", jsonWords.getJSONObject(i).get("example_ko").toString());
				//				row.put("phonetics", jsonWords.getJSONObject(i).get("phonetics").toString());
				//				row.put("picture", (Integer)(jsonWords.getJSONObject(i).get("picture")));
				//				row.put("image_url", jsonWords.getJSONObject(i).get("image_url").toString());
				//				row.put("stage", tmpStageAccumulated);
				//				row.put("xo", "X");
				//
				//				mDB.insert("dic", null, row);



			} catch (Exception e) {

			}
		}
	}
}

