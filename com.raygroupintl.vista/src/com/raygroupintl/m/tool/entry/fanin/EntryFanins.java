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

package com.raygroupintl.m.tool.entry.fanin;

import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;

import com.raygroupintl.m.parsetree.data.EntryId;

public class EntryFanins {
	Map<EntryId, SortedSet<EntryId>> pathPieces = new TreeMap<EntryId, SortedSet<EntryId>>();

	public void add(PathPieceToEntry ppte) {
		if (ppte.exist()) {
			this.pathPieces.put(ppte.getStartEntry(), ppte.getNextEntries());
		}
	}
	
	public boolean hasFaninEntry(EntryId entryId) {
		return this.pathPieces.containsKey(entryId);
	}
	
	public Set<EntryId> getFaninEntries() {
		return this.pathPieces.keySet();
	}
	
	public Set<EntryId> getFaninNextEntries(EntryId entry) {
		return this.pathPieces.get(entry);
	}
}
