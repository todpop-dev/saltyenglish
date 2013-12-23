package com.todpop.saltyenglish;

import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.flurry.android.FlurryAgent;


import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class HomeMyPagePurchasedDetail extends Activity {
	

	TextView productTitle;
	ImageView productImg;
	TextView productName;
	TextView place;
	TextView validate;
	ImageView barcode;
	TextView detail;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_my_page_purchased_detail);
		Intent intent = getIntent();
		int couponId = intent.getIntExtra("couponId", 0);
		productTitle = (TextView)findViewById(R.id.home_my_page_purchased_detail_title);
		productName = (TextView)findViewById(R.id.home_my_page_purchased_detail_product);
		productImg = (ImageView)findViewById(R.id.home_my_page_purchased_detail_product_img);
		place = (TextView)findViewById(R.id.home_my_page_purchased_detail_place);
		validate = (TextView)findViewById(R.id.home_my_page_purchased_detail_validate);
		barcode = (ImageView)findViewById(R.id.home_my_page_purchased_detail_barcode);
		detail = (TextView)findViewById(R.id.home_my_page_purchased_detail_info);
		
		new GetCouponsInfo().execute("http://todpop.co.kr/api/etc/" + couponId + "/get_coupon_free_info.json");
	}
    
    private class GetCouponsInfo extends AsyncTask<String, Void, JSONObject> 
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
				if(json.getBoolean("status")==true){
					
					productTitle.setText(json.getString("name"));
					productName.setText(json.getString("name"));
					place.setText(json.getString("place"));
					validate.setText(json.getString("valid_end"));
					//TODO detail enter
					detail.setText(json.getString("information"));
					
					new DownloadImageTask(productImg).execute("http://todpop.co.kr" + json.getJSONObject("image").getJSONObject("image").getJSONObject("thumb").getString("url"));
					new DownloadImageTask(barcode).execute("http://todpop.co.kr" + json.getString("barcode"));
				}
				else{
					Log.e("STEVEN", "detail set text");
					detail.setText(R.string.get_coupon_error);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private class DownloadImageTask  extends AsyncTask<String, Void, Bitmap> {

			ImageView bmImage;

			public DownloadImageTask(ImageView bmImage) {
				this.bmImage = bmImage;
			}
		    protected Bitmap doInBackground(String... urls) 
		    {
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
		    	bmImage.setImageBitmap(result);
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
		return false;
	}
	@Override
	protected void onStart()
	{
		super.onStart();
		FlurryAgent.onStartSession(this, "ZKWGFP6HKJ33Y69SP5QY");
		FlurryAgent.logEvent("Purchased Detail");
	}
	 
	@Override
	protected void onStop()
	{
		super.onStop();		
		FlurryAgent.onEndSession(this);
	}
}
