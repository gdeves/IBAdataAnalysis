/*
 * Created on 01.02.2006
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
public class ReportCountComparisonOperation extends ReportingOperation {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1721031244846080026L;
	protected ResultsTable HMeasurements;
	protected ResultsTable YMeasurements;
	protected ResultsTable CMeasurements;
	
	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		// Set parameterTypes, parameterNames and resultType
		parameterTypes = new Class[4];
		parameterTypes[0] = Class.forName("ij.measure.ResultsTable");
		parameterTypes[1] = Class.forName("java.lang.String");
		parameterTypes[2] = Class.forName("ij.measure.ResultsTable");
		parameterTypes[3] = Class.forName("ij.measure.ResultsTable");
		parameterNames = new String[4];
		parameterNames[0] = "HMeasurements";
		parameterNames[1] = "ImageName";
		parameterNames[2] = "YMeasurements";
		parameterNames[3] = "CMeasurements";
	}
	
	/* (non-Javadoc)
	 * @see operations.reporting.ReportingOperation#headingsFirstSheet()
	 */
	protected String headingsFirstSheet() {
		String headings = "image\th\ty\tc";
		headings = headings + "\tfolder";
		return headings;
	}

	/* (non-Javadoc)
	 * @see operations.reporting.ReportingOperation#getSheetName(int)
	 */
	protected String getSheetName(int i) {
		String[] names = {"nr of stained nuclei"};
		return names[i];
	}

	/**
	 * @return Returns the cMeasurements.
	 */
	public ResultsTable getCMeasurements() {
		return CMeasurements;
	}
	/**
	 * @param measurements The cMeasurements to set.
	 */
	public void setCMeasurements(ResultsTable measurements) {
		CMeasurements = measurements;
	}
	/**
	 * @return Returns the hMeasurements.
	 */
	public ResultsTable getHMeasurements() {
		return HMeasurements;
	}
	/**
	 * @param measurements The hMeasurements to set.
	 */
	public void setHMeasurements(ResultsTable measurements) {
		HMeasurements = measurements;
	}
	/**
	 * @return Returns the yMeasurements.
	 */
	public ResultsTable getYMeasurements() {
		return YMeasurements;
	}
	/**
	 * @param measurements The yMeasurements to set.
	 */
	public void setYMeasurements(ResultsTable measurements) {
		YMeasurements = measurements;
	}
	
	protected void cleanUpInput() {
		super.cleanUpInput();
		this.setHMeasurements(null);
		this.setYMeasurements(null);
		this.setCMeasurements(null);
	}
	
	public void doIt() {
	    setupOutputPath();
		ExcelIO excelIO = this.loadOrCreateWorkbook(1);
		ExcelWriter excelProxy = new ExcelWriter();
		excelProxy.setWb(excelIO.wb);
		excelProxy.setSheet(excelIO.s);
		excelProxy.setSheet(excelIO.wb.getSheetAt(0));
		this.createHeadingsFirstSheet(excelProxy, excelIO);
		this.reportComparison(excelIO, excelProxy);
		this.saveWorkbook(excelIO.wb);
	}
	
	/**
	 * @param excelIO
	 * @param excelProxy
	 */
	private void reportComparison(ExcelIO excelIO, ExcelWriter excelProxy) {
		excelProxy.setSheet(excelIO.wb.getSheetAt(0));
		String row = this.getImageName() + "\t";
		row = row + Integer.toString(this.getHCount()) + "\t";
		row = row + Integer.toString(this.getYCount()) + "\t";
		row = row + Integer.toString(this.getCCount()) + "\t";
		row = row + this.getImageFolder();
		excelProxy.writeTabSeparatedDataAsRow(row);
		excelProxy.setSheet(excelIO.wb.getSheetAt(1));
		
	}

	/**
	 * @return
	 */
	protected int getCCount() {
		int result = this.getCMeasurements().getCounter();
		if (!this.getCMeasurements().getColumnHeadings().contains("X")) result = 0;
		return result;
	}

	/**
	 * @return
	 */
	protected int getYCount() {
		int result = this.getYMeasurements().getCounter();
		if (!this.getYMeasurements().getColumnHeadings().contains("X")) result = 0;
		return result;
	}

	/**
	 * @return
	 */
	protected int getHCount() {
		int result = this.getHMeasurements().getCounter();
		if (!this.getHMeasurements().getColumnHeadings().contains("X")) result = 0;
		return result;
	}
}
