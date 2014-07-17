package operations.reporting;

import ij.measure.ResultsTable;
import reporting.ExcelIO;
import reporting.ExcelWriter;

public class ReportPointMeasurementsOperation extends ReportMeasurementsOperation {

	private static final long serialVersionUID = -2941071719574518606L;

	public ReportPointMeasurementsOperation() {
		super();
	}
	
	protected String headingsFirstSheet() {
		String headings = "image\tnumber of objects";
		headings = headings + "\tX"+ "\tY" + "\tZ" + "\tValue" + "\tfolder";
		return headings;
	}
	
	protected String headingsSecondSheet() {
		String headings = "image";
		headings = headings + "\tNr.\tX"+ "\tY" + "\tZ" + "\tValue"; 
		headings = headings + "\tfolder";
		return headings;
	}
	
	protected void reportAverage(ExcelIO excelIO, ExcelWriter excelProxy) {
		excelProxy.setSheet(excelIO.wb.getSheetAt(0));
		String row = this.getImageName() + "\t";
		int count = this.getMeasurements().getCounter();
		if (this.getMeasurements().getColumnIndex("X") == ResultsTable.COLUMN_NOT_FOUND) {
			count = 0;
			row = row + count + "\t \t \t \t \t";
		} else {
			row = row + count + "\t";
				String data = this.getAverageForColumn(this.getMeasurements().getColumnIndex("X"));
				if (data != null) {row = row + data + "\t";}
				data = this.getAverageForColumn(this.getMeasurements().getColumnIndex("Y"));
				if (data != null) {row = row + data + "\t";}
				data = this.getAverageForColumn(this.getMeasurements().getColumnIndex("Slice"));
				if (data != null) {row = row + data + "\t";}
				data = this.getAverageForColumn(this.getMeasurements().getColumnIndex("IntDen"));
				if (data != null) {row = row + data + "\t";}
		}
		row = row + this.getImageFolder();
		excelProxy.writeTabSeparatedDataAsRow(row);
		excelProxy.setSheet(excelIO.wb.getSheetAt(1));
	}
	
	/**
	 * 
	 */
	protected void doReportMeasurement(ExcelIO excelIO, ExcelWriter excelProxy, int i) {
		if (this.getMeasurements().getColumnIndex("X") == ResultsTable.COLUMN_NOT_FOUND) {
			// noop
		} else {
			String row = this.getImageName() + "\t";
			row = row + (i+1) + "\t";
			row = row + this.getMeasurements().getValue("X", i) + "\t";
			row = row + this.getMeasurements().getValue("Y", i) + "\t";
			row = row + this.getMeasurements().getValue("Slice", i) + "\t";
			row = row + this.getMeasurements().getValue("IntDen", i) + "\t";
			row = row + this.getImageFolder();
			excelProxy.writeTabSeparatedDataAsRow(row);
		}
	}
}
