package operations.segmentation;

import ij.IJ;
import ij.ImagePlus;
import gui.Options;
import gui.options.BooleanOption;
import gui.options.Option;
import operations.FilterOperation;

public class GreyscaleWatershedOperation extends FilterOperation {
	private static final long serialVersionUID = 264719250521823897L;
	protected Option maxLevel;
	protected BooleanOption eightConnected; 
	protected Option smoothingRadius;
	
	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		parameterTypes = new Class[1];
		parameterTypes[0] = Class.forName("ij.ImagePlus");
		parameterNames = new String[1];
		parameterNames[0] = "InputImage";
		resultTypes = new Class[1];
		resultTypes[0] = Class.forName("ij.ImagePlus");
		resultNames = new String[1];
		resultNames[0] = "Result";
		optionsNames = new String[3];
		optionsNames[0] = "max. level";
		optionsNames[1] = "eight connected";
		optionsNames[2] = "smoothing diameter";
	}
	
	protected void setupOptions() {
		options = new Options(); 
		options.setName(this.name() + " options");
		String[] optionsNames = this.getOptionsNames();
		this.maxLevel = new Option();
		this.maxLevel.setName(optionsNames[0]);
		this.setMaxLevel(255);
		this.maxLevel.setMin(0);
		this.maxLevel.setMax(255);
		this.maxLevel.setShortHelpText("not used, only there for backward compability");
		options.add(this.maxLevel);
		this.eightConnected = new BooleanOption();
		this.eightConnected.setName(optionsNames[1]);
		this.setEightConnected(true);
		this.eightConnected.setShortHelpText("not used, only there for backward compability");
		options.add(this.eightConnected);
		this.smoothingRadius = new Option();
		this.smoothingRadius.setName(optionsNames[2]);
		this.setSmoothingRadius(9);
		this.smoothingRadius.setMin(1);
		this.smoothingRadius.setMax(255);
		this.smoothingRadius.setShortHelpText("the diameter of the smoothing filter");
		options.add(smoothingRadius);
	}

	public boolean getEightConnected() {
		return eightConnected.getBooleanValue();
	}

	public void setEightConnected(boolean eightConnected) {
		this.eightConnected.setValue(Boolean.toString(eightConnected));
	}

	public int getMaxLevel() {
		return maxLevel.getIntegerValue();
	}

	public void setMaxLevel(int maxLevel) {
		this.maxLevel.setValue(Integer.toString(maxLevel));
	}
	
	public int getSmoothingRadius() {
		return smoothingRadius.getIntegerValue();
	}

	public void setSmoothingRadius(int smoothingRadius) {
		this.smoothingRadius.setValue(Integer.toString(smoothingRadius));
	}
	
	public void connectOptions() {
		this.maxLevel = (Option) this.options.getOptions().get(0);
		this.eightConnected = (BooleanOption) this.options.getOptions().get(1);
		this.smoothingRadius = (Option) this.options.getOptions().get(2);
	}

	@Override
	public void runFilter() {
		ImagePlus inputImage = this.getInputImage();
		result = this.getCopyOfOrReferenceTo(inputImage, inputImage.getTitle());;
		int radius = this.getSmoothingRadius();
		IJ.run("Gaussian Blur...", "sigma=" + radius / 2.5);
		IJ.run("Watershed Algorithm");
	}
}
