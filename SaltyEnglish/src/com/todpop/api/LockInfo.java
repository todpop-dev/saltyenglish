package com.todpop.api;

public class LockInfo{
	int group;
	int id;
	int type;
	String image;
	String target_url;
	int reward;
	int point;
	
	public LockInfo(int inGroup, int inId, int inType, String inImage, String inTarget, int inReward, int inPoint){
		group = inGroup;
		id = inId;
		type = inType;
		image = inImage;
		target_url = inTarget;
		reward = inReward;
		point = inPoint;
	}

	public int getGroup(){
		return group;
	}
	public int getId(){
		return id;
	}
	public String getGroupId(){
		return String.valueOf(group) + String.valueOf(id);
	}
	public int getType(){
		return type;
	}
	public String getImage(){
		return image;
	}
	public String getTargetUrl(){
		return target_url;
	}
	public int getReward(){
		return reward;
	}
	public int getPoint(){
		return point;
	}
	public void setReward(int in){
		reward = in;
	}
	public void setPoint(int in){
		point = in;
	}
}
