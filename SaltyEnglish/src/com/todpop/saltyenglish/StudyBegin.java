package com.todpop.saltyenglish;


import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
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
import org.json.JSONObject;


import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class StudyBegin extends FragmentActivity {
	// popup view
	PopupWindow popupWindow;
	View popupview;
	RelativeLayout relative;
	TextView popupText;
		
		
	static int totalStage = 0;
	static int currentStage = 0;
	static int level = 0;
	static int stage = 0;
	
	// Slide Level Page
	ViewPager studyStartPageView;
	// PageAdapter for pageView
	StudyStartPagerAdapter studyStartPagerAdapter;
	// Fragment attached to pageView
	StudyStartFragment studyStartFragment;
	// word json
	static JSONArray jsonWords;
	// word json bitmap array
	static ArrayList<Bitmap> bitmapArr;
	
	// CPD Front & Back images
	static Bitmap cpdFrontImage;
	static Bitmap cpdBackImage;
	
	// Infomation for send CPD cound
	static int adId;
	static int adType;
	static String userId;
	static int adAct;
	static int picNull;
	
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
 	
 	// CPD image view
 	static ImageView cpdView;
 	 	
 	
 	static ArrayList<View> rootViewArr = new ArrayList<View>();
 	
 	static boolean isCardBack = false;
 	
 	// Database
 	WordDBHelper mHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_study_begin);
		
		// Get Level Stage info
//		Bundle bundle = getIntent().getExtras();
//		totalStage = bundle.getInt("currentStage");
		
		SharedPreferences levelInfo = getSharedPreferences("StudyLevelInfo",0);
		currentStage = levelInfo.getInt("currentStage", 1);
		Log.d("current stage ------------ ", Integer.toString(currentStage));
		
		level = (currentStage-1)/10+1;
		stage = currentStage%10;
		
		// Bitmap ArrayList
		bitmapArr = new ArrayList<Bitmap>();
		
		// Database initiation
		mHelper = new WordDBHelper(this);
		
		//popupview
		relative = (RelativeLayout)findViewById(R.id.studybegin_id_main_activity);;
		popupview = View.inflate(this, R.layout.popup_studyfinish_coupon_view, null);
		float density = getResources().getDisplayMetrics().density;
		popupWindow = new PopupWindow(popupview,(int)(300*density),(int)(180*density),true);
		popupText = (TextView)popupview.findViewById(R.id.popup_id_text);

		
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
		
		// Add Intro Button
		introBtn = (ImageButton)findViewById(R.id.studybegin_id_intro_button);
		SharedPreferences pref = getSharedPreferences("rgInfo",0);
		String introOk = pref.getString("introOk", "N");
		if (introOk.equals("N")) {
			introBtn.setVisibility(View.VISIBLE);
		} else {
			introBtn.setVisibility(View.GONE);
		}
		
		// Get word list - callback when get word list to setup page view
		//new GetWord().execute("http://todpop.co.kr/api/studies/get_level_words.json?stage=" + stage + "&level=" + level);

		String getWordsUrl = "http://todpop.co.kr/api/studies/get_level_words.json?stage=" + stage + "&level=" + level;
		
		GetWord getWordTask = new GetWord();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
			getWordTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, getWordsUrl);
		else
			getWordTask.execute(getWordsUrl);

		// Get CPD ad -> Moved to after GetWords Request
