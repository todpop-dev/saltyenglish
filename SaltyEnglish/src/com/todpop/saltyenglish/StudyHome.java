package com.todpop.saltyenglish;


import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import com.todpop.api.LoadingDialog;
import com.todpop.api.NoticeInfo;
import com.todpop.api.TypefaceActivity;
import com.todpop.saltyenglish.db.WordDBHelper;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.Animator.AnimatorListener;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import android.view.View.OnTouchListener;
import android.view.animation.Interpolator;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

public class StudyHome extends TypefaceActivity {
	boolean isOnSlide = false;

	// CPI View show in return from study test
	RelativeLayout cpxView;
	ImageView cpxAdImageView;
	TextView cpxAdTextView;
	TextView cpxAdTextReward;
	ImageView cpxAdInfoTitle;
	Button cpxAdSaveNowBtn;
	Button cpxAdNoButton;
	ImageView cpxAdPlus;
	ProgressBar cpxProgress;
	
	
	boolean closeFlag = false;
	
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
	String[] myRankArray;
	String[] myScoreArray;
	
	
	ArrayList<NoticeInfo> noticeList;
	ArrayList<Bitmap> noticeListImg;

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
	ImageView popupImage;
	TextView popupTitle;
	TextView popupText;
	
	//loading progress dialog
	LoadingDialog loadingDialog;
	
	String kakaoMent = "";
	String kaokaoAndroidUrl = "";
	String iosUrl = "";
	String userId;
	
	boolean majorVersionUpdate = false;
	
	SharedPreferences rgInfo;
	SharedPreferences studyInfo;
	SharedPreferences.Editor studyInfoEdit;
	//SharedPreferences myRankInfo;
	
	ViewPager categoryPager;
	
	int scrollState;
	
	EdgeSwipe edgeSwipe;
	
	int category, period;
	
	//CPX Info
	int cpxAdId;
	int cpxAdType;	
	String cpxAdImageUrl;
	String cpxAdText;
	String cpxAdAction;
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
		
		rgInfo = getSharedPreferences("rgInfo",0);
		userId = rgInfo.getString("mem_id", "0");
		
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
		
		noticeList = new ArrayList<NoticeInfo>();
		noticeListImg = new ArrayList<Bitmap>();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String currentDateandTime = sdf.format(new Date());
		
		studyInfoEdit = studyInfo.edit();
		studyInfoEdit.putString("lastUse", currentDateandTime);
		studyInfoEdit.apply();

