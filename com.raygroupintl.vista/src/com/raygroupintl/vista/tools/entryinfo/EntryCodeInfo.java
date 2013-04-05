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

package com.raygroupintl.vista.tools.entryinfo;

import java.util.Set;

import com.raygroupintl.m.parsetree.data.EntryId;
import com.raygroupintl.output.Terminal;
import com.raygroupintl.output.TerminalFormatter;
import com.raygroupintl.vista.tools.fnds.ToolResult;

public class EntryCodeInfo implements ToolResult {
	public EntryId entryId;
	public String[] formals;
	public AssumedVariablesTR assumedVariables;
	public BasicCodeInfoTR basicCodeInfo;

	public EntryCodeInfo(EntryId entryId, String[] formals, AssumedVariablesTR assumedVariables, BasicCodeInfoTR basicCodeInfo) {
		this.entryId = entryId;
		this.formals = formals;
		this.assumedVariables = assumedVariables;
		this.basicCodeInfo = basicCodeInfo;
	}
	
	public Set<String> getAssumedVariables() {
		return this.assumedVariables.getData();
	}
	
	public BasicCodeInfo getBasicCodeInfo() {
		return this.basicCodeInfo.getData();
	}
	
	@Override
	public void write(Terminal t, TerminalFormatter tf) {
		t.writeEOL(" " + this.entryId.toString2());		
		if ((this.formals == null) && (! this.assumedVariables.isValid()) && (! this.basicCodeInfo.isValid())) {
			t.writeEOL("  ERROR: Invalid entry point");
			return;
		} else {
			t.writeFormatted("FORMAL", this.formals, tf);
			this.assumedVariables.writeVariables(t, tf);
			this.basicCodeInfo.writeInfo(t, tf);
			t.writeEOL();
		}
	}
}
