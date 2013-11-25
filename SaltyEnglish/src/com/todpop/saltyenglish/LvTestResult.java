package com.todpop.saltyenglish;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class LvTestResult extends Activity {
	
	ViewHolder viewHolder = null;

	ArrayList<MyItem> arItem;
	
	ArrayList<String> enArray = new ArrayList<String>() ;
	ArrayList<String> krArray = new ArrayList<String>();
	
	//ArrayList<String> enSave = new ArrayList<String>() ;
	//ArrayList<String> krSave = new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lvtest_result);
		
		SharedPreferences lvTextWord = getSharedPreferences("lvTextWord",0);
		arItem = new ArrayList<MyItem>();
		MyItem mi;
		
		TextView level = (TextView)findViewById(R.id.lvtest_result_id_level);
		SharedPreferences rgInfo = getSharedPreferences("rgInfo",0);
		
		level.setText(rgInfo.getString("level", "NO"));
		
		// Save level info to StudyLevelInfo
		SharedPreferences prefs = getSharedPreferences("StudyLevelInfo", MODE_PRIVATE);
		SharedPreferences.Editor ed = prefs.edit();
		ed.putInt("totalStage", (Integer.parseInt(rgInfo.getString("level", "1"))-1)*10+1);
		ed.commit();
		// ---- 
		
		for(int i=0;i<20;i++) {
			mi = new MyItem(lvTextWord.getString("enWord"+i, "N"),lvTextWord.getString("krWord"+i, "N"),lvTextWord.getString("check"+i, "N"));
			arItem.add(mi);
		}
		
		
		MyListAdapter MyAdapter = new MyListAdapter(this,R.layout.lvtest_result_list_item_view, arItem);
		
		
		ListView MyList;
		MyList=(ListView)findViewById(R.id.lvtestresult_id_listview);
		MyList.setAdapter(MyAdapter);
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

	class MyListAdapter extends BaseAdapter
	{
		Context maincon;
		LayoutInflater Inflater;
		ArrayList<MyItem> arSrc;
		int layout;
		
		public MyListAdapter(Context context,int alayout,ArrayList<MyItem> aarSrc)
		{
			maincon = context;
			Inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			arSrc = aarSrc;
			layout = alayout;
		}
		public int getCount()
		{
			
			return arSrc.size();
		}
		
		public String getItem(int position)
		{
			return arSrc.get(position).en;
		}
		
		public long getItemId(int position)
		{
			return position;
		}
		
		public View getView(int position,View convertView,ViewGroup parent)
		{
			View v = convertView;
			if(convertView == null)
			{
				viewHolder = new ViewHolder();
				v = Inflater.inflate(layout, parent,false);
				viewHolder.textEn = (TextView)v.findViewById(R.id.lv_test_english);
				viewHolder.textKr = (TextView)v.findViewById(R.id.lv_test_kr);
				viewHolder.checkView= (ImageView)v.findViewById(R.id.lv_test_check_correct);
				viewHolder.selectBtn =(CheckBox)v.findViewById(R.id.lv_test_btn);
				
				v.setTag(viewHolder);
			}
			else {
				viewHolder = (ViewHolder)v.getTag();
			}
			viewHolder.textEn.setText(arSrc.get(position).en);
			viewHolder.textKr.setText(arSrc.get(position).kr);
			viewHolder.selectBtn.setOnClickListener(buttonClickListener);
			
			viewHolder.textEn.setTag(position);
			viewHolder.textEn.setTag(position);
			viewHolder.selectBtn.setTag(position);
			
//			if(enSave != null)
//			{
//				if(enSave.get(position).equals(arSrc.get(position).en))
//				{
//					viewHolder.selectBtn.setEnabled(false);
//				}else{
//					viewHolder.selectBtn.setEnabled(true);
//				}
//			}
			
			if(arSrc.get(position).check.equals("Y"))
			{
				viewHolder.checkView.setImageResource(R.drawable.lvtest_10_text_correct);

			}else
			{
				viewHolder.checkView.setImageResource(R.drawable.lvtest_10_text_incorrect);
				
			}
			
			if (position%2 == 1) {
				v.setBackgroundResource(R.drawable.lvtest_10_image_separatebox_blue_center);
			} else {
				v.setBackgroundResource(R.drawable.lvtest_10_image_separatebox_skyblue_center);
			}
			
			
			return v;
		}
		public View.OnClickListener buttonClickListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				
				case R.id.lv_test_btn:
					//Button btn = (Button)v.getTag();
					v.setEnabled(false);
					
//					enSave.add(enArray.get((Integer)(v.getTag())));
//					krSave.add(krArray.get((Integer)(v.getTag())));
					Log.d("-------------------------",""+v.getTag());
					break;
				default:
					break;
				}
			}
		};
	}
	
	class ViewHolder{
		public TextView textEn = null;
		public TextView textKr = null;
		public ImageView checkView = null;
		public CheckBox selectBtn = null;
		
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
				        intent.setClass(LvTestResult.this, MainActivity.class);    
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
	
	public void showHomeActivity(View v)
	{
		
		SharedPreferences settings = getSharedPreferences("myword", 0);
		SharedPreferences.Editor editor = settings.edit();
		
		for(int i =0;i<enArray.size();i++)
		{
			editor.putString("enWord", enArray.get(i));
			editor.putString("krWord", krArray.get(i));
		}
		
		editor.commit();
	
		Intent intent = new Intent(getApplicationContext(), StudyHome.class);
		startActivity(intent);
		finish();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.lvtest_result, menu);
		return true;
	}

}
