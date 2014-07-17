/*
 * Created on 11.08.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package operations.segmentation;

import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ImageProcessor;
import operations.Operation;

/**
 * @author Volker
 *
 */
public abstract class ThresholdBaseOperation extends Operation {

	private static final long serialVersionUID = 6387286503320722145L;

	/**
	 * @throws ClassNotFoundException
	 *  
	 */
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
	}

	/**
	 * 
	 */
	public void doIt() {
		ImagePlus inputImage = this.getInputImage();
		result = this.getCopyOfOrReferenceTo(inputImage, inputImage.getTitle());
		this.processSlices();
	}
	
	/**
	 * @param ip
	 */
	protected void doItForSlice(int sliceNumber, ImageStack stack) {
		ImageProcessor ip = stack.getProcessor(sliceNumber);
		ip.threshold(this.getThresholdForSlice(sliceNumber));
	}
	
	abstract public int getThresholdForSlice(int i);
}
