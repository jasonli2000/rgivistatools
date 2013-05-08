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

package com.raygroupintl.m.tool.entry.quittype;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.raygroupintl.m.parsetree.data.EntryId;
import com.raygroupintl.m.struct.CodeLocation;

public class QuitType {
	private QuitTypeState state = QuitTypeState.NO_QUITS;
	private CodeLocation firstQuitLocation;
	private CodeLocation conflictingLocation;
	
	private Map<EntryId, CallType> fanoutCalls = new HashMap<EntryId, CallType>();
	
	public QuitType() {		
	}
	
	public QuitType(QuitType rhs) {
		this.state = rhs.state;
		this.firstQuitLocation = rhs.firstQuitLocation;
		this.conflictingLocation = rhs.conflictingLocation;
	}
	
	public boolean addFanout(EntryId id, CallType type) {
		CallType ct = this.fanoutCalls.get(id);
		if (ct == null) {
			this.fanoutCalls.put(id, type);
			return true;						
		} else {
			if (ct.getState() == type.getState()) {
				return false;
			} else {
				this.fanoutCalls.put(id, type);
				return true;
			}
		}
	}
	
	public CallType getFanout(EntryId id) {
		return this.fanoutCalls.get(id);
	}
		
	public Map<EntryId, CallType> getFanoutCalls() {
		if (this.fanoutCalls == null) {
			return Collections.emptyMap();
		} else {
			return this.fanoutCalls;
		}
	}
	
	public QuitTypeState getQuitTypeState() {
		return this.state;
	}

	public CodeLocation getFirstQuitLocation() {
		return this.firstQuitLocation;
	}
	
	public CodeLocation getConflictingLocation() {
		return this.conflictingLocation;
	}
	
	public void markQuitWithValue(CodeLocation location) {
		switch (this.state) {
		case NO_QUITS:
			this.state = QuitTypeState.QUITS_WITH_VALUE;
			this.firstQuitLocation = location;
			break;
		case QUITS_WITH_VALUE:
		case CONFLICTING_QUITS:
			break;
		default:
			this.state = QuitTypeState.CONFLICTING_QUITS;
			this.conflictingLocation = location;
			break;
		}
	}
	
	public void markQuitWithoutValue(CodeLocation location) {
		switch (this.state) {
		case NO_QUITS:
			this.state = QuitTypeState.QUITS_WITHOUT_VALUE;
			this.firstQuitLocation = location;
			break;
		case QUITS_WITHOUT_VALUE:
		case CONFLICTING_QUITS:
			break;
		default:
			this.state = QuitTypeState.CONFLICTING_QUITS;
			this.conflictingLocation = location;
			break;
		}		
	}
	
	public int markQuitFromGoto(QuitType gotoQuitType, CodeLocation location) {
		QuitTypeState gotoState = gotoQuitType.state;
		if (gotoState == QuitTypeState.NO_QUITS) {
			return 0;
		}
		if (this.state == QuitTypeState.CONFLICTING_QUITS) {
			return 0;
		}
		if ((this.state == QuitTypeState.NO_QUITS) || (gotoState == QuitTypeState.CONFLICTING_QUITS)) {
			this.state = gotoQuitType.state;
			this.firstQuitLocation = gotoQuitType.firstQuitLocation;
			this.conflictingLocation = gotoQuitType.conflictingLocation;
			return 1;		
		}
		if (this.state == QuitTypeState.QUITS_WITHOUT_VALUE) {
			if (gotoState == QuitTypeState.QUITS_WITH_VALUE) {
				this.state = QuitTypeState.CONFLICTING_QUITS;
				this.conflictingLocation = gotoQuitType.firstQuitLocation;
				return 1;
			}
			return 0;				
		}
		if (this.state == QuitTypeState.QUITS_WITH_VALUE) {
			if (gotoState == QuitTypeState.QUITS_WITHOUT_VALUE) {
				this.state = QuitTypeState.CONFLICTING_QUITS;
				this.conflictingLocation = gotoQuitType.firstQuitLocation;
				return 1;
			}
			return 0;				
		}
		return 0;
	}
	
	public int updateCallTypes(QuitType source) {
		int result = 0;
		if (source.fanoutCalls != null) {
			Set<EntryId> entryIds = source.fanoutCalls.keySet();
			for (EntryId entryId : entryIds) {
				CallType ct = source.fanoutCalls.get(entryId);
				boolean updated = this.addFanout(entryId, ct);
				if (updated) ++result;
			}
		}
		return result;
	}
	
	public boolean hasConflict() {
		if (this.state == QuitTypeState.CONFLICTING_QUITS) {
			return true;
		}
		for (CallType ct : this.fanoutCalls.values()) {
			if (ct.getState().isConflictingState()) {
				return true;
			}
		}
		return false;
	}
}