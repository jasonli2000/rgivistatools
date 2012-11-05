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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.raygroupintl.m.parsetree.filter.SourcedFanoutFilter;
import com.raygroupintl.m.parsetree.visitor.APIRecorder;

public abstract class Block {
	private final static Logger LOGGER = Logger.getLogger(APIRecorder.class.getName());
	private static Set<EntryId> reported = new HashSet<EntryId>();

	private int index;
	private EntryId entryId;
	private Blocks siblings;
	
	private List<IndexedFanout> fanouts = new ArrayList<IndexedFanout>();
	private boolean closed;
	
	public Block(int index, EntryId entryId, Blocks siblings) {
		this.index = index;
		this.entryId = entryId;
		this.siblings = siblings;
	}
	
	public void close() {
		this.closed = true;
	}
	
	public boolean isClosed() {
		return this.closed;
	}
	
	public int getIndex() {
		return this.index;
	}
	
	public EntryId getEntryId() {
		return this.entryId;
	}
		
	public void addFanout(int index, EntryId fanout, CallArgument[] arguments) {
		if (! this.closed) {
			IndexedFanout ifo = new IndexedFanout(index, fanout);			
			ifo.setByRefs(arguments);
			this.fanouts.add(ifo);
		}
	}	
	
	private void update(FanoutBlocks fanoutBlocks, BlocksSupply blocksSupply, SourcedFanoutFilter filter, Map<String, String> replacedRoutines) {
		for (IndexedFanout ifout : this.fanouts) {
			EntryId fout = ifout.getFanout();
			if ((filter != null) && (! filter.isValid(fout))) continue;
			String routineName = fout.getRoutineName();
			String tagName = fout.getTag();					
			if (tagName == null) {
				tagName = routineName;
			}			
			if (routineName == null) {
				Block tagBlock = this.siblings.get(tagName);
				if (tagBlock == null) {
					String inRoutineName = this.entryId.getRoutineName();
					if (inRoutineName != null) {
						String replacement = replacedRoutines.get(tagName);
						if ((replacement != null) && (replacement.equals(inRoutineName))) {
							tagBlock = this.siblings.get(replacement);
						}
					}
					if (tagBlock == null) {
						if (! reported.contains(fout)) {						
							LOGGER.log(Level.WARNING, "Unable to find information about tag " + tagName + " in " + this.entryId.getRoutineName());
							reported.add(fout);
						}
						continue;
					}
				}
				fanoutBlocks.add(this, tagBlock, ifout.getIndex());
			} else {
				Blocks routineBlocks = blocksSupply.getBlocks(routineName);
				String originalTagName = tagName;
				if (routineBlocks == null) {
					if (replacedRoutines != null) {
						String replacement = replacedRoutines.get(routineName);
						if (replacement != null) {
							routineBlocks = blocksSupply.getBlocks(replacement);
							if (tagName.equals(routineName)) {
								tagName = replacement;
							}
						}
					}
					if (routineBlocks == null) {					
						if (! reported.contains(fout)) {
							LOGGER.log(Level.WARNING, "Unable to find information about routine " + routineName + ".");
							reported.add(fout);
						}
						continue;
					}
				}
				Block tagBlock = routineBlocks.get(tagName);
				if (tagBlock == null) {
					if (! originalTagName.equals(tagName)) {
						tagBlock = routineBlocks.get(originalTagName);
					}
					if (tagBlock == null) {
						if (! reported.contains(fout)) {
							LOGGER.log(Level.WARNING, "Unable to find information about tag " + fout.toString() + " in " + routineName);
							reported.add(fout);
						}
						continue;
					}
				}
				fanoutBlocks.add(this, tagBlock, ifout.getIndex());
			}				 
		}
	}
	
	private void updateFanoutBlocks(FanoutBlocks fanoutBlocks, BlocksSupply blocksSupply, SourcedFanoutFilter filter, Map<String, String> replacedRoutines) {
		int index = 0;	
		while (index < fanoutBlocks.getSize()) {
			Block b = fanoutBlocks.getBlock(index);
			b.update(fanoutBlocks, blocksSupply, filter, replacedRoutines);
			++index;
		}		
	}
	
	public FanoutBlocks getFanoutBlocks(BlocksSupply blocksSupply, DataStore<APIData> store, SourcedFanoutFilter filter, Map<String, String> replacedRoutines) {
		FanoutBlocks fanoutBlocks = new FanoutBlocks(this, store);
		this.updateFanoutBlocks(fanoutBlocks, blocksSupply, filter, replacedRoutines);
		return fanoutBlocks;
	}
	
	public FanoutBlocks getFanoutBlocks(BlocksSupply blocksSupply, SourcedFanoutFilter filter, Map<String, String> replacedRoutines) {
		FanoutBlocks fanoutBlocks = new FanoutBlocks(this, null);
		this.updateFanoutBlocks(fanoutBlocks, blocksSupply, filter, replacedRoutines);
		return fanoutBlocks;
	}
	
	public abstract APIData getInitialAccumulativeData();
		
	public abstract APIData getInitialAdditiveData();
		
	public abstract int mergeAccumulative(APIData target, APIData source, int sourceIndex);
	
	public abstract void mergeAdditiveTo(APIData target);
}
