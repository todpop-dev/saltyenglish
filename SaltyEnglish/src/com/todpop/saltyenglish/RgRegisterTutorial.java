package com.todpop.saltyenglish;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


public class RgRegisterTutorial extends Activity {
	ViewPager viewPager;
	
	ImageView indi_1;
	ImageView indi_2;
	ImageView indi_3;
	ImageView indi_4;
	ImageView indi_5;
	ImageView indi_6;
	ImageView indi_7;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rg_register_tutorial);
		
		viewPager = (ViewPager)findViewById(R.id.rg_register_tutorial_id_viewpager);
		viewPager.setAdapter(new TutorialViewPagerAdapter(this));
		
		viewPager.setOnPageChangeListener(new TutorialViewPagerListener());
		
		indi_1 = (ImageView)findViewById(R.id.rg_register_tutorial_id_indicator_1);
		indi_2 = (ImageView)findViewById(R.id.rg_register_tutorial_id_indicator_2);
		indi_3 = (ImageView)findViewById(R.id.rg_register_tutorial_id_indicator_3);
		indi_4 = (ImageView)findViewById(R.id.rg_register_tutorial_id_indicator_4);
		indi_5 = (ImageView)findViewById(R.id.rg_register_tutorial_id_indicator_5);
		indi_6 = (ImageView)findViewById(R.id.rg_register_tutorial_id_indicator_6);
		indi_7 = (ImageView)findViewById(R.id.rg_register_tutorial_id_indicator_7);
		
		SharedPreferences pref = getSharedPreferences("rgInfo",0);
		SharedPreferences.Editor rgInfoEdit = pref.edit();
		rgInfoEdit.putBoolean("introMainOk", true);
		rgInfoEdit.apply();
	}   
	
	public void dismissTutorial(View v){
		Intent intent = new Intent(getApplicationContext(), StudyHome.class);
		startActivity(intent);
		finish();
	}
	
	private class TutorialViewPagerAdapter extends PagerAdapter{
    	private LayoutInflater mInflater;
    	
    	public TutorialViewPagerAdapter(Context c){
    		super();
    		mInflater = LayoutInflater.from(c);
    	}

		@Override
		public int getCount() {
			return 7;
		}

		@Override
		public Object instantiateItem(View pager, int position){
			View v = null;
			v = mInflater.inflate(R.layout.fragment_com_tutorial, null);
			
			RelativeLayout background = (RelativeLayout)v.findViewById(R.id.fragment_com_tutorial_id_mainview);
			LinearLayout linear = (LinearLayout)v.findViewById(R.id.fragment_com_tutorial_id_linear);
			Button button = (Button)v.findViewById(R.id.fragment_com_tutorial_id_button);
			
			if(position == 0){
				background.setBackgroundResource(R.drawable.home_tutorial_img_1);
				linear.setVisibility(View.GONE);
			}
			else if(position == 1){
				background.setBackgroundResource(R.drawable.home_tutorial_img_2);
				linear.setVisibility(View.GONE);
			}
			else if(position == 2){
				background.setBackgroundResource(R.drawable.home_tutorial_img_3);
				linear.setVisibility(View.GONE);
			}
			else if(position == 3){
				background.setBackgroundResource(R.drawable.home_tutorial_img_4);
				linear.setVisibility(View.GONE);
			}
			else if(position == 4){
				background.setBackgroundResource(R.drawable.home_tutorial_img_5);
				linear.setVisibility(View.GONE);
			}
			else if(position == 5){
				background.setBackgroundResource(R.drawable.home_tutorial_img_6);
				linear.setVisibility(View.GONE);
			}
			else if(position == 6){
				background.setBackgroundResource(R.drawable.home_tutorial_img_7);
				button.setBackgroundResource(R.drawable.rgregister_drawable_btn_tutorial);
				linear.setVisibility(View.VISIBLE);
			}
			
			((ViewPager)pager).addView(v, 0);
			
			return v;
		}
		@Override
		public void destroyItem(View pager, int position, Object view){
			((ViewPager)pager).removeView((View)view);
		}
		@Override
		public boolean isViewFromObject(View pager, Object obj) {
			return pager == obj;
		}
    	
    }
    private class TutorialViewPagerListener implements OnPageChangeListener{

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int position) {
			if(position == 0){
				indi_1.setImageResource(R.drawable.wordbook_tutorial_img_indicator_pressed);
				indi_2.setImageResource(R.drawable.wordbook_tutorial_img_indicator_normal);
			}
			else if(position == 1){
				indi_1.setImageResource(R.drawable.wordbook_tutorial_img_indicator_normal);
				indi_2.setImageResource(R.drawable.wordbook_tutorial_img_indicator_pressed);
				indi_3.setImageResource(R.drawable.wordbook_tutorial_img_indicator_normal);
			}
			else if(position == 2){
				indi_2.setImageResource(R.drawable.wordbook_tutorial_img_indicator_normal);
				indi_3.setImageResource(R.drawable.wordbook_tutorial_img_indicator_pressed);
				indi_4.setImageResource(R.drawable.wordbook_tutorial_img_indicator_normal);
			}
			else if(position == 3){
				indi_3.setImageResource(R.drawable.wordbook_tutorial_img_indicator_normal);
				indi_4.setImageResource(R.drawable.wordbook_tutorial_img_indicator_pressed);
				indi_5.setImageResource(R.drawable.wordbook_tutorial_img_indicator_normal);
			}
			else if(position == 4){
				indi_4.setImageResource(R.drawable.wordbook_tutorial_img_indicator_normal);
				indi_5.setImageResource(R.drawable.wordbook_tutorial_img_indicator_pressed);
				indi_6.setImageResource(R.drawable.wordbook_tutorial_img_indicator_normal);
			}
			else if(position == 5){
				indi_5.setImageResource(R.drawable.wordbook_tutorial_img_indicator_normal);
				indi_6.setImageResource(R.drawable.wordbook_tutorial_img_indicator_pressed);
				indi_7.setImageResource(R.drawable.wordbook_tutorial_img_indicator_normal);
			}
			else if(position == 6){
				indi_6.setImageResource(R.drawable.wordbook_tutorial_img_indicator_normal);
				indi_7.setImageResource(R.drawable.wordbook_tutorial_img_indicator_pressed);
			}
		}
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
						editor.apply();
						
						Intent intent = new Intent();
				        intent.setClass(RgRegisterTutorial.this, MainActivity.class);    
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
		getMenuInflater().inflate(R.menu.lv_test_bigin, menu);
		return false;
	}
}
