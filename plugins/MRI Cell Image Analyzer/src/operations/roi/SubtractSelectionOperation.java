/*
 * Created on 18.11.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package operations.roi;

import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.PointRoi;
import ij.gui.Roi;
import ij.gui.ShapeRoi;
import operations.Operation;

/**
 * @author Volker
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SubtractSelectionOperation extends Operation {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4275754561524309876L;
	
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
	
	/**
	 *  
	 */
	public void doIt() {
		ImagePlus destImage = this.getDestinationImage();
		Roi destRoi = destImage.getRoi();
		destImage.killRoi();
		result = this.getCopyOfOrReferenceTo(destImage, this.getDestinationImage().getTitle() + " - subtract selection");
		destImage.setRoi(destRoi);
		if (destRoi==null) return;
		Roi inputRoi = getInputImage().getRoi();
		ShapeRoi destinationShape = null;
		if (destRoi.isArea()) {
			ShapeRoi inputShape = null;
			destinationShape = new ShapeRoi(destRoi);
			if (inputRoi!=null) {
				inputShape = new ShapeRoi(inputRoi);
				destinationShape = destinationShape.not(inputShape);
			}
			if (destinationShape.getBounds().width==0 || destinationShape.getBounds().height==0) {
				result.killRoi();
				return;
			}
			result.getProcessor().setRoi(destinationShape);
			result.setRoi(destinationShape);
			result.setProcessor(null, destinationImage.getProcessor());
		} else {
			PointRoi destPointRoi = (PointRoi) destinationImage.getRoi();
			PointRoi newDestRoi = destPointRoi.subtractPoints(inputRoi);
			result.getProcessor().setRoi(newDestRoi);
			result.setRoi(newDestRoi);
			result.setProcessor(null, destinationImage.getProcessor());
		}
	}
	
	protected void cleanUpInput() {
		super.cleanUpInput();
		this.setDestinationImage(null);	
	}
}
