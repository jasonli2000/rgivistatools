//---------------------------------------------------------------------------
// Copyright 2012 Ray Group International
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//---------------------------------------------------------------------------

package com.raygroupintl.parser;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;

import com.raygroupintl.parser.annotation.AdapterSupply;

public class TFDelimitedList extends TFBasic {
	private static final DelimitedListAdapter DEFAULT_ADAPTER = new DelimitedListAdapter() {		
		@Override
		public Token convert(List<Token> tokens) {
			return new TDelimitedList(tokens);
		}
	}; 
	
	private TFSequence effective;	
	private DelimitedListAdapter adapter;
	
	public TFDelimitedList(String name) {
		this(name, DEFAULT_ADAPTER);
	}
	
	public TFDelimitedList(String name, DelimitedListAdapter adapter) {
		super(name);
		this.adapter = adapter == null ? DEFAULT_ADAPTER : adapter;
	}
		
	private TFDelimitedList(String name, TFSequence effective, DelimitedListAdapter adapter) {
		super(name);
		this.adapter = adapter == null ? DEFAULT_ADAPTER : adapter;
	}
		
	@Override
	public void copyWoutAdapterFrom(TFBasic rhs) {
		if (rhs instanceof TFDelimitedList) {
			TFDelimitedList rhsCasted = (TFDelimitedList) rhs;
			this.effective = rhsCasted.effective;
		} else {
			throw new IllegalArgumentException("Illegal attemp to copy from " + rhs.getClass().getName() + " to " + TFDelimitedList.class.getName());
		}
	}
	
	@Override
	public TFBasic getCopy(String name) {
		return new TFDelimitedList(name, this.effective, this.adapter);
	}

	@Override
	public boolean isInitialized() {
		return this.effective != null;
	}

	public void setAdapter(DelimitedListAdapter adapter) {
		this.adapter = adapter;
	}
	
	private Token getToken(List<Token> tokens) {
		return this.adapter.convert(tokens);
	}
	
	private TokenFactory getLeadingFactory(TokenFactory element, TokenFactory delimiter, boolean emptyAllowed) {
		if (emptyAllowed) {
			String elementName = this.getName() + "." + element.getName();
			String emptyName = this.getName() + "." + "empty";
			return new TFChoiceBasic(elementName, element, new TFEmpty(emptyName, delimiter));	
		} else {
			return element;
		}
	}
	
	public void set(TokenFactory element, TokenFactory delimiter, boolean emptyAllowed) {
		TokenFactory leadingElement = this.getLeadingFactory(element, delimiter, emptyAllowed);
		String tailElementName = this.getName() + "." + "tailelement";
		TFSequence tailElement = new TFSequence(tailElementName, delimiter, element);
		tailElement.setRequiredFlags(true, !emptyAllowed);
		String tailListName = this.getName() + "." + "taillist";
		TokenFactory tail = new TFList(tailListName, tailElement);
		String name = this.getName() + "." + "effective";
		this.effective = new TFSequence(name, leadingElement, tail);
		this.effective.setRequiredFlags(true, false);
	}
	
	public void set(TokenFactory element, TokenFactory delimiter) {
		this.set(element, delimiter, false);
	}

	@Override
	public Token tokenize(Text text, AdapterSupply adapterSupply) throws SyntaxErrorException {
		if (this.effective == null) {
			throw new IllegalStateException("TFDelimitedList.set needs to be called before TFDelimitedList.tokenize");
		} else {
			TSequence internalResult = (TSequence) this.effective.tokenize(text, adapterSupply);
			if (internalResult == null) {
				return null;
			} else {
				Token leadingToken = internalResult.get(0);
				Token tailTokens = internalResult.get(1);
				if (tailTokens == null) {
					Token[] tmpResult = {leadingToken};
					return this.getToken(Arrays.asList(tmpResult));	
				} else {		
					TList result = (TList) tailTokens;
					List<Token> list = result.getList();
					list.add(0, leadingToken);
					return this.getToken(list);
				}
			}
		}
	}

	@Override
	public void setTargetType(Class<? extends Token> cls) {
		final Constructor<? extends Token> constructor = getConstructor(cls, List.class, TDelimitedList.class);
		this.adapter = new DelimitedListAdapter() {			
			@Override
			public Token convert(List<Token> tokens) {
				try{
					return (Token) constructor.newInstance(tokens);
				} catch (Exception e) {	
					return null;
				}
			}
		};
	}
	
	@Override
	public void setAdapter(Object adapter) {
		if (adapter instanceof DelimitedListAdapter) {
			this.adapter = (DelimitedListAdapter) adapter;					
		} else {
			throw new IllegalArgumentException("Wrong adapter type " + adapter.getClass().getName());
		}
	}	
}
