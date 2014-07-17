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

import ij.gui.Roi;
import ij.measure.ResultsTable;
import operations.Operation;
import regionGrowing.ImageTypeDoesntExist;
import utils.ImageAccessor;

/**
 * Calculates the image entropy within the image or the selection. The image must be 8-bit.
 * 
 * @author	Dimitri Vibert 
 */
public class MeasureEntropyOperation extends Operation {
	private static final long serialVersionUID = 1L;
	private ImageAccessor data;
	private ResultsTable measurements;
	
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
		try {
			data = ImageAccessor.newFor(this.getInputImage());
		} catch (ImageTypeDoesntExist e) {
			e.printStackTrace();
		}
		this.measurements = new ResultsTable();
		this.measurements.reset();
		this.measurements.incrementCounter();
		Roi roi = this.getInputImage().getRoi();
		if(roi==null) roi = new Roi(0, 0, this.getInputImage().getWidth(), this.getInputImage().getHeight());
		int[] hist = new int[256];
		for(int i=0;i<hist.length;i++) hist[i]=0;
		int x = roi.getBounds().x;
		int y = roi.getBounds().y;
		int width = roi.getBounds().width;
		int height = roi.getBounds().height;
		int pixels = 0;
		for(int i=0;i<width+x;i++) {
			for(int j=0;j<height+y;j++) {
				if(roi.contains(i, j)){
					int index = (int) data.getPixel(i, j);
					hist[index]++;
					pixels++;
				}
			}
		}
		double sum = 0;
		int i = 0;
		for(i=0;i<hist.length;i++) {
			if(hist[i]==0) continue;
			double ratio = hist[i]/((double)pixels);
			sum += (ratio*(Math.log(ratio)/Math.log(2)));	
		}		
		this.measurements.addValue(0, -sum);
		this.measurements.incrementCounter();
		measurements.deleteRow(measurements.getCounter()-1);
	}
	
	public void showResult() {
		this.measurements.show("Entropy");
	}
}
