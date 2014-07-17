/*
 * Created on 13.10.2005
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
public class ReportMeasurementsOperation extends ReportingOperation {
	/**
	 * 
	 */
	private static final long serialVersionUID = -118837904231449374L;
	
	protected ResultsTable measurements;
	
	/**
	 * @return Returns the measurements.
	 */
	public ResultsTable getMeasurements() {
		return measurements;
	}
	/**
	 * @param measurements The measurements to set.
	 */
	public void setMeasurements(ResultsTable measurements) {
		this.measurements = measurements;
	}
	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		// Set parameterTypes, parameterNames and resultType
		parameterTypes = new Class[2];
		parameterTypes[0] = Class.forName("ij.measure.ResultsTable");
		parameterTypes[1] = Class.forName("java.lang.String");
		parameterNames = new String[2];
		parameterNames[0] = "Measurements";
		parameterNames[1] = "ImageName";
	}
	
	public void doIt() {
	    setupOutputPath();
		ExcelIO excelIO = this.loadOrCreateWorkbook(1);
		ExcelWriter excelProxy = new ExcelWriter();
		excelProxy.setWb(excelIO.wb);
		excelProxy.setSheet(excelIO.s);
		excelProxy.setSheet(excelIO.wb.getSheetAt(0));
		this.createHeadingsFirstSheet(excelProxy, excelIO);
		this.createHeadingsSecondSheet(excelProxy, excelIO);
		this.reportAverage(excelIO, excelProxy);
		for (int i=0; i<this.getMeasurements().getCounter(); i++) {
			this.reportMeasurement(excelIO, excelProxy, i);
		}
		this.saveWorkbook(excelIO.wb);
	}

	protected void reportMeasurement(ExcelIO excelIO, ExcelWriter excelProxy, int i) {
		int lastSheetNumber = excelIO.wb.getNumberOfSheets()-1;
		int rowInSheet = excelIO.wb.getSheetAt(excelIO.wb.getNumberOfSheets()-1).getLastRowNum();
		int index = ((lastSheetNumber - 1) * 32767) + rowInSheet;
		int sheet = (index / 32767) + 1;
		if (index%32767==0 && sheet>1) {
			excelIO.wb.createSheet(getSheetName(1) + " " + sheet);
			boolean valueBefore = continueExistingFile;
			continueExistingFile = false;
			this.createHeadingsSheet(sheet, excelProxy, excelIO);
			continueExistingFile = valueBefore;
		}
		excelProxy.setSheet(excelIO.wb.getSheetAt(sheet));
		doReportMeasurement(excelIO, excelProxy, i);
		excelProxy.setSheet(excelIO.wb.getSheetAt(1));
	}
	
	protected void doReportMeasurement(ExcelIO excelIO, ExcelWriter excelProxy, int i) {
		String row = this.getImageName() + "\t";
		row = row + this.getMeasurements().getRowAsString(i) + "\t";
		row = row + this.getImageFolder();
		excelProxy.writeTabSeparatedDataAsRow(row);
	}

	protected void reportAverage(ExcelIO excelIO, ExcelWriter excelProxy) {
		excelProxy.setSheet(excelIO.wb.getSheetAt(0));
		String row = this.getImageName() + "\t";
		row = row + this.getMeasurements().getCounter() + "\t";
		boolean dataFound = false;
		String data = null;
		for (int i=0; i<ResultsTable.MAX_COLUMNS; i++) {
			data = this.getAverageForColumn(i);
			if (data!=null) {
				row = row + this.getAverageForColumn(i) + "\t";
				dataFound = true;
			} 
		}
		if (!dataFound) {
			int numberOfColumns = this.getMeasurements().getColumnHeadings().split("\t").length;
			for (int i=0; i<numberOfColumns; i++ ) {
				row = row + " \t";
			}
		}
		row = row + this.getImageFolder();
		excelProxy.writeTabSeparatedDataAsRow(row);
		excelProxy.setSheet(excelIO.wb.getSheetAt(1));
	}
	/**
	 * @param i
	 * @return
	 */
	protected String getAverageForColumn(int i) {
		float[] column = this.getMeasurements().getColumn(i);
		if (column==null) return null;
		double average = 0.0;
		for (int row=0; row<column.length; row++) {
			average = average + column[row];
		}
		average = average / ((column.length)*1.0);
		String result = Double.toString(average);
		return result;
	}
	/* (non-Javadoc)
	 * @see operations.reporting.ReportingOperation#headingsFirstSheet()
	 */
	protected String headingsFirstSheet() {
		String headings = "image\tnumber of objects";
		headings = headings + this.getMeasurements().getColumnHeadings() + "\tfolder";
		return headings;
	}

	/* (non-Javadoc)
	 * @see operations.reporting.ReportingOperation#getSheetName(int)
	 */
	protected String getSheetName(int i) {
		int index = i;
		String[] names = {"objects averages", "objects"};
		if (index>=names.length) index = names.length-1;
		return names[index];
	}
	
	protected String headingsSecondSheet() {
		String headings = "image";
		headings = headings + "\t" + this.getMeasurements().getColumnHeadings();
		headings = headings + "\tfolder";
		return headings;
	}
	
	protected void cleanUpInput() {
		super.cleanUpInput();
		this.setMeasurements(null);
	}
}
