package com.todpop.saltyenglish;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;

public class LvTestPause extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lv_test_pause);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.lv_test_pause, menu);
		return true;
	}
	
	public void continueTest(View v)
	{
		finish();
	}

}
