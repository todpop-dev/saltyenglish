package com.todpop.saltyenglish;

import java.util.ArrayList;


import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class HomeMyPageSaving extends Activity {
	

	SavingListViewAdapter savingListViewAdapter;
	ArrayList<SavingListViewItem> itemArray;
	SavingListViewItem mSavingListItem;
	ListView listView;
	int count = 0;
	
	RelativeLayout listItemView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_my_page_saving);
		
		
		listView = (ListView)findViewById(R.id.home_mypage_saving_id_list_view);
		
		
		itemArray = new ArrayList<SavingListViewItem>();
		for(int i=0;i<20;i++) {
			mSavingListItem = new SavingListViewItem("10:15","cake","eleven","3,000");
			itemArray.add(mSavingListItem);
		}
		
		savingListViewAdapter = new SavingListViewAdapter(this,R.layout.home_my_page_purchased_list_item_view, itemArray);
    	listView.setAdapter(savingListViewAdapter);
	}
	
	class SavingListViewItem 
    {
		SavingListViewItem(String aTime,String aName1,String aName2,String aCoin)
    	{
    		time = aTime;
    		name1 = aName1;
    		name2 = aName2;
    		coin = aCoin;
    	}
    	String time;
    	String name1;
    	String name2;
    	String coin;
    }

    class SavingListViewAdapter extends BaseAdapter
    {
    	Context maincon;
    	LayoutInflater Inflater;
    	ArrayList<SavingListViewItem> arSrc;
    	int layout;

    	public SavingListViewAdapter(Context context,int alayout,ArrayList<SavingListViewItem> aarSrc)
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
    		return arSrc.get(position).name1;
    	}

    	public long getItemId(int position)
    	{
    		return position;
    	}

    	public View getView(int position,View convertView,ViewGroup parent)
    	{
    		count++;
    		if(convertView == null)
    		{
    			convertView = Inflater.inflate(layout, parent,false);
    		}
    		
    		TextView timeText = (TextView)convertView.findViewById(R.id.home_mypage_purchased_list_item_id_time);
    		timeText.setText(arSrc.get(position).time);

    		TextView name1Text = (TextView)convertView.findViewById(R.id.home_mypage_purchased_list_item_id_name1);
    		name1Text.setText(arSrc.get(position).name1);

    		TextView name2Text = (TextView)convertView.findViewById(R.id.home_mypage_purchased_list_item_id_name2);
    		name2Text.setText(arSrc.get(position).name2);
    		TextView coinText = (TextView)convertView.findViewById(R.id.home_mypage_purchased_list_item_id_coins);
    		coinText.setText(arSrc.get(position).coin);

    		if (count%2 == 1) {
    			convertView.setBackgroundResource(R.drawable.store_32_image_separatebox_white);
    		} else {
    			convertView.setBackgroundResource(R.drawable.store_32_image_separatebox_pink);
    		}
    		return convertView;
    	}
    }
	
	
	// on click
	public void onClickBack(View v)
	{
		finish();
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home_my_page_saving, menu);
		return true;
	}

}
