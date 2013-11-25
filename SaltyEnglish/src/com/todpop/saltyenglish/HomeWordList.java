package com.todpop.saltyenglish;

import java.util.ArrayList;


import android.os.Bundle;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class HomeWordList extends Activity {
	ViewHolder viewHolder = null;
	
	HomeWordViewAdapter homeWordViewAdapter;
	ArrayList<HomeWordViewItem> listArray;
	HomeWordViewItem mHomeWordViewItem;
	ListView listView;
	
	Button card;
	ObjectAnimator cardAni;
	
	boolean checkCardAni = false;
	boolean checkChangeWord = false;
	boolean checkEdit = false;
	
	Button deleteBtn;
	float density;
	
	ImageView editBg;
	CheckBox selectAllBtn;
	
	// popup view
	PopupWindow popupWindow;
	View popupview;
	RelativeLayout relative;
	
	
	SharedPreferences myWord;
	
	ArrayList<Boolean> boolList = new ArrayList<Boolean>();  
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_word_list);
		
		editBg = (ImageView)findViewById(R.id.wordbook_16_image_edit_bg);
		selectAllBtn = (CheckBox)findViewById(R.id.home_word_list_id_select_all_btn);
		
		myWord = getSharedPreferences("myword", 0);

		
		deleteBtn = (Button)findViewById(R.id.home_word_list_id_delete);
		//popupview
		relative = (RelativeLayout)findViewById(R.id.home_word_list_id_main_view);
		popupview = View.inflate(this, R.layout.popup_view_home_word_list, null);
		popupview.setFocusable(true); 
		popupview.setFocusableInTouchMode(true);
		density = getResources().getDisplayMetrics().density;
		popupWindow = new PopupWindow(popupview,(int)(300*density),(int)(180*density),true);
		popupWindow.setFocusable(true);
		
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
		
		
		
		for(int i=0;!myWord.getString("enWord"+i, "").equals("");i++) {
			mHomeWordViewItem = new HomeWordViewItem(myWord.getString("enWord"+i, ""),myWord.getString("krWord"+i, ""));
			listArray.add(mHomeWordViewItem);
			boolList.add(false);
		}

		updateListView();
	}
	
	public void updateListView()
    {
		homeWordViewAdapter = new HomeWordViewAdapter(this,R.layout.home_word_list_list_item_view, listArray);
		listView.setAdapter(homeWordViewAdapter);
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
			if(v == null)
			{
				viewHolder = new ViewHolder();
				v = Inflater.inflate(layout, parent,false);
				viewHolder.textEn = (TextView)v.findViewById(R.id.home_word_list_id_word1);
				viewHolder.textKr = (TextView)v.findViewById(R.id.home_word_list_id_word2);
				viewHolder.select = (CheckBox)v.findViewById(R.id.home_word_list_id_check);
				v.setTag(viewHolder);
			}else{
				viewHolder = (ViewHolder)v.getTag();
			}
			if(checkEdit==false)
			{
				viewHolder.select.setVisibility(LinearLayout.GONE);
			}else
			{
				viewHolder.select.setVisibility(LinearLayout.VISIBLE);
			}
			viewHolder.textEn.setText(arSrc.get(position).word1);
			viewHolder.textEn.setTag(position);

			viewHolder.textKr.setText(arSrc.get(position).word2);
			viewHolder.textKr.setTag(position);
			
			viewHolder.select.setTag(position);
			viewHolder.select.setOnClickListener(buttonClickListener);

			if (position%2 == 1) {
				v.setBackgroundResource(R.drawable.wordbook_1_image_separatebox_white);
			} else {
				v.setBackgroundResource(R.drawable.wordbook_1_image_separatebox_yellow);
			}
			return v;
		}
		
		public View.OnClickListener buttonClickListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				 if (((CheckBox) v).isChecked()) {
                     //Case 1
				 }
				 else {
					 
				 }
			}
		};
	
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
		float density = getResources().getDisplayMetrics().density;
		if(checkCardAni==false)
		{
			cardAni = ObjectAnimator.ofFloat(card,"x",-180f*density); 
			cardAni.setDuration(500);
			cardAni.start();
			checkCardAni = true;
		}else{
			cardAni = ObjectAnimator.ofFloat(card,"x",-285f*density); 
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
		if(checkEdit==false)
		{
			listArray.clear();
			if(checkChangeWord == false)
			{
				checkChangeWord = false;
				for(int i=0;!myWord.getString("enWord"+i, "").equals("");i++) {
					mHomeWordViewItem = new HomeWordViewItem(myWord.getString("enWord"+i, ""),myWord.getString("krWord"+i, ""));
					listArray.add(mHomeWordViewItem);
				}
			}else{
				for(int i=0;!myWord.getString("enWord"+i, "").equals("");i++) {
					mHomeWordViewItem = new HomeWordViewItem(myWord.getString("krWord"+i, ""),myWord.getString("enWord"+i, ""));
					listArray.add(mHomeWordViewItem);
				}
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
		}else
		{
			listArray.clear();
			if(checkChangeWord == false)
			{
				for(int i=0;!myWord.getString("enWord"+i, "").equals("");i++) {
					mHomeWordViewItem = new HomeWordViewItem(myWord.getString("enWord"+i, ""),myWord.getString("krWord"+i, ""));
					listArray.add(mHomeWordViewItem);
				}
			}else{
				for(int i=0;!myWord.getString("enWord"+i, "").equals("");i++) {
					mHomeWordViewItem = new HomeWordViewItem(myWord.getString("krWord"+i, ""),myWord.getString("enWord"+i, ""));
					listArray.add(mHomeWordViewItem);
				}
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
		popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
	}
	
	
	public void homeWordTest(View v)
	{
		Intent intent = new Intent(getApplicationContext(), StudyTestA.class);
		startActivity(intent);
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home_word_list, menu);
		return true;
	}

}
