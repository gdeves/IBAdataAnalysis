package splitAndMerge;

public class SignalToNoiseRatioMergeCondition extends MergeCondition {

	private int threshold;
	private String operator;
	private boolean not;

	public boolean getNot() {
		return not;
	}

	public void setNot(boolean not) {
		this.not = not;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	boolean evaluateWith(NodeData data1, NodeData data2) {

		return true;
	}
	
	public int getThreshold() {
		return threshold;
	}
	public void setThreshold(int threshold) {
		this.threshold = threshold;
	}


}
