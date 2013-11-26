package com.todpop.saltyenglish;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class HomeStore extends Activity {

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
	
	
	StoreListViewAdapter storeListViewAdapter;
	ArrayList<StoreListViewItem> itemArray;
	StoreListViewItem mStoreListItem;
	ListView storeListView;
	int count = 0;

	RelativeLayout listItemView;
	ScrollView refundView;

	SharedPreferences rgInfo;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_store);
		
		rgInfo = getSharedPreferences("rgInfo",0);
		
		eduationBtn = (RadioButton) findViewById(R.id.homestore_id_btn_education);
		foodBtn = (RadioButton) findViewById(R.id.homestore_id_btn_food);
		cafeBtn = (RadioButton) findViewById(R.id.homestore_id_btn_cafe);
		convenientBtn = (RadioButton) findViewById(R.id.homestore_id_btn_convenient);
		beautyBtn = (RadioButton) findViewById(R.id.homestore_id_btn_beauty);
		reFundBtn = (RadioButton) findViewById(R.id.homestore_id_btn_refund);
		
		store_main_curReward = (TextView)findViewById(R.id.home_store_curReward);
		
		listItemView = (RelativeLayout) findViewById(R.id.home_store_id_list_view);
		refundView = (ScrollView) findViewById(R.id.home_store_id_refund_view);

		eduationBtn.setOnClickListener(radio_listener);
		foodBtn.setOnClickListener(radio_listener);
		cafeBtn.setOnClickListener(radio_listener);
		convenientBtn.setOnClickListener(radio_listener);
		beautyBtn.setOnClickListener(radio_listener);
		reFundBtn.setOnClickListener(radio_listener);

		refund_curReward = (TextView)findViewById(R.id.home_store_refund_curReward);
		refund_notice = (TextView)findViewById(R.id.home_store_refund_notice);
		
		bank = (Spinner) findViewById(R.id.home_store_refund_bank);
		amount = (Spinner) findViewById(R.id.home_store_refund_amount);
		// TODO amount spinner
		
		itemArray = new ArrayList<StoreListViewItem>();
		storeListView = (ListView) findViewById(R.id.homestore_id_listiew);
		storeListView.setOnItemClickListener(listViewItemListener);
		for (int i = 0; i < 20; i++) {
			mStoreListItem = new StoreListViewItem(
					R.drawable.store_33_image_dinosaur_on,
					getString(R.string.home_store_prep), "", "");
			itemArray.add(mStoreListItem);
		}

		//should move to onClickListener homestore_id_btn_refund case. This function stays here for store's current reward amount.
		new RefundCheck().execute("http://todpop.co.kr/api/etc/"+rgInfo.getString("mem_id", "NO")+"/refund_info.json");
		
		listItemView.setVisibility(View.VISIBLE);
		storeListView.setVisibility(View.VISIBLE);
		this.updateListView();
	}

	public void updateListView() {
		storeListViewAdapter = new StoreListViewAdapter(this,
				R.layout.home_store_list_item_view, itemArray);
		storeListView.setAdapter(storeListViewAdapter);
	}

	OnItemClickListener listViewItemListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parentView, View clickedView,
				int position, long id) {
			// String toastMessage =
			// ((TextView)clickedView).getText().toString() +
			// " is selected."+position;
			String toastMessage = " is selected." + position;
			Toast.makeText(getApplicationContext(), toastMessage,
					Toast.LENGTH_SHORT).show();

		}
	};

	OnClickListener radio_listener = new OnClickListener() {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.homestore_id_btn_education:
				listItemView.setVisibility(RelativeLayout.VISIBLE);
				refundView.setVisibility(ScrollView.GONE);
				count = 0;
				itemArray.clear();
				for (int i = 0; i < 20; i++) {
					mStoreListItem = new StoreListViewItem(
							R.drawable.store_33_image_dinosaur_on,
							getString(R.string.home_store_prep), "", "");
					itemArray.add(mStoreListItem);
				}
				updateListView();
				break;
			case R.id.homestore_id_btn_food:
				listItemView.setVisibility(RelativeLayout.VISIBLE);
				refundView.setVisibility(ScrollView.GONE);
				count = 0;
				itemArray.clear();
				for (int i = 0; i < 20; i++) {
					mStoreListItem = new StoreListViewItem(
							R.drawable.store_33_image_dinosaur_on,
							getString(R.string.home_store_prep), "", "");
					itemArray.add(mStoreListItem);
				}
				updateListView();
				break;
			case R.id.homestore_id_btn_cafe:
				refundView.setVisibility(ScrollView.GONE);
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
	};

	class StoreListViewItem {
		StoreListViewItem(int aItem, String aName1, String aName2, String aCoin) {
			item = aItem;
			name1 = aName1;
			name2 = aName2;
			coin = aCoin;
		}

		int item;
		String name1;
		String name2;
		String coin;
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

		public String getItem(int position) {
			return arSrc.get(position).name1;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			count++;
			if (convertView == null) {
				convertView = Inflater.inflate(layout, parent, false);
			}
			ImageView itemImg = (ImageView) convertView
					.findViewById(R.id.home_store_list_item_id_item);
			itemImg.setImageResource(arSrc.get(position).item);

			TextView name1Text = (TextView) convertView
					.findViewById(R.id.home_store_list_item_id_name1);
			name1Text.setText(arSrc.get(position).name1);

			TextView name2Text = (TextView) convertView
					.findViewById(R.id.home_store_list_item_id_name2);
			name2Text.setText(arSrc.get(position).name2);
			TextView coinText = (TextView) convertView
					.findViewById(R.id.home_store_list_item_id_coins);
			coinText.setText(arSrc.get(position).coin);

			if (count % 2 == 1) {
				convertView
						.setBackgroundResource(R.drawable.store_2_image_separatebox_white);
			} else {
				convertView
						.setBackgroundResource(R.drawable.store_2_image_separatebox_yellow);
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
					store_main_curReward.setText(json.getJSONObject("data").getString("current_reward"));
					refund_curReward.setText(json.getJSONObject("data").getString("current_reward"));
					refund_notice.setText(json.getJSONObject("data").getString("content"));
					List<String> list = new ArrayList<String>();
					if(json.getJSONObject("data").getInt("current_reward") >= 30000){
						for (int i = 30000; (i <= 100000) && (i <= json.getJSONObject("data").getInt("current_reward")); i+=10000) {
							String temp = String.valueOf(i);
							list.add(temp.substring(temp.length()-3)+","+"000");
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
					Log.d("-----------------------", "Login Failed");
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
					Log.i("STEVEN", "json.getBoolean(" + "status" + ")==true");
					JSONArray bankScr = json.getJSONObject("data")
							.getJSONArray("bank_list");
					List<String> list = new ArrayList<String>();
					for (int i = 0; i < bankScr.length(); i++) {
						list.add(bankScr.getString(i));
						Log.i("STEVEN", bankScr.getString(i));
					}
					list.add("은행을 선택해주세요");
					MySpinnerAdapter BankHintSpinner = new MySpinnerAdapter(
							HomeStore.this,
							android.R.layout.simple_spinner_item, list);
					BankHintSpinner
							.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					bank.setAdapter(BankHintSpinner);
					bank.setSelection(BankHintSpinner.getCount());
					Log.i("STEVEN", "DONE!!");
				} else {
					Log.d("-----------------------", "Login Failed");
				}
			} catch (Exception e) {

			}

		}
	}

	public void onClickBack(View view) {
		finish();
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
}
