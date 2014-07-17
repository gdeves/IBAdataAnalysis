package operations.roi;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.measure.ResultsTable;
import operations.Operation;
import operations.analysis.MeasureOperation;

public class NormalizeRoiRotationOperation extends Operation
{
	private static final long serialVersionUID = 8753160264436550183L;

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
		ImagePlus inputImage = this.getInputImage();
		Roi aRoi = inputImage.getRoi();
		if (aRoi==null) return;
		Roi newRoi = new PolygonRoi(aRoi.getPolygon(), Roi.POLYGON);
		inputImage.setRoi(newRoi);
		WindowManager.setTempCurrentImage(inputImage);
		MeasureOperation measure = new MeasureOperation();
		measure.setInputImage(inputImage);
		measure.setMeasureFitEllipse(true);
		measure.setShowResult(false);
		measure.run();
		ResultsTable measurements = measure.getMeasurements();
		double angle = measurements.getValue("Angle", 0);
		IJ.run("Rotate...", "angle=" + angle);
		WindowManager.setTempCurrentImage(null);
		result = inputImage;
	}
}
