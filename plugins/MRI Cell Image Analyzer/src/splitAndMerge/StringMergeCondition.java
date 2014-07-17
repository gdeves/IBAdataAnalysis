package splitAndMerge;

public class StringMergeCondition extends MergeCondition {

	boolean evaluateWith(NodeData data1, NodeData data2) {
		NodeString nodeString1 = (NodeString) data1;
		NodeString nodeString2 = (NodeString) data2;
		if(nodeString1.getColor()==nodeString2.getColor()) return true;
		return false;
	}

	String getOperator() {
		// TODO Auto-generated method stub
		return null;
	}

	boolean getNot() {
		// TODO Auto-generated method stub
		return false;
	}

	
}
