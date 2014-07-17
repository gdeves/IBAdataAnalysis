/*
 * Created on 18.10.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package operations.reporting;

import reporting.ExcelIO;
import reporting.ExcelWriter;
import ij.measure.ResultsTable;


/**
 * @author Volker
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ReportSpotsOnNucleiOperation extends ReportingOperation {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3966116538301255094L;
	protected ResultsTable nucleusMeasurements;
	protected ResultsTable spotsMeasurements;
	
	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		// Set parameterTypes, parameterNames and resultType
		parameterTypes = new Class[3];
		parameterTypes[0] = Class.forName("ij.measure.ResultsTable");
		parameterTypes[1] = Class.forName("java.lang.String");
		parameterTypes[2] = Class.forName("ij.measure.ResultsTable");
		parameterNames = new String[3];
		parameterNames[0] = "NucleusMeasurements";
		parameterNames[1] = "ImageName";
		parameterNames[2] = "SpotsMeasurements";
	}
	
	/**
	 * @return Returns the nucleusMeasurements.
	 */
	public ResultsTable getNucleusMeasurements() {
		return nucleusMeasurements;
	}
	/**
	 * @param nucleusMeasurements The nucleusMeasurements to set.
	 */
	public void setNucleusMeasurements(ResultsTable nucleusMeasurements) {
		this.nucleusMeasurements = nucleusMeasurements;
	}
	/**
	 * @return Returns the spotsMeasurements.
	 */
	public ResultsTable getSpotsMeasurements() {
		return spotsMeasurements;
	}
	/**
	 * @param spotsMeasurements The spotsMeasurements to set.
	 */
	public void setSpotsMeasurements(ResultsTable spotsMeasurements) {
		this.spotsMeasurements = spotsMeasurements;
	}
	/* (non-Javadoc)
	 * @see operations.reporting.ReportingOperation#headingsFirstSheet()
	 */
	protected String headingsFirstSheet() {
		String headings = "image\tnucleus nr.\tnucleus area\tnr. of spots\tav. spot area\tn. av. spot area\ttotal spot area\tn. total spot area";
		headings = headings + "\tfolder";
		return headings;
	}

	/* (non-Javadoc)
	 * @see operations.reporting.ReportingOperation#getSheetName(int)
	 */
	protected String getSheetName(int i) {
		String[] names = {"nuclei and spots"};
		return names[i];
	}
	
	public void doIt() {
	    setupOutputPath();
		ExcelIO excelIO = this.loadOrCreateWorkbook(1);
		ExcelWriter excelProxy = new ExcelWriter();
		excelProxy.setWb(excelIO.wb);
		excelProxy.setSheet(excelIO.s);
		excelProxy.setSheet(excelIO.wb.getSheetAt(0));
		this.createHeadingsFirstSheet(excelProxy, excelIO);
		this.reportSpots(excelIO, excelProxy);
		this.saveWorkbook(excelIO.wb);
	}

	/**
	 * @param excelIO
	 * @param excelProxy
	 */
	private void reportSpots(ExcelIO excelIO, ExcelWriter excelProxy) {
		excelProxy.setSheet(excelIO.wb.getSheetAt(0));
		String row = this.getImageName() + "\t";
		row = row + Integer.toString(this.getNumberOfNucleus() + 1) + "\t";
		row = row + Float.toString(this.getAreaOfNucleus()) + "\t";
		row = row + Integer.toString(this.getNumberOfSpots()) + "\t";
		row = row + Double.toString(this.getAverageSpotArea()) + "\t";
		row = row + Double.toString(this.getNormalizedAverageSpotArea()) + "\t";
		row = row + Double.toString(this.getTotalSpotArea()) + "\t";
		row = row + Double.toString(this.getNormalizedTotalSpotArea()) + "\t";
		row = row + this.getImageFolder();
		excelProxy.writeTabSeparatedDataAsRow(row);
		excelProxy.setSheet(excelIO.wb.getSheetAt(1));
	}

	/**
	 * @return
	 */
	private double getNormalizedTotalSpotArea() {
		double result = this.getTotalSpotArea() / (this.getAreaOfNucleus() * 1.0);
		return result;
	}

	/**
	 * @return
	 */
	private double getNormalizedAverageSpotArea() {
		double result = this.getAverageSpotArea() / (this.getAreaOfNucleus() * 1.0);
		return result;
	}

	/**
	 * @return
	 */
	private double getTotalSpotArea() {
		float[] column = this.getSpotsMeasurements().getColumn(ResultsTable.AREA);
		double average = 0.0;
		for (int i=0; i<column.length; i++) {
			average = average + column[i];
		}
		return average;
	}

	/**
	 * @return
	 */
	private double getAverageSpotArea() {
		double average = this.getTotalSpotArea() / (this.getNumberOfSpots() * 1.0);
		return average;
	}

	/**
	 * @return
	 */
	private int getNumberOfSpots() {
		return this.getSpotsMeasurements().getCounter();
	}

	/**
	 * @return
	 */
	private float getAreaOfNucleus() {
		float[] column = this.getNucleusMeasurements().getColumn(ResultsTable.AREA);
		return column[this.getNumberOfNucleus()];
	}

	/**
	 * @return
	 */
	private int getNumberOfNucleus() {
		return this.application.getCurrentIndexInLoopFor(this);
	}

	protected void cleanUpInput() {
		super.cleanUpInput();
		this.setNucleusMeasurements(null);
		this.setSpotsMeasurements(null);
	}
}
