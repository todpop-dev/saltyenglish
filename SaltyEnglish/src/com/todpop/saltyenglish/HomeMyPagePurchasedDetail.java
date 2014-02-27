package com.todpop.saltyenglish;

import java.io.InputStream;
import java.util.EnumMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

public class HomeMyPagePurchasedDetail extends Activity {

	TextView productTitle;
	ImageView productImg;
	TextView productName;
	TextView place;
	TextView validate;
	RelativeLayout barcodeLayout;
	ImageView barcode_image;
	TextView barcode_text;
	TextView detail;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_my_page_purchased_detail);
		Intent intent = getIntent();
		String couponId = intent.getStringExtra("couponId");
		Boolean isFree = intent.getBooleanExtra("isFree", true);
		productTitle = (TextView) findViewById(R.id.home_my_page_purchased_detail_title);
		productName = (TextView) findViewById(R.id.home_my_page_purchased_detail_product);
		productImg = (ImageView) findViewById(R.id.home_my_page_purchased_detail_product_img);
		place = (TextView) findViewById(R.id.home_my_page_purchased_detail_place);
		validate = (TextView) findViewById(R.id.home_my_page_purchased_detail_validate);
		barcodeLayout = (RelativeLayout) findViewById(R.id.home_my_page_purchased_detail_barcode_layout);
		barcode_image = (ImageView) findViewById(R.id.home_my_page_purchased_detail_barcode_image);
		barcode_text = (TextView) findViewById(R.id.home_my_page_purchased_detail_barcode_text);
		detail = (TextView) findViewById(R.id.home_my_page_purchased_detail_info);

		/*
		 * if(couponId == 9999){ //fake
		 * productTitle.setText(R.string.temp_list_cafe_1_name);
		 * productName.setText(R.string.temp_list_cafe_1_name);
		 * place.setText(R.string.temp_list_cafe_1_place);
		 * validate.setText("2014-03-17"); //TODO detail enter
		 * detail.setText(R.string.temp_list_angel_info);
		 * productImg.setImageResource(R.drawable.cafe1);
		 * barcode_image.setImageResource(R.drawable.fake_barcode); } else
		 */
		if(isFree){
			new GetCouponsInfo().execute("http://todpop.co.kr/api/etc/" + couponId
					+ "/get_coupon_free_info.json");
		}
		else{
			new GetQPConInfo().execute("http://todpop.co.kr/api/etc/get_qpcon_info.json?order_id=" + couponId);
		}
	}

	private class GetCouponsInfo extends AsyncTask<String, Void, JSONObject> {
		@Override
		protected JSONObject doInBackground(String... urls) {
			JSONObject result = null;
			try {
				String getURL = urls[0];
				HttpGet httpGet = new HttpGet(getURL);
				HttpParams httpParameters = new BasicHttpParams();
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
				e.printStackTrace();
			}

			return result;
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
					else
						detail.setText(json.getString("information"));

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
				} else {
					detail.setText(R.string.get_coupon_error);
				}
			} catch (Exception e) {
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
			}
		}
	}
	private class GetQPConInfo extends AsyncTask<String, Void, JSONObject> {
		@Override
		protected JSONObject doInBackground(String... urls) {
			JSONObject result = null;
			try {
				String getURL = urls[0];
				HttpGet httpGet = new HttpGet(getURL);
				HttpParams httpParameters = new BasicHttpParams();
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
				e.printStackTrace();
			}

			return result;
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
					else
						detail.setText(json.getString("information"));

					new DownloadImageTask(productImg)
							.execute(json.getString("image"));
					if(!json.getString("bar_code").equals("null")){
						barcodeLayout.setVisibility(View.VISIBLE);
						barcode_image.setImageBitmap(encodeAsBitmap(
								json.getString("bar_code"), BarcodeFormat.CODE_128,
								barcode_image.getWidth(), barcode_image.getHeight()));
						barcode_text.setText(json.getString("bar_code"));
					}
				} else {
					detail.setText(R.string.get_coupon_error);
				}
			} catch (Exception e) {
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
		getMenuInflater().inflate(R.menu.home_my_page_purchased, menu);
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
