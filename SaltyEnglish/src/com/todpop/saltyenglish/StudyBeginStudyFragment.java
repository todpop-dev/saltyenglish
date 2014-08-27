package com.todpop.saltyenglish;

import java.io.InputStream;
import java.lang.ref.WeakReference;

import com.todpop.api.TypefaceFragmentActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class StudyBeginStudyFragment extends Fragment{
	StudyBeginWord studyBeginWord;
	
	LinearLayout card;
	
	TextView word;
	TextView pron;
	TextView example;
	ImageView wordImg;
	
	private boolean isKor;
	Animation animationFirst;
	Animation animationLast;
	private AnimationListener aniListener;
	
	static StudyBeginStudyFragment init(int position, StudyBeginWord studyBeginWord){
		StudyBeginStudyFragment fragment = new StudyBeginStudyFragment();
		
		Bundle args = new Bundle();
		args.putInt("position", position);
		args.putParcelable("studyWord", studyBeginWord);
		fragment.setArguments(args);
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		isKor = false;
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
				if (isKor) {
					word.setText(studyBeginWord.getEngWord());
					pron.setText(studyBeginWord.getPhonetics());
					example.setText(studyBeginWord.getEngExample());
					isKor = false;
				} else {
					word.setText(studyBeginWord.getKorWord());
					pron.setText("");
					example.setText(studyBeginWord.getKorExample());
					isKor = true;
				}
				card.startAnimation(animationLast);
			}
		};
		animationFirst.setAnimationListener(aniListener);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		Bundle bundle = getArguments();
		
		int position = bundle.getInt("position");
		studyBeginWord = bundle.getParcelable("studyWord");
		
		View layoutView = inflater.inflate(R.layout.fragment_study_begin, container, false);
		ImageView pageNo = (ImageView)layoutView.findViewById(R.id.fragment_study_begin_id_word_on);
		card = (LinearLayout)layoutView.findViewById(R.id.fragment_study_begin_whole_card);
		card.setOnClickListener(new ClickListener());
		
		word = (TextView)layoutView.findViewById(R.id.study_word_tv);
		pron = (TextView)layoutView.findViewById(R.id.study_word_pron_tv);
		example = (TextView)layoutView.findViewById(R.id.study_word_ex_tv);
		wordImg = (ImageView)layoutView.findViewById(R.id.fragment_study_begin_id_word_img);
		
		TypefaceFragmentActivity.setFont(word);
		TypefaceFragmentActivity.setFont(pron);
		TypefaceFragmentActivity.setFont(example);
		
		word.setText(studyBeginWord.getEngWord());
		pron.setText(studyBeginWord.getPhonetics());
		example.setText(studyBeginWord.getEngExample());
		
		DownloadImageTask downloadImg = new DownloadImageTask(wordImg);
		downloadImg.execute("http://todpop.co.kr" + studyBeginWord.getImgUrl());
		
		switch(position){
		case 0:
			pageNo.setBackgroundResource(R.drawable.study_8_img_number_1);
			break;
		case 1:
			pageNo.setBackgroundResource(R.drawable.study_8_img_number_2);
			break;
		case 2:
			pageNo.setBackgroundResource(R.drawable.study_8_img_number_3);
			break;
		case 3:
			pageNo.setBackgroundResource(R.drawable.study_8_img_number_4);
			break;
		case 4:
			pageNo.setBackgroundResource(R.drawable.study_8_img_number_5);
			break;
		case 5:
			pageNo.setBackgroundResource(R.drawable.study_8_img_number_6);
			break;
		case 6:
			pageNo.setBackgroundResource(R.drawable.study_8_img_number_7);
			break;
		case 7:
			pageNo.setBackgroundResource(R.drawable.study_8_img_number_8);
			break;
		case 8:
			pageNo.setBackgroundResource(R.drawable.study_8_img_number_9);
			break;
		case 9:
			pageNo.setBackgroundResource(R.drawable.study_8_img_number_10);
			break;
		}
		
		return layoutView;
	}
	@Override
	public void onDestroyView(){
		super.onDestroyView();
		
		Drawable imgDrawable = wordImg.getDrawable();
		
		if(imgDrawable instanceof BitmapDrawable){
			Bitmap bitmap = ((BitmapDrawable)imgDrawable).getBitmap();
			bitmap.recycle();
			bitmap = null;
		}
		if(imgDrawable != null)
			imgDrawable.setCallback(null);
	}
	
	private class ClickListener implements OnClickListener{
		@Override
		public void onClick(View v)
		{
			card.startAnimation(animationFirst);
		} 
	}
	
	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
		private final WeakReference<ImageView> imageViewReference;
		
		public DownloadImageTask(ImageView front){
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
					if( imageView != null){
						/*Drawable[] layers = {imageView.getDrawable(), new BitmapDrawable(getResources(), result)};
						TransitionDrawable transDrawable = new TransitionDrawable(layers);
						imageView.setImageDrawable(transDrawable);
						transDrawable.startTransition(300);*/
						imageView.setImageDrawable( new BitmapDrawable(getResources(), result));
					}
				}
			}
			catch(Exception e){
				e.printStackTrace();
				result.recycle();
			}
		}
	}
}
