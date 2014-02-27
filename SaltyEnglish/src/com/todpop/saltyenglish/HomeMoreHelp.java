package com.todpop.saltyenglish;


import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;


import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

public class HomeMoreHelp extends Activity {
	
	//declare define popup view
	PopupWindow popupWindow;
	View popupview;
	LinearLayout mainLayout;
	TextView popupText;
	ExpandableListView expandListView;
	ArrayList<String> titleArray = new ArrayList<String>() ;
	ArrayList<String> itemArray = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_more_help);

		expandListView = (ExpandableListView)findViewById(R.id.homemorehelp_id_listview_item);
		mainLayout = (LinearLayout)findViewById(R.id.activity_home_more_help);
		popupview = View.inflate(this, R.layout.popup_view, null);
		popupWindow = new PopupWindow(popupview, ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT,true);
		popupText = (TextView)popupview.findViewById(R.id.popup_id_text);
		
		new GetHelp().execute("http://todpop.co.kr/api/app_infos/get_helps.json");
	}

	//--- request class ---
	private class GetHelp extends AsyncTask<String, Void, JSONObject> 
	{
		DefaultHttpClient httpClient ;
		@Override
		protected JSONObject doInBackground(String... urls) 
		{
			JSONObject result = null;
			try
			{
				String getURL = urls[0];
				HttpGet httpGet = new HttpGet(getURL); 
				HttpParams httpParameters = new BasicHttpParams(); 
				httpClient = new DefaultHttpClient(httpParameters); 
				HttpResponse response = httpClient.execute(httpGet); 
				HttpEntity resEntity = response.getEntity();


				if (resEntity != null)
				{    
					result = new JSONObject(EntityUtils.toString(resEntity)); 
					Log.d("RESPONSE---- ", result.toString());				        	
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
		protected void onPostExecute(JSONObject json) {
			if(json == null) {
			}

			try {
				if	(json.getBoolean("status")==true)
				{
					if(json.getJSONArray("data").length() > 0)
					{
						JSONArray jsonArray = json.getJSONArray("data");
						for(int i=0;i<jsonArray.length();i++)
						{
							titleArray.add(jsonArray.getJSONObject(i).getString("title"));
							itemArray.add(jsonArray.getJSONObject(i).getString("content"));
						}

						ExpandableListAdapter adapter = new BaseExpandableListAdapter()
						{
							@Override
							public Object getChild(int groupPosition, int childPosition)
							{
								//return arms[groupPosition][childPosition];
								return itemArray.get(groupPosition);
							}
							@Override
							public long getChildId(int groupPosition, int childPosition)
							{
								return childPosition;
							}
							@Override
							public int getChildrenCount(int groupPosition)
							{
								//return arms[groupPosition].length;
								return 1;//itemArray.size();
							}
							private TextView getTextView()
							{
								AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
										ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
								TextView textView = new TextView(HomeMoreHelp.this);
								textView.setLayoutParams(lp);
								textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
								textView.setPadding(70, 20, 0, 20);
								textView.setTextColor(Color.rgb(0, 0, 0));
								textView.setTextSize(19);
								return textView;
							}
							@Override
							public View getChildView(int groupPosition, int childPosition,
									boolean isLastChild, View convertView, ViewGroup parent)
							{
								TextView textView = getTextView();			
								textView.setText(getChild(groupPosition, childPosition).toString());
								return textView;
							}
							@Override
							public Object getGroup(int groupPosition)
							{
								return titleArray.get(groupPosition);
							}
							@Override
							public int getGroupCount()
							{
								return titleArray.size();
							}
							@Override
							public long getGroupId(int groupPosition)
							{
								return groupPosition;
							}
							@Override
							public View getGroupView(int groupPosition, boolean isExpanded,
									View convertView, ViewGroup parent)
							{
								LinearLayout ll = new LinearLayout(HomeMoreHelp.this);
								ll.setOrientation(0);
								TextView textView = getTextView();
								textView.setText(getGroup(groupPosition).toString());				
								ll.addView(textView);			
								return ll;
							}
							@Override
							public boolean isChildSelectable(int groupPosition, int childPosition)
							{
								return true;
							}
							@Override
							public boolean hasStableIds()
							{
								return true;
							}
						};
						expandListView.setAdapter(adapter);

					}else{
						new GetHelp().execute("http://todpop.co.kr/api/app_infos/get_helps.json?page=1");
					}
				}

			} catch (Exception e) {

			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home_more_help, menu);
		return true;
	}
	// on click

	public void onClickBack(View view)
	{
		finish();
	}
	@Override
	protected void onStart()
	{
		super.onStart();
		FlurryAgent.onStartSession(this, "ZKWGFP6HKJ33Y69SP5QY");
		FlurryAgent.logEvent("Help");
	    EasyTracker.getInstance(this).activityStart(this);
	}
	 
	@Override
	protected void onStop()
	{
		super.onStop();		
		FlurryAgent.onEndSession(this);
	    EasyTracker.getInstance(this).activityStop(this);
	}
	public void onClickAsk(View view){
		popupText.setText(getResources().getString(R.string.home_more_help_info));
		popupWindow.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);
	}
	public void closePopup(View v)
	{
		popupWindow.dismiss();
	}
}
