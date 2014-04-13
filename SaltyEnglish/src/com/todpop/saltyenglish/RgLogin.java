package com.todpop.saltyenglish;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.todpop.api.LoadingDialog;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class RgLogin extends Activity {

	// email
	EditText email;
	EditText emailPassword;
	Button loginBtn;

	// declare define popup view
	PopupWindow popupWindow;
	View popupview;
	RelativeLayout relative;
	TextView popupText;

	// loading progress dialog
	LoadingDialog loadingDialog;

	// facebook
	private UiLifecycleHelper uiHelper;
	private String userInfo = null;
	String fbId = null;
	String fbEmail = null;

	SharedPreferences rgInfo;
	SharedPreferences.Editor rgInfoEdit;
	SharedPreferences setting;
	SharedPreferences.Editor settingEdit;
	SharedPreferences studyInfo;
	SharedPreferences.Editor studyInfoEdit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		rgInfo = getSharedPreferences("rgInfo", 0);
		rgInfoEdit = rgInfo.edit();
		setting = getSharedPreferences("setting", 0);
		settingEdit = setting.edit();
		studyInfo = getSharedPreferences("studyInfo", 0);
		studyInfoEdit = studyInfo.edit();

		Log.d("RgLogin", "1");

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rg_login);

		Log.d("RgLogin", "2");

		email = (EditText) findViewById(R.id.rglogin_id_email);
		emailPassword = (EditText) findViewById(R.id.rglogin_id_emailpassword);
		loginBtn = (Button) findViewById(R.id.rglogin_id_loginbtn);

		// popupview
		relative = (RelativeLayout) findViewById(R.id.rglogin_id_main_activity);
		popupview = View.inflate(this, R.layout.popup_view, null);
		popupWindow = new PopupWindow(popupview,
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT, true);
		popupText = (TextView) popupview.findViewById(R.id.popup_id_text);

		// loading dialog
		loadingDialog = new LoadingDialog(this);

		// facebook login btn
		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);

		final LoginButton fb_btn = (LoginButton) findViewById(R.id.rg_facebook_login_btn);
		fb_btn.setReadPermissions(Arrays.asList("user_location",
				"user_birthday", "user_likes", "email"));
		fb_btn.setBackgroundResource(R.drawable.rglogin_drawable_btn_facebook);
	}

	public void emailLogin(View view) // email login button click
	{
		new SignInWithEmailAPI()
				.execute("http://todpop.co.kr/api/users/sign_in.json");

	}

	// --- request class ---
	private class SignInWithEmailAPI extends
			AsyncTask<String, Void, JSONObject> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			loadingDialog.show();
		}

		@Override
		protected JSONObject doInBackground(String... urls) {
			JSONObject json = null;

			try {
				String postURL = urls[0];
				HttpPost post = new HttpPost(postURL);
				HttpParams httpParams = new BasicHttpParams();

				List<NameValuePair> params = new ArrayList<NameValuePair>();

				params.add(new BasicNameValuePair("email", email.getText()
						.toString()));
				params.add(new BasicNameValuePair("password",
						returnSHA512(emailPassword.getText().toString())));

				UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params,
						HTTP.UTF_8);
				post.setEntity(ent);

				HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
				HttpConnectionParams.setSoTimeout(httpParams, 5000);
				DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);

				HttpResponse responsePOST = httpClient.execute(post);
				HttpEntity resEntity = responsePOST.getEntity();

				if (resEntity != null) {
					json = new JSONObject(EntityUtils.toString(resEntity));
					Log.d("login", json.toString());
					return json;
				}
				return json;
			} catch (Exception e) {
				Log.e("STEVEN", e.toString());
				return json;
			}
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			try {
				loadingDialog.dissmiss();
				if (result != null) {
					if (result.getBoolean("status") == true) {

						settingEdit.putString("isLogin", "YES");
						settingEdit.putString("loginType", "email");
						settingEdit.apply();
						Log.i("STEVEN RGLONGIN 172",
								result.getJSONObject("data")
										.getJSONObject("user")
										.getString("level_test"));

						// email, facebook skip (not needed)
						rgInfoEdit.putString(
								"mem_id",
								result.getJSONObject("data")
										.getJSONObject("user").getString("id"));
						rgInfoEdit.putString(
								"nickname",
								result.getJSONObject("data")
										.getJSONObject("user")
										.getString("nickname"));
						rgInfoEdit.putString(
								"mobile",
								result.getJSONObject("data")
										.getJSONObject("user")
										.getString("mobile"));
						rgInfoEdit.putString(
								"level",
								result.getJSONObject("data")
										.getJSONObject("user")
										.getString("level_test"));
						rgInfoEdit.putString(
								"password",
								result.getJSONObject("data")
										.getJSONObject("user")
										.getString("is_set_facebook_password"));
						rgInfoEdit.apply();

						new GetStageInfoAPI()
								.execute("http://todpop.co.kr/api/studies/get_stage_info.json?user_id="
										+ rgInfo.getString("mem_id", null));
					} else {
						popupText.setText(R.string.popup_emailogin_error);
						popupWindow.showAtLocation(relative, Gravity.CENTER, 0,
								0);
						popupWindow.showAsDropDown(loginBtn);
					}
				} else {
					popupText.setText(R.string.popup_common_timeout);
					popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
				}
			} catch (Exception e) {
			}

		}
	}

	public String returnSHA512(String password) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-512");
		md.update(password.getBytes());
		byte byteData[] = md.digest();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < byteData.length; i++) {
			sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16)
					.substring(1));
		}

		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < byteData.length; i++) {
			String hex = Integer.toHexString(0xff & byteData[i]);
			if (hex.length() == 1) {
				hexString.append('0');
			}
			hexString.append(hex);
		}
		// System.out.println("SHA512: " + hexString.toString());
		return hexString.toString();
	}

	// -------------- get stage info
	// ---------------------------------------------------------------

	private class GetStageInfoAPI extends AsyncTask<String, Void, JSONObject> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			loadingDialog.show();
		}

		@Override
		protected JSONObject doInBackground(String... urls) {
			Log.d("R L", "222");

			JSONObject result = null;
			try {
				DefaultHttpClient httpClient;
				String getURL = urls[0];
				HttpGet httpGet = new HttpGet(getURL);

				HttpParams httpParameters = new BasicHttpParams();

				int timeoutConnection = 3000;
				HttpConnectionParams.setConnectionTimeout(httpParameters,
						timeoutConnection);
				int timeoutSocket = 3000;
				HttpConnectionParams
						.setSoTimeout(httpParameters, timeoutSocket);

				httpClient = new DefaultHttpClient(httpParameters);
				HttpResponse httpResponse = httpClient.execute(httpGet);
				HttpEntity resEntity = httpResponse.getEntity();

				Log.d("R L", "233");

				if (resEntity != null) {
					result = new JSONObject(EntityUtils.toString(resEntity));
					return result;
				}

				return result;
			} catch (Exception e) {
				Log.e("STEVEN", e.toString());
				return result;
			}
		}

		@Override
		protected void onPostExecute(JSONObject json) {
			try {
				loadingDialog.dissmiss();
				Log.d("R L", "255");
				if (json != null) {
					if (json.getBoolean("status")) {
						String stage_info = json.getJSONObject("data")
								.getString("stage");
						studyInfoEdit.putString("stageInfo", stage_info);
						studyInfoEdit.apply();

						if (rgInfo.getString("level", "0").equals("0")) {
							Intent intent = new Intent(getApplicationContext(),
									LvTestBigin.class);
							startActivity(intent);
							finish();
						} else {
							Intent intent = new Intent(getApplicationContext(),
									StudyHome.class);
							startActivity(intent);
							finish();
						}
					} else {
						Log.d("R L", "268");
					}
				} else {
					popupText.setText(R.string.popup_common_timeout);
					popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// ----------------------------------------------------------------------------------------------------------------

	// ----button onClick----

	public void onClickBack(View view) {

		finish();
	}

	public void closePopup(View v) {
		popupWindow.dismiss();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.rg_login, menu);
		return true;
	}

	// Facebook delegates

	// Get User Info
	private String buildUserInfoDisplay(GraphUser user) {
		StringBuilder userInfo = new StringBuilder("");

		userInfo.append(String.format("ID: %s\n\n", user.getId()));
		fbId = user.getId();

		// Example: typed access (name)
		// - no special permissions required
		userInfo.append(String.format("Name: %s\n\n", user.getName()));

		// Example: typed access (birthday)
		// - requires user_birthday permission
		userInfo.append(String.format("Birthday: %s\n\n", user.getBirthday()));

		// Example: partially typed access, to location field,
		// name key (location)
		// - requires user_location permission
		// userInfo.append(String.format("Location: %s\n\n",
		// user.getLocation().getProperty("name")));

		userInfo.append(String.format("Email: %s\n\n",
				user.getProperty("email")));
		fbEmail = String.format("%s", user.getProperty("email"));

		return userInfo.toString();
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

		if (session.isOpened()) {

			// Request user data and show the results
			Request.newMeRequest(session, new Request.GraphUserCallback() {

				@Override
				public void onCompleted(GraphUser user, Response response) {
					if (user != null) {
						// Display the parsed user info
						userInfo = buildUserInfoDisplay(user);

						// Check if facebook id exist
						if (fbEmail.equals("null")) {
							popupText
									.setText(R.string.rg_register_facebook_email_null);
							popupWindow.showAtLocation(relative,
									Gravity.CENTER, 0, 0);
							popupWindow.showAsDropDown(loginBtn);
						} else {
							// try login to SaltyEnglish server with obtained
							// fbEmail
							new SignInWithFacebookAPI()
									.execute("http://todpop.co.kr/api/users/sign_in.json");
						}
					} else {
						Log.e("fbEmail 222---------------------- ", "");
					}
				}
			}).executeAsync();
		}
	}

	private class SignInWithFacebookAPI extends
			AsyncTask<String, Void, JSONObject> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			loadingDialog.show();
		}

		@Override
		protected JSONObject doInBackground(String... urls) {
			JSONObject json = null;

			try {
				String postURL = urls[0];
				HttpPost post = new HttpPost(postURL);
				HttpParams httpParams = new BasicHttpParams();

				List<NameValuePair> params = new ArrayList<NameValuePair>();

				params.add(new BasicNameValuePair("facebook", fbEmail));

				UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params,
						HTTP.UTF_8);
				post.setEntity(ent);

				HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
				HttpConnectionParams.setSoTimeout(httpParams, 5000);
				DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);

				HttpResponse responsePOST = httpClient.execute(post);

				HttpEntity resEntity = responsePOST.getEntity();

				if (resEntity != null) {
					json = new JSONObject(EntityUtils.toString(resEntity));
					Log.d("login", json.toString());
					return json;
				}
				return json;
			} catch (Exception e) {
				Log.e("STEVEN", e.toString());
				return json;
			}
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			try {
				loadingDialog.dissmiss();
				if (result != null) {
					if (result.getBoolean("status") == true) {

						settingEdit.putString("isLogin", "YES");
						settingEdit.putString("loginType", "fb");
						settingEdit.apply();

						// email, facebook skip (not needed)
						rgInfoEdit.putString(
								"mem_id",
								result.getJSONObject("data")
										.getJSONObject("user").getString("id"));
						rgInfoEdit.putString(
								"nickname",
								result.getJSONObject("data")
										.getJSONObject("user")
										.getString("nickname"));
						rgInfoEdit.putString(
								"mobile",
								result.getJSONObject("data")
										.getJSONObject("user")
										.getString("mobile"));
						rgInfoEdit.putString(
								"level",
								result.getJSONObject("data")
										.getJSONObject("user")
										.getString("level_test"));
						rgInfoEdit.putString(
								"password",
								result.getJSONObject("data")
										.getJSONObject("user")
										.getString("is_set_facebook_password"));
						rgInfoEdit.apply();

						new GetStageInfoAPI()
								.execute("http://todpop.co.kr/api/studies/get_stage_info.json?user_id="
										+ rgInfo.getString("mem_id", null));

					} else {

						// Popup facebook login error message (not registered fb
						// ID)
						popupText.setText(R.string.popup_facebooklogin_error);
						popupWindow.showAtLocation(relative, Gravity.CENTER, 0,
								0);
						popupWindow.showAsDropDown(loginBtn);

						Session session = Session.getActiveSession();
						if (session != null) {

							if (!session.isClosed()) {
								session.closeAndClearTokenInformation();
								// clear your preferences if saved
							}
						} else {

							session = new Session(getApplicationContext());
							Session.setActiveSession(session);

							session.closeAndClearTokenInformation();
							// clear your preferences if saved

						}

					}
				} else {
					popupText.setText(R.string.popup_common_timeout);
					popupWindow.showAtLocation(relative, Gravity.CENTER, 0, 0);
				}
			} catch (Exception e) {
			}

		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		com.facebook.AppEventsLogger.activateApp(this, "218233231697811");
		uiHelper.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(this, "ZKWGFP6HKJ33Y69SP5QY");
		FlurryAgent.logEvent("Login");
		EasyTracker.getInstance(this).activityStart(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
		EasyTracker.getInstance(this).activityStop(this);
	}
}
