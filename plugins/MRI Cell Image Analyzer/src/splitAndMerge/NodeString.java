package splitAndMerge;

public class NodeString implements NodeData {

	private String value;
	private int color;
	
	public NodeString (String data, int color) {
		this.value = data;
		this.color = color;
	}
	
	public void merge(NodeData node) {
		value = value + "," + node.getValue();
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = (String) value;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

}
