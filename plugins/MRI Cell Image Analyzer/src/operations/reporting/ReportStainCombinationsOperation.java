package operations.reporting;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import reporting.ExcelIO;
import reporting.ExcelWriter;
import ij.measure.ResultsTable;
import gui.Options;
import gui.options.Option;

public class ReportStainCombinationsOperation extends ReportingOperation {
	protected Option radius;
	protected ResultsTable blueMeasurements;
	protected ResultsTable redMeasurements;
	protected ResultsTable greenMeasurements;
	protected ArrayList<Point2D> doubleNuclei;
	protected ArrayList<Point2D> notGreen;
	
	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		// Set parameterTypes, parameterNames and resultType
		parameterTypes = new Class[4];
		parameterTypes[0] = Class.forName("ij.measure.ResultsTable");
		parameterTypes[1] = Class.forName("java.lang.String");
		parameterTypes[2] = Class.forName("ij.measure.ResultsTable");
		parameterTypes[3] = Class.forName("ij.measure.ResultsTable");
		parameterNames = new String[4];
		parameterNames[0] = "BlueMeasurements";
		parameterNames[1] = "ImageName";
		parameterNames[2] = "RedMeasurements";
		parameterNames[3] = "GreenMeasurements";
		optionsNames = new String[2];
		optionsNames[0] = "output path";
		optionsNames[1] = "radius";
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -7004386436274503306L;

	protected String headingsFirstSheet() {
		String headings = "image\ttotal\tgreen\tred\tdouble nuclei\t" +
				"!g & r\t" +
				"g & r\t" + 
				"g/t\t" +
				"!g & r / (t-g)\t" + 
				"g & r / g\t" +
				"g & 2n / g\t" + 
				"!g & 2n / (t-g)\t" +
				"g & r & 2n / (g & 2n)\t" +
				"g & r & 2n / (2n & !g)\t"
				;
		headings = headings + "\tfolder";
		return headings;
	}

	protected String getSheetName(int i) {
		String[] names = {"count of stain combinations"};
		return names[i];
	}

	public ResultsTable getBlueMeasurements() {
		return blueMeasurements;
	}

	public void setBlueMeasurements(ResultsTable firstChannelMeasurements) {
		this.blueMeasurements = firstChannelMeasurements;
	}

	public int getRadius() {
		return radius.getIntegerValue();
	}

	public void setRadius(int radius) {
		this.radius.setValue(Integer.toString(radius));
	}

	public ResultsTable getRedMeasurements() {
		return redMeasurements;
	}

	public void setRedMeasurements(ResultsTable secondChannelMeasurements) {
		this.redMeasurements = secondChannelMeasurements;
	}

	public ResultsTable getGreenMeasurements() {
		return greenMeasurements;
	}

	public void setGreenMeasurements(ResultsTable thirdChannelMeasurements) {
		this.greenMeasurements = thirdChannelMeasurements;
	}

	protected void setupOptions() {
		options = new Options();
		options.setName(this.name() + " options");
		String[] optionsNames = this.getOptionsNames();
		this.outputPath = new Option();
		this.outputPath.setName(optionsNames[0]);
		options.add(outputPath);
		this.radius = new Option();
		this.radius.setValue("50");
		this.radius.setName(optionsNames[1]);
		options.add(this.radius);
	}

	public void connectOptions() {
		outputPath = (Option) options.getOptions().get(0); 
		radius = (Option) options.getOptions().get(1);
	}
	
	public void doIt() {
		setupOutputPath();
		ExcelIO excelIO = this.loadOrCreateWorkbook(10);
		ExcelWriter excelProxy = new ExcelWriter();
		excelProxy.setWb(excelIO.wb);
		excelProxy.setSheet(excelIO.s);
		excelProxy.setSheet(excelIO.wb.getSheetAt(0));
		this.createHeadingsFirstSheet(excelProxy, excelIO);
		this.report(excelIO, excelProxy);
		this.saveWorkbook(excelIO.wb);
	}

