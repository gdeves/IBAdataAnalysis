package splitAndMerge;

abstract public class MergeCondition {
	abstract boolean evaluateWith(NodeData data1, NodeData data2);
	abstract String getOperator();
	abstract boolean getNot();
}
