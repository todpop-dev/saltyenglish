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


import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class HomeDownload extends Activity {

	CpiListViewAdapter cpiListViewAdapter;
	ArrayList<CpiListViewItem> cpiArray;
	CpiListViewItem mCpiListItem;
	ListView cpiListView;
	int cpiCount = 0;

	CouponListViewAdapter couponListViewAdapter;
	ArrayList<CouponListViewItem> couponArray;
	CouponListViewItem mCouponListViewItem;
	ListView couponListView;
	int couponCount = 0;

	ImageView noCPIimage;
	RadioButton cpiBtn;
	RadioButton couponBtn;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_download);
//		noCPIimage = (ImageView)findViewById(R.id.homedownloal_id_nopci);
//		cpiBtn = (RadioButton) findViewById(R.id.homedownload_id_btn_cpi);
//		couponBtn = (RadioButton) findViewById(R.id.homedownload_id_btn_coupon);
//		cpiBtn.setOnClickListener(radio_listener);
//		couponBtn.setOnClickListener(radio_listener);
//
//		cpiArray = new ArrayList<CpiListViewItem>();
//		cpiListView=(ListView)findViewById(R.id.homedownload_id_listiew_cpi);
//
//		couponArray = new ArrayList<CouponListViewItem>();
//		couponListView=(ListView)findViewById(R.id.homedownload_id_listiew_coupon);
//		for(int i=0;i<20;i++) {
//			mCouponListViewItem = new CouponListViewItem(R.drawable.store_33_image_dinosaur_on,"seoga & cook 20% sale");
//			couponArray.add(mCouponListViewItem);
//		}
//		couponListViewAdapter = new CouponListViewAdapter(this,R.layout.home_download_list_item_coupon, couponArray);
//		couponListView.setAdapter(couponListViewAdapter);
//
//		SharedPreferences pref = getSharedPreferences("setting",0);
//
//		new GetCPI().execute("http://todpop.co.kr/api/etc/1/show_cpx_list.json&user_id="+pref.getString("id", "N"));
	}


