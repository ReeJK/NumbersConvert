package com.app.numconv;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import com.app.numconv.ClearableEditText.OnClearListener;
import com.app.numconv.NumberPickerView.OnChangeListener;

import android.content.Context;
import android.content.SharedPreferences;
import android.inputmethodservice.KeyboardView;
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

public class CalcActivity extends BarActivity {
	
	private static enum Operations {
		PLUS, MINUS, CROSS, DIVIDE
	}
	
	private class OperationButton implements OnClickListener {
		private Button _operations[] = new Button[4];
		private Operations _operation;
		
		public OperationButton() {
			_operations[0] = (Button) findViewById(R.id.operation_plus);
			_operations[1] = (Button) findViewById(R.id.operation_minus);
			_operations[2] = (Button) findViewById(R.id.operation_cross);
			_operations[3] = (Button) findViewById(R.id.operation_divide);
			
			for(Button button : _operations) {
				button.setOnClickListener(this);
			}
			
			_operations[0].setEnabled(false);
			_operation = Operations.PLUS;
		}

		public void onClick(View v) {
			for(Button button : _operations) {
				button.setEnabled(v != button);
			}
			
			if(v == _operations[0]) _operation = Operations.PLUS;
			else if(v == _operations[1]) _operation = Operations.MINUS;
			else if(v == _operations[2]) _operation = Operations.CROSS;
			else if(v == _operations[3]) _operation = Operations.DIVIDE;
			
			updateResult();
		}
		
		public Operations getOperation() {
			return _operation;
		}
		
		public int getOperationInt() {
			switch(_operation) {
			case PLUS: return 1;
			case MINUS: return 2;
			case CROSS: return 3;
			case DIVIDE: return 4;
			}
			return 0;
		}
		
		public void setOperation(Operations operation) {
			_operation = operation;
			
			for(Button button : _operations) {
				button.setEnabled(true);
			}
			
			switch(_operation) {
			case PLUS: _operations[0].setEnabled(false); break;
			case MINUS: _operations[1].setEnabled(false); break;
			case CROSS: _operations[2].setEnabled(false); break;
			case DIVIDE: _operations[3].setEnabled(false); break;
			}
		}
		
		public void setOperation(int operation) {
			if(operation >= 1 && operation <= 4)
				setOperation(Operations.values()[operation - 1]);
		}
	}
	
	private NumberPickerView _fromView1;
	private ClearableEditText _numberView1;
	private NumberPickerView _fromView2;
	private ClearableEditText _numberView2;
	private NumberPickerView _toView;
	private EditText _resultView;
	private OperationButton _operation;
	
	private KeyboardView _keyboardView;
	
