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

import gui.options.Option;
import ij.measure.ResultsTable;
import operations.Operation;

/**
 * Filter objects from a results table by width of the bounding rectangle, circularity and ratio of
 * width and height of the bounding rectangle (height times factor > width). The input table must
 * contain the measurements: centroid, bounding box and circularity. The result table will contain
 * the centroids of the objects that fullfil the conditions.
 * 
 * @author	Volker Baecker
 **/
public class FilterLongObjectsOperation extends Operation {
	private static final long serialVersionUID = -4636314007082067134L;
	protected ResultsTable measurements;
	protected ResultsTable resultMeasurements;
	protected Option minSize;
	protected Option minCircularity;
	protected Option minSizeHeightFactor;

	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		parameterTypes = new Class[1];
		parameterTypes[0] = Class.forName("ij.measure.ResultsTable");
		parameterNames = new String[1];
		parameterNames[0] = "Measurements";
		optionsNames = new String[3];
		optionsNames[0] = "min size";
		optionsNames[1] = "min circularity";
		optionsNames[2] = "min size-height factor";
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

	public float getMinCircularity() {
		return minCircularity.getFloatValue();
	}

	public void setMinCircularity(float minCircularity) {
		this.minCircularity.setValue(new Float(minCircularity).toString());
	}
	
	public float getMinSize() {
		return minSize.getFloatValue();
	}

	public void setMinSize(float minSize) {
		this.minSize.setValue(new Float(minSize).toString());
	}

	public float getMinSizeHeightFactor() {
		return minSizeHeightFactor.getFloatValue();
	}

	public void setMinSizeHeightFactor(float minSizeHeightFactor) {
		this.minSizeHeightFactor.setValue(new Float(minSizeHeightFactor).toString());
	}
	
	protected void setupOptions() {
		super.setupOptions();
		this.setMinSize(10);
		this.setMinCircularity(0.3f);
		this.setMinSizeHeightFactor(1.5f);
	}
	
	public void connectOptions() {
		this.minSize= (Option) this.options.getOptions().get(0);
		this.minCircularity = (Option) this.options.getOptions().get(1);
		this.minSizeHeightFactor = (Option) this.options.getOptions().get(2);
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
			double xSize = measurements.getValueAsDouble(ResultsTable.ROI_WIDTH, i);
			double circularity = measurements.getValueAsDouble(ResultsTable.CIRCULARITY, i);
			double ySize = measurements.getValueAsDouble(ResultsTable.ROI_HEIGHT, i);
			if (xSize<this.getMinSize()) continue;
			if (circularity>this.getMinCircularity()) continue;
			if (ySize*this.getMinSizeHeightFactor()>xSize) continue;
			resultMeasurements.incrementCounter();
			resultMeasurements.addValue(ResultsTable.X_CENTROID, measurements.getValueAsDouble(ResultsTable.X_CENTROID, i));
			resultMeasurements.addValue(ResultsTable.Y_CENTROID, measurements.getValueAsDouble(ResultsTable.Y_CENTROID, i));
		}
	}
}
