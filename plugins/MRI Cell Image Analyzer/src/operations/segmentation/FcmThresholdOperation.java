package operations.segmentation;

import analysis.clustering.FuzzyCMeansClustering;
import ij.ImagePlus;
import ij.process.ImageProcessor;
import operations.clustering.FuzzyCMeansClusteringOperation;

public class FcmThresholdOperation extends FuzzyCMeansClusteringOperation {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3227889461265445190L;
	
	public void doIt() {
		ImagePlus inputImage = this.getInputImage();
		result = this.getCopyOfOrReferenceTo(inputImage, inputImage.getTitle() + " - fcm threshold");
		FuzzyCMeansClustering fcm = FuzzyCMeansClustering.newFor(inputImage, this.getNumberOfClusters(), this.getMaxIterations(), this.getFuzziness(), this.getMinQuality(), this.getQualityChangeThreshold());
		fcm.run();
		float threshold = fcm.getSmallestIntensityFromMaxIntensityCluster();
		ImageProcessor ip = result.getProcessor();
		ip.threshold(Math.round(threshold));
	}

}
