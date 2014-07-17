package splitAndMerge;

public class GradientMergeCondition extends MergeCondition {

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
		NodeShape nodeShape2 = (NodeShape) data2;
		
		Shape aShape = (Shape)nodeShape2.getValue();

		if(aShape.getGradient() > threshold) return false;
		return true;
	}
	
	public int getThreshold() {
		return threshold;
	}
	public void setThreshold(int threshold) {
		this.threshold = threshold;
	}


}
