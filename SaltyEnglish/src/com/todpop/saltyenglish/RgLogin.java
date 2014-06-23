package com.todpop.saltyenglish;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;
import com.todpop.api.TypefaceActivity;

public class RgLogin extends TypefaceActivity {

	// email
	EditText email;
	EditText emailPassword;
	Button loginBtn;

	// declare define popup view
	PopupWindow popupWindow;
	View popupview;
	RelativeLayout relative;
	TextView popupText;


	SharedPreferences rgInfo;
	SharedPreferences.Editor rgInfoEdit;
	SharedPreferences setting;
	SharedPreferences.Editor settingEdit;
	SharedPreferences studyInfo;
	SharedPreferences.Editor studyInfoEdit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		rgInfo = getSharedPreferences("rgInfo", 0);
		rgInfoEdit = rgInfo.edit();
		setting = getSharedPreferences("setting", 0);
		settingEdit = setting.edit();
		studyInfo = getSharedPreferences("studyInfo", 0);
		studyInfoEdit = studyInfo.edit();

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rg_login);

		email = (EditText) findViewById(R.id.rglogin_id_email);
		emailPassword = (EditText) findViewById(R.id.rglogin_id_emailpassword);
		loginBtn = (Button) findViewById(R.id.rglogin_id_loginbtn);

		// popupview
		relative = (RelativeLayout) findViewById(R.id.rglogin_id_main_activity);

		popupview = View.inflate(this, R.layout.popup_view, null);
		popupWindow = new PopupWindow(popupview,
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT, true);
		popupText = (TextView) popupview.findViewById(R.id.popup_id_text);

		setFont(popupText);

		// loading dialog

	}

	public void emailLogin(View view){ // email login button click
		
		rgInfoEdit.putString("nickname", email.getText().toString());
		rgInfoEdit.apply();
		
		settingEdit.putString("isLogin", "YES");
		settingEdit.apply();
		
		String stageInfo = "";
		for(int i=0;i<15;i++){
			stageInfo+="Y";
			for(int j=0;j<9;j++)
				stageInfo+="x";
		}
		studyInfoEdit.putString("stageInfo", stageInfo);
		studyInfoEdit.apply();
		
		Intent intent = new Intent(getApplicationContext(), RgRegisterTutorial.class);
		startActivity(intent);
		finish();
	}

	public void onClickBack(View view) {
		finish();
	}

	public void closePopup(View v) {
		popupWindow.dismiss();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(this, "ZKWGFP6HKJ33Y69SP5QY");
		FlurryAgent.logEvent("Login");
		EasyTracker.getInstance(this).activityStart(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
		EasyTracker.getInstance(this).activityStop(this);
	}
}
