package com.todpop.saltyenglish;

import java.util.ArrayList;
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
import com.todpop.api.LoadingDialog;
import com.todpop.api.TypefaceActivity;
import com.todpop.saltyenglish.db.WordDBHelper;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.AnimationDrawable;


public class StudyTestWeekly extends TypefaceActivity {

	View redBack;
	
	TextView bestScore;
	
	TextView english;
	
	//answer Buttons
	Button option1;
	Button option2;
	Button option3;
	Button option4;
	
	ImageView timeImg;
	Animation timeAni;
	
	ProgressBar circularProgress; 
	
	ImageView correctImg;
	ImageView incorrectImg;
	
	ImageView comboImg;
	Animation comboAni;
	
	TotalTimer timer;
	ProgressTimer progressTimer;
	
	private static final long TIME = 60000;
	private long timeLeft = 0;
	
	private ArrayList<Word> wordList;
	
	private static int qTotal;
	private int qCount = -1;
	
	private int comboCount = -1;
	
	private String comboList = "0";
	private String lastHigh;
	
	private static Runnable resetWord;
	private static Runnable redTime;
	private static Runnable goNext;
	
	RelativeLayout tutorial;
	
	LinearLayout timesUp;
	ImageView timesUpImg;
	Animation timesUpAni;
	
	LoadingDialog loadingDialog;
	
	WordDBHelper mHelper;
	SQLiteDatabase db;
	
	private class Word{
		public Word(String word, String mean, String incorrect1, String incorrect2, String incorrect3){
			this.word = word;
			this.mean = mean;
			this.incorrect1 = incorrect1;
			this.incorrect2 = incorrect2;
			this.incorrect3 = incorrect3;
		}
		public String getWord(){
			return word;
		}
		public String getMean(){
			return mean;
		}
		public String getIncorrect1(){
			return incorrect1;
		}
		public String getIncorrect2(){
			return incorrect2;
		}
		public String getIncorrect3(){
			return incorrect3;
		}
		
		private String word;
		private String mean;
		private String incorrect1;
		private String incorrect2;
		private String incorrect3;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_study_test_weekly);

		FlurryAgent.logEvent("Weekly Test");
		
		redBack = (View)findViewById(R.id.study_testweekly_id_red);
		
		bestScore = (TextView)findViewById(R.id.study_testweekly_id_text_best);
		
		english = (TextView)findViewById(R.id.study_testweekly_id_english);
		
		option1 = (Button)findViewById(R.id.study_testweekly_id_option1);
		option2 = (Button)findViewById(R.id.study_testweekly_id_option2);
		option3 = (Button)findViewById(R.id.study_testweekly_id_option3);
		option4 = (Button)findViewById(R.id.study_testweekly_id_option4);
		
		option1.setOnClickListener(new ButtonListener());
		option2.setOnClickListener(new ButtonListener());
		option3.setOnClickListener(new ButtonListener());
		option4.setOnClickListener(new ButtonListener());
		
