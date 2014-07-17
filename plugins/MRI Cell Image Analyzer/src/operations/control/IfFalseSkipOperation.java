package operations.control;
import gui.options.Option;
import operations.Operation;

public class IfFalseSkipOperation extends Operation {
	private static final long serialVersionUID = 7008974034633106722L;
	protected Option numberOfOperations;
	protected Boolean condition;
	
	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		parameterTypes = new Class[1];
		parameterTypes[0] = Class.forName("java.lang.Boolean");
		parameterNames = new String[1];
		parameterNames[0] = "Condition";
		optionsNames = new String[1];
		optionsNames[0] = "number of operations";
		resultTypes = new Class[0];
		resultNames = new String[0];
	}
	
	public void doIt() {
		if (!getCondition())
			application.setProgramCounter(application.getProgramCounter()+this.getNumberOfOperations());
	}
	
	protected void setupOptions() {
		super.setupOptions();
		this.setNumberOfOperations(0);
		numberOfOperations.setMin(0);
		numberOfOperations.setShortHelpText("the number of operations to skip");
	}
	
	public void connectOptions() {
		this.numberOfOperations = (Option) this.options.getOptions().get(0);
	}

	public int getNumberOfOperations() {
		return numberOfOperations.getIntegerValue();
	}

	public void setNumberOfOperations(int numberOfOperations) {
		this.numberOfOperations.setValue(Integer.toString(numberOfOperations));
	}

	public Boolean getCondition() {
		return condition;
	}

	public void setCondition(Boolean condition) {
		this.condition = condition;
	}
}
