package com.todpop.saltyenglish;


import java.io.File;

import org.json.JSONObject;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;
import com.todpop.api.TypefaceActivity;
import com.todpop.api.request.DownloadPronounce;
import com.todpop.saltyenglish.db.PronounceDBHelper;
import com.todpop.saltyenglish.db.WordDBHelper;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

public class HomeMorePronounce extends TypefaceActivity {

	private static final int NO = 0;
	
	private static final int BASIC = 1;
	private static final int MIDDLE = 2;
	private static final int HIGH = 3;
	private static final int TOEIC = 4;
	private static final int ALL = 0;
	
	LinearLayout mainLayout;
	
    PopupWindow progressPopupWindow;
    View progressPopupView;
    TextView progressPopupText;
    TextView progressPopupCountText;
    ProgressBar progressPopupLoadProgBar;
    ProgressBar progressPopupProgBar;
    Button progressPopupCancel;
    Button progressPopupDone;
    
    boolean deletingFlag;
    
	SharedPreferences rgInfo;
	SharedPreferences.Editor rgInfoEdit;
	
	DownloadPronounce downloadPronounce;
	DeleteFile deleteFile;

	PronounceDBHelper pHelper;
	SQLiteDatabase db;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_more_pronounce);
		
		mainLayout = (LinearLayout)findViewById(R.id.homemore_pronounce_id_layout);
		
		rgInfo = getSharedPreferences("rgInfo",0);
		rgInfoEdit = rgInfo.edit();

		progressPopupView = View.inflate(this, R.layout.popup_view_download_progressbar, null);
		progressPopupWindow = new PopupWindow(progressPopupView, ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT,true);
		progressPopupText = (TextView)progressPopupView.findViewById(R.id.popup_id_text);
    	progressPopupCountText = (TextView)progressPopupView.findViewById(R.id.popup_download_id_count);	
    	progressPopupLoadProgBar = (ProgressBar)progressPopupView.findViewById(R.id.popup_download_id_loading_progressbar);
    	progressPopupProgBar = (ProgressBar)progressPopupView.findViewById(R.id.popup_download_id_progressbar);
    	progressPopupCancel = (Button)progressPopupView.findViewById(R.id.popup_download_id_btn_cancel);
    	progressPopupDone = (Button)progressPopupView.findViewById(R.id.popup_download_id_btn_done);

		setFont(progressPopupText);
		setFont(progressPopupCountText);
    	
		pHelper = new PronounceDBHelper(this);
		
		deletingFlag = false;
	}
	
	@Override
	public void onResume()
	{
		super.onResume();

		com.facebook.AppEventsLogger.activateApp(this, "218233231697811");
	}

	public void deleteBasic(View view){
		deleteFile = new DeleteFile();
		deleteFile.execute(BASIC);
	}
	
	public void downBasic(View view){
		deletingFlag = false;
		progressPopupWindow.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);
		downloadPronounce = DownloadPronounce.getTask(getApplicationContext(), this, BASIC, progressPopupView);
		downloadPronounce.execute("");
	}

	public void deleteMiddle(View view){
		deleteFile = new DeleteFile();
		deleteFile.execute(MIDDLE);	
	}
	
	public void downMiddle(View view){
		deletingFlag = false;
		progressPopupWindow.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);
		downloadPronounce = DownloadPronounce.getTask(getApplicationContext(), this, MIDDLE, progressPopupView);
		downloadPronounce.execute("");
		
	}
	
	public void deleteHigh(View view){
		deleteFile = new DeleteFile();
		deleteFile.execute(HIGH);
	}
	
	public void downHigh(View view){
		deletingFlag = false;
		progressPopupWindow.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);
		downloadPronounce = DownloadPronounce.getTask(getApplicationContext(), this, HIGH, progressPopupView);
		downloadPronounce.execute("");
		
	}
	
	public void deleteToeic(View view){
		deleteFile = new DeleteFile();
		deleteFile.execute(TOEIC);	
	}
	
	public void downToeic(View view){
		deletingFlag = false;
		progressPopupWindow.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);
		downloadPronounce = DownloadPronounce.getTask(getApplicationContext(), this, TOEIC, progressPopupView);
		downloadPronounce.execute("");
		
	}
	
	public void deleteAll(View view){
		deleteFile = new DeleteFile();
		deleteFile.execute(ALL);
	}
	
	public void downAll(View view){
		deletingFlag = false;
		progressPopupWindow.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);
		downloadPronounce = DownloadPronounce.getTask(getApplicationContext(), this, ALL, progressPopupView);
		downloadPronounce.execute("");
		
	}
	
	private class DeleteFile extends AsyncTask<Integer, Integer, Boolean>{
		int category;
		boolean noFile;
		@Override
		protected void onPreExecute(){
			Log.i("STEVEN", "deleteFile");
			progressPopupText.setText(R.string.popup_view_download_progressbar_list_loading);
			progressPopupWindow.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);
			db = pHelper.getWritableDatabase();
			noFile = true;
			deletingFlag = true;

			progressPopupProgBar.setVisibility(View.VISIBLE);
	        progressPopupLoadProgBar.setVisibility(View.GONE);
			progressPopupCountText.setVisibility(View.VISIBLE);
	        progressPopupProgBar.setIndeterminate(false);
		}
		@Override
		protected Boolean doInBackground(Integer... params) {

			Log.i("STEVEN", "DELETE FILE starts!!!!!");
			// TODO Auto-generated method stub
			category = params[0];
			if(category == 0){ //delete all
				for(int i = 1; i <= 4; i++){
					final int current = i;
					final Cursor find = db.rawQuery("SELECT distinct word FROM pronounce WHERE category=\'" + i + "\'", null);
					if(find.getCount() > 0){
						noFile = false;
						runOnUiThread(new Runnable(){
							@Override
							public void run(){
								progressPopupText.setText(getResources().getIdentifier("popup_view_download_progressbar_deleting_" + current, "string", getPackageName()));
								progressPopupProgBar.setMax(find.getCount());
							}
						});
						int count = 1;
						while(find.moveToNext()){
							if(isCancelled()){
								runOnUiThread(new Runnable(){
									@Override
									public void run(){
										cancelDeleteFunc();
									}
								});
								deletingFlag = false;
								return false;
							}
							publishProgress(count, find.getCount());
							String name = Environment.getExternalStorageDirectory().getAbsolutePath() 
									+ "/Android/data/com.todpop.saltyenglish/pronounce/" + find.getString(0) + ".data";
							//Log.i("STEVEN", "DELETE FILE name = " + name);
							File file = new File(name);
							
							if(!file.delete()){ //if fail to delete file	
								runOnUiThread(new Runnable(){
									@Override
									public void run(){
										deleteErrorFunc();
									}
								});		
								return false;
							}
							else{
								db.delete("pronounce", "word='" + name + "'", null);
							}
							count++;
						}
					}
				}
				if(noFile){	
					runOnUiThread(new Runnable(){
						@Override
						public void run(){
							noFileFunc();
						}
					});		
					return false;
				}
			}
			else{
				final Cursor find = db.rawQuery("SELECT distinct word FROM pronounce WHERE category=\'" + category + "\'", null);
				if(find.getCount() > 0){
					runOnUiThread(new Runnable(){
						@Override
						public void run(){
							progressPopupText.setText(getResources().getIdentifier("popup_view_download_progressbar_deleting_" + category, "string", getPackageName()));
					        progressPopupProgBar.setMax(find.getCount());
						}
					});
					int count = 1;
					while(find.moveToNext()){
						if(isCancelled()){
							runOnUiThread(new Runnable(){
								@Override
								public void run(){
									cancelDeleteFunc();
								}
							});
							return false;
						}
						publishProgress(count, find.getCount());
						String name = Environment.getExternalStorageDirectory().getAbsolutePath() 
								+ "/Android/data/com.todpop.saltyenglish/pronounce/" + find.getString(0) + ".data";
						//Log.i("STEVEN", "DELETE FILE name = " + name);
						File file = new File(name);
						if(!file.delete()){ //if fail to delete file		
							runOnUiThread(new Runnable(){
								@Override
								public void run(){
									deleteErrorFunc();
								}
							});		
							return false;
						}
						else{
							db.delete("pronounce", "word='" + name + "'", null);
						}
						count++;
					}
				}
				else{ //no file
					runOnUiThread(new Runnable(){
						@Override
						public void run(){
							noFileFunc();
						}
					});		
					return false;
				}
			}
			return true;
		}	   
		
		@Override
	    protected void onProgressUpdate(Integer... progress) {
	        super.onProgressUpdate(progress);
	        progressPopupProgBar.setProgress(progress[0]);
	        progressPopupCountText.setText(progress[0] + "/" + progress[1]);
	    }
		@Override
	    protected void onPostExecute(Boolean result) {
			deletingFlag = false;
			if(result){ 	//delete complete
				progressPopupText.setText(R.string.popup_view_download_progressbar_delete_done);
				progressPopupCancel.setVisibility(View.GONE);
				progressPopupDone.setVisibility(View.VISIBLE);
				SharedPreferences.Editor studyInfoEdit = getSharedPreferences("studyInfo",0).edit();
				switch(category){
				case 1:
					studyInfoEdit.putInt("basicCategorySound", NO);
					break;
				case 2:
					studyInfoEdit.putInt("middleCategorySound", NO);
					break;
				case 3:
					studyInfoEdit.putInt("highCategorySound", NO);
					break;
				case 4:
					studyInfoEdit.putInt("toeicCategorySound", NO);
					break;
				}
				studyInfoEdit.apply();
			}
			return;
		}
	}
	
	private void cancelDeleteFunc(){
		progressPopupText.setText(R.string.popup_view_download_progressbar_canceled);
		progressPopupProgBar.setVisibility(View.GONE);
		progressPopupCountText.setVisibility(View.GONE);
		progressPopupCancel.setVisibility(View.GONE);
		progressPopupDone.setVisibility(View.VISIBLE);
	}
	private void deleteErrorFunc(){
		progressPopupText.setText(R.string.popup_view_download_progressbar_delete_error);
		progressPopupProgBar.setVisibility(View.GONE);
		progressPopupCountText.setVisibility(View.GONE);
		progressPopupCancel.setVisibility(View.GONE);
		progressPopupDone.setVisibility(View.VISIBLE);
	}
	private void noFileFunc(){
		progressPopupText.setText(R.string.popup_view_download_progressbar_delete_no_list);
        progressPopupLoadProgBar.setVisibility(View.GONE);
		progressPopupProgBar.setVisibility(View.GONE);
		progressPopupCountText.setVisibility(View.GONE);
		progressPopupCancel.setVisibility(View.GONE);
		progressPopupDone.setVisibility(View.VISIBLE);
		
	}
	public void cancelDownload(View view){
		Log.i("STEVEN", String.valueOf(deletingFlag));
		if(deletingFlag){
			Log.i("STEVEN", "inside flag");
			deleteFile.cancel(true);
		}
		else{
			downloadPronounce.cancel();
			Log.i("STEVEn", "cancelDonwload function");
		}
	}
	public void doneDownload(View view){
		progressPopupWindow.dismiss();
		progressPopupDone.setVisibility(View.GONE);
		progressPopupCancel.setVisibility(View.VISIBLE);
		progressPopupProgBar.setProgress(0);
		progressPopupCountText.setText("");
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.home_more, menu);
		return false;
	}
	
	// on click
	public void onClickBack(View view)
	{
		finish();
	}
	
	@Override
	protected void onStart()
	{
		super.onStart();
		FlurryAgent.onStartSession(this, "ZKWGFP6HKJ33Y69SP5QY");
		FlurryAgent.logEvent("See More");
	    EasyTracker.getInstance(this).activityStart(this);
	}
	 
	@Override
	protected void onStop()
	{
		super.onStop();		
		FlurryAgent.onEndSession(this);
	    EasyTracker.getInstance(this).activityStop(this);
	}	
}