//	OnClickListener radio_listener = new OnClickListener (){
//		public void onClick(View v) {
//			switch(v.getId())
//			{
//			case R.id.homedownload_id_btn_cpi:
//				cpiListView.setVisibility(RelativeLayout.VISIBLE);
//				couponListView.setVisibility(RelativeLayout.GONE);
//				break;
//			case R.id.homedownload_id_btn_coupon:
//				cpiListView.setVisibility(RelativeLayout.GONE);
//				couponListView.setVisibility(RelativeLayout.VISIBLE);
//				break;
//			}
//		}
//	};

	class CpiListViewItem 
	{
		CpiListViewItem(String aImage,String aName,String aCoin)
		{
			image = aImage;
			name = aName;
			coin = aCoin;
		}
		String image;
		String name;
		String coin;
	}

	class CpiListViewAdapter extends BaseAdapter
	{
		Context maincon;
		LayoutInflater Inflater;
		ArrayList<CpiListViewItem> arSrc;
		int layout;

		public CpiListViewAdapter(Context context,int alayout,ArrayList<CpiListViewItem> aarSrc)
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
			return arSrc.get(position).name;
		}

		public long getItemId(int position)
		{
			return position;
		}

		public View getView(int position,View convertView,ViewGroup parent)
		{
			cpiCount++;
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
				// show The Image
				String imgUrl = arSrc.get(position).image;
				URL url = new URL(imgUrl);
				Log.d("url ------ ", url.toString());
				new DownloadImageTask(itemImg)
				.execute(url.toString());
			} catch (Exception e) {

			} 

			if (cpiCount%2 == 1) {
				convertView.setBackgroundResource(R.drawable.store_2_image_separatebox_white);
			} else {
				convertView.setBackgroundResource(R.drawable.store_2_image_separatebox_yellow);
			}
			return convertView;
		}
	}

	class CouponListViewItem 
	{
		CouponListViewItem(int aItem,String aName)
		{
			item = aItem;
			name = aName;
		}
		int item;
		String name;
	}

	class CouponListViewAdapter extends BaseAdapter
	{
		Context maincon;
		LayoutInflater Inflater;
		ArrayList<CouponListViewItem> arSrc;
		int layout;

		public CouponListViewAdapter(Context context,int alayout,ArrayList<CouponListViewItem> aarSrc)
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
			return arSrc.get(position).name;
		}

		public long getItemId(int position)
		{
			return position;
		}

		public View getView(int position,View convertView,ViewGroup parent)
		{
			couponCount++;
			if(convertView == null)
			{
				convertView = Inflater.inflate(layout, parent,false);
			}
			ImageView itemImg = (ImageView)convertView.findViewById(R.id.homedownload_list_item_coupon_id_image);
			itemImg.setImageResource(arSrc.get(position).item);

			TextView name1Text = (TextView)convertView.findViewById(R.id.homedownload_list_item_coupon_id_name);
			name1Text.setText(arSrc.get(position).name);
			
			if (couponCount%2 == 1) {
				convertView.setBackgroundResource(R.drawable.store_2_image_separatebox_white);
			} else {
				convertView.setBackgroundResource(R.drawable.store_2_image_separatebox_yellow);
			}
			return convertView;
		}
	}

	// request 
	private class GetCPI extends AsyncTask<String, Void, JSONObject> 
	{
		@Override
		protected JSONObject doInBackground(String... urls) 
		{

			JSONObject result = null;
			try
			{
				DefaultHttpClient httpClient = new DefaultHttpClient();
				String getURL = urls[0];
				HttpGet httpGet = new HttpGet(getURL);
				HttpResponse httpResponse = httpClient.execute(httpGet);
				HttpEntity resEntity = httpResponse.getEntity();

				if (resEntity != null)
				{    
					result = new JSONObject(EntityUtils.toString(resEntity)); 
					Log.d("RESPONSE ---- ", result.toString());				        	
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
		protected void onPostExecute(JSONObject result) 
		{
			try {
				if	(result.getBoolean("status")==true)
				{
					if(result.getString("msg").equals("not exist log"))
					{
						noCPIimage.setVisibility(View.VISIBLE);
					}else{
						noCPIimage.setVisibility(View.GONE);
					}
					JSONArray jsonArray = result.getJSONArray("data");
					for(int i=0;i<jsonArray.length();i++)
					{
						mCpiListItem = new CpiListViewItem(jsonArray.getJSONObject(i).getString("image"),jsonArray.getJSONObject(i).getString("name"),jsonArray.getJSONObject(i).getString("reward"));
						cpiArray.add(mCpiListItem);

					}	

					cpiListViewAdapter = new CpiListViewAdapter(HomeDownload.this,R.layout.home_download_list_item_cpi, cpiArray);
					cpiListView.setAdapter(cpiListViewAdapter);

				}
			} catch (Exception e) {

			}
		}
	}

	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
		ImageView bmImage;

		public DownloadImageTask(ImageView bmImage) {
			this.bmImage = bmImage;
		}
		protected Bitmap doInBackground(String... urls) {
			String urldisplay = urls[0];
			Bitmap mIcon11 = null;
			try {
				InputStream in = new java.net.URL(urldisplay).openStream();
				mIcon11 = BitmapFactory.decodeStream(in);
				//Bitmap.createScaledBitmap(mIcon11, int dstWidth, int dstHeight, false);
			} catch (Exception e) {
				Log.e("Error", e.getMessage());
				e.printStackTrace();
			}
			return mIcon11;
		}

		protected void onPostExecute(Bitmap result) {
			bmImage.setImageBitmap(result);
		}
	}

	// on click
	public void onClickBack(View view)
	{
		finish();
	}
	
	public void SavingCPI(View v)
	{
		Button savingBtin = (Button)v;
		savingBtin.setEnabled(false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home_download, menu);
		return true;
	}

}
