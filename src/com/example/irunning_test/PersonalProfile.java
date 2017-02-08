package com.example.irunning_test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.irunning_test.R;
import com.example.irunning_test.MainFunctionActivity.RelfashActBorad;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class PersonalProfile extends Fragment {

	// private String value = "";

	private TextView Username, Usersex, Userlocation;
	private ProgressDialog pDialog;
	private ListView listView;
	private View v;
	private JSONObject json;

	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_MEMBER = "member";
	// private static final String TAG_MEMBER_ID = "member_ID";
	// private static final String TAG_ACCOUNT = "account";
	// private static final String TAG_PASTWORD = "pastword";
	private static final String TAG_NICKNAME = "nickname";
	private static final String TAG_SEX = "sex";
	private static final String TAG_ADDRESS = "address";
	JSONParser jParser = new JSONParser();
	ArrayList<HashMap<String, String>> memberlist;
	JSONArray member, member_ID, nickname, sex, address = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

	}

	// @Override
	// public void onAttach(Activity activity) {
	// super.onAttach(activity);

	// MainActScreen MAS = (MainActScreen)activity;
	// value = MAS.getAppleData();
	// }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		v = inflater.inflate(R.layout.fragement_personalfile, container, false);
		getActivity().getActionBar().setTitle("個人檔案");
		Username = (TextView) v.findViewById(R.id.UserName);
		Usersex = (TextView) v.findViewById(R.id.UserSex);
		Userlocation = (TextView) v.findViewById(R.id.Userlocation);
		memberlist = new ArrayList<HashMap<String, String>>();
		new RelfashMemberBorad().execute();
		// btn_reflash.setOnClickListener(new Button.OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// new RelfashMemberBorad().execute();
		// // Log.d("Create Response", "intent2");
		// }
		// });
		// super.onCreate(savedInstanceState);
		// setContextView(R.layout.fragement_personalfile);
		// findViews();
		// setListeners();

		return v; // inflater.inflate(R.layout.fragement_personalfile,
					// container,false);
	}

	class RelfashMemberBorad extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(getActivity());
			pDialog.setMessage("更新頁面中...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/*
		 * public void onActivityCreated(Bundle savedInstanceState) {
		 * super.onActivityCreated(savedInstanceState); TextView txtResult =
		 * (TextView) this.getView().findViewById(R.id.textView1);
		 * txtResult.setText(value); }
		 */
		protected String doInBackground(String... args) {
			// Building Parameters
			String url_get_all_post = "http://140.118.110.188/db_loginfunction/getAllPost.php";
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("tag", "getPersonalMember"));
			params.add(new BasicNameValuePair("account",
					MainActivity.outputAccount));
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
					member = json.getJSONArray(TAG_MEMBER);
					// title = json.getJSONArray(TAG_TITLE);
					// members_count = json.getJSONArray(TAG_MEMBERSCOUNT);
					// Log.d("TAG_TITLE ", title.toString());
					// looping through All Activitys
					for (int i = 0; i < member.length(); i++) {
						JSONObject c = member.getJSONObject(i);
						Log.d("JSonObj-c ", c.toString());
						// Storing each json item in variable
						// String member_ID = c.getString(TAG_MEMBER_ID);

						String nickname = c.getString(TAG_NICKNAME);
						String sex = c.getString(TAG_SEX);
						String address = c.getString(TAG_ADDRESS);
						// Log.d("TAG_MEMBER_ID ", member_ID);

						// creating new HashMap
						HashMap<String, String> map = new HashMap<String, String>();

						// adding each child node to HashMap key => value
						// map.put(TAG_MEMBER_ID, member_ID);

						map.put(TAG_NICKNAME, nickname);
						map.put(TAG_SEX, sex);
						map.put(TAG_ADDRESS, address);

						// adding HashList to ArrayList
						memberlist.add(map);

					}

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

		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all products
			pDialog.dismiss();
			TextView[] tv = new TextView[3];
			tv[0] = (TextView) v.findViewById(R.id.UserName);
			tv[1] = (TextView) v.findViewById(R.id.UserSex);
			tv[2] = (TextView) v.findViewById(R.id.Userlocation);
			// for(int i=0;i<memberlist.size();i++)
			// {
			// tv[i].setText(memberlist.get(i).toString());
			// }
			for (HashMap<String, String> map : memberlist) {
				Object tagNickName = map.get(TAG_NICKNAME);
				Object tagSex = map.get(TAG_SEX);
				Object tagAddress = map.get(TAG_ADDRESS);
				tv[0].setText(tagNickName.toString());
				tv[1].setText(tagSex.toString());
				tv[2].setText(tagAddress.toString());
			}

			// updating UI from Background Thread

		}
		// private Button button_get_record;
		//
		// private void findViews() {
		// // TODO Auto-generated method stub
		// // button_get_record = (Button)findViewById(R.id.get_record);
		// }
		//
		// private void setListeners() {
		// OnClickListener getDBRecord;
		// // TODO Auto-generated method stub
		// // button_get_record.setOnClickListener(getDBRecord);
		// }
		//
		// private void setContextView(int fragementPersonalfile) {
		// // TODO Auto-generated method stub
		//
		// }
	}
}
