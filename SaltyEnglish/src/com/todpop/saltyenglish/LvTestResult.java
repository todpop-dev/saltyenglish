package com.todpop.saltyenglish;

import java.util.ArrayList;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class LvTestResult extends Activity {
	
	ViewHolder viewHolder = null;

	ArrayList<MyItem> arItem;
	
 	// Database
 	WordDBHelper mHelper;
	
	ArrayList<String> enArray = new ArrayList<String>() ;
	ArrayList<String> krArray = new ArrayList<String>();
	
	//ArrayList<String> enSave = new ArrayList<String>() ;
	//ArrayList<String> krSave = new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lvtest_result);
		
		mHelper = new WordDBHelper(this);

		
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
			if(convertView == null) {
				viewHolder = new ViewHolder();
				v = Inflater.inflate(layout, parent,false);
				viewHolder.textEn = (TextView)v.findViewById(R.id.lv_test_english);
				viewHolder.textKr = (TextView)v.findViewById(R.id.lv_test_kr);
				viewHolder.checkView= (ImageView)v.findViewById(R.id.lv_test_check_correct);
				viewHolder.selectBtn =(CheckBox)v.findViewById(R.id.lv_test_btn);
				
				//v.setTag(viewHolder);
			} 
			
//			else {
//				viewHolder = (ViewHolder)v.getTag();
//			}
			
			viewHolder.textEn.setText(arSrc.get(position).en);
			viewHolder.textKr.setText(arSrc.get(position).kr);
			
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
			
			if(arSrc.get(position).check.equals("Y")) {
				viewHolder.checkView.setImageResource(R.drawable.lvtest_10_text_correct);
			} else {
				viewHolder.checkView.setImageResource(R.drawable.lvtest_10_text_incorrect);
			}
			
			if (position%2 == 1) {
				v.setBackgroundResource(R.drawable.lvtest_10_image_separatebox_blue_center);
			} else {
				v.setBackgroundResource(R.drawable.lvtest_10_image_separatebox_skyblue_center);
			}
			
			// Check if word is in word list
    		SQLiteDatabase db = mHelper.getWritableDatabase();
    		Cursor c = db.rawQuery("SELECT * FROM mywords WHERE name='" + arSrc.get(position).en + "'" , null);
    		if (c.getCount() > 0) {
    			viewHolder.selectBtn.setChecked(true);
    		} else {
    			viewHolder.selectBtn.setChecked(false);
    		}
    		
    		viewHolder.selectBtn.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if (isChecked) {
                    	// Insert word to DB
                		SQLiteDatabase db = mHelper.getWritableDatabase();

            			ContentValues cv = new ContentValues();
            			cv.put("name", arSrc.get((Integer)(buttonView.getTag())).en);
            			cv.put("mean", arSrc.get((Integer)(buttonView.getTag())).kr);
            			db.replace("mywords", null, cv);
                    } else {
                    	// Delete word to DB
                		SQLiteDatabase db = mHelper.getWritableDatabase();       
                		try {
                    		db.delete("mywords", "name='" + arSrc.get((Integer)(buttonView.getTag())).en+"'", null);
                		} catch(Exception e) {
                			e.printStackTrace();
                		}
                    }
                }
            });
			
			return v;
		}
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

	
	//------- Database Operation ------------------
	private class WordDBHelper extends SQLiteOpenHelper {
		public WordDBHelper(Context context) {
			super(context, "EngWord.db", null, 1);
		}
		
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE mywords ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
		"name TEXT NOT NULL UNIQUE, mean TEXT);");
		}
		
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS mywords");
			onCreate(db);
		}
	}


	@Override
	public void onDestroy()
	{
		super.onDestroy();
		mHelper.close();
	}
}
