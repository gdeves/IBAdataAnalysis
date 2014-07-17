package tools.multipleImageWindow;
import java.util.ArrayList;
import java.util.Iterator;
import ij.ImagePlus;
import ij.gui.Roi;
import ij.gui.ShapeRoi;
import ij.measure.ResultsTable;
import ij.plugin.Duplicator;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import operations.analysis.MeasureOperation;
import operations.channel.SplitChannelsOperation;
import operations.image.ConvertImageTypeOperation;
import operations.image.DivideOperation;
import operations.roi.ClearOperation;
import operations.roi.EnlargeSelectionOperation;
import operations.roi.InverseSelectionOperation;
import operations.roi.MakeBandSelectionOperation;

public class FluoDistributionInCellAnalyzer implements PlugableImageAnalysisApplication, TableModel {
	protected FluoDistributionInCellAnalyzerView view;
	protected ArrayList<Object> cells;
	protected ArrayList<Object> nuclei;
	protected ArrayList<Object> cytoplasms;
	protected ArrayList<Object> membranes;
	protected ArrayList<TableModelListener> tableModelListeners;
	protected int membraneWidth = 4;
	protected ImagePlus blueImage;
	protected ImagePlus greenImage;
	protected ImagePlus redImage;
	protected boolean imageExisted = false;
	protected ResultsTable results;
	private ImagePlus redWorkingImage;
	private ImagePlus greenWorkingImage;
	private FluoDistributionInCellAnalyzerOptionsView optionsView;

	public FluoDistributionInCellAnalyzer() {
		this.initialize();
	}
	
	public JComponent getComponent() {
		return this.getView().getMainComponent();
	}

	public JMenu getMenus() {
		// TODO Auto-generated method stub
		return null;
	}

	public JFrame getOptions() {
		// TODO Auto-generated method stub
		return null;
	}

	public JButton[] getTaskbarButtons() {
		// TODO Auto-generated method stub
		return null;
	}

	public JFrame getWizard() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setBlueImage(ImagePlus blueImage) {
		if (blueImage!=null) setImageExisted(true);
		this.blueImage = blueImage;
	}

	public void setGreenImage(ImagePlus greenImage) {
		this.greenImage = greenImage;
	}

	public void setRGBImage(ImagePlus inputImage) {
		setImageExisted(true);
		if (inputImage.getType()!=ImagePlus.COLOR_RGB) return;
		SplitChannelsOperation split = new SplitChannelsOperation();
		split.setInputImage(inputImage);
		split.setShowResult(false);
		split.doIt();
		this.setRedImage(split.getResultRed());
		this.setGreenImage(split.getResultGreen());
		this.setBlueImage(split.getResultBlue());
	}

	public void setRedImage(ImagePlus redInputImage) {
		this.redImage = redInputImage;
	}

	public FluoDistributionInCellAnalyzerView getView() {
		if (view==null) view = new FluoDistributionInCellAnalyzerView(this);
		return view;
	}

	public Class<?> getColumnClass(int index) {
		return this.getColumnClasses()[index];
	}
	
	public int getColumnCount() {
		return this.getColumnClasses().length;
	}

	public String getColumnName(int index) {
		return this.getColumnNames()[index];
	}

	public int getRowCount() {
		return numberOfCells();
	}

	public int numberOfCells() {
		return this.getCells().size();
	}

	protected ArrayList<Object> getCells() {
		return cells;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		ArrayList<?> column = getColumn(columnIndex); 
		if (rowIndex<column.size()) return column.get(rowIndex); 
		else
			return null;
	}

	private ArrayList<Object> getColumn(int columnIndex) {
		ArrayList<Object> column = this.getCells();
		if (columnIndex==1) column = this.getNuclei(); 
		if (columnIndex==2) column = this.getCytoplasms(); 
		if (columnIndex==3) column = this.getMembranes();
		return column;
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		boolean result = false;
		if (columnIndex==0) result = true;
		return result;
	}

	public void addTableModelListener(TableModelListener listener) {
		this.getTableModelListeners().add(listener);
	}
	
