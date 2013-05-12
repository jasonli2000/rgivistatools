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

package com.raygroupintl.m.tool.routine.occurance;

import java.util.EnumSet;

import com.raygroupintl.m.tool.ParseTreeSupply;
import com.raygroupintl.m.tool.routine.RoutineToolParams;

public class OccuranceToolParams extends RoutineToolParams {
	private EnumSet<OccuranceType> includeTypes;
		
	public OccuranceToolParams(ParseTreeSupply parseTreeSupply) {
		super(parseTreeSupply);		
		this.includeTypes = EnumSet.range(OccuranceType.WRITE, OccuranceType.EXECUTE);
	}
	
	public void setIncludeTypes(EnumSet<OccuranceType> includeTypes) {
		this.includeTypes = includeTypes;
	}
	
	public EnumSet<OccuranceType> getIncludeTypes() {
		return this.includeTypes;
	}
}
