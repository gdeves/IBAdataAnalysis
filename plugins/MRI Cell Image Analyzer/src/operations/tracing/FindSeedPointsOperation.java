package operations.tracing;

import ij.ImagePlus;

import java.awt.Point;
import java.util.HashSet;

import gui.options.BooleanOption;
import gui.options.Option;
import operations.Operation;
import tracing.SeedPointsFinder;

public class FindSeedPointsOperation extends Operation {

	protected Option filter;
	protected Option horizontalLineFraction;
	protected Option verticalLineFraction;
	protected Option numberOfVerticalLines;
	protected HashSet<Point> resultSeeds;
	protected ImagePlus resultSeedsImage;
	protected SeedPointsFinder finder;
	/**
	 * 
	 */
	private static final long serialVersionUID = 6953549393331703939L;
	
	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		optionsNames = new String[3];
		optionsNames[0] = "filter";
		optionsNames[1] = "fraction of h-lines";
		optionsNames[2] = "fraction of v-lines";
		parameterTypes = new Class[1];
		parameterTypes[0] = Class.forName("ij.ImagePlus");
		parameterNames = new String[1];
		parameterNames[0] = "InputImage";
		resultTypes = new Class[2];
		resultTypes[0] = Class.forName("java.util.HashSet");
		resultTypes[1] = Class.forName("ij.ImagePlus");
		resultNames = new String[2];
		resultNames[0] = "ResultSeeds";
		resultNames[1] = "ResultSeedsImage";
	}
	
	public void doIt() {
		ImagePlus inputImage = this.getInputImage();
		int width = inputImage.getWidth();
		int height = inputImage.getHeight();
		finder = SeedPointsFinder.newFor(inputImage, 
								    	 Math.round(height*this.getHorizontalLineFraction()), 
										 Math.round(width*this.getVerticalLineFraction()));
		finder.run();
		if (this.getFilter()) {
			finder.filterSeedPoints();
		}
		this.resultSeeds = finder.getLocalMaxima();
	}
	
	public HashSet<Point> getResultSeeds() {
		return resultSeeds;
	}
	
	public void setResultSeeds(HashSet<Point> seeds) {
		this.resultSeeds = seeds;
	}
	
	public ImagePlus getResultSeedsImage() {
		return finder.getMaximaImage();
	}
	
	public void setResultSeedsImage(ImagePlus seedsImage) {
		this.finder=null;
	}
	
	public boolean getFilter() {
		return filter.getBooleanValue();
	}
	
	public void setFilter(boolean filter) {
		this.filter.setBooleanValue(filter);
	}
	
	protected void setupOptions() {
		super.setupOptions();
		
		String[] optionsNames = this.getOptionsNames();
		
		this.filter = new BooleanOption();
		this.filter.setName(optionsNames[0]);
		this.setFilter(false);
		this.filter.setShortHelpText("If selected unreliable seed points are suppressed");
		options.getOptions().set(0, this.filter);
		
		this.setHorizontalLineFraction(0.25f);
		horizontalLineFraction.setMin(0);
		horizontalLineFraction.setMax(1);
		horizontalLineFraction.setShortHelpText("Set the fraction of horizontal lines to be used.");
		
		this.setVerticalLineFraction(0.25f);
		verticalLineFraction.setMin(0);
		verticalLineFraction.setMax(1);
		verticalLineFraction.setShortHelpText("Set the fraction of vertical lines to be used.");
	}
	
	/**
	 * 
	 */
	protected void showResult() {
		this.getResultSeedsImage().show();
	}
	
	public void connectOptions() {
		filter = (Option) options.getOptions().get(0);
		horizontalLineFraction = (Option) options.getOptions().get(1);
		verticalLineFraction = (Option) options.getOptions().get(2);
	}

	public float getHorizontalLineFraction() {
		return horizontalLineFraction.getFloatValue();
	}

	public void setHorizontalLineFraction(float horizontalLineFraction) {
		this.horizontalLineFraction.setFloatValue(horizontalLineFraction);
	}

	public float getVerticalLineFraction() {
		return verticalLineFraction.getFloatValue();
	}

	public void setVerticalLineFraction(float verticalLineFraction) {
		this.verticalLineFraction.setFloatValue(verticalLineFraction);
	}
}