	public void removeTableModelListener(TableModelListener aListener) {
		this.getTableModelListeners().remove(aListener);
	}

	public void setValueAt(Object object, int rowIndex, int columnIndex) {
		ArrayList<Object> column = getColumn(columnIndex); 
		column.set(rowIndex, object);
	}
	
	private void initialize() {
		cells = new ArrayList<Object>();
		nuclei = new ArrayList<Object>();
		cytoplasms = new ArrayList<Object>();
		membranes = new ArrayList<Object>();
		tableModelListeners = new ArrayList<TableModelListener>();
	}

	private Class<?>[] getColumnClasses() {
		Class<?>[] columnClasses = {String.class, Roi.class, Roi.class, Roi.class};
		return columnClasses;
	}

	private String[] getColumnNames() {
		String[] columnNames = {"cell", "nucleus", "cytoplasm", "membrane"};
		return columnNames;
	}
	
	private ArrayList<Object> getCytoplasms() {
		return cytoplasms;
	}

	private ArrayList<Object> getNuclei() {
		return nuclei;
	}
	
	private ArrayList<Object> getMembranes() {
		return membranes;
	}

	public ArrayList<TableModelListener> getTableModelListeners() {
		return tableModelListeners;
	}

	public void addCell(String name) {
		if (cells.contains(name)) return;
		cells.add(name);
		nuclei.add(null);
		cytoplasms.add(null);
		membranes.add(null);
		this.tableModelChanged();
	}

	private void tableModelChanged() {
		Iterator<TableModelListener> it = tableModelListeners.iterator();
		while(it.hasNext()) {
			TableModelListener listener = it.next();
			listener.tableChanged(new TableModelEvent(this));
		}
	}

	public void setCytoplasmAndMembrane(int selectedRow, ImagePlus anImage) {
		EnlargeSelectionOperation enlargeRoi = new EnlargeSelectionOperation();
		enlargeRoi.setShowResult(false);
		enlargeRoi.setKeepSource(false);
		enlargeRoi.setInputImage(anImage);
		enlargeRoi.setPixels(-this.getMebraneWidth());
		enlargeRoi.run();
		Roi cytoplasmRoi = (Roi)enlargeRoi.getResult().getRoi().clone();
		cytoplasms.set(selectedRow, cytoplasmRoi);
		MakeBandSelectionOperation makeBand = new MakeBandSelectionOperation();
		makeBand.setShowResult(false);
		makeBand.setKeepSource(false);
		makeBand.setInputImage(anImage);
		makeBand.setPixels(this.getMebraneWidth());
		makeBand.run();
		membranes.set(selectedRow, makeBand.getResult().getRoi());
		this.tableModelChanged();
	}

	public int getMebraneWidth() {
		return membraneWidth ;
	}

	public void setCytoplasm(int selectedRow, ImagePlus image) {
		cytoplasms.set(selectedRow, image.getRoi());
		this.tableModelChanged();
	}

	public void setMembrane(int selectedRow, ImagePlus image) {
		membranes.set(selectedRow, image.getRoi());
		this.tableModelChanged();
	}

	public void setNucleus(int selectedRow, ImagePlus image) {
		nuclei.set(selectedRow, image.getRoi());
		this.tableModelChanged();
	}

	public ImagePlus getBlueImage() {
		return blueImage;
	}

	public ImagePlus getGreenImage() {
		return greenImage;
	}

	public ImagePlus getRedImage() {
		return redImage;
	}

	public void setImageExisted(boolean b) {
		imageExisted = b;
	}
	 
	boolean imageExisted() {
		return imageExisted;
	}

	public void measure() {
		for (int i=0; i<cells.size(); i++) {
			if (membranes.get(i)!=null && cytoplasms.get(i)!=null && nuclei.get(i)!=null) {
				this.measureCell(i);
			}
		}
		this.getResults().show("intensity distribution");
	} 

	public void measureCell(int i) {
		this.setupWorkingImagesForCell(i); 
		measureCellOnImage(this.getGreenImage().getTitle(), greenWorkingImage, i);
		measureCellOnImage(this.getRedImage().getTitle(), redWorkingImage, i);
	}

