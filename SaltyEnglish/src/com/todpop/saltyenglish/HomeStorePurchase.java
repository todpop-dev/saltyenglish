package com.todpop.saltyenglish;

import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
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
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class HomeStorePurchase extends Activity {
	

	TextView productTitle;
	
	ImageView productImg;
	
	TextView productPrice;
	TextView place;
	TextView validate;
	
	TextView currentReward;
	TextView remainReward;
	
	TextView detail;

	RelativeLayout mainLayout;
	
	PopupWindow confirmPopupWindow;
	View confirmPopupView;
	TextView confirmPopupPrice;
	TextView confirmPopupMobile;
	EditText confirmPopupPwd;
	ProgressBar confirmProgressBar;
	Button cancleBtn;
	Button confirmBtn;
	
	PopupWindow donePopupWindow;
	View donePopupView;
	
	PopupWindow kickBackPopupWindow;
	View kickBackPopupView;
	TextView kickBackPopupText;

	SharedPreferences rgInfo;
	SharedPreferences.Editor rgInfoEdit;
	
	String productId;
	String price;
	String curReward;
	String nickName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_store_purchase);
		Intent intent = getIntent();
		productId = intent.getStringExtra("productId");
		String title = intent.getStringExtra("title");
		String provider = intent.getStringExtra("provider");
		price = intent.getStringExtra("price");
		String imgUrl = intent.getStringExtra("imgUrl");
		curReward = intent.getStringExtra("curReward");
		
		rgInfo = getSharedPreferences("rgInfo",0);
		rgInfoEdit = rgInfo.edit();
		
		
		mainLayout = (RelativeLayout)findViewById(R.id.home_store_purchase_layout);
		
		productTitle = (TextView)findViewById(R.id.home_store_purchase_detail_title);
		
		productImg = (ImageView)findViewById(R.id.home_store_purchase_detail_product_img);
		
		productPrice = (TextView)findViewById(R.id.home_store_purchase_detail_product);
		place = (TextView)findViewById(R.id.home_store_purchase_detail_place);
		validate = (TextView)findViewById(R.id.home_store_purchase_detail_validate);
		
		currentReward = (TextView)findViewById(R.id.home_store_purchase_detail_current_amount_number);
		remainReward = (TextView)findViewById(R.id.home_store_purchase_detail_remain_amount_number);
		
		detail = (TextView)findViewById(R.id.home_store_purchase_detail_info);

		//confirm popup
		confirmPopupView = View.inflate(this, R.layout.popup_view_home_store_purchase_confirm, null);
		confirmPopupWindow = new PopupWindow(confirmPopupView, ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT,true);
		confirmPopupPrice = (TextView)confirmPopupView.findViewById(R.id.popup_store_id_price);
		confirmPopupMobile = (TextView)confirmPopupView.findViewById(R.id.popup_store_id_nick);
		confirmPopupPwd = (EditText)confirmPopupView.findViewById(R.id.popup_store_id_pwd);
		confirmProgressBar = (ProgressBar)confirmPopupView.findViewById(R.id.popup_store_id_progressBar);
		cancleBtn = (Button)confirmPopupView.findViewById(R.id.popup_store_id_cancle);
		confirmBtn = (Button)confirmPopupView.findViewById(R.id.popup_store_id_confirm);
		
		donePopupView = View.inflate(this, R.layout.popup_view_home_store_purchase_done, null);
		donePopupWindow = new PopupWindow(donePopupView, ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT,true);
		
		kickBackPopupView = View.inflate(this, R.layout.popup_view, null);
		kickBackPopupWindow = new PopupWindow(kickBackPopupView, ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT,true);
		kickBackPopupText = (TextView)kickBackPopupView.findViewById(R.id.popup_id_text);
		
		productTitle.setText(title);
		productPrice.setText(price);
		place.setText(provider);
		currentReward.setText(curReward);
		int remainRewardAmount = Integer.valueOf(curReward) - Integer.valueOf(price);
		remainReward.setText(String.valueOf(remainRewardAmount));
		
		new DownloadImageTask(productImg).execute(imgUrl);
		
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

	
	
	// on click
	public void onClickBack(View v)
	{
		finish();
	}
	
	public void onClickPurchase(View v){
		if(Integer.valueOf(curReward) > Integer.valueOf(price)){
			SharedPreferences rgInfo;
			rgInfo = getSharedPreferences("rgInfo",0);
			confirmPopupPrice.setText(price);
			confirmPopupMobile.setText(rgInfo.getString("nickname", "NO"));
			confirmPopupWindow.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);
			confirmPopupWindow.showAsDropDown(null);
		}
		else{			
			kickBackPopupText.setText(R.string.not_enough_reward);
			kickBackPopupWindow.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);
			kickBackPopupWindow.showAsDropDown(null);
		}
	}
	
	public void closePopup(View v)
	{
		confirmPopupWindow.dismiss();
		if(kickBackPopupWindow.isShowing() || donePopupWindow.isShowing()){
			finish();
		}
	}
	
	public void confirmPurchase(View v)
	{
		cancleBtn.setClickable(false);
		confirmBtn.setClickable(false);
		confirmPopupPwd.setClickable(false);
		confirmProgressBar.setVisibility(View.VISIBLE);
		new RequestForPurchase().execute("http://todpop.co.kr/api/qpcon_coupons/purchase.json");
	}
	
	public void goToPurchasedList(View v){
		Intent intent = new Intent(getApplicationContext(), HomeMyPagePurchased.class);
		intent.putExtra("state", 1);
		startActivity(intent);
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
	    EasyTracker.getInstance(this).activityStart(this);
	}
	 
	@Override
	protected void onStop()
	{
		super.onStop();		
		FlurryAgent.onEndSession(this);
	    EasyTracker.getInstance(this).activityStop(this);
	}
	
	//--- request class ---
	private class RequestForPurchase extends AsyncTask<String, Void, JSONObject> 
	{
        @Override
        protected JSONObject doInBackground(String... urls) 
        {
        	JSONObject json = null;

        	try
	        {
	        	HttpClient client = new DefaultHttpClient();  
	        	String postURL = urls[0];
	        	HttpPost post = new HttpPost(postURL); 
	        	List<NameValuePair> params = new ArrayList<NameValuePair>();

        		params.add(new BasicNameValuePair("user_id", rgInfo.getString("mem_id", "NO")));
        		params.add(new BasicNameValuePair("coupon_id", productId));
        		params.add(new BasicNameValuePair("password", returnSHA512(confirmPopupPwd.getText().toString())));

        		UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params,HTTP.UTF_8);
        		post.setEntity(ent);
        		HttpResponse responsePOST = client.execute(post);  
        		HttpEntity resEntity = responsePOST.getEntity();

        		if (resEntity != null)
        		{    
        			json = new JSONObject(EntityUtils.toString(resEntity)); 
        			Log.d("send request for purchase", json.toString());				        	
        			return json;
        		}
        		return json;
        	}
        	catch (Exception e)
        	{
			        e.printStackTrace();
			}
	        	
        	return json;
        }
	        
        @Override
        protected void onPostExecute(JSONObject result) {
        	try {
        		confirmPopupWindow.dismiss();
        		if (result.getBoolean("status")==true) {
        			Log.i("STEVEN purchase 228", "status true");
        			donePopupWindow.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);
        			donePopupWindow.showAsDropDown(null);
        			}
        		else
        		{
        			kickBackPopupText.setText(result.getString("msg"));
        			kickBackPopupWindow.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);
        			kickBackPopupWindow.showAsDropDown(null);
        		}
        	}catch (Exception e) {
    			kickBackPopupText.setText("error 01");
    			kickBackPopupWindow.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);
    			kickBackPopupWindow.showAsDropDown(null);
        	}

        }
	}
	
	public String returnSHA512(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(password.getBytes());
        byte byteData[] = md.digest();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
	 
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
            String hex = Integer.toHexString(0xff & byteData[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
      //  System.out.println("SHA512: " + hexString.toString());
        return hexString.toString();
    }
}
