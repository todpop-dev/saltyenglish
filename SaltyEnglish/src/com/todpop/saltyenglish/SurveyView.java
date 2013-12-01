package com.todpop.saltyenglish;

import java.net.URL;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.todpop.saltyenglish.HomeDownload.CpiListViewAdapter;
import com.todpop.saltyenglish.HomeDownload.CpiListViewItem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;



public class SurveyView extends Activity {
	
	ListView listView;
	ContentItem contentItem;
	ArrayList<ContentItem> listArray;
	
	ListViewAdapter listViewAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_survey_view);
		
		SharedPreferences cpxInfo = getSharedPreferences("cpxInfo",0);

		listView = (ListView)findViewById(R.id.survey_id_list_view);
		listArray = new ArrayList<ContentItem>();
		
		
		listViewAdapter = new ListViewAdapter(SurveyView.this,R.layout.survey_view_list_item, listArray);
		listView.setAdapter(listViewAdapter);
		new GetInfo().execute("http://todpop.co.kr/api/advertises/get_cps_questions.json?ad_id="+cpxInfo.getInt("adId", 0));
	}
	
	// on click
	public void onClickBack(View view)
	{
		finish();
	}
	
	private class GetInfo extends AsyncTask<String, Void, JSONObject> 
	{
		@Override
		protected JSONObject doInBackground(String... urls) 
		{
			JSONObject result = null;
			try {
				DefaultHttpClient httpClient = new DefaultHttpClient();
				String getURL = urls[0];
				HttpGet httpGet = new HttpGet(getURL);
				HttpResponse httpResponse = httpClient.execute(httpGet);
				HttpEntity resEntity = httpResponse.getEntity();

				if (resEntity != null) {    
					result = new JSONObject(EntityUtils.toString(resEntity)); 
					Log.d("CPX RESPONSE ---- ", result.toString());				        	
				}
				return result;
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		}

		@Override
		protected void onPostExecute(JSONObject json) {

			try {
				if(json.getBoolean("status")==true) {
					JSONArray jsonArray = json.getJSONArray("data");
					
					for(int i=0;i<jsonArray.length();i++)
					{
						//contentItem = new ContentItem(jsonArray.getJSONObject(i).getString("image"),jsonArray.getJSONObject(i).getString("name"),jsonArray.getJSONObject(i).getString("reward"));
						//listArray.add(contentItem);

					}	
					
				} else {		   
					
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}
	
	
	class ContentItem 
	{
		ContentItem(String aPoint,String aQuestion,String aImage,boolean aSelect,String aAnswer1,String aAnswer2,String aAnswer3,String aAnswer4,String aAnswer5)
		{
			point = aPoint;
			question = aQuestion;
			image = aImage;
			select = aSelect;
			
			answer1 = aAnswer1;
			answer2 = aAnswer2;
			answer3 = aAnswer3;
			answer4 = aAnswer4;
			answer5 = aAnswer5;
		}
		String point;
		String question;
		String image;
		boolean select;
		String answer1;
		String answer2;
		String answer3;
		String answer4;
		String answer5;
	}

	class ListViewAdapter extends BaseAdapter
	{
		Context maincon;
		LayoutInflater Inflater;
		ArrayList<ContentItem> arSrc;
		int layout;

		public ListViewAdapter(Context context,int alayout,ArrayList<ContentItem> aarSrc)
		{
			maincon = context;
			Inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			arSrc = aarSrc;
			layout = alayout;
		}
		public int getCount()
		{
			return 5;//arSrc.size();
		}

		public String getItem(int position)
		{
			return arSrc.get(position).point;
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
			ImageView itemImg = (ImageView)convertView.findViewById(R.id.homedownload_list_item_id_image);
			TextView name1Text = (TextView)convertView.findViewById(R.id.homedownload_list_item_id_name);
			//name1Text.setText(arSrc.get(position).name);

			TextView name2Text = (TextView)convertView.findViewById(R.id.homedownload_list_item_id_coin);
			//name2Text.setText(arSrc.get(position).coin);
			
			try {
				
			} catch (Exception e) {

			} 
			return convertView;
		}
	}

	
}










