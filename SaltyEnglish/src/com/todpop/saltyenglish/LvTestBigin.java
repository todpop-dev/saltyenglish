package com.todpop.saltyenglish;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LvTestBigin extends Activity {
	RelativeLayout introView;
	AnimationDrawable imageTimeAni ;
	TranslateAnimation imageTimeBlindAni;
	ImageView imageTimeBlind;
	RelativeLayout endView;
	AnimationDrawable imageTestendAni;
	ImageView numberView;
	
	Button select1;
	Button select2;
	Button select3;
	Button select4;
	TextView enWord;
	String krWord1;
	String krWord2;
	String krWord3;
	String krWord4;
	int count=1;
	String level=null;
	float density;

	SharedPreferences rgInfo;
	SharedPreferences.Editor rgInfoEdit;
	
	SharedPreferences lvTextWord;
	SharedPreferences.Editor lvTextWordEdit;
	
	TranslateAnimation.AnimationListener MyAnimationListener;
	
	String userId = "";
	String checkRW = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lv_test_bigin);
		
		
		lvTextWord = getSharedPreferences("lvTextWord",0);
		lvTextWordEdit = lvTextWord.edit();
		
		rgInfo = getSharedPreferences("rgInfo",0);
		rgInfoEdit = rgInfo.edit();
		
		userId = rgInfo.getString("mem_id", "NO");
		Log.d("-----------mem_id  info-------------- ", rgInfo.getString("mem_id", "NO"));

		introView = (RelativeLayout)findViewById(R.id.lvtestbigin_id_introView);

		numberView = (ImageView)findViewById(R.id.lvtextbigin_id_view_number);

		enWord = (TextView)findViewById(R.id.lvtextbigin_id_enword);
		select1 = (Button)findViewById(R.id.lvtestbigin_id_select1);
		select2 = (Button)findViewById(R.id.lvtestbigin_id_select2);
		select3 = (Button)findViewById(R.id.lvtestbigin_id_select3);
		select4 = (Button)findViewById(R.id.lvtestbigin_id_select4);

		select1.setOnClickListener(new ButtonListener());
		select2.setOnClickListener(new ButtonListener());
		select3.setOnClickListener(new ButtonListener());
		select4.setOnClickListener(new ButtonListener());
		select1.setClickable(false);
		select2.setClickable(false);
		select3.setClickable(false);
		select4.setClickable(false);

		new GetWord().execute("http://todpop.co.kr/api/studies/get_level_test_words.json?step=1");


		ImageView textReady = (ImageView) findViewById(R.id.lvtest_2_text_ready);
		textReady.setBackgroundResource(R.drawable.lvtest_begin_drawable_readygo);
		AnimationDrawable textReadyAni = (AnimationDrawable) textReady.getBackground();
		textReadyAni.start();

		ImageView imageTestStart = (ImageView) findViewById(R.id.lvtest_2_image_teststart);
		imageTestStart.setBackgroundResource(R.drawable.lvtest_begin_drawable_startani);
		AnimationDrawable imageTestStartAni = (AnimationDrawable) imageTestStart.getBackground();
		imageTestStartAni.start();	

		ImageView imageTime = (ImageView) findViewById(R.id.lv_test_image_time);
		imageTime.setBackgroundResource(R.drawable.lvtest_begin_drawable_time_img);
		imageTimeAni = (AnimationDrawable) imageTime.getBackground();


		imageTimeBlind = (ImageView)findViewById(R.id.lv_test_image_timeblind);
		//density = getResources().getDisplayMetrics().density;
		//imageTimeBlindAni = new TranslateAnimation(0, 360*density,0, 0); 
		imageTimeBlindAni = new TranslateAnimation(
				TranslateAnimation.RELATIVE_TO_PARENT, 0.0f,
				TranslateAnimation.RELATIVE_TO_PARENT, 1.0f,
				TranslateAnimation.RELATIVE_TO_PARENT, 0.0f,
				TranslateAnimation.RELATIVE_TO_PARENT, 0.0f
				);

		imageTimeBlindAni.setDuration(10000);
		imageTimeBlindAni.setRepeatCount(0);

		endView = (RelativeLayout)findViewById(R.id.lvtest_7_view);

		ImageView imageTestend = (ImageView) findViewById(R.id.lvtest_7_image_testend);
		imageTestend.setBackgroundResource(R.drawable.lvtest_begin_drawable_finish_ani);
		imageTestendAni = (AnimationDrawable) imageTestend.getBackground();


		MyAnimationListener = new Animation.AnimationListener() {
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub			
			}		
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub			
			}		
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub	
				count++;

				imageTimeAni.stop();
				imageTimeBlind.setAnimation(null);
				//imageTimeBlindAni = new TranslateAnimation(0, 360*density,0, 0);  
				imageTimeBlindAni = new TranslateAnimation(
						TranslateAnimation.RELATIVE_TO_PARENT, 0.0f,
						TranslateAnimation.RELATIVE_TO_PARENT, 1.0f,
						TranslateAnimation.RELATIVE_TO_PARENT, 0.0f,
						TranslateAnimation.RELATIVE_TO_PARENT, 0.0f
						);
				imageTimeBlindAni.setDuration(10000);
				imageTimeBlindAni.setRepeatCount(0);

				lvTextWordEdit.putString("enWord"+(count-2), enWord.getText().toString());
				lvTextWordEdit.putString("krWord"+(count-2), select1.getText().toString());
				lvTextWordEdit.putString("check"+(count-2), "N");
				lvTextWordEdit.commit();
				checkRW = "x";

				if(count == 21)
				{
					Log.d("finel level: ", level);
					new GetWord().execute("http://todpop.co.kr/api/studies/get_level_test_words.json?user_id="+userId+"&step="+21+"&level="+level+"&ox="+checkRW);
					endView.setVisibility(View.VISIBLE);
					imageTestendAni.start();
//					Handler goNextHandler = new Handler();
//					goNextHandler.postDelayed(GoNextHandler, 1000);
				}else{
					new GetWord().execute("http://todpop.co.kr/api/studies/get_level_test_words.json?step="+count+"&level="+level+"&ox="+checkRW);
				}

			}
		};
		imageTimeBlindAni.setAnimationListener(MyAnimationListener);

		Handler mHandler = new Handler();
		mHandler.postDelayed(mLaunchTaskMain, 4000);
	}

	private class ButtonListener implements OnClickListener{
		public void onClick(View v)
		{
			
			count++;
			select1.setClickable(false);
			select2.setClickable(false);
			select3.setClickable(false);
			select4.setClickable(false);
			
			switch(v.getId())
			{
				case R.id.lvtestbigin_id_select1:
					lvTextWordEdit.putString("enWord"+(count-2), enWord.getText().toString());
					lvTextWordEdit.putString("krWord"+(count-2), select1.getText().toString());
					lvTextWordEdit.putString("check"+(count-2), "Y");
					lvTextWordEdit.commit();			
        			
        			checkRW = "o";
					
				break;
				
				case R.id.lvtestbigin_id_select2:
				case R.id.lvtestbigin_id_select3:
				case R.id.lvtestbigin_id_select4:
					
					lvTextWordEdit.putString("enWord"+(count-2), enWord.getText().toString());
					lvTextWordEdit.putString("krWord"+(count-2), select1.getText().toString());
					lvTextWordEdit.putString("check"+(count-2), "N");
					lvTextWordEdit.commit();
        			checkRW = "x";
				break;
				

			}
			if(count == 21)
			{
				Log.d("final level: ---------------- ", level);
				new GetWord().execute("http://todpop.co.kr/api/studies/get_level_test_words.json?user_id="+userId+"&step="+21+"&level="+level+"&ox="+checkRW);
				endView.setVisibility(View.VISIBLE);
				imageTestendAni.start();
//				Handler goNextHandler = new Handler();
//				goNextHandler.postDelayed(GoNextHandler, 1000);
			}else{
				new GetWord().execute("http://todpop.co.kr/api/studies/get_level_test_words.json?step="+count+"&level="+level+"&ox="+checkRW);
			}
			
			imageTimeAni.stop();
			imageTimeBlind.setAnimation(null);
			//imageTimeBlindAni = new TranslateAnimation(0, 360*density,0, 0); 
			imageTimeBlindAni = new TranslateAnimation(
					TranslateAnimation.RELATIVE_TO_PARENT, 0.0f,
					TranslateAnimation.RELATIVE_TO_PARENT, 1.0f,
					TranslateAnimation.RELATIVE_TO_PARENT, 0.0f,
					TranslateAnimation.RELATIVE_TO_PARENT, 0.0f
					);
			imageTimeBlindAni.setDuration(10000);
			imageTimeBlindAni.setRepeatCount(0);

		}
	}
	
	
	private class GetWord extends AsyncTask<String, Void, JSONObject> {
		
		
        @Override
        protected JSONObject doInBackground(String... urls) 
        {
              
        	JSONObject result = null;
			try
			{
				DefaultHttpClient httpClient = new DefaultHttpClient();
				String getURL = urls[0];
				HttpGet httpGet = new HttpGet(getURL);
				HttpResponse httpResponse = httpClient.execute(httpGet);
				HttpEntity resEntity = httpResponse.getEntity();
				
				if (resEntity != null)
				{    
					result = new JSONObject(EntityUtils.toString(resEntity)); 
					Log.d("RESPONSE ---- ", result.toString());				        	
				}
				return result;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			return result;
		
        }
	
        @Override
        protected void onPostExecute(JSONObject result) 
        {
        	
        	if	(result != null)
        	{
        		try {
        			
        			JSONArray jsonArray = result.getJSONObject("data").getJSONArray("wrong");
					List<String> list = new ArrayList<String>();
					for(int i=0;i<jsonArray.length();i++)
					{
						 list.add( jsonArray.getString(i) );
						 Log.d("123111111111111111 ---- ", jsonArray.getString(i));
					}	
        			
        			
        			enWord.setText(result.getJSONObject("data").getString("word"));
        			select1.setText(result.getJSONObject("data").getString("mean"));
        			level = result.getJSONObject("data").getString("level");
        			
        			select2.setText(list.get(0));
        			select3.setText(list.get(1));
        			select4.setText(list.get(2));
        			
        			
        			select1.setClickable(true);
        			select2.setClickable(true);
        			select3.setClickable(true);
        			select4.setClickable(true);
        			
        			if(count<21)
        			{
        				imageTimeAni.start();
        				imageTimeBlind.setAnimation(imageTimeBlindAni); 
        				imageTimeBlindAni.setAnimationListener(MyAnimationListener);
        				switch(count)
        				{
        					case 2:
        						numberView.setImageResource(R.drawable.lvtest_3_image_number2);					
        					break;
        					case 3:
        						numberView.setImageResource(R.drawable.lvtest_3_image_number3);					
        					break;
        					case 4:
        						numberView.setImageResource(R.drawable.lvtest_3_image_number4);					
        					break;
        					case 5:
        						numberView.setImageResource(R.drawable.lvtest_3_image_number5);					
        					break;
        					case 6:
        						numberView.setImageResource(R.drawable.lvtest_3_image_number6);					
        					break;
        					case 7:
        						numberView.setImageResource(R.drawable.lvtest_3_image_number7);					
        					break;
        					case 8:
        						numberView.setImageResource(R.drawable.lvtest_3_image_number8);					
        					break;
        					case 9:
        						numberView.setImageResource(R.drawable.lvtest_3_image_number9);					
        					break;
        					case 10:
        						numberView.setImageResource(R.drawable.lvtest_3_image_number10);					
        					break;
        					case 11:
        						numberView.setImageResource(R.drawable.lvtest_3_image_number11);					
        					break;
        					case 12:
        						numberView.setImageResource(R.drawable.lvtest_3_image_number12);					
        					break;
        					case 13:
        						numberView.setImageResource(R.drawable.lvtest_3_image_number13);					
        					break;
        					case 14:
        						numberView.setImageResource(R.drawable.lvtest_3_image_number14);					
        					break;
        					case 15:
        						numberView.setImageResource(R.drawable.lvtest_3_image_number15);					
        					break;
        					case 16:
        						numberView.setImageResource(R.drawable.lvtest_3_image_number16);					
        					break;
        					case 17:
        						numberView.setImageResource(R.drawable.lvtest_3_image_number17);					
        					break;
        					case 18:
        						numberView.setImageResource(R.drawable.lvtest_3_image_number18);					
        					break;
        					case 19:
        						numberView.setImageResource(R.drawable.lvtest_3_image_number19);					
        					break;
        					case 20:
        						numberView.setImageResource(R.drawable.lvtest_3_image_number20);					
        					break;
        				

        				}
        			}else{
        				Log.d("------------------------- ", "finish_________________");
        				
        				imageTestendAni.stop();
        				rgInfoEdit.putString("level", level);
        				rgInfoEdit.commit();

        				Intent intent = new Intent(getApplicationContext(), LvTestFinish.class);
        				startActivity(intent);
        				finish();
        			}
        			
        		} catch (Exception e) {
        			
        		}
        	}
        }
	}
	
	private Runnable mLaunchTaskMain = new Runnable() {
		public void run() {
			introView.setVisibility(View.GONE);

			imageTimeAni.start();
			imageTimeBlind.setAnimation(imageTimeBlindAni); 
			//image_timeblindAni.start();
	    }
	};
//	private Runnable GoNextHandler = new Runnable() {
//		public void run() {
//			imageTestendAni.stop();
//			SharedPreferences pref = getSharedPreferences("levelTest",0);
//			SharedPreferences.Editor edit = pref.edit();
//			edit.putString("level", level);
//			edit.commit();
//
//			Intent intent = new Intent(getApplicationContext(), LvTestFinish.class);
//			startActivity(intent);
//			finish();
//	    }
//	};
    @Override
    protected void onStart() {
        super.onStart();
    }
	
	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public void onStop() {
		super.onStop();
	}
	
	@Override
    public void onPause() {
       super.onPause();  
    }
	
    @Override
    protected void onRestart() {
        super.onRestart();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    
    
    public class JSONParser {
    	 
        InputStream is = null;
        JSONObject jObj = null;
        String json = "";
     
        // constructor
        public JSONParser() {
     
        }
     
        public JSONObject getJSONFromUrl(String url) {
     
            // Making HTTP request
            try {
                // defaultHttpClient
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url);
     
                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();           
     
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
             
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                json = sb.toString();
            } catch (Exception e) {
                Log.e("Buffer Error", "Error converting result " + e.toString());
            }
     
            // try parse the string to a JSON object
            try {
                jObj = new JSONObject(json);
            } catch (JSONException e) {
                Log.e("JSON Parser", "Error parsing data " + e.toString());
            }
     
            // return JSON String
            return jObj;
     
        }
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) 
		{
			final AlertDialog.Builder isExit = new AlertDialog.Builder(this);

			DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() 
			{
				public void onClick(DialogInterface dialog, int which) 
				{
					switch (which) 
					{
					case AlertDialog.BUTTON_POSITIVE:
						SharedPreferences settings = getSharedPreferences("setting", 0);
						SharedPreferences.Editor editor = settings.edit();
						editor.putString("check","YES");
						editor.commit();
						
						Intent intent = new Intent();
				        intent.setClass(LvTestBigin.this, MainActivity.class);    
				        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
				         startActivity(intent);
						finish();				
						break;
					case AlertDialog.BUTTON_NEGATIVE:
						break;
					default:
						break;
					}
				}
			};

			isExit.setTitle(getResources().getString(R.string.register_alert_title));
			isExit.setMessage(getResources().getString(R.string.register_alert_text));
			isExit.setPositiveButton("OK", listener);
			isExit.setNegativeButton("Cancel", listener);
			isExit.show();

			return false;
		}
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.lv_test_bigin, menu);
		return true;
	}

}
