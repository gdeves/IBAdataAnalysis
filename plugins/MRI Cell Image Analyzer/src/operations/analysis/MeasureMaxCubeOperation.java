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

import java.awt.Rectangle;

import ij.ImagePlus;
import ij.measure.ResultsTable;
import ij.process.ImageProcessor;
import gui.options.Option;
import operations.Operation;

/**
 * Find the brightest cube of radius r within the rectangular selection and measure its integrated intensity and its
 * average intensity.
 * 
 * @author	Volker Baecker 
 */
public class MeasureMaxCubeOperation extends Operation {
	private static final long serialVersionUID = -921442050558400265L;
	protected Option radius;
	protected ResultsTable measurements;
	protected int startX;
	protected int startY;
	protected int startZ;
	protected int endX;
	protected int endY;
	protected int endZ;
	protected int bestX;
	protected int bestY;
	protected int bestZ;
	protected int count;
	
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
		optionsNames = new String[1];
		optionsNames[0] = "radius";
	}

	public void doIt() {
		ImagePlus inputImage = this.getInputImage();
		long max = 0;
		bestX = -1;
		bestY = -1;
		bestZ = -1;
		long currentValue;
		calculateStartAndEndPoints(inputImage);
		for (int x=startX; x<=endX; x++) {
			for (int y=startY; y<=endY; y++) {
				for (int z=startZ; z<=endZ; z++) {
					currentValue = this.calculateIntensity(x,y,z);
					if (currentValue>max) {
						max = currentValue;
						bestX = x;
						bestY = y;
						bestZ = z;
					}
				}
			}
		}
		createResultsTable(max);
		if (this.getShowResult()) this.measurements.show("best cube");
	}

	protected void createResultsTable(long max) {
		this.measurements = new ResultsTable();
		this.measurements.reset();
		this.measurements.incrementCounter();
		this.measurements.addValue("integrated intensity", max);
		this.measurements.addValue("average intensity", max / (count * 1.0));
		this.measurements.addValue("x", bestX);
		this.measurements.addValue("y", bestY);
		this.measurements.addValue("z", bestZ);
		this.measurements.addValue("radius", this.getRadius());
	}

	protected long calculateIntensity(int x, int y, int z) {
		long sum = 0;
		count = 0; 
		int r = this.getRadius();
		for (int cubeX=x-r; cubeX<=x+r; cubeX++) {
			for (int cubeY=y-r; cubeY<=y+r; cubeY++) {
				for (int cubeZ=z-r; cubeZ<=z+r; cubeZ++) {
					count++;
					sum = sum + this.getValueAt(cubeX, cubeY, cubeZ);
				}
			}
		}
		return sum;
	}

	private long getValueAt(int cubeX, int cubeY, int cubeZ) {
		ImagePlus inputImage = this.getInputImage();
		if (cubeZ<1 || cubeZ > inputImage.getImageStackSize()) return 0;
		ImageProcessor ip = inputImage.getImageStack().getProcessor(cubeZ);
		return ip.getPixel(cubeX, cubeY);
	}

	protected void calculateStartAndEndPoints(ImagePlus inputImage) {
		startZ = 1;
		endZ = inputImage.getStackSize();
		startX = 0;
		startY = 0;
		endX = inputImage.getWidth()-1;
		endY = inputImage.getHeight()-1;
		if (inputImage.getRoi()!=null) {
			Rectangle bounds = this.getInputImage().getRoi().getBounds();
			startX = bounds.x;
			startY = bounds.y;
			endX = startX + bounds.width - 1;
			endY = startY + bounds.height - 1;
		}
	}
	
	public ResultsTable getMeasurements() {
		return measurements;
	}

	public void setMeasurements(ResultsTable measurements) {
		this.measurements = measurements;
	}

	public int getRadius() {
		return radius.getIntegerValue();
	}

	public void setRadius(int radius) {
		this.radius.setValue(Integer.toString(radius));
	}

	protected void setupOptions() {
		super.setupOptions();
		this.setRadius(1);
		radius.setShortHelpText("The radius of the cube");
	}
	
	public void connectOptions() {
		radius = (Option) options.getOptions().get(0);
	}
}
