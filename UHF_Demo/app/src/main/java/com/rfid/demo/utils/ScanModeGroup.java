package com.rfid.demo.utils;

import android.app.ActivityGroup;
import android.os.Bundle;

public class ScanModeGroup extends ActivityGroup {

	public ActivityGroup group;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		group = this;
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		group.getLocalActivityManager().getCurrentActivity().onBackPressed(); 
	}
	
	@Override  
    protected void onStart() {  
        super.onStart();
    }
}
