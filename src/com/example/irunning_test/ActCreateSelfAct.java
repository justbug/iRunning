package com.example.irunning_test;

import android.support.v7.app.ActionBarActivity;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.example.irunning_test.R;
import com.example.irunning_test.ActCreateActivity.CreateNewAct;

import android.app.AlertDialog;
//import com.example.irunning_test.ActCreateMember.CreateNewMember;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class ActCreateSelfAct extends ActionBarActivity {
	private static String register_tag = "p_add_post";
	private ProgressDialog pDialog;
	private JSONObject json;
	EditText inputlocation;
	EditText inputpText_content;
	JSONParser jsonParser = new JSONParser();
	private static String url_create_act = "http://140.118.110.188/db_loginfunction/index.php";
	private static final String TAG_SUCCESS = "success";
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_act_self_create);
		//inputTitle = (EditText) findViewById(R.id.editText1);
		//inputlocation = (EditText) findViewById(R.id.editText4);
		inputpText_content = (EditText) findViewById(R.id.pText_content);
		
		//radioGroup = (RadioGroup) findViewById(R.id.sex);// b

		//Spinner spinner_items = (Spinner) findViewById(R.id.Spinner1);// b

	
		
	

		Button btnCreateAct = (Button) findViewById(R.id.button2);
		btnCreateAct.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				// creating new product in background thread
				// Log.d("Create Response", "onclick");
				new CreateNewSelfAct().execute();
			}
		});
		
	}
	
		class CreateNewSelfAct extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(ActCreateSelfAct.this);
			pDialog.setMessage("上傳po文中...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}
		protected String doInBackground(String... args) {
			// Log.d("Create Response", "doInBackground");
			//String title = inputTitle.getText().toString();
			String ptext_content = inputpText_content.getText().toString();
			//String members_count = inputMemberCount.getText().toString();
			//String datetime = inputDatetime.getText().toString();
			//Log.d("Create Response", title);
			// Building Parameters
			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("tag", register_tag));
			//params.add(new BasicNameValuePair("title", title));
			//params.add(new BasicNameValuePair("datetime", datetime));
			params.add(new BasicNameValuePair("created_user", MainActivity.outputAccount));
			
			params.add(new BasicNameValuePair("ptext_content", ptext_content));
			//params.add(new BasicNameValuePair("members_count", members_count));
			//params.add(new BasicNameValuePair("current_member_count", "1"));
			//Log.d("Create Response title ", title);
			// Log.d("Create Response text_content", text_content);
			// Log.d("Create Response members_count ", members_count);
			// getting JSON Object
			// Note that create product url accepts POST method
			json = jsonParser.makeHttpRequest(url_create_act, "POST", params);
			Log.d("Create Response", "json");
			// check log cat fro response
			Log.d("Create Response", json.toString());// b

			// check for success tag

			return null;
		}

		protected void onPostExecute(String file_url) {
			// dismiss the dialog once done
			pDialog.dismiss();
			try {
				// Log.d("Create Response",TAG_SUCCESS);
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					// successfully created product
					AlertDialog.Builder bdr = new AlertDialog.Builder(
							ActCreateSelfAct.this);
					bdr.setMessage("po文成功!\n").setTitle("恭喜");
					bdr.setCancelable(true);
					bdr.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									Intent i = new Intent(
											getApplicationContext(),
											MainActScreen.class);
									startActivity(i);
									// closing this screen
									finish();
								}
							});
					bdr.show();
					// 讓ListView更新資料

				} else {
					// failed to create product

				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
}