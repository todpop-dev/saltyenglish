package com.todpop.api;

import com.todpop.saltyenglish.R;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
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
	TextView progressText;
	Animation spinImgAni;
	
    public LoadingDialog(Context aContext){
    	this.context = aContext;
    	
    	dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_progress);
		dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		progressImg = (ImageView)dialog.findViewById(R.id.popup_loading_id_img);
		progressText = (TextView)dialog.findViewById(R.id.popup_loading_id_text);
		new TypefaceActivity().setFont(progressText);
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
    
    public void showMigration(){
    	progressText.setText(R.string.popup_progress_update);
		progressImg.startAnimation(spinImgAni);
		dialog.show();
    }
}