	private void report(ExcelIO excelIO, ExcelWriter excelProxy) {
		excelProxy.setSheet(excelIO.wb.getSheetAt(0));
		int b = this.getBlueCount();
		int g = this.getGreenCount();
		int r = this.getRedCount();
		int dn = this.getDoubleNucleusCount();
		int ng = b - g;
		int notGreenButRed = this.getNotGreenButRedCount();
		int greenAndRed = this.getGreenAndRedCount();
		int greenAndDoubleNucleus = this.getGreenAndDoubleNucleusCount();
		int notGreenButDoubleNucleus = this.getNotGreenButDoubleNucleusCount();
		int greenAndRedAndDoubleNucleus = this.getGreenAndRedAndDoubleNucleusCount();
		
		String row = this.getImageName() + "\t";
		row = row + Integer.toString(b) + "\t";
		row = row + Integer.toString(g) + "\t";
		row = row + Integer.toString(r) + "\t";
		row = row + Integer.toString(dn) + "\t";
		
		row = row + Integer.toString(notGreenButRed) + "\t";
		row = row + Integer.toString(greenAndRed) + "\t";
		
		row = row + Float.toString(g / (b * 1.0f)) + "\t";
		row = row + Float.toString(notGreenButRed / (ng * 1.0f)) + "\t";
		row = row + Float.toString(greenAndRed / (g * 1.0f)) + "\t";
		
		row = row + Float.toString(greenAndDoubleNucleus / (g * 1.0f)) + "\t";
		row = row + Float.toString(notGreenButDoubleNucleus / (ng * 1.0f)) + "\t";
		row = row + Float.toString(greenAndRedAndDoubleNucleus / (greenAndDoubleNucleus * 1.0f)) + "\t";
		row = row + Float.toString(greenAndRedAndDoubleNucleus / (notGreenButDoubleNucleus * 1.0f)) + "\t";
		
		row = row + this.getImageFolder();
		excelProxy.writeTabSeparatedDataAsRow(row);
	}

	protected int getGreenAndRedCount() {
		ResultsTable greenTable = this.getGreenMeasurements();
		ResultsTable redTable = this.getRedMeasurements();
		int count = 0;
		for (int i = 0; i<redTable.getCounter(); i++) {
			double x1 = redTable.getValueAsDouble(ResultsTable.X_CENTROID, i);
			double y1 = redTable.getValueAsDouble(ResultsTable.Y_CENTROID, i);
			Point2D p1 = new Point2D.Double(x1,y1);
			for (int j = 0; j<greenTable.getCounter(); j++) {
				double x2 = greenTable.getValueAsDouble(ResultsTable.X_CENTROID, j);
				double y2 = greenTable.getValueAsDouble(ResultsTable.Y_CENTROID, j);
				Point2D p2 = new Point2D.Double(x2,y2);
				if (p1.distance(p2)<this.getRadius()) {
					count++;
				}
			}
		}
		return count;
	}

	protected int getNotGreenButRedCount() {
		ResultsTable greenTable = this.getGreenMeasurements();
		ResultsTable redTable = this.getRedMeasurements();
		int count = redTable.getCounter();
		for (int i = 0; i<redTable.getCounter(); i++) {
			double x1 = redTable.getValueAsDouble(ResultsTable.X_CENTROID, i);
			double y1 = redTable.getValueAsDouble(ResultsTable.Y_CENTROID, i);
			Point2D p1 = new Point2D.Double(x1,y1);
			for (int j = 0; j<greenTable.getCounter(); j++) {
				double x2 = greenTable.getValueAsDouble(ResultsTable.X_CENTROID, j);
				double y2 = greenTable.getValueAsDouble(ResultsTable.Y_CENTROID, j);
				Point2D p2 = new Point2D.Double(x2,y2);
				if (p1.distance(p2)<this.getRadius()) {
					count--;
				}
			}
		}
		return count;
	}

	protected int getDoubleNucleusCount() {
		int count = this.getDoubleNuclei().size();
		return count;
	}
	
	protected ArrayList<Point2D> getDoubleNuclei() {
		if (doubleNuclei==null) {
			doubleNuclei = new ArrayList<Point2D>();
			ResultsTable table = this.getBlueMeasurements();
			for (int i = 0; i<table.getCounter(); i++) {
				double x1 = table.getValueAsDouble(ResultsTable.X_CENTROID, i);
				double y1 = table.getValueAsDouble(ResultsTable.Y_CENTROID, i);
				Point2D p1 = new Point2D.Double(x1,y1);
				for (int j = i+1; j<table.getCounter(); j++) {
					double x2 = table.getValueAsDouble(ResultsTable.X_CENTROID, j);
					double y2 = table.getValueAsDouble(ResultsTable.Y_CENTROID, j);
					Point2D p2 = new Point2D.Double(x2,y2);
					if (p1.distance(p2)<this.getRadius()) {
						doubleNuclei.add(p1);
					}
				}
			}
		}
		
		return doubleNuclei;
	}
	
