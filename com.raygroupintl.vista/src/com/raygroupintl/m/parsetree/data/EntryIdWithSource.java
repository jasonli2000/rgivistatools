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

public class EntryIdWithSource {
	private EntryId entryId;
	private String source;
	
	public EntryIdWithSource(EntryId entryId, String source) {
		this.entryId = entryId;
		this.source = source;
	}
	
	public EntryId getEntryId() {
		return this.entryId;
	}
	
	public String getSource() {
		return this.source;
	}
}
