package com.todpop.saltyenglish;




import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.Animator.AnimatorListener;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

public class StudyTestC extends Activity {
	ArrayList<String> englishWords;
	ArrayList<String> englishMeans;
	ArrayList<String> tmpArray;
	
	ArrayList<String> randomArrange1;
	ArrayList<String> randomArrange2;
	ArrayList<String> randomArrange3;
	ArrayList<String> randomArrange4;
	ArrayList<String> randomArrange5;
	ArrayList<String> randomArrange6;
	
	static int totalStage;
	static int testSetCount;
	static int cardCount;
	static int finalAnswerForRequest = 0;

	
 	// Database
 	WordDBHelper mHelper;
	
	Button card1_1;
	Button card1_2;
	Button card1_3;
	Button card1_4;
	Button card2_1;
	Button card2_2;
	Button card2_3;
	Button card2_4;
	Button card3_1;
	Button card3_2;
	Button card3_3;
	Button card3_4;
	
	String cardText1_1;
	String cardText1_2;
	String cardText1_3;
	String cardText1_4;
	String cardText2_1;
	String cardText2_2;
	String cardText2_3;
	String cardText2_4;
	String cardText3_1;
	String cardText3_2;
	String cardText3_3;
	String cardText3_4;
	
	Button select1;
	Button select2;
	
	int TAG_FRONT = 1;
	int TAG_BACK = 0;
	
	// Intro Button
	ImageButton introBtn;
	static int introCount;
	
	static int progressBarLength; 
	
//	String isSelect1;
//	String isSelect2;
	
	int cardCheck = 0;
	
	Animation animation;
	PoliceAnim policeManAni;
	ImageView policeMan;
	ProgressTask progressTask;
	CountDownTimer progressTimer;
	private long startTime = 180000;
	private boolean isPause = false;
	
	private ProgressBar progress;
	
	// Pause Layout
	RelativeLayout pauseView;
	
