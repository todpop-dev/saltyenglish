package com.todpop.saltyenglish;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;
import com.todpop.api.FileManager;
import com.todpop.api.LoadingDialog;
import com.todpop.api.TypefaceFragmentActivity;
import com.todpop.saltyenglish.db.PronounceDBHelper;
import com.todpop.saltyenglish.db.WordDBHelper;

public class StudyBegin extends TypefaceFragmentActivity {
	// popup view
	PopupWindow popupWindow;
	View popupview;
	RelativeLayout relative;
	TextView popupText;

	static int tmpStageAccumulated = 1;
	static int tmpLevel = 1;
	static int tmpStage = 1;
	static int tmpCategory = 1;

	// Slide Level Page
	ViewPager studyStartPageView;
	// PageAdapter for pageView
	StudyStartPagerAdapter studyStartPagerAdapter;
	// Fragment attached to pageView
	StudyStartFragment studyStartFragment;
	// word json
	//	static JSONArray jsonWords;
	static JSONArray curJsonWords=null;
	// word json bitmap array
	static ArrayList<Bitmap> bitmapArr;

	// CPD Front & Back images
	static Bitmap cpdFrontImage;
	static Bitmap cpdBackImage;
	boolean isDicTableDeleted=false;

	// Infomation for send CPD cound
	int adId;
	static int adType;
	static String sharedHistory;
	String couponId;
	String userId;
	int adAct;
	int picNull;

	// page point
	ImageView point1;
	ImageView point2;
	ImageView point3;
	ImageView point4;
	ImageView point5;
	ImageView point6;
	ImageView point7;
	ImageView point8;
	ImageView point9;
	ImageView point10;

	// word change
	boolean touch = false;
	TextView word1;
	TextView word2;
	TextView word3;

	// Start Intro Button
	ImageButton introBtn;

	ImageView lowBanner;

	// CPD image view
	private static ImageView cpdView;
	private static Button cpdCoupon;
	private static Button cpdFbShare;

	private static RelativeLayout fbShareLayout;
	private static TextView fbShareReward;

	SharedPreferences studyInfo;

	static ArrayList<View> rootViewArr = new ArrayList<View>();

	boolean isCardBack = false;

	boolean cpdLogSent = false;

	// Database
	WordDBHelper mHelper;
	PronounceDBHelper pHelper;
	SQLiteDatabase mDB;
	SQLiteDatabase pDB;

	static String reward;
	static String point;
	String name;
	String caption;
	String description;
	String link;
	String picture;

	LoadingDialog loadingDialog;

