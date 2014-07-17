package splitAndMerge;

public class EntropyMergeCondition extends MergeCondition {
	
	private int threshold;
	private String operator;
	private boolean not;

	boolean evaluateWith(NodeData data1, NodeData data2) {
		NodeShape nodeShape1 = (NodeShape) data1;
		NodeShape nodeShape2 = (NodeShape) data2;
		
		Shape shape = (Shape)nodeShape1.getValue();
		Shape aShape = (Shape)nodeShape2.getValue();
		
		double entropyShape = shape.getEntropy();
		double entropyAShape = aShape.getEntropy();
		
		if(shape.getArea()<30 || aShape.getArea()<30) return true;
		if((entropyShape >= (entropyAShape-threshold)) && (entropyShape <= (entropyAShape+threshold))) return true;
		return false;
	}

	public int getThreshold() {
		return threshold;
	}
	public void setThreshold(int threshold) {
		this.threshold = threshold;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public boolean getNot() {
		return not;
	}

	public void setNot(boolean not) {
		this.not = not;
	}

}
