/*
 * Created on 13.10.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package operations.reporting;

import gui.ListEditor;
import gui.options.Option;
import ij.IJ;
import ij.io.OpenDialog;
import ij.io.SaveDialog;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JFileChooser;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import operations.Operation;
import reporting.ExcelIO;
import reporting.ExcelWriter;

/**
 * @author Volker
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class ReportingOperation extends Operation {

	private static final long serialVersionUID = 6509614725852348096L;
	protected String imageName;
	protected Option outputPath;
	protected String filename;
	protected String folder;
	protected String path;
	protected boolean continueExistingFile;
	
	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		optionsNames = new String[1];
		optionsNames[0] = "output path";
		resultTypes = new Class[0];
		resultNames = new String[0];
	}

	protected String getImageFolder() {
		int index = imageName.lastIndexOf(File.separator);
		return imageName.substring(0, index+1);
	}

	/**
	 * @return Returns the imageName.
	 */
	public String getImageName() {
		int index = imageName.lastIndexOf(File.separator);
		return imageName.substring(index+1, imageName.length());
	}

	/**
	 * @param imageName The imageName to set.
	 */
	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	/**
	 * @return Returns the outputPath.
	 */
	public String getOutputPath() {
		return outputPath.getValue();
	}

	/**
	 * @param outputPath The outputPath to set.
	 */
	public void setOutputPath(String outputPath) {
		this.outputPath.setValue(outputPath);
	}

	protected void setupOptions() {
		super.setupOptions();
		this.setOutputPath(null);
		outputPath.beForFilename();
	}

	public void connectOptions() {
		this.outputPath = (Option) this.options.getOptions().get(0);
		outputPath.beForFilename();
	}

	public void getFilenameFromUser() {
		String defaultDir = OpenDialog.getDefaultDirectory();
		SaveDialog saveDialog = new SaveDialog("Save data", "report", ".xls");
		filename = saveDialog.getFileName();
		if (filename==null) {
			this.path = null;
			return;
		}
		folder = saveDialog.getDirectory();
		path = folder + filename;
		this.setOutputPath(path);
		OpenDialog.setDefaultDirectory(defaultDir);
	}

	/**
	 * @return Returns the filename.
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * @param filename The filename to set.
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}

	public void resetPathOptions() {
		if (!this.isInteractive) return;
		this.setOutputPath(null);
	}

	public void setPathOptionsFromUser() {
		if (!this.isInteractive) return;
		this.getFilenameFromUser();
		if (this.getOutputPath()==null || this.getOutputPath().trim().equals("")) {
	    	this.stopApplication();
	    	return;
	    }
	}
	
	/**
	 * @param excelIO
	 */
	protected void createWorkbook(ExcelIO excelIO, int numberOfSheets) {
		excelIO.wb = new HSSFWorkbook();
		excelIO.s = excelIO.wb.createSheet("Sheet"+(1));
		continueExistingFile = false;
		for (int i=0; i<numberOfSheets; i++) {
			excelIO.addWorkSheet();
		}
	}

	/**
	 * @param wb
	 */
	protected void saveWorkbook(HSSFWorkbook wb) {
		 FileOutputStream out = null;
		try {
			out = new FileOutputStream(this.getOutputPath());
			wb.write(out);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	/**
	 * 
	 */
	protected void setupOutputPath() {
		if (this.getOutputPath()==null || this.getOutputPath().trim().equals("")) {
			this.getFilenameFromUser();
		}
	}

	public int browseFileForOption(Option anOption) {
		JFileChooser fileChooser = new JFileChooser();
		if (IJ.getDirectory("image")!=null) {
			fileChooser.setCurrentDirectory(new File(IJ.getDirectory("image")));
		}
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.addChoosableFileFilter(ListEditor.getExcelFileFilter());
		int returnVal = fileChooser.showSaveDialog(anOption.getView());
		if (returnVal != JFileChooser.APPROVE_OPTION) return -1;
		anOption.setValue(fileChooser.getSelectedFile().getAbsolutePath());
		return returnVal;
	}

	protected void cleanUpInput() {
		super.cleanUpInput();
		this.setImageName(null);
		this.filename = null;
		this.folder = null;
		this.path = null;
	}
	
	protected ExcelIO loadOrCreateWorkbook(int numberOfSheets) {
		ExcelIO excelIO = new ExcelIO();
		excelIO.setFilePath(this.getOutputPath());
		File outFile = new File(this.getOutputPath());
		if (outFile.exists()) {
			excelIO.loadExcelFile();
			continueExistingFile = true;
		} else {
			createWorkbook(excelIO, numberOfSheets);
		}
		return excelIO;
	}
	
	/**
	 * @return
	 */
	abstract protected String headingsFirstSheet();
	
	abstract protected String getSheetName(int i);

	/**
	 * @param excelProxy
	 * @param excelIO
	 */
	protected void createHeadingsFirstSheet(ExcelWriter excelProxy, ExcelIO excelIO) {
		if (continueExistingFile) return;
		excelProxy.setSheet(excelIO.wb.getSheetAt(0));
		excelIO.wb.setSheetName(0, this.getSheetName(0));
		excelProxy.writeTabSeparatedColumnHeadings(this.headingsFirstSheet());
	}

	protected void createHeadingsSecondSheet(ExcelWriter excelProxy, ExcelIO excelIO) {
		createHeadingsSheet(1, excelProxy, excelIO);
	}
	
	protected void createHeadingsSheet(int sheet, ExcelWriter excelProxy, ExcelIO excelIO) {
		excelProxy.setSheet(excelIO.wb.getSheetAt(sheet));
		excelIO.wb.setSheetName(sheet, this.getSheetName(sheet));
		if (continueExistingFile) return;
		String line = headingsSecondSheet();
		excelProxy.writeTabSeparatedColumnHeadings(line);
	}
	
	protected String headingsSecondSheet() {
		return "";
	}
}
