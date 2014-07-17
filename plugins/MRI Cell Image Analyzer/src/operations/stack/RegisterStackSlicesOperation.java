package operations.stack;

import ij.IJ;
import gui.Options;
import gui.options.ChoiceOption;
import operations.FilterOperation;

public class RegisterStackSlicesOperation extends FilterOperation {
	protected ChoiceOption transformation;
	private static final long serialVersionUID = -929841859186080141L;
	
	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		optionsNames = new String[1];
		optionsNames[0] = "transformation";
		parameterTypes = new Class[1];
		parameterTypes[0] = Class.forName("ij.ImagePlus");
		parameterNames = new String[1];
		parameterNames[0] = "InputImage";
		resultTypes = new Class[1];
		resultTypes[0] = Class.forName("ij.ImagePlus");
		resultNames = new String[1];
		resultNames[0] = "Result";
	}
	
	public String getTransformation() {
		return transformation.getValue();
	}
	public void setTransformation(String transformation) {
		this.transformation.setValue(transformation);
	}

	public String[] transformationChoices() {
		String[] types = {"Translation", "Rigid Body", "Scaled Rotation", "Affine"};
		return types;
	}
	
	protected void setupOptions() {
		options = new Options(); 
		options.setName(this.name() + " options");
		String[] optionsNames = this.getOptionsNames();
		this.transformation = new ChoiceOption(this.transformationChoices());
		this.transformation.setValue(this.transformationChoices()[1]);
		this.transformation.setName(optionsNames[0]);
		this.transformation.setShortHelpText("choose the transformation to be used");
		options.add(this.transformation);
	}
	
	public void connectOptions() {
		transformation = (ChoiceOption) options.getOptions().get(0);
	}

	public void runFilter() {
		IJ.run("StackReg ", "transformation=["+this.getTransformation()+"]");
	}
}
