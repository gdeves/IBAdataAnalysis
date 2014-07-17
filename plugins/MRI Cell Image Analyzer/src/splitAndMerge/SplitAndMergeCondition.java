package splitAndMerge;

public class SplitAndMergeCondition {

	private String condition;
	private int value;
	private String operator;
	private boolean not;
	
	public String getCondition() {
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	public boolean getNot() {
		return not;
	}
	public void setNot(boolean not) {
		this.not = not;
	}
	
}
