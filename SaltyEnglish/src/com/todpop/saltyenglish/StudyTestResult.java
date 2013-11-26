package com.todpop.saltyenglish;

import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;


import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class StudyTestResult extends Activity {

	ArrayList<MyItem> arItem;
	int count = 0;
	
 	// Database
 	WordDBHelper mHelper;

 	
 	static int currentStage;
	String resultScore;
	String resultReward;
	String resultMedal;
 	
	MyItem mi;

	TextView scoreView;
	TextView rewardView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_study_test_result);
		
		mHelper = new WordDBHelper(this);
		
		arItem = new ArrayList<MyItem>();
		
		scoreView = (TextView)findViewById(R.id.lvtest_result_id_level);
		rewardView = (TextView)findViewById(R.id.study_test_result_id_score_view);
		//level.setText(resultScore + " " +  getResources().getString(R.string.study_result_score_text));
		
		
		// Database to store words for My Word list
		SQLiteDatabase db = mHelper.getWritableDatabase();
		try {
			db.execSQL("CREATE TABLE mywords ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
					"name TEXT NOT NULL UNIQUE, mean TEXT);");
		} catch (Exception e) {
			
		}
		

			
			// Save to Flip DB
//			ContentValues cv = new ContentValues();
//			cv.put("name", englishWords.get(i));
//			cv.put("mean", englishMeans.get(i));
//			cv.put("xo", "X");
//			db.insert("flip", null, cv);
		
//		for(int i=0;i<20;i++)
//		{
//			mi = new MyItem(pref.getString("enWord"+i, "N"),pref.getString("krWord"+i, "N"),pref.getString("check"+i, "N"));
//			arItem.add(mi);
//		}
		
		
		
