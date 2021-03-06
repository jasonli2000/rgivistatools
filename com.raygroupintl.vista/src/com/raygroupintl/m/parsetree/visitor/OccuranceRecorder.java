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

package com.raygroupintl.m.parsetree.visitor;

import java.util.HashMap;
import java.util.Map;

import com.raygroupintl.m.parsetree.AtomicDo;
import com.raygroupintl.m.parsetree.AtomicGoto;
import com.raygroupintl.m.parsetree.Do;
import com.raygroupintl.m.parsetree.DoBlock;
import com.raygroupintl.m.parsetree.ErrorNode;
import com.raygroupintl.m.parsetree.ExternalDo;
import com.raygroupintl.m.parsetree.Extrinsic;
import com.raygroupintl.m.parsetree.Goto;
import com.raygroupintl.m.parsetree.Indirection;
import com.raygroupintl.m.parsetree.Routine;
import com.raygroupintl.m.struct.LineLocation;

public class OccuranceRecorder extends LocationMarker {
	private Map<LineLocation, Integer> errors = new HashMap<LineLocation, Integer>();
	private Map<LineLocation, Integer> indirection = new HashMap<LineLocation, Integer>();
	private Map<LineLocation, Integer> doBlocks = new HashMap<LineLocation, Integer>();
	private Map<LineLocation, Integer> externalDoCalls = new HashMap<LineLocation, Integer>();
	private Map<LineLocation, Integer> atomicDoCalls = new HashMap<LineLocation, Integer>();
	private Map<LineLocation, Integer> atomicGotoCalls = new HashMap<LineLocation, Integer>();
	private Map<LineLocation, Integer> doCalls = new HashMap<LineLocation, Integer>();
	private Map<LineLocation, Integer> gotoCalls = new HashMap<LineLocation, Integer>();
	private Map<LineLocation, Integer> extrinsics = new HashMap<LineLocation, Integer>();

	private static void increment(Map<LineLocation, Integer> map, LineLocation location) {
		Integer count = map.get(location);
		if (count == null) {
			count = 1;
		} else {
			count = count + 1;
		}
		map.put(location, count);
	}

	protected void visitErrorNode(ErrorNode error) {
		LineLocation location = this.getLastLocation();
		increment(this.errors, location);
		super.visitErrorNode(error);
	}

	protected void visitIndirection(Indirection indirection) {
		LineLocation location = this.getLastLocation();
		increment(this.indirection, location);
		super.visitIndirection(indirection);
	}

	protected void visitDoBlock(DoBlock doBlock) {
		LineLocation location = this.getLastLocation();
		increment(this.doBlocks, location);
		super.visitDoBlock(doBlock);
	}
	
	protected void visitExternalDo(ExternalDo externalDo) {
		LineLocation location = this.getLastLocation();
		increment(this.externalDoCalls, location);
		super.visitExternalDo(externalDo);
	}

	protected void visitAtomicDo(AtomicDo atomicDo) {
		LineLocation location = this.getLastLocation();
		increment(this.atomicDoCalls, location);
		super.visitAtomicDo(atomicDo);
	}
	
	protected void visitAtomicGoto(AtomicGoto atomicGoto) {
		LineLocation location = this.getLastLocation();
		increment(this.atomicGotoCalls, location);
		super.visitAtomicGoto(atomicGoto);
	}
	
	protected void visitExtrinsic(Extrinsic extrinsic) {
		LineLocation location = this.getLastLocation();
		increment(this.extrinsics, location);
		super.visitExtrinsic(extrinsic);
	}
	
	protected void visitDo(Do d) {
		LineLocation location = this.getLastLocation();
		increment(this.doCalls, location);
		super.visitDo(d);
	}	
	
	protected void visitGoto(Goto g) {
		LineLocation location = this.getLastLocation();
		increment(this.gotoCalls, location);
		super.visitGoto(g);
	}	
	
	private static int countAllOn(Map<LineLocation, Integer> map) {
		int count = 0;
		for (LineLocation location : map.keySet()) {
			Integer value = map.get(location);
			count += value;
		}
		return count;
	}

	public int getErrorNodeCount() {
		return countAllOn(this.errors);
	}

	public int getDoBlockCount() {
		return countAllOn(this.doBlocks);
	}

	public int getIndirectionCount() {
		return countAllOn(this.indirection);
	}

	public int getExternalDoCount() {
		return countAllOn(this.externalDoCalls);
	}

	public int getAtomicDoCount() {
		return countAllOn(this.atomicDoCalls);
	}

	public int getDoCount() {
		return countAllOn(this.doCalls);
	}

	public int getAtomicGotoCount() {
		return countAllOn(this.atomicGotoCalls);
	}

	public int getGotoCount() {
		return countAllOn(this.gotoCalls);
	}

	public int getExtrinsicCount() {
		return countAllOn(this.extrinsics);
	}

	private static int countOn(Map<LineLocation, Integer> map, LineLocation location) {
		Integer count = map.get(location);
		if (count == null) {
			return 0;
		} else  {
			return count.intValue();
		}
	}
	
	public int countErrorNodeOn(LineLocation location) {
		return countOn(this.errors, location);
	}

	public int countIndirectionOn(LineLocation location) {
		return countOn(this.indirection, location);
	}

	public int countDoBlockOn(LineLocation location) {
		return countOn(this.doBlocks, location);
	}

	public int countExternalDoOn(LineLocation location) {
		return countOn(this.externalDoCalls, location);
	}

	public int countAtomicDoOn(LineLocation location) {
		return countOn(this.atomicDoCalls, location);
	}

	public int countDoOn(LineLocation location) {
		return countOn(this.doCalls, location);
	}
	
	public int countAtomicGotoOn(LineLocation location) {
		return countOn(this.atomicGotoCalls, location);
	}

	public int countGotoOn(LineLocation location) {
		return countOn(this.gotoCalls, location);
	}
	
	public int countExtrinsicOn(LineLocation location) {
		return countOn(this.extrinsics, location);
	}
	
	public static OccuranceRecorder record(Routine routine) {
		OccuranceRecorder result = new OccuranceRecorder();
		routine.accept(result);
		return result;
	}
}
