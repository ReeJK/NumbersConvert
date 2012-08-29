package com.app.numconv;

import java.util.ArrayList;

public class Converter {
	
	public static String convert(String input, int from, int to) {
		return Converter.convert(input, from, to, false);
	}
	
	public static String convert(String input, int from, int to, boolean noDots) {
		return new Converter(input, from).convertTo(to, noDots);
	}
	
	private Integer _inputInteger[];
	private Integer _inputFractional[];
	private int _inputSystem;
	private boolean _minus;

	private Converter(String input, int system) {
		_inputSystem = system;
		
		_minus = input.charAt(0) == '-';
		
		int i;
		ArrayList<Integer> inputList = new ArrayList<Integer>();
		for(i = _minus ? 1 : 0; i < input.length(); i++) {
			if(input.charAt(i) == '.') break;
			inputList.add(charToInt(input.charAt(i)));
		}
		_inputInteger = inputList.toArray(new Integer[0]);
		
		inputList.clear();
		for(++i; i < input.length(); i++) {
			inputList.add(charToInt(input.charAt(i)));
		}
		_inputFractional = inputList.toArray(new Integer[0]);
	}

	private int charToInt(char c) {
		if(c >= '0' && c <= '9' && (c - '0') < _inputSystem)
			return c - '0';
		else if(c >= 'a' && c <= 'z' && 10 + (c - 'a') < _inputSystem)
			return c - 'a' + 10;
		else throw new NumberFormatException("Symbol '" + c + "' out of number system bounds");
	}

	private char intToChar(int c) {
		if(c >= 0 && c <= 9 )
			return (char)(c + '0');
		return (char)(c + 'a' - 10);
	}
	
	private int nextIntegerNumber(int system) {
		int temp = 0;
		
		for(int i = 0; i < _inputInteger.length; i++) {
			temp = temp * _inputSystem + _inputInteger[i];
			_inputInteger[i] = temp / system;
			temp = temp % system;
		}
		
		return temp;
	}

	private boolean isIntegerZero() {
		for(int i = 0; i < _inputInteger.length; i++)
			if(_inputInteger[i] != 0) return false;
		return true;
	}
	
	private int nextFractionalNumber(int system) {
		int result = 0;
		// .125 # 2 #  #  #  #  #  #
		for(int i = 0; i < _inputFractional.length; i++)
			_inputFractional[i] = _inputFractional[i] * system;
		// 1 #  #  #  #  #  #  #  #
		for(int i = _inputFractional.length - 1; i >= 0; i--) {
			if(i == 0) result = _inputFractional[i] / _inputSystem;
			else _inputFractional[i - 1] += _inputFractional[i] / _inputSystem;
			_inputFractional[i] = _inputFractional[i] % _inputSystem;
		}
		
		return result;
	}

	private boolean isFractionalZero() {
		for(int i = 0; i < _inputFractional.length; i++)
			if(_inputFractional[i] != 0) return false;
		return true;
	}

	private String convertTo(int system, boolean noDots) {
		ArrayList<Integer> result = new ArrayList<Integer>();
		
		do result.add(nextIntegerNumber(system)); while( !isIntegerZero() );
		
		StringBuilder resultBuilder = new StringBuilder();
		
		if(_minus) resultBuilder.append('-');
		
		for(int i = result.size() - 1; i >= 0; i--)
			resultBuilder.append(intToChar(result.get(i)));
		
		if(!isFractionalZero()) {
			StringBuilder fractionBuilder = new StringBuilder(".");
			for(int i = 0; i < 16 && !isFractionalZero(); i++) {
				fractionBuilder.append(intToChar(nextFractionalNumber(system)));
			}
			
			if(!isFractionalZero() && !noDots) {
				fractionBuilder.delete(9, 20);
				fractionBuilder.append("...");
			}
			
			resultBuilder.append(fractionBuilder.toString());
		}
		
		return resultBuilder.toString();
	}
};