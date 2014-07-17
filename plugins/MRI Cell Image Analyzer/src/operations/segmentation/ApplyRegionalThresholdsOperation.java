package operations.segmentation;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.Roi;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import gui.Options;
import gui.options.ChoiceOption;
import gui.options.Option;
import operations.Operation;
import utils.ImageDuplicator;

public class ApplyRegionalThresholdsOperation extends Operation {

	protected ChoiceOption thresholdMethod;
	protected Option radius;
	protected Hashtable<String, Operation> thresholdOperations;
	private static final long serialVersionUID = 1L;
	
	public ApplyRegionalThresholdsOperation() {
		super();
	}

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
		optionsNames = new String[2];
		optionsNames[0] = "threshold method";
		optionsNames[1] = "radius";
	}
	
	public void doIt() {
		ImagePlus inputImage = this.getInputImage();
		result = this.getCopyOfOrReferenceTo(inputImage, inputImage.getTitle());
		ImagePlus tmp1 = ImageDuplicator.copyImage(result, "tmp1");
		ImagePlus tmp2 = ImageDuplicator.copyImage(result, "tmp2");
		int radius = this.getRadius();
		for (int x=radius; x<inputImage.getWidth(); x=x+(2*radius+1)) {
			for (int y=radius; y<inputImage.getHeight(); y=y+(2*radius+1)) {
				System.out.println(x + ", " + y);
				int width = 2*this.getRadius()+1;
				if (x+radius>=inputImage.getWidth()) {
					width = inputImage.getWidth()-x / 2;
				}
				Roi roi = new Roi(x-radius, y-radius, width, width);
				tmp1.setRoi(roi);
				Operation threshold = this.thresholdOperations.get(this.getThresholdMethod());
				threshold.setInputImage(tmp1);
				threshold.setShowResult(false);
				threshold.run();
				ImagePlus thresholdedRegion = threshold.getResult();
				WindowManager.setTempCurrentImage(thresholdedRegion);
				IJ.run("Copy");
				tmp2.setRoi(roi);
				WindowManager.setTempCurrentImage(tmp2);
				IJ.run("Paste");
			}
		}
		result = tmp2;
	}
	
	protected void setupOptions() {
		options = new Options(); 
		options.setName(this.name() + " options");
		String[] optionsNames = this.getOptionsNames();
		this.thresholdMethod = new ChoiceOption(this.thresholdMethods());
		this.thresholdMethod.setValue(this.thresholdMethods()[0]);
		this.thresholdMethod.setName(optionsNames[0]);
		this.thresholdMethod.setShortHelpText("choose the threshold method");
		options.add(this.thresholdMethod);
		this.radius = new Option();
		this.radius.setMin(1);
		this.radius.setName(optionsNames[1]);
		this.setRadius(9);
		this.radius.setShortHelpText("enter the radius of the local threshold area");
		options.add(this.radius);
	}

	public String getThresholdMethod() {
		return thresholdMethod.getValue();
	}

	public void setThresholdMethod(String thresholdMethod) {
		this.thresholdMethod.setValue(thresholdMethod);
	}

	public int getRadius() {
		return radius.getIntegerValue();
	}

	public void setRadius(int radius) {
		this.radius.setValue(Integer.toString(radius));
	}
	
	public Hashtable<String, Operation> thresholdOperations() {
		if (this.thresholdOperations == null) {
			this.setupThresholdOperations();
		}
		return thresholdOperations;
	}
	
	protected void setupThresholdOperations() {
		this.thresholdOperations = new Hashtable<String, Operation>();
		Operation op = new AutoThresholdOperation();
		this.thresholdOperations.put(op.name(), op);
		op = new EntropyThresholdOperation();
		this.thresholdOperations.put(op.name(), op);
		op = new MeanThresholdOperation();
		this.thresholdOperations.put(op.name(), op);
		op = new MedianThresholdOperation();
		this.thresholdOperations.put(op.name(), op);
		op = new OtsuThresholdOperation();
		this.thresholdOperations.put(op.name(), op);
	}

	public String[] thresholdMethods() {
		String[] methods = new String[this.thresholdOperations().size()];
		Iterator<String> it = this.thresholdOperations().keySet().iterator();
		int counter = 0;
		while(it.hasNext()) {
			String method = it.next();
			methods[counter] = method;
			counter++;
		}
		Arrays.sort(methods);
		return methods;
	}
	
	public void connectOptions() {
		thresholdMethod = (ChoiceOption) options.getOptions().get(0);
		radius = (Option) options.getOptions().get(1);
	}
}
