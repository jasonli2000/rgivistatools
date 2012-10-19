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

package com.raygroupintl.parsergen.ruledef;

import com.raygroupintl.parser.Token;
import com.raygroupintl.parser.Tokens;

public class TChoiceOfSymbols extends TDelimitedList implements RuleSupplies {
	public TChoiceOfSymbols(Token leadingToken, Tokens tailTokens) {
		super(leadingToken, tailTokens);
	}
		
	@Override
	public void accept(RuleDefinitionVisitor visitor, String name, RuleSupplyFlag flag) {
		if (this.size() == 1) {
			((RuleSupply) this.getToken(0)).accept(visitor, name, flag);	
		} else {
			visitor.visitChoiceOfSymbols(this, name, flag);
		}
	}
	
	@Override
	public int getSize() {
		return this.size();
	}
	
	@Override
	public void acceptElement(RuleDefinitionVisitor visitor, int index, String name, RuleSupplyFlag flag) {
		RuleSupply rs = (RuleSupply) this.getLogicalToken(index);
		rs.accept(visitor, name, flag);
	}
}