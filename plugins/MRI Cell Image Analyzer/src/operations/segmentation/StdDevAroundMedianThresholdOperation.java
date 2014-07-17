package operations.segmentation;

import statistics.BasicStatistics;

public class StdDevAroundMedianThresholdOperation extends
		StdDevAroundMeanThresholdOperation {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4707992807829734392L;

	protected void calculateMeanAndStdDev() {
		BasicStatistics stats = BasicStatistics.newFor(result);
		mean = stats.getMedian();
		stdDev = stats.getMedianStdDev();
	}
}
