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

import android.content.ContentValues;
import android.content.Context;
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
import com.todpop.saltyenglish.db.WordDBHelper;

public class StudyTestCookieResult extends TypefaceActivity {

	ArrayList<MyItem> arItem;
	int count = 0;

	// Database
	WordDBHelper mHelper;

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

	LinearLayout popupBoth;
	LinearLayout popupBothNoAtt;	//w/o attendance reward
	TextView popupBothNoAttReward;
	TextView popupBothNoAttPoint;
	LinearLayout popupBothAtt;	//w/ attendance reward
	TextView popupBothAttReward;
	TextView popupBothAttPoint;
	TextView popupAttReward; //reward for attendance
	TextView popupAttPoint; //point for attendance

	LinearLayout popupOnly;
	ImageView popupOnlyTitle;
	ImageView popupOnlyType;
	ImageView popupOnlyImg;
	TextView popupOnlyAmount;

	//loading progress dialog
	LoadingDialog loadingDialog;

	SharedPreferences studyInfo;
	SharedPreferences.Editor studyInfoEdit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_study_test_result);

		studyInfo = getSharedPreferences("studyInfo",0);
		studyInfoEdit = studyInfo.edit();

		mHelper = new WordDBHelper(this);

		arItem = new ArrayList<MyItem>();

		scoreView = (TextView)findViewById(R.id.lvtest_result_id_level);

		studyInfo = getSharedPreferences("studyInfo", 0);
		int score = studyInfo.getInt("cookieTestScore", 0);
		//		scoreView.setText(score+"쩜");

		rewardView = (TextView)findViewById(R.id.study_test_result_id_score_view);
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
		tmpStageAccumulated = studyInfo.getInt("tmpStageAccumulated", 1);
		getTestWords();

		// ----------- Request Result -------------
		SharedPreferences pref = getSharedPreferences("rgInfo",0);
		// levelCount could be 1, 16, 61, 121 etc... 
		int category = studyInfo.getInt("tmpCategory", 1);
		String userId = pref.getString("mem_id", "0");
		SharedPreferences levelPref = getSharedPreferences("StudyLevelInfo",0);
		String testComboResult = levelPref.getString("testComboResult", "");
		int testCntCorrect = levelPref.getInt("testCntCorrect", 0);
		int testCntWrong = levelPref.getInt("testCntWrong", 0);
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

		relative = (RelativeLayout)findViewById(R.id.test_result_id_main);
		popupView = View.inflate(this, R.layout.popup_view_test_result, null);
		popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);

		popupBoth = (LinearLayout)popupView.findViewById(R.id.popup_test_result_id_both);
		// w/o attendace
		popupBothNoAtt = (LinearLayout)popupView.findViewById(R.id.popup_test_result_id_both_no_attendance);
		popupBothNoAttReward = (TextView)popupView.findViewById(R.id.popup_test_result_id_both_no_attendance_reward);
		popupBothNoAttPoint = (TextView)popupView.findViewById(R.id.popup_test_result_id_both_no_attendance_point);

		setFont(popupBothNoAttReward);
		setFont(popupBothNoAttPoint);

		// w/ attendace
		popupBothAtt = (LinearLayout)popupView.findViewById(R.id.popup_test_result_id_both_attendance);
		popupBothAttReward = (TextView)popupView.findViewById(R.id.popup_test_result_id_both_attendance_reward);
		popupBothAttPoint = (TextView)popupView.findViewById(R.id.popup_test_result_id_both_attendance_point);
		popupAttReward = (TextView)popupView.findViewById(R.id.popup_test_result_id_attendance_reward);
		popupAttPoint = (TextView)popupView.findViewById(R.id.popup_test_result_id_attendance_point);

		setFont(popupBothAttReward);
		setFont(popupBothAttPoint);
		setFont(popupAttReward);
		setFont(popupAttPoint);

		popupOnly = (LinearLayout)popupView.findViewById(R.id.popup_test_result_id_only);
		popupOnlyTitle = (ImageView)popupView.findViewById(R.id.popup_test_result_id_only_title);
		popupOnlyType = (ImageView)popupView.findViewById(R.id.popup_test_result_id_only_type);
		popupOnlyImg = (ImageView)popupView.findViewById(R.id.popup_test_result_id_only_image);
		popupOnlyAmount = (TextView)popupView.findViewById(R.id.popup_test_result_id_only_amount);

		setFont(popupOnlyAmount);

		resultUrl = "http://todpop.co.kr/api/studies/send_word_result.json?level=" + ((tmpStageAccumulated-1)/10+1) + 
				"&stage=" + tmpStageAccumulated%10 + "&result=" + testCntCorrect + "&count="+ (testCntCorrect+testCntWrong) +
				"&user_id=" + userId + "&category=" + category+"&combo="+testComboResult;
		

		loadingDialog = new LoadingDialog(this);
		loadingDialog.show();
		new GetTestResult().execute(resultUrl);
		Log.e("-------- result url ------- ", resultUrl);
		// ----------- End of  Request Result -------------

		MyListAdapter MyAdapter = new MyListAdapter(this,R.layout.lvtest_result_list_item_view, arItem);

		ListView MyList;
		MyList=(ListView)findViewById(R.id.lvtestresult_id_listview);
		MyList.setAdapter(MyAdapter);
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
				loadingDialog.dissmiss();
				Log.e("Get Result JSON RESPONSE ---- ", json.toString());				        	

				if(json.getBoolean("status")==true) {
					try {
						JSONObject resultObj = json.getJSONObject("data");
						Log.e("Results!!! = ",resultObj.toString());
						resultScore = resultObj.getString("score");
						resultReward = resultObj.getString("reward");
						resultPoint = resultObj.getString("rank_point");
						resultMedal = resultObj.getString("medal");

						resultAttendReward = resultObj.getString("attend_reward");
						resultAttendPoint = resultObj.getString("attend_point");

						String stageInfo = resultObj.getString("stage_info");		// stageInfo
						studyInfoEdit.putString("stageInfo", stageInfo);
						studyInfoEdit.apply();

						rewardView.setText(resultReward);
						scoreView.setText(resultScore + getResources().getString(R.string.study_result_score_text));

						if(resultAttendReward.equals("null")){
							if(resultReward.equals("0")){
								if(resultPoint.equals("0")){
									//no popup
								}
								else{
									popupBoth.setVisibility(View.GONE);
									popupOnly.setVisibility(View.VISIBLE);
									popupOnlyTitle.setBackgroundResource(R.drawable.study_popup_3_img_title);
									popupOnlyType.setBackgroundResource(R.drawable.study_popup_1_text_point);
									popupOnlyImg.setBackgroundResource(R.drawable.study_popup_common_img_bigpoint);
									popupOnlyAmount.setText("+" + resultPoint);
									popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
								}
							}
							else if(resultPoint.equals("0")){
								popupBoth.setVisibility(View.GONE);
								popupOnly.setVisibility(View.VISIBLE);
								popupOnlyTitle.setBackgroundResource(R.drawable.study_popup_4_img_title);
								popupOnlyType.setBackgroundResource(R.drawable.study_popup_1_text_money);
								popupOnlyImg.setBackgroundResource(R.drawable.study_popup_common_img_bigcoin);
								popupOnlyAmount.setText("+" + resultReward);
								popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
							}
							else{
								popupBoth.setVisibility(View.VISIBLE);
								popupOnly.setVisibility(View.GONE);
								popupBothAtt.setVisibility(View.GONE);
								popupBothNoAtt.setVisibility(View.VISIBLE);
								popupBothNoAttReward.setText("+" + resultReward);
								popupBothNoAttPoint.setText("+" + resultPoint);
								popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
							}
						}
						else{
							popupBoth.setVisibility(View.VISIBLE);
							popupOnly.setVisibility(View.GONE);
							popupBothAtt.setVisibility(View.VISIBLE);
							popupBothNoAtt.setVisibility(View.GONE);

							popupBothAttReward.setText("+" + resultReward);
							popupBothAttPoint.setText("+" + resultPoint);
							popupAttReward.setText("+ " + resultAttendReward);
							popupAttPoint.setText("+ " + resultAttendPoint);
							popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
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

			Cursor cursor = db.rawQuery("SELECT DISTINCT name, mean, xo FROM dic WHERE stage=" + tmpStageAccumulated + ";", null);

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

			setFont(textEn);
			setFont(textKr);

			ImageView checkView = (ImageView)convertView.findViewById(R.id.lv_test_check_correct);
			//Button itemBtn = (Button)convertView.findViewById(R.id.lv_test_btn);

			if(arSrc.get(position).check.equals("O")) {
				checkView.setImageResource(R.drawable.lvtest_10_text_correct);
				//itemBtn.setBackgroundResource(R.drawable.lvtest_10_btn_pencil_on);
			} else {
				checkView.setImageResource(R.drawable.lvtest_10_text_incorrect);
				//itemBtn.setBackgroundResource(R.drawable.lvtest_10_btn_pencil_off);
			}

			if (count%2 == 1) {
				convertView.setBackgroundResource(R.drawable.weekly_2_img_saparatebox_1);
			} else {
				convertView.setBackgroundResource(R.drawable.weekly_2_img_saparatebox_2);
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
			finish();
		}
		return false;
	}

	public void showStudyLearnActivity(View v)
	{
		finish();
	}

	public void showHomeActivity(View v)
	{
		goHome();
	}
	private void goHome(){
		SharedPreferences pref = getSharedPreferences("rgInfo",0);
		String userId = pref.getString("mem_id", "1");
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
