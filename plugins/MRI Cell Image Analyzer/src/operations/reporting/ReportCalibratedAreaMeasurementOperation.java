package operations.reporting;

import reporting.ExcelIO;
import reporting.ExcelWriter;
import gui.options.Option;
import ij.measure.ResultsTable;

public class ReportCalibratedAreaMeasurementOperation extends ReportingOperation {

	private static final long serialVersionUID = -5784992566357232009L;

	protected ResultsTable calibrationMeasurement;
	protected ResultsTable measurement;
	protected Option referenceDistance;
	protected Option unit;
	
	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		// Set parameterTypes, parameterNames and resultType
		parameterTypes = new Class[3];
		parameterTypes[0] = Class.forName("ij.measure.ResultsTable");
		parameterTypes[1] = Class.forName("java.lang.String");
		parameterTypes[2] = Class.forName("ij.measure.ResultsTable");
		parameterNames = new String[3];
		parameterNames[0] = "Measurement";
		parameterNames[1] = "ImageName";
		parameterNames[2] = "CalibrationMeasurement";
		optionsNames = new String[3];
		optionsNames[0] = "output path";
		optionsNames[1] = "reference distance";
		optionsNames[2] = "unit";
	}
	protected String headingsFirstSheet() {
		String headings = "image\tmeasurement (pixel^2)\tmeasurement (" + this.getUnit() + "^2)" + "\tcalibration distance (pixel)\tknown distance (" + this.getUnit() + ")";
		headings = headings + "\tfolder";
		return headings;
	}

	protected String getSheetName(int i) {
		String[] names = {"measurements"};
		return names[i];
	}
	public ResultsTable getCalibrationMeasurement() {
		return calibrationMeasurement;
	}
	public void setCalibrationMeasurement(ResultsTable calibrationMeasurement) {
		this.calibrationMeasurement = calibrationMeasurement;
	}
	public ResultsTable getMeasurement() {
		return measurement;
	}
	public void setMeasurement(ResultsTable measurement) {
		this.measurement = measurement;
	}
	public float getReferenceDistance() {
		return referenceDistance.getFloatValue();
	}
	public void setReferenceDistance(float referenceDistance) {
		this.referenceDistance.setValue(Float.toString(referenceDistance));
	}
	public String getUnit() {
		return unit.getValue();
	}
	public void setUnit(String unit) {
		this.unit.setValue(unit);
	}

	protected void setupOptions() {
		super.setupOptions();
		this.setReferenceDistance(10);
		referenceDistance.setShortHelpText("enter the known distance that will be used to calibrate the image");
		this.setUnit("mm");
		unit.setShortHelpText("enter the unit of the known distance");
	}

	public void connectOptions() {
		outputPath = (Option) options.getOptions().get(0); 
		referenceDistance = (Option) options.getOptions().get(1);
		unit = (Option) options.getOptions().get(2);
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
	
	protected void report(ExcelIO excelIO, ExcelWriter excelProxy) {
		excelProxy.setSheet(excelIO.wb.getSheetAt(0));
		float measuredCalibrationDistance = (float) this.calibrationMeasurement.getValueAsDouble(this.calibrationMeasurement.getColumnIndex("Length"), 0);
		float area = (float) measurement.getValueAsDouble(ResultsTable.AREA, 0);
		float factor = (this.getReferenceDistance() * this.getReferenceDistance()) / (measuredCalibrationDistance * measuredCalibrationDistance * 1.0f);
		float convertedArea = area * factor;
		String row = this.getImageName() + "\t";
		row = row + area + "\t";
		row = row + convertedArea + "\t";
		row = row + measuredCalibrationDistance + "\t";
		row = row + this.getReferenceDistance() + "\t";
		row = row + this.getImageFolder();
		excelProxy.writeTabSeparatedDataAsRow(row);
	}
}
