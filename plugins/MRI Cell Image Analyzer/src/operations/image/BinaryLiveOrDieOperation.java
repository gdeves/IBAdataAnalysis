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
package operations.image;

import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.Roi;
import ij.process.ImageProcessor;
import gui.options.Option;
import operations.Operation;
/**
 * The operation implements a totalistic cellular automaton rule.
 * The operation runs a given number of iterations. In each iteration each pixel is replaced by
 * 255 if the number of pixels in his neighborhood is not smaller than the threshold and by 0
 * otherwise.  
 * 
 * @author Volker Bäcker
 */
public class BinaryLiveOrDieOperation extends Operation {
	private static final long serialVersionUID = 1068109090253399035L;
	protected Option radius;
	protected Option countThreshold;
	protected Option iterations;
	private ImagePlus current;
	private ImagePlus next;
	private int width;
	private int height;
	private int r;
	private int t;
	private int n;
	
	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		parameterTypes = new Class[1];
		parameterTypes[0] = Class.forName("ij.ImagePlus");
		parameterNames = new String[1];
		parameterNames[0] = "InputImage";
		resultTypes = new Class[1];
		resultTypes[0] = Class.forName("ij.ImagePlus");
		resultNames = new String[1];
		resultNames[0] = "Result";
		optionsNames = new String[3];
		optionsNames[0] = "radius";
		optionsNames[1] = "count threshold";
		optionsNames[2] = "iterations";
	}
	
	public void doIt() {
		ImagePlus inputImage = this.getInputImage();
		Roi roi = inputImage.getRoi();
		inputImage.killRoi();
		current = this.getCopyOfOrReferenceTo(inputImage, inputImage.getTitle());
		result = current;
		next = this.getCopyOfOrReferenceTo(inputImage, inputImage.getTitle());
		width = inputImage.getWidth();
		height = inputImage.getHeight();
		r = this.getRadius();
		t = this.getCountThreshold();
		n = this.getIterations();
		this.processSlices();
		inputImage.setRoi(roi);
		if (n%2==0) 
			result = current; 
		else 
			result = next;
	}
	
	protected void doItForSlice(int sliceNumber, ImageStack stack) {
		ImageStack currentStack = stack;
		ImageStack nextStack = next.getImageStack();
		ImageProcessor currentProcessor = currentStack.getProcessor(sliceNumber);
		ImageProcessor nextProcessor = nextStack.getProcessor(sliceNumber);
		ImageProcessor tmp = null;
		for (int generation=0; generation<n; generation++) {
			for (int x=0; x<width; x++) {
				for (int y=0; y<height; y++) {
					int counter = 0;
					for (int nx = x-r; nx<=x+r; nx++) {
						for (int ny = y-r; ny<=y+r; ny++) {
							if (currentProcessor.getPixel(nx, ny)>0) counter++;
						}
					}
					if (counter>=t) 
						nextProcessor.set(x, y, 255);	// live 
					else
						nextProcessor.set(x, y, 0);		// die
				}
			}
			tmp = nextProcessor;
			nextProcessor = currentProcessor;
			currentProcessor = tmp;
		}
	}

	public int getCountThreshold() {
		return countThreshold.getIntegerValue();
	}

	public void setCountThreshold(int countThreshold) {
		this.countThreshold. setIntegerValue(countThreshold);
	}

	public int getIterations() {
		return iterations.getIntegerValue();
	}

	public void setIterations(int iterations) {
		this.iterations.setIntegerValue(iterations);
	}

	public int getRadius() {
		return radius.getIntegerValue();
	}

	public void setRadius(int radius) {
		this.radius.setIntegerValue(radius);
	}
	
	protected void setupOptions() {
		super.setupOptions();
		this.setRadius(1);
		radius.setMin(1);
		radius.setShortHelpText("Enter the radius of the neighborhood.");
		this.setCountThreshold(5);
		countThreshold.setMin(0);
		countThreshold.setShortHelpText("The min. count for a cell to stay or become alive");
		this.setIterations(1);
		iterations.setMin(1);
		iterations.setShortHelpText("The number of iterations the operation will run.");
	}
	
	public void connectOptions() {
		radius = (Option)options.getOptions().get(0);
		countThreshold = (Option)options.getOptions().get(1);
		iterations = (Option)options.getOptions().get(2);
	}
}
