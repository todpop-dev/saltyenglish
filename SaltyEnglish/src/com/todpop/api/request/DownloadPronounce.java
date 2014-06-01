package com.todpop.api.request;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.todpop.api.TypefaceActivity;
import com.todpop.saltyenglish.R;
import com.todpop.saltyenglish.db.PronounceDBHelper;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
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
    private Activity activity;
    private int selectedCategoryInt;
    //private DownloadTask downLoadTask;
    private TextView progressPopupText;
    private TextView progressPopupCountText;
    private ProgressBar progressPopupLoadProgBar;
    private ProgressBar progressPopupProgBar;
    private Button progressPopupCancel;
    private Button progressPopupDone;
    private Boolean getWordFlag;
    private Boolean getSoundFlag;
    private Boolean downloadCancel;

	ArrayList<WordPair> wordList;
	ArrayList<WordPair> downloadWordList;
	
	PronounceDBHelper pHelper;
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
	
    public DownloadPronounce(Context context, Activity activity, int selectedCategoryInt, View progressPopupView){
    	this.context = context;
    	this.activity = activity;
    	this.selectedCategoryInt = selectedCategoryInt;
    	progressPopupText = (TextView)progressPopupView.findViewById(R.id.popup_id_text);
    	progressPopupCountText = (TextView)progressPopupView.findViewById(R.id.popup_download_id_count);
    	
    	TypefaceActivity.setFont(progressPopupText);
    	TypefaceActivity.setFont(progressPopupCountText);
    	
    	progressPopupLoadProgBar = (ProgressBar)progressPopupView.findViewById(R.id.popup_download_id_loading_progressbar);
    	progressPopupProgBar = (ProgressBar)progressPopupView.findViewById(R.id.popup_download_id_progressbar);
    	progressPopupCancel = (Button)progressPopupView.findViewById(R.id.popup_download_id_btn_cancel);
    	progressPopupDone = (Button)progressPopupView.findViewById(R.id.popup_download_id_btn_done);
		wordList = new ArrayList<WordPair>();
		downloadWordList = new ArrayList<WordPair>();
		pHelper = new PronounceDBHelper(context);
		getWordFlag = false;
		getSoundFlag = false;
		downloadCancel = false;

        System.setProperty("http.keepAlive", "false");
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
    public static DownloadPronounce getTask(Context context, Activity activity, int selectedCategoryInt, View progressPopupView) {
        if(downloadPronounce != null)
        	return downloadPronounce;
        
        return new DownloadPronounce(context, activity, selectedCategoryInt, progressPopupView);
    }
    
	@Override
	protected JSONObject doInBackground(String... urls) {
		JSONObject result = null;
		try {
			downloadCancel = false;
			activity.runOnUiThread(new Runnable(){
				@Override
				public void run(){
					progressPopupText.setText(context.getResources().getString(R.string.popup_view_download_progressbar_list_loading));
				}
			});
			DefaultHttpClient httpClient = new DefaultHttpClient();
			String getURL = "http://www.todpop.co.kr/api/studies/voice.json?category=" + selectedCategoryInt;
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
				JSONArray jsonArray = json.getJSONObject("data").getJSONArray("list");
				for(int i = 0; i < jsonArray.length(); i++){
					JSONArray inner = jsonArray.getJSONArray(i);
					wordList.add(new WordPair(inner.getString(0), jsonArray.getString(1)));
				}
		        startDownload();
			} else {
				activity.runOnUiThread(new Runnable(){
					@Override
					public void run(){
						progressPopupText.setText(context.getResources().getString(R.string.popup_view_download_progressbar_real_error));
						progressPopupCancel.setVisibility(View.GONE);
						progressPopupDone.setVisibility(View.VISIBLE);
						progressPopupLoadProgBar.setVisibility(View.GONE);
						}
				});
				Log.d("STEVEN", "Get word list from server failed");
			}
		} catch (Exception e) {
			e.printStackTrace();
			activity.runOnUiThread(new Runnable(){
				@Override
				public void run(){
					progressPopupText.setText(context.getResources().getString(R.string.popup_view_download_progressbar_real_error));
					progressPopupCancel.setVisibility(View.GONE);
					progressPopupDone.setVisibility(View.VISIBLE);
					progressPopupLoadProgBar.setVisibility(View.GONE);
					}
			});
		}
		getWordFlag = true;
	}	
	
	private void startDownload(){
		int wordListSize;
		wordListSize = wordList.size();
		//find word from database and check version
        progressPopupText.setText(context.getResources().getString(R.string.popup_view_download_progressbar_list_checking));
        progressPopupLoadProgBar.setVisibility(View.GONE);
		progressPopupProgBar.setVisibility(View.VISIBLE);
		progressPopupCancel.setVisibility(View.VISIBLE);
		progressPopupDone.setVisibility(View.GONE);
		progressPopupCountText.setVisibility(View.VISIBLE);
        progressPopupProgBar.setIndeterminate(false);
        progressPopupProgBar.setMax(wordListSize);
        progressPopupProgBar.setProgress(1);
        progressPopupCountText.setText("1/" + wordList.size());
        
        
        //new CheckExist().execute();
        Runnable checkDB = new Runnable(){
			@Override
			public void run(){			    
				int wordListSize = wordList.size();
				for(int i = 0; i < wordListSize; i++){
					final int progress = i;
                    if (downloadCancel){	//user cancel
                    	activity.runOnUiThread(new Runnable(){
		    				@Override
		    				public void run(){
		    					progressPopupText.setText(context.getResources().getString(R.string.popup_view_download_progressbar_canceled));
		    					progressPopupCancel.setVisibility(View.GONE);
		    					progressPopupDone.setVisibility(View.VISIBLE);
		    				}
	                	});	
                    	return;
                    }
					db = pHelper.getWritableDatabase();
					Cursor find = db.rawQuery("SELECT distinct word, version FROM pronounce WHERE word=\'" + wordList.get(i).getWord() + "\'", null);
					if(find.moveToFirst()){
						if(!wordList.get(i).getVersion().equals(find.getString(1))){
							db.delete("pronounce", "word='" + wordList.get(i).getWord() + "'", null);
							downloadWordList.add(wordList.get(i));
						}
					}
					else{
						downloadWordList.add(wordList.get(i));
					}
					find.close();
			    	db.close();

	                activity.runOnUiThread(new Runnable(){
	    				@Override
	    				public void run(){
				        	progressPopupProgBar.setProgress(progress + 1);
				        	progressPopupCountText.setText((progress + 1) + "/" + wordList.size());
	    				}
	    			});	
				}

                activity.runOnUiThread(new Runnable(){
    				@Override
    				public void run(){
    		            progressPopupText.setText(context.getResources().getString(R.string.popup_view_download_progressbar_downloading));
    		            progressPopupProgBar.setMax(downloadWordList.size());
    		            progressPopupCountText.setText("1/" + downloadWordList.size());
    				}
                });
	            startDownloadThread();
		    }    
		};
		new Thread(checkDB).start();
	}
	
	public void cancel(){
		Log.i("STEVEN", "down cancel 195");
		progressPopupText.setText(context.getResources().getString(R.string.popup_view_download_progressbar_canceling));
		if(!getWordFlag)
			this.cancel(true);
		else
			downloadCancel = true;
	}
	
	private void startDownloadThread(){
		Runnable download = new Runnable(){
			@Override
			public void run(){			    
				PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
		        wl.acquire();

		        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/com.todpop.saltyenglish/pronounce/";
		        File saltEng = new File(path);
		        if(!saltEng.exists())
		        	saltEng.mkdirs();
		        
				getSoundFlag = true;
	        	progressPopupProgBar.setProgress(0);

				Log.i("STEVEN", "start Downlaod");
		        int length = downloadWordList.size();
		        for(int i = 0; i < length; i++){
		        	final int progress = i;
			        try {
			            InputStream input = null;
			            FileOutputStream fileOutput = null;
			            HttpURLConnection connection = null;
			            try {
			            	URL url = new URL("http://www.todpop.co.kr/uploads/voice/" + downloadWordList.get(i).getWord() + ".mp3");
			                connection = (HttpURLConnection) url.openConnection();
			                connection.connect();
		
			                // expect HTTP 200 OK, so we don't mistakenly save error report 
			                // instead of the file
			                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK){
			                	final int responseCode = connection.getResponseCode();
			                	final String responseMessage = connection.getResponseMessage();
			                	activity.runOnUiThread(new Runnable(){
				    				@Override
				    				public void run(){
				    					progressPopupText.setText(context.getResources().getString(R.string.popup_view_download_progressbar_real_error) 
					                    		 + "Server returned HTTP " + responseCode
					                    		 + " " + responseMessage);
				    					progressPopupCancel.setVisibility(View.GONE);
				    					progressPopupDone.setVisibility(View.VISIBLE);
				    				}
			                	});	
			    	            wl.release();
			                    return;
			                }
			                // download the file
			                String finalPath = path + downloadWordList.get(i).getWord() + ".data";

			                File file = new File(finalPath);
			                file.createNewFile();
			                
			                input = connection.getInputStream();
			                fileOutput = new FileOutputStream(finalPath);
			                //output = context.openFileOutput(downloadWordList.get(i).getWord(), Context.MODE_PRIVATE);

			                byte data[] = new byte[1024];
			                int count;
			                while ((count = input.read(data)) != -1) {
			                    // allow canceling with back button
			                    if (downloadCancel){
			                    	Log.i("STEVEN", "donwload canceled");
			                    	activity.runOnUiThread(new Runnable(){
					    				@Override
					    				public void run(){
					    					progressPopupText.setText(context.getResources().getString(R.string.popup_view_download_progressbar_canceled));
					    					progressPopupCancel.setVisibility(View.GONE);
					    					progressPopupDone.setVisibility(View.VISIBLE);
					    				}
				                	});	
			        	            wl.release();
			                    	return;
			                    }
			                    fileOutput.write(data, 0, count);
			                }
			            } catch (final Exception e) {
		                    activity.runOnUiThread(new Runnable(){
				    			@Override
				    			public void run(){
				    				progressPopupText.setText(context.getResources().getString(R.string.popup_view_download_progressbar_real_error) + e.toString());
				    				progressPopupCancel.setVisibility(View.GONE);
				    				progressPopupDone.setVisibility(View.VISIBLE);
				    			}
			                });	
		    	            wl.release();
		                    return;
			            } finally {
			                try {
			                    if (fileOutput != null){
			                    	fileOutput.flush();
			                        fileOutput.close();
			                    }
			                    if (input != null)
			                        input.close();
			                } 
			                catch (IOException ignored) { }
		
			                if (connection != null)
			                    connection.disconnect();
			                
			                activity.runOnUiThread(new Runnable(){
			    				@Override
			    				public void run(){
						        	progressPopupProgBar.setProgress(progress + 1);
						        	progressPopupCountText.setText((progress + 1) + "/" + downloadWordList.size());

			    				}
			    			});	
			                
							db = pHelper.getWritableDatabase();
					        ContentValues row = new ContentValues();
							row.put("word", downloadWordList.get(i).getWord());
							row.put("version", downloadWordList.get(i).getVersion());
							row.put("category", selectedCategoryInt);

							db.insert("pronounce", null, row);
					    	db.close();
			            }
			        }catch(Exception e){
			        	e.printStackTrace();
			        }
		        }
	            wl.release();

	            Log.i("STEVEN", "DONE");
                activity.runOnUiThread(new Runnable(){
    				@Override
    				public void run(){
    					progressPopupProgBar.setVisibility(View.INVISIBLE);
    					progressPopupCountText.setVisibility(View.INVISIBLE);
    					progressPopupText.setText(context.getResources().getString(R.string.popup_view_download_progressbar_done));
    					progressPopupCancel.setVisibility(View.GONE);
    					progressPopupDone.setVisibility(View.VISIBLE);
    				}
    			});	
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
				getSoundFlag = false;
		    }    
		};
		new Thread(download).start();
	}
}