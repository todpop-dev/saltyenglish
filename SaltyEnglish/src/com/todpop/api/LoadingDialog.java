package com.todpop.api;

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

import com.todpop.saltyenglish.R;
import com.todpop.saltyenglish.StudyHome;

import android.app.Activity;
import android.app.Dialog;
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
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.ImageView;
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
public class LoadingDialog{

    private Context context;
    private static LoadingDialog loadingDialog = null;

	Dialog dialog;
	ImageView progressImg;
	Animation spinImgAni;
	
    public LoadingDialog(Context aContext){
    	this.context = aContext;
    	
    	dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_progress);
		dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		progressImg = (ImageView)dialog.findViewById(R.id.popup_loading_id_img);
		dialog.setCancelable(false);
		
		spinImgAni = AnimationUtils.loadAnimation(context, R.anim.popup_loading_spin_set);
		spinImgAni.setAnimationListener(new AnimationListener(){
			@Override
			public void onAnimationEnd(Animation animation) {
				Animation spinImgAni = AnimationUtils.loadAnimation(context, R.anim.popup_loading_spin_set);
				spinImgAni.setAnimationListener(this);
				progressImg.startAnimation(spinImgAni);
			}
			@Override
			public void onAnimationRepeat(Animation animation) {
			}
			@Override
			public void onAnimationStart(Animation animation) {
			}
		});
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
    public static LoadingDialog getTask(Context context) {
        if(loadingDialog != null)
        	return loadingDialog;
        
        return new LoadingDialog(context);
    }
    
    public void show(){
		progressImg.startAnimation(spinImgAni);
		dialog.show();
    }
    
    public void dissmiss(){
    	dialog.dismiss();
    }
}