		//setting category ViewPager.
		categoryPager = (ViewPager)findViewById(R.id.study_home_id_pager);
		Log.i("STVEN", "407");
		categoryPager.setAdapter(new StudyHomePagerAdapter(this));
		categoryPager.setOffscreenPageLimit(3);
		categoryPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				Log.i("STEVEN=========A", "categoryPager onPageSelected");
				if(arg0%4 == 0){ //basic
					Log.i("STEVEN up", "basic category");
    				studyInfoEdit.putInt("currentCategory", 1);
    				studyInfoEdit.apply();
    				
    				if(rankingItemArray_basicWeek.isEmpty()){
    					new GetRank(1).execute("http://todpop.co.kr/api/users/get_users_score.json?category="+
    						1+"&period="+1+"&nickname="+rgInfo.getString("nickname", "NO"));
    				}
    				else{
    					myRank.setText(myRankArray[0]);
    					myScore.setText(myScoreArray[0]);
    				}
				}
				else if(arg0%4 == 1){		//middle
					Log.i("STEVEN up", "middle category");
    				studyInfoEdit.putInt("currentCategory", 2);
    				studyInfoEdit.apply();
    				
    				if(rankingItemArray_middleWeek.isEmpty()){
    					new GetRank(2).execute("http://todpop.co.kr/api/users/get_users_score.json?category="+
    						2+"&period="+1+"&nickname="+rgInfo.getString("nickname", "NO"));
    				}
    				else{
    					myRank.setText(myRankArray[1]);
    					myScore.setText(myScoreArray[1]);
    				}
				}
				else if(arg0%4 == 2){		//high
					Log.i("STEVEN up", "high category");
    				studyInfoEdit.putInt("currentCategory", 3);
    				studyInfoEdit.apply();
    				
    				if(rankingItemArray_highWeek.isEmpty()){
    					new GetRank(3).execute("http://todpop.co.kr/api/users/get_users_score.json?category="+
    						3+"&period="+1+"&nickname="+rgInfo.getString("nickname", "NO"));
    				}
    				else{
    					myRank.setText(myRankArray[2]);
    					myScore.setText(myScoreArray[2]);
    				}
				}
				else{		//toiec
					Log.i("STEVEN up", "toiec category");
    				studyInfoEdit.putInt("currentCategory", 4);
    				studyInfoEdit.apply();
    				
    				if(rankingItemArray_toeicWeek.isEmpty()){
    					new GetRank(4).execute("http://todpop.co.kr/api/users/get_users_score.json?category="+
    						4+"&period="+1+"&nickname="+rgInfo.getString("nickname", "NO"));
    				}
    				else{
    					myRank.setText(myRankArray[3]);
    					myScore.setText(myScoreArray[3]);
    				}
				}
			}
			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}
		});		
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
		
		// CPX View
		cpxView = (RelativeLayout)findViewById(R.id.studyhome_cpi_view);
		cpxAdImageView = (ImageView)findViewById(R.id.study_home_id_cpi_ad_image);
		cpxAdTextView = (TextView)findViewById(R.id.study_home_id_cpx_type);
		cpxAdTextReward = (TextView)findViewById(R.id.study_home_id_cpx_reward);
		cpxAdInfoTitle = (ImageView)findViewById(R.id.study_home_id_infotitle);
		cpxAdSaveNowBtn = (Button)findViewById(R.id.studyhome_id_save_now);
		cpxAdNoButton = (Button)findViewById(R.id.studyhome_id_go_home);
		cpxAdPlus = (ImageView)findViewById(R.id.study_home_id_cpx_plus);
		cpxProgress = (ProgressBar)findViewById(R.id.studyhome_id_cpx_progress);
		
		//popupview
		mainLayout = (LinearLayout)findViewById(R.id.frag_home_rela_id);
		popupview = View.inflate(this, R.layout.popup_view_notice, null);
		popupWindow = new PopupWindow(popupview, ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT,true);
		//popupImage = (ImageView)popupview.findViewById(R.id.popup_notice_id_img);
		popupTitle = (TextView)popupview.findViewById(R.id.popup_notice_id_content_title);
		popupText = (TextView)popupview.findViewById(R.id.popup_notice_id_content);
		
		setFont(popupTitle);
		setFont(popupText);

		//loading animation dialog
		loadingDialog = new LoadingDialog(this);
		
		edgeSwipe = new EdgeSwipe();
		mainLayout.setOnTouchListener(edgeSwipe);
		
		// CPX Popup view
		//cpxPopupRelative = (RelativeLayout)findViewById(R.id.rgregisteremailinfo_id_main_activity);
		cpxPopupView = View.inflate(this, R.layout.popup_view, null);
		cpxPopupWindow = new PopupWindow(cpxPopupView, ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT,true);
		cpxPopupText = (TextView)cpxPopupView.findViewById(R.id.popup_id_text);
		
		setFont(cpxPopupText);
		
		new GetNotice().execute("http://www.todpop.co.kr/api/etc/main_notice.json");
		new GetKakao().execute("http://todpop.co.kr/api/app_infos/get_cacao_msg.json");
	}		

	@Override
	public void onResume()
	{
		super.onResume();
		com.facebook.AppEventsLogger.activateApp(this, "218233231697811");

		if(isOnSlide)
			slideOff();
		
		getInfo();
		
		// Get CPX Info onResume

		SharedPreferences cpxInfo = getSharedPreferences("cpxInfo",0);

		cpxAdId = cpxInfo.getInt("adId", 0);
		cpxAdType = cpxInfo.getInt("adType", 0);	
		cpxAdImageUrl = cpxInfo.getString("adImageUrl", "");
		cpxAdText = cpxInfo.getString("adText", "");
		cpxAdAction = cpxInfo.getString("adAction", "");
		cpxTargetUrl = cpxInfo.getString("targetUrl", "");
		cpxPackageName = cpxInfo.getString("packageName", "");
		cpxConfirmUrl = cpxInfo.getString("confirmUrl", "");
		cpxReward = cpxInfo.getString("reward", "0");
		cpxPoint = cpxInfo.getString("point", "0");
		cpxQuestionCount = cpxInfo.getInt("questionCount", 0);
		
		// Download CPX Image and update UI
		if(cpxAdType != 0){
			cpxAdInfoTitle.setVisibility(View.VISIBLE);
			cpxAdTextView.setVisibility(View.VISIBLE);
			cpxAdTextReward.setVisibility(View.VISIBLE);
			cpxAdSaveNowBtn.setVisibility(View.VISIBLE);
			cpxAdPlus.setVisibility(View.VISIBLE);
			cpxProgress.setVisibility(View.GONE);
			Log.i("STEVEN", "download image task called");
			if(cpxReward.equals("0") || cpxReward.equals("null")){
				cpxAdInfoTitle.setBackgroundResource(R.drawable.test_27_img_point);
			}
			new DownloadImageTask().execute(cpxAdImageUrl);
		}
		//cpxAdType = 0;
		cpxInfo.edit().clear().apply();
		cpxSendLog();
	}
	
	public class StudyHomePagerAdapter extends PagerAdapter{
		private LayoutInflater mInflater;
		
		public StudyHomePagerAdapter(Context c) {
			super();
			mInflater = LayoutInflater.from(c);
		}
		@Override
		public int getCount() {
            //return Integer.MAX_VALUE;
			return 200;
			//return 4;
		}

		@Override
		public Object instantiateItem(View container, int position) {
			View v = null;
			v = mInflater.inflate(R.layout.fragment_study_home_category, null);
			ImageView weekTitle = (ImageView)v.findViewById(R.id.study_home_category_week);
			ListView rankingList = (ListView)v.findViewById(R.id.studyhome_id_listview);
			ProgressBar rankProgressBar = (ProgressBar)v.findViewById(R.id.studyhome_id_progressBar);
							
			switch(position%4){
			case 0:
				weekTitle.setBackgroundResource(R.drawable.home_text_subtitle_basicweek_pink);
				if(!rankingItemArray_basicWeek.isEmpty()){
					rankingList.setAdapter(new RankingListAdapter(StudyHome.this, rankingItemArray_basicWeek));
					rankProgressBar.setVisibility(View.GONE);
				}
				break;
			case 1:
				weekTitle.setBackgroundResource(R.drawable.home_text_subtitle_middleweek_pink);
				if(!rankingItemArray_middleWeek.isEmpty()){
					rankingList.setAdapter(new RankingListAdapter(StudyHome.this, rankingItemArray_middleWeek));
					rankProgressBar.setVisibility(View.GONE);
				}
				break;
			case 2:
				weekTitle.setBackgroundResource(R.drawable.home_text_subtitle_highweek_pink);
				if(!rankingItemArray_highWeek.isEmpty()){
					rankingList.setAdapter(new RankingListAdapter(StudyHome.this, rankingItemArray_highWeek));
					rankProgressBar.setVisibility(View.GONE);
				}
				break;
			case 3:
				weekTitle.setBackgroundResource(R.drawable.home_text_subtitle_toeicweek_pink);
				if(!rankingItemArray_toeicWeek.isEmpty()){
					rankingList.setAdapter(new RankingListAdapter(StudyHome.this, rankingItemArray_toeicWeek));
					rankProgressBar.setVisibility(View.GONE);
				}
				break;
			}	
			((ViewPager)container).addView(v, 0);
			return v;
		}
	    @Override
	    public void destroyItem(ViewGroup container, int position, Object object) {
	        ((ViewPager) container).removeView((View) object);
	    }
	    @Override
	    public int getItemPosition(Object object){
	        return POSITION_NONE;
	   }

		@Override
		public boolean isViewFromObject(View pager, Object obj) {
			return pager == obj;
		}
	}	
	
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
					
					cpxAdId = adDetails.getInt("ad_id");
					cpxAdType = adDetails.getInt("ad_type");
					cpxAdImageUrl = "http://todpop.co.kr/" + adDetails.getString("ad_image");
					cpxAdText = adDetails.getString("ad_text");
					cpxAdAction = adDetails.getString("ad_action");
					cpxTargetUrl = adDetails.getString("target_url");
					cpxPackageName = adDetails.getString("package_name");
					cpxConfirmUrl = adDetails.getString("confirm_url");
					cpxReward = adDetails.getString("reward");
					cpxPoint = adDetails.getString("point");
					cpxQuestionCount = adDetails.getInt("n_question");
					
					// Download CPX Image and update UI
					if(cpxAdType != 0 && cpxAdType != 300){
						cpxAdInfoTitle.setVisibility(View.VISIBLE);
						cpxAdTextView.setVisibility(View.VISIBLE);
						cpxAdTextReward.setVisibility(View.VISIBLE);
						cpxAdSaveNowBtn.setVisibility(View.VISIBLE);
						cpxAdPlus.setVisibility(View.VISIBLE);
						cpxProgress.setVisibility(View.GONE);
						cpxAdTextView.setText(cpxAdAction);
						if(cpxReward.equals("0") || cpxReward.equals("null")){
					    	cpxAdTextReward.setText(cpxPoint + getResources().getString(R.string.study_home_point));
							cpxAdInfoTitle.setBackgroundResource(R.drawable.test_27_img_point);
						}
						else{
					    	cpxAdTextReward.setText(cpxReward + getResources().getString(R.string.study_home_reward));
						}
						new DownloadImageTask().execute(cpxAdImageUrl);
						cpxSendLog();
					}
					else{
						cpxAdInfoTitle.setVisibility(View.INVISIBLE);
						cpxAdTextView.setVisibility(View.INVISIBLE);
						cpxAdTextReward.setVisibility(View.INVISIBLE);
						cpxAdSaveNowBtn.setVisibility(View.INVISIBLE);
						cpxAdPlus.setVisibility(View.INVISIBLE);
						cpxProgress.setVisibility(View.GONE);
						new DownloadImageTask().execute(cpxAdImageUrl);
					}
				} else {		   
				}
			} catch (Exception e) {
				e.printStackTrace();
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
	    }
	}
	// ******************** End of CPX UTILITY CLASS *************************


	
	public void getInfo()
	{
		rgInfo = getSharedPreferences("rgInfo",0);

		FlurryAgent.setUserId(rgInfo.getString("mem_id", "NO"));
		
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
		
		//categoryPager.getAdapter().notifyDataSetChanged();

		Log.i("STEVEN", "852");
		//categoryPager.setCurrentItem(1073741819 + studyInfo.getInt("currentCategory", 1), false);
		categoryPager.setCurrentItem(99 + studyInfo.getInt("currentCategory", 1), false);
		
		if(period == 1)
			new GetRank(category).execute("http://todpop.co.kr/api/users/get_users_score.json?category="+
					category+"&period=1&nickname="+rgInfo.getString("nickname", "NO"));
		else
			new GetRank(category + 4).execute("http://todpop.co.kr/api/users/get_users_score.json?category="+
					category+"&period=2&nickname="+rgInfo.getString("nickname", "NO"));
		Log.e("STEVEN", "609");
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

		public RankingListAdapter(Context context, ArrayList<RankingListItem> aarSrc)
		{
			maincon = context;
			Inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			arSrc = (ArrayList<RankingListItem>) aarSrc.clone();
		}
		@Override
		public int getCount()
		{
			return 6;//arSrc.size();
		}
		@Override
		public String getItem(int position)
		{
			return arSrc.get(position).rank;
		} 
		@Override
		public long getItemId(int position)
		{
			return position;
		}
		@Override
		public View getView(int position,View convertView,ViewGroup parent)
		{
			if(convertView == null) {
				convertView = Inflater.inflate(R.layout.home_rank_list_item_view, parent, false);
			}
			
			ImageView crown = (ImageView)convertView.findViewById(R.id.home_list_id_rank_crown);
			TextView rank = (TextView)convertView.findViewById(R.id.home_list_id_rank_rank);

			ImageView rankImage = (ImageView)convertView.findViewById(R.id.home_list_id_rank_user_image);
			TextView name = (TextView)convertView.findViewById(R.id.home_list_id_rank_name_text);
			TextView fraction = (TextView)convertView.findViewById(R.id.home_list_rank_id_user_fraction);

			setFont(rank);
			setFont(name);
			setFont(fraction);
			
			RankingListItem posRank = arSrc.get(position);
			setRankImage(posRank.imageNO, rankImage);

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
				fraction.setText(arSrc.get(position).fraction+maincon.getResources().getString(R.string.home_list_score_string));
			} else {
				rank.setTextColor(Color.parseColor("#000000"));
				rank.setText(arSrc.get(position).rank);

				name.setTextColor(Color.parseColor("#000000"));
				name.setText(arSrc.get(position).name);

				fraction.setTextColor(Color.parseColor("#000000"));
				fraction.setText(arSrc.get(position).fraction+maincon.getResources().getString(R.string.home_list_score_string));
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
					Log.i("STEVEN","ff");
					switch(category_period){
					case 1:
						Log.i("STEVEN","case 1");
						for(int i=0; i<6; i++) {
							rankingItem = new RankingListItem(jsonArray.getJSONObject(i).getString("rank"),
									jsonArray.getJSONObject(i).getString("image"),jsonArray.getJSONObject(i).getString("name"),jsonArray.getJSONObject(i).getString("score"));
							rankingItemArray_basicWeek.add(rankingItem);
						}	
						//rankingListAdapter = new RankingListAdapter(StudyHome.this,R.layout.home_rank_list_item_view, rankingItemArray_basicWeek);
						//rankingList.setAdapter(rankingListAdapter);
						myRankArray[0] = result.getJSONObject("data").getJSONObject("mine").getString("rank");
						myScoreArray[0] = result.getJSONObject("data").getJSONObject("mine").getString("score")+getResources().getString(R.string.home_list_score_string);

						myRank.setText(myRankArray[0]);
						myScore.setText(myScoreArray[0]);
						break;
					case 2:
						Log.i("STEVEN","case 2");
						for(int i=0; i<6; i++) {
							rankingItem = new RankingListItem(jsonArray.getJSONObject(i).getString("rank"),
									jsonArray.getJSONObject(i).getString("image"),jsonArray.getJSONObject(i).getString("name"),jsonArray.getJSONObject(i).getString("score"));
							rankingItemArray_middleWeek.add(rankingItem);
						}	
						//rankingListAdapter = new RankingListAdapter(StudyHome.this,R.layout.home_rank_list_item_view, rankingItemArray_middleWeek);
						//rankingList.setAdapter(rankingListAdapter);
						myRankArray[1] = result.getJSONObject("data").getJSONObject("mine").getString("rank");
						myScoreArray[1] = result.getJSONObject("data").getJSONObject("mine").getString("score")+getResources().getString(R.string.home_list_score_string);
						
						myRank.setText(myRankArray[1]);
						myScore.setText(myScoreArray[1]);
						break;
					case 3:
						Log.i("STEVEN","case 3");
						for(int i=0; i<6; i++) {
							rankingItem = new RankingListItem(jsonArray.getJSONObject(i).getString("rank"),
									jsonArray.getJSONObject(i).getString("image"),jsonArray.getJSONObject(i).getString("name"),jsonArray.getJSONObject(i).getString("score"));
							rankingItemArray_highWeek.add(rankingItem);
						}	
						//rankingListAdapter = new RankingListAdapter(StudyHome.this,R.layout.home_rank_list_item_view, rankingItemArray_highWeek);
						//rankingList.setAdapter(rankingListAdapter);
						myRankArray[2] = result.getJSONObject("data").getJSONObject("mine").getString("rank");
						myScoreArray[2] = result.getJSONObject("data").getJSONObject("mine").getString("score")+getResources().getString(R.string.home_list_score_string);
						
						myRank.setText(myRankArray[2]);
						myScore.setText(myScoreArray[2]);
						break;
					case 4:
						Log.i("STEVEN","case 4");
						for(int i=0; i<6; i++) {
							rankingItem = new RankingListItem(jsonArray.getJSONObject(i).getString("rank"),
									jsonArray.getJSONObject(i).getString("image"),jsonArray.getJSONObject(i).getString("name"),jsonArray.getJSONObject(i).getString("score"));
							rankingItemArray_toeicWeek.add(rankingItem);
						}	
						//rankingListAdapter = new RankingListAdapter(StudyHome.this,R.layout.home_rank_list_item_view, rankingItemArray_toeicWeek);
						//rankingList.setAdapter(rankingListAdapter);
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
						//rankingListAdapter = new RankingListAdapter(StudyHome.this,R.layout.home_rank_list_item_view, rankingItemArray_basicMonth);
						//rankingList.setAdapter(rankingListAdapter);
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
						//rankingListAdapter = new RankingListAdapter(StudyHome.this,R.layout.home_rank_list_item_view, rankingItemArray_middleMonth);
						//rankingList.setAdapter(rankingListAdapter);
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
						//rankingListAdapter = new RankingListAdapter(StudyHome.this,R.layout.home_rank_list_item_view, rankingItemArray_highMonth);
						//rankingList.setAdapter(rankingListAdapter);
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
						//rankingListAdapter = new RankingListAdapter(StudyHome.this,R.layout.home_rank_list_item_view, rankingItemArray_toeicMonth);
						//rankingList.setAdapter(rankingListAdapter);
						myRankArray[7] = result.getJSONObject("data").getJSONObject("mine").getString("rank");
						myScoreArray[7] = result.getJSONObject("data").getJSONObject("mine").getString("score")+getResources().getString(R.string.home_list_score_string);

						myRank.setText(myRankArray[7]);
						myScore.setText(myScoreArray[7]);
						break;
					}					

					Log.i("STEVEN","done");
					setRankImage(result.getJSONObject("data").getJSONObject("mine").getString("image"), myImage);
					myName.setText(result.getJSONObject("data").getJSONObject("mine").getString("name"));
					categoryPager.getAdapter().notifyDataSetChanged();
				}

			} catch (Exception e) {
				e.printStackTrace();

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
		//getMenuInflater().inflate(R.menu.study_home, menu);
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
		protected void onPreExecute(){
			super.onPreExecute();
			loadingDialog.show();
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
				loadingDialog.dissmiss();	
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
					noticeList.add(new NoticeInfo(getResources().getString(R.string.study_home_popup_version_title), 
							getResources().getString(R.string.study_home_popup_version_check) 
							+ "\n" + getResources().getString(R.string.study_home_popup_version_current) + " " + curVersion 
							+ "\n" + getResources().getString(R.string.study_home_popup_version_latest) + " " + newVersion));

					if(curA != newA || curB != newB){
						majorVersionUpdate = true;
					}
				}
				JSONArray noticeArray = json.getJSONObject("data").getJSONArray("ment_arr");
				for(int i = 0; i < noticeArray.length(); i++){
					JSONObject jsonObj = noticeArray.getJSONObject(i);
					String content = jsonObj.getString("content");
					content = content.replace("\\n", "\n");
					noticeList.add(new NoticeInfo(jsonObj.getString("title"), content));
				}
				
				if(!noticeList.isEmpty()){
					if(noticeList.get(0).getTitle().equals("null")){
						popupTitle.setVisibility(View.GONE);
					}
					else{
						popupTitle.setText(noticeList.get(0).getTitle());
					}
					popupText.setText(noticeList.get(0).getContent());
					noticeList.remove(0);
					Log.i("STEVEN", "just before pop");
					popupWindow.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);
					Log.i("STEVNE", "just before drop down");
					popupWindow.showAsDropDown(null);
				}
			} catch (Exception e) {

				Log.e("STEVEN", "app version check and notice something wrong");
			}
		}
	}
	
	//TODO
	private class DownloadNoticeImageTask extends AsyncTask<String, Void, Bitmap> {
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
	    	noticeListImg.add(result);
	    }
	}
	private class AccessCheck extends AsyncTask<String, Void, JSONObject> {
		@Override
		protected void onPreExecute(){
			super.onPreExecute();
			loadingDialog.show();
		}
		@Override
		protected JSONObject doInBackground(String... urls) {
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
		protected void onPostExecute(JSONObject json) {
			loadingDialog.dissmiss();
			try {
				if(json.getBoolean("status")){
					int result = json.getJSONObject("data").getInt("result");
					if(result == 0){	//closed
						popupText.setText(R.string.store_temporary_closed);
						popupWindow.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);
					}
					else{ //opened, may not set password(doesn't matter)
						Intent intent = new Intent(getApplicationContext(),HomeStore.class);
						startActivity(intent);
					}
				}
			} catch (Exception e) {

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
		new AccessCheck().execute("http://todpop.co.kr/api/qpcon_coupons/can_shopping.json?user_id=" + rgInfo.getString("mem_id", "NO"));
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
		if(closeFlag)
			cpxQuit();
		else{
			FlurryAgent.logEvent("Intall Later");
			SharedPreferences cpxInfo = getSharedPreferences("cpxInfo",0);
			SharedPreferences cpxSInstallInfo = getSharedPreferences("cpxInstallInfo",0);
			cpxInfo.edit().clear().apply();
			cpxSInstallInfo.edit().clear().apply();
			cpxView.setVisibility(View.GONE);
		}
	}
	public void cpxGoReward(View v)
	{	
		closeFlag = false;
		
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
				cpxInstallInfoEditor.apply();			
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
				cpxInstallInfoEditor.apply();			
				Toast toast = Toast.makeText(getApplicationContext(), R.string.cpa_install_notice, Toast.LENGTH_LONG);
				toast.show();
				if(cpxTargetUrl.contains("**ad_id**")){
					cpxTargetUrl = cpxTargetUrl.replace("**ad_id**", String.valueOf(cpxAdId));
				}
				if(cpxTargetUrl.contains("**user_id**")){
					cpxTargetUrl = cpxTargetUrl.replace("**user_id**", String.valueOf(userId));
				}
				Log.i("STEVEN", "cpx test url : "+cpxTargetUrl);
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(cpxTargetUrl)));
			}
		}else if (cpxAdType == 305) {
			SharedPreferences cpxInfo = getSharedPreferences("cpxInfo",0);
			SharedPreferences.Editor cpxInfoEditor = cpxInfo.edit();
			cpxInfoEditor.putString("reward", cpxReward);
			cpxInfoEditor.putString("point", cpxPoint);
			cpxInfoEditor.apply();
			Intent intent = new Intent(getApplicationContext(), SurveyView.class);
			startActivity(intent);
		}else if(cpxAdType == 306){
			Toast toast = Toast.makeText(getApplicationContext(), R.string.cpc_rewarded, Toast.LENGTH_LONG);
			toast.show();
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(cpxTargetUrl)));
			new SendCPXLog().execute("http://todpop.co.kr/api/advertises/set_cpx_log.json?ad_id=" + cpxAdId +
					"&ad_type=" + cpxAdType +"&user_id=" + userId + "&act=3");
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
			Toast.makeText(getApplicationContext(), getResources().getString(R.string.study_home_popup_nokakao), Toast.LENGTH_SHORT).show();
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

			String nickname = rgInfo.getString("nickname", null);
			String strMessage = kakaoMent + nickname +"]";
			String strURL = "http://market.android.com/details?id=com.todpop.saltyenglish";			// fix & hidden
			String strAppId = "com.todpop.saltyenglish";											// fix
			String strAppVer = "0.1.x";																// cannot get real AppVer automatically (no matter)
			String strAppName = getResources().getString(R.string.app_name);
						
			kakaoLink.openKakaoAppLink(StudyHome.this, strURL, strMessage, strAppId, strAppVer, strAppName, "UTF-8", metaInfoArray);
			
		}
	}

	private void cpxQuit(){
		SharedPreferences settings = getSharedPreferences("setting", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("check","YES");
		editor.apply();

		Intent intent = new Intent();
		intent.setClass(StudyHome.this, MainActivity.class);    
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
		startActivity(intent);
		finish();	
	}
	
	private void cpxSendLog(){
		SharedPreferences cpxInfo = getSharedPreferences("cpxInfo",0);
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
			cpxInfoEditor.apply();
			
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
			
			if(!cpxConfirmUrl.equals("")){
				new CheckCPA().execute(cpxConfirmUrl + "?mobile=" + mobile);
			}
		} else if (cpxAdType == 305) {
			FlurryAgent.logEvent("CPS");
			cpxView.setVisibility(View.VISIBLE);
			
			SharedPreferences.Editor cpxInfoEditor;
			cpxInfoEditor = cpxInfo.edit();

			cpxInfoEditor.putInt("adId", cpxAdId);
			cpxInfoEditor.apply();
			
			// Send CPX Log
			new SendCPXLog().execute("http://todpop.co.kr/api/advertises/set_cpx_log.json?ad_id="+cpxAdId+
					"&ad_type=" + cpxAdType +"&user_id=" + userId + "&act=1");
			
			new SendCPXLog().execute("http://todpop.co.kr/api/advertises/set_cpx_log.json?ad_id="+cpxAdId+
					"&ad_type=" + cpxAdType +"&user_id=" + userId + "&act=2");
		} else if(cpxAdType == 306){
			FlurryAgent.logEvent("CPC");
			cpxView.setVisibility(View.VISIBLE);
			
			SharedPreferences.Editor cpxInfoEditor;
			cpxInfoEditor = cpxInfo.edit();

			cpxInfoEditor.putInt("adId", cpxAdId);
			cpxInfoEditor.apply();
			
			new SendCPXLog().execute("http://todpop.co.kr/api/advertises/set_cpx_log.json?ad_id="+cpxAdId+
					"&ad_type=" + cpxAdType +"&user_id=" + userId + "&act=1");
			new SendCPXLog().execute("http://todpop.co.kr/api/advertises/set_cpx_log.json?ad_id="+cpxAdId+
					"&ad_type=" + cpxAdType +"&user_id=" + userId + "&act=2");
		}else if(cpxAdType == 300){
			//do nothing. type 300 is for when there is no cpx for user.
		}
		else{
			
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
	
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			
			if(cpxView.isShown()){
				closeFlag = false;
				FlurryAgent.logEvent("Intall Later");
				SharedPreferences cpxInfo = getSharedPreferences("cpxInfo",0);
				SharedPreferences cpxSInstallInfo = getSharedPreferences("cpxInstallInfo",0);
				cpxInfo.edit().clear().apply();
				cpxSInstallInfo.edit().clear().apply();
				cpxView.setVisibility(View.GONE);
			}
			else if(isOnSlide == true){
				FlurryAgent.logEvent("Back Btn slide off");
				slideOff();
			}
			else{
				new GetCPX().execute("http://todpop.co.kr/api/advertises/get_cpx_ad.json?user_id=" + userId + "&type=1");
				closeFlag = true;
				cpxAdImageView.setImageResource(R.drawable.test_27_image_end);
				cpxAdInfoTitle.setVisibility(View.INVISIBLE);
				cpxAdTextView.setVisibility(View.INVISIBLE);
				cpxAdPlus.setVisibility(View.INVISIBLE);
				cpxAdTextReward.setVisibility(View.INVISIBLE);
				cpxAdSaveNowBtn.setVisibility(View.INVISIBLE);
				cpxAdNoButton.setBackgroundResource(R.drawable.studytest_drawable_btn_end);
				cpxProgress.setVisibility(View.VISIBLE);
				cpxView.setVisibility(View.VISIBLE);
				/*
				final AlertDialog.Builder isExit = new AlertDialog.Builder(this);
				DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) 
					{
						switch (which) {
						case AlertDialog.BUTTON_POSITIVE:
							SharedPreferences settings = getSharedPreferences("setting", 0);
							SharedPreferences.Editor editor = settings.edit();
							editor.putString("check","YES");
							editor.apply();

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
				*/
			}

			return false;
		}
		return false;
	}
	//----button onClick----
	public void closeCpxPopup(View v){
		closeFlag = false;
		FlurryAgent.logEvent("Intall Later");
		SharedPreferences cpxInfo = getSharedPreferences("cpxInfo",0);
		SharedPreferences cpxSInstallInfo = getSharedPreferences("cpxInstallInfo",0);
		cpxInfo.edit().clear().apply();
		cpxSInstallInfo.edit().clear().apply();
		cpxView.setVisibility(View.GONE);
	}
	
	public void closePopup(View v)
	{
		if(majorVersionUpdate){
			SharedPreferences settings = getSharedPreferences("setting", 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.putString("check","YES");
			editor.apply();

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
				if(noticeList.get(0).getTitle().equals("null")){
					popupTitle.setVisibility(View.GONE);
				}
				else{
					popupTitle.setText(noticeList.get(0).getTitle());
				}
				popupText.setText(noticeList.get(0).getContent());
				noticeList.remove(0);
				popupWindow.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);
				popupWindow.showAsDropDown(null);
			}
			cpxPopupWindow.dismiss();
			cpxView.setVisibility(View.GONE);
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
