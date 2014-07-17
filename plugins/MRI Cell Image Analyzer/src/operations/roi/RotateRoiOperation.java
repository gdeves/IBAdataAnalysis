package operations.roi;

import gui.options.Option;
import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import operations.Operation;

public class RotateRoiOperation extends Operation {
	private static final long serialVersionUID = -2809387478827979485L;
	
	Option angle;
	
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
		optionsNames = new String[1];
		optionsNames[0] = "angle";
	}
	
	public void doIt() {
		ImagePlus inputImage = this.getInputImage();
		Roi aRoi = inputImage.getRoi();
		if (aRoi==null) return;
		Roi newRoi = new PolygonRoi(aRoi.getPolygon(), Roi.POLYGON);
		inputImage.setRoi(newRoi);
		WindowManager.setTempCurrentImage(inputImage);
		float angle = this.getAngle();
		IJ.run("Rotate...", "angle=" + angle);
		WindowManager.setTempCurrentImage(null);
		result = inputImage;
	}
	
	protected void setupOptions() {
		super.setupOptions();
		setAngle(90);
		angle.setMin(0);
		angle.setMax(360);
		angle.setShortHelpText("The angle by which the roi will be rotated.");
	}
	
	public void connectOptions() {
		this.angle = (Option) this.options.getOptions().get(0);
	}

	public float getAngle() {
		return angle.getFloatValue();
	}

	public void setAngle(float angle) {
		this.angle.setFloatValue(angle);
	}
}
