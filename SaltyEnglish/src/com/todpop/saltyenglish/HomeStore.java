package com.todpop.saltyenglish;


import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class HomeStore extends Activity{
	
	RadioButton eduationBtn;
	RadioButton foodBtn;
	RadioButton cafeBtn;
	RadioButton convenientBtn;
	RadioButton beautyBtn;
	RadioButton reFundBtn;
	
	StoreListViewAdapter storeListViewAdapter;
	ArrayList<StoreListViewItem> itemArray;
	StoreListViewItem mStoreListItem;
	ListView storeListView;
	int count = 0;
	
	RelativeLayout listItemView;
	ScrollView refundView;


    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_store);

//        eduationBtn = (RadioButton) findViewById(R.id.homestore_id_btn_education);
//        foodBtn = (RadioButton) findViewById(R.id.homestore_id_btn_food);
//        cafeBtn = (RadioButton) findViewById(R.id.homestore_id_btn_cafe);
//        convenientBtn = (RadioButton) findViewById(R.id.homestore_id_btn_convenient);
//        beautyBtn = (RadioButton) findViewById(R.id.homestore_id_btn_beauty);
//        reFundBtn = (RadioButton) findViewById(R.id.homestore_id_btn_refund);
//        
//        listItemView = (RelativeLayout)findViewById(R.id.home_store_id_list_view);
//        refundView = (ScrollView)findViewById(R.id.home_store_id_refund_view);
//        
//    	 eduationBtn.setOnClickListener(radio_listener);
//    	 foodBtn.setOnClickListener(radio_listener);
//    	 cafeBtn.setOnClickListener(radio_listener);
//    	 convenientBtn.setOnClickListener(radio_listener);
//    	 beautyBtn.setOnClickListener(radio_listener);
//    	 reFundBtn.setOnClickListener(radio_listener);
//    	 
//    	 
//    	 
//    	 itemArray = new ArrayList<StoreListViewItem>();
//    	 storeListView=(ListView)findViewById(R.id.homestore_id_listiew);
//    	 storeListView.setOnItemClickListener(listViewItemListener);
//    	 for(int i=0;i<20;i++) {
//    		 mStoreListItem = new StoreListViewItem(R.drawable.store_33_image_dinosaur_on,"cake","eleven","3,000");
//    		 itemArray.add(mStoreListItem);
//    	 }
//    	 this.updateListView();
    }
    
    public void updateListView()
    {
    	storeListViewAdapter = new StoreListViewAdapter(this,R.layout.home_store_list_item_view, itemArray);
    	storeListView.setAdapter(storeListViewAdapter);
    }
    
    OnItemClickListener listViewItemListener = new OnItemClickListener()
    {
        public void onItemClick(AdapterView<?> parentView, View clickedView, int position, long id)
        {
        	 //String toastMessage = ((TextView)clickedView).getText().toString() + " is selected."+position;
        	 String toastMessage = " is selected."+position;
             Toast.makeText(
                 getApplicationContext(),
                 toastMessage,
                 Toast.LENGTH_SHORT
             ).show();


        }
    };

    OnClickListener radio_listener = new OnClickListener (){
    	public void onClick(View v) {
    		switch(v.getId())
    		{
    		case R.id.homestore_id_btn_education:
    			listItemView.setVisibility(RelativeLayout.VISIBLE);
    			refundView.setVisibility(ScrollView.GONE);
    			itemArray.clear();
    			for(int i=0;i<20;i++) {
    	    		 mStoreListItem = new StoreListViewItem(R.drawable.store_33_image_dinosaur_on,"cake","eleven","3,000");
    	    		 itemArray.add(mStoreListItem);
    	    	 }
    			updateListView();
    			break;
    		case R.id.homestore_id_btn_food:
    			listItemView.setVisibility(RelativeLayout.VISIBLE);
    			refundView.setVisibility(ScrollView.GONE);
    			itemArray.clear();
    			for(int i=0;i<20;i++) {
    				mStoreListItem = new StoreListViewItem(R.drawable.store_33_image_dinosaur_on,"banana","cu","2,000");
    				itemArray.add(mStoreListItem);
    			}
    			updateListView();
    			break;
    		case R.id.homestore_id_btn_cafe:
    			break;
    		case R.id.homestore_id_btn_convenient:
    			break;
    		case R.id.homestore_id_btn_beauty:
    			break;
    		case R.id.homestore_id_btn_refund:
    			listItemView.setVisibility(RelativeLayout.GONE);
    			refundView.setVisibility(ScrollView.VISIBLE);
    			break;
    		}

    	}
    };

    class StoreListViewItem 
    {
    	StoreListViewItem(int aItem,String aName1,String aName2,String aCoin)
    	{
    		item = aItem;
    		name1 = aName1;
    		name2 = aName2;
    		coin = aCoin;
    	}
    	int item;
    	String name1;
    	String name2;
    	String coin;
    }

    class StoreListViewAdapter extends BaseAdapter
    {
    	Context maincon;
    	LayoutInflater Inflater;
    	ArrayList<StoreListViewItem> arSrc;
    	int layout;

    	public StoreListViewAdapter(Context context,int alayout,ArrayList<StoreListViewItem> aarSrc)
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
    		ImageView itemImg = (ImageView)convertView.findViewById(R.id.home_store_list_item_id_item);
    		itemImg.setImageResource(arSrc.get(position).item);

    		TextView name1Text = (TextView)convertView.findViewById(R.id.home_store_list_item_id_name1);
    		name1Text.setText(arSrc.get(position).name1);

    		TextView name2Text = (TextView)convertView.findViewById(R.id.home_store_list_item_id_name2);
    		name2Text.setText(arSrc.get(position).name2);
    		TextView coinText = (TextView)convertView.findViewById(R.id.home_store_list_item_id_coins);
    		coinText.setText(arSrc.get(position).coin);

    		if (count%2 == 1) {
    			convertView.setBackgroundResource(R.drawable.store_2_image_separatebox_white);
    		} else {
    			convertView.setBackgroundResource(R.drawable.store_2_image_separatebox_yellow);
    		}
    		return convertView;
    	}
    }


    public void onClickBack(View view)
    {
    	finish();
    }

}
