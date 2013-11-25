package com.todpop.saltyenglish;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;

public class ReminderService extends IntentService {
    public ReminderService(){
        super("ReminderService");
    }

    @SuppressLint("NewApi")
	@Override
      protected void onHandleIntent(Intent intent) {
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        long when = System.currentTimeMillis();         // notification time
        
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
}
