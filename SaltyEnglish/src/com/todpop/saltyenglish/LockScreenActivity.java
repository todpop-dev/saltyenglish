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
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.todpop.api.FileManager;
import com.todpop.api.LockInfo;
import com.todpop.api.LockScreenClock;
import com.todpop.api.LockScreenClock.OnClockTickListner;
import com.todpop.api.request.LockScreenDownloadImage;
import com.todpop.api.VerticalViewPager;
import com.todpop.saltyenglish.db.LockerDBHelper;
import com.todpop.saltyenglish.db.WordDBHelper;

public class LockScreenActivity extends Activity {
	static int verticalPageCount = 1;
	
	static boolean active = true;
	
	//static LockScreenClock clock;
	
	VerticalViewPager verViewPager;
	
	ImageView arrowUp;
	ImageView arrowDown;
	
	static TextView hhmm;
	static TextView aa;
	static TextView date;
	
	SeekBar seekBar;
	
	TextView leftAmount;
	TextView rightAmount;
	
	ImageView leftImg;
	ImageView rightImg;

	static ArrayList<LockInfo> lockList;
	static ArrayList<String> engList;
	static ArrayList<String> korList;
	
	SharedPreferences rgInfo;
	String userId;
	
	static FileManager fm;
	
	WordDBHelper wHelper;
	SQLiteDatabase wDB;
	LockerDBHelper lHelper;
	SQLiteDatabase db;
	
	//MainApplication mainApp;
	
