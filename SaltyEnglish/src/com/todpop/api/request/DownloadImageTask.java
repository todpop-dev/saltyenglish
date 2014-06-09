package com.todpop.api.request;

import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

/**
 * Copyright 2014 TODPOP Corp. All rights reserved.
 * 
 * @author steven@todpop.co.kr
 * @version 1.0
 * 
 */
public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
	private static DownloadImageTask downloadImageTask = null;
	private ImageView imageView;
	
    private DownloadImageTask(ImageView imageView){
    	this.imageView = imageView;
    }
    /**
     * Return the default singleton instance
     * 
     * @param context
     * @param imageView
     * 
     * @return DownloadPronounce instance.
     */
    public static DownloadImageTask getTask(ImageView imageView) {
        if(downloadImageTask != null)
        	return downloadImageTask;
        
        return new DownloadImageTask(imageView);
    }
    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
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

    protected void onPostExecute(Bitmap result) 
    {	        
    	// Update UI
    	imageView.setImageBitmap(result);
    }
}
