package operations.analysis;

import java.awt.Frame;
import ij.IJ;
import ij.WindowManager;
import ij.measure.ResultsTable;
import ij.text.TextWindow;
import gui.Options;
import gui.options.BooleanOption;
import gui.options.Option;
import operations.FilterOperation;

public class FractalBoxCountOperation extends FilterOperation {

	private static final long serialVersionUID = 6862689664181263792L;
	protected Option boxSizes;
	protected Option blackBackground;
	protected ResultsTable measurements;
	
	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		optionsNames = new String[2];
		optionsNames[0] = "box sizes";
		optionsNames[1] = "black background";
		resultTypes = new Class[2];
		resultTypes[0] = Class.forName("ij.ImagePlus");
		resultTypes[1] = Class.forName("ij.measure.ResultsTable");
		resultNames = new String[2];
		resultNames[0] = "Result";
		resultNames[1] = "Measurements";
	}
	
	protected void setupOptions() {
		options = new Options(); 
		options.setName(this.name() + " options");
		String[] optionsNames = this.getOptionsNames();
		
		boxSizes = new Option();
		boxSizes.setName(optionsNames[0]);
		this.setBoxSizes("2,3,4,6,8,12,16,32,64");
		boxSizes.setShortHelpText("Enter the box sizes");
		options.add(boxSizes);
		
		blackBackground = new BooleanOption();
		blackBackground.setName(optionsNames[1]);
		this.setBlackBackground(true);
		blackBackground.setShortHelpText("Select if the background is black.");
		options.add(blackBackground);
	}
	
	public void connectOptions() {
		this.boxSizes = (Option) this.options.getOptions().get(0);
		this.blackBackground = (BooleanOption) this.options.getOptions().get(1);
	}
	
	@Override
	public void runFilter() {
		String background ="";
		if (getBlackBackground()) background = "black";
		IJ.run("Fractal Box Count...", "box=" + this.getBoxSizes()+ " " + background);
		copyResultTextToTable();
		ResultsTable.getResultsTable().reset();
		Frame frame = WindowManager.getFrame("Results");
		((TextWindow)frame).close();
	}

	protected void copyResultTextToTable() {
		setMeasurements((ResultsTable)ResultsTable.getResultsTable().clone());
	}

	public boolean getBlackBackground() {
		return blackBackground.getBooleanValue();
	}

	public void setBlackBackground(boolean blackBackground) {
		this.blackBackground.setBooleanValue(blackBackground);
	}

	public String getBoxSizes() {
		return boxSizes.getValue();
	}

	public void setBoxSizes(String boxSizes) {
		this.boxSizes.setValue(boxSizes);
	}

	public ResultsTable getMeasurements() {
		return measurements;
	}

	public void setMeasurements(ResultsTable measurements) {
		this.measurements = measurements;
	}
	
	protected void showResult() {
		if (this.getResult()==null) return;
		this.getResult().show();
		if (this.getMeasurements()==null) return;
		this.getMeasurements().show("fractal box count measurements");
	}
}
