package com.todpop.saltyenglish;


import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.Animator.AnimatorListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Interpolator;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

public class StudyHome extends Activity {
	boolean isOnSlide = false;

	// CPI View show in return from study test
	RelativeLayout cpxView;
	ImageView cpxAdImageView;
	TextView cpxAdTextView;
	ImageView cpxAdInfoTitle;

	// Home Ranking List
	static ListView rankingList;
	
	// Ranking Item and Adaptor
	RankingListItem rankingItem;
	ArrayList<RankingListItem> rankingItemArray_basicWeek;
	ArrayList<RankingListItem> rankingItemArray_basicMonth;
	ArrayList<RankingListItem> rankingItemArray_middleWeek;
	ArrayList<RankingListItem> rankingItemArray_middleMonth;
	ArrayList<RankingListItem> rankingItemArray_highWeek;
	ArrayList<RankingListItem> rankingItemArray_highMonth;
	ArrayList<RankingListItem> rankingItemArray_toeicWeek;
	ArrayList<RankingListItem> rankingItemArray_toeicMonth;
	ListView testList;
	String[] myRankArray;
	String[] myScoreArray;
	
	
	ArrayList<String> noticeList;
	RankingListAdapter rankingListAdapter;

	TextView myRank;
	ImageView myImage;
	TextView myName;
	TextView myScore;
	
	ImageView categoryWhiteBox;
	View otherSideView;
	
	//declare define popup view
	PopupWindow popupWindow;
	View popupview;
	LinearLayout mainLayout;
	TextView popupText;
	
	String kakaoMent = "";
	String kaokaoAndroidUrl = "";
	String iosUrl = "";
	String userId;
	
	boolean majorVersionUpdate = false;
	
	SharedPreferences pref;
	SharedPreferences studyInfo;
	SharedPreferences.Editor studyInfoEdit;
	//SharedPreferences myRankInfo;
	
	ViewPager categoryPager;
	StudyHomePagerAdapter pagerAdapter;
	
	int scrollState;
	
	EdgeSwipe edgeSwipe;
	
	int category, period;
	
	//CPX Info
	int cpxAdId;
	int cpxAdType;	
	String cpxAdImageUrl;
	String cpxAdText;
	String cpxTargetUrl;
	String cpxPackageName;
	String cpxConfirmUrl;
	String cpxReward;
	String cpxPoint;
	int cpxQuestionCount;
	boolean cpxHistoryFlag;
	
	//CPX Popup
	PopupWindow cpxPopupWindow;
	View cpxPopupView;
	RelativeLayout cpxPopupRelative;
	TextView cpxPopupText;
    
 	// Database
 	WordDBHelper mHelper;
	@Override
	protected void onStart()
	{
		super.onStart();
		FlurryAgent.onStartSession(this, "ZKWGFP6HKJ33Y69SP5QY");
		FlurryAgent.logEvent("Study Home");
	    EasyTracker.getInstance(this).activityStart(this);
	}
	 
	@Override
	protected void onStop()
	{
		super.onStop();		
		FlurryAgent.onEndSession(this);
	    EasyTracker.getInstance(this).activityStop(this);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_study_home);
		
		//myRankInfo = getSharedPreferences("myRankInfo", 0);
		studyInfo = getSharedPreferences("studyInfo",0);
		
		pref = getSharedPreferences("rgInfo",0);
		userId = pref.getString("mem_id", "0");
		
		mHelper = new WordDBHelper(this);

		myRank = (TextView)findViewById(R.id.studyhome_id_my_rank);
		myImage = (ImageView)findViewById(R.id.studyhome_id_my_rank_image);
		myName = (TextView)findViewById(R.id.studyhome_id_my_rank_name_text);
		myScore = (TextView)findViewById(R.id.studyhome_id_my_rank_fraction);
		//categoryWhiteBox = (ImageView)findViewById(R.id.bgimg_whitebox);

		otherSideView = (View)findViewById(R.id.studyhome_id_emptyview);
		
		//rankingList = (ListView)findViewById(R.id.studyhome_id_listview);
		myRankArray = new String[8];
		myScoreArray = new String[8];

		rankingItemArray_basicWeek = new ArrayList<RankingListItem>();
		rankingItemArray_basicMonth = new ArrayList<RankingListItem>();
		rankingItemArray_middleWeek = new ArrayList<RankingListItem>();
		rankingItemArray_middleMonth = new ArrayList<RankingListItem>();
		rankingItemArray_highWeek = new ArrayList<RankingListItem>();
		rankingItemArray_highMonth = new ArrayList<RankingListItem>();
		rankingItemArray_toeicWeek = new ArrayList<RankingListItem>();
		rankingItemArray_toeicMonth = new ArrayList<RankingListItem>();
		
		noticeList = new ArrayList<String>();
		
		studyInfoEdit = studyInfo.edit();
		studyInfoEdit.putInt("currentPeriod", 1);
		studyInfoEdit.commit();

		//setting category ViewPager.
		categoryPager = (ViewPager)findViewById(R.id.study_home_id_pager);

        pagerAdapter = new StudyHomePagerAdapter(this);
		categoryPager.setAdapter(pagerAdapter);

		categoryPager.setOffscreenPageLimit(3);

		/*categoryPager.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                rankListPager.onTouchEvent(event);
                return false;
            }
        });*/
		//categoryPager.setOnTouchListener(onTouch);
		
