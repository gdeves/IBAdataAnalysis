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
package operations.processing;

import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ImageProcessor;
import ij.process.ImageStatistics;
import gui.options.Option;
import operations.Operation;

/**
 * Search the biggest value around the minima of the image. Subtracts the value from the image.  
 * 
 * @author Volker Bäcker
 */
public class FindAndSubtractBackgroundOperation extends Operation {
	private static final long serialVersionUID = 1072660353849887799L;
	protected Option radius;
	protected Option offset;
	protected Option iterations;
	protected Option skipLimit;

	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		optionsNames = new String[4];
		optionsNames[0] = "radius";
		optionsNames[1] = "offset";
		optionsNames[2] = "iterations";
		optionsNames[3] = "skip limit";
		parameterTypes = new Class[1];
		parameterTypes[0] = Class.forName("ij.ImagePlus");
		parameterNames = new String[1];
		parameterNames[0] = "InputImage";
		resultTypes = new Class[1];
		resultTypes[0] = Class.forName("ij.ImagePlus");
		resultNames = new String[1];
		resultNames[0] = "Result";
	}
	
	public void doIt() {
		ImagePlus inputImage = this.getInputImage();
		result = this.getCopyOfOrReferenceTo(inputImage, inputImage.getTitle());
		this.processSlices();
	}

	protected void doItForSlice(int sliceNumber, ImageStack stack) {
		ImageProcessor ip = stack.getProcessor(sliceNumber);
		double ratio = ip.getHistogram()[0] / (ip.getWidth()*ip.getHeight() * 1.0);
		if (ratio>this.getSkipLimit()) return;
		int iterations = this.getIterations();
		for (int i = 0; i < iterations; i++) {
			int minPlusOffset = (this.getMin(ip) + this.getOffset());
			int max = 0;
			ImageStatistics stats = ImageStatistics.getStatistics(ip, ImageStatistics.MEAN, result.getCalibration());
			double mean = stats.mean;
			for (int x = 0; x < ip.getWidth(); x++) {
				for (int y = 0; y < ip.getHeight(); y++) {
					int intensity = ip.getPixel(x, y);
					if (intensity <= minPlusOffset) {
						int value = this.getMaxIntensityAround(x, y, ip, mean);
						if (value > max)
							max = value;
					}
				}
			}
			ip.add(-1 * (max / (i+1)));
		}
		result.setProcessor(result.getTitle(), ip);
	}

	protected int getMin(ImageProcessor ip) {
		int result = 0;
		int[] histogram = ip.getHistogram();
		for (int i=0; i<histogram.length; i++) {
			if (histogram[i]>0) {
				result = i;
				break;
			}
		}
		return result;
	}

	protected int getMaxIntensityAround(int x, int y, ImageProcessor ip, double mean) {
		int radius = this.getRadius();
		int max = 0;
		for (int i=x-radius; i<=x+radius; i++) {
			if (i<0 || i>= ip.getWidth()) continue;
			for (int j=y-radius; j<=y+radius; j++) {
				if (j<0 || j>=ip.getHeight()) continue;
				int value = ip.getPixel(i,j);
				if (value>=mean) continue;
				if (value>max) max = value;
			}
		}
		return max;
	}

	public int getOffset() {
		return offset.getIntegerValue();
	}

	public void setOffset(int offset) {
		this.offset.setValue(Integer.toString(offset));
	}

	public int getRadius() {
		return radius.getIntegerValue();
	}

	public void setRadius(int radius) {
		this.radius.setValue(Integer.toString(radius));
	}
	
	public int getIterations() {
		return iterations.getIntegerValue();
	}
	
	public void setIterations(int iterations) {
		this.iterations.setValue(Integer.toString(iterations));
	}
	
	protected void setupOptions() {
		super.setupOptions();
		this.setRadius(1);
		this.radius.setMin(1);
		this.radius.setShortHelpText("radius around the minima to search in.");
		this.setOffset(1);
		this.offset.setMin(0);
		this.offset.setShortHelpText("values offset above minimum are still regarded as minimum.");
		this.setIterations(2);
		this.iterations.setMin(1);
		this.iterations.setShortHelpText("number of times the procedure is run.");
		this.setSkipLimit(0.05);
		this.skipLimit.setMin(0);
		this.skipLimit.setShortHelpText("images where the portion of pixel is already zero are skipped.");
	}
	
	public void connectOptions() {
		this.radius = (Option) this.options.getOptions().get(0);
		this.offset = (Option) this.options.getOptions().get(1);
		this.iterations = (Option) this.options.getOptions().get(2);
		this.skipLimit = (Option) this.options.getOptions().get(3);
	}

	public double getSkipLimit() {
		return skipLimit.getDoubleValue();
	}

	public void setSkipLimit(double skipLimit) {
		this.skipLimit.setValue(Double.toString(skipLimit));
	}
}
