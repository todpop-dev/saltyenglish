package com.todpop.saltyenglish;

import android.os.Parcel;
import android.os.Parcelable;

public class StudyBeginWord implements Parcelable {
	private String engWord;
	private String korWord;
	private String engExample;
	private String korExample;
	private String phonetics;
	private String imgUrl;
	private String voiceVer;

	public StudyBeginWord() {

	}

	public StudyBeginWord(Parcel in) {
		readFromParcel(in);
	}

	public StudyBeginWord(String inEngWord, String inKorWord, String inEngEx,
			String inKorEx, String inPhonetics, String inImgUrl, String inVoiceVer) {
		engWord = inEngWord;
		korWord = inKorWord;
		engExample = inEngEx;
		korExample = inKorEx;
		phonetics = inPhonetics;
		imgUrl = inImgUrl;
		voiceVer = inVoiceVer;
	}

	@Override
	public void writeToParcel(Parcel dest, int flag) {
		dest.writeString(engWord);
		dest.writeString(korWord);
		dest.writeString(engExample);
		dest.writeString(korExample);
		dest.writeString(phonetics);
		dest.writeString(imgUrl);
		dest.writeString(voiceVer);
	}

	private void readFromParcel(Parcel in) {
		engWord = in.readString();
		korWord = in.readString();
		engExample = in.readString();
		korExample = in.readString();
		phonetics = in.readString();
		imgUrl = in.readString();
		voiceVer = in.readString();
	}

	public static final Parcelable.Creator<StudyBeginWord> CREATOR = new Parcelable.Creator<StudyBeginWord>() {

		@Override
		public StudyBeginWord createFromParcel(Parcel source) {
			return new StudyBeginWord(source);
		}

		@Override
		public StudyBeginWord[] newArray(int size) {
			return new StudyBeginWord[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}
	
	public String getEngWord() {
		return engWord;
	}

	public void setEngWord(String engWord) {
		this.engWord = engWord;
	}

	public String getKorWord() {
		return korWord;
	}

	public void setKorWord(String korWord) {
		this.korWord = korWord;
	}

	public String getEngExample() {
		return engExample;
	}

	public void setEngExample(String engExample) {
		this.engExample = engExample;
	}

	public String getKorExample() {
		return korExample;
	}

	public void setKorExample(String korExample) {
		this.korExample = korExample;
	}

	public String getPhonetics() {
		return phonetics;
	}

	public void setPhonetics(String phonetics) {
		this.phonetics = phonetics;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getVoiceVer() {
		return voiceVer;
	}

	public void setVoiceVer(String voiceVer) {
		this.voiceVer = voiceVer;
	}
}