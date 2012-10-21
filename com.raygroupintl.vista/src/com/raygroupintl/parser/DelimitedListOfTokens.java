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

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.raygroupintl.struct.IterableSingle;
import com.raygroupintl.struct.IterableSingleAndList;
import com.raygroupintl.struct.SingleAndListIterator;
import com.raygroupintl.struct.SingleIterator;

public class DelimitedListOfTokens extends CollectionOfTokens implements Iterable<Token> {
	private static class TDelimitedListIterator extends SingleAndListIterator<Token> {
		public TDelimitedListIterator(Token leading, Iterable<Token> iterable) {
			super(leading, iterable);
		}
		
		@Override
		public Token next() throws NoSuchElementException {
			if (this.inInitialState()) {
				return super.next();
			} else {
				//@SuppressWarnings("unchecked")
				Tokens ts = (Tokens) super.next();
				return ts.getToken(1);
			}
		}
	}

	private Token leadingToken;
	private Tokens remainingTokens;
	
	public DelimitedListOfTokens(Token leadingToken, Tokens tailTokens) {
		this.leadingToken = leadingToken;
		this.remainingTokens = tailTokens;
	}

	@Override
	public void setToken(int index, Token token) {
		if (index == 0) {
			this.leadingToken = token;
		} else {
			this.remainingTokens.setToken(index-1, token);
		}
	}

	@Override
	public void addToken(Token token) {
		if (this.remainingTokens == null) {
			this.remainingTokens = new ListOfTokens();
		}
		this.remainingTokens.addToken(token);
	}

	@Override
	public Token getToken(int index) {
		if (index == 0) {
			return this.leadingToken;
		} else if (this.remainingTokens != null) {
			return this.remainingTokens.getToken(index-1);
		} else {
			return null;
		}
	}

	@Override
	public int size() {
		return 1 + (this.remainingTokens == null ? 0 : this.remainingTokens.size());
	}

	@Override
	public boolean hasToken() {
		return true;
	}
	
	@Override
	public TextPiece toValue() {	
		TextPiece result = new TextPiece();
		result.add(this.leadingToken.toValue());
		if (this.remainingTokens != null) for (Token t : this.remainingTokens.toIterable()) if (t != null) {
			result.add(t.toValue());
		}		
		return result;
	}

	public Iterable<Token> toLogicalIterable() {
		return this;
	}
	
	public Iterable<Token> toIterable() {
		if (this.remainingTokens == null) {
			return new IterableSingle<Token>(this.leadingToken);
		} else {
			return new IterableSingleAndList<Token>(this.leadingToken, this.remainingTokens.toIterable());
		}
	}
	
	@Override
	public Iterator<Token> iterator() {
		if (this.remainingTokens == null) {
			return new SingleIterator<Token>(this.leadingToken);
		} else {
			return new TDelimitedListIterator(this.leadingToken, this.remainingTokens.toIterable());
		}
	}
	
	public Token getLogicalToken(int index) {
		if (index == 0) {
			return this.getToken(0);
		} else {
			Tokens ts = this.getTokens(index);
			return ts.getToken(1);
		}
	}
}
