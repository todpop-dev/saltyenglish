package com.todpop.saltyenglish;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.todpop.api.TypefaceActivity;
import com.todpop.saltyenglish.db.WordDBHelper;

public class HomeWordListGroup extends TypefaceActivity {


	private ListView lvGroups;
	private WordGroupAdapter adapter;
	private ArrayList<WordGroup> arrGroups;
	private WordDBHelper dbHelper;
	private View vPopupNewGroup;
	private PopupWindow popupNewGroup;
	private LinearLayout mainLayout;
	private EditText etPopupNewGroupTitle;
	private ImageView ivPopupNewGroupCancel;
	private ImageView ivPopupNewGroupSave;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_word_list_group);

		dbHelper = new WordDBHelper(getApplicationContext());
		arrGroups = new ArrayList<HomeWordListGroup.WordGroup>();
		lvGroups = (ListView)findViewById(R.id.lv_wordlist_group);

		initGroupList();

		adapter = new WordGroupAdapter(arrGroups);
		lvGroups.setAdapter(adapter);

		mainLayout = (LinearLayout)findViewById(R.id.ll_home_word_list_group);

		initPopupView();
		initListeners();

		popupNewGroup = new PopupWindow(vPopupNewGroup, ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT,true);

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

				Intent intent = new Intent(getApplicationContext(),HomeWordListRenewal.class);
				intent.putExtra("groupName", cv.getAsString("name"));
				startActivity(intent);
				finish();
			}
		});
		lvGroups.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				WordGroup item = arrGroups.get(position);
				Intent intent = new Intent(getApplicationContext(),HomeWordListRenewal.class);
				intent.putExtra("groupName", item.getTitle());
				startActivity(intent);
				finish();
			}
		});

	}

	private void initGroupList() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor groupCursor = db.rawQuery("SELECT name FROM word_groups", null);
		while(groupCursor.moveToNext()){
			Cursor wordCursor = db.rawQuery("SELECT COUNT(*) FROM mywords WHERE group_name='"+groupCursor.getString(0)+"'", null); 
			wordCursor.moveToNext();
			arrGroups.add(new WordGroup(groupCursor.getString(0), wordCursor.getInt(0)));
		}
	}

	public void addNewGroup(View v){
		popupNewGroup.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);
	}

	class WordGroup{
		private String title;
		private int cnt;
		public WordGroup(String title, int cnt){
			this.setTitle(title);
			this.setCnt(cnt);
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public int getCnt() {
			return cnt;
		}
		public void setCnt(int cnt) {
			this.cnt = cnt;
		}
	}

	class WordGroupAdapter extends BaseAdapter{

		class ViewHolder{
			TextView title;
			TextView cnt;
		}

		ArrayList<WordGroup> arrGroups;

		public WordGroupAdapter(ArrayList<WordGroup> arrGroups) {
			this.arrGroups = arrGroups;
		}

		@Override
		public int getCount() {
			return arrGroups.size();
		}

		@Override
		public Object getItem(int position) {
			return arrGroups.get(position);
		}

		@Override
		public long getItemId(int position) {
			return arrGroups.hashCode();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.wordlist_group_item, null);
				holder.title = (TextView) convertView.findViewById(R.id.tv_wordlist_group_item_title);
				holder.cnt = (TextView) convertView.findViewById(R.id.tv_wordlist_group_item_cnt);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			WordGroup item = (WordGroup) getItem(position);
			holder.title.setText(item.title);
			holder.cnt.setText("("+item.cnt+")");

			return convertView;
		}

	}
}
