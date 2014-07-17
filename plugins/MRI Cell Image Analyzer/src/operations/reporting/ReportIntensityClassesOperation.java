package operations.reporting;

import reporting.ExcelIO;
import reporting.ExcelWriter;
import ij.measure.ResultsTable;
import gui.Options;
import gui.options.ChoiceOption;
import gui.options.Option;

public class ReportIntensityClassesOperation extends ReportingOperation {
	protected ResultsTable measurements;

	protected ChoiceOption measurement;

	protected Option min1;

	protected Option max1;

	protected Option min2;

	protected Option max2;

	protected Option min3;

	protected Option max3;

	protected Option min4;

	protected Option max4;

	protected Option min5;

	protected Option max5;

	/**
	 * 
	 */
	private static final long serialVersionUID = 5184834164972441991L;

	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		// Set parameterTypes, parameterNames and resultType
		parameterTypes = new Class[2];
		parameterTypes[0] = Class.forName("ij.measure.ResultsTable");
		parameterTypes[1] = Class.forName("java.lang.String");
		parameterNames = new String[2];
		parameterNames[0] = "Measurements";
		parameterNames[1] = "ImageName";
		optionsNames = new String[12];
		optionsNames[0] = "output path";
		optionsNames[1] = "measurement";
		optionsNames[2] = "min 1";
		optionsNames[3] = "max 1";
		optionsNames[4] = "min 2";
		optionsNames[5] = "max 2";
		optionsNames[6] = "min 3";
		optionsNames[7] = "max 3";
		optionsNames[8] = "min 4";
		optionsNames[9] = "max 4";
		optionsNames[10] = "min 5";
		optionsNames[11] = "max 5";
	}

	protected String headingsFirstSheet() {
		String headings = "image\tcells\tpositive\tratio";
		headings = headings + "\tfolder";
		return headings;
	}

	protected String headingsSecondSheet() {
		String headings = "image\tobject\tvalue\tx\ty";
		headings = headings + "\tfolder";
		return headings;
	}

	protected String getSheetName(int i) {
		String result = "";
		if (i < 5) {
			result = result + this.getMeasurement() + " ";
			if (i == 0) {
				result = result + min1.getValue() + "-";
				result = result + max1.getValue();
			}
			if (i == 1) {
				result = result + min2.getValue() + "-";
				result = result + max2.getValue();
			}
			if (i == 2) {
				result = result + min3.getValue() + "-";
				result = result + max3.getValue();
			}
			if (i == 3) {
				result = result + min4.getValue() + "-";
				result = result + max4.getValue();
			}
			if (i == 4) {
				result = result + min5.getValue() + "-";
				result = result + max5.getValue();
			}
		} else
			result = result + "objects " + (i-4);
		return result;
	}

	protected void cleanUpInput() {
		super.cleanUpInput();
		this.setMeasurements(null);
	}

	public ResultsTable getMeasurements() {
		return measurements;
	}

	public void setMeasurements(ResultsTable measurements) {
		this.measurements = measurements;
	}

	public String getMeasurement() {
		return measurement.getValue();
	}

	public void setMeasurement(String measurement) {
		this.measurement.setValue(measurement);
	}

	public void doIt() {
		setupOutputPath();
		ExcelIO excelIO = this.loadOrCreateWorkbook(10);
		ExcelWriter excelProxy = new ExcelWriter();
		excelProxy.setWb(excelIO.wb);
		excelProxy.setSheet(excelIO.s);
		excelProxy.setSheet(excelIO.wb.getSheetAt(0));
		this.createHeadingsAllSheets(excelProxy, excelIO);
		this.reportRange(0, this.min1, this.max1, excelIO, excelProxy);
		this.reportRange(1, this.min2, this.max2, excelIO, excelProxy);
		this.reportRange(2, this.min3, this.max3, excelIO, excelProxy);
		this.reportRange(3, this.min4, this.max4, excelIO, excelProxy);
		this.reportRange(4, this.min5, this.max5, excelIO, excelProxy);
		this.saveWorkbook(excelIO.wb);
	}

	protected void createHeadingsAllSheets(ExcelWriter excelProxy,
			ExcelIO excelIO) {
		if (continueExistingFile)
			return;
		for (int i = 0; i < 10; i++) {
			excelProxy.setSheet(excelIO.wb.getSheetAt(i));
			excelIO.wb.setSheetName(i, this.getSheetName(i));
			if (i < 5)
				excelProxy.writeTabSeparatedColumnHeadings(this
						.headingsFirstSheet());
			else
				excelProxy.writeTabSeparatedColumnHeadings(this
						.headingsSecondSheet());
		}
	}

	private void reportRange(int sheet, Option minOpt, Option maxOpt,
			ExcelIO excelIO, ExcelWriter excelProxy) {
		if (minOpt.getValue().equals("null") || minOpt.getValue().trim().equals(""))
			return;
		int min = minOpt.getIntegerValue();
		int max = maxOpt.getIntegerValue();
		int totalCount = this.getMeasurements().getCounter();
		int inRangeCounter = 0;
		int measurement = this.getMeasurementIndex();
		excelProxy.setSheet(excelIO.wb.getSheetAt(sheet + 5));
		for (int i = 0; i < totalCount; i++) {
			float current = (float) this.getMeasurements().getValueAsDouble(measurement, i);
			float xCoord = (float) this.getMeasurements().getValueAsDouble(ResultsTable.X_CENTROID, i);
			float yCoord = (float) this.getMeasurements().getValueAsDouble(ResultsTable.Y_CENTROID, i);
			if (current >= min && current < max) {
				inRangeCounter++;
				String row = this.getImageName() + "\t";
				row = row + (i+1) + "\t";
				row = row + current + "\t";
				row = row + xCoord + "\t";
				row = row + yCoord + "\t";
				row = row + this.getImageFolder();
				excelProxy.writeTabSeparatedDataAsRow(row);
			}
		}
		float ratio = (inRangeCounter * 1.0f) / totalCount;
		excelProxy.setSheet(excelIO.wb.getSheetAt(sheet));
		String row = this.getImageName() + "\t";
		row = row + totalCount + "\t";
		row = row + inRangeCounter + "\t";
		row = row + ratio + "\t";
		row = row + this.getImageFolder();
		excelProxy.writeTabSeparatedDataAsRow(row);
		excelProxy.setSheet(excelIO.wb.getSheetAt(1));
	}

	protected String[] getMeasurementChoices() {
		String[] choices = { "average", "inegrated", "min", "max", "mode" };
		return choices;
	}

	protected int getMeasurementIndex() {
		int[] values = { ResultsTable.MEAN, ResultsTable.INTEGRATED_DENSITY,
				ResultsTable.MIN, ResultsTable.MAX, ResultsTable.MODE };
		int index = 0;
		for (; index < this.getMeasurementChoices().length; index++) {
			if (this.getMeasurementChoices()[index].equals(this
					.getMeasurement()))
				break;
		}
		return values[index];
	}

	protected void setupOptions() {
		options = new Options();
		options.setName(this.name() + " options");
		String[] optionsNames = this.getOptionsNames();
		this.outputPath = new Option();
		this.outputPath.setName(optionsNames[0]);
		options.add(outputPath);
		this.measurement = new ChoiceOption(this.getMeasurementChoices());
		this.measurement.setValue(this.getMeasurementChoices()[0]);
		this.measurement.setName(optionsNames[1]);
		this.measurement.setShortHelpText("choose measurement to be used");
		options.add(this.measurement);
		this.min1 = new Option();
		this.min1.setValue("200");
		this.min1.setName(optionsNames[2]);
		options.add(this.min1);
		this.max1 = new Option();
		this.max1.setValue("255");
		this.max1.setName(optionsNames[3]);
		options.add(this.max1);
		this.min2 = new Option();
		this.min2.setName(optionsNames[4]);
		options.add(this.min2);
		this.max2 = new Option();
		this.max2.setName(optionsNames[5]);
		options.add(this.max2);
		this.min3 = new Option();
		this.min3.setName(optionsNames[6]);
		options.add(this.min3);
		this.max3 = new Option();
		this.max3.setName(optionsNames[7]);
		options.add(this.max3);
		this.min4 = new Option();
		this.min4.setName(optionsNames[8]);
		options.add(this.min4);
		this.max4 = new Option();
		this.max4.setName(optionsNames[9]);
		options.add(this.max4);
		this.min5 = new Option();
		this.min5.setName(optionsNames[10]);
		options.add(this.min5);
		this.max5 = new Option();
		this.max5.setName(optionsNames[11]);
		options.add(this.max5);
	}

	public void connectOptions() {
		outputPath = (Option) options.getOptions().get(0); 
		measurement = (ChoiceOption) options.getOptions().get(1);
		min1 = (Option) options.getOptions().get(2);
		max1 = (Option) options.getOptions().get(3);
		min2 = (Option) options.getOptions().get(4);
		max2 = (Option) options.getOptions().get(5);
		min3 = (Option) options.getOptions().get(6);
		max3 = (Option) options.getOptions().get(7);
		min4 = (Option) options.getOptions().get(8);
		max4 = (Option) options.getOptions().get(9);
		min5 = (Option) options.getOptions().get(10);
		max5 = (Option) options.getOptions().get(11);
	}
}
