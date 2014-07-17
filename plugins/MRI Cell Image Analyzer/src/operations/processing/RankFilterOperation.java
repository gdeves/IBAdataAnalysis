/*
 * Created on 10.08.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package operations.processing;

import gui.options.Option;
import ij.ImagePlus;
import ij.ImageStack;
import ij.plugin.filter.RankFilters;
import ij.process.ImageProcessor;
import operations.Operation;

/**
 * @author Volker
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class RankFilterOperation extends Operation {
	private static final long serialVersionUID = 7439173527365631955L;
	Option radius;
	protected RankFilters filter;
	
	public int filterType() {
		return RankFilters.MEDIAN;
	}

	public void doIt() {
		ImagePlus inputImage = this.getInputImage();
		result = this.getCopyOfOrReferenceTo(inputImage, inputImage.getTitle());
		this.processSlices();
		filter = null;
	}

	protected void setupOptions() {
		super.setupOptions();
		this.setRadius(1);
		this.radius.setShortHelpText("pixel in the radius are used to compute the new value of the central pixel");
	}

	public void connectOptions() {
		this.radius = (Option) this.options.getOptions().get(0);
	}

	/**
	 * @param i
	 */
	public void setRadius(int radius) {
		this.radius.setValue((new Integer(radius)).toString());	
	}

	/**
	 * @return Returns the radius.
	 */
	public int getRadius() {
		return radius.getIntegerValue();
	}

	protected void doItForSlice(int sliceNumber, ImageStack stack) {
		ImageProcessor ip = stack.getProcessor(sliceNumber);
		this.getFilter().rank(ip, this.getRadius(), this.filterType());
	}
	
	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		// Set parameterTypes, parameterNames and resultType
		optionsNames = new String[1];
		optionsNames[0] = "radius";
		parameterTypes = new Class[1];
		parameterTypes[0] = Class.forName("ij.ImagePlus");
		parameterNames = new String[1];
		parameterNames[0] = "InputImage";
		resultTypes = new Class[1];
		resultTypes[0] = Class.forName("ij.ImagePlus");
		resultNames = new String[1];
		resultNames[0] = "Result";
	}
	/**
	 * @return Returns the filter.
	 */
	public RankFilters getFilter() {
		if (filter == null) {
			filter = new RankFilters();
		}
		return filter;
	}
}
