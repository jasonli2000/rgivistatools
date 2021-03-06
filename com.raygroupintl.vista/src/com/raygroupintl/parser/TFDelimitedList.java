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

import java.util.Arrays;
import java.util.List;

import com.raygroupintl.parser.annotation.ObjectSupply;

public class TFDelimitedList extends TokenFactory {
	private TFSequence effective;	
	
	public TFDelimitedList(String name) {
		super(name);
	}
		
	private TFDelimitedList(String name, TFSequence effective) {
		super(name);
		this.effective = effective;
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
		TFSequence tailElement = new TFSequence(tailElementName, delimiter, emptyAllowed ? leadingElement : element);
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
	protected TDelimitedList tokenizeOnly(Text text, ObjectSupply objectSupply) throws SyntaxErrorException {
		if (this.effective == null) {
			throw new IllegalStateException("TFDelimitedList.set needs to be called before TFDelimitedList.tokenize");
		} else {
			TSequence internalResult = this.effective.tokenizeOnly(text, objectSupply);
			if (internalResult == null) {
				return null;
			} else {
				Token leadingToken = internalResult.get(0);
				Token tailTokens = internalResult.get(1);
				if (tailTokens == null) {
					Token[] tmpResult = {leadingToken};
					List<Token> list = Arrays.asList(tmpResult);
					return objectSupply.newDelimitedList(list);
				} else {		
					List<Token> list = tailTokens.toList();
					list.add(0, leadingToken);
					int lastIndex = list.size() - 1;
					List<Token> lastToken = list.get(lastIndex).toList();
					if ((lastToken.size() < 2) || (lastToken.get(1) == null)) {
						TSequence newLast = objectSupply.newSequence(2);
						newLast.addToken(lastToken.get(0));
						newLast.addToken(objectSupply.newEmpty());
						list.set(lastIndex, newLast);
					}
					return objectSupply.newDelimitedList(list);
				}
			}
		}
	}
}
