package operations.reporting;

import reporting.ExcelIO;
import reporting.ExcelWriter;
import ij.measure.ResultsTable;

public class ReportTwoCountComparisonOperation extends ReportingOperation {

	/**
	 * 
	 */
	private static final long serialVersionUID = 594017248986168834L;

	protected ResultsTable measurements;
	protected ResultsTable subMeasurements;
	
	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		// Set parameterTypes, parameterNames and resultType
		parameterTypes = new Class[3];
		parameterTypes[0] = Class.forName("ij.measure.ResultsTable");
		parameterTypes[1] = Class.forName("java.lang.String");
		parameterTypes[2] = Class.forName("ij.measure.ResultsTable");
		parameterNames = new String[3];
		parameterNames[0] = "Measurements";
		parameterNames[1] = "ImageName";
		parameterNames[2] = "SubMeasurements";
	}
	
	
	/* (non-Javadoc)
	 * @see operations.reporting.ReportingOperation#headingsFirstSheet()
	 */
	protected String headingsFirstSheet() {
		String headings = "image\tcount\tsub-count\tratio";
		headings = headings + "\tfolder";
		return headings;
	}

	/* (non-Javadoc)
	 * @see operations.reporting.ReportingOperation#getSheetName(int)
	 */
	protected String getSheetName(int i) {
		String[] names = {"counted objects"};
		return names[i];
	}
	
	public ResultsTable getMeasurements() {
		return measurements;
	}

	public void setMeasurements(ResultsTable measurements) {
		this.measurements = measurements;
	}

	public ResultsTable getSubMeasurements() {
		return subMeasurements;
	}

	public void setSubMeasurements(ResultsTable subMeasurements) {
		this.subMeasurements = subMeasurements;
	}

	protected void cleanUpInput() {
		super.cleanUpInput();
		this.setMeasurements(null);
		this.setSubMeasurements(null);
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


	private void reportComparison(ExcelIO excelIO, ExcelWriter excelProxy) {
		excelProxy.setSheet(excelIO.wb.getSheetAt(0));
		String row = this.getImageName() + "\t";
		int count = this.getMeasurementCount();
		int subCount = this.getSubMeasurementCount();
		float ratio = subCount / (count * 1.0f);
		row = row + Integer.toString(count) + "\t";
		row = row + Integer.toString(subCount) + "\t";
		row = row + Float.toString(ratio) + "\t";
		row = row + this.getImageFolder();
		excelProxy.writeTabSeparatedDataAsRow(row);
		excelProxy.setSheet(excelIO.wb.getSheetAt(1));
	}


	private int getSubMeasurementCount() {
		int result = this.getSubMeasurements().getCounter();
		return result;
	}


	private int getMeasurementCount() {
		int result = this.getMeasurements().getCounter();
		return result;
	}
}
