package com.todpop.saltyenglish;

import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
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
import org.apache.http.protocol.HTTP;
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
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.AttributeSet;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
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
	
	//RadioButton eduationBtn;
	RadioButton foodBtn;
	RadioButton beautyBtn;
	RadioButton drinkBtn;
	RadioButton cvsBtn;
	RadioButton etcBtn;
	RadioButton reFundBtn;
	
	Spinner bank;
	Spinner amount;

	EditText productName;
	
	TextView store_main_curReward;
	TextView refund_curReward;
	TextView refund_notice;
	EditText refund_name;
	EditText refund_account_no;
	EditText refund_password;
	Button refund_request;
	
	StoreListViewAdapter storeListViewAdapter;
	ArrayList<StoreListViewItem> itemArray;
	ArrayList<StoreListViewItem> foodItemArray;
	ArrayList<StoreListViewItem> beautyItemArray;
	ArrayList<StoreListViewItem> drinkItemArray;
	ArrayList<StoreListViewItem> cvsItemArray;
	ArrayList<StoreListViewItem> etcItemArray;
	ArrayList<StoreListViewItem> emptyArray;

	StoreListViewItem mStoreListItem;
	
	int curCategory = 1;
	
	ListView storeListView;
	//int count = 0;
	String curReward;

	RelativeLayout listItemView;
	RelativeLayout refundView;

	PopupWindow pwdPopupWindow;
	View pwdPopupView;
	TextView pwdPopupText;
	
	Boolean isClosed = false;
	Boolean pwNotSet = false;
	
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
		
		//eduationBtn = (RadioButton) findViewById(R.id.homestore_id_btn_education);
		foodBtn = (RadioButton) findViewById(R.id.homestore_id_btn_food);
		beautyBtn = (RadioButton) findViewById(R.id.homestore_id_btn_beauty);
		drinkBtn = (RadioButton) findViewById(R.id.homestore_id_btn_drink);
		cvsBtn = (RadioButton) findViewById(R.id.homestore_id_btn_cvs);
		etcBtn = (RadioButton) findViewById(R.id.homestore_id_btn_etc);
		reFundBtn = (RadioButton) findViewById(R.id.homestore_id_btn_refund);
		
		store_main_curReward = (TextView)findViewById(R.id.home_store_curReward);

		productName = (EditText)findViewById(R.id.home_store_id_edit_pname);
		
		listItemView = (RelativeLayout) findViewById(R.id.home_store_id_list_view);
		refundView = (RelativeLayout) findViewById(R.id.home_store_id_refund_view);
		
		loadingProgressBar = (ProgressBar)findViewById(R.id.loadingProgressBar);
		
		pwdPopupView = View.inflate(this, R.layout.popup_view_home_store_password, null);
		pwdPopupWindow = new PopupWindow(pwdPopupView, ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT,true);
		pwdPopupText = (TextView)pwdPopupView.findViewById(R.id.popup_id_text);
		
		searchTempPopupView = View.inflate(this, R.layout.popup_view, null);
		searchTempPopupWindow = new PopupWindow(searchTempPopupView, ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT,true);
		searchTempPopupText = (TextView)searchTempPopupView.findViewById(R.id.popup_id_text);
		
		//eduationBtn.setOnClickListener(radio_listener);
		foodBtn.setOnClickListener(radio_listener);
		beautyBtn.setOnClickListener(radio_listener);
		drinkBtn.setOnClickListener(radio_listener);
		cvsBtn.setOnClickListener(radio_listener);
		etcBtn.setOnClickListener(radio_listener);
		reFundBtn.setOnClickListener(radio_listener);

		//Refund
		refund_curReward = (TextView)findViewById(R.id.home_store_refund_curReward);
		refund_notice = (TextView)findViewById(R.id.home_store_refund_notice);
		refund_name = (EditText)findViewById(R.id.home_store_refund_name);
		refund_account_no = (EditText)findViewById(R.id.home_store_refund_account);
		refund_password = (EditText)findViewById(R.id.home_store_refund_password);
		
		bank = (Spinner) findViewById(R.id.home_store_refund_bank);
		amount = (Spinner) findViewById(R.id.home_store_refund_amount);
		
		refund_request = (Button)findViewById(R.id.home_store_refund_confirm);
		
		itemArray = new ArrayList<StoreListViewItem>();

		foodItemArray = new ArrayList<StoreListViewItem>();
		beautyItemArray = new ArrayList<StoreListViewItem>();
		drinkItemArray = new ArrayList<StoreListViewItem>();
		cvsItemArray = new ArrayList<StoreListViewItem>();
		etcItemArray = new ArrayList<StoreListViewItem>();
		
		emptyArray = new ArrayList<StoreListViewItem>();
		
		storeListView = (ListView) findViewById(R.id.homestore_id_listiew);
		storeListView.setOnItemClickListener(item_listener);

		listItemView.setVisibility(View.VISIBLE);
		storeListView.setVisibility(View.VISIBLE);
	}
	public void onResume(){
		super.onResume();
		new AccessCheck().execute("http://todpop.co.kr/api/qpcon_coupons/can_shopping.json?user_id=" + rgInfo.getString("mem_id", "NO"));
		//should move to onClickListener homestore_id_btn_refund case. This function stays here for store's current reward amount.
		new RefundCheck().execute("http://todpop.co.kr/api/etc/"+rgInfo.getString("mem_id", "NO")+"/refund_info.json");
	}

	public void updateListView(int category) {
		switch(category){
		case 0:
			storeListViewAdapter = new StoreListViewAdapter(this, R.layout.home_store_list_item_view, emptyArray);
			storeListView.setAdapter(storeListViewAdapter);
			break;
		case 1:
			storeListViewAdapter = new StoreListViewAdapter(this, R.layout.home_store_list_item_view, foodItemArray);
			storeListView.setAdapter(storeListViewAdapter);
			break;
		case 2:
			storeListViewAdapter = new StoreListViewAdapter(this, R.layout.home_store_list_item_view, drinkItemArray);
			storeListView.setAdapter(storeListViewAdapter);
			break;
		case 3:
			storeListViewAdapter = new StoreListViewAdapter(this, R.layout.home_store_list_item_view, beautyItemArray);
			storeListView.setAdapter(storeListViewAdapter);
			break;
		case 4:
			storeListViewAdapter = new StoreListViewAdapter(this, R.layout.home_store_list_item_view, cvsItemArray);
			storeListView.setAdapter(storeListViewAdapter);
			break;
		case 5:
			storeListViewAdapter = new StoreListViewAdapter(this, R.layout.home_store_list_item_view, etcItemArray);
			storeListView.setAdapter(storeListViewAdapter);
			break;
		}
		
	}
	
	public void searchItem(View v){
		searchTempPopupText.setText(getResources().getString(R.string.temp_search_being_prepare));
		searchTempPopupWindow.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);
		
		//String pFindName = productName.getText().toString();
	}
	
	public void sendRefundRequest(View v){
		refund_request.setVisibility(View.GONE);
		searchTempPopupText.setText(getResources().getString(R.string.store_refund_waiting));
		searchTempPopupWindow.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);
		
		new RequestForRefund().execute("http://todpop.co.kr/api/etc/refund.json");
	}

	OnItemClickListener item_listener = new OnItemClickListener(){
		@Override
		public void onItemClick(AdapterView<?> parentView, View clickedView, int position,
				long id) {
			StoreListViewItem item = (StoreListViewItem)parentView.getAdapter().getItem(position);
			Intent intent = new Intent(getApplicationContext(), HomeStorePurchase.class);
			intent.putExtra("productId", item.id);
			intent.putExtra("curReward", curReward);
			intent.putExtra("pwNotSet", pwNotSet);
			startActivity(intent);
		}
	};
	OnClickListener radio_listener = new OnClickListener() {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.homestore_id_btn_food:
				curCategory = 1;
				listItemView.setVisibility(View.VISIBLE);
				refundView.setVisibility(View.GONE);
				if(foodItemArray.isEmpty()){
					updateListView(0);
					loadingProgressBar.setVisibility(View.VISIBLE);
					new GetCoupons(1).execute("http://todpop.co.kr/api/qpcon_coupons/product_list.json?type=1");
				}
				else{
					updateListView(1);
				}
				break;
			case R.id.homestore_id_btn_drink:
				curCategory = 2;
				listItemView.setVisibility(View.VISIBLE);
				refundView.setVisibility(View.GONE);
				if(drinkItemArray.isEmpty()){
					updateListView(0);
					loadingProgressBar.setVisibility(View.VISIBLE);
					new GetCoupons(2).execute("http://todpop.co.kr/api/qpcon_coupons/product_list.json?type=2");
				}
				else{
					updateListView(2);					
				}
				break;
			case R.id.homestore_id_btn_beauty:
				curCategory = 3;
				listItemView.setVisibility(View.VISIBLE);
				refundView.setVisibility(View.GONE);
				if(beautyItemArray.isEmpty()){
					updateListView(0);
					loadingProgressBar.setVisibility(View.VISIBLE);
					new GetCoupons(3).execute("http://todpop.co.kr/api/qpcon_coupons/product_list.json?type=3");
				}
				else{
					updateListView(3);	
				}
				break;
			case R.id.homestore_id_btn_cvs:
				curCategory = 4;
				listItemView.setVisibility(View.VISIBLE);
				refundView.setVisibility(View.GONE);
				if(cvsItemArray.isEmpty()){
					updateListView(0);
					loadingProgressBar.setVisibility(View.VISIBLE);
					new GetCoupons(4).execute("http://todpop.co.kr/api/qpcon_coupons/product_list.json?type=4");
				}
				else{
					updateListView(4);	
				}
				break;
			case R.id.homestore_id_btn_etc:
				curCategory = 5;
				listItemView.setVisibility(View.VISIBLE);
				refundView.setVisibility(View.GONE);
				if(etcItemArray.isEmpty()){
					updateListView(0);
					loadingProgressBar.setVisibility(View.VISIBLE);
					new GetCoupons(5).execute("http://todpop.co.kr/api/qpcon_coupons/product_list.json?type=5");
				}
				else{
					updateListView(5);
				}
				break;
			case R.id.homestore_id_btn_refund:
				new GetBank().execute("http://todpop.co.kr/api/etc/get_bank_list.json");
				listItemView.setVisibility(View.GONE);
				refundView.setVisibility(View.VISIBLE);
				break;
			}
		}
	};

	class StoreListViewItem {
		StoreListViewItem(String aId, String aImg, String aTitle, String aProvider, String aPrice, String aStock, Bitmap aBitmap) {
			id = aId;
			img = aImg;
			title = aTitle;
			provider = aProvider;
			price = aPrice;
			stock = aStock;
			bitmap = aBitmap;
			tryDownImg = false;
		}

		String id;
		String img;
		String title;
		String provider;
		String price;
		String stock;
		Bitmap bitmap;
		Boolean tryDownImg;
	}

	class StoreListViewAdapter extends BaseAdapter {
		Context maincon;
		LayoutInflater Inflater;
		ArrayList<StoreListViewItem> arSrc;
        Drawable emptyImg = getResources().getDrawable(R.drawable.store_2_image_goodsimage);
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
			ViewHolder holder;
			if (convertView == null) {
				convertView = Inflater.inflate(layout, parent, false);
				holder = new ViewHolder();
				holder.name1 = (TextView) convertView
						.findViewById(R.id.home_store_list_item_id_name1);
				holder.name2 = (TextView) convertView
						.findViewById(R.id.home_store_list_item_id_name2);
				holder.coins = (TextView) convertView
						.findViewById(R.id.home_store_list_item_id_coins);
				holder.item = (ImageView) convertView
						.findViewById(R.id.home_store_list_item_id_item);
				convertView.setTag(holder);
			} 
			else{
				holder = (ViewHolder)convertView.getTag();
			}
			
			StoreListViewItem item = (StoreListViewItem) arSrc.get(position);
			
			holder.name1.setText(item.title);
			holder.name2.setText(item.provider);
			holder.coins.setText(item.price);
			
			if(holder.item != null){
				if(item.bitmap != null){
					holder.item.setImageBitmap(item.bitmap);
				}
				else{
					holder.item.setImageDrawable(emptyImg);
					if(!item.tryDownImg){
						item.tryDownImg = true;
						new DownloadImageTask(curCategory, position).execute(item.img);
					}
				}
			}

			if (position % 2 == 0) {
				convertView.setBackgroundResource(R.drawable.store_2_image_separatebox_white);
			} else {
				convertView.setBackgroundResource(R.drawable.store_2_image_separatebox_yellow);
			}
			return convertView;
		}
	}
	
	public static class ViewHolder{
		TextView name1;
		TextView name2;
		TextView coins;
		ImageView item;
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

					String notice_content = json.getJSONObject("data").getString("content");
					notice_content = notice_content.replace("\\n", "\n");
					refund_notice.setText(notice_content);
					
					List<String> list = new ArrayList<String>();
					
					if(json.getJSONObject("data").getInt("current_reward") >= 20000){
						String tempStr = curReward.substring(0, curReward.length()-4);
						Log.i("STVEN", "tempStr : "+tempStr);
						for (int i = 2; (i <= 10) && (i <= Integer.parseInt(tempStr)); i++) {
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
				if(json.getBoolean("status")){
					int result = json.getJSONObject("data").getInt("result");
					if(result == 0){	//closed
						isClosed = true;
						searchTempPopupText.setText(R.string.store_temporary_closed);
						searchTempPopupWindow.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);
					}
					else{
						new GetCoupons(curCategory).execute("http://todpop.co.kr/api/qpcon_coupons/product_list.json?type=" + curCategory);
	
						if (result == 2) {	//password not set
							pwNotSet = true;
							//pwdPopupText.setText(json.getString("msg"));
							pwdPopupText.setText(getResources().getString(R.string.store_pw_not_set));
							pwdPopupWindow.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);
						}
					}
				}
			} catch (Exception e) {

			}

		}
	}	
	
	private class GetCoupons extends AsyncTask<String, Void, JSONObject> {
		int category;
		
		private GetCoupons(int iCategory){
			category = iCategory;
		}
		
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
					JSONArray jsonArray = json.getJSONObject("data").getJSONArray("list");
					switch(category){
					case 0:
						itemArray.clear();
						for(int i = 0; i < jsonArray.length(); i++){
							mStoreListItem = new StoreListViewItem(
									jsonArray.getJSONObject(i).getString("product_id"),
									jsonArray.getJSONObject(i).getString("img_url_70"),
									jsonArray.getJSONObject(i).getString("product_name"),
									jsonArray.getJSONObject(i).getString("market_name"),
									jsonArray.getJSONObject(i).getString("market_cost"),
									jsonArray.getJSONObject(i).getString("stock_count"),
									null);
							itemArray.add(mStoreListItem);
						}
						break;
					case 1:
						foodItemArray.clear();
						for(int i = 0; i < jsonArray.length(); i++){
							mStoreListItem = new StoreListViewItem(
									jsonArray.getJSONObject(i).getString("product_id"),
									jsonArray.getJSONObject(i).getString("img_url_70"),
									jsonArray.getJSONObject(i).getString("product_name"),
									jsonArray.getJSONObject(i).getString("market_name"),
									jsonArray.getJSONObject(i).getString("market_cost"),
									jsonArray.getJSONObject(i).getString("stock_count"),
									null);
							foodItemArray.add(mStoreListItem);
						}
						break;
					case 2:
						drinkItemArray.clear();
						for(int i = 0; i < jsonArray.length(); i++){
							mStoreListItem = new StoreListViewItem(
									jsonArray.getJSONObject(i).getString("product_id"),
									jsonArray.getJSONObject(i).getString("img_url_70"),
									jsonArray.getJSONObject(i).getString("product_name"),
									jsonArray.getJSONObject(i).getString("market_name"),
									jsonArray.getJSONObject(i).getString("market_cost"),
									jsonArray.getJSONObject(i).getString("stock_count"),
									null);
							drinkItemArray.add(mStoreListItem);
						}
						break;
					case 3:
						beautyItemArray.clear();
						for(int i = 0; i < jsonArray.length(); i++){
							mStoreListItem = new StoreListViewItem(
									jsonArray.getJSONObject(i).getString("product_id"),
									jsonArray.getJSONObject(i).getString("img_url_70"),
									jsonArray.getJSONObject(i).getString("product_name"),
									jsonArray.getJSONObject(i).getString("market_name"),
									jsonArray.getJSONObject(i).getString("market_cost"),
									jsonArray.getJSONObject(i).getString("stock_count"),
									null);
							beautyItemArray.add(mStoreListItem);
						}
						break;
					case 4:
						cvsItemArray.clear();
						for(int i = 0; i < jsonArray.length(); i++){
							mStoreListItem = new StoreListViewItem(
									jsonArray.getJSONObject(i).getString("product_id"),
									jsonArray.getJSONObject(i).getString("img_url_70"),
									jsonArray.getJSONObject(i).getString("product_name"),
									jsonArray.getJSONObject(i).getString("market_name"),
									jsonArray.getJSONObject(i).getString("market_cost"),
									jsonArray.getJSONObject(i).getString("stock_count"),
									null);
							cvsItemArray.add(mStoreListItem);
						}
						break;
					case 5:
						etcItemArray.clear();
						for(int i = 0; i < jsonArray.length(); i++){
							mStoreListItem = new StoreListViewItem(
									jsonArray.getJSONObject(i).getString("product_id"),
									jsonArray.getJSONObject(i).getString("img_url_70"),
									jsonArray.getJSONObject(i).getString("product_name"),
									jsonArray.getJSONObject(i).getString("market_name"),
									jsonArray.getJSONObject(i).getString("market_cost"),
									jsonArray.getJSONObject(i).getString("stock_count"),
									null);
							etcItemArray.add(mStoreListItem);
						}
						break;
					}
					updateListView(category);
				} else {
					
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}
	
	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
		int category;
		int position;

		public DownloadImageTask(int aCategory, int aPosition) {
			category = aCategory;
			position = aPosition;
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
			switch(category){
			case 1:
				foodItemArray.get(position).bitmap = result;
				break;
			case 2:
				drinkItemArray.get(position).bitmap = result;
				break;
			case 3:
				beautyItemArray.get(position).bitmap = result;
				break;
			case 4:
				cvsItemArray.get(position).bitmap = result;
				break;
			case 5:
				etcItemArray.get(position).bitmap = result;
				break;
			}
			storeListViewAdapter.notifyDataSetChanged();
		}
	}
	//--- request class ---
	private class RequestForRefund extends AsyncTask<String, Void, JSONObject> 
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
        		params.add(new BasicNameValuePair("name", refund_name.getText().toString()));
        		params.add(new BasicNameValuePair("bank", bank.getSelectedItem().toString()));
        		params.add(new BasicNameValuePair("account", refund_account_no.getText().toString()));
        		String editedAmount = amount.getSelectedItem().toString().replace(",", "");
        		params.add(new BasicNameValuePair("amount", editedAmount));
        		params.add(new BasicNameValuePair("password", returnSHA512(refund_password.getText().toString())));

        		UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params,HTTP.UTF_8);
        		post.setEntity(ent);
        		HttpResponse responsePOST = client.execute(post);  
        		HttpEntity resEntity = responsePOST.getEntity();

        		if (resEntity != null)
        		{    
        			json = new JSONObject(EntityUtils.toString(resEntity)); 	
    				Log.d("RESPONSE ---- ", json.toString());
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
        		if(result.getBoolean("status")){
        			searchTempPopupText.setText(getResources().getString(R.string.store_refund_done));
        			new RefundCheck().execute("http://todpop.co.kr/api/etc/"+rgInfo.getString("mem_id", "NO")+"/refund_info.json");
        		}
        		else if(result.getString("msg").contains("params")){
        			refund_request.setVisibility(View.VISIBLE);
        			searchTempPopupText.setText(getResources().getString(R.string.store_refund_error_param));        			
        		}
        		else if(result.getString("msg").contains("password")){
        			refund_request.setVisibility(View.VISIBLE);
        			searchTempPopupText.setText(getResources().getString(R.string.store_refund_error_pw));
        		}
        		else{
        			refund_request.setVisibility(View.VISIBLE);
        			searchTempPopupText.setText(getResources().getString(R.string.store_refund_error));
        		}
        	}catch (Exception e) {
    			searchTempPopupText.setText(getResources().getString(R.string.store_refund_error));
        	}
        }
	}

	public void onClickBack(View view) {
		finish();
	}
	
	public void closePopup(View view){
		if(isClosed){
			finish();
		}
		else{
			searchTempPopupWindow.dismiss();
		}
	}
	
	public void goPasswordSetting(View v){
		Intent intent = new Intent(getApplicationContext(), HomeMoreAccountInfo.class);
		startActivity(intent);
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
		updateListView(0);
		FlurryAgent.onEndSession(this);
	    EasyTracker.getInstance(this).activityStop(this);
	}
}
