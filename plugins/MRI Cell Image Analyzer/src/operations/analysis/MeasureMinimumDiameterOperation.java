package operations.analysis;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import ij.ImagePlus;
import ij.measure.ResultsTable;
import operations.Operation;
import utils.LineProcessor;

public class MeasureMinimumDiameterOperation extends Operation {
	private static final long serialVersionUID = -6821397044955287017L;
	
	protected ResultsTable measurements;
	
	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		parameterTypes = new Class[1];
		parameterTypes[0] = Class.forName("ij.ImagePlus");
		parameterNames = new String[1];
		parameterNames[0] = "InputImage";
		resultTypes = new Class[1];
		resultTypes[0] = Class.forName("ij.measure.ResultsTable");
		resultNames = new String[1];
		resultNames[0] = "Measurements";
	}
	
	public void doIt() {
		ImagePlus inputImage = this.getInputImage();
		FindObjectsOperation analyze = new FindObjectsOperation();
		analyze.setShowResult(false);
		analyze.setInputImage(inputImage);
		analyze.setMeasureCentroids(true);
		analyze.run();
		ResultsTable table = analyze.getMeasurements();
		analyze.setInputImage(null);
		analyze.setMask(null);
		analyze.setOutlines(null);
		double centroidX = table.getValue("X", 0);
		double centroidY = table.getValue("Y", 0);
		Point2D centroid = new Point2D.Double((float)centroidX, (float)centroidY);
		Line2D line = new Line2D.Double(0f, (float)centroidY, (float)inputImage.getWidth(), (float)centroidY);
		double minDist = Integer.MAX_VALUE;
		for (int angle=270; angle<360; angle++) {
			LineProcessor.rotate(line, centroid, angle);
			Point2D currentPointOne = new Point2D.Double((float)centroidX, (float)centroidY);
			while(inputImage.getProcessor().getPixel((int)Math.round(currentPointOne.getX()), (int)Math.round(currentPointOne.getY()))>0) {
				LineProcessor.movePointAlongLine(currentPointOne, line);
			}
			Point2D currentPointTwo = new Point2D.Double((float)centroidX, (float)centroidY);
			Line2D otherLine = LineProcessor.inverseLine(line);
			while(inputImage.getProcessor().getPixel((int)Math.round(currentPointTwo.getX()), (int)Math.round(currentPointTwo.getY()))>0) {
				LineProcessor.movePointAlongLine(currentPointTwo, otherLine);
			}
			double currentDist = currentPointOne.distance(currentPointTwo);
			if (currentDist<minDist) minDist = currentDist;
		}
		for (int angle=0; angle<90; angle++) {
			LineProcessor.rotate(line, centroid, angle);
			Point2D currentPointOne = new Point2D.Double((float)centroidX, (float)centroidY);
			while(inputImage.getProcessor().getPixel((int)Math.round(currentPointOne.getX()), (int)Math.round(currentPointOne.getY()))>0) {
				LineProcessor.movePointAlongLine(currentPointOne, line);
			}
			Point2D currentPointTwo = new Point2D.Double((float)centroidX, (float)centroidY);
			Line2D otherLine = LineProcessor.inverseLine(line);
			while(inputImage.getProcessor().getPixel((int)Math.round(currentPointTwo.getX()), (int)Math.round(currentPointTwo.getY()))>0) {
				LineProcessor.movePointAlongLine(currentPointTwo, otherLine);
			}
			double currentDist = currentPointOne.distance(currentPointTwo);
			if (currentDist<minDist) minDist = currentDist;
		}
		if (this.getApplication()!=null) this.setMeasurements(null);
		this.getMeasurements().incrementCounter();
		this.getMeasurements().addValue("Width", minDist);
		analyze.setMeasurements(null);
	}

	public ResultsTable getMeasurements() {
		if  (measurements==null) measurements = new ResultsTable();
		return measurements;
	}

	public void setMeasurements(ResultsTable measurements) {
		this.measurements = measurements;
	}
	
	protected void showResult() {
		this.getMeasurements().show("shortest diameter");
	}
}
