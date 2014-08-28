package com.todpop.saltyenglish;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;
import com.todpop.saltyenglish.db.WordDBHelper;
public class StudyTestCookie extends Activity {
	public static final int TIME_PER_TICK = 1000;
	public static final int TIME_CNT = 40;

	String strComboResult="";

	int cntTotalEmergedCookie = 0; // total emerged cookies,, correctAnswer + wrongAnswer != emergedCookie
	int cntCorrectCookie = 0; 
	int cntWrongCookie = 0;
	int timeRemain = 0;
	int widthToTick; // timebar width to add each tick
	int tmpStageAccumulated;
	int cntNonstopCorrect = 0;
	boolean isFirstOnCreated = false; // chk onCreated

	TimebarCountdownTimer timebarCounter;
	CookieFactory cookieFactory;

	WordDBHelper mHelper;
	HashMap<String, String[]> hashWords; // key = word , value =  { mean, wrong mean}

	Animation animLeftArm;
	Animation animRightArm;
	Animation animLeftArmBack;
	Animation animRightArmBack;
	Animation animCntdown;
	Animation animTimesup;
	Animation animCombo;
	Animation animSuperCombo;

	Animation animHookLeft;
	Animation animHookRight;
	AnimationListener animHookListener;

	LinearLayout llTestCookies; 
	LinearLayout llTimebarNums;
	RelativeLayout rlTimebar;
	RelativeLayout rlPauseView;
	RelativeLayout rlTopView;

	ImageView ivLeftArm;
	ImageView ivRightArm;
	ImageView ivTimebarFrontNum;
	ImageView ivTimebarBackNum;
	ImageView ivCombo;
	ImageView ivSuperCombo;
	ImageView ivWrongAnswer;
	ImageView ivCntdown;
	ImageView ivTimesup;

	ImageView ivBlackLightbox;
	ImageView ivRedLightBoxTop;
	ImageView ivRedLightBoxBot;

	ImageButton btnLeft;
	ImageButton btnRight;
	TextView txtLeft;
	TextView txtRight;

	TextView tvNumber;

