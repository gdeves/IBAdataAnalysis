package splitAndMerge;

public class IntensityMergeCondition extends MergeCondition {

	private int threshold;
	private String operator;
	private boolean not;
	
	public IntensityMergeCondition() {
	}
	
	boolean evaluateWith(NodeData data1, NodeData data2) {
		NodeShape nodeShape1 = (NodeShape) data1;
		NodeShape nodeShape2 = (NodeShape) data2;
		
		Shape shape = (Shape)nodeShape1.getValue();
		Shape aShape = (Shape)nodeShape2.getValue();

		//image.setRoi(shape);
		//image.setRoi(aShape);

		if((shape.getIntensity() >= (aShape.getIntensity()-threshold)) && (shape.getIntensity() <= (aShape.getIntensity()+threshold))) return true;
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
