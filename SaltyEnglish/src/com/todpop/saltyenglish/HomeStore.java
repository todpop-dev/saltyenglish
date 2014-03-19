package com.todpop.saltyenglish;

import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

public class HomeStore extends Activity {
	RelativeLayout mainLayout;
	
	RadioButton eduationBtn;
	RadioButton foodBtn;
	RadioButton cafeBtn;
	RadioButton convenientBtn;
	RadioButton beautyBtn;
	RadioButton reFundBtn;

	Spinner bank;
	Spinner amount;

	TextView store_main_curReward;
	TextView refund_curReward;
	TextView refund_notice;
	EditText refund_name;
	EditText refund_account_no;
	EditText refund_password;
	
	StoreListViewAdapter storeListViewAdapter;
	ArrayList<StoreListViewItem> itemArray;
	StoreListViewItem mStoreListItem;
	ListView storeListView;
	int count = 0;
	String curReward;

	RelativeLayout listItemView;
	ScrollView refundView;

	PopupWindow pwdPopupWindow;
	View pwdPopupView;
	TextView pwdPopupText;
	
	PopupWindow searchTempPopupWindow;
	View searchTempPopupView;
	TextView searchTempPopupText;
	
	SharedPreferences rgInfo;
	SharedPreferences.Editor rgInfoEdit;
	
