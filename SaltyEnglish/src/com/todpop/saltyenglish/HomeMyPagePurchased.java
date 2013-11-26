package com.todpop.saltyenglish;

import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;


import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class HomeMyPagePurchased extends Activity {
	
	PurchasedListViewAdapter purchasedListViewAdapter;
	ArrayList<PurchasedListViewItem> itemArray;
	PurchasedListViewItem mPurchasedListItem;
	ListView listView;
	
	RelativeLayout listItemView;
	SharedPreferences rgInfo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_my_page_purchased);
//		rgInfo = getSharedPreferences("rgInfo",0);
//		listView = (ListView)findViewById(R.id.home_mypage_purchased_id_list_view);
//		
//		new GetPurchasedInfo().execute("http://todpop.co.kr/api/etc/"+rgInfo.getString("mem_id", "NO")+"/get_purchase_list.json?coupon_type=1");
//		
//		
//		itemArray = new ArrayList<PurchasedListViewItem>();
//		for(int i=0;i<20;i++) {
//			mPurchasedListItem = new PurchasedListViewItem(R.drawable.home_image_ex3,"10:15","cake","eleven","3,000");
//			itemArray.add(mPurchasedListItem);
//		}
//		
//		purchasedListViewAdapter = new PurchasedListViewAdapter(this,R.layout.home_my_page_purchased_list_item_view, itemArray);
//    	listView.setAdapter(purchasedListViewAdapter);

	}

	class PurchasedListViewItem 
    {
		PurchasedListViewItem(int aImageId,String aTime,String aName1,String aName2,String aCoin)
    	{
			imageId = aImageId;
    		time = aTime;
    		name1 = aName1;
    		name2 = aName2;
    		coin = aCoin;
    	}
		int imageId;
    	String time;
    	String name1;
    	String name2;
    	String coin;
    }

    class PurchasedListViewAdapter extends BaseAdapter
    {
    	Context maincon;
    	LayoutInflater Inflater;
    	ArrayList<PurchasedListViewItem> arSrc;
    	int layout;

    	public PurchasedListViewAdapter(Context context,int alayout,ArrayList<PurchasedListViewItem> aarSrc)
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
    		if(convertView == null)
    		{
    			convertView = Inflater.inflate(layout, parent,false);
    		}
    		ImageView image = (ImageView)convertView.findViewById(R.id.home_mypage_purchased_list_item_id_item);
    		image.setImageResource(arSrc.get(position).imageId);
    		
    		TextView timeText = (TextView)convertView.findViewById(R.id.home_mypage_purchased_list_item_id_time);
    		timeText.setText(arSrc.get(position).time);

    		TextView name1Text = (TextView)convertView.findViewById(R.id.home_mypage_purchased_list_item_id_name1);
    		name1Text.setText(arSrc.get(position).name1);

    		TextView name2Text = (TextView)convertView.findViewById(R.id.home_mypage_purchased_list_item_id_name2);
    		name2Text.setText(arSrc.get(position).name2);
    		TextView coinText = (TextView)convertView.findViewById(R.id.home_mypage_purchased_list_item_id_coins);
    		coinText.setText(arSrc.get(position).coin);

    		if (position%2 == 1) {
    			convertView.setBackgroundResource(R.drawable.store_32_image_separatebox_white);
    		} else {
    			convertView.setBackgroundResource(R.drawable.store_32_image_separatebox_pink);
    		}
    		
    		Log.d("0ÑÑÑÑÑÑÑÑÑÑÑÑÑÑ2",""+position);
    		return convertView;
    	}
    }
    
    private class GetPurchasedInfo extends AsyncTask<String, Void, JSONObject> 
	{
		@Override
		protected JSONObject doInBackground(String... urls) 
		{
			JSONObject result = null;
			try
			{
				String getURL = urls[0];
				HttpGet httpGet = new HttpGet(getURL); 
				HttpParams httpParameters = new BasicHttpParams(); 
				DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters); 
				HttpResponse response = httpClient.execute(httpGet); 
				HttpEntity resEntity = response.getEntity();


				if (resEntity != null)
				{    
					result = new JSONObject(EntityUtils.toString(resEntity)); 
					Log.d("RESPONSE JSON CHECK MOBILE EXIST ---- ", result.toString());				        	
				}
				return result;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		
			return result;
		}

		@Override
		protected void onPostExecute(JSONObject json) {
			
			try {
				if(json.getBoolean("status")==true)
				{
				}
			} catch (Exception e) {

			}
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
		getMenuInflater().inflate(R.menu.home_my_page_purchased, menu);
		return true;
	}

}
