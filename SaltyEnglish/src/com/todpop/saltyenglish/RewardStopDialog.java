package com.todpop.saltyenglish;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

public class RewardStopDialog extends DialogFragment{
	static RewardStopDialog newInstance(){
		RewardStopDialog dialog = new RewardStopDialog();
		return dialog;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View v = inflater.inflate(R.layout.dialog_reward_popup, container, false);
		
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		
		ImageView img = (ImageView)v.findViewById(R.id.dialog_reward_stop_img);
		img.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				getDialog().dismiss();
			}
		});
		
		return v;
	}
}
