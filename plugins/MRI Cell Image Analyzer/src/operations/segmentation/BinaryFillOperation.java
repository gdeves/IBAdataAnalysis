/*
 * Created on 27.09.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package operations.segmentation;

import ij.IJ;
import operations.FilterOperation;

/**
 * @author Volker
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class BinaryFillOperation extends FilterOperation {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6030692605828232173L;

	@Override
	public void runFilter() {
		IJ.run("Fill Holes");
	}
}
