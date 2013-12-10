package com.todpop.saltyenglish;


import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.facebook.Session;
import com.flurry.android.FlurryAgent;

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
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

public class StudyHome extends Activity {
	private String encoding = "UTF-8";
	boolean isOnSlide = false;

	// CPI View show in return from study test
	RelativeLayout cpiView;
	ImageView cpiAdImageView;
	TextView cpiAdTextView;

	// Home Ranking List
	ListView rankingList;
	
	// Ranking Item and Adaptor
	RankingListItem rankingItem;
	ArrayList<RankingListItem> rankingItemArrayWeek;
	ArrayList<RankingListItem> rankingItemArrayMonth;
	RankingListAdapter rankingListAdapterWeek;
	RankingListAdapter rankingListAdapterMonth;

	TextView myRank;
	ImageView myImage;
	TextView myName;
	TextView myScore;
	
	ImageView categoryWhiteBox;
	
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
	SharedPreferences myRankInfo;
	SharedPreferences StudyLevelInfo;				// test purpose
	
	ViewPager categoryPager;
	ImageAdapter adapter;
	Point size;
	
	int category, period;
	
	//CPX Info
	int cpxAdId;
	int cpxAdType;	
	String cpxAdImageUrl;
	String cpxAdText;
	String cpxTargetUrl;
	String cpxPackageName;
	String cpxConfirmUrl;
	int cpxReward;
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
	}
	 
	@Override
	protected void onStop()
	{
		super.onStop();		
		FlurryAgent.onEndSession(this);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_study_home);
		
		myRankInfo = getSharedPreferences("myRankInfo", 0);
		studyInfo = getSharedPreferences("studyInfo",0);
		StudyLevelInfo = getSharedPreferences("StudyLevelInfo",0);			// test purpose
		
		pref = getSharedPreferences("rgInfo",0);
		userId = pref.getString("mem_id", "0");
		mHelper = new WordDBHelper(this);

		myRank = (TextView)findViewById(R.id.studyhome_id_my_rank);
		myImage = (ImageView)findViewById(R.id.studyhome_id_my_rank_image);
		myName = (TextView)findViewById(R.id.studyhome_id_my_rank_name_text);
		myScore = (TextView)findViewById(R.id.studyhome_id_my_rank_fraction);
		//categoryWhiteBox = (ImageView)findViewById(R.id.bgimg_whitebox);

		rankingList = (ListView)findViewById(R.id.studyhome_id_listview);
		rankingItemArrayWeek = new ArrayList<RankingListItem>();
		rankingItemArrayMonth = new ArrayList<RankingListItem>();
		categoryPager = (ViewPager)findViewById(R.id.study_home_id_pager);
		
        adapter = new ImageAdapter(this);
		categoryPager.setAdapter(adapter);
		categoryPager.setCurrentItem(1073741823);
		Display display = getWindowManager().getDefaultDisplay();
		size = new Point();
		display.getSize(size);
		categoryPager.setPageMargin(-size.x/2);
		categoryPager.setOffscreenPageLimit(5);
		categoryPager.setOnTouchListener(new View.OnTouchListener() {

			private float mLastX;
			private float mFirstX;
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()){
					case MotionEvent.ACTION_DOWN:
						mFirstX = event.getX();
						mLastX = event.getX();
						Log.i("STEVEN ACTION_DOWN", "mFirstX :" + mFirstX +"mLastX : " +mLastX);
						//categoryWhiteBox.setVisibility(View.INVISIBLE);
					break;
					case MotionEvent.ACTION_MOVE:
							categoryPager.scrollBy((int)((mLastX-event.getX())/2), 0);
							categoryPager.invalidate();
							mLastX = event.getX();
							Log.i("STEVEN ACTION_MOVE", "mFirstX :" + mFirstX +"mLastX : " +mLastX);
						break;
					case MotionEvent.ACTION_UP:
						Log.i("STEVEN ACTION_UP", "mFirstX :" + mFirstX +"mLastX : " +mLastX);
						if(mFirstX != mLastX){
							if((mFirstX - mLastX) > size.x/6){
								categoryPager.setCurrentItem(categoryPager.getCurrentItem()+1, true);
							}else if((mLastX - mFirstX) > size.x/6){
								categoryPager.setCurrentItem(categoryPager.getCurrentItem()-1, true);
							}
							else{
								categoryPager.setCurrentItem(categoryPager.getCurrentItem()+1, true);
								categoryPager.setCurrentItem(categoryPager.getCurrentItem()-1, true);
							}
						}
						//categoryWhiteBox.setVisibility(View.VISIBLE);
						break;
					default:
						break;
				}
				return true;
			}
		});
		categoryPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				SharedPreferences stdInfo = getSharedPreferences("studyInfo",0);
				SharedPreferences.Editor stdInfoEdit = stdInfo.edit();
				// TODO Auto-generated method stub
				if(arg0%2 == 1){ //Weekly
    				period =1;
    				stdInfoEdit.putInt("currentPeriod", 1);
    				stdInfoEdit.commit();
					rankingList.setAdapter(rankingListAdapterWeek);

					myRank.setText(myRankInfo.getString("weekRank", "null"));
					myScore.setText(myRankInfo.getString("weekScore", "null"));
					
    				Log.i("TESTING", "id_week getInfo() called");
				}
				else{		//Monthly
    				period =2;
    				stdInfoEdit.putInt("currentPeriod", 2);
    				stdInfoEdit.commit();
					rankingListAdapterMonth = new RankingListAdapter(StudyHome.this,R.layout.home_rank_list_item_view, rankingItemArrayMonth);
					rankingList.setAdapter(rankingListAdapterMonth);

					myRank.setText(myRankInfo.getString("monthRank", "null"));
					myScore.setText(myRankInfo.getString("monthScore", "null"));
    				Log.i("TESTING", "id_moon getInfo() called");
				}
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		try {
			Field mScroller = ViewPager.class.getDeclaredField("mScroller");
			mScroller.setAccessible(true);
			FixedSpeedScroller scroller = new FixedSpeedScroller(categoryPager.getContext());
			mScroller.set(categoryPager, scroller);
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// CPX View
		cpiView = (RelativeLayout)findViewById(R.id.studyhome_cpi_view);
		cpiAdImageView = (ImageView)findViewById(R.id.study_home_id_cpi_ad_image);
		cpiAdTextView = (TextView)findViewById(R.id.study_home_id_cpi_ad_text);
		
		//popupview
		mainLayout = (LinearLayout)findViewById(R.id.frag_home_rela_id);
		popupview = View.inflate(this, R.layout.popup_view_notice, null);
		float density = getResources().getDisplayMetrics().density;
		popupWindow = new PopupWindow(popupview,(int)(300*density),(int)(300*density),true);
		popupText = (TextView)popupview.findViewById(R.id.popup_notice_id_text);
		
		
		// CPX Popup view
		//cpxPopupRelative = (RelativeLayout)findViewById(R.id.rgregisteremailinfo_id_main_activity);
		cpxPopupView = View.inflate(this, R.layout.popup_view, null);
		cpxPopupWindow = new PopupWindow(cpxPopupView,(int)(300*density),(int)(100*density),true);
		cpxPopupText = (TextView)cpxPopupView.findViewById(R.id.popup_id_text);
		
		//TODO 
		new GetNotice().execute("http://www.todpop.co.kr/api/etc/main_notice.json");
		new GetKakao().execute("http://todpop.co.kr/api/app_infos/get_cacao_msg.json");
		
		Log.d("S H ----","196");
		Log.d("stageInfo",studyInfo.getString("stageInfo", null));
		Log.d("S H ----","198");
		
		int totalStage = StudyLevelInfo.getInt("totalStage", -99);
		int currentStage = StudyLevelInfo.getInt("currentStage", -99);
		int Level1 = StudyLevelInfo.getInt("Level1", -99);
		int Level2 = StudyLevelInfo.getInt("Level2", -99);
		Log.d("total",String.valueOf(totalStage));
		Log.d("current",String.valueOf(currentStage));
		Log.d("Level1",String.valueOf(Level1));
		Log.d("Level2",String.valueOf(Level2));


		
		
	}
	
	
	@Override
	public void onResume()
	{
		super.onResume();


		// Facebook Logout
		Session session = Session.getActiveSession();
		if (session != null) {
			if (!session.isClosed()) {
				session.closeAndClearTokenInformation();
				//clear your preferences if saved
			}
		} else {
			session = new Session(getApplicationContext());
			Session.setActiveSession(session);

			session.closeAndClearTokenInformation();
			//clear your preferences if saved
		}

		getInfo();
		
		adapter.notifyDataSetChanged();
		// Get CPX Info onResume

		SharedPreferences cpxInfo = getSharedPreferences("cpxInfo",0);

		cpxAdId = cpxInfo.getInt("adId", 0);
		cpxAdType = cpxInfo.getInt("adType", 0);	
		cpxAdImageUrl = cpxInfo.getString("adImageUrl", "");
		cpxAdText = cpxInfo.getString("adText", "");
		cpxTargetUrl = cpxInfo.getString("targetUrl", "");
		cpxPackageName = cpxInfo.getString("packageName", "");
		cpxConfirmUrl = cpxInfo.getString("confirmUrl", "");
		cpxReward = cpxInfo.getInt("reward", 0);
		cpxQuestionCount = cpxInfo.getInt("questionCount", 0);
		
		// Download CPX Image and update UI
		new DownloadImageTask().execute(cpxAdImageUrl);
		//cpxAdType = 0;
		cpxInfo.edit().clear().commit();
		if (cpxAdType == 301) {

			FlurryAgent.logEvent("CPI");
			cpiView.setVisibility(View.VISIBLE);
			
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
			cpiView.setVisibility(View.VISIBLE);
			
			SharedPreferences.Editor cpxInfoEditor;
			cpxInfoEditor = cpxInfo.edit();

			cpxInfoEditor.putInt("adId", cpxAdId);
			cpxInfoEditor.commit();
			
			// Send CPX Log
			new SendCPXLog().execute("http://todpop.co.kr/api/advertises/set_cpx_log.json?ad_id="+cpxAdId+
					"&ad_type=" + cpxAdType +"&user_id=" + userId + "&act=1");

			TelephonyManager phoneMgr=(TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE); 
			String mobile =phoneMgr.getLine1Number().toString();
			
			new CheckCPA().execute(cpxConfirmUrl + "?mobile=" + mobile);
		} else if (cpxAdType == 305) {
			FlurryAgent.logEvent("CPS");
			cpiView.setVisibility(View.VISIBLE);
			
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
			
			cpiView.setVisibility(View.GONE);			
			
			if (goMyDownload == true) {
				// Save to DB and JUMP to HomwDownload activity
				Intent intent = new Intent(getApplicationContext(), HomeDownload.class);
				startActivity(intent);
			} 
		
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
	    	cpiAdImageView.setImageBitmap(result);
	    	cpiAdTextView.setText(cpxAdText);
	    }
	}
	// ******************** End of CPX UTILITY CLASS *************************


	
	public void getInfo()
	{
		pref = getSharedPreferences("rgInfo",0);


		FlurryAgent.setUserId(pref.getString("mem_id", "NO"));
		
		category = studyInfo.getInt("currentCategory", 1);
		period = studyInfo.getInt("currentPeriod", 1);
		
		rankingItemArrayWeek.clear();
		rankingItemArrayMonth.clear();
		//get weekly, monthly rank
		new GetRankWeek().execute("http://todpop.co.kr/api/users/get_users_score.json?category="+
					category+"&period="+1+"&nickname="+pref.getString("nickname", "NO"));
		Log.e("STEVEN", "609");
		new GetRankMonth().execute("http://todpop.co.kr/api/users/get_users_score.json?category="+
				category+"&period="+2+"&nickname="+pref.getString("nickname", "NO"));
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


	private class GetRankWeek extends AsyncTask<String, Void, JSONObject> {
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
					for(int i=0;i<6;i++) {
						rankingItem = new RankingListItem(jsonArray.getJSONObject(i).getString("rank"),
								jsonArray.getJSONObject(i).getString("image"),jsonArray.getJSONObject(i).getString("name"),jsonArray.getJSONObject(i).getString("score"));
						rankingItemArrayWeek.add(rankingItem);
					}	
					SharedPreferences.Editor myRankInfoEdit = myRankInfo.edit();
					myRankInfoEdit.putString("weekRank", result.getJSONObject("data").getJSONObject("mine").getString("rank"));
					myRankInfoEdit.putString("weekScore", result.getJSONObject("data").getJSONObject("mine").getString("score")+getResources().getString(R.string.home_list_score_string));
					myRankInfoEdit.commit();

					//move current ranking page to weekly ranking page
					categoryPager.setCurrentItem(1073741823);

					rankingListAdapterWeek = new RankingListAdapter(StudyHome.this,R.layout.home_rank_list_item_view, rankingItemArrayWeek);
					rankingList.setAdapter(rankingListAdapterWeek);
					
					setRankImage(result.getJSONObject("data").getJSONObject("mine").getString("image"), myImage);
					myRank.setText(result.getJSONObject("data").getJSONObject("mine").getString("rank"));
					myScore.setText(result.getJSONObject("data").getJSONObject("mine").getString("score")+getResources().getString(R.string.home_list_score_string));
					myName.setText(result.getJSONObject("data").getJSONObject("mine").getString("name"));
				}

			} catch (Exception e) {

			}
		}
	}	
	private class GetRankMonth extends AsyncTask<String, Void, JSONObject> {
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
					for(int i=0;i<6;i++) {
						rankingItem = new RankingListItem(jsonArray.getJSONObject(i).getString("rank"),
								jsonArray.getJSONObject(i).getString("image"),jsonArray.getJSONObject(i).getString("name"),jsonArray.getJSONObject(i).getString("score"));
						rankingItemArrayMonth.add(rankingItem);
					}	
					SharedPreferences.Editor myRankInfoEdit = myRankInfo.edit();
					myRankInfoEdit.putString("monthRank", result.getJSONObject("data").getJSONObject("mine").getString("rank"));
					myRankInfoEdit.putString("monthScore", result.getJSONObject("data").getJSONObject("mine").getString("score")+getResources().getString(R.string.home_list_score_string));
					myRankInfoEdit.commit();
					
					setRankImage(result.getJSONObject("data").getJSONObject("mine").getString("image"),myImage);
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
		return true;
	}

	public void showShop(View view)
	{
	}

	// Sidebar Menu Callback
	public void slideMenuBtnCB(View v) {
		if (isOnSlide == false) {

			FlurryAgent.logEvent("Slide Button Clicked (On)");
			isOnSlide = true;

			float density = getResources().getDisplayMetrics().density;
			ObjectAnimator slideAni;
			LinearLayout rl = (LinearLayout)findViewById(R.id.frag_home_rela_id);
			slideAni = ObjectAnimator.ofFloat(rl, "x",0*density, (173/2)*density);

			slideAni.addListener(mAnimationListener);

			slideAni.setDuration(300);
			slideAni.start();
		} else {

			FlurryAgent.logEvent("Slide Button Clicked (Off)");
			isOnSlide = false;

			float density = getResources().getDisplayMetrics().density;
			ObjectAnimator slideAni;
			LinearLayout rl = (LinearLayout)findViewById(R.id.frag_home_rela_id);
			slideAni = ObjectAnimator.ofFloat(rl, "x", (173/2)*density,0*density);

			slideAni.addListener(mAnimationListener);

			slideAni.setDuration(300);
			slideAni.start();
		}

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

				if(curA < newA || curB < newB || curC < newC){
					popupText.setText(R.string.study_home_popup_version_check);
					if(curA != newA || curB != newB){
						majorVersionUpdate = true;
					}
				}
				else if(json.getJSONObject("data").getString("ment")!=""){
					String notice = json.getJSONObject("data").getString("ment");
					notice = notice.replace("\\n", "\n");
					popupText.setText(notice);
				}
				Log.i("STEVEN", "just before pop");
				popupWindow.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);
				Log.i("STEVNE", "just before drop down");
				popupWindow.showAsDropDown(rankingList);
			} catch (Exception e) {

				Log.i("STEVEN", "app version check and notice something wrong");
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
		Intent intent = new Intent(getApplicationContext(),HomeStore.class);
		startActivity(intent);
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
		cpiView.setVisibility(View.GONE);
	}
	public void cpxGoReward(View v)
	{	
	
		
		if (cpxAdType == 301) {
			if (cpxHistoryFlag) {
				// App Installed 
				cpxPopupText.setText(R.string.cpi_popup_text);
				cpxPopupWindow.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);
				cpxPopupWindow.showAsDropDown(rankingList);
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
				cpxPopupWindow.showAsDropDown(rankingList);
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
		KakaoLink kakaoLink = KakaoLink.getLink(getApplicationContext());

		// check, intent is available.
		if (!kakaoLink.isAvailableIntent()) {
			//alert("Not installed KakaoTalk.");			
			return;
		}
		//TODO -- need to rearrange
		kakaoLink.openKakaoLink(this, 
				kaokaoAndroidUrl, 
				iosUrl, 
				getPackageName(), 
				getPackageManager().getPackageInfo(getPackageName(), 0).versionName, 
				kakaoMent, 
				encoding);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if(cpiView.isShown()){
				FlurryAgent.logEvent("Intall Later");
				SharedPreferences cpxInfo = getSharedPreferences("cpxInfo",0);
				SharedPreferences cpxSInstallInfo = getSharedPreferences("cpxInstallInfo",0);
				cpxInfo.edit().clear().commit();
				cpxSInstallInfo.edit().clear().commit();
				cpiView.setVisibility(View.GONE);
			}
			else{
				final AlertDialog.Builder isExit = new AlertDialog.Builder(this);
				DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) 
					{
						switch (which) {
						case AlertDialog.BUTTON_POSITIVE:
							Log.e("STEVEN", String.valueOf(cpiView.isShown()));
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
			Intent market = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.todpop.saltyenglish"));
		    startActivity(market);
			//moveTaskToBack(true);
			finish();
		}
		else {
			popupWindow.dismiss();
			Log.i("STEVEN", "popupWindow dismiss done");
			cpxPopupWindow.dismiss();
			Log.i("STEVEN", "cpxPopupWindow dismiss done");
			cpiView.setVisibility(View.GONE);
			Log.i("STEVEN", "setVisibility done");
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
	public class ImageAdapter extends PagerAdapter {
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
	}
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
}
