package com.todpop.saltyenglish;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
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
import com.playmobs.crosswalk.*;
import com.playmobs.crosswalk.PlaymobsAgent.ErrorListener;
import com.playmobs.crosswalk.PlaymobsAgent.ResponseListener;


import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class HomeDownload extends Activity {

	final int CPI_SAVING_ON = 2;
	final int CPI_SAVING_COMP = 3;
	final int CPI_SAVING_NO = 4;
	final int CPI_SAVING_EXHAUS = 9;

	//for cross walk
	PlaymobsAgent pm;
	ResponseListener listener;
	ErrorListener errorListner;
	
	CpiListViewAdapter cpiListViewAdapter;
	ArrayList<CpiListViewItem> cpiArray;
	CpiListViewItem mCpiListItem;
	ListView cpiListView;
	int cpiCount = 0;
	
	ImageView noDownload;

	RelativeLayout homeDownload;
//	int couponCount = 0;


	//popup
	PopupWindow cpxPopupWindow;
	View cpxPopupview;
	TextView cpxPopupText;
	
	String mobile;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_download);
		homeDownload = (RelativeLayout)findViewById(R.id.home_download);

		//popupview
		cpxPopupview = View.inflate(this, R.layout.popup_view, null);
		cpxPopupWindow = new PopupWindow(cpxPopupview,ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,true);
		cpxPopupText = (TextView)cpxPopupview.findViewById(R.id.popup_id_text);
		
		cpiArray = new ArrayList<CpiListViewItem>();
		cpiListView=(ListView)findViewById(R.id.homedownload_id_listiew_cpi);
		
		noDownload = (ImageView)findViewById(R.id.homedownload_id_no_donwload);


		//get phone number
		try {
			TelephonyManager phoneMgr=(TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE); 
			mobile =phoneMgr.getLine1Number().toString();
			mobile = mobile.replace("+82", "0");
		} catch(Exception e) {
			mobile = "010test0000";
		}
		SharedPreferences cpxInstallInfo = getSharedPreferences("cpxInstallInfo",0);
		SharedPreferences.Editor cpxInstallInfoEditor = cpxInstallInfo.edit();
		cpxInstallInfoEditor.putBoolean("cpxGoMyDownload", false);
		cpxInstallInfoEditor.apply();
		
		//crosswalk
		listener = new ResponseListener() {
			@Override
			public void OnResponse(CrossPromotionData data) {
				pm.CloseSession();
				//Log.i("tag", data.getReward());
				//Log.i("STEVEN", "data.getUid() = "+ data.getUid() + "    data.getCampaign_index() = " + data.getCampaign_index() + " data.getCampaign_title() = " + data.getCampaign_title());
				cpxPopupText.setText(R.string.home_download_crosswalk_success);
				cpxPopupWindow.showAtLocation(homeDownload, Gravity.CENTER, 0, 0);
				cpxPopupWindow.showAsDropDown(null);
				//new SendCrossWalkLog(data.getUid(), data.getCampaign_index(), data.getCampaign_title()).execute("http://todpop.co.kr/api/advertises/set_crosswalk_log.json");
			}
		};
		
		errorListner = new ErrorListener() {
			@Override
			public void OnErrorResponse(CrossPromotionError error) {
				switch(error.getErrorCode()){
				case 002:
					cpxPopupText.setText(R.string.home_download_crosswalk_error_002);
				case 003:
					cpxPopupText.setText(R.string.home_download_crosswalk_error_003);
				case 004:
					cpxPopupText.setText(R.string.home_download_crosswalk_error_004);
				case 005:
					cpxPopupText.setText(R.string.home_download_crosswalk_error_005);
				case 006:
					cpxPopupText.setText(R.string.home_download_crosswalk_error_006);
				case 007:
					cpxPopupText.setText(R.string.home_download_crosswalk_error_007);
				case 010:
					cpxPopupText.setText(R.string.home_download_crosswalk_error_010);
				case 011:
					cpxPopupText.setText(R.string.home_download_crosswalk_error_011);
				}
				cpxPopupWindow.showAtLocation(homeDownload, Gravity.CENTER, 0, 0);
				cpxPopupWindow.showAsDropDown(null);
			}
		};

		SharedPreferences pref = getSharedPreferences("rgInfo",0);
		String userId = pref.getString("mem_id", "0");
		pm = new PlaymobsAgent(userId, "691824", this, listener, errorListner);
		
		pm.setDefaultToast(true);
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		com.facebook.AppEventsLogger.activateApp(this, "218233231697811");
		getList();
		//cpiListViewAdapter.notifyDataSetChanged();
		
	}
	

	class CpiListViewItem 
	{
		CpiListViewItem(int aId, int aad_type, String aImage, String aName, String aCoin, String aPoint, int aState)
		{
			id = aId;
			ad_type = aad_type;
			image = aImage;
			name = aName;
			coin = aCoin;
			point = aPoint;
			state = aState;
		}
		int id;
		int ad_type;
		String image;
		String name;
		String coin;
		String point;
		int state;
	}

	class CpiListViewAdapter extends BaseAdapter
	{
		Context maincon;
		LayoutInflater Inflater;
		ArrayList<CpiListViewItem> arSrc;
		int layout;

		public CpiListViewAdapter(Context context,int alayout,ArrayList<CpiListViewItem> aarSrc)
		{
			maincon = context;
			Inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			arSrc = aarSrc;
			layout = alayout;
		}
		public int getCount()
		{
			return arSrc.size();
		}

		public String getItem(int position)
		{
			return arSrc.get(position).name;
		}

		public long getItemId(int position)
		{
			return position;
		}

		public View getView(int position,View convertView,ViewGroup parent)
		{
			cpiCount++;
			final int id = arSrc.get(position).id;
			final int type = arSrc.get(position).ad_type;
			
			if(convertView == null)
			{
				convertView = Inflater.inflate(layout, parent,false);
			}
			ImageView itemImg = (ImageView)convertView.findViewById(R.id.homedownload_list_item_id_image);
			TextView name1Text = (TextView)convertView.findViewById(R.id.homedownload_list_item_id_name);
			name1Text.setText(arSrc.get(position).name);

			TextView name2Text = (TextView)convertView.findViewById(R.id.homedownload_list_item_id_coin);
			ImageView coinImg = (ImageView)convertView.findViewById(R.id.homedownload_list_item_id_imagecoin);
			if(arSrc.get(position).point.equals("null") || arSrc.get(position).point.equals("0")){
				name2Text.setText(arSrc.get(position).coin);
			}
			else{
				name2Text.setText(arSrc.get(position).point);
				coinImg.setImageResource(R.drawable.common_image_smallpoint);
			}
				
			final Button getRewardBut = (Button)convertView.findViewById(R.id.homedownload_list_item_id_btn);
			
			switch(arSrc.get(position).state){
			case 1:
				//TODO ad act 1 add
			case 2:
				getRewardBut.setBackgroundResource(R.drawable.homedownload_drawable_btn_saving);
				getRewardBut.setOnClickListener(new Button.OnClickListener(){
					public void onClick(View V){
						if(type==901){
							pm.OpenSession();
						}
						else{
							getRewardBut.setEnabled(false);
							getRewardBut.setBackgroundResource(R.drawable.store_36_btn_saving_ing);
							new GetCPXInfo().execute("http://todpop.co.kr/api/advertises/show_cpx_ad.json?ad_id="+id);
						}
					}
				});
				break;
			case 3:
				getRewardBut.setBackgroundResource(R.drawable.store_36_btn_savingcomplete);
				break;
			case 4:
				getRewardBut.setBackgroundResource(R.drawable.store_36_btn_savingno);
				break;
			case 9:
				getRewardBut.setBackgroundResource(R.drawable.store_36_btn_savingexhaustion);
				break;
			default:
				break;	
			}
			
			try {
				// show The Image
				String imgUrl = arSrc.get(position).image;
				//URL url = new URL(imgUrl);
				//Log.d("url ------ ", url.toString());
				new DownloadImageTask(itemImg)
				.execute("http://todpop.co.kr"
						+ imgUrl);
			} catch (Exception e) {
				e.printStackTrace();
			} 

			if (cpiCount%2 == 1) {
				convertView.setBackgroundResource(R.drawable.store_2_image_separatebox_white);
			} else {
				convertView.setBackgroundResource(R.drawable.store_2_image_separatebox_yellow);
			}
			return convertView;
		}
	 
	}

	// request 
	private class GetCPX extends AsyncTask<String, Void, JSONObject> 
	{
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
				if	(result.getBoolean("status")==true) {
					
					JSONArray jsonArray = result.getJSONArray("data");
					
					for(int i=0;i<jsonArray.length();i++) {
						mCpiListItem = new CpiListViewItem(jsonArray.getJSONObject(i).getInt("ad_id"), jsonArray.getJSONObject(i).getInt("ad_type"),
								jsonArray.getJSONObject(i).getString("image"),jsonArray.getJSONObject(i).getString("name"),
								jsonArray.getJSONObject(i).getString("reward"),jsonArray.getJSONObject(i).getString("point"),
								jsonArray.getJSONObject(i).getInt("act"));
						cpiArray.add(mCpiListItem);
					}	
					
					if(!cpiArray.isEmpty()){
						noDownload.setVisibility(View.GONE);
						cpiListViewAdapter = new CpiListViewAdapter(HomeDownload.this,R.layout.home_download_list_item_cpi, cpiArray);
						cpiListView.setAdapter(cpiListViewAdapter);
					}
				}
			} catch (Exception e) {

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
	
	
	// request for cpx detail info
	private class GetCPXInfo extends AsyncTask<String, Void, JSONObject> 
	{
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
				if	(result.getBoolean("status")==true) {

					JSONObject adDetails = result.getJSONObject("data");
					int adId = adDetails.getInt("ad_id");
					int adType = adDetails.getInt("ad_type");
					
					String targetUrl = adDetails.getString("target_url");
					String packageName = adDetails.getString("package_name");
					String confirmUrl = adDetails.getString("confirm_url");
					
					String reward = adDetails.getString("reward");
					String point = adDetails.getString("point");
					int questionCount = adDetails.getInt("n_question=");

			
					
					SharedPreferences cpxInfo = getSharedPreferences("cpxInfo",0);
					SharedPreferences.Editor cpxInfoEditor = cpxInfo.edit();
					cpxInfoEditor.putInt("adId", adId);					
					cpxInfoEditor.putInt("adType", adType);		
					cpxInfoEditor.putString("targetUrl", targetUrl);
					cpxInfoEditor.putString("packageName", packageName);
					cpxInfoEditor.putString("confirmUrl", confirmUrl);
					cpxInfoEditor.putString("reward", reward);
					cpxInfoEditor.putString("point", point);
					cpxInfoEditor.putInt("questionCount", questionCount);

					cpxInfoEditor.apply();

					SharedPreferences pref = getSharedPreferences("rgInfo",0);
					String userId = pref.getString("mem_id", "0");
					if(adType == 301){
						cpxInfo.edit().clear().apply();
						if(checkIsAppInstalled(packageName)){
							new SendCPXLog().execute("http://todpop.co.kr/api/advertises/set_cpx_log.json?ad_id=" + adId + "&ad_type=301&user_id=" + userId + "&act=3");

							cpxPopupText.setText(R.string.home_download_install_confirmed);
							cpxPopupWindow.showAtLocation(homeDownload, Gravity.CENTER, 0, 0);
							cpxPopupWindow.showAsDropDown(null);
						}
						else{
							try {
							    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+packageName)));
							} catch (android.content.ActivityNotFoundException anfe) {
							    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id="+packageName)));
							}
						}
					}
					else if(adType == 303){
						if(!confirmUrl.equals("")){
							Log.i("STEVEN", "confirm url not null");
							new CheckCPA().execute(confirmUrl + "?mobile=" + mobile);
						}
						else{
							Toast toast = Toast.makeText(getApplicationContext(), R.string.cpa_install_notice, Toast.LENGTH_LONG);
							toast.show();
							if(targetUrl.contains("**ad_id**")){
								targetUrl.replace("**ad_id**", String.valueOf(adId));
							}
							if(targetUrl.contains("**user_id**")){
								targetUrl.replace("**user_id**", String.valueOf(userId));
							}
							startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(targetUrl)));
						}
					}
					else if(adType == 305){
						Intent intent = new Intent(getApplicationContext(), SurveyView.class);
						startActivity(intent);
						finish();
					}
					else if(adType == 306){
						Toast toast = Toast.makeText(getApplicationContext(), R.string.cpc_rewarded, Toast.LENGTH_LONG);
						toast.show();
						new SendCPXLog().execute("http://todpop.co.kr/api/advertises/set_cpx_log.json?ad_id=" + adId + "&ad_type=306&user_id=" + userId + "&act=3");
						startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(targetUrl)));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	// ******************** CPA signed up Check *************************
	private class CheckCPA extends AsyncTask<String, Void, JSONObject> {
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

				SharedPreferences cpxInfo = getSharedPreferences("cpxInfo",0);
				SharedPreferences pref = getSharedPreferences("rgInfo",0);
				String userId = pref.getString("mem_id", "0");
				if	(result.getBoolean("status")==true) {
					new SendCPXLog().execute("http://todpop.co.kr/api/advertises/set_cpx_log.json?ad_id=" + cpxInfo.getString("adId", "") +
							"&ad_type=" + cpxInfo.getString("adType", "") +"&user_id=" + userId + "&act=3");

					cpxInfo.edit().clear().apply();

					cpxPopupText.setText(R.string.home_download_action_confirmed);
					cpxPopupWindow.showAtLocation(homeDownload, Gravity.CENTER, 0, 0);
					cpxPopupWindow.showAsDropDown(null);

				} else {
					Toast toast = Toast.makeText(getApplicationContext(), R.string.cpa_install_notice, Toast.LENGTH_LONG);
					toast.show();
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(cpxInfo.getString("targetUrl", ""))));
					cpxInfo.edit().clear().apply();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	private class SendCPXLog extends AsyncTask<String, Void, JSONObject> {
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
		protected void onPostExecute(JSONObject result) {

			try {
				if (result.getBoolean("status") == true) {
					//Log.d("CPX LOG:  ---- ", "Send CPX act=3 Log OK!");
				} else {
					//Log.d("CPX LOG:  ---- ", "Send CPX act=3 Log Failed!");
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}	
	
	// Check if Application is installed
    private boolean checkIsAppInstalled (String uri)
    {
        PackageManager pm = getPackageManager();
        boolean app_installed = false;
        try
        {
               pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
               app_installed = true;
        }
        catch (PackageManager.NameNotFoundException e)
        {
               app_installed = false;
        }
        return app_installed ;
    }

	// on click
	public void onClickBack(View view)
	{
		getSharedPreferences("cpxInfo",0).edit().clear().apply();
		finish();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.home_download, menu);
		return true;
	}
	@Override
	protected void onStart()
	{
		super.onStart();
		FlurryAgent.onStartSession(this, "ZKWGFP6HKJ33Y69SP5QY");
		FlurryAgent.logEvent("Download Right now");
	    EasyTracker.getInstance(this).activityStart(this);
	}
	 
	@Override
	protected void onStop()
	{
		super.onStop();		
		FlurryAgent.onEndSession(this);
		EasyTracker.getInstance(this).activityStop(this);
	}
	public void getList(){
		cpiArray.clear();
		cpiCount = 0;
		SharedPreferences pref = getSharedPreferences("rgInfo",0);
		String userId = pref.getString("mem_id", "0");

		//TODO
		/*mCpiListItem = new CpiListViewItem(901, 901,
				"/uploads/cpd_advertisement/back_image/1/images.jpg", "추가 랭킹 포인트!",
				"0", "설치시 랭킹 포인트 지급!", 2);
		cpiArray.add(mCpiListItem);
		cpiListViewAdapter = new CpiListViewAdapter(HomeDownload.this,R.layout.home_download_list_item_cpi, cpiArray);
		cpiListView.setAdapter(cpiListViewAdapter);*/
		new GetCPX().execute("http://todpop.co.kr/api/etc/" + userId + "/show_cpx_list.json");
	}
	//----button onClick----
	public void closePopup(View v)
	{
		getList();
		cpxPopupWindow.dismiss();
	}
	
}
