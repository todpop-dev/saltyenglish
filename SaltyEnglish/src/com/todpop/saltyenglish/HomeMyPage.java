package com.todpop.saltyenglish;


import java.io.InputStream;
import java.net.URL;
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

import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class HomeMyPage extends FragmentActivity {

	ViewPager pageView;
	PagerAdapter pagerAdapter;
	LevelFragment levelFragment;

	static ArrayList<String> rankImageList = new ArrayList<String>();
	static ArrayList<String> rankNickNameList = new ArrayList<String>();

	int myLevel = 0;
	int myRank = 0 ;
	int myAttendance = 0;
	int myPoint = 0;
	int myRemainPoint = 0;
	int myRewardTotay = 0;
	int myRewardCurrent = 0;
	int myRewardTotal = 0;
	
	TextView levelBox;
	TextView rankBox;
	Button attendanceBtn;
	TextView PointBox;
	TextView remainBox;
	TextView rewardTotayBox;
	TextView rewardcurrentBox;
	TextView rewardTotalBox;
	TextView myNicknameBox;
	
	
	String prideImageUrl;
	
	static ArrayList<Bitmap> prizeImageArr;
	
	ImageView characterBtn;
	
	static int prizeImageCount = 0;

	SharedPreferences rgInfo;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_my_page);
		
		rgInfo = getSharedPreferences("rgInfo",0);
		
		characterBtn = (ImageView)findViewById(R.id.home_mypage_id_character_btn);		
		levelBox = (TextView)findViewById(R.id.home_mypage_id_level_text);
		rankBox = (TextView)findViewById(R.id.home_mypage_id_myrank_text);
		attendanceBtn = (Button)findViewById(R.id.home_mypage_id_attendance_btn);
		PointBox = (TextView)findViewById(R.id.home_mypage_id_mypoint_text);
		remainBox = (TextView)findViewById(R.id.home_mypage_id_myremain_text);
		rewardTotayBox = (TextView)findViewById(R.id.home_mypage_id_rewardtotay_text);
		rewardcurrentBox = (TextView)findViewById(R.id.home_mypage_id_rewardcurrent_text);
		rewardTotalBox = (TextView)findViewById(R.id.home_mypage_id_rewardtotal_text);
		myNicknameBox = (TextView)findViewById(R.id.home_mypage_id_my_nickname);
		
		myNicknameBox.setText(rgInfo.getString("nickname", "NO"));
		
		prizeImageArr = new ArrayList<Bitmap>();
		
		for (int i=0;i<3;i++)
		{
			rankImageList.add(i,"null");
			rankNickNameList.add(i,"null");
		}	
		
		SharedPreferences rgInfo = getSharedPreferences("rgInfo",0);
		int level = Integer.parseInt(rgInfo.getString("level", "0"));
		int period = rgInfo.getInt("period", 1);
		int category = 0;
			if(level >= 1 && level <= 15) {
				category = 1;
			} else if(level >=16 && level <= 60) {
				category = 2;
			} else if(level >=61 && level <= 120) {
				category = 3;
			} else if(level >=121 && level <= 180) {
				category = 4;
			}
		
		new GetRankInfo().execute("http://todpop.co.kr/api/etc/"+rgInfo.getString("mem_id", "NO")+"/my_home.json?category=" + category + "&period=" + period);
	}

	@Override
	public void onResume()
	{
		super.onResume();
		
		new CheckCharater().execute("http://todpop.co.kr/api/etc/"+rgInfo.getString("mem_id", "NO")+"/character.json");
		
		
	}

	public  class PagerAdapter extends FragmentStatePagerAdapter {

		public PagerAdapter(FragmentManager fm) {
			super(fm);
		}
		@Override
		public Fragment getItem(int i) {
			Fragment fragment = new LevelFragment();

			Bundle args = new Bundle();
			args.putInt("page",i);
			//args.putString("nickName", rankNickNameList.get(i));
			fragment.setArguments(args);
			
			return fragment;
		}

		@Override
		public int getCount() {
			return 3;
		}
	}

	public static class LevelFragment extends Fragment {

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_home_my_page_runking, container, false);

			ImageView indicatorL = (ImageView)rootView.findViewById(R.id.left);
			ImageView indicatorR = (ImageView)rootView.findViewById(R.id.right);
			
			ImageView rankImage = (ImageView)rootView.findViewById(R.id.home_mypage_id_rankingid_img);
			ImageView iamge = (ImageView)rootView.findViewById(R.id.home_mypage_id_ranking_img);
			TextView nickName = (TextView)rootView.findViewById(R.id.home_mypage_id_ranking_nickName);

			Bundle args = getArguments();
