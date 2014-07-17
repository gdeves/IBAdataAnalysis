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

import gui.options.Option;
import ij.ImagePlus;
import ij.ImageStack;
import ij.plugin.filter.GaussianBlur;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import operations.Operation;

/**
 * Applies a Gaussian blur filter to the image.
 * 
 * @author Volker Bäcker
 */
public class GaussianBlurOperation extends Operation {
	private static final long serialVersionUID = -3947860506816536185L;
	protected GaussianBlur gaussianFilter;
	protected Option sigma;	
	
	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		optionsNames = new String[1];
		optionsNames[0] = "sigma";
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
		gaussianFilter = null;
	}
	
	protected void doItForSlice(int sliceNumber, ImageStack stack) {
		ImageProcessor ip = stack.getProcessor(sliceNumber);
		 double accuracy = (ip instanceof ByteProcessor || ip instanceof ColorProcessor) ?
		            0.002 : 0.0002;
		this.getGaussianBlurFilter(). blurGaussian(ip, this.getSigma(), this.getSigma(), accuracy); 
	}
	
	protected GaussianBlur getGaussianBlurFilter() {
		if (gaussianFilter==null)  {
			gaussianFilter = new GaussianBlur();
			gaussianFilter.setup("", result);
		}
		return gaussianFilter;
	}
	
	protected void setupOptions() {
		super.setupOptions();
		this.setSigma(1/2.5f);
		this.sigma.setShortHelpText("The standard deviation of the Gaussian.");
	}

	public void connectOptions() {
		this.sigma = (Option) this.options.getOptions().get(0);
	}

	public void setSigma(float radius) {
		this.sigma.setValue((new Float(radius)).toString());	
	}

	public float getSigma() {
		return sigma.getFloatValue();
	}
}
