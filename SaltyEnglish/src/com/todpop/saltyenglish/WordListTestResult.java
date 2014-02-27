package com.todpop.saltyenglish;

import java.util.ArrayList;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;
import com.todpop.saltyenglish.StudyTestResult.MyItem;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class WordListTestResult extends Activity {

	ViewHolder viewHolder = null;

	ArrayList<MyItem> arItem;

	// Database
	WordDBHelper mHelper;

	int wordListSize = 0;
	int wordCorrectCnt = 0;
	MyItem mi;

	ArrayList<String> enArray = new ArrayList<String>();
	ArrayList<String> krArray = new ArrayList<String>();

	SharedPreferences studyInfo;
	SharedPreferences.Editor studyInfoEdit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wordlist_test_result);

		FlurryAgent.logEvent("Level Test Result");

		mHelper = new WordDBHelper(this);

		studyInfo = getSharedPreferences("studyInfo", 0);
		studyInfoEdit = studyInfo.edit();

		arItem = new ArrayList<MyItem>();

		SQLiteDatabase db = mHelper.getWritableDatabase();
		try {
			db.execSQL("CREATE TABLE mywords ( _id INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ "name TEXT NOT NULL UNIQUE, mean TEXT);");
		} catch (Exception e) {
			e.printStackTrace();
		}

		getTestWords();

		TextView levelShow = (TextView) findViewById(R.id.wordlisttestresult_result_id_level);
		String resultShowA = getResources().getString(R.string.word_list_test_result_a);
		String resultShowB = getResources().getString(R.string.word_list_test_result_b);
		levelShow.setText(String.valueOf(wordListSize) + resultShowA + String.valueOf(wordCorrectCnt) +
				resultShowB);

		MyListAdapter MyAdapter = new MyListAdapter(this,
				R.layout.lvtest_result_list_item_view, arItem);

		ListView MyList;
		MyList = (ListView) findViewById(R.id.wordlisttestresult_id_listview);
		MyList.setAdapter(MyAdapter);
	}

	private void getTestWords() {

		try {
			SQLiteDatabase db = mHelper.getReadableDatabase();
			Cursor cursor = db.rawQuery("SELECT name, mean, xo FROM mywordtest;",
					null);

			Log.e("cursor.getCount()",
					"cursor.getCount() : " + cursor.getCount());
			wordListSize = cursor.getCount();
			if (cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					Log.i("STEVEN ABC",
							cursor.getString(0) + "  " + cursor.getString(1)
									+ "   " + cursor.getString(2));
					mi = new MyItem(cursor.getString(0), cursor.getString(1),
							cursor.getString(2));
					arItem.add(mi);
					if(cursor.getString(2).equals("O")){
						wordCorrectCnt++;
					}
				}
				db.delete("mywordtest", null, null);
			}
		} catch (Exception e) {
			Log.e("AFDSDFDSFSDFDSF", "catch error");
			e.printStackTrace();
		}

	}

	class MyItem {
		MyItem(String aEn, String aKr, String Check) {
			en = aEn;
			kr = aKr;
			check = Check;
		}

		String en;
		String kr;
		String check;
	}

	class MyListAdapter extends BaseAdapter {
		Context maincon;
		LayoutInflater Inflater;
		ArrayList<MyItem> arSrc;
		int layout;

		public MyListAdapter(Context context, int alayout,
				ArrayList<MyItem> aarSrc) {
			maincon = context;
			Inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			arSrc = aarSrc;
			layout = alayout;
			for (int i = 0; i < wordListSize; i++) {
				Log.e("RESULT 129", arSrc.get(i).en + arSrc.get(i).kr);

			}
		}

		public int getCount() {

			return arSrc.size();
		}

		public String getItem(int position) {
			return arSrc.get(position).en;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (convertView == null) {
				convertView = Inflater.inflate(layout, parent, false);

			}
			viewHolder = new ViewHolder();
			v = Inflater.inflate(layout, parent, false);
			viewHolder.textEn = (TextView) v.findViewById(R.id.lv_test_english);
			viewHolder.textKr = (TextView) v.findViewById(R.id.lv_test_kr);
			viewHolder.checkView = (ImageView) v
					.findViewById(R.id.lv_test_check_correct);
			viewHolder.selectBtn = (CheckBox) v.findViewById(R.id.lv_test_btn);

			// v.setTag(viewHolder);

			// else {
			// viewHolder = (ViewHolder)v.getTag();
			// }

			viewHolder.textEn.setText(arSrc.get(position).en);
			viewHolder.textKr.setText(arSrc.get(position).kr);

			viewHolder.textEn.setTag(position);
			// viewHolder.textKr.setTag(position);
			viewHolder.selectBtn.setTag(position);

			Log.d("LvTestResult", "line 169" + "positoin = " + position + " "
					+ arSrc.get(position).en + arSrc.get(position).kr);
			// if(enSave != null)
			// {
			// if(enSave.get(position).equals(arSrc.get(position).en))
			// {
			// viewHolder.selectBtn.setEnabled(false);
			// }else{
			// viewHolder.selectBtn.setEnabled(true);
			// }
			// }

			if (arSrc.get(position).check.equals("O")) {
				viewHolder.checkView
						.setImageResource(R.drawable.lvtest_10_text_correct);
			} else {
				viewHolder.checkView
						.setImageResource(R.drawable.lvtest_10_text_incorrect);
			}

			if (position % 2 == 1) {
				v.setBackgroundResource(R.drawable.lvtest_10_image_separatebox_blue_center);
			} else {
				v.setBackgroundResource(R.drawable.lvtest_10_image_separatebox_skyblue_center);
			}

			// Check if word is in word list
			SQLiteDatabase db = mHelper.getWritableDatabase();
			Cursor c = db.rawQuery(
					"SELECT * FROM mywords WHERE name='"
							+ arSrc.get(position).en + "'", null);
			if (c.getCount() > 0) {
				viewHolder.selectBtn.setChecked(true);
			} else {
				viewHolder.selectBtn.setChecked(false);
			}

			viewHolder.selectBtn
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {

							if (isChecked) {
								// Insert word to DB
								SQLiteDatabase db = mHelper
										.getWritableDatabase();

								ContentValues cv = new ContentValues();
								cv.put("name", arSrc.get((Integer) (buttonView
										.getTag())).en);
								cv.put("mean", arSrc.get((Integer) (buttonView
										.getTag())).kr);
								db.replace("mywords", null, cv);
							} else {
								// Delete word to DB
								SQLiteDatabase db = mHelper
										.getWritableDatabase();
								try {
									db.delete(
											"mywords",
											"name='"
													+ arSrc.get((Integer) (buttonView
															.getTag())).en
													+ "'", null);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					});

			return v;
		}
	}

	class ViewHolder {
		public TextView textEn = null;
		public TextView textKr = null;
		public ImageView checkView = null;
		public CheckBox selectBtn = null;

	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finish();
			return false;
		}
		return false;
	}

	public void continueActivity(View v) {
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.lvtest_result, menu);
		return false;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mHelper.close();
	}

	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(this, "ZKWGFP6HKJ33Y69SP5QY");
	    EasyTracker.getInstance(this).activityStart(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	    EasyTracker.getInstance(this).activityStop(this);
	}
}
