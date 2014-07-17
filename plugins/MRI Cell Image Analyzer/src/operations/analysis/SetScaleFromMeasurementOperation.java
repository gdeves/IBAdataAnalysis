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

import gui.Options;
import gui.options.Option;
import ij.IJ;
import ij.measure.ResultsTable;
import ij.plugin.filter.Analyzer;
import operations.FilterOperation;

/**
 * Sets the spacial calibration of the image. The known distance and the unit are
 * options of the operation. The measured distance is taken from the results table
 * that is passed in as a parameter. It must contain the distance in th first row
 * of the column width.
 * 
 * @author	Volker Baecker
 **/
public class SetScaleFromMeasurementOperation extends FilterOperation {
	private static final long serialVersionUID = 6431280192717450380L;
	protected ResultsTable measuredLength;
	protected Option length;
	protected Option unit;
	
	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		parameterTypes = new Class[2];
		parameterTypes[0] = Class.forName("ij.ImagePlus");
		parameterTypes[1] = Class.forName("ij.measure.ResultsTable");
		parameterNames = new String[2];
		parameterNames[0] = "InputImage";
		parameterNames[1] = "MeasuredLength";
		resultTypes = new Class[1];
		resultTypes[0] = Class.forName("ij.ImagePlus");
		resultNames = new String[1];
		resultNames[0] = "Result";
		optionsNames = new String[2];
		optionsNames[0] = "length";
		optionsNames[1] = "unit";
	}

	public void runFilter() {
		float distance = (float)getMeasuredLength().getValue("Width", 0);
		String parameter = "distance="+ distance + " known="+ this.getLength()+" pixel=1 unit=" + this.getUnit();
		IJ.run("Set Scale...", parameter);
	}

	public float getLength() {
		return length.getFloatValue();
	}

	public void setLength(float length) {
		this.length.setFloatValue(length);
	}

	public ResultsTable getMeasuredLength() {
		if (measuredLength==null) {
			measuredLength = Analyzer.getResultsTable();
		}
		return measuredLength;
	}

	public void setMeasuredLength(ResultsTable measuredLength) {
		this.measuredLength = measuredLength;
	}

	public String getUnit() {
		return unit.getValue();
	}

	public void setUnit(String unit) {
		this.unit.setValue(unit);
	}
	
	protected void cleanUpInput() {
		super.cleanUpInput();
		this.setMeasuredLength(null);
	}
	
	protected void setupOptions() {
		options = new Options(); 
		options.setName(this.name() + " options");
		String[] optionsNames = this.getOptionsNames();
	
		this.length = new Option();
		this.setLength(1);
		this.length.setName(optionsNames[0]);
		this.length.setMin(0);
		this.length.setShortHelpText("Enter the known length");
		options.add(length);
		
		this.unit = new Option();
		this.setUnit("cm");
		this.unit.setName(optionsNames[1]);
		this.unit.setShortHelpText("Enter the unit of the known length");
		options.add(unit);
	}
	
	public void connectOptions() {
		this.length = (Option) this.options.getOptions().get(0);
		this.unit = (Option) this.options.getOptions().get(1);
	}
}
