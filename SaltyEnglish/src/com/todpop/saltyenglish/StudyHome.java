package com.todpop.saltyenglish;


import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.facebook.Session;

import android.os.AsyncTask;
import android.os.Bundle;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.Animator.AnimatorListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class StudyHome extends Activity {
	private String encoding = "UTF-8";
	boolean isOnSlide = false;

	// CPI View show in return from study test
	RelativeLayout cpiView;

	// Home Ranking List
	ListView rankingList;
	
	// Ranking Item and Adaptor
	RankingListItem rankingItem;
	ArrayList<RankingListItem> rankingItemArray;
	RankingListAdapter rankingListAdapter;

	TextView myRank;
	ImageView myImage;
	TextView myName;
	TextView myScore;
	
	
	
	String kakaoMent = "";
	String kaokaoAndroidUrl = "";
	String iosUrl = "";
	
	SharedPreferences pref;
	SharedPreferences.Editor perfEdit;
	
	RadioGroup weekMoonBtn;
	
	int category = 0;
	int period = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_study_home);
		
		
		cpiView = (RelativeLayout)findViewById(R.id.studyhome_cpi_view);
		cpiView.setVisibility(View.GONE);

		myRank = (TextView)findViewById(R.id.studyhome_id_my_rank);
		myImage = (ImageView)findViewById(R.id.studyhome_id_my_rank_image);
		myName = (TextView)findViewById(R.id.studyhome_id_my_rank_name_text);
		myScore = (TextView)findViewById(R.id.studyhome_id_my_rank_fraction);

		rankingList = (ListView)findViewById(R.id.studyhome_id_listview);
		rankingItemArray = new ArrayList<RankingListItem>();
		
		weekMoonBtn= (RadioGroup)findViewById(R.id.homestore_id_week_moon_rank_group);
		weekMoonBtn.setOnCheckedChangeListener(new OnCheckedChangeListener() 
	    {
	        public void onCheckedChanged(RadioGroup group, int checkedId) {
				SharedPreferences pref = getSharedPreferences("rgInfo",0);
				SharedPreferences.Editor editor = pref.edit();
	        	switch(checkedId)
        		{
        			case R.id.studyhome_id_week:
        				period =1;
        				getInfo();

        				editor.putInt("period", 1);
        				editor.commit();
        			break;
        			
        			case R.id.studyhome_id_moon:
        				period =2;
        				getInfo();

        				editor.putInt("period", 2);
        				editor.commit();
        			break;
        			
        			default:
        			break;
        		}
	        }
	    });
		
		
		
		new GetKakao().execute("http://todpop.co.kr/api/app_infos/get_cacao_msg.json");
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		
		// Facebook Logout
		Session session = Session.getActiveSession();
		if (session != null) {
			if (!session.isClosed()) {
				session.closeAndClearTokenInformation();
				//clear your preferences if saved
			}
		} else {
			session = new Session(getApplicationContext());
			Session.setActiveSession(session);

			session.closeAndClearTokenInformation();
			//clear your preferences if saved
		}
		
		getInfo();
	}
	
	public void getInfo()
	{
		pref = getSharedPreferences("rgInfo",0);
		perfEdit = pref.edit();
		
		if(pref.getInt("settingStage", 0)==0)
		{
			int savedLevel = Integer.parseInt(pref.getString("level", "1"));
			if(savedLevel<15)
			{
				category = 1;
			}else if(savedLevel>15&&savedLevel<61){
				category = 2;
			}else if(savedLevel>60&&savedLevel<121){
				category = 3;
			}else if(savedLevel>120)
			{
				category = 4;
			}
			perfEdit.putInt("settingStage",category);
			perfEdit.commit();
		}else{
			
		}
	
		
		new GetRank().execute("http://todpop.co.kr/api/users/get_users_score.json?category="+pref.getInt("settingStage", 0)+"&period="+period+"&nickname="+pref.getString("nickname", "NO"));
		
	}

	class RankingListItem{
		RankingListItem(String aRank,String aImageNO,String aName,String  aFraction)
		{
			rank = aRank;
			imageNO = aImageNO;
			name =aName;
			fraction = aFraction;
		}
		String rank;
		String imageNO;
		String name;
		String fraction;
	}

	class RankingListAdapter extends BaseAdapter
	{
		Context maincon;
		LayoutInflater Inflater;
		ArrayList<RankingListItem> arSrc;
		int layout;

		public RankingListAdapter(Context context,int alayout,ArrayList<RankingListItem> aarSrc)
		{
			maincon = context;
			Inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			arSrc = aarSrc;
			layout = alayout;
		}
		
		public int getCount()
		{
			return 6;//arSrc.size();
		}

		public String getItem(int position)
		{
			return arSrc.get(position).rank;
		}

		public long getItemId(int position)
		{
			return position;
		}

		public View getView(int position,View convertView,ViewGroup parent)
		{
			if(convertView == null) {
				convertView = Inflater.inflate(layout, parent,false);
			}
			
			ImageView crown = (ImageView)convertView.findViewById(R.id.home_list_id_rank_crown);
			TextView rank = (TextView)convertView.findViewById(R.id.home_list_id_rank_rank);

			ImageView rankImage = (ImageView)convertView.findViewById(R.id.home_list_id_rank_user_image);
			TextView name = (TextView)convertView.findViewById(R.id.home_list_id_rank_name_text);
			TextView fraction = (TextView)convertView.findViewById(R.id.home_list_rank_id_user_fraction);

			setRankImage(arSrc.get(position).imageNO,rankImage);

			if(position<3) {

				if(position == 0) {
					crown.setImageResource(R.drawable.home_image_goldcrown);
				} else if(position == 1) {
					crown.setImageResource(R.drawable.home_image_silvercrown);
				} else if(position == 2) {
					crown.setImageResource(R.drawable.home_image_coppercrown);
				}

				rank.setTextColor(Color.parseColor("#E6947F"));
				rank.setText(arSrc.get(position).rank);

				name.setTextColor(Color.parseColor("#E6947F"));
				name.setText(arSrc.get(position).name);

				fraction.setTextColor(Color.parseColor("#E6947F"));
				fraction.setText(arSrc.get(position).fraction+getResources().getString(R.string.home_list_score_string));
			} else {
				rank.setTextColor(Color.parseColor("#000000"));
				rank.setText(arSrc.get(position).rank);

				name.setTextColor(Color.parseColor("#000000"));
				name.setText(arSrc.get(position).name);

				fraction.setTextColor(Color.parseColor("#000000"));
				fraction.setText(arSrc.get(position).fraction+getResources().getString(R.string.home_list_score_string));
			}

			return convertView;
		}


	}


	private class GetRank extends AsyncTask<String, Void, JSONObject> {
		@Override
		protected JSONObject doInBackground(String... urls) 
		{
			JSONObject result = null;
			try
			{
				DefaultHttpClient httpClient = new DefaultHttpClient();
				String getURL = urls[0];
				HttpGet httpGet = new HttpGet(getURL);
				HttpResponse httpResponse = httpClient.execute(httpGet);
				HttpEntity resEntity = httpResponse.getEntity();

				if (resEntity != null)
				{    
					result = new JSONObject(EntityUtils.toString(resEntity)); 
					Log.d("RESPONSE ---- ", result.toString());				        	
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
		protected void onPostExecute(JSONObject result) 
		{

			try {
				rankingItemArray.clear();
				if	(result.getBoolean("status")==true) {
					JSONArray jsonArray = result.getJSONObject("data").getJSONArray("score");
					for(int i=0;i<6;i++) {
						rankingItem = new RankingListItem(jsonArray.getJSONObject(i).getString("rank"),jsonArray.getJSONObject(i).getString("image"),jsonArray.getJSONObject(i).getString("name"),jsonArray.getJSONObject(i).getString("score"));
						rankingItemArray.add(rankingItem);
					}	

					

					myRank.setText(result.getJSONObject("data").getJSONObject("mine").getString("rank"));
					setRankImage(result.getJSONObject("data").getJSONObject("mine").getString("image"),myImage);
					myName.setText(result.getJSONObject("data").getJSONObject("mine").getString("name"));;
					myScore.setText(result.getJSONObject("data").getJSONObject("mine").getString("score")+getResources().getString(R.string.home_list_score_string));
					
					rankingListAdapter = new RankingListAdapter(StudyHome.this,R.layout.home_rank_list_item_view, rankingItemArray);
					rankingList.setAdapter(rankingListAdapter);
				}

			} catch (Exception e) {

			}
		}
	}
	public void setRankImage(String imageID,ImageView mRankImage)
	{		
		if(imageID.equals("1"))
		{
			mRankImage.setImageResource(R.drawable.home_character_eric);
		}else if(imageID.equals("2")){
			mRankImage.setImageResource(R.drawable.home_character_selly);
		}else if(imageID.equals("3")){
			mRankImage.setImageResource(R.drawable.home_character_john);
		}else if(imageID.equals("4")){
			mRankImage.setImageResource(R.drawable.home_character_amanda);
		}else if(imageID.equals("5")){
			mRankImage.setImageResource(R.drawable.home_character_tom);
		}else if(imageID.equals("6")){
			mRankImage.setImageResource(R.drawable.home_character_jenny);
		}else if(imageID.equals("7")){
			mRankImage.setImageResource(R.drawable.home_character_monkey);
		}else{
			mRankImage.setImageResource(R.drawable.home_character_dino);
		}
	}

	private  AnimatorListener mAnimationListener = new AnimatorListenerAdapter() {
		public void onAnimationEnd(Animator animation) {}
		public void onAnimationCancel(Animator animation) {}
		public void onAnimationRepeat(Animator animation) {}
		public void onAnimationStart(Animator animation) {}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.study_home, menu);
		return true;
	}

	public void showShop(View view)
	{
	}

	// Sidebar Menu Callback
	public void slideMenuBtnCB(View v) {
		if (isOnSlide == false) {
			isOnSlide = true;

			float density = getResources().getDisplayMetrics().density;
			ObjectAnimator slideAni;
			RelativeLayout rl = (RelativeLayout)findViewById(R.id.frag_home_rela_id);
			slideAni = ObjectAnimator.ofFloat(rl, "x",0*density, (173/2)*density);

			slideAni.addListener(mAnimationListener);

			slideAni.setDuration(300);
			slideAni.start();
		} else {
			isOnSlide = false;

			float density = getResources().getDisplayMetrics().density;
			ObjectAnimator slideAni;
			RelativeLayout rl = (RelativeLayout)findViewById(R.id.frag_home_rela_id);
			slideAni = ObjectAnimator.ofFloat(rl, "x", (173/2)*density,0*density);

			slideAni.addListener(mAnimationListener);

			slideAni.setDuration(300);
			slideAni.start();
		}

	}

	//--- request class ---
	private class GetKakao extends AsyncTask<String, Void, JSONObject> 
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
					Log.d("RESPONSE JSON CHECK MOBILE EXIST ---- ", result.toString());				        	
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
			if(json == null) {
			}

			try {
				kakaoMent = json.getJSONObject("data").getString("ment");
				kaokaoAndroidUrl = json.getJSONObject("data").getString("android_url");
				iosUrl = json.getJSONObject("data").getString("ios_url");
			} catch (Exception e) {

			}
		}
	}



	// on click
	public void showStudyCategory(View view)
	{
		Intent intent = new Intent(getApplicationContext(), StudyCategory.class);
		intent.putExtra("from_home", true);
		startActivity(intent);
		//finish();
	}

	public void showHomeStore(View view)
	{
		Intent intent = new Intent(getApplicationContext(),HomeStore.class);
		startActivity(intent);
	}

	public void showMore(View view)
	{
		Intent intent = new Intent(getApplicationContext(),HomeMore.class);
		startActivity(intent);
	}

	public void showHomeMyPage(View view)
	{
		Intent intent = new Intent(getApplicationContext(),HomeMyPage.class);
		startActivity(intent);
	}

	public void showHomeDownload(View v)
	{
		Intent intent = new Intent(getApplicationContext(), HomeDownload.class);
		startActivity(intent);
	}

	public void showHomeWordList(View v)
	{
		Intent intent = new Intent(getApplicationContext(), HomeWordList.class);
		startActivity(intent);
	}
	
	public void goHome(View v)
	{
		cpiView.setVisibility(View.GONE);
	}
	public void goSaving(View v)
	{
		Intent intent = new Intent(getApplicationContext(), HomeMyPageSaving.class);
		startActivity(intent);
	}

	public void kakaoInvitefriend(View v)throws NameNotFoundException
	{
		KakaoLink kakaoLink = KakaoLink.getLink(getApplicationContext());

		// check, intent is available.
		if (!kakaoLink.isAvailableIntent()) {
			//alert("Not installed KakaoTalk.");			
			return;
		}
		kakaoLink.openKakaoLink(this, 
				kaokaoAndroidUrl, 
				iosUrl, 
				getPackageName(), 
				getPackageManager().getPackageInfo(getPackageName(), 0).versionName, 
				kakaoMent, 
				encoding);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			final AlertDialog.Builder isExit = new AlertDialog.Builder(this);
			DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) 
				{
					switch (which) {
					case AlertDialog.BUTTON_POSITIVE:
						SharedPreferences settings = getSharedPreferences("setting", 0);
						SharedPreferences.Editor editor = settings.edit();
						editor.putString("check","YES");
						editor.commit();

						Intent intent = new Intent();
						intent.setClass(StudyHome.this, MainActivity.class);    
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
						startActivity(intent);
						finish();	
						break;
					case AlertDialog.BUTTON_NEGATIVE:
						break;
					default:
						break;
					}
				}
			};
			
			isExit.setTitle(getResources().getString(R.string.register_alert_title));
			isExit.setMessage(getResources().getString(R.string.register_alert_text));
			isExit.setPositiveButton("OK", listener);
			isExit.setNegativeButton("Cancel", listener);
			isExit.show();

			return false;
		}
		return false;
	}
}
