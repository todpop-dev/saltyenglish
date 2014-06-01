package com.todpop.saltyenglish;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.EnumMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.todpop.api.LoadingDialog;
import com.todpop.api.TypefaceActivity;

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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class HomeMyPagePurchasedDetail extends TypefaceActivity {

	LinearLayout productLayout;
	
	TextView productTitle;
	ImageView productImg;
	ImageView makerImg;
	TextView makerText;
	TextView productName;
	TextView place;
	TextView validate;
	RelativeLayout confirmLayout;
	TextView confirmNumber;
	RelativeLayout barcodeLayout;
	ImageView barcode_image;
	TextView barcode_text;
	RelativeLayout qpconImg;

	// loading progress dialog
	LoadingDialog loadingDialog;
	
	//for homeplus product
	LinearLayout homeplusLayout;
	
	TextView hpTopTitle;
	TextView hpValue;
	TextView hpMidTitle;
	ImageView hpBarcodeImg;
	TextView hpBarcodeText;
	TextView hpBotTitle;
	TextView hpValidate;
	TextView hpPlace;
	
	//common
	TextView detail;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_my_page_purchased_detail);
		Intent intent = getIntent();
		String couponId = intent.getStringExtra("couponId");
		Boolean isFree = intent.getBooleanExtra("isFree", true);
		productLayout = (LinearLayout) findViewById(R.id.activity_home_my_page_purchased_detail_upper_pink_box);
		productTitle = (TextView) findViewById(R.id.home_my_page_purchased_detail_title);
		productName = (TextView) findViewById(R.id.home_my_page_purchased_detail_product);
		productImg = (ImageView) findViewById(R.id.home_my_page_purchased_detail_product_img);
		makerImg = (ImageView)findViewById(R.id.home_my_page_purchased_detail_maker_img);
		makerText = (TextView)findViewById(R.id.home_my_page_purchased_detail_maker);
		place = (TextView) findViewById(R.id.home_my_page_purchased_detail_place);
		validate = (TextView) findViewById(R.id.home_my_page_purchased_detail_validate);
		confirmLayout = (RelativeLayout)findViewById(R.id.home_my_page_purchased_detail_confirm_layout);
		confirmNumber = (TextView)findViewById(R.id.home_my_page_purchased_detail_confirm_number);
		barcodeLayout = (RelativeLayout) findViewById(R.id.home_my_page_purchased_detail_barcode_layout);
		barcode_image = (ImageView) findViewById(R.id.home_my_page_purchased_detail_barcode_image);
		barcode_text = (TextView) findViewById(R.id.home_my_page_purchased_detail_barcode_text);
		qpconImg = (RelativeLayout)findViewById(R.id.home_my_page_purchased_detail_qpcon_layout);
		
		//homeplus
		homeplusLayout = (LinearLayout) findViewById(R.id.activity_home_my_page_purchased_detail_homeplus_upper_pink_box);
		hpTopTitle = (TextView)findViewById(R.id.home_my_page_purchased_detail_homeplus_title_top);
		hpValue = (TextView)findViewById(R.id.home_my_page_purchased_detail_homeplus_value);
		hpMidTitle = (TextView)findViewById(R.id.home_my_page_purchased_detail_homeplus_title_middle);
		hpBarcodeText = (TextView)findViewById(R.id.home_my_page_purchased_detail_homeplus_barcode_text);
		hpBarcodeImg = (ImageView)findViewById(R.id.home_my_page_purchased_detail_homeplus_barcode_image);
		hpBotTitle = (TextView)findViewById(R.id.home_my_page_purchased_detail_homeplus_title_bottom);
		hpValidate = (TextView)findViewById(R.id.home_my_page_purchased_detail_homeplus_validate);
		hpPlace = (TextView)findViewById(R.id.home_my_page_purchased_detail_homeplus_place);

		// loading dialog
		loadingDialog = new LoadingDialog(this);
		
		//common
		detail = (TextView) findViewById(R.id.home_my_page_purchased_detail_info);

		if(isFree){
			new GetCouponsInfo().execute("http://todpop.co.kr/api/etc/" + couponId
					+ "/get_coupon_free_info.json");
		}
		else{
			qpconImg.setVisibility(View.VISIBLE);
			new GetQPConInfo().execute("http://todpop.co.kr/api/etc/get_qpcon_info.json?order_id=" + couponId);
		}
	}

	private class GetCouponsInfo extends AsyncTask<String, Void, JSONObject> {
		@Override
		protected void onPreExecute(){
			super.onPreExecute();
			loadingDialog.show();
		}
		@Override
		protected JSONObject doInBackground(String... urls) {
			JSONObject result = null;
			try {
				String getURL = urls[0];
				HttpGet httpGet = new HttpGet(getURL);
				HttpParams httpParameters = new BasicHttpParams();

				int timeoutConnection = 3000; 
				HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection); 
				int timeoutSocket = 5000; 
				HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket); 

				DefaultHttpClient httpClient = new DefaultHttpClient(
						httpParameters);
				HttpResponse response = httpClient.execute(httpGet);
				HttpEntity resEntity = response.getEntity();

				if (resEntity != null) {
					result = new JSONObject(EntityUtils.toString(resEntity));
					Log.d("RESPONSE JSON ---- ", result.toString());
				}
				return result;
			} catch (Exception e) {
				Log.e("STEVEN", e.toString());
				return result;
			}
		}

		@Override
		protected void onPostExecute(JSONObject json) {

			try {
				if (json.getBoolean("status") == true) {

					productTitle.setText(json.getString("name"));
					productName.setText(json.getString("name"));
					place.setText(json.getString("place"));
					validate.setText(json.getString("valid_end"));
					// TODO detail enter
					if(json.getString("information").equals("null"))
						detail.setText(getResources().getString(R.string.home_purchased_detail_info_null));
					else{
						String cleanedInfo;
						cleanedInfo = json.getString("information")
								.replace("<ul>", "")
								.replace("<li>", "")
								.replace("</ul>", "")
								.replace("</li>", "");
						detail.setText(cleanedInfo);
					}

					new DownloadImageTask(productImg)
							.execute("http://todpop.co.kr"
									+ json.getJSONObject("image")
											.getJSONObject("image")
											.getJSONObject("thumb")
											.getString("url"));
					if(!json.getString("bar_code").equals("null")){
						barcodeLayout.setVisibility(View.VISIBLE);
						barcode_image.setImageBitmap(encodeAsBitmap(
								json.getString("bar_code"), BarcodeFormat.CODE_128,
								barcode_image.getWidth(), barcode_image.getHeight()));
						barcode_text.setText(json.getString("bar_code"));
					}
					if(!json.getString("maker_logo_url").equals("null")){
						new DownloadImageTask(makerImg)
								.execute(json.getString("maer_logo_url"));
					}
					if(!json.getString("maker").equals("null")){
						makerText.setText(json.getString("maker"));
					}
					if(!json.getString("admit_id").equals("null")){
						confirmNumber.setText(json.getString("admit_id"));
					}
				} else {
					loadingDialog.dissmiss();
					detail.setText(R.string.get_coupon_error);
				}
			} catch (Exception e) {
				loadingDialog.dissmiss();
				e.printStackTrace();
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
				} catch (Exception e) {
					Log.e("Error", e.getMessage());
					e.printStackTrace();
				}
				return mIcon11;
			}

			protected void onPostExecute(Bitmap result) {
				bmImage.setImageBitmap(result);
				loadingDialog.dissmiss();
			}
		}
	}
	private class GetQPConInfo extends AsyncTask<String, Void, JSONObject> {
		@Override
		protected void onPreExecute(){
			super.onPreExecute();
			loadingDialog.show();
		}
		@Override
		protected JSONObject doInBackground(String... urls) {
			JSONObject result = null;
			try {
				String getURL = urls[0];
				HttpGet httpGet = new HttpGet(getURL);
				HttpParams httpParameters = new BasicHttpParams();

				int timeoutConnection = 3000; 
				HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection); 
				int timeoutSocket = 5000; 
				HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket); 
				
				DefaultHttpClient httpClient = new DefaultHttpClient(
						httpParameters);
				HttpResponse response = httpClient.execute(httpGet);
				HttpEntity resEntity = response.getEntity();

				if (resEntity != null) {
					result = new JSONObject(EntityUtils.toString(resEntity));
					Log.d("RESPONSE JSON ---- ", result.toString());
				}
				return result;
			} catch (Exception e) {
				Log.e("STEVEN", e.toString());
				return result;
			}
		}

		@Override
		protected void onPostExecute(JSONObject json) {

			try {
				if (json.getBoolean("status") == true) {
				
					if(json.getString("category_id").equals("M10255")){
						productLayout.setVisibility(View.GONE);
						homeplusLayout.setVisibility(View.VISIBLE);
						
						hpTopTitle.setText(json.getString("name"));
						hpMidTitle.setText(json.getString("name"));
						hpBotTitle.setText(json.getString("name"));
						
						String price;
						DecimalFormat df = new DecimalFormat("#,###");
						price = df.format(json.getInt("price")) + getResources().getString(R.string.testname8);
						hpValue.setText(price);
						
						String date;
						date = json.getString("valid_end").replace('-', '.') + getResources().getString(R.string.testname9);
						hpValidate.setText(date);
						hpPlace.setText(json.getString("place"));

						if(json.getString("information").equals("null"))
							detail.setText(getResources().getString(R.string.home_purchased_detail_info_null));
						else{
							String cleanedInfo;
							cleanedInfo = json.getString("information")
									.replace("<ul>", "")
									.replace("<li>", "")
									.replace("</ul>", "")
									.replace("</li>", "");
							detail.setText(cleanedInfo);
						}
						
						if(!json.getString("bar_code").equals("null")){
							hpBarcodeImg.setImageBitmap(encodeAsBitmap(
									json.getString("bar_code"), BarcodeFormat.CODE_128,
									barcode_image.getWidth(), barcode_image.getHeight()));
							hpBarcodeText.setText(json.getString("bar_code"));
						}

						loadingDialog.dissmiss();
					}
					else{
						productTitle.setText(json.getString("name"));
						productName.setText(json.getString("name"));
						place.setText(json.getString("place"));
						validate.setText(json.getString("valid_end"));
						// TODO detail enter
						if(json.getString("information").equals("null"))
							detail.setText(getResources().getString(R.string.home_purchased_detail_info_null));
						else{
							String cleanedInfo;
							cleanedInfo = json.getString("information")
									.replace("<ul>", "")
									.replace("<li>", "")
									.replace("</ul>", "")
									.replace("</li>", "");
							detail.setText(cleanedInfo);
						}
	
						new DownloadImageTask(productImg)
								.execute(json.getString("image"));
						if(!json.getString("bar_code").equals("null")){
							barcodeLayout.setVisibility(View.VISIBLE);
							barcode_image.setImageBitmap(encodeAsBitmap(
									json.getString("bar_code"), BarcodeFormat.CODE_128,
									barcode_image.getWidth(), barcode_image.getHeight()));
							barcode_text.setText(json.getString("bar_code"));
						}
						if(!json.getString("maker_logo_url").equals("null")){
							new DownloadImageTask(makerImg)
									.execute(json.getString("maker_logo_url"));
						}
						if(!json.getString("maker").equals("null")){
							makerText.setText(json.getString("maker"));
						}
						/*if(!json.getString("admit_id").equals("null")){
							confirmNumber.setText(json.getString("admit_id"));
						}*/
						if(!json.getString("veri_num").equals("null")){
							confirmLayout.setVisibility(View.VISIBLE);
							confirmNumber.setText(json.getString("veri_num"));
						}
					}
				} else {
					loadingDialog.dissmiss();
					detail.setText(R.string.get_coupon_error);
				}
			} catch (Exception e) {
				loadingDialog.dissmiss();
				detail.setText(R.string.get_coupon_error);
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
				} catch (Exception e) {
					Log.e("Error", e.getMessage());
					e.printStackTrace();
				}
				return mIcon11;
			}

			protected void onPostExecute(Bitmap result) {
				bmImage.setImageBitmap(result);
				loadingDialog.dissmiss();
			}
		}
	}
	private static final int WHITE = 0xFFFFFFFF;
	private static final int BLACK = 0xFF000000;

	Bitmap encodeAsBitmap(String contents, BarcodeFormat format, int img_width,
			int img_height) throws WriterException {
		String contentsToEncode = contents;
		if (contentsToEncode == null) {
			return null;
		}
		Map<EncodeHintType, Object> hints = null;
		String encoding = guessAppropriateEncoding(contentsToEncode);
		if (encoding != null) {
			hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
			hints.put(EncodeHintType.CHARACTER_SET, encoding);
		}
		MultiFormatWriter writer = new MultiFormatWriter();
		BitMatrix result;
		try {
			result = writer.encode(contentsToEncode, format, img_width,
					img_height, hints);
		} catch (IllegalArgumentException iae) {
			// Unsupported format
			return null;
		}
		int width = result.getWidth();
		int height = result.getHeight();
		int[] pixels = new int[width * height];
		for (int y = 0; y < height; y++) {
			int offset = y * width;
			for (int x = 0; x < width; x++) {
				pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
			}
		}

		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}

	private static String guessAppropriateEncoding(CharSequence contents) {
		// Very crude at the moment
		for (int i = 0; i < contents.length(); i++) {
			if (contents.charAt(i) > 0xFF) {
				return "UTF-8";
			}
		}
		return null;
	}

	// on click
	public void onClickBack(View v) {
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.home_my_page_purchased, menu);
		return false;
	}

	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(this, "ZKWGFP6HKJ33Y69SP5QY");
		FlurryAgent.logEvent("Purchased Detail");
		EasyTracker.getInstance(this).activityStart(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
		EasyTracker.getInstance(this).activityStop(this);
	}
}
