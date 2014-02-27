package com.todpop.saltyenglish;




import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

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
import com.google.analytics.tracking.android.EasyTracker;

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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnTouchListener;
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
	
	static int tmpStageAccumulated;
	static int testSetCount;
	static int cardCount;
	static boolean flipAll;
	int finalAnswerForRequest = 0;

	
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
		FlurryAgent.logEvent("Study Test");
		
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
		flipAll = false;
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
		
		SharedPreferences studyInfo = getSharedPreferences("studyInfo", 0);
		tmpStageAccumulated = studyInfo.getInt("tmpStageAccumulated", 1);
		Log.d("first totalStage ----", Integer.toString(tmpStageAccumulated));
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
			 
			Handler delayBeforeFlipAll = new Handler();
			delayBeforeFlipAll.postDelayed(flipAllCards, 500);
		 }	 
	}
	private Runnable flipAllCards = new Runnable() {
		public void run(){
			flipAll = true;

			card1_1.performClick();
			card1_2.performClick();
			card1_3.performClick();
			card1_4.performClick();
			card2_1.performClick();
			card2_2.performClick();
			card2_3.performClick();
			card2_4.performClick();
			card3_1.performClick();
			card3_2.performClick();
			card3_3.performClick();
			card3_4.performClick();
		
			flipAll = false;
		}
	};
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
				editor.apply();
				
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
			editor.apply();
			
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
			Log.d("stage number", Integer.toString(tmpStageAccumulated));
			Cursor otherCursor = db.rawQuery("SELECT DISTINCT name, mean FROM dic WHERE " +
					"xo=\'X\' AND stage>" + (tmpStageAccumulated-10) + " AND stage <" + tmpStageAccumulated + " ORDER BY RANDOM() LIMIT 36", null);

			if (otherCursor.getCount()>0) {
				while(otherCursor.moveToNext()) {
					englishWords.add(otherCursor.getString(0));
					englishMeans.add(otherCursor.getString(1));
				}
			}
			

			
			if (englishWords.size() < 36) {
				Cursor otherCursor2 = db.rawQuery("SELECT DISTINCT name, mean FROM dic WHERE " +
						"xo=\'O\' AND stage>" + (tmpStageAccumulated-10) + " AND stage <" + tmpStageAccumulated + 
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
		

		
		if (englishWords.size() < 36) {
			new GetWord().execute("http://todpop.co.kr/api/studies/get_level_words.json?level=" + (tmpStageAccumulated / 10) + "&stage=10");
		}
		else{
		
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
		
	}
	
	class BtnFlipListener implements OnClickListener { 
		int cardId;
		public void onClick(View v)
		{
			Log.i("STEVEN", "button clicked cardCheck = " +cardCheck);
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
					/*if(cardCheck ==2) {
						Handler mFrontToBackHandler = new Handler();
						mFrontToBackHandler.postDelayed(checkCardText, 1000);
					}*/
				} 
			});

			if(flipAll){
				v.startAnimation(animation);
				final Button tempView = (Button)findViewById(v.getId());
				Handler reFlipAllHandler = new Handler();
				reFlipAllHandler.postDelayed(new Runnable(){
					public void run(){
						tempView.setTag(TAG_FRONT);
						setCardFlip(tempView,"");
						tempView.setTag(TAG_BACK);
					}
				}, 3000);
			}
			else if(cardCheck<2) {
				
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
			if(cardCheck ==2) {
				Handler mFrontToBackHandler = new Handler();
				mFrontToBackHandler.postDelayed(checkCardText, 1500);
			}
		}
		
		private Runnable checkCardText = new Runnable() {
			public void run() {
				Log.e("STEVEN", "Just before log.i");
				Log.i("STEVEN", "select1 = " + select1.toString() + " select2 = " + select2.toString());
				Log.i("STEVEN", "select1 = " + select1.getText().toString() + " select2 = " + select2.getText().toString());
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

					if (cardCount == 0) {
						resetCards();
					}
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
		
		if (testSetCount < 6) {
			String imageName = "test_18_image_number6_"+(testSetCount+1);
			int resId = getResources().getIdentifier(imageName , "drawable", getPackageName());
			setNumView.setImageResource(resId);
		}
		
		if (testSetCount == 6) {
			SharedPreferences levelPref = getSharedPreferences("StudyLevelInfo",0);
			SharedPreferences.Editor editor = levelPref.edit();
			editor.putString("testResult", Integer.toString(finalAnswerForRequest));
			editor.apply();
			
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


			Handler delayBeforeFlipAll = new Handler();
			delayBeforeFlipAll.postDelayed(flipAllCards, 500);
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
					
					JSONArray spareWords = json.getJSONArray("spare");
					
					for(int i = 0; englishWords.size() < 36; i++) {
						englishWords.add(spareWords.getJSONObject(i).get("name").toString());
						englishMeans.add(spareWords.getJSONObject(i).get("mean").toString());
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
				} else {		        
				}

			} catch (Exception e) {
				Log.d("Exception: ", e.toString());
			}
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
	protected void onStop()
	{
		super.onStop();		
		FlurryAgent.onEndSession(this);
	    EasyTracker.getInstance(this).activityStop(this);
	}
}
