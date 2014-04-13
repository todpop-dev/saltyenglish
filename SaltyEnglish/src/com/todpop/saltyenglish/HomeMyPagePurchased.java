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
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class HomeMyPagePurchased extends Activity {
	
	PurchasedListViewAdapter purchasedListViewAdapter;
	ArrayList<CouponListViewItem> couponList;
	ArrayList<CouponListViewItem> ticketList;
	CouponListViewItem mCouponList;
	ListView listView;
	
	RadioButton couponBtn;
	RadioButton ticketBtn;
	
	ImageView noCoupon;
	
	RelativeLayout listItemView;
	
	// declare define popup view
	PopupWindow popupWindow;
	View popupview;
	RelativeLayout relative;
	TextView popupText;
	
	SharedPreferences rgInfo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_my_page_purchased);
		Intent intent = getIntent();
		int state = intent.getIntExtra("state", 0);
		
		rgInfo = getSharedPreferences("rgInfo",0);
		listView = (ListView)findViewById(R.id.home_mypage_purchased_id_list_view);
		noCoupon = (ImageView)findViewById(R.id.home_mypage_purchased_id_no_coupon);
		couponList = new ArrayList<CouponListViewItem>();
		ticketList = new ArrayList<CouponListViewItem>();
		
		couponBtn = (RadioButton)findViewById(R.id.homemypagepurchased_id_btn_coupon);
		ticketBtn = (RadioButton)findViewById(R.id.homemypagepurchased_id_btn_purchased);
		couponBtn.setOnClickListener(radioButton);
		ticketBtn.setOnClickListener(radioButton);
		
		// popupview
		relative = (RelativeLayout) findViewById(R.id.homemypagepurchased_id_main_layout);
		popupview = View.inflate(this, R.layout.popup_view, null);
		popupWindow = new PopupWindow(popupview, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
		popupText = (TextView) popupview.findViewById(R.id.popup_id_text);
		
		if(state == 0){
			new GetCoupons(0).execute("http://todpop.co.kr/api/etc/"+rgInfo.getString("mem_id", "NO")+"/get_purchase_list.json?coupon_type=0");
		}
		else{
			ticketBtn.setChecked(true);
			new GetCoupons(1).execute("http://todpop.co.kr/api/etc/"+rgInfo.getString("mem_id", "NO")+"/get_purchase_list.json?coupon_type=1");
		}
	}
	
	class CouponListViewItem{
		CouponListViewItem(String cId, Boolean cUsed, Boolean cExpired, Boolean cCanceled, String img, String creatTime, String couponName, String usePlace, String cPrice, Bitmap cBitmapImg){
			couponId = cId;
			used = cUsed;
			expired = cExpired;
			canceled = cCanceled;
			image = img;
			created_at = creatTime;
			name = couponName;
			place = usePlace;
			price = cPrice;
			bitmapImg = cBitmapImg;
			tryDownImg = false;
		}
		String couponId;
		Boolean used;
		Boolean expired;
		Boolean canceled;
		String image;
		String created_at;
		String name;
		String place;
		String price;
		Bitmap bitmapImg;
		Boolean tryDownImg;
	}
	
	OnClickListener radioButton = new OnClickListener(){
		public void onClick(View v){
			switch(v.getId()){
			case R.id.homemypagepurchased_id_btn_coupon:
				purchasedListViewAdapter = new PurchasedListViewAdapter(HomeMyPagePurchased.this, R.layout.home_my_page_purchased_list_item_view, couponList);
				listView.setAdapter(purchasedListViewAdapter);
				if(couponList.isEmpty()){
					noCoupon.setVisibility(View.VISIBLE);
					new GetCoupons(0).execute("http://todpop.co.kr/api/etc/"+rgInfo.getString("mem_id", "NO")+"/get_purchase_list.json?coupon_type=0");
				}
				break;
			case R.id.homemypagepurchased_id_btn_purchased:
				purchasedListViewAdapter = new PurchasedListViewAdapter(HomeMyPagePurchased.this, R.layout.home_my_page_purchased_list_item_view, ticketList);
			   	listView.setAdapter(purchasedListViewAdapter);
				if(ticketList.isEmpty()){
					noCoupon.setVisibility(View.VISIBLE);
					new GetCoupons(1).execute("http://todpop.co.kr/api/etc/"+rgInfo.getString("mem_id", "NO")+"/get_purchase_list.json?coupon_type=1");
				}
				break;
			}
		}
	};

    class PurchasedListViewAdapter extends BaseAdapter
    {
    	Context maincon;
    	LayoutInflater Inflater;
    	ArrayList<CouponListViewItem> arSrc;
    	Drawable emptyImg = getResources().getDrawable(R.drawable.store_2_image_goodsimage);
		
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
    		ViewHolder holder;
    		CouponListViewItem item = (CouponListViewItem)arSrc.get(position);
    		final String couponId = item.couponId;
    		final Boolean is_used = item.used;
    		final Boolean is_expired = item.expired;
    		final Boolean is_canceled = item.canceled;
    		
    		if(convertView == null)
    		{
    			convertView = Inflater.inflate(layout, parent,false);
    			holder = new ViewHolder();
    			holder.timeText = (TextView)convertView.findViewById(R.id.home_mypage_purchased_list_item_id_time);
    			holder.name1Text = (TextView)convertView.findViewById(R.id.home_mypage_purchased_list_item_id_name1);
    			holder.name2Text = (TextView)convertView.findViewById(R.id.home_mypage_purchased_list_item_id_name2);
    			holder.coinText = (TextView)convertView.findViewById(R.id.home_mypage_purchased_list_item_id_coins);
        		holder.coinImage = (ImageView)convertView.findViewById(R.id.home_mypage_purchased_list_item_id_coin_image);
        		holder.image = (ImageView)convertView.findViewById(R.id.home_mypage_purchased_list_item_id_item);
        		convertView.setTag(holder);
    		}
    		else{
    			holder = (ViewHolder)convertView.getTag();
    		}
    		
    		holder.timeText.setText(item.created_at);
    		holder.name1Text.setText(item.name);
    		holder.name2Text.setText(item.place);
    		
    		if(couponBtn.isChecked()){
    			holder.coinImage.setVisibility(View.GONE);
        		holder.coinText.setVisibility(View.GONE);
        		
        		//image.setImageResource(arSrc.get(position).imageId);
        		if(holder.image != null){
        			if(item.bitmapImg != null){
        				holder.image.setImageBitmap(item.bitmapImg);
        			}
        			else{
        				holder.image.setImageDrawable(emptyImg);
        				if(!item.tryDownImg){
        					item.tryDownImg = true;
        					new DownloadImageTask(position, 0).execute("http://todpop.co.kr" + item.image);
        				}
        			}
        		}
        	}
    		else{
    			holder.coinText.setText(item.price);
        		if(holder.image != null){
        			if(item.bitmapImg != null){
        				holder.image.setImageBitmap(item.bitmapImg);        				
        			}
        			else{
        				holder.image.setImageDrawable(emptyImg);
        				if(!item.tryDownImg){
        					item.tryDownImg = true;
        					new DownloadImageTask(position, 1).execute(item.image);
                	    }        				
        			}
        		}
        	}

    		convertView.setOnClickListener(new OnClickListener(){
    			@Override
    			public void onClick(View v){
    				if(is_used){
						popupText.setText(R.string.home_my_page_purchased_used);
						popupWindow.showAtLocation(relative, Gravity.CENTER, 0,	0);
    				}
    				else if(is_canceled){
						popupText.setText(R.string.home_my_page_purchased_canceled);
						popupWindow.showAtLocation(relative, Gravity.CENTER, 0,	0);    					
    				}
    				else if(is_expired){
						popupText.setText(R.string.home_my_page_purchased_expired);
						popupWindow.showAtLocation(relative, Gravity.CENTER, 0,	0);    					
    				}
    				else{
	    				Intent intent = new Intent(getApplicationContext(), HomeMyPagePurchasedDetail.class);
	    				intent.putExtra("couponId", couponId);
	    				if(ticketBtn.isChecked())
	    					intent.putExtra("isFree", false);
	    				else
	    					intent.putExtra("isFree", true);
	    				startActivity(intent);
	    				//new GetCouponsInfo().execute("http://todpop.co.kr/api/etc/" + couponId + "/get_coupon_free_info.json");
    				}
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
    
    public static class ViewHolder{
    	TextView timeText;
    	TextView name1Text;
		TextView name2Text;
		TextView coinText;
		ImageView coinImage;
		ImageView image;
    }
    
	public void closePopup(View v) {
		popupWindow.dismiss();
	}
    
	private class GetCoupons extends AsyncTask<String, Void, JSONObject> 
	{
		int type;
		public GetCoupons(int aType){
			type = aType;
		}
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
					Log.d("RESPONSE JSON---- ", result.toString());				        	
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
						if(type == 0){
							for(int i = 0; i < product.length(); i++){
								JSONObject jsonObject = product.getJSONObject(i);
								String time = jsonObject.getString("created_at");
								time = time.substring(5, 16);
								time = time.replace('T', ' ');
								mCouponList = new CouponListViewItem(jsonObject.getString("order_id"), jsonObject.getBoolean("is_used"), jsonObject.getBoolean("is_expired"),
										jsonObject.getBoolean("is_canceled"), jsonObject.getString("image"), time, jsonObject.getString("name"), jsonObject.getString("maker"),
										jsonObject.getString("price"), null);
								couponList.add(mCouponList);
							}
							if(!couponList.isEmpty()){
								noCoupon.setVisibility(View.GONE);
							}
							purchasedListViewAdapter = new PurchasedListViewAdapter(HomeMyPagePurchased.this, R.layout.home_my_page_purchased_list_item_view, couponList);
					    	listView.setAdapter(purchasedListViewAdapter);
						}
						else{
							for(int i = 0; i < product.length(); i++){
								JSONObject jsonObject = product.getJSONObject(i);
								String time = jsonObject.getString("created_at");
								time = time.substring(5, 16);
								time = time.replace('T', ' ');
								mCouponList = new CouponListViewItem(jsonObject.getString("order_id"), jsonObject.getBoolean("is_used"), jsonObject.getBoolean("is_expired"),
										jsonObject.getBoolean("is_canceled"), jsonObject.getString("image"), time, jsonObject.getString("name"), jsonObject.getString("maker"),
										jsonObject.getString("price"), null);
								ticketList.add(mCouponList);
							}
							if(!ticketList.isEmpty()){
								noCoupon.setVisibility(View.GONE);
							}
							purchasedListViewAdapter = new PurchasedListViewAdapter(HomeMyPagePurchased.this, R.layout.home_my_page_purchased_list_item_view, ticketList);
					    	listView.setAdapter(purchasedListViewAdapter);							
						}
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
		int position;
		int type;

		public DownloadImageTask(int aPosition, int aType) {
			position = aPosition;
			type = aType;
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
			if(type == 0)
				couponList.get(position).bitmapImg = result;
			else
				ticketList.get(position).bitmapImg = result;
			
			purchasedListViewAdapter.notifyDataSetChanged();
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
