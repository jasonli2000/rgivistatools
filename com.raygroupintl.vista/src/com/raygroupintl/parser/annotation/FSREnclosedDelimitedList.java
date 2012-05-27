package com.raygroupintl.parser.annotation;

import com.raygroupintl.parser.TFDelimitedList;
import com.raygroupintl.parser.TFSequence;
import com.raygroupintl.parser.TokenFactory;

public class FSREnclosedDelimitedList extends FSRBase {
	private static class TokenFactoriesByNamesAndSelf implements TokenFactoriesByName {
		private TokenFactoriesByName factories;
		private TokenFactory self;
		
		public TokenFactoriesByNamesAndSelf(TokenFactoriesByName factories, TokenFactory self) {
			this.factories = factories;
			this.self = self;
		}
		
		@Override
		public TokenFactory get(String name) {
			if (self.getName().equals(name)) {
				return self;
			} else {
				return this.factories.get(name);
			}
		}
		
		@Override
		public void put(String name, TokenFactory f) {
			this.factories.put(name, f);
		}
		
		@Override
		public boolean isInitialized(TokenFactory f) {
			if (f == this.self) {
				return true;
			} else {
				return f.isInitialized();
			}
		}
	}

	private FactorySupplyRule element;
	private FactorySupplyRule delimiter;
	private FactorySupplyRule left;
	private FactorySupplyRule right;
	private boolean empty;
	private boolean none;
	
	public FSREnclosedDelimitedList(FactorySupplyRule element, FactorySupplyRule delimiter, FactorySupplyRule left, FactorySupplyRule right, boolean required) {
		super(required);
		this.element = element;
		this.delimiter = delimiter;
		this.left = left;
		this.right = right;
	}
	
	public void setEmptyAllowed(boolean b) {
		this.empty = b;
	}
	
	
	public void setNoneAllowed(boolean b) {
		this.none = b;
	}
		
	@Override
	public TFSequence getFactory(String name, TokenFactoriesByName symbols) {
		TFSequence result = new TFSequence(name);
		TokenFactoriesByNamesAndSelf localSymbols = new TokenFactoriesByNamesAndSelf(symbols, result);
		TokenFactory e = this.element.getFactory(name + ".element", localSymbols);
		if (e == null) {
			return null;
		}
		TokenFactory d = this.delimiter.getFactory(name + ".delimiter", symbols);
		TFDelimitedList dl = new TFDelimitedList(name);		
		dl.set(e, d, this.empty);
		TokenFactory l = this.left.getFactory(name + ".left", symbols);
		TokenFactory r = this.right.getFactory(name + ".right", symbols);
		TokenFactory[] factories = {l, dl, r};
		boolean[] required = {true, ! this.none, true};
		result.setFactories(factories, required);
		return result;
	}

	@Override
	public TFSequence getTopFactory(String name, TokenFactoriesByName symbols, boolean asShell) {
		if (! asShell) {
			return this.getFactory(name, symbols);
		} else {			
			return new TFSequence(name);
		}
	}
}