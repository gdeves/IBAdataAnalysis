package splitAndMerge;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class Graph {
	private ArrayList<Node> nodes;
	private int[] gradient;
	private ArrayList<MergeCondition> conditionList;
	
	public Graph() {
		nodes = new ArrayList<Node>();
	}

	public ArrayList<Node> getNodes() {
		return nodes;
	}

	public void setNodes(ArrayList<Node> nodes) {
		this.nodes = nodes;
	}
	
	public void addNode(Node node) {
		nodes.add(node);
	}
	
	public void mergeAllNodes(ArrayList<MergeCondition> mergeConditionList) {
		boolean merged = true;
		conditionList = new ArrayList<MergeCondition>(mergeConditionList);
		while(merged) {
			ArrayList<Node> nodesToMerge = getNodesToMerge();
			if(nodesToMerge!=null) {
				Node node1 = nodesToMerge.get(0);
				Node node2 = nodesToMerge.get(1);
				node1.mergeWith(node2);
				node2.setRemove(true);
			}
			else {
				merged = false;
				continue;
			}
		}
	}
	
	private ArrayList<Node> getNodesToMerge() {
		ArrayList<Node> result = new ArrayList<Node>();
		Iterator<Node> it = nodes.iterator();
		while(it.hasNext()) {
			Node node = it.next();
			if(node.getVisited() || node.getRemove()) continue;
			HashSet<Node> neighbors = node.getNeighbors();
			for(Node aNode : neighbors) {
				if(aNode.getVisited()) continue;
				if(verifCondition(node.getData(), aNode.getData())) {
					result.add(node);
					result.add(aNode);
					return result;
				}
			}
			node.setVisited(true);
		}
	return null;
	}

	private boolean verifCondition(NodeData data, NodeData data2) {
		boolean result = false;
		int index = 0;
		String operator = null;
		boolean not = false;
		ArrayList<MergeCondition> conditions = conditionList;
		for(MergeCondition condition : conditions) {
			not = condition.getNot();
			if(conditionList.size()==1) {
				result = condition.evaluateWith(data, data2);
				if(not) 
					result = !result;
			}
			else {
				if(index!=0) {
					boolean temp = result;
					result = condition.evaluateWith(data, data2);
					if(not) result = !result;
					if(operator.equals("and")) {
						result = temp && result;
					}
					else if(operator.equals("or")) {
						result = temp || result;
					}
					operator = condition.getOperator();
				}
				else {
					result = condition.evaluateWith(data, data2);
					if(not) result = !result;
					operator = condition.getOperator();
				}
			}
			index++;
		}
		return result;
	}

	public int[] getGradient() {
		return gradient;
	}

	public void setGradient(int[] gradient) {
		this.gradient = gradient;
	}

	public ArrayList<MergeCondition> getConditionList() {
		return conditionList;
	}

	public void setConditionList(ArrayList<MergeCondition> conditionList) {
		this.conditionList = conditionList;
	}
}