//			mi = new MyItem("i",R.string.kr1,"Y");
//			arItem.add(mi);
//			mi = new MyItem("few",R.string.kr2,"N");
//			arItem.add(mi);
//			mi = new MyItem("building",R.string.kr3,"Y");
//			arItem.add(mi);
//			mi = new MyItem("million",R.string.kr4,"Y");
//			arItem.add(mi);
//			mi = new MyItem("sunny",R.string.kr5,"Y");
//			arItem.add(mi);
//			mi = new MyItem("straight",R.string.kr6,"N");
//			arItem.add(mi);
//			mi = new MyItem("ring",R.string.kr7,"Y");
//			arItem.add(mi);
//			mi = new MyItem("today",R.string.kr8,"Y");
//			arItem.add(mi);
//			mi = new MyItem("too",R.string.kr9,"N");
//			arItem.add(mi);
//			mi = new MyItem("oh",R.string.kr10,"Y");
//			arItem.add(mi);
//		
		// Get Test result from database
		
		SharedPreferences levelInfoSp = getSharedPreferences("StudyLevelInfo", 0);
		currentStage = levelInfoSp.getInt("currentStage", 1);
		getTestWords();
		
		
		if (currentStage%10 != 0) {
			// ----------- Request Result -------------
			SharedPreferences pref = getSharedPreferences("rgInfo",0);
			// levelCount could be 1, 16, 61, 121 etc... 
			int category = pref.getInt("categoryStage", 1);
			String userId = pref.getString("mem_id", "1");
			SharedPreferences levelPref = getSharedPreferences("StudyLevelInfo",0);
			String finalAnswerForRequest = levelPref.getString("testResult", "");
			
			String resultUrl = "http://todpop.co.kr/api/studies/send_word_result.json?level=" + ((currentStage-1)/10+1) + 
					"&stage=" + currentStage%10 + "&result=" + finalAnswerForRequest + "&count=10&user_id=" + userId + "&category=" + category;
			Log.d("-------- result url ------- ", resultUrl);
			new GetTestResult().execute(resultUrl);
			// ----------- End of  Request Result -------------
		} else {
			// ----------- Request Result -------------
			SharedPreferences pref = getSharedPreferences("rgInfo",0);
			// levelCount could be 1, 16, 61, 121 etc... 
			int category = pref.getInt("categoryStage", 1);
			String userId = pref.getString("mem_id", "1");
			SharedPreferences levelPref = getSharedPreferences("StudyLevelInfo",0);
			String finalAnswerForRequest = levelPref.getString("testResult", "");
			
			String resultUrl = "http://todpop.co.kr/api/studies/send_word_result.json?level=" + ((currentStage-1)/10+1) + 
					"&stage=" + 10 + "&result=" + finalAnswerForRequest + "&count=36&user_id=" + userId + "&category=" + category;
			Log.d("-------- result url ------- ", resultUrl);
			new GetTestResult().execute(resultUrl);
			// ----------- End of  Request Result -------------
		}


		
		MyListAdapter MyAdapter = new MyListAdapter(this,R.layout.lvtest_result_list_item_view, arItem);
		
		
		ListView MyList;
		MyList=(ListView)findViewById(R.id.lvtestresult_id_listview);
		MyList.setAdapter(MyAdapter);
		
		
		// Update Counts
		SharedPreferences sp = getSharedPreferences("StudyLevelInfo", 0);
		SharedPreferences.Editor editor = sp.edit();
		int totalStage = sp.getInt("totalStage", 1);
		int currentStage = sp.getInt("currentStage", 1);


		
		if ((currentStage+1) > totalStage) {
			
			editor.putInt("currentStage", (currentStage+1));			
			editor.putInt("totalStage", (currentStage+1));
			editor.commit();
			
			SharedPreferences pref = getSharedPreferences("rgInfo",0);
			SharedPreferences.Editor editor2 = pref.edit();
			
			int testLevel = currentStage/10+1;
			int savedLevel = Integer.parseInt(pref.getString("level", "1"));
			
			if (testLevel > savedLevel) {
				editor2.putString("level", Integer.toString(testLevel));
				String levelLabel = "Level"+testLevel;
				editor.putInt(levelLabel, 1);
			} else {
				String levelLabel = "Level"+(testLevel);
				int levelInt = sp.getInt(levelLabel, 1)+1;
				editor.putInt(levelLabel, levelInt);
			}
			
			editor.commit();
			editor2.commit();
		} else {
			int nextStage = currentStage+1;
			int testLevel = (nextStage-1)/10+1;
			int stageCount = nextStage%10;
			if (stageCount ==0){
				stageCount = 10;
			}
			
			String levelLabel = "Level"+(testLevel);
			int levelInt = sp.getInt(levelLabel, 1);
			if (stageCount > levelInt) {
				editor.putInt(levelLabel, stageCount);
				editor.commit();
			}
		}
	}
	
	private class GetTestResult extends AsyncTask<String, Void, JSONObject> 
	{
		DefaultHttpClient httpClient ;
		@Override
		protected JSONObject doInBackground(String... urls) 
		{
			JSONObject result = null;
			try {
				String getURL = urls[0];
				HttpGet httpGet = new HttpGet(getURL); 
				HttpParams httpParameters = new BasicHttpParams(); 
				int timeoutConnection = 5000; 
				HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection); 
				int timeoutSocket = 5000; 
				HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket); 

				httpClient = new DefaultHttpClient(httpParameters); 
				HttpResponse response = httpClient.execute(httpGet); 
				HttpEntity resEntity = response.getEntity();

				if (resEntity != null) {    
					result = new JSONObject(EntityUtils.toString(resEntity)); 
					//Log.d("RESPONSE ---- ", result.toString());				        	
				}
				return result;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		}

		@Override
		protected void onPostExecute(JSONObject json) {
			try {
				Log.d("Get Result JSON RESPONSE ---- ", json.toString());				        	

				if(json.getBoolean("status")==true) {
					try {
						JSONObject resultObj = json.getJSONObject("data");
						resultScore = resultObj.getString("score");
						resultReward = resultObj.getString("reward");
						resultMedal = resultObj.getString("medal");
					
						rewardView.setText(resultReward);
						scoreView.setText(resultScore + getResources().getString(R.string.study_result_score_text));
						
					} catch (Exception e) {
						e.printStackTrace();
					}


				}else{		    
				}

			} catch (Exception e) {
				Log.d("Exception: ", e.toString());
			}
		}
	}
	

	private void getTestWords()
	{
		
		try {
			SQLiteDatabase db = mHelper.getReadableDatabase();

			if (currentStage%10 == 0) {
				Cursor cursor = db.rawQuery("SELECT name, mean, xo FROM flip;", null);
				if (cursor.getCount() > 0) {
					while(cursor.moveToNext()) {
						Log.d("D E F ------", cursor.getString(0) + "  " + cursor.getString(1) + "   " + cursor.getString(2));
						mi = new MyItem(cursor.getString(0), cursor.getString(1), cursor.getString(2));
						arItem.add(mi);
					}
					
					db.delete("flip", null, null);
				}


			} else {
				Cursor cursor = db.rawQuery("SELECT name, mean, xo FROM dic WHERE stage=" + currentStage + ";", null);
				if (cursor.getCount() > 0) {
					while(cursor.moveToNext()) {
						Log.d("A B C ------", cursor.getString(0) + "  " + cursor.getString(1) + "   " + cursor.getString(2));
						mi = new MyItem(cursor.getString(0), cursor.getString(1), cursor.getString(2));
						arItem.add(mi);
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		


	}
	
	class MyItem{
		MyItem(String aEn,String aKr,String Check)
		{
			en = aEn;
			kr = aKr;
			check =Check;
		}
		String en;
		String kr;
		String check;
	}

	class MyListAdapter extends BaseAdapter
	{
		Context maincon;
		LayoutInflater Inflater;
		ArrayList<MyItem> arSrc;
		int layout;
		
		public MyListAdapter(Context context,int alayout,ArrayList<MyItem> aarSrc)
		{
			maincon = context;
			Inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			arSrc = aarSrc;
			layout = alayout;
		}
		public int getCount()
		{
			
			return arSrc.size();
		}
		
		public String getItem(int position)
		{
			return arSrc.get(position).en;
		}
		
		public long getItemId(int position)
		{
			return position;
		}
		
		public View getView(int position,View convertView,ViewGroup parent)
		{
			count++;
			if(convertView == null){
				convertView = Inflater.inflate(layout, parent,false);
			}
			
			TextView textEn = (TextView)convertView.findViewById(R.id.lv_test_english);
			textEn.setText(arSrc.get(position).en);
			
			TextView textKr = (TextView)convertView.findViewById(R.id.lv_test_kr);
			textKr.setText(arSrc.get(position).kr);
			
			ImageView checkView = (ImageView)convertView.findViewById(R.id.lv_test_check_correct);
			//Button itemBtn = (Button)convertView.findViewById(R.id.lv_test_btn);
			
			if(arSrc.get(position).check.equals("O")) {
				checkView.setImageResource(R.drawable.lvtest_10_text_correct);
				//itemBtn.setBackgroundResource(R.drawable.lvtest_10_btn_pencil_on);
			} else {
				checkView.setImageResource(R.drawable.lvtest_10_text_incorrect);
				//itemBtn.setBackgroundResource(R.drawable.lvtest_10_btn_pencil_off);
			}
			
//			if (count ==1) {
//				convertView.setBackgroundResource(R.drawable.lvtest_10_image_separatebox_blue_first);
//			} else if (count == 20) {
//				convertView.setBackgroundResource(R.drawable.lvtest_10_image_separatebox_blue_end);
//			} else {
//				if (count%2 == 1) {
//					convertView.setBackgroundResource(R.drawable.lvtest_10_image_separatebox_blue_center);
//				} else {
//					convertView.setBackgroundResource(R.drawable.lvtest_10_image_separatebox_skyblue_center);
//				}
//			}
			
			if (count%2 == 1) {
				convertView.setBackgroundResource(R.drawable.lvtest_10_image_separatebox_blue_center);
			} else {
				convertView.setBackgroundResource(R.drawable.lvtest_10_image_separatebox_skyblue_center);
			}
			
			CheckBox wordListCB = (CheckBox)convertView.findViewById(R.id.lv_test_btn);
			wordListCB.setTag(position);
			
			// Check if word is in word list
			try {
	    		SQLiteDatabase db = mHelper.getWritableDatabase();
	    		Cursor c = db.rawQuery("SELECT * FROM mywords WHERE name='" + arSrc.get(position).en + "'" , null);
	    		if (c.getCount() > 0) {
	    			wordListCB.setChecked(true);
	    		} else {
	    			wordListCB.setChecked(false);
	    		}
			} catch (Exception e) {

			}

    		
			wordListCB.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                	try {
                        if (isChecked) {
                        	// Insert word to DB
                    		SQLiteDatabase db = mHelper.getWritableDatabase();

                			ContentValues cv = new ContentValues();
                			cv.put("name", arSrc.get((Integer)(buttonView.getTag())).en);
                			cv.put("mean", arSrc.get((Integer)(buttonView.getTag())).kr);
                			db.replace("mywords", null, cv);
                        } else {
                        	// Delete word to DB
                    		SQLiteDatabase db = mHelper.getWritableDatabase();       
                    		try {
                        		db.delete("mywords", "name='" + arSrc.get((Integer)(buttonView.getTag())).en+"'", null);
                    		} catch(Exception e) {
                    			e.printStackTrace();
                    		}
                        }
                	} catch (Exception e) {
                		e.printStackTrace();
                	}

                }
            });
			
			return convertView;
		}
			
		
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) 
		{
			final AlertDialog.Builder isExit = new AlertDialog.Builder(this);

			DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() 
			{
				public void onClick(DialogInterface dialog, int which) 
				{
					switch (which) 
					{
					case AlertDialog.BUTTON_POSITIVE:
						SharedPreferences settings = getSharedPreferences("setting", 0);
						SharedPreferences.Editor editor = settings.edit();
						editor.putString("check","YES");
						editor.commit();
						
						Intent intent = new Intent();
				        intent.setClass(StudyTestResult.this, MainActivity.class);    
				        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
				        startActivity(intent);
						finish();	

						break;
					case AlertDialog.BUTTON_NEGATIVE:
						break;
					default:
						break;
					}
				}
			};

			isExit.setTitle(getResources().getString(R.string.register_alert_title));
			isExit.setMessage(getResources().getString(R.string.register_alert_text));
			isExit.setPositiveButton("OK", listener);
			isExit.setNegativeButton("Cancel", listener);
			isExit.show();

			return false;
		}
		return false;
	}
	
	public void showStudyLearnActivity(View v)
	{
		finish();
	}
	
	public void showHomeActivity(View v)
	{
		Intent intent = new Intent(getApplicationContext(), StudyHome.class);
		startActivity(intent);
		finish();
	}
	
	
	//------- Database Operation ------------------
	private class WordDBHelper extends SQLiteOpenHelper {
		public WordDBHelper(Context context) {
			super(context, "EngWord.db", null, 1);
		}
		
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE dic ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
		"name TEXT, mean TEXT, example_en TEXT, example_ko TEXT, phonetics TEXT, picture INTEGER, image_url TEXT, stage INTEGER, xo TEXT);");
		}
		
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS dic");
			db.execSQL("DROP TABLE IF EXISTS flip");
			onCreate(db);
		}
	}


	@Override
	public void onDestroy()
	{
		super.onDestroy();
		mHelper.close();
	}
}
