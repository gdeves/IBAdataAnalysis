package operations.stack;

import gui.options.Option;
import ij.ImagePlus;
import ij.measure.ResultsTable;

import java.util.Vector;

import operations.Operation;
import operations.file.OpenImageOperation;
import operations.file.SaveImageOperation;
import operations.image.ResizeToRotateOperation;
import sun.awt.shell.ShellFolder;

public class AutoAlignSlicesOperation extends Operation {

	protected Option padNTimes;
	protected Vector<ShellFolder> imageList;
	protected ResultsTable translations;
	protected int width;
	protected int height;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6047685837168879960L;

	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		parameterTypes = new Class[1];
		parameterTypes[0] = Class.forName("java.util.Vector");
		parameterNames = new String[1];
		parameterNames[0] = "ImageList";
		resultTypes = new Class[1];
		resultTypes[0] = Class.forName("ij.measure.ResultsTable");
		resultNames = new String[1];
		resultNames[0] = "Translations";
		optionsNames = new String[1];
		optionsNames[0] = "pad n times";
	}
	
	public void doIt() {
		int counter = 0;
		OpenImageOperation open = new OpenImageOperation();
		String firstImageFileName = ((ShellFolder)(this.getImageList().get(counter))).getAbsolutePath();
		open.setAbsoluteFilename(firstImageFileName);
		open.setShowResult(false);
		open.run();
		ImagePlus firstImage = open.getResult();
		AutoAlignImagesOperation autoAlign = new AutoAlignImagesOperation();
		autoAlign.setShowResult(false);
		autoAlign.setWidth(firstImage.getWidth());
		autoAlign.setHeight(firstImage.getHeight());
		// pad first image, save it and add first translation of 0,0 to results table
		this.padImage(firstImage);
		SaveImageOperation save = new SaveImageOperation();
		save.setShowResult(false);
		save.setInputImage(firstImage);
		save.setPath(firstImageFileName);
		save.setOutputFolder("aligned");
		save.setCreateInSourceFolder(true);
		save.run();
		this.translations = new ResultsTable();
		this.translations.reset();
		translations.incrementCounter();
		translations.addValue(ResultsTable.X_CENTROID, 0);
		translations.addValue(ResultsTable.Y_CENTROID, 0);
		translations.addValue(ResultsTable.INTEGRATED_DENSITY, 0);
		counter++;
		double xTrans=0;
		double yTrans=0;
		while (counter<this.getImageList().size()) {
			System.gc();
			String secondImageFileName = ((ShellFolder)(this.getImageList().get(counter))).getAbsolutePath();
			open.setAbsoluteFilename(secondImageFileName);
			open.run();
			ImagePlus secondImage = open.getResult();
			this.padImage(secondImage);
			autoAlign.setStartOffsetX((int)Math.round(xTrans));
			autoAlign.setStartOffsetY((int)Math.round(yTrans));
			autoAlign.setInputImage(firstImage);
			autoAlign.setSecondInputImage(secondImage);
			autoAlign.run();
			ImagePlus newSecondImage = autoAlign.getResult();
			ResultsTable translation = autoAlign.getTranslation();
			// save second image and add line to results table
			save.setInputImage(newSecondImage);
			save.setPath(secondImageFileName);
			save.run();
			xTrans = translation.getValueAsDouble(ResultsTable.X_CENTROID, translation.getCounter()-1);
			yTrans = translation.getValueAsDouble(ResultsTable.Y_CENTROID, translation.getCounter()-1);
			double score = translation.getValueAsDouble(ResultsTable.INTEGRATED_DENSITY, translation.getCounter()-1);
			translations.incrementCounter();
			translations.addValue(ResultsTable.X_CENTROID, xTrans);
			translations.addValue(ResultsTable.Y_CENTROID, yTrans);
			translations.addValue(ResultsTable.INTEGRATED_DENSITY, score);
			// prepare next loop cycle
			firstImage = newSecondImage;
			counter++;
		}
	}

	protected void padImage(ImagePlus anImage) {
		ResizeToRotateOperation pad = new ResizeToRotateOperation();
		pad.setInputImage(anImage);
		pad.setKeepSource(false);
		for (int i=0; i<this.padNTimes(); i++) {
			pad.doIt();
		}	
	}

	private int padNTimes() {
		return this.getPadNTimes();
	}
	
	public Vector<ShellFolder> getImageList() {
		return imageList;
	}

	public void setImageList(Vector<ShellFolder> imageList) {
		this.imageList = imageList;
	}

	public ResultsTable getTranslations() {
		return translations;
	}

	public void setTranslations(ResultsTable translations) {
		this.translations = translations;
	}
	
	protected void setupOptions() {
		super.setupOptions();
		this.setPadNTimes(3);
		padNTimes.setShortHelpText("enlarge the image canvas n times");
	}
	
	public void connectOptions() {
		padNTimes = (Option)options.getOptions().get(0);
	}

	public int getPadNTimes() {
		return padNTimes.getIntegerValue();
	}

	public void setPadNTimes(int padNTimes) {
		this.padNTimes.setValue(Integer.toString(padNTimes));
	}
}
