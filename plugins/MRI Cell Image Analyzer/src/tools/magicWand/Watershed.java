package tools.magicWand;

import java.util.Observer;

import operations.Operation;
import operations.image.ConvertImageTypeOperation;
import operations.image.ImageCalculationOperation;
import operations.processing.FindEdgesOperation;
import operations.processing.GaussianBlurOperation;
import operations.segmentation.GreyscaleWatershedOperation;
import utils.Broadcaster;
import utils.ImageDuplicator;
import ij.ImagePlus;
import ij.WindowManager;
import gui.Options;
import gui.options.Option;

public class Watershed implements MagicWandBackstoreImageCreator {
	protected Options options;
	protected Option correctionSmoothingRadius;
	protected Option smoothingRadius;
	protected Broadcaster broadcaster;

	public Watershed() {
		this.setupOptions();
	}
	
	public Watershed(Observer observer) {
		this.setupOptions();
		this.getBroadcaster().addObserver(observer);
	}
	
	protected Broadcaster getBroadcaster() {
		if (this.broadcaster == null) this.broadcaster = new Broadcaster();
		return broadcaster;
	}
	
	protected void setupOptions() {
		options = new Options();
		correctionSmoothingRadius = new Option();
		correctionSmoothingRadius.setName("unsharp mask smoothing radius");
		correctionSmoothingRadius.setShortHelpText("set the radius of the smoothing filter for the unsharp mask");
		correctionSmoothingRadius.setMin(1);
		correctionSmoothingRadius.setValue(Integer.toString(60));
		options.add(correctionSmoothingRadius);
		smoothingRadius = new Option();
		smoothingRadius.setName("smoothing radius");
		smoothingRadius.setShortHelpText("set the radius of the gaussian blur filter");
		smoothingRadius.setMin(1);
		smoothingRadius.setValue(Integer.toString(9));
		options.add(smoothingRadius);
	}
	
	public void run() {
		//this.getBroadcaster().changed("", "start calculating");
		WindowManager.getCurrentImage().killRoi();
		ConvertImageTypeOperation convert = new ConvertImageTypeOperation();
		configureAndRunOperation(convert);
		convert.getResult().setImage(convert.getResult().getProcessor().createImage());
		FindEdgesOperation edges = new FindEdgesOperation();
		configureAndRunOperation(edges);
		
		ImagePlus flatfield = ImageDuplicator.copyImage(edges.getResult(), "blurred");
		GaussianBlurOperation blur = new GaussianBlurOperation();
		blur.setInputImage(flatfield);
		blur.setSigma(correctionSmoothingRadius.getIntegerValue());
		configureAndRunOperation(blur);
		
		ImageCalculationOperation calc = new ImageCalculationOperation();
		calc.setInputImage(edges.getResult());
		calc.setSecondInputImage(flatfield);
		calc.setOperator("Subtract");
		configureAndRunOperation(calc);
		
		GreyscaleWatershedOperation op = new GreyscaleWatershedOperation();
		op.setSmoothingRadius(this.smoothingRadius.getIntegerValue());
		op.setInputImage(calc.getResult());
		configureAndRunOperation(op);
		
		WindowManager.setTempCurrentImage(op.getResult());
		this.getBroadcaster().changed("", "stop calculating");
	}

	private void configureAndRunOperation(Operation op) {
		op.setShowResult(false);
		op.setKeepSource(false);
		op.run();
	}

	public void showOptions() {
		this.options.view().setVisible(true);
	}

	public String name() {
		return "watershed";
	}

}
