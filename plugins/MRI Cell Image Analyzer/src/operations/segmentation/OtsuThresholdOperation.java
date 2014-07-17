/*
 * Created on 11.08.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package operations.segmentation;

import analysis.ThresholdFinderUtil;
import ij.process.ImageProcessor;


/**
 * @author Volker
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class OtsuThresholdOperation extends ThresholdBaseOperation {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6708928851242681591L;

	public int getThresholdForSlice(int n) {
		ImageProcessor ip = this.getInputImage().getStack().getProcessor(n);
		return (int)ThresholdFinderUtil.getOtsuThresholdFor(ip);
	}
}
