package com.raygroupintl.vista.mtoken;

import com.raygroupintl.vista.fnds.IToken;
import com.raygroupintl.vista.token.TCopy;

public class TGlobalNaked extends TCopy {
	public TGlobalNaked(IToken source) {
		super(source);
	}

	@Override
	public String getStringValue() {
		return '^' + super.getStringValue();
	}

	@Override
	public int getStringSize() {
		return 1 + super.getStringSize();
	}	
}
