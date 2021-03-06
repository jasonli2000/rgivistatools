package com.raygroupintl.parser.annotation;

import com.raygroupintl.parser.TFDelimitedList;
import com.raygroupintl.parser.TFSequence;
import com.raygroupintl.parser.TokenFactory;

public class FSREnclosedDelimitedList extends FSRBase {
	private FactorySupplyRule element;
	private FactorySupplyRule delimiter;
	private FactorySupplyRule left;
	private FactorySupplyRule right;
	private boolean empty;
	private boolean none;
	private TFSequence factory;
	
	public FSREnclosedDelimitedList(String name, RuleSupplyFlag flag, FactorySupplyRule element, FactorySupplyRule delimiter, FactorySupplyRule left, FactorySupplyRule right) {
		super(flag);
		this.element = element;
		this.delimiter = delimiter;
		this.left = left;
		this.right = right;
		this.factory = new TFSequence(name);
	}
	
	public void setEmptyAllowed(boolean b) {
		this.empty = b;
	}
	
	
	public void setNoneAllowed(boolean b) {
		this.none = b;
	}
		
	@Override
	public String getName() {
		return this.factory.getName();
	}
	
	@Override
	public boolean update(RulesByName symbols) {
		RulesByNameLocal localSymbols = new RulesByNameLocal(symbols, this);
		String name = this.factory.getName();
		this.element.update(localSymbols);
		this.delimiter.update(localSymbols);
		TokenFactory e = this.element.getTheFactory(localSymbols);
		TokenFactory d = this.delimiter.getTheFactory(localSymbols);
		TFDelimitedList dl = new TFDelimitedList(name);		
		dl.set(e, d, this.empty);
		this.left.update(localSymbols);
		this.right.update(localSymbols);
		TokenFactory l = this.left.getTheFactory(localSymbols);
		TokenFactory r = this.right.getTheFactory(localSymbols);
		TokenFactory[] factories = {l, dl, r};
		boolean[] required = {true, ! this.none, true};	
		this.factory.setFactories(factories, required);				
		return true;		
	}

	@Override
	public TFSequence getShellFactory() {
		return this.factory;
	}
}