//		SharedPreferences rgInfo = getSharedPreferences("rgInfo",0);
//		userId = rgInfo.getString("mem_id", "1");
//		new GetCPD().execute("http://todpop.co.kr/api/advertises/get_cpd_ad.json?user_id=" + userId);
		
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
		editor.commit();
	}

	public void setupPagerView()
	{
		studyStartPageView = (ViewPager)findViewById(R.id.study_begin_id_pager);
		studyStartPagerAdapter = new StudyStartPagerAdapter(getSupportFragmentManager());		
		studyStartPageView.setAdapter(studyStartPagerAdapter);

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
			
			ImageView wholeCard = null;
			isCardBack = false;
			
			Bundle studyBeginArgs = getArguments();
			if(studyBeginArgs.getInt("studyStartPage")<10)
			{
				rootView = inflater.inflate(R.layout.fragment_study_begin, container, false);
				ImageView wordOn = (ImageView)rootView.findViewById(R.id.fragment_study_begin_id_word_on);
				wholeCard = (ImageView)rootView.findViewById(R.id.fragment_study_begin_whole_card);
				wholeCard.setOnClickListener(new BtnFlipListener());
							
				int pageNum = studyBeginArgs.getInt("studyStartPage");
				switch(pageNum)
				
				{
				case 0:
					wordOn.setBackgroundResource(R.drawable.study_8_image_number_01);
					break;
				case 1:
					wordOn.setBackgroundResource(R.drawable.study_8_image_number_02);
					break;
				case 2:
					wordOn.setBackgroundResource(R.drawable.study_8_image_number_03);
					break;
				case 3:
					wordOn.setBackgroundResource(R.drawable.study_8_image_number_04);
					break;
				case 4:
					wordOn.setBackgroundResource(R.drawable.study_8_image_number_05);
					break;
				case 5:
					wordOn.setBackgroundResource(R.drawable.study_8_image_number_06);
					break;
				case 6:
					wordOn.setBackgroundResource(R.drawable.study_8_image_number_07);
					break;
				case 7:
					wordOn.setBackgroundResource(R.drawable.study_8_image_number_08);
					break;
				case 8:
					wordOn.setBackgroundResource(R.drawable.study_8_image_number_09);
					break;
				case 9:
					wordOn.setBackgroundResource(R.drawable.study_8_image_number_10);
					break;
				}
				
				TextView word =  (TextView)rootView.findViewById(R.id.study_word_tv);
				TextView pron = (TextView)rootView.findViewById(R.id.study_word_pron_tv);
				TextView example = (TextView)rootView.findViewById(R.id.study_word_ex_tv);
				ImageView wordImage = (ImageView)rootView.findViewById(R.id.fragment_study_begin_id_word_img);

				// Setup word textview
				try {
					word.setText(jsonWords.getJSONObject(pageNum).get("name").toString());
					pron.setText("["+jsonWords.getJSONObject(pageNum).get("phonetics").toString()+"]");		// [+phonetics+]
					example.setText(jsonWords.getJSONObject(pageNum).get("example_en").toString());
					wordImage.setImageBitmap(bitmapArr.get(pageNum));
					wordImage.setScaleType(ImageView.ScaleType.FIT_CENTER);        // center and stretch
				} catch (Exception e) {
					
				}

				
			} else {
				rootView = inflater.inflate(R.layout.fragment_study_begin_finish, container, false);
				cpdView = (ImageView)rootView.findViewById(R.id.studyfinish_id_pop);
				cpdView.setOnClickListener(new CPDFlipListener());
				cpdView.setImageBitmap(cpdFrontImage);
				
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
					if (jsonWords.length() > i.intValue()) {
						TextView word =  (TextView)v.findViewById(R.id.study_word_tv);
						TextView pron = (TextView)v.findViewById(R.id.study_word_pron_tv);
						TextView example = (TextView)v.findViewById(R.id.study_word_ex_tv);
						
						// Setup word text view
						word.setText(jsonWords.getJSONObject(i).get("mean").toString());
						pron.setText("");
						example.setText(jsonWords.getJSONObject(i).get("example_ko").toString());
					}

				} catch (Exception e) {
					
				}
			}
			public void fillEnWordView(View v, Integer i)
			{
				try {
					if (jsonWords.length() > i.intValue()) {
						TextView word =  (TextView)v.findViewById(R.id.study_word_tv);
						TextView pron = (TextView)v.findViewById(R.id.study_word_pron_tv);
						TextView example = (TextView)v.findViewById(R.id.study_word_ex_tv);

						// Setup word textview
						word.setText(jsonWords.getJSONObject(i).get("name").toString());
						pron.setText(jsonWords.getJSONObject(i).get("phonetics").toString());
						example.setText(jsonWords.getJSONObject(i).get("example_en").toString());
					}

				} catch (Exception e) {
					
				}
			}
		} 
		
		class CPDFlipListener implements OnClickListener 
		{ 
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
					
					SQLiteDatabase db = mHelper.getWritableDatabase();
					try {
						db.execSQL("DELETE FROM dic WHERE stage=" + currentStage + ";");
					} catch (Exception e) {
						
					}
					
					jsonWords = json.getJSONArray("data");
					
					for(int i=0;i<jsonWords.length();i++) {												
//						ImageView wordImage = (ImageView)rootViewArr.get(i).findViewById(R.id.fragment_study_begin_id_word_img);
//						TextView word =  (TextView)rootViewArr.get(i).findViewById(R.id.study_word_tv);
//						TextView pron = (TextView)rootViewArr.get(i).findViewById(R.id.study_word_pron_tv);
//						TextView example = (TextView)rootViewArr.get(i).findViewById(R.id.study_word_ex_tv);
						
						// Setup Image
//						Integer picInt = (Integer)jsonWords.getJSONObject(i).get("picture");
//						if (picInt == 1) {
//							try {
//								// show The Image
//								//JSONObject img = jsonWords.getJSONObject(i).getJSONObject("image_url");
//								String imgUrl = "http://todpop.co.kr" + jsonWords.getJSONObject(i).getString("image_url");
//								URL url = new URL(imgUrl);
//								Log.d("url ------ ", url.toString());
//								new DownloadImageTask()
//								            .execute(url.toString());
//							} catch (Exception e) {
//								
//							}
//						}
						
						// Setup word textview
//						word.setText(jsonWords.getJSONObject(i).get("name").toString());
//						pron.setText(jsonWords.getJSONObject(i).get("phonetics").toString());
//						example.setText(jsonWords.getJSONObject(i).get("example_en").toString());
						
						// Save info to Database
						ContentValues row = new ContentValues();
						row.put("name", jsonWords.getJSONObject(i).get("name").toString());
						row.put("mean", jsonWords.getJSONObject(i).get("mean").toString());
						row.put("example_en", jsonWords.getJSONObject(i).get("example_en").toString());
						row.put("example_ko", jsonWords.getJSONObject(i).get("example_ko").toString());
						row.put("phonetics", jsonWords.getJSONObject(i).get("phonetics").toString());
						row.put("picture", (Integer)(jsonWords.getJSONObject(i).get("picture")));
						row.put("image_url", jsonWords.getJSONObject(i).get("image_url").toString());
						row.put("stage", currentStage);
						row.put("xo", "X");

						db.insert("dic", null, row);
						
					}
					
					if (jsonWords.length() < 10) {
												
						// Add additional 3 words
						Cursor otherCursor = db.rawQuery("SELECT name, mean, example_en, example_ko, phonetics, picture, image_url FROM dic WHERE " +
								"xo=\'X\' AND stage>=" + currentStage/10*10 + " AND stage <=" + (currentStage/10+1)*10 + 
								" AND stage <> " + currentStage + " ORDER BY RANDOM() LIMIT 3" , null);
						
						if (otherCursor.getCount() > 0) {
							while(otherCursor.moveToNext()) {
								JSONObject jsonObj= new JSONObject();
								jsonObj.put("id", 0);
								jsonObj.put("name", otherCursor.getString(0));
								jsonObj.put("mean", otherCursor.getString(1));
								jsonObj.put("example_en", otherCursor.getString(2));
								jsonObj.put("example_ko", otherCursor.getString(3));
								jsonObj.put("phonetics", otherCursor.getString(4));
								jsonObj.put("picture", otherCursor.getInt(5));
								jsonObj.put("image_url", otherCursor.getString(6));


								jsonWords.put(jsonObj);
								
								ContentValues row = new ContentValues();
								row.put("name", otherCursor.getString(0));
								row.put("mean", otherCursor.getString(1));
								row.put("example_en", otherCursor.getString(2));
								row.put("example_ko", otherCursor.getString(3));
								row.put("phonetics", otherCursor.getString(4));
								row.put("picture", otherCursor.getString(5));
								row.put("image_url", otherCursor.getString(6));
								row.put("stage", currentStage);
								row.put("xo", "X");

								db.insert("dic", null, row);
							}
						}

						
						if (jsonWords.length()<10) {
							Cursor otherCursor2 = db.rawQuery("SELECT name, mean, example_en, example_ko, phonetics, picture, image_url FROM dic WHERE " +
									"xo=\'O\' AND stage>=" + currentStage/10*10 + " AND stage <=" + (currentStage/10+1)*10 + 
									" AND stage <> " + currentStage + " ORDER BY RANDOM() LIMIT " + (10-jsonWords.length()) , null);
							if (otherCursor2.getCount()>0) {
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


									jsonWords.put(jsonObj);
									
									ContentValues row = new ContentValues();
									row.put("name", otherCursor2.getString(0));
									row.put("mean", otherCursor2.getString(1));
									row.put("example_en", otherCursor2.getString(2));
									row.put("example_ko", otherCursor2.getString(3));
									row.put("phonetics", otherCursor2.getString(4));
									row.put("picture", otherCursor2.getString(5));
									row.put("image_url", otherCursor2.getString(6));
									row.put("stage", currentStage);
									row.put("xo", "X");

									db.insert("dic", null, row);
								}
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
		        if (stage%10 == 1) {
		        	if (bitmapArr.size() == 10) {
		        		setupPagerView();
				        // Get CPD after Get Words
						SharedPreferences rgInfo = getSharedPreferences("rgInfo",0);
						userId = rgInfo.getString("mem_id", "1");
						new GetCPD().execute("http://todpop.co.kr/api/advertises/get_cpd_ad.json?user_id=" + userId);
		        	}
		        } else if (stage%10 <=9) {
		        	if (bitmapArr.size() == 7) {
		        		setupPagerView();
				        // Get CPD after Get Words
						SharedPreferences rgInfo = getSharedPreferences("rgInfo",0);
						userId = rgInfo.getString("mem_id", "1");
						new GetCPD().execute("http://todpop.co.kr/api/advertises/get_cpd_ad.json?user_id=" + userId);
		        	}
		        }
		        

		    }
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
				Log.d("Get CPD JSON RESPONSE ---- ", json.toString());				        	

				if(json.getBoolean("status")==true) {
					JSONObject cpdJsonObj = json.getJSONObject("data");
					
					adId = cpdJsonObj.getInt("ad_id");
					adType = cpdJsonObj.getInt("ad_type");
					
					try {
						String imgUrl = "http://todpop.co.kr" + cpdJsonObj.getString("front_image");
						URL url = new URL(imgUrl);
						Log.d("CPD front image url ------ ", url.toString());
						new DownloadImageTask("FRONT").execute(url.toString());
						
						String imgUrl2 = "http://todpop.co.kr" + cpdJsonObj.getString("back_image");
						URL url2 = new URL(imgUrl2);
						Log.d("CPD back image url ------ ", url2.toString());
						new DownloadImageTask("BACK").execute(url2.toString());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}else{		    
					Log.d("--------- CPD -----------", "JSON return null");
				}

			} catch (Exception e) {
				Log.d("Exception: ", e.toString());
			}
		}
		
		private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
			
			String imgTag = null;
			public DownloadImageTask (String imgTag) 
			{
				this.imgTag = imgTag;
			}
			
		    protected Bitmap doInBackground(String... urls) 
		    {
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
		    	if (imgTag.equals("FRONT")) {
		    		cpdFrontImage = result;
		    	} else if (imgTag.equals("BACK")) {
		    		cpdBackImage = result;
		    	}
		    }
		}		
	}
	
	private class SendCPDInfo extends AsyncTask<String, Void, JSONObject> 
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
			} catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		}

		@Override
		protected void onPostExecute(JSONObject json) {
			try {
				if(json.getBoolean("status")==true) {
					Log.d("Send CPD Info OK!", "OK");
				}else{		  
					Log.d("Send CPD Info Failed!", "NO");
				}

			} catch (Exception e) {
				Log.d("Exception: ", e.toString());
			}
		}		
	}
	//----button onClick----
	
	

	public void onClickBack(View view)
	{
//		Intent intent = new Intent(getApplicationContext(), StudyLearn.class);
//		startActivity(intent);
//		finish();
		finish();
	}
	
	public void showCouponPopView(View v)
	{
		popupText.setText(R.string.study_finish_popup_text);
		popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
		popupWindow.showAsDropDown(null);
	}
	
	public void closePopup(View v)
	{
		popupWindow.dismiss();
	}
	
	public void showTestActivity(View view)
	{		
//		SharedPreferences settings = getSharedPreferences("StudyLevelInfo", 0);
//		SharedPreferences.Editor editor = settings.edit();
//
//		editor.putInt("totalStage", totalStage);
//		editor.putInt("testLevel", level);
//		editor.putInt("testStage", stage);
//		editor.commit();
		
		Log.d("------- send info -----", "info");
		new SendCPDInfo().execute("http://todpop.co.kr/api/advertises/set_cpd_log.json?ad_id=" + adId + "&ad_type=" + adType + "&user_id=" + userId + "&act=1");
		
		if(stage==1 || stage==2 || stage==4 || stage==5 || stage==7 || stage==8) {
			Intent intent = new Intent(getApplicationContext(), StudyTestA.class);
			startActivity(intent);
			finish();
			
		} else if(stage==3 || stage==6 || stage==9) {
			Intent intent = new Intent(getApplicationContext(), StudyTestB.class);
			startActivity(intent);
			finish();
		}else if(stage==10)
		{
			Intent intent = new Intent(getApplicationContext(), StudyTestC.class);
			startActivity(intent);
			finish();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.study_bigin, menu);
		return true;
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		mHelper.close();
	}
	
	
	//------- Database Operation ------------------
	private class WordDBHelper extends SQLiteOpenHelper 
	{
		public WordDBHelper(Context context) {
			super(context, "EngWord.db", null, 1);
		}
		
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE dic ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
		"name TEXT, mean TEXT, example_en TEXT, example_ko TEXT, phonetics TEXT, picture INTEGER, image_url TEXT, stage INTEGER, xo TEXT);");
		}
		
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS dic");
			onCreate(db);
		}
	}
}



































