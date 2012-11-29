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

package com.raygroupintl.vista.tools.entryfanin;

import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;

import com.raygroupintl.m.parsetree.data.EntryId;
import com.raygroupintl.output.Terminal;
import com.raygroupintl.output.TerminalFormatter;
import com.raygroupintl.vista.tools.fnds.ToolResult;

public class EntryFanins implements ToolResult {
	public EntryId entryUnderTest;
	Map<EntryId, SortedSet<EntryId>> pathPieces = new TreeMap<EntryId, SortedSet<EntryId>>();

	public EntryFanins(EntryId entryUnderTest) {
		this.entryUnderTest = entryUnderTest;
	}

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
	
	@Override
	public void write(Terminal t, TerminalFormatter tf) {
		t.writeEOL(" " + this.entryUnderTest.toString2());				
		Set<EntryId> starts = this.pathPieces.keySet();
		for (EntryId start : starts) {
			Set<EntryId> nextUps = this.pathPieces.get(start);
			for (EntryId nextUp : nextUps) {
				t.write("   " + start.toString2() + " thru ");
				t.writeEOL(nextUp.toString2());
			}
		}	
	}
}
