package segmentation;

import ij.ImagePlus;
import ij.gui.Roi;
import ij.measure.Calibration;
import ij.process.ImageProcessor;
import ij.process.ImageStatistics;

import java.util.ArrayList;
import java.util.Iterator;

public class Measurements {
	protected ImageObject object; 
	protected ArrayList<String> activeMeasurements;
	protected double area;
	protected ImageProcessor ip;
	protected ImagePlus image;
	protected Calibration calibration; 
	
	public Measurements(ImageObject object) {
		this.object = object;
		activeMeasurements = new ArrayList<String>();
		activeMeasurements.add("area");
		ip = object.getImage().getProcessor();
		image = object.getImage();
		calibration = image.getCalibration(); 
	}
	
	static public String[] availableMeasurements() {
		String[] measurements = {"area"};
		return measurements;
	}

	public void run() {
		Roi oldRoi = image.getRoi();
		image.setRoi(object.getRoi());
		Iterator<String> it = activeMeasurements.iterator();
		while(it.hasNext()) {
			String measurement = it.next();
			this.measure(measurement);
		}
		image.setRoi(oldRoi);
	}

	protected void measure(String measurement) {
		if (measurement.equals("area")) this.measureArea();
	}

	public void measureArea() {
		area = image.getStatistics(ImageStatistics.AREA).area;
	}

	public double getArea() {
		return area;
	}
	
	public String getMeasurementsString() {
		String result = "";
		if (activeMeasurements.contains("area")) {
			result += " | area="+this.getArea(); 
		}
		return result;
	}

	public double getMeasure(String measurement) {
		double result = -1;
		if (measurement.equals("area")) return getArea();
		return result;
	}
}
