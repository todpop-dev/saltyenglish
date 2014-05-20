package com.todpop.api;

import com.todpop.saltyenglish.LockScreenService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver{

	SharedPreferences setting;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
			setting = context.getSharedPreferences("setting", 0);
			
			if(setting.getBoolean("lockerEnabled", true)){
				Intent i = new Intent(context, LockScreenService.class);
				context.startService(i);
			}
		}
	}
}
