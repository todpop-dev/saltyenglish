package com.todpop.api;

import com.todpop.saltyenglish.LockScreenActivity;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class LockScreenReceiver extends BroadcastReceiver{

	private KeyguardManager km = null;
	private KeyguardManager.KeyguardLock keyLock = null;
	private TelephonyManager teleManager = null;
	private boolean isPhoneIdle = true;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
			if(km == null)
				km = (KeyguardManager)context.getSystemService(Context.KEYGUARD_SERVICE);
			
			if(keyLock == null)
				keyLock = km.newKeyguardLock(Context.KEYGUARD_SERVICE);
			
			if(teleManager == null){
				teleManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
				teleManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
			}
			
			if(isPhoneIdle){
				disableKeyguard();
							
				//MainApplication mainApp = (MainApplication)context.getApplicationContext();
				
				//if(mainApp.getCurrentLockerCnt() == 0){
					Intent lockIntent = new Intent(context, LockScreenActivity.class);
					lockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(lockIntent);
				//}
			}
		}		
	}
	
	public void reenableKeyguard(){
		keyLock.reenableKeyguard();
	}
	
	public void disableKeyguard(){
		keyLock.disableKeyguard();
	}
	
	private PhoneStateListener phoneListener = new PhoneStateListener(){
		@Override
		public void onCallStateChanged(int state, String incomingNumber){
			switch(state){
			case TelephonyManager.CALL_STATE_IDLE:
				isPhoneIdle = true;
				break;
			case TelephonyManager.CALL_STATE_RINGING:
				isPhoneIdle = false;
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				isPhoneIdle = false;
				break;
			}
		}
	};
}
