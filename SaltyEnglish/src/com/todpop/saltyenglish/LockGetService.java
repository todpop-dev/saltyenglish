package com.todpop.saltyenglish;

import com.todpop.api.request.GetLockScreen;
import com.todpop.api.request.GetLockScreenMockTest;

import android.app.IntentService;
import android.content.Intent;

public class LockGetService extends IntentService{
	public LockGetService(){
		super("LockGetService");
	}
	@Override
	protected void onHandleIntent(Intent intent) {
		new GetLockScreen(this).execute();		
		new GetLockScreenMockTest(this).execute();		//junho
	}
}