/*
 * Created on 11.10.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package operations.roi;

import operations.Operation;

/**
 * @author Volker
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ClearOperation extends Operation {
	private static final long serialVersionUID = 7984297142558111472L;

	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		parameterTypes = new Class[1];
		parameterTypes[0] = Class.forName("ij.ImagePlus");
		parameterNames = new String[1];
		parameterNames[0] = "InputImage";
		resultTypes = new Class[1];
		resultTypes[0] = Class.forName("ij.ImagePlus");
		resultNames = new String[1];
		resultNames[0] = "Result";
	}
	
	public void doIt() {
		result = this.getInputImage();
/*		if (keepSource) {
			Duplicater copyAction = new Duplicater();
			result = copyAction.duplicateStack(inputImage, inputImage.getTitle());
			result.getProcessor().setMask(inputImage.getMask());
			result.setRoi(inputImage.getRoi());
			result.restoreRoi();
		} */
		result.getProcessor().fill(result.getMask());
		result.updateAndDraw();
	}
}
