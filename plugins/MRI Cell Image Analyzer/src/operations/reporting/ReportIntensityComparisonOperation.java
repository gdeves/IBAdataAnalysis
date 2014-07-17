/*
 * Created on 18.11.2005
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
public class ReportIntensityComparisonOperation extends ReportingOperation {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9115545074894408250L;
	protected ResultsTable baseMeasurements;
	protected ResultsTable includedMeasurements;
	
	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		// Set parameterTypes, parameterNames and resultType
		parameterTypes = new Class[3];
		parameterTypes[0] = Class.forName("ij.measure.ResultsTable");
		parameterTypes[1] = Class.forName("java.lang.String");
		parameterTypes[2] = Class.forName("ij.measure.ResultsTable");
		parameterNames = new String[3];
		parameterNames[0] = "BaseMeasurements";
		parameterNames[1] = "ImageName";
		parameterNames[2] = "IncludedMeasurements";
	}
	/* (non-Javadoc)
	 * @see operations.reporting.ReportingOperation#headingsFirstSheet()
	 */
	protected String headingsFirstSheet() {
		String headings = "image\t\ticn factor\tpercent nuclei\tpercent cytoplasm\tav. nuclei intensity\tav. cytoplasm intensity\tnuclei area\tcytoplasm area\tt. nuclei intensity\tt. cytoplasm intensity";
		headings = headings + "\tfolder";
		return headings;
	}

	/* (non-Javadoc)
	 * @see operations.reporting.ReportingOperation#getSheetName(int)
	 */
	protected String getSheetName(int i) {
		String[] names = {"intensities nuclei/cytoplasm"};
		return names[i];
	}

	/**
	 * @return Returns the baseMeasurements.
	 */
	public ResultsTable getBaseMeasurements() {
		return baseMeasurements;
	}
	/**
	 * @param baseMeasurements The baseMeasurements to set.
	 */
	public void setBaseMeasurements(ResultsTable baseMeasurements) {
		this.baseMeasurements = baseMeasurements;
	}
	/**
	 * @return Returns the includedMeasurements.
	 */
	public ResultsTable getIncludedMeasurements() {
		return includedMeasurements;
	}
	/**
	 * @param includedMeasurements The includedMeasurements to set.
	 */
	public void setIncludedMeasurements(ResultsTable includedMeasurements) {
		this.includedMeasurements = includedMeasurements;
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
		row = row + Double.toString(this.getFactorIntensityNucleiCytoplasm()) + "\t";
		row = row + Double.toString(this.getPercentNucleiIntensity()) + "\t";
		row = row + Double.toString(this.getPercentCytoplasmIntensity()) + "\t";
		row = row + Double.toString(this.getAverageNucleiIntensity()) + "\t";
		row = row + Double.toString(this.getAverageCytoplasmIntensity()) + "\t";
		row = row + Integer.toString(this.getNucleiArea()) + "\t";
		row = row + Integer.toString(this.getCytoplasmArea()) + "\t";
		row = row + Integer.toString(this.getTotalNucleiIntensity()) + "\t";
		row = row + Integer.toString(this.getTotalCytoplasmIntensity()) + "\t";
		row = row + this.getImageFolder();
		excelProxy.writeTabSeparatedDataAsRow(row);
		excelProxy.setSheet(excelIO.wb.getSheetAt(1));
		
	}
	/**
	 * @return
	 */
	private double getFactorIntensityNucleiCytoplasm() {
		double result = this.getPercentNucleiIntensity() / (this.getPercentCytoplasmIntensity() * 1.0);
		return result;
	}
	/**
	 * @return
	 */
	private double getPercentCytoplasmIntensity() {
		double result = this.getTotalCytoplasmIntensity() / ((this.getTotalCytoplasmIntensity() + this.getTotalNucleiIntensity())*1.0);
		return result;
	}
	/**
	 * @return
	 */
	private double getPercentNucleiIntensity() {
		double result = this.getTotalNucleiIntensity() / ((this.getTotalCytoplasmIntensity() + this.getTotalNucleiIntensity())*1.0);
		return result;
	}
	/**
	 * @return
	 */
	private int getTotalCytoplasmIntensity() {
		float[] column = this.getBaseMeasurements().getColumn(ResultsTable.INTEGRATED_DENSITY);
		return (int)column[0] - this.getTotalNucleiIntensity();
	}
	/**
	 * @return
	 */
	private int getTotalNucleiIntensity() {
		float[] column = this.getIncludedMeasurements().getColumn(ResultsTable.INTEGRATED_DENSITY);
		return (int)column[0];
	}
	/**
	 * @return
	 */
	private int getCytoplasmArea() {
		float[] column = this.getBaseMeasurements().getColumn(ResultsTable.AREA);
		return (int)column[0] - this.getNucleiArea();
	}
	/**
	 * @return
	 */
	private int getNucleiArea() {
		float[] column = this.getIncludedMeasurements().getColumn(ResultsTable.AREA);
		return (int)column[0];
	}
	/**
	 * @return
	 */
	private double getAverageCytoplasmIntensity() {
		return this.getTotalCytoplasmIntensity() / (this.getCytoplasmArea() * 1.0);
	}
	/**
	 * @return
	 */
	private double getAverageNucleiIntensity() {
		float[] column = this.getIncludedMeasurements().getColumn(ResultsTable.MEAN);
		return column[0];
	}
	protected void cleanUpInput() {
		super.cleanUpInput();
		this.setBaseMeasurements(null);
		this.setIncludedMeasurements(null);
	}
}
