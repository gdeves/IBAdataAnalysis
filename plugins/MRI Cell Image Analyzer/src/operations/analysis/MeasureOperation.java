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
package operations.analysis;
import ij.IJ;
import ij.measure.ResultsTable;
import ij.text.TextWindow;
import imagejProxies.SilentAnalyzer;
import gui.Options;
import gui.options.BooleanOption;

/**
 * Measure the features of an image or a selection. 
 * This operation calls analyzer from ImageJ.
 * 
 * @author	Volker Baecker
 **/
public class MeasureOperation extends FindObjectsOperation {
	private static final long serialVersionUID = 492489307720350606L;

	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		parameterTypes = new Class[1];
		parameterTypes[0] = Class.forName("ij.ImagePlus");
		parameterNames = new String[1];
		parameterNames[0] = "InputImage";
		setupOptionNames();
		resultTypes = new Class[1];
		resultTypes[0] = Class.forName("ij.measure.ResultsTable");
		resultNames = new String[1];
		resultNames[0] = "Measurements";
	}

	protected void setupOptionNames() {
		optionsNames = new String[16];
		optionsNames[0] = "measure area";
		optionsNames[1] = "measure bounds";
		optionsNames[2] = "measure center of mass";
		optionsNames[3] = "measure centroids";
		optionsNames[4] = "measure circularity";
		optionsNames[5] = "Feret's diameter";
		optionsNames[6] = "measure fit ellipse";
		optionsNames[7] = "integrated density";
		optionsNames[8] = "measure mean";
		optionsNames[9] = "measure min & max";
		optionsNames[10] = "modal gray value";
		optionsNames[11] = "measure perimeter";
		optionsNames[12] = "standard deviation";
		optionsNames[13] = "display labels";
		optionsNames[14] = "invert y";
		optionsNames[15] = "limit to threshold";
	}
	
	public void doIt() {
		ResultsTable measurements = this.getMeasurements();
		measurements.reset();
		SilentAnalyzer analyzer = new SilentAnalyzer(this.getInputImage(), 
										 this.getMeasurementsWord(), 
										 measurements);
		analyzer.setup("", this.getInputImage());
		analyzer.run(this.getInputImage().getProcessor());
		if (this.getInputImage().getRoi()!= null && this.getInputImage().getRoi().isLine()) {
			((TextWindow)(IJ.getTextPanel().getParent())).close();
		}
	}
	
	protected void setupOptions() {
		options = new Options(); 
		options.setName(this.name() + " options");
		String[] optionsNames = this.getOptionsNames();
		// area
		this.measureArea = new BooleanOption();
		this.setMeasureArea(false);
		measureArea.setName(optionsNames[0]);
		options.add(this.measureArea);
		// bounds
		this.measureBounds = new BooleanOption();
		this.setMeasureBounds(true);
		measureBounds.setName(optionsNames[1]);
		options.add(this.measureBounds);
		// center of mass
		this.measureCenterOfMass = new BooleanOption();
		this.setMeasureCenterOfMass(false);
		measureCenterOfMass.setName(optionsNames[2]);
		options.add(this.measureCenterOfMass);
		// ccentroids
		this.measureCentroids = new BooleanOption();
		this.setMeasureCentroids(true);
		measureCentroids.setName(optionsNames[3]);
		options.add(this.measureCentroids);
		// circularity
		this.measureCircularity = new BooleanOption();
		this.setMeasureCircularity(true);
		measureCircularity.setName(optionsNames[4]);
		options.add(this.measureCircularity);
		// deret's diameter
		this.measureFeret = new BooleanOption();
		this.setMeasureFeret(false);
		measureFeret.setName(optionsNames[5]);
		options.add(this.measureFeret);
		// ellipse
		this.measureFitEllipse = new BooleanOption();
		this.setMeasureFitEllipse(false);
		measureFitEllipse.setName(optionsNames[6]);
		options.add(this.measureFitEllipse);
		// integrated density
		this.measureIntegratedDensity = new BooleanOption();
		this.setMeasureIntegratedDensity(false);
		measureIntegratedDensity.setName(optionsNames[7]);
		options.add(this.measureIntegratedDensity);
		// mean
		this.measureMean = new BooleanOption();
		this.setMeasureMean(false);
		measureMean.setName(optionsNames[8]);
		options.add(this.measureMean);
		// min max
		this.measureMinMaxGrayValue = new BooleanOption();
		this.setMeasureMinMaxGrayValue(false);
		measureMinMaxGrayValue.setName(optionsNames[9]);
		options.add(this.measureMinMaxGrayValue);
		// modal gray value
		this.measureMode = new BooleanOption();
		this.setMeasureMode(false);
		measureMode.setName(optionsNames[10]);
		options.add(this.measureMode);
		// perimeter
		this.measurePerimeter = new BooleanOption();
		this.setMeasurePerimeter(true);
		measurePerimeter.setName(optionsNames[11]);
		options.add(this.measurePerimeter);
		// standard deviation
		this.measureStandardDeviation = new BooleanOption();
		this.setMeasureStandardDeviation(false);
		measureStandardDeviation.setName(optionsNames[12]);
		options.add(this.measureStandardDeviation);
		// display labels
		this.displayLabels = new BooleanOption();
		this.setDisplayLabels(false);
		displayLabels.setName(optionsNames[13]);
		options.add(this.displayLabels);
		// invert y
		this.invertY = new BooleanOption();
		this.setInvertY(false);
		invertY.setName(optionsNames[14]);
		options.add(this.invertY);
		// limit to threshold
		this.limitToThreshold = new BooleanOption();
		this.setLimitToThreshold(false);
		limitToThreshold.setName(optionsNames[15]);
		options.add(this.limitToThreshold);		
	}
	
	public void connectOptions() {
		this.measureArea = (BooleanOption) this.options.getOptions().get(0);
		this.measureBounds = (BooleanOption) this.options.getOptions().get(1);
		this.measureCenterOfMass = (BooleanOption) this.options.getOptions().get(2);
		this.measureCentroids = (BooleanOption) this.options.getOptions().get(3);
		this.measureCircularity = (BooleanOption) this.options.getOptions().get(4);
		this.measureFeret = (BooleanOption) this.options.getOptions().get(5);
		this.measureFitEllipse = (BooleanOption) this.options.getOptions().get(6);
		this.measureIntegratedDensity = (BooleanOption) this.options.getOptions().get(7);
		this.measureMean = (BooleanOption) this.options.getOptions().get(8);
		this.measureMinMaxGrayValue = (BooleanOption) this.options.getOptions().get(9);
		this.measureMode = (BooleanOption) this.options.getOptions().get(10);
		this.measurePerimeter = (BooleanOption) this.options.getOptions().get(11);
		this.measureStandardDeviation = (BooleanOption) this.options.getOptions().get(12);
		this.displayLabels = (BooleanOption) this.options.getOptions().get(13);
		this.invertY = (BooleanOption) this.options.getOptions().get(14);
		this.limitToThreshold = (BooleanOption) this.options.getOptions().get(15);
	}
}
