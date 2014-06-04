package com.todpop.saltyenglish;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.todpop.saltyenglish.db.WordDBHelper;

public class StudyTestCookie extends Activity {
	public static final int TIME_PER_TICK = 1000;
	public static final int TIME_CNT = 40;

	int cntTotalCookie = 0;
	int cntCorrectCookie = 0;
	int cntWrongCookie = 0;
	int timeRemain = 0;
	int widthToTick;
	int tmpStageAccumulated;

	CookieFactory cookieFactory;
	WordDBHelper mHelper;
	HashMap<String, String[]> hashWords;

	Animation animLeftArm;
	Animation animRightArm;
	Animation animLeftArmBack;
	Animation animRightArmBack;

	Animation animHookLeft;
	Animation animHookRight;
	AnimationListener animHookListener;

	LinearLayout llTestCookies;
	LinearLayout llTimebarNums;
	RelativeLayout rlTimebar;

	ImageView ivLeftArm;
	ImageView ivRightArm;
	ImageView ivTimebarFrontNum;
	ImageView ivTimebarBackNum;
	
	Button btnLeft;
	Button btnRight;
	
	TextView tvNumber;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_study_test_cookie);

		timeRemain = TIME_CNT * TIME_PER_TICK;

		mHelper = new WordDBHelper(this);
		SharedPreferences studyInfo = getSharedPreferences("studyInfo", 0);
		tmpStageAccumulated = studyInfo.getInt("tmpStageAccumulated", 1);

		cookieFactory = CookieFactory.getInstance();
		llTestCookies =(LinearLayout)findViewById(R.id.ll_test_cookies);

		ivTimebarFrontNum = (ImageView)findViewById(R.id.iv_test_cookie_timebar_front_num);
		ivTimebarBackNum = (ImageView)findViewById(R.id.iv_test_cookie_timebar_back_num);

		ivLeftArm = (ImageView)findViewById(R.id.iv_test_cookie_left_arm);
		ivRightArm = (ImageView)findViewById(R.id.iv_test_cookie_right_arm);
		btnLeft = (Button)findViewById(R.id.btn_test_cookie_left);
		btnRight = (Button)findViewById(R.id.btn_test_cookie_right);
		tvNumber = (TextView)findViewById(R.id.tv_test_cookie_number);

		rlTimebar = (RelativeLayout)findViewById(R.id.rl_test_cookie_timebar);
		llTimebarNums = (LinearLayout)findViewById(R.id.ll_test_cookie_timebar_nums);

		initWords();
		initAnims();
		initCookies();

		new CountDownTimer((TIME_CNT+2)*TIME_PER_TICK,TIME_PER_TICK) {

			@Override
			public void onTick(long millisUntilFinished) {
				if(timeRemain == (TIME_CNT*TIME_PER_TICK) ) widthToTick = getTimebarTickWidth();
				llTimebarNums.getLayoutParams().width += widthToTick;
				updateTimebarNums();
				timeRemain -= TIME_PER_TICK;
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
			public void onFinish() {
				Toast.makeText(getApplicationContext(), "Cookie end", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(getApplicationContext(),StudyTestFinish.class);
				startActivity(intent);
				finish();
			}
		}.start();

	}
	void initWords()
	{
		hashWords = new HashMap<String, String[]>();
		SQLiteDatabase db = mHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT name,  mean FROM dic WHERE stage=" + tmpStageAccumulated + " order by random()", null);
		if (cursor.getCount()>0) {
			while(cursor.moveToNext()){
				Cursor otherCursor = db.rawQuery("SELECT DISTINCT mean FROM dic WHERE mean <> '" + cursor.getString(1) + "' ORDER BY RANDOM() LIMIT 1", null);
				otherCursor.moveToNext();
				hashWords.put(cursor.getString(0), new String[]{cursor.getString(1),otherCursor.getString(0)});
			}
		}
	}

	private String[] getWordInfo() {
		String str_correct_name = null, str_correct_mean = null, str_wrong_mean = null;
		ArrayList<String> keyList = new ArrayList<String>(hashWords.keySet());
		int randNum = (int)(Math.random() * keyList.size());
		str_correct_name = keyList.get(randNum);
		String[] means = hashWords.get(str_correct_name);
		str_correct_mean = means[0];
		str_wrong_mean = means[1];

		return new String[]{str_correct_name,str_correct_mean,str_wrong_mean};
	}

	private void initCookies() {
		for(int i=0;i<4;i++) setCookie();
	}

	private void setCookie() {
		String[] infos = getWordInfo();
		View v = cookieFactory.getCookie(getApplicationContext(),infos[0] ,cntTotalCookie % 3);
		v.setTag(infos);
		llTestCookies.addView(v,0);

		TextView targetCookie = (TextView) llTestCookies.getChildAt(llTestCookies.getChildCount()-1);
		String[] targetInfos = (String[]) targetCookie.getTag();
		int numRand = (int)(Math.random()*2)+1;
		btnLeft.setText(targetInfos[numRand]);
		numRand = numRand == 1 ? 2 : 1; // reverse, 1 to 2 , 2 to 1
		btnRight.setText(targetInfos[numRand]);
		cntTotalCookie++;
	}

	private void initAnims() {
		animLeftArm = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_test_cookie_left_arm);
		animRightArm = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_test_cookie_right_arm); 

		animLeftArmBack = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_test_cookie_left_arm_back);
		animRightArmBack = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_test_cookie_right_arm_back);

		animHookLeft = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_test_cookie_hooked_left);
		animHookRight = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_test_cookie_hooked_right);

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

		animHookLeft.setAnimationListener(animHookListener);
		animHookRight.setAnimationListener(animHookListener);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.study_test_cookie, menu);
		return true;
	}

	public boolean chkIsCorrectAnswer(int direction){
		TextView target = (TextView) llTestCookies.getChildAt(llTestCookies.getChildCount()-1);
		String[] infos = (String[]) target.getTag();
		String correctAnswer = infos[1];
		if(direction == -1) return btnLeft.getText().toString().equals(correctAnswer);
		else if(direction == 1) return btnRight.getText().toString().equals(correctAnswer);
		else return false;
	}

	public void updateCorrectOrWrong(String XorO,String mean){
		SQLiteDatabase db = mHelper.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("xo", XorO);
		db.update("dic", cv, "mean='"+ mean + "'", null);
	}

	public void correctAnswer(View v){
		cntCorrectCookie++;
		tvNumber.setText(cntCorrectCookie+"");

		updateCorrectOrWrong("O",((Button)v).getText().toString());
	}

	public void wrongAnswer(View v){
		cntWrongCookie++;
		updateCorrectOrWrong("X",((Button)v).getText().toString());
	}

	public void onLeftBtnClick(View v) {	
		if(chkIsCorrectAnswer(-1)) correctAnswer(v);
		else wrongAnswer(v);

		ivLeftArm.startAnimation(animLeftArm);
		btnLeft.setClickable(false);
		btnRight.setClickable(false);
	}

	public void onRightBtnClick(View v) {	
		if(chkIsCorrectAnswer(1)) correctAnswer(v);
		else wrongAnswer(v);

		ivRightArm.startAnimation(animRightArm);
		btnLeft.setClickable(false);
		btnRight.setClickable(false);
	}

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
			v.setBackgroundResource(R.drawable.test_cookie_img_biscuit_1 + color);
			v.setText(word);
			v.setLayoutParams(params);
			return v;
		}
	}
}
