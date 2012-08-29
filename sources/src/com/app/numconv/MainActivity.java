package com.app.numconv;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.app.numconv.ClearableEditText.OnClearListener;
import com.app.numconv.NumberPickerView.OnChangeListener;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.inputmethodservice.KeyboardView;
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.os.Bundle;
import android.preference.PreferenceManager;

import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class MainActivity extends BarActivity {
	private NumberPickerView _fromView;
	private NumberPickerView _toView;
	private EditText _resultView;
	private ClearableEditText _numberView;
	
	private KeyboardView _keyboardView;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main, R.layout.titlebar);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		
		int versionCode = 0;
		try {
			versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) { }
		
		if(PreferenceManager.getDefaultSharedPreferences(this).getInt("run", 0) < versionCode) {
			PreferenceManager.getDefaultSharedPreferences(this).edit()
				.putInt("run", versionCode)
				.commit();
			
			Intent intent = new Intent(this, NewsActivity.class);
			intent.putExtra("createNew", false);
			startActivity(intent);
		}
		
		/*findViewById(R.id.calculatorButton).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, CalcActivity.class));
			}
		});*/
		
		_fromView = (NumberPickerView)findViewById(R.id.from);
		_toView = (NumberPickerView)findViewById(R.id.to);
		_resultView = (EditText)findViewById(R.id.result);
		_numberView = (ClearableEditText)findViewById(R.id.number);
		
		_fromView.setRange(2, 36);
		_fromView.select(10);
		_toView.setRange(2, 36);
		_toView.select(2);
		
		OnChangeListener onNumberChangeListener = new OnChangeListener() {
			public void onChange(View v, int oldValue, int newValue) {
				if(oldValue == newValue) return;
				
				if(v == _fromView) {
					ThisApplication app = (ThisApplication) getApplication();
					_keyboardView.setKeyboard(app.getKeyboard(newValue));
					_keyboardView.invalidateAllKeys();
				}
				
				String text = _numberView.getText().toString();
				if(text.length() == 0) _resultView.setText("");
				else try {
					_resultView.setText(Converter.convert(text, _fromView.getNumber(), _toView.getNumber()));
				} catch(NumberFormatException e) {
					_resultView.setText(R.string.change_number_system_error);
				}
			}
		};
		
		_fromView.setOnChangeListener(onNumberChangeListener);
		_toView.setOnChangeListener(onNumberChangeListener);
		
		_numberView.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				String text = _numberView.getText().toString();
				if(text.length() == 0) _resultView.setText("");
				else try {
					_resultView.setText(Converter.convert(text, _fromView.getNumber(), _toView.getNumber()));
				} catch(NumberFormatException e) {
					_resultView.setText("Change system or remove symbols");
				}
				return false;
			}
		});
		
		_numberView.setOnClearListener(new OnClearListener() {
			public void onClear() {
				_resultView.setText("");
			}
		});
		
		_numberView.setOnFocusChangeListener(new OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				_keyboardView.setVisibility(hasFocus ? View.VISIBLE : View.GONE);
				if(hasFocus) {
					InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(
							Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(_numberView.getWindowToken(), 0);
				}
			}
		});
		
		_resultView.setOnFocusChangeListener(new OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus) {
					_fromView.addHot();
					_toView.addHot();
				}
			}
		});
		
		_keyboardView = (KeyboardView) findViewById(R.id.keyboard);
		if(_keyboardView == null) {
			_numberView.setInputType(InputType.TYPE_CLASS_TEXT);
		} else {
			_keyboardView.setOnKeyboardActionListener(new OnKeyboardActionListener() {
	
				public void onKey(int primaryCode, int[] keyCodes) {
					
					Log.d("keyboard", "primaryCode = " + primaryCode);
					getCurrentFocus().onKeyDown(primaryCode, 
							new KeyEvent(KeyEvent.ACTION_DOWN, primaryCode));
				}
	
				public void onPress(int primaryCode) {
					Log.d("keyboardPress", "primaryCode = " + primaryCode);
					//getCurrentFocus().onKeyDown(primaryCode, 
					//		new KeyEvent(KeyEvent.ACTION_DOWN, primaryCode));
				}
				
				public void onRelease(int primaryCode) {
					Log.d("keyboardRelease", "primaryCode = " + primaryCode);
					//getCurrentFocus().onKeyUp(primaryCode, 
					//		new KeyEvent(KeyEvent.ACTION_UP, primaryCode));
				}
				
				public void onText(CharSequence text) {
					Log.d("keyboardText", "text = " + text);
				}
				
				public void swipeDown() { }
				public void swipeLeft() { }
				public void swipeRight() { }
				public void swipeUp() { }
			});
			
			ThisApplication app = (ThisApplication) getApplication();
			
			_keyboardView.setKeyboard(app.getKeyboard(_fromView.getNumber()));
			_keyboardView.setVisibility(View.GONE);
			_keyboardView.invalidateAllKeys();
		}
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
       MenuInflater inflater = getSupportMenuInflater();
       inflater.inflate(R.menu.menu, menu);
       return super.onCreateOptionsMenu(menu);
    }
		
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch(item.getItemId()) {
		case R.id.calculatorButton:
			startActivity(new Intent(this, CalcActivity.class));
			break;
		case android.R.id.home:
			startActivity(new Intent(this, NewsActivity.class));
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}
}
