package splitAndMerge;

public interface NodeData {
	
	public void merge(NodeData node);
	
	public Object getValue();
	public void setValue(Object value);

}
