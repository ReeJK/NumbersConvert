package com.app.numconv;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NumberPickerView extends LinearLayout {
	
	public static interface OnChangeListener {
		void onChange(View v, int oldValue, int newValue);
	}
	
	private static int _sDefaultDelay = 300;
	private int _buttonPressed = 0;
	private Handler _valueChanger = new Handler() {
		public void handleMessage(Message msg) {
			select(_current + _buttonPressed);
			if(_current + _buttonPressed * _interval >= _min && 
					_current + _buttonPressed * _interval <= _max) 
				sendEmptyMessageDelayed(0, _sDefaultDelay);
		}
	};
	
	private static Handler _sDisableHandler = new Handler() {
		public void handleMessage(Message msg) {
			View v = (View) msg.obj;
			v.setPressed(false);
			v.clearFocus();
			v.setEnabled(msg.arg1 == 1);
			Log.d("Disable", msg.what + " " + msg.arg1 + " " + msg.arg2 + " " + v);
		}
	};
	
	private TextView _hintView;
	private TextView _numberView;
	private ImageButton _downButton;
	private ImageButton _upButton;
	
	private int _min, _max;
	private int _interval;
	private int _current;
	
	private OnChangeListener _onChangeListener;
	
	private static int _sHotSystems[] = {0, 0, 0, 0, 0, 0, 0};
	
	public NumberPickerView(Context context) {
		super(context);
		initialize(context, null);
	}
	
	public NumberPickerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(context, attrs);
	}

	private void initialize(Context context, AttributeSet attrs) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.number_picker, this, true);
		
		_hintView = (TextView) findViewById(R.id.hintView);
		_numberView = (TextView) findViewById(R.id.numberView);
		_downButton = (ImageButton) findViewById(R.id.numberDownButton);
		_upButton = (ImageButton) findViewById(R.id.numberUpButton);
		
		TypedArray params = context.obtainStyledAttributes(attrs, R.styleable.NumberPickerView);
	
		String hint = params.getString(R.styleable.NumberPickerView_android_hint);
		if(hint != null) _hintView.setText(hint);
		
		setInterval(params.getInteger(R.styleable.NumberPickerView_interval, 1));
		setRange(params.getInteger(R.styleable.NumberPickerView_min, 0),
				params.getInteger(R.styleable.NumberPickerView_max, 9));
		
		params.recycle();
		
		if(params.hasValue(R.styleable.NumberPickerView_select))
			select(params.getInteger(R.styleable.NumberPickerView_select, 1));
		
		Log.d("Buttons", "down " + _downButton + " up " + _upButton);
		
		_downButton.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN) {
					_buttonPressed = -1;
					_valueChanger.sendEmptyMessageDelayed(0, _sDefaultDelay);
				} else if(event.getAction() == MotionEvent.ACTION_UP) {
					select(_current + _buttonPressed);
					_buttonPressed = 0;
					_valueChanger.removeMessages(0);
				}
				
				return false;
			}
		});
		
		_upButton.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN) {
					_buttonPressed = 1;
					_valueChanger.sendEmptyMessageDelayed(0, _sDefaultDelay);
				} else if(event.getAction() == MotionEvent.ACTION_UP) {
					select(_current + _buttonPressed);
					_buttonPressed = 0;
					_valueChanger.removeMessages(0);
				}
				
				return false;
			}
		});
		
		_numberView.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				createDialog();
			}
		});
		
		if(_sHotSystems[0] == 0) {
			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
			if(pref != null) {
				for(int i = 0; i < _sHotSystems.length; i++)
					_sHotSystems[i] = pref.getInt("hot" + i, 0);
			}
		}
	}
	
	public int getNumber() {
		return _current;
	}
	
	public void setRange(int min, int max) {
		_min = min;
		_max = max;
		
		if(_max < _min) {
			int temp = _max;
			_max = _min;
			_min = temp;
		}
		
		select(_current);
	}
	
	public void setInterval(int interval) {
		_interval = interval;
		if(_interval < 1) _interval = 1;
	}
	
	public void select(int number) {
		int oldValue = _current;
		
		if(number < _min) _current = _min;
		else if(number > _max) _current = _max;
		else _current = number;
		if(_interval > 1) _current -= (_current - _min) % _interval; 
		_numberView.setText(String.valueOf(_current));
		
		if(_onChangeListener != null) 
			_onChangeListener.onChange(this, oldValue, _current);
		
		Log.d("selectFor", _downButton + " : " + _upButton);
		Log.d("select", _min + " <= " + _current + " <= " + _max + " : " + _interval);
		
		_sDisableHandler.sendMessageDelayed(Message.obtain(_sDisableHandler, 1, 
				_current + _interval <= _max ? 1 : 0, 0, _upButton), 50);
		
		_sDisableHandler.sendMessageDelayed(Message.obtain(_sDisableHandler, 1, 
				_current - _interval >= _min ? 1 : 0, 0, _downButton), 50);
	}
	
	public void setEnabled(boolean enabled) {
		_numberView.setEnabled(enabled);
		_upButton.setEnabled(enabled && _current + _interval <= _max);
		_downButton.setEnabled(enabled && _current - _interval >= _min);
	}
	
	public void setHint(CharSequence hint) {
		_hintView.setText(hint);
	}
	
	public void setOnChangeListener(OnChangeListener l) {
		_onChangeListener = l;
	}
	
	public void addHot() {
		addHot(getContext(), _current);
	}
	
	private static void addHot(Context context, int system) {
		if(system == 2 || system == 8 || system == 10 || system == 16) return;
		
		for(int i : _sHotSystems) if(i == system) return;
		
		for(int i = 0; i < _sHotSystems.length; i++)
			if(_sHotSystems[i] == 0) {
				_sHotSystems[i] = system;
				PreferenceManager.getDefaultSharedPreferences(context).edit()
				.putInt("hot" + i, system).commit();
				return;
			}
		
		for(int i = 0; i < _sHotSystems.length - 1; i++) {
			_sHotSystems[i] = _sHotSystems[i + 1];
		}
		
		_sHotSystems[_sHotSystems.length - 1] = system;
		
		Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
		for(int i = 0; i < _sHotSystems.length; i++) {
			editor.putInt("hot" + (i), _sHotSystems[i]);
		}
		editor.commit();
	}
	
	private void createDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		builder.setTitle(R.string.select_system_dialog_title);
		builder.setItems(getHotItems(), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				int length = 3;
				for(int i : _sHotSystems) if(i > 0) length++;
				Log.d("which", "w = " + which + " / " + length);
				switch(length - which) {
				case 0: select(16); break;
				case 1: select(10); break;
				case 2: select(8); break;
				case 3: select(2); break;
				default: select(_sHotSystems[length - 4 - which]); break;
				}
			}
		});
		
		builder.create().show();
	}
	
	private CharSequence[] getHotItems() {
		int length = 4;
		for(int i : _sHotSystems) if(i > 0) length++;
		CharSequence result[] = new CharSequence[length];
		
		for(int i = 0; i < length; i++) {
			switch(i) {
			case 0: result[length - 1 - i] = "16"; break;
			case 1: result[length - 1 - i] = "10"; break;
			case 2: result[length - 1 - i] = "8"; break;
			case 3: result[length - 1 - i] = "2"; break;
			default: 
				result[length - 1 - i] = String.valueOf(_sHotSystems[i - 4]); 
				break;
			}
		}
		
		return result;
	}
	
	public void setSolidRightStyle(boolean yes) {
		_upButton.setBackgroundResource(
				yes ? R.drawable.solid_button_background : R.drawable.right_button_background);
	}
}
