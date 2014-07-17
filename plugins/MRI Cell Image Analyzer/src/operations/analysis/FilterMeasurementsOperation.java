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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import gui.options.Option;
import ij.measure.ResultsTable;
import operations.Operation;

/**
 * Filter a results table using minimal and maximal values of the measurements.
 * 
 * @author	Volker Baecker
 **/
public class FilterMeasurementsOperation extends Operation {
	private static final long serialVersionUID = -5098569786394788296L;
	protected ResultsTable measurements;
	protected ResultsTable resultMeasurements;
	protected Option minArea;
	protected Option maxArea;
	protected Option minMean;
	protected Option maxMean;
	protected Option minStdDev;
	protected Option maxStdDev;
	protected Option minMode;
	protected Option maxMode;
	protected Option minMin;
	protected Option maxMin;
	protected Option minMax;
	protected Option maxMax;
	protected Option minCentroidX;
	protected Option maxCentroidX;
	protected Option minCentroidY;
	protected Option maxCentroidY;
	protected Option minCenterOfMassX;
	protected Option maxCenterOfMassX;
	protected Option minCenterOfMassY;
	protected Option maxCenterOfMassY;
	protected Option minPerimeter;
	protected Option maxPerimeter;
	protected Option minBoundingBoxX;
	protected Option maxBoundingBoxX;
	protected Option minBoundingBoxY;
	protected Option maxBoundingBoxY;
	protected Option minBoundingBoxWidth;
	protected Option maxBoundingBoxWidth;
	protected Option minBoundingBoxHeight;
	protected Option maxBoundingBoxHeight;
	protected Option minMajor;
	protected Option maxMajor;
	protected Option minMinor;
	protected Option maxMinor;
	protected Option minAngle;
	protected Option maxAngle;
	protected Option minCircularity;
	protected Option maxCircularity;
	protected Option minFeret;
	protected Option maxFeret;
	protected Option minIntDensity;
	protected Option maxIntDensity;
	protected Option minMedian;
	protected Option maxMedian;
	protected Option minSkew;
	protected Option maxSkew;
	protected Option minKurt;
	protected Option maxKurt;
	private HashMap<Option, Integer> map;

	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		parameterTypes = new Class[1];
		parameterTypes[0] = Class.forName("ij.measure.ResultsTable");
		parameterNames = new String[1];
		parameterNames[0] = "Measurements";
		optionsNames = new String[48];
		optionsNames[0] = "min area";
		optionsNames[1] = "max area";
		optionsNames[2] = "min mean";
		optionsNames[3] = "max mean";
		optionsNames[4] = "min std. dev.";
		optionsNames[5] = "max std. dev.";
		optionsNames[6] = "min mode";
		optionsNames[7] = "max mode";
		optionsNames[8] = "min min intensity";
		optionsNames[9] = "max min intensity";
		optionsNames[10] = "min max intensity";
		optionsNames[11] = "max max intensity";
		optionsNames[12] = "min centroid x";
		optionsNames[13] = "max centroid x";
		optionsNames[14] = "min centroid y";
		optionsNames[15] = "max centroid y";
		optionsNames[16] = "min center of mass x";
		optionsNames[17] = "max center of mass x";
		optionsNames[18] = "min center of mass y";
		optionsNames[19] = "max center of mass y";
		optionsNames[20] = "min perimeter";
		optionsNames[21] = "max perimeter";
		optionsNames[22] = "min bounding box x";
		optionsNames[23] = "max bounding box x";
		optionsNames[24] = "min bounding box y";
		optionsNames[25] = "max bounding box y";
		optionsNames[26] = "min bounding box width";
		optionsNames[27] = "max bounding box width";
		optionsNames[28] = "min bounding box height";
		optionsNames[29] = "max bounding box height";
		optionsNames[30] = "min major";
		optionsNames[31] = "max major";
		optionsNames[32] = "min minor";
		optionsNames[33] = "max minor";
		optionsNames[34] = "min angle";
		optionsNames[35] = "max angle";
		optionsNames[36] = "min circularity";
		optionsNames[37] = "max circularity";
		optionsNames[38] = "min feret";
		optionsNames[39] = "max feret";
		optionsNames[40] = "min int. density";
		optionsNames[41] = "max int. density";
		optionsNames[42] = "min median";
		optionsNames[43] = "max median";
		optionsNames[44] = "min skew";
		optionsNames[45] = "max skew";
		optionsNames[46] = "min kurt";
		optionsNames[47] = "max kurt";
		resultTypes = new Class[1];
		resultTypes[0] = Class.forName("ij.measure.ResultsTable");
		resultNames = new String[1];
		resultNames[0] = "ResultMeasurements";
	}
	
	public ResultsTable getMeasurements() {
		if (measurements == null) {
			measurements = ResultsTable.getResultsTable();
		}
		return measurements;
	}

	public void setMeasurements(ResultsTable measurements) {
		this.measurements = measurements;
	}

	public ResultsTable getResultMeasurements() {
		return resultMeasurements;
	}

	public void setResultMeasurements(ResultsTable resultMeasurements) {
		this.resultMeasurements = resultMeasurements;
	}
	
	protected void cleanUpInput() {
		this.setMeasurements(null);
	}

	protected void showResult() {
		this.getResultMeasurements().show("objects");
	}

	public void doIt() {
		resultMeasurements = new ResultsTable();
		resultMeasurements.reset();
		ResultsTable measurements = this.getMeasurements();
		for (int i=0; i<measurements.getCounter(); i++) {
			if (this.shouldAccept(i)){
				resultMeasurements.incrementCounter();
				for (int j=0; j<ResultsTable.MAX_COLUMNS; j++) {
					if (measurements.columnExists(j)) {
					resultMeasurements.addValue(j, measurements.getValueAsDouble(j, i));
					}
				}
			}
		}
	}

	private boolean shouldAccept(int i) {
		ResultsTable measurements = this.getMeasurements();
		boolean mustBeBigger = true;
		Map<Option, Integer> theMap = this.columnForOption();
		Set<Option> keys = theMap.keySet();
		int counter = 0;
		for (Option option : keys) {
			counter++;
			if (!valueSet(option.getValue())) continue;
			mustBeBigger = option.getName().startsWith("min");
			System.out.println(option.getName() + ": " + measurements.getValueAsDouble(theMap.get(option), i));
			if  (mustBeBigger) { 
				if (measurements.getValueAsDouble(theMap.get(option), i) <= option.getFloatValue()) return false;
			} else {
				if (measurements.getValueAsDouble(theMap.get(option), i) >= option.getFloatValue()) return false;
			}
		}
		return true;
	}

	private boolean valueSet(String value) {
		return value!=null && !value.equals("null") && !value.trim().isEmpty();
	}

	public Map<Option, Integer> columnForOption() {
		if (map==null) {
			map = new HashMap<Option, Integer>();
			map.put(options.getOptions().get(0), ResultsTable.AREA);
			map.put(options.getOptions().get(1), ResultsTable.AREA);
			map.put(options.getOptions().get(2), ResultsTable.MEAN);
			map.put(options.getOptions().get(3), ResultsTable.MEAN);
			map.put(options.getOptions().get(4), ResultsTable.STD_DEV);
			map.put(options.getOptions().get(5), ResultsTable.STD_DEV);
			map.put(options.getOptions().get(6), ResultsTable.MODE);
			map.put(options.getOptions().get(7), ResultsTable.MODE);
			map.put(options.getOptions().get(8), ResultsTable.MIN);
			map.put(options.getOptions().get(9), ResultsTable.MIN);
			map.put(options.getOptions().get(10), ResultsTable.MAX);
			map.put(options.getOptions().get(11), ResultsTable.MAX);
			map.put(options.getOptions().get(12), ResultsTable.X_CENTROID);
			map.put(options.getOptions().get(13), ResultsTable.X_CENTROID);
			map.put(options.getOptions().get(14), ResultsTable.Y_CENTROID);
			map.put(options.getOptions().get(15), ResultsTable.Y_CENTROID);
			map.put(options.getOptions().get(16), ResultsTable.X_CENTER_OF_MASS);
			map.put(options.getOptions().get(17), ResultsTable.X_CENTER_OF_MASS);
			map.put(options.getOptions().get(18), ResultsTable.Y_CENTER_OF_MASS);
			map.put(options.getOptions().get(19), ResultsTable.Y_CENTER_OF_MASS);
			map.put(options.getOptions().get(20), ResultsTable.PERIMETER);
			map.put(options.getOptions().get(21), ResultsTable.PERIMETER);
			map.put(options.getOptions().get(22), ResultsTable.ROI_X);
			map.put(options.getOptions().get(23), ResultsTable.ROI_X);
			map.put(options.getOptions().get(24), ResultsTable.ROI_Y);
			map.put(options.getOptions().get(25), ResultsTable.ROI_Y);
			map.put(options.getOptions().get(26), ResultsTable.ROI_WIDTH);
			map.put(options.getOptions().get(27), ResultsTable.ROI_WIDTH);
			map.put(options.getOptions().get(28), ResultsTable.ROI_HEIGHT);
			map.put(options.getOptions().get(29), ResultsTable.ROI_HEIGHT);
			map.put(options.getOptions().get(30), ResultsTable.MAJOR);
			map.put(options.getOptions().get(31), ResultsTable.MAJOR);
			map.put(options.getOptions().get(32), ResultsTable.MINOR);
			map.put(options.getOptions().get(33), ResultsTable.MINOR);
			map.put(options.getOptions().get(34), ResultsTable.ANGLE);
			map.put(options.getOptions().get(35), ResultsTable.ANGLE);
			map.put(options.getOptions().get(36), ResultsTable.CIRCULARITY);
			map.put(options.getOptions().get(37), ResultsTable.CIRCULARITY);
			map.put(options.getOptions().get(38), ResultsTable.FERET);
			map.put(options.getOptions().get(39), ResultsTable.FERET);
			map.put(options.getOptions().get(40), ResultsTable.INTEGRATED_DENSITY);
			map.put(options.getOptions().get(41), ResultsTable.INTEGRATED_DENSITY);
			map.put(options.getOptions().get(42), ResultsTable.MEDIAN);
			map.put(options.getOptions().get(43), ResultsTable.MEDIAN);
			map.put(options.getOptions().get(44), ResultsTable.SKEWNESS);
			map.put(options.getOptions().get(45), ResultsTable.SKEWNESS);
			map.put(options.getOptions().get(46), ResultsTable.KURTOSIS);
			map.put(options.getOptions().get(47), ResultsTable.KURTOSIS);
		}
		return map;
	}
	
	public void connectOptions() {
		this.minArea= (Option) this.options.getOptions().get(0);
		this.maxArea = (Option) this.options.getOptions().get(1);
		this.minMean = (Option) this.options.getOptions().get(2);
		this.maxMean= (Option) this.options.getOptions().get(3);
		this.minStdDev = (Option) this.options.getOptions().get(4);
		this.maxStdDev = (Option) this.options.getOptions().get(5);
		this.minMode= (Option) this.options.getOptions().get(6);
		this.maxMode = (Option) this.options.getOptions().get(7);
		this.minMin = (Option) this.options.getOptions().get(8);
		this.maxMin= (Option) this.options.getOptions().get(9);
		this.minMax = (Option) this.options.getOptions().get(10);
		this.maxMax = (Option) this.options.getOptions().get(11);
		this.minCentroidX= (Option) this.options.getOptions().get(12);
		this.maxCentroidX = (Option) this.options.getOptions().get(13);
		this.minCentroidY = (Option) this.options.getOptions().get(14);
		this.maxCentroidY= (Option) this.options.getOptions().get(15);
		this.minCenterOfMassX = (Option) this.options.getOptions().get(16);
		this.maxCenterOfMassX = (Option) this.options.getOptions().get(17);
		this.minCenterOfMassY = (Option) this.options.getOptions().get(18);
		this.maxCenterOfMassY = (Option) this.options.getOptions().get(19);
		this.minPerimeter = (Option) this.options.getOptions().get(20);
		this.maxPerimeter = (Option) this.options.getOptions().get(21);
		this.minBoundingBoxX = (Option) this.options.getOptions().get(22);
		this.maxBoundingBoxX = (Option) this.options.getOptions().get(23);
		this.minBoundingBoxY = (Option) this.options.getOptions().get(24);
		this.maxBoundingBoxY = (Option) this.options.getOptions().get(25);
		this.minBoundingBoxWidth = (Option) this.options.getOptions().get(26);
		this.maxBoundingBoxWidth = (Option) this.options.getOptions().get(27);
		this.minBoundingBoxHeight = (Option) this.options.getOptions().get(28);
		this.maxBoundingBoxHeight = (Option) this.options.getOptions().get(29);
		this.minMajor = (Option) this.options.getOptions().get(30);
		this.maxMajor = (Option) this.options.getOptions().get(31);
		this.minMinor = (Option) this.options.getOptions().get(32);
		this.maxMinor= (Option) this.options.getOptions().get(33);
		this.minAngle = (Option) this.options.getOptions().get(34);
		this.maxAngle = (Option) this.options.getOptions().get(35);
		this.minCircularity = (Option) this.options.getOptions().get(36);
		this.maxCircularity = (Option) this.options.getOptions().get(37);
		this.minFeret = (Option) this.options.getOptions().get(38);
		this.maxFeret= (Option) this.options.getOptions().get(39);
		this.minIntDensity = (Option) this.options.getOptions().get(40);
		this.maxIntDensity = (Option) this.options.getOptions().get(41);
		this.minMedian= (Option) this.options.getOptions().get(42);
		this.maxMedian = (Option) this.options.getOptions().get(43);
		this.minSkew = (Option) this.options.getOptions().get(44);
		this.maxSkew= (Option) this.options.getOptions().get(45);
		this.minKurt = (Option) this.options.getOptions().get(46);
		this.maxKurt = (Option) this.options.getOptions().get(47);
	}
}