	OnChangeListener onNumberChangeListener = new OnChangeListener() {
		public void onChange(View v, int oldValue, int newValue) {
			if(oldValue == newValue) return;
			
			Log.d("changeVal", newValue + " " + (v == _fromView1) + _numberView1.isFocused() + " " +
					(v == _fromView2) + _numberView2.isFocused() + " " + _numberView1.isSelected());
			
			if(_keyboardView != null && ((v == _fromView1 && _numberView1.isFocused()) ||
					(v == _fromView2 && _numberView2.isFocused()))) {
				ThisApplication app = (ThisApplication) getApplication();
				_keyboardView.setKeyboard(app.getKeyboard(newValue));
				_keyboardView.invalidateAllKeys();
			}
			
			updateResult();
		}
	};
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calc, R.layout.titlebar);
        
        //findViewById(R.id.calculatorButton).setVisibility(View.GONE);
        
        _fromView1 = (NumberPickerView)findViewById(R.id.from1);
        _numberView1 = (ClearableEditText)findViewById(R.id.number1);
        _fromView2 = (NumberPickerView)findViewById(R.id.from2);
        _numberView2 = (ClearableEditText)findViewById(R.id.number2);
		_toView = (NumberPickerView)findViewById(R.id.to);
		_resultView = (EditText)findViewById(R.id.result);
		
		_operation = new OperationButton();
		
		_toView.setSolidRightStyle(true);
		_resultView.setBackgroundResource(R.drawable.left_edittext_background);
		_resultView.setSingleLine();
		
		_toView.setRange(2, 36);
		_toView.select(2);
		
		_toView.setOnChangeListener(onNumberChangeListener);
		
		prepareViews(_numberView1, _fromView1);
		prepareViews(_numberView2, _fromView2);
		
		_resultView.setOnFocusChangeListener(new OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus) {
					//_fromView1.addHot();
					//_fromView2.addHot();
					//_toView.addHot();
				}
			}
		});
		
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		
		if(preferences.getBoolean("use_app_keyboard", true))
			_keyboardView = (KeyboardView) findViewById(R.id.keyboard);
		else _keyboardView = null;
		
		if(_keyboardView == null) {
			_numberView1.setInputType(InputType.TYPE_CLASS_TEXT);
			_numberView2.setInputType(InputType.TYPE_CLASS_TEXT);
		} else {
			_keyboardView.setOnKeyboardActionListener(new OnKeyboardActionListener() {
	
				public void onKey(int primaryCode, int[] keyCodes) {
					
					Log.d("keyboard", "primaryCode = " + primaryCode);
					
					getCurrentFocus().onKeyDown(primaryCode, 
							new KeyEvent(KeyEvent.ACTION_DOWN, primaryCode));
				}
	
				public void onPress(int primaryCode) {
					//getCurrentFocus().onKeyDown(primaryCode, 
					//		new KeyEvent(KeyEvent.ACTION_DOWN, primaryCode));
				}
				public void onRelease(int primaryCode) {
					//getCurrentFocus().onKeyUp(primaryCode, 
					//		new KeyEvent(KeyEvent.ACTION_UP, primaryCode));
				}
				public void onText(CharSequence text) { }
				public void swipeDown() { }
				public void swipeLeft() { }
				public void swipeRight() { }
				public void swipeUp() { }
			});
			
			ThisApplication app = (ThisApplication) getApplication();
			
			_keyboardView.setKeyboard(app.getKeyboard(_fromView1.getNumber()));
			_keyboardView.setVisibility(View.GONE);
			_keyboardView.invalidateAllKeys();
		}
	}
	
	private void prepareViews(ClearableEditText number, final NumberPickerView np) {
		np.setRange(2, 36);
		np.select(10);
		np.setSolidRightStyle(true);
		number.setSolidLeftStyle(true);
		
		np.setOnChangeListener(onNumberChangeListener);
		
		number.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				updateResult();
				return false;
			}
		});
		
		number.setOnClearListener(new OnClearListener() {
			public void onClear() {
				_resultView.setText("");
			}
		});
		
		number.setOnFocusChangeListener(new OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if(_keyboardView == null) return;
				_keyboardView.setVisibility(hasFocus ? View.VISIBLE : View.GONE);
				if(hasFocus) {
					InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(
							Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
					
					ThisApplication app = (ThisApplication) getApplication();
					_keyboardView.setKeyboard(app.getKeyboard(np.getNumber()));
				}
			}
		});
	}
	
	private void updateResult() {
		String text1 = _numberView1.getText().toString();
		String text2 = _numberView2.getText().toString();
		if(text1.length() == 0 || text2.length() == 0) _resultView.setText("");
		else try {
			double value1 = Double.valueOf(Converter.convert(text1, _fromView1.getNumber(), 10, true));
			double value2 = Double.valueOf(Converter.convert(text2, _fromView2.getNumber(), 10, true));
			double result = 0;
			
			switch(_operation.getOperation()) {
			case PLUS: result = value1 + value2; break;
			case MINUS: result = value1 - value2; break;
			case CROSS: result = value1 * value2; break;
			case DIVIDE: 
				result = value2 == 0 ? 0 : value1 / value2; 
				break;
			default: result = value1;
			}
			
			boolean minus_sign = result < 0;
			if(minus_sign) result = Math.abs(result);
			
			DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols();
			formatSymbols.setDecimalSeparator('.');
			formatSymbols.setNaN("0");
			DecimalFormat format = new DecimalFormat();
			format.setDecimalFormatSymbols(formatSymbols);
			format.setMaximumFractionDigits(20);
			format.setGroupingUsed(false);
			
			if(Double.isInfinite(result)) _resultView.setText(R.string.infinity);
			else _resultView.setText((minus_sign ? "-" : "") + 
					Converter.convert(format.format(result), 10, _toView.getNumber()));
		} catch(NumberFormatException e) {
			Log.w("Calc", "NFE: " + e.getMessage());
			_resultView.setText(R.string.change_number_system_error);
		}
	}
	
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt("from1", _fromView1.getNumber());
		outState.putInt("from2", _fromView2.getNumber());
		outState.putString("number1", _numberView1.getText().toString());
		outState.putString("number2", _numberView2.getText().toString());
		outState.putInt("to", _toView.getNumber());
		outState.putInt("operation", _operation.getOperationInt());
	}
	
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		int from1 = savedInstanceState.getInt("from1", 0);
		int from2 = savedInstanceState.getInt("from2", 0);
		String number1 = savedInstanceState.getString("number1");
		String number2 = savedInstanceState.getString("number2");
		int to = savedInstanceState.getInt("to", 0);
		int operation = savedInstanceState.getInt("operation");
		
		if(from1 != 0) _fromView1.select(from1);
		if(from2 != 0) _fromView2.select(from2);
		if(to != 0) _toView.select(to);
		if(number1 != null) _numberView1.setText(number1);
		if(number2 != null) _numberView2.setText(number2);	
		_operation.setOperation(operation);
		
		updateResult();
	}
}
