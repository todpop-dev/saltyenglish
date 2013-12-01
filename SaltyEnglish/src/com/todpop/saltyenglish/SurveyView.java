package com.todpop.saltyenglish;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
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
		listView.setDividerHeight(0);
		listArray = new ArrayList<ContentItem>();
		
		
		
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
						contentItem = new ContentItem(jsonArray.getJSONObject(i).getString("q_no"),
								       			      jsonArray.getJSONObject(i).getString("q_type"),
								       			      jsonArray.getJSONObject(i).getString("q_text"),
								       			      jsonArray.getJSONObject(i).getString("q_image"),
								       			      jsonArray.getJSONObject(i).getString("n_answer"),
								       			      jsonArray.getJSONObject(i).getString("a1"),
								       			      jsonArray.getJSONObject(i).getString("a2"),
								       			      jsonArray.getJSONObject(i).getString("a3"),
								       			      jsonArray.getJSONObject(i).getString("a4"),
								       			      jsonArray.getJSONObject(i).getString("a5"));
						listArray.add(contentItem);

					}	
					listViewAdapter = new ListViewAdapter(SurveyView.this, listArray);
					listView.setAdapter(listViewAdapter);
					
				} else {		   
					
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}
	
	
	class ContentItem 
	{
		ContentItem(String aPoint,String aType, String aQuestion,String aImage,String aAnswer,String aAnswer1,String aAnswer2,String aAnswer3,String aAnswer4,String aAnswer5)
		{
			point = aPoint;
			type = aType;
			question = aQuestion;
			image = aImage;
			answer = aAnswer;
			
			answer1 = aAnswer1;
			answer2 = aAnswer2;
			answer3 = aAnswer3;
			answer4 = aAnswer4;
			answer5 = aAnswer5;
		}
		String point;
		String type;
		String question;
		String image;
		String answer;
		
		String answer1;
		String answer2;
		String answer3;
		String answer4;
		String answer5;
	}

	class ListViewAdapter extends BaseAdapter
	{
		private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
			ImageView surveyImage;
			
			public DownloadImageTask(ImageView imageView) 
			{      
				surveyImage = imageView;
			}
			
		    protected Bitmap doInBackground(String... urls) {
		        String urldisplay = urls[0];
		        Bitmap mIcon11 = null;
		        try {
		            InputStream in = new java.net.URL(urldisplay).openStream();
		            mIcon11 = BitmapFactory.decodeStream(in);
		        } catch (Exception e) {
		            Log.e("Error", e.getMessage());
		            e.printStackTrace();
		        }
		        return mIcon11;
		    }

		    protected void onPostExecute(Bitmap result) 
		    {	        
		    	// Update UI
		    	surveyImage.setImageBitmap(result);
		    }
		}
		
		Context maincon;
		LayoutInflater Inflater;
		ArrayList<ContentItem> arSrc;
		int layout0 = R.layout.survey_view_list_item_q_type_0;
		int layout1_2 = R.layout.survey_view_list_item_q_type_1_2;
		int layout3_4 = R.layout.survey_view_list_item_q_type_3_4;
		int layout5 = R.layout.survey_view_list_item_q_type_5;

		public ListViewAdapter(Context context,ArrayList<ContentItem> aarSrc)
		{
			maincon = context;
			Inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			arSrc = aarSrc;
		}
		public int getCount()
		{
			return arSrc.size()+1;
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
			if(arSrc.size()==position)
			{
				convertView = Inflater.inflate(layout5, parent,false);
				
				return convertView;
			}
			
			if(arSrc.get(position).type.equals("0")) {
				convertView = Inflater.inflate(layout0, parent,false);
				TextView topText = (TextView)convertView.findViewById(R.id.survey_id_top_text);
				topText.setText(arSrc.get(position).question);
				return convertView;
			}else if(arSrc.get(position).type.equals("1")||arSrc.get(position).type.equals("2")){
				convertView = Inflater.inflate(layout1_2, parent,false);
				TextView point = (TextView)convertView.findViewById(R.id.survey_id_point);
				point.setText(arSrc.get(position).point);
				TextView question = (TextView)convertView.findViewById(R.id.survey_id_question);
				question.setText(arSrc.get(position).question);
				
				
				if(!arSrc.get(position).image.equals("null")){
					Log.d("!!!!!!!!!!!!!!!!!!", arSrc.get(position).image);
					ImageView image = (ImageView)convertView.findViewById(R.id.survey_id_image);
					new DownloadImageTask(image).execute("http://todpop.co.kr"+arSrc.get(position).image);
				}
				
				RadioButton btn1 = (RadioButton)convertView.findViewById(R.id.survey_id_btn_1);
				RadioButton btn2 = (RadioButton)convertView.findViewById(R.id.survey_id_btn_2);
				RadioButton btn3 = (RadioButton)convertView.findViewById(R.id.survey_id_btn_3);
				RadioButton btn4 = (RadioButton)convertView.findViewById(R.id.survey_id_btn_4);
				RadioButton btn5 = (RadioButton)convertView.findViewById(R.id.survey_id_btn_5);
				
				if(!arSrc.get(position).answer1.equals("")){
					btn1.setText(arSrc.get(position).answer1);
				}else{
					btn1.setVisibility(View.GONE);
				}
				if(!arSrc.get(position).answer2.equals("")){
					btn2.setText(arSrc.get(position).answer2);
				}else{
					btn2.setVisibility(View.GONE);
				}
				if(!arSrc.get(position).answer3.equals("")){
					btn3.setText(arSrc.get(position).answer3);
				}else{
					btn3.setVisibility(View.GONE);
				}
				if(!arSrc.get(position).answer4.equals("")){
					btn4.setText(arSrc.get(position).answer4);
				}else{
					btn4.setVisibility(View.GONE);
				}
				if(!arSrc.get(position).answer5.equals("")){
					btn5.setText(arSrc.get(position).answer5);
				}else{
					btn5.setVisibility(View.GONE);
				}
				
				return convertView;
				
			}else {
				convertView = Inflater.inflate(layout3_4, parent,false);
				
				TextView point = (TextView)convertView.findViewById(R.id.survey_id_point);
				point.setText(arSrc.get(position).point);
				TextView question = (TextView)convertView.findViewById(R.id.survey_id_question);
				question.setText(arSrc.get(position).question);
				
				
				if(!arSrc.get(position).image.equals("null")){
					Log.d("!!!!!!!!!!!!!!!!!!", arSrc.get(position).image);
					ImageView image = (ImageView)convertView.findViewById(R.id.survey_id_image);
					new DownloadImageTask(image).execute("http://todpop.co.kr"+arSrc.get(position).image);
				}
				
				RadioButton btn1 = (RadioButton)convertView.findViewById(R.id.survey_id_btn_1);
				RadioButton btn2 = (RadioButton)convertView.findViewById(R.id.survey_id_btn_2);
				RadioButton btn3 = (RadioButton)convertView.findViewById(R.id.survey_id_btn_3);
				RadioButton btn4 = (RadioButton)convertView.findViewById(R.id.survey_id_btn_4);
				RadioButton btn5 = (RadioButton)convertView.findViewById(R.id.survey_id_btn_5);
				
				if(!arSrc.get(position).answer1.equals("")){
					btn1.setText(arSrc.get(position).answer1);
				}else{
					btn1.setVisibility(View.GONE);
				}
				if(!arSrc.get(position).answer2.equals("")){
					btn2.setText(arSrc.get(position).answer2);
				}else{
					btn2.setVisibility(View.GONE);
				}
				if(!arSrc.get(position).answer3.equals("")){
					btn3.setText(arSrc.get(position).answer3);
				}else{
					btn3.setVisibility(View.GONE);
				}
				if(!arSrc.get(position).answer4.equals("")){
					btn4.setText(arSrc.get(position).answer4);
				}else{
					btn4.setVisibility(View.GONE);
				}
				if(!arSrc.get(position).answer5.equals("")){
					btn5.setText(arSrc.get(position).answer5);
				}else{
					btn5.setVisibility(View.GONE);
				}
				return convertView;
			}
		}
	}

	
}










