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

package com.raygroupintl.parsergen.rulebased;

import com.raygroupintl.parser.TFConstant;
import com.raygroupintl.parsergen.ruledef.RuleSupplyFlag;

public class FSRConst extends FSRBase {
	private String value;
	private TFConstant factory;
	
	public FSRConst(String value, boolean ignoreCase, RuleSupplyFlag flag) {
		super(flag);
		this.value = value;
		String key = "\"" + this.value + "\"";
		this.factory = new TFConstant(key, this.value, ignoreCase);
	}
	
	@Override
	public String getName() {
		return "\"" + this.value + "\"";
	}
	
	@Override
	public TFConstant getShellFactory() {
		return this.factory;
	}
}