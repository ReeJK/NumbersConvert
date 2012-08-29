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
	
	public void setContentView(int layoutResID, int titlebarResID) {
		setContentView(layoutResID);
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

/*import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

public class BarActivity extends Activity {

	private boolean _customTitlebar;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_customTitlebar = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
	}
	
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
	}
	
	public void setContentView(int layoutResID, int titlebarResID) {
		setContentView(layoutResID);
		
		if(_customTitlebar) {
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
			
			findViewById(R.id.home).setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					startActivity(new Intent(getBaseContext(), NewsActivity.class));
				}
			});
		}
	}
}
*/