	Vibrator vibe;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_locker);
		Log.e("STEVEN", "onCreate");
		getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
						| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		
		fm = new FileManager();
		
		//mainApp = (MainApplication)getApplication();
		
		//mainApp.increaseLockerCnt();
		
		wHelper = new WordDBHelper(this);
		wDB = wHelper.getReadableDatabase();
		lHelper = new LockerDBHelper(this);

		rgInfo = getSharedPreferences("rgInfo",0);
		userId = rgInfo.getString("mem_id", "1");
		
		//new GetWord().execute("http://www.todpop.co.kr/api/screen_lock/word.json?user_id=" + userId);

		//LockScreenClock clock = new LockScreenClock(this, 0);
		
		verViewPager = (VerticalViewPager) findViewById(R.id.locker_id_viertical_view_pager);
		verViewPager.setAdapter(new DummyAdapter(getFragmentManager()));
		
		arrowUp = (ImageView)findViewById(R.id.locker_id_arrow_up);
		arrowDown = (ImageView)findViewById(R.id.locker_layout_id_arrow_down);
		
		hhmm = (TextView)findViewById(R.id.locker_id_text_clock);
		aa = (TextView)findViewById(R.id.locker_id_text_apm);
		date = (TextView)findViewById(R.id.locker_id_date);

		seekBar = (SeekBar) findViewById(R.id.locker_layout_id_seekbar);
		seekBar.setOnSeekBarChangeListener(new SeekBarChangeListener());
		
		leftAmount = (TextView)findViewById(R.id.locker_layout_id_left_text);
		rightAmount = (TextView)findViewById(R.id.locker_layout_id_right_text);
		
		leftImg = (ImageView)findViewById(R.id.locker_layout_id_left_img);
		rightImg = (ImageView)findViewById(R.id.locker_layout_id_right_img);

		vibe = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

		lockList = new ArrayList<LockInfo>();
		engList = new ArrayList<String>();
		korList = new ArrayList<String>();

		verViewPager.setOnPageChangeListener(new OnPageChangeListener(){

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageSelected(int position) {
				if(position == 0){
					arrowUp.setVisibility(View.INVISIBLE);
					leftAmount.setVisibility(View.INVISIBLE);
				}
				else{
					if(position == (verticalPageCount - 1)){
						arrowDown.setVisibility(View.INVISIBLE);
					}
					else{
						arrowUp.setVisibility(View.VISIBLE);
						arrowDown.setVisibility(View.VISIBLE);
					}
					
					int reward = lockList.get(position - 1).getReward();
					int point = lockList.get(position - 1).getPoint();
					
					if(reward != 0){
						leftAmount.setText("+" + reward);
						leftAmount.setVisibility(View.VISIBLE);
					}
					else if(point != 0){
						leftAmount.setText(point + "P");
						leftAmount.setVisibility(View.VISIBLE);
					}
					else{
						leftAmount.setVisibility(View.INVISIBLE);
					}
				}
			}
		});	
	}
	
	@Override
	protected void onResume(){
		super.onResume();

		Log.e("STEVEN", "onResume");
		LockScreenClock clock = new LockScreenClock(this, 0);
		clock.AddClockTickListner(new OnClockTickListner(){
			@Override
			public void OnSecondTick(Time currentTime){
				String time = DateFormat.format("hh:mm", currentTime.toMillis(true)).toString();
				if(time.charAt(0) == '0'){
					StringBuilder sb = new StringBuilder(time);
					sb.deleteCharAt(0);
					time = sb.toString();
				}
				hhmm.setText(time);
				String apm = DateFormat.format("a", currentTime.toMillis(true)).toString();
				if(apm.equals("오전")){
					apm = "AM";
				}
				else if(apm.equals("오후")){
					apm = "PM";
				}
				aa.setText(apm);
				date.setText(DateFormat.format("M.dd EEEE", currentTime.toMillis(true)).toString());
			}
			@Override
			public void OnMinuteTick(Time currentTime){
				
			}
		});
		
		db = lHelper.getReadableDatabase();
		Cursor find = db.rawQuery("SELECT * FROM latest order by category asc", null);
		int count = find.getCount();
		verticalPageCount = count + 1;
		if(count > 0){
			arrowDown.setVisibility(View.VISIBLE);
			find.moveToFirst();
			
			for(int i = 0; count > i; i++){
				LockInfo lockInfo = new LockInfo(find.getInt(1), find.getInt(2), find.getInt(3), 
					find.getString(4), find.getString(5), find.getInt(6), find.getInt(7));
				
				Cursor rePoCursor = db.rawQuery("SELECT reward, point FROM history WHERE category_id=\'" + lockInfo.getGroupId() + "\'", null);
				rePoCursor.moveToFirst();
				
				lockInfo.setReward(rePoCursor.getInt(0));
				lockInfo.setPoint(rePoCursor.getInt(1));
				
				lockList.add(i, lockInfo);
				find.moveToNext();
			}
		}
		else{
			arrowDown.setVisibility(View.INVISIBLE);
		}
		db.close();
		
		if(engList.size() != 10){
			int toFillSize = 10 - engList.size();
			
			// Add random words from db
			Cursor cursor = wDB.rawQuery("SELECT distinct name, mean FROM dic ORDER BY RANDOM() LIMIT " + toFillSize , null);
			
			cursor.moveToFirst();

			for(int i = 0; i < cursor.getCount(); i++){
				engList.add(cursor.getString(0));
				korList.add(cursor.getString(1).replace("/", "\n"));
				
				cursor.moveToNext();
			}
			if(engList.size() != 10){

				String loading = getResources().getString(R.string.popup_progress_loading);
				while(engList.size() < 10){
					engList.add(loading);
					korList.add(loading);
				}
			}
		}
		
		verViewPager.setAdapter(new DummyAdapter(getFragmentManager()));
	}
	@Override
	protected void onPause(){
		super.onPause();
		Log.e("STEVEN", "onPause");
	}
	@Override
	protected void onStop(){
		super.onStop();
		Log.e("STEVEN", "onStop");
		new GetWord().execute("http://www.todpop.co.kr/api/screen_lock/word.json?user_id=" + userId);
	}

	@Override
	protected void onDestroy(){
		super.onDestroy();
		Log.e("STEVEN", "onDestroy");
		//mainApp.decreaseLockerCnt();
	}
	public class DummyAdapter extends FragmentPagerAdapter {

		public DummyAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a PlaceholderFragment (defined as a static inner class
			// below).
			return PlaceholderFragment.newInstance(position + 1,
					LockScreenActivity.this);
		}

		@Override
		public int getCount() {
			return verticalPageCount;
		}
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		static Context context;
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber,
				Context c) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			context = c;
			return fragment;
		}

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView;
			Bundle args = getArguments();
			int position = args.getInt(ARG_SECTION_NUMBER);

			if (position == 1) {
				rootView = inflater.inflate(R.layout.fragment_locker_viewpager,
						container, false);
				ViewPager viewPager;

				viewPager = (ViewPager) rootView
						.findViewById(R.id.locker_fragment_id_viewpager);
				viewPager.setAdapter(new StudyWordAdapter(context));
			} 
			else {
				rootView = inflater.inflate(R.layout.fragment_locker_image,
						container, false);
				ImageView img;

				img = (ImageView) rootView
						.findViewById(R.id.locker_fragment_id_image);
				String gId = lockList.get(position - 2).getGroupId();
				
				img.setImageBitmap(fm.getImgFile(gId));
			}
			return rootView;
		}

		public class StudyWordAdapter extends PagerAdapter {
			private LayoutInflater mInflater;

			public StudyWordAdapter(Context c) {
				super();
				mInflater = LayoutInflater.from(c);
			}

			@Override
			public int getCount() {
				return 10;
			}

			@Override
			public Object instantiateItem(View container, int position) {
				// mInflater = LayoutInflater.from(LockScreenActivity.this);
				View v = null;
				v = mInflater.inflate(R.layout.fragment_locker_word, null);

				ImageView leftArrow = (ImageView)v.findViewById(R.id.locker_fragment_word_id_left);
				ImageView rightArrow = (ImageView)v.findViewById(R.id.locker_fragment_word_id_right);
				
				TextView english;
				TextView korean;

				english = (TextView) v
						.findViewById(R.id.locker_fragment_word_id_eng);
				korean = (TextView) v
						.findViewById(R.id.locker_fragment_word_id_kor);

				if(position == 0){
					leftArrow.setVisibility(View.INVISIBLE);
					rightArrow.setVisibility(View.VISIBLE);
				}
				else if(position == 9){
					leftArrow.setVisibility(View.VISIBLE);
					rightArrow.setVisibility(View.INVISIBLE);
				}
				
				//if(engList.size() != 0 && korList.size() != 0){
					english.setText(engList.get(position));
					korean.setText(korList.get(position));
				/*}
				else{
					wDB
					english.setText(getResources().getString(R.string.popup_progress_loading));
					korean.setText(getResources().getString(R.string.popup_progress_loading));
				}*/
				
				((ViewPager) container).addView(v, 0);
				return v;
			}

			@Override
			public void destroyItem(ViewGroup container, int position,
					Object object) {
				((ViewPager) container).removeView((View) object);
			}

			@Override
			public boolean isViewFromObject(View pager, Object obj) {
				return pager == obj;
			}
		}
	}

	private final class SeekBarChangeListener implements
			OnSeekBarChangeListener {
		boolean sideFlag = false;
		Drawable pressed = getResources().getDrawable(R.drawable.locker_common_btn_circle_pressed);
		Drawable normal = getResources().getDrawable(R.drawable.locker_common_btn_circle_normal);
		
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			int current = seekBar.getProgress();
			if (current == 100) {
				finish();
			} else if (current == 0) {
				int position = verViewPager.getCurrentItem();
				if(position == 0){
    				Intent intent = new Intent(getApplicationContext(), MainActivity.class);
    				startActivity(intent);
    				finish();
				}
				else{
					new SendLockLog().execute("http://www.todpop.co.kr/api/screen_lock/set_ad_log.json?user_id=" + userId 
							+ "&ad_type=" + lockList.get(position - 1).getType() + "&ad_id=" + lockList.get(position - 1).getId());
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(lockList.get(position - 1).getTargetUrl())));
					finish();
				}
			} else {
				seekBar.setProgress(50);
				seekBar.setThumb(normal);
				leftImg.setImageResource(R.drawable.locker_common_btn_view_normal);
				rightImg.setImageResource(R.drawable.locker_common_btn_unlock_normal);
			}
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			seekBar.setThumb(pressed);
			leftImg.setImageResource(R.drawable.locker_common_btn_view_pressed);
			rightImg.setImageResource(R.drawable.locker_common_btn_unlock_pressed);
		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			if (fromUser) {
				if (progress >= 85) {
					if (sideFlag == false) {
						vibe.vibrate(50);
						sideFlag = true;
					}
					seekBar.setThumb(null);
					seekBar.setProgress(100);
					sideFlag = true;
				} else if (progress <= 15) {
					if (sideFlag == false) {
						vibe.vibrate(50);
						sideFlag = true;
					}
					seekBar.setThumb(null);
					seekBar.setProgress(0);
				} else {
					seekBar.setThumb(pressed);
					sideFlag = false;
				}
			} else {
				return;
			}
		}
	}
	
	private class GetWord extends AsyncTask<String, Void, JSONObject> {

		@Override
		protected JSONObject doInBackground(String... urls) {
			JSONObject result = null;
			try
			{
				DefaultHttpClient httpClient;
				String getURL = urls[0];
				HttpGet httpGet = new HttpGet(getURL);
				HttpParams httpParameters = new BasicHttpParams(); 
				
				int timeoutConnection = 3000; 
				HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection); 
				int timeoutSocket = 5000; 
				HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket); 

				httpClient = new DefaultHttpClient(httpParameters); 
				HttpResponse httpResponse = httpClient.execute(httpGet);
				HttpEntity resEntity = httpResponse.getEntity();
				
				if (resEntity != null)
				{    
					result = new JSONObject(EntityUtils.toString(resEntity)); 
					Log.d("RESPONSE ---- ", result.toString());
					return result;
				}
				return result;
			}
			catch (Exception e)
			{
			    Log.e("STEVEN", e.toString());
            	return null;
			}
		}

		@Override
		protected void onPostExecute(JSONObject json) {
			try{
				if(json != null){
					if(json.getBoolean("status")){
						JSONArray wordsArray = json.getJSONObject("data").getJSONArray("word");
						JSONObject jsonObj = json.getJSONObject("data").getJSONObject("quiz");

						engList.clear();
						korList.clear();
						
						for(int i = 0; i < wordsArray.length(); i++){
							JSONArray eng_kor = wordsArray.getJSONArray(i);
							engList.add(eng_kor.getString(0));
							korList.add(eng_kor.getString(1).replace("/", "\n"));
						}
						
						if(!jsonObj.getString("image").equals("null")){
							int id = jsonObj.getInt("id");
							String image = jsonObj.getString("image");
							String target_url = jsonObj.getString("target_url");
							int reward = jsonObj.getInt("reward");
							int point = jsonObj.getInt("point");
	
							db = lHelper.getWritableDatabase();
							ContentValues row = new ContentValues();
							db.delete("latest", "category = 412", null);
							if(checkImgHistory(412, id)){	//if image file download history exist, store content in database
								row.put("category", 412);
								row.put("id", id);
								row.put("type", 412);
								row.put("image", image);
								row.put("target_url", target_url);
								row.put("reward", reward);
								row.put("point", point);
					
								db.insert("latest", null, row);
							}
							else{
								new LockScreenDownloadImage(LockScreenActivity.this, new LockInfo(412, id, 412, image, target_url, reward, point)).execute();
							}
							db.close();
						}
						verViewPager.setAdapter(new DummyAdapter(getFragmentManager()));
					}
				}
			}catch (Exception e){
				e.printStackTrace();
			}
		}
	}	
	
	private class SendLockLog extends AsyncTask<String, Void, JSONObject> {
		@Override
		protected JSONObject doInBackground(String... urls) 
		{
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
		protected void onPostExecute(JSONObject result) 
		{

			try {
				if	(result.getBoolean("status")==true) {

					int position = verViewPager.getCurrentItem();
					String category_id = lockList.get(position - 1).getGroupId();
					
					db = lHelper.getWritableDatabase();
					
					ContentValues values = new ContentValues();
					values.put("reward", 0);
					values.put("point", 0);
					
					db.update("history", values, "category_id = " + category_id, null);
					db.close();
				}

			} catch (Exception e) {

			}
		}
	}
	
	private boolean checkImgHistory(int group, int id){
		try{			
			String group_id = String.valueOf(group) + String.valueOf(id);

			Cursor find = db.rawQuery("SELECT distinct category_id FROM history WHERE category_id=\'" + group_id + "\'", null);
			
			if(find.getCount() > 0){
				return true;
			}
			else{
				return false;
			}
		}catch(Exception e){
			e.printStackTrace();
			return false;			
		}
	}
	
	/*private static OnClockTickListner onClockTick = new OnClockTickListner(){
		@Override
		public void OnSecondTick(Time currentTime){
			String time = DateFormat.format("hh:mm", currentTime.toMillis(true)).toString();
			if(time.charAt(0) == '0'){
				StringBuilder sb = new StringBuilder(time);
				sb.deleteCharAt(0);
				time = sb.toString();
			}
			hhmm.setText(time);
			String apm = DateFormat.format("a", currentTime.toMillis(true)).toString();
			if(apm.equals("오전")){
				apm = "AM";
			}
			else if(apm.equals("오후")){
				apm = "PM";
			}
			aa.setText(apm);
			date.setText(DateFormat.format("M.dd EEEE", currentTime.toMillis(true)).toString());
		}
		@Override
		public void OnMinuteTick(Time currentTime){
			
		}
	};*/
	
	@Override
	protected void onUserLeaveHint(){
		super.onUserLeaveHint();
		finish();
	}
	
	@Override
	public void onBackPressed(){
		//Do nothing
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return false;
	}
}