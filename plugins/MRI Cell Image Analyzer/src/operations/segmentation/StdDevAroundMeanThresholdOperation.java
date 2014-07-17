package operations.segmentation;

import operations.FilterOperation;
import gui.Options;
import gui.options.ChoiceOption;
import gui.options.Option;
import ij.IJ;
import ij.ImagePlus;
import ij.measure.Measurements;
import ij.process.ImageStatistics;

public class StdDevAroundMeanThresholdOperation extends FilterOperation {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected Option factor;
	protected ChoiceOption range;
	protected double mean;
	protected double stdDev;
	
	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		optionsNames = new String[2];
		optionsNames[0] = "factor";
		optionsNames[1] = "range";
	}
	
	public int getThresholdForSlice(int i) {
		// TODO Auto-generated method stub
		return 0;
	}

	public double getFactor() {
		return factor.getDoubleValue();
	}

	public void setFactor(double factor) {
		this.factor.setValue(Double.toString(factor));
	}
	
	protected void setupOptions() {
		options = new Options(); 
		options.setName(this.name() + " options");
		String[] optionsNames = this.getOptionsNames();
		this.factor = new Option();
		factor.setName(optionsNames[0]);
		this.setFactor(1.0);
		factor.setMin(0);
		factor.setShortHelpText("The pixels selected will be those below and above mean plus/minus factor times stdDev.");
		options.add(factor);
		this.range = new ChoiceOption(this.choices());
		this.range.setName(optionsNames[1]);
		this.range.setValue(this.choices()[2]);
		this.range.setName(optionsNames[1]);
		this.range.setShortHelpText("Select below only, in between or above only.");
		options.add(range);
	}
	
	protected String[] choices() {
		String[] choices = {"below only", "in between", "above only"};
		return choices;
	}

	public void connectOptions() {
		this.factor = (Option) this.options.getOptions().get(0);
		this.range = (ChoiceOption) this.options.getOptions().get(1);
	}

	public String getRange() {
		return range.getValue();
	}

	public void setRange(String range) {
		this.range.setValue(range);
	}

	public void runFilter() {
		ImagePlus inputImage = this.getInputImage();
		result = this.getCopyOfOrReferenceTo(inputImage, inputImage.getTitle());
		calculateMeanAndStdDev();
		double min = mean - (this.getFactor() * stdDev);
		double max = mean + (this.getFactor() * stdDev);
		IJ.setThreshold(min, max);
		if (this.belowOnly()) IJ.setThreshold(0, min);
		if (this.aboveOnly()) IJ.setThreshold(max, 65535);
		IJ.run("Convert to Mask");
	}

	protected void calculateMeanAndStdDev() {
		ImageStatistics stats = result.getStatistics(Measurements.MEAN+Measurements.STD_DEV);
		mean = stats.mean;
		stdDev = stats.stdDev;
	}

	public boolean belowOnly() {
		return this.getRange()=="below only";
	}
	
	public boolean inBetween() {
		return this.getRange()=="in between";
	}
	
	public boolean aboveOnly() {
		return this.getRange()=="above only";
	}

}
