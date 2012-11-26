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

import com.raygroupintl.struct.ObjectIdContainer;

public class DataStore<T> implements ObjectIdContainer {
	private Map<Integer, T> map = new HashMap<Integer, T>();

	public void reset() {
		this.map = new HashMap<Integer, T>();
	}

	public <U> T get(Block<U> block) {
		int id = System.identityHashCode(block);
		return this.map.get(id);
	}
	
	public boolean contains(int id) {
		return this.map.containsKey(id);				
	}
	
	public <U> T put(Block<U> block, Map<Integer, T> datas) {
		int id = System.identityHashCode(block);
		T data = datas.get(id);
		this.map.put(id, data);
		return data;
	}
}