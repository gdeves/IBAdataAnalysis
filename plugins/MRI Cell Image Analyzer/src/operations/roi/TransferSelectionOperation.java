/*
 * Created on 10.10.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package operations.roi;

import operations.Operation;
import operations.control.WaitForUserOperation;

import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.Roi;

/**
 * @author Volker
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TransferSelectionOperation extends Operation {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4649472620897387962L;
	
	protected ImagePlus destinationImage;
	
	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		parameterTypes = new Class[2];
		parameterTypes[0] = Class.forName("ij.ImagePlus");
		parameterTypes[1] = Class.forName("ij.ImagePlus");
		parameterNames = new String[2];
		parameterNames[0] = "InputImage";
		parameterNames[1] = "DestinationImage";
		resultTypes = new Class[1];
		resultTypes[0] = Class.forName("ij.ImagePlus");
		resultNames = new String[1];
		resultNames[0] = "Result";
	}
	
	
	/**
	 * @return Returns the destinationImage.
	 */
	public ImagePlus getDestinationImage() {
		if (destinationImage==null) {
			if (this.getApplication()==null) {
				WaitForUserOperation op = new WaitForUserOperation();
				op.run();
			}
			destinationImage = WindowManager.getCurrentImage();
		}
		return destinationImage;
	}
	/**
	 * @param destinationImage The destinationImage to set.
	 */
	public void setDestinationImage(ImagePlus destinationImage) {
		this.destinationImage = destinationImage;
	}
	
	public void doIt() {
		ImagePlus inputImage = this.getInputImage();
		Roi aRoi = inputImage.getRoi();
		Roi copyOfRoi = (Roi) aRoi.clone();
		inputImage.setRoi((Roi)null);
		result = this.getCopyOfOrReferenceTo(this.getDestinationImage(), this.getDestinationImage().getTitle());
		inputImage.setRoi(aRoi);
//		Roi.previousRoi = aRoi;
//		result.getProcessor().setMask(inputImage.getMask());
//		result.getProcessor().setRoi(inputImage.getProcessor().getRoi());
		result.setRoi(copyOfRoi);
//		result.restoreRoi();
		result.updateAndDraw();
	}
	
	protected void cleanUpInput() {
		super.cleanUpInput();
		this.setDestinationImage(null);
	}
}
