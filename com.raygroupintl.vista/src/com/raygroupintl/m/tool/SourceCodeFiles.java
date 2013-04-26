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

package com.raygroupintl.m.tool;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class SourceCodeFiles implements SourceCodeSupply {
	private Map<String, String> filesByRoutineName = new HashMap<String, String>();
	
	public void put(String routineName, String fileName) {
		this.filesByRoutineName.put(routineName, fileName);		
	}
	
	@Override
	public InputStream getStream(String routineName) {
		String fileName = this.filesByRoutineName.get(routineName);
		if (fileName != null) {
			try {
				InputStream is = new FileInputStream(fileName);
				return is;
			} catch(IOException e) {
				return null;
			}
		}
		return null;
	}
}
