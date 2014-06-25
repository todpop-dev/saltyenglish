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
	private boolean isEditMode;
	private Button btnEditbarDel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_word_list_group);

		btnEditbarDel= (Button)findViewById(R.id.btn_wordlist_group_del);

		dbHelper = new WordDBHelper(getApplicationContext());
		lvGroups = (ListView)findViewById(R.id.lv_wordlist_group);

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
			}
		});
		lvGroups.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				WordGroup item = arrGroups.get(position);
				Intent intent = new Intent(getApplicationContext(),HomeWordListRenewal.class);
				intent.putExtra("groupName", item.title);
				startActivity(intent);
			}
		});

	}

	private void initGroupList() {
		arrGroups = new ArrayList<HomeWordListGroup.WordGroup>();

		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor allCursor = db.rawQuery("SELECT COUNT(*) FROM mywords", null);
		allCursor.moveToNext();
		arrGroups.add(new WordGroup("전체 단어", allCursor.getInt(0)));

		Cursor groupCursor = db.rawQuery("SELECT name FROM word_groups", null);
		while(groupCursor.moveToNext()){
			Cursor wordCursor = db.rawQuery("SELECT COUNT(*) FROM mywords WHERE group_name='"+groupCursor.getString(0)+"'", null); 
			wordCursor.moveToNext();
			arrGroups.add(new WordGroup(groupCursor.getString(0), wordCursor.getInt(0)));
		}

		adapter = new WordGroupAdapter(arrGroups);
		lvGroups.setAdapter(adapter);
	}

	public void addNewGroup(View v){
		popupNewGroup.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);
	}

	public void editGroup(View v){
		isEditMode = !isEditMode;
		adapter.notifyDataSetChanged();
		if(isEditMode){
			btnEditbarDel.setVisibility(View.VISIBLE);
		}else{
			btnEditbarDel.setVisibility(View.GONE);
		}
	}
	public void delGroup(View v){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		for(WordGroup group : arrGroups){
			if(group.isChecked){
				String query = "DELETE FROM word_groups WHERE name='"+group.title+"'";	
				db.execSQL(query);

				query = "DELETE FROM mywords WHERE group_name='"+group.title+"'";
				db.execSQL(query);
			}
		}
		initGroupList();
		adapter.notifyDataSetChanged();
	}

	public void onClickBack(View v){
		finish();
	}

	class WordGroup{
		String title;
		int cnt;
		boolean isChecked;
		public WordGroup(String title, int cnt){
			this.title = title;
			this.cnt = cnt;
			this.isChecked = false;
		}
	}

	class WordGroupAdapter extends BaseAdapter{

		class ViewHolder{
			CheckBox delChk;
			TextView title;
			TextView cnt;
			ImageView icon;
			ImageView arrow;
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
				holder.delChk = (CheckBox) convertView.findViewById(R.id.cb_wordlist_group_item_del);
				holder.icon = (ImageView) convertView.findViewById(R.id.iv_wordlist_group_item_book);
				holder.arrow = (ImageView) convertView.findViewById(R.id.iv_wordlist_group_item_arrow);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			final WordGroup item = (WordGroup) getItem(position);
			holder.title.setText(item.title);
			holder.cnt.setText("("+item.cnt+")");

			if(item.title.equals("전체 단어")){
				holder.icon.setBackgroundResource(R.drawable.wordbook_1_img_basicgroup);
			}

			if(isEditMode){
				holder.arrow.setVisibility(View.GONE);
				if(!item.title.equals("전체 단어")){
					holder.delChk.setVisibility(View.VISIBLE);
					holder.delChk.setChecked(item.isChecked);
					holder.delChk.setOnCheckedChangeListener(new OnCheckedChangeListener() {
						@Override
						public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
							item.isChecked = isChecked;
						}
					});
				}
			}else{
				holder.delChk.setVisibility(View.GONE);
				holder.arrow.setVisibility(View.VISIBLE);
			}
			return convertView;
		}

	}
	
	@Override
	protected void onResume() {
		super.onResume();
		initGroupList();
	}
}
