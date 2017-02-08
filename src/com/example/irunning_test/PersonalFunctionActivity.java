package com.example.irunning_test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

import com.example.irunning_test.R;
import com.example.irunning_test.MainFunctionActivity.RelfashActBorad;

public class PersonalFunctionActivity extends Fragment {
	private ProgressDialog pDialog;
	private ListView listView;
	private Button btn_reflash;
	private View v;
	private JSONObject json;

	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_ACTIVITY = "self_activity";
	//private static final String TAG_TITLE = "title";
	private static final String TAG_PTEXTCONTENT = "ptext_content";
	private static final String TAG_CREATEDUSER = "created_user";
	//private static final String TAG_MEMBERSCOUNT = "members_count";
	private static final String TAG_PPOSTTIME = "ppost_time";

	JSONParser jParser = new JSONParser();
	ArrayList<HashMap<String, Object>> pactivitysList;
	JSONArray pactivity, title, members_count = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		v = inflater.inflate(R.layout.fragement_personalactivity, container, false);
		getActivity().getActionBar().setTitle("個人po文");
		listView = (ListView) v.findViewById(R.id.listView3);
		btn_reflash = (Button) v.findViewById(R.id.button1);
		pactivitysList = new ArrayList<HashMap<String, Object>>();
		//設定listview點擊事件
//		listView.setOnItemClickListener(new OnItemClickListener() {
//
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				HashMap<String, Object> aAct = pactivitysList.get(position);
//				RunningActivity aRunAct = (RunningActivity) aAct
//						.get("RunningActivity");
//				Intent intent = new Intent(getActivity(), DetailActivity.class);
//				intent.putExtra("RunningActivity", aRunAct);
//				startActivity(intent);
//				// finish();
//
//			}
//		});
		new RelfashActBorad().execute();// 放這裡可以直接顯示
		btn_reflash.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				pactivitysList.clear();
				new RelfashActBorad().execute();
				// Log.d("Create Response", "intent2");
			}
		});

		//
		// // String[] arr = new String[]{
		// // "A","B","C","D","E","F","G"
		// // };
		// ArrayAdapter<String> adapter =
		// new ArrayAdapter<String>(getActivity(),
		// android.R.layout.simple_list_item_1);
		// listView.setAdapter(adapter);
		return v;

	}

	/**
	 * Background Async Task to Load all product by making HTTP Request
	 * */
	class RelfashActBorad extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(getActivity());
			pDialog.setMessage("更新個人po文中...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * getting All products from url
		 * */
		protected String doInBackground(String... args) {
			// Building Parameters
			String url_get_all_post = "http://140.118.110.188/db_loginfunction/getAllPost.php";
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("tag", "getAllPersonalActivity"));
			// getting JSON string from URL
			json = jParser.makeHttpRequest(url_get_all_post, "POST", params);
			JSONObject json = jParser.makeHttpRequest(url_get_all_post, "GET",
					params);

			// Check your log cat for JSON reponse
			Log.d("All Activitys: ", json.toString());

			try {
				// Checking for SUCCESS TAG
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					// products found
					Log.isLoggable("success", success);
					// Getting Array of Activitys
					pactivity = json.getJSONArray(TAG_ACTIVITY);
					// title = json.getJSONArray(TAG_TITLE);
					// members_count = json.getJSONArray(TAG_MEMBERSCOUNT);
					// Log.d("TAG_TITLE ", title.toString());
					// looping through All Activitys
					for (int i = 0; i < pactivity.length(); i++) {
						JSONObject c = pactivity.getJSONObject(i);
						Log.d("JSonObj-c ", c.toString());
						// Storing each json item in variable
						String ptext_content = c.getString(TAG_PTEXTCONTENT);
						String created_user = c.getString(TAG_CREATEDUSER);
						//String members_count = c.getString(TAG_MEMBERSCOUNT);
						String ppost_time = c.getString(TAG_PPOSTTIME);

						//RunningActivity runact = new RunningActivity(c);
						//Log.d("TAG_TITLE ", title);

						// creating new HashMap
						HashMap<String, Object> map = new HashMap<String, Object>();

						// adding each child node to HashMap key => value
						map.put(TAG_PTEXTCONTENT, ptext_content);
						map.put(TAG_CREATEDUSER, created_user);
						// map.put(TAG_TEXTCONTENT, text_content);
						//map.put(TAG_MEMBERSCOUNT, members_count);
						map.put(TAG_PPOSTTIME, ppost_time);
						//map.put("RunningActivity", runact);

						// adding HashList to ArrayList
						pactivitysList.add(map);
					}
					// Sorting
					List<HashMap<String, Object>> actList = (List<HashMap<String, Object>>) pactivitysList;
					Collections.sort(actList,
							new Comparator<HashMap<String, Object>>() {
								@Override
								public int compare(HashMap<String, Object> lhs,
										HashMap<String, Object> rhs) {
									Date date1 = new Date();
									Date date2 = new Date();
									String datetime;
									SimpleDateFormat dateFormat = new SimpleDateFormat();
									dateFormat
											.applyPattern("yyyy-MM-dd HH:mm:ss");
									try {
										datetime = (String) lhs.get("ppost_time");
										date1 = dateFormat.parse(datetime);
										datetime = (String) rhs.get("ppost_time");
										date2 = dateFormat.parse(datetime);
									} catch (ParseException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									return date1.compareTo(date2);
								}
							});
				} // else {
					// // no products found
					// // Launch Add New product Activity
					// Intent i = new Intent(getApplicationContext(),
					// NewProductActivity.class);
					// // Closing all previous activities
					// i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					// startActivity(i);
					// }
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all products
			pDialog.dismiss();
			// updating UI from Background Thread
			(getActivity()).runOnUiThread(new Runnable() {
				public void run() {
					/**
					 * Updating parsed JSON data into ListView
					 * */

					ListAdapter adapter = new SimpleAdapter(getActivity(),
							pactivitysList, R.layout.list_pactivitys,
							new String[] { TAG_PTEXTCONTENT, TAG_PPOSTTIME, TAG_CREATEDUSER }, new int[] {
									R.id.textPersonal_content, R.id.textPost_time, R.id.textCreated_user});
					// updating listview
					listView.setAdapter(adapter);
				}

			});

		}

	}

}
