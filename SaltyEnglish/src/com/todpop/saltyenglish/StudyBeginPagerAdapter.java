package com.todpop.saltyenglish;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class StudyBeginPagerAdapter extends FragmentStatePagerAdapter{
	
	ArrayList<StudyBeginWord> wordList;
	int adType;
	boolean history;
	String reward;
	String point;
	String frontImgSrc;
	String backImgSrc;
	
	public StudyBeginPagerAdapter(FragmentManager fm, ArrayList<StudyBeginWord> words, int adType, boolean history, String reward, String point, String frontImgSrc, String backImgSrc){
		super(fm);
		wordList = words;
		this.adType = adType;
		this.history = history;
		this.reward = reward;
		this.point = point;
		this.frontImgSrc = frontImgSrc;
		this.backImgSrc = backImgSrc;
	}
	@Override
	public Fragment getItem(int position){
		if(position != 10)
			return StudyBeginStudyFragment.init(position, wordList.get(position));
		else
			return StudyBeginCPDFragment.init(adType, history, reward, point, frontImgSrc, backImgSrc);
	}
	@Override
	public int getCount(){
		return 11;
	}
}
