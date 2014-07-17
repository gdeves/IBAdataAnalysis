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

import analysis.signalToNoise.SignalToNoiseEstimator;
import ij.ImagePlus;
import ij.measure.ResultsTable;
import gui.options.Option;
import operations.Operation;

/**
 * Estimate the signal to noise ratio for each pixel in the image,
 * based on a region of a given size by applying an otsu-threshold 
 * to the region and counting pixels with an intensity above the 
 * threshold as foreground and other pixels as background. 
 * 
 * sne = (foreground mean - background mean) / background stdDev
 * 
 * @author	Volker Baecker 
 */
public class LocalSnrOperation extends Operation {
	private static final long serialVersionUID = -239273230908221865L;
	protected Option neighborhoodRadiusX;
	protected Option neighborhoodRadiusY;
	protected ResultsTable median;

	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		parameterTypes = new Class[1];
		parameterTypes[0] = Class.forName("ij.ImagePlus");
		parameterNames = new String[1];
		parameterNames[0] = "InputImage";
		optionsNames = new String[2];
		optionsNames[0] = "radius x";
		optionsNames[1] = "radius y";
		resultTypes = new Class[2];
		resultTypes[0] = Class.forName("ij.ImagePlus");
		resultTypes[1] = Class.forName("ij.measure.ResultsTable");
		resultNames = new String[2];
		resultNames[0] = "Result";
		resultNames[1] = "Median";
	}
	
	public void doIt() {
		ImagePlus inputImage = this.getInputImage();
		this.getMedian().reset();
		SignalToNoiseEstimator sne = SignalToNoiseEstimator.newFor(inputImage, this.getNeighborhoodRadiusX(), 
																			   this.getNeighborhoodRadiusY());
		sne.run();
		this.result = sne.getLocalSNRImage();
		median.incrementCounter();
		median.addValue(0, sne.getMedianSNR());
	}

	public int getNeighborhoodRadiusX() {
		return neighborhoodRadiusX.getIntegerValue();
	}

	public void setNeighborhoodRadiusX(int neighborhoodRadiusX) {
		this.neighborhoodRadiusX.setIntegerValue(neighborhoodRadiusX);
	}

	public int getNeighborhoodRadiusY() {
		return neighborhoodRadiusY.getIntegerValue();
	}

	public void setNeighborhoodRadiusY(int neighborhoodRadiusY) {
		this.neighborhoodRadiusY.setIntegerValue(neighborhoodRadiusY);
	}	
	
	protected void setupOptions() {
		super.setupOptions();
		this.setNeighborhoodRadiusX(50);
		this.neighborhoodRadiusX.setMin(1);
		this.neighborhoodRadiusX.setShortHelpText("The radius of the local neighborhood in x direction.");
		this.setNeighborhoodRadiusY(50);
		this.neighborhoodRadiusY.setMin(1);
		this.neighborhoodRadiusY.setShortHelpText("The radius of the local neighborhood in y direction.");
	}
	public void connectOptions() {
		this.neighborhoodRadiusX= (Option) this.options.getOptions().get(0);
		this.neighborhoodRadiusY = (Option) this.options.getOptions().get(1);
	}

	public ResultsTable getMedian() {
		if (median==null) median = new ResultsTable();
		return median;
	}

	public void setMedian(ResultsTable median) {
		this.median = median;
	}
	
	protected void showResult() {
		if (this.getResult()==null) return;
		this.getResult().show();
		this.getMedian().show("median snr estimation");
	}
}