	RelativeLayout rlTutorial;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_study_test_cookie);

		timeRemain = TIME_CNT * TIME_PER_TICK; // init timeRemain
		timebarCounter = new TimebarCountdownTimer((TIME_CNT+2)*TIME_PER_TICK,TIME_PER_TICK);

		mHelper = new WordDBHelper(this);
		SharedPreferences studyInfo = getSharedPreferences("studyInfo", 0);
		tmpStageAccumulated = studyInfo.getInt("tmpStageAccumulated", 1);

		cookieFactory = CookieFactory.getInstance();
		llTestCookies =(LinearLayout)findViewById(R.id.ll_test_cookies);
		rlTutorial = (RelativeLayout)findViewById(R.id.rl_test_cookie_tutorial);

		ivTimebarFrontNum = (ImageView)findViewById(R.id.iv_test_cookie_timebar_front_num);
		ivTimebarBackNum = (ImageView)findViewById(R.id.iv_test_cookie_timebar_back_num);
		ivCombo=(ImageView) findViewById(R.id.iv_test_cookie_combo);
		ivSuperCombo=(ImageView) findViewById(R.id.iv_test_cookie_super_combo);
		ivWrongAnswer=(ImageView) findViewById(R.id.iv_test_cookie_wrong);
		ivCntdown=(ImageView) findViewById(R.id.iv_test_cookie_countdown);
		ivTimesup=(ImageView) findViewById(R.id.iv_test_cookie_timesup);

		ivBlackLightbox=(ImageView) findViewById(R.id.iv_test_cookie_whole_black_box);

		ivRedLightBoxTop = (ImageView) findViewById(R.id.iv_test_cookie_top_redbox);
		ivRedLightBoxBot = (ImageView) findViewById(R.id.iv_test_cookie_bot_redbox);

		ivLeftArm = (ImageView)findViewById(R.id.iv_test_cookie_left_arm);
		ivRightArm = (ImageView)findViewById(R.id.iv_test_cookie_right_arm);
		btnLeft = (ImageButton)findViewById(R.id.btn_test_cookie_left_btn);
		btnRight = (ImageButton)findViewById(R.id.btn_test_cookie_right_btn);
		txtLeft = (TextView)findViewById(R.id.btn_test_cookie_left_txt);
		txtRight = (TextView)findViewById(R.id.btn_test_cookie_right_txt);
		tvNumber = (TextView)findViewById(R.id.tv_test_cookie_number);

		rlTimebar = (RelativeLayout)findViewById(R.id.rl_test_cookie_timebar);
		rlPauseView = (RelativeLayout)findViewById(R.id.rl_test_cookie_pause_view);
		rlTopView = (RelativeLayout)findViewById(R.id.rl_test_cookie_top);
		llTimebarNums = (LinearLayout)findViewById(R.id.ll_test_cookie_timebar_nums);

		initWords();
		initAnims();
		initCookies();
		
	}

	void initWords(){
		if(!isFirstOnCreated)
			initWordXoByN();
		hashWords = new HashMap<String, String[]>();
		SQLiteDatabase db = mHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT DISTINCT name,  mean, xo FROM dic WHERE stage=" + tmpStageAccumulated + " order by random()", null);
		if (cursor.getCount()>0) {
			while(cursor.moveToNext()){
				Log.e("INIT WORDS",cursor.getString(0)+" / "+cursor.getString(1) +" / "+cursor.getString(2));
				// get wrong mean randomly except correct mean  
				Cursor otherCursor = db.rawQuery("SELECT DISTINCT mean FROM dic WHERE mean <> '" + cursor.getString(1) + "' ORDER BY RANDOM() LIMIT 1", null);
				otherCursor.moveToNext();
				hashWords.put(cursor.getString(0), new String[]{cursor.getString(1),otherCursor.getString(0)});
			}
		}
	}

	private void initWordXoByN() {
		// N is Not Solved Answer
		SQLiteDatabase db = mHelper.getWritableDatabase();
		db.execSQL("UPDATE dic SET xo='N'");
	}

	private String[] getWordInfo() {
		String str_correct_name = null, str_correct_mean = null, str_wrong_mean = null;
		if(hashWords.isEmpty() || hashWords.size() == 0) // if there are no words in hashmap, init words
			initWords();
		ArrayList<String> keyList = new ArrayList<String>(hashWords.keySet());
		str_correct_name = keyList.get(0);
		String[] means = hashWords.get(str_correct_name);
		str_correct_mean = means[0];
		str_wrong_mean = means[1];
		hashWords.remove(keyList.get(0));
		return new String[]{str_correct_name,str_correct_mean,str_wrong_mean};
	}

	private void initCookies() {
		// in first, cookie init 
		for(int i=0;i<4;i++) setCookie();
	}

	private void setCookie() {
		String[] infos = getWordInfo();
		View v = cookieFactory.getCookie(getApplicationContext(),infos[0] ,cntTotalEmergedCookie % 3);
		v.setTag(infos); // each cookies has word, mean, wrong mean by string array
		llTestCookies.addView(v,0);

		TextView targetCookie = (TextView) llTestCookies.getChildAt(llTestCookies.getChildCount()-1); // bottom cookie
		String[] targetInfos = (String[]) targetCookie.getTag();
		int numRand = (int)(Math.random()*2)+1; // set Text in Left or Right Btn
		txtLeft.setText(targetInfos[numRand]);
		numRand = numRand == 1 ? 2 : 1; // reverse, 1 to 2 , 2 to 1
		txtRight.setText(targetInfos[numRand]);
		cntTotalEmergedCookie++;
	}

	private void initAnims() {
		animLeftArm = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_test_cookie_left_arm);
		animRightArm = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_test_cookie_right_arm); 

		animLeftArmBack = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_test_cookie_left_arm_back);
		animRightArmBack = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_test_cookie_right_arm_back);

		animHookLeft = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_test_cookie_hooked_left);
		animHookRight = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_test_cookie_hooked_right);

		animCombo = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.weekly_pop_combo_ani);
		animSuperCombo = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.weekly_pop_combo_ani);
		animCntdown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.weekly_pop_time_ani);
		animTimesup = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.weekly_pop_timesup_ani);

		animHookListener = new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				llTestCookies.removeView(llTestCookies.getChildAt(llTestCookies.getChildCount()-1));
				setCookie();
				btnLeft.setClickable(true);
				btnRight.setClickable(true);
			}
		};

		animLeftArm.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) { 
				ivLeftArm.startAnimation(animLeftArmBack);
				llTestCookies.getChildAt(llTestCookies.getChildCount()-1).startAnimation(animHookLeft);
			}
		});

		animRightArm.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				ivRightArm.startAnimation(animRightArmBack);
				llTestCookies.getChildAt(llTestCookies.getChildCount()-1).startAnimation(animHookRight);
			}
		});

		animCombo.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				ivCombo.setVisibility(View.VISIBLE);	
			}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				ivCombo.setVisibility(View.GONE);				
			}
		});

		animSuperCombo.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				ivSuperCombo.setVisibility(View.VISIBLE);
				rlTopView.setBackgroundResource(R.drawable.test_cookie_bg_super_top);
			}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				ivSuperCombo.setVisibility(View.GONE);
			}
		});

		animCntdown.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				ivCntdown.setVisibility(View.GONE);
			}
		});

		animTimesup.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				btnLeft.setClickable(false);
				btnRight.setClickable(false);

				ivBlackLightbox.setVisibility(View.VISIBLE);
				ivTimesup.setVisibility(View.VISIBLE);

				if(strComboResult.equals(""))
					strComboResult = "0";
				else if(cntNonstopCorrect != 0)
					strComboResult += "-" + (cntNonstopCorrect-1);

				SharedPreferences levelPref = getSharedPreferences("StudyLevelInfo",0);
				Editor editor = levelPref.edit();
				editor.putString("testComboResult", strComboResult);
				editor.putInt("testCntCorrect", cntCorrectCookie);
				editor.putInt("testCntWrong", cntWrongCookie);
				editor.apply();
			}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						Intent intent = new Intent(getApplicationContext(),StudyTestCookieFinish.class);
						startActivity(intent);
						finish();
					}
				}, 500);
			}
		});

		animHookLeft.setAnimationListener(animHookListener);
		animHookRight.setAnimationListener(animHookListener);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.study_test_cookie, menu);
		return true;
	}

	// direction -1 = left , 1 = right
	public boolean chkIsCorrectAnswer(int direction){
		TextView target = (TextView) llTestCookies.getChildAt(llTestCookies.getChildCount()-1);
		String[] infos = (String[]) target.getTag();
		String correctAnswer = infos[1];
		if(direction == -1) return txtLeft.getText().toString().equals(correctAnswer);
		else if(direction == 1) return txtRight.getText().toString().equals(correctAnswer);
		else return false;
	}

	public void updateCorrectOrWrong(String XorO,String mean){
		SQLiteDatabase db = mHelper.getWritableDatabase();
		Cursor cursor = db.rawQuery("SELECT xo FROM dic WHERE mean='"+mean+"'", null);
		while(cursor.moveToNext()){
			String xo = cursor.getString(0);
			if(xo.equals("N") || xo.equals("O")) // N is First inited
				db.execSQL("UPDATE dic SET xo='"+XorO+"' WHERE mean='"+mean+"'");
//			else if(xo.equals("N")) 
		}
		
	}

	public void correctAnswer(View v){
		cntCorrectCookie++;
		cntNonstopCorrect++;

		if(cntNonstopCorrect>=11) showSuperCombo();
		else if(cntNonstopCorrect>=2) showCombo();
		tvNumber.setText(cntCorrectCookie+"");
		updateCorrectOrWrong("O",((TextView)v).getText().toString());
	}

	private void showCombo() {
		ivCombo.setImageResource(R.drawable.test_cookie_img_combo1+cntNonstopCorrect-2);
		ivCombo.startAnimation(animCombo);
	}

	private void showSuperCombo() {
		ivSuperCombo.startAnimation(animSuperCombo);
	}

	public void wrongAnswer(View v){
		cntWrongCookie++;
		strComboResult += (strComboResult.length() != 0 ? "-" : "") + (cntNonstopCorrect == 0 ? 0 : cntNonstopCorrect - 1);
		cntNonstopCorrect = 0;
		rlTopView.setBackgroundResource(R.drawable.test_cookie_bg_top);
		showWrongAnswer();
		updateCorrectOrWrong("X",((TextView)v).getText().toString());
	}

	private void showWrongAnswer() {
		ivWrongAnswer.setVisibility(View.VISIBLE);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				ivWrongAnswer.setVisibility(View.GONE);
			}
		}, 500);
	}

	public void onLeftBtnClick(View v) {	
		if(chkIsCorrectAnswer(-1)) correctAnswer(txtLeft);
		else wrongAnswer(txtLeft);

		ivLeftArm.startAnimation(animLeftArm);
		btnLeft.setClickable(false);
		btnRight.setClickable(false);
	}

	public void onRightBtnClick(View v) {	
		if(chkIsCorrectAnswer(1)) correctAnswer(txtRight);
		else wrongAnswer(txtRight);

		ivRightArm.startAnimation(animRightArm);
		btnLeft.setClickable(false);
		btnRight.setClickable(false);
	}

	// cookie factory , factory pattern
	private static class CookieFactory{
		private static CookieFactory obj;
		private CookieFactory(){}

		public static CookieFactory getInstance(){
			if(obj==null) obj = new CookieFactory();
			return obj;
		}

		public View getCookie(Context con, String word ,int color){
			TextView v = (TextView) LayoutInflater.from(con).inflate(R.layout.textview_study_test_cookie, null);
			LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.setMargins(0, 30, 0, 0);
			params.weight = 1;
			v.setBackgroundResource(R.drawable.test_cookie_img_biscuit_1 + color);
			v.setText(word);
			v.setLayoutParams(params);
			return v;
		}
	}

	// for tutorial
	public void onClickTuto(View v){
		rlTutorial.setVisibility(View.GONE);
		timebarCounter.start();
	}

	// for pause case
	@Override
	public void onBackPressed() {
		timebarCounter.cancel();
		onResume();
	}

	public void pauseViewContinueTest(View v){
		timebarCounter.start();
		rlPauseView.setVisibility(View.GONE);
	}

	public void pauseViewFinishTest(View v) {
		Intent intent = new Intent(getApplicationContext(), StudyHome.class);
		startActivity(intent);
		finish();
	}

	@Override
	protected void onPause() {
		super.onPause();
		timebarCounter.cancel();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(isFirstOnCreated)
			rlPauseView.setVisibility(View.VISIBLE);
		isFirstOnCreated = true;
	}

	// for flury 
	@Override
	protected void onStart(){
		super.onStart();
		FlurryAgent.onStartSession(this, "ZKWGFP6HKJ33Y69SP5QY");
		EasyTracker.getInstance(this).activityStart(this);
	}

	@Override
	protected void onStop(){
		super.onStop();		
		FlurryAgent.onEndSession(this);
		EasyTracker.getInstance(this).activityStop(this);
	}

	// for timerbar
	private class TimebarCountdownTimer extends CountDownTimer{

		public TimebarCountdownTimer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			if(timeRemain == 0) ivTimesup.startAnimation(animTimesup); // end timebar remain
			else if(timeRemain == (TIME_CNT*TIME_PER_TICK) ) widthToTick = getTimebarTickWidth(); // first timebar width init
			else if(timeRemain<=5000 && timeRemain!=0) showCntdown();
			llTimebarNums.getLayoutParams().width += widthToTick; // update timebar width
			updateTimebarNums(); // update timebar numbs
			timeRemain -= TIME_PER_TICK; // decrease timebar Remain
		}

		private void showCntdown() {
			int cntdown= timeRemain/1000;
			ivCntdown.setImageResource(R.drawable.test_cookie_text_time_1+cntdown-1);
			ivCntdown.setVisibility(View.VISIBLE);

			ivCntdown.startAnimation(animCntdown);

			ivRedLightBoxTop.setVisibility(View.VISIBLE);
			ivRedLightBoxBot.setVisibility(View.VISIBLE);	

			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					ivRedLightBoxTop.setVisibility(View.GONE);
					ivRedLightBoxBot.setVisibility(View.GONE);
				}
			}, 500);
		}

		private void updateTimebarNums() {
			if( timeRemain >= (TIME_PER_TICK*10) ) {
				int frontNumIndex =  timeRemain/10000;
				int backNumIndex =  (timeRemain - (frontNumIndex*TIME_PER_TICK*10) ) / TIME_PER_TICK;

				ivTimebarFrontNum.setImageResource(R.drawable.test_cookie_img_time_0 + frontNumIndex);
				ivTimebarBackNum.setImageResource(R.drawable.test_cookie_img_time_0 + backNumIndex);
			}
			else{
				if(timeRemain == TIME_PER_TICK*9) ivTimebarBackNum.setVisibility(View.GONE);
				int backNumIndex =  timeRemain/1000;
				ivTimebarFrontNum.setImageResource(R.drawable.test_cookie_img_time_0 + backNumIndex);
			}
		}

		private int getTimebarTickWidth() {
			DisplayMetrics metrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(metrics);
			int widthLlTimebarNums = llTimebarNums.getLayoutParams().width;
			return ((metrics.widthPixels - widthLlTimebarNums) / TIME_CNT) - 1;
		}

		@Override
		public void onFinish() {}
	}

}
