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

import operations.Operation;
import ij.gui.Roi;
import ij.measure.ResultsTable;
import ij.process.ImageProcessor;

/**
 * Write a list of all intensity values in the image or in the selection into
 * a results table. The values are written from left to right, column by column.
 * The input image must be a float image (32-bit).
 * 
 * @author	Volker Baecker 
 */
public class MeasureIntensityValuesOperation extends Operation {
	private static final long serialVersionUID = 6189922614106287660L;	
	ResultsTable measurements;
	
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
		this.measurements = new ResultsTable();
		this.measurements.reset();
		this.measurements.incrementCounter();
		ImageProcessor ip = this.getInputImage().getProcessor();
		float[] pixels = (float[]) ip.getPixels();
		Roi roi = inputImage.getRoi();
		for (int x=0; x<inputImage.getWidth(); x++) {
			for (int y=0; y<inputImage.getHeight(); y++) {
				if (roi==null || roi.contains(x, y)) {
					int pos = y*inputImage.getWidth()+x;
					this.measurements.addValue(0, pixels[pos]);
					this.measurements.incrementCounter();
				}
			}
		}
		measurements.deleteRow(measurements.getCounter()-1);
		if (this.getShowResult()) this.measurements.show("intensities");
	}
}
