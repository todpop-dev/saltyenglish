package com.todpop.saltyenglish;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.flurry.android.FlurryAgent;
import com.todpop.api.LoadingDialog;
import com.todpop.api.TypefaceFragmentActivity;
import com.todpop.api.request.DownloadAndPlayPronounce;
import com.todpop.api.request.SimpleSend;
import com.todpop.saltyenglish.db.PronounceDBHelper;
import com.todpop.saltyenglish.db.WordDBHelper;

public class StudyBeginReBuild extends TypefaceFragmentActivity{
	private int tmpStageAccumulated;
	private int stage;
	private int level;
	private int category;
	private String userId;
	
	private ViewPager viewPager;
	
	// indicators
	private ImageView point1;
	private ImageView point2;
	private ImageView point3;
	private ImageView point4;
	private ImageView point5;
	private ImageView point6;
	private ImageView point7;
	private ImageView point8;
	private ImageView point9;
	private ImageView point10;
	
	private ArrayList<StudyBeginWord> words;
	
	private int adId;
	private int adType;
	private boolean hasHistory;
	private String couponId;
	private String reward;
	private String point;
	private String frontImg;
	private String backImg;
	private String fbName;
	private String fbCaption;
	private String fbDescription;
	private String fbLink;
	private String fbPicture;
	
	private PopupWindow popupWindow;
	private View poupView;
	private RelativeLayout relative;
	private TextView popupText;
	
	private PronounceDBHelper pHelper;
	private SQLiteDatabase pDB;
	
	private DownloadAndPlayPronounce player;
	
	private LoadingDialog loadingDialog;
	
	private int threadDoneCnt = 0;
	private boolean oneOfThreadIsDone = false;
	
