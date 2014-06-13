package com.todpop.saltyenglish;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;
import com.todpop.api.TypefaceActivity;
import com.todpop.saltyenglish.db.WordDBHelper;

public class LvTestBigin extends TypefaceActivity {
	RelativeLayout introView;
	
	AnimationDrawable imageTimeAni ;
	ImageView imageTime ;
	
	ImageView imageTimeBlind;
	
	RelativeLayout endView;
	AnimationDrawable imageTestendAni;
	ImageView numberView;
	
	WordDBHelper mHelper;
	Button select1;
	Button select2;
	Button select3;
	Button select4;
	TextView enWord;
	String krWord1;
	String krWord2;
	String krWord3;
	String krWord4;
	int count=1;
	int correct = 0;
	String level=null;
	int density;
	static int correctOption;

	SharedPreferences rgInfo;
	SharedPreferences.Editor rgInfoEdit;
	
	SharedPreferences lvTextWord;
	SharedPreferences.Editor lvTextWordEdit;
	
	BlindAnim imageTimeBlindAni;
	TranslateAnimation.AnimationListener MyAnimationListener;
	
	String userId = "";
	String checkRW = "";
	
	boolean checkAni = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lv_test_bigin);

		FlurryAgent.logEvent("Level Test Start");
		
		density = (int) getResources().getDisplayMetrics().density;
		
		lvTextWord = getSharedPreferences("lvTextWord",0);
		lvTextWordEdit = lvTextWord.edit();
		mHelper = new WordDBHelper(this);
		rgInfo = getSharedPreferences("rgInfo",0);
		rgInfoEdit = rgInfo.edit();
		
		userId = rgInfo.getString("mem_id", "NO");

		introView = (RelativeLayout)findViewById(R.id.lvtestbigin_id_introView);

		numberView = (ImageView)findViewById(R.id.lvtextbigin_id_view_number);
		//test word
		enWord = (TextView)findViewById(R.id.lvtextbigin_id_enword);
		
		//select word button
		select1 = (Button)findViewById(R.id.lvtestbigin_id_select1);
		select2 = (Button)findViewById(R.id.lvtestbigin_id_select2);
		select3 = (Button)findViewById(R.id.lvtestbigin_id_select3);
		select4 = (Button)findViewById(R.id.lvtestbigin_id_select4);
		
		
		// my_word_book DB reset
		getApplicationContext().deleteDatabase("EngWord.db");				
		// Force create Database
		
		SQLiteDatabase db = mHelper.getReadableDatabase();
		try {
			db.execSQL("CREATE TABLE dic ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
					"name TEXT, mean TEXT, example_en TEXT, example_ko TEXT, phonetics TEXT, picture INTEGER, image_url TEXT, stage INTEGER, xo TEXT);");
			db.execSQL("CREATE TABLE mywords ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
					"name TEXT NOT NULL UNIQUE, mean TEXT);");
			db.execSQL("CREATE TABLE flip ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
						"name TEXT, mean TEXT, xo TEXT);");
			db.execSQL("CREATE TABLE cpxInfo ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
					"name TEXT NOT NULL UNIQUE, ad_id INTEGER, ad_type INTEGER, reward INTEGER, installed TEXT);");
			db.execSQL("CREATE TABLE mywordtest ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
					"name TEXT, mean TEXT, xo TEXT);");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		select1.setOnClickListener(new ButtonListener());
		select2.setOnClickListener(new ButtonListener());
		select3.setOnClickListener(new ButtonListener());
		select4.setOnClickListener(new ButtonListener());
		
		// For correct answer comparison 
		select1.setTag(1);
		select2.setTag(2);
		select3.setTag(3);
		select4.setTag(4);
		
		select1.setClickable(false);
		select2.setClickable(false);
		select3.setClickable(false);
		select4.setClickable(false);

		
		new GetWord().execute("http://todpop.co.kr/api/studies/get_level_test_words.json?step=1");


		ImageView textReady = (ImageView) findViewById(R.id.lvtest_2_text_ready);
		textReady.setBackgroundResource(R.drawable.lvtest_begin_drawable_readygo);
		AnimationDrawable textReadyAni = (AnimationDrawable) textReady.getBackground();
		textReadyAni.start();

		ImageView imageTestStart = (ImageView) findViewById(R.id.lvtest_2_image_teststart);
		imageTestStart.setBackgroundResource(R.drawable.lvtest_begin_drawable_startani);
		AnimationDrawable imageTestStartAni = (AnimationDrawable) imageTestStart.getBackground();
		imageTestStartAni.start();	

		imageTime = (ImageView) findViewById(R.id.lv_test_image_time);
		imageTime.setBackgroundResource(R.drawable.lvtest_begin_drawable_time_img);
		imageTimeAni = (AnimationDrawable) imageTime.getBackground();


		imageTimeBlind = (ImageView)findViewById(R.id.lv_test_image_timeblind);
		
		imageTimeBlindAni = new BlindAnim(
				TranslateAnimation.RELATIVE_TO_PARENT, 0.0f,
				TranslateAnimation.RELATIVE_TO_PARENT, 1.0f,
				TranslateAnimation.RELATIVE_TO_PARENT, 0.0f,
				TranslateAnimation.RELATIVE_TO_PARENT, 0.0f
				);	
		imageTimeBlindAni.setDuration(10000);
		imageTimeBlindAni.setRepeatCount(20);

		MyAnimationListener = new Animation.AnimationListener() {
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

				imageTimeAni.stop();

				imageTimeBlindAni.start();

				lvTextWordEdit.putString("check"+(count-1), "N");
				lvTextWordEdit.apply();
				checkRW = "x";

				count++;
				if(count == 21)
				{
					new GetWord().execute("http://todpop.co.kr/api/studies/get_level_test_words.json?user_id="+userId+"&step="+21+"&level="+level+"&ox="+checkRW);
					endView.setVisibility(View.VISIBLE);
					imageTestendAni.start();
//					Handler goNextHandler = new Handler();
//					goNextHandler.postDelayed(GoNextHandler, 1000);
				}else{
					new GetWord().execute("http://todpop.co.kr/api/studies/get_level_test_words.json?step="+count+"&level="+level+"&ox="+checkRW);
				}

			}
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
		};
		imageTimeBlindAni.setAnimationListener(MyAnimationListener);
		imageTimeBlind.setAnimation(imageTimeBlindAni);

		endView = (RelativeLayout)findViewById(R.id.lvtest_7_view);

		ImageView imageTestend = (ImageView) findViewById(R.id.lvtest_7_image_testend);
		imageTestend.setBackgroundResource(R.drawable.lvtest_begin_drawable_finish_ani);
		imageTestendAni = (AnimationDrawable) imageTestend.getBackground();


		
		Handler mHandler = new Handler();
		mHandler.postDelayed(mLaunchTaskMain, 4000);
	}
	
	private class BlindAnim extends TranslateAnimation {
		
		public BlindAnim(int fromXType, float fromXValue, int toXType,
				float toXValue, int fromYType, float fromYValue, int toYType,
				float toYValue) {
			super(fromXType, fromXValue, toXType, toXValue, fromYType, fromYValue, toYType,
					toYValue);
		}

		private long mPauseTime =0;
		private boolean mPaused = false;
		@Override
		public boolean getTransformation(long currentTime,
				Transformation outTransformation) {
			if(mPaused && mPauseTime == 0) {
				mPauseTime = currentTime-getStartTime();
			}
			if(mPaused) {
				setStartTime(currentTime-mPauseTime);
			}
			return super.getTransformation(currentTime, outTransformation);
		}
		
		public void pause() {
			mPauseTime = 0;
			mPaused = true;
		}
		
		public void resume() {
			mPaused = false;
		}
	}

	private class ButtonListener implements OnClickListener{
		public void onClick(View v)
		{
			
			select1.setClickable(false);
			select2.setClickable(false);
			select3.setClickable(false);
			select4.setClickable(false);
			
			int buttonTag = (Integer)v.getTag();
			if(buttonTag == correctOption){

				lvTextWordEdit.putString("check"+(count-1), "Y");
				lvTextWordEdit.apply();			
    			
				correct++;
    			checkRW = "o";
			}
			else{

				lvTextWordEdit.putString("check"+(count-1), "N");
				lvTextWordEdit.apply();
    			checkRW = "x";
			}

			count++;
			if(count == 21)
			{
				new GetWord().execute("http://todpop.co.kr/api/studies/get_level_test_words.json?user_id="+userId+"&step="+21+"&level="+level+"&ox="+checkRW);
				endView.setVisibility(View.VISIBLE);
				imageTestendAni.start();
//				Handler goNextHandler = new Handler();
//				goNextHandler.postDelayed(GoNextHandler, 1000);
			}else{
				new GetWord().execute("http://todpop.co.kr/api/studies/get_level_test_words.json?step="+count+"&level="+level+"&ox="+checkRW);
			}
			
			imageTimeAni.stop();
	
			imageTimeBlindAni.start();

		}
	}
	
	
	private class GetWord extends AsyncTask<String, Void, JSONObject> {
		
		
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
        	
        	if	(result != null)
        	{
        		try {
        			
        			JSONArray jsonArray = result.getJSONObject("data").getJSONArray("wrong");
					List<String> list = new ArrayList<String>();
					for(int i=0;i<jsonArray.length();i++)
					{
						 list.add( jsonArray.getString(i) );
					}	
        			

					lvTextWordEdit.putString("enWord"+(count-1), result.getJSONObject("data").getString("word"));
					lvTextWordEdit.putString("krWord"+(count-1), result.getJSONObject("data").getString("mean"));
					lvTextWordEdit.apply();
					
        			enWord.setText(result.getJSONObject("data").getString("word"));
        			select1.setText(result.getJSONObject("data").getString("mean"));
        			level = result.getJSONObject("data").getString("level");

        			int ran = new Random().nextInt(4);
        			
        			if (ran == 0) {
        				select1.setText(result.getJSONObject("data").getString("mean"));
        				select2.setText(list.get(0));
        				select3.setText(list.get(1));
        				select4.setText(list.get(2));
        				correctOption = 1;
        			} else if (ran == 1) {
        				select1.setText(list.get(0));
        				select2.setText(result.getJSONObject("data").getString("mean"));
        				select3.setText(list.get(1));
        				select4.setText(list.get(2));
        				correctOption = 2;
        			} else if (ran == 2) {
        				select1.setText(list.get(0));
        				select2.setText(list.get(1));
        				select3.setText(result.getJSONObject("data").getString("mean"));
        				select4.setText(list.get(2));
        				correctOption = 3;
        			} else if (ran == 3) {
        				select1.setText(list.get(0));
        				select2.setText(list.get(1));
        				select3.setText(list.get(2));
        				select4.setText(result.getJSONObject("data").getString("mean"));
        				correctOption = 4;
        			}
        			
        			
        			select1.setClickable(true);
        			select2.setClickable(true);
        			select3.setClickable(true);
        			select4.setClickable(true);
        			
        			if(count<21)
        			{
        				
        				if(checkAni == true){
        					imageTimeAni.start();
        				    imageTimeBlindAni.start();
        				}
        				
        				switch(count)
        				{
        					case 2:
        						numberView.setImageResource(R.drawable.lvtest_3_image_number2);					
        					break;
        					case 3:
        						numberView.setImageResource(R.drawable.lvtest_3_image_number3);					
        					break;
        					case 4:
        						numberView.setImageResource(R.drawable.lvtest_3_image_number4);					
        					break;
        					case 5:
        						numberView.setImageResource(R.drawable.lvtest_3_image_number5);					
        					break;
        					case 6:
        						numberView.setImageResource(R.drawable.lvtest_3_image_number6);					
        					break;
        					case 7:
        						numberView.setImageResource(R.drawable.lvtest_3_image_number7);					
        					break;
        					case 8:
        						numberView.setImageResource(R.drawable.lvtest_3_image_number8);					
        					break;
        					case 9:
        						numberView.setImageResource(R.drawable.lvtest_3_image_number9);					
        					break;
        					case 10:
        						numberView.setImageResource(R.drawable.lvtest_3_image_number10);					
        					break;
        					case 11:
        						numberView.setImageResource(R.drawable.lvtest_3_image_number11);					
        					break;
        					case 12:
        						numberView.setImageResource(R.drawable.lvtest_3_image_number12);					
        					break;
        					case 13:
        						numberView.setImageResource(R.drawable.lvtest_3_image_number13);					
        					break;
        					case 14:
        						numberView.setImageResource(R.drawable.lvtest_3_image_number14);					
        					break;
        					case 15:
        						numberView.setImageResource(R.drawable.lvtest_3_image_number15);					
        					break;
        					case 16:
        						numberView.setImageResource(R.drawable.lvtest_3_image_number16);					
        					break;
        					case 17:
        						numberView.setImageResource(R.drawable.lvtest_3_image_number17);					
        					break;
        					case 18:
        						numberView.setImageResource(R.drawable.lvtest_3_image_number18);					
        					break;
        					case 19:
        						numberView.setImageResource(R.drawable.lvtest_3_image_number19);					
        					break;
        					case 20:
        						numberView.setImageResource(R.drawable.lvtest_3_image_number20);					
        					break;
        				

        				}
        			}else{
        				imageTestendAni.stop();
        				
        				SharedPreferences studyInfo = getSharedPreferences("studyInfo",0);
        				SharedPreferences.Editor studyInfoEdit = studyInfo.edit();
        				
        				if(count==21)
        				{
	            			String stageInfo = result.getJSONObject("data").getString("stage_info");
        					studyInfoEdit.putString("myLevel", level);
	            			studyInfoEdit.putString("stageInfo", stageInfo);
	            			studyInfoEdit.apply();
	            			
        				}

        				Intent intent = new Intent(getApplicationContext(), LvTestFinish.class);
        				startActivity(intent);
        				finish();
        			}
        			
        		} catch (Exception e) {
        			
        		}
        	}
        }
	}
	
	private Runnable mLaunchTaskMain = new Runnable() {
		public void run() {
			introView.setVisibility(View.GONE);
			checkAni =true;
			imageTimeAni.stop();
			imageTimeAni.start();

			imageTimeBlindAni.start();
	    }
	};

	
    @Override
    protected void onStart() {
        super.onStart();
        FlurryAgent.onStartSession(this, "ZKWGFP6HKJ33Y69SP5QY");
        EasyTracker.getInstance(this).activityStart(this);
    }
	
	@Override
	public void onResume() {
		super.onResume();
		com.facebook.AppEventsLogger.activateApp(this, "218233231697811");
		imageTimeBlindAni.resume();
	    //imageTimeBlindAni.start();
	    
	    imageTimeAni.start();
	}
	
	@Override
	public void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	    EasyTracker.getInstance(this).activityStop(this);
	}
	
	@Override
    public void onPause() {
       super.onPause();  
       imageTimeBlindAni.pause();
       imageTestendAni.stop();
       imageTimeAni.stop();
       
    }
	
    @Override
    protected void onRestart() {
        super.onRestart();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Map<String, String> stopParams = new HashMap<String, String>();
        stopParams.put("Stoped Question", String.valueOf(count));
        float score = (float)correct / count * 100;
        stopParams.put("Score", String.format("%.1f", score));
		FlurryAgent.logEvent("Level Test Destroyed", stopParams);
    }
    
    
    public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) 
		{
			final AlertDialog.Builder isExit = new AlertDialog.Builder(this);

			DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() 
			{
				public void onClick(DialogInterface dialog, int which) 
				{
					switch (which) 
					{
					case AlertDialog.BUTTON_POSITIVE:
						SharedPreferences settings = getSharedPreferences("setting", 0);
						SharedPreferences.Editor editor = settings.edit();
						editor.putString("check","YES");
						editor.apply();
						
						Intent intent = new Intent();
				        intent.setClass(LvTestBigin.this, MainActivity.class);    
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

			return false;
		}
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.lv_test_bigin, menu);
		return false;
	}
}
