package com.example.irunning_test;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;

import com.example.irunning_test.R;

public class MainActScreen extends FragmentActivity {
  
  private TabHost mTabHost;
  private TabManager mTabManager;
  ProgressDialog pDialog;
//  int[] mIcon = new int[] { R.drawable.main_act_icon, R.drawable.personal_act_icon, R.drawable.nearby_act_icon};
//  String[] mTitle = new String[] { "塗鴉牆", "個人po文", "附近活動"};
  //mClass = new int[] {MainFunctionActivity.class, PersonalFunctionActivity.class,GPScatch.class };
  
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
	 
    setContentView(R.layout.activity_main_screen);

    mTabHost = (TabHost)findViewById(android.R.id.tabhost);
/*    Intent intentAndroid = new Intent().setClass(this, MainFunctionActivity.class);
	TabSpec tabSpecAndroid = mTabHost
	  .newTabSpec("塗鴉牆")
	  .setIndicator("塗鴉牆", getResources().getDrawable(R.drawable.main_act_icon))
	  .setContent(intentAndroid);
	Intent intentAndroid2 = new Intent().setClass(this, PersonalFunctionActivity.class);
	TabSpec tabSpecAndroid2 = mTabHost
	  .newTabSpec("個人po文")
	  .setIndicator("個人po文", getResources().getDrawable(R.drawable.personal_act_icon))
	  .setContent(intentAndroid2);
	
	Intent intentAndroid3 = new Intent().setClass(this, GPScatch.class);
	TabSpec tabSpecAndroid3 = mTabHost
	  .newTabSpec("附近活動")
	  .setIndicator("附近活動", getResources().getDrawable(R.drawable.nearby_act_icon))
	  .setContent(intentAndroid3);
	mTabHost.addTab(tabSpecAndroid);
	mTabHost.addTab(tabSpecAndroid2);
	mTabHost.addTab(tabSpecAndroid3);
	mTabHost.setCurrentTab(0);
*/
    mTabHost.setup();
    mTabManager = new TabManager(this, mTabHost, R.id.realtabcontent);
    mTabHost.setCurrentTab(0);//設定一開始就跳到第一個分頁
    Log.d("Create Response", "tabhost");
	//Resources ressources = getResources(); 

    //Intent intent = new Intent();
    //intent.setClass(MainActScreen.this,mClass)
//    setupTab(MainFunctionActivity.class,mTitle[0],"123",mIcon[0]);
//    setupTab(PersonalFunctionActivity.class,mTitle[0],"123",mIcon[0]);
//    setupTab(GPScatch.class,mTitle[0],"123",mIcon[0]);
//   for (int i = 0; i < mTitle.length; i++) { 
    	mTabManager.addTab(
    			mTabHost.newTabSpec("塗鴉牆").setIndicator("塗鴉牆",this.getResources().getDrawable(android.R.drawable.ic_dialog_alert)),MainFunctionActivity.class,null
    			);
	    mTabManager.addTab(
	        mTabHost.newTabSpec("個人po文").setIndicator("個人po文",this.getResources().getDrawable(android.R.drawable.ic_dialog_alert)),
	        PersonalFunctionActivity.class, null);
	    mTabManager.addTab(
	        mTabHost.newTabSpec("附近活動").setIndicator("附近活動",this.getResources().getDrawable(android.R.drawable.ic_dialog_alert)),
	        GPScatch.class,null);
	    //mTabHost.getTabWidget().setBackgroundResource(R.drawable.tab_background_color);
	    
