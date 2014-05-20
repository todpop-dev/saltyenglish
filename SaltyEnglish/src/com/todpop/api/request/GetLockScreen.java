package com.todpop.api.request;

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

import com.todpop.api.LockInfo;
import com.todpop.saltyenglish.db.LockerDBHelper;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Copyright 2014 TODPOP Corp. All rights reserved.
 * 
 * @author steven@todpop.co.kr
 * @version 1.0
 * 
 */
public class GetLockScreen extends AsyncTask<Void, Void, JSONObject> {	
    private static GetLockScreen downloadPronounce = null;
    Context context;
	
    SharedPreferences rgInfo;
    
	LockerDBHelper lHelper;
	SQLiteDatabase db;
	
    public GetLockScreen(Context context){
    	this.context = context;
    	rgInfo = context.getSharedPreferences("rgInfo",0);
    	lHelper = new LockerDBHelper(context);
    }
    /**
     * Return the default singleton instance
     * 
     * @param context
     * @param selectedCategoryInt
     * @param mainLayout
     * 
     * @return DownloadPronounce instance.
     */
    public static GetLockScreen getTask(Context context) {
        if(downloadPronounce != null)
        	return downloadPronounce;
        
        return new GetLockScreen(context);
    }
    
	@Override
	protected JSONObject doInBackground(Void... urls) {
		JSONObject result = null;
		try {
			DefaultHttpClient httpClient;
			String getURL = "http://www.todpop.co.kr/api/screen_lock/get_ad.json?user_id=" + rgInfo.getString("mem_id", null);
			Log.i("STEVEN", getURL);
			HttpGet httpGet = new HttpGet(getURL);
			HttpParams httpParameters = new BasicHttpParams();
			
			int timeoutConnection = 3000;
			HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
			int timeoutSocket = 5000; 
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket); 

			httpClient = new DefaultHttpClient(httpParameters); 
			
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
			if(json != null){
				if(json.getBoolean("status") == true){
					JSONArray jsonArray = json.getJSONObject("data").getJSONArray("list");
					
					JSONObject jsonObj;
					
					int group;
					int id;
					int type;
					String image;
					String target_url;
					int reward;
					int point;

					db = lHelper.getWritableDatabase();
					db.delete("latest", "category != 412", null);
					
					ContentValues row = new ContentValues();
					
					for(int i = 0; i < jsonArray.length(); i++){
						jsonObj = jsonArray.getJSONObject(i);
						
						group = jsonObj.getInt("group");
						id = jsonObj.getInt("ad_id");
						type = jsonObj.getInt("ad_type");
						image = jsonObj.getString("ad_image");
						target_url = jsonObj.getString("target_url");
						reward = jsonObj.getInt("reward");
						point = jsonObj.getInt("point");
						
						//lockArray.add(new LockInfo(group, id, type, image, target_url, reward, point));

						Log.i("STEVEN", "before checkImgHishtory");
						if(checkImgHistory(group, id)){	//if image file download history exist, store content in database
							Log.i("STEVEN", "inside checkImgHishtory");
							row.put("category", group);
							row.put("id", id);
							row.put("type", type);
							row.put("image", image);
							row.put("target_url", target_url);
							row.put("reward", reward);
							row.put("point", point);
				
							db.insert("latest", null, row);
						}
						else{
							Log.i("STEVEN", "else checkImgHishtory");
							new LockScreenDownloadImage(context, new LockInfo(group, id, type, image, target_url, reward, point)).execute();
						}
					}
					db.close();
				}
				else{
					Log.i("STEVEN", "get lockscreen ad status false");
				}
			}
			else{
				Log.i("STEVEN", "get lockscreen ad return null");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	private boolean checkImgHistory(int group, int id){
		try{			
			String group_id = String.valueOf(group) + String.valueOf(id);
			
			Cursor find = db.rawQuery("SELECT distinct category_id FROM history WHERE category_id=\'" + group_id + "\'", null);
			Log.i("STEVEN", "checkImgHishtory");
			
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
	
}
