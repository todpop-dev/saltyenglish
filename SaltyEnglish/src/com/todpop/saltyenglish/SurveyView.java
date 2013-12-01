package com.todpop.saltyenglish;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class SurveyView extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_survey_view);
		
		// Send act 1
	}
	
	// on click
	public void onClickBack(View view)
	{
		finish();
	}
	
	private class GetCPSiInfo extends AsyncTask<String, Void, JSONObject> 
	{
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
					Log.d("CPX RESPONSE ---- ", result.toString());				        	
				}
				return result;
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		}

		@Override
		protected void onPostExecute(JSONObject json) {

			try {
				if(json.getBoolean("status")==true) {
					JSONObject adDetails = json.getJSONObject("data");
					int adId = adDetails.getInt("ad_id");
					int adType = adDetails.getInt("ad_type");
					Log.d("CPX Type: ---------- ", Integer.toString(adType));
					
					String adImageUrl = "http://todpop.co.kr/" + adDetails.getString("ad_image");
					String adText = adDetails.getString("ad_text");
					String targetUrl = adDetails.getString("target_url");
					String packageName = adDetails.getString("package_name");
					String confirmUrl = adDetails.getString("confirm_url");
					int reward = adDetails.getInt("reward");
					int questionCount = adDetails.getInt("n_question");

					SharedPreferences cpxInfo = getSharedPreferences("cpxInfo",0);
					SharedPreferences.Editor cpxInfoEditor = cpxInfo.edit();
					cpxInfoEditor.putInt("adId", adId);					
					cpxInfoEditor.putInt("adType", adType);		
					cpxInfoEditor.putString("adImageUrl", adImageUrl);
					cpxInfoEditor.putString("adText", adText);
					cpxInfoEditor.putString("targetUrl", targetUrl);
					cpxInfoEditor.putString("packageName", packageName);
					cpxInfoEditor.putString("confirmUrl", confirmUrl);
					cpxInfoEditor.putInt("reward", reward);
					cpxInfoEditor.putInt("questionCount", questionCount);
					
					cpxInfoEditor.commit();
					
					// TODO: Add more CPX Support. Now only support CPI and CPS

						Intent intent = new Intent(getApplicationContext(), StudyHome.class);
						startActivity(intent);
						finish();
					
				} else {		   
					// In the case CPX Request Failed
					Intent intent = new Intent(getApplicationContext(), StudyHome.class);
					startActivity(intent);
					finish();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}
	
}










