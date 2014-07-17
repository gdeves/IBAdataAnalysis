package operations.processing;

import ij.IJ;
import gui.options.Option;
import operations.FilterOperation;

public class TranslateImageOperation extends FilterOperation {

	protected Option deltaX;
	protected Option deltaY;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8040014667689379115L;

	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		optionsNames = new String[2];
		optionsNames[0] = "delta x";
		optionsNames[1] = "delta y";
	}
	
	public int getDeltaX() {
		return deltaX.getIntegerValue();
	}

	public void setDeltaX(int deltaX) {
		this.deltaX.setValue(Integer.toString(deltaX));
	}

	public int getDeltaY() {
		return deltaY.getIntegerValue();
	}

	public void setDeltaY(int deltaY) {
		this.deltaY.setValue(Integer.toString(deltaY));
	}
	
	public void connectOptions() {
		deltaX = (Option)options.getOptions().get(0);
		deltaY = (Option)options.getOptions().get(1);
	}
	
	protected void setupOptions() {
		super.setupOptions();
		this.setDeltaX(10);
		this.setDeltaY(10);
		deltaX.setShortHelpText("translation in x direction, use negative values to move to the left");
		deltaY.setShortHelpText("translation in Y direction, use negative values to move up");
	}
	
	public void runFilter() {
		IJ.run("Select All");
	    IJ.run("Cut");
	    IJ.run("Fill");
	    IJ.makeRectangle(this.getDeltaX(), this.getDeltaY(), inputImage.getWidth(), inputImage.getHeight());
	    IJ.run("Paste");
	    IJ.run("Select None");
	}
	
}
