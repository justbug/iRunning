package com.example.irunning_test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.support.v4.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;


public class GPScatch extends Fragment {
	private ListView listView;
	private Button btn_reflash;
	ProgressDialog pDialog;
	private final double EARTH_RADIUS = 6378137.0;
	ArrayList<HashMap<String, Object>> activitysList;
	private JSONObject json;
	private View v;
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_ACTIVITY = "activity";
	private static final String TAG_TITLE = "title";
	private static final String TAG_TEXTCONTENT = "text_content";
	private static final String TAG_CREATEDUSER = "created_user";
	private static final String TAG_MEMBERSCOUNT = "members_count";
	private static final String TAG_CURMEMBERSCOUNT = "current_members_count";
	private double self_weidu = 0;
	private double self_jindu = 0;
	private LocationManager locationMgr;
	private String provider;
	private String State = "create";
	
	private Location prelocation = null;
	JSONArray activity = null;
	JSONParser jParser = new JSONParser();
	
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		v = inflater.inflate(R.layout.fragement_act_near, container, false);
		getActivity().getActionBar().setTitle("方圓1公里內的活動");

		listView = (ListView) v.findViewById(R.id.listView3);
		btn_reflash = (Button) v.findViewById(R.id.button2);
		activitysList = new ArrayList<HashMap<String, Object>>();
		if (initLocationProvider()) {
			whereAmI();
		}else{
			Toast.makeText(getActivity(), "請開啟定位！", Toast.LENGTH_SHORT).show();
		}
		btn_reflash.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (initLocationProvider()) {
					State = "create";
					activitysList.clear();
					whereAmI();
				}else{
					Toast.makeText(getActivity(), "請開啟定位！", Toast.LENGTH_SHORT).show();
				}
				
				
			}
			
		});
		
		
		
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				HashMap<String, Object> aAct = activitysList.get(position);
				
//				Iterator<Map.Entry<String,Object>> iter = aAct.entrySet().iterator();
//				while (iter.hasNext()) {
//				    Map.Entry<String,Object> entry = iter.next();
//				    if("Sample".equalsIgnoreCase((String) entry.getValue())){
//				        iter.remove();
//				    }
//				}
				RunningActivity aRunAct = (RunningActivity) aAct
						.get("RunningActivity");
				Intent intent = new Intent(getActivity(), DetailActivity.class);
				intent.putExtra("RunningActivity", aRunAct);
				startActivity(intent);
				// finish();

			}
		});
		return v;
	}
	
	class ShowNearbyAct extends AsyncTask<String, String, String> {
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(getActivity());
			pDialog.setMessage("更新塗鴉牆中...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}
		protected String doInBackground(String... args) {
			String url_get_all_post = "http://140.118.110.188/db_loginfunction/getAllPost.php";
		    
//		    LocationManager locationm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
//			Criteria criteria = new Criteria();
//			criteria.setAccuracy(Criteria.ACCURACY_FINE);
//			criteria.setAltitudeRequired(false);
//			criteria.setBearingRequired(false);
//			criteria.setCostAllowed(true);
//			criteria.setPowerRequirement(Criteria.POWER_LOW);
//			String provider = locationm.getBestProvider(criteria, true);
//			Location location = locationm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//			gps_loc(location);
			
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("tag", "getNearActivity"));
			params.add(new BasicNameValuePair("locationLat", String.valueOf(self_weidu)));
			params.add(new BasicNameValuePair("locationLng", String.valueOf(self_jindu)));
			Log.d("locationLat", String.valueOf(self_weidu));
			 
			
			// getting JSON string from URL
			json = jParser.makeHttpRequest(url_get_all_post, "POST", params);
			JSONObject json = jParser.makeHttpRequest(url_get_all_post, "GET",params);
			//HashMap<String, Double> map = null;
			try {
				// Checking for SUCCESS TAG
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					activity = json.getJSONArray(TAG_ACTIVITY);
					
					for (int i = 0; i < activity.length(); i++) {
						JSONObject c = activity.getJSONObject(i);
						Log.d("JSonObj-c ", c.toString());
						
						String title = c.getString(TAG_TITLE);
						String datetime = c.getString("datetime");
						String members_count = c.getString(TAG_MEMBERSCOUNT);
						String current_members_count = c.getString(TAG_CURMEMBERSCOUNT);
						String created_user = c.getString(TAG_CREATEDUSER);
						
						RunningActivity runact = new RunningActivity(c);
						HashMap<String, Object> map = new HashMap<String, Object>();
						
						// adding each child node to HashMap key => value
						map.put(TAG_TITLE, title);
						map.put("datetime", datetime);
						// map.put(TAG_TEXTCONTENT, text_content);
						map.put(TAG_MEMBERSCOUNT, members_count);
						map.put(TAG_CURMEMBERSCOUNT, current_members_count);
						map.put(TAG_CREATEDUSER, created_user);
						map.put("RunningActivity", runact);

						// adding HashList to ArrayList
						activitysList.add(map);
						
						
						// Storing each json item in variabl
						

						// adding each child node to HashMap key => value
					   
						
						

					}
					
					
				}
			}catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}
	
