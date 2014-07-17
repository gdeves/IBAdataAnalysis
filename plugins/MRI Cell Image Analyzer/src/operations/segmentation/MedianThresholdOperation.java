package operations.segmentation;

import ij.process.ImageStatistics;

public class MedianThresholdOperation extends ThresholdBaseOperation {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7935411031549705085L;

	public int getThresholdForSlice(int i) {
		ImageStatistics stats = ImageStatistics.getStatistics(inputImage.getStack().getProcessor(i), ImageStatistics.MEDIAN, inputImage.getCalibration());
		return (int)Math.floor(stats.median);
	}

}
