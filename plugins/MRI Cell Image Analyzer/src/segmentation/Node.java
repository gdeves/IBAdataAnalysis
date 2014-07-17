package segmentation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

import javax.swing.tree.TreeNode;

public class Node<T> implements TreeNode {
	protected Node<T> parent;
	protected ArrayList<Node<T>> children = new ArrayList<Node<T>>();
	protected T content;
	
	public Node(T content) {
		this.content = content;
	}
	
	public Node<T> addChild(Node<T> aNode) {
		children.add(aNode);
		aNode.parent = this;
		return aNode;
	}
	
	public Node<T> removeChild(Node<T> aNode) {
		if (children.remove(aNode)) aNode.parent = null;
		return this;
	}
	
	public Node<T> removeParent() {
		if (parent!=null) parent.removeChild(this);
		return this;
	}
	
	public Node<T> setParent(Node<T> aNode) {
		removeParent();
		parent = aNode;
		parent.addChild(this);
		return aNode;
	}
	
	public T getContent() {
		return content;
	}
	
	public ArrayList<Node<T>> getChildren() {
		return children;
	}
	
	public Node<T> getParent() {
		return parent;
	}
	
	public int getChildCount() {
		return children.size();
	}

	public Enumeration<Node<T>> children() {
		return Collections.enumeration(getChildren());
	}

	public boolean getAllowsChildren() {
		return true;
	}

	public TreeNode getChildAt(int index) {
		return children.get(index);
	}

	public int getIndex(TreeNode arg0) {
		return children.indexOf(arg0);
	}

	public boolean isLeaf() {
		return getChildCount()==0;
	}
	
	public String toString() {
		return content.toString();
	}
} 
