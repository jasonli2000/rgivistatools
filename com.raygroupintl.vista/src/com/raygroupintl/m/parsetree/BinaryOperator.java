package com.raygroupintl.m.parsetree;

import com.raygroupintl.struct.IterableArray;

public class BinaryOperator extends Nodes {
	private IterableArray<Node> nodes;

	public BinaryOperator(Node lhs, Node rhs) {
		Node[] array = {lhs, rhs};
		this.nodes = new IterableArray<Node>(array);
	}
	
	@Override
	public Iterable<Node> getNodes() {
		return this.nodes;
	}	
}