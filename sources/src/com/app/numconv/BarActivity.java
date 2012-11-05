package com.app.numconv;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

import android.os.Bundle;

public class BarActivity extends SherlockActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setBackgroundDrawable(
				getResources().getDrawable(R.drawable.titlebar_background_repeat));
	}
	
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
	}
	
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch(item.getItemId()) {
		case android.R.id.home: 
			onBackPressed();
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}
}