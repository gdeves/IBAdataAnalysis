package splitAndMerge;

import java.util.HashSet;
import java.util.Iterator;

public class Node {
	private NodeData data;
	private HashSet<Node> neighbors;
	private boolean visited;
	private boolean merged;
	private boolean remove;
	private float gradient;
	
	public Node(NodeData shape) {
		neighbors = new HashSet<Node>();
		data = shape;
		visited = false;
		merged = false;
		remove = false;
	}

	public void addNeighbor(Node node) {
		neighbors.add(node);
	}

	public NodeData getData() {
		return data;
	}

	public void setData(NodeData data) {
		this.data = data;
	}

	public HashSet<Node> getNeighbors() {
		return neighbors;
	}
	
	public void mergeWithoutCopyNeighbors(Node node) {
		this.getNeighbors().remove(node);
		this.getData().merge(node.getData());
		node.pointNeighborsTo(this);
	}
	
	public void mergeWith(Node node)
	{
		this.getNeighbors().addAll(node.getNeighbors());
		this.getNeighbors().remove(node);
		this.getNeighbors().remove(this);
		node.pointNeighborsTo(this);
		this.getData().merge(node.getData());
	}
	
	public void pointNeighborsTo(Node node) {
		Iterator<Node> it = this.getNeighbors().iterator();
		while(it.hasNext()) {
			Node neighbor = it.next();
			if(neighbor.getNeighbors().remove(this))
				neighbor.addNeighbor(node);
		}
	}

	public void setVisited(boolean visited) {
		this.visited = visited;
	}
	
	public boolean getVisited() {
		return this.visited;
	}

	public boolean getMerged() {
		return merged;
	}

	public void setMerged(boolean merged) {
		this.merged = merged;
	}

	public boolean getRemove() {
		return remove;
	}

	public void setRemove(boolean remove) {
		this.remove = remove;
	}

	public float getGradient() {
		return gradient;
	}

	public void setGradient(float gradient) {
		this.gradient = gradient;
	}
}
