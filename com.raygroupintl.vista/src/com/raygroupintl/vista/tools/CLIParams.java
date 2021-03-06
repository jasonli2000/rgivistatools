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

package com.raygroupintl.vista.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.raygroupintl.util.CLIParamMgr;
import com.raygroupintl.util.CLIParameter;

public class CLIParams {
	@CLIParameter
	public List<String> reportContent = new ArrayList<String>();
	
	@CLIParameter(names={"-p", "--package"})
	public List<String> packages = new ArrayList<String>();
	
	@CLIParameter(names={"-o", "--output"})
	public String outputFile;
	 
	@CLIParameter(names={"-i", "--input"})
	public String inputFile;
	 
	@CLIParameter(names={"-t", "--type"})
	public String analysisType = "error";
	
	private static void logError(String msg) {
		Logger logger = Logger.getLogger(MRoutineAnalyzer.class.getName());		
		logger.log(Level.SEVERE, msg);
	}
	
	private static void logError(String msg, Throwable t) {
		Logger logger = Logger.getLogger(MRoutineAnalyzer.class.getName());		
		logger.log(Level.SEVERE, msg, t);
	}
	
	public boolean validate() {
		if (this.outputFile == null) {
			CLIParams.logError("No output path is specified.");
			return false;
		}
		if (this.analysisType.equals("api")) {
			if (this.inputFile == null) {
				CLIParams.logError("No input file is specified.");
				return false;				
			}
		}
		return true;
	}
	
	public static CLIParams getInstance(String[] args) {
		try {
			CLIParams options = CLIParamMgr.parse(CLIParams.class, args);
			if (options.validate()) {
				return options;
			} else {
				return null;
			}
		} catch (Throwable t) {
			CLIParams.logError("Invalid options", t);
			return null;
		}
	}
}

