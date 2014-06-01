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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;
import com.todpop.api.LoadingDialog;
import com.todpop.api.TypefaceActivity;
import com.todpop.saltyenglish.db.LockerDBHelper;
import com.todpop.saltyenglish.db.WordDBHelper;

public class StudyTestMockResult extends TypefaceActivity {

	ArrayList<MyItem> arItem;
	int count = 0;

	// Database
	WordDBHelper mHelper;
	
	static int cntWords=0;
	static int cntRightWords=0;
	static int score=0;
	
	int tmpStageAccumulated;
	String resultScore;
	String resultReward;
	String resultPoint;
	String resultAttendReward;
	String resultAttendPoint;
	String resultMedal;

	MyItem mi;

	TextView scoreView;
	TextView rewardView;

	//popup view
	RelativeLayout relative;
	View popupView;
	PopupWindow popupWindow;

	//loading progress dialog
	LoadingDialog loadingDialog;

	SharedPreferences studyInfo;
	SharedPreferences.Editor studyInfoEdit;
	int ad_id;
	SharedPreferences rgInfo;
	private TextView popupTvAnswerPercentage;
	private LinearLayout popupLayoutReward;
	private TextView popupReward;
	private LinearLayout popupLayoutPoint;
	private TextView popupPoint;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_study_test_mock_result);


		rgInfo = getSharedPreferences("rgInfo",0);
		
		studyInfo = getSharedPreferences("studyInfo",0);
		studyInfoEdit = studyInfo.edit();

		mHelper = new WordDBHelper(this);

		arItem = new ArrayList<MyItem>();

		scoreView = (TextView)findViewById(R.id.lvmocktest_result_id_level);
		rewardView = (TextView)findViewById(R.id.study_mock_test_result_id_score_view);
		//level.setText(resultScore + " " +  getResources().getString(R.string.study_result_score_text));


		// Database to store words for My Word list
		SQLiteDatabase db = mHelper.getWritableDatabase();
		try {
			db.execSQL("CREATE TABLE mywords ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
					"name TEXT NOT NULL UNIQUE, mean TEXT);");
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Get Test result from database
		SharedPreferences studyInfo = getSharedPreferences("studyInfo", 0);
		tmpStageAccumulated = studyInfo.getInt("tmpStageAccumulated", 1);
		getTestWords();
		score = (int) (( ((float)cntRightWords/cntWords)*100 )); //rounded
		scoreView.setText(score + getResources().getString(R.string.study_result_score_text));
		// ----------- Request Result -------------
		SharedPreferences pref = getSharedPreferences("rgInfo",0);
		// levelCount could be 1, 16, 61, 121 etc... 
		int category = studyInfo.getInt("tmpCategory", 1);
		String userId = pref.getString("mem_id", "0");
		SharedPreferences levelPref = getSharedPreferences("StudyLevelInfo",0);
		String finalAnswerForRequest = levelPref.getString("testResult", "");
		String resultUrl;

		// ------- cys added -----------
		studyInfoEdit.putInt("currentCategory", category);
		studyInfoEdit.putInt("currentStageAccumulated", tmpStageAccumulated);		// save tmpStage -> currentStage
		int level = ((tmpStageAccumulated-1)/10) + 1;
		if(category==1)			{studyInfoEdit.putInt("levelLast1", level);Log.e("STR1",String.valueOf(level));}
		else if(category==2)	{studyInfoEdit.putInt("levelLast2", level);Log.e("STR2",String.valueOf(level));}
		else if(category==3)	{studyInfoEdit.putInt("levelLast3", level);Log.e("STR3",String.valueOf(level));}
		else					{studyInfoEdit.putInt("levelLast4", level);Log.e("STR4",String.valueOf(level));}
		studyInfoEdit.apply();
		// ----------------------------

		relative = (RelativeLayout)findViewById(R.id.mock_test_result_id_main);
		popupView = View.inflate(this, R.layout.popup_view_test_mock_result, null);
		popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
		
		popupTvAnswerPercentage = (TextView)popupView.findViewById(R.id.popup_mock_test_answer_percentage);
		
		popupLayoutReward = (LinearLayout)popupView.findViewById(R.id.popup_mock_test_result_reward_layout);
		popupReward = (TextView)popupView.findViewById(R.id.popup_mock_test_result_reward);
		
		popupLayoutPoint = (LinearLayout)popupView.findViewById(R.id.popup_mock_test_result_point_layout);
		popupPoint= (TextView)popupView.findViewById(R.id.popup_mock_test_result_point);
		
		setFont(popupReward);
		setFont(popupPoint);

		loadingDialog = new LoadingDialog(this);
		loadingDialog.show();

		SQLiteDatabase locker_db = new LockerDBHelper(getApplicationContext()).getReadableDatabase();
		Cursor cursor = locker_db.rawQuery("SELECT * FROM latest where category=413 order by id asc", null);
		if(cursor.moveToLast())
			ad_id = cursor.getInt(2); //id    ad_id
		String setTestLogUrl = "http://www.todpop.co.kr/api/screen_lock/exam_words.json?";
		setTestLogUrl += "ad_id="+ad_id+"&";
		setTestLogUrl += "user_id="+rgInfo.getString("mem_id", null)+"&";
		setTestLogUrl += "score="+ score;
		
		new SetTestLog().execute(setTestLogUrl);
	
		

		MyListAdapter MyAdapter = new MyListAdapter(this,R.layout.lvtest_result_list_item_view, arItem);

		ListView MyList;
		MyList=(ListView)findViewById(R.id.lvmocktestresult_id_listview);
		MyList.setAdapter(MyAdapter);
	}

	private class SetTestLog extends AsyncTask<String, Void, JSONObject> 
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
				loadingDialog.dissmiss();
				Log.d("Get Result JSON RESPONSE ---- ", json.toString());				        	

				if(json.getBoolean("status")==true) {
					try {
						int reward=0,point=0;
						if(json.has("reward"))
							reward = json.getInt("reward");
						if(json.has("point"))
							point = json.getInt("point");
						
						rewardView.setText(reward+"");
						// reward and point
						
						popupTvAnswerPercentage.setText(score+"");
						if(reward > 0 && point > 0)
						{
							popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
							popupPoint.setText("+ "+point);
							popupReward.setText("+ "+reward);
							Log.e("MockTestResult","Both");
						}
						// reward only
						else if(reward > 0)
						{
							popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
							popupReward.setText("+ "+reward);
							
							popupLayoutPoint.setVisibility(View.GONE);
							Log.e("MockTestResult","reward");	
						}
						//point only
						else if(point > 0)
						{
							popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
							popupPoint.setText("+ "+point);

							popupLayoutReward.setVisibility(View.GONE);
							Log.e("MockTestResult","point");
						}
						// nothing
						else
						{
							popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
							
							popupLayoutReward.setVisibility(View.GONE);
							popupLayoutPoint.setVisibility(View.GONE);
							Log.e("MockTestResult","nothing");
							// don't show popup page
						}
						
						
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
	public void dismissPopup(View v){
		popupWindow.dismiss();
	}

	private void getTestWords()
	{

		try {
			SQLiteDatabase db = mHelper.getReadableDatabase();

			Cursor cursor = db.rawQuery("SELECT name, mean, xo FROM dic WHERE stage=" + tmpStageAccumulated + ";", null);

			Log.e("cursor.getCount()", "cursor.getCount() : "+cursor.getCount());
			if (cursor.getCount() > 0) {
				while(cursor.moveToNext()) {
					Log.e("A B C ------", cursor.getString(0) + "  " + cursor.getString(1) + "   " + cursor.getString(2));
					mi = new MyItem(cursor.getString(0), cursor.getString(1), cursor.getString(2));
					arItem.add(mi);
				}
			}
		} catch (Exception e) {
			Log.e("AFDSDFDSFSDFDSF", "catch error");
			e.printStackTrace();
		}



	}

	class MyItem{
		MyItem(String aEn,String aKr,String Check)
		{
			++cntWords;
			en = aEn;
			kr = aKr;
			if(Check == null) 
				check="X";
			else{
				check = Check;
				++cntRightWords;
			}

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
			
			setFont(textEn);
			setFont(textKr);

			ImageView checkView = (ImageView)convertView.findViewById(R.id.lv_test_check_correct);
			CheckBox itemBtn = (CheckBox)convertView.findViewById(R.id.lv_test_btn);
			itemBtn.setChecked(((ListView)parent).isItemChecked(position));
			//itemBtn.setBackgroundResource(R.drawable.lvtest_10_btn_pencil_off);
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
						editor.apply();

						Intent intent = new Intent();
						intent.setClass(StudyTestMockResult.this, MainActivity.class);    
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
		Intent intent=new Intent(getApplicationContext(),MainActivity.class);
		startActivity(intent);
		finish();
	}

	public void showHomeActivity(View v)
	{
		String userId = rgInfo.getString("mem_id", "1");
		String cpxRequestUrl = "http://todpop.co.kr/api/advertises/get_cpx_ad.json?user_id="+userId;
		Log.d("CPX URL ---- ", cpxRequestUrl);
		new GetCPX().execute(cpxRequestUrl);
	}

	// Get CPX - here we get CPI first
	private class GetCPX extends AsyncTask<String, Void, JSONObject> 
	{
		@Override
		protected JSONObject doInBackground(String... urls) 
		{
			JSONObject result = null;
			try {
				DefaultHttpClient httpClient = new DefaultHttpClient();
				String getURL = urls[0];
				HttpGet httpGet = new HttpGet(getURL);
				HttpResponse httpResponse = httpClient.execute(httpGet);
				HttpEntity resEntity = httpResponse.getEntity();

				if (resEntity != null) {    
					result = new JSONObject(EntityUtils.toString(resEntity)); 
					Log.d("CPX RESPONSE ---- ", result.toString());				        	
				}
				return result;
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		}

		@Override
		protected void onPostExecute(JSONObject json) {

			try {
				//TODO cpx function to be remove
				if(json.getBoolean("status")==true) {
					JSONObject adDetails = json.getJSONObject("data");
					int adId = adDetails.getInt("ad_id");
					int adType = adDetails.getInt("ad_type");
					Log.d("CPX Type: ---------- ", Integer.toString(adType));

					String adImageUrl = "http://todpop.co.kr/" + adDetails.getString("ad_image");
					String adText = adDetails.getString("ad_text");
					String adAction = adDetails.getString("ad_action");
					String targetUrl = adDetails.getString("target_url");
					String packageName = adDetails.getString("package_name");
					String confirmUrl = adDetails.getString("confirm_url");
					String reward = adDetails.getString("reward");
					String point = adDetails.getString("point");
					int questionCount = adDetails.getInt("n_question");

					SharedPreferences cpxInfo = getSharedPreferences("cpxInfo",0);
					SharedPreferences.Editor cpxInfoEditor = cpxInfo.edit();
					cpxInfoEditor.putInt("adId", adId);					
					cpxInfoEditor.putInt("adType", adType);		
					cpxInfoEditor.putString("adImageUrl", adImageUrl);
					cpxInfoEditor.putString("adText", adText);
					cpxInfoEditor.putString("adAction", adAction);
					cpxInfoEditor.putString("targetUrl", targetUrl);
					cpxInfoEditor.putString("packageName", packageName);
					cpxInfoEditor.putString("confirmUrl", confirmUrl);
					cpxInfoEditor.putString("reward", reward);
					cpxInfoEditor.putString("point", point);
					cpxInfoEditor.putInt("questionCount", questionCount);

					cpxInfoEditor.apply();

					// TODO: Add more CPX Support. Now only support CPI and CPS

					Intent intent = new Intent(getApplicationContext(), StudyHome.class);
					startActivity(intent);
					finish();

				} else {		   
					// In the case CPX Request Failed
					Intent intent = new Intent(getApplicationContext(), StudyHome.class);
					startActivity(intent);
					finish();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		mHelper.close();
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