	    DisplayMetrics dm = new DisplayMetrics();   
        getWindowManager().getDefaultDisplay().getMetrics(dm); //先取得螢幕解析度  
        int screenWidth = dm.widthPixels;   //取得螢幕的寬
        TabWidget tabWidget = mTabHost.getTabWidget();   //取得tab的物件
        int count = tabWidget.getChildCount();   //取得tab的分頁有幾個
        if (count > 3) {   
            for (int i = 0; i < count; i++) {   
                tabWidget.getChildTabViewAt(i)
                      .setMinimumWidth((screenWidth)/3);//設定每一個分頁最小的寬度
//                TextView tv = (TextView) tabWidget.getChildAt(i).findViewById(android.R.id.title);
//                tv.setTextSize(23);
            }   
        }
        
//    DisplayMetrics dm = new DisplayMetrics();
//    getWindowManager().getDefaultDisplay().getMetrics(dm); //先取得螢幕解析度  
//    int screenWidth = dm.widthPixels;   //取得螢幕的寬
//       
//       
//    TabWidget tabWidget = mTabHost.getTabWidget();   //取得tab的物件
//    int count = tabWidget.getChildCount();   //取得tab的分頁有幾個
//    if (count > 3) {   
//        for (int i = 0; i < count; i++) {   
//            tabWidget.getChildTabViewAt(i)
//                  .setMinimumWidth((screenWidth)/3);//設定每一個分頁最小的寬度   
//        }   
//    }
    /*mTabManager.addTab(
        mTabHost.newTabSpec("Fragment3").setIndicator("Fragment3"),
        Fragment3.class, null);
    mTabManager.addTab(
        mTabHost.newTabSpec("Fragment4").setIndicator("Fragment4"),
        Fragment4.class, null);*/
   
    //
    //tabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
   
    //1
//    tabHost.addTab(tabHost.newTabSpec("Apple")
//                          .setIndicator("Apple"), 
//                   MainFunctionActivity.class, 
//                   null);
//    //2
//    tabHost.addTab(tabHost.newTabSpec("Google")
//                          .setIndicator("Google"), 
//                   PersonalProfile.class, 
//                   null);
//    //3
    /*tabHost.addTab(tabHost.newTabSpec("Facebook")
                          .setIndicator("Facebook"), 
                   FacebookFragment.class, 
                   null);
    //4
    tabHost.addTab(tabHost.newTabSpec("Twitter")
                          .setIndicator("Twitter"), 
                   TwitterFragment.class, 
                   null);*/
  }
//  	public View getTabItemView(int i) {  
//      // TODO Auto-generated method stub  
//      View view = LayoutInflater.from(this).inflate(R.layout.custom_tab, null);  
//      ImageView imageView = (ImageView) view.findViewById(R.id.icon);  
//      imageView.setImageResource(mIcon[i]);  
//      TextView textView = (TextView) view.findViewById(R.id.text1);  
//      textView.setText(mTitle[i]);
//      return view;  
//   }  

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.act_mainfunction, menu);
		menu.add(0, 0, 0, "揪團去");
		menu.add(0, 1, 1, "我要po文");
		return super.onCreateOptionsMenu(menu);
	}
	public boolean onOptionsItemSelected(MenuItem item) {
		
		        //依據itemId來判斷使用者點選哪一個item
		  switch(item.getItemId()) {
		       case 0:
		    	   //Intent intent = new Intent(MainActScreen.this, ActCreateActivity.class);
		    	   //startActivity(intent);
		    	   startActivity(new Intent("com.example.irunning_test.ActCreateActivity"));
		    	   //startActivity(new Intent("com.example.irunning_test.ActCreateSelfAct"));
		    	   
		           break;
		        case 1:
		        	
		        	startActivity(new Intent("com.example.irunning_test.ActCreateSelfAct"));
		            
		            break;

		        default:
		   }
		   return super.onOptionsItemSelected(item);
	}
	
//	private void setupTab(Class<?> ccls, String name, String label, Integer iconId) {
//	    Intent intent = new Intent().setClass(this, ccls);
//
//
//	    View tab = LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
//	    ImageView image = (ImageView) tab.findViewById(R.id.icon);
//	    TextView text = (TextView) tab.findViewById(R.id.text1);
//	    if(iconId != null){
//	        image.setImageResource(iconId);
//	    }
//	    text.setText(label);
//
//	    TabSpec spec = mTabHost.newTabSpec(name).setIndicator(tab).setContent(intent);
//	    mTabHost.addTab(spec);
//
//	}*/
}

