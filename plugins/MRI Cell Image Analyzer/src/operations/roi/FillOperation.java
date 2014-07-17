package operations.roi;

import operations.Operation;

public class FillOperation extends Operation {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4957638028161157745L;
	
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
		result.getProcessor().setValue(255);
		result.getProcessor().fill(result.getMask());
		result.getProcessor().setValue(0);
		result.updateAndDraw();
	}
}
