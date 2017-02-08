package com.example.irunning_test;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
//import memberlogin.DashboardActivity;
//import memberlogin.DatabaseHandler;
//import memberlogin.LoginActivity;
//import memberlogin.UserFunctions;
//import memberlogin.LoginActivity.LoginMember;
//import com.example.irunning_login.ActivityMember;
//import com.example.irunning_login.MainActivity;
import com.example.irunning_test.R;

public class MainActivity extends ActionBarActivity {
	public static int REQUEST_PATH = 1;
	private Button createMember;
	private ProgressDialog pDialog;
	private Button btnLogin;
	EditText inputAccount;
	public static String outputAccount;
	EditText inputPassword;
	TextView loginErrorMsg;
	// JSON Response node names
	private static String login_tag = "login";
	private static String KEY_SUCCESS = "success";
	private static String KEY_ERROR = "error";
	private static String KEY_ERROR_MSG = "error_msg";
	private static String KEY_UID = "uid";
	// private static String KEY_NAME = "name";
	private static String KEY_ACCOUNT = "account";
	private static String KEY_CREATED_AT = "created_at";
	private JSONObject json;
	private static String loginURL = "http://140.118.110.188/db_loginfunction/index.php";
	JSONParser jsonParser = new JSONParser();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setTitle("Welcome to iRunning");
		inputAccount = (EditText) findViewById(R.id.account);
		inputPassword = (EditText) findViewById(R.id.password);
		createMember = (Button) findViewById(R.id.button2);
		loginErrorMsg = (TextView) findViewById(R.id.login_error);
		// locateSpinner =(Spinner)findViewById(R.id.Spinner1);
		btnLogin = (Button) findViewById(R.id.button1);
		// fb1 = (ImageButton)findViewById(R.id.imageButton1);

		btnLogin.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new LoginMember().execute();
				Log.d("Create Response", "intent2");
			}
		});
		createMember.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.this,
						ActCreateMember.class);
				startActivityForResult(intent, REQUEST_PATH);
				Log.d("Create Response", "intent");
			}
		});
		
	}

	class LoginMember extends AsyncTask<String, String, String> {
		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
 			pDialog = new ProgressDialog(MainActivity.this);
			pDialog.setMessage("Now login...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Creating product
		 **/
		protected String doInBackground(String... args) {
			//Log.d("params", "doInBackground");
			String account = inputAccount.getText().toString();
			String password = inputPassword.getText().toString();
			// Building Parameters
			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("tag", login_tag));
			params.add(new BasicNameValuePair("account", account));
			params.add(new BasicNameValuePair("password", password));
			// JSONObject json = jsonParser.getJSONFromUrl(loginURL, params);
			// return json

			json = jsonParser.makeHttpRequest(loginURL, "POST", params);
			Log.d("JSON", json.toString());
			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog once done
			pDialog.dismiss();
			// Log.d("JSON", "pDialog");
			// check for login response
			try {
				if (json.getString(KEY_SUCCESS) != null) {
					loginErrorMsg.setText("");
					String res = json.getString(KEY_SUCCESS);
					if (Integer.parseInt(res) == 1) {
						// user successfully logged in
						// Store user details in SQLite Database
						outputAccount = inputAccount.getText().toString();
						DatabaseHandler db = new DatabaseHandler(
								getApplicationContext());
						JSONObject json_user = json.getJSONObject("user");

						// Clear all previous data in database
						// userFunction.logoutUser(getApplicationContext());
						db.addUser(json_user.getString(KEY_ACCOUNT),
								json.getString(KEY_UID),
								json_user.getString(KEY_CREATED_AT));

						// Launch Dashboard Screen
						Intent dashboard = new Intent(getApplicationContext(),
								MainActScreen.class);

						// Close all views before launching Dashboard
						dashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(dashboard);

						// Close Login Screen
						finish();
					} else {
						// Error in login
						loginErrorMsg.setText("錯誤的帳號或密碼!");
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 * 
	 * public static class PlaceholderFragment extends Fragment {
	 * 
	 * public PlaceholderFragment() { }
	 * 
	 * @Override public View onCreateView(LayoutInflater inflater, ViewGroup
	 *           container, Bundle savedInstanceState) { View rootView =
	 *           inflater.inflate(R.layout.fragment_main, container, false);
	 *           return rootView; } }
	 */

}
