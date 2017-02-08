package com.example.irunning_test;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.irunning_test.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class DetailActivity extends ActionBarActivity {

	private RunningActivity mRunAct;
	private ProgressDialog pDialog;
	private JSONObject json;
	JSONParser jsonParser = new JSONParser();
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_ACTIVITY = "activity";
	private static String url_join_activity = "http://140.118.110.188/db_loginfunction/index.php";
    GoogleMap mMap;
	TextView inputTitle, inputDatetime, inputMemberscount, inputTextcontent, inputCurmemberscount, inputTest;
	Double startlat,startlng;
	Double deslat,deslng;
	private LatLng  startPosition;
	private LatLng  desPosition;
	private JSONArray activity;
	private static final LatLng  NTUST = new LatLng(25.013068,
			121.541651);

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_act);
		Intent intent = getIntent();
		mRunAct = (RunningActivity) intent
				.getSerializableExtra("RunningActivity");
		inputTest = (TextView) findViewById(R.id.textView7);
		inputTitle = (TextView) findViewById(R.id.textTitle);
		inputDatetime = (TextView) findViewById(R.id.textDate);
		inputMemberscount = (TextView) findViewById(R.id.text_membersCount);
		inputTextcontent = (TextView) findViewById(R.id.text_content);
		inputCurmemberscount = (TextView) findViewById(R.id.text_current_members_count);
		SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.gMap2);
	    mMap =  fragment.getMap();
	    mMap.setMyLocationEnabled(true);
		mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		startlat = Double.parseDouble(mRunAct.mStartLat);
		startlng = Double.parseDouble(mRunAct.mStartLng);
		deslat = Double.parseDouble(mRunAct.mDesLat);
		deslng = Double.parseDouble(mRunAct.mDesLng);
		startPosition = new LatLng(startlat,startlng);
		desPosition = new LatLng(deslat,deslng);
		
		MarkerOptions options = new MarkerOptions();
		options.position(startPosition);
		options.position(desPosition);
		mMap.addMarker(options);
		String url = getMapsApiDirectionsUrl();
		ReadTask readTask = new ReadTask();
		readTask.execute(url);
		
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(NTUST,13));
		addMarkers();
		

		Button btnJoin = (Button) findViewById(R.id.button_Confirm);
		btnJoin.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				// creating new product in background thread
				new JoinActivity().execute();

			}
		});
		Button btnGoBack = (Button) findViewById(R.id.button_Cancel);
		btnGoBack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				// creating new product in background thread
				// Log.d("Create Response", "onclick");
				Intent i = new Intent(DetailActivity.this,
						MainActScreen.class);
				startActivity(i);
			}
		});
		  
              // Getting the place entered
          
		//inputTest.setText(mRunAct.mStartLat);
		inputTitle.setText(mRunAct.mTitle);
		inputDatetime.setText(mRunAct.mDatetime);
		inputMemberscount.setText(mRunAct.mMaxmembers_count);
		inputTextcontent.setText(mRunAct.mtext_content);
		inputCurmemberscount.setText(mRunAct.mCurrent_members_count);
	}
	
	private String getMapsApiDirectionsUrl() {
		String waypoints = "waypoints=optimize:true|"
				+ startPosition.latitude + "," + startPosition.longitude
				+ "|" + "|" + desPosition.latitude + ","
				+ desPosition.longitude;
		
		String sensor = "sensor=false";
		String params = waypoints + "&" + sensor;
		String output = "json";
		String url = "https://maps.googleapis.com/maps/api/directions/"
				+ output + "?" + params + "&" + "mode=walking";
		return url;
		
	}
	
	private void addMarkers() {
		if (mMap != null) {
			mMap.addMarker(new MarkerOptions().position(startPosition)
					.title("起點"));
			mMap.addMarker(new MarkerOptions().position(desPosition)
					.title("終點"));
		}
	}

	private class ReadTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... url) {
			String data = "";
			try {
				HttpConnection http = new HttpConnection();
				data = http.readUrl(url[0]);
			} catch (Exception e) {
				Log.d("Background Task", e.toString());
			}
			return data;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			new pathParserTask().execute(result);
		}
	}
	
	private class pathParserTask extends
		AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

		@Override
    protected List<List<HashMap<String, String>>> doInBackground(
		String... jsonData) {

		JSONObject jObject;
	    List<List<HashMap<String, String>>> routes = null;
	
	try {
		jObject = new JSONObject(jsonData[0]);
		DirectionsJSONParser parser = new DirectionsJSONParser();
		routes = parser.parse(jObject);
	} catch (Exception e) {
		e.printStackTrace();
	}
	return routes;
}

@Override
protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
	ArrayList<LatLng> points = null;
	PolylineOptions polyLineOptions = null;

	// traversing through routes
	for (int i = 0; i < routes.size(); i++) {
		points = new ArrayList<LatLng>();
		polyLineOptions = new PolylineOptions();
		List<HashMap<String, String>> path = routes.get(i);

		for (int j = 0; j < path.size(); j++) {
			HashMap<String, String> point = path.get(j);

			double lat = Double.parseDouble(point.get("lat"));
			double lng = Double.parseDouble(point.get("lng"));
			LatLng position = new LatLng(lat, lng);
			
			points.add(position);
		}

		polyLineOptions.addAll(points);
		polyLineOptions.width(4);
		polyLineOptions.color(Color.BLUE);
	}

	mMap.addPolyline(polyLineOptions);
}
}
	
	class JoinActivity extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Log.d("Create Response", "onPreExecute");
			pDialog = new ProgressDialog(DetailActivity.this);
			pDialog.setMessage("加入中...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Creating product
		 **/
		protected String doInBackground(String... args) {
			Log.d("Create Response", "doInBackground");
			// String maxmembercount = inputMemberscount.getText().toString();
			String actid = mRunAct.mAct_id;
			// Building Parameters
			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("tag", "join_activity"));

			params.add(new BasicNameValuePair("act_id", actid));

			// Log.d("Create Response", "Parameters");
			// getting JSON Object
			// Note that create product url accepts POST method
			json = jsonParser
					.makeHttpRequest(url_join_activity, "POST", params);
			Log.d("Create Response", "json");
			// check log cat fro response
			Log.d("Create Response", json.toString());
			;
			// check for success tag

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
					// successfully created product
					AlertDialog.Builder bdr = new AlertDialog.Builder(
							DetailActivity.this);
					bdr.setMessage("加入成功!\n" + "點一下返回塗鴉牆").setTitle("恭喜");
					bdr.setCancelable(true);
					bdr.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {

									// closing this screen
									finish();
								}
							});
					bdr.show();

				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

	}

}