	static int countDownTime = 180000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_study_test_c);
		
		englishWords = new ArrayList<String>();
		englishMeans = new ArrayList<String>();
		
		randomArrange1 = new ArrayList<String>();
		randomArrange2 = new ArrayList<String>();
		randomArrange3 = new ArrayList<String>();
		randomArrange4 = new ArrayList<String>();
		randomArrange5 = new ArrayList<String>();
		randomArrange6 = new ArrayList<String>();
		
		testSetCount = 0;
		cardCount = 12;
		boolean introFlag = false;
		// Show Introduction in first launch
		introBtn = (ImageButton)findViewById(R.id.study_test_c_id_intro_button);
		SharedPreferences pref = getSharedPreferences("rgInfo",0);
		String introOk = pref.getString("introTestCOk", "N");
		if (introOk.equals("N")) {
			introCount = 0;
			introFlag = true;
			introBtn.setVisibility(View.VISIBLE);
		} else {
			introFlag = false;
			introBtn.setVisibility(View.GONE);
		}
		
		// Database initiation
		mHelper = new WordDBHelper(this);
		
		// Pause Layout
		pauseView = (RelativeLayout)findViewById(R.id.study_test_c_id_pause_layout);
		pauseView.setVisibility(View.GONE);
		
		SharedPreferences levelInfoSp = getSharedPreferences("StudyLevelInfo", 0);
		totalStage = levelInfoSp.getInt("currentStage", 1);
		Log.d("first totalStage ----", Integer.toString(totalStage));
		getTestWords();
		
		card1_1 = (Button)findViewById(R.id.study_testc_card_1_1);
		card1_2 = (Button)findViewById(R.id.study_testc_card_1_2);
		card1_3 = (Button)findViewById(R.id.study_testc_card_1_3);
		card1_4 = (Button)findViewById(R.id.study_testc_card_1_4);
		card2_1 = (Button)findViewById(R.id.study_testc_card_2_1);
		card2_2 = (Button)findViewById(R.id.study_testc_card_2_2);
		card2_3 = (Button)findViewById(R.id.study_testc_card_2_3);
		card2_4 = (Button)findViewById(R.id.study_testc_card_2_4);
		card3_1 = (Button)findViewById(R.id.study_testc_card_3_1);
		card3_2 = (Button)findViewById(R.id.study_testc_card_3_2);
		card3_3 = (Button)findViewById(R.id.study_testc_card_3_3);
		card3_4 = (Button)findViewById(R.id.study_testc_card_3_4);
		
		card1_1.setTag(TAG_BACK);
		card1_2.setTag(TAG_BACK);
		card1_3.setTag(TAG_BACK);
		card1_4.setTag(TAG_BACK);
		card2_1.setTag(TAG_BACK);
		card2_2.setTag(TAG_BACK);
		card2_3.setTag(TAG_BACK);
		card2_4.setTag(TAG_BACK);
		card3_1.setTag(TAG_BACK);
		card3_2.setTag(TAG_BACK);
		card3_3.setTag(TAG_BACK);
		card3_4.setTag(TAG_BACK);

		
		card1_1.setOnClickListener(new BtnFlipListener());
		card1_2.setOnClickListener(new BtnFlipListener());
		card1_3.setOnClickListener(new BtnFlipListener());
		card1_4.setOnClickListener(new BtnFlipListener());
		card2_1.setOnClickListener(new BtnFlipListener());
		card2_2.setOnClickListener(new BtnFlipListener());
		card2_3.setOnClickListener(new BtnFlipListener());
		card2_4.setOnClickListener(new BtnFlipListener());
		card3_1.setOnClickListener(new BtnFlipListener());
		card3_2.setOnClickListener(new BtnFlipListener());
		card3_3.setOnClickListener(new BtnFlipListener());
		card3_4.setOnClickListener(new BtnFlipListener());
		
		 cardText1_1 = "monday";
		 cardText1_2 = "tuesday";
		 cardText1_3 ="wednesday";
		 cardText1_4 ="thursday";
		 cardText2_1 ="friday";
		 cardText2_2 ="saturday";
		 cardText2_3 ="monday";
		 cardText2_4 ="tuesday";
		 cardText3_1 ="wednesday";
		 cardText3_2 ="thursday";
		 cardText3_3 ="friday";
		 cardText3_4 ="saturday";
		 if(!introFlag) {
			 progress = (ProgressBar)findViewById(R.id.study_testc_id_progress_bar);
			 progress.setMax(180000);;
			 
			 policeMan = (ImageView)findViewById(R.id.study_testc_id_police);
			 policeManAni = new PoliceAnim(
						TranslateAnimation.RELATIVE_TO_PARENT, 0.0f,
						TranslateAnimation.RELATIVE_TO_PARENT, 0.7f,
						TranslateAnimation.RELATIVE_TO_PARENT, 0.0f,
						TranslateAnimation.RELATIVE_TO_PARENT, 0.0f
						);
			 policeManAni.setDuration(180000);
			 policeManAni.setRepeatCount(0);
			 policeMan.setAnimation(policeManAni);
			 
			 startTimeCount(startTime);
		 }	 
	}
	private class PoliceAnim extends TranslateAnimation {
		
		public PoliceAnim(int fromXType, float fromXValue, int toXType,
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
	public void startTimeCount(long start) {
		Log.d("start time----", Long.toString(start));
		progressTimer =  new CountDownTimer(start, 1000) {
			public void onTick(long millisUntilFinished) {
				startTime = millisUntilFinished;
				progressBarLength = 180000-(int)millisUntilFinished;
				progress.setProgress(progressBarLength);
				Log.d("time count --- xxxx --- ", Long.toString(startTime));
			}
			 
			public void onFinish() {
				SharedPreferences levelPref = getSharedPreferences("StudyLevelInfo",0);
				SharedPreferences.Editor editor = levelPref.edit();
				editor.putString("testResult", Integer.toString(finalAnswerForRequest));
				editor.commit();
				
				Intent intent = new Intent(getApplicationContext(), StudyTestFinish.class);
				startActivity(intent);
				finish();
			 }
		 };
		isPause = false;
		progressTimer.start();
	}
	public void stopTimeCount() {
		isPause = true;
		progressTimer.cancel();
	}
	@Override
	public void onPause()
	{
		super.onPause();
		if(!isPause) {
			stopTimeCount();
		}
		policeManAni.pause();
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
		policeManAni.pause();
		stopTimeCount();
	}
	
	public void pauseViewContinueTest(View v)
	{
		pauseView.setVisibility(View.GONE);
		startTimeCount(startTime);
		policeManAni.resume();
	}
	
	public void pauseViewFinishTest(View v) 
	{
		Intent intent = new Intent(getApplicationContext(), StudyHome.class);
		startActivity(intent);
		finish();
	}
	
	public void introClickCB(View v)
	{
		introCount++;
		if(introCount == 1) {
			v.setBackgroundResource(R.drawable.test_10_image_tutorial2);
		} else if (introCount == 2) {
			v.setBackgroundResource(R.drawable.test_10_image_tutorial3);
		} else if (introCount == 3) {
			v.setVisibility(View.GONE);
			
			SharedPreferences pref = getSharedPreferences("rgInfo",0);
			SharedPreferences.Editor editor = pref.edit();
			editor.putString("introTestCOk", "Y");
			editor.commit();
			
			progress = (ProgressBar)findViewById(R.id.study_testc_id_progress_bar);
			progress.setMax(180000);;
			 
			policeMan = (ImageView)findViewById(R.id.study_testc_id_police);
			policeManAni = new PoliceAnim(
					TranslateAnimation.RELATIVE_TO_PARENT, 0.0f,
					TranslateAnimation.RELATIVE_TO_PARENT, 0.7f,
					TranslateAnimation.RELATIVE_TO_PARENT, 0.0f,
					TranslateAnimation.RELATIVE_TO_PARENT, 0.0f
					);
			policeManAni.setDuration(180000);
			policeManAni.setRepeatCount(0);
			policeMan.setAnimation(policeManAni);
			
			startTimeCount(startTime);
		}
	}
	
	private void getTestWords()
	{
		SQLiteDatabase db = mHelper.getWritableDatabase();

		try {
			
			// It should be 66 words 11 sets
			Log.d("stage number", Integer.toString(totalStage));
			Cursor otherCursor = db.rawQuery("SELECT DISTINCT name, mean FROM dic WHERE " +
					"xo=\'X\' AND stage>" + (totalStage-10) + " AND stage <" + totalStage + " ORDER BY RANDOM() LIMIT 36", null);

			if (otherCursor.getCount()>0) {
				while(otherCursor.moveToNext()) {
					englishWords.add(otherCursor.getString(0));
					englishMeans.add(otherCursor.getString(1));
				}
			}
			

			
			if (englishWords.size() < 36) {
				Cursor otherCursor2 = db.rawQuery("SELECT DISTINCT name, mean FROM dic WHERE " +
						"xo=\'O\' AND stage>" + (totalStage-10) + " AND stage <" + totalStage + 
						" ORDER BY RANDOM() LIMIT " + (36 - englishWords.size()), null);
				if (otherCursor2.getCount()>0) {
					while(otherCursor2.moveToNext()) {
						englishWords.add(otherCursor2.getString(0));
						englishMeans.add(otherCursor2.getString(1));
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		

		
		Log.d("Array Size: ----- ", Integer.toString(englishWords.size()));
		
		
		//flipDb.rawQuery("DELETE FROM flip IF EXISTS", null);
		try {
			db.execSQL("CREATE TABLE flip ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
					"name TEXT, mean TEXT, xo TEXT);");
			db.delete("flip", null, null);
		} catch (Exception e) {
			
		}
		


		for (int i=0; i<englishWords.size(); i++) {
			
			try {
				// Save to Flip DB
				ContentValues cv = new ContentValues();
				cv.put("name", englishWords.get(i));
				cv.put("mean", englishMeans.get(i));
				cv.put("xo", "X");
				db.insert("flip", null, cv);
			} catch (Exception e) {
				e.printStackTrace();
			}

			
			// Randomize
			if (i<6) {
				randomArrange1.add(englishWords.get(i));
				randomArrange1.add(englishMeans.get(i));
			} else if (i<12) {
				randomArrange2.add(englishWords.get(i));
				randomArrange2.add(englishMeans.get(i));
			} else if (i<18) {
				randomArrange3.add(englishWords.get(i));
				randomArrange3.add(englishMeans.get(i));
			} else if (i<24) {
				randomArrange4.add(englishWords.get(i));
				randomArrange4.add(englishMeans.get(i));
			} else if (i<30) {
				randomArrange5.add(englishWords.get(i));
				randomArrange5.add(englishMeans.get(i));
			} else if (i<36) {
				randomArrange6.add(englishWords.get(i));
				randomArrange6.add(englishMeans.get(i));
			}

		}
		
		long seed = System.nanoTime();
		Collections.shuffle(randomArrange1, new Random(seed));
		 seed = System.nanoTime();
		Collections.shuffle(randomArrange2, new Random(seed));
		 seed = System.nanoTime();
		Collections.shuffle(randomArrange3, new Random(seed));
		 seed = System.nanoTime();
		Collections.shuffle(randomArrange4, new Random(seed));
		 seed = System.nanoTime();
		Collections.shuffle(randomArrange5, new Random(seed));
		 seed = System.nanoTime();
		Collections.shuffle(randomArrange6, new Random(seed));
		
	}
	
	class BtnFlipListener implements OnClickListener { 
		int cardId;
		public void onClick(View v)
		{
			cardId = v.getId();
			
			
			animation = AnimationUtils.loadAnimation(StudyTestC.this, R.drawable.studytestc_drawable_flip_card_back_scale); 
			animation.setAnimationListener(new Animation.AnimationListener() { 
				@Override 
				public void onAnimationStart(Animation animation) { 
				} 
				@Override 
				public void onAnimationRepeat(Animation animation) { 
				} 
				@Override 
				public void onAnimationEnd(Animation animation) { 
					
					if (testSetCount == 0) {
						tmpArray = randomArrange1;
					} else if (testSetCount == 1) {
						tmpArray = randomArrange2;
					} else if (testSetCount == 2) {
						tmpArray = randomArrange3;
					} else if (testSetCount == 3) {
						tmpArray = randomArrange4;
					} else if (testSetCount == 4) {
						tmpArray = randomArrange5;
					} else if (testSetCount == 5) {
						tmpArray = randomArrange6;
					}
					
					switch(cardId) {
						case R.id.study_testc_card_1_1:
							setCardFlip(card1_1,tmpArray.get(0));
							break;
						case R.id.study_testc_card_1_2:
							setCardFlip(card1_2, tmpArray.get(1));
							break;
						case R.id.study_testc_card_1_3:
							setCardFlip(card1_3, tmpArray.get(2));
							break;
						case R.id.study_testc_card_1_4:
							setCardFlip(card1_4, tmpArray.get(3));
							break;
						case R.id.study_testc_card_2_1:
							setCardFlip(card2_1, tmpArray.get(4));
							break;
						case R.id.study_testc_card_2_2:
							setCardFlip(card2_2, tmpArray.get(5));
							break;
						case R.id.study_testc_card_2_3:
							setCardFlip(card2_3, tmpArray.get(6));
							break;
						case R.id.study_testc_card_2_4:
							setCardFlip(card2_4, tmpArray.get(7)); 
							break;
						case R.id.study_testc_card_3_1:
							setCardFlip(card3_1, tmpArray.get(8)); 
							break;
						case R.id.study_testc_card_3_2:
							setCardFlip(card3_2, tmpArray.get(9)); 
							break;
						case R.id.study_testc_card_3_3:
							setCardFlip(card3_3, tmpArray.get(10)); 
							break;
						case R.id.study_testc_card_3_4:
							setCardFlip(card3_4, tmpArray.get(11)); 
							break;
					}
					
					if(cardCheck ==2) {
						Handler mFrontToBackHandler = new Handler();
						mFrontToBackHandler.postDelayed(checkCardText, 1000);
					}
				} 
			});


			if(cardCheck<2) {
				if((Integer)v.getTag() == TAG_BACK) {
					if ((select1 == null) || (select1.getId() != v.getId())) {
						setCardOnTouchFlip((Button)v);
					} 
				}
			}
		} 
		
		public void setCardOnTouchFlip(Button card) 
		{
			if(select1==null){
				select1 = card;
			}else{
				select2 = card;
			}
			cardCheck++;
			card.startAnimation(animation);
		}
		
		private Runnable checkCardText = new Runnable() {
			public void run() {
				int index1 = englishWords.indexOf(select1.getText().toString());
				int index2 = englishMeans.indexOf(select2.getText().toString());
				int index3 = englishMeans.indexOf(select1.getText().toString());
				int index4 = englishWords.indexOf(select2.getText().toString());
				if( (index1 >= 0 && index2 >= 0 && index1==index2) || (index3>=0 && index4>=0 && index3==index4)) {
					select1.setVisibility(View.INVISIBLE);
					select2.setVisibility(View.INVISIBLE);
					
					select1.setTag(TAG_FRONT);
					select2.setTag(TAG_FRONT);
					setCardFlip(select1,"");
					setCardFlip(select2,"");
					
					cardCount -= 2;
					
					if (cardCount == 0) {
						resetCards();
					}
					
					try {
						SQLiteDatabase db = mHelper.getWritableDatabase();

						if (index1 >= 0 && index2 >= 0 && index1==index2) {
							ContentValues cv = new ContentValues();
							cv.put("xo", "O");
							db.update("flip", cv, "name='"+ englishWords.get(index1) +"'", null);
						} else if (index3>=0 && index4>=0 && index3==index4) {
							ContentValues cv = new ContentValues();
							cv.put("xo", "O");
							db.update("flip", cv, "name='"+ englishWords.get(index4) +"'", null);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

					
					finalAnswerForRequest++;
					
				} else {
					select1.setTag(TAG_FRONT);
					select2.setTag(TAG_FRONT);
					setCardFlip(select1,"");
					setCardFlip(select2,"");	
				}
				
				cardCheck = 0;
				select1.setTag(TAG_BACK);
				select2.setTag(TAG_BACK);
				select1=null;
				select2=null;
		    }
		};
	} 
	
	public void resetCards()
	{
		testSetCount++;
		ImageView setNumView = (ImageView)findViewById(R.id.study_test_c_id_set_number);
		
		if (testSetCount <= 6) {
			String imageName = "test_18_image_number6_"+(testSetCount+1);
			int resId = getResources().getIdentifier(imageName , "drawable", getPackageName());
			setNumView.setImageResource(resId);
		}
		
		if (testSetCount == 6) {
			SharedPreferences levelPref = getSharedPreferences("StudyLevelInfo",0);
			SharedPreferences.Editor editor = levelPref.edit();
			editor.putString("testResult", Integer.toString(finalAnswerForRequest));
			editor.commit();
			
			Intent intent = new Intent(getApplicationContext(), StudyTestFinish.class);
			startActivity(intent);
			finish();
		} else {
			
			card1_1.setVisibility(View.VISIBLE);
			card1_2.setVisibility(View.VISIBLE);
			card1_3.setVisibility(View.VISIBLE);
			card1_4.setVisibility(View.VISIBLE);
			card2_1.setVisibility(View.VISIBLE);
			card2_2.setVisibility(View.VISIBLE);
			card2_3.setVisibility(View.VISIBLE);
			card2_4.setVisibility(View.VISIBLE);
			card3_1.setVisibility(View.VISIBLE);
			card3_2.setVisibility(View.VISIBLE);
			card3_3.setVisibility(View.VISIBLE);
			card3_4.setVisibility(View.VISIBLE);
			
			cardCount = 12;
		}
	}
	
	public void setCardFlip(Button card ,String cardText) {
		if((Integer)card.getTag() == TAG_FRONT){ 
			card.setBackgroundResource(R.drawable.test_18_image_card_back); 
			card.setText(cardText);
		}else if ((Integer)card.getTag() == TAG_BACK){ 
			card.setBackgroundResource(R.drawable.test_18_image_whitecard_front); 
			card.setText(cardText);
		} 
		card.startAnimation(AnimationUtils.loadAnimation(StudyTestC.this, R.drawable.studytestc_drawable_flip_card_front_scale));
	}
	
	
	///////////// Button Callbacks From XML
	public void moveToNextBtnCB(View v) 
	{
		resetCards();
	}
	
	///////////////////////////////////////////////////
	class ProgressTask extends AsyncTask<Integer, Integer, Void>{

		@Override
		protected void onPreExecute() {
			// initialize the progress bar
			// set maximum progress to 100.
			progress.setMax(1800);

		}

		@Override
		protected void onCancelled() {
			// stop the progress
			progress.setMax(0);

		}

		@Override
		protected Void doInBackground(Integer... params) {
			// get the initial starting value
			int start=params[0];
			// increment the progress
			for(int i=start;i<=1800;i++){
				try {
					boolean cancelled=isCancelled();
					//if async task is not cancelled, update the progress
					if(!cancelled){
						publishProgress(i);
						SystemClock.sleep(100);

					}

				} catch (Exception e) {
					Log.e("Error", e.toString());
				}

			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// increment progress bar by progress value
			progress.setProgress(values[0]);

		}

		@Override
		protected void onPostExecute(Void result) {
			// async task finished
			Log.v("Progress", "Finished");
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
		getMenuInflater().inflate(R.menu.study_test_c, menu);
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
			
			db.execSQL("CREATE TABLE flip ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
		"name TEXT, mean TEXT, xo TEXT);");
		}
		
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS dic");
			db.execSQL("DROP TABLE IF EXISTS flip");

			onCreate(db);
		}
	}
}
