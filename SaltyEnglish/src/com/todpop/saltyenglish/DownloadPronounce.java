package com.todpop.saltyenglish;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Copyright 2014 TODPOP Corp. All rights reserved.
 * 
 * @author steven@todpop.co.kr
 * @version 1.0
 * 
 */
public class DownloadPronounce extends AsyncTask<String, Void, JSONObject> {

	private static final int YES = 1;
	
    private static DownloadPronounce downloadPronounce = null;
    private Context context;
    private int selectedCategoryInt;
    private DownloadTask downLoadTask;
    private TextView progressPopupText;
    private TextView progressPopupCountText;
    private ProgressBar progressPopupLoadProgBar;
    private ProgressBar progressPopupProgBar;
    private Button progressPopupCancel;
    private Button progressPopupDone;
    private Boolean getWordFlag;
    private Boolean getSoundFlag;

	ArrayList<WordPair> wordList;
	ArrayList<WordPair> downloadWordList;
	
	WordDBHelper mHelper;
	SQLiteDatabase db;
	
	private class WordPair{
		WordPair(String inWord, String inVersion){
			word = inWord;
			version = inVersion;
		}
		private String word;
		private String version;
		
		public String getWord(){
			return word;
		}
		public String getVersion(){
			return version;
		}
	}
	
