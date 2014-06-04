package com.todpop.saltyenglish;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.util.Log;
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

import com.todpop.saltyenglish.db.WordDBHelper;

public class StudyTestCookie extends Activity {
	public static final int TIME_CNT_TICK = 1000;
	public static final int TIME_CNT_END = 40000;


	CookieFactory cookieFactory;

	ImageView ivLeftArm;
	ImageView ivRightArm;
	Animation animLeftArm;
	Animation animRightArm;
	Animation animLeftArmBack;
	Animation animRightArmBack;

	int cntTotalCookie = 0;
	int cntCorrectCookie = 0;
	int cntWrongCookie = 0;
	int timeRemain = 0;

	int screenWidth;

	int tmpStageAccumulated;

	Animation animHookLeft;
	Animation animHookRight;
	LinearLayout llTestCookies;
	AnimationListener animHookListener;

	Button btnLeft;
	Button btnRight;
	TextView tvNumber;
	WordDBHelper mHelper;
	HashMap<String, String[]> hashWords;
	TextView tvTimebar;
	RelativeLayout rlTimebar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_study_test_cookie);

		timeRemain = TIME_CNT_END;

		mHelper = new WordDBHelper(this);
		SharedPreferences studyInfo = getSharedPreferences("studyInfo", 0);
		tmpStageAccumulated = studyInfo.getInt("tmpStageAccumulated", 1);

		cookieFactory = CookieFactory.getInstance();
		llTestCookies =(LinearLayout)findViewById(R.id.ll_test_cookies);

		ivLeftArm = (ImageView)findViewById(R.id.iv_test_cookie_left_arm);
		ivRightArm = (ImageView)findViewById(R.id.iv_test_cookie_right_arm);
		btnLeft = (Button)findViewById(R.id.btn_test_cookie_left);
		btnRight = (Button)findViewById(R.id.btn_test_cookie_right);
		tvNumber = (TextView)findViewById(R.id.tv_test_cookie_number);

		tvTimebar= (TextView)findViewById(R.id.tv_test_cookie_timebar);
		rlTimebar = (RelativeLayout)findViewById(R.id.rl_test_cookie_timebar);

		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		screenWidth = metrics.widthPixels;

		initWords();
		initAnims();
		initCookies();

		//		tvTimebar.setWidth(screenWidth);

		new CountDownTimer(TIME_CNT_END,TIME_CNT_TICK) {

			@Override
			public void onTick(long millisUntilFinished) {
				int numberCount = timeRemain / 1000;
				tvTimebar.setWidth(screenWidth / numberCount);
				timeRemain -= 1000;
				Log.e("Tick",timeRemain+" / ("+tvTimebar.getWidth()+"/"+screenWidth+")");
			}

			@Override
			public void onFinish() {

			}
		}.start();

	}
	void initWords()
	{
		hashWords = new HashMap<String, String[]>();
		SQLiteDatabase db = mHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT name,  mean FROM dic WHERE stage=" + tmpStageAccumulated + " order by random()", null);
		if (cursor.getCount()>0) {
			while(cursor.moveToNext())
			{
				Cursor otherCursor = db.rawQuery("SELECT DISTINCT mean FROM dic WHERE mean <> '" + cursor.getString(1) + "' ORDER BY RANDOM() LIMIT 1", null);
				otherCursor.moveToNext();
				hashWords.put(cursor.getString(0), new String[]{cursor.getString(1),otherCursor.getString(0)});
			}
		}
	}


	private String[] getWordInfo() {
		String str_correct_name = null;
		String str_correct_mean = null;
		String str_wrong_mean = null;
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
				// IMPOOOOOOOOOORTANT METHOOOOOOOOD
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

	public boolean chkIsCorrectAnswer(int direction)
	{
		TextView target = (TextView) llTestCookies.getChildAt(llTestCookies.getChildCount()-1);
		String word = target.getText().toString();
		String[] infos = (String[]) target.getTag();
		String correctAnswer = infos[1];
		if(direction == -1) return btnLeft.getText().toString().equals(correctAnswer);
		else if(direction == 1) return btnRight.getText().toString().equals(correctAnswer);
		else return false;
	}

	public void correctAnswer()
	{
		cntCorrectCookie++;
		tvNumber.setText(cntCorrectCookie+"");
	}

	public void wrongAnswer()
	{
		cntWrongCookie++;
	}

	public void onLeftBtnClick(View v) 
	{	
		if(chkIsCorrectAnswer(-1)) correctAnswer();
		else wrongAnswer();

		ivLeftArm.startAnimation(animLeftArm);
		btnLeft.setClickable(false);
		btnRight.setClickable(false);
	}

	public void onRightBtnClick(View v) 
	{	
		if(chkIsCorrectAnswer(1)) correctAnswer();
		else wrongAnswer();

		ivRightArm.startAnimation(animRightArm);
		btnLeft.setClickable(false);
		btnRight.setClickable(false);
	}

	private static class CookieFactory
	{
		private static CookieFactory obj;
		private CookieFactory(){}

		public static CookieFactory getInstance()
		{
			if(obj==null) obj = new CookieFactory();
			return obj;
		}

		public View getCookie(Context con, String word ,int color)
		{
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
