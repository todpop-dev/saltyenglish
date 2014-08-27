package com.todpop.saltyenglish;

import java.io.InputStream;
import java.lang.ref.WeakReference;

import com.todpop.api.TypefaceFragmentActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class StudyBeginCPDFragment extends Fragment{
	ImageView cpdImg;
	
	BitmapDrawable front;
	BitmapDrawable back;
	
	private boolean isBack;
	Animation animationFirst;
	Animation animationLast;
	private AnimationListener aniListener;
	
	private Button cpdfs;
	
	private boolean shareTried = false;
	
	static StudyBeginCPDFragment init(int adType, boolean history, String reward, String point, String frontImgSrc, String backImgSrc){
		StudyBeginCPDFragment fragment = new StudyBeginCPDFragment();
		
		Bundle args = new Bundle();
		args.putInt("adType", adType);
		args.putBoolean("history", history);
		args.putString("reward", reward);
		args.putString("point", point);
		args.putString("frontImgSrc", frontImgSrc);
		args.putString("backImgSrc", backImgSrc);
		fragment.setArguments(args);
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		isBack = false;
		animationFirst = AnimationUtils.loadAnimation( getActivity(), R.drawable.studytestc_drawable_flip_card_back_scale);
		animationLast = AnimationUtils.loadAnimation( getActivity(), R.drawable.studytestc_drawable_flip_card_front_scale); 
		aniListener = new Animation.AnimationListener() 
		{ 
			@Override 
			public void onAnimationStart(Animation animation) { 
			} 
			@Override 
			public void onAnimationRepeat(Animation animation) { 
			} 
			@Override 
			public void onAnimationEnd(Animation animation) { 	
				if (isBack) {
					cpdImg.setImageDrawable(front);
					isBack = false;
				} else {
					cpdImg.setImageDrawable(back);
					isBack = true;
				}
				cpdImg.startAnimation(animationLast);
			}
		};
		animationFirst.setAnimationListener(aniListener);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){		
		Bundle bundle = getArguments();
		
		int adType = bundle.getInt("adType");
		boolean history = bundle.getBoolean("history");
		String reward = bundle.getString("reward");
		String point = bundle.getString("point");
		String frontImgSrc = bundle.getString("frontImgSrc");
		String backImgSrc = bundle.getString("backImgSrc");
		
		View layoutView = inflater.inflate(R.layout.fragment_study_begin_finish, container, false);
		cpdImg = (ImageView)layoutView.findViewById(R.id.studyfinish_id_pop);
		Button cpdCoupon = (Button)layoutView.findViewById(R.id.studyfinish_id_coupon);
		cpdfs = (Button)layoutView.findViewById(R.id.studyfinish_id_facebook_share);
		RelativeLayout cpdfsLayout = (RelativeLayout)layoutView.findViewById(R.id.studyfinish_fb_share_layout);
		TextView cpdfsReward = (TextView)layoutView.findViewById(R.id.studyfinish_fb_share_reward);
		
		if(adType == 102){		 			//cpd with coupon
			cpdCoupon.setVisibility(View.VISIBLE);
		}				
		else if(adType == 103){				//cpdfs
			cpdfs.setVisibility(View.VISIBLE);
			cpdfsLayout.setVisibility(View.VISIBLE);
			if(history || shareTried){
				cpdfs.setEnabled(false);
				cpdfsReward.setText(R.string.facebook_share_history);
			}
			else{
				if(reward.equals("0") || reward.equals("null")){
					cpdfsReward.setText(point + " point");
				}
				else{
					cpdfsReward.setText(reward + getResources().getString(R.string.testname8));
				}
			}
		}
		ClickListener listener = new ClickListener();
		cpdImg.setOnClickListener(listener);
		cpdfs.setOnClickListener(listener);

		TypefaceFragmentActivity.setFont(cpdfsReward);
		
		FrontImageDownloadTask frontImageDown = new FrontImageDownloadTask(cpdImg);
		frontImageDown.execute(frontImgSrc);
		BackImageDownloadTask backImageDown = new BackImageDownloadTask();
		backImageDown.execute(backImgSrc);
		
		return layoutView;
	}
	@Override
	public void onDestroyView(){
		super.onDestroyView();
		
		Drawable imgDrawable = cpdImg.getDrawable();
		
		if(imgDrawable instanceof BitmapDrawable){
			Bitmap bitmap = ((BitmapDrawable)imgDrawable).getBitmap();
			bitmap.recycle();
			bitmap = null;
		}
		if(imgDrawable != null)
			imgDrawable.setCallback(null);
		
		if(front != null){
			front.getBitmap().recycle();
			front = null;
		}
		if(back != null){
			back.getBitmap().recycle();
			back = null;
		}
	}
	
	private class ClickListener implements OnClickListener{
		@Override
		public void onClick(View v)
		{
			switch(v.getId()){
			case R.id.studyfinish_id_pop:
				cpdImg.startAnimation(animationFirst);
				break;
			case R.id.studyfinish_id_facebook_share:
				((StudyBeginReBuild)getActivity()).publishAdBtn();
				cpdfs.setEnabled(false);
			}
		} 
	}
	
	private class FrontImageDownloadTask extends AsyncTask<String, Void, Bitmap> {
		private final WeakReference<ImageView> imageViewReference;
		
		public FrontImageDownloadTask(ImageView front){
			imageViewReference = new WeakReference<ImageView>(front);
		}
		
		protected Bitmap doInBackground(String... urls) {
			String urldisplay = urls[0];
			Bitmap mIcon11 = null;
			try {
				InputStream in = new java.net.URL(urldisplay).openStream();
				mIcon11 = BitmapFactory.decodeStream(in);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return mIcon11;
		}

		protected void onPostExecute(Bitmap result) 
		{
			try{
				if( imageViewReference != null && result != null){
					final ImageView imageView = imageViewReference.get();

					if( imageView != null && result != null){
						front = new BitmapDrawable(getResources(), result);
						/*Drawable[] layers = {imageView.getDrawable(), bitmap};
						TransitionDrawable transDrawable = new TransitionDrawable(layers);*/
						imageView.setImageDrawable(front);
						//transDrawable.startTransition(300);
					}
				}
			}
			catch(Exception e){
				e.printStackTrace();
				result.recycle();
			}
		}
	}
	private class BackImageDownloadTask extends AsyncTask<String, Void, Bitmap> {
		protected Bitmap doInBackground(String... urls) {
			String urldisplay = urls[0];
			Bitmap mIcon11 = null;
			try {
				InputStream in = new java.net.URL(urldisplay).openStream();
				mIcon11 = BitmapFactory.decodeStream(in);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return mIcon11;
		}

		protected void onPostExecute(Bitmap result) 
		{
			try{
				if( result != null){
					back = new BitmapDrawable(getResources(), result);
				}
			}
			catch(Exception e){
				e.printStackTrace();
				result.recycle();
			}
		}
	}
}
