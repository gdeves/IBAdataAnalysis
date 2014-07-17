/*
 * Created on 23.08.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package operations.processing;

import gui.options.Option;
import ij.ImagePlus;
import ij.ImageStack;
import ij.plugin.filter.BackgroundSubtracter;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import operations.Operation;

/**
 * @author Volker
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SubtractBackgroundOperation extends Operation {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8559204882420482079L;
	
	protected Option radius;
	private BackgroundSubtracter subtracter;
	
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
	
	public void doIt() {
		subtracter = this.getSubtracter();
		ImagePlus inputImage = this.getInputImage();
		result = this.getCopyOfOrReferenceTo(inputImage, inputImage.getTitle());
		this.processSlices();
	}

	protected void doItForSlice(int sliceNumber, ImageStack stack) {
		ImageProcessor ip = stack.getProcessor(sliceNumber);
		if (ip instanceof ColorProcessor)
			subtracter.rollingBallBrightnessBackground((ColorProcessor)ip, this.getRadius(), false, false, false, true, true);
		else
			subtracter.rollingBallBackground(ip, this.getRadius(), false, false, false, true, true);
	}
	
	/**
	 * @return
	 */
	public int getRadius() {
		return radius.getIntegerValue();
	}
	
	public void setRadius(int radius) {
		this.radius.setValue(Integer.valueOf(radius).toString());
	}
	
	protected void setupOptions() {
		super.setupOptions();
		this.setRadius(50);
	}
	
	public void connectOptions() {
		this.radius = (Option) this.options.getOptions().get(0);
	}
	/**
	 * @return Returns the subtracter.
	 */
	public BackgroundSubtracter getSubtracter() {
		subtracter = new BackgroundSubtracter();
		return subtracter;
	}
}
