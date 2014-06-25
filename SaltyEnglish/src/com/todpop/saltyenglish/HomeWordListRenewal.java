package com.todpop.saltyenglish;

import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.animation.ObjectAnimator;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.todpop.api.TypefaceActivity;
import com.todpop.saltyenglish.HomeWordListGroup.WordGroup;
import com.todpop.saltyenglish.db.WordDBHelper;

public class HomeWordListRenewal extends TypefaceActivity {
	Point displaySize;
	private Button btnCard;
	private ObjectAnimator cardAni;
	boolean isBlinded=false;
	private String groupName;
	private ListView lvWordList;
	private ArrayList<HomeWordViewItem> arrWords;
	private HomeWordViewAdapter adapterWords;

	private boolean checkEdit=false;
	private LinearLayout llEditBar;
	private LinearLayout llSearchBar;
	private LinearLayout llEditBarBot;
	private WordDBHelper dbHelper;
	private EditText etSearchText;
	private LinearLayout mainLayout;

	private GroupPopupItem selectedItem;
	private int cntSelectedItem;
	private boolean isTestPopupShown;
	private View vPopupTest;
	private PopupWindow popupTest;
	private PopupWindow popupNewGroup;
	private View vPopupNewGroup;
	private EditText etPopupNewGroupTitle;
	private Object findViewById;
	private ImageView ivPopupNewGroupCancel;
	private ImageView ivPopupNewGroupSave;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_word_list_renewal);
		btnCard = (Button) findViewById(R.id.btn_wordlist_card);
		mainLayout = (LinearLayout)findViewById(R.id.ll_home_word_list);

		lvWordList = (ListView)findViewById(R.id.lv_wordlist);

		llEditBar = (LinearLayout)findViewById(R.id.ll_wordlist_edit_bar);
		llSearchBar = (LinearLayout)findViewById(R.id.ll_wordlist_search_bar);
		llEditBarBot = (LinearLayout)findViewById(R.id.ll_wordlist_edit_bar_bot);

		etSearchText = (EditText)findViewById(R.id.et_wordlist_search);
		etSearchText.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if( actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_SEARCH)) ){
					searchWord(etSearchText.getText().toString());
				}
				return false;
			}
		});

		initPopupView();
		initListeners();

		popupNewGroup = new PopupWindow(vPopupNewGroup, ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT,true);

		displaySize = new Point();
		getWindowManager().getDefaultDisplay().getSize(displaySize);
		initWords();
	}

	private void initPopupView() {
		vPopupNewGroup = LayoutInflater.from(getApplicationContext()).inflate(R.layout.popup_wordlist_group_new, null);
		etPopupNewGroupTitle = (EditText)vPopupNewGroup.findViewById(R.id.et_wordlist_group_new_title);
		ivPopupNewGroupCancel = (ImageView)vPopupNewGroup.findViewById(R.id.iv_wordlist_group_cancel);
		ivPopupNewGroupSave = (ImageView)vPopupNewGroup.findViewById(R.id.iv_wordlist_group_save);
	}

	private void initListeners() {
		ivPopupNewGroupCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				popupNewGroup.dismiss();
			}
		});

		ivPopupNewGroupSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SQLiteDatabase db = dbHelper.getWritableDatabase();
				ContentValues cv = new ContentValues();
				cv.put("name", etPopupNewGroupTitle.getText().toString());
				db.insert("word_groups", null, cv);
				popupNewGroup.dismiss();
			}
		});

	}

	private void searchWord(String string) {
		String keyword = etSearchText.getText().toString();
		if (keyword.length() != 0) {
			arrWords.clear();
			String url = "http://todpop.co.kr/api/studies/search_word.json?word="+keyword;
			new SerachWord().execute(url);
		} else {
			Toast.makeText(getApplicationContext(), "한글자 이상을 입력해주세요.", Toast.LENGTH_LONG).show();
		}

	}

	private void initWords() {
		groupName = getIntent().getExtras().getString("groupName");
		arrWords = new ArrayList<HomeWordViewItem>();
		dbHelper = new WordDBHelper(getApplicationContext());

		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String query=null;

		if(groupName.equals("전체 단어")){
			query = "SELECT name,mean FROM mywords"; 
		}else{
			query = "SELECT name,mean FROM mywords WHERE group_name='"+groupName+"'";	
		}

		Cursor cursor = db.rawQuery(query,null);
		while(cursor.moveToNext()){
			arrWords.add(new HomeWordViewItem(cursor.getString(0), cursor.getString(1), true));
		}

		adapterWords = new HomeWordViewAdapter(getApplicationContext(), R.layout.home_word_list_list_item_view, arrWords);
		lvWordList.setAdapter(adapterWords);
	}

	public void addNewGroup(View v){
		popupNewGroup.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);
	}

	public void cardBlind(View v) {
		if (!isBlinded) {
			cardAni = ObjectAnimator.ofFloat(btnCard,"translationX", -35f); 
			cardAni.setDuration(500);
			cardAni.start();
			isBlinded = true;
		} else {
			cardAni = ObjectAnimator.ofFloat(btnCard,"translationX",-displaySize.x/2); 
			cardAni.setDuration(500);
			cardAni.start();
			isBlinded = false;
		}
	}

	public void reverseWord(View v){
		ArrayList<HomeWordViewItem> arrClone = (ArrayList<HomeWordViewItem>)arrWords.clone();
		arrWords.clear();
		for(int i=0;i<arrClone.size();i++){
			HomeWordViewItem item = arrClone.get(i);
			arrWords.add(new HomeWordViewItem(item.word2, item.word1,true));
		}
		adapterWords.notifyDataSetChanged();
	}

	public void showTestPopup(View v){
		vPopupTest = LayoutInflater.from(getApplicationContext()).inflate(R.layout.popup_wordlist_test, null);
		vPopupTest.findViewById(R.id.iv_wordlist_test_15).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), WordListTestRenewal.class);
				intent.putExtra("testGroupName", groupName);
				if (arrWords.size() >= 15) {
					intent.putExtra("testListSize", 15);
				} else {
					intent.putExtra("testListSize", arrWords.size());
				}
				popupTest.dismiss();
				startActivity(intent);
				finish();
			}
		});

		vPopupTest.findViewById(R.id.iv_wordlist_test_30).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), WordListTestRenewal.class);
				intent.putExtra("testGroupName", groupName);
				if (arrWords.size() >= 30) {
					intent.putExtra("testListSize", 30);
				} else {
					intent.putExtra("testListSize", arrWords.size());
				}
				popupTest.dismiss();
				startActivity(intent);
				finish();
			}
		});

		vPopupTest.findViewById(R.id.iv_wordlist_test_all).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), WordListTestRenewal.class);
				intent.putExtra("testGroupName", groupName);
				intent.putExtra("testListSize", arrWords.size());
				popupTest.dismiss();
				startActivity(intent);
				finish();
			}
		});

		popupTest = new PopupWindow(vPopupTest, ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT,true);
		popupTest.setOutsideTouchable(true);
		popupTest.setBackgroundDrawable(new BitmapDrawable()) ;
		popupTest.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);
		isTestPopupShown=true;
	}

	public void editWord(View v){
		checkEdit = !checkEdit;
		initWords();
		adapterWords.notifyDataSetChanged();

		if(!checkEdit) {
			btnCard.setVisibility(View.VISIBLE);
			llEditBar.setVisibility(View.GONE);
			llSearchBar.setVisibility(View.VISIBLE);
			llEditBarBot.setVisibility(View.GONE);
		} else {
			btnCard.setVisibility(View.GONE);
			llEditBar.setVisibility(View.VISIBLE);
			llSearchBar.setVisibility(View.GONE);
			llEditBarBot.setVisibility(View.VISIBLE);
		}

	}

	public void allSelect(View v){
		for(int i=0;i<arrWords.size();i++){
			arrWords.get(i).isChecked = true;
		}
		adapterWords.notifyDataSetChanged();
	}

	public void exportWords(View v){
		View vPopupExport = LayoutInflater.from(getApplicationContext()).inflate(R.layout.popup_wordlist_export, null);
		final PopupWindow popupExport = new PopupWindow(vPopupExport, ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT,true);

		ListView lvPopupGroup = (ListView) vPopupExport.findViewById(R.id.lv_wordlist_export_popup_group);

		final ArrayList<GroupPopupItem> arrGroups = new ArrayList<HomeWordListRenewal.GroupPopupItem>();

		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor groupCursor = db.rawQuery("SELECT name FROM word_groups", null);
		while(groupCursor.moveToNext()){
			Cursor wordCursor = db.rawQuery("SELECT COUNT(*) FROM mywords WHERE group_name='"+groupCursor.getString(0)+"'", null); 
			wordCursor.moveToNext();
			arrGroups.add(new GroupPopupItem(groupCursor.getString(0), wordCursor.getInt(0)));
		}
		final GroupPopupAdapter adapter = new GroupPopupAdapter(arrGroups);

		lvPopupGroup.setAdapter(adapter);

		lvPopupGroup.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(selectedItem != null)
					selectedItem.isSelected = false;
				selectedItem = arrGroups.get(position);
				selectedItem.isSelected = true;
				adapter.notifyDataSetChanged();
			}
		});

		vPopupExport.findViewById(R.id.iv_wordlist_export_popup_cancel).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				popupExport.dismiss();
			}
		});
		vPopupExport.findViewById(R.id.iv_wordlist_export_popup_save).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ArrayList<HomeWordViewItem> arrWordsClone = (ArrayList<HomeWordViewItem>) arrWords.clone();

				SQLiteDatabase db = dbHelper.getWritableDatabase();
				for ( HomeWordViewItem word : arrWordsClone) {
					if(word.isChecked){
						db.execSQL("UPDATE mywords SET group_name='"+selectedItem.groupName+"' WHERE name='"+word.word1+"'");

						for(int i=0;i<arrWords.size();i++){
							HomeWordViewItem item = arrWords.get(i);
							if(item.word1.equals(word.word1)){
								arrWords.remove(i);
								break;
							}
						}

					}
				}

				initWords();
				adapterWords.notifyDataSetChanged();
				popupExport.dismiss();
			}
		});

		popupExport.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);
		adapterWords.notifyDataSetChanged();
	}
	public void deleteWords(View v){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		for(int i=0;i<arrWords.size();i++){
			HomeWordViewItem item = arrWords.get(i);
			if(item.isChecked == true){
				db.execSQL("DELETE FROM mywords WHERE group_name='"+groupName+"' and name='"+item.word1+"'");
			}
		}
		initWords();
		adapterWords.notifyDataSetChanged();
	}

	class GroupPopupItem{
		String groupName;
		int cnt;
		boolean isSelected;
		public GroupPopupItem(String groupName,int cnt) {
			this.groupName = groupName;
			this.cnt = cnt;
			this.isSelected = false;
		}
	}

	class GroupPopupAdapter extends BaseAdapter{
		ArrayList<GroupPopupItem> arrGroups;
		public GroupPopupAdapter(ArrayList<GroupPopupItem> arrGroups) {
			this.arrGroups = arrGroups;
		}

		@Override
		public int getCount() {
			return arrGroups.size();
		}

		@Override
		public GroupPopupItem getItem(int position) {
			return arrGroups.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			if(convertView == null){
				viewHolder =  new ViewHolder();
				convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.home_word_list_export_item_view, null);
				viewHolder.groupName = (TextView) convertView.findViewById(R.id.tv_wordlist_export_item_group_name);
				viewHolder.cnt = (TextView) convertView.findViewById(R.id.tv_wordlist_export_item_group_cnt);
				convertView.setTag(viewHolder);
			}else{
				viewHolder = (ViewHolder) convertView.getTag();
			}
			GroupPopupItem item = getItem(position);
			viewHolder.groupName.setText(item.groupName);
			viewHolder.cnt.setText("("+item.cnt+")");

			if(item.isSelected){
				convertView.setBackgroundResource(R.drawable.wordbook_5_img_popup_selectbox);
			}else{
				convertView.setBackgroundColor(getResources().getColor(android.R.color.transparent));
			}

			return convertView;
		}
		class ViewHolder{
			TextView groupName;
			TextView cnt;
		}

	}


	class HomeWordViewItem {
		String word1;
		String word2;
		boolean isChecked;
		boolean isMine;

		HomeWordViewItem(String aWord1,String aWord2){
			word1 = aWord1;
			word2 = aWord2;
			isChecked = false;
			isMine = false;
		}
		HomeWordViewItem(String aWord1,String aWord2,boolean isMine){
			word1 = aWord1;
			word2 = aWord2;
			isChecked = false;
			this.isMine = isMine;
		}
	}

	class HomeWordViewAdapter extends BaseAdapter{
		Context maincon;
		LayoutInflater Inflater;
		ArrayList<HomeWordViewItem> arSrc;
		int layout;

		public HomeWordViewAdapter(Context context,int alayout,ArrayList<HomeWordViewItem> aarSrc){
			maincon = context;
			Inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			arSrc = aarSrc;
			layout = alayout;
		}

		public int getCount(){
			return arSrc.size();
		}

		public HomeWordViewItem getItem(int position){
			return arSrc.get(position);
		}

		public long getItemId(int position){
			return position;
		}

		public View getView(final int position,View convertView,ViewGroup parent){
			View v = convertView;
			ViewHolder viewHolder;
			if (v == null) {
				viewHolder = new ViewHolder();
				v = Inflater.inflate(layout, parent,false);
				viewHolder.textEn = (TextView)v.findViewById(R.id.home_word_list_id_word1);
				viewHolder.textKr = (TextView)v.findViewById(R.id.home_word_list_id_word2);
				viewHolder.select = (CheckBox)v.findViewById(R.id.home_word_list_id_check);
				viewHolder.add = (Button)v.findViewById(R.id.home_word_list_id_add);
				viewHolder.add.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						ViewHolder holder = (ViewHolder) v.getTag();
						SQLiteDatabase db = dbHelper.getWritableDatabase();
						ContentValues cv = new ContentValues();
						cv.put("name", holder.textEn.getText().toString());
						cv.put("mean", holder.textKr.getText().toString());
						cv.put("group_name", groupName);
						db.insert("mywords", null, cv);

						initWords();
						adapterWords.notifyDataSetChanged();						
					}
				});

				setFont(viewHolder.textEn);
				setFont(viewHolder.textKr);

				v.setTag(viewHolder);
				viewHolder.add.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder)v.getTag();
			}

			if(!checkEdit) {
				viewHolder.select.setVisibility(View.INVISIBLE);
			} else {
				viewHolder.select.setVisibility(View.VISIBLE);
			}

			if(getItem(position).isMine){
				viewHolder.add.setVisibility(View.GONE);
			} else {
				viewHolder.add.setVisibility(View.VISIBLE);
			}

			viewHolder.select.setChecked(getItem(position).isChecked);

			viewHolder.textEn.setText(arSrc.get(position).word1);
			viewHolder.textEn.setTag(position);

			viewHolder.textKr.setText(arSrc.get(position).word2);
			viewHolder.textKr.setTag(position);

			viewHolder.select.setTag(position);
			viewHolder.select.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					getItem(position).isChecked = isChecked;
				}
			});

			v.setBackgroundResource(R.drawable.common_img_bar);

			return v;
		}

	}

	class ViewHolder {
		public TextView textEn = null;
		public TextView textKr = null;
		public CheckBox select = null;
		public Button add = null;
	}

	private class SerachWord extends AsyncTask<String, Void, JSONObject> {
		DefaultHttpClient httpClient ;
		@Override
		protected JSONObject doInBackground(String... urls) {
			JSONObject result = null;
			try {
				String getURL = urls[0];
				HttpGet httpGet = new HttpGet(getURL); 
				HttpParams httpParameters = new BasicHttpParams(); 
				int timeoutConnection = 5000; 
				HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection); 
				int timeoutSocket = 5000; 
				HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket); 

				httpClient = new DefaultHttpClient(httpParameters); 
				HttpResponse response = httpClient.execute(httpGet); 
				HttpEntity resEntity = response.getEntity();
				if (resEntity != null) {    
					result = new JSONObject(EntityUtils.toString(resEntity)); 
				}
				return result;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		}

		@Override
		protected void onPostExecute(JSONObject json) {
			Log.e("Get Result JSON RESPONSE ---- ", json.toString());				        	

			try {
				if(json.getBoolean("status")==true) {
					JSONArray resultArr = json.getJSONArray("words");
					Log.e("ResultArr!!Words",resultArr.toString());
					for (int i = 0; i < resultArr.length() ; i++) {
						String name = resultArr.getJSONObject(i).get("name").toString();
						String mean = resultArr.getJSONObject(i).get("mean").toString();
						HomeWordViewItem item = new HomeWordViewItem(name, mean, false);
						arrWords.add(item);
					}

					adapterWords.notifyDataSetChanged();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	};

	// on click
	public void onClickBack(View v) {
		finish();
	}

	@Override
	protected void onResume() 
	{
		super.onResume();

		cardAni = ObjectAnimator.ofFloat(btnCard,"translationX",-displaySize.x/2); 
		cardAni.setDuration(500);
		cardAni.start();
	}


}