	private static boolean shareTried = false;
	private final List<String> PERMISSIONS = Arrays.asList("publish_actions");
	private boolean pendingPublishReauthorization = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_study_begin);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		studyInfo = getSharedPreferences("studyInfo",0);
		tmpStageAccumulated = studyInfo.getInt("tmpStageAccumulated", 1);
		tmpCategory = studyInfo.getInt("tmpCategory", 1);
		Log.d("current stage ------------ ", Integer.toString(tmpStageAccumulated));

		tmpLevel = (tmpStageAccumulated-1)/10+1;
		tmpStage = tmpStageAccumulated%10;

		// Bitmap ArrayList
		bitmapArr = new ArrayList<Bitmap>();

		// Database initiation
		mHelper = new WordDBHelper(this);
		pHelper = new PronounceDBHelper(this);

		//popupview
		relative = (RelativeLayout)findViewById(R.id.studybegin_id_main_activity);;
		popupview = View.inflate(this, R.layout.popup_view, null);
		popupWindow = new PopupWindow(popupview,ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT,true);
		popupText = (TextView)popupview.findViewById(R.id.popup_id_text);

		setFont(popupText);

		// word change
		word1 = (TextView)findViewById(R.id.study_word_tv);
		word2 = (TextView)findViewById(R.id.study_word_pron_tv);
		word3 = (TextView)findViewById(R.id.study_word_ex_tv);

		//page point
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
		// End of Saving TMP data to Shared Preference

		lowBanner = (ImageView)findViewById(R.id.studybegin_id_banner);
		if(tmpCategory == 3){
			lowBanner.setImageResource(R.drawable.study_8_img_kingkong);
		}

		// Add Intro Button
		introBtn = (ImageButton)findViewById(R.id.studybegin_id_intro_button);
		SharedPreferences pref = getSharedPreferences("rgInfo",0);
		String introOk = pref.getString("introOk", "N");
		if (introOk.equals("N")) {
			introBtn.setVisibility(View.VISIBLE);
		} else {
			introBtn.setVisibility(View.GONE);
		}

		loadingDialog = new LoadingDialog(this);

		Log.d("=======================","=========================");
		Log.d("level",String.valueOf(tmpLevel));
		Log.d("stage",String.valueOf(tmpStage));
		Log.d("=======================","=========================");

		String getWordsUrl = "http://todpop.co.kr/api/studies/get_level_words.json?stage=" + tmpStage + "&level=" + tmpLevel;
		final String getWordsUrl2 = "http://todpop.co.kr/api/studies/get_level_words.json?stage=" + (tmpStage-1) + "&level=" + tmpLevel;
		final String getWordsUrl3 = "http://todpop.co.kr/api/studies/get_level_words.json?stage=" + (tmpStage-2) + "&level=" + tmpLevel;
		loadingDialog.show();

		GetWord getWordTask = new GetWord();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		{
			getWordTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, getWordsUrl);
			if(tmpStage == 3 || tmpStage == 6 || tmpStage == 9)
			{
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {new GetWord().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, getWordsUrl2);}
				}, 50);
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {new GetWord().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, getWordsUrl3);}
				}, 50);
				Log.e("JJun","Cookie Stage Selected");
			}
		}
		else
		{
			getWordTask.execute(getWordsUrl);
			if(tmpStage == 3 || tmpStage == 6 || tmpStage == 9)
			{
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {new GetWord().execute(getWordsUrl2);}
				}, 50);
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {new GetWord().execute(getWordsUrl3);}
				}, 50);
				Log.e("JJun","Cookie Stage Selected");
			}
		}

		picNull = 0;

	}

	@Override
	public void onPause() {
		super.onPause();

	}

	public void hideIntroView(View v) 
	{
		introBtn.setVisibility(View.GONE);
		SharedPreferences pref = getSharedPreferences("rgInfo",0);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString("introOk", "Y");
		editor.apply();
	}

	public void setupPagerView()
	{
		studyStartPageView = (ViewPager)findViewById(R.id.study_begin_id_pager);
		studyStartPagerAdapter = new StudyStartPagerAdapter(getSupportFragmentManager());		
		studyStartPageView.setAdapter(studyStartPagerAdapter);

		loadingDialog.dissmiss();

		studyStartPageView.setOnPageChangeListener(new OnPageChangeListener() {    
			@Override public void onPageSelected(int position) {
				Log.d("----------------", Integer.toString(position));

				point1.setImageResource(R.drawable.study_8_image_indicator_blue_off);
				point2.setImageResource(R.drawable.study_8_image_indicator_blue_off);
				point3.setImageResource(R.drawable.study_8_image_indicator_blue_off);
				point4.setImageResource(R.drawable.study_8_image_indicator_blue_off);
				point5.setImageResource(R.drawable.study_8_image_indicator_blue_off);
				point6.setImageResource(R.drawable.study_8_image_indicator_blue_off);
				point7.setImageResource(R.drawable.study_8_image_indicator_blue_off);
				point8.setImageResource(R.drawable.study_8_image_indicator_blue_off);
				point9.setImageResource(R.drawable.study_8_image_indicator_blue_off);
				point10.setImageResource(R.drawable.study_8_image_indicator_blue_off);

				switch(position)
				{

				case 0:
					point1.setImageResource(R.drawable.study_8_image_indicator_blue_on);	
					break;
				case 1:
					point2.setImageResource(R.drawable.study_8_image_indicator_blue_on);
					break;
				case 2:
					point3.setImageResource(R.drawable.study_8_image_indicator_blue_on);
					break;
				case 3:
					point4.setImageResource(R.drawable.study_8_image_indicator_blue_on);
					break;
				case 4:
					point5.setImageResource(R.drawable.study_8_image_indicator_blue_on);
					break;
				case 5:
					point6.setImageResource(R.drawable.study_8_image_indicator_blue_on);
					break;
				case 6:
					point7.setImageResource(R.drawable.study_8_image_indicator_blue_on);
					break;
				case 7:
					point8.setImageResource(R.drawable.study_8_image_indicator_blue_on);
					break;
				case 8:
					point9.setImageResource(R.drawable.study_8_image_indicator_blue_on);
					break;
				case 9:
					point10.setImageResource(R.drawable.study_8_image_indicator_blue_on);
					break;
				case 10:
					if(cpdLogSent == false){
						//						Log.d("------- send info -----", "info");
						//						new SendLog().execute("http://todpop.co.kr/api/advertises/set_cpd_log.json?ad_id=" + adId + "&ad_type=" + adType + "&user_id=" + userId + "&act=1");
						cpdLogSent = true;
					}
					break;
				default:
					break;
				}
			}
			@Override public void onPageScrolled(int position, float positionOffest, int positionOffsetPixels) {}
			@Override public void onPageScrollStateChanged(int state) {}
		});
	}

	public class StudyStartPagerAdapter extends FragmentStatePagerAdapter{

		public StudyStartPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int i) {
			Fragment fragment = new StudyStartFragment();
			Bundle studyBeginArgs = new Bundle();

			studyBeginArgs.putInt("studyStartPage",i);
			fragment.setArguments(studyBeginArgs);
			return fragment;
		}

		@Override
		public int getCount() {
			return 11;
		}


	}

	public static class StudyStartFragment extends Fragment {

		View rootView;
		Animation animation;

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			LinearLayout wholeCard = null;


			Bundle studyBeginArgs = getArguments();
			if(studyBeginArgs.getInt("studyStartPage")<10)
			{
				rootView = inflater.inflate(R.layout.fragment_study_begin, container, false);
				ImageView wordOn = (ImageView)rootView.findViewById(R.id.fragment_study_begin_id_word_on);
				wholeCard = (LinearLayout)rootView.findViewById(R.id.fragment_study_begin_whole_card);
				wholeCard.setOnClickListener(new BtnFlipListener());

				int pageNum = studyBeginArgs.getInt("studyStartPage");
				switch(pageNum)

				{
				case 0:
					wordOn.setBackgroundResource(R.drawable.study_8_img_number_1);
					break;
				case 1:
					wordOn.setBackgroundResource(R.drawable.study_8_img_number_2);
					break;
				case 2:
					wordOn.setBackgroundResource(R.drawable.study_8_img_number_3);
					break;
				case 3:
					wordOn.setBackgroundResource(R.drawable.study_8_img_number_4);
					break;
				case 4:
					wordOn.setBackgroundResource(R.drawable.study_8_img_number_5);
					break;
				case 5:
					wordOn.setBackgroundResource(R.drawable.study_8_img_number_6);
					break;
				case 6:
					wordOn.setBackgroundResource(R.drawable.study_8_img_number_7);
					break;
				case 7:
					wordOn.setBackgroundResource(R.drawable.study_8_img_number_8);
					break;
				case 8:
					wordOn.setBackgroundResource(R.drawable.study_8_img_number_9);
					break;
				case 9:
					wordOn.setBackgroundResource(R.drawable.study_8_img_number_10);
					break;
				}

				TextView word =  (TextView)rootView.findViewById(R.id.study_word_tv);
				TextView pron = (TextView)rootView.findViewById(R.id.study_word_pron_tv);
				TextView example = (TextView)rootView.findViewById(R.id.study_word_ex_tv);
				ImageView wordImage = (ImageView)rootView.findViewById(R.id.fragment_study_begin_id_word_img);

				setFont(word);
				setFont(pron);
				setFont(example);

				// Setup word textview
				try {
					word.setText(curJsonWords.getJSONObject(pageNum).get("name").toString());
					pron.setText("["+curJsonWords.getJSONObject(pageNum).get("phonetics").toString()+"]");		// [+phonetics+]
					example.setText(curJsonWords.getJSONObject(pageNum).get("example_en").toString());
					wordImage.setImageBitmap(bitmapArr.get(pageNum));
					wordImage.setScaleType(ImageView.ScaleType.FIT_CENTER);        // center and stretch
				} catch (Exception e) {

				}


			} else {
				rootView = inflater.inflate(R.layout.fragment_study_begin_finish, container, false);
				cpdView = (ImageView)rootView.findViewById(R.id.studyfinish_id_pop);
				cpdCoupon = (Button)rootView.findViewById(R.id.studyfinish_id_coupon);
				cpdFbShare = (Button)rootView.findViewById(R.id.studyfinish_id_facebook_share);
				fbShareLayout = (RelativeLayout)rootView.findViewById(R.id.studyfinish_fb_share_layout);
				fbShareReward = (TextView)rootView.findViewById(R.id.studyfinish_fb_share_reward);

				setFont(fbShareReward);

				cpdView.setOnClickListener(new CPDFlipListener());
				cpdView.setImageBitmap(cpdFrontImage);
				if(adType == 102){
					FlurryAgent.logEvent("CPD (Coupon)");
					cpdCoupon.setVisibility(View.VISIBLE);
				}
				else if(adType == 103){
					FlurryAgent.logEvent("CPD (Facebook)");
					cpdFbShare.setVisibility(View.VISIBLE);
					fbShareLayout.setVisibility(View.VISIBLE);
					if(sharedHistory.equals("0") || shareTried){
						cpdFbShare.setEnabled(false);
						fbShareReward.setText(R.string.facebook_share_history);
					}
					else{
						if(reward.equals("0") || reward.equals("null")){
							fbShareReward.setText(point + " point");
						}
						else{
							fbShareReward.setText(reward + getResources().getString(R.string.testname8));
						}
					}
				}
				else{
					FlurryAgent.logEvent("CPD (none)");
				}

				if (cpdFrontImage == null || cpdBackImage == null) {
					cpdView.setVisibility(View.INVISIBLE);
				} else {
					cpdView.setVisibility(View.VISIBLE);
				}
			}

			// Add Image, Text to ListArray
			rootViewArr.add(rootView);
			rootView.setTag(studyBeginArgs.getInt("studyStartPage"));
			return rootView;

		}

		class BtnFlipListener implements OnClickListener 
		{ 
			private boolean isCardBack = false;
			public void onClick(View v)
			{

				animation = AnimationUtils.loadAnimation( getActivity(), R.drawable.studytestc_drawable_flip_card_back_scale); 
				animation.setAnimationListener(new Animation.AnimationListener() 
				{ 
					@Override 
					public void onAnimationStart(Animation animation) { 
					} 
					@Override 
					public void onAnimationRepeat(Animation animation) { 
					} 
					@Override 
					public void onAnimationEnd(Animation animation) { 	
						if (isCardBack == false) {
							fillKoWordView(rootView, (Integer)rootView.getTag());
							isCardBack = true;
						} else {
							fillEnWordView(rootView, (Integer)rootView.getTag());
							isCardBack = false;
						}

						animation = AnimationUtils.loadAnimation( getActivity(), R.drawable.studytestc_drawable_flip_card_front_scale); 
						rootView.startAnimation(animation);
					}
				});
				rootView.startAnimation(animation);
			} 

			public void fillKoWordView(View v, Integer i)
			{
				try {
					if (curJsonWords.length() > i.intValue()) {
						TextView word =  (TextView)v.findViewById(R.id.study_word_tv);
						TextView pron = (TextView)v.findViewById(R.id.study_word_pron_tv);
						TextView example = (TextView)v.findViewById(R.id.study_word_ex_tv);

						// Setup word text view
						word.setText(curJsonWords.getJSONObject(i).get("mean").toString());
						pron.setText("");
						example.setText(curJsonWords.getJSONObject(i).get("example_ko").toString());
					}

				} catch (Exception e) {

				}
			}
			public void fillEnWordView(View v, Integer i)
			{
				try {
					if (curJsonWords.length() > i.intValue()) {
						TextView word =  (TextView)v.findViewById(R.id.study_word_tv);
						TextView pron = (TextView)v.findViewById(R.id.study_word_pron_tv);
						TextView example = (TextView)v.findViewById(R.id.study_word_ex_tv);

						// Setup word textview
						word.setText(curJsonWords.getJSONObject(i).get("name").toString());
						pron.setText("["+curJsonWords.getJSONObject(i).get("phonetics").toString()+"]");		// [+phonetics+]
						example.setText(curJsonWords.getJSONObject(i).get("example_en").toString());
					}

				} catch (Exception e) {

				}
			}
		} 

		class CPDFlipListener implements OnClickListener 
		{ 
			private boolean isCardBack = false;
			public void onClick(View v)
			{

				FlurryAgent.logEvent("CPD Image Flipped");

				animation = AnimationUtils.loadAnimation( getActivity(), R.drawable.studytestc_drawable_flip_card_back_scale); 
				animation.setAnimationListener(new Animation.AnimationListener() 
				{ 
					@Override 
					public void onAnimationStart(Animation animation) { 
					} 
					@Override 
					public void onAnimationRepeat(Animation animation) { 
					} 
					@Override 
					public void onAnimationEnd(Animation animation) { 	
						if (isCardBack == false) {
							cpdView.setImageBitmap(cpdBackImage);
							isCardBack = true;
						} else {
							cpdView.setImageBitmap(cpdFrontImage);
							isCardBack = false;
						}

						animation = AnimationUtils.loadAnimation( getActivity(), R.drawable.studytestc_drawable_flip_card_front_scale); 
						cpdView.startAnimation(animation);
					}
				});
				cpdView.startAnimation(animation);
			} 
		} 
	}


	private class GetWord extends AsyncTask<String, Void, JSONObject> 
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

				if (resEntity != null)
				{    
					result = new JSONObject(EntityUtils.toString(resEntity)); 
					//Log.d("RESPONSE ---- ", result.toString());				        	
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
				if(json.getBoolean("status")==true) {
					Log.d("Get Word JSON RESPONSE ---- ", json.toString());				        	

					mDB = mHelper.getWritableDatabase();
					try {
						if(!isDicTableDeleted)
						{
							mDB.execSQL("DELETE FROM dic WHERE stage=" + tmpStageAccumulated + ";");
							isDicTableDeleted = true;
						}
					} catch (Exception e) {

					}
					//

					JSONArray jsonWords = json.getJSONArray("data");
					curJsonWords = (curJsonWords == null ? jsonWords : curJsonWords);
					for(int i=0;i<jsonWords.length();i++) {	
						// Save info to Database
						ContentValues row = new ContentValues();
						row.put("name", jsonWords.getJSONObject(i).get("name").toString());
						row.put("mean", jsonWords.getJSONObject(i).get("mean").toString());
						row.put("example_en", jsonWords.getJSONObject(i).get("example_en").toString());
						row.put("example_ko", jsonWords.getJSONObject(i).get("example_ko").toString());
						row.put("phonetics", jsonWords.getJSONObject(i).get("phonetics").toString());
						row.put("picture", (Integer)(jsonWords.getJSONObject(i).get("picture")));
						row.put("image_url", jsonWords.getJSONObject(i).get("image_url").toString());
						row.put("stage", tmpStageAccumulated);
						row.put("xo", "X");
						Log.e("test insert row",row.toString());
						mDB.insert("dic", null, row);

					}

					if (jsonWords.length() < 10) {

						// Add additional 3 words
						Cursor otherCursor = mDB.rawQuery("SELECT distinct name, mean, example_en, example_ko, phonetics, picture, image_url FROM dic WHERE " +
								"xo=\'X\' AND stage>=" + tmpStageAccumulated/10*10 + " AND stage <=" + (tmpStageAccumulated-1) + 
								" ORDER BY RANDOM() LIMIT 3" , null);

						if (otherCursor.getCount() > 0) {
							while(otherCursor.moveToNext()) {
								Log.i("STEVEN", "708");
								JSONObject jsonObj= new JSONObject();
								jsonObj.put("id", 0);
								jsonObj.put("name", otherCursor.getString(0));
								jsonObj.put("mean", otherCursor.getString(1));
								jsonObj.put("example_en", otherCursor.getString(2));
								jsonObj.put("example_ko", otherCursor.getString(3));
								jsonObj.put("phonetics", otherCursor.getString(4));
								jsonObj.put("picture", otherCursor.getInt(5));
								jsonObj.put("image_url", otherCursor.getString(6));

								pDB = pHelper.getReadableDatabase();
								Cursor soundCursor = pDB.rawQuery("SELECT version FROM pronounce WHERE word='" + otherCursor.getString(0) + "'", null);

								if(soundCursor.getCount() > 0){
									soundCursor.moveToFirst();
									jsonObj.put("voice", soundCursor.getString(0));
								}
								else{
									jsonObj.put("voice", 1);
								}
								pDB.close();

								jsonWords.put(jsonObj);

								ContentValues row = new ContentValues();
								row.put("name", otherCursor.getString(0));
								row.put("mean", otherCursor.getString(1));
								row.put("example_en", otherCursor.getString(2));
								row.put("example_ko", otherCursor.getString(3));
								row.put("phonetics", otherCursor.getString(4));
								row.put("picture", otherCursor.getString(5));
								row.put("image_url", otherCursor.getString(6));
								row.put("stage", tmpStageAccumulated);
								row.put("xo", "X");

								mDB.insert("dic", null, row);
							}
						}

						if (jsonWords.length() < 10) {
							String overlap = "";
							for(int i = jsonWords.length()-1; i >=7 ; i-- ) {
								overlap += "'"+jsonWords.getJSONObject(i).getString("name")+"'";
								if(i != 7) {
									overlap += ",";
								}
							}

							Cursor otherCursor2 = mDB.rawQuery("SELECT distinct name, mean, example_en, example_ko, phonetics, picture, image_url FROM dic WHERE " +
									"xo=\'O\' AND stage>=" + tmpStageAccumulated/10*10 + " AND stage <=" + (tmpStageAccumulated-1) + 
									" AND name NOT IN ("+overlap+") ORDER BY RANDOM() LIMIT " + (10-jsonWords.length()) , null);
							if (otherCursor2.getCount() > 0) {
								while(otherCursor2.moveToNext()) {
									JSONObject jsonObj= new JSONObject();
									jsonObj.put("id", 0);
									jsonObj.put("name", otherCursor2.getString(0));
									jsonObj.put("mean", otherCursor2.getString(1));
									jsonObj.put("example_en", otherCursor2.getString(2));
									jsonObj.put("example_ko", otherCursor2.getString(3));
									jsonObj.put("phonetics", otherCursor2.getString(4));
									jsonObj.put("picture", otherCursor2.getInt(5));
									jsonObj.put("image_url", otherCursor2.getString(6));

									Log.i("STEVEN", "772");
									Log.i("STEVEN", "SELECT version FROM pronounce WHERE word='" + otherCursor2.getString(0) + "'");

									pDB = pHelper.getReadableDatabase();
									Cursor soundCursor = pDB.rawQuery("SELECT version FROM pronounce WHERE word='" + otherCursor2.getString(0) + "'", null);

									if(soundCursor.getCount() > 0){
										soundCursor.moveToFirst();
										jsonObj.put("voice", soundCursor.getString(0));
									}
									else{
										jsonObj.put("voice", 1);
									}
									pDB.close();

									jsonWords.put(jsonObj);

									ContentValues row = new ContentValues();
									row.put("name", otherCursor2.getString(0));
									row.put("mean", otherCursor2.getString(1));
									row.put("example_en", otherCursor2.getString(2));
									row.put("example_ko", otherCursor2.getString(3));
									row.put("phonetics", otherCursor2.getString(4));
									row.put("picture", otherCursor2.getString(5));
									row.put("image_url", otherCursor2.getString(6));
									row.put("stage", tmpStageAccumulated);
									row.put("xo", "X");

									mDB.insert("dic", null, row);
								}
							}

						}
						if(jsonWords.length() < 10){

							JSONArray spareWords = json.getJSONArray("spare");
							for(int i = 0; jsonWords.length() < 10; i++) {

								if(i > 2)
									i = 0;
								Log.i("STEVEN", "count testing");
								jsonWords.put(spareWords.getJSONObject(i));

								ContentValues row = new ContentValues();
								row.put("name", spareWords.getJSONObject(i).get("name").toString());
								row.put("mean", spareWords.getJSONObject(i).get("mean").toString());
								row.put("example_en", spareWords.getJSONObject(i).get("example_en").toString());
								row.put("example_ko", spareWords.getJSONObject(i).get("example_ko").toString());
								row.put("phonetics", spareWords.getJSONObject(i).get("phonetics").toString());
								row.put("picture", (Integer)(spareWords.getJSONObject(i).get("picture")));
								row.put("image_url", spareWords.getJSONObject(i).get("image_url").toString());
								row.put("stage", tmpStageAccumulated);
								row.put("xo", "X");

								mDB.insert("dic", null, row);
							}
						}

						Log.d("-----------***********-----------", jsonWords.toString());
						Log.d("jsonArray length: ", Integer.toString(jsonWords.length()));
					}

					for(int i=0;i<jsonWords.length();i++)
					{												
						// Setup Image
						Integer picInt = (Integer)jsonWords.getJSONObject(i).get("picture");
						if (picInt == 1) {
							try {
								// show The Image
								//JSONObject img = jsonWords.getJSONObject(i).getJSONObject("image_url");
								String imgUrl = "http://todpop.co.kr" + jsonWords.getJSONObject(i).getString("image_url");
								URL url = new URL(imgUrl);
								Log.d("url ------ ", url.toString());
								new DownloadImageTask()
								.execute(url.toString());
							} catch (Exception e) {

							}
						} else {
							new DownloadImageTask()
							.execute("");						}
					}
				} else {		        
				}

			} catch (Exception e) {
				Log.d("Exception: ", e.toString());
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
				//bmImage.setImageBitmap(result);
				bitmapArr.add(result);
				Log.d("------------- big count ------", Integer.toString(bitmapArr.size()));
				if (tmpStage == 1) {
					if (bitmapArr.size() == 10) {
						setupPagerView();
						// Get CPD after Get Words
						//						SharedPreferences rgInfo = getSharedPreferences("rgInfo",0);
						//						userId = rgInfo.getString("mem_id", "1");
						//						new GetCPD().execute("http://todpop.co.kr/api/advertises/get_cpd_ad.json?user_id=" + userId);
					}
				} else if (tmpStage <=9) {
					if (bitmapArr.size() == 7) {
						setupPagerView();
						// Get CPD after Get Words
						//						SharedPreferences rgInfo = getSharedPreferences("rgInfo",0);
						//						userId = rgInfo.getString("mem_id", "1");
						//						new GetCPD().execute("http://todpop.co.kr/api/advertises/get_cpd_ad.json?user_id=" + userId);
					}
				}


			}
		}		
	}

	//	private class GetCPD extends AsyncTask<String, Void, JSONObject> 
	//	{
	//		DefaultHttpClient httpClient ;
	//		@Override
	//		protected JSONObject doInBackground(String... urls) 
	//		{
	//			JSONObject result = null;
	//			try
	//			{
	//				String getURL = urls[0];
	//				HttpGet httpGet = new HttpGet(getURL); 
	//				HttpParams httpParameters = new BasicHttpParams(); 
	//				int timeoutConnection = 5000; 
	//				HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection); 
	//				int timeoutSocket = 5000; 
	//				HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket); 
	//
	//				httpClient = new DefaultHttpClient(httpParameters); 
	//				HttpResponse response = httpClient.execute(httpGet); 
	//				HttpEntity resEntity = response.getEntity();
	//
	//				if (resEntity != null)
	//				{    
	//					result = new JSONObject(EntityUtils.toString(resEntity)); 
	//					//Log.d("RESPONSE ---- ", result.toString());				        	
	//				}
	//				return result;
	//			}
	//			catch (Exception e)
	//			{
	//				e.printStackTrace();
	//			}
	//			return result;
	//		}
	//
	//		@Override
	//		protected void onPostExecute(JSONObject json) {
	//			try {
	//				Log.d("Get CPD JSON RESPONSE ---- ", json.toString());				        	
	//
	//				if(json.getBoolean("status")==true) {
	//					JSONObject cpdJsonObj = json.getJSONObject("data");
	//
	//					adId = cpdJsonObj.getInt("ad_id");
	//					adType = cpdJsonObj.getInt("ad_type");
	//
	//					sharedHistory = cpdJsonObj.getString("history");
	//
	//					couponId = cpdJsonObj.getString("coupon");
	//
	//					reward = cpdJsonObj.getString("reward");
	//					point = cpdJsonObj.getString("point");
	//					name = cpdJsonObj.getString("name");
	//					caption = cpdJsonObj.getString("caption");
	//					description = cpdJsonObj.getString("description");
	//					link = cpdJsonObj.getString("link");
	//					picture = cpdJsonObj.getString("picture");
	//
	//					try {
	//						String imgUrl = "http://todpop.co.kr" + cpdJsonObj.getString("front_image");
	//						URL url = new URL(imgUrl);
	//						new DownloadImageTask("FRONT").execute(url.toString());
	//
	//						String imgUrl2 = "http://todpop.co.kr" + cpdJsonObj.getString("back_image");
	//						URL url2 = new URL(imgUrl2);
	//						new DownloadImageTask("BACK").execute(url2.toString());
	//					} catch (Exception e) {
	//						e.printStackTrace();
	//					}
	//				}else{		    
	//					Log.d("--------- CPD -----------", "JSON return null");
	//				}
	//
	//			} catch (Exception e) {
	//				Log.d("Exception: ", e.toString());
	//			}
	//		}
	//
	//		private class DownloadImageTask  extends AsyncTask<String, Void, Bitmap> {
	//
	//			String imgTag = null;
	//			public DownloadImageTask (String imgTag) 
	//			{
	//				this.imgTag = imgTag;
	//			}
	//
	//			protected Bitmap doInBackground(String... urls) 
	//			{
	//				String urldisplay = urls[0];
	//				Bitmap mIcon11 = null;
	//				try {
	//					InputStream in = new java.net.URL(urldisplay).openStream();
	//					mIcon11 = BitmapFactory.decodeStream(in);
	//				} catch (Exception e) {
	//					Log.e("Error", e.getMessage());
	//					e.printStackTrace();
	//				}
	//				return mIcon11;
	//			}
	//
	//			protected void onPostExecute(Bitmap result) 
	//			{
	//				if (imgTag.equals("FRONT")) {
	//					Log.e("STEVEN CPD IMAGE FRONT", "get done");
	//					cpdFrontImage = result;
	//				} else if (imgTag.equals("BACK")) {
	//					Log.e("STEVEN CPD IMAGE BACK", "get done");
	//					cpdBackImage = result;
	//				}
	//			}
	//		}		
	//	}

	private class SendLog extends AsyncTask<String, Void, JSONObject> 
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
			try {
				if(json.getBoolean("status")==true) {
					Log.d("Send CPD Info or Coupon Save OK!", "OK");
				}else{		  
					Log.d("Send CPD Info or Coupon Save Failed!", "NO");
				}

			} catch (Exception e) {
				Log.d("Exception: ", e.toString());
			}
		}		
	}

	public void readItForMe(View v){
		Log.i("STEVEN", "readItForMe");
		String word = null;
		String version = null;
		try {
			word = curJsonWords.getJSONObject(studyStartPageView.getCurrentItem()).getString("name");
			version = curJsonWords.getJSONObject(studyStartPageView.getCurrentItem()).getString("voice");
			Log.i("STEVEN", "version is : " + version);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		pDB = pHelper.getWritableDatabase();
		Cursor find = pDB.rawQuery("SELECT word, version FROM pronounce WHERE word=\'" + word + "\'", null);
		//TODO Testing

		Log.i("STEVEN", "find.getCount() = " + find.getCount());
		if(find.getCount() > 0){
			find.moveToFirst();
			if(new FileManager().pronounceFileCheck(word)){
				Log.i("STEVEN", "find.getString(1) is : " + find.getString(1) + "  version is : " + version);
				if(!find.getString(1).equals(version)){
					String name = Environment.getExternalStorageDirectory().getAbsolutePath() 
							+ "/Android/data/com.todpop.saltyenglish/pronounce/" + find.getString(0) + ".data";
					File file = new File(name);

					file.delete();

					pDB.delete("pronounce", "word='" + word + "'", null);
					new DownloadTask().execute(word, version);
				}
				else{
					pronouncePlay(word);
				}
			}
			else{
				pDB.delete("pronounce", "word='" + word + "'", null);
				new DownloadTask().execute(word, version);
			}
		}
		else{
			Log.i("STEVEN", "3");
			new DownloadTask().execute(word, version);
		}
		find.close();
		pDB.close();
	}	
	public void pronouncePlay(String word){
		SoundPool mSoundPool =  new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		mSoundPool.setOnLoadCompleteListener (new OnLoadCompleteListener() {
			@Override
			public void onLoadComplete(SoundPool soundPool, int soundId, int status) {
				soundPool.play(soundId, 100, 100, 1, 0, 1.0f); 
			}
		});
		mSoundPool.load(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/com.todpop.saltyenglish/pronounce/" + word + ".data", 1);
	}
	private class DownloadTask extends AsyncTask<String, String, String> {
		String word;
		String version;

		@Override
		protected String doInBackground(String... param) {
			try {
				String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/com.todpop.saltyenglish/pronounce/";
				File saltEng = new File(path);
				if(!saltEng.exists())
					saltEng.mkdirs();

				word = param[0];
				version = param[1];
				InputStream input = null;
				FileOutputStream fileOutput = null;
				HttpURLConnection connection = null;
				try {
					//TODO testing
					URL url = new URL("http://www.todpop.co.kr/uploads/voice/" + word + ".mp3");
					connection = (HttpURLConnection) url.openConnection();
					connection.connect();

					// expect HTTP 200 OK, so we don't mistakenly save error report 
					// instead of the file
					if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
						return getResources().getString(R.string.popup_view_download_progressbar_error);
					/*+ "Server returned HTTP " + connection.getResponseCode() 
		                    		 + " " + connection.getResponseMessage();*/

					// download the file
					String finalPath = path + word + ".data";

					File file = new File(finalPath);
					file.createNewFile();

					input = connection.getInputStream();
					fileOutput = new FileOutputStream(finalPath);
					//output = openFileOutput(word, Context.MODE_PRIVATE);

					byte data[] = new byte[1024];
					int count;
					while ((count = input.read(data)) != -1) {
						fileOutput.write(data, 0, count);
					}
				} catch (Exception e) {
					return getResources().getString(R.string.popup_view_download_progressbar_real_error) + e.toString();
				} finally {
					try {
						if (fileOutput != null){
							fileOutput.flush();
							fileOutput.close();
						}
						if (input != null)
							input.close();
					} 
					catch (IOException ignored) { }

					if (connection != null)
						connection.disconnect();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			return null;
		}    
		@Override
		protected void onPostExecute(String result) {
			if (result != null){
				Toast.makeText(StudyBegin.this, result, Toast.LENGTH_LONG).show();
			}
			else{
				pDB = pHelper.getWritableDatabase();

				ContentValues row = new ContentValues();
				Log.i("STEVEN", "before save");
				row.put("word", word);
				row.put("version", version);
				row.put("category", tmpCategory);

				pDB.insert("pronounce", null, row);
				pDB.close();

				Log.i("STEVEN", "saved version is " + version);
				pronouncePlay(word);
			}
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


	//----button onClick----


	public void onClickBack(View view)
	{
		//		Intent intent = new Intent(getApplicationContext(), StudyLearn.class);
		//		startActivity(intent);
		//		finish();
		finish();
	}

	//	public void showCouponPopView(View v)
	//	{
	//		FlurryAgent.logEvent("Coupon Get");
	//		new SendLog().execute("http://todpop.co.kr/api/advertises/set_cpd_log.json?ad_id=" + adId + "&ad_type=" + adType + "&user_id=" + userId + "&act=2&coupon_id=" + couponId);
	//		popupText.setText(R.string.study_finish_popup_text);
	//		popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
	//		popupWindow.showAsDropDown(null);
	//	}

	public void closePopup(View v)
	{
		popupWindow.dismiss();
	}

	public void showTestActivity(View view)
	{		
		if(tmpStage==1 || tmpStage==2 || tmpStage==4 || tmpStage==5 || tmpStage==7 || tmpStage==8) {
			//			Intent intent = new Intent(getApplicationContext(), StudyTestA.class);
			Intent intent = new Intent(getApplicationContext(), StudyTestRenewal.class);
			startActivity(intent);
			finish();
		} else if(tmpStage==3 || tmpStage==6 || tmpStage==9) {
			// jangjunho StudyTestB -> StudyTestCookie
			Intent intent = new Intent(getApplicationContext(), StudyTestCookie.class);
			startActivity(intent);
			finish();
		} else if(tmpStage==10) {
			Intent intent = new Intent(getApplicationContext(), StudyTestC.class);
			startActivity(intent);
			finish();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.study_bigin, menu);
		return false;
	}

	@Override
	public void onRestart() {
		super.onRestart();
		Log.i("STEVEN", "onRestart()");
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		mHelper.close();
		bitmapArr.clear();
		rootViewArr.clear();
		curJsonWords = null;
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