		timeImg = (ImageView)findViewById(R.id.study_testweekly_id_img_time);
		timeAni = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.weekly_pop_time_ani);
		timeImg.setAnimation(timeAni);
		
		circularProgress = (ProgressBar)findViewById(R.id.study_testweekly_id_progress);
		circularProgress.setMax((int)TIME/10);
		
		correctImg = (ImageView)findViewById(R.id.study_testweekly_id_o);
		incorrectImg = (ImageView)findViewById(R.id.study_testweekly_id_x);
		
		comboImg = (ImageView)findViewById(R.id.study_testweekly_id_combo);
		comboAni = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.weekly_pop_combo_ani);
		comboImg.setAnimation(comboAni);
		
		tutorial = (RelativeLayout)findViewById(R.id.study_testweekly_id_tuto);
		
		timesUp = (LinearLayout)findViewById(R.id.study_testweekly_id_timeup);
		timesUpImg = (ImageView)findViewById(R.id.study_testweekly_id_img_timeup);
		timesUpAni = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.weekly_pop_timesup_ani);
		timesUpImg.setAnimation(timesUpAni);
		
		wordList = new ArrayList<Word>();
		
		loadingDialog = new LoadingDialog(this);
		loadingDialog.show();
		
		mHelper = new WordDBHelper(this);
		db = mHelper.getWritableDatabase();
		db.delete("mywordtest", null, null);
		
		resetWord = new Runnable(){
			@Override
			public void run() {		
				correctImg.setVisibility(View.INVISIBLE);
				incorrectImg.setVisibility(View.INVISIBLE);
				setWord();
			}
		};
		redTime = new Runnable(){
			@Override
			public void run() {		
				redBack.setVisibility(View.INVISIBLE);
			}
		};
		goNext = new Runnable(){
			@Override
			public void run() {		
				goNextActivity();
			}
		};

		SharedPreferences pref = getSharedPreferences("rgInfo",0);
		String userId = pref.getString("mem_id", "0");
		
		new GetWord().execute("http://www.todpop.co.kr/api/studies/weekly_challenge.json?user_id=" + userId);
	}
	@Override
	public void onResume(){
		super.onResume();
		if(timeLeft != 0){
			timer = new TotalTimer(timeLeft, 1000);
			progressTimer = new ProgressTimer(timeLeft, 10);
			timer.start();
			progressTimer.start();
		}
		else{
			timer = new TotalTimer(TIME, 1000);
			progressTimer = new ProgressTimer(TIME, 10);
		}

	}
	@Override
	public void onPause(){
		super.onPause();
		timer.cancel();
		progressTimer.cancel();
	}
	private void setWord(){
		qCount++;
		if(qTotal > qCount){
			english.setText(wordList.get(qCount).getWord());
			
			int ran = new Random().nextInt(4);
			
			switch(ran){
			case 0:
				option1.setText(wordList.get(qCount).getMean());
				option2.setText(wordList.get(qCount).getIncorrect1());
				option3.setText(wordList.get(qCount).getIncorrect2());
				option4.setText(wordList.get(qCount).getIncorrect3());
				option1.setTag(true);
				option2.setTag(false);
				option3.setTag(false);
				option4.setTag(false);
				break;
			case 1:
				option1.setText(wordList.get(qCount).getIncorrect1());
				option2.setText(wordList.get(qCount).getMean());
				option3.setText(wordList.get(qCount).getIncorrect2());
				option4.setText(wordList.get(qCount).getIncorrect3());
				option1.setTag(false);
				option2.setTag(true);
				option3.setTag(false);
				option4.setTag(false);
				break;
			case 2:
				option1.setText(wordList.get(qCount).getIncorrect1());
				option2.setText(wordList.get(qCount).getIncorrect2());
				option3.setText(wordList.get(qCount).getMean());
				option4.setText(wordList.get(qCount).getIncorrect3());
				option1.setTag(false);
				option2.setTag(false);
				option3.setTag(true);
				option4.setTag(false);
				break;
			case 3:
				option1.setText(wordList.get(qCount).getIncorrect1());
				option2.setText(wordList.get(qCount).getIncorrect2());
				option3.setText(wordList.get(qCount).getIncorrect3());
				option4.setText(wordList.get(qCount).getMean());
				option1.setTag(false);
				option2.setTag(false);
				option3.setTag(false);
				option4.setTag(true);
				break;
			}
			option1.setClickable(true);
			option2.setClickable(true);
			option3.setClickable(true);
			option4.setClickable(true);
		}
		else{	//finish test
			goNextActivity();
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
				loadingDialog.dissmiss();
				
				if(json.getBoolean("status")==true) {
					JSONArray testArray;
					testArray = json.getJSONArray("test");
					
					qTotal = testArray.length();
					
					String word;
					String mean;
					String incorrect1;
					String incorrect2;
					String incorrect3;
					
					for(int i=0; i < qTotal; i++) {
						word = testArray.getJSONObject(i).getString("word");
						mean = testArray.getJSONObject(i).getString("mean");
						incorrect1 = testArray.getJSONObject(i).getString("incorrect1");
						incorrect2 = testArray.getJSONObject(i).getString("incorrect2");
						incorrect3 = testArray.getJSONObject(i).getString("incorrect3");
						wordList.add(new Word(word, mean, incorrect1, incorrect2, incorrect3));
					}
					
					lastHigh = json.getString("high_score");
					bestScore.setText(lastHigh);
				}
			}
			catch(Exception e){
				
			}
		}
	}
	
	public void onClickTuto(View v){
		tutorial.setVisibility(View.GONE);
		setWord();
		timer.start();
		progressTimer.start();
	}
	
	private class ButtonListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			disableAndStop();
			if((Boolean)v.getTag()){ //correct
				comboCount++;
				correctImg.setVisibility(View.VISIBLE);
				try{
					ContentValues row = new ContentValues();
					row.put("name", wordList.get(qCount).getWord());
					row.put("mean", wordList.get(qCount).getMean());
					row.put("xo", "O");
					db.insert("mywordtest", null, row);
				}
				catch(Exception e){
					e.printStackTrace();
				}
				new Handler().postDelayed(resetWord, 500);
				if(comboCount == 10){ //10combo
					comboImg.setImageResource(R.drawable.weekly_1_img_10combo);
					comboAni.start();
				}
				else if(comboCount == 20){	//20combo
					comboImg.setImageResource(R.drawable.weekly_1_img_20combo);
					comboAni.start();
				}
				else if(comboCount == 30){  //30combo
					comboImg.setImageResource(R.drawable.weekly_1_img_30combo);
					comboAni.start();
				}
				else if(comboCount > 0){
					comboImg.setImageResource(R.drawable.weekly_1_img_combo);
					comboAni.start();
				}
			}
			else{
				incorrect();
			}
		}
		
	}
	private void incorrect(){
		disableAndStop();
		if(comboCount > 0){
			comboList = comboList + "-" + comboCount;
		}
		comboCount = -1;
		incorrectImg.setVisibility(View.VISIBLE);
		try{
			ContentValues row = new ContentValues();
			row.put("name", wordList.get(qCount).getWord());
			row.put("mean", wordList.get(qCount).getMean());
			row.put("xo", "X");
			db.insert("mywordtest", null, row);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		new Handler().postDelayed(resetWord, 500);
	}
	private void goNextActivity(){
		if(comboCount > 0){
			comboList = comboList + "-" + comboCount;
		}
		Intent intent = new Intent(getApplicationContext(), StudyTestWeeklyResult.class);
		intent.putExtra("combo", comboList);
		intent.putExtra("lastHigh", lastHigh);
		startActivity(intent);
		finish();
	}
	
	private void disableAndStop(){
		option1.setClickable(false);
		option2.setClickable(false);
		option3.setClickable(false);
		option4.setClickable(false);
	}
	
	private class TotalTimer extends CountDownTimer{

		public TotalTimer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {	//time up
			//time's up effect and end activity
			timesUp.setVisibility(View.VISIBLE);
			timesUpAni.start();
			new Handler().postDelayed(goNext, 2500);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			timeLeft = millisUntilFinished;
			if(millisUntilFinished < 6000){	
				redBack.setVisibility(View.VISIBLE);
				new Handler().postDelayed(redTime, 500);
				
				if(millisUntilFinished < 2000){
					timeImg.setImageResource(R.drawable.weekly_1_text_time_1);
					timeAni.start();
				}else if(millisUntilFinished < 3000){
					timeImg.setImageResource(R.drawable.weekly_1_text_time_2);
					timeAni.start();
				}else if(millisUntilFinished < 4000){
					timeImg.setImageResource(R.drawable.weekly_1_text_time_3);
					timeAni.start();
				}else if(millisUntilFinished < 5000){
					timeImg.setImageResource(R.drawable.weekly_1_text_time_4);
					timeAni.start();
				}else if(millisUntilFinished < 6000){
					timeImg.setVisibility(View.VISIBLE);
					timeAni.start();
				}
			}
		}
		
	}
	private class ProgressTimer extends CountDownTimer{

		public ProgressTimer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
		}

		@Override
		public void onTick(long millisUntilFinished) {
			circularProgress.setProgress((int) ((TIME - millisUntilFinished) / 10));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}
	
	@Override
	public void onBackPressed(){
		finish();
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		mHelper.close();
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
