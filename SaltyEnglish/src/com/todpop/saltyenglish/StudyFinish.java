package com.todpop.saltyenglish;

import java.net.URL;

import com.flurry.android.FlurryAgent;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class StudyFinish extends Activity {

	// popup view
	PopupWindow popupWindow;
	View popupview;
	RelativeLayout relative;
	TextView popupText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_study_finish);
		//popupview
		relative = (RelativeLayout)findViewById(R.id.studyfinish_id_main_activity);;
		popupview = View.inflate(this, R.layout.popup_view, null);
		float density = getResources().getDisplayMetrics().density;
		popupWindow = new PopupWindow(popupview,(int)(300*density),(int)(180*density),true);
		popupText = (TextView)popupview.findViewById(R.id.popup_id_text);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.study_finish, menu);
		return true;
	}

	public void showPopView(View v)
	{

		popupText.setText(R.string.study_finish_popup_text);
		popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
		popupWindow.showAsDropDown(null);
	}

	public void closePopup(View v)
	{
		popupWindow.dismiss();
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
