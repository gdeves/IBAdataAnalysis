package operations.reporting;

import reporting.ExcelIO;
import reporting.ExcelWriter;
import ij.ImagePlus;
import ij.gui.Roi;
import ij.gui.ShapeRoi;
import ij.measure.ResultsTable;

public class ReportObjectNumberRatioOperation extends ReportingOperation {

	protected ResultsTable measurements;
	protected ImagePlus roiImage;
	/**
	 * 
	 */
	private static final long serialVersionUID = -948106037341609604L;

	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		// Set parameterTypes, parameterNames and resultType
		parameterTypes = new Class[3];
		parameterTypes[0] = Class.forName("ij.measure.ResultsTable");
		parameterTypes[1] = Class.forName("java.lang.String");
		parameterTypes[2] = Class.forName("ij.ImagePlus");
		parameterNames = new String[3];
		parameterNames[0] = "Measurements";
		parameterNames[1] = "ImageName";
		parameterNames[2] = "RoiImage";
	}

	public void doIt() {
	    setupOutputPath();
		ExcelIO excelIO = this.loadOrCreateWorkbook(3);
		ExcelWriter excelProxy = new ExcelWriter();
		excelProxy.setWb(excelIO.wb);
		excelProxy.setSheet(excelIO.s);
		excelProxy.setSheet(excelIO.wb.getSheetAt(0));
		this.createHeadingsFirstSheet(excelProxy, excelIO);
		this.reportCounting(excelIO, excelProxy);
		this.saveWorkbook(excelIO.wb);
	}
	
	protected void reportCounting(ExcelIO excelIO, ExcelWriter excelProxy) {
		Roi aRoi = this.getRoiImage().getRoi();
		ShapeRoi roi = new ShapeRoi(aRoi);
		float[] x = this.getMeasurements().getColumn(ResultsTable.X_CENTROID);
		float[] y = this.getMeasurements().getColumn(ResultsTable.Y_CENTROID);
		float numberInside = 0;
		float totalNumber = this.getMeasurements().getCounter();
		for (int i=0; i<totalNumber; i++) {
			if (roi.contains((int)x[i], (int)y[i])) numberInside++;
		}
		int areas = roi.getRois().length;
		float ratio = (numberInside * 1.0f)/ totalNumber;
		String row = this.getImageName() + "\t";
		row = row + areas + "\t" + totalNumber + "\t" + numberInside + "\t" + ratio + "\t";
		row = row + this.getImageFolder();
		excelProxy.writeTabSeparatedDataAsRow(row);
	}

	public ResultsTable getMeasurements() {
		return measurements;
	}

	public void setMeasurements(ResultsTable measurements) {
		this.measurements = measurements;
	}

	public ImagePlus getRoiImage() {
		return roiImage;
	}

	public void setRoiImage(ImagePlus roiImage) {
		this.roiImage = roiImage;
	}

	/* (non-Javadoc)
	 * @see operations.reporting.ReportingOperation#headingsFirstSheet()
	 */
	protected String headingsFirstSheet() {
		String headings = "image\tareas\ttotal numer of objects\tnumber number of objects inside areas\tratio";
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
	
	protected void cleanUpInput() {
		super.cleanUpInput();
		this.setMeasurements(null);
		this.setRoiImage(null);
	}
}
