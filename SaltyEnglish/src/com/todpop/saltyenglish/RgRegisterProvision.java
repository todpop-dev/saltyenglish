package com.todpop.saltyenglish;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

public class RgRegisterProvision extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rg_register_provision);
		ImageView provisionImage = (ImageView)this.findViewById(R.id.provisionImage);
		TextView textViewField = (TextView)this.findViewById(R.id.textViewField);
		Intent intent = getIntent();
		int state = intent.getIntExtra("wButton", 0);
		
		if(state == 1){
			provisionImage.setImageResource(R.drawable.register_28_bgimg_agreement);
			textViewField.setText(R.string.userAgreement);
		}
		else{
			provisionImage.setImageResource(R.drawable.register_28_bgimg_personalinfo);
			textViewField.setText(R.string.personalInfo);
		}
	}

	//----button onClick----
	
	public void onClickBack(View view)
	{
		finish();
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) 
		{
			finish();
		}
		return false;
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.rg_register_provision, menu);
		return true;
	}

}
