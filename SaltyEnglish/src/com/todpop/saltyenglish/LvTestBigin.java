package com.todpop.saltyenglish;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.flurry.android.FlurryAgent;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.animation.Animator.AnimatorListener;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
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
	ImageView imageTime ;
	
	ObjectAnimator imageTimeBlindAni;
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
	int correct = 0;
	String level=null;
	int density;
	static int correctOption;

	SharedPreferences rgInfo;
	SharedPreferences.Editor rgInfoEdit;
	
	SharedPreferences lvTextWord;
	SharedPreferences.Editor lvTextWordEdit;
	
	TranslateAnimation.AnimationListener MyAnimationListener;
	
	String userId = "";
	String checkRW = "";
	
	boolean checkAni = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lv_test_bigin);

		FlurryAgent.logEvent("Level Test Start");
		
		density = (int) getResources().getDisplayMetrics().density;
		
		lvTextWord = getSharedPreferences("lvTextWord",0);
		lvTextWordEdit = lvTextWord.edit();
		
		rgInfo = getSharedPreferences("rgInfo",0);
		rgInfoEdit = rgInfo.edit();
		
		userId = rgInfo.getString("mem_id", "NO");
		Log.d("-----------mem_id  info-------------- ", rgInfo.getString("mem_id", "NO"));

		introView = (RelativeLayout)findViewById(R.id.lvtestbigin_id_introView);

		numberView = (ImageView)findViewById(R.id.lvtextbigin_id_view_number);
		//test word
		enWord = (TextView)findViewById(R.id.lvtextbigin_id_enword);
		
		//select word button
		select1 = (Button)findViewById(R.id.lvtestbigin_id_select1);
		select2 = (Button)findViewById(R.id.lvtestbigin_id_select2);
		select3 = (Button)findViewById(R.id.lvtestbigin_id_select3);
		select4 = (Button)findViewById(R.id.lvtestbigin_id_select4);
		select1.setOnClickListener(new ButtonListener());
		select2.setOnClickListener(new ButtonListener());
		select3.setOnClickListener(new ButtonListener());
		select4.setOnClickListener(new ButtonListener());
		
		// For correct answer comparison 
		select1.setTag(1);
		select2.setTag(2);
		select3.setTag(3);
		select4.setTag(4);
		
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

		imageTime = (ImageView) findViewById(R.id.lv_test_image_time);
		imageTime.setBackgroundResource(R.drawable.lvtest_begin_drawable_time_img);
		imageTimeAni = (AnimationDrawable) imageTime.getBackground();


		imageTimeBlind = (ImageView)findViewById(R.id.lv_test_image_timeblind);
		
		imageTimeBlindAni = ObjectAnimator.ofFloat(imageTimeBlind, "x",-720/2*density, 0/2*density);		
		imageTimeBlindAni.setDuration(10000);
		imageTimeBlindAni.addListener(mAnimationListener);

		endView = (RelativeLayout)findViewById(R.id.lvtest_7_view);

		ImageView imageTestend = (ImageView) findViewById(R.id.lvtest_7_image_testend);
		imageTestend.setBackgroundResource(R.drawable.lvtest_begin_drawable_finish_ani);
		imageTestendAni = (AnimationDrawable) imageTestend.getBackground();


		
		Handler mHandler = new Handler();
		mHandler.postDelayed(mLaunchTaskMain, 4000);
	}
	
	private  AnimatorListener mAnimationListener = new AnimatorListenerAdapter() {
		public void onAnimationEnd(Animator animation) 
		{
			count++;
			imageTimeAni.stop();
			imageTimeBlindAni.start();

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
//				Handler goNextHandler = new Handler();
//				goNextHandler.postDelayed(GoNextHandler, 1000);
			}else{
				new GetWord().execute("http://todpop.co.kr/api/studies/get_level_test_words.json?step="+count+"&level="+level+"&ox="+checkRW);
			}

	
		}
		public void onAnimationCancel(Animator animation) {}
		public void onAnimationRepeat(Animator animation) {}
		public void onAnimationStart(Animator animation) {}
	};

	private class ButtonListener implements OnClickListener{
		public void onClick(View v)
		{
			
			count++;
			select1.setClickable(false);
			select2.setClickable(false);
			select3.setClickable(false);
			select4.setClickable(false);
			
			int buttonTag = (Integer)v.getTag();
			if(buttonTag == correctOption){

				lvTextWordEdit.putString("enWord"+(count-2), enWord.getText().toString());
				lvTextWordEdit.putString("krWord"+(count-2), select1.getText().toString());
				lvTextWordEdit.putString("check"+(count-2), "Y");
				lvTextWordEdit.commit();			
    			
				correct++;
    			checkRW = "o";
			}
			else{

				lvTextWordEdit.putString("enWord"+(count-2), enWord.getText().toString());
				lvTextWordEdit.putString("krWord"+(count-2), select1.getText().toString());
				lvTextWordEdit.putString("check"+(count-2), "N");
				lvTextWordEdit.commit();
    			checkRW = "x";
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
	
			imageTimeBlindAni.start();

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

        			int ran = (int)(Math.random() * 4);
        			Log.d("ran number ------ ", Integer.toString(ran));
        			
        			if (ran == 0) {
        				select1.setText(result.getJSONObject("data").getString("mean"));
        				select2.setText(list.get(0));
        				select3.setText(list.get(1));
        				select4.setText(list.get(2));
        				correctOption = 1;
        			} else if (ran == 1) {
        				select1.setText(list.get(0));
        				select2.setText(result.getJSONObject("data").getString("mean"));
        				select3.setText(list.get(1));
        				select4.setText(list.get(2));
        				correctOption = 2;
        			} else if (ran == 2) {
        				select1.setText(list.get(0));
        				select2.setText(list.get(1));
        				select3.setText(result.getJSONObject("data").getString("mean"));
        				select4.setText(list.get(2));
        				correctOption = 3;
        			} else if (ran == 3) {
        				select1.setText(list.get(0));
        				select2.setText(list.get(1));
        				select3.setText(list.get(2));
        				select4.setText(result.getJSONObject("data").getString("mean"));
        				correctOption = 4;
        			}
        			
        			
        			select1.setClickable(true);
        			select2.setClickable(true);
        			select3.setClickable(true);
        			select4.setClickable(true);
        			
        			if(count<21)
        			{
        				
        				if(checkAni == true){
        					imageTimeAni.start();
        				      
            				imageTimeBlindAni.start();
        				}
        				
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
        				
        				SharedPreferences studyInfo = getSharedPreferences("studyInfo",0);
        				SharedPreferences.Editor studyInfoEdit = studyInfo.edit();
        				
        				if(count==21)
        				{
	            			String stageInfo = result.getJSONObject("data").getString("stage_info");
        					studyInfoEdit.putString("myLevel", level);
	            			studyInfoEdit.putString("stageInfo", stageInfo);
	            			studyInfoEdit.commit();
	            			
	            			getApplicationContext().deleteDatabase("EngWord.db");				// my_word_book DB reset
        				}

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
			checkAni =true;
			imageTimeAni.start();

			imageTimeBlindAni.start();
	    }
	};

	
    @Override
    protected void onStart() {
        super.onStart();
        FlurryAgent.onStartSession(this, "ZKWGFP6HKJ33Y69SP5QY");
    }
	
	@Override
	public void onResume() {
		super.onResume();
	    imageTimeBlindAni.start();
	    imageTimeAni.start();
	}
	
	@Override
	public void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}
	
	@Override
    public void onPause() {
       super.onPause();  
       imageTimeBlindAni.cancel();
       imageTestendAni.stop();
       imageTimeAni.stop();
       
    }
	
    @Override
    protected void onRestart() {
        super.onRestart();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Map<String, String> stopParams = new HashMap<String, String>();
        stopParams.put("Stoped Question", String.valueOf(count));
        float score = (float)correct / count * 100;
        stopParams.put("Score", String.format("%.1f", score));
		FlurryAgent.logEvent("Level Test Destroyed", stopParams);
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
