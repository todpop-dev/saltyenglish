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
	
	NotificationManager notificationManager;
	
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
		

        
		Log.d("hour - ", Integer.toString(hour));
		Log.d("minute - ", Integer.toString(minute));
		
		setAlarm.setText("      "+pad(hour)+"         "+pad(minute));
		
		alarmCheckBox = (CheckBox)findViewById(R.id.home_more_setting_id_alarm_box);
		if(stdInfo.getBoolean("alarm", false)){
			alarmCheckBox.setChecked(true);
		}
		else{
			alarmCheckBox.setChecked(false);
		}
		alarmCheckBox.setOnCheckedChangeListener(
				new CompoundButton.OnCheckedChangeListener() {

					@SuppressLint("NewApi")
					@Override
					public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) 
					{
						notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
					    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
					    
					    Intent intent = new Intent(getApplicationContext(), ReminderService.class);
					    PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 0, intent, 0);

						if (isChecked == true) {
							stdInfoEdit.putBoolean("alarm", true);
							stdInfoEdit.commit();
							// Setup Alarm
						    Calendar calendar =  Calendar.getInstance();
						    calendar.set(Calendar.HOUR_OF_DAY, targetHour);
						    calendar.set(Calendar.MINUTE, targetMinute);
						    long when = calendar.getTimeInMillis();         // notification time

						    //alarmManager.set(AlarmManager.RTC, when, pendingIntent);
						    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, when, AlarmManager.INTERVAL_DAY, pendingIntent);
						} else {
							stdInfoEdit.putBoolean("alarm", false);
							stdInfoEdit.commit();
							notificationManager.cancelAll();
							alarmManager.cancel(pendingIntent);
						}
					}
				});
		
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
        				Log.d("!!!!!!!!!!!!!!","1111111111111111");
        			break;
        			
        			case R.id.home_more_radiobtn_middle:
        				FlurryAgent.logEvent("Category set middle");
        				stdInfoEdit.putInt("currentCategory", 2);        				
        				Log.d("222222222222222","2222222222222");
        			break;
        			
        			case R.id.home_more_radiobtn_high:
        				FlurryAgent.logEvent("Category set high");
        				stdInfoEdit.putInt("currentCategory", 3);
        				Log.d("222222222222222","2222222222222");
        			break;
        			
        			case R.id.home_more_radiobtn_toeic:
        				FlurryAgent.logEvent("Category set toiec");
        				stdInfoEdit.putInt("currentCategory", 4);
        				Log.d("222222222222222","2222222222222");
        			break;
        			
        			default:
        			break;
        		}
	        	stdInfoEdit.commit();
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
			stdInfoEdit.commit();
			
			if(alarmCheckBox.isChecked()){
				notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
			    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
			    
			    Intent intent = new Intent(getApplicationContext(), ReminderService.class);
			    PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 0, intent, 0);

				notificationManager.cancelAll();
				alarmManager.cancel(pendingIntent);
				// Setup Alarm
				Calendar calendar =  Calendar.getInstance();
				calendar.set(Calendar.HOUR_OF_DAY, targetHour);
				calendar.set(Calendar.MINUTE, targetMinute);
				long when = calendar.getTimeInMillis();         // notification time

				alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, when, AlarmManager.INTERVAL_DAY, pendingIntent);
			}
		}
	};
	
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
