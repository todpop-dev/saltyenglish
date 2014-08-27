package com.todpop.api.request;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class SimpleSend extends AsyncTask<String, Void, Void>{
	DefaultHttpClient httpClient ;
	@Override
	protected Void doInBackground(String... urls) 
	{
		JSONObject result = null;
		try
		{
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

			if (resEntity != null)
			{    
				result = new JSONObject(EntityUtils.toString(resEntity));
				Log.d("RESPONSE ---- ", result.toString());				  
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
