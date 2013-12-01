package com.todpop.saltyenglish;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.content.*;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

public class CpxPackageChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context ctx, Intent intent) {
    	Uri data = intent.getData();
		Log.d("Receveir ---------- ", "Action: " + intent.getAction());
		// data.getSchemeSpecificPart() is the package name
		Log.d("Receveir ---------- ", "The DATA: " + data.getSchemeSpecificPart());
		String packageName = data.getSchemeSpecificPart();
		
		SharedPreferences cpxInstallInfo = ctx.getSharedPreferences("cpxInstallInfo",0);
		boolean isCpxInstalling = cpxInstallInfo.getBoolean("isCpxInstalling", false);
		int adType = cpxInstallInfo.getInt("cpxAdType", 0);
		int adId = cpxInstallInfo.getInt("cpxAdId", 0);
		String adPackageName = cpxInstallInfo.getString("cpxPackageName", "");
				
		if (isCpxInstalling==true && packageName.equals(adPackageName)) {
			// Send CPX act=3 to Server
			SharedPreferences pref = ctx.getSharedPreferences("rgInfo",0);
			String userId = pref.getString("mem_id", "0");
			new SendCPXLog().execute("http://todpop.co.kr/api/advertises/set_cpx_log.json?ad_id="+adId+
					"&ad_type=" + adType +"&user_id=" + userId + "&act=3");
		}
			
		cpxInstallInfo.edit().clear().commit();
    }
    
 	private class SendCPXLog extends AsyncTask<String, Void, JSONObject> {
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
 					Log.d("CPX LOG:  ---- ", "Send CPX act=3 Log OK!");
 				} else {
 					Log.d("CPX LOG:  ---- ", "Send CPX act=3 Log Failed!");
 				}

 			} catch (Exception e) {

 			}
 		}
 	}

}