package com.todpop.saltyenglish;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;



public class PopupNotification extends Activity {
	ImageView crocodile;
	ImageView chicken;
	
	SeekBar chick;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_popup_notification);
		
		crocodile = (ImageView)findViewById(R.id.popup_notification_id_crocodile);
		chicken = (ImageView)findViewById(R.id.popup_notification_id_chicken);
		
		chick = (SeekBar)findViewById(R.id.popup_notification_id_seekbar);
		
	}

	public void startApp(View view){
		Intent intent = new Intent(getApplicationContext(), MainActivity.class);
		startActivity(intent);
		finish();
	}
	
	public void closePopup(View view){
		finish();
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		finish();
	}
	
	@SuppressLint("NewApi")
	@Override
	protected void onStart()
	{
		super.onStart();
		FlurryAgent.onStartSession(this, "ZKWGFP6HKJ33Y69SP5QY");
	    EasyTracker.getInstance(this).activityStart(this);
	    
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);

        boolean isScreenOn = pm.isScreenOn();
        if(isScreenOn==false)
        {

        	WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK |PowerManager.ACQUIRE_CAUSES_WAKEUP |PowerManager.ON_AFTER_RELEASE,"MyLock");
            wl.acquire(20000);
            
            WakeLock wl_cpu = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"MyCpuLock");
            wl_cpu.acquire(20000);
        }
        
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, notificationIntent , 0);
        
		Notification n;
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			n  = new Notification.Builder(getApplicationContext())
	        .setContentTitle(getResources().getString(R.string.notificationTitle))
	        .setContentText(getResources().getString(R.string.notificationSubject))
	        .setSmallIcon(R.drawable.icon)
	        .setContentIntent(pIntent).build();
		} else {
			n  = new Notification.Builder(getApplicationContext())
	        .setContentTitle(getResources().getString(R.string.notificationTitle))
	        .setContentText(getResources().getString(R.string.notificationSubject))
	        .setSmallIcon(R.drawable.icon)
	        .setContentIntent(pIntent).getNotification();
		}
        
        n.defaults |= Notification.DEFAULT_SOUND;
        n.flags |= Notification.FLAG_AUTO_CANCEL;
        
        nm.notify(0, n);
	}
	 
	@Override
	protected void onStop()
	{
		super.onStop();		
		FlurryAgent.onEndSession(this);
	    EasyTracker.getInstance(this).activityStop(this);
	}
}
