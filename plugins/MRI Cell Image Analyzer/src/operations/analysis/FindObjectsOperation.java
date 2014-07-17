/* This file is part of MRI Cell Image Analyzer.
 * (c) 2005-2007 Volker B�cker
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
package operations.analysis;

import operations.Operation;
import gui.Options;
import gui.options.BooleanOption;
import gui.options.Option;
import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.measure.ResultsTable;
import ij.plugin.filter.Analyzer;
import ij.plugin.filter.ParticleAnalyzer;
import imagejProxies.SilentParticleAnalyzer;

/**
 * Count and measure features of objects. This operation calls the ImageJ particle analyzer.
 * 
 * @author	Volker Baecker
 **/
public class FindObjectsOperation extends Operation {
	private static final long serialVersionUID = -6870468713039447989L;
	ResultsTable measurements;
	ImagePlus mask;
	ImagePlus outlines;
	Option minSize;
	Option maxSize;
	BooleanOption displayLabels;
	BooleanOption excludeEdgeObjects;
	BooleanOption invertY;
	BooleanOption limitToThreshold;
	BooleanOption measureArea;
	BooleanOption measureBounds;
	BooleanOption measureCenterOfMass;
	BooleanOption measureCentroids;
	BooleanOption measureCircularity;
	BooleanOption measureFeret;
	BooleanOption measureFitEllipse;
	BooleanOption measureIntegratedDensity;
	BooleanOption measureMean;
	BooleanOption measureMinMaxGrayValue;
	BooleanOption measureMode;
	BooleanOption measurePerimeter;
	BooleanOption measureStandardDeviation;
	protected Thread workingThread; 

	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		parameterTypes = new Class[1];
		parameterTypes[0] = Class.forName("ij.ImagePlus");
		parameterNames = new String[1];
		parameterNames[0] = "InputImage";
		setupOptionNames();
		resultTypes = new Class[3];
		resultTypes[0] = Class.forName("ij.measure.ResultsTable");
		resultTypes[1] = Class.forName("ij.ImagePlus");
		resultTypes[2] = Class.forName("ij.ImagePlus");
		resultNames = new String[3];
		resultNames[0] = "Measurements";
		resultNames[1] = "Mask";
		resultNames[2] = "Outlines";
	}

	protected void setupOptionNames() {
		optionsNames = new String[19];
		optionsNames[0] = "min size";
		optionsNames[1] = "max size";
		optionsNames[2] = "display labels";
		optionsNames[3] = "exclude edge objects";
		optionsNames[4] = "invert y";
		optionsNames[5] = "limit to threshold";
		optionsNames[6] = "measure area";
		optionsNames[7] = "measure bounds";
		optionsNames[8] = "measure center of mass";
		optionsNames[9] = "measure centroids";
		optionsNames[10] = "measure circularity";
		optionsNames[11] = "Feret's diameter";
		optionsNames[12] = "measure fit ellipse";
		optionsNames[13] = "integrated density";
		optionsNames[14] = "measure mean";
		optionsNames[15] = "measure min & max";
		optionsNames[16] = "modal gray value";
		optionsNames[17] = "measure perimeter";
		optionsNames[18] = "standard deviation";
	}
	
	public boolean getExcludeEdgeObjects() {
		return excludeEdgeObjects.getBooleanValue();
	}

	public void setExcludeEdgeObjects(boolean excludeEdgeObjects) {
		this.excludeEdgeObjects.setValue(new Boolean(excludeEdgeObjects).toString());
	}

	public int getMaxSize() {
		return maxSize.getIntegerValue();
	}

	public void setMaxSize(int maxSize) {
		this.maxSize.setValue(new Integer(maxSize).toString());
	}

	public boolean getMeasureCentroids() {
		return measureCentroids.getBooleanValue();
	}

	public void setMeasureCentroids(boolean measureCentroids) {
		this.measureCentroids.setValue(new Boolean(measureCentroids).toString());
	}

	public ResultsTable getMeasurements() {
		if (measurements==null) {
			measurements = new ResultsTable();
		}
		return measurements;
	}

	public void setMeasurements(ResultsTable measurements) {
		this.measurements = measurements;
	}

	public int getMinSize() {
		return minSize.getIntegerValue();
	}

	public void setMinSize(int minSize) {
		this.minSize.setValue(new Integer(minSize).toString());	
		}

	public void doIt() {
		int measurementsWord = this.getMeasurementsWord();
		int oldMeasurements = Analyzer.getMeasurements();
		Analyzer.setMeasurements(measurementsWord);
		ImagePlus inputImage = this.getInputImage();
		ResultsTable measurements = this.getMeasurements();
		measurements.reset();
		SilentParticleAnalyzer analyzer = new SilentParticleAnalyzer(this.getOptionsWord(), 
				measurementsWord, 
				measurements,
				this.getMinSize(),
				this.getMaxSize());
		int previousShowChoice = SilentParticleAnalyzer.getStaticShowChoice();
		SilentParticleAnalyzer.setStaticShowChoice(2);	// mask
		analyzer.analyze(inputImage, inputImage.getProcessor());
		IJ.showStatus(measurements.getCounter() + " objects found");
		mask = analyzer.getOutlineImage();
		SilentParticleAnalyzer.setStaticShowChoice(previousShowChoice);
		this.computeOutlines();
		Analyzer.setMeasurements(oldMeasurements);
		workingThread = Thread.currentThread();
		if (mask!=null) mask.hide();
	}

	public ImagePlus getMask() {
		return mask;
	}

	public void setMask(ImagePlus mask) {
		if (mask==null && this.mask!=null) {
			this.mask.close();
			WindowManager.setTempCurrentImage(workingThread, null);
		}
		this.mask = mask;
	}

	protected int getMeasurementsWord() {
		int result = 0;
		if (this.getMeasureArea()) {
			result = result | ParticleAnalyzer.AREA; 
		}
		if (this.getMeasureBounds()) { 
			result = result | ParticleAnalyzer.RECT;
		}
		if (this.getMeasureCenterOfMass()) { 
			result = result | ParticleAnalyzer.CENTER_OF_MASS;
		}
		if (this.getMeasureCentroids()) {
			result = result | ParticleAnalyzer.CENTROID; 
		}
		if (this.getMeasureCircularity()) { 
			result = result | ParticleAnalyzer.CIRCULARITY;
		}
		if (this.getMeasureFeret()) { 
			result = result | ParticleAnalyzer.FERET;
		}
		if (this.getMeasureFitEllipse()) { 
			result = result | ParticleAnalyzer.ELLIPSE;
		}
		if (this.getMeasureIntegratedDensity()) { 
			result = result | ParticleAnalyzer.INTEGRATED_DENSITY;
		}
		if (this.getMeasureMean()) { 
			result = result | ParticleAnalyzer.MEAN;
		}
		if (this.getMeasureMinMaxGrayValue()) { 
			result = result | ParticleAnalyzer.MIN_MAX;
		}
		if (this.getMeasureMode()) { 
			result = result | ParticleAnalyzer.MODE;
		}
		if (this.getMeasurePerimeter()) { 
			result = result | ParticleAnalyzer.PERIMETER;
		}
		if (this.getMeasureStandardDeviation()) { 
			result = result | ParticleAnalyzer.STD_DEV;
		}
		if (this.getDisplayLabels()) {
			result = result | ParticleAnalyzer.LABELS;
		}
		if (this.getInvertY()) {
			result = result | ParticleAnalyzer.INVERT_Y;
		}
		if (this.getLimitToThreshold()) {
			result = result | ParticleAnalyzer.LIMIT;
		}
		return result;
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

	protected int getOptionsWord() {
		int result = 0;
		if (this.getExcludeEdgeObjects()) {
			result = result | ParticleAnalyzer.EXCLUDE_EDGE_PARTICLES;
		}
		return result;
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

	protected void showResult() {
		this.getMeasurements().show("result from find objects");
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
	
	protected void setupOptions() {
		options = new Options(); 
		options.setName(this.name() + " options");
		String[] optionsNames = this.getOptionsNames();
		// min 
		this.minSize = new Option();
		this.setMinSize(10);
		minSize.setName(optionsNames[0]);
		options.add(this.minSize);
		// max
		this.maxSize = new Option();
		maxSize.setName(optionsNames[1]);
		this.setMaxSize(999999);
		options.add(this.maxSize);
		// display labels
		this.displayLabels = new BooleanOption();
		this.setDisplayLabels(true);
		displayLabels.setName(optionsNames[2]);
		options.add(this.displayLabels);
		// exclude edge objects
		this.excludeEdgeObjects = new BooleanOption();
		this.setExcludeEdgeObjects(true);
		excludeEdgeObjects.setName(optionsNames[3]);
		options.add(this.excludeEdgeObjects);
		// invert y
		this.invertY = new BooleanOption();
		this.setInvertY(false);
		invertY.setName(optionsNames[4]);
		options.add(this.invertY);
		// limit to threshold
		this.limitToThreshold = new BooleanOption();
		this.setLimitToThreshold(false);
		limitToThreshold.setName(optionsNames[5]);
		options.add(this.limitToThreshold);
		// area
		this.measureArea = new BooleanOption();
		this.setMeasureArea(false);
		measureArea.setName(optionsNames[6]);
		options.add(this.measureArea);
		// bounds
		this.measureBounds = new BooleanOption();
		this.setMeasureBounds(true);
		measureBounds.setName(optionsNames[7]);
		options.add(this.measureBounds);
		// center of mass
		this.measureCenterOfMass = new BooleanOption();
		this.setMeasureCenterOfMass(false);
		measureCenterOfMass.setName(optionsNames[8]);
		options.add(this.measureCenterOfMass);
		// ccentroids
		this.measureCentroids = new BooleanOption();
		this.setMeasureCentroids(true);
		measureCentroids.setName(optionsNames[9]);
		options.add(this.measureCentroids);
		// circularity
		this.measureCircularity = new BooleanOption();
		this.setMeasureCircularity(true);
		measureCircularity.setName(optionsNames[10]);
		options.add(this.measureCircularity);
		// deret's diameter
		this.measureFeret = new BooleanOption();
		this.setMeasureFeret(false);
		measureFeret.setName(optionsNames[11]);
		options.add(this.measureFeret);
		// ellipse
		this.measureFitEllipse = new BooleanOption();
		this.setMeasureFitEllipse(false);
		measureFitEllipse.setName(optionsNames[12]);
		options.add(this.measureFitEllipse);
		// integrated density
		this.measureIntegratedDensity = new BooleanOption();
		this.setMeasureIntegratedDensity(false);
		measureIntegratedDensity.setName(optionsNames[13]);
		options.add(this.measureIntegratedDensity);
		// mean
		this.measureMean = new BooleanOption();
		this.setMeasureMean(false);
		measureMean.setName(optionsNames[14]);
		options.add(this.measureMean);
		// min max
		this.measureMinMaxGrayValue = new BooleanOption();
		this.setMeasureMinMaxGrayValue(false);
		measureMinMaxGrayValue.setName(optionsNames[15]);
		options.add(this.measureMinMaxGrayValue);
		// modal gray value
		this.measureMode = new BooleanOption();
		this.setMeasureMode(false);
		measureMode.setName(optionsNames[16]);
		options.add(this.measureMode);
		// perimeter
		this.measurePerimeter = new BooleanOption();
		this.setMeasurePerimeter(true);
		measurePerimeter.setName(optionsNames[17]);
		options.add(this.measurePerimeter);
		// standard deviation
		this.measureStandardDeviation = new BooleanOption();
		this.setMeasureStandardDeviation(false);
		measureStandardDeviation.setName(optionsNames[18]);
		options.add(this.measureStandardDeviation);
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

	public void connectOptions() {
		this.minSize = (Option) this.options.getOptions().get(0);
		this.maxSize = (Option) this.options.getOptions().get(1);
		this.displayLabels = (BooleanOption) this.options.getOptions().get(2);
		this.excludeEdgeObjects = (BooleanOption) this.options.getOptions().get(3);
		this.invertY = (BooleanOption) this.options.getOptions().get(4);
		this.limitToThreshold = (BooleanOption) this.options.getOptions().get(5);
		this.measureArea = (BooleanOption) this.options.getOptions().get(6);
		this.measureBounds = (BooleanOption) this.options.getOptions().get(7);
		this.measureCenterOfMass = (BooleanOption) this.options.getOptions().get(8);
		this.measureCentroids = (BooleanOption) this.options.getOptions().get(9);
		this.measureCircularity = (BooleanOption) this.options.getOptions().get(10);
		this.measureFeret = (BooleanOption) this.options.getOptions().get(11);
		this.measureFitEllipse = (BooleanOption) this.options.getOptions().get(12);
		this.measureIntegratedDensity = (BooleanOption) this.options.getOptions().get(13);
		this.measureMean = (BooleanOption) this.options.getOptions().get(14);
		this.measureMinMaxGrayValue = (BooleanOption) this.options.getOptions().get(15);
		this.measureMode = (BooleanOption) this.options.getOptions().get(16);
		this.measurePerimeter = (BooleanOption) this.options.getOptions().get(17);
		this.measureStandardDeviation = (BooleanOption) this.options.getOptions().get(18);
	}
	
	public void computeOutlines() {
		ResultsTable measurements = new ResultsTable();
		measurements.reset();
		SilentParticleAnalyzer analyzer = new SilentParticleAnalyzer(this.getOptionsWord(), 
				this.getMeasurementsWord(), 
				measurements,
				this.getMinSize(),
				this.getMaxSize());
		int previousShowChoice = SilentParticleAnalyzer.getStaticShowChoice();
		SilentParticleAnalyzer.setStaticShowChoice(1);
		analyzer.analyze(this.getInputImage(), this.getInputImage().getProcessor());
		outlines = analyzer.getOutlineImage();
		SilentParticleAnalyzer.setStaticShowChoice(previousShowChoice);
	}

	public ImagePlus getOutlines() {
		return outlines;
	}

	public void setOutlines(ImagePlus outlines) {
		if (outlines==null && this.outlines!=null) {
			this.outlines.close();
			WindowManager.setTempCurrentImage(workingThread, null);
		}
		this.outlines = outlines;
	}
}
