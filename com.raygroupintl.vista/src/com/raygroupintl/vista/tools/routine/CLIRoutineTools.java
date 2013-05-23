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

package com.raygroupintl.vista.tools.routine;

import java.util.Map;

import com.raygroupintl.vista.tools.CLIParams;
import com.raygroupintl.vista.tools.Tool;
import com.raygroupintl.vista.tools.Tools;

public class CLIRoutineTools extends Tools {
	public CLIRoutineTools(String name) {
		super(name);
	}

	@Override
	protected void updateTools(Map<String, MemberFactory> tools) {
		tools.put("entry", new MemberFactory() {				
			@Override
			public Tool getInstance(CLIParams params) {
				return new CLIEntryListTool(params);
			}
		});
		tools.put("fanout", new MemberFactory() {				
			@Override
			public Tool getInstance(CLIParams params) {
				return new CLIFanoutTool(params);
			}
		});
		tools.put("fanin", new MemberFactory() {				
			@Override
			public Tool getInstance(CLIParams params) {
				return new CLIFaninTool(params);
			}
		});
		tools.put("topentries", new MemberFactory() {				
			@Override
			public Tool getInstance(CLIParams params) {
				return new CLITopEntriesTool(params);
			}
		});
		tools.put("error", new MemberFactory() {				
			@Override
			public Tool getInstance(CLIParams params) {
				return new CLIErrorTool(params);
			}
		});
		tools.put("occurance", new MemberFactory() {				
			@Override
			public Tool getInstance(CLIParams params) {
				return new CLIOccuranceTool(params);
			}
		});
	}
}