//			if(args.getInt("page") ==1)
//			{
//				indicatorL.setVisibility(View.VISIBLE);
//				indicatorR.setVisibility(View.VISIBLE);
//			}else{
//				indicatorL.setVisibility(View.GONE);
//				indicatorR.setVisibility(View.GONE);
//			}
//			if(args.getInt("page") ==0)
//			{
//				//nickName.setText(args.getInt("nickName"));
//			}if(args.getInt("page") ==1){
//				nickName.setText(rankNickNameList.get(0));
//			}if(args.getInt("page") ==2){
//				nickName.setText(rankNickNameList.get(1));
//			}
			
			switch(args.getInt("page"))
			{
				case 0:
					//rankImage.setImageResource(R.drawable.store_31_image_2nd);
					rankImage.setImageBitmap(prizeImageArr.get(2));
					
					nickName.setText(rankNickNameList.get(2));
					Log.d("+++++++++++++++++++++","1");
					indicatorL.setVisibility(View.GONE);
					indicatorR.setVisibility(View.GONE);
				break;
				case 1:
					Log.d("+++++++++++++++++++++","2");
					//rankImage.setImageResource(R.drawable.store_31_image_1st);
					rankImage.setImageBitmap(prizeImageArr.get(0));

					nickName.setText(rankNickNameList.get(0));
					indicatorL.setVisibility(View.VISIBLE);
					indicatorR.setVisibility(View.VISIBLE);
				break;
				case 2:
					Log.d("+++++++++++++++++++++","3");
					//rankImage.setImageResource(R.drawable.store_31_image_3rd);
					rankImage.setImageBitmap(prizeImageArr.get(1));

					nickName.setText(rankNickNameList.get(1));
					indicatorL.setVisibility(View.GONE);
					indicatorR.setVisibility(View.GONE);
				break;
			}
			
			
			return rootView;
		}
	}

	//--- request class ---
	private class GetRankInfo extends AsyncTask<String, Void, JSONObject> 
	{
		DefaultHttpClient httpClient ;
		@Override
		protected JSONObject doInBackground(String... urls) 
		{
			JSONObject result = null;
			try
			{
				String getURL = urls[0];
				HttpGet httpGet = new HttpGet(getURL); 
				HttpParams httpParameters = new BasicHttpParams(); 
				httpClient = new DefaultHttpClient(httpParameters); 
				HttpResponse response = httpClient.execute(httpGet); 
				HttpEntity resEntity = response.getEntity();


				if (resEntity != null)
				{    
					result = new JSONObject(EntityUtils.toString(resEntity)); 
					Log.d("RESPONSE JSON ---- ", result.toString());				        	
				}
				return result;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			return result;
		}

		@Override
		protected void onPostExecute(JSONObject json) {
			try {
				if(json.getBoolean("status") == true) {
					myLevel = json.getJSONObject("data").getInt("my_level");
					myRank = json.getJSONObject("data").getInt("my_rank");
					myAttendance = json.getJSONObject("data").getInt("attendance");
					myPoint = json.getJSONObject("data").getInt("my_point");
					myRemainPoint = json.getJSONObject("data").getInt("remain_point");
					myRewardTotay = json.getJSONObject("data").getInt("reward_today");
					myRewardCurrent = json.getJSONObject("data").getInt("reward_current");
					myRewardTotal = json.getJSONObject("data").getInt("reward_total");
			
					
					levelBox.setText("Lv."+myLevel);
					rankBox.setText(Integer.toString(myRank));
					String atdText1 = getString(R.string.home_page_attendance_text1);
					String atdText2 = getString(R.string.home_page_attendance_text2);
					attendanceBtn.setText(atdText1+myAttendance+atdText2);
					PointBox.setText(Integer.toString(myPoint)+getString(R.string.testname7));
					remainBox.setText(Integer.toString(myRemainPoint)+getString(R.string.testname7));
					rewardTotayBox.setText(Integer.toString(myRewardTotay)+getString(R.string.testname8));
					rewardcurrentBox.setText(Integer.toString(myRewardCurrent)+getString(R.string.testname8));
					rewardTotalBox.setText(Integer.toString(myRewardTotal)+getString(R.string.testname8));
					
					JSONArray jsonArray = json.getJSONObject("data").getJSONArray("prize");
					for (int i=0;i<jsonArray.length();i++) {
						rankImageList.add(i,jsonArray.getJSONObject(i).getString("image"));
						rankNickNameList.add(i,jsonArray.getJSONObject(i).getString("nickname"));
						
						String imgUrl = "http://todpop.co.kr" + jsonArray.getJSONObject(i).getJSONObject("image").getJSONObject("image").getJSONObject("thumb").getString("url");
						URL url = new URL(imgUrl);
						Log.d("prize url ------ ", url.toString());
						new DownloadImageTask()
						            .execute(url.toString());
					}	
							
					
					pageView = (ViewPager)findViewById(R.id.pager);
					pageView.setClipChildren(false);
					pageView.setAdapter(pagerAdapter);
					pageView.setOffscreenPageLimit(pagerAdapter.getCount());
					pageView.setPageMargin(-210);
					pageView.setClipChildren(false);
					pageView.setCurrentItem(1);
					
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
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

		    protected void onPostExecute(Bitmap result) 
		    {
		        //bmImage.setImageBitmap(result);
		    	prizeImageArr.add(result);

		    	if (prizeImageArr.size()==3) {
					pagerAdapter = new PagerAdapter(getSupportFragmentManager());
		    	}

		    }
		}
	}
	
	
	private class CheckCharater extends AsyncTask<String, Void, JSONObject> 
	{
		@Override
		protected JSONObject doInBackground(String... urls) 
		{
			JSONObject result = null;
			try
			{
				String getURL = urls[0];
				HttpGet httpGet = new HttpGet(getURL); 
				HttpParams httpParameters = new BasicHttpParams(); 
				DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters); 
				HttpResponse response = httpClient.execute(httpGet); 
				HttpEntity resEntity = response.getEntity();

				if (resEntity != null)
				{    
					result = new JSONObject(EntityUtils.toString(resEntity)); 
					Log.d("RESPONSE JSON ---- ", result.toString());				        	
				}
				return result;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		
			return result;
		}

		@Override
		protected void onPostExecute(JSONObject json) {

			try {
				if(json.getBoolean("status")==true) 
				{
					setCharacter(json.getJSONObject("data").getString("url"));
					
					Log.d("character  url",json.getJSONObject("data").getString("url"));
				} else {		      
				
				}

			} catch (Exception e) {

			}
		}
	}
	
	public void setCharacter(String imageID)
	{		
		if(imageID.equals("1"))
		{
			characterBtn.setBackgroundResource(R.drawable.store_44_btn_eric_off);
		}else if(imageID.equals("2")){
			characterBtn.setBackgroundResource(R.drawable.store_44_btn_selly_off);
		}else if(imageID.equals("3")){
			characterBtn.setBackgroundResource(R.drawable.store_44_btn_john_off);
		}else if(imageID.equals("4")){
			characterBtn.setBackgroundResource(R.drawable.store_44_btn_amanda_off);
		}else if(imageID.equals("5")){
			characterBtn.setBackgroundResource(R.drawable.store_44_btn_tom_off);
		}else if(imageID.equals("6")){
			characterBtn.setBackgroundResource(R.drawable.store_44_btn_jenny_off);
		}else if(imageID.equals("7")){
			characterBtn.setBackgroundResource(R.drawable.store_44_btn_monkey_off);
		}else{
			characterBtn.setBackgroundResource(R.drawable.store_44_btn_dino_off);
		}
	}
	
	//on click
	public void onClickBack(View v)
	{
		finish();
	}

	public void showAttendanceActivity(View v)
	{
		Intent intent = new Intent(getApplicationContext(), HomeMyPageAttendance.class);
		startActivity(intent);
	}

	public void showHomeStoreActivity(View v)
	{
		Intent intent = new Intent(getApplicationContext(), HomeStore.class);
		startActivity(intent);
	}

	public void showHomeMyPagePurchased(View v)
	{
		Intent intent = new Intent(getApplicationContext(), HomeMyPagePurchased.class);
		startActivity(intent);
	}
	public void showHomeMyPageSaving(View v)
	{
		Intent intent = new Intent(getApplicationContext(), HomeMyPageSaving.class);
		startActivity(intent);
	}
	public void showHomeDownload(View v)
	{
		Intent intent = new Intent(getApplicationContext(), HomeDownload.class);
		startActivity(intent);
	}
	public void showMyCharacter(View v)
	{
		Intent intent = new Intent(getApplicationContext(), HomeMypageOptionCharacter.class);
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home_my_page, menu);
		return true;
	}


}
