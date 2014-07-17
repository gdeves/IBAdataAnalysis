package tools.magicWand;

import java.util.Observer;

import operations.analysis.HessianOperation;
import operations.segmentation.MeanThresholdOperation;

import utils.Broadcaster;
import ij.IJ;
import ij.WindowManager;
import gui.Options;
import gui.options.Option;

public class CalculateMorphologicDams implements MagicWandBackstoreImageCreator {
	
	protected Options options;
	protected Option smoothingRadius;
	protected Option numberOfDilations;
	protected Broadcaster broadcaster;
	
	
	public CalculateMorphologicDams() {
		this.setupOptions();
	}
	
	public CalculateMorphologicDams(Observer observer) {
		this.setupOptions();
		this.getBroadcaster().addObserver(observer);
	}

	protected Broadcaster getBroadcaster() {
		if (this.broadcaster == null) this.broadcaster = new Broadcaster();
		return broadcaster;
	}

	protected void setupOptions() {
		options = new Options();
		smoothingRadius = new Option();
		smoothingRadius.setName("radius");
		smoothingRadius.setShortHelpText("set the radius of the gaussian blur filter");
		smoothingRadius.setMin(0);
		smoothingRadius.setValue(Integer.toString(8));
		options.add(smoothingRadius);
		numberOfDilations = new Option();
		numberOfDilations.setName("dilations");
		numberOfDilations.setShortHelpText("set the number of dilations");
		numberOfDilations.setMin(0);
		numberOfDilations.setValue(Integer.toString(4));
		options.add(numberOfDilations);
	}

	public void run() {
		this.getBroadcaster().changed("", "start calculating");
		IJ.run("8-bit");
		String smoothOptionString = "radius=" + this.smoothingRadius.getIntegerValue();
		IJ.run("Gaussian Blur...", smoothOptionString);
		HessianOperation gradientOperation = this.getGradientOperation();
		gradientOperation.run();
		WindowManager.setTempCurrentImage(gradientOperation.getHessian().getImage());
		for (int i=0; i<this.numberOfDilations.getIntegerValue(); i++) {
			WindowManager.getTempCurrentImage().getProcessor().dilate();
		}
		MeanThresholdOperation threshold = this.getThresholdOperation();
		threshold.run();
		IJ.run("Skeletonize");
		this.getBroadcaster().changed("", "stop calculating");
	}

	protected MeanThresholdOperation getThresholdOperation() {
		MeanThresholdOperation op = new MeanThresholdOperation();
		op.setKeepSource(false);
		op.setShowResult(false);
		op.setInputImage(WindowManager.getTempCurrentImage());
		return op;
	}

	protected HessianOperation getGradientOperation() {
		HessianOperation result = new HessianOperation();
		result.setInputImage(WindowManager.getTempCurrentImage());
		result.setShowResult(false);
		return result;
	}

	public void showOptions() {
		this.options.view().setVisible(true);
	}

	public String name() {
		return "morphologic dams";
	}

}
