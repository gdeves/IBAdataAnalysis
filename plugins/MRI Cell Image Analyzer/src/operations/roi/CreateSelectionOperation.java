package operations.roi;

import ij.IJ;
import operations.FilterOperation;

public class CreateSelectionOperation extends FilterOperation {

	private static final long serialVersionUID = 7473625846115528096L;

	public void runFilter() {
		IJ.run("Create Selection");
	}

}
