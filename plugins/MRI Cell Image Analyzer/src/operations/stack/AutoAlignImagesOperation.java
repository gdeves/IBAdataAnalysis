package operations.stack;

import gui.options.Option;
import ij.ImagePlus;
import ij.WindowManager;
import ij.measure.ResultsTable;
import operations.Operation;
import operations.analysis.ComputeDifferenceOperation;
import operations.control.WaitForUserOperation;
import operations.processing.TranslateImageOperation;

public class AutoAlignImagesOperation extends Operation {
	protected ImagePlus secondInputImage;
	protected ResultsTable translation;
	protected Option width;
	protected Option height;
	protected Option startOffsetX;
	protected Option startOffsetY;
	
	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		parameterTypes = new Class[2];
		parameterTypes[0] = Class.forName("ij.ImagePlus");
		parameterTypes[1] = Class.forName("ij.ImagePlus");
		parameterNames = new String[2];
		parameterNames[0] = "InputImage";
		parameterNames[1] = "SecondInputImage";
		resultTypes = new Class[2];
		resultTypes[0] = Class.forName("ij.ImagePlus");
		resultTypes[1] = Class.forName("ij.measure.ResultsTable");
		resultNames = new String[2];
		resultNames[0] = "Result";
		resultNames[1] = "Translation";
		optionsNames = new String[4];
		optionsNames[0] = "width";
		optionsNames[1] = "height";
		optionsNames[2] = "start offset x";
		optionsNames[3] = "start offset y";
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -1088871617082291118L;

	
	public void doIt() {
		inputImage = this.getInputImage();
		secondInputImage = this.getSecondInputImage();
		int x0 = this.getStartOffsetX() - this.getWidth() / 2;
		int y0 = this.getStartOffsetY() - this.getHeight() / 2;
		TranslateImageOperation translate = new TranslateImageOperation();
		translate.setInputImage(secondInputImage);
		translate.setKeepSource(false);
		translate.setShowResult(false);
		translate.setDeltaX(x0);
		translate.setDeltaY(y0);
		translate.run();
		ImagePlus startSecondImage = this.copyImage(secondInputImage, secondInputImage.getTitle() + " start");
		int bestX = x0;
		int bestY = y0;
		result = secondInputImage;
		double bestScoreSoFar = AutoAlignImagesOperation.computeDifference(inputImage, secondInputImage);
		for (int i=1; i<this.getWidth(); i+=10) {
			for (int j=1; j<this.getHeight(); j+=10) {
				translate.setKeepSource(true);
				translate.setInputImage(startSecondImage);
				translate.setDeltaX(i);
				translate.setDeltaY(j);
				translate.run();
				double newScore = AutoAlignImagesOperation.computeDifference(inputImage, translate.getResult());
				System.out.println((x0 + i) + ", " + (y0 + j) + " : " + newScore);
				if (newScore<bestScoreSoFar) {
					System.out.println("---");
					bestScoreSoFar = newScore;
					result = translate.getResult();
					bestX = x0 + i;
					bestY = y0 + j;
				}
			}
		}
		x0 = bestX;
		y0 = bestY;
		startSecondImage = result;
		for (int i=-10; i<=10; i++) {
			for (int j=-10; j<=10; j++) {
				translate.setKeepSource(true);
				translate.setInputImage(startSecondImage);
				translate.setDeltaX(i);
				translate.setDeltaY(j);
				translate.run();
				double newScore = AutoAlignImagesOperation.computeDifference(inputImage, translate.getResult());
				if (newScore<bestScoreSoFar) {
					bestScoreSoFar = newScore;
					result = translate.getResult();
					bestX = x0 + i;
					bestY = y0 + j;
				}
			}
		}
		
		
		this.translation= new ResultsTable();
		this.translation.reset();
		translation.incrementCounter();
		translation.addValue(ResultsTable.X_CENTROID, bestX);
		translation.addValue(ResultsTable.Y_CENTROID, bestY);
		translation.addValue(ResultsTable.INTEGRATED_DENSITY, bestScoreSoFar);
		System.out.println("best score: " + bestScoreSoFar);
	}
	
	public static double computeDifference(ImagePlus firstImage, ImagePlus secondImage) {
		ComputeDifferenceOperation difference = new ComputeDifferenceOperation();
		difference.setShowResult(false);
		difference.setFirstImage(firstImage);
		difference.setSecondImage(secondImage);
		difference.run();
		ResultsTable resultTable = difference.getDifference();
		double result = resultTable.getValue("average difference", 0);
		return  result;
	}
	
	public void setSecondInputImage(ImagePlus anImage) {
		this.secondInputImage = anImage;
	}

	public ImagePlus getSecondInputImage() {
		if (secondInputImage==null) {
			if (application==null) {
				WaitForUserOperation wait = new WaitForUserOperation();
				wait.run();
			}
			secondInputImage = WindowManager.getCurrentImage();
		}
		return secondInputImage;
	}
	
	public ResultsTable getTranslation() {
		return translation;
	}

	public void setTranslation(ResultsTable translation) {
		this.translation = translation;
	}

	public int getHeight() {
		return height.getIntegerValue();
	}

	public void setHeight(int height) {
		this.height.setValue(Integer.toString(height));
	}

	public int getWidth() {
		return width.getIntegerValue();
	}

	public void setWidth(int width) {
		this.width.setValue(Integer.toString(width));
	}
	
	public void connectOptions() {
		width = (Option)options.getOptions().get(0);
		height = (Option)options.getOptions().get(1);
		startOffsetX = (Option)options.getOptions().get(2);
		startOffsetY = (Option)options.getOptions().get(3);
	}
	
	protected void setupOptions() {
		super.setupOptions();
		this.setWidth(400);
		width.setShortHelpText("the x-size of the image within the black area");
		this.setHeight(300);
		height.setShortHelpText("the y-size of the image within the black area");
		this.setStartOffsetX(0);
		startOffsetX.setShortHelpText("the x-offset from the middle of the image within the black area");
		this.setStartOffsetY(0);
		startOffsetY.setShortHelpText("the y-offset from the middle of the image within the black area");
	}

	public int getStartOffsetY() {
		return startOffsetY.getIntegerValue();
	}

	public void setStartOffsetY(int startOffsetY) {
		this.startOffsetY.setValue(Integer.toString(startOffsetY));
	}

	public int getStartOffsetX() {
		return startOffsetX.getIntegerValue();
	}

	public void setStartOffsetX(int startOffsetX) {
		this.startOffsetX.setValue(Integer.toString(startOffsetX));
	}
}
