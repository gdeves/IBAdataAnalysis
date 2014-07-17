package operations.morphology;

import ij.IJ;
import gui.Options;
import gui.options.BooleanOption;
import gui.options.Option;
import operations.FilterOperation;

public abstract class BinaryMorphologyOperation extends FilterOperation {
	private static final long serialVersionUID = 1396551274509433462L;

	protected Option iterations;
	protected Option count;
	protected Option blackBackground;
	
	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		optionsNames = new String[3];
		optionsNames[0] = "iterations";
		optionsNames[1] = "count";
		optionsNames[2] = "blackBackground";
	}	
	
	public void runFilter() {
		setOptions();
		IJ.run(this.getCommand());
		resetOptions();
	}

	abstract protected String getCommand();

	public String getSetOptionsString() {
		String result = "";
		result += "iterations=" + this.getIterations() + " count =" + this.getCount();
		if (this.getBlackBackground()) result += " black";
		return result;
	}
	
	public void resetOptions() {
		IJ.run("Options...", "iterations=1 count=1");
	}
	
	public void setOptions() {
		IJ.run("Options...", this.getSetOptionsString());
	}
	
	public boolean getBlackBackground() {
		return blackBackground.getBooleanValue();
	}

	public void setBlackBackground(boolean blackBackground) {
		this.blackBackground.setBooleanValue(blackBackground);
	}

	public int getCount() {
		return count.getIntegerValue();
	}

	public void setCount(int count) {
		this.count.setIntegerValue(count);
	}

	public int getIterations() {
		return iterations.getIntegerValue();
	}

	public void setIterations(int iterations) {
		this.iterations.setIntegerValue(iterations);
	}

	protected void setupOptions() {
		options = new Options(); 
		options.setName(this.name() + " options");
		String[] optionsNames = this.getOptionsNames();
		this.iterations = new Option();
		this.setIterations(1);
		this.iterations.setName(optionsNames[0]);
		this.iterations.setShortHelpText("The number of erosion/dilation operations executed.");
		options.add(this.iterations);
		this.count = new Option();
		this.setCount(1);
		this.count.setName(optionsNames[1]);
		this.count.setShortHelpText("Number of adjacent background/foreground pixels before a pixel is eroded/dilated");
		options.add(this.count);
		this.blackBackground = new BooleanOption();
		this.setBlackBackground(false);
		this.blackBackground.setName(optionsNames[2]);
		this.blackBackground.setShortHelpText("Check to use white objects on black background.");
		options.add(this.blackBackground);
	}
	
	public void connectOptions() {
		this.iterations = (Option) this.options.getOptions().get(0);
		this.count = (Option) this.options.getOptions().get(1);
		this.blackBackground = (BooleanOption) this.options.getOptions().get(2);
	}
}