		try {
			Field mScroller = ViewPager.class.getDeclaredField("mScroller");
			mScroller.setAccessible(true);
			FixedSpeedScroller scroller = new FixedSpeedScroller(categoryPager.getContext());
			mScroller.set(categoryPager, scroller);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		categoryPager.setCurrentItem(1073741819 + studyInfo.getInt("currentCategory", 1));
		//category ViewPager setting done
		
		//rank list  setting
		rankingList = (ListView)findViewById(R.id.studyhome_id_listview);
		
		
		categoryPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				Log.i("STEVEN=========A", "categoryPager onPageSelected");
				if(arg0%4 == 0){ //basic
					Log.i("STEVEN up", "basic category");
    				/*period =1;
    				studyInfoEdit.putInt("currentPeriod", 1);*/
    				studyInfoEdit.putInt("currentCategory", 1);
    				studyInfoEdit.commit();
    				
    				if(studyInfo.getInt("currentPeriod", 1) == 1){
    					if(rankingItemArray_basicWeek.isEmpty()){
    						new GetRank(1).execute("http://todpop.co.kr/api/users/get_users_score.json?category="+
    								1+"&period="+1+"&nickname="+pref.getString("nickname", "NO"));
    					}
    					else{
    						rankingListAdapter = new RankingListAdapter(StudyHome.this,R.layout.home_rank_list_item_view, rankingItemArray_basicWeek);
    						rankingList.setAdapter(rankingListAdapter);
    						myRank.setText(myRankArray[0]);
    						myScore.setText(myScoreArray[0]);
    					}
    				}
    				else{
    					if(rankingItemArray_basicMonth.isEmpty()){
    						new GetRank(5).execute("http://todpop.co.kr/api/users/get_users_score.json?category="+
    								1+"&period="+2+"&nickname="+pref.getString("nickname", "NO"));
    					}
    					else{
    						rankingListAdapter = new RankingListAdapter(StudyHome.this,R.layout.home_rank_list_item_view, rankingItemArray_basicMonth);
    						rankingList.setAdapter(rankingListAdapter);
    						myRank.setText(myRankArray[4]);
    						myScore.setText(myScoreArray[4]);
    					}
    				}
				}
				else if(arg0%4 == 1){		//middle
					Log.i("STEVEN up", "middle category");
    				/*period =1;
    				studyInfoEdit.putInt("currentPeriod", 1);*/
    				studyInfoEdit.putInt("currentCategory", 2);
    				studyInfoEdit.commit();
    				
    				if(studyInfo.getInt("currentPeriod", 1) == 1){
    					if(rankingItemArray_middleWeek.isEmpty()){
    						new GetRank(2).execute("http://todpop.co.kr/api/users/get_users_score.json?category="+
    								2+"&period="+1+"&nickname="+pref.getString("nickname", "NO"));
    					}
    					else{
    						rankingListAdapter = new RankingListAdapter(StudyHome.this,R.layout.home_rank_list_item_view, rankingItemArray_middleWeek);
    						rankingList.setAdapter(rankingListAdapter);
    						myRank.setText(myRankArray[1]);
    						myScore.setText(myScoreArray[1]);
    					}
    				}
    				else{
    					if(rankingItemArray_middleMonth.isEmpty()){
    						new GetRank(6).execute("http://todpop.co.kr/api/users/get_users_score.json?category="+
    								2+"&period="+2+"&nickname="+pref.getString("nickname", "NO"));
    					}
    					else{
    						rankingListAdapter = new RankingListAdapter(StudyHome.this,R.layout.home_rank_list_item_view, rankingItemArray_middleMonth);
    						rankingList.setAdapter(rankingListAdapter);
    						myRank.setText(myRankArray[5]);
    						myScore.setText(myScoreArray[5]);
    					}
    				}
				}
				else if(arg0%4 == 2){		//high
					Log.i("STEVEN up", "high category");
    				/*period =1;
    				studyInfoEdit.putInt("currentPeriod", 1);*/
    				studyInfoEdit.putInt("currentCategory", 3);
    				studyInfoEdit.commit();
    				
    				if(studyInfo.getInt("currentPeriod", 1) == 1){
    					if(rankingItemArray_highWeek.isEmpty()){
    						new GetRank(3).execute("http://todpop.co.kr/api/users/get_users_score.json?category="+
    								3+"&period="+1+"&nickname="+pref.getString("nickname", "NO"));
    					}
    					else{
    						rankingListAdapter = new RankingListAdapter(StudyHome.this,R.layout.home_rank_list_item_view, rankingItemArray_highWeek);
    						rankingList.setAdapter(rankingListAdapter);
    						myRank.setText(myRankArray[2]);
    						myScore.setText(myScoreArray[2]);
    					}
    				}
    				else{
    					if(rankingItemArray_highMonth.isEmpty()){
    						new GetRank(7).execute("http://todpop.co.kr/api/users/get_users_score.json?category="+
    								3+"&period="+2+"&nickname="+pref.getString("nickname", "NO"));
    					}
    					else{
    						rankingListAdapter = new RankingListAdapter(StudyHome.this,R.layout.home_rank_list_item_view, rankingItemArray_highMonth);
    						rankingList.setAdapter(rankingListAdapter);
    						myRank.setText(myRankArray[6]);
    						myScore.setText(myScoreArray[6]);
    					}
    				}
				}
				else{		//toiec
					Log.i("STEVEN up", "toiec category");
    				/*period =1;
    				studyInfoEdit.putInt("currentPeriod", 1);*/
    				studyInfoEdit.putInt("currentCategory", 4);
    				studyInfoEdit.commit();
    				
    				if(studyInfo.getInt("currentPeriod", 1) == 1){
    					if(rankingItemArray_toeicWeek.isEmpty()){
    						new GetRank(4).execute("http://todpop.co.kr/api/users/get_users_score.json?category="+
    								4+"&period="+1+"&nickname="+pref.getString("nickname", "NO"));
    					}
    					else{
    						rankingListAdapter = new RankingListAdapter(StudyHome.this,R.layout.home_rank_list_item_view, rankingItemArray_toeicWeek);
    						rankingList.setAdapter(rankingListAdapter);
    						myRank.setText(myRankArray[3]);
    						myScore.setText(myScoreArray[3]);
    					}
    				}
    				else{
    					if(rankingItemArray_toeicMonth.isEmpty()){
    						new GetRank(8).execute("http://todpop.co.kr/api/users/get_users_score.json?category="+
    								4+"&period="+2+"&nickname="+pref.getString("nickname", "NO"));
    					}
    					else{
    						rankingListAdapter = new RankingListAdapter(StudyHome.this,R.layout.home_rank_list_item_view, rankingItemArray_toeicMonth);
    						rankingList.setAdapter(rankingListAdapter);
    						myRank.setText(myRankArray[7]);
    						myScore.setText(myScoreArray[7]);
    					}
    				}
				}
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
		});		
		
		// CPX View
		cpxView = (RelativeLayout)findViewById(R.id.studyhome_cpi_view);
		cpxAdImageView = (ImageView)findViewById(R.id.study_home_id_cpi_ad_image);
		cpxAdTextView = (TextView)findViewById(R.id.study_home_id_cpi_ad_text);
		cpxAdInfoTitle = (ImageView)findViewById(R.id.study_home_id_infotitle);
		
		//popupview
		mainLayout = (LinearLayout)findViewById(R.id.frag_home_rela_id);
		popupview = View.inflate(this, R.layout.popup_view_notice, null);
		popupWindow = new PopupWindow(popupview, ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT,true);
		popupText = (TextView)popupview.findViewById(R.id.popup_notice_id_text);
		
		edgeSwipe = new EdgeSwipe();
		mainLayout.setOnTouchListener(edgeSwipe);
		
		// CPX Popup view
		//cpxPopupRelative = (RelativeLayout)findViewById(R.id.rgregisteremailinfo_id_main_activity);
		cpxPopupView = View.inflate(this, R.layout.popup_view, null);
		cpxPopupWindow = new PopupWindow(cpxPopupView, ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT,true);
		cpxPopupText = (TextView)cpxPopupView.findViewById(R.id.popup_id_text);
		
		//TODO 
		new GetNotice().execute("http://www.todpop.co.kr/api/etc/main_notice.json");
		new GetKakao().execute("http://todpop.co.kr/api/app_infos/get_cacao_msg.json");
	}		

	@Override
	public void onResume()
	{
		super.onResume();

		com.facebook.AppEventsLogger.activateApp(this, "218233231697811");

		//if current page is not current category setting, get different between two and change page
		if(categoryPager.getCurrentItem() % 4 != (studyInfo.getInt("currentCategory", 1) - 1)){
			int different = categoryPager.getCurrentItem() % 4 - (studyInfo.getInt("currentCategory", 1) - 1);
			categoryPager.setCurrentItem(categoryPager.getCurrentItem() - different, false);
		}
		else{
			getInfo();
		}
		//pagerAdapter.notifyDataSetChanged();
		
		// Get CPX Info onResume

		SharedPreferences cpxInfo = getSharedPreferences("cpxInfo",0);

		cpxAdId = cpxInfo.getInt("adId", 0);
		cpxAdType = cpxInfo.getInt("adType", 0);	
		cpxAdImageUrl = cpxInfo.getString("adImageUrl", "");
		cpxAdText = cpxInfo.getString("adText", "");
		cpxTargetUrl = cpxInfo.getString("targetUrl", "");
		cpxPackageName = cpxInfo.getString("packageName", "");
		cpxConfirmUrl = cpxInfo.getString("confirmUrl", "");
		cpxReward = cpxInfo.getString("reward", "0");
		cpxPoint = cpxInfo.getString("point", "0");
		cpxQuestionCount = cpxInfo.getInt("questionCount", 0);
		
		// Download CPX Image and update UI
		if(cpxAdType != 0){
			Log.i("STEVEN", "download image task called");
			if(cpxReward.equals("0") || cpxReward.equals("null")){
				cpxAdInfoTitle.setBackgroundResource(R.drawable.test_27_image_pointinfotitle);
			}
			new DownloadImageTask().execute(cpxAdImageUrl);
		}
		//cpxAdType = 0;
		cpxInfo.edit().clear().commit();
		if (cpxAdType == 301) {

			FlurryAgent.logEvent("CPI");
			cpxView.setVisibility(View.VISIBLE);
			
			// Send CPX Log
			new SendCPXLog().execute("http://todpop.co.kr/api/advertises/set_cpx_log.json?ad_id="+cpxAdId+
					"&ad_type=" + cpxAdType +"&user_id=" + userId + "&act=1");
			if (this.checkIsAppInstalled(cpxPackageName)) {
				cpxHistoryFlag = true;
				// App Installed Send act=4 to server
				new SendCPXLog().execute("http://todpop.co.kr/api/advertises/set_cpx_log.json?ad_id="+cpxAdId+
						"&ad_type=" + cpxAdType +"&user_id=" + userId + "&act=4");
			} else {
				cpxHistoryFlag = false;
				// Process CPI
				new SendCPXLog().execute("http://todpop.co.kr/api/advertises/set_cpx_log.json?ad_id="+cpxAdId+
						"&ad_type=" + cpxAdType +"&user_id=" + userId + "&act=2");	
			}
		}else if(cpxAdType == 303){
			FlurryAgent.logEvent("CPA");
			cpxView.setVisibility(View.VISIBLE);
			
			SharedPreferences.Editor cpxInfoEditor;
			cpxInfoEditor = cpxInfo.edit();

			cpxInfoEditor.putInt("adId", cpxAdId);
			cpxInfoEditor.commit();
			
			// Send CPX Log
			new SendCPXLog().execute("http://todpop.co.kr/api/advertises/set_cpx_log.json?ad_id="+cpxAdId+
					"&ad_type=" + cpxAdType +"&user_id=" + userId + "&act=1");

			//get phone number
			String mobile;
			try {
				TelephonyManager phoneMgr=(TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE); 
				mobile =phoneMgr.getLine1Number().toString();
				mobile = mobile.replace("+82", "0");
			} catch(Exception e) {
				mobile = "010test0000";
			}
			
			new CheckCPA().execute(cpxConfirmUrl + "?mobile=" + mobile);
		} else if (cpxAdType == 305) {
			FlurryAgent.logEvent("CPS");
			cpxView.setVisibility(View.VISIBLE);
			
			SharedPreferences.Editor cpxInfoEditor;
			cpxInfoEditor = cpxInfo.edit();

			cpxInfoEditor.putInt("adId", cpxAdId);
			cpxInfoEditor.commit();
			
			// Send CPX Log
			new SendCPXLog().execute("http://todpop.co.kr/api/advertises/set_cpx_log.json?ad_id="+cpxAdId+
					"&ad_type=" + cpxAdType +"&user_id=" + userId + "&act=1");
			
			new SendCPXLog().execute("http://todpop.co.kr/api/advertises/set_cpx_log.json?ad_id="+cpxAdId+
					"&ad_type=" + cpxAdType +"&user_id=" + userId + "&act=2");
		} else {
			
			SharedPreferences cpxInstallInfo = getSharedPreferences("cpxInstallInfo",0);
			boolean goMyDownload = cpxInstallInfo.getBoolean("cpxGoMyDownload", false);
			
			cpxView.setVisibility(View.GONE);			
			
			if (goMyDownload == true) {
				// Save to DB and JUMP to HomwDownload activity
				Intent intent = new Intent(getApplicationContext(), HomeDownload.class);
				startActivity(intent);
			} 
		}
	}
	
	public class StudyHomePagerAdapter extends PagerAdapter{
		private LayoutInflater mInflater;
		
		public StudyHomePagerAdapter(Context c) {
			super();
			mInflater = LayoutInflater.from(c);
		}

		@Override
		public int getCount() {
            return Integer.MAX_VALUE;
		}

		@Override
		public Object instantiateItem(View container, int position) {
			View v = null;
			v = mInflater.inflate(R.layout.fragment_study_home_category, null);
			RadioButton weekBtn = (RadioButton)v.findViewById(R.id.study_home_category_week);
			RadioButton monthBtn = (RadioButton)v.findViewById(R.id.study_home_category_month);
			if(studyInfo.getInt("currentPeriod", 1) == 1){
				weekBtn.setChecked(true);
			}
			else{
				monthBtn.setChecked(true);
			}
			weekBtn.setOnClickListener(radioButton);
			monthBtn.setOnClickListener(radioButton);
							
			switch(position%4){
			case 0:
				weekBtn.setButtonDrawable(R.drawable.studyhome_drawable_ranklist_btn_basicweek);
				monthBtn.setButtonDrawable(R.drawable.studyhome_drawable_ranklist_btn_basicmonth);
				break;
			case 1:
				weekBtn.setButtonDrawable(R.drawable.studyhome_drawable_ranklist_btn_middleweek);
				monthBtn.setButtonDrawable(R.drawable.studyhome_drawable_ranklist_btn_middlemonth);
				break;
			case 2:
				weekBtn.setButtonDrawable(R.drawable.studyhome_drawable_ranklist_btn_highweek);
				monthBtn.setButtonDrawable(R.drawable.studyhome_drawable_ranklist_btn_highmonth);
				break;
			case 3:
				weekBtn.setButtonDrawable(R.drawable.studyhome_drawable_ranklist_btn_toeicweek);
				monthBtn.setButtonDrawable(R.drawable.studyhome_drawable_ranklist_btn_toeicmonth);
				break;
			}	
			((ViewPager)container).addView(v, 0);
			return v;
		}
	    @Override
	    public void destroyItem(ViewGroup container, int position, Object object) {
	        ((ViewPager) container).removeView((View) object);
	    }
	    public int getItemPosition(Object object){
	        return POSITION_NONE;
	   }

		@Override
		public boolean isViewFromObject(View pager, Object obj) {
			return pager == obj;
		}

		OnClickListener radioButton = new OnClickListener(){
			public void onClick(View v){
				switch(v.getId()){
				case R.id.study_home_category_week:
					studyInfoEdit.putInt("currentPeriod", 1);
					studyInfoEdit.commit();
					switch(categoryPager.getCurrentItem() % 4){
						case 0:
							if(rankingItemArray_basicWeek.isEmpty()){
	    						new GetRank(1).execute("http://todpop.co.kr/api/users/get_users_score.json?category="+
	    								1+"&period="+1+"&nickname="+pref.getString("nickname", "NO"));
	    					}
	    					else{
	    						rankingListAdapter = new RankingListAdapter(StudyHome.this,R.layout.home_rank_list_item_view, rankingItemArray_basicWeek);
	    						rankingList.setAdapter(rankingListAdapter);
	    						myRank.setText(myRankArray[0]);
	    						myScore.setText(myScoreArray[0]);
	    					}
							break;
						case 1:
							if(rankingItemArray_middleWeek.isEmpty()){
	    						new GetRank(2).execute("http://todpop.co.kr/api/users/get_users_score.json?category="+
	    								2+"&period="+1+"&nickname="+pref.getString("nickname", "NO"));
	    					}
	    					else{
	    						rankingListAdapter = new RankingListAdapter(StudyHome.this,R.layout.home_rank_list_item_view, rankingItemArray_middleWeek);
	    						rankingList.setAdapter(rankingListAdapter);
	    						myRank.setText(myRankArray[1]);
	    						myScore.setText(myScoreArray[1]);
	    					}
							break;
						case 2:
							if(rankingItemArray_highWeek.isEmpty()){
	    						new GetRank(3).execute("http://todpop.co.kr/api/users/get_users_score.json?category="+
	    								3+"&period="+1+"&nickname="+pref.getString("nickname", "NO"));
	    					}
	    					else{
	    						rankingListAdapter = new RankingListAdapter(StudyHome.this,R.layout.home_rank_list_item_view, rankingItemArray_highWeek);
	    						rankingList.setAdapter(rankingListAdapter);
	    						myRank.setText(myRankArray[2]);
	    						myScore.setText(myScoreArray[2]);
	    					}
							break;
						case 3:
							if(rankingItemArray_toeicWeek.isEmpty()){
	    						new GetRank(4).execute("http://todpop.co.kr/api/users/get_users_score.json?category="+
	    								4+"&period="+1+"&nickname="+pref.getString("nickname", "NO"));
	    					}
	    					else{
	    						rankingListAdapter = new RankingListAdapter(StudyHome.this,R.layout.home_rank_list_item_view, rankingItemArray_toeicWeek);
	    						rankingList.setAdapter(rankingListAdapter);
	    						myRank.setText(myRankArray[3]);
	    						myScore.setText(myScoreArray[3]);
	    					}
							break;
					}
					
					break;
				case R.id.study_home_category_month:
					studyInfoEdit.putInt("currentPeriod", 2);
					studyInfoEdit.commit();
					switch(categoryPager.getCurrentItem() % 4){
						case 0:
	    					if(rankingItemArray_basicMonth.isEmpty()){
	    						new GetRank(5).execute("http://todpop.co.kr/api/users/get_users_score.json?category="+
	    								1+"&period="+2+"&nickname="+pref.getString("nickname", "NO"));
	    					}
	    					else{
	    						rankingListAdapter = new RankingListAdapter(StudyHome.this,R.layout.home_rank_list_item_view, rankingItemArray_basicMonth);
	    						rankingList.setAdapter(rankingListAdapter);
	    						myRank.setText(myRankArray[4]);
	    						myScore.setText(myScoreArray[4]);
	    					}
							break;
						case 1:
	    					if(rankingItemArray_middleMonth.isEmpty()){
	    						new GetRank(6).execute("http://todpop.co.kr/api/users/get_users_score.json?category="+
	    								2+"&period="+2+"&nickname="+pref.getString("nickname", "NO"));
	    					}
	    					else{
	    						rankingListAdapter = new RankingListAdapter(StudyHome.this,R.layout.home_rank_list_item_view, rankingItemArray_middleMonth);
	    						rankingList.setAdapter(rankingListAdapter);
	    						myRank.setText(myRankArray[5]);
	    						myScore.setText(myScoreArray[5]);
	    					}
							break;
						case 2:
	    					if(rankingItemArray_highMonth.isEmpty()){
	    						new GetRank(7).execute("http://todpop.co.kr/api/users/get_users_score.json?category="+
	    								3+"&period="+2+"&nickname="+pref.getString("nickname", "NO"));
	    					}
	    					else{
	    						rankingListAdapter = new RankingListAdapter(StudyHome.this,R.layout.home_rank_list_item_view, rankingItemArray_highMonth);
	    						rankingList.setAdapter(rankingListAdapter);
	    						myRank.setText(myRankArray[6]);
	    						myScore.setText(myScoreArray[6]);
	    					}
							break;
						case 3:
	    					if(rankingItemArray_toeicMonth.isEmpty()){
	    						new GetRank(8).execute("http://todpop.co.kr/api/users/get_users_score.json?category="+
	    								4+"&period="+2+"&nickname="+pref.getString("nickname", "NO"));
	    					}
	    					else{
	    						rankingListAdapter = new RankingListAdapter(StudyHome.this,R.layout.home_rank_list_item_view, rankingItemArray_toeicMonth);
	    						rankingList.setAdapter(rankingListAdapter);
	    						myRank.setText(myRankArray[7]);
	    						myScore.setText(myScoreArray[7]);
	    					}
							break;
					}
					
					break;
				}
				categoryPager.getAdapter().notifyDataSetChanged();
			}
		};
	}	
	
	
	
	public void goLeft(View v){
		Log.i("STEVEN", "go left");
		categoryPager.setCurrentItem(categoryPager.getCurrentItem()+1, true);
		Log.i("STEVEN", "go left done");
	}
	public void goRight(View v){
		Log.i("STEVEN", "go right");
		categoryPager.setCurrentItem(categoryPager.getCurrentItem()-1, true);
		Log.i("STEVEN", "go right done");
	}
	
	/*@Override
	public void onRestart(){
		super.onRestart();
		//if current page is not current category setting, get different between two and change page
		if(categoryPager.getCurrentItem() % 4 != (studyInfo.getInt("currentCategory", 1) - 1)){
			int different = categoryPager.getCurrentItem() % 4 - (studyInfo.getInt("currentCategory", 1) - 1);
			categoryPager.setCurrentItem(categoryPager.getCurrentItem() - different, false);
		}
		else{
			getInfo();
		}
	}*/
	// ******************** CPA signed up Check *************************
		private class CheckCPA extends AsyncTask<String, Void, JSONObject> {
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
						Log.d("RESPONSE ---- ", result.toString());				        	
					}
					return result;
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				return result;
			}

			@Override
			protected void onPostExecute(JSONObject result) 
			{

				try {
					if	(result.getBoolean("status")==true) {
						new SendCPXLog().execute("http://todpop.co.kr/api/advertises/set_cpx_log.json?ad_id="+cpxAdId+
								"&ad_type=" + cpxAdType +"&user_id=" + userId + "&act=4");
						cpxHistoryFlag = true;
						Log.d("CPX LOG:  ---- ", "Send CPX Log OK!");
					} else {
						new SendCPXLog().execute("http://todpop.co.kr/api/advertises/set_cpx_log.json?ad_id="+cpxAdId+
								"&ad_type=" + cpxAdType +"&user_id=" + userId + "&act=2");
						cpxHistoryFlag = false;
						Log.d("CPX LOG:  ---- ", "Send CPX Log Failed!");
					}

				} catch (Exception e) {

				}
			}
		}
	// ******************** CPX UTILITY CLASS *************************
	private class SendCPXLog extends AsyncTask<String, Void, JSONObject> {
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
					Log.d("RESPONSE ---- ", result.toString());				        	
				}
				return result;
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return result;
		}

		@Override
		protected void onPostExecute(JSONObject result) 
		{

			try {
				if	(result.getBoolean("status")==true) {
					Log.d("CPX LOG:  ---- ", "Send CPX Log OK!");
				} else {
					Log.d("CPX LOG:  ---- ", "Send CPX Log Failed!");
				}

			} catch (Exception e) {

			}
		}
	}
	
	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
	    protected Bitmap doInBackground(String... urls) {
	        String urldisplay = urls[0];
	        Bitmap mIcon11 = null;
	        try {
	            InputStream in = new java.net.URL(urldisplay).openStream();
	            mIcon11 = BitmapFactory.decodeStream(in);
	        } catch (Exception e) {
	            Log.e("Error", e.getMessage());
	            e.printStackTrace();
	        }
	        return mIcon11;
	    }

	    protected void onPostExecute(Bitmap result) 
	    {	        
	    	// Update UI
	    	cpxAdImageView.setImageBitmap(result);
	    	cpxAdTextView.setText(cpxAdText);
	    }
	}
	// ******************** End of CPX UTILITY CLASS *************************


	
	public void getInfo()
	{
		pref = getSharedPreferences("rgInfo",0);

		FlurryAgent.setUserId(pref.getString("mem_id", "NO"));
		
		category = studyInfo.getInt("currentCategory", 1);
		period = studyInfo.getInt("currentPeriod", 1);
		
		Log.e("STEVEN", "current category is " + category + "and period is " + period);
		
		//clear every list
		rankingItemArray_basicWeek.clear();
		rankingItemArray_basicMonth.clear();
		rankingItemArray_middleWeek.clear();
		rankingItemArray_middleMonth.clear();
		rankingItemArray_highWeek.clear();
		rankingItemArray_highMonth.clear();
		rankingItemArray_toeicWeek.clear();
		rankingItemArray_toeicMonth.clear();
		
		//get weekly, monthly rank
		new GetRank(category).execute("http://todpop.co.kr/api/users/get_users_score.json?category="+
				category+"&period="+1+"&nickname="+pref.getString("nickname", "NO"));
		Log.e("STEVEN", "609");
		/*new GetRank(category + 4).execute("http://todpop.co.kr/api/users/get_users_score.json?category="+
				category+"&period="+2+"&nickname="+pref.getString("nickname", "NO"));*/
	}

	class RankingListItem{
		RankingListItem(String aRank,String aImageNO,String aName,String  aFraction)
		{
			rank = aRank;
			imageNO = aImageNO;
			name =aName;
			fraction = aFraction;
		}
		String rank;
		String imageNO;
		String name;
		String fraction;
	}


	class RankingListAdapter extends BaseAdapter
	{
		Context maincon;
		LayoutInflater Inflater;
		ArrayList<RankingListItem> arSrc;
		int layout;

		public RankingListAdapter(Context context,int alayout,ArrayList<RankingListItem> aarSrc)
		{
			maincon = context;
			Inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			arSrc = aarSrc;
			layout = alayout;
		}
		
		public int getCount()
		{
			return 6;//arSrc.size();
		}

		public String getItem(int position)
		{
			return arSrc.get(position).rank;
		}

		public long getItemId(int position)
		{
			return position;
		}

		public View getView(int position,View convertView,ViewGroup parent)
		{
			if(convertView == null) {
				convertView = Inflater.inflate(layout, parent,false);
			}
			
			ImageView crown = (ImageView)convertView.findViewById(R.id.home_list_id_rank_crown);
			TextView rank = (TextView)convertView.findViewById(R.id.home_list_id_rank_rank);

			ImageView rankImage = (ImageView)convertView.findViewById(R.id.home_list_id_rank_user_image);
			TextView name = (TextView)convertView.findViewById(R.id.home_list_id_rank_name_text);
			TextView fraction = (TextView)convertView.findViewById(R.id.home_list_rank_id_user_fraction);
			
			setRankImage(arSrc.get(position).imageNO, rankImage);

			if(position<3) {

				if(position == 0) {
					crown.setImageResource(R.drawable.home_image_goldcrown);
				} else if(position == 1) {
					crown.setImageResource(R.drawable.home_image_silvercrown);
				} else if(position == 2) {
					crown.setImageResource(R.drawable.home_image_coppercrown);
				}

				rank.setTextColor(Color.parseColor("#E6947F"));
				rank.setText(arSrc.get(position).rank);

				name.setTextColor(Color.parseColor("#E6947F"));
				name.setText(arSrc.get(position).name);

				fraction.setTextColor(Color.parseColor("#E6947F"));
				fraction.setText(arSrc.get(position).fraction+getResources().getString(R.string.home_list_score_string));
			} else {
				rank.setTextColor(Color.parseColor("#000000"));
				rank.setText(arSrc.get(position).rank);

				name.setTextColor(Color.parseColor("#000000"));
				name.setText(arSrc.get(position).name);

				fraction.setTextColor(Color.parseColor("#000000"));
				fraction.setText(arSrc.get(position).fraction+getResources().getString(R.string.home_list_score_string));
			}
			return convertView;
		}
	}

	private class GetRank extends AsyncTask<String, Void, JSONObject> {
		int category_period;
		
		public GetRank(int input){
			category_period = input;
		}
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
		protected void onPostExecute(JSONObject result) 
		{

			try {
				if	(result.getBoolean("status")==true) {
					JSONArray jsonArray = result.getJSONObject("data").getJSONArray("score");
					
					switch(category_period){
					case 1:
						for(int i=0; i<6; i++) {
							rankingItem = new RankingListItem(jsonArray.getJSONObject(i).getString("rank"),
									jsonArray.getJSONObject(i).getString("image"),jsonArray.getJSONObject(i).getString("name"),jsonArray.getJSONObject(i).getString("score"));
							rankingItemArray_basicWeek.add(rankingItem);
						}	
						rankingListAdapter = new RankingListAdapter(StudyHome.this,R.layout.home_rank_list_item_view, rankingItemArray_basicWeek);
						rankingList.setAdapter(rankingListAdapter);
						myRankArray[0] = result.getJSONObject("data").getJSONObject("mine").getString("rank");
						myScoreArray[0] = result.getJSONObject("data").getJSONObject("mine").getString("score")+getResources().getString(R.string.home_list_score_string);
						myRank.setText(myRankArray[0]);
						myScore.setText(myScoreArray[0]);
						break;
					case 2:
						for(int i=0; i<6; i++) {
							rankingItem = new RankingListItem(jsonArray.getJSONObject(i).getString("rank"),
									jsonArray.getJSONObject(i).getString("image"),jsonArray.getJSONObject(i).getString("name"),jsonArray.getJSONObject(i).getString("score"));
							rankingItemArray_middleWeek.add(rankingItem);
						}	
						rankingListAdapter = new RankingListAdapter(StudyHome.this,R.layout.home_rank_list_item_view, rankingItemArray_middleWeek);
						rankingList.setAdapter(rankingListAdapter);
						myRankArray[1] = result.getJSONObject("data").getJSONObject("mine").getString("rank");
						myScoreArray[1] = result.getJSONObject("data").getJSONObject("mine").getString("score")+getResources().getString(R.string.home_list_score_string);
						myRank.setText(myRankArray[1]);
						myScore.setText(myScoreArray[1]);
						break;
					case 3:
						for(int i=0; i<6; i++) {
							rankingItem = new RankingListItem(jsonArray.getJSONObject(i).getString("rank"),
									jsonArray.getJSONObject(i).getString("image"),jsonArray.getJSONObject(i).getString("name"),jsonArray.getJSONObject(i).getString("score"));
							rankingItemArray_highWeek.add(rankingItem);
						}	
						rankingListAdapter = new RankingListAdapter(StudyHome.this,R.layout.home_rank_list_item_view, rankingItemArray_highWeek);
						rankingList.setAdapter(rankingListAdapter);
						myRankArray[2] = result.getJSONObject("data").getJSONObject("mine").getString("rank");
						myScoreArray[2] = result.getJSONObject("data").getJSONObject("mine").getString("score")+getResources().getString(R.string.home_list_score_string);
						myRank.setText(myRankArray[2]);
						myScore.setText(myScoreArray[2]);
						break;
					case 4:
						for(int i=0; i<6; i++) {
							rankingItem = new RankingListItem(jsonArray.getJSONObject(i).getString("rank"),
									jsonArray.getJSONObject(i).getString("image"),jsonArray.getJSONObject(i).getString("name"),jsonArray.getJSONObject(i).getString("score"));
							rankingItemArray_toeicWeek.add(rankingItem);
						}	
						rankingListAdapter = new RankingListAdapter(StudyHome.this,R.layout.home_rank_list_item_view, rankingItemArray_toeicWeek);
						rankingList.setAdapter(rankingListAdapter);
						myRankArray[3] = result.getJSONObject("data").getJSONObject("mine").getString("rank");
						myScoreArray[3] = result.getJSONObject("data").getJSONObject("mine").getString("score")+getResources().getString(R.string.home_list_score_string);
						myRank.setText(myRankArray[3]);
						myScore.setText(myScoreArray[3]);
						break;
					case 5:
						for(int i=0; i<6; i++) {
							rankingItem = new RankingListItem(jsonArray.getJSONObject(i).getString("rank"),
									jsonArray.getJSONObject(i).getString("image"),jsonArray.getJSONObject(i).getString("name"),jsonArray.getJSONObject(i).getString("score"));
							rankingItemArray_basicMonth.add(rankingItem);
						}	
						rankingListAdapter = new RankingListAdapter(StudyHome.this,R.layout.home_rank_list_item_view, rankingItemArray_basicMonth);
						rankingList.setAdapter(rankingListAdapter);
						myRankArray[4] = result.getJSONObject("data").getJSONObject("mine").getString("rank");
						myScoreArray[4] = result.getJSONObject("data").getJSONObject("mine").getString("score")+getResources().getString(R.string.home_list_score_string);
						myRank.setText(myRankArray[4]);
						myScore.setText(myScoreArray[4]);
						break;
					case 6: 
						for(int i=0; i<6; i++) {
							rankingItem = new RankingListItem(jsonArray.getJSONObject(i).getString("rank"),
									jsonArray.getJSONObject(i).getString("image"),jsonArray.getJSONObject(i).getString("name"),jsonArray.getJSONObject(i).getString("score"));
							rankingItemArray_middleMonth.add(rankingItem);
						}	
						rankingListAdapter = new RankingListAdapter(StudyHome.this,R.layout.home_rank_list_item_view, rankingItemArray_middleMonth);
						rankingList.setAdapter(rankingListAdapter);
						myRankArray[5] = result.getJSONObject("data").getJSONObject("mine").getString("rank");
						myScoreArray[5] = result.getJSONObject("data").getJSONObject("mine").getString("score")+getResources().getString(R.string.home_list_score_string);
						myRank.setText(myRankArray[5]);
						myScore.setText(myScoreArray[5]);
						break;
					case 7:
						for(int i=0; i<6; i++) {
							rankingItem = new RankingListItem(jsonArray.getJSONObject(i).getString("rank"),
									jsonArray.getJSONObject(i).getString("image"),jsonArray.getJSONObject(i).getString("name"),jsonArray.getJSONObject(i).getString("score"));
							rankingItemArray_highMonth.add(rankingItem);
						}	
						rankingListAdapter = new RankingListAdapter(StudyHome.this,R.layout.home_rank_list_item_view, rankingItemArray_highMonth);
						rankingList.setAdapter(rankingListAdapter);
						myRankArray[6] = result.getJSONObject("data").getJSONObject("mine").getString("rank");
						myScoreArray[6] = result.getJSONObject("data").getJSONObject("mine").getString("score")+getResources().getString(R.string.home_list_score_string);
						myRank.setText(myRankArray[6]);
						myScore.setText(myScoreArray[6]);
						break;
					case 8:
						for(int i=0; i<6; i++) {
							rankingItem = new RankingListItem(jsonArray.getJSONObject(i).getString("rank"),
									jsonArray.getJSONObject(i).getString("image"),jsonArray.getJSONObject(i).getString("name"),jsonArray.getJSONObject(i).getString("score"));
							rankingItemArray_toeicMonth.add(rankingItem);
						}	
						rankingListAdapter = new RankingListAdapter(StudyHome.this,R.layout.home_rank_list_item_view, rankingItemArray_toeicMonth);
						rankingList.setAdapter(rankingListAdapter);
						myRankArray[7] = result.getJSONObject("data").getJSONObject("mine").getString("rank");
						myScoreArray[7] = result.getJSONObject("data").getJSONObject("mine").getString("score")+getResources().getString(R.string.home_list_score_string);
						myRank.setText(myRankArray[7]);
						myScore.setText(myScoreArray[7]);
						break;
					}					
					setRankImage(result.getJSONObject("data").getJSONObject("mine").getString("image"), myImage);
					myName.setText(result.getJSONObject("data").getJSONObject("mine").getString("name"));
				}

			} catch (Exception e) {

			}
		}
	}	
	
	public void setRankImage(String imageID,ImageView mRankImage)
	{		
		if(imageID.equals("1")){
			mRankImage.setImageResource(R.drawable.home_character_eric);
		}else if(imageID.equals("2")){
			mRankImage.setImageResource(R.drawable.home_character_selly);
		}else if(imageID.equals("3")){
			mRankImage.setImageResource(R.drawable.home_character_john);
		}else if(imageID.equals("4")){
			mRankImage.setImageResource(R.drawable.home_character_amanda);
		}else if(imageID.equals("5")){
			mRankImage.setImageResource(R.drawable.home_character_tom);
		}else if(imageID.equals("6")){
			mRankImage.setImageResource(R.drawable.home_character_jenny);
		}else if(imageID.equals("7")){
			mRankImage.setImageResource(R.drawable.home_character_monkey);
		}else{
			mRankImage.setImageResource(R.drawable.home_character_dino);
		}
	}

	private  AnimatorListener mAnimationListener = new AnimatorListenerAdapter() {
		public void onAnimationEnd(Animator animation) {}
		public void onAnimationCancel(Animator animation) {}
		public void onAnimationRepeat(Animator animation) {}
		public void onAnimationStart(Animator animation) {}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.study_home, menu);
		return false;
	}

	public void showShop(View view)
	{
	}

	// Sidebar Menu Callback
	public void slideMenuBtnCB(View v) {
		if (isOnSlide == false) {
			FlurryAgent.logEvent("Slide Button Clicked (On)");
			slideOn();
		} else {
			FlurryAgent.logEvent("Slide Button Clicked (Off)");
			slideOff();
		}
	}
	public void otherSide(View v){
		slideOff();
	}
	public void slideOn(){
		isOnSlide = true;
		otherSideView.setVisibility(View.VISIBLE);

		float density = getResources().getDisplayMetrics().density;
		
		ObjectAnimator slideAni;
		LinearLayout rl = (LinearLayout)findViewById(R.id.frag_home_rela_id);
		slideAni = ObjectAnimator.ofFloat(rl, "x",0*density, (173/2)*density);

		slideAni.addListener(mAnimationListener);

		slideAni.setDuration(300);
		slideAni.start();
	}
	public void slideOff(){
		isOnSlide = false;
		otherSideView.setVisibility(View.GONE);

		float density = getResources().getDisplayMetrics().density;
		
		ObjectAnimator slideAni;
		LinearLayout rl = (LinearLayout)findViewById(R.id.frag_home_rela_id);
		slideAni = ObjectAnimator.ofFloat(rl, "x", (173/2)*density,0*density);

		slideAni.addListener(mAnimationListener);

		slideAni.setDuration(300);
		slideAni.start();
	}
	//--- request class ---
	private class GetKakao extends AsyncTask<String, Void, JSONObject> 
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
					Log.d("RESPONSE JSON CHECK MOBILE EXIST ---- ", result.toString());				        	
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
				kakaoMent = json.getJSONObject("data").getString("ment");
				kaokaoAndroidUrl = json.getJSONObject("data").getString("android_url");
				iosUrl = json.getJSONObject("data").getString("ios_url");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	private class GetNotice extends AsyncTask<String, Void, JSONObject> 
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
					Log.d("RESPONSE JSON CHECK GET NOTICE ---- ", result.toString());				        	
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
				
				String curVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
				String newVersion = json.getJSONObject("data").getString("android_version");
				
				Log.i("cys c=",curVersion);
				Log.i("cys n=",newVersion);
				int curA, curB, curC, newA, newB, newC;
				curA = Integer.valueOf(curVersion.substring(0, 1));
				curB = Integer.valueOf(curVersion.substring(2, 3));
				curC = Integer.valueOf(curVersion.substring(4, curVersion.length()));
				newA = Integer.valueOf(newVersion.substring(0, 1));
				newB = Integer.valueOf(newVersion.substring(2, 3));
				newC = Integer.valueOf(newVersion.substring(4, newVersion.length()));

				int curVersionInt = curA*1000000 + curB*1000 + curC;
				int newVersionInt = newA*1000000 + newB*1000 + newC;
				
				if(curVersionInt < newVersionInt){
					noticeList.add(getResources().getString(R.string.study_home_popup_version_check) 
							+ "\ncurrent version = " + curVersion + "\nnew version =" + newVersion);

					if(curA != newA || curB != newB){
						majorVersionUpdate = true;
					}
				}
				JSONArray noticeArray = json.getJSONObject("data").getJSONArray("ment");
				for(int i = 0; i < noticeArray.length(); i++){
					String notice = noticeArray.getString(i);
					notice = notice.replace("\\n", "\n");
					noticeList.add(notice);
				}
					
				popupText.setText(noticeList.get(0));
				noticeList.remove(0);
				Log.i("STEVEN", "just before pop");
				popupWindow.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);
				Log.i("STEVNE", "just before drop down");
				popupWindow.showAsDropDown(null);
			} catch (Exception e) {

				Log.e("STEVEN", "app version check and notice something wrong");
			}
		}
	}


	// on click
	public void showStudyCategory(View view)
	{
		Intent intent = new Intent(getApplicationContext(), StudyCategory.class);
		intent.putExtra("from_home", true);
		startActivity(intent);
		//finish();
	}

	public void showHomeStore(View view)
	{
		popupText.setText(R.string.temp_store_closed);
		popupWindow.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);
		
	}

	public void showMore(View view)
	{
		Intent intent = new Intent(getApplicationContext(),HomeMore.class);
		startActivity(intent);
	}

	public void showHomeMyPage(View view)
	{
		Intent intent = new Intent(getApplicationContext(),HomeMyPage.class);
		startActivity(intent);
	}

	public void showHomeDownload(View v)
	{
		Intent intent = new Intent(getApplicationContext(), HomeDownload.class);
		startActivity(intent);
	}

	public void showHomeWordList(View v)
	{
		Intent intent = new Intent(getApplicationContext(), HomeWordList.class);
		startActivity(intent);
	}
	
	
	// CPI Button CB
	public void cpxGoHome(View v)
	{
		FlurryAgent.logEvent("Intall Later");
		SharedPreferences cpxInfo = getSharedPreferences("cpxInfo",0);
		SharedPreferences cpxSInstallInfo = getSharedPreferences("cpxInstallInfo",0);
		cpxInfo.edit().clear().commit();
		cpxSInstallInfo.edit().clear().commit();
		cpxView.setVisibility(View.GONE);
	}
	public void cpxGoReward(View v)
	{	
	
		
		if (cpxAdType == 301) {
			if (cpxHistoryFlag) {
				// App Installed 
				cpxPopupText.setText(R.string.cpi_popup_text);
				cpxPopupWindow.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);
				cpxPopupWindow.showAsDropDown(null);
			} else {
				// Process CPI
				try {
				    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+cpxPackageName)));
				} catch (android.content.ActivityNotFoundException anfe) {
				    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id="+cpxPackageName)));
				}
				
				// Save status and Jump to HomeDownload Activity
				SharedPreferences cpxSInstallInfo = getSharedPreferences("cpxInstallInfo",0);
				SharedPreferences.Editor cpxInstallInfoEditor = cpxSInstallInfo.edit();
				
				//for CpxPackageChangeReceiver.java
				cpxInstallInfoEditor.putInt("cpxAdType", cpxAdType);
				cpxInstallInfoEditor.putInt("cpxAdId", cpxAdId);
				cpxInstallInfoEditor.putString("cpxPackageName", cpxPackageName);
				cpxInstallInfoEditor.putBoolean("isCpxInstalling", true);
				cpxInstallInfoEditor.putBoolean("cpxGoMyDownload", true);
				cpxInstallInfoEditor.commit();			
			}
		} else if(cpxAdType == 303){
			if (cpxHistoryFlag) {
				// already signed up
				cpxPopupText.setText(R.string.cpa_popup_text);
				cpxPopupWindow.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);
				cpxPopupWindow.showAsDropDown(null);
			}else{
				SharedPreferences cpxSInstallInfo = getSharedPreferences("cpxInstallInfo",0);
				SharedPreferences.Editor cpxInstallInfoEditor = cpxSInstallInfo.edit();
				cpxInstallInfoEditor.putBoolean("cpxGoMyDownload", true);
				cpxInstallInfoEditor.commit();			
				Toast toast = Toast.makeText(null, R.string.cpa_install_notice, Toast.LENGTH_LONG);
				toast.show();
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(cpxTargetUrl)));
			}
		}else if (cpxAdType == 305) {
			SharedPreferences cpxInfo = getSharedPreferences("cpxInfo",0);
			SharedPreferences.Editor cpxInfoEditor = cpxInfo.edit();
			cpxInfoEditor.putString("reward", cpxReward);
			cpxInfoEditor.putString("point", cpxPoint);
			cpxInfoEditor.commit();
			Intent intent = new Intent(getApplicationContext(), SurveyView.class);
			startActivity(intent);
		}


	}
	
	// Check if Application is installed
    private boolean checkIsAppInstalled (String uri)
    {
        PackageManager pm = getPackageManager();
        boolean app_installed = false;
        try
        {
               pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
               app_installed = true;
        }
        catch (PackageManager.NameNotFoundException e)
        {
               app_installed = false;
        }
        return app_installed ;
    }

	public void kakaoInvitefriend(View v)throws NameNotFoundException
	{
		slideOff();
		KakaoLink kakaoLink = KakaoLink.getLink(getApplicationContext());

		// check, intent is available.
		if (!kakaoLink.isAvailableIntent()) {
			Toast.makeText(getApplicationContext(), getResources().getString(R.string.study_home_popup_nokakao), 1000).show();
			return;
		}
		else
		{
			ArrayList<Map<String, String>> metaInfoArray = new ArrayList<Map<String, String>>();
			
			Map<String, String> metaInfoAndroid = new Hashtable<String, String>(1);
			metaInfoAndroid.put("os", "android");
			metaInfoAndroid.put("devicetype", "phone");
			metaInfoAndroid.put("installurl", "market://details?id=com.todpop.saltyenglish");		// fix
			metaInfoArray.add(metaInfoAndroid);

			String nickname = pref.getString("nickname", null);
			String strMessage = kakaoMent + nickname +"]";
			String strURL = "http://market.android.com/details?id=com.todpop.saltyenglish";			// fix & hidden
			String strAppId = "com.todpop.saltyenglish";											// fix
			String strAppVer = "0.1.x";																// cannot get real AppVer automatically (no matter)
			String strAppName = getResources().getString(R.string.app_name);
						
			kakaoLink.openKakaoAppLink(StudyHome.this, strURL, strMessage, strAppId, strAppVer, strAppName, "UTF-8", metaInfoArray);
			
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			
			if(cpxView.isShown()){
				FlurryAgent.logEvent("Intall Later");
				SharedPreferences cpxInfo = getSharedPreferences("cpxInfo",0);
				SharedPreferences cpxSInstallInfo = getSharedPreferences("cpxInstallInfo",0);
				cpxInfo.edit().clear().commit();
				cpxSInstallInfo.edit().clear().commit();
				cpxView.setVisibility(View.GONE);
			}
			if(isOnSlide == true){
				FlurryAgent.logEvent("Back Btn slide off");
				slideOff();
			}
			else{
				final AlertDialog.Builder isExit = new AlertDialog.Builder(this);
				DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) 
					{
						switch (which) {
						case AlertDialog.BUTTON_POSITIVE:
							SharedPreferences settings = getSharedPreferences("setting", 0);
							SharedPreferences.Editor editor = settings.edit();
							editor.putString("check","YES");
							editor.commit();

							Intent intent = new Intent();
							intent.setClass(StudyHome.this, MainActivity.class);    
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
			}

			return false;
		}
		return false;
	}
	//----button onClick----
	public void closePopup(View v)
	{
		if(majorVersionUpdate){
			SharedPreferences settings = getSharedPreferences("setting", 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.putString("check","YES");
			editor.commit();

			//clear activities
		    Intent intent = new Intent();
			intent.setClass(StudyHome.this, MainActivity.class);    
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
			startActivity(intent);
			
			//go to market
			Intent market = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.todpop.saltyenglish"));
		    startActivity(market);
		    
		    //close app
			finish();
		}
		else {
			popupWindow.dismiss();
			if(!noticeList.isEmpty()){	//if more then one notice left
				popupText.setText(noticeList.get(0));
				noticeList.remove(0);
				popupWindow.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);
				popupWindow.showAsDropDown(null);
			}
			cpxPopupWindow.dismiss();
			cpxView.setVisibility(View.GONE);
		}
	}
	
	//------- Database Operation ------------------
	private class WordDBHelper extends SQLiteOpenHelper {
		public WordDBHelper(Context context) {
			super(context, "EngWord.db", null, 1);
		}
		
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE cpxInfo ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
					"name TEXT NOT NULL UNIQUE, ad_id INTEGER, ad_type INTEGER, reward INTEGER, installed TEXT);");
		}
		
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS cpxInfo");
			onCreate(db);
		}
	}


	@Override
	public void onDestroy()
	{
		super.onDestroy();
		mHelper.close();
	}
	
	/**
     * PagerAdapter 
     */
	/*public class ImageAdapter extends PagerAdapter {
	    Context context;
	 
	    private int[] basicImages = new int[] {
	        R.drawable.home_text_subtitle_basicmonth_white,
	        R.drawable.home_text_subtitle_basicweek_white
	    };
	    private int[] middleImages = new int[] {
			R.drawable.home_text_subtitle_middlemonth_white,
		    R.drawable.home_text_subtitle_middleweek_white
		};
	    private int[] highImages = new int[] {
		    R.drawable.home_text_subtitle_highmonth_white,
		    R.drawable.home_text_subtitle_highweek_white
		};
	    private int[] toiecImages = new int[] {
			R.drawable.home_text_subtitle_toeicmonth_white,
		    R.drawable.home_text_subtitle_toeicweek_white
		};
	 
	    public ImageAdapter(Context context){
	        this.context=context;
	    }
	    
	    @Override
	    public int getCount() {
            return Integer.MAX_VALUE;
	    }
	      
	    @Override
	        public boolean isViewFromObject(View view, Object object) {
	        return view == ((ImageView) object);
	    }
	      
	    @Override
	    public Object instantiateItem(View container, int position) { 	
	    	
	    	
	        ImageView imageView = new ImageView(container.getContext());
	        imageView.setScaleType(ImageView.ScaleType.CENTER);
	        switch(category){
	        	case 1:
	        		imageView.setImageResource(basicImages[position%2]);
	        		break;
	        	case 2:
	        		imageView.setImageResource(middleImages[position%2]);
	        		break;
	        	case 3:
	        		imageView.setImageResource(highImages[position%2]);
	        		break;
	        	case 4:
	        		imageView.setImageResource(toiecImages[position%2]);
	        		break;
	        	default:
	        		break;
	        }	
	        ((ViewPager) container).addView(imageView, 0);
	 
	        return imageView;
	    }
	      
	    @Override
	    public void destroyItem(ViewGroup container, int position, Object object) {
	        ((ViewPager) container).removeView((ImageView) object);
	    }
	    public int getItemPosition(Object object){
	        return POSITION_NONE;
	   }
	}*/
	public class FixedSpeedScroller extends Scroller {

	    private int mDuration = 1000;

	    public FixedSpeedScroller(Context context) {
	        super(context);
	    }

	    public FixedSpeedScroller(Context context, Interpolator interpolator) {
	        super(context, interpolator);
	    }

	    public FixedSpeedScroller(Context context, Interpolator interpolator, boolean flywheel) {
	        super(context, interpolator, flywheel);
	    }


	    @Override
	    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
	        // Ignore received duration, use fixed one instead
	        super.startScroll(startX, startY, dx, dy, mDuration);
	    }

	    @Override
	    public void startScroll(int startX, int startY, int dx, int dy) {
	        // Ignore received duration, use fixed one instead
	        super.startScroll(startX, startY, dx, dy, mDuration);
	    }
	}
	public class EdgeSwipe implements OnTouchListener{
		
	    private final GestureDetector gestureDetector = new GestureDetector(new GestureListener());

		@Override
		public boolean onTouch(final View view, final MotionEvent motionEvent) {
			// TODO Auto-generated method stub
			return gestureDetector.onTouchEvent(motionEvent);
		}
		private final class GestureListener extends SimpleOnGestureListener {

	        private static final int SWIPE_THRESHOLD = 100;
	        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

	        @Override
	        public boolean onDown(MotionEvent e) {
	        	Log.i("STEVEN", "onDown " + String.valueOf(e.getX()));
	            return true;
	        }

	        @Override
	        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
	        	Log.i("STEVEN", "onFling");
	            boolean result = false;
	            try {
	                float diffY = e2.getY() - e1.getY();
	                float diffX = e2.getX() - e1.getX();
		        	Log.i("STEVEN", String.valueOf(diffX));
		        	Log.i("STEVEN", "e1.getX() = " + String.valueOf(e1.getX()) + "   5.0f = "+ 50f);
	                if(e1.getX() < 50f){
			        	Log.i("STEVEN", "Edge Left");
	                	 if (Math.abs(diffX) > Math.abs(diffY)) {
	                         if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
	     			        	Log.i("STEVEN", "SWIPE_THERSHOLD");
	                             if (diffX > 0) {
	                                 onSwipeRight();
	                             }
	                         }
	                	 }
	                }
	            } catch (Exception exception) {
	                exception.printStackTrace();
	            }
	            return result;
	        }
	    }

	    public void onSwipeRight() {
			if (isOnSlide == false) {
				FlurryAgent.logEvent("StudyHome Edge Slide(On)");
				slideOn();
			}
	    }	
	}
}
