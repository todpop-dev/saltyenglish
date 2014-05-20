package com.todpop.saltyenglish;

import java.util.Calendar;
import java.util.Random;

import com.todpop.api.LockScreenReceiver;
import com.todpop.api.request.GetLockScreen;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

public class LockScreenService extends Service{
	BroadcastReceiver mReceiver;
	//GetLockScreen getLock;
	
	private AlarmManager alarmMgr;
	private PendingIntent alarmIntent;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	@Override
	public void onCreate(){
		super.onCreate();
		
		//new GetLockScreen(this).execute();

		mReceiver = new LockScreenReceiver();
		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		registerReceiver(mReceiver, filter);
		
		//set alarm to get lock screen contents once an hour
		alarmMgr = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(this, LockGetService.class);
		alarmIntent = PendingIntent.getService(this, 0, intent, 0);
		
		//set alarm start time
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		
		alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000 * 60 * 60, alarmIntent);
	}

	@SuppressLint("NewApi")
	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		super.onStartCommand(intent, flags, startId);
		
		if(Build.VERSION.SDK_INT >= 18){
	        Intent notificationIntent = new Intent(this, MainActivity.class);
	        PendingIntent pIntent = PendingIntent.getActivity(this, 0, notificationIntent , 0);
	        
			Notification notification;
			notification  = new Notification.Builder(getApplicationContext())
	        .setContentTitle(getResources().getString(R.string.app_name))
	        .setContentText(getResources().getString(R.string.locker_notification_running))
	        .setSmallIcon(R.drawable.icon)
	        .setContentIntent(pIntent).build();
			startForeground(1, notification);		
		}
		else{
			startForeground(1, new Notification());			
		}
		
		if(intent != null){
			if(intent.getAction() == null){
				if(mReceiver == null){
					mReceiver = new LockScreenReceiver();
					IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
					registerReceiver(mReceiver, filter);
				}
			}
		}
		return START_REDELIVER_INTENT;
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		
		if(mReceiver != null){
			unregisterReceiver(mReceiver);
		}
	}
}