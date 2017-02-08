package com.example.irunning_test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.example.irunning_test.R;
import com.example.irunning_test.DirectionsJSONParser;
import com.example.irunning_test.GeocodeJSONParser;
import com.example.irunning_test.HttpConnection;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;


import android.app.AlertDialog;
//import com.example.irunning_test.ActCreateMember.CreateNewMember;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


public class ActCreateActivity extends ActionBarActivity {
	private static String register_tag = "add_post";
	private ProgressDialog pDialog;
	private JSONObject json;
	static final int DATE_DIALOG_ID = 1;
	static final int TIME_DIALOG_ID = 2;
	private TextView dateDisplay;
	private Button pickDate;
	private int year, month, day;
	private TextView timeDisplay;
	private Button pickTime;
	private int hours, min;
	EditText inputTitle;
	EditText inputTextContent;
	EditText inputMemberCount;
	TextView inputDatetime;
	RadioGroup radioGroup;// b
	JSONParser jsonParser = new JSONParser();
	private static String url_create_act = "http://140.118.110.188/db_loginfunction/index.php";
	private static final String TAG_SUCCESS = "success";
	Calendar cal;
	
    Button mBtnFind;
    GoogleMap mMap;
    EditText startPoint;
    EditText endPoint;
    TextView txt1,txt2;
	String sLat,sLng;
	private Double  startLat,startLng;
	private Double desLat,desLng;
	private static final LatLng  NTUST = new LatLng(25.013068,
			121.541651);
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_act_event);
		inputTitle = (EditText) findViewById(R.id.editText1);
		inputTextContent = (EditText) findViewById(R.id.editText4);
		inputMemberCount = (EditText) findViewById(R.id.editText3);
		inputDatetime = (TextView) findViewById(R.id.textDate);
		radioGroup = (RadioGroup) findViewById(R.id.sex);// b
		//maps 
		SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.gMap1);
	    mMap =  fragment.getMap();
	    mMap.setMyLocationEnabled(true);
		mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		mBtnFind = (Button) findViewById(R.id.btn_find);
        startPoint = (EditText) findViewById(R.id.startPoint);
        endPoint = (EditText) findViewById(R.id.endPoint);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(NTUST,
				13));
        mBtnFind.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Getting the place entered
                String startLocation = startPoint.getText().toString();
                String destination = endPoint.getText().toString();
                
                if(startLocation==null || startLocation.equals("")){
                    Toast.makeText(getBaseContext(), "No Place is entered", Toast.LENGTH_SHORT).show();
                    return;
                }
                

                if(destination==null || destination.equals("")){
                    Toast.makeText(getBaseContext(), "No Place is entered", Toast.LENGTH_SHORT).show();
                    return;
                }
 
                String startUrl = "https://maps.googleapis.com/maps/api/geocode/json?";
                String endUrl = "https://maps.googleapis.com/maps/api/geocode/json?";

                try {
                    // encoding special characters like space in the user input place
                	startLocation = URLEncoder.encode(startLocation, "utf-8");
                	destination =	URLEncoder.encode(destination, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
 
                String startAddress = "address=" + startLocation;
                String endAddress = "address=" + destination;

                String sensor = "sensor=false";
                
                // url , from where the geocoding data is fetched
                startUrl = startUrl + startAddress + "&" + sensor;
                endUrl = endUrl + endAddress + "&" + sensor;
                
                // Instantiating DownloadTask to get places from Google Geocoding service
                // in a non-ui thread
                DownloadTask downloadTask = new DownloadTask();
                destinationDownloadTask downloadTask2 = new destinationDownloadTask();
        		

                // Start downloading the geocoding places
                downloadTask.execute(startUrl);
                downloadTask2.execute(endUrl);
            }
        });
        //done
        
        
		Spinner spinner_items = (Spinner) findViewById(R.id.Spinner1);// b
		
		
		
		cal = Calendar.getInstance();

		year = cal.get(Calendar.YEAR);
		month = cal.get(Calendar.MONTH);
		day = cal.get(Calendar.DAY_OF_MONTH);

		updateDate();

		findViewById(R.id.button_DatePacker).setOnClickListener(
				new Button.OnClickListener() {
					@Override
					public void onClick(View v) {
						showDialog(DATE_DIALOG_ID);
					}
				});

		findViewById(R.id.button_TimePacker).setOnClickListener(
				new Button.OnClickListener() {
					@Override
					public void onClick(View v) {
						showDialog(TIME_DIALOG_ID);
					}
				});
		Button btnCreateAct = (Button) findViewById(R.id.button_Confirm);
		btnCreateAct.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				// creating new product in background thread
				// Log.d("Create Response", "onclick");
				new CreateNewAct().execute();
			}
		});

	}