    private DownloadPronounce(Context context, int selectedCategoryInt, View progressPopupView){
    	this.context = context;
    	this.selectedCategoryInt = selectedCategoryInt;
    	progressPopupText = (TextView)progressPopupView.findViewById(R.id.popup_id_text);
    	progressPopupCountText = (TextView)progressPopupView.findViewById(R.id.popup_download_id_count);	
    	progressPopupLoadProgBar = (ProgressBar)progressPopupView.findViewById(R.id.popup_download_id_loading_progressbar);
    	progressPopupProgBar = (ProgressBar)progressPopupView.findViewById(R.id.popup_download_id_progressbar);
    	progressPopupCancel = (Button)progressPopupView.findViewById(R.id.popup_download_id_btn_cancel);
    	progressPopupDone = (Button)progressPopupView.findViewById(R.id.popup_download_id_btn_done);
		wordList = new ArrayList<WordPair>();
		downloadWordList = new ArrayList<WordPair>();
		mHelper = new WordDBHelper(context);
		getWordFlag = false;
		getSoundFlag = false;
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
    public static DownloadPronounce getTask(Context context, int selectedCategoryInt, View progressPopupView) {
        if(downloadPronounce != null)
        	return downloadPronounce;
        
        return new DownloadPronounce(context, selectedCategoryInt, progressPopupView);
    }
    
	@Override
	protected JSONObject doInBackground(String... urls) {
		JSONObject result = null;
		try {
			progressPopupText.setText(context.getResources().getString(R.string.popup_view_download_progressbar_list_loading));
			DefaultHttpClient httpClient = new DefaultHttpClient();
			//TODO add URL
			String getURL = "http://www.todpop.co.kr/api/etc/getword/" + selectedCategoryInt;
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
	protected void onPostExecute(JSONObject json) {
		try {
			if (json.getBoolean("status") == true) {
				JSONArray jsonArray = json.getJSONObject("data").getJSONArray("words");
				for(int i = 0; i < jsonArray.length(); i++){
					wordList.add(new WordPair(jsonArray.getJSONObject(i).getString("word"), jsonArray.getJSONObject(i).getString("version")));
				}
		        startDownload();
			} else {
				progressPopupText.setText(context.getResources().getString(R.string.popup_view_download_progressbar_error));
				progressPopupCancel.setVisibility(View.GONE);
				progressPopupDone.setVisibility(View.VISIBLE);
				progressPopupLoadProgBar.setVisibility(View.GONE);
				Log.d("STEVEN", "Get word list from server failed");
			}
		} catch (Exception e) {
			e.printStackTrace();
			progressPopupText.setText(context.getResources().getString(R.string.popup_view_download_progressbar_error));
			progressPopupCancel.setVisibility(View.GONE);
			progressPopupDone.setVisibility(View.VISIBLE);
			progressPopupLoadProgBar.setVisibility(View.GONE);
		}
		getWordFlag = true;
	}	
	
	private void startDownload(){
		db = mHelper.getWritableDatabase();
		//find word from database and check version
		for(int i = 0; i < wordList.size(); i++){
			Cursor find = db.rawQuery("SELECT distinct word, version FROM wordSound WHERE word=\'" + wordList.get(i).getWord() + "\'", null);
			if(find.moveToFirst()){
				if(!wordList.get(i).getVersion().equals(find.getString(1))){
					db.delete("wordSound", "word='" + wordList.get(i).getWord() + "'", null);
					downloadWordList.add(wordList.get(i));
				}
			}
			else{
				downloadWordList.add(wordList.get(i));
			}
		}
        progressPopupText.setText(context.getResources().getString(R.string.popup_view_download_progressbar_downloading));
        progressPopupLoadProgBar.setVisibility(View.GONE);
		progressPopupProgBar.setVisibility(View.VISIBLE);
		progressPopupCountText.setVisibility(View.VISIBLE);
		downLoadTask = new DownloadTask();
		downLoadTask.execute("");
        progressPopupProgBar.setIndeterminate(false);
        progressPopupProgBar.setMax(downloadWordList.size());
        progressPopupCountText.setText("1/" + downloadWordList.size());
	}
	
	public void cancel(){
		Log.i("STEVEN", "down cancel 195");
		progressPopupText.setText(context.getResources().getString(R.string.popup_view_download_progressbar_canceling));
		if(!getWordFlag)
			this.cancel(true);
		else if(getSoundFlag)
			downLoadTask.cancel(true);
	}
	
	private class DownloadTask extends AsyncTask<String, String, String> {
	    private int current = 0;
	    @Override
	    protected String doInBackground(String... sUrl) {
	        // take CPU lock to prevent CPU from going off if the user 
	        // presses the power button during download
	        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
	        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
	        wl.acquire();

			getSoundFlag = true;
			
	        int length = downloadWordList.size();
	        while(current < length){
		        try {
		            InputStream input = null;
		            OutputStream output = null;
		            HttpURLConnection connection = null;
		            try {
		            	//TODO testing
		                //URL url = new URL("http://www.todpop.co.kr/uploads/word/sound/" + downloadWordList.get(current).getWord());
		            	URL url = new URL("https://ssl.gstatic.com/dictionary/static/sounds/de/0/" + downloadWordList.get(current).getWord() + ".mp3");
		                connection = (HttpURLConnection) url.openConnection();
		                connection.connect();
	
		                // expect HTTP 200 OK, so we don't mistakenly save error report 
		                // instead of the file
		                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
		                     return context.getResources().getString(R.string.popup_view_download_progressbar_error) 
		                    		 + "Server returned HTTP " + connection.getResponseCode() 
		                    		 + " " + connection.getResponseMessage();
	
		                // download the file
		                input = connection.getInputStream();
		                output = context.openFileOutput(downloadWordList.get(current).getWord(), Context.MODE_PRIVATE);
	
		                byte data[] = new byte[1024];
		                int count;
		                while ((count = input.read(data)) != -1) {
		                    // allow canceling with back button
		                    if (isCancelled())
		                        return context.getResources().getString(R.string.popup_view_download_progressbar_canceled);
		                    output.write(data, 0, count);
		                }
		            } catch (Exception e) {
		                return context.getResources().getString(R.string.popup_view_download_progressbar_error) + e.toString();
		            } finally {
		                try {
		                    if (output != null)
		                        output.close();
		                    if (input != null)
		                        input.close();
		                } 
		                catch (IOException ignored) { }
	
		                if (connection != null)
		                    connection.disconnect();
				        publishProgress(String.valueOf(current + 1), downloadWordList.get(current).getWord(), downloadWordList.get(current).getVersion());
				        current++;
		            }
		        }catch(Exception e){
		        	e.printStackTrace();
		        }
	        }
            wl.release();
	        return null;
	    }    
	    
	    @Override
	    protected void onProgressUpdate(String... progress) {
	        super.onProgressUpdate(progress);
	        progressPopupProgBar.setProgress(Integer.valueOf(progress[0]));
	        progressPopupCountText.setText(Integer.valueOf(progress[0]) + "/" + downloadWordList.size());
	        
	        ContentValues row = new ContentValues();
			row.put("word", progress[1]);
			row.put("version", progress[2]);
			row.put("category", selectedCategoryInt);

			db.insert("wordSound", null, row);
	    }
	    @Override
	    protected void onPostExecute(String result) {
	    	db.close();
	        if (result != null){
				progressPopupText.setText(result);
				progressPopupCancel.setVisibility(View.GONE);
				progressPopupDone.setVisibility(View.VISIBLE);
	        }
	        else{
				progressPopupProgBar.setVisibility(View.INVISIBLE);
				progressPopupCountText.setVisibility(View.INVISIBLE);
				progressPopupText.setText(context.getResources().getString(R.string.popup_view_download_progressbar_done));
				progressPopupCancel.setVisibility(View.GONE);
				progressPopupDone.setVisibility(View.VISIBLE);
				SharedPreferences.Editor studyInfoEdit = context.getSharedPreferences("studyInfo",0).edit();
				
				switch(selectedCategoryInt){
				case 1:
					studyInfoEdit.putInt("basicCategorySound", YES);
					break;
				case 2:
					studyInfoEdit.putInt("middleCategorySound", YES);
					break;
				case 3:
					studyInfoEdit.putInt("highCategorySound", YES);
					break;
				case 4:
					studyInfoEdit.putInt("toeicCategorySound", YES);
					break;
				}
				studyInfoEdit.apply();
	        }
		    getSoundFlag = false;
	    }
	}
}
