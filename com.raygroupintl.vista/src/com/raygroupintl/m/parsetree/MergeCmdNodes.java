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

package com.raygroupintl.m.parsetree;

public class MergeCmdNodes {
	public static class IndirectAtomicMerge extends BasicNode {
		private static final long serialVersionUID = 1L;

		private Node node;
		
		public IndirectAtomicMerge(Node node) {
			this.node = node;
		}

		public void acceptSubNodes(Visitor visitor) {
			this.node.accept(visitor);
		}
		
		@Override
		public void accept(Visitor visitor) {
			visitor.visitIndirectAtomicMerge(this);
		}			
	}	

	public static class AtomicMerge extends BasicNode {
		private static final long serialVersionUID = 1L;

		private Node lhs;
		private Node rhs;
		
		public AtomicMerge(Node lhs, Node rhs) {
			this.lhs = lhs;
			this.rhs = rhs;
		}
		
		public void acceptSubNodes(Visitor visitor) {
			this.lhs.acceptPreMerge(visitor);
			this.rhs.accept(visitor);			
			this.lhs.acceptPostMerge(visitor);
		}
		
		@Override
		public void accept(Visitor visitor) {
			visitor.visitAtomicMerge(this);
		}	
	}
		
	public static class MergeCmd extends MultiCommand {
		private static final long serialVersionUID = 1L;

		public MergeCmd(Node postCondition, Node argument) {
			super(postCondition, argument);
		}

		@Override
		public void accept(Visitor visitor) {
			visitor.visitMerge(this);
		}		
	}
 }