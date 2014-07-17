package operations.segmentation;

import ij.ImagePlus;
import ij.ImageStack;
import ij.plugin.filter.Binary;
import ij.process.ImageProcessor;
import operations.Operation;

public class SkeletonizeOperation extends Operation {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2098829934341109048L;
	
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
		Binary skeletonize = new Binary();
		skeletonize.setup("skel", this.getResult());
		skeletonize.run(ip);
		// this.result = IJ.getImage();
	}
}
