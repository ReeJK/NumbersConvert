package com.app.numconv;

import android.app.Application;
import android.inputmethodservice.Keyboard;

public class ThisApplication extends Application {

	private static final int _InputKeyboardLayouts[] = { 
		R.xml.keyboard02, R.xml.keyboard03, R.xml.keyboard04, R.xml.keyboard05, R.xml.keyboard06, 
		R.xml.keyboard07, R.xml.keyboard08, R.xml.keyboard09, R.xml.keyboard10, R.xml.keyboard11, 
		R.xml.keyboard12, R.xml.keyboard13, R.xml.keyboard14, R.xml.keyboard15, R.xml.keyboard16, 
		R.xml.keyboard17, R.xml.keyboard18, R.xml.keyboard19, R.xml.keyboard20, R.xml.keyboard21, 
		R.xml.keyboard22, R.xml.keyboard23, R.xml.keyboard24, R.xml.keyboard25, R.xml.keyboard26, 
		R.xml.keyboard27, R.xml.keyboard28, R.xml.keyboard29, R.xml.keyboard30, R.xml.keyboard31, 
		R.xml.keyboard32, R.xml.keyboard33, R.xml.keyboard34, R.xml.keyboard35, R.xml.keyboard36
	};
	
	private Keyboard _inputKeyboards[] = null;
	
	public Keyboard getKeyboard(int numberSystem) {
		if(numberSystem < 2 || numberSystem > 36) return null;
		if(_inputKeyboards == null) createKeyboards();
		return _inputKeyboards[numberSystem - 2];
	}
	
	private void createKeyboards() {
		_inputKeyboards = new Keyboard[_InputKeyboardLayouts.length];
		for(int i = 0; i < _InputKeyboardLayouts.length; i++) {
			_inputKeyboards[i] = new Keyboard(getApplicationContext(), _InputKeyboardLayouts[i]);
		}
	}
}
