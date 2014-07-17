package operations.segmentation;

import ij.measure.ResultsTable;
import gui.Options;
import gui.options.ChoiceOption;
import gui.options.Option;

public class IntensityPortionThresholdOperation extends ThresholdBaseOperation {
	
	private static final long serialVersionUID = 2115643805920488381L;
	protected ChoiceOption measurementToBeUsed;
	protected Option factor;
	protected ResultsTable measurements;

	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		parameterTypes = new Class[2];
		parameterTypes[0] = Class.forName("ij.ImagePlus");
		parameterTypes[1] = Class.forName("ij.measure.ResultsTable");
		parameterNames = new String[2];
		parameterNames[0] = "InputImage";
		parameterNames[1] = "Measurements";
		resultTypes = new Class[1];
		resultTypes[0] = Class.forName("ij.ImagePlus");
		resultNames = new String[1];
		resultNames[0] = "Result";
		optionsNames = new String[2];
		optionsNames[0] = "measurement to be used";
		optionsNames[1] = "factor";
	}
	
	public int getThresholdForSlice(int i) {
		String measurement = this.getMeasurementToBeUsed();
		double measuredValue = this.getMeasurements().getValueAsDouble(this.getMeasurements().getColumnIndex(measurement), 0);
		int result = (int)Math.round(measuredValue * this.getFactor());
		return result;
	}

	public double getFactor() {
		return factor.getDoubleValue();
	}

	public void setFactor(double factor) {
		this.factor.setValue(Double.toString(factor));
	}

	public String getMeasurementToBeUsed() {
		return measurementToBeUsed.getValue();
	}

	public void setMeasurementToBeUsed(String measurementToBeUsed) {
		this.measurementToBeUsed.setValue(measurementToBeUsed);
	}

	public ResultsTable getMeasurements() {
		if (measurements==null && this.getApplication()==null) this.measurements = ResultsTable.getResultsTable();
		return measurements;
	}

	public void setMeasurements(ResultsTable measurements) {
		this.measurements = measurements;
	}

	public String[] availableMeasurements() {
		String[] types = {"Mean", "Mode", "Min", "Max", "IntDen"};
		return types;
	}	
	
	protected void setupOptions() {
		options = new Options(); 
		options.setName(this.name() + " options");
		String[] optionsNames = this.getOptionsNames();
		this.measurementToBeUsed = new ChoiceOption(this.availableMeasurements());
		this.measurementToBeUsed.setValue(this.availableMeasurements()[0]);
		this.measurementToBeUsed.setName(optionsNames[0]);
		this.measurementToBeUsed.setShortHelpText("choose the measurement to calculate the threshold");
		options.add(this.measurementToBeUsed);
		this.factor = new Option();
		this.factor.setMin(0);
		this.setFactor(10.5);
		this.factor.setName(optionsNames[1]);
		this.factor.setShortHelpText("enter the value to multiply the measurement with");
		options.add(this.factor);
	}
	
	public void connectOptions() {
		this.measurementToBeUsed = (ChoiceOption) options.getOptions().get(0);
		this.factor = (Option) options.getOptions().get(1);
	}
}
