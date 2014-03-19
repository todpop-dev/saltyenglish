package com.todpop.saltyenglish;

import java.net.URL;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;

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

public class HomeMyPageSaving extends Activity {

	SavingListViewAdapter savingListViewAdapter;
	ArrayList<SavingListViewItem> itemArray;
	SavingListViewItem mSavingListItem;
	ListView listView;
	
	ImageView noSaving;
	//int count = 0;
	
	SharedPreferences rgInfo;
	RelativeLayout listItemView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_my_page_saving);
		
		rgInfo = getSharedPreferences("rgInfo",0);
		
		noSaving = (ImageView)findViewById(R.id.home_mypage_saving_id_no_history);
		
		listView = (ListView)findViewById(R.id.home_mypage_saving_id_list_view);
		itemArray = new ArrayList<SavingListViewItem>();
		new RewardHistory().execute("http://todpop.co.kr/api/users/"+rgInfo.getString("mem_id", "NO")+"/get_reward_list.json");
		
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

    		if (position%2 == 1) {
    			convertView.setBackgroundResource(R.drawable.store_32_image_separatebox_white);
    		} else {
    			convertView.setBackgroundResource(R.drawable.store_32_image_separatebox_pink);
    		}
    		return convertView;
    	}
    }

	//--- reward history request class ---
	private class RewardHistory extends AsyncTask<String, Void, JSONObject> 
	{
		DefaultHttpClient httpClient ;
		@Override
		protected JSONObject doInBackground(String... urls) 
		{
			JSONObject result = null;
			try
			{
				String getURL = urls[0];
				HttpGet httpGet = new HttpGet(getURL); 
				HttpParams httpParameters = new BasicHttpParams(); 
				httpClient = new DefaultHttpClient(httpParameters); 
				HttpResponse response = httpClient.execute(httpGet); 
				HttpEntity resEntity = response.getEntity();


				if (resEntity != null)
				{    
					result = new JSONObject(EntityUtils.toString(resEntity)); 
					Log.d("RESPONSE JSON ---- ", result.toString());				        	
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
				if(json.getBoolean("status") == true) {
					JSONArray jsonArray = json.getJSONArray("data");
					for (int i=0;i<jsonArray.length();i++) {
						if(jsonArray.getJSONObject(i).getInt("reward_type") < 5000){
							JSONObject jsonObject = jsonArray.getJSONObject(i);
							String time = jsonObject.getString("created_at");
							time = time.substring(5, 16);
							time = time.replace('T', ' ');
							mSavingListItem = new SavingListViewItem(time, jsonObject.getString("title"), jsonObject.getString("sub_title"), jsonObject.getString("reward"));
							itemArray.add(mSavingListItem);
						}
						
					}		
					if(!itemArray.isEmpty()){
						noSaving.setVisibility(View.GONE);
						savingListViewAdapter = new SavingListViewAdapter(getApplicationContext(), R.layout.home_my_page_save_list_item_view, itemArray);
				    	listView.setAdapter(savingListViewAdapter);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
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
		getMenuInflater().inflate(R.menu.home_my_page_saving, menu);
		return true;
	}
	@Override
	protected void onStart()
	{
		super.onStart();
		FlurryAgent.onStartSession(this, "ZKWGFP6HKJ33Y69SP5QY");
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
