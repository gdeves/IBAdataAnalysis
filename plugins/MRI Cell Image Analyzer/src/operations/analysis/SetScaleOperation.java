package operations.analysis;

import gui.Options;
import gui.options.Option;
import ij.IJ;
import operations.FilterOperation;

public class SetScaleOperation extends FilterOperation {
	private static final long serialVersionUID = 1265222387531245006L;
	protected Option measuredLength;	// distance in pixels
	protected Option length;			// known distance
	protected Option unit;				
	
	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		optionsNames = new String[3];
		optionsNames[0] = "measured length";
		optionsNames[1] = "length";
		optionsNames[2] = "unit";
	}

	public void runFilter() {
		float distance = this.getMeasuredLength();
		String parameter = "distance="+ distance + " known="+ this.getLength()+" pixel=1 unit=" + this.getUnit();
		IJ.run("Set Scale...", parameter);
	}

	public float getLength() {
		return length.getFloatValue();
	}

	public void setLength(float length) {
		this.length.setFloatValue(length);
	}

	public float getMeasuredLength() {
		return measuredLength.getFloatValue();
	}

	public void setMeasuredLength(float measuredLength) {
		this.measuredLength.setFloatValue(measuredLength);
	}

	public String getUnit() {
		return unit.getValue();
	}

	public void setUnit(String unit) {
		this.unit.setValue(unit);
	}

	protected void setupOptions() {
		options = new Options(); 
		options.setName(this.name() + " options");
		String[] optionsNames = this.getOptionsNames();
	
		this.measuredLength = new Option();
		this.setMeasuredLength(1);
		this.measuredLength.setName(optionsNames[0]);
		this.measuredLength.setMin(1);
		this.measuredLength.setShortHelpText("Enter the distance in pixels");
		options.add(measuredLength);
		
		this.length = new Option();
		this.setLength(1);
		this.length.setName(optionsNames[1]);
		this.length.setMin(0);
		this.length.setShortHelpText("Enter the known distance");
		options.add(length);
		
		this.unit = new Option();
		this.setUnit("pixel");
		this.unit.setName(optionsNames[2]);
		this.unit.setShortHelpText("Enter the unit of the known distance");
		options.add(unit);
	}
	
	public void connectOptions() {
		this.measuredLength = (Option) this.options.getOptions().get(0);
		this.length = (Option) this.options.getOptions().get(1);
		this.unit = (Option) this.options.getOptions().get(2);
	}
}
