package operations.segmentation;

import statistics.BasicStatistics;
import analysis.signalToNoise.MedianThresholdSignalToNoiseEstimator;
import analysis.signalToNoise.SignalToNoiseRatioCalculator;
import gui.options.Option;
import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.plugin.Thresholder;
import ij.plugin.filter.ImageMath;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

public class SnrThresholdOperation extends ThresholdBaseOperation {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1773046709159280503L;
	protected Option radius;
	/**
	 * 
	 */
	
	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		optionsNames = new String[1];
		optionsNames[0] = "radius";
	}
	
	public void doIt() {
		ImagePlus inputImage = this.getInputImage();
		result = this.getCopyOfOrReferenceTo(inputImage, inputImage.getTitle() + " - snr threshold");
		BasicStatistics stats = BasicStatistics.newFor(result);
		double threshold = stats.getMedian() + stats.getMeanStdDev();
		SignalToNoiseRatioCalculator calc =  SignalToNoiseRatioCalculator.newFor(result);
		double snrThreshold =  calc.calculateSNR(threshold);
		MedianThresholdSignalToNoiseEstimator estimator = MedianThresholdSignalToNoiseEstimator.newFor(inputImage, this.getRadius(), this.getRadius());
		estimator.run();
		ImagePlus snrImage = estimator.getLocalSNRImage();
		((FloatProcessor)(snrImage.getProcessor())).resetMinAndMax();
		WindowManager.setTempCurrentImage(snrImage);
		snrImage.getProcessor().setThreshold(snrThreshold, snrImage.getProcessor().getMax(), ImageProcessor.BLACK_AND_WHITE_LUT);
		ImageMath math = new ImageMath();
		math.setup("nan", snrImage);
		math.run(snrImage.getProcessor());
		IJ.run("8-bit");
		snrImage.getProcessor().setThreshold(1, 255, ImageProcessor.BLACK_AND_WHITE_LUT);
		(new Thresholder()).run("mask");
		result = snrImage;
	}

	public int getThresholdForSlice(int i) {
		// TODO Auto-generated method stub
		return 0;
	}

	protected void setupOptions() {
		super.setupOptions();
		this.setRadius(10);
		this.radius.setMin(1);
		this.radius.setShortHelpText("The radius of the pixel neighborhood for the snr estimation.");
	}
	
	public void connectOptions() {
		this.radius= (Option) this.options.getOptions().get(0);
	}

	public int getRadius() {
		return radius.getIntegerValue();
	}

	public void setRadius(int radius) {
		this.radius.setIntegerValue(radius);
	}
}
