/*
 * Created on 27.09.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package operations.segmentation;

import ij.process.ImageStatistics;

/**
 * @author Volker
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MeanThresholdOperation extends ThresholdBaseOperation {
	/**
	 * 
	 */
	private static final long serialVersionUID = -753271210081231455L;

	public int getThresholdForSlice(int i) {
		ImageStatistics stats = ImageStatistics.getStatistics(inputImage.getStack().getProcessor(i), ImageStatistics.MEAN, inputImage.getCalibration());
		return (int)Math.floor(stats.mean);
	}
}
