package com.todpop.saltyenglish;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;

public class RgLoginAndRegister extends Activity {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rg_login_and_register);
	}
	
	
	//----button onClick----
	public void showRgRegisterActivity(View view)
	{
		Log.d("RgLoginAndRegister","1");
		
		Intent intent = new Intent(getApplicationContext(), RgRegister.class);
		startActivity(intent);
	}
	
	public void showRgLoginActivity(View view)
	{
		Log.d("RgLoginAndRegister","2");
		
		Intent intent = new Intent(getApplicationContext(), RgLogin.class);
		startActivity(intent);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) 
		{
			final AlertDialog.Builder isExit = new AlertDialog.Builder(this);

			DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() 
			{
				public void onClick(DialogInterface dialog, int which) 
				{
					switch (which) 
					{
					case AlertDialog.BUTTON_POSITIVE:
						SharedPreferences settings = getSharedPreferences("setting", 0);
						SharedPreferences.Editor editor = settings.edit();
						editor.putString("check","YES");
						editor.commit();
						
						Intent intent = new Intent();
				        intent.setClass(RgLoginAndRegister.this, MainActivity.class);    
				        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
				         startActivity(intent);
						finish();				

						break;
					case AlertDialog.BUTTON_NEGATIVE:
						break;
					default:
						break;
					}
				}
			};

			isExit.setTitle(getResources().getString(R.string.register_alert_title));
			isExit.setMessage(getResources().getString(R.string.register_alert_text));
			isExit.setPositiveButton("OK", listener);
			isExit.setNegativeButton("Cancel", listener);
			isExit.show();

			return false;
		}
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.rg_login_and_register, menu);
		return true;
	}
	
	
	
}
