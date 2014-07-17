/*
 * Created on 11.08.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package operations.segmentation;

import gui.options.Option;

/**
 * @author Volker
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ThresholdOperation extends ThresholdBaseOperation {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 466852060324755287L;
	protected Option threshold;
	
	/**
	 * @throws ClassNotFoundException
	 *  
	 */
	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		optionsNames = new String[1];
		optionsNames[0] = "threshold";
	}

	 /**
	 * @return
	 */
	public int getThreshold() {
		return threshold.getIntegerValue();
	}
	
	public void setThreshold(int threshold) {
		this.threshold.setValue(new Integer(threshold).toString());
	}
	
	protected void setupOptions() {
		super.setupOptions();
		this.setThreshold(128);
		threshold.setMin(0);
		threshold.setShortHelpText("all intensities above threshold" +
									" will be mapped to 255 and all below to 0");
	}
	
	public void connectOptions() {
		this.threshold = (Option) this.options.getOptions().get(0);
	}

	/* (non-Javadoc)
	 * @see operations.segmentation.ThresholdBaseOperation#getThresholdForSlice(int)
	 */
	public int getThresholdForSlice(int i) {
		return this.getThreshold();
	}
	
	
}
