package com.example.irunning_test;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public class RunningActivity  implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public RunningActivity (JSONObject c) throws JSONException {	
		mAct_id = c.getString("act_id");
		mTitle = c.getString("title");
		mtext_content = c.getString("text_content");
		mDatetime = c.getString("datetime");;
		mMaxmembers_count = c.getString("members_count");
		mCurrent_members_count = c.getString("current_members_count");
		mStartLat = c.getString("startLat");
		mStartLng = c.getString("startLng");
		mDesLat = c.getString("desLat");
		mDesLng = c.getString("desLng");
		//mAccount = c.getString("account");
	}

	public String mAct_id;
	//public String mAccount;
	public String mTitle;
	public String mtext_content;
	public String mDatetime;
	public String mMaxmembers_count;
	public String mCurrent_members_count;
	public String mStartLat;
	public String mStartLng;
	public String mDesLat;
	public String mDesLng;
}
