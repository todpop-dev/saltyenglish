package com.todpop.saltyenglish;


import java.util.ArrayList;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.drawable.AnimationDrawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class StudyTestB extends Activity {
	
	static int tmpStageAccumulated;
	
	// balloon animation view  
	Button balloonPink;
	Button balloonYellow;
	Button balloonBlue;
	Button balloonGreen;
	
	ArrayList<String> englishWords;
	ArrayList<String> optionOne;
	ArrayList<String> optionTwo;
	ArrayList<String> optionThree;
	ArrayList<String> optionFour;
	String finalAnswerForRequest = "";

	
	TextView enWordView; 

	//balloon view animation
	ObjectAnimator pinkAni;
	ObjectAnimator yellowAni;
	ObjectAnimator blueAni;
	ObjectAnimator greenAni;

	AnimationDrawable pinkDrawableAni;
	AnimationDrawable yellowDrawableAni;
	AnimationDrawable blueDrawableAni;
	AnimationDrawable greenDrawableAni;
	//word count
	int wordCount=0;
	//page number
	ImageView pageNumber;
	
 	// Database
 	WordDBHelper mHelper;
 	
 	// Intro Count
 	static int introCount;
 	// intro btn
 	ImageButton introBtn;
 	
 	// Manage Pause case
 	static boolean isRunning;
 	static boolean isTestFinish;
 	
 	static int correctOption = 0;
 	
 	// pivot time 
 	int pivotTime = 0;
 	
 	// Pause View
 	RelativeLayout pauseView;
 	
	private CountDownTimer progressTimer;
	private long timeSpent = 0;
	private long startTime = 10000;
	private boolean isPause = false;
	
	private long pinkTime = 0;
	private long yellowTime = 0;
	private long blueTime = 0;
	private long greenTime = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_study_test_b);
		FlurryAgent.logEvent("Study Test");
		
		// ArrayLists to hold words
		englishWords = new ArrayList<String>();
		optionOne = new ArrayList<String>();
		optionTwo = new ArrayList<String>();
		optionThree = new ArrayList<String>();
		optionFour = new ArrayList<String>();
		
		// Get pivot time
		SharedPreferences prefs = getSharedPreferences("rgInfo",0);
		pivotTime = prefs.getInt("pivotTime", 0);
		
		// Database initiation
		mHelper = new WordDBHelper(this);
		SharedPreferences studyInfo = getSharedPreferences("studyInfo", 0);
		tmpStageAccumulated = studyInfo.getInt("tmpStageAccumulated", 1);
		getTestWords();
		
		// English Word View
		enWordView = (TextView)findViewById(R.id.study_testb_id_enword);
		
		//balloon Button
		balloonPink 	= (Button)findViewById(R.id.study_testb_id_pink);
		balloonYellow 	= (Button)findViewById(R.id.study_testb_id_yellow);
		balloonBlue 	= (Button)findViewById(R.id.study_testb_id_blue);
		balloonGreen 	= (Button)findViewById(R.id.study_testb_id_green);
		
		balloonPink.setOnClickListener(new ButtonListener());
		balloonYellow.setOnClickListener(new ButtonListener());
		balloonBlue.setOnClickListener(new ButtonListener());
		balloonGreen.setOnClickListener(new ButtonListener());
		
		// initial words
		setupTestWords(0);

		//page number
		pageNumber = (ImageView)findViewById(R.id.study_testb_id_pagenumber);
		
		// Relative Layout for pausing
		pauseView = (RelativeLayout)findViewById(R.id.study_test_b_id_pause_layout);
		pauseView.setVisibility(View.GONE);

		// Intro 
		introCount = 0;
		introBtn = (ImageButton)findViewById(R.id.study_test_b_id_intro_button);
		SharedPreferences pref = getSharedPreferences("rgInfo",0);
		String introOk = pref.getString("introTestBOk", "N");
		if (introOk.equals("N")) {
			introCount = 0;
			introBtn.setVisibility(View.VISIBLE);
			balloonPink.setVisibility(View.INVISIBLE);
			balloonYellow.setVisibility(View.INVISIBLE);
			balloonBlue.setVisibility(View.INVISIBLE);
			balloonGreen.setVisibility(View.INVISIBLE);

		} else {
			introBtn.setVisibility(View.GONE);
			setUpAnimation();
			isRunning = true;
		}
		
		isTestFinish = false;
		

	}
	
	@Override
	public void onPause()
	{
		
		super.onPause();
		if(!isPause) {
			stopTimeCount();
		}

		if (isTestFinish == false) {
			isRunning = false;
			pauseView.setVisibility(View.VISIBLE);
		}

	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		if(isPause) {
			startTimeCount(startTime);
		}
	}
	
	public void pauseTestCB(View v) 
	{
		pauseView.setVisibility(View.VISIBLE);
		isRunning = false;
		pinkTime = pinkAni.getCurrentPlayTime();
		yellowTime = yellowAni.getCurrentPlayTime();
		blueTime = blueAni.getCurrentPlayTime();
		greenTime = greenAni.getCurrentPlayTime();
		stopTimeCount();
		
		pinkAni.cancel();
		yellowAni.cancel();
		blueAni.cancel();
		greenAni.cancel();
		
		
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
	
	public void introClickCB(View v)
	{
		introCount++;
		if(introCount == 1) {
			v.setBackgroundResource(R.drawable.test_2_image_tutorial2);
		} else if (introCount == 2) {
			v.setBackgroundResource(R.drawable.test_2_image_tutorial3);
		} else if (introCount == 3) {
			v.setVisibility(View.GONE);
			
			SharedPreferences pref = getSharedPreferences("rgInfo",0);
			SharedPreferences.Editor editor = pref.edit();
			editor.putString("introTestBOk", "Y");
			editor.commit();
			
			balloonPink.setVisibility(View.VISIBLE);
			balloonYellow.setVisibility(View.VISIBLE);
			balloonBlue.setVisibility(View.VISIBLE);
			balloonGreen.setVisibility(View.VISIBLE);
			this.setUpAnimation();
			isRunning = true;
		}
	}
	
	// Pause view call back
	public void pauseViewContinueTest(View v)
	{
		pauseView.setVisibility(View.GONE);
		isRunning = true;

		Log.d("spink pause time ---", Long.toString(pinkTime)+", "+pinkAni.getDuration());
		
		pinkAni.start();
		pinkAni.setCurrentPlayTime(pinkTime);
		
		
		Log.d("syellow pause time ---", Long.toString(yellowTime));

		yellowAni.start();
		yellowAni.setCurrentPlayTime(yellowTime);		
		
		
		Log.d("sblue pause time ---", Long.toString(blueTime));
		
		blueAni.start();
		blueAni.setCurrentPlayTime(blueTime);
		
		
		Log.d("sgreen pause time ---", Long.toString(greenTime));
		
		greenAni.start();
		greenAni.setCurrentPlayTime(greenTime);
		
		startTimeCount(startTime);

		//setUpAnimation();
	}
	
	public void pauseViewFinishTest(View v) 
	{
		Intent intent = new Intent(getApplicationContext(), StudyHome.class);
		startActivity(intent);
		finish();
	}
	
	// End of Pause view call back
	
	private void getTestWords()
	{
		try {
			SQLiteDatabase db = mHelper.getReadableDatabase();
			//Cursor cursor = db.query("dic", new String[] {"name",  "mean"}, null, null, null, null, null);
			Cursor cursor = db.rawQuery("SELECT name,  mean FROM dic WHERE stage=" + tmpStageAccumulated + ";", null);
			
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
	}

	@SuppressLint("NewApi")
	private void setUpAnimation() {
		// Set up page number -------
		String imageID = "test_9_image_number_"+(wordCount+1);
		int resID = getResources().getIdentifier(imageID , "drawable", getPackageName());
		pageNumber.setBackgroundResource(resID);
		// End of Set up page number --------
		
		balloonPink.setEnabled(true);
		balloonYellow.setEnabled(true);
		balloonBlue.setEnabled(true);
		balloonGreen.setEnabled(true);
		
		// Setup Test Words
		setupTestWords(wordCount);
		
		int sdk = android.os.Build.VERSION.SDK_INT;
		if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
			balloonPink.setBackgroundDrawable(null);
			balloonYellow.setBackgroundDrawable(null);
			balloonBlue.setBackgroundDrawable(null);
			balloonGreen.setBackgroundDrawable(null);
		} else {
			balloonPink.setBackground(null);
			balloonYellow.setBackground(null);
			balloonBlue.setBackground(null);
			balloonGreen.setBackground(null);
		}


		balloonPink.setBackgroundResource(R.drawable.studytestb_drawable_balloon_pink);
		balloonYellow.setBackgroundResource(R.drawable.studytestb_drawable_balloon_yellow);
		balloonBlue.setBackgroundResource(R.drawable.studytestb_drawable_balloon_blue);
		balloonGreen.setBackgroundResource(R.drawable.studytestb_drawable_balloon_green);

		pinkDrawableAni 	= (AnimationDrawable)balloonPink.getBackground();		
		yellowDrawableAni 	= (AnimationDrawable)balloonYellow.getBackground();
		blueDrawableAni 	= (AnimationDrawable)balloonBlue.getBackground();
		greenDrawableAni 	= (AnimationDrawable)balloonGreen.getBackground();

		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		
		//get display's density and if it's lower than xhdpi, set balloon's size to originalsize*0.75
		int balloonHeight;
		int balloonWidth;
		if(metrics.densityDpi <= DisplayMetrics.DENSITY_HIGH ){
		    balloonHeight = (int)(341 * 0.75);
		    balloonWidth = (int)(171 * 0.75);
		}
		else{
		    balloonHeight = 341;
		    balloonWidth = 171;
		}
		
		//balloon set random X
		int pinkRandomX = (int)(Math.random() * (metrics.widthPixels-balloonWidth));
		int yellowRandomX = (int)(Math.random() * (metrics.widthPixels-balloonWidth));
		while(((yellowRandomX + balloonWidth) >= pinkRandomX) && (yellowRandomX <= (pinkRandomX + balloonWidth))){
			Log.e("RE RANDOM!!!", "yellow");
			yellowRandomX = (int)(Math.random() * (metrics.widthPixels-balloonWidth));
		}		
		int greenRandomX = (int)(Math.random() * (metrics.widthPixels-balloonWidth));
		while(((greenRandomX + balloonWidth) >= yellowRandomX) && (greenRandomX <= (yellowRandomX + balloonWidth))){
			Log.e("RE RANDOM!!!", "green");
			greenRandomX = (int)(Math.random() * (metrics.widthPixels-balloonWidth));
		}
		int blueRandomX = (int)(Math.random() * (metrics.widthPixels-balloonWidth));
		while(((blueRandomX + balloonWidth) >= greenRandomX) && (blueRandomX <= (greenRandomX + balloonWidth))){
			Log.e("RE RANDOM!!!", "blue");
			blueRandomX = (int)(Math.random() * (metrics.widthPixels-balloonWidth));
		}

		
		balloonPink.setX(pinkRandomX);
		balloonYellow.setX(yellowRandomX);
		balloonBlue.setX(blueRandomX);
		balloonGreen.setX(greenRandomX);
		
		/*//balloon position adjustment
		int balloonSpacing;
		balloonSpacing = (int)(balloonHeight * 0.65);*/
		
		int pinkStartPosition = metrics.heightPixels;
		int yellowStartPosition = pinkStartPosition + balloonHeight;
		int greenStartPosition = yellowStartPosition + balloonHeight;
		int blueStartPosition = greenStartPosition + balloonHeight;

		int blueEndPosition = -balloonHeight;
		int greenEndPosition = blueEndPosition - balloonHeight;
		int yellowEndPosition = greenEndPosition - balloonHeight;
		int pinkEndPosition = yellowEndPosition - balloonHeight;
		
		Log.i("STEVEN BALLOON POSTION Y", "pos"+pinkStartPosition+" "+pinkEndPosition+" "+yellowStartPosition+" "+yellowEndPosition);
		pinkAni = ObjectAnimator.ofFloat(balloonPink, "y", pinkStartPosition, pinkEndPosition);
		yellowAni = ObjectAnimator.ofFloat(balloonYellow, "y", yellowStartPosition, yellowEndPosition);
		greenAni = ObjectAnimator.ofFloat(balloonGreen, "y", greenStartPosition, greenEndPosition);
		blueAni = ObjectAnimator.ofFloat(balloonBlue, "y", blueStartPosition, blueEndPosition);
		
		pinkAni.setRepeatCount(0);
		yellowAni.setRepeatCount(0);
		blueAni.setRepeatCount(0);
		greenAni.setRepeatCount(0);

		pinkAni.setDuration(10000);
		yellowAni.setDuration(10000);
		blueAni.setDuration(10000);
		greenAni.setDuration(10000);

		greenAni.addListener(mAnimationListener);
		pinkAni.start();
		yellowAni.start();
		blueAni.start();
		greenAni.start();
		
		startTime = 10000;
		startTimeCount(startTime);
	}

	private Runnable replayAni = new Runnable() {
		public void run() {
			pinkAni.end();
			yellowAni.end();
			blueAni.end();
			greenAni.end();

			// Setup Test Words
			setupTestWords(wordCount);
		}
	};

	private  AnimatorListener mAnimationListener = new AnimatorListenerAdapter() {
		public void onAnimationEnd(Animator animation) 
		{
			if (isRunning == true) {
				wordCount++;
				if(wordCount<10){
					stopTimeCount();
					setUpAnimation();
				}else{
					isTestFinish = true;
					wordCount = 0;
					Log.d("funny result: -----", finalAnswerForRequest);
					SharedPreferences sp = getSharedPreferences("StudyLevelInfo", 0);
					SharedPreferences.Editor editor = sp.edit();
					editor.putString("testResult", finalAnswerForRequest);
					editor.commit();
					
					Intent intent = new Intent(getApplicationContext(), StudyTestFinish.class);
					startActivity(intent);
					finish();
				}
			}
		}
		public void onAnimationCancel(Animator animation) {}
		public void onAnimationRepeat(Animator animation) {}
		public void onAnimationStart(Animator animation) {}
	};

	private class ButtonListener implements OnClickListener{
		public void onClick(View v){
			stopTimeCount();

			// english word set next count
			if(wordCount<10){	
				Handler animationHandler = new Handler();
				//update test word
				balloonPink.setEnabled(false);
				balloonYellow.setEnabled(false);
				balloonBlue.setEnabled(false);
				balloonGreen.setEnabled(false);
				
				SQLiteDatabase db = mHelper.getWritableDatabase();

				
				switch(v.getId())
				{
					case R.id.study_testb_id_pink:	
						pinkDrawableAni.start();
						animationHandler.postDelayed(replayAni, 500);
						
						if (correctOption == 1) {
							try {
								ContentValues cv = new ContentValues();
								cv.put("xo", "O");
								db.update("dic", cv, "name='"+ englishWords.get(wordCount) +"'", null);
								Log.d("------- save word o --------", englishWords.get(wordCount));
								
								if (timeSpent <= 3) {
									finalAnswerForRequest += "2";
								} else {
									finalAnswerForRequest += "1";
								}
																
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else {
							finalAnswerForRequest += "0";
						}
						
						break;

					case R.id.study_testb_id_yellow:
						yellowDrawableAni.start();
						animationHandler.postDelayed(replayAni, 500);
						
						if (correctOption == 2) {
							try {
								ContentValues cv = new ContentValues();
								cv.put("xo", "O");
								db.update("dic", cv, "name='"+ englishWords.get(wordCount) +"'", null);
								Log.d("------- save word o --------", englishWords.get(wordCount));
								
								if (timeSpent <= 3) {
									finalAnswerForRequest += "2";
								} else {
									finalAnswerForRequest += "1";
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else {
							finalAnswerForRequest += "0";
						}
						
						break;
					
					case R.id.study_testb_id_blue:
						blueDrawableAni.start();
						animationHandler.postDelayed(replayAni, 500);
						
						if (correctOption == 3) {
							try {
								ContentValues cv = new ContentValues();
								cv.put("xo", "O");
								db.update("dic", cv, "name='"+ englishWords.get(wordCount) +"'", null);
								Log.d("------- save word o --------", englishWords.get(wordCount));
								
								if (timeSpent <= 3) {
									finalAnswerForRequest += "2";
								} else {
									finalAnswerForRequest += "1";
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else {
							finalAnswerForRequest += "0";
						}
						
						break;

					case R.id.study_testb_id_green:
						greenDrawableAni.start();
						animationHandler.postDelayed(replayAni, 500);
						
						if (correctOption == 4) {
							try {
								ContentValues cv = new ContentValues();
								cv.put("xo", "O");
								db.update("dic", cv, "name='"+ englishWords.get(wordCount) +"'", null);
								Log.d("------- save word o --------", englishWords.get(wordCount));
								
								if (timeSpent <= 3) {
									finalAnswerForRequest += "2";
								} else {
									finalAnswerForRequest += "1";
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else {
							finalAnswerForRequest += "0";
						}
						
						break;


				}
				
			}else{
				Intent intent = new Intent(getApplicationContext(), StudyTestFinish.class);
				startActivity(intent);
				finish();
			}
		}
	}
	
	private void setupTestWords(int count)
	{
		enWordView.setText(englishWords.get(count));
		
		int ran = (int)(Math.random() * 4);
		Log.d("ran number ------ ", Integer.toString(ran));
		
		if (ran == 0) {
			balloonPink.setText(optionOne.get(count));
			balloonYellow.setText(optionTwo.get(count));
			balloonBlue.setText(optionThree.get(count));
			balloonGreen.setText(optionFour.get(count));
			correctOption = 1;
		} else if (ran == 1) {
			balloonPink.setText(optionTwo.get(count));
			balloonYellow.setText(optionOne.get(count));
			balloonBlue.setText(optionThree.get(count));
			balloonGreen.setText(optionFour.get(count));
			correctOption = 2;

		} else if (ran == 2) {
			balloonPink.setText(optionTwo.get(count));
			balloonYellow.setText(optionThree.get(count));
			balloonBlue.setText(optionOne.get(count));
			balloonGreen.setText(optionFour.get(count));
			correctOption = 3;

		} else if (ran == 3) {
			balloonPink.setText(optionTwo.get(count));
			balloonYellow.setText(optionThree.get(count));
			balloonBlue.setText(optionFour.get(count));
			balloonGreen.setText(optionOne.get(count));
			correctOption = 4;

		}
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		mHelper.close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.study_test_b, menu);
		return true;
	}

	//------- Database Operation ------------------
	private class WordDBHelper extends SQLiteOpenHelper {
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
	@Override
	protected void onStart()
	{
		super.onStart();
		FlurryAgent.onStartSession(this, "ZKWGFP6HKJ33Y69SP5QY");
	    EasyTracker.getInstance(this).activityStart(this);
	}
	 @Override
	 public void onBackPressed(){

			pauseView.setVisibility(View.VISIBLE);
			if(isRunning){
				isRunning = false;
				pinkTime = pinkAni.getCurrentPlayTime();
				yellowTime = yellowAni.getCurrentPlayTime();
				blueTime = blueAni.getCurrentPlayTime();
				greenTime = greenAni.getCurrentPlayTime();
				stopTimeCount();
			
				pinkAni.cancel();
				yellowAni.cancel();
				blueAni.cancel();
				greenAni.cancel();
			}
	 }
	@Override
	protected void onStop()
	{
		super.onStop();		
		FlurryAgent.onEndSession(this);
	    EasyTracker.getInstance(this).activityStop(this);
	}
	
}
