/*
 * Created on 03.02.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package operations.stack;

import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ImageProcessor;
import gui.options.Option;
import operations.Operation;

/**
 * @author Volker
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DuplicateSliceOperation extends Operation {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5932836390917353876L;
	Option sliceNumber;

	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		optionsNames = new String[1];
		optionsNames[0] = "slice number";
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
	 * @return Returns the sliceNumber.
	 */
	public int getSliceNumber() {
		return sliceNumber.getIntegerValue();
	}
	/**
	 * @param sliceNumber The sliceNumber to set.
	 */
	public void setSliceNumber(int sliceNumber) {
		this.sliceNumber.setValue(Integer.toString(sliceNumber));
	}
	
	protected void setupOptions() {
		super.setupOptions();
		this.setSliceNumber(1);
	}
	
	public void connectOptions() {
		this.sliceNumber = (Option) this.options.getOptions().get(0);
	}
	
	public void doIt() {
		ImagePlus inputImage = this.getInputImage();
		ImageStack stack = inputImage.getStack();
		ImageProcessor ip2 = stack.getProcessor(this.getSliceNumber()).crop();
		result = inputImage.createImagePlus();
		result.setProcessor(inputImage.getTitle() + " slice " + this.getSliceNumber(), ip2);
		String info = (String)inputImage.getProperty("Info"); 
		int stackSize = stack.getSize();
		if (info!=null)
			result.setProperty("Info", info);
		if (stackSize>1) {
			String label = stack.getSliceLabel(this.getSliceNumber());
			if (label!=null && label.indexOf('\n')>0)
				result.setProperty("Info", label);
		}
	}
}
