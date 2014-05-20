package com.todpop.saltyenglish;

import java.util.Calendar;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;
import com.todpop.api.request.SendLockState;
import com.todpop.saltyenglish.LockScreenService;

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
	private static final int ENABLED = 1; 
	private static final int DISABLED = 0; 
	
	Button setAlarm;
	private int hour;
	private int minute;
	
	static int targetHour;
	static int targetMinute;
	
	CheckBox alarmCheckBox;
	CheckBox popupCheckBox;
	CheckBox lockerCheckBox;
	
	NotificationManager notificationManager;
	AlarmManager alarmManager;
	
	SharedPreferences stdInfo;
	SharedPreferences.Editor stdInfoEdit;
	
	SharedPreferences setting;
	SharedPreferences.Editor settingEdit;
	

	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_more_setting);
		
		setAlarm = (Button)findViewById(R.id.home_more_setting_btn_alarm);

        stdInfo = getSharedPreferences("studyInfo",0);
        stdInfoEdit = stdInfo.edit();
        
        setting = getSharedPreferences("setting", 0);
        settingEdit = setting.edit();
        
		/*final Calendar c = Calendar.getInstance();*/
		
		hour = stdInfo.getInt("alarmHour", 9);
		minute = stdInfo.getInt("alarmMinute", 0);
		targetHour = hour;
		targetMinute = minute;

		notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

		setAlarm.setText("      "+pad(hour)+"         "+pad(minute));
		
		alarmCheckBox = (CheckBox)findViewById(R.id.home_more_setting_id_alarm_box);
		popupCheckBox = (CheckBox)findViewById(R.id.home_more_setting_id_popup_box);
		lockerCheckBox = (CheckBox)findViewById(R.id.home_more_setting_id_locker_box);
		
		if(stdInfo.getBoolean("alarm", false)){
			alarmCheckBox.setChecked(true);
			popupCheckBox.setEnabled(true);
			if(stdInfo.getBoolean("popupAlarm", false)){
				popupCheckBox.setChecked(true);
			}
		}
		else{
			stdInfoEdit.putBoolean("popupAlarm", false);
			stdInfoEdit.apply();
			popupCheckBox.setChecked(false);
			popupCheckBox.setEnabled(false);
			alarmCheckBox.setChecked(false);
		}

		if(setting.getBoolean("lockerEnabled", true)){
			lockerCheckBox.setChecked(true);
		}
		
		alarmCheckBox.setOnCheckedChangeListener(
			new CompoundButton.OnCheckedChangeListener() {

				@SuppressLint("NewApi")
				@Override
				public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) 
				{
					if (isChecked == true) {
						popupCheckBox.setChecked(true);
						popupCheckBox.setEnabled(true);
						stdInfoEdit.putBoolean("popupAlarm", true);
						stdInfoEdit.putBoolean("alarm", true);
						stdInfoEdit.apply();
						setAlarm();
					} else {
						popupCheckBox.setChecked(false);
						popupCheckBox.setEnabled(false);
						stdInfoEdit.putBoolean("popupAlarm", false);
						stdInfoEdit.putBoolean("alarm", false);
						stdInfoEdit.apply();
						cancelAlarm();
					}
				}
		});
		
		popupCheckBox.setOnCheckedChangeListener(
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
			});
		
		lockerCheckBox.setOnCheckedChangeListener(
				new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if(isChecked){
							settingEdit.putBoolean("lockerEnabled", true);
							new SendLockState(HomeMoreSetting.this).execute(ENABLED);
							Intent i = new Intent(HomeMoreSetting.this, LockScreenService.class);
							startService(i);
						}
						else{
							settingEdit.putBoolean("lockerEnabled", false);
							new SendLockState(HomeMoreSetting.this).execute(DISABLED);
							Intent i = new Intent(HomeMoreSetting.this, LockScreenService.class);
							stopService(i);							
						}
						settingEdit.apply();
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
		
		if(popupCheckBox.isChecked()){
			alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, when, AlarmManager.INTERVAL_DAY, pendingIntentActivity);
		}
		else{
			alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, when, AlarmManager.INTERVAL_DAY, pendingIntentService);		
		}
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
