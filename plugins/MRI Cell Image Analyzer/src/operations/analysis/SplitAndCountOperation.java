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

import java.awt.Color;
import java.awt.Font;

import ij.ImagePlus;
import ij.ImageStack;
import ij.measure.ResultsTable;
import ij.plugin.filter.Analyzer;
import gui.options.Option;
import operations.Operation;

/**
 * Takes a mask and a results table containing the areas and centroids of the objects in the mask.
 * Corrects the count of the objects by using the average size of this kinds of objects, that must
 * be provided as an option. 
 * 
 * @author Volker Baecker
 */
public class SplitAndCountOperation extends Operation {
	private static final long serialVersionUID = 573964876472704428L;
	Option averageSize;
	ResultsTable measurements;
	ResultsTable count;
	protected float[] areas;
	protected double averageArea;
	
	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		optionsNames = new String[1];
		optionsNames[0] = "average size";
		parameterTypes = new Class[2];
		parameterTypes[0] = Class.forName("ij.ImagePlus");
		parameterTypes[1] = Class.forName("ij.measure.ResultsTable");
		parameterNames = new String[2];
		parameterNames[0] = "InputImage";
		parameterNames[1] = "Measurements";
		resultTypes = new Class[2];
		resultTypes[0] = Class.forName("ij.ImagePlus");
		resultTypes[1] = Class.forName("ij.measure.ResultsTable");
		resultNames = new String[2];
		resultNames[0] = "Result";
		resultNames[1] = "Count";
	}

	public double getAverageSize() {
		return averageSize.getDoubleValue();
	}

	public void setAverageSize(double averageSize) {
		this.averageSize.setValue(Double.toString(averageSize));
	}
	
	public void connectOptions() {
		this.averageSize = (Option) this.options.getOptions().get(0);
	}
	
	protected void setupOptions() {
		super.setupOptions();
		this.setAverageSize(0);
	}
	
	public void doIt() {
		ImagePlus inputImage = this.getInputImage();
		result = this.getCopyOfOrReferenceTo(inputImage, inputImage.getTitle());
		this.setupAreasAndAverage();
		this.processSlices();
	}
	
	protected void doItForSlice(int sliceNumber, ImageStack stack) {
		ResultsTable measurements = this.getMeasurements();
		count = new ResultsTable();
		int newCount = 0;
		for (int i=0; i<measurements.getCounter(); i++) {
			float area = areas[i];
			int objectCount = Math.max((int)Math.floor(area / (averageArea * 1.0)), 1);
			newCount += objectCount;
			this.markObject(i, objectCount);
		}
		count.incrementCounter();
		count.addValue("count", newCount);
	}
	
	private void markObject(int i, int objectCount) {
		int x = Math.round(this.getMeasurements().getColumn(ResultsTable.X_CENTROID)[i]);
		int y = Math.round(this.getMeasurements().getColumn(ResultsTable.Y_CENTROID)[i]);
		result.getProcessor().setColor(Color.green);
		result.getProcessor().setFont(new Font("SansSerif", Font.PLAIN, 48));
		result.getProcessor().drawString(Integer.toString(objectCount), x, y);
		result.getProcessor().setColor(Color.black);
	}

	protected void setupAreasAndAverage() {
		areas = this.getMeasurements().getColumn(ResultsTable.AREA);
		if (this.getAverageSize()==0) {
			averageArea = 0;
			for (int i=0; i<areas.length; i++) {
				averageArea += areas[i];
			}
			averageArea /= (areas.length * 1.0);
		} else {
			averageArea = this.getAverageSize();
		}
	}

	public ResultsTable getMeasurements() {
		if (measurements==null) this.measurements = Analyzer.getResultsTable();
		return measurements;
	}

	public void setMeasurements(ResultsTable measurements) {
		this.measurements = measurements;
	}
	
	protected void cleanUpInput() {
		super.cleanUpInput();
		this.setMeasurements(null);
	}

	public ResultsTable getCount() {
		return count;
	}

	public void setCount(ResultsTable count) {
		this.count = count;
	}
	
	protected void showResult() {
		if (this.getResult()==null) return;
		this.getResult().show();
		if (this.getCount()==null) return;
		this.getCount().show("results from split and count");
	}
}
