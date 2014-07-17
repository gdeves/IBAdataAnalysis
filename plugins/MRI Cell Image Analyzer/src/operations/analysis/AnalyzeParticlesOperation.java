package operations.analysis;

/* This file is part of MRI Cell Image Analyzer.
 * (c) 2005-2007 Volker Bäcker
 * MRI Cell Image Analyzer has been created at Montpellier RIO Imaging
 * www.mri.cnrs.fr
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 * 
 */

import gui.Options;
import gui.options.BooleanOption;
import gui.options.Option;
import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.macro.Interpreter;
import ij.measure.ResultsTable;
import operations.Operation;

/**
 * Count and measure features of particles. Operation that wraps the ImageJ particle analyzer.
 * The redirect image is an input parameter. When the operation is run outside of an application
 * the redirect image is the input image itself. Besides the results table there are three possible 
 * result images: the mask, the outlines and the ellipses.
 *
 * @author Volker Baecker 
 **/ 
public class AnalyzeParticlesOperation extends Operation {
	protected ImagePlus redirectToImage;
	protected ImagePlus mask;
	protected ImagePlus outlines;
	protected ImagePlus ellipses;
	protected ResultsTable measurements;
	private Option minSize;
	private Option maxSize;
	private BooleanOption displayLabels;
	private BooleanOption excludeEdgeObjects;
	private BooleanOption invertY;
	private BooleanOption limitToThreshold;
	private BooleanOption measureArea;
	private BooleanOption measureBounds;
	private BooleanOption measureCenterOfMass;
	private BooleanOption measureCentroids;
	private BooleanOption measureCircularity;
	private BooleanOption measureFeret;
	private BooleanOption measureFitEllipse;
	private BooleanOption measureIntegratedDensity;
	private BooleanOption measureMean;
	private BooleanOption measureMinMaxGrayValue;
	private BooleanOption measureMode;
	private BooleanOption measurePerimeter;
	private BooleanOption measureStandardDeviation;
	private Option minCircularity;
	private Option maxCircularity;
	private BooleanOption measureSkewness;
	private BooleanOption measureKurtosis;
	private BooleanOption measureAreaFraction;
	private BooleanOption displaySliceNumber;
	private Option decimalPlaces;
	private BooleanOption addToManager;
	private BooleanOption includeHoles;
	private BooleanOption recordStarts;
	private BooleanOption measureMedian;
	private BooleanOption calculateMask;
	private BooleanOption calculateOutlines;
	private BooleanOption calculateEllipses;

