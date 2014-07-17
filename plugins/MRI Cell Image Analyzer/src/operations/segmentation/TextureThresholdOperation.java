package operations.segmentation;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.NewImage;
import ij.gui.Roi;
import ij.measure.ResultsTable;
import gui.Options;
import gui.options.ChoiceOption;
import gui.options.Option;
import operations.FilterOperation;

public class TextureThresholdOperation extends FilterOperation {
	protected Option radius;
	protected Option step;
	protected Option angle;
	protected Option angularSecondMomentMin;
	protected Option angularSecondMomentMax;
	protected Option contrastMin;
	protected Option contrastMax;
	protected Option correlationMin;
	protected Option correlationMax;
	protected Option inverseDifferenceMomentMin;
	protected Option inverseDifferenceMomentMax;
	protected Option entropyMin;
	protected Option entropyMax;
	/**
	 * 
	 */
	private static final long serialVersionUID = -4696429934470876987L;
	
	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		optionsNames = new String[13];
		optionsNames[0] = "radius";
		optionsNames[1] = "step";
		optionsNames[2] = "angle";
		optionsNames[3] = "angular second moment min";
		optionsNames[4] = "angular second moment max";
		optionsNames[5] = "contrast min";
		optionsNames[6] = "contrast max";
		optionsNames[7] = "correlation min";
		optionsNames[8] = "correlation max";
		optionsNames[9] = "inverse difference moment min";
		optionsNames[10] = "inverse difference moment max";
		optionsNames[11] = "entropy min";
		optionsNames[12] = "entropy max";
	}
	
	public void doIt() {
		ImagePlus inputImage = this.getInputImage();
		result =  NewImage.createByteImage(inputImage.getTitle() + " texture mask", inputImage.getWidth(), 
											inputImage.getHeight(), 1, NewImage.FILL_WHITE);
		WindowManager.setTempCurrentImage(inputImage);
		this.runFilter();
		WindowManager.setTempCurrentImage(null);
	}
	
	public void runFilter() {
		WindowManager.setTempCurrentImage(inputImage);
		String command = "GLCM Texture";
		String options = "enter=" + this.getStep() + " select=["+this.getAngle()+"]";
		boolean measureAngularSecondMoment = false;
		boolean measureContrast = false;
		boolean measureCorrelation = false;
		boolean measureInverseDifferenceMoment = false;
		boolean measureEntropy = false;
		
		if (this.getAngularSecondMomentMin()!=angularSecondMomentMin.getMin().doubleValue() || 
				this.getAngularSecondMomentMax()!=angularSecondMomentMax.getMax().doubleValue()) {
			options = options + " angular";
			measureAngularSecondMoment = true;
		}
		if (this.getContrastMin()!=contrastMin.getMin().doubleValue() || 
				this.getContrastMax()!=contrastMax.getMax().doubleValue()) {
			options = options + " contrast";
			measureContrast = true;
		}
		if (this.getCorrelationMin()!=correlationMin.getMin().doubleValue() || 
				this.getCorrelationMax()!=correlationMax.getMax().doubleValue()) {
			options = options + " correlation";
			measureCorrelation = true;
		}
		if (this.getInverseDifferenceMomentMin()!=inverseDifferenceMomentMin.getMin().doubleValue() || 
				this.getInverseDifferenceMomentMax()!=inverseDifferenceMomentMax.getMax().doubleValue()) {
			options = options + " inverse";
			measureInverseDifferenceMoment = true;
		}
		if (this.getEntropyMin()!=entropyMin.getMin().doubleValue() || 
				this.getEntropyMax()!=entropyMax.getMax().doubleValue()) {
			options = options + " entropy";
			measureEntropy = true;
		}
		int radius = this.getRadius();
		Roi aRoi = new Roi(0,0,radius,radius);
		inputImage.setRoi(aRoi);
		for (int x=0; x<inputImage.getWidth(); x++) {
			for (int y=0; y<inputImage.getHeight(); y++) {
				aRoi.setLocation(x, y);
				IJ.run(command, options);
				ResultsTable table = ResultsTable.getResultsTable();
				if (measureAngularSecondMoment) {
					double angularSecondMoment = table.getValue("Angular Second Moment", 0);
					if (angularSecondMoment<getAngularSecondMomentMin() || angularSecondMoment>getAngularSecondMomentMax()) continue; 
				}
				if (measureContrast) {
					double contrast = table.getValue("Contrast", 0);
					if (contrast<getContrastMin() || contrast>getContrastMax()) continue; 
				}
				if (measureCorrelation) {
					double correlation = table.getValue("Correlation", 0);
					if (correlation<getCorrelationMin() || correlation>getCorrelationMax()) continue; 
				}
				if (measureInverseDifferenceMoment) {
					double inverseDifferenceMoment = table.getValue("Inverse Difference Moment", 0);
					if (inverseDifferenceMoment<getInverseDifferenceMomentMin() || inverseDifferenceMoment>getInverseDifferenceMomentMax()) continue; 
				}
				if (measureEntropy) {
					double entropy = table.getValue("Entropy", 0);
					if (entropy<getEntropyMin() || entropy>getEntropyMax()) continue; 
				}
				result.getProcessor().set(x, y, 0);
			}
		}
	}
	
	public String getAngle() {
		return angle.getValue();
	}
	public void setAngle(String angle) {
		this.angle.setValue(angle);
	}
	
	public double getAngularSecondMomentMin() {
		return angularSecondMomentMin.getDoubleValue();
	}
	public void setAngularSecondMomentMin(double angularSecondMomentMin) {
		this.angularSecondMomentMin.setDoubleValue(angularSecondMomentMin);
	}
	
	public double getAngularSecondMomentMax() {
		return angularSecondMomentMax.getDoubleValue();
	}
	public void setAngularSecondMomentMax(double angularSecondMomentMax) {
		this.angularSecondMomentMax.setDoubleValue(angularSecondMomentMax);
	}
	public double getContrastMax() {
		return contrastMax.getDoubleValue();
	}
	public void setContrastMax(double contrastMax) {
		this.contrastMax.setDoubleValue(contrastMax);
	}
	public double getContrastMin() {
		return contrastMin.getDoubleValue();
	}
	public void setContrastMin(double contrastMin) {
		this.contrastMin.setDoubleValue(contrastMin);
	}
	public double getCorrelationMin() {
		return correlationMin.getDoubleValue();
	}
	public void setCorrelationMin(double correlationMin) {
		this.correlationMin.setDoubleValue(correlationMin);
	}
	public double getCorrelationMax() {
		return correlationMax.getDoubleValue();
	}
	public void setCorrelationMax(double correlationMax) {
		this.correlationMax.setDoubleValue(correlationMax);
	}
	public double getEntropyMax() {
		return entropyMax.getDoubleValue();
	}
	public void setEntropyMax(double entropyMax) {
		this.entropyMax.setDoubleValue(entropyMax);
	}
	public double getEntropyMin() {
		return entropyMin.getDoubleValue();
	}
	public void setEntropyMin(double entropyMin) {
		this.entropyMin.setDoubleValue(entropyMin);
	}
	public double getInverseDifferenceMomentMax() {
		return inverseDifferenceMomentMax.getDoubleValue();
	}
	public void setInverseDifferenceMomentMax(double inverseDifferenceMomentMax) {
		this.inverseDifferenceMomentMax.setDoubleValue(inverseDifferenceMomentMax);
	}
	public double getInverseDifferenceMomentMin() {
		return inverseDifferenceMomentMin.getDoubleValue();
	}
	public void setInverseDifferenceMomentMin(double inverseDifferenceMomentMin) {
		this.inverseDifferenceMomentMin.setDoubleValue(inverseDifferenceMomentMin);
	}
	public int getRadius() {
		return radius.getIntegerValue();
	}
	public void setRadius(int radius) {
		this.radius.setIntegerValue(radius);
	}
	public int getStep() {
		return step.getIntegerValue();
	}
	public void setStep(int step) {
		this.step.setIntegerValue(step);
	}

	protected void setupOptions() {
		options = new Options(); 
		options.setName(this.name() + " options");
		String[] optionsNames = this.getOptionsNames();
		
		this.radius = new Option();
		radius.setName(optionsNames[0]);
		this.setRadius(25);
		radius.setMin(1);
		radius.setShortHelpText("the radius of the neighborhood in which the texture is analyzed");
		options.add(this.radius);
		
		this.step = new Option();
		step.setName(optionsNames[1]);
		this.setStep(1);
		step.setMin(1);
		step.setShortHelpText("the step size for the texture analysis");
		options.add(this.step);
		
		this.angle = new ChoiceOption(this.getAngleChoices());
		angle.setName(optionsNames[2]);
		this.setAngle(getAngleChoices()[1]);
		angle.setShortHelpText("select the direction for the texture analysis");
		options.add(this.angle);
		
		this.angularSecondMomentMin = new Option();
		angularSecondMomentMin.setName(optionsNames[3]);
		this.setAngularSecondMomentMin(0);
		angularSecondMomentMin.setMin(0);
		angularSecondMomentMin.setMax(1);
		options.add(this.angularSecondMomentMin);
		
		this.angularSecondMomentMax = new Option();
		angularSecondMomentMax.setName(optionsNames[4]);
		this.setAngularSecondMomentMax(1);
		angularSecondMomentMax.setMin(0);
		angularSecondMomentMax.setMax(1);
		options.add(this.angularSecondMomentMax);
		
		this.contrastMin = new Option();
		contrastMin.setName(optionsNames[5]);
		this.setContrastMin(0);
		contrastMin.setMin(0);
		contrastMin.setMax(999999);
		options.add(this.contrastMin);
		
		this.contrastMax = new Option();
		contrastMax.setName(optionsNames[6]);
		this.setContrastMax(999999);
		contrastMax.setMin(0);
		contrastMax.setMax(999999);
		options.add(this.contrastMax);
		
		this.correlationMin = new Option();
		correlationMin.setName(optionsNames[7]);
		this.setCorrelationMin(0);
		correlationMin.setMin(0);
		correlationMin.setMax(1);
		options.add(this.correlationMin);
		
		this.correlationMax = new Option();
		correlationMax.setName(optionsNames[8]);
		this.setCorrelationMax(1);
		correlationMax.setMin(0);
		correlationMax.setMax(1);
		options.add(this.correlationMax);
		
		this.inverseDifferenceMomentMin = new Option();
		inverseDifferenceMomentMin.setName(optionsNames[9]);
		this.setInverseDifferenceMomentMin(0);
		inverseDifferenceMomentMin.setMin(0);
		inverseDifferenceMomentMin.setMax(1);
		options.add(this.inverseDifferenceMomentMin);
		
		this.inverseDifferenceMomentMax = new Option();
		inverseDifferenceMomentMax.setName(optionsNames[10]);
		this.setInverseDifferenceMomentMax(1);
		inverseDifferenceMomentMax.setMin(0);
		inverseDifferenceMomentMax.setMax(1);
		options.add(this.inverseDifferenceMomentMax);
		
		this.entropyMin = new Option();
		entropyMin.setName(optionsNames[11]);
		this.setEntropyMin(0);
		entropyMin.setMin(0);
		entropyMin.setMax(1);
		options.add(this.entropyMin);
		
		this.entropyMax = new Option();
		entropyMax.setName(optionsNames[12]);
		this.setEntropyMax(999999);
		entropyMax.setMin(0);
		entropyMax.setMax(999999);
		options.add(this.entropyMax);	
	}
	
	protected String[] getAngleChoices() {
		String[] choices = {"0 degrees", "90 degrees", "180 degrees", "270 degrees"};
		return choices;
	}
	
	public void connectOptions() {
		this.radius = (Option) this.options.getOptions().get(0);
		this.step = (Option) this.options.getOptions().get(1);
		this.angle = (ChoiceOption) this.options.getOptions().get(2);
		this.angularSecondMomentMin = (Option) this.options.getOptions().get(3);	
		this.angularSecondMomentMax = (Option) this.options.getOptions().get(4);
		this.contrastMin = (Option) this.options.getOptions().get(5);
		this.contrastMax = (Option) this.options.getOptions().get(6);
		this.correlationMin = (Option) this.options.getOptions().get(7);
		this.correlationMax = (Option) this.options.getOptions().get(8);
		this.inverseDifferenceMomentMin = (Option) this.options.getOptions().get(9);
		this.inverseDifferenceMomentMax = (Option) this.options.getOptions().get(10);
		this.entropyMin = (Option) this.options.getOptions().get(11);
		this.entropyMax = (Option) this.options.getOptions().get(12);
	}
}
