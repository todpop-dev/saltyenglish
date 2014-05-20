package com.todpop.saltyenglish;

import java.util.ArrayList;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;
import com.todpop.saltyenglish.db.WordDBHelper;

import android.os.Bundle;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Point;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class HomeWordList extends Activity {
	ViewHolder viewHolder = null;
	
	HomeWordViewAdapter homeWordViewAdapter;

	ArrayList<HomeWordViewItem> listArray;
	ArrayList<String> deleteWords;
	
	HomeWordViewItem mHomeWordViewItem;
	ListView listView;
	Point size;
	Button card;
	ObjectAnimator cardAni;
	
	ImageView noWord;
	
	boolean checkCardAni = false;
	boolean checkChangeWord = false;
	boolean checkEdit = false;
	
	Button deleteBtn;
	float density;
	
	RelativeLayout editBg;
	CheckBox selectAllBtn;
	
	RelativeLayout tutorial_layout;
	ViewPager tutorial_view;
	
	ImageView indi_1;
	ImageView indi_2;
	ImageView indi_3;
	ImageView indi_4;
	ImageView indi_5;
	
	// popup view
	PopupWindow popupWindow;
	View popupview;
	RelativeLayout relative;
 	WordDBHelper mHelper;
	EditText searchText;
	
	// popup view for no word

	PopupWindow noWordPopupWindow;
	View noWordPopupView;
	TextView noWordPopupText;
	
	SharedPreferences myWord;
	
	ArrayList<Boolean> boolList = new ArrayList<Boolean>();  
	
	int wordListSize = 0;
	
	static int count = 0;
	
	static boolean isSelectAll = false;
	static boolean isDeleting = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_word_list);
		
		editBg = (RelativeLayout)findViewById(R.id.wordbook_16_image_edit_bg_new);
		selectAllBtn = (CheckBox)findViewById(R.id.home_word_list_id_select_all_btn);
		selectAllBtn.setEnabled(true);
		selectAllBtn.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                	isSelectAll = true;
                	
                } else {
                	isSelectAll = false;
                }
        		updateListView();

            }
        });	

		noWord = (ImageView)findViewById(R.id.home_word_list_id_no_word);
		
		Display display = getWindowManager().getDefaultDisplay();
		size = new Point();
		display.getSize(size);
		myWord = getSharedPreferences("myword", 0);
		
		// Search Text
		searchText = (EditText)findViewById(R.id.my_word_id_edittext);

		// DB Helper
		mHelper = new WordDBHelper(this);
		deleteWords = new ArrayList<String>();
		
		deleteBtn = (Button)findViewById(R.id.home_word_list_id_delete);
		

		SharedPreferences pref = getSharedPreferences("rgInfo",0);
		Boolean introOk = pref.getBoolean("introWordListOk", false);
		if(!introOk){
			//tutorial
			tutorial_layout = (RelativeLayout)findViewById(R.id.home_word_list_id_tutorial);
			tutorial_view = (ViewPager)findViewById(R.id.home_word_list_id_pager);
			
			tutorial_view.setAdapter(new WordListTutoPagerAdapter(this));
			
			tutorial_view.setOnPageChangeListener(new WordListTutoPagerListener());

			indi_1 = (ImageView)findViewById(R.id.home_word_list_id_indicator_1);
			indi_2 = (ImageView)findViewById(R.id.home_word_list_id_indicator_2);
			indi_3 = (ImageView)findViewById(R.id.home_word_list_id_indicator_3);
			indi_4 = (ImageView)findViewById(R.id.home_word_list_id_indicator_4);
			indi_5 = (ImageView)findViewById(R.id.home_word_list_id_indicator_5);
			
			tutorial_layout.setVisibility(View.VISIBLE);
		}
		
		//popupview
		relative = (RelativeLayout)findViewById(R.id.home_word_list_id_main_view);
		popupview = View.inflate(this, R.layout.popup_view_home_word_list, null);
		popupview.setFocusable(true); 
		popupview.setFocusableInTouchMode(true);
		density = getResources().getDisplayMetrics().density;
		popupWindow = new PopupWindow(popupview,ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT,true);
		popupWindow.setFocusable(true);
		
		//popupview for no word
		noWordPopupView = View.inflate(this, R.layout.popup_view, null);
		noWordPopupWindow = new PopupWindow(noWordPopupView, ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT,true);
		noWordPopupText = (TextView)noWordPopupView.findViewById(R.id.popup_id_text);
		
		popupview.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					popupWindow.dismiss();
					return true;
				}
				return false;
			}
		});
		//popupText = (TextView)popupview.findViewById(R.id.popup_id_text);
		

		
		
		card = (Button)findViewById(R.id.home_word_list_id_card);
		listArray = new ArrayList<HomeWordViewItem>();
		listView=(ListView)findViewById(R.id.home_word_list_id_list_view);
		