	private static final long serialVersionUID = 3457557522958964259L;

	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		parameterTypes = new Class[2];
		parameterTypes[0] = Class.forName("ij.ImagePlus");
		parameterTypes[1] = Class.forName("ij.ImagePlus");
		parameterNames = new String[2];
		parameterNames[0] = "InputImage";
		parameterNames[1] = "RedirectToImage";
		setupOptionNames();
		resultTypes = new Class[4];
		resultTypes[0] = Class.forName("ij.ImagePlus");
		resultTypes[1] = Class.forName("ij.ImagePlus");
		resultTypes[2] = Class.forName("ij.ImagePlus");
		resultTypes[3] = Class.forName("ij.measure.ResultsTable");
		resultNames = new String[4];
		resultNames[0] = "Mask";
		resultNames[1] = "Outlines";
		resultNames[2] = "Ellipses";
		resultNames[3] = "Measurements";
	}
	
	protected void setupOptionNames() {
		optionsNames = new String[33];
		optionsNames[0] = "min size";
		optionsNames[1] = "max size";
		optionsNames[2] = "min circularity";
		optionsNames[3] = "max circularity";
		optionsNames[4] = "display labels";
		optionsNames[5] = "exclude edge objects";
		optionsNames[6] = "invert y";
		optionsNames[7] = "limit to threshold";
		optionsNames[8] = "measure area";
		optionsNames[9] = "measure bounds";
		optionsNames[10] = "measure center of mass";
		optionsNames[11] = "measure centroids";
		optionsNames[12] = "measure circularity";
		optionsNames[13] = "Feret's diameter";
		optionsNames[14] = "measure fit ellipse";
		optionsNames[15] = "integrated density";
		optionsNames[16] = "measure mean";
		optionsNames[17] = "measure min & max";
		optionsNames[18] = "modal gray value";
		optionsNames[19] = "measure perimeter";
		optionsNames[20] = "standard deviation";
		optionsNames[21] = "measure skewness";
		optionsNames[22] = "measure kurtosis";
		optionsNames[23] = "measure area fraction";
		optionsNames[24] = "measure median";
		optionsNames[25] = "slice number";
		optionsNames[26] = "decimal places";
		optionsNames[27] = "add to manager";
		optionsNames[28] = "include holes";
		optionsNames[29] = "record starts";
		optionsNames[30] = "calculate mask";
		optionsNames[31] = "calculate outlines";
		optionsNames[32] = "calculate ellipses";
	}
	
	public void connectOptions() {
		this.minSize = (Option) this.options.getOptions().get(0);
		this.maxSize = (Option) this.options.getOptions().get(1);
		this.minCircularity = (Option) this.options.getOptions().get(2);
		this.maxCircularity = (Option) this.options.getOptions().get(3);
		this.displayLabels = (BooleanOption) this.options.getOptions().get(4);
		this.excludeEdgeObjects = (BooleanOption) this.options.getOptions().get(5);
		this.invertY = (BooleanOption) this.options.getOptions().get(6);
		this.limitToThreshold = (BooleanOption) this.options.getOptions().get(7);
		this.measureArea = (BooleanOption) this.options.getOptions().get(8);
		this.measureBounds = (BooleanOption) this.options.getOptions().get(9);
		this.measureCenterOfMass = (BooleanOption) this.options.getOptions().get(10);
		this.measureCentroids = (BooleanOption) this.options.getOptions().get(11);
		this.measureCircularity = (BooleanOption) this.options.getOptions().get(12);
		this.measureFeret = (BooleanOption) this.options.getOptions().get(13);
		this.measureFitEllipse = (BooleanOption) this.options.getOptions().get(14);
		this.measureIntegratedDensity = (BooleanOption) this.options.getOptions().get(15);
		this.measureMean = (BooleanOption) this.options.getOptions().get(16);
		this.measureMinMaxGrayValue = (BooleanOption) this.options.getOptions().get(17);
		this.measureMode = (BooleanOption) this.options.getOptions().get(18);
		this.measurePerimeter = (BooleanOption) this.options.getOptions().get(19);
		this.measureStandardDeviation = (BooleanOption) this.options.getOptions().get(20);
		this.measureSkewness = (BooleanOption) this.options.getOptions().get(21);
		this.measureKurtosis = (BooleanOption) this.options.getOptions().get(22);
		this.measureAreaFraction = (BooleanOption) this.options.getOptions().get(23);
		this.measureMedian = (BooleanOption) this.options.getOptions().get(24);
		this.displaySliceNumber = (BooleanOption) this.options.getOptions().get(25);
		this.decimalPlaces = (Option) this.options.getOptions().get(26);
		this.addToManager = (BooleanOption) this.options.getOptions().get(27);
		this.includeHoles = (BooleanOption) this.options.getOptions().get(28);
		this.recordStarts = (BooleanOption) this.options.getOptions().get(29);
		this.calculateMask = (BooleanOption) this.options.getOptions().get(30);
		this.calculateOutlines = (BooleanOption) this.options.getOptions().get(31);
		this.calculateEllipses = (BooleanOption) this.options.getOptions().get(32);
	}
	
	public void doIt() {
		mask = null;
		outlines = null;
		ellipses = null;
		ImagePlus inputImage = this.getInputImage();
		WindowManager.setTempCurrentImage(inputImage);
		
		IJ.run("Set Measurements...", this.measurementsString());
		boolean wasBatchMode = Interpreter.isBatchMode();
		this.getInterpreter().setBatchMode(true);
		IJ.run("Analyze Particles...", this.optionsString());
		this.measurements = Operation.copySystemResultsTable();
		if (getCalculateMask()) setMask(WindowManager.getCurrentImage());
		if (getCalculateOutlines() && !getCalculateMask()) setOutlines(WindowManager.getCurrentImage());
		if (getCalculateEllipses() && !getCalculateMask() && !getCalculateOutlines()) setEllipses(WindowManager.getCurrentImage());
		WindowManager.setTempCurrentImage(inputImage);
		if (getCalculateOutlines() && getCalculateMask()) this.calculateOutlines();
		WindowManager.setTempCurrentImage(inputImage);
		if (getCalculateEllipses() && (getCalculateMask() || getCalculateOutlines())) this.calculateEllipses();
		IJ.run("Set Measurements...", this.resetRedirectToString());
		this.getInterpreter().setBatchMode(wasBatchMode);
		WindowManager.setTempCurrentImage(null);
	}
	
	private String resetRedirectToString() {
		String result = "redirect=None";
		return result;
	}

	private void calculateEllipses() {
		this.getInterpreter().setBatchMode(true);
		IJ.run("Analyze Particles...", this.ellipsesOptionsString());
		this.setEllipses(WindowManager.getCurrentImage());
	}

	private String ellipsesOptionsString() {
		String result = this.optionsString();
		if (getCalculateMask()) result = result.replace("Masks", "Ellipses"); 
		else result = result.replace("Outlines", "Ellipses");
		return result;
	}

	private void calculateOutlines() {
		this.getInterpreter().setBatchMode(true);
		IJ.run("Analyze Particles...", this.outlinesOptionsString());
		this.setOutlines(WindowManager.getCurrentImage());
	}

	private String outlinesOptionsString() {
		String result = this.optionsString().replace("Masks", "Outlines");
		return result;
	}

	private String optionsString() {
		String result = "size=";
		result += this.getMinSize();
		result += "-" + this.getMaxSize();
		result += " circularity=";
		result += this.getMinCircularity();
		result += "-" + this.getMaxCircularity();
		result += " show=";
		String showString = "Nothing";
		if (this.getCalculateEllipses()) showString = "Ellipses";
		if (this.getCalculateOutlines()) showString = "Outlines";
		if (this.getCalculateMask()) showString = "Masks";
		result += showString;
		if (this.getExcludeEdgeObjects()) result += " exclude";
		if (this.getIncludeHoles()) result += " include";
		if (this.getRecordStarts()) result += " record";
		if (this.getAddToManager()) result += " add";
		if (inputImage.getNSlices()>1) result += " stack";
		return result;
	}

	private String measurementsString() {
		String result = "";
		if (this.getMeasureArea()) result += "area";
		if (this.getMeasureMean()) result += " mean";
		if (this.getMeasureStandardDeviation()) result += " standard";
		if (this.getMeasureMode()) result += " modal";
		if (this.getMeasureMinMaxGrayValue()) result += " min";
		if (this.getMeasureCentroids()) result += " centroid";
		if (this.getMeasureCenterOfMass()) result += " center";
		if (this.getMeasurePerimeter()) result += " perimeter";
		if (this.getMeasureBounds()) result += " bonding";
		if (this.getMeasureFitEllipse()) result += " fit";
		if (this.getMeasureCircularity()) result += " circularity";
		if (this.getMeasureFeret()) result += " feret's";
		if (this.getMeasureIntegratedDensity()) result += " integrated";
		if (this.getMeasureMedian()) result += " median";
		if (this.getMeasureSkewness()) result += " skewness";
		if (this.getMeasureKurtosis()) result += " kurtosis";
		if (this.getMeasureAreaFraction()) result += " area_fraction";
		if (this.getDisplaySliceNumber()) result += " slice";
		if (this.getLimitToThreshold()) result += " limit";
		if (this.getInvertY()) result += " invert";
		result += " redirect=[" + this.getRedirectToImage().getTitle() + "]";
		result += " decimal=" + this.getDecimalPlaces();
		return result;
	}

	protected void setupOptions() {
		options = new Options(); 
		options.setName(this.name() + " options");
		String[] optionsNames = this.getOptionsNames();
		// min 
		this.minSize = new Option();
		this.setMinSize(0);
		minSize.setName(optionsNames[0]);
		options.add(this.minSize);
		// max
		this.maxSize = new Option();
		maxSize.setName(optionsNames[1]);
		this.setMaxSize(Float.POSITIVE_INFINITY);
		options.add(this.maxSize);
		// min circularity
		this.minCircularity= new Option();
		this.setMinCircularity(0);
		minCircularity.setName(optionsNames[2]);
		options.add(this.minCircularity);
		// max circularity
		this.maxCircularity = new Option();
		setMaxCircularity(1);
		maxCircularity.setName(optionsNames[3]);
		options.add(this.maxCircularity);
		// display labels
		this.displayLabels = new BooleanOption();
		this.setDisplayLabels(false);
		displayLabels.setName(optionsNames[4]);
		options.add(this.displayLabels);
		// exclude edge objects
		this.excludeEdgeObjects = new BooleanOption();
		this.setExcludeEdgeObjects(false);
		excludeEdgeObjects.setName(optionsNames[5]);
		options.add(this.excludeEdgeObjects);
		// invert y
		this.invertY = new BooleanOption();
		this.setInvertY(false);
		invertY.setName(optionsNames[6]);
		options.add(this.invertY);
		// limit to threshold
		this.limitToThreshold = new BooleanOption();
		this.setLimitToThreshold(false);
		limitToThreshold.setName(optionsNames[7]);
		options.add(this.limitToThreshold);
		// area
		this.measureArea = new BooleanOption();
		this.setMeasureArea(false);
		measureArea.setName(optionsNames[8]);
		options.add(this.measureArea);
		// bounds
		this.measureBounds = new BooleanOption();
		this.setMeasureBounds(false);
		measureBounds.setName(optionsNames[9]);
		options.add(this.measureBounds);
		// center of mass
		this.measureCenterOfMass = new BooleanOption();
		this.setMeasureCenterOfMass(false);
		measureCenterOfMass.setName(optionsNames[10]);
		options.add(this.measureCenterOfMass);
		// ccentroids
		this.measureCentroids = new BooleanOption();
		this.setMeasureCentroids(true);
		measureCentroids.setName(optionsNames[11]);
		options.add(this.measureCentroids);
		// circularity
		this.measureCircularity = new BooleanOption();
		this.setMeasureCircularity(false);
		measureCircularity.setName(optionsNames[12]);
		options.add(this.measureCircularity);
		// deret's diameter
		this.measureFeret = new BooleanOption();
		this.setMeasureFeret(false);
		measureFeret.setName(optionsNames[13]);
		options.add(this.measureFeret);
		// ellipse
		this.measureFitEllipse = new BooleanOption();
		this.setMeasureFitEllipse(false);
		measureFitEllipse.setName(optionsNames[14]);
		options.add(this.measureFitEllipse);
		// integrated density
		this.measureIntegratedDensity = new BooleanOption();
		this.setMeasureIntegratedDensity(false);
		measureIntegratedDensity.setName(optionsNames[15]);
		options.add(this.measureIntegratedDensity);
		// mean
		this.measureMean = new BooleanOption();
		this.setMeasureMean(false);
		measureMean.setName(optionsNames[16]);
		options.add(this.measureMean);
		// min max
		this.measureMinMaxGrayValue = new BooleanOption();
		this.setMeasureMinMaxGrayValue(false);
		measureMinMaxGrayValue.setName(optionsNames[17]);
		options.add(this.measureMinMaxGrayValue);
		// modal gray value
		this.measureMode = new BooleanOption();
		this.setMeasureMode(false);
		measureMode.setName(optionsNames[18]);
		options.add(this.measureMode);
		// perimeter
		this.measurePerimeter = new BooleanOption();
		this.setMeasurePerimeter(false);
		measurePerimeter.setName(optionsNames[19]);
		options.add(this.measurePerimeter);
		// standard deviation
		this.measureStandardDeviation = new BooleanOption();
		this.setMeasureStandardDeviation(false);
		measureStandardDeviation.setName(optionsNames[20]);
		options.add(this.measureStandardDeviation);
		// skewness
		this.measureSkewness = new BooleanOption();
		this.setMeasureSkewness(false);
		measureSkewness.setName(optionsNames[21]);
		options.add(measureSkewness);
		// kurtosis
		this.measureKurtosis = new BooleanOption();
		this.setMeasureKurtosis(false);
		measureKurtosis.setName(optionsNames[22]);
		options.add(measureKurtosis);
		// area fraction
		this.measureAreaFraction = new BooleanOption();
		this.setMeasureAreaFraction(false);
		measureAreaFraction.setName(optionsNames[23]);
		options.add(measureAreaFraction);
		// median
		this.measureMedian = new BooleanOption();
		this.setMeasureMedian(false);
		measureMedian.setName(optionsNames[24]);
		options.add(measureMedian);
		// slice number
		this.displaySliceNumber = new BooleanOption();
		this.setDisplaySliceNumber(true);
		displaySliceNumber.setName(optionsNames[25]);
		options.add(displaySliceNumber);
		// decimal places
		this.decimalPlaces = new Option();
		this.setDecimalPlaces(3);
		decimalPlaces.setName(optionsNames[26]);
		options.add(decimalPlaces);
		// add to manager
		this.addToManager = new BooleanOption();
		this.setAddToManager(false);
		addToManager.setName(optionsNames[27]);
		options.add(addToManager);
		// include holes
		this.includeHoles = new BooleanOption();
		this.setIncludeHoles(false);
		includeHoles.setName(optionsNames[28]);
		options.add(includeHoles);
		// record starts
		this.recordStarts = new BooleanOption();
		this.setRecordStarts(false);
		recordStarts.setName(optionsNames[29]);
		options.add(recordStarts);
		// calculate mask
		this.calculateMask = new BooleanOption();
		this.setCalculateMask(true);
		calculateMask.setName(optionsNames[30]);
		options.add(calculateMask);
		// calculate outlines
		this.calculateOutlines = new BooleanOption();
		this.setCalculateOutlines(false);
		calculateOutlines.setName(optionsNames[31]);
		options.add(calculateOutlines);
		// calculate ellipses
		this.calculateEllipses = new BooleanOption();
		this.setCalculateEllipses(false);
		calculateEllipses.setName(optionsNames[32]);
		options.add(calculateEllipses);
	}
	
	public ImagePlus getEllipses() {
		return ellipses;
	}

	public void setEllipses(ImagePlus ellipses) {
		this.ellipses = ellipses;
	}

	public ImagePlus getMask() {
		return mask;
	}

	public void setMask(ImagePlus mask) {
		this.mask = mask;
	}

	public ResultsTable getMeasurements() {
		return measurements;
	}

	public void setMeasurements(ResultsTable measurements) {
		this.measurements = measurements;
	}

	public ImagePlus getOutlines() {
		return outlines;
	}

	public void setOutlines(ImagePlus outlines) {
		this.outlines = outlines;
	}

	public ImagePlus getRedirectToImage() {
		if (redirectToImage==null) redirectToImage = inputImage;
		return redirectToImage;
	}

	public void setRedirectToImage(ImagePlus redirectToImage) {
		this.redirectToImage = redirectToImage;
	}

	public boolean getExcludeEdgeObjects() {
		return excludeEdgeObjects.getBooleanValue();
	}

	public void setExcludeEdgeObjects(boolean excludeEdgeObjects) {
		this.excludeEdgeObjects.setValue(new Boolean(excludeEdgeObjects).toString());
	}

	public float getMaxSize() {
		return maxSize.getFloatValue();
	}

	public void setMaxSize(float maxSize) {
		this.maxSize.setFloatValue(maxSize);
	}

	public boolean getMeasureCentroids() {
		return measureCentroids.getBooleanValue();
	}

	public void setMeasureCentroids(boolean measureCentroids) {
		this.measureCentroids.setValue(new Boolean(measureCentroids).toString());
	}
	
	public float getMinSize() {
		return minSize.getFloatValue();
	}

	public void setMinSize(float minSize) {
		this.minSize.setFloatValue(minSize);	
		}
	
	public boolean getMeasureStandardDeviation() {
		return this.measureStandardDeviation.getBooleanValue();
	}

	public boolean getMeasureMode() {
		return this.measureMode.getBooleanValue();
	}

	public boolean getMeasureMinMaxGrayValue() {
		return this.measureMinMaxGrayValue.getBooleanValue();
	}

	public boolean getMeasureMean() {
		return this.measureMean.getBooleanValue();
	}

	public boolean getMeasureIntegratedDensity() {
		return this.measureIntegratedDensity.getBooleanValue();
	}

	public boolean getMeasureFitEllipse() {
		return this.measureFitEllipse.getBooleanValue();
	}

	public boolean getMeasureFeret() {
		return this.measureFeret.getBooleanValue();
	}

	public boolean getMeasureCenterOfMass() {
		return this.measureCenterOfMass.getBooleanValue();
	}

	public boolean getMeasureArea() {
		return this.measureArea.getBooleanValue();	
	}
	
	public boolean getLimitToThreshold() {
		return this.limitToThreshold.getBooleanValue();
	}

	public boolean getInvertY() {
		return this.invertY.getBooleanValue();
	}

	public boolean getDisplayLabels() {
		return this.displayLabels.getBooleanValue();
	}
	
	public boolean getMeasureCircularity() {
		return measureCircularity.getBooleanValue();
	}

	public void setMeasureCircularity(boolean measureCircularity) {
		this.measureCircularity.setValue(new Boolean(measureCircularity).toString());
	}

	public boolean getMeasurePerimeter() {
		return measurePerimeter.getBooleanValue();
	}

	public void setMeasurePerimeter(boolean measurePerimeter) {
		this.measurePerimeter.setValue(new Boolean(measurePerimeter).toString());
	}

	public boolean getMeasureBounds() {
		return measureBounds.getBooleanValue();
	}

	public void setMeasureBounds(boolean measureBounds) {
		this.measureBounds.setValue(new Boolean(measureBounds).toString());
	}
	
	public void setMeasureStandardDeviation(boolean b) {
		this.measureStandardDeviation.setValue(Boolean.valueOf(b).toString());
	}

	public void setMeasureMode(boolean b) {
		this.measureMode.setValue(Boolean.valueOf(b).toString());
	}

	public void setMeasureMinMaxGrayValue(boolean b) {
		this.measureMinMaxGrayValue.setValue(Boolean.valueOf(b).toString());
	}

	public void setMeasureMean(boolean b) {
		this.measureMean.setValue(Boolean.valueOf(b).toString());
	}

	public void setMeasureIntegratedDensity(boolean b) {
		this.measureIntegratedDensity.setValue(Boolean.valueOf(b).toString());
	}

	public void setMeasureFitEllipse(boolean b) {
		this.measureFitEllipse.setValue(Boolean.valueOf(b).toString());
	}

	public void setMeasureFeret(boolean b) {
		this.measureFeret.setValue(Boolean.valueOf(b).toString());
	}

	public void setMeasureCenterOfMass(boolean b) {
		this.measureCenterOfMass.setValue(Boolean.valueOf(b).toString());
	}

	public void setMeasureArea(boolean b) {
		this.measureArea.setValue(Boolean.valueOf(b).toString());
	}

	public void setLimitToThreshold(boolean b) {
		this.limitToThreshold.setValue(Boolean.valueOf(b).toString());
	}

	public void setInvertY(boolean b) {
		this.invertY.setValue(Boolean.valueOf(b).toString());
		
	}

	public void setDisplayLabels(boolean b) {
		this.displayLabels.setValue(Boolean.valueOf(b).toString());
	}

	public boolean getAddToManager() {
		return addToManager.getBooleanValue();
	}

	public void setAddToManager(boolean addToManager) {
		this.addToManager.setBooleanValue(addToManager);
	}

	public int getDecimalPlaces() {
		return decimalPlaces.getIntegerValue();
	}

	public void setDecimalPlaces(int decimalPlaces) {
		this.decimalPlaces.setIntegerValue(decimalPlaces);
	}

	public boolean getDisplaySliceNumber() {
		return displaySliceNumber.getBooleanValue();
	}

	public void setDisplaySliceNumber(boolean displaySliceNumber) {
		this.displaySliceNumber.setBooleanValue(displaySliceNumber);
	}

	public boolean getIncludeHoles() {
		return includeHoles.getBooleanValue();
	}

	public void setIncludeHoles(boolean includeHoles) {
		this.includeHoles.setBooleanValue(includeHoles);
	}

	public boolean getMeasureAreaFraction() {
		return measureAreaFraction.getBooleanValue();
	}

	public void setMeasureAreaFraction(boolean measureAreaFraction) {
		this.measureAreaFraction.setBooleanValue(measureAreaFraction);
	}

	public boolean getMeasureKurtosis() {
		return measureKurtosis.getBooleanValue();
	}

	public void setMeasureKurtosis(boolean measureKurtosis) {
		this.measureKurtosis.setBooleanValue(measureKurtosis);
	}

	public boolean getMeasureSkewness() {
		return measureSkewness.getBooleanValue();
	}

	public void setMeasureSkewness(boolean measureSkewness) {
		this.measureSkewness.setBooleanValue(measureSkewness);
	}

	public float getMinCircularity() {
		return minCircularity.getFloatValue();
	}

	public void setMinCircularity(float minCircularity) {
		this.minCircularity.setFloatValue(minCircularity);
	}

	public boolean getRecordStarts() {
		return recordStarts.getBooleanValue();
	}

	public void setRecordStarts(boolean recordStarts) {
		this.recordStarts.setBooleanValue(recordStarts);
	}

	public float getMaxCircularity() {
		return maxCircularity.getFloatValue();
	}

	public void setMaxCircularity(float maxCircularity) {
		this.maxCircularity.setFloatValue(maxCircularity);
	}

	public boolean getMeasureMedian() {
		return measureMedian.getBooleanValue();
	}

	public void setMeasureMedian(boolean measureMedian) {
		this.measureMedian.setBooleanValue(measureMedian);
	}
	
	public void showResult() {
		this.getMeasurements().show("results of analyze particles operation");
		if (getCalculateMask()) this.getMask().show();
		if (getCalculateOutlines()) this.getOutlines().show();
		if (getCalculateEllipses()) this.getEllipses().show();
	}

	public boolean getCalculateEllipses() {
		return calculateEllipses.getBooleanValue();
	}

	public void setCalculateEllipses(boolean calculateEllipses) {
		this.calculateEllipses.setBooleanValue(calculateEllipses);
	}

	public boolean getCalculateMask() {
		return calculateMask.getBooleanValue();
	}

	public void setCalculateMask(boolean calculateMask) {
		this.calculateMask.setBooleanValue(calculateMask);
	}

	public boolean getCalculateOutlines() {
		return calculateOutlines.getBooleanValue();
	}

	public void setCalculateOutlines(boolean calculateOutlines) {
		this.calculateOutlines.setBooleanValue(calculateOutlines);
	}
}