	ProgressBar loadingProgressBar;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_store);
		
		rgInfo = getSharedPreferences("rgInfo",0);
		
		mainLayout = (RelativeLayout)findViewById(R.id.home_store_id_mainLayout);
		
		/*eduationBtn = (RadioButton) findViewById(R.id.homestore_id_btn_education);
		foodBtn = (RadioButton) findViewById(R.id.homestore_id_btn_food);
		cafeBtn = (RadioButton) findViewById(R.id.homestore_id_btn_cafe);
		convenientBtn = (RadioButton) findViewById(R.id.homestore_id_btn_convenient);
		beautyBtn = (RadioButton) findViewById(R.id.homestore_id_btn_beauty);
		reFundBtn = (RadioButton) findViewById(R.id.homestore_id_btn_refund);*/
		
		store_main_curReward = (TextView)findViewById(R.id.home_store_curReward);
		
		listItemView = (RelativeLayout) findViewById(R.id.home_store_id_list_view);
		refundView = (ScrollView) findViewById(R.id.home_store_id_refund_view);
		
		loadingProgressBar = (ProgressBar)findViewById(R.id.loadingProgressBar);
		
		pwdPopupView = View.inflate(this, R.layout.popup_view_home_more_acount_info, null);
		pwdPopupWindow = new PopupWindow(pwdPopupView, ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT,true);
		pwdPopupText = (TextView)pwdPopupView.findViewById(R.id.popup_id_text);
		
		searchTempPopupView = View.inflate(this, R.layout.popup_view, null);
		searchTempPopupWindow = new PopupWindow(searchTempPopupView, ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT,true);
		searchTempPopupText = (TextView)searchTempPopupView.findViewById(R.id.popup_id_text);
		
		/*eduationBtn.setOnClickListener(radio_listener);
		foodBtn.setOnClickListener(radio_listener);
		cafeBtn.setOnClickListener(radio_listener);
		convenientBtn.setOnClickListener(radio_listener);
		beautyBtn.setOnClickListener(radio_listener);
		reFundBtn.setOnClickListener(radio_listener);*/

		//Refund
		refund_curReward = (TextView)findViewById(R.id.home_store_refund_curReward);
		refund_notice = (TextView)findViewById(R.id.home_store_refund_notice);
		
		bank = (Spinner) findViewById(R.id.home_store_refund_bank);
		amount = (Spinner) findViewById(R.id.home_store_refund_amount);
		
		itemArray = new ArrayList<StoreListViewItem>();
		storeListView = (ListView) findViewById(R.id.homestore_id_listiew);
		storeListView.setOnItemClickListener(item_listener);

		new AccessCheck().execute("http://todpop.co.kr/api/qpcon_coupons/can_shopping.json?user_id=" + rgInfo.getString("mem_id", "NO"));
		//TODO get category list and add category id at GetCoupon
		new GetCoupons().execute("http://todpop.co.kr/api/qpcon_coupons.json");

		listItemView.setVisibility(View.VISIBLE);
		storeListView.setVisibility(View.VISIBLE);
	}
	public void onResume(){
		super.onResume();
		//should move to onClickListener homestore_id_btn_refund case. This function stays here for store's current reward amount.
		new RefundCheck().execute("http://todpop.co.kr/api/etc/"+rgInfo.getString("mem_id", "NO")+"/refund_info.json");
	}

	public void updateListView() {
		storeListViewAdapter = new StoreListViewAdapter(this,
				R.layout.home_store_list_item_view, itemArray);
		storeListView.setAdapter(storeListViewAdapter);
	}

	OnItemClickListener item_listener = new OnItemClickListener(){
		@Override
		public void onItemClick(AdapterView<?> parentView, View clickedView, int position,
				long id) {
			StoreListViewItem item = (StoreListViewItem)parentView.getAdapter().getItem(position);
			Intent intent = new Intent(getApplicationContext(), HomeStorePurchase.class);
			intent.putExtra("productId", item.id);
			intent.putExtra("curReward", curReward);
			startActivity(intent);
		}
	};
	/*OnClickListener radio_listener = new OnClickListener() {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.homestore_id_btn_education:
				listItemView.setVisibility(RelativeLayout.VISIBLE);
				refundView.setVisibility(ScrollView.GONE);
				count = 0;
				itemArray.clear();
				updateListView();
				break;
			case R.id.homestore_id_btn_food:
				listItemView.setVisibility(RelativeLayout.VISIBLE);
				refundView.setVisibility(ScrollView.GONE);
				count = 0;
				itemArray.clear();
				
				updateListView();
				break;
			case R.id.homestore_id_btn_cafe:
				listItemView.setVisibility(RelativeLayout.VISIBLE);
				refundView.setVisibility(ScrollView.GONE);
				count = 0;
				itemArray.clear();
				
				updateListView();
				break;
			case R.id.homestore_id_btn_convenient:
				refundView.setVisibility(ScrollView.GONE);
				break;
			case R.id.homestore_id_btn_beauty:
				refundView.setVisibility(ScrollView.GONE);
				break;
			case R.id.homestore_id_btn_refund:
				new GetBank().execute("http://todpop.co.kr/api/etc/get_bank_list.json");
				listItemView.setVisibility(RelativeLayout.GONE);
				refundView.setVisibility(ScrollView.VISIBLE);
				break;
			}
		}
	};*/

	class StoreListViewItem {
		StoreListViewItem(String aId, String aImg, String aTitle, String aProvider, String aPrice, String mImg) {
			id = aId;
			img = aImg;
			title = aTitle;
			provider = aProvider;
			price = aPrice;
			mSizeImg = mImg;
		}

		String id;
		String img;
		String title;
		String provider;
		String price;
		String mSizeImg;
	}

	class StoreListViewAdapter extends BaseAdapter {
		Context maincon;
		LayoutInflater Inflater;
		ArrayList<StoreListViewItem> arSrc;
		int layout;

		public StoreListViewAdapter(Context context, int alayout,
				ArrayList<StoreListViewItem> aarSrc) {
			maincon = context;
			Inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			arSrc = aarSrc;
			layout = alayout;
		}

		public int getCount() {
			return arSrc.size();
		}

		public StoreListViewItem getItem(int position) {
			return arSrc.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			count++;
			if (convertView == null) {
				convertView = Inflater.inflate(layout, parent, false);
			}

			TextView name1Text = (TextView) convertView
					.findViewById(R.id.home_store_list_item_id_name1);
			name1Text.setText(arSrc.get(position).title);

			TextView name2Text = (TextView) convertView
					.findViewById(R.id.home_store_list_item_id_name2);
			name2Text.setText(arSrc.get(position).provider);
			TextView coinText = (TextView) convertView
					.findViewById(R.id.home_store_list_item_id_coins);
			coinText.setText(arSrc.get(position).price);
			
			ImageView itemImg = (ImageView) convertView
					.findViewById(R.id.home_store_list_item_id_item);
			
			try {
				// show The Image
				new DownloadImageTask(itemImg).execute(arSrc.get(position).img);
			} catch (Exception e) {
				e.printStackTrace();
			} 
			
			if (count % 2 == 1) {
				convertView.setBackgroundResource(R.drawable.store_2_image_separatebox_white);
			} else {
				convertView.setBackgroundResource(R.drawable.store_2_image_separatebox_yellow);
			}
			return convertView;
		}
	}

	// --- request class ---
	private class RefundCheck extends AsyncTask<String, Void, JSONObject> {
		@Override
		protected JSONObject doInBackground(String... urls) {
			JSONObject result = null;
			try {
				DefaultHttpClient httpClient = new DefaultHttpClient();
				String getURL = urls[0];
				HttpGet httpGet = new HttpGet(getURL);
				HttpResponse httpResponse = httpClient.execute(httpGet);
				HttpEntity resEntity = httpResponse.getEntity();

				if (resEntity != null) {
					result = new JSONObject(EntityUtils.toString(resEntity));
					Log.d("RESPONSE ---- ", result.toString());
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
					curReward = json.getJSONObject("data").getString("current_reward");
					store_main_curReward.setText(curReward);
					refund_curReward.setText(curReward);
					refund_notice.setText(json.getJSONObject("data").getString("content"));
					List<String> list = new ArrayList<String>();
					if(json.getJSONObject("data").getInt("current_reward") >= 30000){
						String tempStr = curReward.substring(0, curReward.length()-3);
						for (int i = 3; (i <= 10) && (i <= Integer.parseInt(tempStr)); i++) {
							list.add(String.valueOf(i)+"0,000");
						}
						list.add("금액을 선택해 주세요");
						MySpinnerAdapter AmountHintSpinner = new MySpinnerAdapter(
								HomeStore.this,
								android.R.layout.simple_spinner_item, list);
						AmountHintSpinner
								.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						amount.setAdapter(AmountHintSpinner);
						amount.setSelection(AmountHintSpinner.getCount());
					}
					else{
						list.add("환급 불가");
						list.add("적립금이 부족합니다");
						MySpinnerAdapter AmountHintSpinner = new MySpinnerAdapter(
								HomeStore.this,
								android.R.layout.simple_spinner_item, list);
						AmountHintSpinner
								.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						amount.setAdapter(AmountHintSpinner);
						amount.setSelection(AmountHintSpinner.getCount());
						amount.setClickable(false);
						amount.setFocusable(false);
					}
				} else {
				}
			} catch (Exception e) {

			}

		}
	}

	// --- request class ---
	private class GetBank extends AsyncTask<String, Void, JSONObject> {
		@Override
		protected JSONObject doInBackground(String... urls) {
			JSONObject result = null;
			try {
				DefaultHttpClient httpClient = new DefaultHttpClient();
				String getURL = urls[0];
				HttpGet httpGet = new HttpGet(getURL);
				HttpResponse httpResponse = httpClient.execute(httpGet);
				HttpEntity resEntity = httpResponse.getEntity();

				if (resEntity != null) {
					result = new JSONObject(EntityUtils.toString(resEntity));
					Log.d("RESPONSE ---- ", result.toString());
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
					JSONArray bankScr = json.getJSONObject("data")
							.getJSONArray("bank_list");
					List<String> list = new ArrayList<String>();
					for (int i = 0; i < bankScr.length(); i++) {
						list.add(bankScr.getString(i));
					}
					list.add("은행을 선택해주세요");
					MySpinnerAdapter BankHintSpinner = new MySpinnerAdapter(
							HomeStore.this,
							android.R.layout.simple_spinner_item, list);
					BankHintSpinner
							.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					bank.setAdapter(BankHintSpinner);
					bank.setSelection(BankHintSpinner.getCount());
				} else {
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}
	
	// --- request class ---
	private class AccessCheck extends AsyncTask<String, Void, JSONObject> {
		@Override
		protected JSONObject doInBackground(String... urls) {
			JSONObject result = null;
			try {
				DefaultHttpClient httpClient = new DefaultHttpClient();
				String getURL = urls[0];
				HttpGet httpGet = new HttpGet(getURL);
				HttpResponse httpResponse = httpClient.execute(httpGet);
				HttpEntity resEntity = httpResponse.getEntity();

				if (resEntity != null) {
					result = new JSONObject(EntityUtils.toString(resEntity));
					Log.d("RESPONSE ---- ", result.toString());
				}
				return result;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		}

		@Override
		protected void onPostExecute(JSONObject json) {
			loadingProgressBar.setVisibility(View.GONE);
			try {
				if (json.getBoolean("status") == false) {
					pwdPopupText.setText(json.getString("msg"));
					pwdPopupWindow.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);
					pwdPopupWindow.showAsDropDown(null);
				} else {
				}
				
			} catch (Exception e) {

			}

		}
	}	
	
	private class GetCoupons extends AsyncTask<String, Void, JSONObject> {
		@Override
		protected JSONObject doInBackground(String... urls) {
			JSONObject result = null;
			try {
				DefaultHttpClient httpClient = new DefaultHttpClient();
				String getURL = urls[0];
				HttpGet httpGet = new HttpGet(getURL);
				HttpResponse httpResponse = httpClient.execute(httpGet);
				HttpEntity resEntity = httpResponse.getEntity();

				if (resEntity != null) {
					result = new JSONObject(EntityUtils.toString(resEntity));
					Log.d("RESPONSE ---- ", result.toString());
				}
				return result;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		}

		@Override
		protected void onPostExecute(JSONObject json) {
			loadingProgressBar.setVisibility(View.GONE);
			try {
				if (json.getBoolean("status") == true) {
					itemArray.clear();
					JSONArray jsonArray = json.getJSONArray("data");
					for(int i = 0; i < jsonArray.length(); i++){
						mStoreListItem = new StoreListViewItem(
								jsonArray.getJSONObject(i).getString("product_id"),
								jsonArray.getJSONObject(i).getString("img_url_70"),
								jsonArray.getJSONObject(i).getString("product_name"),
								jsonArray.getJSONObject(i).getString("change_market_name"),
								jsonArray.getJSONObject(i).getString("market_cost"),
								jsonArray.getJSONObject(i).getString("img_url_150"));
						itemArray.add(mStoreListItem);
					}
					updateListView();
				} else {
					
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
	
	public void onClickBack(View view) {
		finish();
	}
	public void onClickSearch(View view){
		searchTempPopupText.setText(getResources().getString(R.string.temp_search_being_prepare));
		searchTempPopupWindow.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);
		searchTempPopupWindow.showAsDropDown(null);
	}
	public void closePopup(View view){
		searchTempPopupWindow.dismiss();
	}

	public class MySpinnerAdapter extends ArrayAdapter<String> {

		public MySpinnerAdapter(Context context, int resource,
				List<String> objects) {
			super(context, resource, objects);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View v = super.getView(position, convertView, parent);
			if (position == getCount()) {
				((TextView) v.findViewById(android.R.id.text1)).setText("");
				((TextView) v.findViewById(android.R.id.text1))
						.setHint(getItem(getCount()));
			}
			return v;
		}

		public int getCount() {
			return super.getCount() - 1;
		}
	}
	
	public void confirmPopup(View view){
		pwdPopupWindow.dismiss();
	}
	
	public void onClickOk(View view){
		//if(true);
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
	@Override
	protected void onStart()
	{
		super.onStart();
		FlurryAgent.onStartSession(this, "ZKWGFP6HKJ33Y69SP5QY");
		FlurryAgent.logEvent("Store");
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
