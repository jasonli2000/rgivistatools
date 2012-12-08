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

package com.raygroupintl.m.parsetree.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Blocks<T> {
	private Map<String, Block<T>> blocks = new HashMap<String, Block<T>>();
	private Blocks<T> parent;
	
	public Blocks() {		
	}
	
	public Blocks(Blocks<T> parent) {
		this.parent = parent;
	}
	
	public Set<String> getTags() {
		return this.blocks.keySet();
	}
	
	public Block<T> get(String name) {
		Block<T> result = this.blocks.get(name);
		if ((result == null) && (this.parent != null)) {
			return this.parent.get(name);
		} else {
			return result;
		}
	}
	
	public void put(String name, Block<T> block) {
		this.blocks.put(name, block);
	}
}