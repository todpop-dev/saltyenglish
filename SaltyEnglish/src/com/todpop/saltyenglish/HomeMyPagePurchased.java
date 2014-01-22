package com.todpop.saltyenglish;

import java.io.InputStream;
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
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class HomeMyPagePurchased extends Activity {
	
	PurchasedListViewAdapter purchasedListViewAdapter;
	ArrayList<CouponListViewItem> couponList;
	CouponListViewItem mCouponList;
	ListView listView;
	
	RadioButton couponBtn;
	RadioButton ticketBtn;
	
	RelativeLayout listItemView;
	SharedPreferences rgInfo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_my_page_purchased);
		Intent intent = getIntent();
		int state = intent.getIntExtra("state", 0);
		
		rgInfo = getSharedPreferences("rgInfo",0);
		listView = (ListView)findViewById(R.id.home_mypage_purchased_id_list_view);
		couponList = new ArrayList<CouponListViewItem>();
		
		couponBtn = (RadioButton)findViewById(R.id.homemypagepurchased_id_btn_coupon);
		ticketBtn = (RadioButton)findViewById(R.id.homemypagepurchased_id_btn_purchased);
		couponBtn.setOnClickListener(radioButton);
		ticketBtn.setOnClickListener(radioButton);
		if(state == 0){
			new GetCoupons().execute("http://todpop.co.kr/api/etc/"+rgInfo.getString("mem_id", "NO")+"/get_purchase_list.json?coupon_type=0");
		}
		else{
			ticketBtn.setChecked(true);
			new GetCoupons().execute("http://todpop.co.kr/api/etc/"+rgInfo.getString("mem_id", "NO")+"/get_purchase_list.json?coupon_type=1");
		}
	}
	
	class CouponListViewItem{
		CouponListViewItem(String cId, int avail, String img, String creatTime, String couponName, String usePlace, String cPrice){
			couponId = cId;
			availability = avail;
			image = img;
			created_at = creatTime;
			name = couponName;
			place = usePlace;
			price = cPrice;
		}
		String couponId;
		int availability;
		String image;
		String created_at;
		String name;
		String place;
		String price;
	}
	
	OnClickListener radioButton = new OnClickListener(){
		public void onClick(View v){
			switch(v.getId()){
			case R.id.homemypagepurchased_id_btn_coupon:
				couponList.clear();
				new GetCoupons().execute("http://todpop.co.kr/api/etc/"+rgInfo.getString("mem_id", "NO")+"/get_purchase_list.json?coupon_type=0");
				break;
			case R.id.homemypagepurchased_id_btn_purchased:
				couponList.clear();
				new GetCoupons().execute("http://todpop.co.kr/api/etc/"+rgInfo.getString("mem_id", "NO")+"/get_purchase_list.json?coupon_type=1");
				break;
			}
		}
	};

    class PurchasedListViewAdapter extends BaseAdapter
    {
    	Context maincon;
    	LayoutInflater Inflater;
    	ArrayList<CouponListViewItem> arSrc;
    	int layout;

    	public PurchasedListViewAdapter(Context context,int alayout,ArrayList<CouponListViewItem> aarSrc)
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
    		return arSrc.get(position).couponId;
    	}

    	public long getItemId(int position)
    	{
    		return position;
    	}

    	public View getView(int position,View convertView,ViewGroup parent)
    	{
    		final String couponId = arSrc.get(position).couponId;
    		if(convertView == null)
    		{
    			convertView = Inflater.inflate(layout, parent,false);
    		}
    		TextView timeText = (TextView)convertView.findViewById(R.id.home_mypage_purchased_list_item_id_time);
    		timeText.setText(arSrc.get(position).created_at);

    		TextView name1Text = (TextView)convertView.findViewById(R.id.home_mypage_purchased_list_item_id_name1);
    		name1Text.setText(arSrc.get(position).name);

    		TextView name2Text = (TextView)convertView.findViewById(R.id.home_mypage_purchased_list_item_id_name2);
    		name2Text.setText(arSrc.get(position).place);
    		
    		TextView coinText = (TextView)convertView.findViewById(R.id.home_mypage_purchased_list_item_id_coins);
    		
    		ImageView coinImage = (ImageView)convertView.findViewById(R.id.home_mypage_purchased_list_item_id_coin_image);

    		ImageView image = (ImageView)convertView.findViewById(R.id.home_mypage_purchased_list_item_id_item);
    		
    		if(couponBtn.isChecked()){
    			coinImage.setVisibility(View.GONE);
        		coinText.setVisibility(View.GONE);
        		
        		//image.setImageResource(arSrc.get(position).imageId);
        		
        		new DownloadImageTask(image).execute("http://todpop.co.kr" + arSrc.get(position).image);
    		}
    		else{
    			coinText.setText(arSrc.get(position).price);
        		new DownloadImageTask(image).execute(arSrc.get(position).image);
    		}

    		convertView.setOnClickListener(new OnClickListener(){
    			@Override
    			public void onClick(View v){
    				Intent intent = new Intent(getApplicationContext(), HomeMyPagePurchasedDetail.class);
    				intent.putExtra("couponId", couponId);
    				if(ticketBtn.isChecked())
    					intent.putExtra("isFree", false);
    				else
    					intent.putExtra("isFree", true);
    				startActivity(intent);
    				//new GetCouponsInfo().execute("http://todpop.co.kr/api/etc/" + couponId + "/get_coupon_free_info.json");
    			}
    		});
    		
    		if (position%2 == 1) {
    			convertView.setBackgroundResource(R.drawable.store_32_image_separatebox_white);
    		} else {
    			convertView.setBackgroundResource(R.drawable.store_32_image_separatebox_pink);
    		}
    		
    		return convertView;
    	}
    }
    
    private class GetCoupons extends AsyncTask<String, Void, JSONObject> 
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
				if(json.getBoolean("status")==true){
					if(json.getJSONObject("data").getJSONArray("product").length() > 0){
						JSONArray product = json.getJSONObject("data").getJSONArray("product");
						for(int i = 0; i < product.length(); i++){
							JSONObject jsonObject = product.getJSONObject(i);
							String time = jsonObject.getString("created_at");
							time = time.substring(5, 16);
							time = time.replace('T', ' ');
							mCouponList = new CouponListViewItem(jsonObject.getString("coupon_id"), jsonObject.getInt("availability"), 
									jsonObject.getString("image"), time, jsonObject.getString("name"), jsonObject.getString("place"), jsonObject.getString("price"));
							couponList.add(mCouponList);
						}
						purchasedListViewAdapter = new PurchasedListViewAdapter(HomeMyPagePurchased.this, R.layout.home_my_page_purchased_list_item_view, couponList);
				    	listView.setAdapter(purchasedListViewAdapter);
					}
				}
				else{
					//NO Coupon
				}
			} catch (Exception e) {
				e.printStackTrace();
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
	@Override
	protected void onStart()
	{
		super.onStart();
		FlurryAgent.onStartSession(this, "ZKWGFP6HKJ33Y69SP5QY");
		FlurryAgent.logEvent("Purchased List");
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
