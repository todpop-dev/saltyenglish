package com.todpop.saltyenglish;


import java.io.InputStream;
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

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;
import com.todpop.api.NoticeInfo;
import com.todpop.api.TypefaceActivity;
import com.todpop.saltyenglish.db.WordDBHelper;

public class StudyHome extends TypefaceActivity {
	// Ranking Item and Adaptor

	ArrayList<NoticeInfo> noticeList;
	ArrayList<Bitmap> noticeListImg;

	TextView myRank;
	ImageView myImage;
	TextView myName;
	TextView myScore;

	ImageView categoryWhiteBox;

	//declare define popup view
	PopupWindow popupWindow;
	View popupview;
	LinearLayout mainLayout;
	ImageView popupImage;
	TextView popupTitle;
	TextView popupText;

	String kakaoMent = "";
	String kaokaoAndroidUrl = "";
	String iosUrl = "";
	String userId;

	boolean majorVersionUpdate = false;

	SharedPreferences rgInfo;
	SharedPreferences studyInfo;
	SharedPreferences.Editor studyInfoEdit;
	//SharedPreferences myRankInfo;

	int category, period;

	//CPX Popup
	PopupWindow cpxPopupWindow;
	View cpxPopupView;
	RelativeLayout cpxPopupRelative;
	TextView cpxPopupText;

	// Database
	WordDBHelper mHelper;

	private RelativeLayout quitView;
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
		quitView = (RelativeLayout)findViewById(R.id.studyhome_cpi_view);
		//categoryWhiteBox = (ImageView)findViewById(R.id.bgimg_whitebox);

		//rankingList = (ListView)findViewById(R.id.studyhome_id_listview);
		noticeList = new ArrayList<NoticeInfo>();
		noticeListImg = new ArrayList<Bitmap>();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String currentDateandTime = sdf.format(new Date());

		studyInfoEdit = studyInfo.edit();
		studyInfoEdit.putString("lastUse", currentDateandTime);
		studyInfoEdit.apply();

		//setting category ViewPager.
		Log.i("STVEN", "407");

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
		getInfo();

		// Get CPX Info onResume
	}

	public void getInfo()
	{
		rgInfo = getSharedPreferences("rgInfo",0);

		category = studyInfo.getInt("currentCategory", 1);
		period = studyInfo.getInt("currentPeriod", 1);
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

	// on click
	public void showStudyCategory(View view)
	{
		Intent intent = new Intent(getApplicationContext(), StudyCategory.class);
		intent.putExtra("from_home", true);
		startActivity(intent);
		//finish();
	}

	public void showHomeWordList(View v)
	{
		Intent intent = new Intent(getApplicationContext(), HomeWordListGroup.class);
		startActivity(intent);
	}
	
	//사이드 메뉴 작업을 위한 더미 버튼,
	public void forTest(View v){
		Intent intent = new Intent(getApplicationContext(), HomeMoreGoal.class);
		startActivity(intent);
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

	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			quitView.setVisibility(View.VISIBLE);
			return false;
		}
		return false;
	}
	//----button onClick----

	public void onClickQuitLater(View v){
		quitView.setVisibility(View.GONE);
	}

	public void onClickQuitRightnow(View v){
		finish();
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
		}
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		mHelper.close();
	}

	public void closeCpxPopup(View v){
		quitView.setVisibility(View.GONE);
	}

}
