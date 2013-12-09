package com.todpop.saltyenglish;






import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.flurry.android.FlurryAgent;
import com.todpop.saltyenglish.LvTestResult.MyItem;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
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
	static SharedPreferences studyInfo;
	static String stageInfo;
	
	
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

		studyInfo = getSharedPreferences("studyInfo",0);
		stageInfo = studyInfo.getString("stageInfo", null);
		
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
				fregmentCount = 20;
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
		
		public void adjustLevelStageButtonStatus(View rootView, final int firstLevel)
		{
			
			
			
			int stageIndexStart = firstLevel*10 - 10;
			String stageInfoFirst = stageInfo.substring(stageIndexStart,stageIndexStart+10);
			String stageInfoSecond = stageInfo.substring(stageIndexStart+10,stageIndexStart+20);
			String stageInfoThird = stageInfo.substring(stageIndexStart+20,stageIndexStart+30);
			
			if (!stageInfoFirst.equals("xxxxxxxxxx")) {
				
				Button button1 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_1_1);
				Button button2 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_1_2);
				Button button3 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_1_3);
				Button button4 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_1_4);
				Button button5 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_1_5);
				Button button6 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_1_6);      
				Button button7 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_1_7);               
				Button button8 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_1_8);                
				Button button9 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_1_9); 
				Button button10 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_1_10);
				
				button1.setTag((firstLevel-1)*10+1);
				button2.setTag((firstLevel-1)*10+2);
				button3.setTag((firstLevel-1)*10+3);
				button4.setTag((firstLevel-1)*10+4);
				button5.setTag((firstLevel-1)*10+5);
				button6.setTag((firstLevel-1)*10+6);
				button7.setTag((firstLevel-1)*10+7);
				button8.setTag((firstLevel-1)*10+8);
				button9.setTag((firstLevel-1)*10+9);
				button10.setTag(firstLevel*10);
				
				if(stageInfoFirst.charAt(0)=='1')		{button1.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_1_s);}
				else if(stageInfoFirst.charAt(0)=='2')	{button1.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_1_g);}
				
				if(stageInfoFirst.charAt(1)=='1')		{button2.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_2_s);}
				else if(stageInfoFirst.charAt(1)=='2')	{button2.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_2_g);}

				if(stageInfoFirst.charAt(2)=='1')		{button3.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_3_s);}
				else if(stageInfoFirst.charAt(2)=='2')	{button3.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_3_g);}

				if(stageInfoFirst.charAt(3)=='1')		{button4.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_4_s);}
				else if(stageInfoFirst.charAt(3)=='2')	{button4.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_4_g);}

				if(stageInfoFirst.charAt(4)=='1')		{button5.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_5_s);}
				else if(stageInfoFirst.charAt(4)=='2')	{button5.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_5_g);}

				if(stageInfoFirst.charAt(5)=='1')		{button6.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_6_s);}
				else if(stageInfoFirst.charAt(5)=='2')	{button6.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_6_g);}

				if(stageInfoFirst.charAt(6)=='1')		{button7.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_7_s);}
				else if(stageInfoFirst.charAt(6)=='2')	{button7.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_7_g);}

				if(stageInfoFirst.charAt(7)=='1')		{button8.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_8_s);}
				else if(stageInfoFirst.charAt(7)=='2')	{button8.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_8_g);}

				if(stageInfoFirst.charAt(8)=='1')		{button9.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_9_s);}
				else if(stageInfoFirst.charAt(8)=='2')	{button9.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_9_g);}

				if(stageInfoFirst.charAt(9)=='1')		{button10.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_10_s);}
				else if(stageInfoFirst.charAt(9)=='2')	{button10.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_10_g);}

				
				
				OnClickListener clickListener = new OnClickListener() {
					public void onClick(View v) {

						int currentBtnClickStage = (Integer)(v.getTag());
						int selectedStageNo = (currentBtnClickStage-1)%10+1; 
						int selectedStageStatus = stageInfo.charAt(currentBtnClickStage-1);

						try
						{
							if (selectedStageStatus != 'Y') {

								Map<String, String> stageParams = new HashMap<String, String>();
								stageParams.put("User Id", userId);
								FlurryAgent.logEvent("Stage clicked (Review)", stageParams);

								ed.putInt("currentStage", currentBtnClickStage);
								ed.commit();
									
								if (selectedStageNo == 10) {
									Intent myIntent = new Intent(getActivity(), StudyTestC.class);
									getActivity().startActivity(myIntent);
								} else {
									Intent myIntent = new Intent(getActivity(), StudyBegin.class);
									getActivity().startActivity(myIntent);
								}
	
							} else {
								// New Stage! So check if it is OK to open-up
								String url = "http://todpop.co.kr/api/studies/get_possible_stage.json?user_id=" + userId + "&level=" + firstLevel + 
										"&stage=" + selectedStageNo + "&category=" + category +"&is_new=1";
								new CheckStageClear(currentBtnClickStage).execute(url);
							}
						}
						catch(Exception e)
						{
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



				
				Log.d("------ ", "-------------------------------------------");
				Log.d("Level --- ", stageInfoFirst);
				Log.d("------ ", "-------------------------------------------");

				char s1 = stageInfoFirst.charAt(0);
				char s2 = stageInfoFirst.charAt(1);
				char s3 = stageInfoFirst.charAt(2);
				char s4 = stageInfoFirst.charAt(3);
				char s5 = stageInfoFirst.charAt(4);
				char s6 = stageInfoFirst.charAt(5);
				char s7 = stageInfoFirst.charAt(6);
				char s8 = stageInfoFirst.charAt(7);
				char s9 = stageInfoFirst.charAt(8);
				char s10 = stageInfoFirst.charAt(9);
				
				if (s1!='x')	{	button1.setEnabled(true);}
				if (s2!='x')	{	button2.setEnabled(true);}
				if (s3!='x')	{	button3.setEnabled(true);}
				if (s4!='x')	{	button4.setEnabled(true);}
				if (s5!='x')	{	button5.setEnabled(true);}
				if (s6!='x')	{	button6.setEnabled(true);}
				if (s7!='x')	{	button7.setEnabled(true);}
				if (s8!='x')	{	button8.setEnabled(true);}
				if (s9!='x')	{	button9.setEnabled(true);}
				if (s10!='x')	{	button10.setEnabled(true);}
			} 

			
			if (!stageInfoSecond.equals("xxxxxxxxxx")) {

				Button button1 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_2_1);
				Button button2 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_2_2);
				Button button3 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_2_3);
				Button button4 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_2_4);
				Button button5 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_2_5);
				Button button6 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_2_6);      
				Button button7 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_2_7);               
				Button button8 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_2_8);                
				Button button9 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_2_9); 
				Button button10 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_2_10);
				
				button1.setTag((firstLevel+1-1)*10+1);
				button2.setTag((firstLevel+1-1)*10+2);
				button3.setTag((firstLevel+1-1)*10+3);
				button4.setTag((firstLevel+1-1)*10+4);
				button5.setTag((firstLevel+1-1)*10+5);
				button6.setTag((firstLevel+1-1)*10+6);
				button7.setTag((firstLevel+1-1)*10+7);
				button8.setTag((firstLevel+1-1)*10+8);
				button9.setTag((firstLevel+1-1)*10+9);
				button10.setTag((firstLevel+1)*10);
				
				if(stageInfoSecond.charAt(0)=='1')		{button1.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_1_s);}
				else if(stageInfoSecond.charAt(0)=='2')	{button1.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_1_g);}
				
				if(stageInfoSecond.charAt(1)=='1')		{button2.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_2_s);}
				else if(stageInfoSecond.charAt(1)=='2')	{button2.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_2_g);}

				if(stageInfoSecond.charAt(2)=='1')		{button3.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_3_s);}
				else if(stageInfoSecond.charAt(2)=='2')	{button3.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_3_g);}

				if(stageInfoSecond.charAt(3)=='1')		{button4.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_4_s);}
				else if(stageInfoSecond.charAt(3)=='2')	{button4.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_4_g);}

				if(stageInfoSecond.charAt(4)=='1')		{button5.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_5_s);}
				else if(stageInfoSecond.charAt(4)=='2')	{button5.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_5_g);}

				if(stageInfoSecond.charAt(5)=='1')		{button6.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_6_s);}
				else if(stageInfoSecond.charAt(5)=='2')	{button6.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_6_g);}

				if(stageInfoSecond.charAt(6)=='1')		{button7.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_7_s);}
				else if(stageInfoSecond.charAt(6)=='2')	{button7.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_7_g);}

				if(stageInfoSecond.charAt(7)=='1')		{button8.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_8_s);}
				else if(stageInfoSecond.charAt(7)=='2')	{button8.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_8_g);}

				if(stageInfoSecond.charAt(8)=='1')		{button9.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_9_s);}
				else if(stageInfoSecond.charAt(8)=='2')	{button9.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_9_g);}

				if(stageInfoSecond.charAt(9)=='1')		{button10.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_10_s);}
				else if(stageInfoSecond.charAt(9)=='2')	{button10.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_10_g);}

				
				OnClickListener clickListener = new OnClickListener() {
					@Override
					public void onClick(View v) {
						
						int currentBtnClickStage = (Integer)(v.getTag());
						int selectedStageNo = (currentBtnClickStage-1)%10+1; 
						int selectedStageStatus = stageInfo.charAt(currentBtnClickStage-1);
						
						try
						{
							if (selectedStageStatus != 'Y') {

								Map<String, String> stageParams = new HashMap<String, String>();
								stageParams.put("User Id", userId);
								FlurryAgent.logEvent("Stage clicked (Review)", stageParams);

								ed.putInt("currentStage", currentBtnClickStage);
								ed.commit();
									
								if (selectedStageNo == 10) {
									Intent myIntent = new Intent(getActivity(), StudyTestC.class);
									getActivity().startActivity(myIntent);
								} else {
									Intent myIntent = new Intent(getActivity(), StudyBegin.class);
									getActivity().startActivity(myIntent);
								}
								
							} else {
								// New Stage! So check if it is OK to open-up
								Log.d("usrid : ", userId);
								Log.d("level : ", Integer.toString(firstLevel+1));
								Log.d("stage : ", Integer.toString(currentBtnClickStage%10));
								
								String url = "http://todpop.co.kr/api/studies/get_possible_stage.json?user_id=" + userId + "&level=" + (firstLevel+1) + 
										"&stage=" + selectedStageNo + "&category=" + category +"&is_new=1";
								new CheckStageClear(currentBtnClickStage).execute(url);
							}
						}
						catch(Exception e)
						{
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


				//String levelCountStr = "Level" + Integer.toString(firstLevel+1);
				//int levelStageCount = prefs.getInt(levelCountStr, 1);
				//Log.d("Level --- ", levelCountStr);
				//Log.d("Count --- ", Integer.toString(levelStageCount));
				
				Log.d("------ ", "-------------------------------------------");
				Log.d("Level --- ", stageInfoSecond);
				Log.d("------ ", "-------------------------------------------");

				char s1 = stageInfoSecond.charAt(0);
				char s2 = stageInfoSecond.charAt(1);
				char s3 = stageInfoSecond.charAt(2);
				char s4 = stageInfoSecond.charAt(3);
				char s5 = stageInfoSecond.charAt(4);
				char s6 = stageInfoSecond.charAt(5);
				char s7 = stageInfoSecond.charAt(6);
				char s8 = stageInfoSecond.charAt(7);
				char s9 = stageInfoSecond.charAt(8);
				char s10 = stageInfoSecond.charAt(9);
				
				if (s1!='x')	{	button1.setEnabled(true);}
				if (s2!='x')	{	button2.setEnabled(true);}
				if (s3!='x')	{	button3.setEnabled(true);}
				if (s4!='x')	{	button4.setEnabled(true);}
				if (s5!='x')	{	button5.setEnabled(true);}
				if (s6!='x')	{	button6.setEnabled(true);}
				if (s7!='x')	{	button7.setEnabled(true);}
				if (s8!='x')	{	button8.setEnabled(true);}
				if (s9!='x')	{	button9.setEnabled(true);}
				if (s10!='x')	{	button10.setEnabled(true);}

			}

			if (!stageInfoThird.equals("xxxxxxxxxx")) {

				Button button1 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_3_1);
				Button button2 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_3_2);
				Button button3 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_3_3);
				Button button4 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_3_4);
				Button button5 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_3_5);
				Button button6 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_3_6);      
				Button button7 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_3_7);               
				Button button8 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_3_8);                
				Button button9 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_3_9); 
				Button button10 = (Button)rootView.findViewById(R.id.studylearn_id_levelbtn_3_10);
				
				button1.setTag((firstLevel+2-1)*10+1);
				button2.setTag((firstLevel+2-1)*10+2);
				button3.setTag((firstLevel+2-1)*10+3);
				button4.setTag((firstLevel+2-1)*10+4);
				button5.setTag((firstLevel+2-1)*10+5);
				button6.setTag((firstLevel+2-1)*10+6);
				button7.setTag((firstLevel+2-1)*10+7);
				button8.setTag((firstLevel+2-1)*10+8);
				button9.setTag((firstLevel+2-1)*10+9);
				button10.setTag((firstLevel+2)*10);
				
				if(stageInfoThird.charAt(0)=='1')		{button1.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_1_s);}
				else if(stageInfoThird.charAt(0)=='2')	{button1.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_1_g);}
				
				if(stageInfoThird.charAt(1)=='1')		{button2.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_2_s);}
				else if(stageInfoThird.charAt(1)=='2')	{button2.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_2_g);}

				if(stageInfoThird.charAt(2)=='1')		{button3.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_3_s);}
				else if(stageInfoThird.charAt(2)=='2')	{button3.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_3_g);}

				if(stageInfoThird.charAt(3)=='1')		{button4.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_4_s);}
				else if(stageInfoThird.charAt(3)=='2')	{button4.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_4_g);}

				if(stageInfoThird.charAt(4)=='1')		{button5.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_5_s);}
				else if(stageInfoThird.charAt(4)=='2')	{button5.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_5_g);}

				if(stageInfoThird.charAt(5)=='1')		{button6.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_6_s);}
				else if(stageInfoThird.charAt(5)=='2')	{button6.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_6_g);}

				if(stageInfoThird.charAt(6)=='1')		{button7.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_7_s);}
				else if(stageInfoThird.charAt(6)=='2')	{button7.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_7_g);}

				if(stageInfoThird.charAt(7)=='1')		{button8.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_8_s);}
				else if(stageInfoThird.charAt(7)=='2')	{button8.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_8_g);}

				if(stageInfoThird.charAt(8)=='1')		{button9.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_9_s);}
				else if(stageInfoThird.charAt(8)=='2')	{button9.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_9_g);}

				if(stageInfoThird.charAt(9)=='1')		{button10.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_10_s);}
				else if(stageInfoThird.charAt(9)=='2')	{button10.setBackgroundResource(R.drawable.studylearn_drawable_btn_level_10_g);}

				
				
				

				
				OnClickListener clickListener = new OnClickListener() {
					@Override
					public void onClick(View v) {
						
						int currentBtnClickStage = (Integer)(v.getTag());
						int selectedStageNo = (currentBtnClickStage-1)%10+1; 
						int selectedStageStatus = stageInfo.charAt(currentBtnClickStage-1);
						
						try
						{
							if (selectedStageStatus != 'Y') {

								Map<String, String> stageParams = new HashMap<String, String>();
								stageParams.put("User Id", userId);
								FlurryAgent.logEvent("Stage clicked (Review)", stageParams);

								ed.putInt("currentStage", currentBtnClickStage);
								ed.commit();
								
								if (selectedStageNo == 10) {
									Intent myIntent = new Intent(getActivity(), StudyTestC.class);
									getActivity().startActivity(myIntent);
								} else {
									Intent myIntent = new Intent(getActivity(), StudyBegin.class);
									getActivity().startActivity(myIntent);
								}
							} else {
								// New Stage! So check if it is OK to open-up
								String url = "http://todpop.co.kr/api/studies/get_possible_stage.json?user_id=" + userId + "&level=" + (firstLevel+2) + 
										"&stage=" + selectedStageNo + "&category=" + category +"&is_new=1";
								new CheckStageClear(currentBtnClickStage).execute(url);
							}
						}
						catch(Exception e)
						{
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


				
				Log.d("------ ", "-------------------------------------------");
				Log.d("Level --- ", stageInfoThird);
				Log.d("------ ", "-------------------------------------------");

				char s1 = stageInfoThird.charAt(0);
				char s2 = stageInfoThird.charAt(1);
				char s3 = stageInfoThird.charAt(2);
				char s4 = stageInfoThird.charAt(3);
				char s5 = stageInfoThird.charAt(4);
				char s6 = stageInfoThird.charAt(5);
				char s7 = stageInfoThird.charAt(6);
				char s8 = stageInfoThird.charAt(7);
				char s9 = stageInfoThird.charAt(8);
				char s10 = stageInfoThird.charAt(9);
				
				if (s1!='x')	{	button1.setEnabled(true);}
				if (s2!='x')	{	button2.setEnabled(true);}
				if (s3!='x')	{	button3.setEnabled(true);}
				if (s4!='x')	{	button4.setEnabled(true);}
				if (s5!='x')	{	button5.setEnabled(true);}
				if (s6!='x')	{	button6.setEnabled(true);}
				if (s7!='x')	{	button7.setEnabled(true);}
				if (s8!='x')	{	button8.setEnabled(true);}
				if (s9!='x')	{	button9.setEnabled(true);}
				if (s10!='x')	{	button10.setEnabled(true);}

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
						
						//TODO stage to be limit remove
						if (isPossible==true) {
							Map<String, String> stageParams = new HashMap<String, String>();
							stageParams.put("User Id", userId);
							FlurryAgent.logEvent("Stage clicked (New)", stageParams);
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
	@Override
	protected void onStart()
	{
		super.onStart();
		FlurryAgent.onStartSession(this, "ZKWGFP6HKJ33Y69SP5QY");
		FlurryAgent.logEvent("Study Stage Select");
	}
	 
	@Override
	protected void onStop()
	{
		super.onStop();		
		FlurryAgent.onEndSession(this);
	}

}
