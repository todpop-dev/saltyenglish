package com.todpop.saltyenglish;




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
import org.json.JSONArray;
import org.json.JSONObject;

import com.flurry.android.FlurryAgent;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;


public class WordListTest extends Activity {
	
	//test word list size from HomeWordList activity
	int wordListSize;
	
	//test word
	TextView enWordText;
	TextView countText;
	//select word button
	Button select1;
	Button select2;
	Button select3;
	Button select4;
	//page number
	//ImageView pageNumber;
	
 	// Database
 	WordDBHelper mHelper;
	
	ArrayList<String> englishWords;
	ArrayList<String> optionOne;
	ArrayList<String> optionTwo;
	ArrayList<String> optionThree;
	ArrayList<String> optionFour;
	static int correctOption;
	
 	// Manage Pause case
 	static boolean isRunning;
 	static boolean isTestFinish;
 	
 	// Pause View
 	RelativeLayout pauseView;
	
	//check word count
	int wordCount=0;
	
	//blind and blind animation
	ImageView blindView;
	BlindAnim imageTimeBlindAni;
	TranslateAnimation.AnimationListener MyAnimationListener;
	
	//crocodile Time animation
	ImageView crocodileTime;
	AnimationDrawable crocodileTimeAni;
	AnimationDrawable crocodileCurrent;
	Drawable currentFrame, checkFrame;
	boolean crocoPause = false;
	
