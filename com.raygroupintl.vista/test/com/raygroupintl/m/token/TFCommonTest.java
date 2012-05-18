package com.raygroupintl.m.token;

import static org.junit.Assert.fail;

import junit.framework.Assert;

import com.raygroupintl.m.struct.MError;
import com.raygroupintl.m.token.TSyntaxError;
import com.raygroupintl.parser.SyntaxErrorException;
import com.raygroupintl.parser.Text;
import com.raygroupintl.parser.Token;
import com.raygroupintl.parser.TokenFactory;
import com.raygroupintl.parser.TokenStore;

public class TFCommonTest {
	public static void validTokenCheck(Token t, String v) {
		Assert.assertEquals(v, t.getStringValue());
		Assert.assertEquals(v.length(), t.getStringSize());		
	}
	
	private static void errorCheck(SyntaxErrorException e, int location, String v) {
		Token t = new TSyntaxError(0, v.substring(location), location);
		int totalSize = t.getStringSize();
		String totalString = t.getStringValue();
		
		for (TokenStore ts : e.getTokenStores()) {
			for (int i=ts.size()-1; i>=0; --i) {
				Token n = ts.get(i);
				if (n != null) {
					totalSize += n.getStringSize();
					totalString = n.getStringValue() + totalString;
				}
			}
		}
		Assert.assertEquals(v, totalString);
		Assert.assertEquals(v.length(), totalSize);		
	}
	
	static void errorCheck(SyntaxErrorException e, int location, String v, int errorCode) {
		Assert.assertEquals(errorCode,  e.getCode() == 0 ? MError.ERR_GENERAL_SYNTAX : e.getCode());
		errorCheck(e, location, v);
	}
			
	static void validCheck(TokenFactory f, String v, boolean checkWithSpace) {
		try {
			Text text = new Text(v);
			Token t = f.tokenize(text);
			validTokenCheck(t, v);
			if (checkWithSpace) {
				validCheck(f, v + " ", v);
			}
		} catch(SyntaxErrorException e) {
			fail("Exception: " + e.getMessage());			
		}
	}

	static void validCheck(TokenFactory f, String v) {
		validCheck(f, v, true);
	}

	static void nullCheck(TokenFactory f, String v) {
		try {
			Text text = new Text(v);
			Token t = f.tokenize(text);
			Assert.assertNull(t);
		} catch(SyntaxErrorException e) {
			fail("Unexpected exception.");
		}
	}
	
	static void validCheckNS(TokenFactory f, String v) {
		validCheck(f, v, false);
	}

	static void errorCheck(TokenFactory f, String v) {
		Text text = new Text(v);
		try {
			f.tokenize(text);
			fail("Expected exception did not fire.");
		} catch(SyntaxErrorException e) {
			errorCheck(e, text.getIndex(), v);
		}
	}

	static void errorCheck(TokenFactory f, String v, int errorCode, int location) {
		Text text = new Text(v);
		try {
			f.tokenize(text);
			fail("Expected exception did not fire.");
		} catch(SyntaxErrorException e) {
			int actualLocation = text.getIndex();
			Assert.assertEquals(location, actualLocation);
			errorCheck(e, text.getIndex(), v, errorCode);
			errorCheck(f, v + " ", v + " ", errorCode);		
		}
	}

	static void validCheck(TokenFactory f, String v, String compare) {
		try {
			Text text = new Text(v);
			Token t = f.tokenize(text);
			validTokenCheck(t, compare);
		} catch(SyntaxErrorException e) {
			fail("Exception: " + e.getMessage());			
		}
	}

	static void errorCheck(TokenFactory f, String v, String compare, int errorCode) {
		Text text = new Text(v);
		try {
			f.tokenize(text);
			fail("Expected exception did not fire.");
		} catch(SyntaxErrorException e) {
			errorCheck(e, text.getIndex(), compare, errorCode);
		}
	}
}