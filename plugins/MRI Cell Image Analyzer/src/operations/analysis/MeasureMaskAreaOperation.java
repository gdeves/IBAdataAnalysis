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
import ij.measure.ResultsTable;
import operations.Operation;

/**
 * Counts the pixel with value 255 in a mask (binary image (0 and 255)).   
 * 
 * @author	Volker Baecker 
 */
public class MeasureMaskAreaOperation extends Operation {
	private static final long serialVersionUID = -3578980958793584230L;
	protected ResultsTable measurements;
	
	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		parameterTypes = new Class[1];
		parameterTypes[0] = Class.forName("ij.ImagePlus");
		parameterNames = new String[1];
		parameterNames[0] = "InputImage";
		resultTypes = new Class[1];
		resultTypes[0] = Class.forName("ij.measure.ResultsTable");
		resultNames = new String[1];
		resultNames[0] = "Measurements";
	}

	public ResultsTable getMeasurements() {
		return measurements;
	}

	public void setMeasurements(ResultsTable measurements) {
		this.measurements = measurements;
	}
	
	public void doIt() {
		MeasureOperation measure = new MeasureOperation();
		measure.setMeasureIntegratedDensity(true);
		measure.setInputImage(this.getInputImage());
		measure.doIt();
		ResultsTable tmpMeasurements = measure.getMeasurements();
		double area = (tmpMeasurements.getValueAsDouble(ResultsTable.INTEGRATED_DENSITY, 0) / (255 * 1.0d));
		this.measurements = new ResultsTable();
		this.measurements.reset();
		this.measurements.incrementCounter();
		this.measurements.addValue("area", area);
		if (this.getShowResult()) this.measurements.show("mask area");
	}
}
