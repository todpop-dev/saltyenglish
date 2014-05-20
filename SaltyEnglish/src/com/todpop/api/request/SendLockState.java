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
public class SendLockState extends AsyncTask<Integer, Void, JSONObject> {	
    private static SendLockState sendLockState = null;
    Context context;
	
    SharedPreferences rgInfo;
    
	LockerDBHelper lHelper;
	SQLiteDatabase db;
	
    public SendLockState(Context context){
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
    public static SendLockState getTask(Context context) {
        if(sendLockState != null)
        	return sendLockState;
        
        return new SendLockState(context);
    }
    
	@Override
	protected JSONObject doInBackground(Integer... urls) {
		JSONObject result = null;
		try {
			DefaultHttpClient httpClient;
			String getURL = "http://www.todpop.co.kr/api/screen_lock/lock_state.json?user_id=" + rgInfo.getString("mem_id", null) + "&state=" + urls[0];
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
	}
}
