package com.todpop.api.request;

import java.io.InputStream;

import com.todpop.api.FileManager;
import com.todpop.api.LockInfo;
import com.todpop.saltyenglish.db.LockerDBHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

public class LockScreenDownloadImage  extends AsyncTask<Void, Void, Bitmap> {
	private static LockScreenDownloadImage downloadImageTask = null;
	private LockInfo lockInfo;
	private LockerDBHelper lHelper;
	private SQLiteDatabase db;
	
    public LockScreenDownloadImage(Context context, LockInfo lockInfo){
    	this.lockInfo = lockInfo;
		lHelper = new LockerDBHelper(context);
    }
    /**
     * Return the default singleton instance
     * 
     * @param context
     * @param imageView
     * 
     * @return DownloadPronounce instance.
     */
    public static LockScreenDownloadImage getTask(Context context, LockInfo lockInfo) {
        if(downloadImageTask != null)
        	return downloadImageTask;
        
        return new LockScreenDownloadImage(context, lockInfo);
    }
    
    protected Bitmap doInBackground(Void... urls) {
        //String urldisplay = "www.todpop.co.kr" + lockInfo.getImage();
        String urldisplay = "http://www.todpop.co.kr" + lockInfo.getImage();
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {	
		Log.i("STEVEN", "img down post execute before save file");        
    	if(new FileManager().saveImgFile(result, lockInfo.getGroupId())){
    		Log.i("STEVEN", "img down post execute after save file success");        
    		
    		db = lHelper.getWritableDatabase();
    		
			ContentValues rowCurrent = new ContentValues();
			ContentValues rowHistory = new ContentValues();
    		
			rowCurrent.put("category", lockInfo.getGroup());
			rowCurrent.put("id", lockInfo.getId());
			rowCurrent.put("type", lockInfo.getType());
			rowCurrent.put("image", lockInfo.getImage());
			rowCurrent.put("target_url", lockInfo.getTargetUrl());
			rowCurrent.put("reward", lockInfo.getReward());
			rowCurrent.put("point", lockInfo.getPoint());

			db.insert("latest", null, rowCurrent);
			
			rowHistory.put("category_id", lockInfo.getGroupId());
			rowHistory.put("reward", lockInfo.getReward());
			rowHistory.put("point", lockInfo.getPoint());
			
			db.insert("history", null, rowHistory);
			
			db.close();
    	}
    	else{
    		Log.i("STEVNE", "downloaded image file save error!!");
    	}
    }
}