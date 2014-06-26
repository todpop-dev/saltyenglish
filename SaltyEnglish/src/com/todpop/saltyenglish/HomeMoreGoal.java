package com.todpop.saltyenglish;

import java.util.ArrayList;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.todpop.api.TypefaceActivity;

public class HomeMoreGoal extends TypefaceActivity {

	ArrayList<String> titleArray = new ArrayList<String>() ;
	ArrayList<String> itemArray = new ArrayList<String>();

	private int curFrontNum;
	private int curBackNum;
	private ImageButton ibFrontNum;
	private ImageButton ibBackNum;
	private int goalDailyWords;
	private SharedPreferences sp;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_more_goal);

		sp = getSharedPreferences("memInfo", 0);
		goalDailyWords= sp.getInt("goalDailyWords", 0);

		ibFrontNum = (ImageButton)findViewById(R.id.ib_goal_front_num);
		ibBackNum= (ImageButton)findViewById(R.id.ib_goal_back_num);

		initGoalNumber();
	}

	private void initGoalNumber() {
		int frontNum = goalDailyWords / 10;
		int backNum = goalDailyWords % 10;

		curFrontNum = frontNum;
		curBackNum = backNum;

		updateGoalNumb();
	}

	public void addGoal(View v){
		if(getGoalNum() != 99){
			curBackNum++;
			if(curBackNum == 10){
				curFrontNum++;
				curBackNum = 0;
			}
			updateGoalNumb();
		}
	}

	public void subGoal(View v){
		if(getGoalNum() != 0){
			curBackNum--;
			if(curBackNum == -1){
				curFrontNum--;
				curBackNum = 9;
			}
			updateGoalNumb();
		}
	}

	public void updateGoalNumb(){
		ibFrontNum.setImageResource(R.drawable.goal_img_number_0+curFrontNum);
		ibBackNum.setImageResource(R.drawable.goal_img_number_0+curBackNum);
	}

	// on click
	public void onClickBack(View view)
	{
		finish();
	}

	public void setGoal(View v){
		Editor editor = sp.edit();
		editor.putInt("goalDailyWords", getGoalNum());
		editor.apply();
		finish();
	}

	int getGoalNum(){
		return (curFrontNum*10)+curBackNum;
	}

}



