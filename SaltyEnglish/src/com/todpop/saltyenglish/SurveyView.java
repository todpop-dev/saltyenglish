package com.todpop.saltyenglish;

import java.net.URL;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_survey_view);
		
		listView = (ListView)findViewById(R.id.survey_id_list_view);
		listArray = new ArrayList<ContentItem>();
		
		// Send act 1
	}
	
	// on click
	public void onClickBack(View view)
	{
		finish();
	}
	
	private class GetCPSiInfo extends AsyncTask<String, Void, JSONObject> 
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
					JSONObject adDetails = json.getJSONObject("data");
					int adId = adDetails.getInt("ad_id");
					int adType = adDetails.getInt("ad_type");
					Log.d("CPX Type: ---------- ", Integer.toString(adType));
					
					String adImageUrl = "http://todpop.co.kr/" + adDetails.getString("ad_image");
					String adText = adDetails.getString("ad_text");
					String targetUrl = adDetails.getString("target_url");
					String packageName = adDetails.getString("package_name");
					String confirmUrl = adDetails.getString("confirm_url");
					int reward = adDetails.getInt("reward");
					int questionCount = adDetails.getInt("n_question");

					SharedPreferences cpxInfo = getSharedPreferences("cpxInfo",0);
					SharedPreferences.Editor cpxInfoEditor = cpxInfo.edit();
					cpxInfoEditor.putInt("adId", adId);					
					cpxInfoEditor.putInt("adType", adType);		
					cpxInfoEditor.putString("adImageUrl", adImageUrl);
					cpxInfoEditor.putString("adText", adText);
					cpxInfoEditor.putString("targetUrl", targetUrl);
					cpxInfoEditor.putString("packageName", packageName);
					cpxInfoEditor.putString("confirmUrl", confirmUrl);
					cpxInfoEditor.putInt("reward", reward);
					cpxInfoEditor.putInt("questionCount", questionCount);
					
					cpxInfoEditor.commit();
					
					// TODO: Add more CPX Support. Now only support CPI and CPS

						Intent intent = new Intent(getApplicationContext(), StudyHome.class);
						startActivity(intent);
						finish();
					
				} else {		   
					// In the case CPX Request Failed
					Intent intent = new Intent(getApplicationContext(), StudyHome.class);
					startActivity(intent);
					finish();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}
	
	
	class ContentItem 
	{
		ContentItem(String aPoint,String aQuestion,String aImage,boolean aSelect)
		{
			point = aPoint;
			question = aQuestion;
			image = aImage;
			select = aSelect;
		}
		String point;
		String question;
		String image;
		boolean select;
	}

	class CpiListViewAdapter extends BaseAdapter
	{
		Context maincon;
		LayoutInflater Inflater;
		ArrayList<ContentItem> arSrc;
		int layout;

		public CpiListViewAdapter(Context context,int alayout,ArrayList<ContentItem> aarSrc)
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
			name1Text.setText(arSrc.get(position).name);

			TextView name2Text = (TextView)convertView.findViewById(R.id.homedownload_list_item_id_coin);
			name2Text.setText(arSrc.get(position).coin);
			
			try {
				
			} catch (Exception e) {

			} 
			return convertView;
		}
	}

	
}