	private boolean shareTried = false;
	private final List<String> PERMISSIONS = Arrays.asList("publish_actions");
	private boolean pendingPublishReauthorization = false;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_study_begin);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		
		Intent intent = getIntent();
		tmpStageAccumulated = intent.getIntExtra("tmpStageAccumulated", 1);
		
		stage = tmpStageAccumulated % 10;
		level = ((tmpStageAccumulated - 1) / 10) + 1;
		category = (level <= 15) ? 1 : (level <= 60) ? 2 : (level <= 120) ? 3 : 4; 
		
		viewPager = (ViewPager)findViewById(R.id.study_begin_id_pager);
		
		//indicators 
		point1 = (ImageView)findViewById(R.id.studybigin_id_point1);
		point2 = (ImageView)findViewById(R.id.studybigin_id_point2);
		point3 = (ImageView)findViewById(R.id.studybigin_id_point3);
		point4 = (ImageView)findViewById(R.id.studybigin_id_point4);
		point5 = (ImageView)findViewById(R.id.studybigin_id_point5);
		point6 = (ImageView)findViewById(R.id.studybigin_id_point6);
		point7 = (ImageView)findViewById(R.id.studybigin_id_point7);
		point8 = (ImageView)findViewById(R.id.studybigin_id_point8);
		point9 = (ImageView)findViewById(R.id.studybigin_id_point9);
		point10 = (ImageView)findViewById(R.id.studybigin_id_point10);
		
		words = new ArrayList<StudyBeginWord>();
		
		pHelper = new PronounceDBHelper(this);
		
		player = new DownloadAndPlayPronounce(this);
		
		loadingDialog = new LoadingDialog(this);
		loadingDialog.show();
		
		viewPager.setOnPageChangeListener(new PageListener());
		
		SharedPreferences rgInfo = getSharedPreferences("rgInfo",0);
		userId = rgInfo.getString("mem_id", "0");
		
		final String wordUrl = "http://todpop.co.kr/api/studies/get_level_words.json?stage=" + stage + "&level=" + level;
		final String wordUrl2 = "http://todpop.co.kr/api/studies/get_level_words.json?stage=" + (stage-1) + "&level=" + level;
		final String wordUrl3 = "http://todpop.co.kr/api/studies/get_level_words.json?stage=" + (stage-2) + "&level=" + level;
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		{
			new GetWord(true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, wordUrl);
			if(stage == 3 || stage == 6 || stage == 9)
			{
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {new GetWord(false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, wordUrl2);}
				}, 50);
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {new GetWord(false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, wordUrl3);}
				}, 50);
			}
		}
		else
		{
			new GetWord(true).execute(wordUrl);
			if(stage == 3 || stage == 6 || stage == 9)
			{
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {new GetWord(false).execute(wordUrl2);}
				}, 50);
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {new GetWord(false).execute(wordUrl3);}
				}, 50);
			}
		}
		
		new GetCPD().execute("http://todpop.co.kr/api/advertises/get_cpd_ad.json?user_id=" + userId);
		
		//popupview
		relative = (RelativeLayout)findViewById(R.id.studybegin_id_main_activity);;
		poupView = View.inflate(this, R.layout.popup_view, null);
		popupWindow = new PopupWindow(poupView,ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT,true);
		popupText = (TextView)poupView.findViewById(R.id.popup_id_text);

		setFont(popupText);
	}
	
	private class GetWord extends AsyncTask<String, Void, Boolean>{
		boolean isMain = false;
		
		DefaultHttpClient httpClient ;
		
		WordDBHelper wHelper;
		SQLiteDatabase db;
		public GetWord(boolean isMain){
			this.isMain = isMain;
		}
		@Override
		protected Boolean doInBackground(String... urls) {
			JSONObject result = null;
			try{
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
				}
				
				if(result != null){
					if(result.getBoolean("status") == true) {
						wHelper = new WordDBHelper(StudyBeginReBuild.this);
						db = wHelper.getWritableDatabase();
						if(isMain)
							db.execSQL("DELETE FROM dic WHERE stage=" + tmpStageAccumulated + ";");
						
						JSONArray jsonWords = result.getJSONArray("data");
						JSONObject jsonObj;
						
						for(int i = 0; i < jsonWords.length(); i++){
							jsonObj = jsonWords.getJSONObject(i);
							jsonObjToWordsAndDB(jsonObj);
						}
						if(jsonWords.length() < 10){
							/*
							 * Add review 3 words from this level's wrong word
							 */
							Cursor reviewCursor = db.rawQuery("SELECT DISTINCT name, mean, example_en, example_ko, phonetics, image_url, picture FROM dic WHERE" + 
												" xo = \'X\' AND stage > " + ((tmpStageAccumulated / 10) * 10) + " AND stage <= " + (tmpStageAccumulated - 1) + 
												" ORDER BY RANDOM() LIMIT 3", null);
							addToWordsAndDB(reviewCursor);
						}
						if(words.size() < 10){
							String overlap = "";
							for(int i = (10 - words.size()); i > 0; i--){
								overlap += "'" + words.get(i).getEngWord() + "'";
								
								if(i != 1)
									overlap += ",";
							}
							
							Cursor reviewCursor = db.rawQuery("SELECT DISTINCT name, mean, example_en, example_ko, phonetics, image_url, picture FROM dic WHERE " +
									"xo=\'O\' AND stage>=" + ((tmpStageAccumulated / 10) * 10) + " AND stage <=" + (tmpStageAccumulated - 1) + 
									" AND name NOT IN ("+overlap+") ORDER BY RANDOM() LIMIT " + (10 - words.size()) , null);
							addToWordsAndDB(reviewCursor);
						}
						if(words.size() < 10){
							JSONArray spareWords = result.getJSONArray("spare");
							for(int i = 0; (words.size() < 10) && (i < spareWords.length()); i++){
								jsonObj = spareWords.getJSONObject(i);
								jsonObjToWordsAndDB(jsonObj);
							}
						}
					}
				}
				wHelper.close();
				return true;
			}
			catch(Exception e){
				
			}
			if((stage % 3) != 0)
				wHelper.close();
			else{
				if(threadDoneCnt >= 2){
					wHelper.close();
				}
				else{
					threadDoneCnt++;
				}
			}
			return false;
		}
		@Override
		protected void onPostExecute(Boolean result){
			if(isMain && result){
				if(oneOfThreadIsDone){
					viewPager.setAdapter(new StudyBeginPagerAdapter(getSupportFragmentManager(), words, adType, hasHistory, reward, point, frontImg, backImg));
					loadingDialog.dissmiss();
				}
				else{
					oneOfThreadIsDone = true;
				}
			}
			else{
			}
		}
		private void jsonObjToWordsAndDB(JSONObject jsonObj){
			ContentValues row = new ContentValues();
			
			try {
				row.put("name", jsonObj.getString("name"));
				row.put("mean", jsonObj.getString("mean"));
				row.put("example_en", jsonObj.getString("example_en"));
				row.put("example_ko", jsonObj.getString("example_ko"));
				row.put("phonetics", jsonObj.getString("phonetics"));
				row.put("picture", jsonObj.getString("picture"));
				row.put("image_url", jsonObj.getString("image_url"));
				row.put("stage", tmpStageAccumulated);
				row.put("xo", "X");
					
				db.insert("dic", null, row);
				
				words.add(new StudyBeginWord(jsonObj.getString("name"), jsonObj.getString("mean"), jsonObj.getString("example_en"),
					jsonObj.getString("example_ko"), jsonObj.getString("phonetics"), jsonObj.getString("image_url"), jsonObj.getString("voice")));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		private void addToWordsAndDB(Cursor cursor){
			pDB = pHelper.getReadableDatabase();

			while(cursor.moveToNext()){
				/*
				 * save review words for study and test 
				 */

				String word = cursor.getString(0);
				if(word.contains("'"));
					word = word.replace("'", "''");
				
				Cursor soundCursor = pDB.rawQuery("SELECT version FROM pronounce WHERE word='" + word + "'", null);

				if(soundCursor.moveToFirst()){
					words.add(new StudyBeginWord(cursor.getString(0), cursor.getString(1), cursor.getString(2)
							, cursor.getString(3), cursor.getString(4), cursor.getString(5), soundCursor.getString(0)));
				}
				else{
					words.add(new StudyBeginWord(cursor.getString(0), cursor.getString(1), cursor.getString(2)
							, cursor.getString(3), cursor.getString(4), cursor.getString(5), "1"));
				}
				
				ContentValues row = new ContentValues();
				row.put("name", cursor.getString(0));
				row.put("mean", cursor.getString(1));
				row.put("example_en", cursor.getString(2));
				row.put("example_ko", cursor.getString(3));
				row.put("phonetics", cursor.getString(4));
				row.put("image_url", cursor.getString(5));
				row.put("picture", cursor.getString(6));
				row.put("stage", tmpStageAccumulated);
				row.put("xo", "X");
				
				db.insert("dic", null, row);
			}
			pDB.close();
		}
	}
	private class GetCPD extends AsyncTask<String, Void, JSONObject> 
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
				}
				return result;
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return result;
			}
		}

		@Override
		protected void onPostExecute(JSONObject json) {
			try {
				if(json.getBoolean("status") == true) {
					JSONObject cpdJsonObj = json.getJSONObject("data");
					adId = cpdJsonObj.getInt("ad_id");
					adType = cpdJsonObj.getInt("ad_type");
					hasHistory = cpdJsonObj.getString("history").equals("0") ? true : false;
					couponId = cpdJsonObj.getString("coupon");
					reward = cpdJsonObj.getString("reward");
					point = cpdJsonObj.getString("point");
					frontImg = "http://todpop.co.kr" + cpdJsonObj.getString("front_image");
					backImg = "http://todpop.co.kr" + cpdJsonObj.getString("back_image");
					fbName = cpdJsonObj.getString("name");
					fbCaption = cpdJsonObj.getString("caption");
					fbDescription = cpdJsonObj.getString("description");
					fbLink = cpdJsonObj.getString("link");
					fbPicture = cpdJsonObj.getString("picture");
					
					if(oneOfThreadIsDone){
						viewPager.setAdapter(new StudyBeginPagerAdapter(getSupportFragmentManager(), words, adType, hasHistory, reward, point, frontImg, backImg));
						loadingDialog.dissmiss();
					}
					else{
						oneOfThreadIsDone = true;
					}
				}
			}
			catch (Exception e){
				e.printStackTrace();
			}
		}
	}
	public void onClickBack(View view)
	{
		finish();
	}

	public void showCouponPopView(View v)
	{
		FlurryAgent.logEvent("Coupon Get");
		new SimpleSend().execute("http://todpop.co.kr/api/advertises/set_cpd_log.json?ad_id=" + adId + "&ad_type=" + adType + "&user_id=" + userId + "&act=2&coupon_id=" + couponId);
		popupText.setText(R.string.study_finish_popup_text);
		popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
		popupWindow.showAsDropDown(null);
	}

	public void closePopup(View v)
	{
		popupWindow.dismiss();
	}
	public void readItForMe(View v){
		StudyBeginWord temp = words.get(viewPager.getCurrentItem());
		player.readItForMe(temp.getEngWord(), String.valueOf(temp.getVoiceVer()), category);
	}
	public void showTestActivity(View v){
		if(stage == 10){
			Intent intent = new Intent(getApplicationContext(), StudyTestC.class);
			startActivity(intent);
			finish();
		}
		else{
			if((stage % 3) == 0){
				Intent intent = new Intent(getApplicationContext(), StudyTestCookie.class);
				startActivity(intent);
				finish();
			}
			else{
				Intent intent = new Intent(getApplicationContext(), StudyTestRenewal.class);
				startActivity(intent);
				finish();
			}
		}
	}
	/*
	 * for facebook share
	 */
	public void publishAdBtn() {
		shareTried = true;

		Session session = Session.getActiveSession();
		if (session == null || session.isClosed()) {
			Session.openActiveSession(this, true, callback);
		} else {
			publishAd();
		}
	}

	public void publishAd() {
		Session session = Session.getActiveSession();
		List<String> permissions = session.getPermissions();
		if (!isSubsetOf(PERMISSIONS, permissions)) {
			pendingPublishReauthorization = true;
			Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(
					this, PERMISSIONS);
			session.requestNewPublishPermissions(newPermissionsRequest);
			return;
		} else {
			Bundle postParams = new Bundle();
			postParams.putString("link", fbLink);
			if(!fbName.equals("null"))
				postParams.putString("name", fbName);
			if(!fbCaption.equals("null"))
				postParams.putString("caption", fbCaption);
			if(!fbDescription.equals("null"))
				postParams.putString("description", fbDescription);
			if(!fbPicture.equals("null"))
				postParams.putString("picture", fbPicture);

			Request.Callback callback = new Request.Callback() {
				public void onCompleted(Response response) {
					Log.i("STEVEN", "callback response : " + response);
					try{
						JSONObject graphResponse = response.getGraphObject()
								.getInnerJSONObject();
						String postId = null;
						try {
							postId = graphResponse.getString("id");
						} catch (JSONException e) {
							Log.i("Facebook StudyTestFinish",
									"JSON error " + e.getMessage());
						}
						FacebookRequestError error = response.getError();
						if (error != null) {
							popupText.setText(R.string.facebook_share_error);
							popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);					
							Toast.makeText(getApplicationContext(),
									error.getErrorMessage(), Toast.LENGTH_SHORT)
									.show();
						} else {
							popupText.setText(R.string.facebook_share_done);							
							popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
							new SimpleSend().execute("http://todpop.co.kr/api/advertises/set_cpd_log.json?ad_id=" + adId + "&ad_type=" + adType + "&user_id=" + userId + "&act=2&facebook_id=" + postId);
						}
					}catch(Exception e){
						popupText.setText(R.string.facebook_share_error);
						popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);						
					}
				}
			};

			Request request = new Request(Session.getActiveSession(),
					"me/feed", postParams, HttpMethod.POST, callback);

			RequestAsyncTask task = new RequestAsyncTask(request);
			task.execute();
		}

	}
	private boolean isSubsetOf(Collection<String> subset,
			Collection<String> superset) {
		for (String string : subset) {
			if (!superset.contains(string)) {
				return false;
			}
		}
		return true;
	}

	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

	private void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		if (state.isOpened()) {
			if(!pendingPublishReauthorization){
				publishAd();
			}
			else{
			}
		}
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode,
				resultCode, data);
	}
	private class PageListener implements OnPageChangeListener{

		@Override
		public void onPageScrollStateChanged(int arg0) {}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {}

		@Override
		public void onPageSelected(int position) {
			switch(position){
			case 0:
				point1.setImageResource(R.drawable.study_8_image_indicator_blue_on);
				point2.setImageResource(R.drawable.study_8_image_indicator_blue_off);
				break;
			case 1:
				point1.setImageResource(R.drawable.study_8_image_indicator_blue_off);
				point2.setImageResource(R.drawable.study_8_image_indicator_blue_on);
				point3.setImageResource(R.drawable.study_8_image_indicator_blue_off);
				break;
			case 2:
				point2.setImageResource(R.drawable.study_8_image_indicator_blue_off);
				point3.setImageResource(R.drawable.study_8_image_indicator_blue_on);
				point4.setImageResource(R.drawable.study_8_image_indicator_blue_off);
				break;
			case 3:
				point3.setImageResource(R.drawable.study_8_image_indicator_blue_off);
				point4.setImageResource(R.drawable.study_8_image_indicator_blue_on);
				point5.setImageResource(R.drawable.study_8_image_indicator_blue_off);
				break;
			case 4: 
				point4.setImageResource(R.drawable.study_8_image_indicator_blue_off);
				point5.setImageResource(R.drawable.study_8_image_indicator_blue_on);
				point6.setImageResource(R.drawable.study_8_image_indicator_blue_off);
				break;
			case 5: 
				point5.setImageResource(R.drawable.study_8_image_indicator_blue_off);
				point6.setImageResource(R.drawable.study_8_image_indicator_blue_on);
				point7.setImageResource(R.drawable.study_8_image_indicator_blue_off);
				break;
			case 6: 
				point6.setImageResource(R.drawable.study_8_image_indicator_blue_off);
				point7.setImageResource(R.drawable.study_8_image_indicator_blue_on);
				point8.setImageResource(R.drawable.study_8_image_indicator_blue_off);
				break;
			case 7: 
				point7.setImageResource(R.drawable.study_8_image_indicator_blue_off);
				point8.setImageResource(R.drawable.study_8_image_indicator_blue_on);
				point9.setImageResource(R.drawable.study_8_image_indicator_blue_off);
				break;
			case 8: 
				point8.setImageResource(R.drawable.study_8_image_indicator_blue_off);
				point9.setImageResource(R.drawable.study_8_image_indicator_blue_on);
				point10.setImageResource(R.drawable.study_8_image_indicator_blue_off);
				break;
			case 9: 
				point9.setImageResource(R.drawable.study_8_image_indicator_blue_off);
				point10.setImageResource(R.drawable.study_8_image_indicator_blue_on);
				break;
			}
		}
		
	}
}
