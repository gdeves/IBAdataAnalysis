package splitAndMerge;

public class NodeShape implements NodeData {

	private Shape shape;
	
	public NodeShape(Shape aShape) {
		shape = aShape;
	}

	public Object getValue() {
		return shape;
	}

	public void merge(NodeData node) {
		Shape shape1 = (Shape) this.getValue();
		Shape shape2 = (Shape) node.getValue();
		
		shape1.setArea((shape1.getArea())+(shape2.getArea()));
		shape1.setEntropy(((shape1.getEntropy())+(shape2.getEntropy())/2));
		
		shape1.or(shape2);
	}

	public void setValue(Object value) {
		shape = (Shape) value;
		
	}

}
