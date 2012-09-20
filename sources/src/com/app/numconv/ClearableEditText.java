package com.app.numconv;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.TextKeyListener;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class ClearableEditText extends RelativeLayout {

	public static interface OnClearListener {
		public void onClear();
	}
	
	private TextKeyListener _keyListener = new TextKeyListener(TextKeyListener.Capitalize.NONE, false) {
		public int getInputType() {
			if(!_customInputType) return InputType.TYPE_NULL;
			return _editText.getInputType();
		}

		public boolean onKeyDown(View view, Editable content, int keyCode, KeyEvent event) {
			Log.d("keyDownContent", "content = " + content.toString());
			boolean result = super.onKeyDown(view, content, keyCode, event);
			//if(_onKeyListener != null) _onKeyListener.onKey(view, keyCode, event);
			return result;
		}
		
		public boolean backspace(View view, Editable content, int keyCode, KeyEvent event) {
			return super.backspace(view, content, keyCode, event);
		}
	};
	
	private TextWatcher _textWatcher = new TextWatcher() {
		public void afterTextChanged(Editable s) {
			boolean hasDot = false;
			for(int i = 0; i < s.length(); i++) {
				if(s.charAt(i) == '-' && i > 0) s.delete(i, i+1);
				else if(s.charAt(i) == '.') {
					if(hasDot) s.delete(i, i+1);
					else hasDot = true;
				} else if(s.charAt(i) < '0' || (s.charAt(i) > '9' && s.charAt(i) < 'a') || 
						s.charAt(i) > 'z') {
					s.delete(i, i+1);
				}
			}
			
			if(_onKeyListener != null) _onKeyListener.onKey(ClearableEditText.this, 0, null);
		}

		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}
	};
	
	private EditText _editText;
	private ImageView _clearButton;
	
	private OnClearListener _onClearListener;
	private OnKeyListener _onKeyListener;
	private boolean _customInputType;
	
	public ClearableEditText(Context context) {
		super(context);
		initialize(context, null);
	}
	
	public ClearableEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(context, attrs);
	}
	
	public ClearableEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initialize(context, attrs);
	}

	private void initialize(Context context, AttributeSet attrs) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.clearable_edittext, this, true);
		
		_customInputType = false;
		_editText = (EditText) findViewById(R.id.editText);
		_clearButton = (ImageView) findViewById(R.id.clear);
		
		TypedArray params = context.obtainStyledAttributes(attrs, R.styleable.ClearableEditText);
		
		_editText.setHint(params.getString(R.styleable.ClearableEditText_android_hint));
		_editText.setText(params.getString(R.styleable.ClearableEditText_android_text));
		
		_editText.setKeyListener(_keyListener);
		_editText.addTextChangedListener(_textWatcher);
		
		params.recycle();
		
		_clearButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				_editText.setText("");
				if(_onClearListener != null)
					_onClearListener.onClear();
			}
		});
	}
	
	public Editable getText() {
		return _editText.getText();
	}
	
	public void setText(CharSequence text) {
		_editText.setText(text);
	}
	
	public void setEnabled(boolean enabled) {
		_editText.setEnabled(enabled);
		_clearButton.setEnabled(enabled);
	}
	
	public boolean isFocused() {
		return getFocusedChild() != null;
	}
	
	public EditText getEditText() {
		return _editText;
	}
	
	public void setOnKeyListener(OnKeyListener l) {
		_onKeyListener = l;
	}
	
	public void setOnClearListener(OnClearListener l) {
		_onClearListener = l;
	}
	
	public void setInputType(int type) {
		_editText.setInputType(type | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
		_customInputType = true;
	}
	
	public void setOnFocusChangeListener(OnFocusChangeListener l) {
		_editText.setOnFocusChangeListener(l);
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return _editText.onKeyDown(keyCode, event);
	}
	
	public void setSolidLeftStyle(boolean yes) {
		_editText.setBackgroundResource(
				yes ? R.drawable.left_edittext_background : R.drawable.edittext_background);
		_editText.setPadding(_editText.getPaddingLeft(), _editText.getPaddingTop(), 
				(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, 
						getResources().getDisplayMetrics()), _editText.getPaddingBottom());
	}
}