//		for(int i=0;!myWord.getString("enWord"+i, "").equals("");i++) {
//			mHomeWordViewItem = new HomeWordViewItem(myWord.getString("enWord"+i, ""),myWord.getString("krWord"+i, ""));
//			listArray.add(mHomeWordViewItem);
//			boolList.add(false);
//		}
		
		// Get Word List
		SQLiteDatabase db = mHelper.getWritableDatabase();
//		try {
//			db.execSQL("CREATE TABLE mywords ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
//					"name TEXT, mean TEXT);");
//		} catch (Exception e) {
//			
//		}
		
		try {
			Cursor c = db.rawQuery("SELECT name, mean FROM mywords", null);
			wordListSize = c.getCount();
			while (c.moveToNext()) {
				mHomeWordViewItem = new HomeWordViewItem(c.getString(0), c.getString(1));
				listArray.add(mHomeWordViewItem);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		updateListView();
	}
    @Override
    protected void onResume() 
    {
        super.onResume();

		com.facebook.AppEventsLogger.activateApp(this, "218233231697811");
		cardAni = ObjectAnimator.ofFloat(card,"translationX",-size.x/2); 
		cardAni.setDuration(500);
		cardAni.start();
    }
    
    private class WordListTutoPagerAdapter extends PagerAdapter{
    	private LayoutInflater mInflater;
    	
    	public WordListTutoPagerAdapter(Context c){
    		super();
    		mInflater = LayoutInflater.from(c);
    	}

		@Override
		public int getCount() {
			return 5;
		}

		@Override
		public Object instantiateItem(View pager, int position){
			View v = null;
			v = mInflater.inflate(R.layout.fragment_com_tutorial, null);
			
			RelativeLayout background = (RelativeLayout)v.findViewById(R.id.fragment_com_tutorial_id_mainview);
			LinearLayout linear = (LinearLayout)v.findViewById(R.id.fragment_com_tutorial_id_linear);
			
			if(position == 0){
				background.setBackgroundResource(R.drawable.wordbook_tutorial_img_1);
				linear.setVisibility(View.GONE);
			}
			else if(position == 1){
				background.setBackgroundResource(R.drawable.wordbook_tutorial_img_2);
				linear.setVisibility(View.GONE);
			}
			else if(position == 2){
				background.setBackgroundResource(R.drawable.wordbook_tutorial_img_3);
				linear.setVisibility(View.GONE);
			}
			else if(position == 3){
				background.setBackgroundResource(R.drawable.wordbook_tutorial_img_4);
				linear.setVisibility(View.GONE);
			}
			else if(position == 4){
				background.setBackgroundResource(R.drawable.wordbook_tutorial_img_5);
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
    private class WordListTutoPagerListener implements OnPageChangeListener{

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
			}
		}
	}
    
    public void dismissTutorial(View v){
		SharedPreferences pref = getSharedPreferences("rgInfo",0);
		SharedPreferences.Editor prefEditor= pref.edit();
		prefEditor.putBoolean("introWordListOk", true);
		prefEditor.apply();
    	tutorial_layout.setVisibility(View.GONE);
    }
    
	public void updateListView()
    {
		if(listArray.isEmpty()){
			noWord.setVisibility(View.VISIBLE);
		}
		else{
			listView.setAdapter(null);
			homeWordViewAdapter = new HomeWordViewAdapter(this,R.layout.home_word_list_list_item_view, listArray);
			listView.setAdapter(homeWordViewAdapter);
		}
    }
	
	public void closePopup(View v)
	{
		noWordPopupWindow.dismiss();
	}
	
	
	class HomeWordViewItem 
	{
		HomeWordViewItem(String aWord1,String aWord2)
		{
			word1 = aWord1;
			word2 = aWord2;
		}
		
		String word1;
		String word2;
	}

	class HomeWordViewAdapter extends BaseAdapter
	{
		Context maincon;
		LayoutInflater Inflater;
		ArrayList<HomeWordViewItem> arSrc;
		int layout;

		public HomeWordViewAdapter(Context context,int alayout,ArrayList<HomeWordViewItem> aarSrc)
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
			return arSrc.get(position).word1;
		}

		public long getItemId(int position)
		{
			return position;
		}

		public View getView(int position,View convertView,ViewGroup parent)
		{
			View v = convertView;
			if(v == null) {
				viewHolder = new ViewHolder();
				v = Inflater.inflate(layout, parent,false);
				viewHolder.textEn = (TextView)v.findViewById(R.id.home_word_list_id_word1);
				viewHolder.textKr = (TextView)v.findViewById(R.id.home_word_list_id_word2);
				viewHolder.select = (CheckBox)v.findViewById(R.id.home_word_list_id_check);
				v.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder)v.getTag();
			}
			
			if(checkEdit==false) {
				viewHolder.select.setVisibility(LinearLayout.GONE);
			} else {
				viewHolder.select.setVisibility(LinearLayout.VISIBLE);
				if (isSelectAll == true) {
					viewHolder.select.setChecked(true);
					if (checkChangeWord==false) {
						deleteWords.add(arSrc.get(position).word1);
					} else {
						deleteWords.add(arSrc.get(position).word2);
					}
				} else {
					viewHolder.select.setChecked(false);
				}
			}
			
			
			viewHolder.textEn.setText(arSrc.get(position).word1);
			viewHolder.textEn.setTag(position);

			viewHolder.textKr.setText(arSrc.get(position).word2);
			viewHolder.textKr.setTag(position);
			
			viewHolder.select.setTag(position);
			//viewHolder.select.setOnClickListener(buttonClickListener);
			viewHolder.select.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if (isChecked) {
    					if (checkChangeWord==false) {
    						deleteWords.add(arSrc.get((Integer)buttonView.getTag()).word1);
    					} else {
    						deleteWords.add(arSrc.get((Integer)buttonView.getTag()).word2);
    					}
                    } else {
    					if (checkChangeWord==false) {
    						deleteWords.remove(arSrc.get((Integer)buttonView.getTag()).word1);
    					} else {
    						deleteWords.remove(arSrc.get((Integer)buttonView.getTag()).word2);
    					}
                    }
                }
            });
			

			if (position%2 == 1) {
				v.setBackgroundResource(R.drawable.wordbook_1_image_separatebox_white);
			} else {
				v.setBackgroundResource(R.drawable.wordbook_1_image_separatebox_yellow);
			}
			
			//selectAllBtn.setChecked(false);
			return v;
		}
	}
	
	class ViewHolder{
		public TextView textEn = null;
		public TextView textKr = null;
		public CheckBox select = null;		
	}
	
	// on click
	public void onClickBack(View v)
	{
		finish();
	}

	public void cardBlind(View v)
	{

		if(checkCardAni==false)
		{
			cardAni = ObjectAnimator.ofFloat(card,"translationX", -35f); 
			cardAni.setDuration(500);
			cardAni.start();
			checkCardAni = true;
		}else{
			cardAni = ObjectAnimator.ofFloat(card,"translationX",-size.x/2); 
			cardAni.setDuration(500);
			cardAni.start();
			checkCardAni = false;
		}
	}
	
	public void changeWord(View v)
	{
		listArray.clear();
		if(checkChangeWord == false)
		{
			checkChangeWord = true;
			for(int i=0;!myWord.getString("enWord"+i, "").equals("");i++) {
				mHomeWordViewItem = new HomeWordViewItem(myWord.getString("krWord"+i, ""),myWord.getString("enWord"+i, ""));
				listArray.add(mHomeWordViewItem);
			}
			
		}else{
			checkChangeWord = false;
			for(int i=0;!myWord.getString("enWord"+i, "").equals("");i++) {
				mHomeWordViewItem = new HomeWordViewItem(myWord.getString("enWord"+i, ""),myWord.getString("krWord"+i, ""));
				listArray.add(mHomeWordViewItem);
			}
		}
		updateListView();
	}
	
	public void editWord(View v)
	{
		if(checkEdit==false) {
			listArray.clear();
			try {
				if(checkChangeWord == false) {				
					SQLiteDatabase db = mHelper.getWritableDatabase();
					Cursor c = db.rawQuery("SELECT name, mean FROM mywords", null);
					while (c.moveToNext()) {
						mHomeWordViewItem = new HomeWordViewItem(c.getString(0), c.getString(1));
						listArray.add(mHomeWordViewItem);
					}
				} else {
					SQLiteDatabase db = mHelper.getWritableDatabase();
					Cursor c = db.rawQuery("SELECT name, mean FROM mywords", null);
					while (c.moveToNext()) {
						mHomeWordViewItem = new HomeWordViewItem(c.getString(1), c.getString(0));
						listArray.add(mHomeWordViewItem);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			
			updateListView();
			LayoutParams lp = (LayoutParams) listView.getLayoutParams();
		       lp.height = 450*(int)density;
		       listView.setLayoutParams(lp);
		       
			card.setVisibility(RelativeLayout.GONE);
			editBg.setVisibility(RelativeLayout.VISIBLE);
			selectAllBtn.setVisibility(RelativeLayout.VISIBLE);
			deleteBtn.setVisibility(RelativeLayout.VISIBLE);
			checkEdit=true;
		} else {
			listArray.clear();
			
			try {
				if(checkChangeWord == false) {
					SQLiteDatabase db = mHelper.getWritableDatabase();
					Cursor c = db.rawQuery("SELECT name, mean FROM mywords", null);
					while (c.moveToNext()) {
						mHomeWordViewItem = new HomeWordViewItem(c.getString(0), c.getString(1));
						listArray.add(mHomeWordViewItem);
					}
				} else {
					SQLiteDatabase db = mHelper.getWritableDatabase();
					Cursor c = db.rawQuery("SELECT name, mean FROM mywords", null);
					while (c.moveToNext()) {
						mHomeWordViewItem = new HomeWordViewItem(c.getString(1), c.getString(0));
						listArray.add(mHomeWordViewItem);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			

			
			updateListView();
			LayoutParams lp = (LayoutParams) listView.getLayoutParams();
		       lp.height = 500*(int)density;
		       listView.setLayoutParams(lp);
			card.setVisibility(RelativeLayout.VISIBLE);
			editBg.setVisibility(RelativeLayout.GONE);
			selectAllBtn.setVisibility(RelativeLayout.GONE);
			deleteBtn.setVisibility(RelativeLayout.GONE);
			checkEdit=false;
		}
	}
	
	public void testBtn(View v)
	{
		if(wordListSize != 0){
			popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
		}
		else{
			noWordPopupText.setText(R.string.word_list_test_no_word);
			noWordPopupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
		}
	}
	
	
	public void homeWordTest15(View v)
	{
		Intent intent = new Intent(getApplicationContext(), WordListTest.class);
		if(wordListSize >= 15){
			intent.putExtra("testListSize", 15);
		}
		else{
			intent.putExtra("testListSize", wordListSize);
		}
		popupWindow.dismiss();
		startActivity(intent);
	}
	
	public void homeWordTest30(View v)
	{
		Intent intent = new Intent(getApplicationContext(), WordListTest.class);
		if(wordListSize >= 30){
			intent.putExtra("testListSize", 30);
		}
		else{
			intent.putExtra("testListSize", wordListSize);
		}
		popupWindow.dismiss();
		startActivity(intent);
	}
	
	public void homeWordTestAll(View v)
	{
		Intent intent = new Intent(getApplicationContext(), WordListTest.class);
		intent.putExtra("testListSize", wordListSize);

		popupWindow.dismiss();
		startActivity(intent);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home_word_list, menu);
		return true;
	}
	
	// Search words
	public void searchWord (View v) 
	{
		String sT = searchText.getText().toString();
		if (sT.length() > 0) {
			// Get Word List
			listArray.clear();
			SQLiteDatabase db = mHelper.getWritableDatabase();
			
			
			try {
				Cursor c = db.rawQuery("SELECT name, mean FROM mywords WHERE name LIKE '%" + sT + "%'", null);
				while (c.moveToNext()) {
					mHomeWordViewItem = new HomeWordViewItem(c.getString(0), c.getString(1));
					listArray.add(mHomeWordViewItem);
				}
				c = db.rawQuery("SELECT name, mean FROM mywords WHERE mean LIKE '%" + sT + "%'", null);
				while (c.moveToNext()) {
					mHomeWordViewItem = new HomeWordViewItem(c.getString(0), c.getString(1));
					listArray.add(mHomeWordViewItem);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			
			updateListView();

		} else {
			// Get Word List
			listArray.clear();
			SQLiteDatabase db = mHelper.getWritableDatabase();
			
			try {
				Cursor c = db.rawQuery("SELECT name, mean FROM mywords", null);
				while (c.moveToNext()) {
					mHomeWordViewItem = new HomeWordViewItem(c.getString(0), c.getString(1));
					listArray.add(mHomeWordViewItem);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			
			updateListView();
		}
	}
	
	// Change Word position
	public void changeWordPosition(View v)
	{
		
		count++;
		
		String sT = searchText.getText().toString();
		if (sT.length() > 0) {
			// Get Word List
			listArray.clear();
			SQLiteDatabase db = mHelper.getWritableDatabase();
			
			try {
				Cursor c = db.rawQuery("SELECT name, mean FROM mywords WHERE name LIKE '%" + sT + "%'", null);
				while (c.moveToNext()) {
					if (count%2==1) {
						checkChangeWord = true;
						mHomeWordViewItem = new HomeWordViewItem(c.getString(1), c.getString(0));
					} else {
						checkChangeWord = false;
						mHomeWordViewItem = new HomeWordViewItem(c.getString(0), c.getString(1));
					}
					listArray.add(mHomeWordViewItem);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			updateListView();

		} else {
			// Get Word List
			listArray.clear();
			SQLiteDatabase db = mHelper.getWritableDatabase();
			
			try {
				Cursor c = db.rawQuery("SELECT name, mean FROM mywords", null);
				while (c.moveToNext()) {
					if (count%2==1) {
						checkChangeWord = true;
						mHomeWordViewItem = new HomeWordViewItem(c.getString(1), c.getString(0));
					} else {
						checkChangeWord = false;
						mHomeWordViewItem = new HomeWordViewItem(c.getString(0), c.getString(1));
					}				
					listArray.add(mHomeWordViewItem);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			
			updateListView();
		}
	}
	
	// Delete words
	public void deleteWords(View v)
	{
		for (int i=0; i<deleteWords.size(); i++) {
			SQLiteDatabase db = mHelper.getWritableDatabase();
			try {
				db.delete("mywords", "name='" + deleteWords.get(i) + "'", null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		listArray.clear();
		SQLiteDatabase db = mHelper.getWritableDatabase();
		
		try {
			Cursor c = db.rawQuery("SELECT name, mean FROM mywords", null);
			wordListSize = c.getCount();
			while (c.moveToNext()) {
				mHomeWordViewItem = new HomeWordViewItem(c.getString(0), c.getString(1));
				listArray.add(mHomeWordViewItem);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		selectAllBtn.setChecked(false);
		updateListView();
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		mHelper.close();
	}
	@Override
	protected void onStart()
	{
		super.onStart();
		FlurryAgent.onStartSession(this, "ZKWGFP6HKJ33Y69SP5QY");
		FlurryAgent.logEvent("My Word List");
	    EasyTracker.getInstance(this).activityStart(this);
	}
	 
	@Override
	protected void onStop()
	{
		super.onStop();		
		FlurryAgent.onEndSession(this);
	    EasyTracker.getInstance(this).activityStop(this);
	}
}
