package com.raygroupintl.bnf;

import com.raygroupintl.fnds.IToken;
import com.raygroupintl.fnds.ITokenFactory;

public abstract class TFChar implements ITokenFactory {
	protected abstract boolean isValid(char ch);
	
	@Override
	public IToken tokenize(String line, int fromIndex) {
		if (fromIndex < line.length()) {
			char ch = line.charAt(fromIndex);
			if (this.isValid(ch)) {
				return new TChar(ch);
			}
		}
		return null;
	}
	
	public static final TFChar DOT = new TFChar() {		
		@Override
		protected boolean isValid(char ch) {
			return ch == '.';
		}
	};

	public static final TFChar LEFT_PAR = new TFChar() {		
		@Override
		protected boolean isValid(char ch) {
			return ch == '(';
		}
	};
	
	public static final TFChar RIGHT_PAR = new TFChar() {		
		@Override
		protected boolean isValid(char ch) {
			return ch == ')';
		}
	};
	
	public static final TFChar COLON = new TFChar() {		
		@Override
		protected boolean isValid(char ch) {
			return ch == ':';
		}
	};
	
	public static final TFChar DOLLAR = new TFChar() {		
		@Override
		protected boolean isValid(char ch) {
			return ch == '$';
		}
	};

	public static final TFChar AMPERSAND = new TFChar() {		
		@Override
		protected boolean isValid(char ch) {
			return ch == '$';
		}
	};

	public static final TFChar SLASH = new TFChar() {		
		@Override
		protected boolean isValid(char ch) {
			return ch == '/';
		}
	};

	public static final TFChar COMMA = new TFChar() {		
		@Override
		protected boolean isValid(char ch) {
			return ch == ',';
		}
	};
}