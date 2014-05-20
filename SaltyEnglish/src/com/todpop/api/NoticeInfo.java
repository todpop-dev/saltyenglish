package com.todpop.api;

public class NoticeInfo{
	String title;
	String content;
	
	public NoticeInfo(String inTitle, String inContent){
		title = inTitle;
		content = inContent;
	}
	public String getTitle(){
		return title;
	}
	public String getContent(){
		return content;
	}
}