	// System pivot time
	int pivotTime = 0;
	CountDownTimer progressTimer;
	private long timeSpent = 0;
	private long startTime = 10000;
	private boolean isPause = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wordlist_test);
		FlurryAgent.logEvent("Word Test");
		Intent intent = getIntent();
		wordListSize = intent.getIntExtra("testListSize", 1);
		
		//test word
		enWordText = (TextView)findViewById(R.id.wordlist_test_id_enword);
		countText = (TextView)findViewById(R.id.wordlist_test_id_count);
		
		//select word button
		select1 = (Button)findViewById(R.id.wordlist_test_id_select1);
		select2 = (Button)findViewById(R.id.wordlist_test_id_select2);
		select3 = (Button)findViewById(R.id.wordlist_test_id_select3);
		select4 = (Button)findViewById(R.id.wordlist_test_id_select4);
		select1.setOnClickListener(new ButtonListener());
		select2.setOnClickListener(new ButtonListener());
		select3.setOnClickListener(new ButtonListener());
		select4.setOnClickListener(new ButtonListener());
		
		// For correct answer comparison 
		select1.setTag(1);
		select2.setTag(2);
		select3.setTag(3);
		select4.setTag(4);
		
		// Database initiation
		mHelper = new WordDBHelper(this);
		
		// ArrayLists to hold words
		englishWords = new ArrayList<String>();
		optionOne = new ArrayList<String>();
		optionTwo = new ArrayList<String>();
		optionThree = new ArrayList<String>();
		optionFour = new ArrayList<String>();
		
		getTestWords();
		
		countText.setText("1/" + String.valueOf(wordListSize));
		
		// Relative Layout for pausing
		pauseView = (RelativeLayout)findViewById(R.id.wordlist_test_id_pause_layout);
		pauseView.setVisibility(View.GONE);
		
		isRunning = true;
		isTestFinish = false;
		
		// Get Pivot Time
		SharedPreferences prefs = getSharedPreferences("rgInfo",0);
		pivotTime = prefs.getInt("pivotTime", 0);


		//set first test word
		setupTestWords(0);
		
		//crocodile Time animation
		
		crocodileTime = (ImageView) findViewById(R.id.wordlist_test_id_crocodile_time);
		crocodileTime.setBackgroundResource(R.drawable.lvtest_begin_drawable_time_img);
		crocodileTimeAni = (AnimationDrawable)crocodileTime.getBackground();
		//blind time animation
		blindView = (ImageView)findViewById(R.id.wordlist_test_id_image_timeblind);
		imageTimeBlindAni = new BlindAnim(
				TranslateAnimation.RELATIVE_TO_PARENT, 0.0f,
				TranslateAnimation.RELATIVE_TO_PARENT, 1.0f,
				TranslateAnimation.RELATIVE_TO_PARENT, 0.0f,
				TranslateAnimation.RELATIVE_TO_PARENT, 0.0f
				);
		
		imageTimeBlindAni.setDuration(10000);
		imageTimeBlindAni.setRepeatCount(wordListSize);
		
		MyAnimationListener = new Animation.AnimationListener() {
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub			
			}		
			public void onAnimationRepeat(Animation animation) {
				// english word set next count
				wordCount++;
				
				if(wordCount < wordListSize) {
					crocodileTimeAni.stop();
					stopTimeCount();				
					crocodileNewStart();
					// string to drawable ID
					countText.setText(String.valueOf(wordCount + 1) + "/" + String.valueOf(wordListSize));
					//update test word
					setupTestWords(wordCount);
					startTime = 10000;
					startTimeCount(startTime);
				} else {
					stopTimeCount();
					isTestFinish = true;
					imageTimeBlindAni.cancel();
					Intent intent = new Intent(getApplicationContext(), WordListTestFinish.class);
					startActivity(intent);
					finish();
				}
			}		
			
			public void onAnimationEnd(Animation animation) 
			{
				
			}
		};
		
		imageTimeBlindAni.setAnimationListener(MyAnimationListener);
		blindView.setAnimation(imageTimeBlindAni);
		crocodileTimeAni.start();
		
		startTimeCount(10000);

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
	
	@SuppressLint("NewApi")
	public void crocodilePause() {
		crocoPause = true;
		currentFrame = crocodileTimeAni.getCurrent();
		crocodileTimeAni.stop();
		crocodileCurrent = new AnimationDrawable();
		boolean flag = false;
		int sync = 1;
		for(int i =0; i < crocodileTimeAni.getNumberOfFrames();i++) {
			checkFrame = crocodileTimeAni.getFrame(i);
			
			if(checkFrame == currentFrame || flag) {
				flag = true;
				crocodileCurrent.addFrame(checkFrame, 422);
			}
		}
		crocodileTimeAni = crocodileCurrent;
		int sdk = android.os.Build.VERSION.SDK_INT;
		if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
			crocodileTime.setBackgroundDrawable(crocodileTimeAni);
		} else {
			crocodileTime.setBackground(crocodileTimeAni);
		}
	}
	
	public void crocodileNewStart() {
		crocoPause = false;
		crocodileTime.setBackgroundResource(R.drawable.lvtest_begin_drawable_time_img);
		crocodileTimeAni = (AnimationDrawable)crocodileTime.getBackground();
		crocodileTimeAni.start();
	}
	
	public void crocodileResume() {
		crocoPause = false;
		crocodileTimeAni.start();
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		isRunning = false;
		if (isTestFinish == false) {
			pauseView.setVisibility(View.VISIBLE);
			imageTimeBlindAni.pause();
			if(!isPause) {
				stopTimeCount();
			}
			if(!crocoPause) {
				crocodilePause();
			}
		}
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		if(isPause) {
			startTimeCount(startTime);
		}
		if(crocoPause) {
			crocodileResume();
		}
	}
	
	public void pauseTestCB(View v) 
	{
		pauseView.setVisibility(View.VISIBLE);
		isRunning = false;
		imageTimeBlindAni.pause();
		crocodilePause();
		stopTimeCount();
	}
	
	// Pause view call back
	public void pauseViewContinueTest(View v)
	{
		pauseView.setVisibility(View.GONE);
		isRunning = true;
		imageTimeBlindAni.resume();
		crocodileResume();
		startTimeCount(startTime);
	}
	
	public void pauseViewFinishTest(View v) 
	{
		finish();
	}
	
	public void startTimeCount(long start) {
		Log.d("start time----", Long.toString(start));
		progressTimer =  new CountDownTimer(start, 1000) {
			 public void onTick(long millisUntilFinished) {
				 startTime = millisUntilFinished;
				 timeSpent = (10000-(int)millisUntilFinished)/1000;
				 Log.d("time spent --- xxxx --- ", Long.toString(timeSpent));
			 }
			 
			 public void onFinish() {
	
			 }
		};
		isPause = false;
		progressTimer.start();
	}
	public void stopTimeCount() {
		isPause = true;
		progressTimer.cancel();
	}
	
	
	private void getTestWords()
	{
		SQLiteDatabase db = mHelper.getReadableDatabase();
		//Cursor cursor = db.query("dic", new String[] {"name",  "mean"}, null, null, null, null, null);
		try {
			Cursor cursor = db.rawQuery("SELECT DISTINCT name,  mean FROM mywords ORDER BY RANDOM() LIMIT " + wordListSize, null);
			
			if (cursor.getCount()>0) {
				while(cursor.moveToNext()) {
					englishWords.add(cursor.getString(0));
					optionOne.add(cursor.getString(1));
					
					Cursor otherCursor = db.rawQuery("SELECT DISTINCT mean FROM dic WHERE mean <> '" + cursor.getString(1) + "' ORDER BY RANDOM() LIMIT 3", null);
					otherCursor.moveToNext();
					optionTwo.add(otherCursor.getString(0));
					otherCursor.moveToNext();
					optionThree.add(otherCursor.getString(0));
					otherCursor.moveToNext();
					optionFour.add(otherCursor.getString(0));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (int i=0; i<englishWords.size(); i++) {
			
			try {
				// Save to Flip DB
				ContentValues cv = new ContentValues();
				cv.put("name", englishWords.get(i));
				cv.put("mean", optionOne.get(i));
				cv.put("xo", "X");
				db.insert("mywordtest", null, cv);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private class ButtonListener implements OnClickListener{
		public void onClick(View v)
		{
			//wordCount++;
			stopTimeCount();

			// english word set next count
			
			//blind animation start
			imageTimeBlindAni.cancel();
			imageTimeBlindAni.start();
			crocodileTimeAni.stop();
			crocodileNewStart();
			// string to drawable ID
			
			// ------- Compare Answer First!! ---------------
			SQLiteDatabase db = mHelper.getWritableDatabase();

			// Here we get correct answer, so save to database as 'O'
			int buttonTag = (Integer)v.getTag();
			Log.d("correct Option", Integer.toString(correctOption));
			if (buttonTag == correctOption) {
				try {
					//Cursor cs = db.rawQuery("UPDATE dic SET xo='O' WHERE name='" + englishWords.get(wordCount) + "'", null);
					ContentValues cv = new ContentValues();
					cv.put("xo", "O");
					db.update("mywordtest", cv, "name='"+ englishWords.get(wordCount) +"'", null);
					Log.d("------- save word o --------", englishWords.get(wordCount));
					
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			// ------- Compare Answer First!! ---------------

			wordCount++;
			
			// Setup Label
			countText.setText(String.valueOf(wordCount + 1) + "/" + String.valueOf(wordListSize));
			
			// Setup Words
			if (wordCount <= wordListSize-1) {
				setupTestWords(wordCount);
				startTime = 10000;
				startTimeCount(startTime);
			} else if (wordCount == wordListSize) {
				stopTimeCount();
				isTestFinish = true;
				Intent intent = new Intent(getApplicationContext(), WordListTestFinish.class);
				startActivity(intent);
				finish();
			}
		}
	}
	

	
	
	private void setupTestWords(int count)
	{
		enWordText.setText(englishWords.get(count));
		
		int ran = (int)(Math.random() * 4);
		Log.d("ran number ------ ", Integer.toString(ran));
		
		if (ran == 0) {
			select1.setText(optionOne.get(count));
			select2.setText(optionTwo.get(count));
			select3.setText(optionThree.get(count));
			select4.setText(optionFour.get(count));
			correctOption = 1;
		} else if (ran == 1) {
			select1.setText(optionTwo.get(count));
			select2.setText(optionOne.get(count));
			select3.setText(optionThree.get(count));
			select4.setText(optionFour.get(count));
			correctOption = 2;
		} else if (ran == 2) {
			select1.setText(optionTwo.get(count));
			select2.setText(optionThree.get(count));
			select3.setText(optionOne.get(count));
			select4.setText(optionFour.get(count));
			correctOption = 3;
		} else if (ran == 3) {
			select1.setText(optionTwo.get(count));
			select2.setText(optionThree.get(count));
			select3.setText(optionFour.get(count));
			select4.setText(optionOne.get(count));
			correctOption = 4;
		}
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		mHelper.close();
	}
	
	//------- Database Operation ------------------
	private class WordDBHelper extends SQLiteOpenHelper {
		public WordDBHelper(Context context) {
			super(context, "EngWord.db", null, 1);
		}
		
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE mywords ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
		"name TEXT NOT NULL UNIQUE, mean TEXT);");
			db.execSQL("CREATE TABLE dic ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
		"name TEXT, mean TEXT, example_en TEXT, example_ko TEXT, phonetics TEXT, picture INTEGER, image_url TEXT, stage INTEGER, xo TEXT);");
			db.execSQL("CREATE TABLE mywordtest ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
		"name TEXT, mean TEXT, xo TEXT);");
		}
		
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS mywords");
			db.execSQL("DROP TABLE IF EXISTS dic");
			db.execSQL("DROP TABLE IF EXISTS mywordtest");
			onCreate(db);
		}
	}
	@Override
	protected void onStart()
	{
		super.onStart();
		FlurryAgent.onStartSession(this, "ZKWGFP6HKJ33Y69SP5QY");
	}
	 
	@Override
	protected void onStop()
	{
		super.onStop();		
		FlurryAgent.onEndSession(this);
	}
}