	protected int getGreenAndRedAndDoubleNucleusCount() {
		int count = 0;
		ResultsTable redTable = this.getRedMeasurements();
		ResultsTable greenTable = this.getGreenMeasurements();
		for (int i=0; i<this.getDoubleNuclei().size(); i++) {
			Point2D p1 = this.getDoubleNuclei().get(i);
			for (int j=0; j<greenTable.getCounter(); j++) {
				double x2 = greenTable.getValueAsDouble(ResultsTable.X_CENTROID, j);
				double y2 = greenTable.getValueAsDouble(ResultsTable.Y_CENTROID, j);
				Point2D p2 = new Point2D.Double(x2,y2);
				for (int k=0; k<redTable.getCounter(); k++) {
					double x3 = redTable.getValueAsDouble(ResultsTable.X_CENTROID, k);
					double y3 = redTable.getValueAsDouble(ResultsTable.Y_CENTROID, k);
					Point2D p3 = new Point2D.Double(x3,y3);
					if (p1.distance(p2)<this.getRadius() && p1.distance(p3)<this.getRadius()) {
						count++;
					}
				}
			}
		}
		return count;
	}
	
	protected int getGreenAndDoubleNucleusCount() {
		int count = 0;
		ResultsTable greenTable = this.getGreenMeasurements();
			for (int i = 0; i<greenTable.getCounter(); i++) {
				double x1 = greenTable.getValueAsDouble(ResultsTable.X_CENTROID, i);
				double y1 = greenTable.getValueAsDouble(ResultsTable.Y_CENTROID, i);
				Point2D p1 = new Point2D.Double(x1,y1);
				for (int j = 0; j<this.getDoubleNuclei().size(); j++) {
					Point2D p2 = this.getDoubleNuclei().get(j);
					if (p1.distance(p2)<this.getRadius()) {
						count++;
					}
				}
				
		}
		return count;
	}
	
	protected int getNotGreenButDoubleNucleusCount() {
		int count = 0;
		for (int i = 0; i<this.getNotGreen().size(); i++) {
			Point2D p1 = this.getNotGreen().get(i); 
			for (int j = 0; j<this.getDoubleNuclei().size(); j++) {
				Point2D p2 = this.getDoubleNuclei().get(j);
				if (p1.distance(p2)<this.getRadius()) {
					count++;
				}
			}
		}
		return count;
	}
	
	
	protected ArrayList<Point2D> getNotGreen() {
		if (notGreen==null) {
			notGreen = new ArrayList<Point2D>();
			ResultsTable blueTable = this.getBlueMeasurements();
			ResultsTable greenTable = this.getGreenMeasurements();
			for (int i = 0; i<blueTable.getCounter(); i++) {
				double x1 = blueTable.getValueAsDouble(ResultsTable.X_CENTROID, i);
				double y1 = blueTable.getValueAsDouble(ResultsTable.Y_CENTROID, i);
				Point2D p1 = new Point2D.Double(x1,y1);
				for (int j = 0; j<greenTable.getCounter(); j++) {
					double x2 = greenTable.getValueAsDouble(ResultsTable.X_CENTROID, j);
					double y2 = greenTable.getValueAsDouble(ResultsTable.Y_CENTROID, j);
					Point2D p2 = new Point2D.Double(x2,y2);
					if (p1.distance(p2)>this.getRadius()) {
						notGreen.add(p1);
					}
				}
			}
		}
		return notGreen;
	}
	
	protected int getRedCount() {
		int result = this.getRedMeasurements().getCounter();
		return result;
	}

	protected int getGreenCount() {
		int result = this.getGreenMeasurements().getCounter();
		return result;
	}

	protected int getBlueCount() {
		int result = this.getBlueMeasurements().getCounter();
		return result;
	}
	
	protected void cleanUpInput() {
		super.cleanUpInput();
		this.setBlueMeasurements(null);
		this.setGreenMeasurements(null);
		this.setRedMeasurements(null);
	}
}
