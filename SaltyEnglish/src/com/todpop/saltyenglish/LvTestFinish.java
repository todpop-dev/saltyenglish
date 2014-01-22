package com.todpop.saltyenglish;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;

import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

public class LvTestFinish extends Activity {

	Button skipBtn;
	ImageView marking;
	ImageView markingDone;
	Button shareBtn;
	
	String reward;
	String point;
	String name;
	String caption;
	String description;
	String link;
	String picture;
	
	SharedPreferences rgInfo;
	private int video_length = 0;
	private VideoView video;
	private int ad_id = -1;
	private int ad_type;
	int view_time = 0;
	private static final List<String> PERMISSIONS = Arrays
			.asList("publish_actions");
	private boolean pendingPublishReauthorization = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_study_test_finish);
		skipBtn = (Button) findViewById(R.id.testfinish_id_skip_btn);
		rgInfo = getSharedPreferences("rgInfo", 0);
		video = (VideoView) findViewById(R.id.test_video_view);
		marking = (ImageView) findViewById(R.id.testfinish_id_marking);
		markingDone = (ImageView) findViewById(R.id.testfinish_id_marking_completed);
		shareBtn = (Button) findViewById(R.id.testfinish_fb_share_btn);
		
		new GetCPDM()
				.execute("http://todpop.co.kr/api/advertises/get_cpdm_ad.json?user_id="
						+ rgInfo.getString("mem_id", "0"));

	}

	private Runnable mLaunchTaskMain = new Runnable() {
		public void run() {
			marking.setVisibility(View.INVISIBLE);
			markingDone.setVisibility(View.VISIBLE);
			skipBtn.setEnabled(true);
			skipBtn.setBackgroundResource(R.drawable.studytestfinish_drawable_btn_skip);
		}
	};

	// --- request class ---
	private class GetCPDM extends AsyncTask<String, Void, JSONObject> {
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
					Log.d("CPDM RESPONSE ---- ", result.toString());
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
					ad_type = json.getJSONObject("data").getInt("ad_type");
					video_length = Integer.parseInt(json.getJSONObject("data")
							.getString("length"));
					ad_id = json.getJSONObject("data").getInt("ad_id");
					video.setVideoPath("http://todpop.co.kr/"
							+ json.getJSONObject("data").getString("url"));
					video.setOnPreparedListener(opl);
					video.start();
					
					if(ad_type == 202){
						shareBtn.setVisibility(View.VISIBLE);
						reward = json.getJSONObject("data").getString("reward");
						point = json.getJSONObject("data").getString("point");
						name = json.getJSONObject("data").getString("name");
						caption = json.getJSONObject("data").getString("caption");
						description = json.getJSONObject("data").getString("description");
						link = json.getJSONObject("data").getString("link");
						picture = json.getJSONObject("data").getString("picture");
					}
					else{
						video.setOnCompletionListener(cl);
					}

					Map<String, String> cpdmParams = new HashMap<String, String>();
					cpdmParams.put("CPDM ID", String.valueOf(ad_id));
					FlurryAgent.logEvent("CPDM", cpdmParams, true);

				} else {
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private class SetCPDMlog extends AsyncTask<String, Void, JSONObject> {

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
					Log.d("SET CPDM LOG RESPONSE ---- ", result.toString());
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
					Intent intent = new Intent(getApplicationContext(),
							LvTestResult.class);
					startActivity(intent);
					LvTestFinish.this.finish();
				} else {
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	private MediaPlayer.OnCompletionListener cl = new MediaPlayer.OnCompletionListener() {

		@Override
		public void onCompletion(MediaPlayer mp) {
			skipBtn.setEnabled(false);
			Log.d("cpdm view_time----", String.valueOf(video_length));
			new SetCPDMlog()
					.execute("http://todpop.co.kr/api/advertises/set_cpdm_log.json?ad_id="
							+ ad_id
							+ "&ad_type="
							+ ad_type
							+ "&user_id="
							+ rgInfo.getString("mem_id", "0")
							+ "&act=1&view_time="
							+ video_length);
		}
	};

	private MediaPlayer.OnPreparedListener opl = new MediaPlayer.OnPreparedListener() {

		@Override
		public void onPrepared(MediaPlayer arg0) {

			Log.e("cpdm----", "ready");

			Handler mHandler = new Handler();
			mHandler.postDelayed(mLaunchTaskMain, 5000); // exact 5000 timing
															// try

		}

	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.study_test_finish, menu);
		return true;
	}

	public void showTestFinishViewCB(View v) {
		v.setEnabled(false);
		
		if(view_time == 0)
			view_time = (int) Math.floor(video.getCurrentPosition() / 1000);
			
		Log.d("cpdm view_time----", "" + view_time);
		FlurryAgent.endTimedEvent("CPDM");
		new SetCPDMlog()
				.execute("http://todpop.co.kr/api/advertises/set_cpdm_log.json?ad_id="
						+ ad_id
						+ "&ad_type="
						+ ad_type
						+ "&user_id="
						+ rgInfo.getString("mem_id", "0")
						+ "&act=1&view_time="
						+ view_time);
	}

	/*
	 * for facebook share
	 */
	public void publishAdBtn(View v) {
		Session session = Session.getActiveSession();
		if (session == null || session.isClosed()) {
			Log.i("STEVEN", "publishAdBtn if");
			view_time = (int) Math.floor(video.getCurrentPosition() / 1000);
			Session.openActiveSession(this, true, callback);
		} else {
			Log.i("STEVEN", "publishAdBtn else");
			publishAd();
		}
	}

	public void publishAd() {
		Session session = Session.getActiveSession();
		List<String> permissions = session.getPermissions();
		if (!isSubsetOf(PERMISSIONS, permissions)) {
			pendingPublishReauthorization = true;
			Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(
					this, PERMISSIONS);
			session.requestNewPublishPermissions(newPermissionsRequest);
			return;
		} else {
			Bundle postParams = new Bundle();
			postParams.putString("link", link);
			if(!name.equals("null"))
				postParams.putString("name", name);
			if(!caption.equals("null"))
				postParams.putString("caption", caption);
			if(!description.equals("null"))
				postParams.putString("description", description);
			if(!picture.equals("null"))
				postParams.putString("picture", picture);

			Request.Callback callback = new Request.Callback() {
				public void onCompleted(Response response) {
					JSONObject graphResponse = response.getGraphObject()
							.getInnerJSONObject();
					String postId = null;
					try {
						postId = graphResponse.getString("id");
					} catch (JSONException e) {
						Log.i("Facebook StudyTestFinish",
								"JSON error " + e.getMessage());
					}
					FacebookRequestError error = response.getError();
					if (error != null) {
						Toast.makeText(getApplicationContext(),
								error.getErrorMessage(), Toast.LENGTH_SHORT)
								.show();
					} else {
						new SetCPDMlog()
						.execute("http://todpop.co.kr/api/advertises/set_cpdm_log.json?ad_id="
								+ ad_id
								+ "&ad_type="
								+ ad_type
								+ "&user_id="
								+ rgInfo.getString("mem_id", "0")
								+ "&act=2&facebook_id="
								+ postId);
					}
				}
			};

			Request request = new Request(Session.getActiveSession(),
					"me/feed", postParams, HttpMethod.POST, callback);

			RequestAsyncTask task = new RequestAsyncTask(request);
			task.execute();
		}
	}

	private boolean isSubsetOf(Collection<String> subset,
			Collection<String> superset) {
		for (String string : subset) {
			if (!superset.contains(string)) {
				return false;
			}
		}
		return true;
	}

	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

	private void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		Log.i("STEVEN", "onSessionStateChange");
		if (state.isOpened()) {
			if(!pendingPublishReauthorization)
				publishAd();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode,
				resultCode, data);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
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