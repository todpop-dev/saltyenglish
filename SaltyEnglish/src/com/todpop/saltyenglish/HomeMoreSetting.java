package com.todpop.saltyenglish;

import android.annotation.SuppressLint;
import android.app.DownloadManager.Request;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.todpop.api.FileManager;
import com.todpop.api.TypefaceActivity;

@SuppressLint("NewApi") public class HomeMoreSetting extends TypefaceActivity {

	private static final int REQUEST_CODE_IMAGE = 0;
	SharedPreferences setting;
	SharedPreferences.Editor settingEdit;

	SharedPreferences rgInfo;
	SharedPreferences.Editor rgInfoEdit;

	private String nickname;
	private boolean isPopupEnabled;
	private String alarmTime;

	private EditText etNickName;
	private TextView tvFrontTime;
	private TextView tvBackTime;

	private TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			String strHour = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
			String strMin = minute < 10 ?  "0" + minute : "" + minute; 
			alarmTime = strHour + ":" + strMin;
			tvFrontTime.setText(strHour);
			tvBackTime.setText(strMin);
		}
	};
	private Switch swAlarmOnOff;
	private Switch swPopupOnOff;
	private boolean isAlarmEnabled;
	private LinearLayout llSettingAlarm;
	private ImageView ivMyPicture;
	private Bitmap bmpMyPicture;
	private FileManager fm;

	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_more_setting);


		setting = getSharedPreferences("setting", 0);
		settingEdit = setting.edit();

		rgInfo = getSharedPreferences("rgInfo", 0);
		rgInfoEdit = rgInfo.edit();

		nickname = rgInfo.getString("nickname", "");
		isPopupEnabled = setting.getBoolean("isPopupEnabled", true);
		isAlarmEnabled = setting.getBoolean("isAlarmEnabled", true);
		alarmTime = setting.getString("alarmTime","00:00");
		
		llSettingAlarm = (LinearLayout)findViewById(R.id.ll_setting_alarm); 
		
		ivMyPicture = (ImageView)findViewById(R.id.iv_setting_mypicture);

		etNickName = (EditText)findViewById(R.id.et_setting_name);
		tvFrontTime = (TextView)findViewById(R.id.tv_setting_alarm_time_front);
		tvBackTime = (TextView)findViewById(R.id.tv_setting_alarm_time_back);

		swAlarmOnOff = (Switch)findViewById(R.id.sw_setting_alarm_onoff);
		swPopupOnOff= (Switch)findViewById(R.id.sw_setting_popup_onoff);
		
		fm = new FileManager();
		bmpMyPicture = fm.getImgFile("myPicture");
		if(bmpMyPicture != null){
			ivMyPicture.setBackground(new BitmapDrawable(bmpMyPicture));
		}

		swAlarmOnOff.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				isAlarmEnabled = isChecked;
				Log.e("?",isAlarmEnabled+"");
				if(!isChecked){
					llSettingAlarm.setAlpha(0.3f);
				}else{
					llSettingAlarm.setAlpha(1f);
				}
			}
		});

		swPopupOnOff.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Log.e("!",isPopupEnabled+"");
				isPopupEnabled = isChecked;
			}
		});

		initSettingViews();
	}

	private void initSettingViews() {
		etNickName.setText(nickname);
		String[] timeToken = alarmTime.split(":");
		Log.e("alarmTime",alarmTime);
		tvFrontTime.setText(timeToken[0]);
		tvBackTime.setText(timeToken[1]);
		swPopupOnOff.setChecked(isPopupEnabled);
		swAlarmOnOff.setChecked(isAlarmEnabled);
		if(!isAlarmEnabled){
			llSettingAlarm.setAlpha(0.3f);
		}
	}

	public void setMyPicture(View v){
		Intent intent =new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		this.startActivityForResult(intent, REQUEST_CODE_IMAGE);
	}
	
	@SuppressLint("NewApi") @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == REQUEST_CODE_IMAGE && resultCode == RESULT_OK && data != null){
			Uri selectImageUri = data.getData();
			String[] filePathCol = {MediaStore.Images.Media.DATA};
			
			Cursor cursor = this.getContentResolver().query(selectImageUri, filePathCol, null, null, null);
			cursor.moveToFirst();
			
			int colIndex = cursor.getColumnIndex(filePathCol[0]);
			String imgPath = cursor.getString(colIndex);
			cursor.close();
			
			bmpMyPicture = BitmapFactory.decodeFile(imgPath);
			ivMyPicture.setBackground(new BitmapDrawable(bmpMyPicture));
			
		}
	}

	public void setAlarmTime(View v){
		TimePickerDialog timePicker = new TimePickerDialog(HomeMoreSetting.this, TimePickerDialog.THEME_HOLO_LIGHT,timeSetListener, 0, 0, false);
		timePicker.setTitle("시간 설정");
		timePicker.show();
	}

	public void initStudyInfo(View v){
		// init all
	}

	public void saveSetting(View v){
		rgInfoEdit.putString("nickname", etNickName.getText().toString());
		settingEdit.putBoolean("isPopupEnabled", isPopupEnabled);
		settingEdit.putBoolean("isAlarmEnabled", isAlarmEnabled);
		settingEdit.putString("alarmTime", alarmTime);
		rgInfoEdit.apply();
		settingEdit.apply();
		
		fm.saveImgFile(bmpMyPicture, "myPicture");
		
		finish();
	}

}
