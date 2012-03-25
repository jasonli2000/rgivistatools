package com.raygroupintl.vista.mtoken;

import java.util.List;

import com.raygroupintl.vista.struct.MError;

public class TBasic extends Base {
	private String value;
		
	public TBasic(String value) {
		this.value = value;
	}
		
	@Override
	public String getStringValue() {
		return this.value;
	}
	
	@Override
	public int getStringSize() {
		return this.value.length();
	}
	
	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public List<MError> getErrors() {
		return null;
	}
	
	@Override
	public boolean isError() {
		return false;
	}

	@Override
	public void beautify() {		
	}
}