//	private void gps_loc(Location location) {
//		if (location != null) {
//			self_weidu = location.getLatitude();
//			self_jindu = location.getLongitude();
//		} else {
//			self_weidu = 0;
//		    self_jindu = 0;
//		}
//	}
	
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
						activitysList, R.layout.list_activitys,
						new String[] { TAG_TITLE, "datetime", TAG_CURMEMBERSCOUNT,
								TAG_MEMBERSCOUNT, TAG_CREATEDUSER }, new int[] {
								R.id.textTitle, R.id.textViewDatetime, R.id.text_current_member_count,
								R.id.text_membersCount, R.id.text_createdUser });
				// updating listview
				listView.setAdapter(adapter);
			}

		});

	}
  }
	
    private boolean initLocationProvider() {
		locationMgr = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

		//1.選擇最佳提供器
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(true);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		
		provider = locationMgr.getBestProvider(criteria, true);
		
		if (provider.equals("passive")) {
					
		}else{
			return true;
		}
		return false;
	}
	private void whereAmI(){
		long minTime = 10;//ms
		float minDist = 1.0f;//meter
		if(prelocation == null){
			locationMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDist, locationListener);			
			}else{
				locationMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDist, locationListener);
			}
		}
	private void updateWithNewLocation(Location location) {
		if (location != null) {
			//經度
			self_jindu = location.getLongitude();
			//緯度
			self_weidu = location.getLatitude();						
			
			if(self_jindu!=0 && self_weidu!=0 && State.equals("create") ){
				new ShowNearbyAct().execute();
				State = "update";
			}
			
			//new searchupdateStore().execute();
		}
		
	}

	LocationListener locationListener = new LocationListener(){

		@Override
		public void onLocationChanged(Location location) {
			if(prelocation == null )
				updateWithNewLocation(location);
			else if(prelocation != null && !location.getProvider().equals("network"))
				updateWithNewLocation(location);
			
		}

		@Override
		public void onProviderDisabled(String provider) {
			updateWithNewLocation(null);
		}

		@Override
		public void onProviderEnabled(String provider) {

		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}

	};
}
//	private double gps2m(double startLat, double startLng) {
//		double radLat1 = (lat_a * Math.PI / 180.0);
//		double radLat2 = (lat_b * Math.PI / 180.0);
//		double a = radLat1 - radLat2;
//		double b = (lng_a - lng_b) * Math.PI / 180.0;
//		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
//		+ Math.cos(radLat1) * Math.cos(radLat2)
//		* Math.pow(Math.sin(b / 2), 2)));
//		s = s * EARTH_RADIUS;
//		s = Math.Round(s * 10000) / 10000;
//		return s;
//	}