//----------------------------maps----------------------------------------------
	private String getMapsApiDirectionsUrl() {
		String waypoints = "waypoints=optimize:true|"
				+ startLat + "," + startLng
				+ "|" + "|" + desLat + ","
				+ desLng;
		
		String sensor = "sensor=false";
		String params = waypoints + "&" + sensor;
		String output = "json";
		String url = "https://maps.googleapis.com/maps/api/directions/"
				+ output + "?" + params + "&" + "mode=walking";
		return url;
		
	}
	
	private String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
 
            // Connecting to url
            urlConnection.connect();
 
            // Reading data from url
            iStream = urlConnection.getInputStream();
 
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
 
            StringBuffer sb = new StringBuffer();
 
            String line = "";
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }
 
            data = sb.toString();
            br.close();
 
        }catch(Exception e){
            Log.d("Exception while downloading url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
    /** A class, to download Places from Geocoding webservice */
    public class DownloadTask extends AsyncTask<String, Integer, String>{
 
        String data = null;
 
        // Invoked by execute() method of this object
        @Override
        protected String doInBackground(String... url) {
            try{
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }
 
        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(String result){
 
            // Instantiating ParserTask which parses the json data from Geocoding webservice
            // in a non-ui thread
            ParserTask parserTask = new ParserTask();
 
            // Start parsing the places in JSON format
            // Invokes the "doInBackground()" method of the class ParseTask
            parserTask.execute(result);
        }
    }
 
    /** A class to parse the Geocoding Places in non-ui thread */
    class ParserTask extends AsyncTask<String, Integer, List<HashMap<String,String>>>{
 
        JSONObject jObject;
 
        // Invoked by execute() method of this object
        @Override
        protected List<HashMap<String,String>> doInBackground(String... jsonData) {
 
            List<HashMap<String, String>> places = null;
            GeocodeJSONParser parser = new GeocodeJSONParser();
 
            try{
                jObject = new JSONObject(jsonData[0]);
 
                /** Getting the parsed data as a an ArrayList */
                places = parser.parse(jObject);
 
            }catch(Exception e){
                Log.d("Exception",e.toString());
            }
            return places;
        }
        
        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(List<HashMap<String,String>> list){
 
            // Clears all the existing markers

        	mMap.clear();
    		for(int i=0;i<list.size();i++){
        		
            	// Creating a marker
                MarkerOptions markerOptions = new MarkerOptions();
                
                // Getting a place from the places list
                HashMap<String, String> hmPlace = list.get(0);
 
                // Getting latitude of the place
                double lat = Double.parseDouble(hmPlace.get("lat"));
 
                // Getting longitude of the place
                double lng = Double.parseDouble(hmPlace.get("lng"));
                // Getting name
                startLat= lat;
                startLng= lng;

                String name = hmPlace.get("formatted_address");
                
                LatLng latLng = new LatLng(lat, lng);
                // Setting the position for the marker
                markerOptions.position(latLng);
                // Setting the title for the marker
                markerOptions.title("起點");
                
                // Placing a marker on the touched position
                mMap.addMarker(markerOptions);
    		}
        }
    }

    public class destinationDownloadTask extends AsyncTask<String, Integer, String>{
    	 
        String data = null;
 
        // Invoked by execute() method of this object
        @Override
        protected String doInBackground(String... url) {
            try{
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }
 
        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(String result){
 
            // Instantiating ParserTask which parses the json data from Geocoding webservice
            // in a non-ui thread
        	destinationParserTask parserTask = new destinationParserTask();
 
            // Start parsing the places in JSON format
            // Invokes the "doInBackground()" method of the class ParseTask
            parserTask.execute(result);
        }
    }
 
    /** A class to parse the Geocoding Places in non-ui thread */
    class destinationParserTask extends AsyncTask<String, Integer, List<HashMap<String,String>>>{
 
        JSONObject jObject;
 
        // Invoked by execute() method of this object
        @Override
        protected List<HashMap<String,String>> doInBackground(String... jsonData) {
 
            List<HashMap<String, String>> places = null;
            GeocodeJSONParser parser = new GeocodeJSONParser();
 
            try{
                jObject = new JSONObject(jsonData[0]);
 
                /** Getting the parsed data as a an ArrayList */
                places = parser.parse(jObject);
 
            }catch(Exception e){
                Log.d("Exception",e.toString());
            }
            return places;
        }
        
        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(List<HashMap<String,String>> list){
 
            // Clears all the existing markers

        		for(int i=0;i<list.size();i++){
            	// Creating a marker
                MarkerOptions markerOptions = new MarkerOptions();
                
                // Getting a place from the places list
                HashMap<String, String> hmPlace = list.get(0);
 
                // Getting latitude of the place
                double lat = Double.parseDouble(hmPlace.get("lat"));
 
                // Getting longitude of the place
                double lng = Double.parseDouble(hmPlace.get("lng"));

                desLat= lat;
                desLng= lng;
                // Getting name
                String name = hmPlace.get("formatted_address");
                
                LatLng latLng = new LatLng(lat, lng);
                // Setting the position for the marker
                markerOptions.position(latLng);
                // Setting the title for the marker
                markerOptions.title("終點");
                
                // Placing a marker on the touched position
                mMap.addMarker(markerOptions);
                // Locate the first location
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,
						13));

                ReadTask readTask = new ReadTask();
                String pathurl = getMapsApiDirectionsUrl();
        		readTask.execute(pathurl);
        		}
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
	
	//---------------------maps done--------------------------------------------------
	
	
	class CreateNewAct extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(ActCreateActivity.this);
			pDialog.setMessage("創建活動中...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Creating product
		 **/
		protected String doInBackground(String... args) {
			// Log.d("Create Response", "doInBackground");
			String title = inputTitle.getText().toString();
			String text_content = inputTextContent.getText().toString();
			String members_count = inputMemberCount.getText().toString();
			String datetime = inputDatetime.getText().toString();
			Log.d("Create Response", title);
			// Building Parameters
			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("tag", register_tag));
			params.add(new BasicNameValuePair("title", title));
			params.add(new BasicNameValuePair("datetime", datetime));
			params.add(new BasicNameValuePair("text_content", text_content));
			params.add(new BasicNameValuePair("members_count", members_count));
			params.add(new BasicNameValuePair("startLat", startLat.toString()));
			params.add(new BasicNameValuePair("startLng", startLng.toString()));
			params.add(new BasicNameValuePair("desLat", desLat.toString()));
			params.add(new BasicNameValuePair("desLng", desLng.toString()));
			params.add(new BasicNameValuePair("created_user",MainActivity.outputAccount));
			params.add(new BasicNameValuePair("current_member_count", "1"));
			Log.d("Create Response title ", title);
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
							ActCreateActivity.this);
					bdr.setMessage("活動創建成功!\n").setTitle("恭喜");
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

//	private void updateTime() {
//		TextView textDate = (TextView) findViewById(R.id.textDate);
//		textDate.setText(
//		            new StringBuilder()
//		                    // Month is 0 based so add 1
//		                    .append(hours).append(":")
//		                    .append(min).append(" ")
//		                    .append(month + 1).append("-")
//		                    .append(day).append("-")
//		                    .append(year).append(" ")
//		                   
//		                    
//							);	
//	}

	private void updateDate() {
		TextView textDate = (TextView) findViewById(R.id.textDate);
		textDate.setText(
		            new StringBuilder()
		                    // Month is 0 based so add 1
		                    .append(String.format("%04d-%02d-%02d %02d:%02d:00", year,month+1,day,hours,min))
							);
							
	}

	private DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int yr, int monthOfYear,
				int dayOfMonth ) {
			year = yr;
			month = monthOfYear;
			day = dayOfMonth;
			
			updateDate();
		}
	};

	private TimePickerDialog.OnTimeSetListener timeListener = new TimePickerDialog.OnTimeSetListener() {
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			hours = hourOfDay;
			min = minute;
			updateDate();
		}

	};
//	private DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
//
//		@Override
//		public void onDateSet(DatePicker view, int yr, int monthOfYear,
//				int dayOfMonth) {
//			year = yr;
//			month = monthOfYear;
//			day = dayOfMonth;
//			updateDate();
//		}
//	};
//
//	private TimePickerDialog.OnTimeSetListener timeListener = new TimePickerDialog.OnTimeSetListener() {
//		@Override
//		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//			hours = hourOfDay;
//			min = minute;
//			updateTime();
//		}
//
//	};

	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			return new DatePickerDialog(this, dateListener, year, month, day);
		case TIME_DIALOG_ID:
			return new TimePickerDialog(this, timeListener, hours, min, false);
		}
		return null;

	}
}
