package operations.segmentation;

import gui.Options;
import gui.options.ChoiceOption;
import gui.options.Option;
import ij.IJ;
import operations.FilterOperation;

public class LocalThresholdOperation extends FilterOperation {
	private static final long serialVersionUID = 6797566344231310523L;
	
	ChoiceOption filter; 
	Option filterRadius;
	
	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		optionsNames = new String[2];
		optionsNames[0] = "filter";
		optionsNames[1] = "filter radius";
		parameterTypes = new Class[1];
		parameterTypes[0] = Class.forName("ij.ImagePlus");
		parameterNames = new String[1];
		parameterNames[0] = "InputImage";
		resultTypes = new Class[1];
		resultTypes[0] = Class.forName("ij.ImagePlus");
		resultNames = new String[1];
		resultNames[0] = "Result";
	}

	public int getFilterRadius() {
		return filterRadius.getIntegerValue();
	}

	public void setFilterRadius(int filterRadius) { 
		this.filterRadius.setValue(Integer.toString(filterRadius));
	}
	
	public void connectOptions() {
		this.filter = (ChoiceOption) this.options.getOptions().get(0);
		this.filterRadius = (Option) this.options.getOptions().get(1);
	}
	
	protected void setupOptions() {
		options = new Options(); 
		options.setName(this.name() + " options");
		String[] optionsNames = this.getOptionsNames();
		this.filter = new ChoiceOption(this.filters());
		this.filter.setName(optionsNames[0]);
		filter.setValue("Median...");
		options.add(this.filter);
		this.filterRadius = new Option();
		this.filterRadius.setName(optionsNames[1]);
		this.setFilterRadius(7);
		this.filterRadius.setMin(1);
		this.filterRadius.setShortHelpText("enter the radius of the median filter");
		options.add(this.filterRadius);
	}

	protected String[] filters() {
		String[] filters = {"Gaussian Blur...", "Mean...", "Median..."};
		return filters;
	}

	public String getFilter() {
		return filter.getValue();
	}

	public void setFilter(String filter) {
		this.filter.setValue(filter);
	}

	public void runFilter() {
		 IJ.run("Duplicate...", "title=Original");
	     IJ.run("32-bit");
	     IJ.run("Duplicate...", "title=Filtered");
	     IJ.selectWindow("Filtered");
	     IJ.run(this.getFilter(), "radius=" + this.getFilterRadius());
	     IJ.run("Image Calculator...", 
	    		"image1=Original operation=Subtract image2=Filtered create");
	     IJ.run("Rename...", "title=Result");
	     IJ.run("8-bit");
	}
}
