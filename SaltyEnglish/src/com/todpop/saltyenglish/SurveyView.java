package com.todpop.saltyenglish;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;
import com.todpop.api.TypefaceActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class SurveyView extends TypefaceActivity {

	ListView listView;
	ContentItem contentItem;
	ArrayList<ContentItem> listArray;

	ListViewAdapter listViewAdapter;

	static int surveySelectionChecker;
	int surveyCount = 0;
	Button submitBtn = null;
	String submitStr = "";

	HashMap<Integer, String> answerMap;
	HashMap<Integer, View> listViewHolder;

	int adId;
	String rewardAmount;
	String pointAmount;
	
	SharedPreferences cpxInfo;
	SharedPreferences pref;
	String userId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_survey_view);

		cpxInfo = getSharedPreferences("cpxInfo", 0);

		listView = (ListView) findViewById(R.id.survey_id_list_view);
		listView.setFocusableInTouchMode(true);
		listView.setDividerHeight(0);
		listArray = new ArrayList<ContentItem>();

		surveySelectionChecker = 0;
		answerMap = new HashMap<Integer, String>();
		listViewHolder = new HashMap<Integer, View>();

		pref = getSharedPreferences("rgInfo", 0);
		userId = pref.getString("mem_id", "0");
		adId = cpxInfo.getInt("adId", 0);

		new GetInfo()
				.execute("http://todpop.co.kr/api/advertises/get_cps_questions.json?ad_id="
						+ adId);

		rewardAmount = cpxInfo.getString("reward", "0");
		pointAmount = cpxInfo.getString("point", "0");
		
		cpxInfo.edit().clear().apply();
	}

	// on click
	public void onClickBack(View view) {
		finish();
	}

	private class GetInfo extends AsyncTask<String, Void, JSONObject> {
		@Override
		protected JSONObject doInBackground(String... urls) {
			JSONObject result = null;
			try {
				DefaultHttpClient httpClient = new DefaultHttpClient();
				String getURL = urls[0];
				HttpGet httpGet = new HttpGet(getURL);
				HttpResponse httpResponse = httpClient.execute(httpGet);
				HttpEntity resEntity = httpResponse.getEntity();

				if (resEntity != null) {
					result = new JSONObject(EntityUtils.toString(resEntity));
					Log.d("CPX RESPONSE ---- ", result.toString());
				}
				return result;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		}

		@Override
		protected void onPostExecute(JSONObject json) {

			try {
				if (json.getBoolean("status") == true) {
					JSONArray jsonArray = json.getJSONArray("data");
					surveyCount = jsonArray.length() - 1;
					for (int i = 0; i < jsonArray.length(); i++) {
						contentItem = new ContentItem(
								jsonArray.getJSONObject(i).getString("q_no"),
								jsonArray.getJSONObject(i).getString("q_type"),
								jsonArray.getJSONObject(i).getString("q_text"),
								jsonArray.getJSONObject(i).getString("q_image"),
								jsonArray.getJSONObject(i)
										.getString("n_answer"), jsonArray
										.getJSONObject(i).getString("a1"),
								jsonArray.getJSONObject(i).getString("a2"),
								jsonArray.getJSONObject(i).getString("a3"),
								jsonArray.getJSONObject(i).getString("a4"),
								jsonArray.getJSONObject(i).getString("a5"));
						listArray.add(contentItem);

					}

					listViewAdapter = new ListViewAdapter(SurveyView.this,
							listArray);
					listView.setAdapter(listViewAdapter);

				} else {

				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	class ContentItem {
		ContentItem(String aPoint, String aType, String aQuestion,
				String aImage, String aAnswer, String aAnswer1,
				String aAnswer2, String aAnswer3, String aAnswer4,
				String aAnswer5) {
			point = aPoint;
			type = aType;
			question = aQuestion;
			image = aImage;
			answer = aAnswer;

			answer1 = aAnswer1;
			answer2 = aAnswer2;
			answer3 = aAnswer3;
			answer4 = aAnswer4;
			answer5 = aAnswer5;
		}

		String point;
		String type;
		String question;
		String image;
		String answer;

		String answer1;
		String answer2;
		String answer3;
		String answer4;
		String answer5;
	}

	class ListViewAdapter extends BaseAdapter {
		private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
			ImageView surveyImage;

			public DownloadImageTask(ImageView imageView) {
				surveyImage = imageView;
			}

			protected Bitmap doInBackground(String... urls) {
				String urldisplay = urls[0];
				Bitmap mIcon11 = null;
				try {
					InputStream in = new java.net.URL(urldisplay).openStream();
					mIcon11 = BitmapFactory.decodeStream(in);
				} catch (Exception e) {
					Log.e("Error", e.getMessage());
					e.printStackTrace();
				}
				return mIcon11;
			}

			protected void onPostExecute(Bitmap result) {
				// Update UI
				surveyImage.setImageBitmap(result);
			}
		}

		Context maincon;
		LayoutInflater Inflater;
		ArrayList<ContentItem> arSrc;
		int layout0 = R.layout.survey_view_list_item_q_type_0;
		int layout1_2 = R.layout.survey_view_list_item_q_type_1_2;
		int layout3_4 = R.layout.survey_view_list_item_q_type_3_4;
		int layout5 = R.layout.survey_view_list_item_q_type_5;

		public ListViewAdapter(Context context, ArrayList<ContentItem> aarSrc) {
			maincon = context;
			Inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			arSrc = aarSrc;
		}

		public int getCount() {
			return arSrc.size() + 1;
		}

		public String getItem(int position) {
			return arSrc.get(position).point;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {
			if (arSrc.size() == position) {
				if (listViewHolder.get(position) == null) {
					convertView = Inflater.inflate(layout5, parent, false);
					submitBtn = (Button) convertView
							.findViewById(R.id.survye_submit_id_btn);
					submitBtn.setEnabled(false);

					listViewHolder.put(position, convertView);
					return convertView;
				} else {
					return (View) listViewHolder.get(position);
				}
			}

			if (arSrc.get(position).type.equals("0")) {
				if (listViewHolder.get(position) == null) {
					convertView = Inflater.inflate(layout0, parent, false);
					TextView topText = (TextView) convertView
							.findViewById(R.id.survey_id_top_text);
					TextView rewardText = (TextView) convertView
							.findViewById(R.id.surey_id_save);
					ImageView coinOrPoint = (ImageView) convertView
							.findViewById(R.id.survey_id_coin);
					
					setFont(topText);
					setFont(rewardText);
					
					topText.setText(arSrc.get(position).question);
					if(pointAmount.equals("0") || pointAmount.equals("null")){
						rewardText.setText(rewardAmount);
					}
					else{
						coinOrPoint.setImageResource(R.drawable.common_image_smallpoint);
						rewardText.setText(pointAmount);
					}

					listViewHolder.put(position, convertView);
					return convertView;
				} else {
					return (View) listViewHolder.get(position);
				}

			} else if (arSrc.get(position).type.equals("1")
					|| arSrc.get(position).type.equals("2")) {
				if (listViewHolder.get(position) == null) {
					convertView = Inflater.inflate(layout1_2, parent, false);
					TextView point = (TextView) convertView
							.findViewById(R.id.survey_id_point);
					point.setText(arSrc.get(position).point);

					TextView question = (TextView) convertView
							.findViewById(R.id.survey_id_question);
					question.setText(arSrc.get(position).question);

					setFont(point);
					setFont(question);
					
					if (!arSrc.get(position).image.equals("null")) {
						ImageView image = (ImageView) convertView
								.findViewById(R.id.survey_id_image);
						new DownloadImageTask(image)
								.execute("http://todpop.co.kr"
										+ arSrc.get(position).image);
					}

					RadioGroup rg = (RadioGroup) convertView
							.findViewById(R.id.survey_id_radioGrop);
					RadioButton btn1 = (RadioButton) convertView
							.findViewById(R.id.survey_id_btn_1);
					RadioButton btn2 = (RadioButton) convertView
							.findViewById(R.id.survey_id_btn_2);
					RadioButton btn3 = (RadioButton) convertView
							.findViewById(R.id.survey_id_btn_3);
					RadioButton btn4 = (RadioButton) convertView
							.findViewById(R.id.survey_id_btn_4);
					RadioButton btn5 = (RadioButton) convertView
							.findViewById(R.id.survey_id_btn_5);
					
					setFont(btn1);
					setFont(btn2);
					setFont(btn3);
					setFont(btn4);
					setFont(btn5);

					rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
						@Override
						public void onCheckedChanged(RadioGroup group,
								int checkedId) {

							listView.setSelection(position);
							
							String tmpAnswer = "";
							switch (checkedId) {
							case R.id.survey_id_btn_1:
								tmpAnswer = "&ans[]=" + 1;
								Log.i("STEVEN",
										"button 1 selected and fired on checked changed");
								break;
							case R.id.survey_id_btn_2:
								tmpAnswer = "&ans[]=" + 2;
								Log.i("STEVEN",
										"button 2 selected and fired on checked changed");
								break;
							case R.id.survey_id_btn_3:
								tmpAnswer = "&ans[]=" + 3;
								Log.i("STEVEN",
										"button 3 selected and fired on checked changed");
								break;
							case R.id.survey_id_btn_4:
								tmpAnswer = "&ans[]=" + 4;
								Log.i("STEVEN",
										"button 4 selected and fired on checked changed");
								break;
							case R.id.survey_id_btn_5:
								tmpAnswer = "&ans[]=" + 5;
								Log.i("STEVEN",
										"button 5 selected and fired on checked changed");
								break;
							default:
								break;
							}

							if (answerMap.get(position - 1) == null) {
								surveySelectionChecker++;
								Log.d("how many selected? ---- ", Integer
										.toString(surveySelectionChecker));
								if (surveySelectionChecker == surveyCount) {

									listView.setSelection(arSrc.size());
									submitBtn.setEnabled(true);
								}
							}

							answerMap.put(position - 1, tmpAnswer);

						}
					});

					if (!arSrc.get(position).answer1.equals("")) {
						btn1.setText(arSrc.get(position).answer1);
					} else {
						btn1.setVisibility(View.GONE);
					}
					if (!arSrc.get(position).answer2.equals("")) {
						btn2.setText(arSrc.get(position).answer2);
					} else {
						btn2.setVisibility(View.GONE);
					}
					if (!arSrc.get(position).answer3.equals("")) {
						btn3.setText(arSrc.get(position).answer3);
					} else {
						btn3.setVisibility(View.GONE);
					}
					if (!arSrc.get(position).answer4.equals("")) {
						btn4.setText(arSrc.get(position).answer4);
					} else {
						btn4.setVisibility(View.GONE);
					}
					if (!arSrc.get(position).answer5.equals("")) {
						btn5.setText(arSrc.get(position).answer5);
					} else {
						btn5.setVisibility(View.GONE);
					}

					listViewHolder.put(position, convertView);
					return convertView;
				} else {
					return (View) listViewHolder.get(position);
				}

			} else { // Type 3, 4
				if (listViewHolder.get(position) == null) {
					convertView = Inflater.inflate(layout3_4, parent, false);

					TextView point = (TextView) convertView
							.findViewById(R.id.survey_id_point);
					point.setText(arSrc.get(position).point);

					TextView question = (TextView) convertView
							.findViewById(R.id.survey_id_question);
					question.setText(arSrc.get(position).question);
					
					setFont(point);
					setFont(question);

					final EditText et = (EditText) convertView
							.findViewById(R.id.survey_id_edit_text);
					if(arSrc.size() - 1 == position){
						Log.i("SETVEN", "action_done");
						et.setImeOptions(EditorInfo.IME_ACTION_DONE);
					}
					else{
						Log.i("SETVEN", "action_next");
						et.setImeOptions(EditorInfo.IME_ACTION_NEXT);
					}
					et.setOnFocusChangeListener(new OnFocusChangeListener() {

						public void onFocusChange(View v, boolean hasFocus) {

							
							if (!hasFocus) {

								EditText tmpEt = (EditText) v;
								
								String tmpAnswer = "&ans[]="
										+ tmpEt.getText().toString()
												.replace("\n", "%0d").replace(" ", "%20");
								answerMap.put(position - 1, tmpAnswer);

							} else {

								listView.setSelection(position);
								
								if (answerMap.get(position - 1) == null) {
									surveySelectionChecker++;
									Log.d("how many selected? ---- ", Integer
											.toString(surveySelectionChecker));
									if (surveySelectionChecker == surveyCount) {
										submitBtn.setEnabled(true);
									}
								}
							}
						}
					});
					et.setOnEditorActionListener(new OnEditorActionListener(){
						@Override
						public boolean onEditorAction(TextView v, int actionId, KeyEvent event){
							if(actionId == EditorInfo.IME_ACTION_DONE){
								et.clearFocus();
							}
							return false;
						}
					});

					if (!arSrc.get(position).image.equals("null")) {
						ImageView image = (ImageView) convertView
								.findViewById(R.id.survey_id_image);
						new DownloadImageTask(image)
								.execute("http://todpop.co.kr"
										+ arSrc.get(position).image);
					}

					RadioGroup rg = (RadioGroup) convertView
							.findViewById(R.id.survey_id_radioGrop);
					RadioButton btn1 = (RadioButton) convertView
							.findViewById(R.id.survey_id_btn_1);
					RadioButton btn2 = (RadioButton) convertView
							.findViewById(R.id.survey_id_btn_2);
					RadioButton btn3 = (RadioButton) convertView
							.findViewById(R.id.survey_id_btn_3);
					RadioButton btn4 = (RadioButton) convertView
							.findViewById(R.id.survey_id_btn_4);
					RadioButton btn5 = (RadioButton) convertView
							.findViewById(R.id.survey_id_btn_5);
					
					setFont(btn1);
					setFont(btn2);
					setFont(btn3);
					setFont(btn4);
					setFont(btn5);

					if (!arSrc.get(position).answer1.equals("")) {
						btn1.setText(arSrc.get(position).answer1);
					} else {
						btn1.setVisibility(View.GONE);
					}
					if (!arSrc.get(position).answer2.equals("")) {
						btn2.setText(arSrc.get(position).answer2);
					} else {
						btn2.setVisibility(View.GONE);
					}
					if (!arSrc.get(position).answer3.equals("")) {
						btn3.setText(arSrc.get(position).answer3);
					} else {
						btn3.setVisibility(View.GONE);
					}
					if (!arSrc.get(position).answer4.equals("")) {
						btn4.setText(arSrc.get(position).answer4);
					} else {
						btn4.setVisibility(View.GONE);
					}
					if (!arSrc.get(position).answer5.equals("")) {
						btn5.setText(arSrc.get(position).answer5);
					} else {
						btn5.setVisibility(View.GONE);
					}

					listViewHolder.put(position, convertView);
					return convertView;
				} else {
					return listViewHolder.get(position);
				}
			}
		}
	}

	public void submitSurvey(View v) {
		submitStr = "http://todpop.co.kr/api/advertises/set_survey_result.json?ad_id="
				+ adId + "&user_id=" + userId;
		for (int i = 0; i < surveyCount; i++) {
			submitStr += answerMap.get(i);
		}
		Log.d("CPS URL: ----- ", submitStr);

		new SubmitCPS().execute(submitStr);
	}

	private class SubmitCPS extends AsyncTask<String, Void, JSONObject> {
		@Override
		protected JSONObject doInBackground(String... urls) {
			JSONObject result = null;
			try {
				DefaultHttpClient httpClient = new DefaultHttpClient();
				String getURL = urls[0];
				HttpGet httpGet = new HttpGet(getURL);
				HttpResponse httpResponse = httpClient.execute(httpGet);
				HttpEntity resEntity = httpResponse.getEntity();

				if (resEntity != null) {
					result = new JSONObject(EntityUtils.toString(resEntity));
					Log.d("CPX RESPONSE ---- ", result.toString());
				}
				return result;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		}

		@Override
		protected void onPostExecute(JSONObject json) {
			try {
				if (json.getBoolean("status") == true) {
					// Send Result OK! Send act=3 to server
					SharedPreferences pref = getSharedPreferences("rgInfo", 0);
					String userId = pref.getString("mem_id", "0");
					new SendCPXLog()
							.execute("http://todpop.co.kr/api/advertises/set_cpx_log.json?ad_id="
									+ adId
									+ "&ad_type="
									+ "305"
									+ "&user_id="
									+ userId + "&act=3");

				} else {
					Log.d("submit cps error: ", json.toString());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	private class SendCPXLog extends AsyncTask<String, Void, JSONObject> {
		@Override
		protected JSONObject doInBackground(String... urls) {
			JSONObject result = null;
			try {
				DefaultHttpClient httpClient = new DefaultHttpClient();
				String getURL = urls[0];
				HttpGet httpGet = new HttpGet(getURL);
				HttpResponse httpResponse = httpClient.execute(httpGet);
				HttpEntity resEntity = httpResponse.getEntity();

				if (resEntity != null) {
					result = new JSONObject(EntityUtils.toString(resEntity));
					Log.d("RESPONSE ---- ", result.toString());
				}
				return result;
			} catch (Exception e) {
				e.printStackTrace();
			}

			return result;
		}

		@Override
		protected void onPostExecute(JSONObject result) {

			try {
				if (result.getBoolean("status") == true) {
					Log.d("CPX LOG:  ---- ", "Send CPX act=3 Log OK!");
					Intent intent = new Intent(getApplicationContext(),
							HomeDownload.class);

					startActivity(intent);
					finish();
				} else {
					Log.d("CPX LOG:  ---- ", "Send CPX act=3 Log Failed!");
					Intent intent = new Intent(getApplicationContext(),
							HomeDownload.class);
					startActivity(intent);
					finish();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(this, "ZKWGFP6HKJ33Y69SP5QY");
	    EasyTracker.getInstance(this).activityStart(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	    EasyTracker.getInstance(this).activityStop(this);
	}

}
