package com.raygroupintl.vista.token;

import com.raygroupintl.vista.fnds.IToken;

public abstract class TFSerialORR extends TFSerial {
	@Override
	protected final int getCodeNextIsNull(IToken[] foundTokens) {
		if (foundTokens.length == 1) {
			if (foundTokens[0] == null) {
				return RETURN_NULL;
			} else {
				return this.getErrorCode();
			}
		}
		if (foundTokens.length == 2) {
			return this.getErrorCode();
		}
		return CONTINUE;
	}
	
	@Override
	protected final int getCodeStringEnds(IToken[] foundTokens) {
		if (foundTokens.length < 3) {
			return this.getErrorCode();
		} else {
			return 0;
		}
	}
}