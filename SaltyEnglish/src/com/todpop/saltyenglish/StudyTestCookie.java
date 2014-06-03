package com.todpop.saltyenglish;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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
import android.widget.TextView;

import com.todpop.saltyenglish.db.WordDBHelper;

public class StudyTestCookie extends Activity {

	ImageView ivLeftArm;
	ImageView ivRightArm;
	Animation animLeftArm;
	Animation animRightArm;
	Animation animLeftArmBack;
	Animation animRightArmBack;

	static int cntTotalCookie = 0;
	static int cntCorrectCookie = 0;
	static int cntWrongCookie = 0;

	private Animation animHookLeft;
	private Animation animHookRight;
	private LinearLayout llTestCookies;
	private AnimationListener animHookListener;

	CookieFactory cookieFactory;
	private Button btnLeft;
	private Button btnRight;
	private TextView tvNumber;
	private WordDBHelper mHelper;
	private int tmpStageAccumulated;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_study_test_cookie);

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

		initAnims();
		initCookies();

	}

	private String[] getWordInfo() {
		String str_correct_name = null;
		String str_correct_mean = null;
		String str_wrong_mean = null;
		try {
			SQLiteDatabase db = mHelper.getReadableDatabase();
			Cursor cursor = db.rawQuery("SELECT name,  mean FROM dic WHERE stage=" + tmpStageAccumulated + " order by random() limit 1;", null);

			if (cursor.getCount()>0) {
				cursor.moveToNext();
				Cursor otherCursor = db.rawQuery("SELECT DISTINCT mean FROM dic WHERE mean <> '" + cursor.getString(1) + "' ORDER BY RANDOM() LIMIT 3", null);
				otherCursor.moveToNext();
				str_correct_name = cursor.getString(0);
				str_correct_mean = cursor.getString(1);
				str_wrong_mean = otherCursor.getString(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
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
