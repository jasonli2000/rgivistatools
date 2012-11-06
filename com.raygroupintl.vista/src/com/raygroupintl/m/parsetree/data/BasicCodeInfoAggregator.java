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

import java.util.List;
import java.util.Map;

import com.raygroupintl.m.parsetree.filter.SourcedFanoutFilter;

public class BasicCodeInfoAggregator {
	Block<BlockCodeInfo> block;
	BlocksSupply<BlockCodeInfo> supply;
	
	public BasicCodeInfoAggregator(Block<BlockCodeInfo> block, BlocksSupply<BlockCodeInfo> supply) {
		this.block = block;
		this.supply = supply;
	}
	
	public BasicCodeInfo getAPIData(SourcedFanoutFilter filter, Map<String, String> replacedRoutines) {
		if (filter != null) filter.setSource(this.block.getEntryId());
		BasicCodeInfo result = new BasicCodeInfo();
		FanoutBlocks<BlockCodeInfo> fanoutBlocks = this.block.getFanoutBlocks(this.supply, filter, replacedRoutines);
		List<Block<BlockCodeInfo>> blocks = fanoutBlocks.getBlocks();
		for (Block<BlockCodeInfo> b : blocks) {
			result.mergeAdditive(b.getData());
		}
		return result;		
	}
}
