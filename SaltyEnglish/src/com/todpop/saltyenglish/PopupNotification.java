package com.todpop.saltyenglish;

import java.util.ArrayList;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;
import com.todpop.api.TypefaceActivity;
import com.todpop.saltyenglish.StudyTestResult.MyItem;
import com.todpop.saltyenglish.db.WordDBHelper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Vibrator;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;



public class PopupNotification extends TypefaceActivity {
	ViewPager wordNote;
	
	
	ImageView crocodile;
	ImageView chicken;
	
	SeekBar chick;
	
	MyItem mi;
	ArrayList<MyItem> arItem;
	int tmpStageAccumulated;
	
	SharedPreferences studyInfo;
	
 	// Database
 	WordDBHelper mHelper;
 	
	Vibrator vibe;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_popup_notification);
		
		wordNote = (ViewPager)findViewById(R.id.popup_notification_id_viewpager);
		
		crocodile = (ImageView)findViewById(R.id.popup_notification_id_crocodile);
		chicken = (ImageView)findViewById(R.id.popup_notification_id_chicken);
		
		chick = (SeekBar)findViewById(R.id.popup_notification_id_seekbar);
		
		vibe = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
		
		chick.setOnSeekBarChangeListener(new SeekBarOnSeekBarChangeListener());
		
		studyInfo = getSharedPreferences("studyInfo",0);
		mHelper = new WordDBHelper(this);
		arItem = new ArrayList<MyItem>();
		tmpStageAccumulated = studyInfo.getInt("tmpStageAccumulated", 1);
		getTestWords();
	}
	
	private void getTestWords()
	{	
		try {
			SQLiteDatabase db = mHelper.getReadableDatabase();

			if (tmpStageAccumulated%10 == 0) {
				tmpStageAccumulated--;
			}
			Cursor cursor = db.rawQuery("SELECT name, mean, xo FROM dic WHERE stage=" + tmpStageAccumulated + ";", null);

			Log.e("cursor.getCount()", "cursor.getCount() : "+cursor.getCount());
			if (cursor.getCount() > 0) {
				while(cursor.moveToNext()) {
					Log.e("A B C ------", cursor.getString(0) + "  " + cursor.getString(1) + "   " + cursor.getString(2));
					mi = new MyItem(cursor.getString(0), cursor.getString(1), cursor.getString(2));
					arItem.add(mi);
				}
			}
		} catch (Exception e) {
			Log.e("AFDSDFDSFSDFDSF", "catch error");
			e.printStackTrace();
		}
	}
	
	private final class SeekBarOnSeekBarChangeListener implements OnSeekBarChangeListener
	{
		boolean sideFlag = false;
	  @Override
	  public void onStopTrackingTouch(SeekBar seekBar)
	  {
	      Log.i("SeekBar", "onStopTrackingTouch");
	      int current = seekBar.getProgress();
	      if(current == 100){
	    	  startApp();
	      }
	      else if(current == 0){
	    	  closePopup();
	      }
	      else{
		      crocodile.setImageResource(R.drawable.store_53_image_crocodile_normal);
		      chicken.setImageResource(R.drawable.store_53_image_chicken_normal);
	    	  seekBar.setProgress(50);
	      }
	  }
	  
	  @Override
	  public void onStartTrackingTouch(SeekBar seekBar)
	  {
	      Log.i("SeekBar", "onStartTrackingTouch");
	      crocodile.setImageResource(R.drawable.store_53_image_crocodile_pressed);
	      chicken.setImageResource(R.drawable.store_53_image_chicken_pressed);
	  }
	  
	  @Override
	  public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
	  {
	      Log.i("SeekBar", "progress : " + progress + " / " + fromUser);
	      if (fromUser)
	      {
	          if(progress >= 85){
	        	  if(sideFlag == false){
	        		  vibe.vibrate(50);
		        	  sideFlag = true;
	        	  }
	        	  seekBar.setProgress(100);
	        	  sideFlag = true;
	          }
	          else if(progress <= 15){
	        	  if(sideFlag == false){
	        		  vibe.vibrate(50);
		        	  sideFlag = true;
	        	  }
	        	  seekBar.setProgress(0);
	          }
	          else{
	        	  sideFlag = false;
	          }
	      }       
	      else{
	    	  return;
	      }
	  }
	}	
	class MyItem{
		MyItem(String aEn,String aKr,String Check)
		{
			en = aEn;
			kr = aKr;
			check =Check;
		}
		String en;
		String kr;
		String check;
	}
	private void startApp(){
		Intent intent = new Intent(getApplicationContext(), MainActivity.class);
		startActivity(intent);
		finish();
	}
	
	private void closePopup(){
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
