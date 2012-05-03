package com.raygroupintl.m.token;

import com.raygroupintl.bnf.TCopy;
import com.raygroupintl.fnds.IToken;

public class TInParantheses extends TCopy {
	public TInParantheses(IToken source) {
		super(source);
	}
	
	@Override
	public String getStringValue() {
		return "(" + super.getStringValue() + ")";
	}
	
	@Override
	public int getStringSize() {
		return 2 + super.getStringSize();
	}	
}