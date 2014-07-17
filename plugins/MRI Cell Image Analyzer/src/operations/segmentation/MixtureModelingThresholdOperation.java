package operations.segmentation;

import ij.ImageStack;
import ij.process.ImageProcessor;

public class MixtureModelingThresholdOperation extends ThresholdBaseOperation {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8519735905045683690L;

	/**
	 * @param ip
	 */
	protected void doItForSlice(int sliceNumber, ImageStack stack) {
		ImageProcessor ip = stack.getProcessor(sliceNumber);
		ip.threshold(this.getThresholdForSlice(sliceNumber));
	}
	
	public int getThresholdForSlice(int i) {
		// TODO Auto-generated method stub
		return 0;
	}

}
