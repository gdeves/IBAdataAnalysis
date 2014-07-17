/*
 * Created on 11.08.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package operations.segmentation;

/**
 * @author Volker
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AutoThresholdOperation extends ThresholdBaseOperation {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8923412655049364326L;

	public int getThresholdForSlice(int i) {
		return this.getInputImage().getStack().getProcessor(i).getAutoThreshold();
	}
}