	private void measureCellOnImage(String title, ImagePlus anImage, int cellIndex) {
		MeasureOperation measure = new MeasureOperation();
		measure.setKeepSource(false);
		measure.setShowResult(false);
		measure.setMeasureIntegratedDensity(true);
		Roi nucleus = (Roi) nuclei.get(cellIndex);
		anImage.setRoi(nucleus);
		measure.setInputImage(anImage);
		measure.run();
		double nucleusIntensity = measure.getMeasurements().getValueAsDouble(ResultsTable.INTEGRATED_DENSITY, 0);
		Roi cytoplasm = (Roi) cytoplasms.get(cellIndex);
		anImage.setRoi(cytoplasm);
		measure.setInputImage(anImage);
		measure.run();
		double cytoplasmIntensity = measure.getMeasurements().getValueAsDouble(ResultsTable.INTEGRATED_DENSITY, 0) - nucleusIntensity;
		Roi membrane = (Roi) membranes.get(cellIndex);
		anImage.setRoi(membrane);
		measure.setInputImage(anImage);
		measure.run();
		double membraneIntensity = measure.getMeasurements().getValueAsDouble(ResultsTable.INTEGRATED_DENSITY, 0);
		ResultsTable results = this.getResults();
		results.incrementCounter();
		results.setLabel(title, results.getCounter()-1);
		results.addValue("nucleus", nucleusIntensity);
		results.addValue("cytoplasm", cytoplasmIntensity);
		results.addValue("membrane", membraneIntensity);
	}

	private void setupWorkingImagesForCell(int cellIndex) {
		Duplicator copyAction = new Duplicator();
		redWorkingImage = copyAction.run(this.getRedImage());
		redWorkingImage.setTitle("red_tmp");
		greenWorkingImage = copyAction.run(this.getGreenImage());
		greenWorkingImage.setTitle("green_tmp");
		ShapeRoi outline = (ShapeRoi)((ShapeRoi)membranes.get(cellIndex)).clone();
		outline.or(new ShapeRoi((ShapeRoi)cytoplasms.get(cellIndex)));
		setupImage(redWorkingImage, outline);
		outline = (ShapeRoi)((ShapeRoi)membranes.get(cellIndex)).clone();
		outline.or(new ShapeRoi((ShapeRoi)cytoplasms.get(cellIndex)));
		setupImage(greenWorkingImage, outline);
	}

	private void setupImage(ImagePlus image, ShapeRoi outline) {
		image.setRoi(outline);
		InverseSelectionOperation inverse = new InverseSelectionOperation();
		inverse.setKeepSource(false);
		inverse.setShowResult(false);
		inverse.setInputImage(image);
		inverse.run();
		ClearOperation clear = new ClearOperation();
		clear.setShowResult(false);
		clear.setKeepSource(false);
		clear.setInputImage(inverse.getResult());
		clear.run();
		image.killRoi();
		ConvertImageTypeOperation convert = new ConvertImageTypeOperation();
		convert.setShowResult(false);
		convert.setKeepSource(false);
		convert.setInputImage(clear.getResult());
		convert.convertTo32Bit();
		convert.run();
		MeasureOperation measure = new MeasureOperation();
		measure.setShowResult(false);
		measure.setMeasureIntegratedDensity(true);
		measure.setInputImage(convert.getResult());
		measure.run();
		double totalIntensity = measure.getMeasurements().getValueAsDouble(ResultsTable.INTEGRATED_DENSITY, 0);
		DivideOperation divide = new DivideOperation();
		divide.setShowResult(false);
		divide.setInputImage(convert.getResult());
		divide.setKeepSource(false);
		divide.setValue(totalIntensity);
		divide.run();
	}

	public ResultsTable getResults() {
		if (results==null) {
			results = new ResultsTable();
		}
		return results;
	}

	public void setResults(ResultsTable results) {
		this.results = results;
	}

	public FluoDistributionInCellAnalyzerOptionsView getOptionsView() {
		optionsView = new FluoDistributionInCellAnalyzerOptionsView(this);
		return optionsView;
	}
}
