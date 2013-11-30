package com.todpop.saltyenglish;






import java.io.InputStream;
import java.net.URL;
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

import com.todpop.saltyenglish.LvTestResult.MyItem;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class StudyLearn extends FragmentActivity {

	// Slide Level Page
	ViewPager pageView;
	// PageAdapter for pageView
	PagerAdapter pagerAdapter;
	// Fragment attached to pageView
	LevelFragment levelFragment;

	static SharedPreferences prefs;
	static SharedPreferences.Editor ed;
	
	
	// TMP page number and stage information
	static int testLevel = 8;
	int stage = 0;
	
	//study category parameter
	Bundle bundle;
	static int levelCount;
	//page point index count
	int pageViewPointIndexCount;
	int currentViewCount;
	ImageView[] pointView;
	int fregmentCount = 1;
	
	static SharedPreferences checkStageIsNew;
	
	static String userId = "0";
	static int category = 0;
	
	// Stage Open Popup
	static PopupWindow popupWindow;
	static View popupview;
	static RelativeLayout relative;
	static TextView popupText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_study_learn);
		
		// User ID
		SharedPreferences rgInfo = getSharedPreferences("rgInfo",0);
		userId = rgInfo.getString("mem_id", "0");
		
		
		// Get level 
		SharedPreferences pref = getSharedPreferences("rgInfo",0);
		// levelCount could be 1, 16, 61, 121 etc... 
		category = pref.getInt("categoryStage", 1);
		if (category == 1)
			{levelCount = 1;}
		else if (category == 2)
			{levelCount = 16;}
		else if (category == 3)
			{levelCount = 61;}
		else						// category 4
			{levelCount = 121;}
		
		// Preference Study Level Info save all dynamic level info
		prefs = getSharedPreferences("StudyLevelInfo", MODE_PRIVATE);
		testLevel = (prefs.getInt("totalStage", 1)-1)/10+1;
		
		ed = prefs.edit();
		
		// For each level how many stages opened?
		for (int i=1; i<=testLevel; i++) {
			String levelCountStr = "Level" + Integer.toString(i);
			try {
				int origCount = prefs.getInt(levelCountStr, 1);
				ed.putInt(levelCountStr, origCount);
			} catch( Exception e) {
				ed.putInt(levelCountStr, 1);
			}
		}
		ed.commit();
		// ---- 

		// stage old and new info
		checkStageIsNew = getSharedPreferences("CheckStageIsNew",0);

		
		pageView = (ViewPager)findViewById(R.id.studylearn_id_pager);
		pagerAdapter = new PagerAdapter(getSupportFragmentManager());	
		pageView.setAdapter(pagerAdapter);
		
		// 1. Calculate Page Count for each Section. 
		if(levelCount ==1)
		{
			pageView.setCurrentItem((testLevel-1)/3);
			currentViewCount = (testLevel-1)/3;
			pageViewPointIndexCount = 5;
		} else if(levelCount == 16) {
			currentViewCount=(testLevel-16)/3;
			if (currentViewCount <0) 
				currentViewCount = 0;
			pageView.setCurrentItem(currentViewCount);
			pageViewPointIndexCount = 15;
		} else if(levelCount == 61) {
			currentViewCount=(testLevel-61)/3;
			if (currentViewCount <0) 
				currentViewCount = 0;
			pageView.setCurrentItem(currentViewCount);
			pageViewPointIndexCount = 20;
		} else if(levelCount == 121) {
			currentViewCount=(testLevel-121)/3;
			if (currentViewCount <0) 
				currentViewCount = 0;
			pageView.setCurrentItem(currentViewCount);
			pageViewPointIndexCount = 20;
		}
		
		
		pageView.setOnPageChangeListener(new OnPageChangeListener() {    
		     @Override public void onPageSelected(int position) {
		    	 changePointerViewIndicator(position);
		         
		     }
		    @Override public void onPageScrolled(int position, float positionOffest, int positionOffsetPixels) {}
		    @Override public void onPageScrollStateChanged(int state) {}
		});
		
		
		LinearLayout pointViewLayout=(LinearLayout)findViewById(R.id.studylearn_id_pagepointview);
		//LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		pointView = new ImageView[pageViewPointIndexCount];
 		for(int i = 0; i < pageViewPointIndexCount; i++) {
 			pointView[i] = new ImageView(this);

			if (i == currentViewCount) {
				pointView[i].setImageResource(R.drawable.study_6_image_indicator_pink_on);
			} else {
				pointView[i].setImageResource(R.drawable.study_6_image_indicator_pink_off);
			}
			pointView[i].setPadding(5, 5, 5, 5);
			pointViewLayout.addView(pointView[i]);
		}
		
		//popupview
		relative = (RelativeLayout)findViewById(R.id.study_learn_activity_id_relative);;
		popupview = View.inflate(this, R.layout.popup_view, null);
		float density = getResources().getDisplayMetrics().density;
		popupWindow = new PopupWindow(popupview,(int)(300*density),(int)(100*density),true);
		popupText = (TextView)popupview.findViewById(R.id.popup_id_text);
 		
		new SendPivotTime().execute("http://todpop.co.kr/api/app_infos/get_fast_pivot_time.json");
		
	}
	
	//--- request class ---
	private class SendPivotTime extends AsyncTask<String, Void, JSONObject> 
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
				httpClient = new DefaultHttpClient(httpParameters); 
				HttpResponse response = httpClient.execute(httpGet); 
				HttpEntity resEntity = response.getEntity();

				if (resEntity != null) {    
					result = new JSONObject(EntityUtils.toString(resEntity)); 
				}
				return result;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		}

		@Override
		protected void onPostExecute(JSONObject json) {
			if(json == null) {
				
			}

			try {
				int pivotTime = Integer.parseInt(json.getJSONObject("data").getString("time"));
				Log.d("------ SYSTEM PIVOT TIME ------", Integer.toString(pivotTime));
				
				SharedPreferences rgInfo = getSharedPreferences("rgInfo",0);
				SharedPreferences.Editor ed = rgInfo.edit();
				ed.putInt("pivotTime", pivotTime);
				ed.commit();
				
			} catch (Exception e) {

			}
		}
	}
	
	private void changePointerViewIndicator(int position) {
		for(int i = 0; i < pageViewPointIndexCount; i++) {
			if (i == position) {
				pointView[i].setImageResource(R.drawable.study_6_image_indicator_pink_on);
			} else {
				pointView[i].setImageResource(R.drawable.study_6_image_indicator_pink_off);
			}
		}
		
    	 
	}


	public  class PagerAdapter extends FragmentStatePagerAdapter {

		public PagerAdapter(FragmentManager fm) {
			super(fm);
		}
		@Override
		public Fragment getItem(int i) {

			Fragment fragment = new LevelFragment();
			Bundle args = new Bundle();

			args.putInt("page",i);

			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			

			
			if(levelCount ==1)
			{
				fregmentCount = 5;
			}else if(levelCount == 16)
			{
				fregmentCount = 15;
			}else if(levelCount == 61)
			{
				fregmentCount = 30;
			}
			else if(levelCount == 121)
			{
				fregmentCount = 20;
			}
			return fregmentCount;
		}
	}

	public static class LevelFragment extends Fragment {


		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_study_learn, container, false);



			TextView levelText1 = (TextView)rootView.findViewById(R.id.studylearn_id_level_text1);
			TextView levelText2 = (TextView)rootView.findViewById(R.id.studylearn_id_level_text2);
			TextView levelText3 = (TextView)rootView.findViewById(R.id.studylearn_id_level_text3);

			Bundle args = getArguments();
			
			int labelNumber = levelCount + 3*(args.getInt("page"));

			levelText1.setText("level  "+Integer.toString(labelNumber));
			levelText2.setText("level  "+Integer.toString(labelNumber+1));
			levelText3.setText("level  "+Integer.toString(labelNumber+2));

			adjustLevelStageButtonStatus(rootView, labelNumber);


			//((TextView) rootView.findViewById(android.R.id.text1)).setText(
			//        Integer.toString(args.getInt(ARG_OBJECT)));
			return rootView;
		}
		
		public void adjustLevelStageButtonStatus(View rootView, final int firstLabel )
		{
			if (firstLabel <= testLevel) {
				Button button1 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_1_1);
				button1.setEnabled(true);

				Button button2 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_1_2);
				Button button3 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_1_3);
				Button button4 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_1_4);
				Button button5 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_1_5);
				Button button6 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_1_6);      
				Button button7 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_1_7);               
				Button button8 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_1_8);                
				Button button9 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_1_9); 
				Button button10 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_1_10);
				
				button1.setTag((firstLabel-1)*10+1);
				button2.setTag((firstLabel-1)*10+2);
				button3.setTag((firstLabel-1)*10+3);
				button4.setTag((firstLabel-1)*10+4);
				button5.setTag((firstLabel-1)*10+5);
				button6.setTag((firstLabel-1)*10+6);
				button7.setTag((firstLabel-1)*10+7);
				button8.setTag((firstLabel-1)*10+8);
				button9.setTag((firstLabel-1)*10+9);
				button10.setTag(firstLabel*10);

				OnClickListener clickListener = new OnClickListener() {
					@Override
					public void onClick(View v) {
						int currentBtnClickStage = (Integer)(v.getTag());
						
						if (checkStageIsNew.getString(Integer.toString(currentBtnClickStage), "NEW").equals("OLD")) {
							ed.putInt("currentStage", currentBtnClickStage);
							ed.commit();
								
							if (currentBtnClickStage%10 ==0) {
								Intent myIntent = new Intent(getActivity(), StudyTestC.class);
								getActivity().startActivity(myIntent);
							} else {
								Intent myIntent = new Intent(getActivity(), StudyBegin.class);
								getActivity().startActivity(myIntent);
							}

						} else {
							// New Stage! So check if it is OK to open-up
							String url = "http://todpop.co.kr/api/studies/get_possible_stage.json?user_id=" + userId + "&level=" + firstLabel + 
									"&stage=" + currentBtnClickStage%10 + "&category=" + category +"&is_new=1";
							new CheckStageClear(currentBtnClickStage).execute(url);
						}
					}
				};

				button1.setOnClickListener(clickListener);
				button2.setOnClickListener(clickListener);
				button3.setOnClickListener(clickListener);
				button4.setOnClickListener(clickListener);
				button5.setOnClickListener(clickListener);
				button6.setOnClickListener(clickListener);
				button7.setOnClickListener(clickListener);
				button8.setOnClickListener(clickListener);
				button9.setOnClickListener(clickListener);
				button10.setOnClickListener(clickListener);



				String levelCountStr = "Level" + Integer.toString(firstLabel);
				int levelStageCount = prefs.getInt(levelCountStr, 1);
				Log.d("Level --- ", levelCountStr);
				Log.d("Count --- ", Integer.toString(levelStageCount));

				switch(levelStageCount) {//levelStageCount
				case 2:
					button2.setEnabled(true);
					break;
				case 3:
					button2.setEnabled(true); button3.setEnabled(true);
					break;
				case 4:
					button2.setEnabled(true); button3.setEnabled(true); button4.setEnabled(true);
					break;
				case 5:
					button2.setEnabled(true); button3.setEnabled(true); button4.setEnabled(true); button5.setEnabled(true);
					break;
				case 6:
					button2.setEnabled(true); button3.setEnabled(true); button4.setEnabled(true); button5.setEnabled(true); button6.setEnabled(true);
					break;
				case 7:
					button2.setEnabled(true); button3.setEnabled(true); button4.setEnabled(true); button5.setEnabled(true); button6.setEnabled(true); button7.setEnabled(true);
					break;
				case 8: 
					button2.setEnabled(true); button3.setEnabled(true); button4.setEnabled(true); button5.setEnabled(true); button6.setEnabled(true); button7.setEnabled(true); button8.setEnabled(true);
					break;
				case 9:                	
					button2.setEnabled(true); button3.setEnabled(true); button4.setEnabled(true); button5.setEnabled(true); button6.setEnabled(true); button7.setEnabled(true); button8.setEnabled(true); button9.setEnabled(true);
					break;
				case 10:
					button2.setEnabled(true); button3.setEnabled(true); button4.setEnabled(true); button5.setEnabled(true); button6.setEnabled(true); button7.setEnabled(true); button8.setEnabled(true); button9.setEnabled(true); button10.setEnabled(true);
					break;
				default:
					break;
				}


			} 

			if (firstLabel + 1 <= testLevel) {
				Button button1 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_2_1);
				button1.setEnabled(true);

				Button button2 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_2_2);
				Button button3 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_2_3);
				Button button4 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_2_4);
				Button button5 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_2_5);
				Button button6 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_2_6);      
				Button button7 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_2_7);               
				Button button8 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_2_8);                
				Button button9 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_2_9); 
				Button button10 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_2_10);
				
				button1.setTag((firstLabel+1-1)*10+1);
				button2.setTag((firstLabel+1-1)*10+2);
				button3.setTag((firstLabel+1-1)*10+3);
				button4.setTag((firstLabel+1-1)*10+4);
				button5.setTag((firstLabel+1-1)*10+5);
				button6.setTag((firstLabel+1-1)*10+6);
				button7.setTag((firstLabel+1-1)*10+7);
				button8.setTag((firstLabel+1-1)*10+8);
				button9.setTag((firstLabel+1-1)*10+9);
				button10.setTag((firstLabel+1)*10);
				
				OnClickListener clickListener = new OnClickListener() {
					@Override
					public void onClick(View v) {
						
						int currentBtnClickStage = (Integer)(v.getTag());
						
						String stageNew = checkStageIsNew.getString(Integer.toString(currentBtnClickStage), "NEW");
						if (stageNew.equals("OLD")) {
							ed.putInt("currentStage", currentBtnClickStage);
							ed.commit();
								
							if (currentBtnClickStage%10 ==0) {
								Intent myIntent = new Intent(getActivity(), StudyTestC.class);
								getActivity().startActivity(myIntent);
							} else {
								Intent myIntent = new Intent(getActivity(), StudyBegin.class);
								getActivity().startActivity(myIntent);
							}
						} else {
							// New Stage! So check if it is OK to open-up
							Log.d("usrid : ", userId);
							Log.d("level : ", Integer.toString(firstLabel+1));
							Log.d("stage : ", Integer.toString(currentBtnClickStage%10));
							
							String url = "http://todpop.co.kr/api/studies/get_possible_stage.json?user_id=" + userId + "&level=" + (firstLabel+1) + 
									"&stage=" + currentBtnClickStage%10 + "&category=" + category +"&is_new=1";
							new CheckStageClear(currentBtnClickStage).execute(url);
						}
					}
				};

				button1.setOnClickListener(clickListener);
				button2.setOnClickListener(clickListener);
				button3.setOnClickListener(clickListener);
				button4.setOnClickListener(clickListener);
				button5.setOnClickListener(clickListener);
				button6.setOnClickListener(clickListener);
				button7.setOnClickListener(clickListener);
				button8.setOnClickListener(clickListener);
				button9.setOnClickListener(clickListener);
				button10.setOnClickListener(clickListener);


				String levelCountStr = "Level" + Integer.toString(firstLabel+1);
				int levelStageCount = prefs.getInt(levelCountStr, 1);
				Log.d("Level --- ", levelCountStr);
				Log.d("Count --- ", Integer.toString(levelStageCount));

				switch(levelStageCount) {
				case 2:
					button2.setEnabled(true);
					break;
				case 3:
					button2.setEnabled(true); button3.setEnabled(true);
					break;
				case 4:
					button2.setEnabled(true); button3.setEnabled(true); button4.setEnabled(true);
					break;
				case 5:
					button2.setEnabled(true); button3.setEnabled(true); button4.setEnabled(true); button5.setEnabled(true);
					break;
				case 6:
					button2.setEnabled(true); button3.setEnabled(true); button4.setEnabled(true); button5.setEnabled(true); button6.setEnabled(true);
					break;
				case 7:
					button2.setEnabled(true); button3.setEnabled(true); button4.setEnabled(true); button5.setEnabled(true); button6.setEnabled(true); button7.setEnabled(true);
					break;
				case 8: 
					button2.setEnabled(true); button3.setEnabled(true); button4.setEnabled(true); button5.setEnabled(true); button6.setEnabled(true); button7.setEnabled(true); button8.setEnabled(true);
					break;
				case 9:                	
					button2.setEnabled(true); button3.setEnabled(true); button4.setEnabled(true); button5.setEnabled(true); button6.setEnabled(true); button7.setEnabled(true); button8.setEnabled(true); button9.setEnabled(true);
					break;
				case 10:
					button2.setEnabled(true); button3.setEnabled(true); button4.setEnabled(true); button5.setEnabled(true); button6.setEnabled(true); button7.setEnabled(true); button8.setEnabled(true); button9.setEnabled(true); button10.setEnabled(true);
					break;
				default:
					break;
				}
			}

			if (firstLabel + 2 <= testLevel) {
				Button button1 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_3_1);
				button1.setEnabled(true);

				Button button2 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_3_2);
				Button button3 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_3_3);
				Button button4 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_3_4);
				Button button5 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_3_5);
				Button button6 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_3_6);      
				Button button7 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_3_7);               
				Button button8 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_3_8);                
				Button button9 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_3_9); 
				Button button10 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_3_10);
				
				button1.setTag((firstLabel+2-1)*10+1);
				button2.setTag((firstLabel+2-1)*10+2);
				button3.setTag((firstLabel+2-1)*10+3);
				button4.setTag((firstLabel+2-1)*10+4);
				button5.setTag((firstLabel+2-1)*10+5);
				button6.setTag((firstLabel+2-1)*10+6);
				button7.setTag((firstLabel+2-1)*10+7);
				button8.setTag((firstLabel+2-1)*10+8);
				button9.setTag((firstLabel+2-1)*10+9);
				button10.setTag((firstLabel+2)*10);
				
				OnClickListener clickListener = new OnClickListener() {
					@Override
					public void onClick(View v) {
						
						int currentBtnClickStage = (Integer)(v.getTag());
						
						if (checkStageIsNew.getString(Integer.toString(currentBtnClickStage), "NEW").equals("OLD")) {
							ed.putInt("currentStage", currentBtnClickStage);
							ed.commit();
								
							if (currentBtnClickStage%10 ==0) {
								Intent myIntent = new Intent(getActivity(), StudyTestC.class);
								getActivity().startActivity(myIntent);
							} else {
								Intent myIntent = new Intent(getActivity(), StudyBegin.class);
								getActivity().startActivity(myIntent);
							}
						} else {
							// New Stage! So check if it is OK to open-up
							String url = "http://todpop.co.kr/api/studies/get_possible_stage.json?user_id=" + userId + "&level=" + (firstLabel+2) + 
									"&stage=" + currentBtnClickStage%10 + "&category=" + category +"&is_new=1";
							new CheckStageClear(currentBtnClickStage).execute(url);
						}
					}
				};

				button1.setOnClickListener(clickListener);
				button2.setOnClickListener(clickListener);
				button3.setOnClickListener(clickListener);
				button4.setOnClickListener(clickListener);
				button5.setOnClickListener(clickListener);
				button6.setOnClickListener(clickListener);
				button7.setOnClickListener(clickListener);
				button8.setOnClickListener(clickListener);
				button9.setOnClickListener(clickListener);
				button10.setOnClickListener(clickListener);


				String levelCountStr = "Level" + Integer.toString(firstLabel+2);
				int levelStageCount = prefs.getInt(levelCountStr, 1);
				Log.d("Level --- ", levelCountStr);
				Log.d("Count --- ", Integer.toString(levelStageCount));

				switch(levelStageCount) {
				case 2:
					button2.setEnabled(true);
					break;
				case 3:
					button2.setEnabled(true); button3.setEnabled(true);
					break;
				case 4:
					button2.setEnabled(true); button3.setEnabled(true); button4.setEnabled(true);
					break;
				case 5:
					button2.setEnabled(true); button3.setEnabled(true); button4.setEnabled(true); button5.setEnabled(true);
					break;
				case 6:
					button2.setEnabled(true); button3.setEnabled(true); button4.setEnabled(true); button5.setEnabled(true); button6.setEnabled(true);
					break;
				case 7:
					button2.setEnabled(true); button3.setEnabled(true); button4.setEnabled(true); button5.setEnabled(true); button6.setEnabled(true); button7.setEnabled(true);
					break;
				case 8: 
					button2.setEnabled(true); button3.setEnabled(true); button4.setEnabled(true); button5.setEnabled(true); button6.setEnabled(true); button7.setEnabled(true); button8.setEnabled(true);
					break;
				case 9:                	
					button2.setEnabled(true); button3.setEnabled(true); button4.setEnabled(true); button5.setEnabled(true); button6.setEnabled(true); button7.setEnabled(true); button8.setEnabled(true); button9.setEnabled(true);
					break;
				case 10:
					button2.setEnabled(true); button3.setEnabled(true); button4.setEnabled(true); button5.setEnabled(true); button6.setEnabled(true); button7.setEnabled(true); button8.setEnabled(true); button9.setEnabled(true); button10.setEnabled(true);
					break;
				default:
					break;
				}
			}
		}
		
		private class CheckStageClear extends AsyncTask<String, Void, JSONObject> 
		{
			DefaultHttpClient httpClient ;
			int currentBtnStage;
			
			public CheckStageClear(int stageCount)
			{
				currentBtnStage = stageCount;
			}
			
			@Override
			protected JSONObject doInBackground(String... urls) 
			{
				JSONObject result = null;
				try
				{
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

					if (resEntity != null)
					{    
						result = new JSONObject(EntityUtils.toString(resEntity)); 
						//Log.d("RESPONSE ---- ", result.toString());				        	
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
					Log.d("CHECK STAGE CLEAR JSON RESPONSE ---- ", json.toString());				        	

					if(json.getBoolean("status")==true) {
						JSONObject checkClearJsonObj = json.getJSONObject("data");
						boolean isPossible = checkClearJsonObj.getBoolean("possible");
						
						if (isPossible==true) {
							Log.d("Insert Current Stage -----", Integer.toString(currentBtnStage));
							ed.putInt("currentStage", currentBtnStage);
							ed.commit();
								
							if (currentBtnStage%10 ==0) {
								Intent myIntent = new Intent(getActivity(), StudyTestC.class);
								getActivity().startActivity(myIntent);
							} else {
								Intent myIntent = new Intent(getActivity(), StudyBegin.class);
								getActivity().startActivity(myIntent);
							}
						} else {
							// POP UP ALERT

							showPopup();
						}
					}else{		    
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}		
		}
		
	}
	
	public static void showPopup()
	{
		popupText.setText(R.string.check_new_stage_popup_string);
		popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
	}
	
	public void closePopup(View v)
	{
		popupWindow.dismiss();
	}

	class StageListener implements View.OnClickListener {

		public void onClick(View v) {

		}
	};

	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.study_learn, menu);
		return true;
	}

	//--- on click---


	public void onClickBack(View view)
	{
//		Intent intent = new Intent(getApplicationContext(), StudyCategory.class);
//		startActivity(intent);
		finish();
	}

	public void showStudyFinish(View view)
	{
		Intent intent = new Intent(getApplicationContext(), StudyFinish.class);
		startActivity(intent);
		//finish();
	}
	
	public void goBackToHome(View v) 
	{	
		Intent intent = new Intent(getApplicationContext(), StudyHome.class);
		startActivity(intent);
		finish();
	}

	@Override
	public void onResume() 
	{
		super.onResume();
	}
	
	@Override
	public void onRestart()
	{
		super.onRestart();
		Log.d("On Restart", "---------------");
		startActivity(new Intent(this, StudyLearn.class));
		finish();
	}

}
