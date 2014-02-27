package com.todpop.saltyenglish;

import java.util.Calendar;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;

import android.os.Build;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class HomeMoreSetting extends Activity {
	
	Button setAlarm;
	private int hour;
	private int minute;
	
	static int targetHour;
	static int targetMinute;
	
	CheckBox alarmCheckBox;
	//CheckBox popupCheckBox;
	
	NotificationManager notificationManager;
	AlarmManager alarmManager;
	
	SharedPreferences stdInfo;
	SharedPreferences.Editor stdInfoEdit;
	
	
	

	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_more_setting);
		
		setAlarm = (Button)findViewById(R.id.home_more_setting_btn_alarm);

        stdInfo = getSharedPreferences("studyInfo",0);
        stdInfoEdit = stdInfo.edit();
        
		/*final Calendar c = Calendar.getInstance();*/
		
		hour = stdInfo.getInt("alarmHour", 9);
		minute = stdInfo.getInt("alarmMinute", 0);
		targetHour = hour;
		targetMinute = minute;

		notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

		setAlarm.setText("      "+pad(hour)+"         "+pad(minute));
		
		alarmCheckBox = (CheckBox)findViewById(R.id.home_more_setting_id_alarm_box);
		//popupCheckBox = (CheckBox)findViewById(R.id.home_more_setting_id_popup_box);
		
		if(stdInfo.getBoolean("alarm", false)){
			alarmCheckBox.setChecked(true);
			//popupCheckBox.setEnabled(true);
			if(stdInfo.getBoolean("popupAlarm", false)){
				//popupCheckBox.setChecked(true);
			}
		}
		else{
			stdInfoEdit.putBoolean("popupAlarm", false);
			stdInfoEdit.apply();
			//popupCheckBox.setChecked(false);
			//popupCheckBox.setEnabled(false);
			alarmCheckBox.setChecked(false);
		}
		
		alarmCheckBox.setOnCheckedChangeListener(
			new CompoundButton.OnCheckedChangeListener() {

				@SuppressLint("NewApi")
				@Override
				public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) 
				{
					if (isChecked == true) {
						//popupCheckBox.setChecked(true);
						//popupCheckBox.setEnabled(true);
						stdInfoEdit.putBoolean("popupAlarm", true);
						stdInfoEdit.putBoolean("alarm", true);
						stdInfoEdit.apply();
						setAlarm();
					} else {
						//popupCheckBox.setChecked(false);
						//popupCheckBox.setEnabled(false);
						stdInfoEdit.putBoolean("popupAlarm", false);
						stdInfoEdit.putBoolean("alarm", false);
						stdInfoEdit.apply();
						cancelAlarm();
					}
				}
		});
		
		/*popupCheckBox.setOnCheckedChangeListener(
				new CompoundButton.OnCheckedChangeListener() {

					@SuppressLint("NewApi")
					@Override
					public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) 
					{
						if (isChecked == true){
							stdInfoEdit.putBoolean("popupAlarm", true);
							stdInfoEdit.apply();
							setAlarm();
						}
						else{
							stdInfoEdit.putBoolean("popupAlarm", false);
							stdInfoEdit.apply();
							setAlarm();							
						}
					}
			});*/
		RadioGroup rb1 = (RadioGroup)findViewById(R.id.home_more_radioGrop);
        RadioButton rbBasic =(RadioButton)findViewById(R.id.home_more_radiobtn_basic);
        RadioButton rbMiddle =(RadioButton)findViewById(R.id.home_more_radiobtn_middle);
        RadioButton rbHigh =(RadioButton)findViewById(R.id.home_more_radiobtn_high);
        RadioButton rbToeic =(RadioButton)findViewById(R.id.home_more_radiobtn_toeic);

        switch(stdInfo.getInt("currentCategory", 1))
        {
        case 1:
        	rbBasic.setChecked(true);
        	break;
        case 2:
        	rbMiddle.setChecked(true);
        	break;
        case 3:
        	rbHigh.setChecked(true);
        	break;
        case 4:
        	rbToeic.setChecked(true);
        	break;
        }

        rb1.setOnCheckedChangeListener(new OnCheckedChangeListener() 
	    {
	        public void onCheckedChanged(RadioGroup group, int checkedId) {
	        	switch(checkedId)
        		{
        			case R.id.home_more_radiobtn_basic:
        				FlurryAgent.logEvent("Category set basic");
        				stdInfoEdit.putInt("currentCategory", 1);
        			break;
        			
        			case R.id.home_more_radiobtn_middle:
        				FlurryAgent.logEvent("Category set middle");
        				stdInfoEdit.putInt("currentCategory", 2);      
        			break;
        			
        			case R.id.home_more_radiobtn_high:
        				FlurryAgent.logEvent("Category set high");
        				stdInfoEdit.putInt("currentCategory", 3);
        			break;
        			
        			case R.id.home_more_radiobtn_toeic:
        				FlurryAgent.logEvent("Category set toiec");
        				stdInfoEdit.putInt("currentCategory", 4);
        			break;
        			
        			default:
        			break;
        		}
	        	stdInfoEdit.apply();
	        }
	    });

	}
		   

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home_more_setting, menu);
		return true;
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case 1:	
			return new TimePickerDialog(this, timePickerListener, hour, minute,true);
		}
		return null;
	}
	
	
	private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
		public void onTimeSet(TimePicker view, int selectedHour,
				int selectedMinute) {
			
			setAlarm.setText("      "+pad(selectedHour)+"         "+pad(selectedMinute));
			targetHour = selectedHour;
			targetMinute = selectedMinute;
			stdInfoEdit.putInt("alarmHour", selectedHour);
			stdInfoEdit.putInt("alarmMinute", selectedMinute);
			stdInfoEdit.apply();
			
			if(alarmCheckBox.isChecked())
				setAlarm();
		}
	};
	
	private void setAlarm(){		    
		Intent intentService = new Intent(getApplicationContext(), ReminderService.class);
		PendingIntent pendingIntentService = PendingIntent.getService(getApplicationContext(), 0, intentService, 0);
		
		Intent intentActivity = new Intent("com.todpop.saltyenglish.popupnotification");
		PendingIntent pendingIntentActivity = PendingIntent.getActivity(getApplicationContext(), 0, intentActivity, Intent.FLAG_ACTIVITY_NEW_TASK);
		
		//cancel all
		notificationManager.cancelAll();
		alarmManager.cancel(pendingIntentService);
		alarmManager.cancel(pendingIntentActivity);

		// Setup Alarm
		Calendar calendar =  Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, targetHour);
		calendar.set(Calendar.MINUTE, targetMinute);
		long when = calendar.getTimeInMillis();         // notification time
		
		/*if(popupCheckBox.isChecked())
			alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, when, AlarmManager.INTERVAL_DAY, pendingIntentActivity);*/
		
		//else
			alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, when, AlarmManager.INTERVAL_DAY, pendingIntentService);		
	}
	
	private void cancelAlarm(){
		Intent intentService = new Intent(getApplicationContext(), ReminderService.class);
		PendingIntent pendingIntentService = PendingIntent.getService(getApplicationContext(), 0, intentService, 0);
		
		Intent intentActivity = new Intent("com.todpop.saltyenglish.PopupNotification");
		PendingIntent pendingIntentActivity = PendingIntent.getActivity(getApplicationContext(), 0, intentActivity, Intent.FLAG_ACTIVITY_NEW_TASK);
		
		//cancel all
		notificationManager.cancelAll();
		alarmManager.cancel(pendingIntentService);
		alarmManager.cancel(pendingIntentActivity);
	}
	
	private static String pad(int c) {
		if (c >= 10)
			return String.valueOf(c);
		else
			return "0" + String.valueOf(c);
	}
	
	// on click

	public void onClickBack(View view)
	{
		finish();
	}
	
	public void setStudyAlarm(View view)
	{
		showDialog(1);
	}
	@Override
	protected void onStart()
	{
		super.onStart();
		FlurryAgent.onStartSession(this, "ZKWGFP6HKJ33Y69SP5QY");
		FlurryAgent.logEvent("Setting");
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
