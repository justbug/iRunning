package com.example.irunning_test;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.Toast;
import com.example.irunning_test.R;


public class ActCreateMember<RatioButton> extends ActionBarActivity {
	
	// Progress Dialog
    private ProgressDialog pDialog;
    private JSONObject json;
    private static String register_tag = "register";
    JSONParser jsonParser = new JSONParser();
    EditText inputAccount;
    EditText inputPassword;
    EditText inputNickname;
    RadioGroup radioGroup;
    RatioButton sex1;
    String userSex = "man",userLocation;
 
    // url to create new product
    private static String url_create_product = "http://140.118.110.188/db_loginfunction/index.php";
 
    // JSON Node names
    private static final String TAG_SUCCESS = "success";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_act_create_member);
	        // Edit Text
			setTitle("創建帳戶");
			Log.d("Create Response", "userSex");
			Log.d("Create Response", "begin");
	        inputAccount = (EditText) findViewById(R.id.account_create);
	        inputPassword = (EditText) findViewById(R.id.password_create);
	        inputNickname = (EditText) findViewById(R.id.nickname_create);
	        radioGroup = (RadioGroup) findViewById(R.id.sex);
	        

	        Spinner spinner_items = (Spinner) findViewById(R.id.Spinner1);
	        
			//從res/values/string.xml讀取資料到Spinner
			ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
					R.array.items,
					android.R.layout.simple_spinner_item);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			        
		    radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() 
		    {
		        public void onCheckedChanged(RadioGroup group, int checkedId) {
		            // checkedId is the RadioButton selected
		        	if(checkedId == R.id.sex1){
		        		userSex="man";
		        	}else{
		        		userSex="women";
		        	}
		        }
		    });
			Log.d("Create Response", userSex);
			spinner_items.setAdapter(adapter);		
			spinner_items.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
				public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id){
					userLocation = (String) adapterView.getSelectedItem();
					Toast.makeText(ActCreateMember.this, "您選擇"+adapterView.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
				}
				public void onNothingSelected(AdapterView<?> arg0) {
					Toast.makeText(ActCreateMember.this, "您沒有選擇任何項目", Toast.LENGTH_SHORT).show();
				}
		    });
	        // Create button
	        Button btnCreateMember = (Button) findViewById(R.id.button1);
	 
	        // button click event
	        btnCreateMember.setOnClickListener(new View.OnClickListener() {
	 
	            @Override
	            public void onClick(View view) {
	                // creating new product in background thread
	            	//Log.d("Create Response", "onclick");
	            	new CreateNewMember().execute();
	            }
	        });
	}
	
	/**
     * Background Async Task to Create new product
     * */
     class CreateNewMember extends AsyncTask<String, String, String> {
 
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("Create Response", "onPreExecute");
            pDialog = new ProgressDialog(ActCreateMember.this);
            pDialog.setMessage("創建帳戶中...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
 
        /**
         * Creating product
         **/
        protected String doInBackground(String... args) {
        	Log.d("Create Response", "doInBackground");
            String account = inputAccount.getText().toString();
            String password = inputPassword.getText().toString();
            String nickname = inputNickname.getText().toString();
            Log.d("Create Response", account);
            // Building Parameters
            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("tag", register_tag));
            params.add(new BasicNameValuePair("account", account));
            params.add(new BasicNameValuePair("password", password));
            params.add(new BasicNameValuePair("nickname", nickname));
            params.add(new BasicNameValuePair("sex", userSex));
            params.add(new BasicNameValuePair("address", userLocation));
 
            Log.d("Create Response", "Parameters");
            // getting JSON Object
            // Note that create product url accepts POST method
            json = jsonParser.makeHttpRequest(url_create_product,
                    "POST", params);
            Log.d("Create Response", "json");
            // check log cat fro response
            Log.d("Create Response", json.toString());
           ;
            //check for success tag
            
 
            return null;
        }
 
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
            try {
                int success = json.getInt(TAG_SUCCESS);
 
                if (success == 1) {
                	//successfully created product
                	AlertDialog.Builder bdr = new AlertDialog.Builder(ActCreateMember.this);
                	bdr.setMessage("創建成功!\n"+"點一下返回登入頁面").setTitle("歡迎");
                    bdr.setCancelable(true);
                    bdr.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        	Intent i = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(i);
                             //closing this screen
                             finish();
                        }
                    });
                    bdr.show();
                    
                    } else {
                    // failed to create product
                    Toast.makeText(ActCreateMember.this, "帳號重複,請重新輸入!", Toast.LENGTH_LONG).show();
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
	 
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}*/

}
